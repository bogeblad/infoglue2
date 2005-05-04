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
 * File Name: ar.js
 * 	Arabic language file.
 * 
 * Version:  2.0 RC3
 * Modified: 2005-03-01 17:26:17
 * 
 * File Authors:
 * 		Abdul-Aziz Abdul-Kareem Al-Oraij (http://aziz.oraij.com)
 */

var FCKLang =
{
// Language direction : "ltr" (left to right) or "rtl" (right to left).
Dir					: "rtl",

ToolbarCollapse		: "ضم شريط الأدوات",
ToolbarExpand		: "تمدد شريط الأدوات",

// Toolbar Items and Context Menu
Save				: "ح�?ظ",
NewPage				: "ص�?حة جديدة",
Preview				: "معاينة الص�?حة",
Cut					: "قص",
Copy				: "نسخ",
Paste				: "لصق",
PasteText			: "لصق كنص بسيط",
PasteWord			: "لصق من وورد",
Print				: "طباعة",
SelectAll			: "تحديد الكل",
RemoveFormat		: "إزالة التنسيقات",
InsertLinkLbl		: "رابط",
InsertLink			: "إدراج/تحرير رابط",
RemoveLink			: "إزالة رابط",
Anchor				: "إدراج/تحرير إشارة مرجعية",
InsertImageLbl		: "صورة",
InsertImage			: "إدراج/تحرير صورة",
InsertTableLbl		: "جدول",
InsertTable			: "إدراج/تحرير جدول",
InsertLineLbl		: "خط �?اصل",
InsertLine			: "إدراج خط �?اصل",
InsertSpecialCharLbl: "رموز",
InsertSpecialChar	: "إدراج  رموز..�?",
InsertSmileyLbl		: "ابتسامات",
InsertSmiley		: "إدراج ابتسامات",
About				: "حول FCKeditor",
Bold				: "غامق",
Italic				: "مائل",
Underline			: "تسطير",
StrikeThrough		: "يتوسطه خط",
Subscript			: "منخ�?ض",
Superscript			: "مرت�?ع",
LeftJustify			: "محاذاة إلى اليسار",
CenterJustify		: "توسيط",
RightJustify		: "محاذاة إلى اليمين",
BlockJustify		: "ضبط",
DecreaseIndent		: "إنقاص المسا�?ة البادئة",
IncreaseIndent		: "زيادة المسا�?ة البادئة",
Undo				: "تراجع",
Redo				: "إعادة",
NumberedListLbl		: "تعداد رقمي",
NumberedList		: "إدراج/إلغاء تعداد رقمي",
BulletedListLbl		: "تعداد نقطي",
BulletedList		: "إدراج/إلغاء تعداد نقطي",
ShowTableBorders	: "معاينة حدود الجداول",
ShowDetails			: "معاينة الت�?اصيل",
Style				: "نمط",
FontFormat			: "تنسيق",
Font				: "خط",
FontSize			: "حجم الخط",
TextColor			: "لون النص",
BGColor				: "لون الخل�?ية",
Source				: "ش�?رة المصدر",
Find				: "بحث",
Replace				: "استبدال",
SpellCheck			: "تدقيق إملائي",
UniversalKeyboard	: "لوحة الم�?اتيح العالمية",

Form			: "نموذج",
Checkbox		: "خانة اختيار",
RadioButton		: "زر خيار",
TextField		: "مربع نص",
Textarea		: "ناحية نص",
HiddenField		: "إدراج حقل خ�?ي",
Button			: "زر ضغط",
SelectionField	: "قائمة منسدلة",
ImageButton		: "زر صورة",

// Context Menu
EditLink			: "تحرير رابط",
InsertRow			: "إدراج ص�?",
DeleteRows			: "حذ�? ص�?و�?",
InsertColumn		: "إدراج عمود",
DeleteColumns		: "حذ�? أعمدة",
InsertCell			: "إدراج خلية",
DeleteCells			: "حذ�? خلايا",
MergeCells			: "دمج خلايا",
SplitCell			: "تقسيم خلية",
CellProperties		: "خصائص الخلية",
TableProperties		: "خصائص الجدول",
ImageProperties		: "خصائص الصورة",

AnchorProp			: "خصائص الإشارة المرجعية",
ButtonProp			: "خصائص زر الضغط",
CheckboxProp		: "خصائص خانة الاختيار",
HiddenFieldProp		: "خصائص الحقل الخ�?ي",
RadioButtonProp		: "خصائص زر الخيار",
ImageButtonProp		: "خصائص زر الصورة",
TextFieldProp		: "خصائص مربع النص",
SelectionFieldProp	: "خصائص القائمة المنسدلة",
TextareaProp		: "خصائص ناحية النص",
FormProp			: "خصائص النموذج",

FontFormats			: "عادي;منسّق;دوس;العنوان 1;العنوان  2;العنوان  3;العنوان  4;العنوان  5;العنوان  6",	// 2.0: The last entry has been added.

// Alerts and Messages
ProcessingXHTML		: "انتظر قليلاً ريثما تتم   معالَجة�? XHTML. لن يستغرق طويلاً...",
Done				: "تم",
PasteWordConfirm	: "يبدو أن النص المراد لصقه منسوخ من برنامج وورد. هل تود تنظي�?ه قبل الشروع �?ي عملية اللصق؟",
NotCompatiblePaste	: "هذه الميزة تحتاج لمتص�?ح من النوعInternet Explorer إصدار 5.5 �?ما �?وق. هل تود اللصق دون تنظي�? الكود؟",
UnknownToolbarItem	: "عنصر شريط أدوات غير معرو�? \"%1\"",
UnknownCommand		: "أمر غير معرو�? \"%1\"",
NotImplemented		: "لم يتم دعم هذا الأمر",
UnknownToolbarSet	: "لم أتمكن من العثور على طقم الأدوات \"%1\" ",

// Dialogs
DlgBtnOK			: "موا�?ق",
DlgBtnCancel		: "إلغاء الأمر",
DlgBtnClose			: "إغلاق",
DlgBtnBrowseServer	: "تص�?ح الخادم",
DlgAdvancedTag		: "متقدم",
DlgOpOther			: "&lt;أخرى&gt;",

// General Dialogs Labels
DlgGenNotSet		: "&lt;بدون تحديد&gt;",
DlgGenId			: "Id",
DlgGenLangDir		: "اتجاه النص",
DlgGenLangDirLtr	: "اليسار لليمين (LTR)",
DlgGenLangDirRtl	: "اليمين لليسار (RTL)",
DlgGenLangCode		: "رمز اللغة",
DlgGenAccessKey		: "م�?اتيح الاختصار",
DlgGenName			: "الاسم",
DlgGenTabIndex		: "الترتيب",
DlgGenLongDescr		: "عنوان الوص�? الم�?صّل",
DlgGenClass			: "�?ئات التنسيق",
DlgGenTitle			: "تلميح الشاشة",
DlgGenContType		: "نوع التلميح",
DlgGenLinkCharset	: "ترميز المادة المرطلوبة",
DlgGenStyle			: "نمط",

// Image Dialog
DlgImgTitle			: "خصائص الصورة",
DlgImgInfoTab		: "معلومات الصورة",
DlgImgBtnUpload		: "أرسلها للخادم",
DlgImgURL			: "موقع الصورة",
DlgImgUpload		: "ر�?ع",
DlgImgAlt			: "الوص�?",
DlgImgWidth			: "العرض",
DlgImgHeight		: "الارت�?اع",
DlgImgLockRatio		: "تناسق الحجم",
DlgBtnResetSize		: "استعادة الحجم الأصلي",
DlgImgBorder		: "سمك الحدود",
DlgImgHSpace		: "تباعد أ�?قي",
DlgImgVSpace		: "تباعد عمودي",
DlgImgAlign			: "محاذاة",
DlgImgAlignLeft		: "يسار",
DlgImgAlignAbsBottom: "أس�?ل النص",
DlgImgAlignAbsMiddle: "وسط السطر",
DlgImgAlignBaseline	: "على السطر",
DlgImgAlignBottom	: "أس�?ل",
DlgImgAlignMiddle	: "وسط",
DlgImgAlignRight	: "يمين",
DlgImgAlignTextTop	: "أعلى النص",
DlgImgAlignTop		: "أعلى",
DlgImgPreview		: "معاينة",
DlgImgAlertUrl		: "�?ضلاً اكتب الموقع الذي توجد عليه هذه الصورة.",

// Link Dialog
DlgLnkWindowTitle	: "ارتباط تشعبي",
DlgLnkInfoTab		: "معلومات الرابط",
DlgLnkTargetTab		: "الهد�?",

DlgLnkType			: "نوع الربط",
DlgLnkTypeURL		: "العنوان",
DlgLnkTypeAnchor	: "مكان �?ي هذا المستند",
DlgLnkTypeEMail		: "بريد إلكتروني",
DlgLnkProto			: "البروتوكول",
DlgLnkProtoOther	: "&lt;أخرى&gt;",
DlgLnkURL			: "الموقع",
DlgLnkAnchorSel		: "اختر علامة مرجعية",
DlgLnkAnchorByName	: "حسب اسم العلامة",
DlgLnkAnchorById	: "حسب تعري�? العنصر",
DlgLnkNoAnchors		: "&lt;لا يوجد علامات مرجعية �?ي هذا المستند&gt;",
DlgLnkEMail			: "عنوان بريد إلكتروني",
DlgLnkEMailSubject	: "موضوع الرسالة",
DlgLnkEMailBody		: "محتوى الرسالة",
DlgLnkUpload		: "ر�?ع",
DlgLnkBtnUpload		: "أرسلها للخادم",

DlgLnkTarget		: "الهد�?",
DlgLnkTargetFrame	: "&lt;إطار&gt;",
DlgLnkTargetPopup	: "&lt;نا�?ذة منبثقة&gt;",
DlgLnkTargetBlank	: "إطار جديد (_blank)",
DlgLnkTargetParent	: "الإطار الأصل (_parent)",
DlgLnkTargetSelf	: "ن�?س الإطار (_self)",
DlgLnkTargetTop		: "ص�?حة كاملة (_top)",
DlgLnkTargetFrameName	: "اسم الإطار الهد�?",
DlgLnkPopWinName	: "تسمية النا�?ذة المنبثقة",
DlgLnkPopWinFeat	: "خصائص النا�?ذة المنبثقة",
DlgLnkPopResize		: "قابلة للتحجيم",
DlgLnkPopLocation	: "شريط العنوان",
DlgLnkPopMenu		: "القوائم الرئيسية",
DlgLnkPopScroll		: "أشرطة التمرير",
DlgLnkPopStatus		: "شريط الحالة الس�?لي",
DlgLnkPopToolbar	: "شريط الأدوات",
DlgLnkPopFullScrn	: "ملئ الشاشة (IE)",
DlgLnkPopDependent	: "تابع (Netscape)",
DlgLnkPopWidth		: "العرض",
DlgLnkPopHeight		: "الارت�?اع",
DlgLnkPopLeft		: "التمركز لليسار",
DlgLnkPopTop		: "التمركز للأعلى",

DlnLnkMsgNoUrl		: "�?ضلاً أدخل عنوان الموقع الذي يشير إليه الرابط",
DlnLnkMsgNoEMail	: "�?ضلاً أدخل عنوان البريد الإلكتروني",
DlnLnkMsgNoAnchor	: "�?ضلاً حدد العلامة المرجعية المرغوبة",

// Color Dialog
DlgColorTitle		: "اختر لوناً",
DlgColorBtnClear	: "مسح",
DlgColorHighlight	: "تحديد",
DlgColorSelected	: "اختيار",

// Smiley Dialog
DlgSmileyTitle		: "إدراج ابتسامات ",

// Special Character Dialog
DlgSpecialCharTitle	: "إدراج رمز",

// Table Dialog
DlgTableTitle		: "إدراج جدول",
DlgTableRows		: "ص�?و�?",
DlgTableColumns		: "أعمدة",
DlgTableBorder		: "سمك الحدود",
DlgTableAlign		: "المحاذاة",
DlgTableAlignNotSet	: "<بدون تحديد>",
DlgTableAlignLeft	: "يسار",
DlgTableAlignCenter	: "وسط",
DlgTableAlignRight	: "يمين",
DlgTableWidth		: "العرض",
DlgTableWidthPx		: "بكسل",
DlgTableWidthPc		: "بالمئة",
DlgTableHeight		: "الارت�?اع",
DlgTableCellSpace	: "تباعد الخلايا",
DlgTableCellPad		: "المسا�?ة البادئة",
DlgTableCaption		: "الوص�?",

// Table Cell Dialog
DlgCellTitle		: "خصائص الخلية",
DlgCellWidth		: "العرض",
DlgCellWidthPx		: "بكسل",
DlgCellWidthPc		: "بالمئة",
DlgCellHeight		: "الارت�?اع",
DlgCellWordWrap		: "الت�?ا�? النص",
DlgCellWordWrapNotSet	: "<بدون تحديد>",
DlgCellWordWrapYes	: "نعم",
DlgCellWordWrapNo	: "لا",
DlgCellHorAlign		: "المحاذاة الأ�?قية",
DlgCellHorAlignNotSet	: "<بدون تحديد>",
DlgCellHorAlignLeft	: "يسار",
DlgCellHorAlignCenter	: "وسط",
DlgCellHorAlignRight: "يمين",
DlgCellVerAlign		: "المحاذاة العمودية",
DlgCellVerAlignNotSet	: "<بدون تحديد>",
DlgCellVerAlignTop	: "أعلى",
DlgCellVerAlignMiddle	: "وسط",
DlgCellVerAlignBottom	: "أس�?ل",
DlgCellVerAlignBaseline	: "على السطر",
DlgCellRowSpan		: "امتداد الص�?و�?",
DlgCellCollSpan		: "امتداد الأعمدة",
DlgCellBackColor	: "لون الخل�?ية",
DlgCellBorderColor	: "لون الحدود",
DlgCellBtnSelect	: "حدّد...",

// Find Dialog
DlgFindTitle		: "بحث",
DlgFindFindBtn		: "ابحث",
DlgFindNotFoundMsg	: "لم يتم العثور على النص المحدد.",

// Replace Dialog
DlgReplaceTitle			: "استبدال",
DlgReplaceFindLbl		: "البحث عن:",
DlgReplaceReplaceLbl	: "استبدال بـ:",
DlgReplaceCaseChk		: "مطابقة حالة الأحر�?",
DlgReplaceReplaceBtn	: "استبدال",
DlgReplaceReplAllBtn	: "استبدال الكل",
DlgReplaceWordChk		: "الكلمة بالكامل �?قط",

// Paste Operations / Dialog
PasteErrorPaste	: "الإعدادات الأمنية للمتص�?ح الذي تستخدمه تمنع اللصق التلقائي. �?ضلاً استخدم لوحة الم�?اتيح ل�?عل ذلك (Ctrl+V).",
PasteErrorCut	: "الإعدادات الأمنية للمتص�?ح الذي تستخدمه تمنع القص التلقائي. �?ضلاً استخدم لوحة الم�?اتيح ل�?عل ذلك (Ctrl+X).",
PasteErrorCopy	: "الإعدادات الأمنية للمتص�?ح الذي تستخدمه تمنع النسخ التلقائي. �?ضلاً استخدم لوحة الم�?اتيح ل�?عل ذلك (Ctrl+C).",

PasteAsText		: "لصق كنص بسيط",
PasteFromWord	: "لصق من وورد",

DlgPasteMsg		: "لم يتمكن المحرر من القيام باللصق تلقائياً، نظراً <STRONG>لإعدادت متص�?حك الأمنية</STRONG>.<BR>�?ضلاً إلصق داخل المربع التالي باستخدام لوحة الم�?اتيح (<STRONG>Ctrl+V</STRONG>) ثم اضغط <STRONG>موا�?ق</STRONG>.",

// Color Picker
ColorAutomatic	: "تلقائي",
ColorMoreColors	: "ألوان إضا�?ية...",

// Document Properties
DocProps		: "خصائص الص�?حة",

// Anchor Dialog
DlgAnchorTitle		: "خصائص إشارة مرجعية",
DlgAnchorName		: "اسم الإشارة المرجعية",
DlgAnchorErrorName	: "الرجاء كتابة اسم الإشارة المرجعية",

// Speller Pages Dialog
DlgSpellNotInDic		: "ليست �?ي القاموس",
DlgSpellChangeTo		: "التغيير إلى",
DlgSpellBtnIgnore		: "تجاهل",
DlgSpellBtnIgnoreAll	: "تجاهل الكل",
DlgSpellBtnReplace		: "تغيير",
DlgSpellBtnReplaceAll	: "تغيير الكل",
DlgSpellBtnUndo			: "تراجع",
DlgSpellNoSuggestions	: "- لا توجد اقتراحات -",
DlgSpellProgress		: "جاري التدقيق إملائياً",
DlgSpellNoMispell		: "تم إكمال التدقيق الإملائي: لم يتم العثور على أي أخطاء إملائية",
DlgSpellNoChanges		: "تم إكمال التدقيق الإملائي: لم يتم تغيير أي كلمة",
DlgSpellOneChange		: "تم إكمال التدقيق الإملائي: تم تغيير كلمة واحدة �?قط",
DlgSpellManyChanges		: "تم إكمال التدقيق الإملائي: تم تغيير %1 كلمات\كلمة",

IeSpellDownload			: "المدقق الإملائي (الإنجليزي) غير مثبّت. هل تود تحميله الآن؟",

// Button Dialog
DlgButtonText	: "القيمة/التسمية",
DlgButtonType	: "نوع الزر",

// Checkbox and Radio Button Dialogs
DlgCheckboxName		: "الاسم",
DlgCheckboxValue	: "القيمة",
DlgCheckboxSelected	: "محدد",

// Form Dialog
DlgFormName		: "الاسم",
DlgFormAction	: "اسم المل�?",
DlgFormMethod	: "الأسلوب",

// Select Field Dialog
DlgSelectName		: "الاسم",
DlgSelectValue		: "القيمة",
DlgSelectSize		: "الحجم",
DlgSelectLines		: "الأسطر",
DlgSelectChkMulti	: "السماح بتحديدات متعددة",
DlgSelectOpAvail	: "الخيارات المتاحة",
DlgSelectOpText		: "النص",
DlgSelectOpValue	: "القيمة",
DlgSelectBtnAdd		: "إضا�?ة",
DlgSelectBtnModify	: "تعديل",
DlgSelectBtnUp		: "تحريك لأعلى",
DlgSelectBtnDown	: "تحريك لأس�?ل",
DlgSelectBtnSetValue : "اجعلها محددة",
DlgSelectBtnDelete	: "إزالة",

// Textarea Dialog
DlgTextareaName	: "الاسم",
DlgTextareaCols	: "الأعمدة",
DlgTextareaRows	: "الص�?و�?",

// Text Field Dialog
DlgTextName			: "الاسم",
DlgTextValue		: "القيمة",
DlgTextCharWidth	: "العرض بالأحر�?",
DlgTextMaxChars		: "عدد الحرو�? الأقصى",
DlgTextType			: "نوع المحتوى",
DlgTextTypeText		: "نص",
DlgTextTypePass		: "كلمة مرور",

// Hidden Field Dialog
DlgHiddenName	: "الاسم",
DlgHiddenValue	: "القيمة",

// Bulleted List Dialog
BulletedListProp	: "خصائص التعداد النقطي",
NumberedListProp	: "خصائص التعداد الرقمي",
DlgLstType			: "النوع",
DlgLstTypeCircle	: "دائرة",
DlgLstTypeDisk		: "قرص",
DlgLstTypeSquare	: "مربع",
DlgLstTypeNumbers	: "أرقام (1، 2، 3)َ",
DlgLstTypeLCase		: "حرو�? صغيرة (a, b, c)َ",
DlgLstTypeUCase		: "حرو�? كبيرة (A, B, C)َ",
DlgLstTypeSRoman	: "ترقيم روماني صغير (i, ii, iii)َ",
DlgLstTypeLRoman	: "ترقيم روماني كبير (I, II, III)َ",

// Document Properties Dialog
DlgDocGeneralTab	: "عام",
DlgDocBackTab		: "الخل�?ية",
DlgDocColorsTab		: "الألوان والهوامش",
DlgDocMetaTab		: "المعرّ�?ات الرأسية",

DlgDocPageTitle		: "عنوان الص�?حة",
DlgDocLangDir		: "اتجاه اللغة",
DlgDocLangDirLTR	: "اليسار لليمين (LTR)",
DlgDocLangDirRTL	: "اليمين لليسار (RTL)",
DlgDocLangCode		: "رمز اللغة",
DlgDocCharSet		: "ترميز الحرو�?",
DlgDocCharSetOther	: "ترميز حرو�? آخر",

DlgDocDocType		: "ترويسة نوع  الص�?حة",
DlgDocDocTypeOther	: "ترويسة نوع  ص�?حة أخرى",
DlgDocIncXHTML		: "تضمين   إعلانات�? لغة XHTMLَ",
DlgDocBgColor		: "لون الخل�?ية",
DlgDocBgImage		: "رابط الصورة الخل�?ية",
DlgDocBgNoScroll	: "جعلها علامة مائية",
DlgDocCText			: "النص",
DlgDocCLink			: "الروابط",
DlgDocCVisited		: "المزارة",
DlgDocCActive		: "النشطة",
DlgDocMargins		: "هوامش الص�?حة",
DlgDocMaTop			: "علوي",
DlgDocMaLeft		: "أيسر",
DlgDocMaRight		: "أيمن",
DlgDocMaBottom		: "س�?لي",
DlgDocMeIndex		: "الكلمات الأساسية (م�?صولة ب�?واصل)َ",
DlgDocMeDescr		: "وص�? الص�?حة",
DlgDocMeAuthor		: "الكاتب",
DlgDocMeCopy		: "المالك",
DlgDocPreview		: "معاينة",

// About Dialog
DlgAboutAboutTab	: "نبذة",
DlgAboutBrowserInfoTab	: "معلومات متص�?حك",
DlgAboutVersion		: "الإصدار",
DlgAboutLicense		: "مرخّص بحسب قانون  GNU LGPL",
DlgAboutInfo		: "لمزيد من المعلومات ت�?ضل بزيارة"
}