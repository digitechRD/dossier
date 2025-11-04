<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:a4j="http://richfaces.org/a4j"
	xmlns:rich="http://richfaces.org/rich"
	xmlns:dossier="http://dossier.digitech.com/jsf/html">

	<script>
	modalPanelCustomAction_show = function() {
    $jQ(jsfIDtoJQID('modalPanelCustomActionForm:customActionInclusion:public')).attr('checked', true);
  };
	</script>

	<h:panelGroup layout="block">
		<h:outputLabel value="Commentaire : " />
		<h:inputTextarea value="#{CustomActionModel.modalPanelModel.comment}"
			styleClass="textAreaModalPanelComment" />
	</h:panelGroup>

	<h:panelGroup layout="block">
		<h:outputLabel value="#{MessageBundleModel.panelSharing_label_public}"
			for="public" />
		<h:selectBooleanCheckbox id="public" value="#{CustomActionModel.modalPanelModel.publicShare}" />
		<h:message showDetail="true" for="public" styleClass="message" />
	</h:panelGroup>
</jsp:root>