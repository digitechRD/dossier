<jsp:root version="2.0"
          xmlns:h="http://java.sun.com/jsf/html"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:rich="http://richfaces.org/rich">
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
      <rich:panel header="#{MessageBundleModel.jsp_comment}"
                  style="overflow:auto; width:550px; height: auto; max-height: 250px; margin-top: 2px; margin-buttom: 0px">
        <rich:panel style="border-width: 0px; padding-top:0px; margin-top: 0px;">
          <h:inputTextarea maxlenght="255" cols="70" rows="10" value="#{CustomActionModel.modalPanelModel.DATA_COMMENT}" escape="false"/>
        </rich:panel>
      </rich:panel>
      <rich:panel header="#{MessageBundleModel.jsp_comment_page}"
                  style="overflow:auto; width:550px; height: auto; max-height: 50px; margin-top: 2px; margin-buttom: 0px">
        <rich:panel style="border-width: 0px; padding-top:0px; margin-top: 0px;">
          <h:inputText id="page" value="#{CustomActionModel.modalPanelModel.DATA_PAGE}"/>
        </rich:panel>
      </rich:panel>
    </rich:panel>
    <center>
      <rich:panel style="overflow:auto; width:540px; margin-top:135px; margin-bottom:10px; text-align: center;"
                  rendered="#{CustomActionModel.modalPanelModel.DATA_ERROR_MSG!=null}">
        <h:outputText value="#{CustomActionModel.modalPanelModel.DATA_ERROR_MSG}" styleClass="message_error"/>
      </rich:panel>
    </center>
  </h:panelGroup>
</jsp:root>