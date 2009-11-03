try { document.execCommand('BackgroundImageCache', false, true); } catch(e) {}

var isDragActive = false;
var isLocalDragActive = false;
var isDragCompleted = false;
var dragHTML = "";

function getIsIGStandardTools()
{
	return true;
}

function notifyDragHTML(html)
{
	dragHTML = html;
	isDragCompleted = false;
	isDragActive = true;
	//alert("dragHTML:" + dragHTML);
	$("#debug").text("html: " + html);
}

function getDragHTML()
{
	return dragHTML;
}

function disableDrag()
{
	isDragActive = false;
	isLocalDragActive = false;
	$("#tempDraggable").remove();
}
function enableDrag()
{
	isDragActive = true;
}

function dragCompleted()
{
	//alert("Drag completed");
	isDragCompleted = true;
	$("#tempDraggable").remove();
}

function getIsDragCompleted()
{
	return isDragCompleted;
}

function getIsDragActive()
{
	return isDragActive;
}

function openInlineDiv(url, height, width, modal, iframe, title) 
{
	var windowHeight = getWindowHeight();
	//alert("windowHeight:" + windowHeight);
	if(windowHeight < height)
		height = windowHeight - 60;

	var windowWidth = getWindowWidth();
	//alert("windowWidth:" + windowWidth);
	if(windowWidth < width)
		width = windowWidth - 60;
	//alert("height:" + height + " - width:" + width);
		
  	var separatorSign = "?";
	if(url.indexOf("?") > -1)
  		separatorSign = "&";
	
	var addition = separatorSign + "KeepThis=true&" + (iframe ? "TB_iframe=true&" : "") + "height=" + height + "&width=" + width + (modal ? "&modal=true" : "");
	
	tb_show(title, url + addition, title);
}

function closeInlineDiv()
{
	tb_remove();
}

function search(repositoryId)
{
	var url = "Search.action?repositoryId=" + repositoryId + "&searchString=" + $("#searchField").val();
	openUrlInWorkArea(url, 'Search', 'search');
	return false;
}

function htmlTreeItemClick(itemId, repoId, path)
{
	//alert("htmlTreeItemClick:" + itemId + repoId);
	$("#workIframe").attr("src", "ViewContent!V3.action?contentId=" + itemId + "&repositoryId=" + repoId);
}

