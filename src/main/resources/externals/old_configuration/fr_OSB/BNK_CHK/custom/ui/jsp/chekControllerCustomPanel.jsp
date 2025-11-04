<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:webuijsf="http://www.sun.com/webui/webuijsf"
	xmlns:a4j="http://richfaces.org/a4j"
	xmlns:rich="http://richfaces.org/rich"
	xmlns:dt="http://ged.digitech.com/jsf/html"
	xmlns:dossier="http://dossier.digitech.com/jsf/html">

	<h:panelGroup id="ajaxStatus" layout="block"
		style="position: absolute; right: 10px; margin-top: 5px;">
		<a4j:status forceId="true" id="cheksLoadingStatus" layout="block">
			<f:facet name="start">
				<h:graphicImage value="#{ImageBundleModel.waiting_request}" />
			</f:facet>
		</a4j:status>
	</h:panelGroup>

	<script>
		var currentIdx = 0;
		var flagKey=0;
	</script>


	<h:panelGroup id="chekControllerPanel">

		<script>
			modalPanelCustomAction_onShow = function() {
				// Attachment layout shortkeys
				// Valider
				$jQ(document).bind('keyup',{combi : 'v',disableInInput : true},function() {					
					if (!$jQ(jsfIDtoJQID('modalPanelCustomActionForm:customActionInclusion:validate')).is(":disabled")) {
						if (flagKey==0){
								flagKey=1;
								$jQ(jsfIDtoJQID('modalPanelCustomActionForm:customActionInclusion:validate')).click();
							}
						}
					});				
				// Refuser
				$jQ(document).bind('keyup',{combi : 'r',disableInInput : true},function() {					
					if (!$jQ(jsfIDtoJQID('modalPanelCustomActionForm:customActionInclusion:refused')).is(":disabled")) {
						if (flagKey==0){
								flagKey=1;
								$jQ(jsfIDtoJQID('modalPanelCustomActionForm:customActionInclusion:refused')).click();
							}
						}
					});
				// Attendre
				$jQ(document).bind('keyup',{combi : 'a',disableInInput : true},function() {					
					if (!$jQ(jsfIDtoJQID('modalPanelCustomActionForm:customActionInclusion:wait')).is(":disabled")) {
						if (flagKey==0){
								flagKey=1;
								$jQ(jsfIDtoJQID('modalPanelCustomActionForm:customActionInclusion:wait')).click();
							}
						}
					});
				// left				
				$jQ(document).bind('keyup',{combi : 'left',disableInInput : true},function() {					
					if (!$jQ(jsfIDtoJQID('modalPanelCustomActionForm:customActionInclusion:previousDocument')).is(":disabled")) {
						if (flagKey==0){
								flagKey=1;
								$jQ(jsfIDtoJQID('modalPanelCustomActionForm:customActionInclusion:previousDocument')).click();
							}
						}
					});
				// Right
				$jQ(document).bind('keyup',{combi : 'right',disableInInput : true},function() {					
					if (!$jQ(jsfIDtoJQID('modalPanelCustomActionForm:customActionInclusion:nextDocument')).is(":disabled")) {
						if (flagKey==0){
								flagKey=1;
								$jQ(jsfIDtoJQID('modalPanelCustomActionForm:customActionInclusion:nextDocument')).click();
							}
						}
					});			
				// Down
				$jQ(document).bind('keyup',{combi : 'down',disableInInput : true},function() {					
					if (!$jQ(jsfIDtoJQID('modalPanelCustomActionForm:customActionInclusion:nextSign')).is(":disabled")) {
						if (flagKey==0){
								flagKey=1;
								$jQ(jsfIDtoJQID('modalPanelCustomActionForm:customActionInclusion:nextSign')).click();
							}
						}
					});			
				// Up
				$jQ(document).bind('keyup',{combi : 'up',disableInInput : true},function() {					
					if (!$jQ(jsfIDtoJQID('modalPanelCustomActionForm:customActionInclusion:previousSign')).is(":disabled")) {
						if (flagKey==0){
								flagKey=1;
								$jQ(jsfIDtoJQID('modalPanelCustomActionForm:customActionInclusion:previousSign')).click();
							}
						}
					});
                resetModalPanelContent();
				}

			modalPanelCustomAction_onHide = function() {
				// Attachment layout shortkeys            
				$jQ(document).unbind('keyup', 'v');
				$jQ(document).unbind('keyup', 'r');
				$jQ(document).unbind('keyup', 'a');
				// up and down		
				$jQ(document).unbind('keyup', 'up');
				$jQ(document).unbind('keyup', 'down');
				// left and right
				$jQ(document).unbind('keyup', 'left');
				$jQ(document).unbind('keyup', 'right');
			}
			resetModalPanelContent = function() {
				flagKey=0;
				var attachmentSignViewerPanel = $jQ('.parentContainer .attachmentSignViewerPanel div.attachment');
                var attachmentSignDocumentViewer = $jQ('.parentContainer .attachmentSignViewerPanel .documentViewer');
				var attachmentSignViewerIFrame = $jQ('.parentContainer .attachmentSignViewerPanel iframe.attachment');

                if (attachmentSignDocumentViewer.is(':visible') || attachmentSignViewerIFrame.is(':visible')) {
                    // Courrier IN
                    attachmentSignViewerPanel.css('height', 420 + 'px').css('width', 600 + 'px');
                    var attachmentSignViewerPanelHeight = attachmentSignViewerPanel.innerHeight() - 10;
                    var attachmentSignViewerPanelWidth = attachmentSignViewerPanel.innerWidth();
                    attachmentSignDocumentViewer.css('height', attachmentSignViewerPanelHeight + 'px').css('width', attachmentSignViewerPanelWidth + 'px');
                    $jQ('.parentContainer .attachmentSignViewerPanel .documentViewer .toolbarDisplayer').css('width', attachmentSignViewerPanelWidth + 'px');
                    $jQ('.parentContainer .attachmentSignViewerPanel .documentViewer .toolbar').css('width', (attachmentSignViewerPanelWidth - 24) + 'px');
                    attachmentSignViewerIFrame.css('height', attachmentSignViewerPanelHeight + 'px').css('width', (attachmentSignViewerPanelWidth - 10) + 'px');

                    documentViewerSign_computeContainerSizeFuntion(0.0020);
                    setTimeout(initDocumentViewerToolbar, 500);
                }

                var attachmentInViewerPanel = $jQ('.parentContainer .attachmentInViewerPanel div.attachment');
                var attachmentInDocumentViewer = $jQ('.parentContainer .attachmentInViewerPanel .documentViewer');
                var attachmentInViewerIFrame = $jQ('.parentContainer .attachmentInViewerPanel iframe.attachment');

                if (attachmentInDocumentViewer.is(':visible') || attachmentInViewerIFrame.is(':visible')) {
                    // Courrier IN
                    attachmentInViewerPanel.css('height', 420 + 'px').css('width', 600 + 'px');
                    var attachmentInViewerPanelHeight = attachmentInViewerPanel.innerHeight() - 10;
                    var attachmentInViewerPanelWidth = attachmentInViewerPanel.innerWidth();
                    attachmentInDocumentViewer.css('height', attachmentInViewerPanelHeight + 'px').css('width', attachmentInViewerPanelWidth + 'px');
                    $jQ('.parentContainer .attachmentInViewerPanel .documentViewer .toolbarDisplayer').css('width', attachmentInViewerPanelWidth + 'px');
                    $jQ('.parentContainer .attachmentInViewerPanel .documentViewer .toolbar').css('width', (attachmentInViewerPanelWidth - 24) + 'px');
                    attachmentInViewerIFrame.css('height', attachmentInViewerPanelHeight + 'px').css('width', (attachmentInViewerPanelWidth - 10) + 'px');

                    documentViewerIn_computeContainerSizeFuntion(0.0020);

                    setTimeout(initDocumentViewerToolbar, 500);
                }

			};


		</script>


		<h:panelGroup layout="block" style="text-align: center;">
			<h:panelGroup styleClass="document row">
				<a4j:commandLink id="previousSign"
					styleClass="Hyp_sun4 #{!CustomActionModel.modalPanelModel.isSignPreviousExisting ? 'disabled' : ''}"
					value="#{MessageBundleModel.attachment_hyperlink_previousSignature}"
					title="#{MessageBundleModel.attachment_tooltip_previousSignature}"
					disabled="#{!CustomActionModel.modalPanelModel.isSignPreviousExisting}"
					action="#{CustomActionController.apply}"
					reRender="notifications, modalPanelError, attachmentSignViewer, previousSign, nextSign"
					oncomplete="resetModalPanelContent();">
					<f:setPropertyActionListener value="PREVIOUS_SIGN"
						target="#{CustomActionModel.modalPanelModel.ctrlCheckAction}" />
				</a4j:commandLink>
				<h:outputText escape="false"
					value="#{MessageBundleModel.document_pager_name} #{MultipleVisaController.model.currentDocumentIndex + 1}/#{MultipleVisaController.model.selectedDocumentsListSize}" />
				<a4j:commandLink id="nextSign"
					styleClass="Hyp_sun4 #{!CustomActionModel.modalPanelModel.isSignNextExisting ? 'disabled' : ''}"
					value="#{MessageBundleModel.attachment_hyperlink_nextSignature}"
					title="#{MessageBundleModel.attachment_tooltip_nextSignature}"
					disabled="#{!CustomActionModel.modalPanelModel.isSignNextExisting}"
					action="#{CustomActionController.apply}"
					reRender="notifications, modalPanelError, attachmentSignViewer, previousSign, nextSign"
					oncomplete="resetModalPanelContent();">
					<f:setPropertyActionListener value="NEXT_SIGN"
						target="#{CustomActionModel.modalPanelModel.ctrlCheckAction}" />
				</a4j:commandLink>
			</h:panelGroup>
		</h:panelGroup>

		<h:panelGrid columns="2" styleClass="parentContainer">
			<h:panelGroup>
				<h:panelGroup id="attachmentSignViewer" layout="block"
					styleClass="attachmentSignViewerPanel"
					style="width:612px; height:434px;">
					<a4j:outputPanel id="attachmentSignLayoutUnitContent"
						layout="block" styleClass="attachmentViewer">

						<h:panelGroup layout="block" styleClass="attachment">
							<h:panelGroup id="notificationsSignPanel" layout="block">
								<dossier:notification id="notificationsSign"
									model="#{MultipleVisaController.model.attachmentOutModel.message}" />
							</h:panelGroup>
							<h:panelGroup
								rendered="#{empty MultipleVisaController.model.attachmentOutModel.message}">
								<h:outputText style="z-index: 2;" rendered="false"
									value="#{MessageBundleModel.attachment_label_noAttachment}" />

								<h:panelGrid columns="2">
									<a4j:queue id="documentViewerQueueSign" requestDelay="500"
										ignoreDupResponses="true" />
									<f:subview id="subviewSign">
										<dt:documentViewer id="documentViewerSign"
											styleClass="documentViewer"
											rendered="#{not empty MultipleVisaController.model.attachmentOutModel and MultipleVisaController.model.attachmentOutModel.existingAttachment}"
											value="#{MultipleVisaController.model.attachmentOutModel.attachmentFilePath}"
											eventsQueue="documentViewerSign" pagerDisplayed="false"
											zoomDisplayed="true" defaultZoom="FIT_TO_PAGE"
											rotatorDisplayed="false" searchDisplayed="false"
											annotatorDisplayed="false" imageCropperEnabled="false"
											magnifyingGlassEnabled="false" outlineEnabled="false"
											thumbnailsEnabled="false" contextMenuEnabled="false"
											annotationDisplayedOnShow="fale"
											reRender="attachmentSignLayoutUnitContent"
											status="cheksLoadingStatus">
										</dt:documentViewer>
									</f:subview>

								</h:panelGrid>
								<script>
							initDocumentViewerToolbar();
						</script>

							</h:panelGroup>
						</h:panelGroup>
					</a4j:outputPanel>
				</h:panelGroup>
			</h:panelGroup>
			<h:panelGroup>
				<h:panelGroup id="attachmentChekViewer" layout="block"
					styleClass="attachmentInViewerPanel">
					<a4j:outputPanel id="attachmentInLayoutUnitContent" layout="block"
						styleClass="attachmentViewer">

						<h:panelGroup layout="block" styleClass="attachment">
							<h:panelGroup id="notificationsIn" layout="block">
								<dossier:notification id="notificationIn"
									model="#{MultipleVisaController.model.attachmentInModel.message}" />
							</h:panelGroup>
							<h:panelGroup
								rendered="#{empty MultipleVisaController.model.attachmentInModel.message}">
								<h:outputText style="z-index: 2;" rendered="false"
									value="#{MessageBundleModel.attachment_label_noAttachment}" />

								<h:panelGrid columns="2">
									<a4j:queue id="documentViewerQueueIn" requestDelay="500"
										ignoreDupResponses="true" />
									<f:subview id="subviewIn">
										<dt:documentViewer id="documentViewerIn"
											styleClass="documentViewer" style="width: 600px; height: 258px;"											
											value="#{MultipleVisaController.model.attachmentInModel.attachmentFilePath}"
											eventsQueue="documentViewerQueueIn" pagerDisplayed="false"
											zoomDisplayed="true" defaultZoom="FIT_TO_PAGE"
											rotatorDisplayed="false" searchDisplayed="false"
											annotatorDisplayed="false" imageCropperEnabled="false"
											magnifyingGlassEnabled="false" outlineEnabled="false"
											thumbnailsEnabled="false" contextMenuEnabled="false"
											annotationDisplayedOnShow="fale"
											reRender="attachmentInLayoutUnitContent"
											status="cheksLoadingStatus">
										</dt:documentViewer>
									</f:subview>

								</h:panelGrid>
								<script>
							initDocumentViewerToolbar();
						</script>

							</h:panelGroup>
						</h:panelGroup>

					</a4j:outputPanel>


				</h:panelGroup>
				<h:panelGroup style="text-align: center;">
					<h:panelGroup>
						<h:panelGroup layout="block" styleClass="document row">
							<h:outputText
								value="#{MessageBundleModel.modalPanelComment_label_comment}"
								for="inputComment" />
						</h:panelGroup>
					</h:panelGroup>
					<h:panelGroup>
						<h:panelGroup layout="block">
							<h:inputTextarea id="inputComment"
								style="resize: none; width: 600px;height: 143px; margin-left: 7px; padding: 0;"
								value="#{MultipleVisaController.model.comment}" />
						</h:panelGroup>
					</h:panelGroup>

				</h:panelGroup>

			</h:panelGroup>


		</h:panelGrid>
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
					reRender="notifications, modalPanelError"
					status="cheksLoadingStatus">
					<f:setPropertyActionListener value="CANCEL"
						target="#{CustomActionModel.modalPanelModel.ctrlCheckAction}" />
				</a4j:commandButton>



				<a4j:commandButton id="nextDocument"
					styleClass="Btn2_sun4 btn_submit" rendered="true"
					disabled="#{!MultipleVisaController.model.nextDocumentExisting}"
					onmouseover="$jQ(this).addClass('Btn2Hov_sun4 btnHov_submit');"
					onmouseout="$jQ(this).removeClass('Btn2Hov_sun4 btnHov_submit');"
					value="#{MessageBundleModel.action_chq_ctrl_next}"
					title="#{MessageBundleModel.action_chq_ctrl_next_toolTip}"
					action="#{CustomActionController.apply}"
					oncomplete="currentIdx++; resetModalPanelContent(); "
					reRender="notifications, modalPanelError, chekControllerPanel"
					status="cheksLoadingStatus">
					<f:setPropertyActionListener value="NEXT_CHEK"
						target="#{CustomActionModel.modalPanelModel.ctrlCheckAction}" />
				</a4j:commandButton>

				<a4j:commandButton id="wait" styleClass="Btn2_sun4 btn_submit wait"
					rendered="true"
					onmouseover="$jQ(this).addClass('Btn2Hov_sun4 btnHov_submit');"
					onmouseout="$jQ(this).removeClass('Btn2Hov_sun4 btnHov_submit');"
					value="#{MessageBundleModel.action_chq_ctrl_wait}"
					title="#{MessageBundleModel.action_chq_ctrl_wait_toolTip}"
					action="#{CustomActionController.apply}" ignoreDupResponses="true"
					oncomplete="resetModalPanelContent();"
					reRender="notifications, modalPanelError, chekControllerPanel"
					status="cheksLoadingStatus">
					<f:setPropertyActionListener value="WAIT"
						target="#{CustomActionModel.modalPanelModel.ctrlCheckAction}" />
				</a4j:commandButton>

				<a4j:commandButton id="refused"
					styleClass="Btn2_sun4 btn_submit refused" rendered="true"
					onmouseover="$jQ(this).addClass('Btn2Hov_sun4 btnHov_submit');"
					onmouseout="$jQ(this).removeClass('Btn2Hov_sun4 btnHov_submit');"
					value="#{MessageBundleModel.action_chq_ctrl_refused}"
					title="#{MessageBundleModel.action_chq_ctrl_refused_toolTip}"
					action="#{CustomActionController.apply}"
					oncomplete="resetModalPanelContent();"
					reRender="notifications, modalPanelError, chekControllerPanel"
					status="cheksLoadingStatus">
					<f:setPropertyActionListener value="REFUSED"
						target="#{CustomActionModel.modalPanelModel.ctrlCheckAction}" />
				</a4j:commandButton>

				<a4j:commandButton id="validate"
					styleClass="Btn2_sun4 btn_submit validate" rendered="true"
					onmouseover="$jQ(this).addClass('Btn2Hov_sun4 btnHov_submit');"
					onmouseout="$jQ(this).removeClass('Btn2Hov_sun4 btnHov_submit');"
					value="#{MessageBundleModel.action_chq_ctrl_validate}"
					title="#{MessageBundleModel.action_chq_ctrl_validate_toolTip}"
					action="#{CustomActionController.apply}" ignoreDupResponses="true"
					oncomplete="resetModalPanelContent();"
					reRender="notifications, modalPanelError, chekControllerPanel"
					status="cheksLoadingStatus">
					<f:setPropertyActionListener value="VALIDATE"
						target="#{CustomActionModel.modalPanelModel.ctrlCheckAction}" />
				</a4j:commandButton>
				<a4j:commandButton id="previousDocument"
					styleClass="Btn2_sun4 btn_submit" rendered="true"
					disabled="#{!MultipleVisaController.model.previousDocumentExisting}"
					onmouseover="$jQ(this).addClass('Btn2Hov_sun4 btnHov_submit');"
					onmouseout="$jQ(this).removeClass('Btn2Hov_sun4 btnHov_submit');"
					value="#{MessageBundleModel.action_chq_ctrl_previous}"
					title="#{MessageBundleModel.action_chq_ctrl_previous_toolTip}"
					action="#{CustomActionController.apply}"
					oncomplete="currentIdx--; resetModalPanelContent();"
					reRender="notifications, modalPanelError, chekControllerPanel"
					status="cheksLoadingStatus">
					<f:setPropertyActionListener value="PREVIOUS_CHEK"
						target="#{CustomActionModel.modalPanelModel.ctrlCheckAction}" />
				</a4j:commandButton>
			</a4j:outputPanel>
		</h:panelGroup>
	</h:panelGroup>



</jsp:root>
