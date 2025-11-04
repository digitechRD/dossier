import com.digitech.dossier.script.model.impl.result.ScriptResultValueFieldInitializer;
import com.digitech.dossier.script.model.impl.result.ScriptResultModel;
import java.util.*

import java.text.ParsePosition;
import java.util.Date;

import org.apache.commons.lang.*
import org.slf4j.Logger

com.digitech.dossier.common.service.IDocument
import com.digitech.courrier.common.model.backend.CourrierConstants
import com.digitech.courrier.common.model.backend.CourrierConstants.FlowType
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backend.airs.IField
import com.digitech.dossier.common.model.backend.airs.ITask
import com.digitech.dossier.common.model.backend.params.CourrierOrga
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker
import com.digitech.dossier.script.model.impl.result.ScriptResultValueFieldInitializer.FieldProperty
import com.digitech.jcorbairs.Term
import com.digitech.jcorbairs.exception.IdentificationException
import com.digitech.jcorbairs.exception.ServerException
import com.digitech.dossier.common.utils.UserUtils
import com.digitech.jcorbairs.Option
import com.digitech.dossier.common.service.ServiceManager
import com.digitech.dossier.common.service.ServiceConstants
import com.digitech.dossier.common.service.IAuthorityList
import com.digitech.jcorbairs.User

import com.digitech.dossier.common.utils.FieldUtils
import com.digitech.dossier.workflow.model.impl.WFTask

import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.digitech.dossier.common.utils.DocumentUtils
import com.digitech.dossier.common.model.backend.airs.ILocutionModel
import com.digitech.dossier.common.model.backend.airs.ISearchModel
import com.digitech.dossier.common.model.backend.airs.IDocument.IComment;
import com.digitech.dossier.common.model.backend.airs.ILocutionModel.Operator
import com.digitech.dossier.common.model.backend.airs.impl.LocutionModel
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.digitech.common.exceptions.DigiInternalException;
import com.digitech.common.framework.bdd.DBConnectionManager;

import static LaProvenceScriptUtils;

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// IDocument document le document courant
// com.digitech.dossier.workflow.model.impl.WFTask wkfTask
/************************************************/

// Input parameters
UserContext usrContext = userContext;
IDocument theDocument = document;
Logger log = scriptLogger;
com.digitech.dossier.workflow.model.impl.WFTask wfTask = wkfTaskModel;
String wkfTaskSortie = wkfTaskOutput;


CONNECT_BDD_AIRS_CAPTURE_LOGIN=LaProvenceScriptUtils.getConstant("CONNECT_BDD_AIRS_CAPTURE_LOGIN");
CONNECT_BDD_AIRS_CAPTURE_MDP=LaProvenceScriptUtils.getConstant("CONNECT_BDD_AIRS_CAPTURE_MDP");
CONNECT_BDD_AIRS_CAPTURE_SERVEUR=LaProvenceScriptUtils.getConstant("CONNECT_BDD_AIRS_CAPTURE_SERVEUR");

CONNECT_BDD_AIRS_DOSSIER_LOGIN=LaProvenceScriptUtils.getConstant("CONNECT_BDD_AIRS_DOSSIER_LOGIN");
CONNECT_BDD_AIRS_DOSSIER_MDP=LaProvenceScriptUtils.getConstant("CONNECT_BDD_AIRS_DOSSIER_MDP");
CONNECT_BDD_AIRS_DOSSIER_SERVEUR=LaProvenceScriptUtils.getConstant("CONNECT_BDD_AIRS_DOSSIER_SERVEUR");

FAC_ENG_NUM_FIELD_CODE = LaProvenceScriptUtils.getConstant("ENG_NUM_FIELD_CODE");
FAC_ENG_VERROU_FIELD_CODE = LaProvenceScriptUtils.getConstant("FAC_ENG_VERROU_FIELD_CODE");
FAC_EMETTEUR_FIELD_CODE = LaProvenceScriptUtils.getConstant("FAC_EMETTEUR_FIELD_CODE");
FAC_SOC_FIELD_CODE = LaProvenceScriptUtils.getConstant("FAC_SOC_FIELD_CODE");
FAC_DATE_FIELD_CODE = LaProvenceScriptUtils.getConstant("FAC_DATE_FIELD_CODE");
FAC_DATE_ECH_FIELD_CODE = LaProvenceScriptUtils.getConstant("FAC_DATE_ECH_FIELD_CODE");
FAC_DATE_VALID_FIELD_CODE = LaProvenceScriptUtils.getConstant("FAC_DATE_VALID_FIELD_CODE");
FAC_ETAT_FIELD_CODE = LaProvenceScriptUtils.getConstant("FAC_ETAT_FIELD_CODE");
FAC_MONTANT_HT_FIELD_CODE = LaProvenceScriptUtils.getConstant("FAC_MONTANT_HT_FIELD_CODE");
FAC_ENG_CT_CODE=LaProvenceScriptUtils.getConstant("FAC_ENG_CT_CODE");
FAC_VERROU_OUI_CODE = LaProvenceScriptUtils.getConstant("FAC_VERROU_OUI_CODE");
FAC_ASSISTANT_CODE = LaProvenceScriptUtils.getConstant("FAC_ASSISTANT_CODE");

