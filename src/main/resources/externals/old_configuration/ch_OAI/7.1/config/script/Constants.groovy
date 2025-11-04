import com.digitech.dossier.common.utils.ApplicationUtils


/**
 * Created by Mathieu Toubache on 23.02.2016.
 * Regroupe l'ensenble des variables
 */

class Constants {
  /* Variable Métier */
  public final static Integer AIRS_PROFIL_LFA = 12
  public final static Integer AIRS_PROFIL_CONFIDENTIEL = 9
  public final static List<Integer> AIRS_PROFILS_WORKFLOW = new ArrayList<Integer>()
  public final static String AIRS_PROFIL_VISITEUR_CODE = "PROFIL_VISITEUR"

  /* Variable globale AIRS */
  public final static String BUILD_VERSION = "3.1"
  public final static String GED_VERSION = "AIRS"
  public final static String SUPPLIER_NAME = "Digitech"
  public final static Integer UNLOCK_TYPE = new Integer(0)
  public final static Integer AIRS_NOTE_ID = new Integer(1)
  public final static Integer DOC_LOCKED_BY_OTHER = new Integer(2)
  public final static String DATE_FORMAT_AIRS = "dd/MM/yyyy"
  public final static String DATE_FORMAT_SWISS = "dd.MM.yyyy"
  public final static String DATE_EXPORT_START_DEFAULT = null
  public final static String DATE_FORMAT_DB = "yyyy-MM-dd"
  public final static String DATE_EXPORT_END_DEFAULT = null
  public final static String HOURS_FORMAT_SWISS = "HH:mm:ss"
  public final static String DATE_FORMAT_INPUT = "EEE MMM d HH:mm:ss zzz yyyy"
  public final static String ADV_EVENT_FIELDCHANGE = "ADV_EVENT_FIELDCHANGE"
  public final static String ADV_EVENT_WF_TASK_SUBMIT = "ADV_EVENT_WF_TASK_SUBMIT"
  public final static String ADV_EVENT_DOCCREATE = "ADV_EVENT_DOC_CREATE"
  public final static String ADV_EVENT_COMMENT = "ADV_EVENT_COMMENT"
  public final static String ADV_EVENT_ATTACHMENT_PAGE_DELETE = "ADV_EVENT_PAGE_DELETE"
  public final static String ADV_EVENT_ATTACHMENT_PAGE_INSERT = "ADV_EVENT_ATTACHMENT_UPDATE"
  public final static Integer NSS_COUNT_CARACTERS = new Integer(13)
  public final static String FILE_EXPORT_BURN = "AIRS.tdd"
  public final static String NSS_MASK = "AAA.AAAA.AAAA.AA"
  public final static Integer ID_TYPE_DOCUMENT_EXPORT_COUNT_DIGIT = 5
  public final static boolean USE_GROUP_LIST = true
  public final static boolean WORKFLOW_SEDEX_TO_ARCHIVE_AUTHORIZED = false


  /* Droits */
  public final static String RIGHT_COPY_PAGE = "DOSSIERS_PJ_COPY_PAGE"
  public final static String RIGHT_ADD_PAGE = "DOSSIERS_PJ_ADD_PAGE"
  public final static String RIGHT_DELETE_PAGE = "DOSSIERS_PJ_DELETE_PAGE"


  /* Niveaux de secret */
  public final static Integer SECRET_LEVEL_DEFAULT = 500
  public final static Integer SECRET_LEVEL_CONFIDENTIEL = 600
  public final static Integer SECRET_LEVEL_LFA = 700


  /* Organisation */
  public final static Integer ORGANIZATION_OAI_ID = 1

  /* Content type */
  public final static String CTY_DOCUMENT_ASSURE = "DOCUMENT_ASSURE"
  public final static String CTY_FOLDER_ASSURE = "DOSSIER_ASSURE"

  /* DGD */
  //Année de rétention des documents
  public final static Integer DGD_MINIMAL_RENTETION_YEAR = 15
  //Année de rétention des dossiers
  public final static Integer DGD_MAXIMAL_RENTETION_YEAR = 150
  public final static Integer DGD_MINIMAL_RENTETION_MONTH = 24

