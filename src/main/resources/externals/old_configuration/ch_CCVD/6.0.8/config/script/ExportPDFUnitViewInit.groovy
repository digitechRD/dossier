import ooo.connector.BootstrapSocketConnector;

import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.model.backend.airs.IAttachment;
import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.service.ServiceManager;
import com.digitech.dossier.common.service.export.impl.PdfExport;

import com.digitech.airs3dossiers.airs.AirsDocument;
import com.digitech.airs3dossiers.airs.AirsFolder;

import com.sun.star.connection.NoConnectException;
import com.sun.star.comp.helper.BootstrapException;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.bridge.XUnoUrlResolver;
import com.sun.star.connection.ConnectionSetupException;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.frame.XStorable;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.lang.XComponent;
import com.sun.star.util.XCloseable;
import com.sun.star.beans.PropertyValue;

import Constants;
import Methods

import java.text.DateFormat
import java.text.SimpleDateFormat;
import java.util.*;

/**************************************************************************************************
 *							          Export fichier au format PDF - INIT
 **************************************************************************************************
 Date : 04.11.2014
 Auteur : MTO

 Description : Permet l'export en téléchargement d'un fichier au format PDF
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - EXPORT PDF UNIT VIEW INIT - START");

/**
 * INITIALISATION
 **************************************************************************************************/
CustomActionController customActionController = null;
Map<String, Object> data = null;
XComponentLoader xCompLoader = null;
File genFolder = null;
DateFormat formatter = null;
List<String> pdfs = new ArrayList<String>();
List<String> filteredType = new ArrayList<String>();
String outFolder = null;
String relativeOutFolder = null;