USR_OPT_MONTANT_MAX = LaProvenceScriptUtils.getConstant("USR_OPT_MONTANT_MAX");


FAC_NOM_TACHE_BLOQUE=LaProvenceScriptUtils.getConstant("FAC_NOM_TACHE_BLOQUE");
FAC_NOM_TACHE_AVALIDER1=LaProvenceScriptUtils.getConstant("FAC_NOM_TACHE_AVALIDER1");
FAC_NOM_TACHE_AVALIDER2=LaProvenceScriptUtils.getConstant("FAC_NOM_TACHE_AVALIDER2");
FAC_NOM_TACHE_RESTEAPAYER=LaProvenceScriptUtils.getConstant("FAC_NOM_TACHE_RESTEAPAYER");
FAC_NOM_TACHE_AENGAGER=LaProvenceScriptUtils.getConstant("FAC_NOM_TACHE_AENGAGER");
FAC_NOM_TACHE_AENGAGERPOUSSER=LaProvenceScriptUtils.getConstant("FAC_NOM_TACHE_AENGAGERPOUSSER");
FAC_NOM_TACHE_ATRAITER=LaProvenceScriptUtils.getConstant("FAC_NOM_TACHE_ATRAITER");



FAC_NOM_SORTIE_BAP=LaProvenceScriptUtils.getConstant("FAC_NOM_SORTIE_BAP");
FAC_NOM_SORTIE_BAPPARTIEL=LaProvenceScriptUtils.getConstant("FAC_NOM_SORTIE_BAPPARTIEL");
FAC_NOM_SORTIE_BLOQUER=LaProvenceScriptUtils.getConstant("FAC_NOM_SORTIE_BLOQUER");
FAC_NOM_SORTIE_AVALIDER=LaProvenceScriptUtils.getConstant("FAC_NOM_SORTIE_AVALIDER");
FAC_NOM_SORTIE_AENGAGER=LaProvenceScriptUtils.getConstant("FAC_NOM_SORTIE_AENGAGER");

FAC_ETAT_AVALIDER = LaProvenceScriptUtils.getConstant("FAC_ETAT_AVALIDER");
FAC_ETAT_BAP = LaProvenceScriptUtils.getConstant("FAC_ETAT_BAP");
FAC_ETAT_BAPPARTIEL =LaProvenceScriptUtils.getConstant("FAC_ETAT_BAPPARTIEL");


// define messages
String FACTURE_NON_AUTORISE = "groovy_en_tete_action_non_autorise";
String FACTURE_COMM_KO = "groovy_error_commentaire_absent";

String FACTURE_EN_TETE_OK = "groovy_en_tete_facture_ok";
String FACTURE_OK = "groovy_ok_enregistrement";

String FACTURE_EN_TETE_ENG_VER = "groovy_en_tete_facture_eng_ver";
String FACTURE_KO_ENG_VER = "groovy_ko_eng_ver";

String FACTURE_EN_TETE_ENG_INCONNU = "groovy_en_tete_eng_inconnu";
String FACTURE_KO_ENG_INCONNU = "groovy_ko_eng_inconnu";

String FACTURE_EN_TETE_ENG_NON_SAISI = "groovy_en_tete_eng_non_saisi";
String FACTURE_KO_ENG_NON_SAISI = "groovy_ko_eng_non_saisi";

String FACTURE_SAUVEGARDER_AVANT = "groovy_en_tete_sauve_avant";
String FACTURE_KO_SAUVEGARDER_AVANT = "groovy_ko_sauve_avant";

String FACTURE_AFFECTATION_IMPOSSIBLE = "facture_affectation_impossible";
String FACTURE_BAD_USER_ENGAGER = "facture_bad_user_engager";

log.debug("Script triggered onSubmitTaskScriptBefore : onSubmitTaskScriptBefore.groovy --- Start");

ScriptResultModel<ScriptResultValueFieldInitializer> theOutput = output;
String wfTaskName = null;

