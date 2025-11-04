<%-- jsf:pagecode language="java" location="/JavaSource/pagecode/pilotage/Pilotage.java" --%><%-- /jsf:pagecode --%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.ibm.com/jsf/html_extended" prefix="hx"%>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<HTML>
<HEAD>
	<META http-equiv="Refresh" content="60" url="/pilotage/pilotage.jsp">
	<META http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<META name="GENERATOR" content="IBM Software Development Platform">
	<META http-equiv="Content-Style-Type" content="text/css">
	<LINK href="../theme/dossiers.css" rel="stylesheet" type="text/css">
		
		<TITLE>pilotage.jsp</TITLE>
	
	<LINK rel="stylesheet" type="text/css" href="../theme/stylesheet.css" title="Style">
    <SCRIPT type="text/javascript" src="../js/overlib.js"><!-- overLIB (c) http://www.bosrup.com/web/overlib/ --></SCRIPT>
	<LINK rel="stylesheet" type="text/css" href="../theme/stylesheet.css"
		title="Style">
	<LINK rel="stylesheet" type="text/css" href="../theme/pilotstyle.css">
</HEAD>

<SCRIPT src="../js/dossiers.js" type="text/javascript"></SCRIPT>
    
<f:view>
	<f:loadBundle basename="com.digitech.airs3dossiers.messageressource.MessageRessource" var="msg"/>
	<% 
		String openWin = "";
		String goBirt = (String)session.getAttribute("goBIRT");
		
		if (goBirt != null && !"".equals(goBirt)) 
		{			
			openWin = "window.open('../PilBirtLauncher.jsp', 'Pilotage', 'dependent=yes resizable=yes')";
			session.setAttribute("goBIRT", ""); 
		} 
	%>
	<BODY onunload="window.parent.autoLogout.fermerDocument();" onload="<% out.print(openWin); %>">
	<hx:scriptCollector id="scriptCollector2">
		<DIV id="overDiv"
			style="position:absolute; visibility:hidden; z-index:1000;"></DIV>
		<%@include file="/WEB-INF/jspf/header.jsf"%>
		<h:form styleClass="form" id="form1">
			<br/>
			<table width="100%">
				<tr>					
					<td width="15%" valign="top">
						<h:panelGrid styleClass="panelGrid" id="grid2" columns="1">
								<h:commandLink styleClass="commandLink" id="link2"
									style="text-align:center;" action="#{pc_Pilotage.doLink2Action}">
									<hx:graphicImageEx styleClass="graphicImageEx" id="imageEx2"
										value="../theme/Images/action.enrichissement.gif"
										style="border:0px;" title="Monitoring" alt="Monitoring"></hx:graphicImageEx>
								</h:commandLink>
								<h:commandLink styleClass="commandLink" id="link1" action="#{pc_Pilotage.doLink1Action}">
									<hx:graphicImageEx styleClass="graphicImageEx" id="imageEx1"
										value="../theme/Images/v8_webmaster.gif" width="32"
										height="32" style="border:0px;" title="Paramétrage" alt="Paramétrage"></hx:graphicImageEx>
								</h:commandLink>
								<h:commandLink styleClass="commandLink" id="link3" action="#{pc_Pilotage.doLink3Action}">
							<hx:graphicImageEx styleClass="graphicImageEx" id="imageEx3"
								value="../theme/Images/NextArrow.gif" width="32" height="32"
								style="border:0px;" title="Transfert" alt="Transfert"></hx:graphicImageEx>
						</h:commandLink>
							</h:panelGrid>
					</td>
					<td valign="top">
						<h:panelGrid styleClass="panelGrid" id="grid3" width="100%"	columns="1">

						<h:panelGrid styleClass="panelGrid" id="grid5" columns="3" width="50%" rowClasses="pilotParamRow1,pilotParamRow2,pilotParamRow2,pilotParamRow2" 
								columnClasses="pilotParamCol1,pilotParamCol2,pilotParamCol3">							
								<h:outputText styleClass="outputText" id="text2" value="Site"></h:outputText>
								<h:outputText styleClass="outputText" id="text4" value="="></h:outputText>
								<h:selectOneMenu styleClass="selectOneMenu" id="menuSites"
								value="#{pc_Pilotage.site}" onchange="submit();">
									<f:selectItems value="#{pc_Pilotage.lstSites}" />
								</h:selectOneMenu>
							<h:outputText styleClass="outputText" id="text5"></h:outputText>
							<h:outputText styleClass="outputText" id="text6"></h:outputText>
							<h:outputText styleClass="outputText" id="text7"></h:outputText>							
							<h:outputText styleClass="outputText" id="text8" style="height:20px;"></h:outputText>							
						</h:panelGrid>
						<h:panelGrid styleClass="panelGrid" id="grid1" columns="4">
							<hx:graphicImageEx styleClass="graphicImageEx" id="imageEx4"
								value="../theme/Images/warning.gif"
								style="visibility:#{pc_Pilotage.warning};"></hx:graphicImageEx>
							<h:outputText styleClass="outputText" id="text1"
								value="Site en surcapacité"
								style="color:red; visibility:#{pc_Pilotage.warning};"></h:outputText>
							<h:outputText styleClass="outputText" id="text9"
								style="width:60px;display:block;float:left;"></h:outputText>
							<h:panelGrid styleClass="panelGrid" id="grid4" columns="2">
								<h:outputText styleClass="outputText" id="text15"
									value="A prendre en charge : "></h:outputText>
								<h:outputText styleClass="outputText" id="text16" value="#{pc_Pilotage.charge}"></h:outputText>
								<h:outputText styleClass="outputText" id="text17" value=""></h:outputText>
								<h:outputText styleClass="outputText" id="text18" value=""></h:outputText>
							</h:panelGrid>
						</h:panelGrid>
						<h:panelGrid styleClass="panelGrid" id="grid6" columns="1" rowClasses="pilotParamRow1,pilotParamRow2">
							<h:dataTable border="0" cellpadding="2" cellspacing="1"
								columnClasses="pilotCol" headerClass=""
								footerClass="footerClass" rowClasses="rowClass1, rowClass2"
								styleClass="dataTable" id="table1" value="#{pc_Pilotage.lstStockCourrier}" var="stock">
								<h:column id="column1">
									<f:facet name="header">
										<h:outputText id="text3" styleClass="pilotHeader"
											value="Cellule" style="width:200px;display:block;float:left;"></h:outputText>
									</f:facet>
									<h:outputText id="text20" value="#{stock.cellule}"/>
								</h:column>
								<h:column id="column2">
									<f:facet name="header">
										<h:outputText id="text10" styleClass="pilotHeader"
											value="Courrier à traiter" style="width:100px;display:block;float:left;"></h:outputText>
									</f:facet>
									<h:outputText id="text21" value="#{stock.courrierStock}"/>
								</h:column>
								<h:column id="column3">
									<f:facet name="header">
										<h:outputText id="text11" styleClass="pilotHeader"
											value="Courrier à corriger" style="width:120px;display:block;float:left;"></h:outputText>
									</f:facet>
									<h:outputText id="text22" value="#{stock.courrierACorriger}"/>
								</h:column>
								<h:column id="column4">
									<f:facet name="header">
										<h:outputText id="text12" styleClass=""
											value="Courrier à réécrire" style="width:120px;display:block;float:left;"></h:outputText>
									</f:facet>
									<h:outputText id="text23" value="#{stock.courrierAReecrire}"/>
								</h:column>
								<h:column id="column5">
									<f:facet name="header">
										<h:outputText id="text13" styleClass="pilotHeader"
											value="Courrier à valider" style="width:100px;display:block;float:left;"></h:outputText>
									</f:facet>
									<h:outputText id="text24" value="#{stock.courrierAValider}"/>
								</h:column>
								<h:column id="column6">
									<f:facet name="header">
										<h:outputText id="text89" styleClass="pilotHeader"
											value="Courbe" style="width:100px;display:block;float:left;"></h:outputText>
									</f:facet>
									<h:commandLink styleClass="commandLink" action="#{pc_Pilotage.doPilotCurve}">
										<h:outputText id="text14" styleClass="outputText"
											value="voir la courbe"></h:outputText>
									</h:commandLink>						
								</h:column>
							</h:dataTable>
							<h:outputText styleClass="outputText" id="text19" value="Il n'y a aucun document à traiter" style="visibility:#{pc_Pilotage.stockEmpty};"></h:outputText>
						</h:panelGrid>							
						</h:panelGrid>
					</td>
				</tr>				
			</table>
		</h:form>
		<%@include file="/WEB-INF/jspf/footer.jsf"%>
	</hx:scriptCollector>
	</BODY>
</f:view>

</HTML>