function openUrlInWorkArea(url, tabLabel, targetTab)
{
	//alert("url:" + url + " - " + tabLabel + " - " + targetTab);
	//$("#workIframe").attr("src", url);
	//if(tabLabel != null && tabLabel != '')
	//	$("#singleTabLabel span").text(tabLabel);

	var tabLabelPrefix = "";
	if(targetTab == "structure")
		tabLabelPrefix = "Page - ";
	else if(targetTab == "content")
		tabLabelPrefix = "Content - ";
	else if(targetTab == "management")
		tabLabelPrefix = "Management - ";
	else if(targetTab == "publishing")
		tabLabelPrefix = "Publishing - ";
	else if(targetTab == "search")
		tabLabelPrefix = "Search - ";

	//alert("targetTab:" + targetTab + ":" + $("#" + targetTab + "TabLabel").size() + " - " + tabLabel);
	if(targetTab && $("#" + targetTab + "TabLabel").size() == 0)
	{
		$("#tabsContainer").tabs("add", "#" + targetTab + "TabDiv", "Loading...");
		//$("#tabsContainer a[href='#" + targetTab + "TabDiv']").attr("id", targetTab + "TabLabel");
		$("#newTabLabel").attr("id", targetTab + "TabLabel");
		$("#newTabLabelMaximize").attr("id", targetTab + "TabLabelMaximize");
		$("#newTabLabelClose").attr("id", targetTab + "TabLabelClose");
		$("#" + targetTab + "TabLabelClose").click(function () { 
                                      					$("#tabsContainer ul li").each(function (i) {
                                                            var tabId = $(this).children("a").attr("id");
															//alert("i:" + i + " - " + tabId);
															if(tabId == targetTab + "TabLabel")
															{
																$("#tabsContainer").tabs('remove', i);
															}
                                                        });
                                    				});

		$("#" + targetTab + "TabLabelMaximize").click(function () { 
			if($("#work").css("position") == "absolute")
				$("#work").css("position", "inherit").css("top", "").css("left", "").css("margin", "4px 4px 4px 0px").css("border-width", "1px").css("zIndex","");
			else
				$("#work").css("position", "absolute").css("top", "0px").css("left", "0px").css("margin", "0px 0px 0px 0px").css("border-width", "0px").css("zIndex","2000");
		});

		//alert("Size:" + $("#newTabDiv").size());
		//alert("Size:" + $(".newTabDiv").size());
		$(".newTabDiv").html("<iframe id='" + targetTab + "WorkIframe" + "' name='" + targetTab + "WorkIframe" + "' src='' width='100%' height='500' frameborder='0'></iframe>");
		
		$(".newTabDiv").attr("id", targetTab + "TabDiv");
		$(".newTabDiv").removeClass("newTabDiv");

		//$("#newWorkIframe").attr("id", targetTab + "WorkIframe");
		//$("#newTabDiv").attr("name", targetTab + "TabDiv").attr("id", targetTab + "TabDiv");
		//$("#newWorkIframe").attr("name", targetTab + "WorkIframe").attr("id", targetTab + "WorkIframe");
		//$("#" + targetTab + "TabDiv").attr("id", targetTab + "TabLabel");
	}

	$("#" + targetTab + "WorkIframe").attr("src", url);
	var tabSize = $("#tabsContainer li").size();
	//alert("tabSize:" + tabSize)
	var i=0;
	for (i=0;i<=tabSize;i++)
	{
		var id = $("#tabsContainer li:eq(" + i + ") a").attr("id");
		if(id)
		{
			//alert("id:" + id)
			if(id.indexOf(targetTab) > -1)
				$("#tabsContainer").tabs("select", i);
		}
	}
	//alert("targetTab:" + targetTab);
	if(tabLabel != null && tabLabel != '')
		$("#" + targetTab + "TabLabel span").text(tabLabelPrefix + tabLabel);
}

function refreshWorkArea(targetTab)
{
	$("#" + targetTab + "WorkIframe").get(0).contentDocument.location.reload();
}

var currentMenutoolbarLeftUrl = "";
function getCurrentMenutoolbarLeftUrl() { return currentMenutoolbarLeftUrl; }

var currentUrls = new Array()
currentUrls["content"] 		= "";
currentUrls["structure"] 	= "";
currentUrls["management"] 	= "";
currentUrls["publishing"] 	= "";
currentUrls["mydesktop"] 	= "";
currentUrls["formeditor"] 	= "";

function refreshTopToolBar(title, toolbarKey, arguments, unrefreshedContentId, changeTypeId, newContentId)
{
	var newUrl = 'ViewToolbarButtons.action?title=' + title + '&toolbarKey=' + toolbarKey + '&' + arguments;
	//alert("newUrl:" + newUrl);
	if(toolbarKey.indexOf("tool.contenttool") > -1)
		currentUrls["content"] = newUrl;
	else if(toolbarKey.indexOf("tool.structuretool") > -1)
		currentUrls["structure"] = newUrl;
	else if(toolbarKey.indexOf("tool.managementtool") > -1)
		currentUrls["management"] = newUrl;
	else if(toolbarKey.indexOf("tool.publishingtool") > -1)
		currentUrls["publishing"] = newUrl;
	else if(toolbarKey.indexOf("tool.mydesktoptool") > -1)
		currentUrls["mydesktop"] = newUrl;
	else if(toolbarKey.indexOf("tool.formeditortool") > -1)
		currentUrls["formeditor"] = newUrl;
	
	currentMenutoolbarLeftUrl = newUrl;
	
	$("#menutoolbarLeft").empty();
	
	jQuery.get(newUrl, function(data){
      	//alert("Data Loaded: " + data);
		$("#menutoolbarLeft").replaceWith(data);
    });
    
	/*
	if(unrefreshedContentId > 0)
	{
		alert("About to call refresh on menu:" + unrefreshedContentId + ":" + changeTypeId + ":" + newContentId);
		if(parent.frames["menu"])
			parent.frames["menu"].refreshContent(unrefreshedContentId, changeTypeId, newContentId);
	}
	*/
}

