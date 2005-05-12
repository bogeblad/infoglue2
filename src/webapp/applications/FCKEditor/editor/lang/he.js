/*
 * FCKeditor - The text editor for internet
 * Copyright (C) 2003-2005 Frederico Caldeira Knabben
 * 
 * Licensed under the terms of the GNU Lesser General Public License:
 * 		http://www.opensource.org/licenses/lgpl-license.php
 * 
 * For further information visit:
 * 		http://www.fckeditor.net/
 * 
 * File Name: he.js
 * 	Hebrew language file.
 * 
 * File Authors:
 * 		Ophir Radnitz (ophir@liqweed.net)
 */

var FCKLang =
{
// Language direction : "ltr" (left to right) or "rtl" (right to left).
Dir					: "rtl",

ToolbarCollapse		: "כיווץ סרגל הכלי�?",
ToolbarExpand		: "פתיחת סרגל הכלי�?",

// Toolbar Items and Context Menu
Save				: "שמירה",
NewPage				: "דף חדש",
Preview				: "תצוגה מקדימה",
Cut					: "גזירה",
Copy				: "העתקה",
Paste				: "הדבקה",
PasteText			: "הדבקה כטקסט פשוט",
PasteWord			: "הדבקה מ-Word",
Print				: "הדפסה",
SelectAll			: "בחירת הכל",
RemoveFormat		: "הסרת העיצוב",
InsertLinkLbl		: "קישור",
InsertLink			: "הוספת/עריכת קישור",
RemoveLink			: "הסרת הקישור",
Anchor				: "Insert/Edit Anchor",	//MISSING
InsertImageLbl		: "תמונה",
InsertImage			: "הוספת/עריכת תמונה",
InsertTableLbl		: "טבלה",
InsertTable			: "הוספת/עריכת טבלה",
InsertLineLbl		: "קו",
InsertLine			: "הוספת קו �?ופקי",
InsertSpecialCharLbl: "תו מיוחד",
InsertSpecialChar	: "הוספת תו מיוחד",
InsertSmileyLbl		: "סמיילי",
InsertSmiley		: "הוספת סמיילי",
About				: "�?ודות FCKeditor",
Bold				: "מודגש",
Italic				: "נטוי",
Underline			: "קו תחתון",
StrikeThrough		: "כתיב מחוק",
Subscript			: "כתיב תחתון",
Superscript			: "כתיב עליון",
LeftJustify			: "יישור לשמ�?ל",
CenterJustify		: "מרכוז",
RightJustify		: "יישור לימין",
BlockJustify		: "יישור לשוליי�?",
DecreaseIndent		: "הקטנת �?ינדנטציה",
IncreaseIndent		: "הגדלת �?ינדנטציה",
Undo				: "ביטול צעד �?חרון",
Redo				: "חזרה על צעד �?חרון",
NumberedListLbl		: "רשימה ממוספרת",
NumberedList		: "הוספת/הסרת רשימה ממוספרת",
BulletedListLbl		: "רשימת נקודות",
BulletedList		: "הוספת/הסרת רשימת נקודות",
ShowTableBorders	: "הצגת מסגרת הטבלה",
ShowDetails			: "הצגת פרטי�?",
Style				: "סגנון",
FontFormat			: "עיצוב",
Font				: "גופן",
FontSize			: "גודל",
TextColor			: "צבע טקסט",
BGColor				: "צבע רקע",
Source				: "מקור",
Find				: "חיפוש",
Replace				: "החלפה",
SpellCheck			: "Check Spell",	//MISSING
UniversalKeyboard	: "Universal Keyboard",	//MISSING

Form			: "Form",	//MISSING
Checkbox		: "Checkbox",	//MISSING
RadioButton		: "Radio Button",	//MISSING
TextField		: "Text Field",	//MISSING
Textarea		: "Textarea",	//MISSING
HiddenField		: "Hidden Field",	//MISSING
Button			: "Button",	//MISSING
SelectionField	: "Selection Field",	//MISSING
ImageButton		: "Image Button",	//MISSING

// Context Menu
EditLink			: "עריכת קישור",
InsertRow			: "הוספת שורה",
DeleteRows			: "מחיקת שורות",
InsertColumn		: "הוספת עמודה",
DeleteColumns		: "מחיקת עמודות",
InsertCell			: "הוספת ת�?",
DeleteCells			: "מחיקת ת�?י�?",
MergeCells			: "מיזוג ת�?י�?",
SplitCell			: "פיצול ת�?י�?",
CellProperties		: "תכונות הת�?",
TableProperties		: "תכונות הטבלה",
ImageProperties		: "תכונות התמונה",

AnchorProp			: "Anchor Properties",	//MISSING
ButtonProp			: "Button Properties",	//MISSING
CheckboxProp		: "Checkbox Properties",	//MISSING
HiddenFieldProp		: "Hidden Field Properties",	//MISSING
RadioButtonProp		: "Radio Button Properties",	//MISSING
ImageButtonProp		: "Image Button Properties",	//MISSING
TextFieldProp		: "Text Field Properties",	//MISSING
SelectionFieldProp	: "Selection Field Properties",	//MISSING
TextareaProp		: "Textarea Properties",	//MISSING
FormProp			: "Form Properties",	//MISSING

FontFormats			: "נורמלי;קוד;כתובת;כותרת;כותרת 2;כותרת 3;כותרת 4;כותרת 5;כותרת 6",

// Alerts and Messages
ProcessingXHTML		: "מעבד XHTML, נ�? להמתין...",
Done				: "המשימה הושלמה",
PasteWordConfirm	: "נר�?ה הטקסט שבכוונתך להדביק מקורו בקובץ Word. ה�?�? ברצונך לנקות �?ותו טר�? ההדבקה?",
NotCompatiblePaste	: "פעולה זו זמינה לדפדפן Internet Explorer מגירס�? 5.5 ומעלה. ה�?�? להמשיך בהדבקה לל�? הניקוי?",
UnknownToolbarItem	: "פריט ל�? ידוע בסרגל הכלי�? \"%1\"",
UnknownCommand		: "ש�? פעולה ל�? ידוע \"%1\"",
NotImplemented		: "הפקודה ל�? מיושמת",
UnknownToolbarSet	: "ערכת סרגל הכלי�? \"%1\" ל�? קיימת",

// Dialogs
DlgBtnOK			: "�?ישור",
DlgBtnCancel		: "ביטול",
DlgBtnClose			: "סגירה",
DlgBtnBrowseServer	: "Browse Server",	//MISSING
DlgAdvancedTag		: "�?פשרויות מתקדמות",
DlgOpOther			: "&lt;Other&gt;",	//MISSING

// General Dialogs Labels
DlgGenNotSet		: "&lt;ל�? נקבע&gt;",
DlgGenId			: "זיהוי (Id)",
DlgGenLangDir		: "כיוון שפה",
DlgGenLangDirLtr	: "שמ�?ל לימין (LTR)",
DlgGenLangDirRtl	: "ימין לשמ�?ל (RTL)",
DlgGenLangCode		: "קוד שפה",
DlgGenAccessKey		: "מקש גישה",
DlgGenName			: "ש�?",
DlgGenTabIndex		: "מספר ט�?ב",
DlgGenLongDescr		: "קישור לתי�?ור מפורט",
DlgGenClass			: "Stylesheet Classes",
DlgGenTitle			: "כותרת מוצעת",
DlgGenContType		: "Content Type מוצע",
DlgGenLinkCharset	: "קידוד המש�?ב המקושר",
DlgGenStyle			: "סגנון",

// Image Dialog
DlgImgTitle			: "תכונות התמונה",
DlgImgInfoTab		: "מידע על התמונה",
DlgImgBtnUpload		: "שליחה לשרת",
DlgImgURL			: "כתובת (URL)",
DlgImgUpload		: "העל�?ה",
DlgImgAlt			: "טקסט חלופי",
DlgImgWidth			: "רוחב",
DlgImgHeight		: "גובה",
DlgImgLockRatio		: "נעילת היחס",
DlgBtnResetSize		: "�?יפוס הגודל",
DlgImgBorder		: "מסגרת",
DlgImgHSpace		: "מרווח �?ופקי",
DlgImgVSpace		: "מרווח �?נכי",
DlgImgAlign			: "יישור",
DlgImgAlignLeft		: "לשמ�?ל",
DlgImgAlignAbsBottom: "לתחתית ה�?בסולוטית",
DlgImgAlignAbsMiddle: "מרכוז �?בסולוטי",
DlgImgAlignBaseline	: "לקו התחתית",
DlgImgAlignBottom	: "לתחתית",
DlgImgAlignMiddle	: "ל�?מצע",
DlgImgAlignRight	: "לימין",
DlgImgAlignTextTop	: "לר�?ש הטקסט",
DlgImgAlignTop		: "למעלה",
DlgImgPreview		: "תצוגה מקדימה",
DlgImgAlertUrl		: "נ�? להקליד �?ת כתובת התמונה",
DlgImgLinkTab		: "Link",	//MISSING

// Link Dialog
DlgLnkWindowTitle	: "קישור",
DlgLnkInfoTab		: "מידע על הקישור",
DlgLnkTargetTab		: "מטרה",

DlgLnkType			: "סוג קישור",
DlgLnkTypeURL		: "כתובת (URL)",
DlgLnkTypeAnchor	: "עוגן בעמוד זה",
DlgLnkTypeEMail		: "דו�?''ל",
DlgLnkProto			: "פרוטוקול",
DlgLnkProtoOther	: "&lt;�?חר&gt;",
DlgLnkURL			: "כתובת (URL)",
DlgLnkAnchorSel		: "בחירת עוגן",
DlgLnkAnchorByName	: "עפ''י ש�? העוגן",
DlgLnkAnchorById	: "עפ''י זיהוי (Id) הרכיב",
DlgLnkNoAnchors		: "&lt;�?ין עוגני�? זמיני�? בדף&gt;",
DlgLnkEMail			: "כתובת הדו�?''ל",
DlgLnkEMailSubject	: "נוש�? ההודעה",
DlgLnkEMailBody		: "גוף ההודעה",
DlgLnkUpload		: "העל�?ה",
DlgLnkBtnUpload		: "שליחה לשרת",

DlgLnkTarget		: "מטרה",
DlgLnkTargetFrame	: "&lt;frame&gt;",
DlgLnkTargetPopup	: "&lt;חלון קופץ&gt;",
DlgLnkTargetBlank	: "חלון חדש (_blank)",
DlgLnkTargetParent	: "חלון ה�?ב (_parent)",
DlgLnkTargetSelf	: "ב�?ותו החלון (_self)",
DlgLnkTargetTop		: "חלון ר�?שי (_top)",
DlgLnkTargetFrameName	: "Target Frame Name",	//MISSING
DlgLnkPopWinName	: "ש�? החלון הקופץ",
DlgLnkPopWinFeat	: "תכונות החלון הקופץ",
DlgLnkPopResize		: "בעל גודל ניתן לשינוי",
DlgLnkPopLocation	: "סרגל כתובת",
DlgLnkPopMenu		: "סרגל תפריט",
DlgLnkPopScroll		: "ניתן לגלילה",
DlgLnkPopStatus		: "סרגל חיווי",
DlgLnkPopToolbar	: "סרגל הכלי�?",
DlgLnkPopFullScrn	: "מסך מל�? (IE)",
DlgLnkPopDependent	: "תלוי (Netscape)",
DlgLnkPopWidth		: "רוחב",
DlgLnkPopHeight		: "גובה",
DlgLnkPopLeft		: "מיקו�? צד שמ�?ל",
DlgLnkPopTop		: "מיקו�? צד עליון",

DlnLnkMsgNoUrl		: "נ�? להקליד �?ת כתובת הקישור (URL)",
DlnLnkMsgNoEMail	: "נ�? להקליד �?ת כתובת הדו�?''ל",
DlnLnkMsgNoAnchor	: "נ�? לבחור עוגן במסמך",

// Color Dialog
DlgColorTitle		: "בחירת צבע",
DlgColorBtnClear	: "�?יפוס",
DlgColorHighlight	: "נוכחי",
DlgColorSelected	: "נבחר",

// Smiley Dialog
DlgSmileyTitle		: "הוספת סמיילי",

// Special Character Dialog
DlgSpecialCharTitle	: "בחירת תו מיוחד",

// Table Dialog
DlgTableTitle		: "תכונות טבלה",
DlgTableRows		: "שורות",
DlgTableColumns		: "עמודות",
DlgTableBorder		: "גודל מסגרת",
DlgTableAlign		: "יישור",
DlgTableAlignNotSet	: "<ל�? נקבע>",
DlgTableAlignLeft	: "שמ�?ל",
DlgTableAlignCenter	: "מרכז",
DlgTableAlignRight	: "ימין",
DlgTableWidth		: "רוחב",
DlgTableWidthPx		: "פיקסלי�?",
DlgTableWidthPc		: "�?חוז",
DlgTableHeight		: "גובה",
DlgTableCellSpace	: "מרווח ת�?",
DlgTableCellPad		: "ריפוד ת�?",
DlgTableCaption		: "כיתוב",

// Table Cell Dialog
DlgCellTitle		: "תכונות ת�?",
DlgCellWidth		: "רוחב",
DlgCellWidthPx		: "פיקסלי�?",
DlgCellWidthPc		: "�?חוז",
DlgCellHeight		: "גובה",
DlgCellWordWrap		: "גלילת שורות",
DlgCellWordWrapNotSet	: "<ל�? נקבע>",
DlgCellWordWrapYes	: "כן",
DlgCellWordWrapNo	: "ל�?",
DlgCellHorAlign		: "יישור �?ופקי",
DlgCellHorAlignNotSet	: "<ל�? נקבע>",
DlgCellHorAlignLeft	: "שמ�?ל",
DlgCellHorAlignCenter	: "מרכז",
DlgCellHorAlignRight: "ימין",
DlgCellVerAlign		: "יישור �?נכי",
DlgCellVerAlignNotSet	: "<ל�? נקבע>",
DlgCellVerAlignTop	: "למעלה",
DlgCellVerAlignMiddle	: "ל�?מצע",
DlgCellVerAlignBottom	: "לתחתית",
DlgCellVerAlignBaseline	: "קו תחתית",
DlgCellRowSpan		: "טווח שורות",
DlgCellCollSpan		: "טווח עמודות",
DlgCellBackColor	: "צבע רקע",
DlgCellBorderColor	: "צבע מסגרת",
DlgCellBtnSelect	: "בחירה...",

// Find Dialog
DlgFindTitle		: "חיפוש",
DlgFindFindBtn		: "חיפוש",
DlgFindNotFoundMsg	: "הטקסט המבוקש ל�? נמצ�?.",

// Replace Dialog
DlgReplaceTitle			: "החלפה",
DlgReplaceFindLbl		: "חיפוש מחרוזת:",
DlgReplaceReplaceLbl	: "החלפה במחרוזת:",
DlgReplaceCaseChk		: "הת�?מת סוג �?ותיות (Case)",
DlgReplaceReplaceBtn	: "החלפה",
DlgReplaceReplAllBtn	: "החלפה בכל העמוד",
DlgReplaceWordChk		: "הת�?מה למילה המל�?ה",

// Paste Operations / Dialog
PasteErrorPaste	: "הגדרות ה�?בטחה בדפדפן שלך ל�? מ�?פשרות לעורך לבצע פעולות הדבקה �?וטומטיות. יש להשתמש במקלדת לש�? כך (Ctrl+V).",
PasteErrorCut	: "הגדרות ה�?בטחה בדפדפן שלך ל�? מ�?פשרות לעורך לבצע פעולות גזירה  �?וטומטיות. יש להשתמש במקלדת לש�? כך (Ctrl+X).",
PasteErrorCopy	: "הגדרות ה�?בטחה בדפדפן שלך ל�? מ�?פשרות לעורך לבצע פעולות העתקה �?וטומטיות. יש להשתמש במקלדת לש�? כך (Ctrl+C).",

PasteAsText		: "הדבקה כטקסט פשוט",
PasteFromWord	: "הדבקה מ-Word",

DlgPasteMsg		: "העורך ל�? הצליח לבצע הדבקה �?וטומטית בגלל<STRONG>הגדרות ה�?בטחה</STRONG> של הדפדפן שלך.<BR>נ�? להדביק לתוך התיבה הב�?ה ב�?מצעות המקלדת (<STRONG>Ctrl+V</STRONG>) וללחוץ על <STRONG>�?ישור</STRONG>.",

// Color Picker
ColorAutomatic	: "�?וטומטי",
ColorMoreColors	: "צבעי�? נוספי�?...",

// Document Properties
DocProps		: "Document Properties",	//MISSING

// Anchor Dialog
DlgAnchorTitle		: "Anchor Properties",	//MISSING
DlgAnchorName		: "Anchor Name",	//MISSING
DlgAnchorErrorName	: "Please type the anchor name",	//MISSING

// Speller Pages Dialog
DlgSpellNotInDic		: "Not in dictionary",	//MISSING
DlgSpellChangeTo		: "Change to",	//MISSING
DlgSpellBtnIgnore		: "Ignore",	//MISSING
DlgSpellBtnIgnoreAll	: "Ignore All",	//MISSING
DlgSpellBtnReplace		: "Replace",	//MISSING
DlgSpellBtnReplaceAll	: "Replace All",	//MISSING
DlgSpellBtnUndo			: "Undo",	//MISSING
DlgSpellNoSuggestions	: "- No suggestions -",	//MISSING
DlgSpellProgress		: "Spell check in progress...",	//MISSING
DlgSpellNoMispell		: "Spell check complete: No misspellings found",	//MISSING
DlgSpellNoChanges		: "Spell check complete: No words changed",	//MISSING
DlgSpellOneChange		: "Spell check complete: One word changed",	//MISSING
DlgSpellManyChanges		: "Spell check complete: %1 words changed",	//MISSING

IeSpellDownload			: "Spell checker not installed. Do you want to download it now?",	//MISSING

// Button Dialog
DlgButtonText	: "Text (Value)",	//MISSING
DlgButtonType	: "Type",	//MISSING

// Checkbox and Radio Button Dialogs
DlgCheckboxName		: "Name",	//MISSING
DlgCheckboxValue	: "Value",	//MISSING
DlgCheckboxSelected	: "Selected",	//MISSING

// Form Dialog
DlgFormName		: "Name",	//MISSING
DlgFormAction	: "Action",	//MISSING
DlgFormMethod	: "Method",	//MISSING

// Select Field Dialog
DlgSelectName		: "Name",	//MISSING
DlgSelectValue		: "Value",	//MISSING
DlgSelectSize		: "Size",	//MISSING
DlgSelectLines		: "lines",	//MISSING
DlgSelectChkMulti	: "Allow multiple selections",	//MISSING
DlgSelectOpAvail	: "Available Options",	//MISSING
DlgSelectOpText		: "Text",	//MISSING
DlgSelectOpValue	: "Value",	//MISSING
DlgSelectBtnAdd		: "Add",	//MISSING
DlgSelectBtnModify	: "Modify",	//MISSING
DlgSelectBtnUp		: "Up",	//MISSING
DlgSelectBtnDown	: "Down",	//MISSING
DlgSelectBtnSetValue : "Set as selected value",	//MISSING
DlgSelectBtnDelete	: "Delete",	//MISSING

// Textarea Dialog
DlgTextareaName	: "Name",	//MISSING
DlgTextareaCols	: "Columns",	//MISSING
DlgTextareaRows	: "Rows",	//MISSING

// Text Field Dialog
DlgTextName			: "Name",	//MISSING
DlgTextValue		: "Value",	//MISSING
DlgTextCharWidth	: "Character Width",	//MISSING
DlgTextMaxChars		: "Maximum Characters",	//MISSING
DlgTextType			: "Type",	//MISSING
DlgTextTypeText		: "Text",	//MISSING
DlgTextTypePass		: "Password",	//MISSING

// Hidden Field Dialog
DlgHiddenName	: "Name",	//MISSING
DlgHiddenValue	: "Value",	//MISSING

// Bulleted List Dialog
BulletedListProp	: "Bulleted List Properties",	//MISSING
NumberedListProp	: "Numbered List Properties",	//MISSING
DlgLstType			: "Type",	//MISSING
DlgLstTypeCircle	: "Circle",	//MISSING
DlgLstTypeDisk		: "Disk",	//MISSING
DlgLstTypeSquare	: "Square",	//MISSING
DlgLstTypeNumbers	: "Numbers (1, 2, 3)",	//MISSING
DlgLstTypeLCase		: "Lowercase Letters (a, b, c)",	//MISSING
DlgLstTypeUCase		: "Uppercase Letters (A, B, C)",	//MISSING
DlgLstTypeSRoman	: "Small Roman Numerals (i, ii, iii)",	//MISSING
DlgLstTypeLRoman	: "Large Roman Numerals (I, II, III)",	//MISSING

// Document Properties Dialog
DlgDocGeneralTab	: "General",	//MISSING
DlgDocBackTab		: "Background",	//MISSING
DlgDocColorsTab		: "Colors and Margins",	//MISSING
DlgDocMetaTab		: "Meta Data",	//MISSING

DlgDocPageTitle		: "Page Title",	//MISSING
DlgDocLangDir		: "Language Direction",	//MISSING
DlgDocLangDirLTR	: "Left to Right (LTR)",	//MISSING
DlgDocLangDirRTL	: "Right to Left (RTL)",	//MISSING
DlgDocLangCode		: "Language Code",	//MISSING
DlgDocCharSet		: "Character Set Encoding",	//MISSING
DlgDocCharSetOther	: "Other Character Set Encoding",	//MISSING

DlgDocDocType		: "Document Type Heading",	//MISSING
DlgDocDocTypeOther	: "Other Document Type Heading",	//MISSING
DlgDocIncXHTML		: "Include XHTML Declarations",	//MISSING
DlgDocBgColor		: "Background Color",	//MISSING
DlgDocBgImage		: "Background Image URL",	//MISSING
DlgDocBgNoScroll	: "Nonscrolling Background",	//MISSING
DlgDocCText			: "Text",	//MISSING
DlgDocCLink			: "Link",	//MISSING
DlgDocCVisited		: "Visited Link",	//MISSING
DlgDocCActive		: "Active Link",	//MISSING
DlgDocMargins		: "Page Margins",	//MISSING
DlgDocMaTop			: "Top",	//MISSING
DlgDocMaLeft		: "Left",	//MISSING
DlgDocMaRight		: "Right",	//MISSING
DlgDocMaBottom		: "Bottom",	//MISSING
DlgDocMeIndex		: "Document Indexing Keywords (comma separated)",	//MISSING
DlgDocMeDescr		: "Document Description",	//MISSING
DlgDocMeAuthor		: "Author",	//MISSING
DlgDocMeCopy		: "Copyright",	//MISSING
DlgDocPreview		: "Preview",	//MISSING

// Templates Dialog
Templates			: "Templates",	//MISSING
DlgTemplatesTitle	: "Content Templates",	//MISSING
DlgTemplatesSelMsg	: "Please select the template to open in the editor<br>(the actual contents will be lost):",	//MISSING
DlgTemplatesLoading	: "Loading templates list. Please wait...",	//MISSING
DlgTemplatesNoTpl	: "(No templates defined)",	//MISSING

// About Dialog
DlgAboutAboutTab	: "About",	//MISSING
DlgAboutBrowserInfoTab	: "Browser Info",	//MISSING
DlgAboutVersion		: "גירס�?",
DlgAboutLicense		: "ברשיון תחת תנ�?י GNU Lesser General Public License",
DlgAboutInfo		: "מידע נוסף ניתן למצו�? כ�?ן:"
}