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
 * File Name: fa.js
 * 	Persian language file.
 * 
 * File Authors:
 * 		Hamed Taj-Abadi (hamed@ranginkaman.com)
 */

var FCKLang =
{
// Language direction : "ltr" (left to right) or "rtl" (right to left).
Dir					: "rtl",

ToolbarCollapse		: "بستن منوابزار",
ToolbarExpand		: "بازکردن منوابزار",

// Toolbar Items and Context Menu
Save				: "ذخيره",
NewPage				: "جديد",
Preview				: "پيش نمايش",
Cut					: "برش",
Copy				: "کپی",
Paste				: "چسباندن",
PasteText			: "چسباندن به عنوان متن ساده",
PasteWord			: "چسباندن از WORD",
Print				: "چاپ",
SelectAll			: "انتخاب همه",
RemoveFormat		: "برداشتن �?رمت",
InsertLinkLbl		: "لينک",
InsertLink			: "درج/ويرايش لينک",
RemoveLink			: "برداشتن لينک",
Anchor				: "درج/ويرايش لنگر",
InsertImageLbl		: "تصوير",
InsertImage			: "درج/ويرايش تصوير",
InsertTableLbl		: "جدول",
InsertTable			: "درج/ويرايش جدول",
InsertLineLbl		: "خط",
InsertLine			: "درج خط ا�?قی",
InsertSpecialCharLbl: "شناسه ويژه",
InsertSpecialChar	: "درج شناسه ويژه",
InsertSmileyLbl		: "خندانک",
InsertSmiley		: "درج خندانک",
About				: "درباره FCKeditor",
Bold				: "پررنگ",
Italic				: "ايتاليک",
Underline			: "زيرخط",
StrikeThrough		: "ميان خط",
Subscript			: "انديس پائين",
Superscript			: "انديس بالا",
LeftJustify			: "چپ چين",
CenterJustify		: "وسط چين",
RightJustify		: "راست چين",
BlockJustify		: "بلوک چين",
DecreaseIndent		: "کاهش تور�?تگی",
IncreaseIndent		: "ا�?زايش تور�?تگی",
Undo				: "واچيدن",
Redo				: "بازچيدن",
NumberedListLbl		: "�?هرست عددی",
NumberedList		: "درج/برداشتن �?هرست عددی",
BulletedListLbl		: "�?هرست نقطه ای",
BulletedList		: "درج/برداشتن �?هرست نقطه ای",
ShowTableBorders	: "نمايش لبه جدول",
ShowDetails			: "نمايش جزئيات",
Style				: "سبک",
FontFormat			: "�?رمت",
Font				: "قلم",
FontSize			: "اندازه",
TextColor			: "رنگ متن",
BGColor				: "رنگ پس زمينه",
Source				: "منبع",
Find				: "جستجو",
Replace				: "جايگزينی",
SpellCheck			: "کنترل املا",
UniversalKeyboard	: "ص�?حه کليد جهانی",

Form			: "�?رم",
Checkbox		: "دکمه گزينه ای",
RadioButton		: "دکمه راديويی",
TextField		: "�?يلد متنی",
Textarea		: "ناحيه متنی",
HiddenField		: "�?يلد پنهان",
Button			: "دکمه",
SelectionField	: "�?يلد انتخابی",
ImageButton		: "دکمه تصويری",

// Context Menu
EditLink			: "ويرايش لينک",
InsertRow			: "درج سطر",
DeleteRows			: "حذ�? سطرها",
InsertColumn		: "درج ستون",
DeleteColumns		: "حذ�? ستونها",
InsertCell			: "درج سلول",
DeleteCells			: "حذ�? سلولها",
MergeCells			: "ادغام سلولها",
SplitCell			: "ت�?کيک سلول",
CellProperties		: "ويژگيهای سلول",
TableProperties		: "ويژگيهای جدول",
ImageProperties		: "ويژگيهای تصوير",

AnchorProp			: "ويژگيهای لنگر",
ButtonProp			: "ويژگيهای دکمه",
CheckboxProp		: "ويژگيهای دکمه گزينه ای",
HiddenFieldProp		: "ويژگيهای �?يلد پنهان",
RadioButtonProp		: "ويژگيهای دکمه راديويی",
ImageButtonProp		: "ويژگيهای دکمه تصويری",
TextFieldProp		: "ويژگيهای �?يلد متنی",
SelectionFieldProp	: "ويژگيهای �?يلد انتخابی",
TextareaProp		: "ويژگيهای ناحيه متنی",
FormProp			: "ويژگيهای �?رم",

FontFormats			: "نرمال;�?رمت شده;آدرس;سرنويس 1;سرنويس 2;سرنويس 3;سرنويس 4;سرنويس 5;سرنويس 6;بند;(DIV)",

// Alerts and Messages
ProcessingXHTML		: "پردازش XHTML. لط�?ا صبر کنيد...",
Done				: "انجام شد",
PasteWordConfirm	: "متنی که می خواهيد بچسبانيد به نظر از WORD کپی شده است. آيا مايليد قبل از چسباندن آنرا تميز کنيد؟ ",
NotCompatiblePaste	: "اين �?رمان برای مرورگر Internet Explorer از نگارش 5.5 يا بالاتر در دسترس است. آيا مايليد بدون تميز کردن متن را بچسبانيد؟",
UnknownToolbarItem	: "�?قره منوابزار ناشناخته \"%1\"",
UnknownCommand		: "نام دستور ناشناخته \"%1\"",
NotImplemented		: "دستور اجرا نشد",
UnknownToolbarSet	: "مجموعه منوابزار \"%1\" وجود ندارد",

// Dialogs
DlgBtnOK			: "تائيد",
DlgBtnCancel		: "انصرا�?",
DlgBtnClose			: "بستن",
DlgBtnBrowseServer	: "�?هرست نمايی سرور",
DlgAdvancedTag		: "پيشر�?ته",
DlgOpOther			: "&lt;غيره&gt;",

// General Dialogs Labels
DlgGenNotSet		: "&lt;تعين نشده&gt;",
DlgGenId			: "کد",
DlgGenLangDir		: "جهت نمای زبان",
DlgGenLangDirLtr	: "چپ به راست (LTR)",
DlgGenLangDirRtl	: "راست به چپ (RTL)",
DlgGenLangCode		: "کد زبان",
DlgGenAccessKey		: "کليد دستيابی",
DlgGenName			: "نام",
DlgGenTabIndex		: "انديس برگه",
DlgGenLongDescr		: "URL توضيح طولانی",
DlgGenClass			: "کلاسهای استايل شيت",
DlgGenTitle			: "عنوان کمکی",
DlgGenContType		: "نوع محتوی کمکی",
DlgGenLinkCharset	: "مجموعه نويسه منبع لينک شده",
DlgGenStyle			: "سبک",

// Image Dialog
DlgImgTitle			: "ويژگيهای تصوير",
DlgImgInfoTab		: "اطلاعات تصوير",
DlgImgBtnUpload		: "به سرور ارسال کن",
DlgImgURL			: "URL",
DlgImgUpload		: "انتقال به سرور",
DlgImgAlt			: "متن جايگزين",
DlgImgWidth			: "پهنا",
DlgImgHeight		: "درازا",
DlgImgLockRatio		: "ق�?ل کردن نسبت",
DlgBtnResetSize		: "بازنشانی اندازه",
DlgImgBorder		: "لبه",
DlgImgHSpace		: "�?اصله ا�?قی",
DlgImgVSpace		: "�?اصله عمودی",
DlgImgAlign			: "چينش",
DlgImgAlignLeft		: "چپ",
DlgImgAlignAbsBottom: "پائين مطلق",
DlgImgAlignAbsMiddle: "وسط مطلق",
DlgImgAlignBaseline	: "خط پايه",
DlgImgAlignBottom	: "پائين",
DlgImgAlignMiddle	: "وسط",
DlgImgAlignRight	: "راست",
DlgImgAlignTextTop	: "متن بالا",
DlgImgAlignTop		: "بالا",
DlgImgPreview		: "پيش نمايش",
DlgImgAlertUrl		: "لط�?ا URL تصوير را انتخاب کنيد",
DlgImgLinkTab		: "لينک",

// Link Dialog
DlgLnkWindowTitle	: "لينک",
DlgLnkInfoTab		: "اطلاعات لينک",
DlgLnkTargetTab		: "مقصد",

DlgLnkType			: "نوع لينک",
DlgLnkTypeURL		: "URL",
DlgLnkTypeAnchor	: "پيوند در ص�?حه جاری",
DlgLnkTypeEMail		: "پست الکترونيکی",
DlgLnkProto			: "پروتکل",
DlgLnkProtoOther	: "&lt;غيره&gt;",
DlgLnkURL			: "URL",
DlgLnkAnchorSel		: "يک پيوند انتخاب کنيد",
DlgLnkAnchorByName	: "با نام پيوند",
DlgLnkAnchorById	: "با کد المان",
DlgLnkNoAnchors		: "&lt;در اين سند پيوندی موجود نيست&gt;",
DlgLnkEMail			: "آدرس پست الکترونيکی",
DlgLnkEMailSubject	: "موضوع پيام",
DlgLnkEMailBody		: "متن پيام",
DlgLnkUpload		: "انتقال به سرور",
DlgLnkBtnUpload		: "به سرور ارسال کن",

DlgLnkTarget		: "مقصد",
DlgLnkTargetFrame	: "&lt;�?ريم&gt;",
DlgLnkTargetPopup	: "&lt;پنجره پاپاپ&gt;",
DlgLnkTargetBlank	: "پنجره جديد (_blank)",
DlgLnkTargetParent	: "پنجره والد (_parent)",
DlgLnkTargetSelf	: "همان پنجره (_self)",
DlgLnkTargetTop		: "بالاترين پنجره (_top)",
DlgLnkTargetFrameName	: "نام �?ريم مقصد",
DlgLnkPopWinName	: "نام پنجره پاپاپ",
DlgLnkPopWinFeat	: "خصوصيات پنجره پاپاپ",
DlgLnkPopResize		: "قابل تغيراندازه",
DlgLnkPopLocation	: "نوار موقعيت",
DlgLnkPopMenu		: "نوار منو",
DlgLnkPopScroll		: "نوارهای طومار",
DlgLnkPopStatus		: "نوار وضعيت",
DlgLnkPopToolbar	: "نوارابزار",
DlgLnkPopFullScrn	: "تمام ص�?حه (IE)",
DlgLnkPopDependent	: "وابسته (Netscape)",
DlgLnkPopWidth		: "پهنا",
DlgLnkPopHeight		: "درازا",
DlgLnkPopLeft		: "موقعيت چپ",
DlgLnkPopTop		: "موقعيت بالا",

DlnLnkMsgNoUrl		: "لط�?ا URL لينک را وارد کنيد",
DlnLnkMsgNoEMail	: "لط�?ا آدرس ايميل را وارد کنيد",
DlnLnkMsgNoAnchor	: "لط�?ا پيوندی انتخاب کنيد",

// Color Dialog
DlgColorTitle		: "انتخاب رنگ",
DlgColorBtnClear	: "پاک کردن",
DlgColorHighlight	: "هايلايت",
DlgColorSelected	: "انتخاب شده",

// Smiley Dialog
DlgSmileyTitle		: "درج  خندانک",

// Special Character Dialog
DlgSpecialCharTitle	: "انتخاب شناسه های ويژه",

// Table Dialog
DlgTableTitle		: "ويژگيهای جدول",
DlgTableRows		: "سطرها",
DlgTableColumns		: "ستونها",
DlgTableBorder		: "اندازه لبه",
DlgTableAlign		: "چينش",
DlgTableAlignNotSet	: "<تعين نشده>",
DlgTableAlignLeft	: "چپ",
DlgTableAlignCenter	: "وسط",
DlgTableAlignRight	: "راست",
DlgTableWidth		: "پهنا",
DlgTableWidthPx		: "پيکسل",
DlgTableWidthPc		: "درصد",
DlgTableHeight		: "درازا",
DlgTableCellSpace	: "�?اصله ميان سلولها",
DlgTableCellPad		: "�?اصله پرشده در سلول",
DlgTableCaption		: "عنوان",

// Table Cell Dialog
DlgCellTitle		: "ويژگيهای سلول",
DlgCellWidth		: "پهنا",
DlgCellWidthPx		: "پيکسل",
DlgCellWidthPc		: "درصد",
DlgCellHeight		: "درازا",
DlgCellWordWrap		: "شکستن کلمات",
DlgCellWordWrapNotSet	: "&lt;تعين نشده&gt;",
DlgCellWordWrapYes	: "بله",
DlgCellWordWrapNo	: "خير",
DlgCellHorAlign		: "چينش ا�?قی",
DlgCellHorAlignNotSet	: "&lt;تعين نشده&gt;",
DlgCellHorAlignLeft	: "چپ",
DlgCellHorAlignCenter	: "وسط",
DlgCellHorAlignRight: "راست",
DlgCellVerAlign		: "چينش عمودی",
DlgCellVerAlignNotSet	: "&lt;تعين نشده&gt;",
DlgCellVerAlignTop	: "بالا",
DlgCellVerAlignMiddle	: "ميان",
DlgCellVerAlignBottom	: "پائين",
DlgCellVerAlignBaseline	: "خط پايه",
DlgCellRowSpan		: "گستردگی سطرها",
DlgCellCollSpan		: "گستردگی ستونها",
DlgCellBackColor	: "رنگ پس زمينه",
DlgCellBorderColor	: "رنگ لبه",
DlgCellBtnSelect	: "انتخاب کنيد...",

// Find Dialog
DlgFindTitle		: "يا�?تن",
DlgFindFindBtn		: "يا�?تن",
DlgFindNotFoundMsg	: "متن مورد نظر يا�?ت نشد.",

// Replace Dialog
DlgReplaceTitle			: "جايگزينی",
DlgReplaceFindLbl		: "چه چيز را می يابيد:",
DlgReplaceReplaceLbl	: "جايگزينی با:",
DlgReplaceCaseChk		: "انطباق قالب",
DlgReplaceReplaceBtn	: "جايگزينی",
DlgReplaceReplAllBtn	: "جايگزينی همه موارد",
DlgReplaceWordChk		: "انطباق کلمه کامل",

// Paste Operations / Dialog
PasteErrorPaste	: "تنظيمات امنيتی مرورگر شما اجازه نمی دهد که ويرايشگر به طور خودکار عملکردهای چسباندن کلمات را انجام دهد. لط�?ا از کلمه کليدی مرتبط با اينکار را است�?اده کنيد (Ctrl+V).",
PasteErrorCut	: "تنظيمات امنيتی مرورگر شما اجازه نمی دهد که ويرايشگر به طور خودکار عملکردهای برش کلمات را انجام دهد. لط�?ا از کلمه کليدی مرتبط با اينکار را است�?اده کنيد (Ctrl+X).",
PasteErrorCopy	: "تنظيمات امنيتی مرورگر شما اجازه نمی دهد که ويرايشگر به طور خودکار عملکردهای کپی کردن کلمات را انجام دهد. لط�?ا از کلمه کليدی مرتبط با اينکار را است�?اده کنيد (Ctrl+C).",

PasteAsText		: "چسباندن به عنوان متن ساده",
PasteFromWord	: "چسباندن از Word",

DlgPasteMsg		: "به خاطر <STRONG>تنظيمات امنيتی</STRONG> مرورگر شما، ويرايشگر نتوانست به طور خودکار عملکرد چسباندن را به انجام برساند.<BR>لط�?ا به کمک کليد  (<STRONG>Ctrl+V</STRONG>) متن را درون جهبه زير بچسبانيد و دکمه <STRONG>تائيد</STRONG> را �?شار دهيد.",

// Color Picker
ColorAutomatic	: "خودکار",
ColorMoreColors	: "رنگهای بيشتر...",

// Document Properties
DocProps		: "ويژگيهای سند",

// Anchor Dialog
DlgAnchorTitle		: "ويژگيهای لنگر",
DlgAnchorName		: "نام لنگر",
DlgAnchorErrorName	: "لط�?ا نام لنگر را وارد کنيد",

// Speller Pages Dialog
DlgSpellNotInDic		: "در واژه نامه موجود نيست",
DlgSpellChangeTo		: "تغير به",
DlgSpellBtnIgnore		: "چشم پوشی",
DlgSpellBtnIgnoreAll	: "چشم پوشی همه",
DlgSpellBtnReplace		: "جايگزينی",
DlgSpellBtnReplaceAll	: "جايگزينی همه",
DlgSpellBtnUndo			: "واچينش",
DlgSpellNoSuggestions	: "- پيشنهادی نيست -",
DlgSpellProgress		: "کنترل املا در حال انجام...",
DlgSpellNoMispell		: "کنترل املا انجام شد. هيچ غلط املائی يا�?ت نشد",
DlgSpellNoChanges		: "کنترل املا انجام شد. هيچ کلمه ای تغير نيا�?ت",
DlgSpellOneChange		: "کنترل املا انجام شد. يک کلمه تغير يا�?ت",
DlgSpellManyChanges		: "کنترل املا انجام شد. %1 کلمه تغير يا�?ت",

IeSpellDownload			: "کنترل کننده املا نصب نشده است. آيا مايليد آنرا هم اکنون دريا�?ت کنيد؟",

// Button Dialog
DlgButtonText	: "متن (مقدار)",
DlgButtonType	: "نوع",

// Checkbox and Radio Button Dialogs
DlgCheckboxName		: "نام",
DlgCheckboxValue	: "مقدار",
DlgCheckboxSelected	: "برگزيده",

// Form Dialog
DlgFormName		: "نام",
DlgFormAction	: "اقدام",
DlgFormMethod	: "متد",

// Select Field Dialog
DlgSelectName		: "نام",
DlgSelectValue		: "مقدار",
DlgSelectSize		: "اندازه",
DlgSelectLines		: "خطوط",
DlgSelectChkMulti	: "انتخاب چند گزينه ای مجاز",
DlgSelectOpAvail	: "گزينه های موجود",
DlgSelectOpText		: "متن",
DlgSelectOpValue	: "مقدار",
DlgSelectBtnAdd		: "اضا�?ه",
DlgSelectBtnModify	: "ويرايش",
DlgSelectBtnUp		: "بالا",
DlgSelectBtnDown	: "پائين",
DlgSelectBtnSetValue : "تنظيم به عنوان مقدار برگزيده",
DlgSelectBtnDelete	: "حذ�?",

// Textarea Dialog
DlgTextareaName	: "نام",
DlgTextareaCols	: "ستونها",
DlgTextareaRows	: "سطرها",

// Text Field Dialog
DlgTextName			: "نام",
DlgTextValue		: "مقدار",
DlgTextCharWidth	: "پهنای شناسه",
DlgTextMaxChars		: "بيشينه شناسه",
DlgTextType			: "نوع",
DlgTextTypeText		: "متن",
DlgTextTypePass		: "کلمه عبور",

// Hidden Field Dialog
DlgHiddenName	: "نام",
DlgHiddenValue	: "مقدار",

// Bulleted List Dialog
BulletedListProp	: "ويژگيهای �?هرست دکمه ای",
NumberedListProp	: "ويژگيهای �?هرست عددی",
DlgLstType			: "نوع",
DlgLstTypeCircle	: "دايره",
DlgLstTypeDisk		: "قرص",
DlgLstTypeSquare	: "مربع",
DlgLstTypeNumbers	: "شماره ها (1، 2، 3)",
DlgLstTypeLCase		: "حرو�? کوچک (a، b، c)",
DlgLstTypeUCase		: "حرو�? بزرگ (A، B، C)",
DlgLstTypeSRoman	: "ارقام يونانی کوچک (i، ii، iii)",
DlgLstTypeLRoman	: "ارقام يونانی بزرگ (I، II، III)",

// Document Properties Dialog
DlgDocGeneralTab	: "عمومی",
DlgDocBackTab		: "پس زمينه",
DlgDocColorsTab		: "رنگها و حاشيه ها",
DlgDocMetaTab		: "�?راداده",

DlgDocPageTitle		: "عنوان ص�?حه",
DlgDocLangDir		: "جهت زبان",
DlgDocLangDirLTR	: "چپ به راست (LTF(",
DlgDocLangDirRTL	: "راست به چپ (RTL(",
DlgDocLangCode		: "کد زبان",
DlgDocCharSet		: "رمزگذاری نويسه",
DlgDocCharSetOther	: "رمزگذاری نويسه های ديگر",

DlgDocDocType		: "عنوان نوع سند",
DlgDocDocTypeOther	: "عنوان نوع سند های ديگر",
DlgDocIncXHTML		: "شامل تعاري�? XHTML",
DlgDocBgColor		: "رنگ پس زمينه",
DlgDocBgImage		: "URL تصوير پس زمينه",
DlgDocBgNoScroll	: "پس زمينه غير طوماری",
DlgDocCText			: "متن",
DlgDocCLink			: "لينک",
DlgDocCVisited		: "لينک مشاهده شده",
DlgDocCActive		: "لينک �?عال",
DlgDocMargins		: "حاشيه ص�?حه",
DlgDocMaTop			: "رو",
DlgDocMaLeft		: "چپ",
DlgDocMaRight		: "راست",
DlgDocMaBottom		: "زير",
DlgDocMeIndex		: "کلمات کليدی انديس کردن سند (با کاما جدا شوند)",
DlgDocMeDescr		: "سند",
DlgDocMeAuthor		: "نويسنده",
DlgDocMeCopy		: "کپی رايت",
DlgDocPreview		: "پيش نمايش",

// Templates Dialog
Templates			: "الگوها",
DlgTemplatesTitle	: "الگوهای محتويات",
DlgTemplatesSelMsg	: "لط�?ا الگوی مورد نظر را برای باز کردن در ويرايشگر انتخاب نمائيد<br>(محتويات اصلی از دست خواهند ر�?ت):",
DlgTemplatesLoading	: "بارگذاری �?هرست الگوها. لط�?ا صبر کنيد...",
DlgTemplatesNoTpl	: "(الگوئی تعري�? نشده است)",

// About Dialog
DlgAboutAboutTab	: "درباره",
DlgAboutBrowserInfoTab	: "اطلاعات مرورگر",
DlgAboutVersion		: "نگارش",
DlgAboutLicense		: "ليسانس تحت توا�?قنامه GNU Lesser General Public License",
DlgAboutInfo		: "برای اطلاعات بيشتر به آدرس زير برويد"
}