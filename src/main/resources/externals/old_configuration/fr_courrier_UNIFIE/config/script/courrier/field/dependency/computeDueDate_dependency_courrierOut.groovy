import java.util.*

import javax.faces.component.UIComponent

import org.apache.commons.lang.*
import org.slf4j.Logger

import com.digitech.courrier.common.model.backend.CourrierConstants.FlowType
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IField
import com.digitech.dossier.common.model.backend.params.CourrierOrga
import com.digitech.dossier.common.model.backing.AbstractFormLocutionModel
import com.digitech.jcorbairs.Term
import com.digitech.jcorbairs.exception.IdentificationException
import com.digitech.jcorbairs.exception.ServerException

import static CourrierScriptUtils

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// IField updatedField le champ de référence pour la dépendance
// IField fieldToUpdate le champ à mettre a jour
/************************************************/

UserContext usrContext = userContext;
org.slf4j.Logger logger = scriptLogger;
IField theFieldToUpdate = fieldToUpdate;
IField theUpdatedField = updatedField;

logger.debug("Script field dependency: computeDueDate_dependency_courrierOut.groovy --- Start");

try {
  // On récupère le type du courrier
  Term term = CourrierScriptUtils.getAuthorityListService().getTerm((Integer) updatedField.getValue());
  if (term != null) {
    com.digitech.dossier.common.model.backend.params.CourrierType courrierType = DossierCoreContext.getCourrierInfos().getCourrierType(usrContext.getCurrentOrgId(), term.getCode(), FlowType.OUT);
    if (courrierType != null) {
      String rule = courrierType.getDueDateComputingRule();
      Date dueDate = CourrierScriptUtils.computeDueDate(usrContext.getCurrentDocument(),rule);
      theFieldToUpdate.setValue(dueDate);
      ((UIComponent)theFieldToUpdate.getComponent()).getAttributes().put(AbstractFormLocutionModel.KEY_ENABLE_EFFECT, Boolean.TRUE);
    }
  }
} catch (IdentificationException e) {
  logger.error(e.getMessage(),e);
} catch (ServerException e) {
  logger.error(e.getMessage(),e);
}

logger.debug("Script field dependency: computeDueDate_dependency_courrierOut.groovy --- End");