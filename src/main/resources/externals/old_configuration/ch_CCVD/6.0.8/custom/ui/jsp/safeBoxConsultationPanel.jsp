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

	<dossier:actionBar id="actionBar" backRendered="false">
		<ui:define name="secondaryActionPanel">
			<a4j:htmlCommandLink actionListener="#{GenericDownLoadController.processAction}" ajaxSingle="true" limitToList="true" ignoreDupResponses="true" styleClass="customAction">
				<f:attribute name="file" value="#{CustomActionModel.modalPanelModel.xmlZip}" />
			</a4j:htmlCommandLink>	
				<a4j:htmlCommandLink actionListener="#{GenericDownLoadController.processAction}" ajaxSingle="true" limitToList="true" ignoreDupResponses="true" styleClass="customAction">
				<f:attribute name="file" value="#{CustomActionModel.modalPanelModel.xmlPath}" />
			</a4j:htmlCommandLink>
		</ui:define>
	</dossier:actionBar>

	<h:panelGroup layout="block">
		<h:outputLabel value="archive:cfec : " />
		<h:inputText value="#{CustomActionModel.modalPanelModel.archivecfec}"
			styleClass="detail" size="75" />
	</h:panelGroup>
	<h:panelGroup layout="block">
		<h:outputLabel value="archive:datetime : " />
		<h:inputText
			value="#{CustomActionModel.modalPanelModel.archivedatetime}"
			styleClass="detail" size="75" />
	</h:panelGroup>
	<h:panelGroup layout="block">
		<h:outputLabel value="archive:serialNumber : " />
		<h:inputText
			value="#{CustomActionModel.modalPanelModel.archiveserialNumber}"
			styleClass="detail" size="75" />
	</h:panelGroup>
	<h:panelGroup layout="block">
		<h:outputLabel value="receipt:name : " />
		<h:inputText value="#{CustomActionModel.modalPanelModel.receiptname}"
			styleClass="detail" size="75" />
	</h:panelGroup>
	<h:panelGroup layout="block">
		<h:outputLabel value="ds:DigestMethod : " />
		<h:inputText
			value="#{CustomActionModel.modalPanelModel.dsDigestMethod}"
			styleClass="detail" size="75" />
	</h:panelGroup>
	<h:panelGroup layout="block">
		<h:outputLabel value="ds:DigestValue : " />
		<h:inputText
			value="#{CustomActionModel.modalPanelModel.dsDigestValue}"
			styleClass="detail" size="75" />
	</h:panelGroup>




</jsp:root>