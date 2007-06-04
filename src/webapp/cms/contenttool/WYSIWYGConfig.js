// Disable the custom Enter Key Handler. This option will be removed in version 2.5.
FCKConfig.DisableEnterKeyHandler = false ;

FCKConfig.CustomConfigurationsPath = '' ;
FCKConfig.EditorAreaCSS = FCKConfig.BasePath + 'css/fck_editorarea.css' ;
FCKConfig.ToolbarComboPreviewCSS = '/infoglueCMS/css/fck_toolbar.css' ;
FCKConfig.DocType = '' ;
FCKConfig.BaseHref = '' ;
FCKConfig.FullPage = false ;
FCKConfig.Debug = false ;
FCKConfig.AllowQueryStringDebug = true ;
FCKConfig.SkinPath = FCKConfig.BasePath + 'skins/default/' ;
FCKConfig.PreloadImages = [ FCKConfig.SkinPath + 'images/toolbar.start.gif', FCKConfig.SkinPath + 'images/toolbar.buttonarrow.gif' ] ;
FCKConfig.PluginsPath = FCKConfig.BasePath + 'plugins/' ;
// FCKConfig.Plugins.Add( 'autogrow' ) ;
FCKConfig.AutoGrowMax = 400 ;
// FCKConfig.ProtectedSource.Add( /<%[\s\S]*?%>/g ) ;	// ASP style server side code <%...%>
// FCKConfig.ProtectedSource.Add( /<\?[\s\S]*?\?>/g ) ;	// PHP style server side code
// FCKConfig.ProtectedSource.Add( /(<asp:[^\>]+>[\s|\S]*?<\/asp:[^\>]+>)|(<asp:[^\>]+\/>)/gi ) ;	// ASP.Net style tags <asp:control>
FCKConfig.AutoDetectLanguage	= true ;
FCKConfig.DefaultLanguage		= 'en' ;
FCKConfig.ContentLangDirection	= 'ltr' ;
FCKConfig.ProcessHTMLEntities	= true ;
FCKConfig.IncludeLatinEntities	= true ;
FCKConfig.IncludeGreekEntities	= true ;
FCKConfig.ProcessNumericEntities = false ;
FCKConfig.AdditionalNumericEntities = ''  ;		// Single Quote: "'"
FCKConfig.FillEmptyBlocks	= true ;
FCKConfig.FormatSource		= true ;
FCKConfig.FormatOutput		= true ;
FCKConfig.FormatIndentator	= '    ' ;
FCKConfig.ForceStrongEm = true ;
FCKConfig.GeckoUseSPAN	= false ;
FCKConfig.StartupFocus	= false ;
FCKConfig.ForcePasteAsPlainText	= false ;
FCKConfig.AutoDetectPasteFromWord = true ;	// IE only.
FCKConfig.ForceSimpleAmpersand	= false ;
FCKConfig.TabSpaces		= 0 ;
FCKConfig.ShowBorders	= true ;
FCKConfig.SourcePopup	= false ;
FCKConfig.ToolbarStartExpanded	= true ;
FCKConfig.ToolbarCanCollapse	= true ;
FCKConfig.IgnoreEmptyParagraphValue = true ;
FCKConfig.PreserveSessionOnFileBrowser = false ;
FCKConfig.FloatingPanelsZIndex = 10000 ;

FCKConfig.TemplateReplaceAll = true ;
FCKConfig.TemplateReplaceCheckbox = true ;

FCKConfig.ToolbarLocation = 'In' ;

FCKConfig.ToolbarSets["Default"] = [
	['Source','DocProps','-','Save','NewPage','Preview','-','Templates'],
	['Cut','Copy','Paste','PasteText','PasteWord','-','Print','SpellCheck'],
	['Undo','Redo','-','Find','Replace','-','SelectAll','RemoveFormat'],
	['Form','Checkbox','Radio','TextField','Textarea','Select','Button','ImageButton','HiddenField'],
	'/',
	['Bold','Italic','Underline','StrikeThrough','-','Subscript','Superscript'],
	['OrderedList','UnorderedList','-','Outdent','Indent'],
	['JustifyLeft','JustifyCenter','JustifyRight','JustifyFull'],
	['Link','Unlink','Anchor'],
	['Image','Flash','Table','Rule','Smiley','SpecialChar','PageBreak'],
	'/',
	['Style','FontFormat','FontName','FontSize'],
	['TextColor','BGColor'],
	['FitWindow','-','About']
] ;

FCKConfig.EnterMode = 'p' ;			// p | div | br
FCKConfig.ShiftEnterMode = 'br' ;	// p | div | br

