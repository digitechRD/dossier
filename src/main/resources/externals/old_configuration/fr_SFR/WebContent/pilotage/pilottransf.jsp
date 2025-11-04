<%-- jsf:pagecode language="java" location="/JavaSource/pagecode/pilotage/Pilottransf.java" --%><%-- /jsf:pagecode --%>
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
		
		<TITLE>pilottransf.jsp</TITLE>
	
	<LINK rel="stylesheet" type="text/css" href="../theme/stylesheet.css" title="Style">
    <SCRIPT type="text/javascript" src="../js/overlib.js"><!-- overLIB (c) http://www.bosrup.com/web/overlib/ --></SCRIPT>
	<LINK rel="stylesheet" type="text/css" href="../theme/stylesheet.css"
		title="Style">
	<LINK rel="stylesheet" type="text/css" href="../theme/pilotstyle.css">	
</HEAD>

<SCRIPT src="../js/dossiers.js" type="text/javascript"></SCRIPT>

<SCRIPT type="text/javascript">
function disableTypo() {
	// Cette fonction met dynamiquement en readonly la typologie
	// car, en statique, JSF réinitialise le champ à chaque submit.
	document.getElementById("form1:TYPOLOGIE_DESC").readOnly = "true";
}

function openTypo(codeField,width,height,top,left)
{
  var hiddenCode="form1:"+codeField;  
  var value=document.forms['form1'].elements[hiddenCode].value;
  var tosend='../AuthorityList.jsp?id='+codeField;
  if(value.length>0)
  {
   tosend=tosend+'&idItem='+value;
  }  
  window.open(tosend,'authListPopup','width=' + width + ',height=' + height + ',TOP=' + top + ',LEFT=' + left + ',toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,copyhistory=no,resizable=yes');
}
</SCRIPT>    
<f:view>
	<f:loadBundle basename="com.digitech.airs3dossiers.messageressource.MessageRessource" var="msg"/>
	<% 
		java.util.Properties properties;
		String urlProgressBarServlet = "";

		properties  = new java.util.Properties();
		java.io.InputStream is = getClass().getResourceAsStream("/../pilotage.properties");
		try 
		{
			try 
			{				
				Integer val = (Integer)session.getAttribute("noCache");
				int intVal;
				if (val == null)
				{
					session.setAttribute("noCache", new Integer(0));
					intVal = 0;
				}
				else
					intVal = val.intValue();
				properties.load(is);
				urlProgressBarServlet = properties.getProperty("pagecode.pilotage.Pilottransf.urlProgressBarServlet");

				urlProgressBarServlet += "?noCache=" + intVal;
			
				if (intVal == 1000)
					intVal = -1;
				session.setAttribute("noCache", new Integer(intVal + 1));
			}
			finally 
			{
				is.close();
			}
		} 
		catch (java.io.IOException e) 
		{
			e.printStackTrace();
		}
	%>
	<BODY onunload="window.parent.autoLogout.fermerDocument();" onload="go(ProgressBar1, '<% out.print(urlProgressBarServlet); %>', ''); disableTypo();">
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
									style="text-align:center;" action="#{pc_Pilottransf.doLink2Action}">
									<hx:graphicImageEx styleClass="graphicImageEx" id="imageEx2"
										value="../theme/Images/action.enrichissement.gif"
										style="border:0px;" alt="Monitoring" title="Monitoring"></hx:graphicImageEx>
								</h:commandLink>
								<h:commandLink styleClass="commandLink" id="link1" action="#{pc_Pilottransf.doLink1Action}">
									<hx:graphicImageEx styleClass="graphicImageEx" id="imageEx1"
										value="../theme/Images/v8_webmaster.gif" width="32"
										height="32" style="border:0px;" alt="Paramétrage" title="Paramétrage"></hx:graphicImageEx>
								</h:commandLink>
								<h:commandLink styleClass="commandLink" id="link3" action="#{pc_Pilottransf.doLink3Action}">
							<hx:graphicImageEx styleClass="graphicImageEx" id="imageEx3"
								value="../theme/Images/NextArrow.gif" width="32" height="32"
								style="border:0px;" alt="Transfert" title="Transfert"></hx:graphicImageEx>
						</h:commandLink>
							</h:panelGrid>
					</td>
					<td valign="top">
						<h:panelGrid styleClass="panelGrid" id="grid3" width="80%"	columns="1">

						<h:panelGrid styleClass="panelGrid" id="grid5" columns="4"
							width="100%" rowClasses="pilotTransfRow1,pilotTransfRow2,pilotTransfRow1,pilotTransfRow2,pilotTransfRow2,pilotTransfRow2"
							columnClasses="pilotTransfCol1,pilotTransfCol2,pilotTransfCol3,pilotTransfCol4">
							<h:outputText styleClass="outputText" id="text2"
								value="Site source"></h:outputText>
							<h:outputText styleClass="outputText" id="text4" value="="></h:outputText>
							<h:selectOneMenu styleClass="selectOneMenu" id="menuSitesSrc"
								value="#{pc_Pilottransf.siteSrc}" onchange="submit();" valueChangeListener="#{pc_Pilottransf.onChange}">
								<f:selectItems value="#{pc_Pilottransf.lstSitesSrc}" />
							</h:selectOneMenu>
							<h:outputText styleClass="outputText" id="text16"></h:outputText>
							<h:outputText styleClass="outputText" id="text3"
								value="Site destinataire"></h:outputText>
							<h:outputText styleClass="outputText" id="text5" value="="></h:outputText>
							<h:selectOneMenu styleClass="selectOneMenu" id="menuSitesDest"
								value="#{pc_Pilottransf.siteDest}">
								<f:selectItems value="#{pc_Pilottransf.lstSitesDest}" />
							</h:selectOneMenu>
							<h:outputText styleClass="outputText" id="text17"></h:outputText>

							<h:outputText styleClass="outputText" id="text7"
								value="Date de réception"></h:outputText>
							<h:selectOneMenu styleClass="selectOneMenu" id="menuDateSelect"
								value="#{pc_Pilottransf.dateSelect}" onchange="submit();">
								<f:selectItems value="#{pc_Pilottransf.lstDateSelect}"/>
							</h:selectOneMenu>
							<h:inputText styleClass="inputText" id="inputDateBegin" value="#{pc_Pilottransf.dateBegin}">
								<f:convertDateTime pattern="dd/MM/yyyy"/>
								<hx:inputHelperDatePicker />
							</h:inputText>
							<h:inputText styleClass="inputText" id="inputDateEnd" value="#{pc_Pilottransf.dateEnd}" style="visibility:#{pc_Pilottransf.renderDateEnd};">
								<f:convertDateTime  pattern="dd/MM/yyyy"/>
								<hx:inputHelperDatePicker />
							</h:inputText>

							<h:outputText styleClass="outputText" id="text9"
								value="Typologie"></h:outputText>
							<h:outputText styleClass="outputText" id="text10" value="="></h:outputText>
							<h:panelGrid styleClass="panelGrid" id="grid1" columns="3" cellpadding="0" border="0">								
									<h:inputText value="#{pc_Pilottransf.typo}" styleClass="inputText" id="TYPOLOGIE_DESC" style="width:180px;" readonly="false"></h:inputText>
									<h:inputHidden id="TYPOLOGIE"></h:inputHidden>									
									<hx:graphicImageEx styleClass="graphicImageEx" id="imageEx4"
										value="../theme/Images/pcl.gif" border="0" onclick="openTypo('TYPOLOGIE',324,700,0,700);"></hx:graphicImageEx>	
							</h:panelGrid>
							<h:outputText styleClass="outputText" id="text21"></h:outputText>
							<h:outputText styleClass="outputText" id="text22"></h:outputText>
							<h:outputText styleClass="outputText" id="text15"></h:outputText>
							<hx:commandExButton type="submit" value="Rechercher"
								styleClass="inactiveButton" id="searchBtn"
								action="#{pc_Pilottransf.doSearchAction}" disabled="#{pc_Pilottransf.disableBtnSearch}"></hx:commandExButton>
							<hx:commandExButton type="submit" value="Effacer les critères"
								styleClass="inactiveButton" id="ResetBtn" action="#{pc_Pilottransf.doResetBtnAction}" disabled="#{pc_Pilottransf.disableBtnReset}"></hx:commandExButton>
							<h:outputText styleClass="outputText" id="text18" style="height:10px;"></h:outputText>
						</h:panelGrid>
	<h:panelGrid styleClass="panelGrid" id="grid45" columns="1"
							width="100%" rowClasses="pilotTransfRow2"
							columnClasses="" style="text-align:center;">
							<h:outputText styleClass="outputText" id="text14"></h:outputText>
							<h:outputText styleClass="outputText" id="text20"></h:outputText>
							<h:messages styleClass="message" id="message1"></h:messages>
						</h:panelGrid>
						<h:panelGrid styleClass="panelGrid" id="grid4" columns="4"
							width="100%" rowClasses="pilotTransfRow1,pilotTransfRow2"
							columnClasses="pilotTransfCol1,pilotTransfCol2,pilotTransfCol3,pilotTransfCol4">
							<h:outputText styleClass="outputText" id="text1"
								value="Quantité site source"></h:outputText>
							<h:outputText styleClass="outputText" id="text6" value="="></h:outputText>
							<h:inputText styleClass="inputText" id="qteSiteSrc" readonly="true"></h:inputText>
							<h:outputText styleClass="outputText" id="text24"></h:outputText>
							<h:outputText styleClass="outputText" id="text12"
								value="Quantité à rediriger"></h:outputText>
							<h:outputText styleClass="outputText" id="text13" value="="></h:outputText>
							<h:inputText styleClass="inputText" id="qteSiteDest" readonly="#{pc_Pilottransf.disableQteSiteDest}"></h:inputText>
							<hx:commandExButton type="submit" value="Confirmer le transfert"
								styleClass="inactiveButton" id="btnTransf" action="#{pc_Pilottransf.doBtnTransf}" disabled="#{pc_Pilottransf.disableBtnTransf}"></hx:commandExButton>
						</h:panelGrid>
						<h:panelGrid styleClass="panelGrid" id="gridProgress" columns="1" width="100%" style="text-align:center;">
							<h:outputText styleClass="outputText" id="text11"></h:outputText>
							<h:outputText styleClass="outputText" id="text8"></h:outputText>
							
						</h:panelGrid>
					</h:panelGrid>
					</td>
				</tr>	
				<tr>
					<td colspan="2">	
						<table width="100%">
							<tr>
								<td align="center">
									<h:outputText styleClass="outputText" id="text19" value="Progression"></h:outputText>
								</td>
							</tr>
							<tr>
								<td align="center">
									<div id="ProgressBar1" name="ProgressBar1" max="100" value="0" style="height: 15px; width: 300px; border: 1px solid #000000; text-align:left;" align="left"></div>					
								    <script type="text/javascript">							
									 	progressBar=function(progressBarNode) {
								        var p=progressBarNode;
								        p.input=document.createElement("input");
								        p.input.type="hidden";
								        p.input.name=p.getAttribute("name");
								        p.layer=document.createElement("div");
								        p.layer.style.backgroundColor="blue";
								        p.layer.style.height="100%";
								        p.layer.style.width="0%";
								        p.appendChild(p.input);
								        p.appendChild(p.layer);
								        p._onchange=function() {
								            var newTaille=((this.Value()*100)/this.Max());
								            this.layer.style.width=newTaille+"%";
								            try {
								                this.OnChange();
								            } catch (ex) {}
								        }
								        p.OnChange=function() {
								            eval(this.getAttribute("onchange"));
								        }
								        p.Max=function(value) {
								            if (value || value==0) { // SET
								                this.setAttribute("max", value);
								                this._onchange();
								            } else { // GET
								                return parseInt(this.getAttribute("max"));
							            	}
							        	}
							        	p.Value=function(value) {
							            	if (value || value==0) { // SET
							                	value=parseInt(value);
							                if (value < 0) {value=0;}
							                if (value > this.Max()) {value=this.Max();}
							                this.setAttribute("value", value);
							                this.input.setAttribute("value", value);
							                this._onchange();
						            		} else { // GET
						               			 return parseInt(this.getAttribute("value"));
						            		}
								        	}
							        		p._onchange();
							      		  	return p;
								    	}
						    			var p=progressBar(document.getElementById("ProgressBar1"));
										var req;
										var urlServ;
										var elemId = '';
										var complete = false;
										var ns = false;
										var val;
										function init ()
									{
										try
										{
											req=new ActiveXObject("Msxml2.XMLHTTP");
										}
										catch(e)
										{
											try
											{
												req=new ActiveXObject("Microsoft.XMLHTTP");
											}
										catch(oc)
										{
											req=null;
										}
									}
										
									if( !req&&typeof XMLHttpRequest != "undefined" )
									{
										req = new XMLHttpRequest();
										req.overrideMimeType('text/plain');
										ns = true;
									}						
								}
					        	function go(progressBarNode, url, id)
								{
									var p=progressBarNode;
									p.Value(0);			
									urlServ = url;									
									elemId = id;
									val = 0;
									sendQuery();								
						    	}	
								function sendQuery ()
								{
									init ();								
									if (req != null)
									{
										req.onreadystatechange = function() {processResponse(req);};
											
										if (document.referrer != document.URL)
										{
											req.open ("GET", urlServ + '&complete=reset&val=' + val, true);
										}
										else
										{
											if (complete)
											{
												req.open ("GET", urlServ + '&complete=true&val=' + val, true);
											}
											else
											{
												req.open ("GET", urlServ + '&complete=false&val=' + val, true);
											}
										}
										val++;						
										req.setRequestHeader("Content-type", "text/plain");
										if (ns)
										{
											req.setRequestHeader("Content-length", 0);
											req.setRequestHeader("Connection", "close");
											req.send('');
										}
										else
										{
											req.send(null);
										}
									}
								}
							function processResponse (req2)
							{
								if (req2 != null)
								{																		
									if ((ns && req2.readyState == 4) || (!ns && req2.readyState == 4 && req2.status == 200))
									{
										try
										{											
											if (req2.responseText != '' && req2.responseText.substr(0,3) != 'end' && req2.responseText != 'error' && req2.responseText != 'conversion_error' && req2.responseText != 'reset')
											{
												p.Value(req2.responseText);												
											}
											if (req2.responseText != '' && req2.responseText != '100' && req2.responseText.substr(0,3) != 'end' && req2.responseText != 'error' && req2.responseText != 'conversion_error' && req2.responseText != 'reset')
											{
												complete = false;
												setTimeout("sendQuery();", 250);
											}
											else if (req.responseText == '100')
											{												
												complete = true;												
												setTimeout("sendQuery();", 250);												
											}
											else if (req.responseText.substr(0,3) == 'end')
											{
												var ind = req.responseText.indexOf ("-", 4);
												var nbdem = req.responseText.substr(4, ind - 4);
												var nbtransf = req.responseText.substr(ind + 1, req.responseText.length);
												alert('Transfert effectué\n\nNombre de documents ayant fait l\'objet de la demande de transfert : ' + nbdem + '\nNombre de documents effectivement transférés : ' + nbtransf);
												document.getElementById('form1:qteSiteDest').value = '';
												form1.submit();
											}
											else if (req.responseText == 'error')
											{
												alert('Erreur : Transfert impossible\n\nVous devez d\'abord faire une recherche');
												document.getElementById('form1:qteSiteDest').value = '';
												form1.submit();
											}
											else if (req.responseText == 'conversion_error')
											{
												alert('Quantité à rediriger : la valeur que vous avez saisi n\'est pas correcte\n\nVeuillez saisir un nombre entier');
												document.getElementById('form1:qteSiteDest').value = '';
												form1.submit();
											}
											else if (req.responseText == 'reset')
											{
												document.getElementById('form1:qteSiteSrc').value = '';
												document.getElementById('form1:qteSiteDest').value = '';
												document.getElementById('form1:btnTransf').disabled = true;
											}
										}
										catch(e)
										{	
											p.Value(0);
										}										
									}
								}
							}
									</script>
								</td>	
							</tr>					
						</table>
					
					</td>
				</tr>			
			</table>
			
		</h:form>
		<%@include file="/WEB-INF/jspf/footer.jsf"%>
	</hx:scriptCollector>
	</BODY>
</f:view>
</HTML>
