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
	</script>

	<h:panelGroup layout="block">
		<rich:panel style="position:relative; border:none; padding-bottom: 15px; height:310px; width:574px;" rendered="#{CustomActionModel.modalPanelModel.DATA_ERROR_MSG==null}">
			<rich:panel header="#{MessageBundleModel.jsp_informations}" style="overflow:auto; width:550px; height: auto; max-height: 50px; margin-top: 2px; margin-buttom: 0px">
                <rich:panel style="border-width: 0px; padding-top:0px; margin-top: 0px;">
					<h:outputLabel value="#{MessageBundleModel.jsp_nss}" for="nss" /><h:inputText id="nss" style="background-color:gainsboro" readonly="true" value="#{CustomActionModel.modalPanelModel.DATA_NSS}" />
                </rich:panel>
            </rich:panel>
			<rich:panel header="#{MessageBundleModel.jsp_exportwebai}" style="overflow:auto; width:550px; height: auto; max-height: 250px; margin-top: 20px; margin-buttom: 0px">
				<rich:panel style="border-width: 0px; padding-top:0px; margin-top: 0px;">
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
                    <h:selectOneMenu id="filter" value="#{CustomActionModel.modalPanelModel.DATA_FILTER}" rendered="#{not empty CustomActionModel.modalPanelModel.DATA_FILTERS}">
                        <f:selectItems value="#{CustomActionModel.modalPanelModel.DATA_FILTERS}"/>
                    </h:selectOneMenu>
				</rich:panel>
			</rich:panel>
		</rich:panel>
		<center>
			<rich:panel style="overflow:auto; width:540px; margin-top:135px; margin-bottom:10px; text-align: center;" rendered="#{CustomActionModel.modalPanelModel.DATA_ERROR_MSG!=null}">
				<h:outputText value="#{CustomActionModel.modalPanelModel.DATA_ERROR_MSG}" styleClass="message_error" />
			</rich:panel>
		</center>
	</h:panelGroup>
</jsp:root>