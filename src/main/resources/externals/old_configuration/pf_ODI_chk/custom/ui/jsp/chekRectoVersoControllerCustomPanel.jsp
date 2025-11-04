<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:webuijsf="http://www.sun.com/webui/webuijsf"
	xmlns:a4j="http://richfaces.org/a4j"
	xmlns:rich="http://richfaces.org/rich"
	xmlns:dt="http://ged.digitech.com/jsf/html"
	xmlns:dossier="http://dossier.digitech.com/jsf/html">

	<h:panelGroup id="chekControllerPanel">
	
	<h:panelGroup id="ajaxStatus" layout="block"
		style="position: absolute; right: 10px; margin-top: 5px;">
		<a4j:status forceId="true" id="cheksLoadingStatus" layout="block">
			<f:facet name="start">
				<h:graphicImage value="#{ImageBundleModel.waiting_request2}" />
			</f:facet>
		</a4j:status>
	</h:panelGroup>
		<h:panelGrid />
		<h:panelGroup styleClass="document">
			<a4j:commandLink id="previousDocument"
				styleClass="Hyp_sun4 #{!MultipleVisaController.model.previousDocumentExisting ? 'disabled' : ''}"
				value="#{MessageBundleModel.attachment_hyperlink_previousDocument}"
				title="#{MessageBundleModel.attachment_tooltip_previousDocument}"
				disabled="#{!MultipleVisaController.model.previousDocumentExisting}"
				action="#{CustomActionController.apply}"
				reRender="notifications, modalPanelError, chekControllerPanel">
				<f:setPropertyActionListener value="PREVIOUS_CHEK"
					target="#{CustomActionModel.modalPanelModel.ctrlCheckAction}" />
			</a4j:commandLink>
			<h:outputText escape="false"
				value="#{MultipleVisaController.model.attachmentInModel.currentDocumentTitle}" />
			<a4j:commandLink id="nextDocument"
				styleClass="Hyp_sun4 #{!MultipleVisaController.model.nextDocumentExisting ? 'disabled' : ''}"
				value="#{MessageBundleModel.attachment_hyperlink_nextDocument}"
				title="#{MessageBundleModel.attachment_tooltip_nextDocument}"
				disabled="#{!MultipleVisaController.model.nextDocumentExisting}"
				action="#{CustomActionController.apply}"
				reRender="notifications, modalPanelError, chekControllerPanel">
				<f:setPropertyActionListener value="NEXT_CHEK"
					target="#{CustomActionModel.modalPanelModel.ctrlCheckAction}" />
			</a4j:commandLink>
		</h:panelGroup>
		<h:panelGroup id="attachmentChekViewer" layout="block"
			styleClass="attachmentInViewerPanel row">
			<a4j:outputPanel id="attachmentInLayoutUnitContent" layout="block"
				styleClass="attachmentViewer"
				rendered="#{not empty MultipleVisaController.model.attachmentInModel and MultipleVisaController.model.attachmentInModel.existingAttachment}">

				<h:panelGroup layout="block" styleClass="attachment">
					<h:panelGroup id="notificationsIn" layout="block">
						<dossier:notification id="notificationIn"
							model="#{MultipleVisaController.model.attachmentInModel.message}" />
					</h:panelGroup>
					<h:panelGroup
						rendered="#{empty MultipleVisaController.model.attachmentInModel.message}">
						<h:outputText style="z-index: 2;"
							rendered="#{!MultipleVisaController.model.attachmentInModel.existingAttachment}"
							value="#{MessageBundleModel.attachment_label_noAttachment}" />

							<webuijsf:iframe styleClass="attachment" height="100%"
								width="100%"
								rendered="#{MultipleVisaController.model.attachmentInModel.existingAttachment}"
								url="#{MultipleVisaController.model.attachmentInModel.attachmentURL}" />
						<!-- script>
							initDocumentViewerToolbar();
						</script-->

					</h:panelGroup>
				</h:panelGroup>
			</a4j:outputPanel>
		</h:panelGroup>
				
		<h:panelGroup id="actionButton" layout="block">
			<a4j:outputPanel layout="block" styleClass="buttons"
				ajaxRendered="true">
				<a4j:commandButton id="cancel" styleClass="Btn2_sun4 btn_cancel"
					onmouseover="$jQ(this).addClass('Btn2Hov_sun4 btnHov_cancel');"
					onmouseout="$jQ(this).removeClass('Btn2Hov_sun4 btnHov_cancel');"
					value="#{MessageBundleModel.action_cancel}"
					title="#{MessageBundleModel.action_cancel_toolTip}"
					action="#{CustomActionController.apply}"
					oncomplete="Richfaces.hideModalPanel('modalPanelCustomAction'); return false;"
					reRender="notifications, modalPanelError, mainPanel"
					immediate="true" />
					
				<a4j:commandButton id="wait" styleClass="Btn2_sun4 btn_submit"
					rendered="true"
					onmouseover="$jQ(this).addClass('Btn2Hov_sun4 btnHov_submit');"
					onmouseout="$jQ(this).removeClass('Btn2Hov_sun4 btnHov_submit');"
					value="#{MessageBundleModel.action_chq_ctrl_wait}"
					title="#{MessageBundleModel.action_chq_ctrl_wait_toolTip}"
					action="#{CustomActionController.apply}"
					oncomplete="if(#{CustomActionModel.modalPanelModel.isSignNextExisting}) { Richfaces.hideModalPanel('modalPanelCustomAction'); } return false;"
					reRender="notifications, modalPanelError, chekControllerPanel">
					<f:setPropertyActionListener value="WAIT"
						target="#{CustomActionModel.modalPanelModel.ctrlCheckAction}" />
				</a4j:commandButton>

				<a4j:commandButton id="refused" styleClass="Btn2_sun4 btn_submit"
					rendered="true"
					onmouseover="$jQ(this).addClass('Btn2Hov_sun4 btnHov_submit');"
					onmouseout="$jQ(this).removeClass('Btn2Hov_sun4 btnHov_submit');"
					value="#{MessageBundleModel.action_chq_ctrl_refused}"
					title="#{MessageBundleModel.action_chq_ctrl_refused_toolTip}"
					action="#{CustomActionController.apply}"
					oncomplete="if(#{CustomActionModel.modalPanelModel.isSignNextExisting}) { Richfaces.hideModalPanel('modalPanelCustomAction'); } return false;"
					reRender="notifications, modalPanelError, chekControllerPanel">
					<f:setPropertyActionListener value="REFUSED"
						target="#{CustomActionModel.modalPanelModel.ctrlCheckAction}" />
				</a4j:commandButton>

				<a4j:commandButton id="validate" styleClass="Btn2_sun4 btn_submit"
					rendered="true" 
					onmouseover="$jQ(this).addClass('Btn2Hov_sun4 btnHov_submit');"
					onmouseout="$jQ(this).removeClass('Btn2Hov_sun4 btnHov_submit');"
					value="#{MessageBundleModel.action_chq_ctrl_validate}"
					title="#{MessageBundleModel.action_chq_ctrl_validate_toolTip}"
					action="#{CustomActionController.apply}"
					oncomplete="if(#{CustomActionModel.modalPanelModel.isSignNextExisting}) { Richfaces.hideModalPanel('modalPanelCustomAction'); } return false;"
					reRender="notifications, modalPanelError, chekControllerPanel">
					<f:setPropertyActionListener value="VALIDATE"
						target="#{CustomActionModel.modalPanelModel.ctrlCheckAction}" />
				</a4j:commandButton>
			</a4j:outputPanel>
		</h:panelGroup>
	</h:panelGroup>

</jsp:root>