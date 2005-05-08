/*
 * FCKeditor - The text editor for internet
 * Copyright (C) 2003-2004 Frederico Caldeira Knabben
 * 
 * Licensed under the terms of the GNU Lesser General Public License:
 * 		http://www.opensource.org/licenses/lgpl-license.php
 * 
 * For further information visit:
 * 		http://www.fckeditor.net/
 * 
 * File Name: fckplugin.js
 * 	This is the sample plugin definition file.
 * 
 * Version:  2.0 RC3
 * Modified: 2004-11-22 11:20:10
 * 
 * File Authors:
 * 		Frederico Caldeira Knabben (fredck@fckeditor.net)
 */
alert("repositoryId:" + repositoryId);
//var url = _applicationContext + "ViewContentVersion!viewAssetsDialog.action?repositoryId=" + repositoryId + "&contentId=" + contentId + "&languageId=" + languageId + "&textAreaId=" + editor.id + extraParameters;
//var url = "/infoglueCMS/ViewContentVersion!viewAssetsDialog.action"; //?repositoryId=" + repositoryId + "&contentId=" + contentId + "&languageId=" + languageId + "&textAreaId=" + editor.id + extraParameters;
var url = "/infoglueCMS/ViewContentVersion!viewAssetsDialog.action"; //?repositoryId=" + repositoryId + "&contentId=" + contentId + "&languageId=" + languageId + "&textAreaId=" + editor.id + extraParameters;

FCKCommands.RegisterCommand( 'InlineImage'	, new FCKDialogCommand( FCKLang['DlgMyReplaceTitle'], FCKLang['DlgMyReplaceTitle'], url, 340, 200 ) ) ;

// Create the "InlineImage" toolbar button.
var oInlineImage			= new FCKToolbarButton( 'InlineImage', FCKLang['DlgMyReplaceTitle'] ) ;
oInlineImage.IconPath	= FCKConfig.PluginsPath + 'inlineImage/inlineimage.gif' ;

FCKToolbarItems.RegisterItem( 'InlineImage', oInlineImage ) ;