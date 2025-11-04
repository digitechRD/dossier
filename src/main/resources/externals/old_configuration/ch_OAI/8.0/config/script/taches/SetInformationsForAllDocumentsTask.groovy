import Constants
import Methods
import com.aspose.slides.Presentation
import com.digitech.common.image.IImage
import com.digitech.common.image.ImageFactory
import com.digitech.common.lib.utils.StringUtils
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.utils.ApplicationUtils
import com.digitech.jcorbairs.Document
import com.digitech.jcorbairs.admin.AuthorityListTermAdmin
import com.digitech.jcorbairs.admin.AuthorityListsManager
import com.digitech.report.service.impl.ooo.DocumentConvertionService
import org.apache.pdfbox.pdmodel.PDDocument

import javax.xml.bind.DatatypeConverter
import java.sql.*
import java.util.Date

/*************************************************************************************************
 *   					    			SetInformationsForNewDocumentTask - EXEC
 **************************************************************************************************
 Date : 12.04.2016
 Auteur : MTO

 Description : Permet de mettre à jour le groupe du document et le nom de l'assuré
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - SET INFORMATIONS FOR NEW DOCUMENT TASK - START")

/**
 * INITIALISATION
 **************************************************************************************************/
List<Integer> listDocumentToTreat = null
Map<String, String> nameByNSSMap = new HashMap<String, String>()

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
  listDocumentToTreat = Methods.getListIdDocumentbyRequest(Constants.DB_AIRS_REQUEST_GET_DOCUMENTS_WITHOUT_INFORMATIONS)
  nameByNSSMap = getMapUserNSS()
  if(!listDocumentToTreat.isEmpty() && !nameByNSSMap.isEmpty()) {
    for(Integer id : listDocumentToTreat) {
      try {
        Document doc = new Document(DossierCoreContext.getAdminJeton(), id)
        String typeId = null
        String name = null
        String groupId = null
        String numberPage = null

        try {
          typeId = Methods.getFieldValue(doc, Constants.LIST_TYPES_DOCUMENT_CODE)
          if(Constants.USE_GROUP_LIST)
            groupId = Methods.getFieldValue(doc, Constants.LIST_GROUPES_DOCUMENT_CODE)
          name = Methods.getFieldValue(doc, Constants.FIELD_NAME_CODE)
          numberPage = Methods.getFieldValue(doc, Constants.FIELD_NUMBER_PAGES_CODE)
        } catch(Exception e) {
          Methods.defineDocumentIndex(doc, Constants.LIST_TYPES_DOCUMENT_CODE, null)
          Methods.defineDocumentIndex(doc, Constants.FIELD_NAME_CODE, null)
          doc.updateContent()
        }

        if(typeId != null || name == null || name.isEmpty() || numberPage == null) {
          if(typeId != null && groupId == null && Constants.USE_GROUP_LIST) {
            AuthorityListTermAdmin altm = AuthorityListsManager.loadTerm(DossierCoreContext.getAdminJeton(), Integer.parseInt(typeId))
            String groupCode = altm.getValue5().replaceAll(";", "")
            List<AuthorityListTermAdmin> listGroup = AuthorityListsManager.loadTerms(DossierCoreContext.getAdminJeton(), Constants.LIST_GROUPES_DOCUMENT_ID)
            for(AuthorityListTermAdmin alta : listGroup) {
              if(groupCode.equals(alta.getCode())) {
                groupId = String.valueOf(alta.getId())
                break
              }
            }
            scriptLogger.debug("[CUSTOM ACTION] - SetInformationsForNewDocumentTask - Groupe - Document traité n° " + id + " / " + groupId + " / " + typeId)
            Methods.defineDocumentIndex(doc, Constants.LIST_GROUPES_DOCUMENT_CODE, groupId)
          }

          if(name == null || name.isEmpty()) {
            String nss = Methods.getFieldValue(doc, Constants.FIELD_NSS_CODE)
            name = nameByNSSMap.get(nss)
            if(name != null) {
              if(name.length() > 25)
                name = name.substring(0, 25)
              Methods.defineDocumentIndex(doc, Constants.FIELD_NAME_CODE, name)
              scriptLogger.debug("[CUSTOM ACTION] - SetInformationsForNewDocumentTask - Name - Document traité n° " + id + " / " + name + " / " + nss)
              doc.updateContent()
            }
          }
          else
            scriptLogger.debug("[CUSTOM ACTION] - SetInformationsForNewDocumentTask - Name :  " + name + " / Document n° " + id)

          if(numberPage == null) {
            File folder = new File(ApplicationUtils.getUserDownloadFolderPath(String.valueOf(DossierCoreContext.getAdminJeton().getTokenValue())))
            if(!folder.exists())
              folder.mkdir()
            PDDocument pddocument = null
            try {
              File file = doc.getPrimaryDocument(doc.getPrimaryDocList().get(0), folder.getAbsolutePath())
              if(StringUtils.isExtensionIgnoreCase(file.getName(), Constants.APPLICATION_PDF_EXTENSION)) {
                pddocument = PDDocument.load(file)
                Methods.defineDocumentIndex(doc, Constants.FIELD_NUMBER_PAGES_CODE, String.valueOf(pddocument.getNumberOfPages()))
                doc.updateContent()
              }
              else if(StringUtils.isExtensionIgnoreCase(file.getName(), Constants.APPLICATION_OFFICE_WORD_EXTENSION)) {
                File tmp = new File(Long.toString(new Date().getTime()) + Constants.APPLICATION_PDF_EXTENSION.toLowerCase())
                new DocumentConvertionService().convert(file, tmp)
                pddocument = PDDocument.load(tmp)
                Methods.defineDocumentIndex(doc, Constants.FIELD_NUMBER_PAGES_CODE, String.valueOf(pddocument.getNumberOfPages()))
                Methods.deleteFile(tmp)
                doc.updateContent()
              }
              else if(StringUtils.isExtensionIgnoreCase(file.getName(), Constants.APPLICATION_OFFICE_POWERPOINT_EXTENSION)) {
                FileInputStream fis = null
                try {
                  fis = new FileInputStream(file)
                  Presentation presentation = new Presentation(fis)
                  Methods.defineDocumentIndex(doc, Constants.FIELD_NUMBER_PAGES_CODE, String.valueOf(presentation.getSlides().size()))
                  doc.updateContent()
                }
                finally {
                  try {
                    fis?.close()
                  }
                  catch(IOException ignored) {
                  }
                }
              }
              else if(StringUtils.isExtensionIgnoreCase(file.getName(), Constants.APPLICATION_TIF_EXTENSION)) {
                IImage tiffImage = null
                try {
                  tiffImage = ImageFactory.buildImageFromFileName(file.getAbsolutePath())
                  Methods.defineDocumentIndex(document, Constants.FIELD_NUMBER_PAGES_CODE, String.valueOf(tiffImage.getImageCount()))
                  document.updateContent()
                } finally {
                  if(tiffImage != null) {
                    tiffImage.close()
                  }
                }
              }
              file.delete()
            } catch(Exception ex) {
              scriptLogger.error("[CUSTOM ACTION] - SetCountPagesModifiedDocumentsTask - ERROR :  Traitement du document impossible ", ex)
            } finally {
              try {
                pddocument?.close()
              }
              catch(IOException ioe) {
                scriptLogger.error("Error while closing pdf file '{}'", file, ioe)
              }
            }
          }
        }

      } catch(Exception ex) {
        scriptLogger.warn("[CUSTOM ACTION] - SetInformationsForNewDocumentTask - ERROR Document n° :  " + id, ex)
      }
    }
  }

  // Liste des noms modifiés dans Web@AI


} catch(Exception e) {
  scriptLogger.error("[CUSTOM ACTION] - SetInformationsForNewDocumentTask - ERROR :  ", e)
}

