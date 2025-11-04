import com.digitech.dossier.common.resources.BundleUtils
import javax.faces.model.SelectItem
import java.util.ArrayList
import java.util.List
import MSC_Utils
import org.apache.commons.collections4.CollectionUtils
import com.digitech.jcorbairs.Term

/************************************************************************************************************************************
 * Auteur 	  	: SNE
 * Date 		  	: 12/08/2025
 * Description  : Script permettant d'initialiser le panel de sélection des documents
 * ------------------------------------------------------------------------------------------------------------------------------------
 * Paramètres d'entree :
 *   - scriptLogger
 *   - document
 *   - userContext
 ************************************************************************************************************************************/

// Constantes
final String SCRIPT_NAME = "initialisation_generateDocsPanel"

final Integer PANEL_WIDTH = 600
final Integer PANEL_HEIGHT = 450
final Map<String, String> STANDARD_PJ_NAME = [
    "Contrat": "contrat.docx",
    "Annexe 1": "annexe1.docx",
    "Annexe 2": "annexe2.docx",
    "Annexe 3": "annexe3.docx",
    "Annexe 4": "annexe4.docx",
    "Annexe 5": "annexe5.docx",
]

final Map<String, Map<String, String>> LINKED_DOCS_NAME = [
    "immobilisation_machine": [label: "Immobilisation machine", file: "immobilisation_machine.docx"],
    "operation_de_plongee": [label: "Opération de plongée", file: "operation_de_plongee.docx"],
    "mise_a_leau_dembarcation": [label: "Mise à l'eau d'embarcation", file: "mise_a_leau_dembarcation.docx"],
    "travaux_a_chaud": [label: "Travaux à chaud", file: "travaux_a_chaud.docx"]
]

try {
  scriptLogger.info("Script groovy de type initialisation de panel : " + SCRIPT_NAME + " --- Start")

  // 1) ====== Initialisation de la modale =======
  _customModel.clear()
  _customModel.setModalPanelWidth(PANEL_WIDTH)
  _customModel.setModalPanelHeight(PANEL_HEIGHT)
  _customModel.setModalPanelTitle(BundleUtils.getTranslation("MSC_action_GenerateDocs_title"))

  // 2) ====== Documents standards =======
  List<SelectItem> filenamesList = new ArrayList<>()
  STANDARD_PJ_NAME.each { key, value ->
    filenamesList.add(new SelectItem(value, key))
  }
  _customModel.addPanelData("filenamesList", filenamesList)
  // toutes les cases cochées par défaut
  _customModel.addPanelData("selectedFiles", STANDARD_PJ_NAME.values().toList())

  // 3) ======= Autres Documents liés ========
  List<SelectItem> linkedDocList = new ArrayList<>()
  LINKED_DOCS_NAME.each { key, value ->
    linkedDocList.add(new SelectItem(value.file, value.label))
  }
  _customModel.addPanelData("linkedDocList", linkedDocList)

  // Liste d'autorité pour le champ MSC_TRAVAUX
  List<String> selectedFilesOther = new ArrayList<>()
  List<Term> terms = MSC_Utils.getAuthorityListService().getTerms("MSC_TRAVAUX")
  if(CollectionUtils.isNotEmpty(terms)) {
    for(Term term : (terms as java.util.List)) {
      if(LINKED_DOCS_NAME.containsKey(term.getCode())) {
        selectedFilesOther.add(LINKED_DOCS_NAME[term.getCode()].file)
      }
    }
  }
  _customModel.addPanelData("selectedFilesOther", selectedFilesOther)

  scriptLogger.info("Script groovy de type initialisation de panel : " + SCRIPT_NAME + " --- End")
}
catch (Exception e) {
  scriptLogger.error("ERREUR : " + e.getLocalizedMessage(), e)
}