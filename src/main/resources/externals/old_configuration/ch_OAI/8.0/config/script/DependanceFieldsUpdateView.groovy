import Constants
import com.digitech.dossier.common.comparator.SelectItemComparator
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backing.factory.SelectItemFactory
import com.digitech.dossier.common.service.ServiceUtils
import com.digitech.dossier.script.utils.ScriptUtilities
import org.apache.commons.lang3.StringUtils

/*************************************************************************************************
 *   					    			DependanceFieldsUpdateView - EXEC
 **************************************************************************************************
 Date : 12.04.2016
 Auteur : MTO
 MaJ : [04.07.2017/NFE] computed list is now cached (see #26515)
 Maj : [20.05.2021/NFE] correctly use Dossier Cache-aware services

 Description : Permet de faire la correspondance entre groupe et type de document
 **************************************************************************************************/

/**
 * INITIALISATION
 **************************************************************************************************/
/**
 * TRAITEMENT
 **************************************************************************************************/

if (binding.variables.containsKey(ScriptUtilities.AIRS_UPDATED_FIELD_VALUES)) {
    _scriptLogger.debug("[CUSTOM ACTION] - DEPENDANCE FIELDS UPDATE VIEW - START: ('{}'/'{}'='{}'). Init Size: {}", _updatedField?.title, _updatedField?.code, _updatedField?.value, _updateFieldValues?.size())

    _updateFieldValues.clear()


    Integer motherValueCode = _updatedField?.getValue() as Integer

    if (motherValueCode != null) {
        String motherValue = ServiceUtils.getAuthorityListService().getTerm(motherValueCode)?.code

        if (StringUtils.isBlank(motherValue)) {
            _scriptLogger scriptLogger.debug("<<< DependanceFieldsUpdateView(empty value)")
            return
        }

        _scriptLogger.debug("SelectItemFactory().getFilteredAuthoritiesSubTerms({}, motherValue='{}', locale: '{}')", _fieldToUpdate.getCode(), motherValue,
                UserContext.getInstance().getLocale())
        SelectItemFactory.getInstance().getFilteredAuthoritiesSubTerms(_fieldToUpdate.getCode(), _userContext?.getJeton(), motherValue, false).each { si -> _updateFieldValues.add(si) }
    } else {
        _scriptLogger.debug("SelectItemFactory.getAuthorityListTerms({})", Constants.LIST_TYPES_DOCUMENT_ID)
        SelectItemFactory.getInstance().getAuthorityListTerms(Constants.LIST_TYPES_DOCUMENT_ID, _userContext?.getJeton(), false).each { si -> _updateFieldValues.add(si) }
    }
    _updateFieldValues.sort(SelectItemComparator.INSTANCE)

    _scriptLogger.debug("[CUSTOM ACTION] - DEPENDANCE FIELDS UPDATE VIEW - END ({} items)", _updateFieldValues?.size())
}

void setup() {

    // pre-load each configuration
    _scriptLogger.debug("[CUSTOM ACTION] - DEPENDANCE FIELDS UPDATE VIEW >>> pre-load")

    ServiceUtils.getAuthorityListService().getTerms("AL_ASSURE_GROUPE_DOC").each { t ->
        SelectItemFactory.getInstance().getFilteredAuthoritiesSubTerms("AL_ASSURE_TYPE_DOC", DossierCoreContext?.adminJeton, t.code, Locale.FRENCH, false)
    }

    _scriptLogger.debug("[CUSTOM ACTION] - DEPENDANCE FIELDS UPDATE VIEW <<< pre-load")
}