import java.util.*

import org.apache.commons.lang.*
import org.slf4j.Logger

import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.airs.IField;

import static CourrierScriptUtils;

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

logger.debug("Script field dependency: validatorRequired_courrierIn.groovy --- Start");

CourrierScriptUtils.changeRequired_U_VALIDEURS(usrContext, logger, theUpdatedField, theFieldToUpdate);

logger.debug("Script field dependency: validatorRequired_courrierIn.groovy --- End");

