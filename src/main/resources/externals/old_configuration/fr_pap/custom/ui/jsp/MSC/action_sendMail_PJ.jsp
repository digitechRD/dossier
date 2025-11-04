<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core"
          xmlns:h="http://java.sun.com/jsf/html"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:a4j="http://richfaces.org/a4j"
          xmlns:ui="http://java.sun.com/jsf/facelets"
          xmlns:digi="http://ged.digitech.com/jsf/html">

  <script type="text/javascript">
    const mailTemplates = {
      "EMPTY": {
        subject: "",
        content: ""
      },
      "SEND_PJ_MANQUANTE": {
        subject: "Montée sur cale - Document manquant",
        content: "Voici les éléments à nous renvoyer pour compléter le dossier :\n\nPour nous envoyer ces éléments, merci d'utiliser le lien suivant et de compléter les champs avec les informations suivantes :\n https://digiportaut.osb.pf/papeete-portal/#/slipway/update \n Type de demande : Dossier\nNuméro de montée sur cale : [N° demande de montée sur cale]\nAdresse mail de contact : [adresse mail du contact]. \n Cordialement.\n L'équipe de la montée sur cale"

      },
      "SEND_DOSSIER": {
        subject: "Montée sur cale - Dossier à renvoyer",
        content: "Bonjour,\nVous trouverez en pièce jointe le dossier à nous renvoyer complété.\nPour nous envoyer ce dossier, merci d'utiliser le lien suivant\nhttps://digiportaut.osb.pf/papeete-portal \nCordialement.\nL'équipe de la montée sur cale"
      }
    }

    function updateMailFields(typeKey) {
      const template = mailTemplates[typeKey];
      if (!template) return;
      document.getElementById('modalPanelCustomActionForm:customActionInclusion:emailSubject').value = template.subject;
      document.getElementById('modalPanelCustomActionForm:customActionInclusion:emailContent').value = template.content;
      verifForm();
    }

    /*Fonction verifier formulaire*/
    function verifForm() {
      if (document.getElementById('modalPanelCustomActionForm:customActionInclusion:emailFrom').value == '' || !verifMail(document.getElementById('modalPanelCustomActionForm:customActionInclusion:emailFrom')) ||
        document.getElementById('modalPanelCustomActionForm:customActionInclusion:emailTo').value == ''
        || !verifMail(document.getElementById('modalPanelCustomActionForm:customActionInclusion:emailTo')) ||
        document.getElementById('modalPanelCustomActionForm:customActionInclusion:emailSubject').value == ''
      ) {
        // document.getElementById('modalPanelCustomActionForm:ok').disabled = true;
        // document.getElementById('modalPanelCustomActionForm:ok').style.display = 'none';
      } else {
        // document.getElementById('modalPanelCustomActionForm:ok').disabled = false;
        // document.getElementById('modalPanelCustomActionForm:ok').style.display = 'inline';
      }
    }

    function verifMail(champ) {
      var regex = /^[a-zA-Z0-9._-]+@[a-z0-9._-]{2,}\.[a-z]{2,4}$/;
      return regex.test(champ.value);
    }

    function selectChildren(map) {
      const parent = map.closest(".parentGroupedAttachments");
      const checkboxes = parent?.querySelectorAll("input[type=checkbox][name*='attachments']");
      checkboxes.forEach(cb => {
        cb.checked = map.checked
      });
    }
  </script>

  <a4j:outputPanel ajaxRendered="true" layout="block">
    <h:messages styleClass="alert alert-danger w-100"/>
    <h:panelGroup id="to" layout="block" styleClass="row">
      <h:outputLabel value="De " styleClass="required col-sm-3" for="emailFrom"/>
      <h:inputText id="emailFrom" required="true" value="#{CustomActionController.model.modalPanelModel.emailFrom}" onchange="verifForm();"
                   onkeyup="verifForm();"
                   styleClass="col-sm-7"/>
    </h:panelGroup>
    <h:panelGroup layout="block" onload="verifForm();" styleClass="row">
      <h:outputLabel value="Pour " styleClass="required col-sm-3" for="emailTo"/>
      <h:inputText id="emailTo" required="true" value="#{CustomActionController.model.modalPanelModel.emailTo}" onchange="verifForm();" onkeyup="verifForm();"
                   styleClass="col-sm-7"/>
    </h:panelGroup>
    <h:panelGroup layout="block" styleClass="row">
      <h:outputLabel value="Copie à " for="emailCc" styleClass="col-sm-3"/>
      <h:inputText id="emailCc" value="#{CustomActionController.model.modalPanelModel.emailCc}" styleClass="col-sm-7"/>
    </h:panelGroup>
    <h:panelGroup layout="block" styleClass="row">
      <h:outputLabel value="Copie cachée à " for="emailBcc" styleClass="col-sm-3"/>
      <h:inputText id="emailBcc" value="#{CustomActionController.model.modalPanelModel.emailBcc}" styleClass="col-sm-7"/>
    </h:panelGroup>

    <h:panelGroup layout="block" styleClass="row">
      <h:outputLabel value="Modèle de mail" for="mailTypeSelector" styleClass="col-sm-3"/>
      <h:selectOneMenu id="mailTypeSelector"
                       value="#{CustomActionController.model.modalPanelModel.selectedMailType}"
                       onchange="updateMailFields(this.value)"
                       styleClass="col-sm-7">
        <f:selectItems value="#{CustomActionController.model.modalPanelModel.mailTypeSelectList}"/>
      </h:selectOneMenu>
    </h:panelGroup>

    <h:panelGroup layout="block" onload="verifForm();" styleClass="row">
      <h:outputLabel value="Sujet " styleClass="required col-sm-3" for="emailSubject"/>
      <h:inputText id="emailSubject" required="true" value="#{CustomActionController.model.modalPanelModel.emailSubject}" onchange="verifForm();"
                   onkeyup="verifForm();" styleClass="col-sm-7"/>
    </h:panelGroup>
    <h:panelGroup layout="block" styleClass="row">
      <h:outputLabel value="Contenu" for="emailContent" styleClass="col-sm-3"/>
      <h:inputTextarea id="emailContent" cols="50" rows="10" value="#{CustomActionController.model.modalPanelModel.emailContent}" styleClass="col-sm-7"/>
    </h:panelGroup>

    <digi:fieldset legend="#{MessageBundleModel.modalPanelMail_label_attachment} 1" id="fsPJ1"
                   styleClass="row w-100 overflow-auto" rendered="#{CustomActionController.model.modalPanelModel.emailSortedAttachments.size()>0}">
      <h:panelGroup layout="block" styleClass="row w-90 pt-2 d-flex flex-column">
        <ui:repeat var="_entry" value="#{CustomActionController.model.modalPanelModel.emailSortedAttachments}">
          <h:panelGroup layout="block" styleClass="row parentGroupedAttachments">
            <h:panelGroup layout="block" style="cursor:pointer" styleClass="w-100">
              <h:outputLabel value="#{_entry.title}" styleClass="font-bold col-sm-10 w-auto"/>
              <h:selectBooleanCheckbox onclick="selectChildren(this)" styleClass="ml-1"/>
            </h:panelGroup>

            <h:selectManyCheckbox id="attachments" value="#{_entry.selectedAttachments}"
                                  layout="pageDirection" styleClass="mailAttachmentsCustom font-italic col-sm-10 text-abbreviate">
              <f:selectItems value="#{_entry.attachments}"/>
              <f:converter converterId="javax.faces.Integer"/>
            </h:selectManyCheckbox>
          </h:panelGroup>
        </ui:repeat>
      </h:panelGroup>
    </digi:fieldset>
  </a4j:outputPanel>

</jsp:root>