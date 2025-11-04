<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:webuijsf="http://www.sun.com/webui/webuijsf"
	xmlns:a4j="http://richfaces.org/a4j"
	xmlns:rich="http://richfaces.org/rich"
	xmlns:digitech="http://ged.digitech.com/jsf/html">

	<webuijsf:form>
		<a4j:jsFunction name="hideDeleteDocumentModalPanel"
			action="#{DocumentDeletionController.cancel}">
		</a4j:jsFunction>
	</webuijsf:form>

	<!-- Report Modal Panel -->
	<rich:modalPanel id="deleteDocumentModalPanel"
		styleClass="panel deleteDocumentModalPanel"
		rendered="#{DocumentDeletionModel.visible}" showWhenRendered="true"
		resizeable="false" minHeight="210" minWidth="400" height="210"
		width="400" zindex="2000" onhide="hideDeleteDocumentModalPanel();">
		<f:facet name="header">
			<h:outputText
				value="#{MessageBundleModel.modalPanelDeleteDocument_title}" />
		</f:facet>
		<f:facet name="controls">
			<h:form>
				<h:commandLink
					onclick="Richfaces.hideModalPanel('deleteDocumentModalPanel'); return false;">
					<h:graphicImage value="#{ImageBundleModel.icon_close_small}"
						style="cursor:pointer" />
				</h:commandLink>
			</h:form>
		</f:facet>

		<webuijsf:form id="deleteDocumentForm">
			<webuijsf:staticText style="padding-bottom: 8px;"
				text="#{MessageBundleModel.modalPanelDeleteDocument_description}" />

			<webuijsf:panelGroup id="contentPanel" block="true">
				<webuijsf:radioButtonGroup id="deletionType"
					styleClass="radioButtonGroup"
					items="#{DocumentDeletionModel.deletionTypes}"
					selected="#{DocumentDeletionModel.deletionType}">
					<f:converter converterId="javax.faces.Integer" />
				</webuijsf:radioButtonGroup>
				<webuijsf:message showDetail="true" for="deletionType" />

				<webuijsf:panelGroup block="true" id="confirmationPanel" styleClass="confirmationPanel">
					<h:selectOneRadio id="deletionConfirmationRadio" styleClass="confirmationRadioButtons" onclick="var applyBtn = document.getElementById('deleteDocumentInclusion:deleteDocumentForm:buttonPanel:applyBtn'); return applyBtn.setProps({disabled: false});">
						<f:selectItem
							itemLabel="#{MessageBundleModel.modalPanelDeleteDocument_confirmation_confirm}" />
						<f:selectItem
							itemLabel="#{MessageBundleModel.modalPanelDeleteDocument_confirmation_cancel}" />
					</h:selectOneRadio>
				</webuijsf:panelGroup>
			</webuijsf:panelGroup>

			<webuijsf:panelGroup id="buttonPanel" block="true" styleClass="buttonPanel">
				<webuijsf:button styleClass="button"
					text="#{MessageBundleModel.action_cancel}"
					actionExpression="#{DocumentDeletionController.cancel}" />
				<webuijsf:button id="applyBtn"
				  styleClass="button"
				  disabled="true"
					text="#{MessageBundleModel.action_apply}"
					onClick="var deletionConfirmationRadio = document.getElementById('deleteDocumentInclusion:deleteDocumentForm:contentPanel:confirmationPanel:deletionConfirmationRadio:0'); if (deletionConfirmationRadio.checked) { Richfaces.showModalPanel('confirmDeleteDocumentModalPanel');} else {Richfaces.hideModalPanel('deleteDocumentModalPanel');} return false;" />
			</webuijsf:panelGroup>
		</webuijsf:form>
	</rich:modalPanel>

	<!-- Report Modal Panel -->
  <rich:modalPanel id="confirmDeleteDocumentModalPanel"
    styleClass="panel confirmDeleteDocumentModalPanel"
    resizeable="false" minHeight="90" minWidth="300" height="90"
    width="300" zindex="2000">
    <f:facet name="header">
      <h:outputText
        value="#{MessageBundleModel.modalPanelConfirmDeleteDocument_title}" />
    </f:facet>
    <f:facet name="controls">
      <h:form>
        <h:commandLink
          onclick="Richfaces.hideModalPanel('confirmDeleteDocumentModalPanel'); return false;">
          <h:graphicImage value="#{ImageBundleModel.icon_close_small}"
            style="cursor:pointer" />
        </h:commandLink>
      </h:form>
    </f:facet>

    <webuijsf:form id="confirmDeleteDocumentForm">
      <webuijsf:staticText style="padding-bottom: 8px;"
        text="#{MessageBundleModel.modalPanelConfirmDeleteDocument_description}" />

      <webuijsf:panelGroup id="buttonPanel" block="true" styleClass="buttonPanel" style="margin-top: 8px;">
        <webuijsf:button styleClass="button"
          text="#{MessageBundleModel.modalPanelConfirmDeleteDocument_action_no}"
          onClick="Richfaces.hideModalPanel('confirmDeleteDocumentModalPanel'); return false;" />
        <webuijsf:button id="applyBtn"
          styleClass="button"
          text="#{MessageBundleModel.modalPanelConfirmDeleteDocument_action_yes}"
          onClick="Richfaces.hideModalPanel('confirmDeleteDocumentModalPanel'); Richfaces.hideModalPanel('deleteDocumentModalPanel'); Richfaces.showModalPanel('modalPanelProgress');"
          actionExpression="#{DocumentDeletionController.submit}" />
      </webuijsf:panelGroup>
    </webuijsf:form>
  </rich:modalPanel>
</jsp:root>