function resetTopToolBar(toolName)
{
	var newUrl = currentUrls[toolName];
	
	currentMenutoolbarLeftUrl = newUrl;
	
	$("#menutoolbarLeft").empty();

	if(newUrl != "")
	{
		jQuery.get(newUrl, function(data){
	      	//alert("Data Loaded: " + data);
			$("#menutoolbarLeft").replaceWith(data);
	    });
	}
}

function resize()
{
	//alert("Resize");
	var windowHeight = getWindowHeight();
	var windowWidth = getWindowWidth();
	
	var paletteDivHeight = $("#paletteDiv").height();
	if(paletteDivHeight == 0)
		paletteDivHeight = 150;
	
	//alert("paletteDivHeight:" + paletteDivHeight + "-" + );
	
	$("#tools").height(windowHeight - 88);
	var toolsWidth = $("#tools").width();
	//alert("toolsWidth:" + toolsWidth);
	if($("#work").css("position") != "absolute")
	{
		$("#work").height(windowHeight - 88);
		$("#work").width(windowWidth - (toolsWidth + 16));
	}
	else
	{
		$("#work").height(windowHeight);
		$("#work").width(windowWidth);
	}
	
	$("#singleTabDiv").height(windowHeight - 115);
	$("#contentTabDiv").height(windowHeight - 115);
	$("#structureTabDiv").height(windowHeight - 115);
	$("#managementTabDiv").height(windowHeight - 115);
	$("#publishingTabDiv").height(windowHeight - 115);
	$("#mydesktopTabDiv").height(windowHeight - 115);
	$("#workIframe").attr("height", windowHeight - 115);
	$("#contentWorkIframe").attr("height", windowHeight - 115);
	$("#structureWorkIframe").attr("height", windowHeight - 115);
	$("#managementWorkIframe").attr("height", windowHeight - 115);
	$("#publishingWorkIframe").attr("height", windowHeight - 115);
	$("#mydesktopWorkIframe").attr("height", windowHeight - 115);
	$("#searchWorkIframe").attr("height", windowHeight - 50);

	var availableToolsHeight = $("#availableTools").height();
	var activeToolHeaderHeight = $("#activeToolHeader").height();
	//alert("availableToolsHeight:" + availableToolsHeight);
	//alert("activeToolHeaderHeight:" + activeToolHeaderHeight);
	
	$("#structureTreeIframe").attr("height", windowHeight - (activeToolHeaderHeight + availableToolsHeight + 118 + paletteDivHeight));
	$("#contentTreeIframe").height(windowHeight - (activeToolHeaderHeight + availableToolsHeight + $("#contentRepositoryChoiceDiv").height() + 92));
	$("#managementTreeIframe").height(windowHeight - (activeToolHeaderHeight + availableToolsHeight + 90));
	$("#publishingTreeIframe").height(windowHeight - (activeToolHeaderHeight + availableToolsHeight + 90));
	$("#mydesktopTreeIframe").height(windowHeight - (activeToolHeaderHeight + availableToolsHeight + 90));
	
	if(activeToolId == "structureMarkup")
		$("#activeTool").height(windowHeight - (activeToolHeaderHeight + availableToolsHeight + 102));
	else
		$("#activeTool").height(windowHeight - (activeToolHeaderHeight + availableToolsHeight + 90));
					
	$("#debug").text("Height: " + $("#managementTreeIframe").height() + ", availableToolsHeight: " + availableToolsHeight + ", activeToolHeaderHeight=" + activeToolHeaderHeight);
	//$("#debug").text("Height: " + $("#contentTreeIframe").attr("height") + ", ContentTool:" + (windowHeight - (activeToolHeaderHeight + availableToolsHeight + 124)) + "minus:" + (activeToolHeaderHeight + availableToolsHeight + 90) + ", availableToolsHeight: " + availableToolsHeight + ", activeToolHeaderHeight=" + activeToolHeaderHeight);
		
	setTimeout("resize();", 1000);
}

