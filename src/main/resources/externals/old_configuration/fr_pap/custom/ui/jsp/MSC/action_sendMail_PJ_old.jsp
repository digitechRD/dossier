<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core"
          xmlns:h="http://java.sun.com/jsf/html"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:a4j="http://richfaces.org/a4j"
          xmlns:digi="http://ged.digitech.com/jsf/html">
  <a4j:outputPanel ajaxRendered="true" layout="block" styleClass="mailPanel">
    <script>

      const mailTemplates = {
        "EMPTY": {
          subject: "",
          content: ""
        },
        "SEND_PJ_MANQUANTE": {
          subject: "Montée sur cale - Document manquant",
          content: "Bonjour,\nVoici les éléments à nous renvoyer pour compléter le dossier :\n\n\nPour nous envoyer ces éléments, merci d'utiliser le lien suivant\nhttps://digiportaut.osb.pf/papeete-portal \nCordialement.\nL'équipe de la montée sur cale"
        },
        "SEND_DOSSIER": {
          subject: "Montée sur cale - Dossier à renvoyer",
          content: "Bonjour,\nVous trouverez en pièce jointe le dossier à nous renvoyer complété.\nPour nous envoyer ce dossier, merci d'utiliser le lien suivant\nhttps://digiportaut.osb.pf/papeete-portal \nCordialement.\nL'équipe de la montée sur cale"
        }
      }

      // Faire mieux avec du onload si possible, mais bon, cela semble fonctionner
      if (document.getElementById('modalPanelCustomActionForm:customActionInclusion:emailFrom').value == '' || !verifMail(document.getElementById('modalPanelCustomActionForm:customActionInclusion:emailFrom')) ||
        document.getElementById('modalPanelCustomActionForm:customActionInclusion:emailTo').value == '' || !verifMail(document.getElementById('modalPanelCustomActionForm:customActionInclusion:emailTo')) ||
        document.getElementById('modalPanelCustomActionForm:customActionInclusion:emailSubject').value == ''
      ) {
        // document.getElementById('modalPanelCustomActionForm:ok').disabled = true;
        // document.getElementById('modalPanelCustomActionForm:ok').style.display = 'none';
      } else {
        // document.getElementById('modalPanelCustomActionForm:ok').disabled = false;
        // document.getElementById('modalPanelCustomActionForm:ok').style.display = 'inline';
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
    </script>

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

   <h:panelGroup layout="block" rendered="#{CustomActionController.model.modalPanelModel.emailAttachments.size()>1}" styleClass="row col-sm-12">
  <digi:fieldset legend="#{MessageBundleModel.modalPanelMail_label_attachment}" id="fsPJ"
                 styleClass="col-sm-12">

    <h:panelGroup layout="block" styleClass="row w-100 pt-2 d-flex flex-column">
      <h:selectManyCheckbox id="emailAttachments"
                            value="#{CustomActionController.model.modalPanelModel.attachments}"
                            converter="#{MailController.model.attachmentConverter}"
                            layout="pageDirection"
                            styleClass="mailAttachmentsCustom">
        <f:selectItems value="#{CustomActionController.model.modalPanelModel.emailAttachments}"/>
      </h:selectManyCheckbox>
    </h:panelGroup>

  </digi:fieldset>
</h:panelGroup>
  </a4j:outputPanel>
</jsp:root>