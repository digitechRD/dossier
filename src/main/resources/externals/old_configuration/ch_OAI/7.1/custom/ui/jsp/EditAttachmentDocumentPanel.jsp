<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core"
          xmlns:h="http://java.sun.com/jsf/html"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:a4j="http://richfaces.org/a4j"
          xmlns:rich="http://richfaces.org/rich">

  <script language="javascript" type="text/javascript">
    window.onload = start();

    function start() {
      displayOptionList(document.getElementById("modalPanelCustomActionForm:customActionInclusion:statusList").value);
      updateListPageCopy();
    }

    function displayOptionList(value) {
      if (value == 0) {
        document.getElementById('modalPanelCustomActionForm:customActionInclusion:deletePages').style.display = 'block';
        document.getElementById('modalPanelCustomActionForm:customActionInclusion:copyPages').style.display = 'none';
        document.getElementById('modalPanelCustomActionForm:customActionInclusion:insertPages').style.display = 'none';
        document.getElementById('modalPanelCustomActionForm:customActionInclusion:rotationPages').style.display = 'none';
        document.getElementById('modalPanelCustomActionForm:customActionInclusion:rotationPagesInput').style.display = 'none';
      } else if (value == 1) {
        document.getElementById('modalPanelCustomActionForm:customActionInclusion:deletePages').style.display = 'none';
        document.getElementById('modalPanelCustomActionForm:customActionInclusion:copyPages').style.display = 'block';
        document.getElementById('modalPanelCustomActionForm:customActionInclusion:insertPages').style.display = 'none';
        document.getElementById('modalPanelCustomActionForm:customActionInclusion:rotationPages').style.display = 'none';
        document.getElementById('modalPanelCustomActionForm:customActionInclusion:rotationPagesInput').style.display = 'none';
      } else if (value == 2) {
        document.getElementById('modalPanelCustomActionForm:customActionInclusion:deletePages').style.display = 'none';
        document.getElementById('modalPanelCustomActionForm:customActionInclusion:copyPages').style.display = 'none';
        document.getElementById('modalPanelCustomActionForm:customActionInclusion:insertPages').style.display = 'block';
        document.getElementById('modalPanelCustomActionForm:customActionInclusion:rotationPages').style.display = 'none';
        document.getElementById('modalPanelCustomActionForm:customActionInclusion:rotationPagesInput').style.display = 'none';
      } else if (value == 3) {
        document.getElementById('modalPanelCustomActionForm:customActionInclusion:deletePages').style.display = 'none';
        document.getElementById('modalPanelCustomActionForm:customActionInclusion:copyPages').style.display = 'none';
        document.getElementById('modalPanelCustomActionForm:customActionInclusion:insertPages').style.display = 'none';
        document.getElementById('modalPanelCustomActionForm:customActionInclusion:rotationPages').style.display = 'block';
        document.getElementById('modalPanelCustomActionForm:customActionInclusion:rotationPagesInput').style.display = 'block';
      }
    }

    function updateListPageCopy() {
      try {
        var i = 0;
        var j = 0;
        var optionHTMLEnd;
        var numberPages = document.getElementById('modalPanelCustomActionForm:customActionInclusion:number_pages').value;
        var listPages = document.getElementById('modalPanelCustomActionForm:customActionInclusion:list_pages').value.split(';');
        var pageCopyStart = + document.getElementById('modalPanelCustomActionForm:customActionInclusion:page_copy_start').value;
        var selectDivEndCopy = document.getElementById("page_copy_end_div");
        optionHTMLEnd += '<div><select id="page_copy_end_select" onchange="selectPageCopyEnd(this.value);">';
        i = pageCopyStart - 1;
        j = pageCopyStart;
        while (listPages[i]) {
          var listPageSplit = listPages[i].split('::');
          if (start == i) optionHTMLEnd += '<option value="' + listPageSplit[0] + '" selected="selected">' + listPageSplit[1] + '</option>';
          else optionHTMLEnd += '<option value="' + listPageSplit[0] + '">' + listPageSplit[1] + '</option>';
          i ++;
        }
        optionHTMLEnd += '</select></div>';
        selectDivEndCopy.innerHTML = optionHTMLEnd;
        selectDivEndCopy.innerHTML = selectDivEndCopy.innerHTML.replace('undefined', '');
        selectPageCopyEnd(j);
      } catch (e) {
        alert(e);
      }
    }

    function selectPageCopyEnd(value) {
      document.getElementById('modalPanelCustomActionForm:customActionInclusion:page_copy_end_value').value = value;
    }


  </script>

  <style>
    .radioStyle tr td {
      width: 120px;
    }

    .radioStyle tr td input {
      float: none;
    }

    .radioStyle tr td label {
      clear: none;
      display: inline;
      float: none;
      font-weight: bold;
      padding-right: 0px;
      text-align: left;
    }

    .selectItemsStyle {
      width: 250px;
    }

    .customActionMsg {
      padding: 1px;
    }

    .customActionDistribute div {
      padding: 1px;
    }

    .customActionDataTable {
      border: 1px solid #666;
    }

    .customActionDataTable thead th {
      border: 1px solid #666;
      background-color: #FFFFFF;
    }

    .customActionDataTable tbody td {
      border-right: 1px solid #666;
      border-bottom: none;
    }

    .customActionDataTable tbody tr:nth-child(odd) {
      background-color: #F2F2F2;
      border-bottom: none;
    }

    .customActionDataTable tbody tr:hover {
      background-color: #e2e059;
    }

    .preformatted {
      white-space: pre-wrap;
    }

    .message_error {
      color: red;
      font-weight: bold;
      font-size: 13px;
      text-align: center;
    }

    .message_warn {
      color: orange;
      font-weight: bold;
      font-size: 13px;
      text-align: center;
    }

    .rich-panel-body {
      padding-top: 5px;
    }
  </style>

  <h:panelGroup layout="block">
    <rich:panel style="position:relative; border:none; padding-bottom: 15px; height:310px; width:574px;"
                rendered="#{CustomActionModel.modalPanelModel.DATA_ERROR_MSG==null}">
      <h:outputLabel style="width:150px;" value="#{MessageBundleModel.jsp_page_selection}" for="statusList"/>
      <h:selectOneMenu id="statusList" value="#{CustomActionModel.modalPanelModel.DATA_STATUS}">
        <f:selectItems id="items" value="#{CustomActionModel.modalPanelModel.DATA_LIST_STATUS}"/>
        <a4j:support event="onchange" oncomplete="displayOptionList(this.value);"/>
      </h:selectOneMenu>

      <rich:panel id="deletePages" header="#{MessageBundleModel.jsp_pages_deleted}"
                  style="overflow:auto; width:550px; height: auto; max-height: 272px; display:block;">
        <rich:dataTable
            styleClass="customActionDataTable"
            cellpadding="0" cellspacing="0"
            width="100%" border="0"
            var="PAGE_ID"
            value="#{CustomActionModel.modalPanelModel.DATA_PAGES_DELETE_ID}">
          <rich:column style="width:33px;">
            <f:facet name="header">
              <h:outputText value=""/>
            </f:facet>
            <h:selectBooleanCheckbox value="#{CustomActionModel.modalPanelModel.DATA_PAGES_DELETE_CHECKED[PAGE_ID]}"/>
          </rich:column>
          <rich:column>
            <f:facet name="header">
              <h:outputText value="#{MessageBundleModel.jsp_page}"/>
            </f:facet>
            <h:outputText value="#{CustomActionModel.modalPanelModel.DATA_PAGES_DELETE_NUMBER_LIST[PAGE_ID]}"/>
          </rich:column>
        </rich:dataTable>
      </rich:panel>
      <rich:panel id="copyPages" header="#{MessageBundleModel.jsp_pages_copy}"
                  style="overflow:auto; width:550px; height: auto; max-height: 200px; display:none;">
        <h:outputLabel style="width:150px;" value="#{MessageBundleModel.jsp_page_copy_name}" for="page_copy_name"/>
        <h:inputText id="page_copy_name" style="width:350px;" value="#{CustomActionModel.modalPanelModel.DATA_PAGES_COPY_NAME}"/>
        <br/>
        <h:outputLabel style="width:150px;" value="#{MessageBundleModel.jsp_page_start}" for="page_copy_start"/>
        <h:selectOneMenu id="page_copy_start" value="#{CustomActionModel.modalPanelModel.DATA_PAGES_COPY_START_PAGE}" onchange="updateListPageCopy();">
          <f:selectItems id="page_copy_start_list" value="#{CustomActionModel.modalPanelModel.DATA_PAGES_COPY_START_LIST}"/>
        </h:selectOneMenu>
        <h:outputLabel style="width:150px;" value="#{MessageBundleModel.jsp_page_end}" for="page_copy_end"/>
        <div id="page_copy_end_div"></div>
        <br/>
        <h:inputText style="width:300px;display:none;" id="list_pages" value="#{CustomActionModel.modalPanelModel.DATA_PAGES_COPY_LIST}"/>
        <h:inputText style="width:300px;display:none;" id="page_copy_end_value" value="#{CustomActionModel.modalPanelModel.DATA_PAGES_COPY_END_PAGE}"/>
      </rich:panel>
      <rich:panel id="insertPages" header="#{MessageBundleModel.jsp_pages_insert}"
                  style="overflow:auto; width:550px; height: auto; max-height: 200px; display:none;">
        <h:outputLabel style="width:150px;" value="#{MessageBundleModel.jsp_page_copy_name}" for="page_insert_name"/>
        <h:selectOneMenu id="page_insert_name" value="#{CustomActionModel.modalPanelModel.DATA_PAGES_INSERT_NAME}">
          <f:selectItems id="page_insert_name_list" value="#{CustomActionModel.modalPanelModel.DATA_PAGES_INSERT_NAME_LIST}"/>
        </h:selectOneMenu>
        <br/>
        <h:outputLabel style="width:150px;" value="#{MessageBundleModel.jsp_page_start}" for="page_insert_start"/>
        <h:selectOneMenu id="page_insert_start" value="#{CustomActionModel.modalPanelModel.DATA_PAGES_INSERT_START_PAGE}">
          <f:selectItems id="page_insert_start_list" value="#{CustomActionModel.modalPanelModel.DATA_PAGES_INSERT_START_LIST}"/>
        </h:selectOneMenu>
      </rich:panel>
      <rich:panel id="rotationPages" header="#{MessageBundleModel.jsp_pages_rotation}"
                  style="overflow:auto; width:550px; height: auto; max-height: 250px; display:block;">
        <rich:dataTable
            styleClass="customActionDataTable"
            cellpadding="0" cellspacing="0"
            width="100%" border="0" height="90%"
            var="PAGE_ID"
            value="#{CustomActionModel.modalPanelModel.DATA_PAGES_ROTATION_ID}">
          <rich:column style="width:33px;">
            <f:facet name="header">
              <h:outputText value=""/>
            </f:facet>
            <h:selectBooleanCheckbox value="#{CustomActionModel.modalPanelModel.DATA_PAGES_ROTATION_CHECKED[PAGE_ID]}"/>
          </rich:column>
          <rich:column>
            <f:facet name="header">
              <h:outputText value="#{MessageBundleModel.jsp_page}"/>
            </f:facet>
            <h:outputText value="#{CustomActionModel.modalPanelModel.DATA_PAGES_ROTATION_NUMBER_LIST[PAGE_ID]}"/>
          </rich:column>
        </rich:dataTable>
      </rich:panel>
      <br/>
      <rich:panel id="rotationPagesInput" style="height: 27px; max-height: 27px; display:block;">
        <h:outputLabel style="width:55px;" value="#{MessageBundleModel.jsp_page_rotation}" for="page_insert_rotation"/>
        <h:selectOneMenu id="page_insert_rotation" value="#{CustomActionModel.modalPanelModel.DATA_PAGES_ROTATION_SELECTED}">
          <f:selectItems id="page_insert_rotation_list" value="#{CustomActionModel.modalPanelModel.DATA_PAGES_ROTATION_LIST}"/>
        </h:selectOneMenu>
      </rich:panel>
      <h:inputText style="width:300px;display:none;" id="number_pages" value="#{CustomActionModel.modalPanelModel.DATA_PAGES_NUMBER}"/>
    </rich:panel>
    <center>
      <rich:panel style="overflow:auto; width:540px; margin-top:135px; margin-bottom:10px; text-align: center;"
                  rendered="#{CustomActionModel.modalPanelModel.DATA_ERROR_MSG!=null}">
        <h:outputText value="#{CustomActionModel.modalPanelModel.DATA_ERROR_MSG}" styleClass="message_error"/>
      </rich:panel>
    </center>
  </h:panelGroup>
</jsp:root>