log.debug("Script triggered onSubmitTaskScriptBefore : onSubmitTaskScriptBefore.groovy --- NomTache : " + wfTask.getName());
log.debug("Script triggered onSubmitTaskScriptBefore : onSubmitTaskScriptBefore.groovy --- Sortie : " + wkfTaskSortie);


log.debug("Script triggered onSubmitTaskScriptBefore : onSubmitTaskScriptBefore.groovy --- Je boucle sur tous les param");
Map<String, Object> listData2 = wfTask.getData();

if ( listData2 != null && listData2.size() > 0)
{
  for (Map.Entry <String, Object> maMap : listData2.entrySet())
  {
    String valeur = maMap.getValue();
    String parametre = maMap.getKey();
    log.debug("Script triggered onSubmitTaskScriptBefore : onSubmitTaskScriptBefore.groovy --- Parametre : "+ parametre + ", valeur : " +valeur);
  }
}

boolean bOK = true;

if ( theDocument.getLockType() != 0 )
{
  log.debug("Script triggered onSubmitTaskScriptBefore : le document est pas vérouillé, on refuse l'action WF");
  // sauvegarder le document avant
  ScriptResultValueChecker result = new ScriptResultValueChecker();
  result.setMessageSeverity(  com.digitech.dossier.script.model.IScriptResultValueModel.Severity.WARN );
  result.setMessageSummary(FACTURE_SAUVEGARDER_AVANT);
  result.setMessageDetail(FACTURE_KO_SAUVEGARDER_AVANT);
  result.setValid(false);
  output.setValue(result);
  bOK = false;
  log.debug("Script triggered onSubmitTaskScriptBefore : onSubmitTaskScriptBefore.groovy --- sauvegardez le document avant");
}


if (bOK && wfTask != null &&
(FAC_NOM_TACHE_AVALIDER1.compareTo(wfTask.getName()) == 0 || FAC_NOM_TACHE_AVALIDER2.compareTo(wfTask.getName()) == 0 || FAC_NOM_TACHE_RESTEAPAYER.compareTo(wfTask.getName()) == 0 ) &&
(FAC_NOM_SORTIE_BLOQUER.compareTo(wkfTaskSortie) == 0  || FAC_NOM_SORTIE_BAPPARTIEL.compareTo(wkfTaskSortie) == 0 )){

  log.debug("Script triggered onSubmitTaskScriptBefore : onSubmitTaskScriptBefore.groovy --- On va vÃ©rifier les commentaires si bloquer ou BAPPArtiel " );

  List<IComment> comments = theDocument.getComments();
  boolean commentInFive = false;
  Date dateNow = new Date();
  log.debug("Script triggered onSubmitTaskScriptBefore : onSubmitTaskScriptBefore.groovy --- nombre de commentaires " + comments.size);
  User monUser = usrContext.getLoggedUser();
  Integer nUserLoggedId = 0;
  if ( monUser != null )
  {
    nUserLoggedId = monUser.getId();
    log.debug("Script triggered onSubmitTaskScriptBefore : onSubmitTaskScriptBefore.groovy --- id du user loggé " + nUserLoggedId);
  }
  else
  {
    log.debug("Script triggered onSubmitTaskScriptBefore : onSubmitTaskScriptBefore.groovy --- pas réussi à récupérer le User");
  }
  for(IComment comment : comments) {
    Integer nActor = comment.getActorId();
    log.debug("Script triggered onSubmitTaskScriptBefore : onSubmitTaskScriptBefore.groovy --- acteur du commentaire : " + nActor);
    Date dateComment = comment.getDate();
    // Get msec from each, and subtract.
    Long diff = dateNow.getTime() - dateComment.getTime();
    diff = diff / (1000 * 60);
    log.debug("Script triggered onSubmitTaskScriptBefore : onSubmitTaskScriptBefore.groovy --- diff " + diff);
    if(diff < 5 && nActor == nUserLoggedId) {
      commentInFive = true;
      continue;
    }
    log.debug("Script triggered onSubmitTaskScriptBefore : onSubmitTaskScriptBefore.groovy --- commentInFive " + commentInFive);
  }
  if (!commentInFive)
  {
    log.debug("Script triggered onSubmitTaskScriptBefore : onSubmitTaskScriptBefore.groovy --- message erreur ");
    ScriptResultValueChecker result = new ScriptResultValueChecker();
    result.setMessageSeverity( com.digitech.dossier.script.model.IScriptResultValueModel.Severity.WARN );
    result.setMessageSummary(FACTURE_NON_AUTORISE);
    result.setMessageDetail(FACTURE_COMM_KO);
    result.setValid(false);
    output.setValue(result);
    bOK = false;
  }
  else
  {
    log.debug("Script triggered onSubmitTaskScriptBefore : onSubmitTaskScriptBefore.groovy --- OK, un commentaire a Ã©tÃ© mis " );
    ScriptResultValueChecker result = new ScriptResultValueChecker();
    result.setValid(true);
    output.setValue(result);
    bOK = true;
  }
}