scriptLogger.debug("[CUSTOM ACTION] - SET INFORMATIONS FOR NEW DOCUMENT TASK - END")

public static Map<String, String> getMapUserNSS() throws Exception {
  Map<String, String> result = new HashMap()
  Properties conf = new Properties()
  InputStreamReader inputStreamReader = null
  Connection connection = null
  Statement statement = null
  ResultSet resultSet = null
  String query = null
  try {
    inputStreamReader = new InputStreamReader(new FileInputStream(Constants.XML_WEBSERVICES_CONFIGURATION_PATH))
    conf.load(inputStreamReader)

    Class.forName(conf.getProperty("webai.db.class"))
    connection = DriverManager.
        getConnection(conf.getProperty("webai.db.url"), conf.getProperty("webai.db.user"), new String(DatatypeConverter.parseBase64Binary(conf.getProperty(
            "webai.db.password")), "UTF-8"))
    query = "select aifrweb.tipavsp.hxnav as ID, trim(aifrweb.titierp.htlde1) || ' ' || trim(aifrweb.titierp.htlde2) as VALUE from aifrweb.titierp INNER JOIN aifrweb.tipavsp ON ( aifrweb.titierp.htitie = aifrweb.tipavsp.htitie ) where aifrweb.titierp.htpphy = '1' and aifrweb.titierp.htinac = '2'"

    statement = connection.createStatement()
    resultSet = statement.executeQuery(query)
    while(resultSet.next()) {
      result.put(resultSet.getString(1).replaceAll("[^0-9\\*\\+]", ""), resultSet.getString(2))
    }
  } catch(Exception e) {
    throw new Exception("Erreur lors de la récupération des dossiers confidentiels : " + query, e)
  } finally {
    if(resultSet != null) {
      try {
        resultSet.close()
      } catch(SQLException ex) {
        throw new Exception("Cloture de la connexion impossible (ResultSet): ", ex)
      }
    }
    if(statement != null) {
      try {
        statement.close()
      } catch(SQLException ex) {
        throw new Exception("Cloture de la connexion impossible (Statement): ", ex)
      }
    }
    if(connection != null) {
      try {
        connection.close()
      } catch(SQLException ex) {
        throw new Exception("Cloture de la connexion impossible (Connection): ", ex)
      }
    }

    if(inputStreamReader != null) {
      try {
        inputStreamReader.close()
      } catch(Exception ex) {
        throw new Exception("Cloture du fichier de configuration impossible : ", ex)
      }
    }
  }

  return result
}