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
 * File Name: zh.js
 * 	Chinese Traditional language file.
 * 
 * File Authors:
 * 		NetRube (NetRube@126.com)
 */

var FCKLang =
{
// Language direction : "ltr" (left to right) or "rtl" (right to left).
Dir					: "ltr",

ToolbarCollapse		: "折疊工具欄",
ToolbarExpand		: "展開工具欄",

// Toolbar Items and Context Menu
Save				: "儲存",
NewPage				: "新建",
Preview				: "�?覽",
Cut					: "剪切",
Copy				: "拷�?",
Paste				: "粘貼",
PasteText			: "粘貼為無格�?文本",
PasteWord			: "從 MS Word 粘貼",
Print				: "列�?�",
SelectAll			: "全�?�",
RemoveFormat		: "清除格�?",
InsertLinkLbl		: "超�?��?",
InsertLink			: "�?�入/編輯超�?��?",
RemoveLink			: "�?�消超�?��?",
Anchor				: "�?�入/編輯錨點�?��?",
InsertImageLbl		: "圖�?",
InsertImage			: "�?�入/編輯圖�?",
InsertTableLbl		: "表格",
InsertTable			: "�?�入/編輯表格",
InsertLineLbl		: "水平線",
InsertLine			: "�?�入水平線",
InsertSpecialCharLbl: "特殊符號",
InsertSpecialChar	: "�?�入特殊符號",
InsertSmileyLbl		: "圖釋",
InsertSmiley		: "�?�入圖釋",
About				: "關於 FCKeditor",
Bold				: "加粗",
Italic				: "傾斜",
Underline			: "下劃線",
StrikeThrough		: "刪除線",
Subscript			: "下標",
Superscript			: "上標",
LeftJustify			: "左�?齊",
CenterJustify		: "居中�?齊",
RightJustify		: "�?��?齊",
BlockJustify		: "兩端�?齊",
DecreaseIndent		: "減少縮進�?",
IncreaseIndent		: "增加縮進�?",
Undo				: "撤銷",
Redo				: "�?�?�",
NumberedListLbl		: "編號列表",
NumberedList		: "�?�入/刪除編號列表",
BulletedListLbl		: "項目列表",
BulletedList		: "�?�入/刪除項目列表",
ShowTableBorders	: "顯示表格邊框",
ShowDetails			: "顯示詳細資料",
Style				: "樣�?",
FontFormat			: "格�?",
Font				: "字體",
FontSize			: "尺寸",
TextColor			: "文本�?色",
BGColor				: "背景�?色",
Source				: "代碼",
Find				: "查找",
Replace				: "替�?�",
SpellCheck			: "拼寫檢查",
UniversalKeyboard	: "軟�?�盤",

Form			: "表單",
Checkbox		: "核�?�方塊",
RadioButton		: "單�?�按鈕",
TextField		: "單行文本",
Textarea		: "多行文本",
HiddenField		: "隱�?域",
Button			: "按鈕",
SelectionField	: "列表/�?�單",
ImageButton		: "圖�?域",

// Context Menu
EditLink			: "編輯超�?��?",
InsertRow			: "�?�入行",
DeleteRows			: "刪除行",
InsertColumn		: "�?�入列",
DeleteColumns		: "刪除列",
InsertCell			: "�?�入單格",
DeleteCells			: "刪除單格",
MergeCells			: "�?�併單格",
SplitCell			: "拆分單格",
CellProperties		: "單格屬性",
TableProperties		: "表格屬性",
ImageProperties		: "圖�?屬性",

AnchorProp			: "錨點�?��?屬性",
ButtonProp			: "按鈕屬性",
CheckboxProp		: "核�?�方塊屬性",
HiddenFieldProp		: "隱�?域屬性",
RadioButtonProp		: "單�?�按鈕屬性",
ImageButtonProp		: "圖�?域屬性",
TextFieldProp		: "單行文本屬性",
SelectionFieldProp	: "功能表/列表屬性",
TextareaProp		: "多行文本屬性",
FormProp			: "表單屬性",

FontFormats			: "普通;帶格�?的;地�?�;標題 1;標題 2;標題 3;標題 4;標題 5;標題 6;段�?�(DIV)",

// Alerts and Messages
ProcessingXHTML		: "正在處�?� XHTML，請�?等...",
Done				: "完�?",
PasteWordConfirm	: "您�?粘貼的內容好�?是來自 MS Word，是�?��?清除 MS Word 格�?後�?粘貼？",
NotCompatiblePaste	: "該命令需�? Internet Explorer 5.5 或更高版本的支�?，是�?�按常�?粘貼進行？",
UnknownToolbarItem	: "未知工具欄項目 \"%1\"",
UnknownCommand		: "未知命令�??稱 \"%1\"",
NotImplemented		: "命令無法執行",
UnknownToolbarSet	: "工具欄設置 \"%1\" �?存在",

// Dialogs
DlgBtnOK			: "確定",
DlgBtnCancel		: "�?�消",
DlgBtnClose			: "關閉",
DlgBtnBrowseServer	: "�?覽伺�?器",
DlgAdvancedTag		: "進階",
DlgOpOther			: "&lt;其他&gt;",

// General Dialogs Labels
DlgGenNotSet		: "&lt;沒有設置&gt;",
DlgGenId			: "ID",
DlgGenLangDir		: "語言方�?�",
DlgGenLangDirLtr	: "自左到�?� (LTR)",
DlgGenLangDirRtl	: "自�?�到左 (RTL)",
DlgGenLangCode		: "語言代碼",
DlgGenAccessKey		: "訪�?�?�",
DlgGenName			: "�??稱",
DlgGenTabIndex		: "Tab �?�次�?",
DlgGenLongDescr		: "詳細說明地�?�",
DlgGenClass			: "樣�?類",
DlgGenTitle			: "標題",
DlgGenContType		: "類型",
DlgGenLinkCharset	: "編碼",
DlgGenStyle			: "樣�?",

// Image Dialog
DlgImgTitle			: "圖�?屬性",
DlgImgInfoTab		: "圖�?",
DlgImgBtnUpload		: "發�?到伺�?器上",
DlgImgURL			: "�?檔案",
DlgImgUpload		: "上載",
DlgImgAlt			: "替�?�文本",
DlgImgWidth			: "寬度",
DlgImgHeight		: "高度",
DlgImgLockRatio		: "鎖定比例",
DlgBtnResetSize		: "�?�復尺寸",
DlgImgBorder		: "邊框尺寸",
DlgImgHSpace		: "水準間�?",
DlgImgVSpace		: "垂直間�?",
DlgImgAlign			: "�?齊方�?",
DlgImgAlignLeft		: "左�?齊",
DlgImgAlignAbsBottom: "絕�?底邊",
DlgImgAlignAbsMiddle: "絕�?居中",
DlgImgAlignBaseline	: "基線",
DlgImgAlignBottom	: "底邊",
DlgImgAlignMiddle	: "居中",
DlgImgAlignRight	: "�?��?齊",
DlgImgAlignTextTop	: "文本上方",
DlgImgAlignTop		: "頂端",
DlgImgPreview		: "�?覽",
DlgImgAlertUrl		: "請輸入圖�?�?�?�",
DlgImgLinkTab		: "Link",	//MISSING

// Link Dialog
DlgLnkWindowTitle	: "超�?��?",
DlgLnkInfoTab		: "超�?��?資訊",
DlgLnkTargetTab		: "目標",

DlgLnkType			: "超�?��?類型",
DlgLnkTypeURL		: "網�?�",
DlgLnkTypeAnchor	: "�?內錨點�?��?",
DlgLnkTypeEMail		: "電�?郵件",
DlgLnkProto			: "�?�議",
DlgLnkProtoOther	: "&lt;其他&gt;",
DlgLnkURL			: "地�?�",
DlgLnkAnchorSel		: "�?�擇一個錨點",
DlgLnkAnchorByName	: "按錨點�??稱",
DlgLnkAnchorById	: "按錨點 ID",
DlgLnkNoAnchors		: "&lt;此文檔沒有�?�用的錨點&gt;",
DlgLnkEMail			: "地�?�",
DlgLnkEMailSubject	: "主題",
DlgLnkEMailBody		: "內容",
DlgLnkUpload		: "上载",
DlgLnkBtnUpload		: "發�?到伺�?器上",

DlgLnkTarget		: "目標",
DlgLnkTargetFrame	: "&lt;框架&gt;",
DlgLnkTargetPopup	: "&lt;彈出窗�?�&gt;",
DlgLnkTargetBlank	: "新窗�?� (_blank)",
DlgLnkTargetParent	: "父窗�?� (_parent)",
DlgLnkTargetSelf	: "本窗�?� (_self)",
DlgLnkTargetTop		: "整�? (_top)",
DlgLnkTargetFrameName	: "目標框架�??稱",
DlgLnkPopWinName	: "彈出視窗�??稱",
DlgLnkPopWinFeat	: "彈出視窗屬性",
DlgLnkPopResize		: "調整大�?",
DlgLnkPopLocation	: "地�?�欄",
DlgLnkPopMenu		: "�?�單欄",
DlgLnkPopScroll		: "�?�軸",
DlgLnkPopStatus		: "狀態欄",
DlgLnkPopToolbar	: "工具欄",
DlgLnkPopFullScrn	: "全�? (IE)",
DlgLnkPopDependent	: "�?附 (NS)",
DlgLnkPopWidth		: "寬",
DlgLnkPopHeight		: "高",
DlgLnkPopLeft		: "左",
DlgLnkPopTop		: "�?�",

DlnLnkMsgNoUrl		: "請輸入超�?��?�?�?�",
DlnLnkMsgNoEMail	: "請輸入電�?郵件�?�?�",
DlnLnkMsgNoAnchor	: "請�?�擇一個錨點",

// Color Dialog
DlgColorTitle		: "�?�擇�?色",
DlgColorBtnClear	: "清除",
DlgColorHighlight	: "�?覽",
DlgColorSelected	: "�?�擇",

// Smiley Dialog
DlgSmileyTitle		: "�?�入一個圖釋",

// Special Character Dialog
DlgSpecialCharTitle	: "�?�擇特殊符號",

// Table Dialog
DlgTableTitle		: "表格屬性",
DlgTableRows		: "行數",
DlgTableColumns		: "列數",
DlgTableBorder		: "邊框",
DlgTableAlign		: "�?齊",
DlgTableAlignNotSet	: "&lt;沒有設置&gt;",
DlgTableAlignLeft	: "左�?齊",
DlgTableAlignCenter	: "居中",
DlgTableAlignRight	: "�?��?齊",
DlgTableWidth		: "寬度",
DlgTableWidthPx		: "圖元",
DlgTableWidthPc		: "百分比",
DlgTableHeight		: "高度",
DlgTableCellSpace	: "間�?",
DlgTableCellPad		: "邊�?",
DlgTableCaption		: "標題",

// Table Cell Dialog
DlgCellTitle		: "單格屬性",
DlgCellWidth		: "寬度",
DlgCellWidthPx		: "圖元",
DlgCellWidthPc		: "百分比",
DlgCellHeight		: "高度",
DlgCellWordWrap		: "自動�?�行",
DlgCellWordWrapNotSet	: "&lt;沒有設置&gt;",
DlgCellWordWrapYes	: "是",
DlgCellWordWrapNo	: "�?�",
DlgCellHorAlign		: "水準�?齊",
DlgCellHorAlignNotSet	: "&lt;沒有設置&gt;",
DlgCellHorAlignLeft	: "左�?齊",
DlgCellHorAlignCenter	: "居中",
DlgCellHorAlignRight: "�?��?齊",
DlgCellVerAlign		: "垂直�?齊",
DlgCellVerAlignNotSet	: "&lt;沒有設置&gt;",
DlgCellVerAlignTop	: "頂端",
DlgCellVerAlignMiddle	: "居中",
DlgCellVerAlignBottom	: "底部",
DlgCellVerAlignBaseline	: "基線",
DlgCellRowSpan		: "縱跨行數",
DlgCellCollSpan		: "橫跨列數",
DlgCellBackColor	: "背景�?色",
DlgCellBorderColor	: "邊框�?色",
DlgCellBtnSelect	: "�?�擇...",

// Find Dialog
DlgFindTitle		: "查找",
DlgFindFindBtn		: "查找",
DlgFindNotFoundMsg	: "指定文本沒有找到。",

// Replace Dialog
DlgReplaceTitle			: "替�?�",
DlgReplaceFindLbl		: "查找:",
DlgReplaceReplaceLbl	: "替�?�:",
DlgReplaceCaseChk		: "�?�分大�?寫",
DlgReplaceReplaceBtn	: "替�?�",
DlgReplaceReplAllBtn	: "全部替�?�",
DlgReplaceWordChk		: "全字匹�?",

// Paste Operations / Dialog
PasteErrorPaste	: "您的�?覽器安全設置�?�?許編輯器自動執行粘貼�?作，請使用�?�盤快�?��?�(Ctrl+V)來完�?。",
PasteErrorCut	: "您的�?覽器安全設置�?�?許編輯器自動執行剪切�?作，請使用�?�盤快�?��?�(Ctrl+X)來完�?。",
PasteErrorCopy	: "您的�?覽器安全設置�?�?許編輯器自動執行複製�?作，請使用�?�盤快�?��?�(Ctrl+C)來完�?。",

PasteAsText		: "粘貼為無格�?文本",
PasteFromWord	: "從 MS Word 粘貼",

DlgPasteMsg		: "因為您的�?覽器編輯器 <STRONG>安全設置</STRONG> 原因，�?能自動執行粘貼。<BR>請使用�?�盤快�?��?�(<STRONG>Ctrl+V</STRONG>)粘貼到下�?�並按 <STRONG>確定</STRONG>。",

// Color Picker
ColorAutomatic	: "自動",
ColorMoreColors	: "其他�?色...",

// Document Properties
DocProps		: "�?�?�屬性",

// Anchor Dialog
DlgAnchorTitle		: "命�??錨點",
DlgAnchorName		: "錨點�??稱",
DlgAnchorErrorName	: "請輸入錨點�??稱",

// Speller Pages Dialog
DlgSpellNotInDic		: "沒有在字典�?",
DlgSpellChangeTo		: "更改為",
DlgSpellBtnIgnore		: "忽略",
DlgSpellBtnIgnoreAll	: "全部忽略",
DlgSpellBtnReplace		: "替�?�",
DlgSpellBtnReplaceAll	: "全部替�?�",
DlgSpellBtnUndo			: "撤銷",
DlgSpellNoSuggestions	: "- 沒有建議 -",
DlgSpellProgress		: "正在進行拼寫檢查...",
DlgSpellNoMispell		: "拼寫檢查完�?：沒有發�?�拼寫錯誤",
DlgSpellNoChanges		: "拼寫檢查完�?：沒有更改任何單詞",
DlgSpellOneChange		: "拼寫檢查完�?：更改了一個單詞",
DlgSpellManyChanges		: "拼寫檢查完�?：更改了 %1 個單詞",

IeSpellDownload			: "拼寫檢查�?�件還沒安�?，你是�?�想�?�在就下載？",

// Button Dialog
DlgButtonText	: "標籤(值)",
DlgButtonType	: "類型",

// Checkbox and Radio Button Dialogs
DlgCheckboxName		: "�??稱",
DlgCheckboxValue	: "�?�定值",
DlgCheckboxSelected	: "已勾�?�",

// Form Dialog
DlgFormName		: "�??稱",
DlgFormAction	: "動作",
DlgFormMethod	: "方法",

// Select Field Dialog
DlgSelectName		: "�??稱",
DlgSelectValue		: "�?�定",
DlgSelectSize		: "高度",
DlgSelectLines		: "行",
DlgSelectChkMulti	: "�?許多�?�",
DlgSelectOpAvail	: "列表值",
DlgSelectOpText		: "標籤",
DlgSelectOpValue	: "值",
DlgSelectBtnAdd		: "新增",
DlgSelectBtnModify	: "修改",
DlgSelectBtnUp		: "上移",
DlgSelectBtnDown	: "下移",
DlgSelectBtnSetValue : "設為�?始化時�?�定",
DlgSelectBtnDelete	: "移除",

// Textarea Dialog
DlgTextareaName	: "�??稱",
DlgTextareaCols	: "字元寬度",
DlgTextareaRows	: "行數",

// Text Field Dialog
DlgTextName			: "�??稱",
DlgTextValue		: "值",
DlgTextCharWidth	: "字元寬度",
DlgTextMaxChars		: "最多字元數",
DlgTextType			: "類型",
DlgTextTypeText		: "文本",
DlgTextTypePass		: "密碼",

// Hidden Field Dialog
DlgHiddenName	: "�??稱",
DlgHiddenValue	: "值",

// Bulleted List Dialog
BulletedListProp	: "項目列表屬性",
NumberedListProp	: "編號列表屬性",
DlgLstType			: "列表類型",
DlgLstTypeCircle	: "圓圈",
DlgLstTypeDisk		: "圓點",
DlgLstTypeSquare	: "方塊",
DlgLstTypeNumbers	: "數字 (1, 2, 3)",
DlgLstTypeLCase		: "�?寫字�? (a, b, c)",
DlgLstTypeUCase		: "大寫字�? (A, B, C)",
DlgLstTypeSRoman	: "�?寫羅馬數字 (i, ii, iii)",
DlgLstTypeLRoman	: "大寫羅馬數字 (I, II, III)",

// Document Properties Dialog
DlgDocGeneralTab	: "常�?",
DlgDocBackTab		: "背景",
DlgDocColorsTab		: "�?色和邊�?",
DlgDocMetaTab		: "Meta 資料",

DlgDocPageTitle		: "�?�?�標題",
DlgDocLangDir		: "語言方�?�",
DlgDocLangDirLTR	: "從左到�?� (LTR)",
DlgDocLangDirRTL	: "從�?�到左 (RTL)",
DlgDocLangCode		: "語言代碼",
DlgDocCharSet		: "字元編碼",
DlgDocCharSetOther	: "其他字元編碼",

DlgDocDocType		: "文檔類型",
DlgDocDocTypeOther	: "其他文檔類型",
DlgDocIncXHTML		: "包�?� XHTML �?�明",
DlgDocBgColor		: "背景�?色",
DlgDocBgImage		: "背景圖�?",
DlgDocBgNoScroll	: "�?滾動背景圖�?",
DlgDocCText			: "文本",
DlgDocCLink			: "超�?��?",
DlgDocCVisited		: "已訪�?的超�?��?",
DlgDocCActive		: "活動超�?��?",
DlgDocMargins		: "�?�?�邊�?",
DlgDocMaTop			: "上",
DlgDocMaLeft		: "左",
DlgDocMaRight		: "�?�",
DlgDocMaBottom		: "下",
DlgDocMeIndex		: "�?�?�索引關�?�字 (用�?�形逗號[,]分隔)",
DlgDocMeDescr		: "�?�?�說明",
DlgDocMeAuthor		: "作者",
DlgDocMeCopy		: "版權",
DlgDocPreview		: "�?覽",

// Templates Dialog
Templates			: "Templates",	//MISSING
DlgTemplatesTitle	: "Content Templates",	//MISSING
DlgTemplatesSelMsg	: "Please select the template to open in the editor<br>(the actual contents will be lost):",	//MISSING
DlgTemplatesLoading	: "Loading templates list. Please wait...",	//MISSING
DlgTemplatesNoTpl	: "(No templates defined)",	//MISSING

// About Dialog
DlgAboutAboutTab	: "關於",
DlgAboutBrowserInfoTab	: "�?覽器資訊",
DlgAboutVersion		: "版本",
DlgAboutLicense		: "基於 GNU 通用公共許�?�證授權發佈 ",
DlgAboutInfo		: "�?�?�得更多資訊請訪�? "
}