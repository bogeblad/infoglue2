var ns = (navigator.appName.indexOf("Netscape") != -1);
var d = document;
var px = document.layers ? "" : "px";
function floatDiv(id, sx, sy)
{
	var el=d.getElementById?d.getElementById(id):d.all?d.all[id]:d.layers[id];
	window[id + "_obj"] = el;
	if(d.layers)el.style=el;
	el.cx = el.sx = sx;el.cy = el.sy = sy;
	el.sP=function(x,y){this.style.left=x+px;this.style.top=y+px;};
	el.flt=function()
	{
		var pX, pY;
		pX = (this.sx >= 0) ? 0 : ns ? innerWidth : 
		document.documentElement && document.documentElement.clientWidth ? 
		document.documentElement.clientWidth : document.body.clientWidth;
		pY = ns ? pageYOffset : document.documentElement && document.documentElement.scrollTop ? 
		document.documentElement.scrollTop : document.body.scrollTop;
		if(this.sy<0) 
		pY += ns ? innerHeight : document.documentElement && document.documentElement.clientHeight ? 
		document.documentElement.clientHeight : document.body.clientHeight;
		this.cx += (pX + this.sx - this.cx)/2;this.cy += (pY + this.sy - this.cy)/2;
		this.sP(this.cx, this.cy);
		setTimeout(this.id + "_obj.flt()", 40);
	}
	return el;
}

/****************************
 * Called when rezising popup
 ****************************/
 
toolbarLockPositionCookieName = "toolbarLockPosition";
pageStructureDivVisibleCookieName = "pageStructureDivVisible";
pageStructureDivWidthCookieName = "pageStructureDivWidth";
pageStructureDivHeightCookieName = "pageStructureDivHeight";
pageStructureDivHeightBodyCookieName = "pageStructureDivHeightBody";
pageComponentsTopPositionCookieName = "pageStructureTopPosition";
pageComponentsLeftPositionCookieName = "pageStructureLeftPosition";

var pageStructureDivWidth = "300px";
var pageStructureDivHeight = "380px";
var pageStructureDivHeightBody = "360px";

/**
 * This method sets a cookie in the browser.
 */
 
function setCookie(name, value)
{
	if(document.cookie != document.cookie)
		index = document.cookie.indexOf(name);
	else
		index = -1;
	
	if (index == -1)
		document.cookie=name+"="+value+"; expires=Monday, 04-Apr-2010 05:00:00 GMT";
}

/**
 * This method gets a cookie
 */
 
function getCookieValue(name)
{ 
	var value = "";
 	if(document.cookie)
	{
		index = document.cookie.indexOf(name);
		//alert("index:" + index);
		if (index != -1)
		{
			namestart = (document.cookie.indexOf("=", index) + 1);
			nameend = document.cookie.indexOf(";", index);
			if (nameend == -1) {nameend = document.cookie.length;}
			value = document.cookie.substring(namestart, nameend);
			//alert("defaultMenuSize" + defaultMenuSize);
		}
	}
	
	return value;
} 

function expandWindow()
{
	width = document.getElementById('pageComponents').style.width;
	//alert("width:" + width);
	if(width.indexOf("300") > -1)
	{
		width = "350";
		height = "500";	
	}
	else if(width.indexOf("350") > -1)
	{
		width = "400";
		height = "550";	
	}
	else if(width.indexOf("400") > -1)
	{
		width = "450";
		height = "600";	
	}
	else
	{
		width = "300";
		height = "450";	
	}
	
	width = width+"px";
	heightBody = height-20+"px";
	height = height+"px";
	
	document.getElementById('pageComponents').style.width=width;
	document.getElementById('pageComponents').style.height=height;
	document.getElementById('pageComponentsBody').style.height=heightBody;
	
	setCookie(pageStructureDivWidthCookieName, width);
	setCookie(pageStructureDivHeightCookieName, height);
	setCookie(pageStructureDivHeightBodyCookieName, heightBody);
	
} 
 

/****************************
 * Hook method to get informed when a drag ends
 ****************************/

toolbarTopPositionCookieName = "toolbarTopPosition";
var defaultToolbarTopPosition = "0px";
 
function dragEnded(object, left, top)
{
	//alert("dragEnded:" + object.id);
	if(object.id == "paletteHandle")
	{
		topPosition = top;
		setCookie(toolbarTopPositionCookieName, topPosition);
	}

	if(object.id == "pageComponentsHandle")
	{
		setCookie(pageComponentsTopPositionCookieName, top);
		setCookie(pageComponentsLeftPositionCookieName, left);
	}
}