  /* Champs AIRS */
  public final static String FIELD_GESTIONNAIRES_CODE = "U_GESTS"
  public final static String FIELD_GESTIONNAIRE_CODE = "U_GEST"
  public final static String FIELD_GESTIONNAIRES_HISTORIQUE_CODE = "U_GESTS_HISTO"
  public final static String FIELD_FLAG_DATE_ARCHIVE_CODE = "D_ARCHIVE"
  public final static String FIELD_NSS_CODE = "N_NSS"
  public final static String FIELD_DEM_CODE = "N_DEM"
  public final static String FIELD_DATE_DOCUMENT_CODE = "D_VALEUR"
  public final static String FIELD_NUMBER_PAGES_CODE = "NOMBRE_PAGES"
  public final static String FIELD_DATE_CREATION_CODE = "D_CREAT"
  public final static String FIELD_NAME_CODE = "NOM"
  public final static String FIELD_DATE_RETENTION_CODE = "D_RETENTION"
  public final static String FIELD_DATE_RETENTION_MODIFY_CODE = "D_RETENTION_MODIF"
  public final static String FIELD_CREATEUR_CODE = "U_CREAT"
  public final static String FIELD_EMETTEUR_CODE = "EMETTEUR"
  public final static String FIELD_DATE_EMISSION_CODE = "D_EMISSION"

  /* Code Liste Autorité */
  // Liste d'autorité des statuts comptabilité
  public final static String LIST_WK_STATUT_CODE = "AL_WKF_STATUT"
  public final static Integer LIST_WK_STATUT_ID = 7
  public final static Integer LIST_WK_STATUT_TRANSFERT_PEC = 7
  public final static Integer LIST_WK_STATUT_SEDEX = 8
  public final static Integer LIST_WK_STATUT_TRANSFERT_DOSSIER = 9
  public final static Integer LIST_WK_STATUT_GEST_INCONNU = 10
  public final static Integer LIST_WK_STATUT_ARCHIVE = 11
  public final static Integer LIST_WK_STATUT_AVERIFICATION_LOT = 12

  // Liste d'autorité des types de documents assuré
  public final static String LIST_TYPES_DOCUMENT_CODE = "AL_ASSURE_TYPE_DOC"
  public final static Integer LIST_TYPES_DOCUMENT_ID = 11

  // Liste d'autorité des groupes de documents assuré
  public final static String LIST_GROUPES_DOCUMENT_CODE = "AL_ASSURE_GROUPE_DOC"
  public final static Integer LIST_GROUPES_DOCUMENT_ID = 10


  /* MAPS */

  /*EXPORT BORDEREAU*/
  public final static String XML_ACTIONS_REQUEST_EXPORT_DEFAULT_DATE = "/configuration/gravage/date_export"
  public final static String XML_ACTIONS_EXPORT_DEFAULT_LANGUAGE = "fr-Français;de-Deutsch;it-Italiano"
  public final static String APPLICATION_EXPORT_FOLDER = "/data/export_gravage/"
  public final static String APPLICATION_TRADUCTION_FILES = "/opt/intairs/apache-tomcat-7.0.64/webapps/AirsDossier/custom/Messages_"

  /* EXPORT GRAVAGE */
  public final static String MODE_EXPORT_GRAVAGE_NORMAL_ID = "1"
  public final static String MODE_EXPORT_GRAVAGE_NORMAL_LBELLE = "xml_configuration_label_gravage_normal"
  public final static String MODE_EXPORT_GRAVAGE_BORDEREAU_ID = "2"
  public final static String MODE_EXPORT_GRAVAGE_BORDEREAU_LIBELLE = "xml_configuration_label_gravage_tb"
  public final static String MODE_EXPORT_BORDEREAU_VISUALISATION_ID = "3"
  public final static String MODE_EXPORT_BORDEREAU_VISUALISATION_LIBELLE = "xml_configuration_label_visualisation"
  public final static String MODE_EXPORT_GRAVAGE_VISUALISATION_BORDEREAU_ID = "4"
  public final static String MODE_EXPORT_GRAVAGE_VISUALISATION_BORDEREAU_LIBELLE = "xml_configuration_label_gravage_tb_visualisation"
  public final static String MODE_EXPORT_EMAIL_ID = "5"
  public final static String MODE_EXPORT_EMAIL_LIBELLE = "xml_configuration_label_envoi_email"
  public final static String MODE_EXPORT_GRAVAGE_VISUALISATION_ID = "6"
  public final static String MODE_EXPORT_GRAVAGE_VISUALISATION_LIBELLE = "xml_configuration_label_gravage_visualisation"
  public final static String MODE_EXPORT_EMAIL_GRAVAGE_ID = "7"
  public final static String MODE_EXPORT_EMAIL_GRAVAGE_LIBELLE = "xml_configuration_label_envoie_email_gravage"


