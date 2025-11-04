import java.lang.*;
import java.util.*;
import javax.faces.*;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.apache.commons.lang.*;
import com.digitech.dossier.common.model.backend.params.UpdateField;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backend.airs.IField;
import com.digitech.dossier.common.service.ServiceManager;
import com.digitech.dossier.common.service.IServer;
import com.digitech.dossier.common.Utils;

import com.digitech.dossier.common.*;

import java.lang.Double;
import com.digitech.dossier.common.service.ServiceManager;
import com.digitech.dossier.common.service.IServer;

import com.digitech.dossier.common.model.backing.factory.*;
import javax.faces.component.html.*;
import javax.faces.component.*;
import javax.faces.model.*;


DOC_TYPE_FACT_CODE = "FACT_TYPE_DOC"
DOC_TYPE_FACT_ID = "1602"

DOC_FACT_HT_CODE = "FACT_HT"
DOC_FACT_TVA55_CODE = "FACT_TVA55"
DOC_FACT_TVA196_CODE = "FACT_TVA196"
DOC_FACT_TTC_CODE = "FACT_TTC"


  IField fieldType_Doc = fieldMap.get(DOC_TYPE_FACT_CODE);  
  String fieldTypeDocValue = getFieldValue( fieldType_Doc );
	
  scriptLogger.warn("Valeur du type doc : " + fieldTypeDocValue );
  

  IField fieldHT_Doc = fieldMap.get(DOC_FACT_HT_CODE);  
  IField fieldTVA55_Doc = fieldMap.get(DOC_FACT_TVA55_CODE);  
  IField fieldTVA196_Doc = fieldMap.get(DOC_FACT_TVA196_CODE);  
  IField fieldTTC_Doc = fieldMap.get(DOC_FACT_TTC_CODE);  
    
  if ( StringUtils.isEmpty(fieldTypeDocValue) )
  {
    ((UpdateField)fieldHT_Doc).setDisplayed( false ); 
    ((UpdateField)fieldTVA55_Doc).setDisplayed( false ); 
    ((UpdateField)fieldTVA196_Doc).setDisplayed( false ); 
    ((UpdateField)fieldTTC_Doc).setDisplayed( false ); 
  }
  else
  {
    if ( fieldTypeDocValue.compareToIgnoreCase(DOC_TYPE_FACT_ID) == 0 )
    {
      ((UpdateField)fieldHT_Doc).setDisplayed( false ); 
      ((UpdateField)fieldTVA55_Doc).setDisplayed( false ); 
      ((UpdateField)fieldTVA196_Doc).setDisplayed( false ); 
      ((UpdateField)fieldTTC_Doc).setDisplayed( false ); 
    }
    else
    {
      ((UpdateField)fieldHT_Doc).setDisplayed( true ); 
      ((UpdateField)fieldTVA55_Doc).setDisplayed( true ); 
      ((UpdateField)fieldTVA196_Doc).setDisplayed( true ); 
      ((UpdateField)fieldTTC_Doc).setDisplayed( true ); 
    }
  }
  


String getFieldValue( IField field )
{
	List<?> values = field.getValues();
	String fieldvalue;
	if(values != null && !values.isEmpty()) {
		fieldvalue =getServerMgr().getFieldValues(values, field.getAirsField());   
		scriptLogger.info("Field Value : " + field.getCode() + " - " + fieldvalue );
	}else
	{
		scriptLogger.info("the field value is null or empty");
	}
	
	return fieldvalue;
}

private IServer getServerMgr() {
	return (IServer) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_SERVER_MGR);
}

private com.digitech.dossier.common.service.IDocument getDocumentMgr() {
	return (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_DOCUMENT_MGR);
}
