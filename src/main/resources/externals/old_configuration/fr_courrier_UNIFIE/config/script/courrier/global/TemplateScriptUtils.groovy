
import org.apache.commons.lang.StringUtils;

import com.digitech.dossier.administration.model.backend.Report;
import com.digitech.dossier.common.model.backend.airs.IField;
import com.digitech.dossier.common.model.backend.report.ReportDocument;
import com.digitech.dossier.common.model.backend.report.ReportFieldComplexValueType;
import com.digitech.dossier.common.model.backend.report.ReportFieldValue;
import com.digitech.dossier.common.model.backend.report.value.IReportAddress;
import com.digitech.dossier.common.model.backend.report.value.IReportOrganization;
import com.digitech.dossier.common.model.backend.report.value.IReportPerson;
import com.digitech.dossier.common.model.backend.report.value.IReportPersonOrganizationRelation;

import static CourrierScriptUtils

/**
 * Utility methods for courrier light templates
 */


public String getEmetteur(ReportDocument reportDocument) {
  ReportFieldValue fieldEmetteur = reportDocument.getReportFieldsMap().get(CourrierScriptUtils.getConstant("FIELD_CODE_C_EMETTEUR"));
  return getCorrespondant(fieldEmetteur);
}

public String getDestinataire(ReportDocument reportDocument) {
  ReportFieldValue fieldDestinataire = reportDocument.getReportFieldsMap().get(CourrierScriptUtils.getConstant("FIELD_CODE_C_DESTINATAIRE"));
  return getCorrespondant(fieldDestinataire);
}

public String getProprietaire(ReportDocument reportDocument) {
  ReportFieldValue fieldProprietaire = reportDocument.getReportFieldsMap().get(CourrierScriptUtils.getConstant("FIELD_CODE_U_PROPRIETAIRE"));
  if (fieldProprietaire.getComplexValue() == null) {
    fieldProprietaire = reportDocument.getReportFieldsMap().get(CourrierScriptUtils.getConstant("FIELD_CODE_O_PROPRIETAIRE"));
  }
  return getCorrespondant(fieldProprietaire);
}

public String getCorrespondant(ReportFieldValue reportFieldValue) {
  
  final String PROPERTY_KEY_APPELLATION   = "FORMAT_ODT_APPELLATION";
  final String PROPERTY_KEY_FIRSTNAME     = "FORMAT_ODT_FIRSTNAME";
  final String PROPERTY_KEY_LASTNAME      = "FORMAT_ODT_LASTNAME";
  final String PROPERTY_KEY_ORGA_NAME     = "FORMAT_ODT_ORGA_NAME";
  final String PROPERTY_KEY_FUNCTION      = "FORMAT_ODT_FUNCTION";
  final String PROPERTY_KEY_SERVICE       = "FORMAT_ODT_SERVICE";
  
  ReportFieldComplexValueType complexValueType =  reportFieldValue.getComplexValueType();
  Object complexValue = reportFieldValue.getComplexValue();
  if (complexValue != null) {
    String fullName = "";
    String fullAddress = "";

    if (complexValueType.equals(ReportFieldComplexValueType.PERSON)) {
      IReportPerson reportPerson  = (IReportPerson)complexValue;

      if(reportPerson.getTitle() != null){
        fullName = fullName + formatOutputText(reportPerson.getTitle(),PROPERTY_KEY_APPELLATION) + " ";
      }
      else if (reportPerson.getAppellation() != null) {
        fullName = fullName + formatOutputText(reportPerson.getAppellation(),PROPERTY_KEY_APPELLATION) + " ";
      }

      if (reportPerson.getFirstName() != null) {
        fullName = fullName + formatOutputText(reportPerson.getFirstName(),PROPERTY_KEY_FIRSTNAME) + " ";
      }

      if (reportPerson.getLastName() != null) {
        fullName = fullName + formatOutputText(reportPerson.getLastName(),PROPERTY_KEY_LASTNAME) + " ";
      }

      fullName = StringUtils.removeEnd(fullName, " ") + "\n";
      fullAddress = getAddress(reportPerson.getPersonalAddress());

      IReportPersonOrganizationRelation reportPersonOrgRel = reportPerson.getSelectedPersonOrganizationRelation();
      if (reportPersonOrgRel != null) {
        String relation = "";

        if (reportPersonOrgRel.getFunction() != null) {
          relation = relation + formatOutputText(reportPersonOrgRel.getFunction(),PROPERTY_KEY_FUNCTION) + " ";
        }
        if (reportPersonOrgRel.getOrganization() != null) {
          relation = relation + formatOutputText(reportPersonOrgRel.getOrganization().getName(),PROPERTY_KEY_ORGA_NAME) + " ";
        }

        if (StringUtils.isNotBlank(relation)) {
          fullName = fullName + StringUtils.removeEnd(relation, " ") + "\n";
        }

        fullAddress = "";
        if (reportPersonOrgRel.getOrganization().getAddress() != null) {
          fullAddress = getAddress(reportPersonOrgRel.getOrganization().getAddress());
        }
      }
    }
    else if (complexValueType.equals(ReportFieldComplexValueType.ORGANIZATION)) {
      IReportOrganization reportOrganization  = (IReportOrganization)complexValue;
      if (reportOrganization.getName() != null) {
        fullName = fullName + formatOutputText(reportOrganization.getName(),PROPERTY_KEY_ORGA_NAME) + "\n";
      }
      String service = reportOrganization.getService();
      if(service!=null){
        fullName = fullName +formatOutputText(service, PROPERTY_KEY_SERVICE)+ "\n";
      }
      fullAddress = getAddress(reportOrganization.getAddress());
    }
    return fullName + fullAddress;
  }
  return "";
}

