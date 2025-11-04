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

	<dossier:actionBar id="actionBar" backRendered="false">
		<ui:define name="secondaryActionPanel">
			<a4j:htmlCommandLink actionListener="#{GenericDownLoadController.processAction}" renderer="#{empty CustomActionModel.modalPanelModel.docsigned}" ajaxSingle="true" limitToList="true" ignoreDupResponses="true" styleClass="customAction">
				<f:attribute name="file" value="#{CustomActionModel.modalPanelModel.docsigned}" />
			</a4j:htmlCommandLink>				
		</ui:define>
	</dossier:actionBar>
	
	<!-- display history from SB  -->
	 <rich:dataTable width="700" var="histoRecord" value="#{CustomActionModel.modalPanelModel.histoRecord}">
	  <f:facet name="header">
            <rich:columnGroup>
                <rich:column>
                    <h:outputText value="Date" />
                </rich:column>
                <rich:column>
                    <h:outputText value="Commentaire" />
                </rich:column>                          
            </rich:columnGroup>
        </f:facet>
        
          <rich:column>
            <h:outputText value="#{histoRecord.date}" />
        </rich:column>
        <rich:column>
            <h:outputText value="#{histoRecord.annotation}" />
        </rich:column>
              
	 </rich:dataTable>
	 <!--  display comment from SB - /!\ date from SBmaybe null  -->
	  <rich:dataTable width="700" var="commentRecord" value="#{CustomActionModel.modalPanelModel.commentRecord}">
	  <f:facet name="header">
            <rich:columnGroup>
                <rich:column>
                    <h:outputText value="Sender_Label" />
                </rich:column>
                <rich:column>
                    <h:outputText value="comment_Date" />
                </rich:column>  
                 <rich:column>
                    <h:outputText value="comment_Text" />
                </rich:column>                          
            </rich:columnGroup>
        </f:facet>
        
          <rich:column>
            <h:outputText value="#{commentRecord.sender_Label}" />
        </rich:column>
        <rich:column>
            <h:outputText value="#{commentRecord.comment_Date}" />
        </rich:column>
              
              <rich:column>
            <h:outputText value="#{commentRecord.comment_Text}" />
        </rich:column>
	 </rich:dataTable>

</jsp:root>