FCKConfig.Keystrokes = [
	[ CTRL + 65 /*A*/, true ],
	[ CTRL + 67 /*C*/, true ],
	[ CTRL + 70 /*F*/, true ],
	[ CTRL + 83 /*S*/, true ],
	[ CTRL + 88 /*X*/, true ],
	[ CTRL + 86 /*V*/, 'Paste' ],
	[ SHIFT + 45 /*INS*/, 'Paste' ],
	[ CTRL + 90 /*Z*/, 'Undo' ],
	[ CTRL + 89 /*Y*/, 'Redo' ],
	[ CTRL + SHIFT + 90 /*Z*/, 'Redo' ],
	[ CTRL + 76 /*L*/, 'Link' ],
	[ CTRL + 66 /*B*/, 'Bold' ],
	[ CTRL + 73 /*I*/, 'Italic' ],
	[ CTRL + 85 /*U*/, 'Underline' ],
	[ CTRL + SHIFT + 83 /*S*/, 'Save' ],
	[ CTRL + ALT + 13 /*ENTER*/, 'FitWindow' ],
	[ CTRL + 9 /*TAB*/, 'Source' ]
] ;

FCKConfig.ContextMenu = ['Generic','Link','Anchor','Image','Flash','Select','Textarea','Checkbox','Radio','TextField','HiddenField','ImageButton','Button','BulletedList','NumberedList','Table','Form'] ;
FCKConfig.FontColors = '000000,993300,333300,003300,003366,FF6600,808000,666699,808080,FF0000,FF9900,99CC00,339966,33CCCC,3366FF,800080,999999,FF00FF,FFCC00,FFFF00,FF99CC,FFCC99,FFFF99,CCFFCC,CCFFFF,99CCFF,CC99FF,FFFFFF' ;
FCKConfig.FontNames = 'Times New Roman;Verdana' ;
FCKConfig.FontSizes = '1/xx-small;2/x-small;3/small;4/medium' ;
//FCKConfig.FontFormats	= 'p;div;pre;address;h1;h2;h3' ;

FCKConfig.StylesXmlPath	= '$request.contextPath/WYSIWYGProperties!viewStylesXML.action?repositoryId=$request.getParameter("repositoryId")&contentId=$request.getParameter("contentId")&languageId=$request.getParameter("languageId")' ;
FCKConfig.TemplatesXmlPath	= FCKConfig.EditorPath + 'fcktemplates.xml' ;

FCKConfig.SpellChecker			= 'ieSpell' ;	// 'ieSpell' | 'SpellerPages'
FCKConfig.IeSpellDownloadUrl	= 'http://www.iespell.com/download.php' ;
FCKConfig.SpellerPagesServerScript = 'server-scripts/spellchecker.php' ;	// Available extension: .php .cfm .pl

FCKConfig.MaxUndoLevels = 15 ;

FCKConfig.DisableObjectResizing = false ;
FCKConfig.DisableFFTableHandles = true ;

FCKConfig.LinkDlgHideTarget		= false ;
FCKConfig.LinkDlgHideAdvanced	= false ;

FCKConfig.ImageDlgHideLink		= false ;
FCKConfig.ImageDlgHideAdvanced	= false ;

FCKConfig.FlashDlgHideAdvanced	= false ;

FCKConfig.ProtectedTags = '' ;

// This will be applied to the body element of the editor
FCKConfig.BodyId = '' ;
FCKConfig.BodyClass = '' ;

// The option switches between trying to keep the html structure or do the changes so the content looks like it was in Word
FCKConfig.CleanWordKeepsStructure = false ;

// The following value defines which File Browser connector and Quick Upload
// "uploader" to use. It is valid for the default implementaion and it is here
// just to make this configuration file cleaner.
// It is not possible to change this value using an external file or even
// inline when creating the editor instance. In that cases you must set the
// values of LinkBrowserURL, ImageBrowserURL and so on.
// Custom implementations should just ignore it.
var _FileBrowserLanguage	= 'asp' ;	// asp | aspx | cfm | lasso | perl | php | py
var _QuickUploadLanguage	= 'asp' ;	// asp | aspx | cfm | lasso | php


// Don't care about the following line. It just calculates the correct connector
// extension to use for the default File Browser (Perl uses "cgi").
var _FileBrowserExtension = _FileBrowserLanguage == 'perl' ? 'cgi' : _FileBrowserLanguage ;

FCKConfig.LinkBrowser = true ;
FCKConfig.LinkBrowserURL = "$request.contextPath/ViewLinkDialog!viewLinkDialogForFCKEditor.action?repositoryId=$request.getParameter("repositoryId")&contentId=$request.getParameter("contentId")&languageId=$request.getParameter("languageId")" ;
FCKConfig.LinkBrowserWindowWidth	= "770" ;
FCKConfig.LinkBrowserWindowHeight	= "660" ;
FCKConfig.ImageBrowser = true ;

