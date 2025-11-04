<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core"
          xmlns:h="http://java.sun.com/jsf/html"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:a4j="http://richfaces.org/a4j"
          xmlns:rich="http://richfaces.org/rich">

	<script language="javascript" type="text/javascript">
		function displayNoteList()
		{
			if(document.getElementById('modalPanelCustomActionForm:customActionInclusion:notes').style.display == 'block'){
			    document.getElementById('modalPanelCustomActionForm:customActionInclusion:notes').style.display = 'none';
			}else{
			    document.getElementById('modalPanelCustomActionForm:customActionInclusion:notes').style.display = 'block';
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
                white-space: pre-wrap;
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
                <h:outputLabel style="width:150px;" value="#{MessageBundleModel.jsp_note_selection}" for="statusList" />
            <h:selectOneMenu id="statusList" value="#{CustomActionModel.modalPanelModel.DATA_STATUS}">
                <f:selectItems id="items" value="#{CustomActionModel.modalPanelModel.DATA_LIST_STATUS}"/>
                <a4j:support event="onchange" oncomplete="displayNoteList();"/>
            </h:selectOneMenu>

            <rich:panel id="notes" header="#{MessageBundleModel.jsp_notes}" style="overflow:auto; width:550px; height: auto; max-height: 200px; display:block;">
                <rich:dataTable
                            styleClass="customActionDataTable"
                            cellpadding="0" cellspacing="0"
                            width="100%" border="0"
                            var="NOTE_ID"
                            value="#{CustomActionModel.modalPanelModel.DATA_NOTES_ID}">
                    <rich:column style="width:33px;">
                        <f:facet name="header">
                            <h:outputText value=""/>
                        </f:facet>
                        <h:selectBooleanCheckbox value="#{CustomActionModel.modalPanelModel.DATA_NOTES_CHECKED[NOTE_ID]}"/>
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="#{MessageBundleModel.jsp_note}"/>
                        </f:facet>
                        <h:outputText value="#{CustomActionModel.modalPanelModel.DATA_NOTES_COMMENT[NOTE_ID]}"/>
                    </rich:column>
                </rich:dataTable>
            </rich:panel>
        </rich:panel>
        <center>
            <rich:panel style="overflow:auto; width:540px; margin-top:135px; margin-bottom:10px; text-align: center;" rendered="#{CustomActionModel.modalPanelModel.DATA_ERROR_MSG!=null}">
                <h:outputText value="#{CustomActionModel.modalPanelModel.DATA_ERROR_MSG}" styleClass="message_error" />
            </rich:panel>
        </center>
    </h:panelGroup>
</jsp:root>