<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<head>

<link title="stylesheet" href="./css/jquery.treeview.css" type="text/css" rel="Stylesheet"/>
<link title="stylesheet" href="./css/dossier.treeview.css" type="text/css" rel="Stylesheet"/>
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
      <li>
      <DIV>
      <span><IMG class="treeviewIMG" SRC="./img/folder.gif"/> ${node.value}</span>
          
      </DIV>
        <ul>
      		<#list node.childrens as child> 
      		 <#if child.isFolder() || child.isComputed() >      		 
            		<@recurse_macro node=child depth=depth+1/>           
           <#elseif child.isFile()>           
           		<li>
           		<DIV>
           		<span><IMG class="treeviewIMG" SRC="./img/leaf.gif"/>
               ${child.value}
               </span>
               <#if child.getLinks()??>
               <#list child.getLinks() as file>        
               <A href="${file}"/><IMG class="treeviewIMG treeviewIMGAttachment" border="0" SRC="./img/attachment.gif"></A>          
               </#list>  
               </#if>
              </DIV>
             </li>             
          	</#if>
           </#list>
           </ul>
      </li>
  <#else>
	  <#list node.childrens as child> 
			 <#if child.isFolder() || child.isComputed() >
			  <@recurse_macro node=child depth=depth+1/>
	    	</#if>
	   </#list> 
  </#if>     
</#macro>
 <ul id="gray" class="treeview" style="">
<@recurse_macro node=ROOT depth=0/>
</ul>
  
<#include "*/footer.ftl">

</HTML>