// Work in progress, new DigitalAsset Browser /Stefan
FCKConfig.ImageBrowserURL = "$request.contextPath/ViewContentVersion!viewAssetsDialogForFCKEditor.action?repositoryId=$request.getParameter("repositoryId")&contentId=$request.getParameter("contentId")&languageId=$request.getParameter("languageId")";	
//FCKConfig.ImageBrowserURL = "$request.contextPath/ViewAssetList!browser.action?repositoryId=$request.getParameter("repositoryId")&contentId=$request.getParameter("contentId")&languageId=$request.getParameter("languageId")";	
FCKConfig.ImageBrowserWindowWidth  = "880" ;
FCKConfig.ImageBrowserWindowHeight = "600" ;

FCKConfig.FlashBrowser = true ;
FCKConfig.FlashBrowserURL = "$request.contextPath/ViewContentVersion!viewAssetsDialogForFCKEditor.action?repositoryId=$request.getParameter("repositoryId")&contentId=$request.getParameter("contentId")&languageId=$request.getParameter("languageId")";
//FCKConfig.FlashBrowserURL = FCKConfig.BasePath + 'filemanager/browser/default/browser.html?Type=Flash&Connector=connectors/' + _FileBrowserLanguage + '/connector.' + _FileBrowserExtension ;
FCKConfig.FlashBrowserWindowWidth  = "880" ;
FCKConfig.FlashBrowserWindowHeight = "600" ;

FCKConfig.LinkUpload = false ;
FCKConfig.LinkUploadURL = FCKConfig.BasePath + 'filemanager/upload/' + _QuickUploadLanguage + '/upload.' + _QuickUploadLanguage ;
FCKConfig.LinkUploadAllowedExtensions	= "" ;			// empty for all
FCKConfig.LinkUploadDeniedExtensions	= ".(html|htm|php|php2|php3|php4|php5|phtml|pwml|inc|asp|aspx|ascx|jsp|cfm|cfc|pl|bat|exe|com|dll|vbs|js|reg|cgi|htaccess|asis)$" ;	// empty for no one

FCKConfig.ImageUpload = false ;
FCKConfig.ImageUploadURL = FCKConfig.BasePath + 'filemanager/upload/' + _QuickUploadLanguage + '/upload.' + _QuickUploadLanguage + '?Type=Image' ;
FCKConfig.ImageUploadAllowedExtensions	= ".(jpg|gif|jpeg|png|bmp)$" ;		// empty for all
FCKConfig.ImageUploadDeniedExtensions	= "" ;							// empty for no one

FCKConfig.FlashUpload = false ;
FCKConfig.FlashUploadURL = FCKConfig.BasePath + 'filemanager/upload/' + _QuickUploadLanguage + '/upload.' + _QuickUploadLanguage + '?Type=Flash' ;
FCKConfig.FlashUploadAllowedExtensions	= ".(swf|fla)$" ;		// empty for all
FCKConfig.FlashUploadDeniedExtensions	= "" ;					// empty for no one

FCKConfig.SmileyPath	= FCKConfig.BasePath + 'images/smiley/msn/' ;
FCKConfig.SmileyImages	= ['regular_smile.gif','sad_smile.gif','wink_smile.gif','teeth_smile.gif','confused_smile.gif','tounge_smile.gif','embaressed_smile.gif','omg_smile.gif','whatchutalkingabout_smile.gif','angry_smile.gif','angel_smile.gif','shades_smile.gif','devil_smile.gif','cry_smile.gif','lightbulb.gif','thumbs_down.gif','thumbs_up.gif','heart.gif','broken_heart.gif','kiss.gif','envelope.gif'] ;
FCKConfig.SmileyColumns = 8 ;
FCKConfig.SmileyWindowWidth		= 320 ;
FCKConfig.SmileyWindowHeight	= 240 ;

//CUSTOM
FCKConfig.StyleFontFormats = 'false';
FCKConfig.StyleStyles = 'false';
FCKConfig.AllowImageSizes = 'false';

FCKConfig.ImageClasses = 'img_full,img_left_small,img_left_medium,img_left_large,img_left_xlarge,img_right_small,img_right_medium,img_right_large,img_right_xlarge';
FCKConfig.ImageClassesNames = 'Fullbredd (Bredd 370px),V&auml; - Liten (Bredd 111px),V&auml; - Mellan (Bredd 148px),V&auml; - Stor (Bredd 185px),V&auml; - St&ouml;rst (Bredd 222px),H&ouml; - Liten (Bredd 111px),H&ouml; - Mellan (Bredd 148px),H&ouml; - Stor (Bredd 185px),H&ouml; - St&ouml;rst (Bredd 222px)';

FCKConfig.FontFormats	= 'h2;h4;h3;h5;p' ;
FCKConfig.FontFormatNames = 'Br&#246dtext;Formaterad;Adress;Rubrik niv&#229 1;Underrubrik;Styckesrubrik;Ingress;Stort citat;Rubrik 6;Div' ;