function setToolbarInitialPosition()
{	
	//alert("setToolbarInitialPosition ran");
	defaultToolbarTopPosition = getCookieValue(toolbarTopPositionCookieName);
	toolbarLockPosition = getCookieValue(toolbarLockPositionCookieName);
	
	pageComponentsVisibility = getCookieValue(pageStructureDivVisibleCookieName);	
	
	//pageComponentsTopPosition = getCookieValue(pageComponentsTopPositionCookieName);
	//pageComponentsLeftPosition = getCookieValue(pageComponentsLeftPositionCookieName);
	
	pageStructureDivWidth = getCookieValue(pageStructureDivWidthCookieName);
	pageStructureDivHeight = getCookieValue(pageStructureDivHeightCookieName);
	pageStructureDivHeightBody = getCookieValue(pageStructureDivHeightBodyCookieName);

	propertiesDiv = document.getElementById('pageComponents');
	
		
	//alert("window.innerHeight:" + document.height + ":" + window.innerHeight);
	pageComponentsTopPosition = (getScrollY() + ((document.body.clientHeight - propertiesDiv.offsetHeight) / 2));
	pageComponentsLeftPosition = (getScrollX() + ((document.body.clientWidth - propertiesDiv.offsetWidth) / 2));
	
	floatDiv("pageComponents", 200, 50).flt();
	
	//alert("defaultToolbarTopPosition" + defaultToolbarTopPosition)
	//alert("toolbarLockPosition" + toolbarLockPosition)
	if(toolbarLockPosition == "up")
		floatDiv("paletteDiv", 0, 0).flt();
	else if(toolbarLockPosition == "down")
		floatDiv("paletteDiv", 0, -250).flt();
	else
		this.document.getElementById('paletteDiv').style.top=defaultToolbarTopPosition;
		
	//alert("getScrollY()" + getScrollY() + ":" + propertiesDiv.offsetHeight + ":" + (document.body.clientHeight));
	//alert("pageComponentsTopPosition" + pageComponentsTopPosition)
	//alert("pageComponentsLeftPosition" + pageComponentsLeftPosition)
	//document.getElementById('pageComponents').style.top=pageComponentsTopPosition + "px";
	//document.getElementById('pageComponents').style.left=pageComponentsLeftPosition + "px";
	document.getElementById('pageComponents').style.width=pageStructureDivWidth;
	document.getElementById('pageComponents').style.height=pageStructureDivHeight;
	document.getElementById('pageComponentsBody').style.height=pageStructureDivHeightBody;
	
	//alert("pageComponentsVisibility:" + pageComponentsVisibility);
	if(pageComponentsVisibility != "")
	{
		if(pageComponentsVisibility == "visible")
			propertiesDiv.style.display = 'block';

		propertiesDiv.style.visibility = pageComponentsVisibility;
	}
	
}

function moveDivDown(id)
{
	//alert("clientHeight:" + this.parent.document.body.clientHeight);
	position = this.parent.document.body.clientHeight - 120 + "px";
	//position = "500px";

	var div = document.getElementById(id);

	setCookie(toolbarLockPositionCookieName, "down");
	floatDiv("paletteDiv", 0, -250).flt();
	
	//if(div)
	//	div.style.top = position;
	/*
	
	
	if(document.cookie != document.cookie)
		index = document.cookie.indexOf(toolbarTopPositionCookieName);
	else
		index = -1;
	
	if (index == -1)
		document.cookie=toolbarTopPositionCookieName+"="+position+"; expires=Monday, 04-Apr-2010 05:00:00 GMT";	
	*/
}

function moveDivUp(id)
{
	position = "0px";

	var div = document.getElementById(id);
	
	setCookie(toolbarLockPositionCookieName, "up");
	
	floatDiv("paletteDiv", 0, 0).flt();
	//if(div)
	//	div.style.top = position;
	/*
	if(document.cookie != document.cookie)
		index = document.cookie.indexOf(toolbarTopPositionCookieName);
	else
		index = -1;
	
	if (index == -1)
		document.cookie=toolbarTopPositionCookieName+"="+position+"; expires=Monday, 04-Apr-2010 05:00:00 GMT";
	*/
}







var activeMenuId = "";
var menuskin = "skin1"; // skin0, or skin1
var display_url = 0; // Show URLs in status bar?
var editUrl = "";

if (navigator.appName == "Netscape") {
  document.captureEvents(Event.CLICK);
}

//document.body.onclick = hidemenuie5();

// returns the scroll left and top for the browser viewport.
function getScrollX() {
   if (document.body.scrollTop != undefined) {	// IE model
      var ieBox = document.compatMode != "CSS1Compat";
      var cont = ieBox ? document.body : document.documentElement;
      return cont.scrollLeft;
   }
   else {
      return window.pageXOffset;
   }
}

// returns the scroll left and top for the browser viewport.
function getScrollY() {
   if (document.body.scrollTop != undefined) {	// IE model
      var ieBox = document.compatMode != "CSS1Compat";
      var cont = ieBox ? document.body : document.documentElement;
      return cont.scrollTop;
   }
   else {
      return window.pageYOffset;
   }
}

function getEventPositionX(e) 
{
	if (navigator.appName == "Microsoft Internet Explorer")
	{
		//scrollX = getScrollX();
		//alert("ScollX:" + scrollX);
    	mX = event.clientX + getScrollX();
  	}
  	else 
  	{
    	mX = e.pageX;
  	}
  	
  	return mX;
}

function getEventPositionY(e) 
{
	if (navigator.appName == "Microsoft Internet Explorer")
	{
		//scrollY = getScrollY();
		//alert("ScollY:" + scrollY);
	
    	mY = event.clientY + getScrollY();
  	}
  	else 
  	{
    	mY = e.pageY;
  	}
  	
  	return mY;
}

function getActiveMenuDiv() 
{
	//alert("activeMenuId:" + activeMenuId);
	return document.getElementById(activeMenuId);
}

var busy = false;
var componentId;
var slotId;
var editUrl   = "";
var insertUrl = "";
var deleteUrl = "";

function setEditUrl(anEditUrl) 
{
	//alert("Setting editUrl:" + anEditUrl);
	editUrl = anEditUrl;
}

function showComponentMenu(event, element, compId, anInsertUrl, anDeleteUrl) 
{
	activeMenuId = "component" + compId + "Menu";

	componentId = compId;
	insertUrl = anInsertUrl;
	deleteUrl = anDeleteUrl;
	//alert("componentId" + componentId);
	//alert("editUrl" + editUrl);
    
    document.body.onclick = hidemenuie5;
	getActiveMenuDiv().className = menuskin;
	
	clientX = getEventPositionX(event);
	clientY = getEventPositionY(event);
	
	var rightedge = document.body.clientWidth - clientX;
	var bottomedge = document.body.clientHeight - clientY;

	menuDiv = getActiveMenuDiv();
	
	if (rightedge < menuDiv.offsetWidth)
		newLeft = (document.body.scrollLeft + clientX - menuDiv.offsetWidth);
	else
		newLeft = (document.body.scrollLeft + clientX);
	
	//if (bottomedge < menuDiv.offsetHeight)
	//	newTop = (document.body.scrollTop + clientY - menuDiv.offsetHeight);
	//else
		newTop = (document.body.scrollTop + clientY);

	menuDiv.style.left 	= newLeft + "px";
	menuDiv.style.top 	= newTop + "px";
	
	menuDiv.style.visibility = "visible";
	
	return false;
}


