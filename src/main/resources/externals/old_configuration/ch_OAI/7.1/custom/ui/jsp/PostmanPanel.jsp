<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core"
					xmlns:h="http://java.sun.com/jsf/html"
					xmlns:jsp="http://java.sun.com/JSP/Page"
					xmlns:a4j="http://richfaces.org/a4j"
					xmlns:rich="http://richfaces.org/rich">

	<script language="javascript" type="text/javascript">
		function displayGestList()
		{
			if(document.getElementById('modalPanelCustomActionForm:customActionInclusion:users').style.display == 'block'){
				document.getElementById('modalPanelCustomActionForm:customActionInclusion:users').style.display = 'none';
			}else{
				document.getElementById('modalPanelCustomActionForm:customActionInclusion:users').style.display = 'block';
			}
		}

	</script>

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
				width:250px;
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
	</style>

	<h:panelGroup layout="block">
		<rich:panel style="position:relative; border:none; padding-bottom: 15px; height:310px; width:574px;" rendered="#{CustomActionModel.modalPanelModel.DATA_ERROR_MSG==null}">

			<h:selectOneMenu id="statusList" value="#{CustomActionModel.modalPanelModel.DATA_STATUS}">
				<f:selectItems id="items" value="#{CustomActionModel.modalPanelModel.DATA_LIST_STATUS}"/>
				<a4j:support event="onchange" oncomplete="displayGestList();"/>
			</h:selectOneMenu>

			<rich:panel id="users" header="#{MessageBundleModel.jsp_postman_users}" style="overflow:auto; width:550px; height: auto; max-height: 189px; display:none;">
				<rich:dataTable
							styleClass="customActionDataTable"
							cellpadding="0" cellspacing="0"
							width="100%" border="0"
							var="USER_ID"
							value="#{CustomActionModel.modalPanelModel.DATA_USERS_ID}">
					<rich:column>
						<f:facet name="header">
							<h:outputText value=""/>
						</f:facet>
						<h:selectBooleanCheckbox value="#{CustomActionModel.modalPanelModel.DATA_USERS_CHECKED[USER_ID]}"/>
					</rich:column>
					<rich:column sortBy="#{CustomActionModel.modalPanelModel.DATA_USERS_LOGINS[USER_ID]}" sortOrder="ASCENDING" filterBy="#{CustomActionModel.modalPanelModel.DATA_USERS_LOGINS[USER_ID]}" filterEvent="onchange">
                        <f:facet name="header">
                            <h:outputText value="#{MessageBundleModel.jsp_login}"/>
                        </f:facet>
                        <h:outputText value="#{CustomActionModel.modalPanelModel.DATA_USERS_LOGINS[USER_ID]}"/>
                    </rich:column>
					<rich:column filterBy="#{CustomActionModel.modalPanelModel.DATA_USERS_NAMES[USER_ID]}" filterEvent="onchange">
						<f:facet name="header">
							<h:outputText value="#{MessageBundleModel.jsp_name}"/>
						</f:facet>
						<h:outputText value="#{CustomActionModel.modalPanelModel.DATA_USERS_NAMES[USER_ID]}"/>
					</rich:column>
					<rich:column filterBy="#{CustomActionModel.modalPanelModel.DATA_USERS_FIRSTNAMES[USER_ID]}" filterEvent="onchange">
						<f:facet name="header">
							<h:outputText value="#{MessageBundleModel.jsp_firstname}"/>
						</f:facet>
						<h:outputText value="#{CustomActionModel.modalPanelModel.DATA_USERS_FIRSTNAMES[USER_ID]}"/>
					</rich:column>
				</rich:dataTable>
			</rich:panel>
			<rich:panel header="#{MessageBundleModel.jsp_comment}" style="overflow:auto; width:550px; height: auto; margin-top: 2px; margin-buttom: 0px">
				<rich:panel style="border-width: 0px; padding-top:0px; margin-top: 0px;">
					<h:inputTextarea maxlenght="255" cols="70" rows="4" value="#{CustomActionModel.modalPanelModel.DATA_COMMENT}"/>
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