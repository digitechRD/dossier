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

  <script language="javascript" type="text/javascript">
    function displayCalendar(val) {
      if (val == '70') {
        document.getElementById('modalPanelCustomActionForm:customActionInclusion:myform:dech').style.display = 'block';
        document.getElementById('modalPanelCustomActionForm:customActionInclusion:myform:acontroler').value = true;
      } else {
        document.getElementById('modalPanelCustomActionForm:customActionInclusion:myform:dech').style.display = 'none';
      }
    }

  </script>


  <h:panelGroup layout="block" id="mypanel">
    <rich:panel rendered="#{CustomActionModel.modalPanelModel.DATA_ERROR_MSG==null and CustomActionModel.modalPanelModel.DATA_WARN_MSG==null}"
                style="height: 315px;">
      <h:form id="myform">
        <h:selectOneMenu id="usrList" value="#{CustomActionModel.modalPanelModel.user}" rendered="#{not empty CustomActionModel.modalPanelModel.users}">
          <f:selectItems value="#{CustomActionModel.modalPanelModel.users}"/>
        </h:selectOneMenu>

        <h:selectOneMenu id="stateList" value="#{CustomActionModel.modalPanelModel.etat}" rendered="#{not empty CustomActionModel.modalPanelModel.etats}">
          <f:selectItems id="etatitems" value="#{CustomActionModel.modalPanelModel.etats}"/>
          <a4j:support event="onchange" oncomplete="displayCalendar(#{CustomActionModel.modalPanelModel.etat});"/>
        </h:selectOneMenu>

        <h:inputHidden id="acontroler" value="#{CustomActionModel.modalPanelModel.acontroler}"/>

        <rich:calendar id="dech"
                       popup="false"
                       datePattern="dd/MM/yyyy"
                       enableManualInput="true"
                       timeZone="#{ApplicationModel.timeZone}"
                       locale="#{ApplicationModel.locale}"
                       buttonClass="picker"
                       inputStyle="vertical-align:top;"
                       value="#{CustomActionModel.modalPanelModel.echeance}"
                       showApplyButton="true"
                       todayControlMode="hidden"
                       style="display:none">
          <a4j:support event="onchange" oncomplete="saveDateValue();"/>
        </rich:calendar>
      </h:form>
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