function showComponentInTreeMenu(event, element, compId, anInsertUrl, anDeleteUrl) 
{
	activeMenuId = "componentInTreeMenu";

	componentId = compId;
	insertUrl = anInsertUrl;
	deleteUrl = anDeleteUrl;
	//alert("componentId" + componentId);
    
    document.body.onclick = hidemenuie5;
	getActiveMenuDiv().className = menuskin;
	
	clientX = getEventPositionX(event);
	clientY = getEventPositionY(event);
	
	var rightedge = document.body.clientWidth - clientX;
	var bottomedge = document.body.clientHeight - clientY;

	menuDiv = getActiveMenuDiv();
	
	if (rightedge < menuDiv.offsetWidth)
		newLeft = (document.body.scrollLeft + clientX - menuDiv.offsetWidth);
	else
		newLeft = (document.body.scrollLeft + clientX);
	
	//if (bottomedge < menuDiv.offsetHeight)
	//	newTop = (document.body.scrollTop + clientY - menuDiv.offsetHeight);
	//else
		newTop = (document.body.scrollTop + clientY);

	menuDiv.style.left 	= newLeft + "px";
	menuDiv.style.top 	= newTop + "px";
	
	menuDiv.style.visibility = "visible";
	
	return false;
}

function showEmptySlotMenu(event, compId, anInsertUrl) 
{
	activeMenuId = "emptySlotMenu";
	
	slotId = compId;
	insertUrl = anInsertUrl;
	//alert("CompId:" + compId);
    //alert(insertUrl);
	
    document.body.onclick = hidemenuie5;
	getActiveMenuDiv().className = menuskin;
	
	clientX = getEventPositionX(event);
	clientY = getEventPositionY(event);
	
	var rightedge = document.body.clientWidth - clientX;
	var bottomedge = document.body.clientHeight - clientY;
	
	menuDiv = getActiveMenuDiv();

	if (rightedge < menuDiv.offsetWidth)
		newLeft = (document.body.scrollLeft + clientX - menuDiv.offsetWidth);
	else
		newLeft = (document.body.scrollLeft + clientX);
	
	//if (bottomedge < menuDiv.offsetHeight)
	//	newTop = (document.body.scrollTop + clientY - menuDiv.offsetHeight);
	//else
		newTop = (document.body.scrollTop + clientY);

	menuDiv.style.left 	= newLeft + "px";
	menuDiv.style.top 	= newTop + "px";

	menuDiv.style.visibility = "visible";

	return false;
}


function release()
{
        //alert("Releasing...");
        busy = false;
}

function hidemenuie5() 
{
	//alert("Hiding menu");
	layer = getActiveMenuDiv();
	if(layer)
		layer.style.visibility = "hidden";
	
	//Settings actions to null again
	editUrl = "";
}

function highlightie5(event) 
{
	var layer = event.srcElement || event.currentTarget || event.target;
	//alert("layer:" + layer.className);
	
	if (layer.className == "menuitems") 
	{
		layer.style.backgroundColor = "highlight";
		layer.style.color = "white";
		
		if (display_url)
			window.status = layer.url;
   	}
}

function lowlightie5(event) 
{
	var layer=event.srcElement || event.currentTarget || event.target;
	
	if (layer.className == "menuitems") 
	{
		layer.style.backgroundColor = "";
		layer.style.color = "black";
		window.status = "";
	}
}


// -------------------------------------
// This part takes care of browser-right-click (disables it).
// -------------------------------------


isIE=document.all;
isNN=!document.all&&document.getElementById;
isN4=document.layers;

if (isIE||isNN)
{
	document.oncontextmenu=checkV;
}
else
{
	document.captureEvents(Event.MOUSEDOWN || Event.MOUSEUP);
	document.onmousedown=checkV;
}


function checkV(e)
{
	if (isN4)
	{
		if (e.which==2||e.which==3)
			return false;
	}
	else
		return false;
}


function showDiv(id)
{
	//alert("id:" + id)
	document.getElementById(id).style.visibility = 'visible';
	if(id == "pageComponents")
	{
		document.getElementById(id).style.display = 'block';
		setCookie(pageStructureDivVisibleCookieName, "visible");
	}
}

function hideDiv(id)
{
	document.getElementById(id).style.visibility = 'hidden';
	if(id == "pageComponents")
	{
		document.getElementById(id).style.display = 'none';
		setCookie(pageStructureDivVisibleCookieName, "hidden");
	}
}

function toggleDiv(id)
{
	var div = document.getElementById(id);
	if(div && div.style.visibility == 'visible')
		div.style.visibility = 'hidden';
	else
		div.style.visibility = 'visible';
		
	if(id == "pageComponents")
	{
		if(div.style.visibility == 'hidden')
			document.getElementById(id).style.display = 'none';
		else
			document.getElementById(id).style.display = 'block';
			
		setCookie(pageStructureDivVisibleCookieName, div.style.visibility);
	}
		
}

/**
 * This method submit a form.
 */

function submitForm(id)
{
	document.getElementById(id).submit();
}

var lastRow = null;
var lastRowOriginalBgColor = null;
var selectedRow = null;
var selectedBgColor = "#FFB62A";

function listRowOn(rowEl)
{
	if (lastRow != null)
	{
		lastRow.style.backgroundColor = lastRowOriginalBgColor;
	}

	lastRowOriginalBgColor = rowEl.style.backgroundColor;
	rowEl.style.backgroundColor = selectedBgColor;
	lastRow = rowEl;
}

function listRowOff()
{
	if (lastRow != null)
	{
		if (lastRow != selectedRow)
		{
			lastRow.style.backgroundColor = lastRowOriginalBgColor;
		}
	}
}



