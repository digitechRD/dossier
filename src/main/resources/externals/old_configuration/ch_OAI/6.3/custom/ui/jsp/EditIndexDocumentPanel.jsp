<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:a4j="http://richfaces.org/a4j"
	xmlns:rich="http://richfaces.org/rich"
	xmlns:webuijsf="http://www.sun.com/webui/webuijsf"
	xmlns:dossier="http://dossier.digitech.com/jsf/html">

	<style>
			.radioStyle tr td {
				width: 120px;
			}

			.radioStyle tr td input{
				 float: none;
			}

			.radioStyle tr td label{
				clear: none;
				display: inline;
				float: none;
				font-weight: bold;
				padding-right: 0px;
				text-align: left;
			}

			.selectItemsStyle{
				width:250ps;
			}

			.customActionMsg{
				padding:1px;
			}

			.customActionDistribute div{
				padding:1px;
			}

			.customActionDataTable{
				border: 1px solid #666;
			}
			.customActionDataTable thead th{
				border: 1px solid #666;
				background-color:#FFFFFF;
			}
			.customActionDataTable tbody td{
				border-right: 1px solid #666;
				border-bottom: none;
			}
			.customActionDataTable tbody tr:nth-child(odd) {
				background-color:#F2F2F2;
				border-bottom: none;
			}
			.customActionDataTable tbody tr:hover {
				background-color:#e2e059;
			}

			.preformatted {
				display: block;
				margin: 3px 0;
			}

			.message_error{
                color:red;
                font-weight:bold;
                font-size:13px;
                text-align:center;
            }

            .message_warn{
                color:orange;
                font-weight:bold;
                font-size:13px;
                text-align:center;
            }

			.rich-panel-body{
				padding-top : 5px;
			}

			.btn-disable{
                cursor: default;
                pointer-events: none;
                color: #fff;
            }

	</style>



	<script type="text/javascript">

		window.onload = start();
        function start(){
            var fields = document.getElementById("modalPanelCustomActionForm:customActionInclusion:fields").value.split("::");
            for(var i = 0; i &lt; fields.length; i++){
                document.getElementById('panel_'+fields[i]).style.display='';
				if(fields[i] === 'AL_ASSURE_GROUPE_DOC'){
					displayTypesList(document.getElementById("modalPanelCustomActionForm:customActionInclusion:groupsDocument").value, true);
					document.getElementById("typesDocument").value = document.getElementById("modalPanelCustomActionForm:customActionInclusion:typeDocument").value;
				}
            }
        }

	    function check(val, field){
		    var error = false;
		    if(field === "date"){
                if(/^([0-9]{2}.[0-9]{2}.[0-9]{4})$/.test(val)){
                    document.getElementById('modalPanelCustomActionForm:customActionInclusion:errorDate').style.display='none';
                }else{
                    document.getElementById('modalPanelCustomActionForm:customActionInclusion:errorDate').style.display='ruby-base';
					error = true;
                }
            }else if(field === "nss"){
                if(/^([0-9]{3}.[0-9]{4}.[0-9]{4}.[0-9]{2})$/.test(val) || /^([0-9]{13})$/.test(val)){
                    document.getElementById('modalPanelCustomActionForm:customActionInclusion:errorNSS').style.display='none';
                }else{
                    document.getElementById('modalPanelCustomActionForm:customActionInclusion:errorNSS').style.display='ruby-base';
					error = true;
                }
            }

			if(error) document.getElementById('modalPanelCustomActionForm:ok').className = "btn-disable";
			else document.getElementById('modalPanelCustomActionForm:ok').className = "Btn2_sun4 btn_submit";
		}

		function change(value){
			document.getElementById("modalPanelCustomActionForm:customActionInclusion:typeDocument").value = value;
			console.log(value+" / "+document.getElementById("modalPanelCustomActionForm:customActionInclusion:typeDocument").value);
		}

		function displayTypesList(value, start){
			var url;
			try {
				// Récupération des items selon le groupe
				var list;
				url = document.getElementById('modalPanelCustomActionForm:customActionInclusion:url').value.replace('##ID_GROUP##', value);
				var xhttp = new XMLHttpRequest();
				xhttp.open("GET", url, false);
				xhttp.send(null);
				var json = JSON.parse(xhttp.responseText.replace('{"wsDocSeriesType":', "").replace(']}', "]").replace('}}', "}"));
				// Vidage du select
				document.getElementById("typesDocument").length = 0
				// Ajout des items au select
				var typeDocument = document.getElementById("typesDocument");
				var opt = null;
				for (var key in json) {
					if (json.hasOwnProperty(key)) {
						opt = document.createElement('option');
						opt.value = json[key].id;
						opt.innerHTML = json[key].value;
						typeDocument.appendChild(opt);
					}
				}
				if(start == false) change(document.getElementById("typesDocument").value);
			}catch(err){
				console.log("ERREUR -> "+err+" : "+url);
			}
		}
	</script>

	<h:panelGroup layout="block">
		<rich:panel style="position:relative; border:none; height:310px; width:574px;" rendered="#{CustomActionModel.modalPanelModel.DATA_ERROR_MSG==null}">
			<rich:panel header="#{MessageBundleModel.jsp_edit}" style="overflow:auto; width:550px; height: auto; max-height: 250px; margin-top: 0px; margin-buttom: 0px">
                <div id="panel_N_NSS" style="border-width: 0px; padding-top:0px; margin-top: 0px; display:none;">
                    <h:outputLabel value="#{MessageBundleModel.jsp_nss}" for="nss" />
                    <h:inputText id="nss" onkeyup="check(this.value, 'nss');" value="#{CustomActionModel.modalPanelModel.DATA_FIELD_N_NSS}" />
                    <h:outputLabel id="errorNSS" value="#{MessageBundleModel.jsp_format_nss_invalide}" style="display:none;color:red;"/>
                </div>
                <div id="panel_N_DEM" style="border-width: 0px;padding-top:0px; margin-top: 10px; display:none;">
                    <h:outputLabel value="#{MessageBundleModel.jsp_ndem}" for="ndem" />
                    <h:selectOneMenu id="ndem" required="true" editable="true" value="#{CustomActionModel.modalPanelModel.DATA_FIELD_N_DEM}" rendered="#{not empty CustomActionModel.modalPanelModel.DATA_LIST_NDEM}">
                        <f:selectItems value="#{CustomActionModel.modalPanelModel.DATA_LIST_NDEM}"/>
                    </h:selectOneMenu>
                </div>
				<div id="panel_D_VALEUR" style="border-width: 0px; padding-top:0px; margin-top: 10px; display:none;">
                    <h:outputLabel value="#{MessageBundleModel.jsp_date}" for="date" />
                    <h:inputText id="date" onkeyup="check(this.value, 'date');" value="#{CustomActionModel.modalPanelModel.DATA_FIELD_D_VALEUR}" >
                        <f:convertDateTime pattern="dd.MM.yyyy" />
                    </h:inputText>
					<h:outputLabel id="errorDate" value="#{MessageBundleModel.jsp_format_date_invalide}" style="display:none;color:red;"/>
				</div>
				<div id="panel_AL_ASSURE_GROUPE_DOC" style="border-width: 0px; padding-top:0px; margin-top: 10px;">
                    <h:outputLabel value="#{MessageBundleModel.jsp_groups_document}" for="groupsDocument" />
                    <h:selectOneMenu id="groupsDocument" required="true" onchange="displayTypesList(this.value, false);" value="#{CustomActionModel.modalPanelModel.DATA_FIELD_AL_ASSURE_GROUPE_DOC}" rendered="#{not empty CustomActionModel.modalPanelModel.DATA_LIST_DOCUMENT_GROUPS}">
                        <f:selectItems value="#{CustomActionModel.modalPanelModel.DATA_LIST_DOCUMENT_GROUPS}"/>
                    </h:selectOneMenu>
                </div>
				<div id="panel_AL_ASSURE_TYPE_DOC" style="border-width: 0px; padding-top:0px; margin-top: 10px;">
                    <h:outputLabel value="#{MessageBundleModel.jsp_types_document}" for="typesDocument" />
					<!--h:selectOneMenu id="typesDocument" required="true" onchange="change(this.value)"/-->
					<div id="type_document_div"><select id="typesDocument" onchange="change(this.value)"></select></div>
                </div>
			</rich:panel>
            <rich:panel style="display:none">
                <h:inputText id="url" value="#{CustomActionModel.modalPanelModel.DATA_AIRSDOSSIER_URL}" />
                <h:inputText id="fields" value="#{CustomActionModel.modalPanelModel.DATA_FIELDS_VISIBLE}" />
				<h:inputText id="typeDocument" value="#{CustomActionModel.modalPanelModel.DATA_FIELD_AL_ASSURE_TYPE_DOC}" />
            </rich:panel>
		</rich:panel>
		<center>
			<rich:panel style="overflow:auto; width:540px; margin-top:135px; margin-bottom:10px; text-align: center;" rendered="#{CustomActionModel.modalPanelModel.DATA_ERROR_MSG!=null}">
				<h:outputText value="#{CustomActionModel.modalPanelModel.DATA_ERROR_MSG}" styleClass="message_error" />
			</rich:panel>
		</center>
	</h:panelGroup>
</jsp:root>