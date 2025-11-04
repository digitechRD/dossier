<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core"
          xmlns:h="http://java.sun.com/jsf/html"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:a4j="http://richfaces.org/a4j"
          xmlns:rich="http://richfaces.org/rich">

  <style>
    .message_error {
      color: red;
      font-weight: bold;
      font-size: 13px;
      text-align: center;
    }

    .message_warn {
      color: orange;
      font-weight: bold;
      font-size: 13px;
      text-align: center;
    }

  </style>

  <h:panelGroup layout="block" id="CustomAppletPanel1">
    <rich:panel rendered="#{CustomActionModel.modalPanelModel.DATA_ERROR_MSG==null and CustomActionModel.modalPanelModel.DATA_WARN_MSG==null}"
                style="height: 310px;">
      <h:selectOneMenu value="#{CustomActionModel.modalPanelModel.adresse}" immediate="true">
        <f:selectItems value="#{CustomActionModel.modalPanelModel.adresses}"/>
        <a4j:support event="onchange" reRender="newwordapplet"/>
      </h:selectOneMenu>

      <a4j:outputPanel id="newwordapplet">
        <applet ARCHIVE="applet/newword/NewWordApplet-1.0.0.jar" CODE="ch.digitech.ccvd.applet.newword.NewWordDocApplet.class" width="500" height="350">
          <PARAM name="config" value="#{CustomActionModel.modalPanelModel.config}"/>
          <PARAM name="WordBinary" value="#{CustomActionModel.modalPanelModel.WordBinary}"/>
          <PARAM name="macroName" value="#{CustomActionModel.modalPanelModel.macroName}"/>
          <PARAM name="gotoFolder" value="#{CustomActionModel.modalPanelModel.gotoFolder}"/>
          <PARAM name="NIP" value="#{CustomActionModel.modalPanelModel.NIP}"/>
          <PARAM name="adr" value="#{CustomActionModel.modalPanelModel.adresse}"/>
          <PARAM name="ORGID" value="#{CustomActionModel.modalPanelModel.ORGID}"/>
          <PARAM name="NE" value="#{CustomActionModel.modalPanelModel.NE}"/>
          <PARAM name="NSS" value="#{CustomActionModel.modalPanelModel.NE}"/>
          <PARAM name="titre" value="#{CustomActionModel.modalPanelModel.titre}"/>
          <PARAM name="Name" value="#{CustomActionModel.modalPanelModel.Name}"/>
          <PARAM name="Surname" value="#{CustomActionModel.modalPanelModel.Surname}"/>
          <PARAM name="taxateur" value="#{CustomActionModel.modalPanelModel.taxateur}"/>
        </applet>
      </a4j:outputPanel>
    </rich:panel>
    <div style="text-align: center;">
      <rich:panel style="overflow:auto; width:540px; margin-top:135px; margin-bottom:10px; text-align: center;"
                  rendered="#{CustomActionModel.modalPanelModel.DATA_ERROR_MSG!=null}">
        <h:outputText value="#{CustomActionModel.modalPanelModel.DATA_ERROR_MSG}" styleClass="message_error"/>
      </rich:panel>
      <rich:panel style="overflow:auto; width:540px; margin-top:135px; margin-bottom:10px; text-align: center;"
                  rendered="#{CustomActionModel.modalPanelModel.DATA_ERROR_MSG==null and CustomActionModel.modalPanelModel.DATA_WARN_MSG!=null}">
        <h:outputText value="#{CustomActionModel.modalPanelModel.DATA_WARN_MSG}" styleClass="message_warn"/>
      </rich:panel>
    </div>
  </h:panelGroup>
</jsp:root>
