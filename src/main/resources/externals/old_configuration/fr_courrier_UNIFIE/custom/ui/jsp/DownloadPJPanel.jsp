<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:c="http://java.sun.com/jstl/core"
	xmlns:ui="http://java.sun.com/jsf/facelets"
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:webuijsf="http://www.sun.com/webui/webuijsf"
	xmlns:a4j="http://richfaces.org/a4j"
	xmlns:rich="http://richfaces.org/rich"
	xmlns:digi="http://ged.digitech.com/jsf/html"
	xmlns:dossier="http://dossier.digitech.com/jsf/html">

  <h:panelGroup id="TreatmentSCOSVPanel" rendered="#{CustomActionModel.modalPanelModel.DownloadPJ}">
  

  
  
  
  	<h:panelGrid columns="1" layout="block" cellspacing="10"  		rendered="true">
  		<h:outputText style="width:450px;text-align:center;float:none;"          value="#{CustomActionModel.modalPanelModel.FileNameToDownload}" />
  		<a4j:htmlCommandLink    			  actionListener="#{GenericDownLoadController.processAction}"
  			    ajaxSingle="true" limitToList="true" ignoreDupResponses="true"  			styleClass="customAction">
  			<h:outputText  				value="--Lien pour télécharger--"   				style="width:1200px;text-align:center;float:none;margin-left:0%;" />
  			<f:attribute name="file"  				value="#{CustomActionModel.modalPanelModel.docsigned}" />
  		</a4j:htmlCommandLink>
  		<h:inputHidden value="noErrorForJQ" id="noErrorForJQ"></h:inputHidden>
  	</h:panelGrid>

	</h:panelGroup>

	<script>
	 	$jQ(document).ready(function() {
  		$jQ(jsfIDtoJQID('modalPanelCustomActionForm:cancel')).val("FERMER");
			$jQ(jsfIDtoJQID('modalPanelCustomActionForm:ok')).addClass('hidden').attr('hidden', 'hidden');
		}
	);
	</script>



</jsp:root>