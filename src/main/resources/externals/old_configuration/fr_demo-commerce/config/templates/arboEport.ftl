<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<head>

<link title="stylesheet" href="./css/jquery.treeview.css" type="text/css" rel="Stylesheet"/>
<script type="text/javascript" src="./js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="./js/jquery.treeview.js"></script>

<title>Export Dossier</title>
<SCRIPT LANGUAGE="JavaScript">
  var jq = jQuery.noConflict();
  jq(document).ready(function()
  {
    jq("#gray").treeview({
      control: "#treecontrol"
    });    
  });

</SCRIPT>
<#include "*/header.ftl">
</head>
 
<#macro recurse_macro node depth>
  
  <#if node.isFolder() || node.isComputed()>  
  
  <ul>
    <li>
      <DIV>
        <span style="vertical-align:bottom;padding-left:3px"><IMG SRC="./img/folder.gif"></span>
	    ${node.value}

  		<#list node.childrens as child> 
      	  <#if child.isFolder() || child.isComputed() >
            <@recurse_macro node=child depth=depth+1/>
          <#elseif child.isFile()>
          	<li>
              <DIV>
           		<span style="vertical-align:bottom;padding-left:3px"><IMG SRC="./img/leaf.gif"></span>
           		${child.value}	
	            <#list child.getLinks() as file>        
	              <A href="${file}" alt="${file}" style="color:#000000; text-decoration:none;"/><IMG border="0" SRC="./img/attachment.gif"></A>          
	            </#list>  
              </DIV>
             </li>
          	</#if>
           </#list>
          </DIV>
      </li>
    </ul>
  </#if>
</#macro>

<ul id="gray" class="treeview">
  <@recurse_macro node=ROOT depth=0/>
</ul>

<#include "*/footer.ftl">

</HTML>
