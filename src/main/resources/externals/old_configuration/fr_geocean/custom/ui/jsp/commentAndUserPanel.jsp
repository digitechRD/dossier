<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:c="http://java.sun.com/jstl/core"
	xmlns:ui="http://java.sun.com/jsf/facelets"
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:webuijsf="http://www.sun.com/webui/webuijsf"
	xmlns:a4j="http://richfaces.org/a4j"
	xmlns:rich="http://richfaces.org/rich"
	xmlns:digi="http://ged.digitech.com/jsf/html"
	xmlns:dossier="http://dossier.digitech.com/jsf/html">

<h:panelGroup id="CommentAndUserPanel" rendered="#{CustomActionModel.modalPanelModel.displayPanelCommentAndUser}">
	<h:panelGroup id="ajaxStatus" layout="block"
		style="position: absolute; right: 10px; margin-top: 5px;">
		<a4j:status forceId="true" id="treatmentUGLoadingStatus"
			layout="block">
			<f:facet name="start">
				<h:graphicImage value="#{ImageBundleModel.waiting_request2}" />
			</f:facet>
		</a4j:status>
	</h:panelGroup>

	<h:panelGroup layout="block">
		<h:outputLabel style="width:160px"
			value="#{MessageBundleModel.modalPanelComment_select}" />
		<h:selectOneMenu value="#{CustomActionModel.modalPanelModel.selectedUser}" style="width:150px">
			<f:selectItems value="#{CustomActionModel.modalPanelModel.userItems}" />
			<a4j:support event="onchange" ajaxSingle="true" limitToList="true"
				ignoreDupResponses="true" 
				status="treatmentUGLoadingStatus" />
		</h:selectOneMenu>
	</h:panelGroup>
	
	<h:panelGroup layout="block">
		<h:outputLabel value="Commentaire : " />
		<h:inputTextarea value="#{CustomActionModel.modalPanelModel.comment}"
			styleClass="textAreaModalPanelComment" />
	</h:panelGroup>
	</h:panelGroup>

</jsp:root>