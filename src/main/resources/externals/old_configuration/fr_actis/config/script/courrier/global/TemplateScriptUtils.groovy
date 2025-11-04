
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
  ReportFieldComplexValueType complexValueType =  reportFieldValue.getComplexValueType();
  Object complexValue = reportFieldValue.getComplexValue();
  if (complexValue != null) {
    String fullName = "";
    String fullAddress = "";

    if (complexValueType.equals(ReportFieldComplexValueType.PERSON)) {
      IReportPerson reportPerson  = (IReportPerson)complexValue;

      if (reportPerson.getAppellation() != null) {
        fullName = fullName + reportPerson.getAppellation() + " ";
      }

      if (reportPerson.getFirstName() != null) {
        fullName = fullName + StringUtils.capitalize(reportPerson.getFirstName()) + " ";
      }

      if (reportPerson.getLastName() != null) {
        fullName = fullName + StringUtils.upperCase(reportPerson.getLastName()) + " ";
      }

      fullName = StringUtils.removeEnd(fullName, " ") + "\n";
      fullAddress = getAddress(reportPerson.getPersonalAddress());

      IReportPersonOrganizationRelation reportPersonOrgRel = reportPerson.getSelectedPersonOrganizationRelation();
      if (reportPersonOrgRel != null) {
        String relation = "";

        if (reportPersonOrgRel.getFunction() != null) {
          relation = relation + reportPersonOrgRel.getFunction() + " ";
        }
        if (reportPersonOrgRel.getOrganization() != null) {
          relation = relation + reportPersonOrgRel.getOrganization().getName() + " ";
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
        fullName = fullName + reportOrganization.getName() + "\n";
      }
      fullAddress = getAddress(reportOrganization.getAddress());
    }
    return fullName + fullAddress;
  }
  return "";
}
  
public String getAddress(IReportAddress reportAddress) {
  String address = "";

  if (reportAddress != null) {
    if (reportAddress.getExtra() != null) {
      address = address + reportAddress.getExtra() + "\n";
    }

    if (reportAddress.getRoadNumber() != null) {
      address = address + reportAddress.getRoadNumber() + " ";
    }
    if (reportAddress.getRoadName() != null) {
      address = address + reportAddress.getRoadName() + "\n";
    }

    if (reportAddress.getPostalCode() != null) {
      address = address + reportAddress.getPostalCode() + " ";
    }
    if (reportAddress.getCity() != null) {
      address = address + reportAddress.getCity() + "\n";
    }

    if (reportAddress.getCountry() != null) {
      address = address + reportAddress.getCountry() + "\n";
    }
  }
  return address;
}
