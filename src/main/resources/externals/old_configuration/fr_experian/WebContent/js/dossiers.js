DoubleClickTrapperCounter = 0;
var mypopup=null;
var noUnload=0;


function copyRefGedIE(msg)
{
  var textRange = document.getElementById('form1:REFAIRS').createTextRange();
  textRange.execCommand("RemoveFormat");
  textRange.execCommand("Copy");
  alert(msg);
  return false;  
} 

function DoubleClickTrapperAction(button) 
{
  DoubleClickTrapperCounter++;
  var trapTheClick = false;
  if(DoubleClickTrapperCounter > 1 )
  {
   trapTheClick = true;
  }

  var valueToReturn = true;
  if(trapTheClick == true)
  {
	valueToReturn = false;
	button.disabled=true;	
  }
  
  return valueToReturn;
}

function attenteMenu()
{
 var nav = document.getElementsByTagName('div');
 var i = 0;
 var nbDiv = nav.length;
 var find=false;
 while (i < nbDiv)
 {    
     if( nav.item(i).id == 'navigation' )
     {       
      nav.item(i).style.display = 'none';
      find=true;       
     }       
     i++;
 }
 
 if(!find)
 {
  //mode 3fen?tres
  nav = parent.parent.document.getElementsByTagName('div');
  i = 0;
  nbDiv = nav.length; 
  while (i < nbDiv)
  {    
     if( nav.item(i).id == 'navigation' )
     {       
      nav.item(i).style.display = 'none';
      find=true;       
     }       
     i++;
  }
  
  parent.parent.attente_navigation.style.display='block';
 }
 else
 {
  attente_navigation.style.display='block';
 }
  
 
 
 return true;
}

function confirmAndAttente(message)
{
  var ret=confirm(message);
  if(ret)
  {
   attenteMenu();
  }
  
  return ret;
}

function activMenu()
{
 var nav = document.getElementsByTagName('div');
 var i = 0;
 var nbDiv = nav.length;
 var find=false;
 while (i < nbDiv)
 {    
     if( nav.item(i).id == 'navigation' )
     {       
      nav.item(i).style.display = 'block';
      find=true;       
     }       
     i++;
 }

 if(!find)
 {
  //mode 3fen?tres
  nav = parent.parent.document.getElementsByTagName('div');
  i = 0;
  nbDiv = nav.length; 
  while (i < nbDiv)
  {    
     if( nav.item(i).id == 'navigation' )
     {       
      nav.item(i).style.display = 'block';
      find=true;       
     }       
     i++;
  }
  
  parent.parent.attente_navigation.style.display='none';
 }
 else
 {
  attente_navigation.style.display='none';
 }
} 

function changeScreenSize(mode)
{
    if( mode == 0 )
    {
      window.resizeTo(screen.availWidth, screen.availHeight);
      self.moveTo(0, 0);
    }
    else if( mode == 1 )
    {
      window.resizeTo(screen.availWidth / 2, screen.availHeight);
      self.moveTo(screen.availWidth / 2, 0);
    }
    else
    {
      window.resizeTo(screen.availWidth / 2, screen.availHeight);
      self.moveTo(0, 0);
    }
}

function tryToResizeParent()
{

    if( window.opener != null && window.opener != 'undefined' && window.opener.resizeWindow !=undefined)
    {
      window.opener.resizeWindow();
    }   

}
  
function resizeWindow()
{
    window.parent.resizeTo(screen.availWidth , screen.availHeight);
    window.parent.moveTo(0, 0);
}

function changeScreenSizeParent(mode)
{
    if( mode == 0 )
    {
      window.parent.resizeTo(screen.availWidth, screen.availHeight);
      window.parent.moveTo(0, 0);
    }
    else if( mode == 1 )
    {
      window.parent.resizeTo(screen.availWidth / 2, screen.availHeight);
      window.parent.moveTo(screen.availWidth / 2, 0);
    }
    else
    {
      window.parent.resizeTo(screen.availWidth / 2, screen.availHeight);
      window.parent.moveTo(0, 0);
    }
}

function openPJ(docId,pjId)
{
     var url='AffichageAirsPJ.jsp?iddoc='+docId+'&pjId='+pjId;
     mypopup=window.open(url,"visu","resizable=yes,scrollbars=1,menubar=1");
     mypopup.opener = self;
          
}

function openPJ(docId,pjId,label)
{
     var url='AffichageAirsPJ.jsp?iddoc='+docId+'&pjId='+pjId+'&label='+label;;
     mypopup=window.open(url,"visu","resizable=yes,scrollbars=1,menubar=1");
     mypopup.opener = self;
          
}