  /* DB AIRS */
  public final static String DB_AIRS_DRIVER = "com.ibm.db2.jcc.DB2Driver"
  public final static String DB_AIRS_URL = "jdbc:db2://ai1022i007s.gilai.oai.ch:52211/INTAIRS"
  public final static String DB_AIRS_USERNAME = "intgddb2"
  public final static String DB_AIRS_PASSWORD = "aW50Z2RkYjI0R2lsYWk="

  //public final static String DB_AIRS_REQUEST_GET_CREATE_DOCUMENTS = "SELECT a.doc_id FROM doc_document_assure a WHERE a.D_CREAT > (current timestamp - 2 minutes) and (NOMBRE_PAGES is NULL OR NOM IS NULL OR NOM = '' OR a.doc_id NOT IN (SELECT doc_id FROM docref_document_assure r INNER JOIN authority_item i ON r.AUI_ID = i.AUI_ID AND i.AUL_ID = 10))";
  public final static String DB_AIRS_REQUEST_GET_CREATE_DOCUMENTS = "SELECT a.doc_id FROM doc_document_assure a WHERE d_creat > (current timestamp - 2000 minutes) AND (NOMBRE_PAGES is NULL OR NOM IS NULL OR NOM = '' OR a.doc_id NOT IN (SELECT doc_id FROM docref_document_assure r INNER JOIN authority_item i ON r.AUI_ID = i.AUI_ID AND i.AUL_ID = 10))"
  public final static String DB_AIRS_REQUEST_GET_DOCUMENTS_WITHOUT_INFORMATIONS = "SELECT a.doc_id FROM doc_document_assure a WHERE NOMBRE_PAGES is NULL OR NOM IS NULL OR NOM = '' OR a.doc_id NOT IN (SELECT doc_id FROM docref_document_assure r INNER JOIN authority_item i ON r.AUI_ID = i.AUI_ID AND i.AUL_ID = 10)"
  public final static String DB_AIRS_REQUEST_GET_MODIFIED_DOCUMENTS = "SELECT distinct a.doc_id FROM evt_airs e INNER JOIN evt_actiondoc a ON e.EVT_ID = a.EVT_ID WHERE (e.tevt_id = 52 or e.tevt_id = 63 or e.tevt_id = 36 or e.tevt_id = 37 or e.tevt_id = 51 or e.tevt_id = 61 or e.tevt_id = 57 ) AND evt_date > (current timestamp - 2 minutes)"
  public final static String DB_AIRS_REQUEST_GET_ALL_DOCUMENTS = "SELECT doc_id from DOC_DOCUMENT_ASSURE where NOMBRE_PAGES is null"
  public final static String DB_AIRS_REQUEST_GET_NEW_LFA_DOCUMENTS = "SELECT doc_id FROM doc_document_assure a WHERE a.S_SECRETLEVEL = 500 AND a.N_DEM = '3333'"
  public final static String DB_AIRS_REQUEST_GET_CONFIDENTIAL_DOCUMENTS = "SELECT distinct n_nss FROM doc_document_assure a WHERE a.S_SECRETLEVEL = 600"
  public final static String DB_AIRS_REQUEST_GET_LFA_DOCUMENTS_INCORRECT = "SELECT distinct n_nss FROM doc_document_assure a WHERE a.S_SECRETLEVEL = 700 AND a.N_DEM != '3333' "
  public final static String DB_AIRS_REQUEST_GET_PASSWORD_FOR_USER = "SELECT USR_PASSWORD FROM USERS WHERE USR_LOGIN='?' "
  public final static String DB_AIRS_REQUEST_UNLOCK_DOCUMENTS = "DELETE FROM doc_lock WHERE CREATED < current timestamp - 20 MINUTES"

