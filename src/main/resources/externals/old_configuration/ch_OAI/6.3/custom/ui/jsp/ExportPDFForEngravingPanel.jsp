<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:a4j="http://richfaces.org/a4j"
	xmlns:rich="http://richfaces.org/rich"
	xmlns:webuijsf="http://www.sun.com/webui/webuijsf"
	xmlns:dossier="http://dossier.digitech.com/jsf/html">

	<style>
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

			.customTable .panel label{
				width: 90px;
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
		function checkDate(val,isBegin){
			if(/^([0-9]{2}.[0-9]{2}.[0-9]{4})$/.test(val)){
				if(isBegin){
					document.getElementById('modalPanelCustomActionForm:customActionInclusion:errorBeginDate').style.display='none';
				}else{
					document.getElementById('modalPanelCustomActionForm:customActionInclusion:errorEndDate').style.display='none';
				}
				document.getElementById('modalPanelCustomActionForm:ok').className = "Btn2_sun4 btn_submit";
			}else{
				 if(isBegin){
					document.getElementById('modalPanelCustomActionForm:customActionInclusion:errorBeginDate').style.display='ruby-base';
				}else{
					document.getElementById('modalPanelCustomActionForm:customActionInclusion:errorEndDate').style.display='ruby-base';
				}
				document.getElementById('modalPanelCustomActionForm:ok').className = "btn-disable";
			}

		}

		window.onload = start();
		function start(){
			setBurn(document.getElementById("modalPanelCustomActionForm:customActionInclusion:burn").value);
			setUnderFilter(document.getElementById("modalPanelCustomActionForm:customActionInclusion:filter").value);
			checkInput();
			updateListPageCopy();
		    setTimeout(function(){ setFocus();}, 1000);
        }

        function setFocus(){
            document.getElementById('modalPanelCustomActionForm:customActionInclusion:selectedModeExport').focus();
        }

		function setBurn(value){
			try{
				var xpathEngraverType = "//gravage/graveurs/graveur[@id='"+value+"']/type_graveur";
				var xpathPathPDF = "//gravage/graveurs/graveur[@id='"+value+"']/chemin_pdf";
																									   
				var xpathPathJDF = "//gravage/graveurs/graveur[@id='"+value+"']/chemin_jdf";
																																	  
				var xpathPathDAT = "//gravage/graveurs/graveur[@id='"+value+"']/chemin_dat";

				var xmlValue = document.getElementById("modalPanelCustomActionForm:customActionInclusion:xml").value;

				parser = new DOMParser();
				xmlDoc = parser.parseFromString(xmlValue,"text/xml");
				var engraverType = xmlDoc.evaluate(xpathEngraverType, xmlDoc, null, XPathResult.FIRST_ORDERED_NODE_TYPE,null).singleNodeValue.textContent;
				var pathPDF = xmlDoc.evaluate(xpathPathPDF, xmlDoc, null, XPathResult.FIRST_ORDERED_NODE_TYPE,null).singleNodeValue.textContent;
				var pathJDF = xmlDoc.evaluate(xpathPathJDF, xmlDoc, null, XPathResult.FIRST_ORDERED_NODE_TYPE,null).singleNodeValue.textContent;
				var pathDAT = xmlDoc.evaluate(xpathPathDAT, xmlDoc, null, XPathResult.FIRST_ORDERED_NODE_TYPE,null).singleNodeValue.textContent;

				document.getElementById("modalPanelCustomActionForm:customActionInclusion:engraverType").value = engraverType;
				document.getElementById("modalPanelCustomActionForm:customActionInclusion:pathPDF").value = pathPDF;
																							
				document.getElementById("modalPanelCustomActionForm:customActionInclusion:pathJDF").value = pathJDF;
				document.getElementById("modalPanelCustomActionForm:customActionInclusion:pathDAT").value = pathDAT;
			}catch(e){
				alert('Erreur :'+e);
			}

        }

		function checkInput(){
			var check = '';
			var elem = document.getElementById("under_filter").getElementsByTagName("input");
            for(var i = 0; i &lt; elem.length; i++)
            {
                if (elem[i].checked) {
                    if(check==='') check += elem[i].value;
                    else check += '::'+elem[i].value;
                }
            }

			document.getElementById("modalPanelCustomActionForm:customActionInclusion:underFilterCheck").value = check;
			//document.getElementById("modalPanelCustomActionForm:customActionInclusion:underFilterCheck").innerHTML = check;

		}

        function setUnderFilter(value){
            try{

				var tabCheck;
				var selects;
				var list;
				var i = 0;
																								   
				var xpathNode = "//gravage/filtres/filtre[@id='"+value+"']/code";
				var xpathCheck = "//gravage/filtres/filtre[@id='"+value+"']/check";
				var xpathFolder = "//gravage/filtres/filtre[@id='"+value+"']/@dossier_complet";

				xmlValue = document.getElementById('modalPanelCustomActionForm:customActionInclusion:xml').value;

				parser = new DOMParser();
				xmlDoc = parser.parseFromString(xmlValue,"text/xml");
				var node = xmlDoc.evaluate(xpathNode, xmlDoc, null, XPathResult.FIRST_ORDERED_NODE_TYPE,null).singleNodeValue.textContent;
				var check = xmlDoc.evaluate(xpathCheck, xmlDoc, null, XPathResult.FIRST_ORDERED_NODE_TYPE,null).singleNodeValue.textContent;
				var folder = xmlDoc.evaluate(xpathFolder, xmlDoc, null, XPathResult.FIRST_ORDERED_NODE_TYPE,null).singleNodeValue.textContent;

				document.getElementById("modalPanelCustomActionForm:customActionInclusion:folder").value = folder;

				var underFilter = document.getElementById("under_filter_div");
				var tabCode = node.split('::');
				if(check !== null) tabCheck = check.split('::');
				while(tabCode[i]){
					if(tabCode[i].indexOf('||') > 0){
						var tabCodeSplit = tabCode[i].split('||');
						if(containsInList(tabCheck, tabCodeSplit[0]) || tabCheck == ''){
							list += '<tr><td><input name="under_filter" id="under_filter:'+i+'" type="checkbox" value="'+tabCodeSplit[0]+'" onchange="checkInput();" checked="checked"/></td><td></td></tr>';
						}else{
							list += '<tr><td><input name="under_filter" id="under_filter:'+i+'" type="checkbox" value="'+tabCodeSplit[0]+'" onchange="checkInput();"/></td><td></td></tr>';
						}
					}else{
						if(containsInList(tabCheck, tabCode[i]) || tabCheck == ''){
							list += '<tr><td><input name="under_filter" id="under_filter:'+i+'" type="checkbox" value="'+tabCode[i]+'" onchange="checkInput();" checked="checked"/></td><td><label style="text-align:left" for="underFilter:'+i+'">'+tabCode[i]+'</label></td></tr>';
						}else{
							list += '<tr><td><input name="under_filter" id="under_filter:'+i+'" type="checkbox" value="'+tabCode[i]+'" onchange="checkInput();"/></td><td><label style="text-align:left" for="underFilter:'+i+'">'+tabCode[i]+'</label></td></tr>';
						}
					}
					i++;
				}


                if(tabCheck == ''){
                    document.getElementById('modalPanelCustomActionForm:customActionInclusion:under_filter_label').style.display='none';
                    document.getElementById('under_filter_div').style.display='none';
                }else{
                    document.getElementById('modalPanelCustomActionForm:customActionInclusion:under_filter_label').style.display='';
                    document.getElementById('under_filter_div').style.display='';
                }

				selects = '<div><table id="under_filter"><tbody>'+list+'</tbody></table></div>';
				underFilter.innerHTML = selects;
				underFilter.innerHTML = underFilter.innerHTML.replace('undefined','');
				checkInput();

            }catch(e){
                alert('Erreur :'+e);
            }
        }

		function updateListPageCopy(){
            try{
				var modeExport = document.getElementById('modalPanelCustomActionForm:customActionInclusion:selectedModeExport').value;
				if(modeExport=='1' || modeExport=='7' || modeExport=='6' ){
					document.getElementById('panelSelectLanguage').style.display='none';
				}else{
					document.getElementById('panelSelectLanguage').style.display='';
				}
				
				
				
				
				if(modeExport=='5' || modeExport=='7'){
					document.getElementById('panelSelectStakeholders').style.display='';
					document.getElementById('panelSelectStakeholdersValue').style.display='';
					document.getElementById('object_tr').style.display='';
					document.getElementById('message_confirmation_mail_send').style.display='';
					document.getElementById('brun_tr').style.display='none';
					document.getElementById('brun_tr_value').style.display='none';
					document.getElementById('password_tr').style.display='none';
					document.getElementById('password_tr_value').style.display='none';
				}else{
					if(modeExport=='3' || modeExport=='6'){
						document.getElementById('brun_tr').style.display='none';
						document.getElementById('brun_tr_value').style.display='none';
						document.getElementById('password_tr').style.display='';
						document.getElementById('password_tr_value').style.display='';
						document.getElementById('panelSelectStakeholders').style.display='none';
						document.getElementById('panelSelectStakeholdersValue').style.display='none';
						document.getElementById('object_tr').style.display='none';
						document.getElementById('message_confirmation_mail_send').style.display='none';
					}else{
						document.getElementById('panelSelectStakeholders').style.display='none';
						document.getElementById('panelSelectStakeholdersValue').style.display='none';
						document.getElementById('object_tr').style.display='none';
						document.getElementById('message_confirmation_mail_send').style.display='none';
						document.getElementById('brun_tr').style.display='';
						document.getElementById('brun_tr_value').style.display='';
						document.getElementById('password_tr').style.display='';
						document.getElementById('password_tr_value').style.display='';
					}
				}
			}catch(e){
				alert(e);
			}
		}

		function containsInList(a, obj) {
			if(a != null){
				var i = a.length;
				while (i--) {
				   if (a[i] === obj) {
					   return true;
				   }
				}
			}
			return false;
		}


    </script>


	<h:panelGroup layout="block">
		<rich:panel style="position:relative; border:none; padding-bottom: 15px; height:400px; width:574px; margin-top:-10px" rendered="#{CustomActionModel.modalPanelModel.DATA_ERROR_MSG==null}">
			<rich:panel  style="overflow:auto; width:550px; height: auto; max-height: 160px; margin-top: 2px; margin-buttom: 0px">
                <rich:panel style="border-width: 0px; padding-top:0px; margin-top: 0px;">
                    <table class="customTable" style="table-layout: fixed; margin-left: -40px; margin-bottom: -10px;">
                        <tr>
                            <td>
					            <h:outputLabel style="width: 90px;" value="#{MessageBundleModel.jsp_nss}"/>
					        </td>
					        <td>
					            <h:inputText id="nss" style="background-color:gainsboro" readonly="true" value="#{CustomActionModel.modalPanelModel.DATA_NSS}" />
					        </td>
					        <td>
                                <h:outputLabel style="width: 90px;" value="#{MessageBundleModel.jsp_name}"/>
					        </td>
					        <td>
					            <h:inputText id="name" style="width: 165px;" value="#{CustomActionModel.modalPanelModel.DATA_NAME}" />
					        </td>
					    </tr>
						<tr>
							<td>
								<h:outputLabel style="width: 90px;" value="#{MessageBundleModel.jsp_mode_export}"/>
							</td>
							<td colspan="2">
								<h:selectOneMenu id="selectedModeExport" value="#{CustomActionModel.modalPanelModel.DATA_MODE_EXPORT}" rendered="#{not empty CustomActionModel.modalPanelModel.DATA_MODES_EXPORT}" onchange="updateListPageCopy();">
									<f:selectItems value="#{CustomActionModel.modalPanelModel.DATA_MODES_EXPORT}"/>
								</h:selectOneMenu>
							</td>
							<td id="panelSelectLanguage">
								<h:selectOneMenu id="selectedLanguage" value="#{CustomActionModel.modalPanelModel.DATA_LANGUE}" rendered="#{not empty CustomActionModel.modalPanelModel.DATA_LANGUES}">
									<f:selectItems value="#{CustomActionModel.modalPanelModel.DATA_LANGUES}"/>
								</h:selectOneMenu>
							</td>
					        
					    </tr>
					    <tr>
							<td id="brun_tr">
                                <h:outputLabel style="width: 90px;" value="#{MessageBundleModel.jsp_burn}"/>
                            </td>
                            <td id="brun_tr_value">
                                <h:selectOneMenu id="burn" value="#{CustomActionModel.modalPanelModel.DATA_BURN}" onchange="setBurn(this.value);" rendered="#{not empty CustomActionModel.modalPanelModel.DATA_BURNS}">
                                    <f:selectItems value="#{CustomActionModel.modalPanelModel.DATA_BURNS}"/>
                                </h:selectOneMenu>
                            </td>
                            <td id="password_tr">
                                <h:outputLabel style="width: 90px;" value="#{MessageBundleModel.jsp_password}"/>
                            </td>
                            <td id="password_tr_value">
                                <h:inputText id="password" value="#{CustomActionModel.modalPanelModel.DATA_PASSWORD}" />
                            </td>
                        </tr>
						<tr>
							<td id="panelSelectStakeholders">
								<h:outputLabel style="width: 90px; margin-top: 6px;" value="#{MessageBundleModel.jsp_stakeholders}"/>
							</td>
							<td colspan="3" id="panelSelectStakeholdersValue">
									<h:selectOneMenu id="selectedStakeHolders" style="margin-left: 10px;" value="#{CustomActionModel.modalPanelModel.DATA_STAKEHOLDER}" rendered="#{not empty CustomActionModel.modalPanelModel.DATA_STAKEHOLDERS}">
									    <f:selectItem itemValue="#{null}" itemLabel=" " />
										<f:selectItems value="#{CustomActionModel.modalPanelModel.DATA_STAKEHOLDERS}"/>
									</h:selectOneMenu>
									<h:outputLabel style="width: 90px; display: ruby-base; margin-left: 16px; color: red" value="#{MessageBundleModel.jsp_stakeholders_empty}" rendered="#{empty CustomActionModel.modalPanelModel.DATA_STAKEHOLDERS}"/>
							</td>
						</tr>
						<tr id="object_tr">
							<td>
					            <h:outputLabel style="width: 90px;" value="#{MessageBundleModel.jsp_email_object}"/>
					        </td>
							<td colspan="3">
					            <h:inputText id="objet" style="width: 165px;" value="#{CustomActionModel.modalPanelModel.DATA_OBJECT}" />
					        </td>
						</tr>
					 </table>
					 
                </rich:panel>
					
            </rich:panel>
			<rich:panel header="#{MessageBundleModel.jsp_exportForEngraving}" style="overflow:auto; width:550px; height: auto; max-height: 129px; margin-top: 4px; margin-buttom: 0px">
				<rich:panel style="border-width: 0px; padding-top:0px; margin-top: 0px; margin-left: 55px;">
					<h:outputLabel value="#{MessageBundleModel.jsp_begin_date}" for="beginDate" />
                    <h:inputText id="beginDate" onkeyup="checkDate(this.value,true);" value="#{CustomActionModel.modalPanelModel.DATA_BEGIN_DATE}" >
                        <f:convertDateTime pattern="dd.MM.yyyy" />
                    </h:inputText>
					<h:outputLabel id="errorBeginDate" value="#{MessageBundleModel.jsp_format_invalide}" style="display:none;color:red;"/>
                    <br/>
					<h:outputLabel value="#{MessageBundleModel.jsp_end_date}" for="endDate" />
					<h:inputText id="endDate" onkeyup="checkDate(this.value, false);" value="#{CustomActionModel.modalPanelModel.DATA_END_DATE}" >
                        <f:convertDateTime pattern="dd.MM.yyyy" />
                    </h:inputText>
					<h:outputLabel id="errorEndDate" value="#{MessageBundleModel.jsp_format_invalide}" style="display:none;color:red;"/>
					<br/>
                    <h:outputLabel value="#{MessageBundleModel.jsp_filter}" for="filter" />
                    <h:selectOneMenu id="filter" value="#{CustomActionModel.modalPanelModel.DATA_FILTER}" onchange="setUnderFilter(this.value);" rendered="#{not empty CustomActionModel.modalPanelModel.DATA_FILTERS}">
                        <f:selectItems value="#{CustomActionModel.modalPanelModel.DATA_FILTERS}"/>
                    </h:selectOneMenu>
                    <br/>
					<h:outputLabel style="display:none" id="under_filter_label" value="#{MessageBundleModel.jsp_under_filter}" for="under_filter" />
                    <div style="display:none" id="under_filter_div"></div>
				</rich:panel>
			</rich:panel>
			<div style="display:none" id="message_confirmation_mail_send">
				<table><tr>
					<td><h:selectBooleanCheckbox value="#{CustomActionModel.modalPanelModel.DATA_CONFIRMATION_SEND}" style="margin-left:1px;"/></td>
					<td colspan="2"><h:outputLabel id="alert" value="#{MessageBundleModel.jsp_confirm_send}" style="color:red;width:400px;text-align:left;"/></td>
				</tr></table>
			</div>
			<rich:panel style="display:none">
				<h:inputText id="xml" value="#{CustomActionModel.modalPanelModel.DATA_FILE}" />
				<h:inputText id="folder" value="#{CustomActionModel.modalPanelModel.DATA_IS_FOLDER}" />
				<h:inputText id="engraverType" value="#{CustomActionModel.modalPanelModel.DATA_ENGRAVER_TYPE}" />
				<h:inputText id="pathPDF" value="#{CustomActionModel.modalPanelModel.DATA_PATH_PDF}" />
				<h:inputText id="pathJDF" value="#{CustomActionModel.modalPanelModel.DATA_PATH_JDF}" />
				<h:inputText id="pathDAT" value="#{CustomActionModel.modalPanelModel.DATA_PATH_DAT}" />
				<h:inputText id="underFilterCheck" value="#{CustomActionModel.modalPanelModel.DATA_UNDER_FILTER}" />
			</rich:panel>
		</rich:panel>
		
		<center>
			<rich:panel style="overflow:auto; width:540px; margin-top:135px; margin-bottom:10px; text-align: center;" rendered="#{CustomActionModel.modalPanelModel.DATA_ERROR_MSG!=null}">
				<h:outputText value="#{CustomActionModel.modalPanelModel.DATA_ERROR_MSG}" styleClass="message_error" />
			</rich:panel>
		</center>

	</h:panelGroup>
</jsp:root>