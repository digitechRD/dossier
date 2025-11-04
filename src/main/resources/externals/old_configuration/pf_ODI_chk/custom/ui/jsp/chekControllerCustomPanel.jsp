<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core"
          xmlns:h="http://java.sun.com/jsf/html"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:a4j="http://richfaces.org/a4j"
          xmlns:dt="http://ged.digitech.com/jsf/html"
          xmlns:dossier="http://dossier.digitech.com/jsf/html">

  <script>
      var currentIdx = 0;
  </script>


  <h:panelGroup id="chekControllerPanel">
    <h:panelGroup id="ajaxStatus" layout="block"
                  style="position: absolute; right: 10px; margin-top: 5px;">
      <a4j:status forceId="true" id="cheksLoadingStatus" layout="block">
        <f:facet name="start">
          <h:graphicImage value="#{ImageBundleModel.waiting_request2}"/>
        </f:facet>
      </a4j:status>
    </h:panelGroup>
    <h:panelGrid>

      <h:panelGroup styleClass="document">
        <a4j:commandLink id="previousDocument"
                         styleClass="Hyp_sun4 #{!MultipleVisaController.model.previousDocumentExisting ? 'disabled' : ''}"
                         value="#{MessageBundleModel.attachment_hyperlink_previousDocument}"
                         title="#{MessageBundleModel.attachment_tooltip_previousDocument}"
                         disabled="#{!MultipleVisaController.model.previousDocumentExisting}"
                         action="#{CustomActionController.apply}"
                         reRender="notifications, modalPanelError, chekControllerPanel">
          <f:setPropertyActionListener value="PREVIOUS_CHEK"
                                       target="#{CustomActionModel.modalPanelModel.ctrlCheckAction}"/>
        </a4j:commandLink>
        <h:outputText escape="false"
                      value="#{MultipleVisaController.model.attachmentInModel.currentDocumentTitle}"/>
        <a4j:commandLink id="nextDocument"
                         styleClass="Hyp_sun4 #{!MultipleVisaController.model.nextDocumentExisting ? 'disabled' : ''}"
                         value="#{MessageBundleModel.attachment_hyperlink_nextDocument}"
                         title="#{MessageBundleModel.attachment_tooltip_nextDocument}"
                         disabled="#{!MultipleVisaController.model.nextDocumentExisting}"
                         action="#{CustomActionController.apply}"
                         reRender="notifications, modalPanelError, chekControllerPanel">
          <f:setPropertyActionListener value="NEXT_CHEK"
                                       target="#{CustomActionModel.modalPanelModel.ctrlCheckAction}"/>
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
                                    model="#{MultipleVisaController.model.attachmentInModel.message}"/>
            </h:panelGroup>
            <h:panelGroup
                rendered="#{empty MultipleVisaController.model.attachmentInModel.message}">
              <h:outputText style="z-index: 2;"
                            rendered="#{!MultipleVisaController.model.attachmentInModel.existingAttachment}"
                            value="#{MessageBundleModel.attachment_label_noAttachment}"/>

              <h:panelGrid columns="2">
                <a4j:queue id="documentViewerQueueIn" requestDelay="500"
                           ignoreDupResponses="true"/>
                <f:subview id="subviewIn">
                  <dt:documentViewer id="documentViewerIn"
                                     styleClass="documentViewer"
                                     rendered="#{MultipleVisaController.model.attachmentInModel.existingAttachment}"
                                     value="#{MultipleVisaController.model.attachmentInModel.attachmentFilePath}"
                                     eventsQueue="documentViewerQueueIn" pagerDisplayed="false"
                                     zoomDisplayed="false" defaultZoom="FIT_TO_PAGE"
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
              <!-- script>
              initDocumentViewerToolbar();
            </script-->

            </h:panelGroup>
          </h:panelGroup>
        </a4j:outputPanel>
      </h:panelGroup>


      <h:panelGroup id="attachmentSignViewer" layout="block"
                    styleClass="attachmentSignViewerPanel row">
        <a4j:outputPanel id="attachmentSignLayoutUnitContent" layout="block"
                         styleClass="attachmentViewer"
                         rendered="#{not empty MultipleVisaController.model.attachmentOutModel and MultipleVisaController.model.attachmentOutModel.existingAttachment}">

          <h:panelGroup layout="block" styleClass="attachment">
            <h:panelGroup id="notificationsSignPanel" layout="block">
              <dossier:notification id="notificationsSign"
                                    model="#{MultipleVisaController.model.attachmentOutModel.message}"/>
            </h:panelGroup>
            <h:panelGroup
                rendered="#{empty MultipleVisaController.model.attachmentOutModel.message}">
              <h:outputText style="z-index: 2;"
                            rendered="#{!MultipleVisaController.model.attachmentOutModel.existingAttachment}"
                            value="#{MessageBundleModel.attachment_label_noAttachment}"/>

              <h:panelGrid columns="2">
                <a4j:queue id="documentViewerQueueSign" requestDelay="500"
                           ignoreDupResponses="true"/>
                <f:subview id="subviewSign">
                  <dt:documentViewer id="documentViewerSign"
                                     styleClass="documentViewer"
                                     rendered="#{MultipleVisaController.model.attachmentOutModel.existingAttachment}"
                                     value="#{MultipleVisaController.model.attachmentOutModel.attachmentFilePath}"
                                     eventsQueue="documentViewerSign" pagerDisplayed="false"
                                     zoomDisplayed="false" defaultZoom="FIT_TO_PAGE"
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
              <!-- script>
              initDocumentViewerToolbar();
            </script-->

            </h:panelGroup>
          </h:panelGroup>
        </a4j:outputPanel>

        <h:panelGroup>
          <h:panelGroup layout="block">
            <h:outputLabel
                value="#{MessageBundleModel.modalPanelComment_label_comment}"
                for="inputComment"/>
            <h:inputTextarea id="inputComment"
                             style="width: 500px;' : 'width: 250px;"
                             value="#{MultipleVisaController.model.comment}"/>
          </h:panelGroup>
        </h:panelGroup>
      </h:panelGroup>

      <h:panelGroup id="actionButton" layout="block">
        <a4j:outputPanel layout="block" styleClass="buttons"
                         ajaxRendered="true">
          <script>
              // Attachment layout shortkeys
              $jQ(document).bind('keyup', {combi: 'v', disableInInput: true}, function () {
                  $jQ(jsfIDtoJQID('modalPanelCustomActionForm:customActionInclusion:validate')).click();
              });
              $jQ(document).bind('keyup', {combi: 'r', disableInInput: true}, function () {
                  $jQ(jsfIDtoJQID('modalPanelCustomActionForm:customActionInclusion:refused')).click();
              });
              $jQ(document).bind('keyup', {combi: 'a', disableInInput: true}, function () {
                  $jQ(jsfIDtoJQID('modalPanelCustomActionForm:customActionInclusion:wait')).click();
              });
              // up and down
              $jQ(document).bind('keyup', {combi: 'up', disableInInput: true}, function () {
                  $jQ(jsfIDtoJQID('modalPanelCustomActionForm:document:nextDocument')).click();
              });
              $jQ(document).bind('keyup', {combi: 'down', disableInInput: true}, function () {
                  $jQ(jsfIDtoJQID('modalPanelCustomActionForm:document:previousDocument')).click();
              });
              // left and right
              $jQ(document).bind('keyup', {combi: 'left', disableInInput: true}, function () {
                  $jQ(jsfIDtoJQID('modalPanelCustomActionForm:customActionInclusion:previous')).click();
              });
              $jQ(document).bind('keyup', {combi: 'right', disableInInput: true}, function () {
                  $jQ(jsfIDtoJQID('modalPanelCustomActionForm:customActionInclusion:next')).click();
              });
          </script>
          <a4j:commandButton id="cancel" styleClass="Btn2_sun4 btn_cancel"
                             onmouseover="$jQ(this).addClass('Btn2Hov_sun4 btnHov_cancel');"
                             onmouseout="$jQ(this).removeClass('Btn2Hov_sun4 btnHov_cancel');"
                             value="#{MessageBundleModel.action_cancel}"
                             title="#{MessageBundleModel.action_cancel_toolTip}"
                             action="#{CustomActionController.apply}"
                             oncomplete="Richfaces.hideModalPanel('modalPanelCustomAction'); return false;"
                             reRender="notifications, modalPanelError, mainPanel"
                             immediate="true"/>

          <a4j:commandButton id="next" styleClass="Btn2_sun4 btn_submit"
                             rendered="true"
                             disabled="#{!CustomActionModel.modalPanelModel.isSignNextExisting}"
                             onmouseover="$jQ(this).addClass('Btn2Hov_sun4 btnHov_submit');"
                             onmouseout="$jQ(this).removeClass('Btn2Hov_sun4 btnHov_submit');"
                             value="#{MessageBundleModel.action_chq_ctrl_next}"
                             title="#{MessageBundleModel.action_chq_ctrl_next_toolTip}"
                             action="#{CustomActionController.apply}"
                             oncomplete="currentIdx++;"
                             reRender="notifications, modalPanelError,attachmentSignViewer, actionButton">
            <f:setPropertyActionListener value="NEXT_SIGN"
                                         target="#{CustomActionModel.modalPanelModel.ctrlCheckAction}"/>
          </a4j:commandButton>

          <a4j:commandButton id="wait" styleClass="Btn2_sun4 btn_submit"
                             rendered="true"
                             onmouseover="$jQ(this).addClass('Btn2Hov_sun4 btnHov_submit');"
                             onmouseout="$jQ(this).removeClass('Btn2Hov_sun4 btnHov_submit');"
                             value="#{MessageBundleModel.action_chq_ctrl_wait}"
                             title="#{MessageBundleModel.action_chq_ctrl_wait_toolTip}"
                             action="#{CustomActionController.apply}" ignoreDupResponses="true"
                             oncomplete="if( (currentIdx &lt; #{MultipleVisaController.model.selectedDocumentsListSize}-1 )){currentIdx++;}else{Richfaces.hideModalPanel('modalPanelCustomAction');}"
                             reRender="notifications, modalPanelError, chekControllerPanel">
            <f:setPropertyActionListener value="WAIT"
                                         target="#{CustomActionModel.modalPanelModel.ctrlCheckAction}"/>
          </a4j:commandButton>

          <a4j:commandButton id="refused" styleClass="Btn2_sun4 btn_submit"
                             rendered="true"
                             onmouseover="$jQ(this).addClass('Btn2Hov_sun4 btnHov_submit');"
                             onmouseout="$jQ(this).removeClass('Btn2Hov_sun4 btnHov_submit');"
                             value="#{MessageBundleModel.action_chq_ctrl_refused}"
                             title="#{MessageBundleModel.action_chq_ctrl_refused_toolTip}"
                             action="#{CustomActionController.apply}"
                             oncomplete="if( (currentIdx &lt; #{MultipleVisaController.model.selectedDocumentsListSize}-1 )){currentIdx++;}else{Richfaces.hideModalPanel('modalPanelCustomAction');}"
                             reRender="notifications, modalPanelError, chekControllerPanel">
            <f:setPropertyActionListener value="REFUSED"
                                         target="#{CustomActionModel.modalPanelModel.ctrlCheckAction}"/>
          </a4j:commandButton>

          <a4j:commandButton id="validate" styleClass="Btn2_sun4 btn_submit"
                             rendered="true"
                             onmouseover="$jQ(this).addClass('Btn2Hov_sun4 btnHov_submit');"
                             onmouseout="$jQ(this).removeClass('Btn2Hov_sun4 btnHov_submit');"
                             value="#{MessageBundleModel.action_chq_ctrl_validate}"
                             title="#{MessageBundleModel.action_chq_ctrl_validate_toolTip}"
                             action="#{CustomActionController.apply}" ignoreDupResponses="true"
                             oncomplete="if( (currentIdx &lt; #{MultipleVisaController.model.selectedDocumentsListSize}-1 )){currentIdx++;}else{Richfaces.hideModalPanel('modalPanelCustomAction');}"
                             reRender="notifications, modalPanelError, chekControllerPanel">
            <f:setPropertyActionListener value="VALIDATE"
                                         target="#{CustomActionModel.modalPanelModel.ctrlCheckAction}"/>
          </a4j:commandButton>

          <a4j:commandButton id="previous" styleClass="Btn2_sun4 btn_submit"
                             rendered="true"
                             disabled="#{!CustomActionModel.modalPanelModel.isSignPreviousExisting}"
                             onmouseover="$jQ(this).addClass('Btn2Hov_sun4 btnHov_submit');"
                             onmouseout="$jQ(this).removeClass('Btn2Hov_sun4 btnHov_submit');"
                             value="#{MessageBundleModel.action_chq_ctrl_previous}"
                             title="#{MessageBundleModel.action_chq_ctrl_previous_toolTip}"
                             action="#{CustomActionController.apply}"
                             oncomplete="currentIdx--;"
                             reRender="notifications, modalPanelError, attachmentSignViewer, actionButton">
            <f:setPropertyActionListener value="PREVIOUS_SIGN"
                                         target="#{CustomActionModel.modalPanelModel.ctrlCheckAction}"/>
          </a4j:commandButton>

        </a4j:outputPanel>
      </h:panelGroup>
    </h:panelGrid>
  </h:panelGroup>


</jsp:root>