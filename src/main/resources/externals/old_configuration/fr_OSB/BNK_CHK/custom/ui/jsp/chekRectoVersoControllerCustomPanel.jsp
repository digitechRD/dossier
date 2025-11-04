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
	</script>



	<script>
		modalPanelCustomAction_onShow = function() {
			// Attachment layout shortkeys            
			$jQ(document).bind('keyup',{combi : 'v',disableInInput : true},function() {$jQ(jsfIDtoJQID('modalPanelCustomActionForm:customActionInclusion:validate')).click();});
			$jQ(document).bind('keyup',{combi : 'r',disableInInput : true},function() {$jQ(jsfIDtoJQID('modalPanelCustomActionForm:customActionInclusion:refused')).click();});
			$jQ(document).bind('keyup',{combi : 'a',disableInInput : true},function() {$jQ(jsfIDtoJQID('modalPanelCustomActionForm:customActionInclusion:wait')).click();});

			// up and down		
			$jQ(document).bind('keyup',{combi : 'left',disableInInput : true},function() {$jQ(jsfIDtoJQID('modalPanelCustomActionForm:customActionInclusion:previousDocument')).click();});
			$jQ(document).bind('keyup',{combi : 'right',disableInInput : true},function() {$jQ(jsfIDtoJQID('modalPanelCustomActionForm:customActionInclusion:nextDocument')).click();});

			modalPanelCustomAction_onResizeCompleted();
		}
		modalPanelCustomAction_onHide = function() {
			// Attachment layout shortkeys            
			$jQ(document).unbind('keyup', 'v');
			$jQ(document).unbind('keyup', 'r');
			$jQ(document).unbind('keyup', 'a');
			// up and down		
			$jQ(document).unbind('keyup', 'up');
			$jQ(document).unbind('keyup', 'down');
		}

		modalPanelCustomAction_onResizeCompleted = function() {

			resetModalPanelContent();
			var modalPanelContentPanel = $jQ('#modalPanelCustomActionContentTable .rich-mpnl-body');
			var modalPanelContentPanelHeight = modalPanelContentPanel
					.innerHeight() - 110;

			optimizeAttachmentDisplay(modalPanelContentPanelHeight);
		};

		optimizeAttachmentDisplay = function(modalPanelContentPanelHeight,
				modalPanelContentPanelWidth) {

			if (!modalPanelContentPanelHeight) {
				var modalPanelContentPanel = $jQ('#modalPanelCustomActionContentTable .rich-mpnl-body');
				modalPanelContentPanelHeight = modalPanelContentPanel
						.innerHeight() - 110;
			}

			// compute the display
			var viewerHeight = modalPanelContentPanelHeight * 0.45;
			var viewerwidth = modalPanelContentPanelWidth;
			var attachmentViewerPanelHeight = viewerHeight - 10;
			var attachmentViewerPanelWidth = viewerwidth - 10;

			// Recto optimization
			var attachmentInViewerPanel = $jQ('.parentContainerRectoVerso .attachmentInRectoPanel div.attachment');
			var attachmentInViewerIFrame = $jQ('.parentContainerRectoVerso .attachmentInRectoPanel iframe.attachment');
			var attachmentInDocumentViewer = $jQ('.parentContainerRectoVerso .attachmentInRectoPanel .documentViewer');

			if (attachmentInDocumentViewer.is(':visible')) {
				// Courrier IN
				attachmentInViewerPanel.css('height', viewerHeight + 'px');
				attachmentInDocumentViewer.css('height',
						attachmentViewerPanelHeight + 'px').css('width',
						attachmentViewerPanelWidth + 'px');
				$jQ(
						'.parentContainerRectoVerso .attachmentInRectoPanel .documentViewer .toolbarDisplayer')
						.css('width', attachmentViewerPanelWidth + 'px');
				$jQ(
						'.parentContainerRectoVerso .attachmentInRectoPanel .documentViewer .toolbar')
						.css('width', (attachmentViewerPanelWidth - 24) + 'px');
				attachmentInViewerIFrame.css('height',
						attachmentViewerPanelHeight + 'px').css('width',
						(attachmentViewerPanelWidth - 10) + 'px');

				//attachmentInDocumentViewer_computeContainerSizeFuntion('FIT_TO_PAGE');

				setTimeout(initDocumentViewerToolbar, 500);
			}

			// Verso optimization
			var attachmentInViewerPanel = $jQ('.parentContainerRectoVerso .attachmentVersoViewerPanel div.attachment');
			var attachmentInViewerIFrame = $jQ('.parentContainerRectoVerso .attachmentVersoViewerPanel iframe.attachment');
			var attachmentInDocumentViewer = $jQ('.parentContainerRectoVerso .attachmentVersoViewerPanel .documentViewer');

			if (attachmentInDocumentViewer.is(':visible')) {
				// Courrier IN
				attachmentInViewerPanel.css('height', viewerHeight + 'px');
				attachmentInDocumentViewer.css('height',
						attachmentViewerPanelHeight + 'px').css('width',
						attachmentViewerPanelWidth + 'px');
				$jQ(
						'.parentContainerRectoVerso .attachmentVersoViewerPanel .documentViewer .toolbarDisplayer')
						.css('width', attachmentViewerPanelWidth + 'px');
				$jQ(
						'.parentContainerRectoVerso .attachmentVersoViewerPanel .documentViewer .toolbar')
						.css('width', (attachmentViewerPanelWidth - 24) + 'px');
				attachmentInViewerIFrame.css('height',
						attachmentViewerPanelHeight + 'px').css('width',
						(attachmentViewerPanelWidth - 10) + 'px');

				//attachmentInDocumentViewer_computeContainerSizeFuntion('FIT_TO_PAGE');

				setTimeout(initDocumentViewerToolbar, 500);
			}

			// the comment 
			var commentPanel = $jQ('.parentContainerRectoVerso .commentPanel');
			var commenttHeight = modalPanelContentPanelHeight * 0.1;
		commentPanel.css('height', commenttHeight + 'px').css('width',attachmentViewerPanelWidth + 'px').css('padding','0');
		};

		resetModalPanelContent = function() {
			// In
			var attachmentInViewerPanel = $jQ('.parentContainerRectoVerso .attachmentInRectoPanel div.attachment');
			var attachmentInViewerIFrame = $jQ('.parentContainerRectoVerso .attachmentInRectoPanel iframe.attachment');
			var attachmentInDocumentViewer = $jQ('.document .attachmentRectoViewer .documentViewer');
			attachmentInViewerPanel.css('height', '1px');
			attachmentInDocumentViewer.css('height', '1px').css('width', '1px');
			attachmentInViewerIFrame.css('height', '1px').css('width', '1px');

			// Out
			var attachmentOutViewerPanel = $jQ('.parentContainerRectoVerso .attachmentVersoViewerPanel div.attachment');
			var attachmentOutViewerIFrame = $jQ('.parentContainerRectoVerso .attachmentVersoViewerPanel iframe.attachment');
			var attachmentOutDocumentViewer = $jQ('.document .attachmentVersoViewerPanel .documentViewer');
			attachmentOutViewerPanel.css('height', '1px');
			attachmentOutDocumentViewer.css('height', '1px')
					.css('width', '1px');
			attachmentOutViewerIFrame.css('height', '1px').css('width', '1px');
		};
	</script>

	<h:panelGroup id="chekControllerPanel" styleClass="rectVersoPanel">
		<h:panelGrid column="2" styleClass="parentContainerRectoVerso">

			<h:panelGroup  layout="block" style="text-align: center;">
				<h:panelGroup layout="block" styleClass="document row" id="documentPager">
					<a4j:commandLink id="previousSign" styleClass="Hyp_sun4 disabled"
						value="#{MessageBundleModel.attachment_hyperlink_previousSignature}"
						disabled="true" />
					<h:outputText	value="#{MessageBundleModel.document_pager_name} #{MultipleVisaController.model.currentDocumentIndex + 1}/#{MultipleVisaController.model.selectedDocumentsListSize}" />
					<a4j:commandLink id="nextSign"
						styleClass="Hyp_sun4 disabled"
						value="#{MessageBundleModel.attachment_hyperlink_nextSignature}"						
						disabled="true" />				
				</h:panelGroup>
			</h:panelGroup>
			
			<h:panelGroup id="attachmentRectoViewer" layout="block"
				styleClass="attachmentInRectoPanel">
				<a4j:outputPanel id="attachmentRectoLayoutUnitContent"
					layout="block" styleClass="attachmentViewer">

					<h:panelGroup layout="block" styleClass="attachment">
						<h:panelGroup id="notificationsRecto" layout="block">
							<dossier:notification id="notificationRecto"
								model="#{MultipleVisaController.model.attachmentInModel.message}" />
						</h:panelGroup>
						<h:panelGroup
							rendered="#{empty MultipleVisaController.model.attachmentInModel.message}">
							<h:panelGrid columns="2">
								<a4j:queue id="documentViewerQueueRecto" requestDelay="500"
									ignoreDupResponses="true" />
								<f:subview id="subviewIn">
									<dt:documentViewer id="attachmentRecto"
										styleClass="documentViewer"
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



			<h:panelGroup id="attachmentVersoViewer" layout="block"
				styleClass="attachmentVersoViewerPanel">
				<a4j:outputPanel id="attachmentSignLayoutUnitContent" layout="block"
					styleClass="attachmentViewer">

					<h:panelGroup layout="block" styleClass="attachment">
						<h:panelGroup id="notificationsVersoPanel" layout="block">
							<dossier:notification id="notificationsSign"
								model="#{MultipleVisaController.model.attachmentOutModel.message}" />
						</h:panelGroup>
						<h:panelGroup
							rendered="#{empty MultipleVisaController.model.attachmentOutModel.message}">
							<h:panelGrid columns="2">
								<a4j:queue id="documentViewerQueueVerso" requestDelay="500"
									ignoreDupResponses="true" />
								<f:subview id="subviewSign">
									<dt:documentViewer id="attachmentVerso"
										styleClass="documentViewer"
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
						</h:panelGroup>
					</h:panelGroup>
				</a4j:outputPanel>


			</h:panelGroup>

			<h:panelGroup  style="text-align: center;">
				<h:panelGroup layout="block"  styleClass="document row">
					<h:outputText
						value="#{MessageBundleModel.modalPanelComment_label_comment}"
						for="inputComment" />
				</h:panelGroup>
			</h:panelGroup>
			<h:panelGroup>
				<h:panelGroup>
					<h:panelGroup layout="block" styleClass="document row">
						<h:inputTextarea id="inputComment" styleClass="commentPanel"
							style="resize: none; width: 600px;margin-left: 7px;"
							value="#{MultipleVisaController.model.comment}" />
					</h:panelGroup>
				</h:panelGroup>
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
						oncomplete="modalPanelCustomAction_onResizeCompleted();"
						reRender="notifications, modalPanelError,  previousDocument, nextDocument, attachmentRectoLayoutUnitContent, attachmentSignLayoutUnitContent, inputComment, actionButton, documentPager"
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
						oncomplete="modalPanelCustomAction_onResizeCompleted();"
						reRender="notifications, modalPanelError,  previousDocument, nextDocument, attachmentRectoLayoutUnitContent, attachmentSignLayoutUnitContent, inputComment, actionButton, documentPager"
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
						oncomplete="modalPanelCustomAction_onResizeCompleted();"
						reRender="notifications, modalPanelError,  previousDocument, nextDocument, attachmentRectoLayoutUnitContent, attachmentSignLayoutUnitContent, inputComment, actionButton, documentPager"
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
						oncomplete="modalPanelCustomAction_onResizeCompleted();"
						reRender="notifications, modalPanelError,  previousDocument, nextDocument, attachmentRectoLayoutUnitContent, attachmentSignLayoutUnitContent, inputComment, actionButton, documentPager"
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
						oncomplete="modalPanelCustomAction_onResizeCompleted();"
						reRender="notifications, modalPanelError,  previousDocument, nextDocument, attachmentRectoLayoutUnitContent, attachmentSignLayoutUnitContent, inputComment, actionButton, documentPager"
						status="cheksLoadingStatus">
						<f:setPropertyActionListener value="PREVIOUS_CHEK"
							target="#{CustomActionModel.modalPanelModel.ctrlCheckAction}" />
					</a4j:commandButton>



				</a4j:outputPanel>
			</h:panelGroup>
		</h:panelGrid>
	</h:panelGroup>
</jsp:root>