log.debug("Script triggered onSubmitTaskScriptBefore : Avant test Avalider ");

if (bOK && wfTask != null && (FAC_NOM_TACHE_AENGAGER.compareTo(wfTask.getName()) == 0 || FAC_NOM_TACHE_ATRAITER.compareTo(wfTask.getName()) == 0) &&
FAC_NOM_SORTIE_AVALIDER.compareTo(wkfTaskSortie) == 0 )
{
  log.debug("Script triggered onSubmitTaskScriptBefore : On a cliqué sur AValider ");

  String engNumValue = FieldUtils.getValue(theDocument, FAC_ENG_NUM_FIELD_CODE);
  if (StringUtils.isNotBlank(engNumValue))
  {
    //on vÃ©rifie qu'il existe un engagement avec ce numÃ©ro
    ILocutionModel locutionModel = new LocutionModel();
    DocumentUtils.buildLocutionModel(locutionModel, FAC_ENG_NUM_FIELD_CODE, Operator.OPERATOR_VALUE_EQUAL, engNumValue);

    // Compute search
    List<IDocument> documentList = DocumentUtils.search(UserUtils.getAdminUserContext(), locutionModel, DocumentUtils.getSearchContentTypeList(FAC_ENG_CT_CODE), null);
    if (documentList != null && documentList.size() == 1)
    {
      for (IDocument docEng : documentList)
      {
        // on vÃ©rouille le document
        docEng.getField(FAC_ENG_VERROU_FIELD_CODE).setValue(LaProvenceScriptUtils.getTermID(FAC_ENG_VERROU_FIELD_CODE, FAC_VERROU_OUI_CODE));
        com.digitech.dossier.common.service.IDocument documentMgr = (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(ServiceConstants.SERVICE_AIRS_DOCUMENT_MGR);
        documentMgr.updateDocument(usrContext, docEng, false);

        ScriptResultValueChecker result = new ScriptResultValueChecker();
        result.setMessageSeverity(  com.digitech.dossier.script.model.IScriptResultValueModel.Severity.INFO );
        result.setMessageSummary(FACTURE_EN_TETE_OK);
        result.setMessageDetail(FACTURE_OK);
        result.setValid(true);
        output.setValue(result);
        bOK = true;
        log.debug("Script triggered onSubmitTaskScriptBefore : onSubmitTaskScriptBefore.groovy --- Tout est bon, on renvoit ok");
      }
      log.debug("Script triggered onSubmitTaskScriptBefore : taille de 1 mais on rentre pas dedans");
    }
    else
    {
      // Numero d'engagement inconnu
      ScriptResultValueChecker result = new ScriptResultValueChecker();
      result.setMessageSeverity(  com.digitech.dossier.script.model.IScriptResultValueModel.Severity.WARN );
      result.setMessageSummary(FACTURE_EN_TETE_ENG_INCONNU);
      result.setMessageDetail(FACTURE_KO_ENG_INCONNU);
      result.setValid(false);
      output.setValue(result);
      bOK = false;
      log.debug("Script triggered onSubmitTaskScriptBefore : onSubmitTaskScriptBefore.groovy --- l'engagement est inconnu, ko");
    }
  }
  else
  {
    // Numero d'engagement absent
    ScriptResultValueChecker result = new ScriptResultValueChecker();
    result.setMessageSeverity(  com.digitech.dossier.script.model.IScriptResultValueModel.Severity.WARN );
    result.setMessageSummary(FACTURE_EN_TETE_ENG_NON_SAISI);
    result.setMessageDetail(FACTURE_KO_ENG_NON_SAISI);
    result.setValid(false);
    output.setValue(result);
    bOK = false;
    log.debug("Script triggered onSubmitTaskScriptBefore : onSubmitTaskScriptBefore.groovy --- pas d'engagement saisi, ko");
  }
}

log.debug("Script triggered onSubmitTaskScriptBefore : Après test Avalider ");

if ( bOK )
{
  log.debug("Script triggered onSubmitTaskScriptBefore : onSubmitTaskScriptBefore.groovy --- Rien Ã  faire sur cette etape");
  ScriptResultValueChecker result = new ScriptResultValueChecker();
  result.setValid(true);
  output.setValue(result);
}


log.debug("Script triggered onSubmitTaskScriptBefore : onSubmitTaskScriptBefore.groovy --- End");



