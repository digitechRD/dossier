<jsp:root version="2.0"
          xmlns:h="http://java.sun.com/jsf/html"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:rich="http://richfaces.org/rich">

  <h:panelGroup layout="block">
    <h:outputLabel value="Date" for="date"/>
    <rich:calendar id="date" datePattern="dd/MM/yy"
                   enableManualInput="true" timeZone="#{ApplicationModel.timeZone}"
                   locale="#{ApplicationModel.locale}" buttonClass="picker"
                   inputStyle="vertical-align:top;"
                   value="#{CustomActionModel.modalPanelModel.date}"
                   showApplyButton="true"/>
  </h:panelGroup>

  <h:panelGroup layout="block">
    <h:outputLabel value="Utilisateur" for="user"/>
    <h:inputText id="user" value="#{CustomActionModel.modalPanelModel.user}"/>
  </h:panelGroup>

</jsp:root>