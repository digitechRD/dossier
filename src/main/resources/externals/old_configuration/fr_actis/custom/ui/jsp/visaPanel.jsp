<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core"
          xmlns:h="http://java.sun.com/jsf/html"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:webuijsf="http://www.sun.com/webui/webuijsf"
          xmlns:a4j="http://richfaces.org/a4j"
          xmlns:dt="http://ged.digitech.com/jsf/html"
          xmlns:dossier="http://dossier.digitech.com/jsf/html">

  <h:panelGroup id="ajaxStatus" layout="block"
                style="position: absolute; right: 10px; margin-top: 5px;">
    <a4j:status forceId="true" id="visaLoadingStatus" layout="block">
      <f:facet name="start">
        <h:graphicImage value="#{ImageBundleModel.waiting_request2}"/>
      </f:facet>
    </a4j:status>
  </h:panelGroup>

  <a4j:jsFunction name="refreshResponseAfterUpload"
                  action="#{VisaController.updateResponseAfterUpload}"
                  reRender="notifications, modalPanelError, documentViewerOut"
                  limitToList="true" ajaxSingle="true" ignoreDupResponses="true"
                  status="visaLoadingStatus" oncomplete="optimizeAttachmentOutDisplay();"/>

  <script>
      modalPanelCustomAction_refresh = function () {
          refreshResponseAfterUpload();
      };

      modalPanelCustomAction_onShow = function () {
          $jQ(jsfIDtoJQID('modalPanelCustomActionForm:ok')).addClass('disabled').attr('disabled', 'disabled');

          $jQ(".visaPanel .cb-enable").click(function () {
              var parent = $jQ(this).parents('.switch');
              $jQ('.cb-disable', parent).removeClass('selected');
              $jQ(this).addClass('selected');
              $jQ('.checkbox', parent).attr('checked', true);
              $jQ(jsfIDtoJQID('modalPanelCustomActionForm:ok')).removeClass('disabled').attr('disabled', '');
          });
          $jQ(".visaPanel .cb-disable").click(function () {
              var parent = $jQ(this).parents('.switch');
              $jQ('.cb-enable', parent).removeClass('selected');
              $jQ(this).addClass('selected');
              $jQ('.checkbox', parent).attr('checked', false);
              $jQ(jsfIDtoJQID('modalPanelCustomActionForm:ok')).removeClass('disabled').attr('disabled', '');
          });

          var attachmentInDocumentViewer = $jQ('.visaPanel .attachmentInViewerPanel .documentViewer');
          var attachmentInViewerIFrame = $jQ('.visaPanel .attachmentInViewerPanel iframe.attachment');
          if (attachmentInDocumentViewer.is(':visible') || attachmentInViewerIFrame.is(':visible')) {
              $jQ('.visaPanel td').css('width', '50%');
          }
          else {
              $jQ('.visaPanel td:nth-child(2)').css('width', '100%');
          }

          modalPanelCustomAction_onResizeCompleted();
      };

      modalPanelCustomAction_onResizeCompleted = function () {
          resetModalPanelContent();
          var modalPanelContentPanel = $jQ('#modalPanelCustomActionContentTable .rich-mpnl-body');
          var commentPanel = $jQ('.visaPanel .commentPanel');
          var modalPanelContentPanelHeight = modalPanelContentPanel.innerHeight() - commentPanel.innerHeight() - 90;

          optimizeAttachmentInDisplay(modalPanelContentPanelHeight);
          optimizeAttachmentOutDisplay(modalPanelContentPanelHeight);
      };

      optimizeAttachmentInDisplay = function (modalPanelContentPanelHeight) {
          if (!modalPanelContentPanelHeight) {
              var modalPanelContentPanel = $jQ('#modalPanelCustomActionContentTable .rich-mpnl-body');
              var commentPanel = $jQ('.visaPanel .commentPanel');
              modalPanelContentPanelHeight = modalPanelContentPanel.innerHeight() - commentPanel.innerHeight() - 90;
          }
          var attachmentInViewerPanel = $jQ('.visaPanel .attachmentInViewerPanel div.attachment');
          var attachmentInDocumentViewer = $jQ('.visaPanel .attachmentInViewerPanel .documentViewer');
          var attachmentInViewerIFrame = $jQ('.visaPanel .attachmentInViewerPanel iframe.attachment');

          if (attachmentInDocumentViewer.is(':visible') || attachmentInViewerIFrame.is(':visible')) {
              // Courrier IN
              attachmentInViewerPanel.css('height', modalPanelContentPanelHeight + 'px');
              var attachmentInViewerPanelHeight = attachmentInViewerPanel.innerHeight() - 10;
              var attachmentInViewerPanelWidth = attachmentInViewerPanel.innerWidth();
              attachmentInDocumentViewer.css('height', attachmentInViewerPanelHeight + 'px').css('width', attachmentInViewerPanelWidth + 'px');
              $jQ('.visaPanel .attachmentInViewerPanel .documentViewer .toolbarDisplayer').css('width', attachmentInViewerPanelWidth + 'px');
              $jQ('.visaPanel .attachmentInViewerPanel .documentViewer .toolbar').css('width', (attachmentInViewerPanelWidth - 24) + 'px');
              attachmentInViewerIFrame.css('height', attachmentInViewerPanelHeight + 'px').css('width', (attachmentInViewerPanelWidth - 10) + 'px');

              documentViewerIn_computeContainerSizeFuntion($jQ(
                  jsfIDtoJQID('modalPanelCustomActionForm:customActionInclusion:subviewIn:zoomSelectOneMenu')).val());

              setTimeout(initDocumentViewerToolbar, 500);
          }
      };

      optimizeAttachmentOutDisplay = function (modalPanelContentPanelHeight) {
          if (!modalPanelContentPanelHeight) {
              var modalPanelContentPanel = $jQ('#modalPanelCustomActionContentTable .rich-mpnl-body');
              var commentPanel = $jQ('.visaPanel .commentPanel');
              modalPanelContentPanelHeight = modalPanelContentPanel.innerHeight() - commentPanel.innerHeight() - 90;
          }
          var attachmentOutViewerPanel = $jQ('.visaPanel .attachmentOutViewerPanel .attachment');
          var attachmentOutDocumentViewer = $jQ('.visaPanel .attachmentOutViewerPanel .documentViewer');

          // Template
          attachmentOutViewerPanel.css('height', modalPanelContentPanelHeight + 'px');
          var attachmentOutViewerPanelHeight = attachmentOutViewerPanel.innerHeight() - 10;
          var attachmentOutViewerPanelWidth = attachmentOutViewerPanel.innerWidth();
          attachmentOutDocumentViewer.css('height', attachmentOutViewerPanelHeight + 'px').css('width', attachmentOutViewerPanelWidth + 'px');
          $jQ('.visaPanel .attachmentOutViewerPanel .documentViewer .toolbarDisplayer').css('width', attachmentOutViewerPanelWidth + 'px');
          $jQ('.visaPanel .attachmentOutViewerPanel .documentViewer .toolbar').css('width', (attachmentOutViewerPanelWidth - 24) + 'px');

          documentViewerOut_computeContainerSizeFuntion($jQ(
              jsfIDtoJQID('modalPanelCustomActionForm:customActionInclusion:subviewOut:zoomSelectOneMenu')).val());

          setTimeout(initDocumentViewerToolbar, 500);
      };

      resetModalPanelContent = function () {
          // In
          var attachmentInViewerPanel = $jQ('.visaPanel .attachmentInViewerPanel div.attachment');
          var attachmentInViewerIFrame = $jQ('.visaPanel .attachmentInViewerPanel iframe.attachment');
          var attachmentInDocumentViewer = $jQ('.visaPanel .attachmentInViewerPanel .documentViewer');
          attachmentInViewerPanel.css('height', '1px');
          attachmentInDocumentViewer.css('height', '1px').css('width', '1px');
          attachmentInViewerIFrame.css('height', '1px').css('width', '1px');

          // Out
          var attachmentOutViewerPanel = $jQ('.visaPanel .attachmentOutViewerPanel .attachment');
          var attachmentOutDocumentViewer = $jQ('.visaPanel .attachmentOutViewerPanel .documentViewer');
          attachmentOutViewerPanel.css('height', '1px');
          attachmentOutDocumentViewer.css('height', '1px').css('width', '1px');
      };
  </script>

  <h:panelGroup styleClass="visaPanel">
    <h:panelGrid columns="2" styleClass="visaPanel">
      <h:panelGroup>
        <h:panelGroup
            rendered="#{not empty VisaModel.availableAttachmentsIn}">
          <h:outputLabel
              value="#{MessageBundleModel.label_select_item_attachment}"/>
          <h:selectOneMenu value="#{VisaModel.selectedAttachmentIn}"
                           converter="#{VisaModel.attachmentConverter}">
            <f:selectItems value="#{VisaModel.availableAttachmentsIn}"/>
            <a4j:support event="onchange" ajaxSingle="true" limitToList="true"
                         ignoreDupResponses="true" reRender="attachmentInViewer"
                         status="visaLoadingStatus"
                         oncomplete="optimizeAttachmentInDisplay();"/>
          </h:selectOneMenu>
        </h:panelGroup>
      </h:panelGroup>
      <h:panelGroup id="buttonPanelGroup">
        <h:outputLabel
            value="#{MessageBundleModel.label_select_item_template}"/>
        <h:selectOneMenu value="#{VisaModel.selectedAttachmentOut}"
                         converter="#{VisaModel.attachmentConverter}">
          <f:selectItems value="#{VisaModel.availableAttachmentsOut}"/>
          <a4j:support event="onchange" ajaxSingle="true" limitToList="true"
                       ignoreDupResponses="true" reRender="documentViewerOut"
                       status="visaLoadingStatus"
                       oncomplete="optimizeAttachmentOutDisplay();"/>
        </h:selectOneMenu>

        <a4j:commandLink immediate="true"
                         title="#{MessageBundleModel.action_edit}"
                         actionListener="#{VisaController.editAttachmentListener}"
                         status="responseLoadingStatus"
                         oncomplete="window.open('./popup-editAttachment.jspx');">
          <h:graphicImage value="#{ImageBundleModel.icon_picker_edit}"/>
        </a4j:commandLink>
      </h:panelGroup>
      <h:panelGroup id="attachmentInViewer" layout="block"
                    styleClass="attachmentInViewerPanel">
        <a4j:outputPanel id="attachmentInLayoutUnitContent" layout="block"
                         styleClass="attachmentViewer"
                         rendered="#{not empty VisaModel.availableAttachmentsIn and VisaModel.attachmentInModel.existingAttachment}">

          <h:panelGroup layout="block" styleClass="attachment">
            <h:panelGroup id="notificationsIn" layout="block">
              <dossier:notification id="notificationIn"
                                    model="#{VisaModel.attachmentInModel.message}"/>
            </h:panelGroup>
            <h:panelGroup
                rendered="#{empty VisaModel.attachmentInModel.message}">
              <h:outputText style="z-index: 2;"
                            rendered="#{!VisaModel.attachmentInModel.existingAttachment}"
                            value="#{MessageBundleModel.attachment_label_noAttachment}"/>

              <h:panelGrid columns="2">
                <a4j:queue id="documentViewerQueueIn" requestDelay="500"
                           ignoreDupResponses="true"/>
                <f:subview id="subviewIn">
                  <dt:documentViewer id="documentViewerIn"
                                     styleClass="documentViewer"
                                     rendered="#{VisaModel.attachmentInModel.existingAttachment and VisaModel.attachmentInModel.documentViewerDisplayed}"
                                     value="#{VisaModel.attachmentInModel.attachmentFilePath}"
                                     eventsQueue="documentViewerQueueIn" pagerDisplayed="true"
                                     zoomDisplayed="true" defaultZoom="FIT_TO_PAGE"
                                     rotatorDisplayed="true" searchDisplayed="true"
                                     annotatorDisplayed="true" imageCropperEnabled="true"
                                     magnifyingGlassEnabled="false" outlineEnabled="true"
                                     thumbnailsEnabled="true" contextMenuEnabled="true"
                                     thumbnailsLazyloadEnabled="#{ApplicationModel.userAgent.browser.group != 'IE'}"
                                     annotationNoteDynamic="#{VisaModel.attachmentInModel.annotationNoteDynamic}"
                                     annotationNoteMetadataDisplayed="#{ResponseModel.attachmentInModel.annotationNoteMetadataDisplayed}"
                                     annotationDisplayedOnShow="true"
                                     annotationFilePath="#{VisaModel.attachmentInModel.annotationFilePath}"
                                     reRender="attachmentInLayoutUnitContent"
                                     status="visaLoadingStatus">
                  </dt:documentViewer>
                </f:subview>

              </h:panelGrid>
              <script>
                  initDocumentViewerToolbar();
              </script>

              <webuijsf:iframe styleClass="attachment" height="100%"
                               width="100%"
                               rendered="#{VisaModel.attachmentInModel.existingAttachment and !VisaModel.attachmentInModel.documentViewerDisplayed}"
                               url="#{VisaModel.attachmentInModel.attachmentURL}"/>
            </h:panelGroup>
          </h:panelGroup>
        </a4j:outputPanel>
      </h:panelGroup>

      <h:panelGroup id="attachmentOutViewer" layout="block"
                    styleClass="attachmentOutViewerPanel">

        <a4j:outputPanel id="attachmentOutLayoutUnitContent" layout="block"
                         styleClass="attachmentViewer"
                         rendered="#{VisaModel.attachmentOutModel.existingAttachment}">

          <h:panelGroup layout="block" styleClass="attachment">
            <h:panelGroup id="notificationsOut" layout="block">
              <dossier:notification id="notificationOut"
                                    model="#{VisaModel.attachmentOutModel.message}"/>
            </h:panelGroup>
            <h:panelGroup
                rendered="#{empty VisaModel.attachmentOutModel.message}">
              <h:outputText style="z-index: 2;"
                            rendered="#{!VisaModel.attachmentOutModel.existingAttachment}"
                            value="#{MessageBundleModel.attachment_label_noAttachment}"/>

              <h:panelGrid columns="2">
                <a4j:queue id="documentViewerQueueOut" requestDelay="500"
                           ignoreDupResponses="true"/>
                <f:subview id="subviewOut">
                  <dt:documentViewer id="documentViewerOut"
                                     styleClass="documentViewer"
                                     rendered="#{VisaModel.attachmentOutModel.existingAttachment and VisaModel.attachmentOutModel.documentViewerDisplayed}"
                                     value="#{VisaModel.attachmentOutModel.attachmentFilePath}"
                                     eventsQueue="documentViewerQueueOut" pagerDisplayed="true"
                                     zoomDisplayed="true" defaultZoom="FIT_TO_PAGE"
                                     rotatorDisplayed="true" searchDisplayed="true"
                                     annotatorDisplayed="true" imageCropperEnabled="true"
                                     magnifyingGlassEnabled="false" outlineEnabled="true"
                                     thumbnailsEnabled="true" contextMenuEnabled="true"
                                     thumbnailsLazyloadEnabled="#{ApplicationModel.userAgent.browser.group != 'IE'}"
                                     annotationNoteDynamic="#{VisaModel.attachmentOutModel.annotationNoteDynamic}"
                                     annotationNoteMetadataDisplayed="#{ResponseModel.attachmentOutModel.annotationNoteMetadataDisplayed}"
                                     annotationDisplayedOnShow="true"
                                     annotationFilePath="#{VisaModel.attachmentOutModel.annotationFilePath}"
                                     reRender="attachmentOutLayoutUnitContent"
                                     status="visaLoadingStatus">
                  </dt:documentViewer>
                </f:subview>

              </h:panelGrid>
              <script>
                  initDocumentViewerToolbar();
              </script>

              <webuijsf:iframe styleClass="attachment" height="100%"
                               width="100%"
                               rendered="#{VisaModel.attachmentOutModel.existingAttachment and !VisaModel.attachmentOutModel.documentViewerDisplayed}"
                               url="#{VisaModel.attachmentOutModel.attachmentURL}"/>
            </h:panelGroup>
          </h:panelGroup>
        </a4j:outputPanel>
      </h:panelGroup>
    </h:panelGrid>
    <h:panelGrid columns="2" styleClass="commentPanel">
      <h:panelGroup>
        <h:panelGroup layout="block">
          <h:outputLabel
              value="#{MessageBundleModel.modalPanelComment_label_comment}"
              for="inputComment"/>
          <h:inputTextarea id="inputComment"
                           style="#{not empty VisaModel.availableAttachmentsIn and VisaModel.attachmentInModel.existingAttachment ? 'width: 500px;' : 'width: 250px;'} "
                           value="#{VisaModel.comment}"/>
        </h:panelGroup>
        <h:panelGroup layout="block">
          <h:outputLabel
              value="#{MessageBundleModel.panelSharing_label_public}"
              for="public"/>
          <h:selectBooleanCheckbox id="public"
                                   value="#{VisaModel.public}"/>
          <h:message showDetail="true" for="public" styleClass="message"/>
        </h:panelGroup>
      </h:panelGroup>

      <h:panelGroup styleClass="field switch" layout="block">
        <label class="cb-enable"><span>${MessageBundleModel.label_checkbox_true}</span></label>
        <label class="cb-disable selected"><span>${MessageBundleModel.label_checkbox_false}</span></label>
        <h:selectBooleanCheckbox id="visaResponseType" styleClass="checkbox"
                                 value="#{VisaModel.selectedVisaType}"/>
      </h:panelGroup>
    </h:panelGrid>
  </h:panelGroup>
</jsp:root>