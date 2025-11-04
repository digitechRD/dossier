<jsp:root version="2.0"
          xmlns:h="http://java.sun.com/jsf/html"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:a4j="http://richfaces.org/a4j"
          xmlns:ui="http://java.sun.com/jsf/facelets"
          xmlns:f="http://java.sun.com/jsf/core"
          xmlns:dossier="http://dossier.digitech.com/jsf/html"
          xmlns:c="http://java.sun.com/jstl/core"
          xmlns:fn="http://java.sun.com/jsp/jstl/functions"
          xmlns:webuijsf="http://www.sun.com/webui/webuijsf"
          xmlns:rich="http://richfaces.org/rich">

    <h:panelGroup id="modalPanelAddUserInCopyContainer" layout="block">

        <script>
            var userSelected = false;

            function #{id}_onHide() {
                selectFirstSelectMultiVal('#{SelectUserAdvancedController.model.locutionModel.fieldComponent.id}');
            }

            function initTableSize() {
                var tableContent = $jQ('.#{id} .dataTable').parent();
                var modalPanelHeight = $jQ('.#{id} .rich-mpnl-content').innerHeight();
                var newHeight = (modalPanelHeight > 510 ? modalPanelHeight : 510) - 85;
                tableContent.css("height", newHeight + "px");
            }

            function #{id}_onResizeCompleted() {
                initTableSize();
            }

            function #{id}_onShow() {
                initTableSize();
            }
        </script>

        <a4j:outputPanel ajaxRendered="true" layout="block"
                         style="height: 510px; overflow: auto;">
            <dossier:dataTable tableId="selectUserTable"
                               clearSortingLabel="#{MessageBundleModel.table_action_clear_sorting_and_filter}"
                               tableModel="#{SelectUserAdvancedController.model.tableModel}"
                               tableController="#{SelectUserAdvancedController.tableController}"
                               titleRendered="false" topActionRendered="true"
                               resultsByPageRendered="true" topDataScrollerRendered="true"
                               styleClass="#{SelectUserAdvancedController.model.mutivalued ? '' : 'selectable'}"
                               clearSortingRendered="#{!ApplicationModel.actionBarEnabled}">

                <ui:define name="customTopAction">
                    <c:if test="#{ApplicationModel.actionBarEnabled}">
                        <dossier:actionBar id="selectUserActionBar" backRendered="false">
                            <ui:define name="secondaryActionPanel">
                                <a4j:commandLink styleClass="clearSorting first"
                                                 title="#{MessageBundleModel.table_action_clear_sorting}"
                                                 action="#{SelectUserAdvancedController.tableController.clearSorting}"
                                                 reRender="selectUserTable"/>
                            </ui:define>
                        </dossier:actionBar>
                    </c:if>
                </ui:define>

                <rich:column id="multipleSelectionColumn"
                             styleClass="#{row.selected ? 'current' : ''}"
                             rendered="#{SelectUserAdvancedController.model.mutivalued}">
                    <f:facet name="header">
                        <h:panelGroup layout="block">
                            <h:selectBooleanCheckbox
                                    rendered="#{! SelectUserAdvancedController.model.numberOfSelectableUserLimited}"
                                    onclick="checkAllCheckboxesInTable(this.name, this.checked);"/>
                        </h:panelGroup>
                    </f:facet>
                    <h:selectBooleanCheckbox value="#{row.selected}"
                                             onclick="updateCheckboxes('userAdvancedModalPanelLazyLoader:lazyModal:userAdvancedModalPanelForm:selectUserTable');blurAction(this);">
                        <a4j:support event="onchange" action="#{row.changeSelection}"/>
                    </h:selectBooleanCheckbox>
                </rich:column>
                <dossier:filteredColumn colId="lastName"
                                        styleClass="#{row.selected ? 'current' : ''}"
                                        sortBy="#{row.lastName}"
                                        headerText="#{MessageBundleModel.modalPanelSelectUser_header_lastName}"
                                        filterExpression="#{fn:startsWith(fn:toLowerCase(row.lastName), fn:toLowerCase(SelectUserAdvancedController.model.tableModel.filter.lastName))}"
                                        filterValue="#{SelectUserAdvancedController.model.tableModel.filter.lastName}"
                                        filterReRender="selectUserTable">
                    <h:outputText value="#{row.lastName}"/>
                </dossier:filteredColumn>
                <dossier:filteredColumn colId="firstName"
                                        styleClass="#{row.selected ? 'current' : ''}"
                                        sortBy="#{row.firstName}"
                                        headerText="#{MessageBundleModel.modalPanelSelectUser_header_firstName}"
                                        filterExpression="#{fn:startsWith(fn:toLowerCase(row.firstName), fn:toLowerCase(SelectUserAdvancedController.model.tableModel.filter.firstName))}"
                                        filterValue="#{SelectUserAdvancedController.model.tableModel.filter.firstName}"
                                        filterReRender="selectUserTable">
                    <h:outputText value="#{row.firstName}"/>
                </dossier:filteredColumn>
                <dossier:filteredColumn colId="trigram"
                                        styleClass="#{row.selected ? 'current' : ''}"
                                        sortBy="#{row.trigram}"
                                        headerText="#{MessageBundleModel.modalPanelSelectUser_header_trigram}"
                                        filterExpression="#{fn:startsWith(fn:toLowerCase(row.trigram), fn:toLowerCase(SelectUserAdvancedController.model.tableModel.filter.trigram))}"
                                        filterValue="#{SelectUserAdvancedController.model.tableModel.filter.trigram}"
                                        filterReRender="selectUserTable">
                    <h:outputText value="#{row.trigram}"/>
                </dossier:filteredColumn>
                <dossier:filteredColumn colId="org"
                                        styleClass="#{row.selected ? 'current' : ''}"
                                        sortBy="#{row.organizationsAsString}"
                                        headerText="#{MessageBundleModel.modalPanelSelectUser_header_organization}"
                                        filterExpression="#{fn:containsIgnoreCase(row.organizationsAsString, SelectUserAdvancedController.model.tableModel.filter.organizations)}"
                                        filterValue="#{SelectUserAdvancedController.model.tableModel.filter.organizations}"
                                        filterReRender="selectUserTable">
                    <rich:dataList var="org" value="#{row.organizations}">
                        <h:outputText value="#{org}"/>
                    </rich:dataList>
                </dossier:filteredColumn>

                <rich:column id="selectionColumn"
                             styleClass="#{row.selected ? 'current' : ''}"
                             rendered="#{not SelectUserAdvancedController.model.mutivalued}">
                </rich:column>

                <a4j:support id="onRowClickSupport" event="onRowClick"
                             onsubmit="if (!userSelected) {userSelected = true;} else {userSelected = false;}"
                             rendered="#{not SelectUserAdvancedController.model.mutivalued}"
                             action="#{SelectUserAdvancedController.select}"
                             reRender="notifications, modalPanelError, #{SelectUserAdvancedController.model.reRender}"
                             limitToList="true"
                             oncomplete="if(#{facesContext.maximumSeverity == null}) { Richfaces.hideModalPanel('userAdvancedModalPanel'); } return false;">
                    <f:setPropertyActionListener value="#{row}"
                                                 target="#{SelectUserAdvancedController.model.selectedRow}"/>
                </a4j:support>
            </dossier:dataTable>

            <webuijsf:script>
                try {
                initTableSize();
                } catch(Exception) {}
            </webuijsf:script>

            <script>
                function updateCheckboxes(table) {
                    var tableId = jsfIDtoJQID(table);

                    var numberOfChecked = #{SelectUserAdvancedController.model.tableModel.numberOfSelectedRows};
                    $jQ(tableId + ' tbody tr td:first-child input:checkbox').each(function () {
                        if (this.checked) {
                            numberOfChecked++;
                        }
                    });

                    $jQ(tableId + ' tbody tr td:first-child input:checkbox').each(function () {
                        if (!this.checked &amp;&amp; numberOfChecked >= #{SelectUserAdvancedController.model.numberOfSelectableUsers}) {
                            $jQ(this).attr('disabled', 'disabled');
                        }
                        else {
                            $jQ(this).removeAttr('disabled');
                        }
                    });
                }
            </script>
            <script>
                function blurAction(checkBox) {
                    checkBox.blur()
                }
            </script>
        </a4j:outputPanel>

        <!--

                    <h:outputLabel value="#{MessageBundleModel.modalPanelAddUserInCopy_label_userList}" for="selectedUsers"/>
                    <h:selectManyListbox id="selectedUsers" readonly="true" size="6" disabled="true">
                        <f:selectItems value="{CustomActionModel.modalPanelModel.selectedUsers}"/>
                    </h:selectManyListbox>
                    <a4j:commandLink reRender="modalPanelAddUserInCopyContainer"
                                     onClick="Richfaces.showModalPanel('userAdvancedModalPanelLazyLoader')">
                        picker
                    </a4j:commandLink>
            -->


    </h:panelGroup>

</jsp:root>
