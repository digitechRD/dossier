<%-- jsf:pagecode language="java" location="/JavaSource/pagecode/pilotage/Pilotparam.java" --%><%-- /jsf:pagecode --%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.ibm.com/jsf/html_extended" prefix="hx"%>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f"%>

<HTML>
<HEAD>
	<META http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<META name="GENERATOR" content="IBM Software Development Platform">
	<META http-equiv="Content-Style-Type" content="text/css">
	<LINK href="../theme/dossiers.css" rel="stylesheet" type="text/css">
		
		<TITLE>pilotparam.jsp</TITLE>
	
	<LINK rel="stylesheet" type="text/css" href="../theme/stylesheet.css" title="Style">
    <SCRIPT type="text/javascript" src="../js/overlib.js"><!-- overLIB (c) http://www.bosrup.com/web/overlib/ --></SCRIPT>
	<LINK rel="stylesheet" type="text/css" href="../theme/stylesheet.css"
		title="Style">
	<LINK rel="stylesheet" type="text/css" href="../theme/pilotstyle.css">
</HEAD>

<SCRIPT src="../js/dossiers.js" type="text/javascript"></SCRIPT>
    
<f:view>
	<f:loadBundle basename="com.digitech.airs3dossiers.messageressource.MessageRessource" var="msg"/>
	<BODY onunload="window.parent.autoLogout.fermerDocument();">
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
									style="text-align:center;" action="#{pc_Pilotparam.doLink2Action}">
									<hx:graphicImageEx styleClass="graphicImageEx" id="imageEx2"
										value="../theme/Images/action.enrichissement.gif"
										style="border:0px;" title="Monitoring" alt="Monitoring"></hx:graphicImageEx>
								</h:commandLink>
								<h:commandLink styleClass="commandLink" id="link1" action="#{pc_Pilotparam.doLink1Action}">
									<hx:graphicImageEx styleClass="graphicImageEx" id="imageEx1"
										value="../theme/Images/v8_webmaster.gif" width="32"
										height="32" style="border:0px;" title="Paramétrage" alt="Paramétrage"></hx:graphicImageEx>
								</h:commandLink>
								<h:commandLink styleClass="commandLink" id="link3" action="#{pc_Pilotparam.doLink3Action}">
							<hx:graphicImageEx styleClass="graphicImageEx" id="imageEx3"
								value="../theme/Images/NextArrow.gif" width="32" height="32"
								style="border:0px;" title="Transfert" alt="Transfert"></hx:graphicImageEx>
						</h:commandLink>
							</h:panelGrid>
					</td>
					<td valign="top">
						<h:panelGrid styleClass="panelGrid" id="grid3" width="60%"	columns="1">
						<h:panelGrid styleClass="panelGrid" id="grid1" columns="1"
							width="100%" style="text-align:center;">
							<h:messages styleClass="message" id="updateMsg"></h:messages>
						</h:panelGrid>
						<h:panelGrid styleClass="panelGrid" id="grid5" columns="3"
							width="100%"
							rowClasses="pilotParamRow1,pilotParamRow1,pilotParamRow2,pilotParamRow2"
							columnClasses="pilotParamCol1,pilotParamCol2,pilotParamCol3">
							<h:outputText styleClass="outputText" id="text2" value="Site"></h:outputText>
							<h:outputText styleClass="outputText" id="text4" value="="></h:outputText>
							<h:selectOneMenu styleClass="selectOneMenu" id="menuSites"
								value="#{pc_Pilotparam.site}" onchange="submit();">
								<f:selectItems value="#{pc_Pilotparam.lstSites}" />
							</h:selectOneMenu>

							<h:outputText styleClass="outputText" id="text3" value="Capacité"></h:outputText>
							<h:outputText styleClass="outputText" id="text5" value="="></h:outputText>
							<h:inputText id="text1"
								value="#{pc_Pilotparam.capabilitie}" style="height:19px;"></h:inputText>
							
							<h:outputText styleClass="outputText" id="text7"></h:outputText>
							<h:outputText styleClass="outputText" id="text8"></h:outputText>
							<hx:commandExButton type="submit" value="Modifier capacité"
								styleClass="inactiveButton" id="button1"
								action="#{pc_Pilotparam.doButton1Action}"></hx:commandExButton>
							<h:outputText styleClass="outputText" id="text12"></h:outputText>

							</h:panelGrid>

						<h:panelGrid styleClass="panelGrid" id="grid6" columns="3"
							width="100%" rowClasses="pilotParamRow1,pilotParamRow2"
							columnClasses="pilotParamCol1,pilotParamCol2,pilotParamCol3">
							<h:outputText styleClass="outputText" id="labelMail" value="Mail"></h:outputText>
							<h:outputText styleClass="outputText" id="gapMail" value="="></h:outputText>
							<h:inputText id="inputMail"
								value="#{pc_Pilotparam.mail}" style="width:200px; height:19px;"/>
							<h:outputText styleClass="outputText" id="text9"></h:outputText>

							<h:outputText styleClass="outputText" id="text10"></h:outputText>
							<hx:commandExButton type="submit" value="Modifier mail"
								styleClass="inactiveButton" id="button2"
								action="#{pc_Pilotparam.doButton2Action}"></hx:commandExButton>
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