var activeToolId = "none";
function getActiveToolId() { return activeToolId; }

function activateTool(toolMarkupDivId, toolName, suffix, checkWorkArea)
{
	$("#" + activeToolId).hide();
	$("#" + toolMarkupDivId).show();
	$("#" + toolMarkupDivId + "Link").addClass("active");
	$("#" + activeToolId + "Link").removeClass("active");

	activeToolId = toolMarkupDivId;
	
	resize();
	
	document.title = "" + toolName + " - " + suffix;
	$("#activeToolHeader h3").html(toolName);
	
	var toolName = toolMarkupDivId.replace("Markup", "");
	if(checkWorkArea)
	{
		var tabSize = $("#tabsContainer li").size();
		var i=0;
		var exists = false;
		for (i=0;i<=tabSize;i++)
		{
			var id = $("#tabsContainer li:eq(" + i + ") a").attr("id");
			if(id)
			{
				//alert("id:" + id)
				if(id.indexOf(toolName) > -1)
					exists = true;
			}
		}
		
		//alert("exists:" + exists)
		if(!exists)
		{
			if(toolName == "content")
				openUrlInWorkArea("ViewContentToolStartPage!V3.action", toolName, "content");
			if(toolName == "structure")
				openUrlInWorkArea("ViewStructureToolStartPage!V3.action", toolName, "structure");
			if(toolName == "management")
				openUrlInWorkArea("ViewManagementToolStartPage!V3.action", toolName, "management");
			if(toolName == "publishing")
				openUrlInWorkArea("ViewPublishingToolStartPage!V3.action", toolName, "publishing");
			if(toolName == "mydesktop")
				openUrlInWorkArea("ViewMyDesktopToolStartPage!V3.action", toolName, "mydesktop");	
			if(toolName == "formeditor")
				openUrlInWorkArea("ViewFormEditorStartPage!V3.action", toolName, "formeditor");	
		}
	}
	
	resetTopToolBar(toolName);
	
	return false;
}

function openMySettings()
{
	openInlineDiv("ViewMySettings.action", 700, 800, true, true, "MySettings");
}

function closeContextMenu()
{
	$("#contextMenuDiv").hide();
}

function showContextMenu(ajaxUrl, e)
{
	//alert("e:" + e);
	//alert("ajaxUrl:" + ajaxUrl);
	//alert("Offset:" + document.getElementById("activeTool").offsetTop);
		
	if(!e)
		e = window.event;
 
	var clientX = getEventPositionX(e) + 16;
	var clientY = getEventPositionY(e) + 80;
		
	var rightedge = document.body.clientWidth - clientX;
	var bottomedge = getWindowHeight() - clientY;

	var menuDiv = document.getElementById("contextMenuDiv");
	
	if (rightedge < menuDiv.offsetWidth)
		clientX = (clientX - menuDiv.offsetWidth);
	
	if (bottomedge < menuDiv.offsetHeight && (clientY - menuDiv.offsetHeight > 0))
		clientY = (clientY - menuDiv.offsetHeight);
		
	menuDiv.style.left 	= clientX + "px";
	menuDiv.style.top 	= clientY + "px";
	
	jQuery.get(ajaxUrl,
	  	function(data){
			$("#contextMenuDiv").html(data);
    	});
	$("#contextMenuDiv").show();

	return false;
}

function changeRepository(repositoryId, repositoryName, treeDiv, baseAddress, closeDivId)
{
	$("#" + closeDivId + " a").removeClass("current");
	$("#" + closeDivId + " a:contains('" + repositoryName + "')").addClass("current");
	$("#" + closeDivId + "Handle").text(repositoryName);
	$("#" + treeDiv).attr("src", baseAddress + repositoryId);
	$("#" + closeDivId).hide();
	
	return false;
}
