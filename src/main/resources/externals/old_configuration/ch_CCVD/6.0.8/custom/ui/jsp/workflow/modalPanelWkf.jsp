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
		<rich:panel rendered="#{CustomActionModel.modalPanelModel.DATA_ERROR_MSG==null and CustomActionModel.modalPanelModel.DATA_WARN_MSG==null}" style="border-width:0px;">
			<h:outputText layout="block" style="font-weight:bold" value="Informations : "/>
			<rich:panel style="overflow:auto; height: 310px; width: 540px; border-width: 1px; margin-bottom: 2px;">
				<rich:dataTable 
					onRowMouseOver="this.style.backgroundColor='#e2e059'"
					onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
					cellpadding="0" cellspacing="0"
					width="550" border="0" var="DOC_ID" value="#{CustomActionModel.modalPanelModel.LIST_DOC_ID}">
							<f:facet name="header">
								<rich:columnGroup>
									<rich:column>
										<h:outputText value="Doc. ID" />
									</rich:column>
									<rich:column>
										<h:outputText value="NIP" />
									</rich:column>
									<rich:column>
										<h:outputText value="Date" />
									</rich:column>
									<rich:column>
										<h:outputText value="Etat" />
									</rich:column>
								</rich:columnGroup>
							</f:facet>
			
							<rich:column>
								<h:outputText value="#{DOC_ID}" />
							</rich:column>
							<rich:column>
								<h:outputText value="#{CustomActionModel.modalPanelModel.DOCUMENTS[DOC_ID].fieldMap.N_AFF.airsValue}" />
							</rich:column>
							<rich:column>
								<h:outputText value="#{CustomActionModel.modalPanelModel.DOCUMENTS[DOC_ID].fieldMap.D_CREAT.airsValue}" />
							</rich:column>
							<rich:column>
								<h:outputText value="#{CustomActionModel.modalPanelModel.STATES[DOC_ID]}" />
							</rich:column>
					</rich:dataTable>
			</rich:panel>
			<h:outputText layout="block" style="font-size:11px;" value="#{CustomActionModel.modalPanelModel.MSG_NB_VALID_DOC}"/>
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