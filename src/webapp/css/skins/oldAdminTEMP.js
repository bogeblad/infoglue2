#*
	<script type="text/javascript">
	<!--
		try { document.execCommand('BackgroundImageCache', false, true); } catch(e) {}

		#if($infoGluePrincipal.isAdministrator)
			alert("You should only use this account to solve access rights issues etc as this is not a normal user account and some features are disabled because of that.");
		#end
		
		function getIsIGStandardTools()
		{
			return true;
		}
		
		var isDragActive = false;
		var isLocalDragActive = false;
		var isDragCompleted = false;
		var dragHTML = "";
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
		
		function search()
		{
			var url = "Search.action?repositoryId=2&searchString=" + $("#searchField").val();
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
			//$('#tabsContainer').tabs('select', tabIndex);
			//alert("targetTab:" + targetTab);
			if(tabLabel != null && tabLabel != '')
				$("#" + targetTab + "TabLabel span").text(tabLabelPrefix + tabLabel);
		}

		var currentMenutoolbarLeftUrl = "";
		function getCurrentMenutoolbarLeftUrl() { return currentMenutoolbarLeftUrl; }
		
        function refreshTopToolBar(title, toolbarKey, arguments, unrefreshedContentId, changeTypeId, newContentId)
        {
			var newUrl = 'ViewToolbarButtons.action?title=' + title + '&toolbarKey=' + toolbarKey + '&' + arguments;
			//alert("newUrl:" + newUrl);
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
		
		function resize()
		{
			var windowHeight = getWindowHeight();
			var windowWidth = getWindowWidth();
			
			var paletteDivHeight = $("#paletteDiv").height();
			
			$("#tools").height(windowHeight - 88);
			var toolsWidth = $("#tools").width();
			//alert("toolsWidth:" + toolsWidth);
			$("#work").height(windowHeight - 88);
			$("#work").width(windowWidth - (toolsWidth + 16));
			$("#singleTabDiv").height(windowHeight - 115);
			$("#contentTabDiv").height(windowHeight - 115);
			$("#structureTabDiv").height(windowHeight - 115);
			$("#managementTabDiv").height(windowHeight - 115);
			$("#workIframe").attr("height", windowHeight - 115);
			$("#contentWorkIframe").attr("height", windowHeight - 115);
			$("#structureWorkIframe").attr("height", windowHeight - 115);
			$("#managementWorkIframe").attr("height", windowHeight - 115);
			$("#searchWorkIframe").attr("height", windowHeight - 115);

			var availableToolsHeight = $("#availableTools").height();
			//alert("availableToolsHeight:" + availableToolsHeight);
			var activeToolHeaderHeight = $("#activeToolHeader").height();
			
			$("#structureTreeIframe").attr("height", windowHeight - (activeToolHeaderHeight + availableToolsHeight + 118 + paletteDivHeight));
			$("#contentTreeIframe").attr("height", windowHeight - (activeToolHeaderHeight + availableToolsHeight + 144));
			$("#managementTreeIframe").attr("height", windowHeight - (activeToolHeaderHeight + availableToolsHeight + 114));
			
			if(activeToolId == "structureMarkup")
				$("#activeTool").height(windowHeight - (activeToolHeaderHeight + availableToolsHeight + 102));
			else
				$("#activeTool").height(windowHeight - (activeToolHeaderHeight + availableToolsHeight + 90));
							
				
			setTimeout("resize();",500);
		}
		
		var activeToolId = "none";
		function getActiveToolId() { return activeToolId; }
		
		function activateTool(toolMarkupDivId, toolName)
		{
			$("#" + activeToolId).hide();
			$("#" + toolMarkupDivId).show();
			$("#" + toolMarkupDivId + "Link").addClass("active");
			$("#" + activeToolId + "Link").removeClass("active");

			activeToolId = toolMarkupDivId;
			
			document.title = "" + toolName + " - $ui.getString("tool.common.adminTool.header")";
			$("#activeToolHeader h3").html(toolName);
			
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

		function showContextMenu(id, e)
		{
    		//alert("e:" + e);
			//alert("id:" + id);
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
        	
			jQuery.get("ViewStructureToolAjaxServices!contextMenu.action", { siteNodeId: id },
              	function(data){
                	//alert("Data Loaded: " + data);
					$("#contextMenuDiv").html(data);
            	});
			/*
			$("#contextMenuDiv #create").click(function () { 
            	openInlineDiv("CreateSiteNode!inputV3.action?isBranch=true&repositoryId=2&parentSiteNodeId=" + id + "&languageId=1&returnAddress=%2FinfoglueCMS%2FViewInlineOperationMessages.action&originalAddress=%2FinfoglueDeliverWorking%2FViewPage%21renderDecoratedPage.action%3FsiteNodeId%3D1%26amp%3BlanguageId%3D1%26amp%3BcontentId%3D-1", 600, 800, true, true, "New page"); 
				});
			*/
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

		$(function() {

			resize();
						
			$("#tabsContainer").tabs({
                                    select: function(event, ui) { 
													//alert("ui.tab:" + ui.tab);
													//alert("ui.tab.href:" + ui.tab.attr("href"));
													//alert("ui.tab.href:" + $(ui.tab).attr("href"));
													var href = $(ui.tab).attr("href");
													if(href.indexOf("structureTabDiv") > -1)
														activateTool('structureMarkup', '$ui.getString('tool.common.structureTool.name')'); 
													else if(href.indexOf("contentTabDiv") > -1)
														activateTool('contentMarkup', '$ui.getString('tool.common.contentTool.name')'); 
													else if(href.indexOf("managementTabDiv") > -1)
														activateTool('managementMarkup', '$ui.getString('tool.common.managementTool.name')'); 
													else if(href.indexOf("publishingTabDiv") > -1)
														activateTool('publishingMarkup', '$ui.getString('tool.common.publishingTool.name')'); 
													else if(href.indexOf("mydesktopTabDiv") > -1)
														activateTool('mydesktopMarkup', '$ui.getString('tool.common.myDesktopTool.name')'); 
												},
												/*panelTemplate: '<div id="newTabDiv" class="inlineTabDiv"><iframe id="newWorkIframe" name="newWorkIframe" src="" width="100%" height="500" frameborder="0"></iframe></div>',*/
												panelTemplate: '<div id="newTabDiv" class="inlineTabDiv newTabDiv">Loading...</div>',
												tabTemplate: '<li><a id="newTabLabel" href="#{href}"><span>#{label}</span></a> <a href="#" id="newTabLabelClose"><span style="display: inline; width: 14px; float: left; background: url(images/v3/closeTabIcon.gif); cursor: pointer;">&nbsp;</span></a></li>',
												add: function(event, ui) { $("#tabsContainer").tabs("select", "\#" + ui.panel.id); }
												/*disabled: [0, 1, 2]*/ });
			//$("#tabsContainer").tabs();

			$("#tools").resizable({
				handles: 'e',
    			resize: function() {
    				$("#toolsAccordion").accordion("resize");
					resize();
    			},
    			minHeight: 140,
				maxWidth: 400,
				minWidth: 200
    		});

			activateTool("structureMarkup", "$ui.getString('tool.common.structureTool.name')");
			#set($rootSiteNode = $this.getRepositoryRootSiteNode($repositoryId))
			openUrlInWorkArea("ViewSiteNode.action?siteNodeId=$rootSiteNode.id&repositoryId=$repositoryId", "$rootSiteNode.name", "structure");
			
			var i = 0;
            $(document).mouseover(function(e){
            	$("#debug").text("mouseover: " + isDragActive + ":"/* + getDragHTML()*/);
				if(isDragActive && !isLocalDragActive && getDragHTML() != "")
				{
					isLocalDragActive = true;
					$("#tempDraggable").remove();
					$("body").append("<div id=\"tempDraggable\" style=\"position: absolute; top: 0px; left: 0px;\">" + getDragHTML() + "</div>");
					$("#debug").text("Added tempDraggable and size:" + $("#tempDraggable").size());
				}
            });

			$(document).mousemove(function(e){
				if(isLocalDragActive)
				{
    				var pageCoords = "( " + e.pageX + ", " + e.pageY + " )";
    			    var clientCoords = "( " + e.clientX + ", " + e.clientY + " )";
    				$("#tempDraggable").css("top", e.pageY + "px").css("left", e.pageX + "px");
					//$("#debug").text("Size:" + $("#tempDraggable").size());
				}
			});
			$(document).mouseup(function(e){
				if(isDragActive)
				{
					isDragActive = false;
					isLocalDragActive = false;
					isDragCompleted = true;
    				$("#tempDraggable").remove();
            	}
			});
			$(document).mouseout(function(){
            	$("#debug").text("mouseout");
              	//$("#tempDraggable").remove();
            });
            
            $("#searchField").focus(function () {
		    	$(this).val("").css("color", "black");
		    }).blur(function () {
		    	if($(this).val() == "")
		    		$(this).val("System search").css("color", "#ccc");
		    	else if($(this).val() == "System search")
	         		$(this).css("color", "#ccc");
    		});

		});
	-->
	</script>
	*#