public String getAddress(IReportAddress reportAddress) {
  final String PROPERTY_KEY_ADDRESS    = "FORMAT_ODT_ADDRESS";
  final String PROPERTY_KEY_CITY    = "FORMAT_ODT_CITY";
  final String PROPERTY_KEY_COUNTRY    = "FORMAT_ODT_COUNTRY";
  String address = "";

  if (reportAddress != null) {
    if (reportAddress.getExtra() != null) {
      address = address + formatOutputText(reportAddress.getExtra(),PROPERTY_KEY_ADDRESS) + "\n";
    }

    if (reportAddress.getRoadNumber() != null) {
      address = address + reportAddress.getRoadNumber() + " ";
    }
    if (reportAddress.getRoadName() != null) {
      address = address + formatOutputText(reportAddress.getRoadName(),PROPERTY_KEY_ADDRESS) + "\n";
    }
    
    if (reportAddress.getExtra2() != null && !reportAddress.getExtra2().isEmpty()) {
      address = address + formatOutputText(reportAddress.getExtra2(),PROPERTY_KEY_ADDRESS) + "\n";
    }

    if (reportAddress.getPostalCode() != null) {
      address = address + reportAddress.getPostalCode() + " ";
    }
    if (reportAddress.getCity() != null) {
      address = address + formatOutputText(reportAddress.getCity(),PROPERTY_KEY_CITY);
    }
    if (StringUtils.isNotBlank(reportAddress.getCedex())) {
      String cedex = StringUtils.trim(reportAddress.getCedex());
      if (!StringUtils.startsWithIgnoreCase(cedex, "CEDEX")) {
        cedex = "CEDEX " + cedex;
      }
      address = address + " " + cedex;
    }
    address = address + "\n";

    if (reportAddress.getCountry() != null) {
      address = address + formatOutputText(reportAddress.getCountry(),PROPERTY_KEY_COUNTRY) + "\n";
    }
  }
  return address;
}

public String formatOutputText(String text, key){
    
  if(text == null || key == null){ 
    return text;
  }

  String format = CourrierScriptUtils.getConstant(key);
  if (format.equals("UPPERCASE")) {
    return text.toUpperCase();
  }
  if (format.equals("LOWERCASE")){
   return text.toLowerCase();
  }
  if (format.equals("CAPITALIZE")) {
    return StringUtils.capitalize(text.toLowerCase());
  }
  return text;
}
