<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:a4j="http://richfaces.org/a4j"
	xmlns:rich="http://richfaces.org/rich"
	xmlns:dossier="http://dossier.digitech.com/jsf/html">


<script>	

	

    modalPanelCustomAction_onShow = function() {
		setTimeout(function(){
			try { 
			  var lookupApplet = document.getElementsByName('editDocument')[0];
			  lookupApplet.display();
			}
			catch(err) {
				alert(err);
			}
		}, 500);
	};

	/*
	modalPanelCustomAction_onHide = function() {
	    alert('b1');
      try { 
	      $jQ(jsfIDtoJQID('modalPanelCustomActionForm:customActionInclusion:editFirstAttachmentModalPanelContent')).html(''); 
        var lookupApplet = document.getElementsByName('editDocument')[0];
        alert(lookupApplet);
        lookupApplet.stop();        
        alert('3');
      }
      catch(err) {
        alert(err);
      }
    };
	*/
	
</script>
	
 <h:panelGroup layout="block" id="editFirstAttachmentModalPanelContent">
	<script type="text/javascript">
      var attributes = {
        'code': 'com.digitech.applet.edit.EditDocument.class',
        'archive': '#{EditAttachmentModel.appletArchive}',
        'id': 'editDocument',
        'name': 'editDocument',
        'width': 580, 'height': 100};

      var parameters = {'jnlp_href': 'http://neairs.gilai.oai.ch/AirsDossier/applet/editDocument/editDocumentApplet.jnlp',
        'bgColor' : '#F0F0F0',
        'locale' : '#{ApplicationModel.localeStr}',
        'sessionId': '#{ApplicationModel.sessionId}',
        'fileToEdit': '#{EditAttachmentModel.absoluteAttachmentFilePath}',
        'httpContext': '#{EditAttachmentModel.url}',
        'fileName': '#{EditAttachmentModel.attachmentFileName}',
        'uploadURL': '/servlet/UploadAppletModifyFileServlet',
        'closeWindowURL': '',
        'onUploadCompleted': 'window.refreshAttachmentTable();',
        'onClose': 'Richfaces.hideModalPanel(\'modalPanelCustomAction\');',
        'autoStart': 'false'};

      deployJava.runApplet(attributes, parameters, '1.5', document.getElementById('modalPanelCustomActionForm:customActionInclusion:editFirstAttachmentModalPanelContent'));	  

	</script>
</h:panelGroup>

</jsp:root>