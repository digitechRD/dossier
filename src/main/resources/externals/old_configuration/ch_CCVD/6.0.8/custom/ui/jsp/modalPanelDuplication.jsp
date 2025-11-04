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

	
	<h:panelGrid id="panel" columns="2"  >	
		<rich:panel rendered="#{CustomActionModel.modalPanelModel.DATA_ERROR_MSG==null and CustomActionModel.modalPanelModel.DATA_WARN_MSG==null}" style="height: 310px; width: 570px;">
			<h:outputLabel value="NIP :" for="libelle" />
			<h:inputText maxlenght="7" cols="50" rows="1" value="#{CustomActionModel.modalPanelModel.NAFF_FIELD}"/>	
					
			<h:outputLabel value="Service :" for="libelle" />
			<h:selectOneMenu id="serviceList" value="#{CustomActionModel.modalPanelModel.SERVICE_LIST_VALUE}" rendered="#{not empty CustomActionModel.modalPanelModel.SERVICE_LIST}">
				<f:selectItems id="itemsservice" value="#{CustomActionModel.modalPanelModel.SERVICE_LIST}"/>
			</h:selectOneMenu>	
			
			<h:outputLabel value="Type :" for="libelle" />
			<h:selectOneMenu id="typeList" value="#{CustomActionModel.modalPanelModel.TYPE_LIST_VALUE}" rendered="#{not empty CustomActionModel.modalPanelModel.TYPE_LIST}">
				<f:selectItems id="itemstype" value="#{CustomActionModel.modalPanelModel.TYPE_LIST}"/>
			</h:selectOneMenu>
				
			<h:outputLabel value="Description :" for="libelle" />
			<h:inputTextarea maxlenght="255" cols="50" rows="3" value="#{CustomActionModel.modalPanelModel.DESC_FIELD}"/>			
		
			<h:outputLabel value="Commentaire :" for="libelle"/>
			<h:inputTextarea maxlenght="255" cols="50" rows="3" value="#{CustomActionModel.modalPanelModel.COMMENT}"/>			
		</rich:panel>
		<center>
			<rich:panel style="overflow:auto; width:540px; margin-top:135px; margin-bottom:10px; text-align: center;" rendered="#{CustomActionModel.modalPanelModel.DATA_ERROR_MSG!=null}">
				<h:outputText value="#{CustomActionModel.modalPanelModel.DATA_ERROR_MSG}" styleClass="message_error" />
			</rich:panel>
			<rich:panel style="overflow:auto; width:540px; margin-top:135px; margin-bottom:10px; text-align: center;" rendered="#{CustomActionModel.modalPanelModel.DATA_ERROR_MSG==null and CustomActionModel.modalPanelModel.DATA_WARN_MSG!=null}">
				<h:outputText value="#{CustomActionModel.modalPanelModel.DATA_WARN_MSG}" styleClass="message_warn" />
			</rich:panel>
		</center>
	</h:panelGrid>


</jsp:root>