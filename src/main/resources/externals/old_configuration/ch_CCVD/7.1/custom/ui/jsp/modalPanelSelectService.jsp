<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core"
          xmlns:h="http://java.sun.com/jsf/html"
          xmlns:jsp="http://java.sun.com/JSP/Page"
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
    function displayOrganisation(val) {
      if (val == '34' || val == '24' || val == '25' || val == '35' || val == '36' || val == '37' || val == '38' || val == '39' || val == '40' || val == '28') {

        document.getElementById('modalPanelCustomActionForm:customActionInclusion:myform:orgas').style.display = 'block';
        document.getElementById('modalPanelCustomActionForm:customActionInclusion:myform:orgaVisible').value = true;
      } else {
        document.getElementById('modalPanelCustomActionForm:customActionInclusion:myform:orgas').style.display = 'none';
        //document.getElementById('modalPanelCustomActionForm:customActionInclusion:myform:orgaVisible').value = false;
      }
    }

  </script>

  <h:panelGroup layout="block" id="mypanel">
    <rich:panel rendered="#{CustomActionModel.modalPanelModel.DATA_ERROR_MSG==null and CustomActionModel.modalPanelModel.DATA_WARN_MSG==null}"
                style="height: 315px;">
      <h:form id="myform">

        <!--h:selectOneMenu id="servicelist" value="#{CustomActionModel.modalPanelModel.service}" rendered="#{not empty CustomActionModel.modalPanelModel.services}" onchange="displayOrganisation(this.value);">
					<f:selectItems value="#{CustomActionModel.modalPanelModel.services}"/>
				</h:selectOneMenu-->

        <!--h:inputHidden id="orgaVisible" value="#{CustomActionModel.modalPanelModel.orgaVisible}"/-->

        <h:selectOneMenu id="orgas" value="#{CustomActionModel.modalPanelModel.orgaWkf}"
                         rendered="#{not empty CustomActionModel.modalPanelModel.organisations}" onclick="this.focus()">
          <f:selectItems value="#{CustomActionModel.modalPanelModel.organisations}"/>
        </h:selectOneMenu>

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