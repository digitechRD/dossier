<!DOCTYPE html>
<jsp:root version="2.0"
          xmlns:h="http://java.sun.com/jsf/html"
          xmlns:jsp="http://java.sun.com/JSP/Page">

  <script type="text/javascript" src="#{ScriptBundleModel.clipboard}"/>

  <h:panelGroup layout="block">
    <div layout="block" id="copyBtn" class="copyBtn input-group-text bg-warning"
         onclick="switchStyleClasses(this, 'bg-warning', 'bg-success'); displayTooltip('Copie effectuée!', 'copyBtn'); return false;"
         data-clipboard-target="#modalPanelCustomActionForm\:customActionInclusion\:textToBeCopied">
      <h:outputLabel value="Cliquer ici pour copier l'url ci-dessous dans le presse-papier " styleClass="text-left ml-1 w-100"/>
      <i id="icoClipboard" class="far fa-clipboard text-white float-right"/>
    </div>
    <h:outputLabel id="textToBeCopied" value="#{CustomActionModel.modalPanelModel.url}" styleClass="text-left text-break my-2 mt-2 text-info w-100"/>
  </h:panelGroup>

  <script type="text/javascript">
    $jQ(document).ready(function () {
          document.getElementById('modalPanelCustomActionForm:ok').disabled = true;
          document.getElementById('modalPanelCustomActionForm:ok').style.display = 'none';
          new ClipboardJS('.copyBtn');
        }
    );

    function displayTooltip(txt, item) {
      // console.info('displayTooltip 111:' + $jQ(jsfIDtoJQID(item)) + ' : ' + txt);
      $jQ(jsfIDtoJQID(item)).tooltip({title: txt, html: false, placement: 'auto'}).tooltip('show');
      // console.info('displayTooltip 222');
    }
  </script>
</jsp:root>