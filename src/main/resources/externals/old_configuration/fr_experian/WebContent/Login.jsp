<%-- jsf:pagecode language="java" location="/JavaSource/pagecode/Login.java" --%><%-- /jsf:pagecode --%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">


<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@taglib uri="http://www.ibm.com/jsf/html_extended" prefix="hx"%>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@taglib uri="WEB-INF/c-rt.tld" prefix="c"%>


<HTML>
<HEAD>

<META http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<META name="GENERATOR" content="IBM Software Development Platform">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK href="./theme/dossiers.css" rel="stylesheet" type="text/css">

	<TITLE>Login.jsp</TITLE>
	<LINK rel="stylesheet" type="text/css" href="theme/stylesheet.css"	title="Style">

</HEAD>

<SCRIPT src="js/dossiers.js" type="text/javascript"></SCRIPT>
<SCRIPT type="text/javascript">
	function initBody()
	{
		document.getElementById('form1:inputLogin').focus();
	}
</SCRIPT> 

<f:view>
	<f:loadBundle basename="com.digitech.airs3dossiers.messageressource.MessageRessource" var="msg"/>
	<BODY onload="javascript:initBody();">
		<hx:scriptCollector id="scriptCollector1">
			<P></P>

			<h:form styleClass="form" id="form1">
				<TABLE border="0" width="100%" height="100%" cellpadding="0">
				<TBODY>
					<TR>
						<TD colspan="2" align="center" valign="middle" nowrap>
															
								<hx:scriptCollector id="scriptCollector2">
								<BR>
									<TABLE border="2">
										<TBODY>
											<TR align="center">
												<TH width="450"><DIV align="left">
													<TABLE class="tableTitreAppliLogin">
													<TBODY>
														<TR>
															<TD colspan="3" Class="airsBordereauHeaderClass" align="left">
																<hx:graphicImageEx styleClass="graphicImageEx" id="imageExLogoProduct"
																value="theme/Images/prestataire.jpg" ></hx:graphicImageEx>
																<h:outputText styleClass="outputText" id="textVersion" escape="false"
																value="#{msg.version}"></h:outputText>
															</TD>
														</TR>
														<TR>
															<TD colspan="3" Class="airsLineHeaderClass">
																&nbsp;
															</TD>
														</TR>
														<TR>
															<TD width="107" height="26" align="center"></TD>
															<TD align="left" colspan="3" width="306" height="26">
																<h:outputText styleClass="outputText" id="textLoginDescription" value="#{msg.loginDescription}"></h:outputText>
																&nbsp;
																<h:outputText styleClass="message" id="sessionExpired" value="#{sessionScope.LOGIN_MESSAGE}"></h:outputText>
															</TD>
														</TR>

														<TR>
															<TD colspan=3" align="center">
																<c:if test="${requestScope.pc_Login.error}">
																	<div id="outputError" class="styleError">
																		<h:outputText id="textErrorLogin" value="#{pc_Login.textError}"></h:outputText>
																	</div>
																</c:if>
															</TD>											 															
														<TR>
															<TD width="107" height="26" align="left"><h:outputText
																styleClass="loginBold" id="textIdentifiant"
																value="#{msg.identifiant}"></h:outputText></TD>
															<TD colspan="2" width="306" height="26" align="left">
																<h:inputText styleClass="inputText" id="inputLogin"
																valueChangeListener="#{pc_Login.handleTextLoginValueChange}"
																required="true"></h:inputText>
																<h:message styleClass="message" id="errorLogin" for="inputLogin"></h:message>
															</TD>
														</TR>
														<TR>
															<TD width="107" height="26" align="left">
																<h:outputText styleClass="loginBold" id="textPassword"
																value="#{msg.password}"></h:outputText>
															</TD>
															<TD colspan="2" width="306" height="26" align="left">
																<h:inputSecret styleClass="inputSecret" id="inputPassword"></h:inputSecret>
																<h:message styleClass="message" id="errorPassword" for="inputPassword"></h:message>
															</TD>
														</TR>
														<TR>
															<TD align="center" width="107" height="26"></TD>
															<TD align="left" colspan="2" width="306" height="26">
																<hx:commandExButton type="submit" value="#{msg.loginButton}"
																onclick="DoubleClickTrapperAction(this);"
																styleClass="inactiveButton" id="buttonValidateLogin"
																action="#{pc_Login.doButtonValidateAction}">
																</hx:commandExButton>
															</TD>
														</TR>
														<TR>
															<TD width="107" height="53" align="center"></TD>
															<TD colspan="2" align="right" height="53">
																<hx:graphicImageEx styleClass="graphicImageEx" id="imageExLogoClient"
																value="theme/Images/client.gif" align="right">
																</hx:graphicImageEx>
															</TD>
														</TR>
													</TBODY>
													</TABLE>
												</DIV></TH>
											</TR>
										</TBODY>
									</TABLE>
									<CENTER></CENTER>
								<BR></hx:scriptCollector>
						</TD>
					</TR>
					<TR>
						<TD width="92" class="tdBasPage"></TD>
						<TD width="421" class="tdBasPage"></TD>
					</TR>
				</TBODY>
				</TABLE>
			</h:form>
			
			<P></P>
		</hx:scriptCollector>
	</BODY>
</f:view>

</HTML>
