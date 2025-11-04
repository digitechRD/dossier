import java.util.*

import javax.faces.component.UIComponent

import org.apache.commons.lang.*
import org.slf4j.Logger

import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IField
import com.digitech.dossier.common.model.backing.AbstractFormLocutionModel

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
IField updatedField = updatedField;

logger.debug("Script field dependency: generateChronoNumber_dependency_courrierOut.groovy --- Start");

String numChrono = CourrierScriptUtils.generateNumChrono(usrContext.getCurrentOrgId(), usrContext, true);
if (numChrono != null){
  theFieldToUpdate.setValue(numChrono);
  logger.debug("generate_TEMP_NumChrono=[" + numChrono + "]");
  ((UIComponent)theFieldToUpdate.getComponent()).getAttributes().put(AbstractFormLocutionModel.KEY_ENABLE_EFFECT, Boolean.TRUE);
}


logger.debug("Script field dependency: generateChronoNumber_dependency_courrierOut.groovy --- End");