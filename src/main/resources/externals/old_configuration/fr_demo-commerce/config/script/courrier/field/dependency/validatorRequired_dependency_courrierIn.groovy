import java.util.*

import org.apache.commons.lang.*
import org.slf4j.Logger

import com.digitech.courrier.common.model.backend.CourrierConstants.FlowType
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IField
import com.digitech.dossier.common.model.backend.params.CourrierOrga
import com.digitech.dossier.common.utils.FieldUtils
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

logger.debug("Script field dependency: validatorRequired_courrierIn.groovy --- Start");

boolean required = false;

try {
  // On récupère le type du courrier
  Term term = CourrierScriptUtils.getAuthorityListService().getTerm((Integer) updatedField.getValue());
  if (term != null) {
    com.digitech.dossier.common.model.backend.params.CourrierType courrierType = DossierCoreContext.getCourrierInfos().getCourrierType(usrContext.getCurrentOrgId(), term.getCode(), FlowType.IN);
    if (courrierType != null) {
      // On regarde si un validator est requis
      required= courrierType.isValidatorRequired();
    }
  }
} catch (IdentificationException e) {
  logger.error(e.getMessage(),e);
} catch (ServerException e) {
  logger.error(e.getMessage(),e);
}

FieldUtils.setRequired(theFieldToUpdate, required, true);

logger.debug("Script field dependency: validatorRequired_courrierIn.groovy --- End");

