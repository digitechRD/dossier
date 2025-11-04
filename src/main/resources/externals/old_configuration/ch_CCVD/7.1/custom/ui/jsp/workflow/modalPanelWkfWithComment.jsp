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

  <h:panelGroup layout="block">
    <rich:panel rendered="#{CustomActionModel.modalPanelModel.DATA_ERROR_MSG==null and CustomActionModel.modalPanelModel.DATA_WARN_MSG==null}"
                style="border-width:0px;">
      <h:outputText layout="block" style="font-weight:bold" value="Informations : "/>
      <rich:panel style="overflow:auto; height: 210px; width: 570px; border-width: 1px; margin-bottom: 2px;">
        <rich:dataTable
            onRowMouseOver="this.style.backgroundColor='#e2e059'"
            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
            cellpadding="0" cellspacing="0"
            width="550" border="0" var="DOC_ID" value="#{CustomActionModel.modalPanelModel.LIST_DOC_ID}">
          <f:facet name="header">
            <rich:columnGroup>
              <rich:column>
                <h:outputText value="Doc. ID"/>
              </rich:column>
              <rich:column>
                <h:outputText value="NIP"/>
              </rich:column>
              <rich:column>
                <h:outputText value="Date"/>
              </rich:column>
              <rich:column>
                <h:outputText value="Etat"/>
              </rich:column>
            </rich:columnGroup>
          </f:facet>

          <rich:column>
            <h:outputText value="#{DOC_ID}"/>
          </rich:column>
          <rich:column>
            <h:outputText value="#{CustomActionModel.modalPanelModel.DOCUMENTS[DOC_ID].fieldMap.N_AFF.airsValue}"/>
          </rich:column>
          <rich:column>
            <h:outputText value="#{CustomActionModel.modalPanelModel.DOCUMENTS[DOC_ID].fieldMap.D_CREAT.airsValue}"/>
          </rich:column>
          <rich:column>
            <h:outputText value="#{CustomActionModel.modalPanelModel.STATES[DOC_ID]}"/>
          </rich:column>
        </rich:dataTable>
      </rich:panel>
      <h:outputText layout="block" style="font-size:11px;" value="#{CustomActionModel.modalPanelModel.MSG_NB_VALID_DOC}"/>
      <h:outputText layout="block" style="font-size:11px; padding-left:190px;" value="Document(s) pour validation au service Finance"
                    rendered="#{CustomActionModel.modalPanelModel.DATA_CONTROLE_FINANCE}"/>
      <h:selectBooleanCheckbox value="#{CustomActionModel.modalPanelModel.DATA_CONTROLE_FINANCE_VALUE}" style="float: none;"
                               rendered="#{CustomActionModel.modalPanelModel.DATA_CONTROLE_FINANCE}"/>
      <br/>
      <h:outputText layout="block" style="font-weight:bold;" value="Commentaire : "/>
      <br/>
      <h:inputTextarea maxlenght="255" cols="79" rows="3" style="margin-left:0px;" value="#{CustomActionModel.modalPanelModel.commentaire}"/>
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