function assignComponent(siteNodeId, languageId, contentId, parentComponentId, slotId, specifyBaseTemplate) 
{
	//alert("draggedComponentId:" + draggedComponentId);
	if(draggedComponentId > 0)
	{
		//alert("siteNodeId" + siteNodeId);
		//alert("languageId" + languageId);
		//alert("contentId" + contentId);
		//alert("parentComponentId" + parentComponentId);
		//alert("slotId" + slotId);
		//alert("specifyBaseTemplate" + specifyBaseTemplate);
		
		insertUrl = componentEditorUrl + "ViewSiteNodePageComponents!addComponent.action?siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&parentComponentId=" + parentComponentId + "&componentId=" + draggedComponentId + "&slotId=" + slotId + "&specifyBaseTemplate=" + specifyBaseTemplate + "";
		//alert("insertUrl:" + insertUrl);
		document.location.href = insertUrl;
		draggedComponentId = -1;
	}
}


function saveComponentStructure(url) 
{
	//alert("insertUrl in insertComponent:" + insertUrl.substring(0, 50) + '\n' + insertUrl.substring(50));
	details = "width=500,height=600,left=" + (document.body.clientWidth / 4) + ",top=" + (document.body.clientHeight / 4) + ",toolbar=no,status=no,scrollbars=yes,location=no,menubar=no,directories=no,resizable=no";
	window.open(url, "Save", details);
}
	
//--------------------------------------------
// Here comes the menu items actions
//--------------------------------------------

function edit() 
{
	if(!editUrl || editUrl == "")
	{
		alert("You must right click on a text to be able to use this feature.");
	}
	else
	{
		//alert("editUrl in edit:" + editUrl.substring(0, 50) + '\n' + editUrl.substring(50));
		details = "width=500,height=600,left=" + (document.body.clientWidth / 4) + ",top=" + (document.body.clientHeight / 4) + ",toolbar=no,status=no,scrollbars=yes,location=no,menubar=no,directories=no,resizable=no";
		window.open(editUrl, "Edit", details);
	}
}

function executeTask(url) 
{
	//alert("insertUrl in insertComponent:" + insertUrl.substring(0, 50) + '\n' + insertUrl.substring(50));
	details = "width=500,height=600,left=" + (document.body.clientWidth / 4) + ",top=" + (document.body.clientHeight / 4) + ",toolbar=no,status=no,scrollbars=yes,location=no,menubar=no,directories=no,resizable=no";
	window.open(url, "Edit", details);
}

function insertComponent() 
{
	//alert("insertUrl in insertComponent:" + insertUrl.substring(0, 50) + '\n' + insertUrl.substring(50));
	details = "width=500,height=600,left=" + (document.body.clientWidth / 4) + ",top=" + (document.body.clientHeight / 4) + ",toolbar=no,status=no,scrollbars=yes,location=no,menubar=no,directories=no,resizable=no";
	window.open(insertUrl, "Edit", details);
}

function deleteComponent() 
{
	//alert("deleteUrl in deleteComponent:" +  + deleteUrl.substring(0, 50) + '\n' + deleteUrl.substring(50));
	details = "width=500,height=700,left=" + (document.body.clientWidth / 4) + ",top=" + (document.body.clientHeight / 4) + ",toolbar=no,status=no,scrollbars=yes,location=no,menubar=no,directories=no,resizable=yes";
	document.location.href = deleteUrl;
}

function showComponent() 
{
	showComponentProperties("component" + componentId + "Properties");
}

function showComponentProperties(id) 
{
	showDiv(id);

	propertiesDiv = document.getElementById(id);

	newLeft = (getScrollX() + (document.body.clientWidth / 2) - (propertiesDiv.offsetWidth / 2));
	newTop = (getScrollY() + (document.body.clientHeight / 2) - (propertiesDiv.offsetHeight / 2));
	
	propertiesDiv.style.top = newTop + "px";	
	propertiesDiv.style.left = newLeft + "px";	
}

function invokeAction() 
{
	//alert("editUrl in invokeAction:" + editUrl);
	details = "width=500,height=700,left=" + (document.body.clientWidth / 4) + ",top=" + (document.body.clientHeight / 4) + ",toolbar=no,status=no,scrollbars=yes,location=no,menubar=no,directories=no,resizable=yes";
	//window.open(editUrl, "Edit", details);
}

