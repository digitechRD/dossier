<jsp:root version="2.0"
          xmlns:h="http://java.sun.com/jsf/html"
          xmlns:ui="http://java.sun.com/jsf/facelets"
          xmlns:digi="http://ged.digitech.com/jsf/html"
          xmlns:f="http://java.sun.com/jsf/core"
          xmlns:jsp="http://java.sun.com/JSP/Page"
>

  <script>
    // empty script section
  </script>

  <!--    Documents standards -->
    <h:panelGroup layout="block" styleClass="row">
      <digi:fieldset legend="Documents standards" id="docsAppendix"
                     styleClass="col-sm-11 mb-4">
        <br/>
        <h:selectManyCheckbox
            value="#{CustomActionController.model.modalPanelModel.selectedFiles}"
            layout="pageDirection">
          <f:selectItems value="#{CustomActionController.model.modalPanelModel.filenamesList}" />
        </h:selectManyCheckbox>
      </digi:fieldset>
    </h:panelGroup>

  <!--    Autres Documents liés -->
    <h:panelGroup layout="block" styleClass="row">
      <digi:fieldset legend="Documents liés aux travaux à réaliser" id="docsLinked"
                     styleClass="col-sm-11">
        <br/>

        <h:selectManyCheckbox
            value="#{CustomActionController.model.modalPanelModel.selectedFilesOther}"
            layout="pageDirection">
          <f:selectItems value="#{CustomActionController.model.modalPanelModel.linkedDocList}" />
        </h:selectManyCheckbox>

      </digi:fieldset>
    </h:panelGroup>

</jsp:root>