  /* Applicatif OAI */

  // Configuration actions personnalisées
  public final static String XML_ACTIONS_CONFIGURATION_PATH = ApplicationUtils.getXMLConfigurationFolderPath() + "/configuration_oai.xml"
  public final static String XML_WEBSERVICES_CONFIGURATION_PATH = ApplicationUtils.getXMLConfigurationFolderPath() + "/ws.properties"

  public final static String XML_ACTIONS_REQUEST_BURNS_TITLE = "//graveurs/graveur/libelle"
  public final static String XML_ACTIONS_REQUEST_BURNS_FILTRES_TITLE_ALL = "//gravage/filtres/filtre/libelle"
  public final static String XML_ACTIONS_REQUEST_BURNS_FILTRES_TITLE_WITHOUT_SELECTED_DOCUMENTS = "//gravage/filtres/filtre[@dossier_complet='1']/libelle"
  public final static String XML_ACTIONS_REQUEST_BURNS_PASSWORD_ADMINISTRATOR = "//gravage/admin_password"
  public final static String XML_ACTIONS_REQUEST_BURNS_PASSWORD_OWNER = "//gravage/owner_password"

  public final static String XML_ACTIONS_REQUEST_EXPORT_WEBAI_TITLE_ALL = "//export_webai/filtres/filtre/libelle"
  public final static String XML_ACTIONS_REQUEST_EXPORT_WEBAI_TITLE_WITHOUT_SELECTED_DOCUMENTS = "//export_webai/filtres/filtre[@dossier_complet='1']/libelle"
  public final static String XML_ACTIONS_REQUEST_EXPORT_WEBAI_IS_EXPORT_FOLDER_BY_ID = "//export_webai/filtres/filtre[@id='##ID##'][@dossier_complet='1']"
  public final static String XML_ACTIONS_REQUEST_EXPORT_WEBAI_FILTERS_BY_ID_CODE = "//export_webai/filtres/filtre[@id='##ID##']/code"
  public final static String XML_ACTIONS_REQUEST_EXPORT_WEBAI_FILTERS_FIELD = "//export_webai/filtres/champ_code"
  public final static String XML_ACTIONS_REQUEST_EXPORT_WEBAI_WITH_EXPORT_NOT_PDF = "//export_webai/filtres/filtre[@id='##ID##'][@with_no_pdf='0']"

  public final static String XML_ACTIONS_REQUEST_EXPORT_FILE_FILTERS = "//filtre/code/text()"
  public final static String XML_ACTIONS_REQUEST_EXPORT_FILE_FILTER_FIELD = "//filtre/champ_code/text()"
  public final static String XML_ACTIONS_REQUEST_EXPORT_FILE_SUMMARY = "//sommaire/code/text()"
  public final static String XML_ACTIONS_REQUEST_EXPORT_FILE_COLUMN_SORTED = "//sommaire/colonneTri/code/text()"
  public final static String XML_ACTIONS_REQUEST_EXPORT_FILE_OPTION_SORTED = "//sommaire/colonneTri/ordre/text()"
  public final static String XML_ACTIONS_REQUEST_EXPORT_FILE_FILTERS_INCLUDED = "//inclus/text()"
  public final static String XML_ACTIONS_REQUEST_EXPORT_FILE_PROFILS = "//export_documents/profil/text()"

  public final static String XML_ACTIONS_REQUEST_GLOBAL_USE_NDEM = "//global/champ_demande_utilise"
  public final static String XML_ACTIONS_REQUEST_GLOBAL_DEFAULT_NDEM = "//global/defaut_champ_demande"
  public final static String XML_ACTIONS_REQUEST_NDEM_LIST = "//global/liste_numeros_demande"

