<jsp:root version="2.0"
          xmlns:h="http://java.sun.com/jsf/html"
          xmlns:jsp="http://java.sun.com/JSP/Page">


  <script>


    modalPanelCustomAction_onShow = function () {
      setTimeout(function () {
        try {
          var lookupApplet = document.getElementsByName('editDocument')[0];
          lookupApplet.display();
        } catch (err) {
          alert(err);
        }
      }, 500);
    };
  </script>

  <h:panelGroup layout="block" id="editFirstAttachmentModalPanelContent">
    <script type="text/javascript">
      var attributes = {
        'code': 'com.digitech.applet.edit.EditDocument.class',
        'archive': '#{EditAttachmentModel.appletArchive}',
        'id': 'editDocument',
        'name': 'editDocument',
        'width': 580, 'height': 100
      };

      var parameters = {
        'jnlp_href': 'http://neairs.gilai.oai.ch/AirsDossier/applet/editDocument/editDocumentApplet.jnlp',
        'bgColor': '#F0F0F0',
        'locale': '#{ApplicationModel.localeStr}',
        'sessionId': '#{ApplicationModel.sessionId}',
        'fileToEdit': '#{EditAttachmentModel.absoluteAttachmentFilePath}',
        'httpContext': '#{EditAttachmentModel.url}',
        'fileName': '#{EditAttachmentModel.attachmentFileName}',
        'uploadURL': '/servlet/UploadAppletModifyFileServlet',
        'closeWindowURL': '',
        'onUploadCompleted': 'window.refreshAttachmentTable();',
        'onClose': 'Richfaces.hideModalPanel(\'modalPanelCustomAction\');',
        'autoStart': 'false'
      };

      deployJava.runApplet(attributes, parameters, '1.5', document.getElementById('modalPanelCustomActionForm:customActionInclusion:editFirstAttachmentModalPanelContent'));

    </script>
  </h:panelGroup>

</jsp:root>