try {
    customActionController = Utils.getCustomActionController();
    data = customActionController.getModel().getModalPanelModel();

    formatter = new SimpleDateFormat("yyyyMMddhhmmssS");

}catch(Exception e){
    Methods.addStateMessage(data, "DATA_ERROR_MSG", "Erreur lors de l'initialisation du traitement. Veuillez contacter votre administrateur", false);
    scriptLogger.error("[CUSTOM ACTION] - ExportPDFUnitViewInit - ERREUR : ",e);
    return;
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try{
    String tempFolderId = formatter.format(new Date());
    genFolder = new File(userContext.getInstance().getUserDownloadPath()+File.separator+tempFolderId+File.separator);
    genFolder.mkdir();
    outFolder = userContext.getInstance().getUserDownloadPath()+File.separator+tempFolderId+File.separator;
    relativeOutFolder = userContext.getInstance().getUserDownloadRelativePath()+File.separator+tempFolderId+File.separator;
    filteredType.add("AMFL1W1EXP");
    String oooAcceptOption = "-accept=socket,host="+Constants.APPLICATION_OPENOFFICE_HOST+",port="+Constants.APPLICATION_OPENOFFICE_PORT+";urp;";
    String oooConnectionString = "uno:socket,host="+Constants.APPLICATION_OPENOFFICE_HOST+",port="+Constants.APPLICATION_OPENOFFICE_PORT+";urp;StarOffice.ComponentContext";

    getAttachments(document,outFolder,files,filteredType);

    XComponentContext xContext = BootstrapSocketConnector.bootstrap(Constants.PATH_APPLICATION_OPENOFFICE);
    XComponentContext xLocalContext = xContext;
    XComponentContext compCont = xContext;
    XMultiComponentFactory xMCF = compCont.getServiceManager();
    Object oDesktop = xMCF.createInstanceWithContext("com.sun.star.frame.Desktop", compCont);
    xCompLoader = (XComponentLoader)UnoRuntime.queryInterface(XComponentLoader.class, oDesktop);
    String outText="";
    for(File file:files) {
        if (isPDF(file.getName())) {
            outText += "<br>" + file.getName() + " inclus dans le pdf </br>";
            pdfs.add(file.getAbsolutePath());
        } else if (isKnown(file.getName())) {
            outText += "<br>" + file.getName() + " inclus dans le pdf </br>";
            pdfs.add(convertToPdf(xCompLoader, file, new File(outFolder)).getAbsolutePath());
        } else {
            outText += "<br>" + file.getName() + " non inclus dans le PDF - format non pris en compte </br>";
        }

        PdfExport exporter = new PdfExport();
        OutputStream fo = null;
        try {
            fo = new FileOutputStream(outFolder + File.separator + "ExportedFolder.pdf");
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        exporter.concatPDFs(pdfs, fo, false)

        data.put("file", relativeOutFolder + "ExportedFolder.pdf");
        data.put("text", outText);
        data.put("folder", genFolder);
    }

}catch(Exception e){
    Methods.addStateMessage(data, "DATA_ERROR_MSG", "Erreur lors de l'initialisation du traitement. Veuillez contacter votre administrateur", false);
    scriptLogger.error("[CUSTOM ACTION] - ExportPDFUnitViewInit - ERREUR : ",e);
    return;
}

scriptLogger.debug("[CUSTOM ACTION] - EXPORT PDF UNIT VIEW INIT - END");



/**
 * METHODES
 **************************************************************************************************/

private com.digitech.dossier.common.service.IDocument getDocumentMgr() {
  return (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_DOCUMENT_MGR);
}

private XComponentContext getLocalContext()
throws BootstrapException, Exception
   {
     XComponentContext xLocalContext = Bootstrap.bootstrap();
     if (xLocalContext == null) {
         System.err.println("no local component context!");
     }

     return xLocalContext;
   }
   
private XComponentContext getRemoteContext(XUnoUrlResolver xUrlResolver,String oooConnectionString)
    throws BootstrapException, ConnectionSetupException, IllegalArgumentException, NoConnectException
   {
     Object context = xUrlResolver.resolve(oooConnectionString);
   XComponentContext xContext = (XComponentContext)UnoRuntime.queryInterface(XComponentContext.class, context);
   if (xContext == null) {
      System.err.println("no component context!");
     }
     return xContext;
  }
private boolean isPDF(String name){
  String ext=name.substring(name.lastIndexOf(".")+1,name.length()).toLowerCase();
  return ext.equals("pdf");
}   
private boolean isWriter(String name){
  String ext=name.substring(name.lastIndexOf(".")+1,name.length()).toLowerCase();  
  Set<String> map=new HashSet<String>();
  map.add("odt");
  map.add("ott");
  map.add("sxw");
  map.add("stw");
  map.add("doc");
  map.add("dot");
  map.add("rtf");
  map.add("txt");
  map.add("docx");
  map.add("docm");
  map.add("dotx");
  map.add("dotm");
  map.add("602");
  map.add("wpd");
  map.add("hwp");  
  return map.contains(ext);               
}
private boolean isWebWriter(String name){
      
  String ext=name.substring(name.lastIndexOf(".")+1,name.length()).toLowerCase();
  Set<String> map=new HashSet<String>();
  map.add("html");
  map.add("htm");
  map.add("oth");
  return map.contains(ext);
}
private boolean isCalc(String name){
  String ext=name.substring(name.lastIndexOf(".")+1,name.length()).toLowerCase();
  Set<String> map=new HashSet<String>();
  map.add("ods");
  map.add("ots");
  map.add("sxc");
  map.add("dif");
  map.add("dbf");
  map.add("xls");
  map.add("xlc");
  map.add("xlm");
  map.add("xlt");
  map.add("slk");
  map.add("csv");
  map.add("xlsb");
  map.add("xlsm");
  map.add("xlsx");
  map.add("wk1");
  map.add("wks");
  map.add("123");
  map.add("wb2");   
  return map.contains(ext);                 
    
}
private boolean isDraw(String name){                               
  String ext=name.substring(name.lastIndexOf(".")+1,name.length()).toLowerCase();
  Set<String> map=new HashSet<String>();
  map.add("odg");
  map.add("otg");
  map.add("sxd");
  map.add("std");
  map.add("dxf");
  map.add("emf");
  map.add("eps");
  map.add("met");
  map.add("pct");
  map.add("pict");
  map.add("sgf");
  map.add("sgv");
  map.add("wmf");
  map.add("bmp");
  map.add("gif");
  map.add("jpg");
  map.add("jpeg");
  map.add("pbm");
  map.add("pcx");
  map.add("png");
  map.add("pgm");
  map.add("ppm");
  map.add("ras");
  map.add("psd");
  map.add("tga");
  map.add("tif");
  map.add("tiff");
  map.add("xbm");
  map.add("xpm");
  map.add("pcd");
  return map.contains(ext);
}
private boolean isImpress(String name){
  String ext=name.substring(name.lastIndexOf(".")+1,name.length()).toLowerCase();
  Set<String> map=new HashSet<String>();
  map.add("otp");
  map.add("odp");
  map.add("sxi");
  map.add("sti");
  map.add("ppt");
  map.add("pps");
  map.add("cgm");
  map.add("pptm");
  map.add("pptx");
  map.add("potm");
  return map.contains(ext);         
}
private boolean isKnown(String name){
  return isWriter(name)||isWebWriter(name)||isCalc(name)||isDraw(name)||isImpress(name);
}
private File convertToPdf(XComponentLoader xCompLoader, File input,File output ) {
  String sConvertType="writer_globaldocument_pdf_Export";
  if(isWriter(input.getName())){
   sConvertType="writer_pdf_Export";}
  if(isWebWriter(input.getName())){
     sConvertType="writer_web_pdf_Export";}
  if(isCalc(input.getName())){
     sConvertType="calc_pdf_Export";}
  if(isDraw(input.getName())){
     sConvertType="draw_pdf_Export";}
  if(isImpress(input.getName())){
     sConvertType="impress_pdf_Export";}
  
  String sExtension="pdf";
  String sOutUrl = "file:///" + output.getAbsolutePath().replace( '\\', '/' );
  System.out.println("\nThe converted documents will stored in \"" + output.getPath());
  System.out.println( "[" + input.getName() + "]");
  try {
    String sUrl ="file:///" +  input.getAbsolutePath().replace( '\\', '/' ).replaceAll(" ", "%20");
    System.out.println("sUrl: "+sUrl);
    
    PropertyValue[] propertyValues = new PropertyValue[1];
    propertyValues[0] = new PropertyValue();
    propertyValues[0].Name = "Hidden";
    propertyValues[0].Value = new Boolean(true);
    Object oDocToStore = xCompLoader.loadComponentFromURL( sUrl, "_blank", 0,propertyValues);
    XStorable xStorable = (XStorable)UnoRuntime.queryInterface(XStorable.class, oDocToStore );
  
    propertyValues = new PropertyValue[3];    
    propertyValues[0] = new PropertyValue();
    propertyValues[0].Name = "Overwrite";
    propertyValues[0].Value = new Boolean(true);    
    propertyValues[1] = new PropertyValue();
    propertyValues[1].Name = "FilterName";
    propertyValues[1].Value = sConvertType;
    propertyValues[2] = new PropertyValue();
    propertyValues[2].Name = "CompressionMode";
    propertyValues[2].Value = "1";    
    
    int index1 = sUrl.lastIndexOf('/');
    int index2 = sUrl.lastIndexOf('.');
    String sStoreUrl = sOutUrl + sUrl.substring(index1, index2 + 1) + sExtension;
    System.out.println("sStoreUrl: "+sStoreUrl);
    xStorable.storeToURL(sStoreUrl, propertyValues);
    System.out.println(sStoreUrl);

    XCloseable xCloseable = (XCloseable)UnoRuntime.queryInterface(XCloseable.class, xStorable);

    if ( xCloseable != null ) {
      xCloseable.close(false);
    } else {
      XComponent xComp = (XComponent)UnoRuntime.queryInterface(XComponent.class, xStorable);
      xComp.dispose();
    }
  }
  catch( Exception e ) {
    e.printStackTrace(System.err);
  }
  return new File(output.getAbsolutePath()+File.separator+input.getName().substring(0,input.getName().lastIndexOf("."))+".pdf");
}

void getAttachments(IDocument doc,String outFolder,List<File> files,List<String>filteredType){
    if(doc.isFolder()){
        AirsFolder airsFolder = (AirsFolder) doc.getAirsDocument();
        for(AirsDocument docChild : airsFolder.getChildList()){
            getAttachments(Methods.getDocumentMgr().getDocument(userContext.getJeton(),docChild.getId()),outFolder,files,filteredType);
        }
    }else if(!filteredType.contains(doc.getFieldMap().get(Constants.LIST_TYPE_CODE))){
        for(IAttachment attachment:doc.getAttachment(userContext)){
            files.add(Methods.getDocumentMgr().loadDocumentAttachment(userContext, doc, attachment, outFolder));
        }
    }
}