function viewSource() 
{
	window.location = "view-source:" + window.location.href;
}

      var hit;
	  var draggedComponentId = -1;
	  
      // -- Determine browser
      var IE  = (document.all)? true: false;
      var Mac = (navigator.cpuClass && navigator.cpuClass.match(/PPC/))? true: false;

      //-----------------------------------------------------------------
      // browser-independent routines for determining event position
      //-----------------------------------------------------------------
      function getX(e) {
      	var x = 0;
        if (IE) {
          x = e.clientX;
          if (!Mac) {
            x += document.documentElement.scrollLeft;
            x += document.body.scrollLeft;
		  }
        } else {
          x = e.pageX + window.scrollX;
        }
        return x
      }

      function getY(e) {
        var y = 0;
        if (IE) {
          y = e.clientY;
          if (!Mac) {
            y += document.documentElement.scrollTop;
            y += document.body.scrollTop;
		  }
        } else {
          y = e.pageY + window.scrollY;
        }
        return y;
      }

      //-----------------------------------------------------------------
      // onmousedown handler.  Start drag op
      //-----------------------------------------------------------------
      function grabIt(e) {

        // -- event and target element references are browser-specific.  UGH
        var e      = e? e: window.event;
        var field  = IE? e.srcElement: e.target;   
        var target = getRect(field);	// target element position offsets
		
		//alert("field:" + field)
        //alert("field:" + field.id)
        draggedComponentId = field.id;
        //alert("draggedComponentId" + draggedComponentId)
        
        // -- initialize drag object and store difference between its edges and the mouse
        drag  = document.getElementById("buffer");
        var x = getX(e);
        var y = getY(e);
        //alert("x:" + x);
        //alert("y:" + y);
        
        //alert("target:" + target.left + ":" + target.top);
        
        //alert("drag.dx:" + drag.dx);
        //alert("drag.dy:" + drag.dy);
        
        newXPos = x; // - target.left - 16;
        newYPos = y; // - target.top;
        //alert("newXPos" + newXPos);
        //alert("newYPos" + newYPos);
        //drag.dx =  x - target.left - 16;
        //drag.dy = y - target.top;
        drag.style.left = newXPos + "px";
        drag.style.top = newYPos + "px";
        
        //alert("drag.dx:" + drag.dx);
        //alert("drag.dy:" + drag.dy);
        //alert("drag.style.left:" + drag.style.left);
        //alert("drag.style.top:" + drag.style.top);
        
		//alert("field:" + field)
		
        // -- deactivate cloaking device
        with (drag) 
        {
          //style.top        = target.top;
	      //style.left       = target.left;
	      //style.width      = target.right - target.left;
          style.visibility = "visible";
          //innerHTML        = field.innerHTML;
        }

        // -- Capture mousemove and mouseup events on the page.
        document.onmousemove = dragIt;
        //document.onmouseup   = dropIt;

	// -- block all other events
        if (IE) {
          e.cancelBubble = true;
          e.returnValue  = false;
        } else {
          e.preventDefault();
        }
        return false;
      }

    //-----------------------------------------------------------------
    // onmousemove handler
    //-----------------------------------------------------------------
    function dragIt(e) 
    {
		var e = e? e: window.event;
	    
        // -- Move drag element by the same amount the cursor has moved.
        with (drag) 
        {
        	style.left = getX(e) + 1 + "px"; //- dx;
          	style.top  = getY(e) + 1 + "px"; //- dy;
        	//style.left = getX(e) - dx;
          	//style.top  = getY(e) - dy;
        }

        return false;
    }

      //-----------------------------------------------------------------
      // onmouseup handler
      //-----------------------------------------------------------------
      function dropIt(e) {

        // -- engage cloaking device
        drag.style.visibility = "hidden";

        // -- get bounding rectangle for drag buffer
        var rd = getRect(drag);

        // -- loop over all form elements
		var divs = document.getElementsByTagName("span"); 
        for(i = 0; i < divs.length; i++) 
        {

          // -- if we aren't a drag target, go to the next element
          var field = divs[i];
          if (! field.className.match(/dragTarget/)) continue;

  		  // -- is drag buffer over this target?
          var rt = getRect(field);
          var boundHorz = (rd.left > rt.left) && (rd.right  < rt.right);
          var boundVert = (rd.top  > rt.top)  && (rd.bottom < rt.bottom);
          //alert(field.className);
          //alert("buffer.left:" + rd.left);
          //alert("span.left:" + rt.left);
          //alert("buffer.right:" + rd.right);
          //alert("span.right:" + rt.right);
          
          //alert(boundHorz);
          //alert(boundVert);
          
          if (boundHorz && boundVert) {
            //alert("Drag is over:" + field.id);
			field.value += drag.innerHTML + ', ';
            break;
          }
        }
        
        // -- IE5/Mac requires this so the drag element doesn't take up full screen width
        drag.innerHTML = '';

        // -- Stop capturing mousemove & mouseup events.
        document.onmousemove = null;
        document.onmouseup   = null;
      }


      function getRect(obj) {
        var rect = new Object();
        rect.top = rect.left = 0;
        var parentObj = obj;
        while (parentObj != null) {
          rect.top  += parentObj.offsetTop;
          rect.left += parentObj.offsetLeft;
          parentObj = parentObj.offsetParent;
        }
        rect.bottom = rect.top  + obj.offsetHeight;
        rect.right  = rect.left + obj.offsetWidth;

		//if()
        return rect;
      }



      //-----------------------------------------------------------------
      // onmouseup handler
      //-----------------------------------------------------------------
      function dropItem(e) {

        // -- engage cloaking device
        drag.style.visibility = "hidden";
		drag.style.left = -50;
        drag.style.top  = -50;
          
        // -- IE5/Mac requires this so the drag element doesn't take up full screen width
        //drag.innerHTML = '';

        // -- Stop capturing mousemove & mouseup events.
        document.onmousemove = null;
        document.onmouseup   = null;
      }
      
      	function xGetElementById(e) 
	{
		if(typeof(e)!='string') return e;
		if(document.getElementById) e=document.getElementById(e);
	  	else if(document.all) e=document.all[e];
	  	else if(document.layers) e=xLayer(e);
	  	else e=null;
	  	return e;
	}
	
	function xName(e) 
	{
	  	if (!e) return e;
	  	else if (e.id && e.id != "") return e.id;
	  	else if (e.nodeName && e.nodeName != "") return e.nodeName;
	  	else if (e.tagName && e.tagName != "") return e.tagName;
	  	else return e;
	}
	
	// Event:
	function xAddEventListener(e, eventType, eventListener, useCapture) 
	{
	  	if(!(e=xGetElementById(e))) return;
	  	eventType=eventType.toLowerCase();
	  	if((!xIE4Up && !xOp7) && e==window) 
	  	{
	    	if(eventType=='resize') { window.xPCW=xClientWidth(); window.xPCH=xClientHeight(); window.xREL=eventListener; xResizeEvent(); return; }
			if(eventType=='scroll') { window.xPSL=xScrollLeft(); window.xPST=xScrollTop(); window.xSEL=eventListener; xScrollEvent(); return; }
	  	}
	  	
	  	var eh='e.on'+eventType+'=eventListener';
	  	if(e.addEventListener) e.addEventListener(eventType,eventListener,useCapture);
	  	else if(e.attachEvent) e.attachEvent('on'+eventType,eventListener);
	  	else if(e.captureEvents) {
	    	if(useCapture||(eventType.indexOf('mousemove')!=-1)) { e.captureEvents(eval('Event.'+eventType.toUpperCase())); }
	    	eval(eh);
	  	}
	  	else eval(eh);
	}
		
		
	function initializeSlotEventHandler(id, insertUrl, deleteUrl)
	{
		//alert("initializeSlotEventHandler:" + id);
		var object = new emptySlotEventHandler(id, id, insertUrl, deleteUrl);
	}

	function emptySlotEventHandler(eleId, objName, insertUrl, deleteUrl)
	{
		this.objName = objName;           // objName is a property of myObject4
		this.insertUrl = insertUrl;
		this.deleteUrl = deleteUrl;
		//alert("eleId:" + eleId);
		//alert("this.insertUrl:" + this.insertUrl);
		var ele = xGetElementById(eleId); // ele points to our related Element
		//alert("ele:" + ele);
		ele.thisObj = this;              // Add a property to ele which points
		                                    // to our myObject4 'this'.
		ele.onclick = function(e)         // onclick is a method of ele not myObject4
		{   
		  	//alert("onclick");                              // so 'this' will point to event.currentTarget.
		    this.thisObj.onClick(e, this);
		}
		  
		ele.oncontextmenu = function(e)         	// onclick is a method of ele not myObject4
		{ 
		  	//alert("oncontextmenu");           		// so 'this' will point to event.currentTarget.
		    this.thisObj.onContextMenu(e, this);
		    return false;
		}
		
		this.onClick = function(evt, ele) // onClick is a method of myObject4
		{
			//alert('emptySlotEventHandler.onClick()\nthis.objName = ' + this.objName + '\nele = ' + xName(ele));
		    //assignComponent();
		    //assignComponent(siteNodeId, languageId, contentId, componentId, slotId, specifyBaseTemplate);
		    hidemenuie5();
		    // cancel event bubbling
		    if (evt && evt.stopPropagation) {evt.stopPropagation();}
		    else if (window.event) {window.event.cancelBubble = true;}
		}
		  
		this.onContextMenu = function(evt, ele) // onContextMenu is a method of myObject4
		{
			//alert('emptySlotEventHandler.oncontextmenu()\nthis.objName = ' + this.objName + '\nele = ' + xName(ele));
		    showEmptySlotMenu(evt, ele.id, insertUrl);
		    // cancel event bubbling
		    if (evt && evt.stopPropagation) {evt.stopPropagation();}
		    else if (window.event) {window.event.cancelBubble = true;}
		}
	}
	
	function initializeComponentEventHandler(id, compId, insertUrl, deleteUrl)
	{
		//alert("initializeComponentEventHandler" + id + " " + deleteUrl);
		var object = new componentEventHandler(id, id, compId, insertUrl, deleteUrl);
	}
		
	function componentEventHandler(eleId, objName, objId, insertUrl, deleteUrl)
	{
		this.objName = objName;           // objName is a property of myObject4
		this.objId = objId;
		this.insertUrl = insertUrl;
		this.deleteUrl = deleteUrl;
		//alert("eleId:" + eleId);
		//alert("this.insertUrl:" + this.insertUrl);
		//alert("this.deleteUrl:" + this.deleteUrl);
		var ele = xGetElementById(eleId); // ele points to our related Element
		//alert("ele:" + ele);
		ele.thisObj = this;              // Add a property to ele which points
		                                    // to our myObject4 'this'.
		ele.onclick = function(e)         // onclick is a method of ele not myObject4
		{   
		  	//alert("onclick");                              // so 'this' will point to event.currentTarget.
		    this.thisObj.onClick(e, this);
		}
		  
		ele.oncontextmenu = function(e)         	// onclick is a method of ele not myObject4
		{ 
		  	//alert("oncontextmenu:" + e);           		// so 'this' will point to event.currentTarget.
		    this.thisObj.onContextMenu(e, this);
		    return false;
		}
		
		this.onClick = function(evt, ele) // onClick is a method of myObject4
		{
			//alert('componentEventHandler.onClick()\nthis.objName = ' + this.objName + '\nele = ' + xName(ele));
		    hidemenuie5();
		    // cancel event bubbling
		    if (evt && evt.stopPropagation) {evt.stopPropagation();}
		    else if (window.event) {window.event.cancelBubble = true;}
		}
		  
		this.onContextMenu = function(evt, ele) // onContextMenu is a method of myObject4
		{
			//alert('componentEventHandler.oncontextmenu()\nthis.objName = ' + this.objName + '\nele = ' + xName(ele));
		    showComponentMenu(evt, ele.id, this.objId, insertUrl, deleteUrl);
		    // cancel event bubbling
		    if (evt && evt.stopPropagation) {evt.stopPropagation();}
		    else if (window.event) {window.event.cancelBubble = true;}
		}
	}
	
	function initializeComponentInTreeEventHandler(id, compId, insertUrl, deleteUrl)
	{
		//alert("initializeComponentInTreeEventHandler" + id + " " + deleteUrl);
		var object = new componentInTreeEventHandler(id, id, compId, insertUrl, deleteUrl);
	}
		
	function componentInTreeEventHandler(eleId, objName, objId, insertUrl, deleteUrl)
	{
		this.objName = objName;           // objName is a property of myObject4
		this.objId = objId;
		this.insertUrl = insertUrl;
		this.deleteUrl = deleteUrl;
		//alert("eleId:" + eleId);
		//alert("this.insertUrl:" + this.insertUrl);
		//alert("this.deleteUrl:" + this.deleteUrl);
		var ele = xGetElementById(eleId); // ele points to our related Element
		//alert("ele:" + ele);
		ele.thisObj = this;              // Add a property to ele which points
		                                    // to our myObject4 'this'.
		ele.onclick = function(e)         // onclick is a method of ele not myObject4
		{   
		  	//alert("onclick");                              // so 'this' will point to event.currentTarget.
		    this.thisObj.onClick(e, this);
		}
		  
		ele.oncontextmenu = function(e)         	// onclick is a method of ele not myObject4
		{ 
		  	//alert("oncontextmenu");           		// so 'this' will point to event.currentTarget.
		    this.thisObj.onContextMenu(e, this);
		    return false;
		}
		
		this.onClick = function(evt, ele) // onClick is a method of myObject4
		{
			//alert('componentEventHandler.onClick()\nthis.objName = ' + this.objName + '\nele = ' + xName(ele));
		    // cancel event bubbling
		    if (evt && evt.stopPropagation) {evt.stopPropagation();}
		    else if (window.event) {window.event.cancelBubble = true;}
		}
		  
		this.onContextMenu = function(evt, ele) // onContextMenu is a method of myObject4
		{
			//alert('componentEventHandler.oncontextmenu()\nthis.objName = ' + this.objName + '\nele = ' + xName(ele));
		    showComponentInTreeMenu(evt, ele.id, this.objId, insertUrl, deleteUrl);
		    // cancel event bubbling
		    if (evt && evt.stopPropagation) {evt.stopPropagation();}
		    else if (window.event) {window.event.cancelBubble = true;}
		}
	}


	//var currentGroup = "Navigation";
	
	function changeTab(group)
	{
		//alert("group" + group);
		//alert("currentGroup" + currentGroup);
		
		document.getElementById(currentGroup + "Tab").style.zIndex = 2;
		document.getElementById(currentGroup + "Tab").className = "tab";
		document.getElementById(currentGroup + "ComponentsBg").style.zIndex = 2;
		
		document.getElementById(group + "Tab").style.zIndex = 3;
		document.getElementById(group + "Tab").className = "thistab";
		document.getElementById(group + "ComponentsBg").style.zIndex = 3;

		currentGroup = group;
		currentComponentsDiv = currentGroup + "Components";
	}
	
	//The code below is to take care of scroll in tabs
	
	function lib_bwcheck(){ //Browsercheck (needed)
		this.ver=navigator.appVersion
		this.agent=navigator.userAgent
		this.dom=document.getElementById?1:0
		this.opera5=this.agent.indexOf("Opera 5")>-1
		this.ie5=(this.ver.indexOf("MSIE 5")>-1 && this.dom && !this.opera5)?1:0; 
		this.ie6=(this.ver.indexOf("MSIE 6")>-1 && this.dom && !this.opera5)?1:0;
		this.ie4=(document.all && !this.dom && !this.opera5)?1:0;
		this.ie=this.ie4||this.ie5||this.ie6
		this.mac=this.agent.indexOf("Mac")>-1
		this.ns6=(this.dom && parseInt(this.ver) >= 5) ?1:0; 
		this.ns4=(document.layers && !this.dom)?1:0;
		this.bw=(this.ie6 || this.ie5 || this.ie4 || this.ns4 || this.ns6 || this.opera5)
		return this
	}
	var bw=new lib_bwcheck()
	
	
	/**************************************************************************
	Variables to set.
	***************************************************************************/
	sMenuheight = 20  //The height of the menu
	sArrowwidth = 5  //Width of the arrows
	sScrollspeed = 20 //Scroll speed: (in milliseconds, change this one and the next variable to change the speed)
	sScrollPx = 8     //Pixels to scroll per timeout.
	sScrollExtra = 15 //Extra speed to scroll onmousedown (pixels)
	
	var scrollHash = new Array();
	
	/**************************************************************************
	Scrolling functions
	***************************************************************************/
	var tim = 0
	var noScroll = true
	function mLeft(){
		id = currentComponentsDiv;
		div = document.getElementById(id);
		
		oBg = scrollHash[id + "oBg"]
		oMenu = scrollHash[id + "oMenu"]
	
		pageWidth = (bw.ns4 || bw.ns6 || window.opera)?innerWidth:document.body.clientWidth;
	
		if (!noScroll && oMenu.x<0){
			oMenu.moveBy(sScrollPx,0)
			tim = setTimeout("mLeft()",sScrollspeed)
		}
	}
	function mRight(){
		id = currentComponentsDiv;
		div = document.getElementById(id);
		
		oBg = scrollHash[id + "oBg"]
		oMenu = scrollHash[id + "oMenu"]
		
		pageWidth = (bw.ns4 || bw.ns6 || window.opera)?innerWidth:document.body.clientWidth;
				
		if (!noScroll && oMenu.x>-(oMenu.scrollWidth-(pageWidth))-sArrowwidth){
			oMenu.moveBy(-sScrollPx,0)
			tim = setTimeout("mRight()",sScrollspeed)
		}
	}
	function noMove(){	
		clearTimeout(tim);
		noScroll = true;
		sScrollPx = sScrollPxOriginal;
	}
	/**************************************************************************
	Object part
	***************************************************************************/
	function makeObj(obj,nest,menu){
	    nest = (!nest) ? "":'document.'+nest+'.';
		this.elm = bw.ns4?eval(nest+"document.layers." +obj):bw.ie4?document.all[obj]:document.getElementById(obj);
	   	this.css = bw.ns4?this.elm:this.elm.style;
		this.scrollWidth = bw.ns4?this.css.document.width:this.elm.offsetWidth;
		this.x = bw.ns4?this.css.left:this.elm.offsetLeft;
		this.y = bw.ns4?this.css.top:this.elm.offsetTop;
		this.moveBy = b_moveBy;
		this.moveIt = b_moveIt;
		this.clipTo = b_clipTo;
		return this;
	}
	var px = bw.ns4||window.opera?"":"px";
	function b_moveIt(x,y){
		if (x!=null){this.x=x; this.css.left=this.x+px;}
		if (y!=null){this.y=y; this.css.top=this.y+px;}
	}
	function b_moveBy(x,y){this.x=this.x+x; this.y=this.y+y; this.css.left=this.x+px; this.css.top=this.y+px;}
	function b_clipTo(t,r,b,l){
		if(bw.ns4){this.css.clip.top=t; this.css.clip.right=r; this.css.clip.bottom=b; this.css.clip.left=l;}
		else this.css.clip="rect("+t+"px "+r+"px "+b+"px "+l+"px)";
	}
	/**************************************************************************
	Object part end
	***************************************************************************/
	
	/**************************************************************************
	Init function. Set the placements of the objects here.
	***************************************************************************/
	var sScrollPxOriginal = sScrollPx;
	function tabInit(id)
	{
		div = document.getElementById(id);
		
		//Width of the menu, Currently set to the width of the document.
		//If you want the menu to be 500px wide for instance, just 
		//set the pageWidth=500 in stead.
		pageWidth = (bw.ns4 || bw.ns6 || window.opera)?innerWidth:document.body.clientWidth;
		
		//Making the objects...
		oBg = new makeObj(id + 'Bg')
		oMenu = new makeObj(id,id + 'Bg',1)
		
		//Storing them for later
		scrollHash[id + "oBg"] = oBg;
		scrollHash[id + "oMenu"] = oMenu;
		
		//Placing the menucontainer, the layer with links, and the right arrow.
		oBg.moveIt(0,40) //Main div, holds all the other divs.
		oMenu.moveIt(0,null)
		
		//Setting the width and the visible area of the links.
		if (!bw.ns4) oBg.css.overflow = "hidden";
		if (bw.ns6) oMenu.css.position = "relative";
		//oBg.css.width = pageWidth+px;
		//oBg.clipTo(0,pageWidth,sMenuheight,0)
		//oBg.css.visibility = "visible";
		
	}
	
	/**
	 * This method sets the status text in the list - showing off the full name of the component.
	 */
	 
	function showDetails(name)
	{
		statusDiv = document.getElementById("statusText");
		statusDivText=statusDiv.childNodes.item(0);
		statusDivText.data = name;
	} 
	
	
	//*******************************************
	// This function changes language version
	//*******************************************
	
	function changeLanguage(siteNodeId, languageId, contentId)
	{
		window.location.href = "ViewPage!renderDecoratedPage.action?siteNodeId=" + siteNodeId + "&languageId=" + languageId.value + "&contentId=" + contentId + "";
	}
	
	//*******************************************
	// This function changes language version
	//*******************************************
	
	function refreshComponents(currentLocation)
	{
		var newLocation = currentLocation + "&refresh=true";
		//alert("newLocation" + newLocation);
		document.location.href = newLocation;
	}

	var groupHash = new Array();
	//var componentIndex = 0;
	
	function moveRight(groupName)
	{
		componentIndex = groupHash[groupName + "CurrentIndex"];
		//alert("componentIndex" + componentIndex);
		if(!componentIndex)
		{
			groupHash[groupName + "CurrentIndex"] = 0;
			componentIndex = 0;
		}
			
		//alert("groupName: " + groupName);
		var div = document.getElementById(groupName + "Components");
		//alert("div: " + div.id);
		var componentTDs = div.getElementsByTagName("div");
		//alert("componentTDs:" + componentTDs.length);
		//alert("componentIndex" + componentTDs.length);

		if(componentIndex < componentTDs.length - 1)
		{
			//alert("componentIndex:" + componentIndex);
			componentIndex = componentIndex + 1;
		
			for (var i = 0; i < componentTDs.length - 1; i++) 
			{ 
				//alert("Width of this a element is : " + componentTDs[i].style.width + ":" + componentTDs[i].id + "\n"); 
				if(i < componentIndex)
				{
					//componentTDs[i].style.width = "0px";
					componentTDs[i].style.display = "none";
				}
			} 
		}
		
		groupHash[groupName + "CurrentIndex"] = componentIndex;
	}
	
	function moveLeft(groupName)
	{
		componentIndex = groupHash[groupName + "CurrentIndex"];
		//alert("componentIndex" + componentIndex);
		if(!componentIndex)
		{
			groupHash[groupName + "CurrentIndex"] = 0;
			componentIndex = 0;
		}
			
		if(componentIndex > 0)
		{
			componentIndex = componentIndex - 1;
			//alert("groupName: " + groupName);
			var div = document.getElementById(groupName + "Components");
			//alert("div: " + div.id);
			var componentTDs = div.getElementsByTagName("div");
			//alert("componentTDs:" + componentTDs.length);
			//alert("componentIndex" + componentTDs.length);
		
			for (var i = 0; i < componentTDs.length - 1; i++) 
			{ 
				//alert("Width of this a element is : " + componentTDs[i].style.width + ":" + componentTDs[i].id + "\n"); 
				//alert("i: " + i);
				//alert("componentIndex: " + componentIndex);
				if(i == componentIndex)
				{
					//componentTDs[i].style.width = "150px";
					componentTDs[i].style.display = "block";					
				}
			} 
	
			//alert("Current:" + eval(groupName + "componentIndexStart"));
			//alert("Current:" + eval(groupName + "componentIndexMax"));
		}
		
		groupHash[groupName + "CurrentIndex"] = componentIndex;
	}
	
	
	//
	// QueryString
	//
	
	function QueryString(key)
	{
		var value = null;
		for (var i=0;i<QueryString.keys.length;i++)
		{
			if (QueryString.keys[i]==key)
			{
				value = QueryString.values[i];
				break;
			}
		}
		return value;
	}
	
	QueryString.keys = new Array();
	QueryString.values = new Array();
	
	function QueryString_Parse()
	{
		var query = window.location.search.substring(1);
		var pairs = query.split("&");
		
		for (var i=0;i<pairs.length;i++)
		{
			var pos = pairs[i].indexOf('=');
			if (pos >= 0)
			{
				var argname = pairs[i].substring(0,pos);
				var value = pairs[i].substring(pos+1);
				QueryString.keys[QueryString.keys.length] = argname;
				QueryString.values[QueryString.values.length] = value;		
			}
		}
	
	}
	
	QueryString_Parse();