  public final static String XML_ACTIONS_REQUEST_INDEXATION_DOCUMENTS = "//indexation/champs"

  public final static String XML_WEBAI_REQUEST_EXPORT_WEBAI_DEPOSIT_FOLDER_PATH = "//dossierDepot"
  public final static String XML_WEBAI_REQUEST_EXPORT_WEBAI_CSV_FILE_PATH = "//cheminCsv"
  public final static String XML_WEBAI_REQUEST_EXPORT_WEBAI_XML_FILE_PATH = "//cheminXml"
  public final static String XML_WEBAI_REQUEST_EXPORT_WEBAI_LOT_ID = "//idLot"
  public final static String XML_WEBAI_REQUEST_EXPORT_WEBAI_OFFICE = "//office"
  public final static String XML_WEBAI_REQUEST_EXPORT_WEBAI_UID = "//uid"

  public final static String CSV_SEPARATOR = ";"

  // Applications
  public final static String[] APPLICATION_OFFICE_WORD_EXTENSION = [".DOC", ".DOCX"]
  public final static String[] APPLICATION_OFFICE_EXCEL_EXTENSION = [".XLS", ".XLSX"]
  public final static String[] APPLICATION_OFFICE_POWERPOINT_EXTENSION = [".PPT", ".PPTX"]
  public final static String[] APPLICATION_TIF_EXTENSION = [".TIF", ".TIFF"]
  public final static String APPLICATION_PDF_EXTENSION = ".PDF"
  public final static String APPLICATION_DAT_EXTENSION = ".DAT"
  public final static String APPLICATION_JDF_EXTENSION = ".JDF"
  public final static String APPLICATION_OPENOFFICE_HOST = "localhost"
  public final static String APPLICATION_OPENOFFICE_PORT = "8100"
  public final static String APPLICATION_PERSONNAL_SPACE_CODE = "PERSONNAL_SPACE"
  public final static String APPLICATION_AIRSDOSSIER_URL = "https://eduairs7.gilai.oai.ch/AirsDossier/"
  public final static String APPLICATION_AIRSDOSSIER_HOSTNAME = "ai1022i007s"
  public final static String APPLICATION_WEBAI_FOLDER = "/opt/intairs/tmp/"
  public final static String APPLICATION_AIRSDOSSIER_FOLDER = "/opt/intairs/apache-tomcat-7.0.64/webapps/AirsDossier/"
  public final static String APPLICATION_BURN_FOLDER = "H:\\AppRepository\\GedToDVD"
  public final static String APPLICATION_BURN_TYPE = ""
  public final static String APPLICATION_AIRSSERVEUR_HOST = "localhost"
  public final static String APPLICATION_LICENCE_ASPOSE_PDF = "/opt/intairs/apache-tomcat-7.0.64/webapps/AirsDossier/xml/Aspose.Pdf.lic"

  //Viewer
  public final static String APPLICATION_VIEWER_CODE = "DOCUMENTVIEWER_FILE_EXTENSIONS"
  public final static String APPLICATION_VIEWER_ADOBE_VALUE = "tif tiff png gif jpeg jpe jpg bmp doc docx xls xlsx ppt pptx odt ods odp odg rtf"
  public final static String APPLICATION_VIEWER_DIGITECH_VALUE = "pdf tif tiff png gif jpeg jpe jpg bmp doc docx xls xlsx ppt pptx odt ods odp odg rtf"

  //Export
  public final static String APPLICATION_EXPORT_PDF_HEADER = "/opt/intairs/apache-tomcat-7.0.64/webapps/AirsDossier/custom/ui/img/entete.pdf"
  public final static String APPLICATION_EXPORT_PDF_ERROR_PATH = "/opt/intairs/apache-tomcat-7.0.64/webapps/AirsDossier/custom/ui/img/"
  public final static String APPLICATION_EXPORT_PDF_ERROR_FILE_RIGHTS = "erreur_droitUser.pdf"
  public final static String APPLICATION_EXPORT_PDF_ERROR_FILE_SUBFOLDER = "erreur_sousDossier.pdf"
}