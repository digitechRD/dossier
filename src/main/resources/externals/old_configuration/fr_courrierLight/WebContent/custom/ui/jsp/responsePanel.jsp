<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core"
          xmlns:h="http://java.sun.com/jsf/html"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:webuijsf="http://www.sun.com/webui/webuijsf"
          xmlns:a4j="http://richfaces.org/a4j"
          xmlns:dt="http://ged.digitech.com/jsf/html"
          xmlns:dossier="http://dossier.digitech.com/jsf/html">

  <a4j:queue id="panelEventQueue" requestDelay="500" ignoreDupResponses="true"/>

  <h:panelGroup id="ajaxStatus" layout="block"
                style="position: absolute; right: 10px; margin-top: 5px;">
    <a4j:status forceId="true" id="responseLoadingStatus" layout="block">
      <f:facet name="start">
        <h:graphicImage value="#{ImageBundleModel.waiting_request2}"/>
      </f:facet>
    </a4j:status>
  </h:panelGroup>

  <a4j:jsFunction name="refreshResponseAfterUpload"
                  action="#{ResponseController.updateResponseAfterUpload}"
                  reRender="notifications, modalPanelError, documentViewerOut"
                  limitToList="true" ajaxSingle="true" ignoreDupResponses="true"
                  status="responseLoadingStatus"
                  oncomplete="optimizeAttachmentOutDisplay();"/>

  <script>
      modalPanelCustomAction_refresh = function () {
          refreshResponseAfterUpload();
      };

      modalPanelCustomAction_show = function () {
          var attachmentInDocumentViewer = $jQ('.responsePanel .attachmentInViewerPanel .documentViewer');
          var attachmentInViewerIFrame = $jQ('.responsePanel .attachmentInViewerPanel iframe.attachment');
          if (attachmentInDocumentViewer.is(':visible') || attachmentInViewerIFrame.is(':visible')) {
              $jQ('.responsePanel td').css('width', '50%');
          }
          else {
              $jQ('.responsePanel td:nth-child(2)').css('width', '100%');
          }

          modalPanelCustomAction_onResizeCompleted();
      };

      modalPanelCustomAction_onResizeCompleted = function () {
          resetModalPanelContent();
          var modalPanelContentPanelHeight = $jQ('#modalPanelCustomActionContentTable .rich-mpnl-body').innerHeight() - 90;

          optimizeAttachmentInDisplay(modalPanelContentPanelHeight);
          optimizeAttachmentOutDisplay(modalPanelContentPanelHeight);
      };

      optimizeAttachmentInDisplay = function (modalPanelContentPanelHeight) {
          if (!modalPanelContentPanelHeight) {
              modalPanelContentPanelHeight = $jQ('#modalPanelCustomActionContentTable .rich-mpnl-body').innerHeight() - 90;
          }
          var attachmentInViewerPanel = $jQ('.responsePanel .attachmentInViewerPanel div.attachment');
          var attachmentInDocumentViewer = $jQ('.responsePanel .attachmentInViewerPanel .documentViewer');
          var attachmentInViewerIFrame = $jQ('.responsePanel .attachmentInViewerPanel iframe.attachment');

          if (attachmentInDocumentViewer.is(':visible') || attachmentInViewerIFrame.is(':visible')) {
              // Courrier IN
              attachmentInViewerPanel.css('height', modalPanelContentPanelHeight + 'px');
              var attachmentInViewerPanelHeight = attachmentInViewerPanel.innerHeight() - 10;
              var attachmentInViewerPanelWidth = attachmentInViewerPanel.innerWidth();
              attachmentInDocumentViewer.css('height', attachmentInViewerPanelHeight + 'px').css('width', attachmentInViewerPanelWidth + 'px');
              $jQ('.responsePanel .attachmentInViewerPanel .documentViewer .toolbarDisplayer').css('width', attachmentInViewerPanelWidth + 'px');
              $jQ('.responsePanel .attachmentInViewerPanel .documentViewer .toolbar').css('width', (attachmentInViewerPanelWidth - 24) + 'px');
              attachmentInViewerIFrame.css('height', attachmentInViewerPanelHeight + 'px').css('width', (attachmentInViewerPanelWidth - 10) + 'px');

              documentViewerIn_computeContainerSizeFuntion($jQ(
                  jsfIDtoJQID('modalPanelCustomActionForm:customActionInclusion:subviewIn:zoomSelectOneMenu')).val());

              setTimeout(initDocumentViewerToolbar, 500);
          }
      };

      optimizeAttachmentOutDisplay = function (modalPanelContentPanelHeight) {
          if (!modalPanelContentPanelHeight) {
              modalPanelContentPanelHeight = $jQ('#modalPanelCustomActionContentTable .rich-mpnl-body').innerHeight() - 90;
          }
          var attachmentOutViewerPanel = $jQ('.responsePanel .attachmentOutViewerPanel .attachment');
          var attachmentOutDocumentViewer = $jQ('.responsePanel .attachmentOutViewerPanel .documentViewer');

          // Template
          attachmentOutViewerPanel.css('height', modalPanelContentPanelHeight + 'px');
          var attachmentOutViewerPanelHeight = attachmentOutViewerPanel.innerHeight() - 10;
          var attachmentOutViewerPanelWidth = attachmentOutViewerPanel.innerWidth();
          attachmentOutDocumentViewer.css('height', attachmentOutViewerPanelHeight + 'px').css('width', attachmentOutViewerPanelWidth + 'px');
          $jQ('.responsePanel .attachmentOutViewerPanel .documentViewer .toolbarDisplayer').css('width', attachmentOutViewerPanelWidth + 'px');
          $jQ('.responsePanel .attachmentOutViewerPanel .documentViewer .toolbar').css('width', (attachmentOutViewerPanelWidth - 24) + 'px');

          documentViewerOut_computeContainerSizeFuntion($jQ(
              jsfIDtoJQID('modalPanelCustomActionForm:customActionInclusion:subviewOut:zoomSelectOneMenu')).val());

          setTimeout(initDocumentViewerToolbar, 500);
      };

      resetModalPanelContent = function () {
          // In
          var attachmentInViewerPanel = $jQ('.responsePanel .attachmentInViewerPanel div.attachment');
          var attachmentInViewerIFrame = $jQ('.responsePanel .attachmentInViewerPanel iframe.attachment');
          var attachmentInDocumentViewer = $jQ('.responsePanel .attachmentInViewerPanel .documentViewer');
          attachmentInViewerPanel.css('height', '1px');
          attachmentInDocumentViewer.css('height', '1px').css('width', '1px');
          attachmentInViewerIFrame.css('height', '1px').css('width', '1px');

          // Out
          var attachmentOutViewerPanel = $jQ('.responsePanel .attachmentOutViewerPanel .attachment');
          var attachmentOutDocumentViewer = $jQ('.responsePanel .attachmentOutViewerPanel .documentViewer');
          attachmentOutViewerPanel.css('height', '1px');
          attachmentOutDocumentViewer.css('height', '1px').css('width', '1px');
      };
  </script>

  <h:panelGrid columns="2" styleClass="responsePanel popup">
    <h:panelGroup>
      <h:panelGroup
          rendered="#{not empty ResponseModel.availableAttachments}">
        <h:outputLabel
            value="#{MessageBundleModel.label_select_item_attachment}"/>
        <h:selectOneMenu value="#{ResponseModel.selectedAttachment}"
                         converter="#{ResponseModel.attachmentConverter}">
          <f:selectItems value="#{ResponseModel.availableAttachments}"/>
          <a4j:support event="onchange" ajaxSingle="true" limitToList="true"
                       ignoreDupResponses="true" reRender="attachmentInViewer"
                       status="responseLoadingStatus"
                       oncomplete="optimizeAttachmentInDisplay();"/>
        </h:selectOneMenu>
      </h:panelGroup>
    </h:panelGroup>
    <h:panelGroup id="buttonPanelGroup">
      <h:outputLabel
          value="#{MessageBundleModel.label_select_item_template}"/>
      <h:selectOneMenu value="#{ResponseModel.selectedTemplate}"
                       immediate="true">
        <f:selectItems value="#{ResponseModel.availableTemplates}"/>
        <a4j:support event="onchange"
                     reRender="documentViewerOut, buttonPanelGroup" limitToList="true"
                     ajaxSingle="true" status="responseLoadingStatus"
                     action="#{ResponseController.generate}"
                     oncomplete="optimizeAttachmentOutDisplay();"/>
      </h:selectOneMenu>

      <a4j:commandLink immediate="true"
                       title="#{MessageBundleModel.action_edit}"
                       actionListener="#{ResponseController.editAttachmentListener}"
                       status="responseLoadingStatus"
                       oncomplete="window.open('./popup-editAttachment.jspx');">
        <h:graphicImage value="#{ImageBundleModel.icon_picker_edit}"/>
      </a4j:commandLink>
    </h:panelGroup>
    <h:panelGroup id="attachmentInViewer" layout="block"
                  styleClass="attachmentInViewerPanel">
      <a4j:outputPanel id="attachmentLayoutUnitContent" layout="block"
                       styleClass="attachmentViewer"
                       rendered="#{not empty ResponseModel.availableAttachments and ResponseModel.attachmentInModel.existingAttachment}">

        <h:panelGroup layout="block" styleClass="attachment">
          <h:panelGroup id="notificationsIn" layout="block">
            <dossier:notification id="notificationIn"
                                  model="#{ResponseModel.attachmentInModel.message}"/>
          </h:panelGroup>
          <h:panelGroup
              rendered="#{empty ResponseModel.attachmentInModel.message}">
            <h:outputText style="z-index: 2;"
                          rendered="#{!ResponseModel.attachmentInModel.existingAttachment}"
                          value="#{MessageBundleModel.attachment_label_noAttachment}"/>

            <h:panelGrid columns="2">

              <f:subview id="subviewIn">
                <dt:documentViewer id="documentViewerIn"
                                   styleClass="documentViewer"
                                   rendered="#{ResponseModel.attachmentInModel.existingAttachment and ResponseModel.attachmentInModel.documentViewerDisplayed}"
                                   value="#{ResponseModel.attachmentInModel.attachmentFilePath}"
                                   eventsQueue="panelEventQueue" pagerDisplayed="true"
                                   zoomDisplayed="true" defaultZoom="FIT_TO_PAGE"
                                   rotatorDisplayed="true" searchDisplayed="true"
                                   annotatorDisplayed="true" imageCropperEnabled="true"
                                   magnifyingGlassEnabled="false" outlineEnabled="true"
                                   thumbnailsEnabled="true" contextMenuEnabled="true"
                                   annotationNoteDynamic="#{ResponseModel.attachmentInModel.annotationNoteDynamic}"
                                   annotationNoteMetadataDisplayed="#{ResponseModel.attachmentInModel.annotationNoteMetadataDisplayed}"
                                   annotationDisplayedOnShow="true"
                                   annotationFilePath="#{ResponseModel.attachmentInModel.annotationFilePath}"
                                   reRender="attachmentLayoutUnitContent"
                                   status="responseLoadingStatus">
                </dt:documentViewer>
              </f:subview>
            </h:panelGrid>

            <webuijsf:iframe styleClass="attachment" height="100%"
                             width="100%"
                             rendered="#{ResponseModel.attachmentInModel.existingAttachment and !ResponseModel.attachmentInModel.documentViewerDisplayed}"
                             url="#{ResponseModel.attachmentInModel.attachmentURL}"/>
          </h:panelGroup>
        </h:panelGroup>
      </a4j:outputPanel>
    </h:panelGroup>

    <h:panelGroup id="attachmentOutViewer" layout="block"
                  styleClass="attachmentOutViewerPanel">

      <a4j:outputPanel id="attachmentOutLayoutUnitContent" layout="block"
                       styleClass="attachmentViewer"
                       rendered="#{ResponseModel.attachmentOutModel.existingAttachment}">

        <h:panelGroup layout="block" styleClass="attachment">
          <h:panelGroup id="notificationsOut" layout="block">
            <dossier:notification id="notificationOut"
                                  model="#{ResponseModel.attachmentOutModel.message}"/>
          </h:panelGroup>
          <h:panelGroup
              rendered="#{empty ResponseModel.attachmentOutModel.message}">
            <h:outputText style="z-index: 2;"
                          rendered="#{!ResponseModel.attachmentOutModel.existingAttachment}"
                          value="#{MessageBundleModel.attachment_label_noAttachment}"/>

            <h:panelGrid columns="2">
              <f:subview id="subviewOut">
                <dt:documentViewer id="documentViewerOut"
                                   styleClass="documentViewer"
                                   rendered="#{ResponseModel.attachmentOutModel.existingAttachment and ResponseModel.attachmentOutModel.documentViewerDisplayed}"
                                   value="#{ResponseModel.attachmentOutModel.attachmentFilePath}"
                                   eventsQueue="panelEventQueue" pagerDisplayed="true"
                                   zoomDisplayed="true" defaultZoom="FIT_TO_PAGE"
                                   rotatorDisplayed="true" searchDisplayed="true"
                                   annotatorDisplayed="true" imageCropperEnabled="true"
                                   magnifyingGlassEnabled="false" outlineEnabled="true"
                                   thumbnailsEnabled="true" contextMenuEnabled="true"
                                   annotationNoteDynamic="#{ResponseModel.attachmentOutModel.annotationNoteDynamic}"
                                   annotationNoteMetadataDisplayed="#{ResponseModel.attachmentOutModel.annotationNoteMetadataDisplayed}"
                                   annotationDisplayedOnShow="true"
                                   annotationFilePath="#{ResponseModel.attachmentOutModel.annotationFilePath}"
                                   reRender="attachmentOutLayoutUnitContent"
                                   status="responseLoadingStatus">
                </dt:documentViewer>
              </f:subview>
            </h:panelGrid>

            <webuijsf:iframe styleClass="attachment" height="100%"
                             width="100%"
                             rendered="#{ResponseModel.attachmentOutModel.existingAttachment and !ResponseModel.attachmentOutModel.documentViewerDisplayed}"
                             url="#{ResponseModel.attachmentOutModel.attachmentURL}"/>
          </h:panelGroup>
        </h:panelGroup>
      </a4j:outputPanel>
    </h:panelGroup>
  </h:panelGrid>
</jsp:root>