function modificationPJ(docId,pjId,label)
{
     var url='ModificationAirsPJ.jsp?iddoc='+docId+'&pjId='+pjId+'&label='+label;
     window.open(url,"modification","resizable=yes,scrollbars=1,menubar=1");          
}

function scanPJ()
{
     var url='ScanApplet.jsp';
     window.open(url,"NumWeb","resizable=yes,scrollbars=1,menubar=1");          
}

function openPJSimple(docId,pjId)
{
     noUnload=1;
     openPJ(docId,pjId);          
}

function openPJSimple(docId,pjId,label)
{
     noUnload=1;
     openPJ(docId,pjId,label);          
}

function fermerDocument()
{

  /*if ( mypopup != null && noUnload==0)
  {
      mypopup.close();  	
      mypopup = null;
  }
  
  if(noUnload==1)
  {
   //remise ? zero pour la prochaine fois
   noUnload=0;
  }*/
}
function pager(initValue,lastValue)
{
  var alltr=document.getElementsByTagName('tr');
  var total=0;
  var currentValue=0;
  for ( var i=0; i < alltr.length; i++)
  {
    if(alltr[i].getAttribute("name")=='mylign')
    {     
     currentValue++;
    }    
    
  }
  
  total=currentValue;
  
  if(lastValue>total)
  {
   lastValue=total;
  }
  
  
  if(initValue<total && initValue>=0)
  {
    currentValue=0;
    for ( var i=0; i < alltr.length; i++)
    {
     if(alltr[i].getAttribute("name")=='mylign')
     {
      if(currentValue<initValue || currentValue>lastValue)
      {
       alltr[i].style.display = 'none';
      }
      else if(currentValue>=initValue && currentValue<=lastValue)
      {
       alltr[i].style.display = 'block';
      }
      currentValue++;
    }    
    
   }
  }
  
}

function pagerForward(number)
{
  var alltr=document.getElementsByTagName('tr');
  var lastValue=0;
  var currentValue=0;
  for ( var i=0; i < alltr.length; i++)
  {
    if(alltr[i].getAttribute("name")=='mylign')
    {
      if(alltr[i].style.display == 'block')
      {
       lastValue=currentValue;
      }  
      currentValue++;
    }
    
  }

  var initValue=lastValue+1;
  var last=initValue+number;
  
  pager(initValue,last);
}

function pagerBackward(number)
{
  var alltr=document.getElementsByTagName('tr');
  var initValue=0;
  var currentValue=0;
  for ( var i=0; i < alltr.length; i++)
  {
    if(alltr[i].getAttribute("name")=='mylign')
    {
      if(alltr[i].style.display == 'block')
      {
       initValue=currentValue;
       break;
      }  
      currentValue++;
    }    
  }

  var init=initValue-1-number;
  var last=initValue-1;
  
  pager(init,last);
}

function openHier(codeField,width,height,top,left)
{
  var hiddenCode="form1:"+codeField;  
  var value=document.forms['form1'].elements[hiddenCode].value;
  var tosend='AuthorityList.jsp?id='+codeField;
  if(value.length>0)
  {
   tosend=tosend+'&idItem='+value;
  }  
  window.open(tosend,'authListPopup','width=' + width + ',height=' + height + ',TOP=' + top + ',LEFT=' + left + ',toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,copyhistory=no,resizable=yes');
}

function disableSearch()
{
 document.getElementById('form1:buttonValidateSearch').disabled=true;
 document.getElementById('form1:buttonInitCriteria').disabled=true;
}

function showLayer(whichLayer, bShow)
{
	if( document.getElementById )
	{
		// this is the way the standards work
		var style2 = document.getElementById( whichLayer ).style;
		style2.display = bShow ? "block" : "none";
	}
	else if( document.all )
	{
		// this is the way old msie versions work
		var style2 = document.all[ whichLayer ].style;
		style2.display = bShow ? "block" : "none";
	}
	else if( document.layers )
	{
		// this is the way nn4 works
		var style2 = document.layers[ whichLayer ].style;
		style2.display = bShow ? "block" : "none";
	}
}

function toggleVisible( whichLayer )
{
	if( document.getElementById )
	{
		// this is the way the standards work
		var style2 = document.getElementById( whichLayer ).style;
		style2.display = style2.display=="block" ? "none" : "block";
	}
	else if( document.all )
	{
		// this is the way old msie versions work
		var style2 = document.all[ whichLayer ].style;
		style2.display = style2.display=="block" ? "none" : "block";
	}
	else if( document.layers )
	{
		// this is the way nn4 works
		var style2 = document.layers[ whichLayer ].style;
		style2.display = style2.display=="block" ? "none" : "block";
	}
}