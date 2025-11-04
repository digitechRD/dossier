<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:a4j="http://richfaces.org/a4j"
	xmlns:rich="http://richfaces.org/rich"
	xmlns:dossier="http://dossier.digitech.com/jsf/html">

  <h:panelGroup layout="block">
    <h:outputLabel value="Date" for="date" />
    <rich:calendar id="date" datePattern="dd/MM/yy"
      enableManualInput="true" timeZone="#{ApplicationModel.timeZone}"
      locale="#{ApplicationModel.locale}" buttonClass="picker"
      inputStyle="vertical-align:top;"
      value="#{CustomActionModel.modalPanelModel.date}"
      showApplyButton="true" />
  </h:panelGroup>

	<h:panelGroup layout="block">
		<h:outputLabel value="Utilisateur" for="user" />
		<h:inputText id="user"
			value="#{CustomActionModel.modalPanelModel.user}" />
	</h:panelGroup>

</jsp:root>