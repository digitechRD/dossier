import Constants
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IField
import com.digitech.dossier.common.model.backing.factory.SelectItemFactory
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.jcorbairs.admin.AuthorityListTermAdmin
import com.digitech.jcorbairs.admin.AuthorityListsManager

import javax.faces.component.UIComponent
import javax.faces.component.html.HtmlSelectOneMenu
import javax.faces.model.SelectItem

/*************************************************************************************************
 *   					    			DependanceFieldsUpdateView - EXEC
 **************************************************************************************************
 Date : 12.04.2016
 Auteur : MTO
 MaJ : [04.07.2017/NFE] computed list is now cached (see #26515)

 Description : Permet de faire la correspondance entre groupe et type de document
 **************************************************************************************************/

/**
 * INITIALISATION
 **************************************************************************************************/
IField fieldMother = updatedField
IField fieldChild = fieldToUpdate

/**
 * TRAITEMENT
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - DEPENDANCE FIELDS UPDATE VIEW - START");
Integer motherValueCode = (Integer) (fieldMother.getValue())

String motherValue = "";
if (motherValueCode != null) {
    AuthorityListTermAdmin aLTA = AuthorityListsManager.loadTerm(UserContext.getInstance().getJeton(), motherValueCode)
    motherValue = aLTA.getCode()
    if (motherValue == null || motherValue == "") {
        //scriptLogger.debug("<<< DependanceFieldsUpdateView(empty value)")
        return
    }
}


List<SelectItem> selectItems = new ArrayList<SelectItem>()
if (motherValueCode == null) {
    // adding empty entry/option
    selectItems.add(new SelectItem("", ""))
    //scriptLogger.debug("AuthorityListsManager.loadTerms({})", Constants.LIST_TYPES_DOCUMENT_ID)
    List<AuthorityListTermAdmin> alItems = AuthorityListsManager.loadTerms(UserContext.getInstance().getJeton(), Constants.LIST_TYPES_DOCUMENT_ID)
    for (AuthorityListTermAdmin authorityListTermAdmin : alItems) {
        selectItems.add(new SelectItem(authorityListTermAdmin.getId(), BundleUtils.getTranslation(authorityListTermAdmin.getValue1())));
    }
    Collections.sort(selectItems, new CustomComparator());
} else {
    //scriptLogger.debug("SelectItemFactory().getFilteredAuthoritiesSubTerm({}), motherValue='{}'", fieldChild.getCode(), motherValue)
    selectItems = new SelectItemFactory().getFilteredAuthoritiesSubTerms(fieldChild.getCode(), UserContext.getInstance().getJeton(), motherValue, true)
}

HtmlSelectOneMenu component = ((HtmlSelectOneMenu) ((UIComponent) fieldChild.getComponent()))
component.getChildren().get(0).setValue(selectItems)

scriptLogger.debug("[CUSTOM ACTION] - DEPENDANCE FIELDS UPDATE VIEW - END");