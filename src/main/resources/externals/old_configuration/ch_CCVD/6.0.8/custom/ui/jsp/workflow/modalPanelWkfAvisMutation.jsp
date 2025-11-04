<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:a4j="http://richfaces.org/a4j"
	xmlns:rich="http://richfaces.org/rich"
	xmlns:webuijsf="http://www.sun.com/webui/webuijsf"
	xmlns:dossier="http://dossier.digitech.com/jsf/html">
	
	<style>
	.message_error{
		color:red;
		font-weight:bold;
		font-size:13px;
		text-align:center;
	}
	
	.message_warn{
		color:orange;
		font-weight:bold;
		font-size:13px;
		text-align:center;
	}
	
	</style>

	<h:panelGroup layout="block">
		<rich:panel rendered="#{CustomActionModel.modalPanelModel.DATA_ERROR_MSG==null and CustomActionModel.modalPanelModel.DATA_WARN_MSG==null}" style="height: 315px;font-size:12px;">
			<h:outputLabel value="Service" for="orgas" />
			<h:selectOneMenu id="orgas" value="#{CustomActionModel.modalPanelModel.ORGA_WKF}">
                <f:selectItems value="#{CustomActionModel.modalPanelModel.ORGS}"/>
            </h:selectOneMenu>
			<br/><br/>
			<h:outputLabel value="NIP" for="dossier" />
            <h:inputText id="dossier" size="7" maxlenght="7" value="#{CustomActionModel.modalPanelModel.NIP}"/>
            <br/><br/>
            <h:outputLabel value="Commentaire" for="comment" />
            <h:inputTextarea maxlenght="255" cols="40" rows="5" value="#{CustomActionModel.modalPanelModel.COMM}" onclick="this.focus()"/>
		</rich:panel>

		<center>
			<rich:panel style="overflow:auto; width:540px; margin-top:135px; margin-bottom:10px; text-align: center;" rendered="#{CustomActionModel.modalPanelModel.DATA_ERROR_MSG!=null}">
				<h:outputText value="#{CustomActionModel.modalPanelModel.DATA_ERROR_MSG}" styleClass="message_error" />
			</rich:panel>
			<rich:panel style="overflow:auto; width:540px; margin-top:135px; margin-bottom:10px; text-align: center;" rendered="#{CustomActionModel.modalPanelModel.DATA_ERROR_MSG==null and CustomActionModel.modalPanelModel.DATA_WARN_MSG!=null}">
				<h:outputText value="#{CustomActionModel.modalPanelModel.DATA_WARN_MSG}" styleClass="message_warn" />
			</rich:panel>
		</center>
	</h:panelGroup>
</jsp:root>