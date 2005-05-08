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

FCKCommands.RegisterCommand( 'InlineLink'	, new FCKDialogCommand( FCKLang['DlgMyReplaceTitle'], FCKLang['DlgMyReplaceTitle']	, FCKConfig.PluginsPath + 'inlineLink/replace.html', 340, 200 ) ) ;

// Create the "InlineLink" toolbar button.
var oInlineLink			= new FCKToolbarButton( 'InlineLink', FCKLang['DlgMyReplaceTitle'] ) ;
oInlineLink.IconPath	= FCKConfig.PluginsPath + 'inlineLink/inlinelink.gif' ;

FCKToolbarItems.RegisterItem( 'InlineLink', oInlineLink ) ;