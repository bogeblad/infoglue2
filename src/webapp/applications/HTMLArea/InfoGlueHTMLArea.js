function configInfoGlue(config)
{
	//alert("Loading InfoGlue Config");

	config.statusBar = false;

	/** CUSTOMIZING THE TOOLBAR
	 * -------------------------
	 *
	 * It is recommended that you customize the toolbar contents in an
	 * external file (i.e. the one calling HTMLArea) and leave this one
	 * unchanged.  That's because when we (InteractiveTools.com) release a
	 * new official version, it's less likely that you will have problems
	 * upgrading HTMLArea.
	 */
	
	//alert("personalConfig:" + personalConfig);
	if(personalConfig != "")
	{
		config.toolbar = eval(personalConfig);
	}
	else
	{
		config.toolbar = [
		[ "fontname", "space",
		  "fontsize", "space",
		  "bold", "italic", "underline", "separator",
		  "justifyleft", "justifycenter", "justifyright", "justifyfull", "separator",
		  "insertorderedlist", "insertunorderedlist", "outdent", "indent", "separator",
		  "forecolor", "hilitecolor", "separator",
		  "inserthorizontalrule", "createlink", "inlinelink", "inlineimage", "insertimage", "inserttable", "separator",
		  "popupeditor", "htmlmode"] /*, ["formatblock"]*/
		];
	}
	
	config.fontname = {
		"Arial":	   'arial,helvetica,sans-serif',
		"Courier New": 'courier new,courier,monospace',
		"Georgia":	   'georgia,times new roman,times,serif',
		"Tahoma":	   'tahoma,arial,helvetica,sans-serif',
		"Times New Roman": 'times new roman,times,serif',
		"Verdana":	   'verdana,arial,helvetica,sans-serif',
		"impact":	   'impact'
	};

	config.fontsize = {
		"1 (8 pt)":  "1",
		"2 (10 pt)": "2",
		"3 (12 pt)": "3",
		"4 (14 pt)": "4",
		"5 (18 pt)": "5",
		"6 (24 pt)": "6",
		"7 (36 pt)": "7"
	};

	config.formatblock = {
		"Heading 1": "h1",
		"Heading 2": "h2",
		"Heading 3": "h3",
		"Normal": "p",
		"Formatted": "pre"
	};

	//alert("cssPluginArgs:" + cssPluginArgs);
	if(enableCSSPlugin == true && cssPluginArgs != "")
	{
		config.css_plugin_args = cssPluginArgs;
	}
	else if(enableCSSPlugin == true)
	{
	    config.css_plugin_args = 
	    {
	      combos : 
	      [{ 
	        	label: "Syntax",
	        	options: { "None"       	: "",
	                     "Code" 			: "code",
	                     "String" 			: "string",
	                     "Comment" 			: "comment",
	                     "Variable name" 	: "variable-name",
	                     "Type" 			: "type",
	                     "Reference" 		: "reference",
	                     "Preprocessor" 	: "preprocessor",
	                     "Keyword" 			: "keyword",
	                     "Function name" 	: "function-name",
	                     "Html tag" 		: "html-tag",
	                     "Html italic"	 	: "html-helper-italic",
	                     "Warning" 			: "warning",
	                     "Html bold" 		: "html-helper-bold"
	                   	},
	          	context: "pre"
	       }, 
	       { 
	          	label: "Info",
	          	options: { "None"         : "",
	                     "title"          : "title",
	                     "Highlight"      : "highlight",
	                     "Deprecated"     : "deprecated"
	                   }
	       }
	    ]};
	}

    
    
    //END CSS STUFF
        
	config.customSelects = {};

	function cut_copy_paste(e, cmd, obj) {
		e.execCommand(cmd);
	};

	// ADDING CUSTOM BUTTONS: please read below!
	// format of the btnList elements is "ID: [ ToolTip, Icon, Enabled in text mode?, ACTION ]"
	//    - ID: unique ID for the button.  If the button calls document.execCommand
	//	    it's wise to give it the same name as the called command.
	//    - ACTION: function that gets called when the button is clicked.
	//              it has the following prototype:
	//                 function(editor, buttonName)
	//              - editor is the HTMLArea object that triggered the call
	//              - buttonName is the ID of the clicked button
	//              These 2 parameters makes it possible for you to use the same
	//              handler for more HTMLArea objects or for more different buttons.
	//    - ToolTip: default tooltip, for cases when it is not defined in the -lang- file (HTMLArea.I18N)
	//    - Icon: path to an icon image file for the button (TODO: use one image for all buttons!)
	//    - Enabled in text mode: if false the button gets disabled for text-only mode; otherwise enabled all the time.
	
	if(personalConfig != "")
	{
		config.btnList = buttonConfig;
	}
	else
	{
		config.btnList = {
			bold: [ "Bold", "ed_format_bold.gif", false, function(e) {e.execCommand("bold");} ],
			italic: [ "Italic", "ed_format_italic.gif", false, function(e) {e.execCommand("italic");} ],
			underline: [ "Underline", "ed_format_underline.gif", false, function(e) {e.execCommand("underline");} ],
			justifyleft: [ "Justify Left", "ed_align_left.gif", false, function(e) {e.execCommand("justifyleft");} ],
			justifycenter: [ "Justify Center", "ed_align_center.gif", false, function(e) {e.execCommand("justifycenter");} ],
			justifyright: [ "Justify Right", "ed_align_right.gif", false, function(e) {e.execCommand("justifyright");} ],
			justifyfull: [ "Justify Full", "ed_align_justify.gif", false, function(e) {e.execCommand("justifyfull");} ],
			insertorderedlist: [ "Ordered List", "ed_list_num.gif", false, function(e) {e.execCommand("insertorderedlist");} ],
			insertunorderedlist: [ "Bulleted List", "ed_list_bullet.gif", false, function(e) {e.execCommand("insertunorderedlist");} ],
			outdent: [ "Decrease Indent", "ed_indent_less.gif", false, function(e) {e.execCommand("outdent");} ],
			indent: [ "Increase Indent", "ed_indent_more.gif", false, function(e) {e.execCommand("indent");} ],
			forecolor: [ "Font Color", "ed_color_fg.gif", false, function(e) {e.execCommand("forecolor");} ],
			hilitecolor: [ "Background Color", "ed_color_bg.gif", false, function(e) {e.execCommand("hilitecolor");} ],
			inserthorizontalrule: [ "Horizontal Rule", "ed_hr.gif", false, function(e) {e.execCommand("inserthorizontalrule");} ],
			createlink: [ "Insert Web Link", "ed_link.gif", false, function(e) {e.execCommand("createlink", true);} ],
		    inlinelink: ["Lets the user create a link to a page in infoglue", "ed_inlinelink.gif", false, function(e) {e.execCommand("createinlinelink");} ],
			inlineimage: ["Lets the user select a inline image", "ed_inlineimage.gif", false, function(e) {e.execCommand("insertinlineimage");} ],
		    insertimage: [ "Insert/Modify Image", "ed_image.gif", false, function(e) {e.execCommand("insertimage");} ],
			inserttable: [ "Insert Table", "insert_table.gif", false, function(e) {e.execCommand("inserttable");} ],
			htmlmode: [ "Toggle HTML Source", "ed_html.gif", true, function(e) {e.execCommand("htmlmode");} ],
			popupeditor: [ "Enlarge Editor", "fullscreen_maximize.gif", true, function(e) {e.execCommand("popupeditor");} ]
		};
	}
	
	
	/* ADDING CUSTOM BUTTONS
	 * ---------------------
	 *
	 * It is recommended that you add the custom buttons in an external
	 * file and leave this one unchanged.  That's because when we
	 * (InteractiveTools.com) release a new official version, it's less
	 * likely that you will have problems upgrading HTMLArea.
	 *
	 * Example on how to add a custom button when you construct the HTMLArea:
	 *
	 *   var editor = new HTMLArea("your_text_area_id");
	 *   var cfg = editor.config; // this is the default configuration
	 *   cfg.btnList["my-hilite"] =
	 *	[ function(editor) { editor.surroundHTML('<span style="background:yellow">', '</span>'); }, // action
	 *	  "Highlight selection", // tooltip
	 *	  "my_hilite.gif", // image
	 *	  false // disabled in text mode
	 *	];
	 *   cfg.toolbar.push(["linebreak", "my-hilite"]); // add the new button to the toolbar
	 *
	 * An alternate (also more convenient and recommended) way to
	 * accomplish this is to use the registerButton function below.
	 */
	 
	 //config.registerButton("my-hilite", "Hilite text", "my-hilite.gif", false, function(editor) {...});
	
	// initialize tooltips from the I18N module and generate correct image path
	
	for (var i in config.btnList) {
		var btn = config.btnList[i];
		//alert("In Infoglue initializer btn[1]: " + btn[1]);
		//alert("In Infoglue initializer config.imgURL: " + config.imgURL);
		if(btn[1].indexOf("images") == -1)
			btn[1] = _editor_url + config.imgURL + btn[1];
	
		//alert("In Infoglue initializer btn[1]: " + btn[1]);
		if (typeof HTMLArea.I18N.tooltips[i] != "undefined") {
			btn[0] = HTMLArea.I18N.tooltips[i];
		}
	}
	

	
	//register custom buttons 
	//config.registerButton("my-popup", "test popup", "ed_custom.gif", false, demopopup); 
	HTMLArea.prototype._createInlineLink = function(link) {
		var editor = this;
		var outparam = null;
		if (typeof link == "undefined") {
			link = this.getParentElement();
			if (link && !/^a$/i.test(link.tagName))
				link = null;
		}
		
		var text = editor.getSelectedHTML();
		//alert("text:" + text);
		
		if (link) 
		{
			//alert("link:" + link);
			//alert("link:" + link.title);
		    outparam = {
			f_href   : HTMLArea.is_ie ? editor.stripBaseURL(link.href) : link.getAttribute("href"),
			f_title  : link.title,
			f_target : link.target,
			f_text 	 : text
		    };
		}
		else
		    outparam = {
		    f_href   : "",
			f_title  : "",
			f_target : "",
			f_text 	 : text
		    };
		
		//alert("outparam:" + outparam);
		//alert("f_href:" + outparam.f_href);
		//alert("f_title:" + outparam.f_title);
		//alert("f_target:" + outparam.f_target);
		//alert("f_text:" + outparam.f_text);
		
		
		// InlineLink
		urlPrefix = "";
	  	if(!contentId)
	  		contentId = self.opener.contentId;
	  	if(!languageId)
	  		languageId = self.opener.languageId;
	  	
	  	//alert("urlPrefix:" + urlPrefix);
	  	//urlPrefix = _applicationContext;
	  	if((link && text.indexOf("getInlineAssetUrl") > -1) || (text.indexOf("getPageUrl") == -1 && confirm("Do you want to link to an internal asset instead of to a page? Click on OK to link to an asset or click on cancel to link to a page.")))
	  	{
		  	/**
	  		 * ASSET INLINE LINK
	  		 */
	  	
	  		if (link)
	  		{
	  			transformedTag = text;
		  		//alert("transformedTag:" + transformedTag);
		  		transformedTag = untransformAttribute(transformedTag);
		  		//alert("transformedTag:" + transformedTag);
		  		parenthesisIndex = transformedTag.indexOf("(");
				stopIndex 		 = transformedTag.indexOf(",");
				parenthesisStopIndex = transformedTag.indexOf(")");
				oldContentId = transformedTag.substring(parenthesisIndex + 1, stopIndex);
				assetKey = transformedTag.substring(stopIndex, parenthesisStopIndex);
				assetKey = assetKey.substring(assetKey.indexOf("\"") + 1);
				assetKey = assetKey.substring(0, assetKey.indexOf("\""));
				//alert("oldContentId:" + oldContentId);
				//alert("assetKey:" + assetKey);
				
				textStartIndex = transformedTag.indexOf(">");
				textStopIndex = transformedTag.indexOf("<", textStartIndex);
				//alert("textStartIndex:" + textStartIndex);
				//alert("textStopIndex:" + textStopIndex);
				outparam.f_text = transformedTag.substring(textStartIndex + 1, textStopIndex);
				
				extraParameters = "&oldContentId=" + oldContentId + "&assetKey=" + assetKey;
			}
			else
			{
				extraParameters = "&oldContentId=&assetKey=";
			}
			  		
	  	  	url = _applicationContext + "ViewContentVersion!viewAssetsDialog.action?repositoryId=" + repositoryId + "&contentId=" + contentId + "&languageId=" + languageId + "&treatAsLink=true&textAreaId=" + editor.id + extraParameters;
	  	
	  		//alert("outparam.f_text:" + outparam.f_text);
	  	
	  		this._relativePopupDialog(url, function(param) 
			{
				//alert("relativePopupDialog");
		
				if (!param)
					return false;
				
				//alert("param:" + param);
				//alert("param:" + param["image"]);
				//alert("param:" + param["f_text"]);
				//alert("param:" + param["f_url"]);
				//alert("param:" + param["f_href"]);
				//alert("param:" + param["f_title"]);
				//alert("param:" + param["f_target"]);
				var a = link;
				if (!a)
				{ 
					//alert("no a found:" + a);
					try 
					{
						editor._doc.execCommand("createlink", false, param.f_href);
						a = editor.getParentElement();
						var sel = editor._getSelection();
						var range = editor._createRange(sel);
						if (!HTMLArea.is_ie) {
							a = range.startContainer;
							if (!/^a$/i.test(a.tagName)) {
								a = a.nextSibling;
								if (a == null)
									a = range.startContainer.parentNode;
							}
						}
					} 
					catch(e) {alert("error:" + e);}
				}
				else 
				{
					var href = param.f_href.trim();
					editor.selectNodeContents(a);
					if (href == "") 
					{
						editor._doc.execCommand("unlink", false, null);
						editor.updateToolbar();
						return false;
					}
					else 
					{
						a.href = href;
					}
				}
				
				if (!(a && /^a$/i.test(a.tagName)))
				{
					//alert("returning false a:" + a.tagName);	
					//alert("returning false i:" + i);	
					return false;
				}
					
				//alert("a:" + a);	
				a.target = param.f_target.trim();
				a.title = param.f_title.trim();
				//alert("param.originaltag:" + param.originaltag);
				//alert("param.originaltag:" + untransformAttribute(param.originaltag));	
				a.setAttribute("originaltag", escape(untransformAttribute(param.originaltag)));
				
				//alert("a2:" + a);	
				editor.selectNodeContents(a);
				editor.updateToolbar();
			}, outparam);
			
	  	}
	  	else
	  	{	
	  		/**
	  		 * NORMAL INLINE LINK
	  		 */
	  		
	  		if (link)
	  		{
	  			transformedTag = text;
		  		//alert("transformedTag:" + transformedTag);
		  		transformedTag = untransformAttribute(transformedTag);
		  		//alert("transformedTag:" + transformedTag);
		  		
		  		parenthesisIndex = transformedTag.indexOf("(");
				stopIndex 		 = transformedTag.indexOf(",");
				//parenthesisStopIndex = transformedTag.indexOf(")");
				oldSiteNodeId = transformedTag.substring(parenthesisIndex + 1, stopIndex);
				//alert("oldSiteNodeId:" + oldSiteNodeId);
				
				textStartIndex = transformedTag.indexOf(">");
				textStopIndex = transformedTag.indexOf("<", textStartIndex);
				//alert("textStartIndex:" + textStartIndex);
				//alert("textStopIndex:" + textStopIndex);
				outparam.f_text = transformedTag.substring(textStartIndex + 1, textStopIndex);
				
				extraParameters = "&oldSiteNodeId=" + oldSiteNodeId;
			}
			else
			{
				extraParameters = "&oldSiteNodeId=";
			}
			 
	  		url = _applicationContext + "ViewStructureTreeForInlineLink.action?contentId=" + contentId + "&languageId=" + languageId + "&textAreaId=" + editor.id + extraParameters;
	  		
	  		//alert("outparam.f_text:" + outparam.f_text);
	  	
	  		this._relativePopupDialog(url, function(param) 
			{
				//alert("relativePopupDialog");
		
				if (!param)
					return false;
				
				//alert("param:" + param);
				//alert("param:" + param["f_href"]);
				//alert("param:" + param["f_title"]);
				//alert("param:" + param["f_target"]);
				//alert("param:" + param["f_text"]);
				
				var a = link;
				if (!a)
				{ 
					//alert("no a found:" + a);
					try 
					{
						editor._doc.execCommand("createlink", false, param.f_href);
						a = editor.getParentElement();
						var sel = editor._getSelection();
						var range = editor._createRange(sel);
						if (!HTMLArea.is_ie) {
							a = range.startContainer;
							if (!/^a$/i.test(a.tagName)) {
								a = a.nextSibling;
								if (a == null)
									a = range.startContainer.parentNode;
							}
						}
					} 
					catch(e) {alert("error:" + e);}
				}
				else 
				{
					var href = param.f_href.trim();
					editor.selectNodeContents(a);
					if (href == "") 
					{
						editor._doc.execCommand("unlink", false, null);
						editor.updateToolbar();
						return false;
					}
					else 
					{
						a.href = href;
					}
				}
				
				if (!(a && /^a$/i.test(a.tagName)))
				{
					//alert("returning false a:" + a.tagName);	
					//alert("returning false i:" + i);	
					return false;
				}
					
				//alert("a:" + a);	
				a.target = param.f_target.trim();
				a.title = param.f_title.trim();
				//alert("param.originaltag:" + param.originaltag);
				//alert("param.originaltag:" + untransformAttribute(param.originaltag));	
				a.setAttribute("originaltag", escape(untransformAttribute(param.originaltag)));
				
				//alert("a2:" + a);	
				editor.selectNodeContents(a);
				editor.updateToolbar();
			}, outparam);
	  	}
		
	};


	// Called when the user clicks on "InsertImage" button.  If an image is already
	// there, it will just modify it's properties.
	HTMLArea.prototype._insertInlineImage = function(image) {
		var editor = this;	// for nested functions
		var outparam = null;
		
		//alert("image1:" + image);
		if (typeof image == "undefined") {
			image = this.getParentElement();
			if (image && !/^img$/i.test(image.tagName))
				image = null;
		}
		
		extraParameters = "";
		if(image)
		{
			originalTag = image.getAttribute("originalTag");
			//alert("originalTag:" + originalTag);
			//alert("imgTag:" + imgTag);
			
			imgTag = unescape(originalTag);
			//imgTag = unescape(imgTag);
			//alert("ImageTag som skall vara ren:" + imgTag);
			transformedTag = imgTag;
			
			//var transformedTag = untransformAttribute(image)
			//alert("transformedTag:" + transformedTag);
	
			parenthesisIndex = transformedTag.indexOf("(");
			stopIndex 		 = transformedTag.indexOf(",");
			parenthesisStopIndex = transformedTag.indexOf(")");
			oldContentId = transformedTag.substring(parenthesisIndex + 1, stopIndex);
			assetKey = transformedTag.substring(stopIndex, parenthesisStopIndex);
			assetKey = assetKey.substring(assetKey.indexOf("\"") + 1);
			assetKey = assetKey.substring(0, assetKey.indexOf("\""));
			//alert("oldContentId:" + oldContentId);
			//alert("assetKey:" + assetKey);
			extraParameters = "&oldContentId=" + oldContentId + "&assetKey=" + assetKey;
			//alert("extraParameters:" + extraParameters);
		}
	
		if (image) outparam = {
			f_url    : HTMLArea.is_ie ? editor.stripBaseURL(image.src) : image.getAttribute("src"),
			f_alt    : image.alt,
			f_border : image.border,
			f_align  : image.align,
			f_vert   : image.vspace,
			f_horiz  : image.hspace
		};
		//alert("outparam:" + outparam);
		
	  	if(!repositoryId)
	  		repositoryId = self.opener.repositoryId;
	  	if(!contentId)
	  		contentId = self.opener.contentId;
	  	if(!languageId)
	  		languageId = self.opener.languageId;
	  	
	  	url = _applicationContext + "ViewContentVersion!viewAssetsDialog.action?repositoryId=" + repositoryId + "&contentId=" + contentId + "&languageId=" + languageId + "&textAreaId=" + editor.id + extraParameters;
	
		//alert("url:" + url);
		this._relativePopupDialog(url, function(param) 
		{
			if (!param) {	// user must have pressed Cancel
				return false;
			}
			var img = image;
			//alert("img:" + img);
			if (!img) {
				var sel = editor._getSelection();
				//alert("sel:" + sel);
				var range = editor._createRange(sel);
				//alert("range1:" + range);
				//alert("range1:" + range.text);
				//alert("range1:" + range.htmlText);
				//alert("param:" + param);
				//alert("Calling insertinlineimage with " + param.f_url);
				
				//editor._doc.execCommand("insertimage", false, param.f_url);
				
				if (HTMLArea.is_ie) {
					img = range.parentElement();
					// wonder if this works...
					if (img.tagName.toLowerCase() != "img") {
						img = img.previousSibling;
					}
				} else {
					img = range.startContainer.previousSibling;
				}
				insertMethod = "insert";
			} else {
				img.src = param.f_url;
				insertMethod = "update";			
			}
			//alert("img:" + img);
	
			var sel = editor._getSelection();
			//alert("sel1:" + sel);
			//alert("sel1:" + sel.type);
			//alert("sel1:" + sel.TextRange);
			var range = editor._createRange(sel);
			//alert("range1:" + range);
			//alert("range1:" + range.text);
			//alert("range1:" + range.htmlText);
				
			if(insertMethod == "insert")	
			{	
				//alert("param.image:" + param.image);
				editor.insertHTML(param.image); 
				//alert("param.image:" + param.image);
			}
			else
			{
				for (field in param) 
				{
					var value = param[field];
					//alert("field:" + field);
					//alert("value:" + value);
					switch (field) {
						case "f_alt"    : img.alt	 = value; break;
					    case "f_border" : img.border = parseInt(value || "0"); break;
					    case "f_align"  : img.align	 = value; break;
					    case "f_vert"   : img.vspace = parseInt(value || "0"); break;
					    case "f_horiz"  : img.hspace = parseInt(value || "0"); break;
					    case "image"  	: img.removeAttribute("originaltag"); img.setAttribute("originaltag", escape(untransformAttribute(value))); break;
					}
				}
			}		
		}, outparam);
	};

	
};

