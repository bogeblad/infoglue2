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
 * File Name: uk.js
 * 	Ukrainian language file.
 * 
 * File Authors:
 * 		Alexander Pervak (pervak@gmail.com)
 */

var FCKLang =
{
// Language direction : "ltr" (left to right) or "rtl" (right to left).
Dir					: "ltr",

ToolbarCollapse		: "Згорнути панель ін�?трументів",
ToolbarExpand		: "Розгорнути панель ін�?трументів",

// Toolbar Items and Context Menu
Save				: "Зберегти",
NewPage				: "�?ова �?торінка",
Preview				: "Попередній перегл�?д",
Cut					: "Вирізати",
Copy				: "Копіювати",
Paste				: "В�?тавити",
PasteText			: "В�?тавити тільки тек�?т",
PasteWord			: "В�?тавити з Word",
Print				: "Друк",
SelectAll			: "Виділити в�?е",
RemoveFormat		: "Прибрати форматуванн�?",
InsertLinkLbl		: "По�?иланн�?",
InsertLink			: "В�?тавити/Редагувати по�?иланн�?",
RemoveLink			: "Знищити по�?иланн�?",
Anchor				: "В�?тавити/Редагувати �?кір",
InsertImageLbl		: "Зображенн�?",
InsertImage			: "В�?тавити/Редагувати зображенн�?",
InsertTableLbl		: "Таблиц�?",
InsertTable			: "В�?тавити/Редагувати таблицю",
InsertLineLbl		: "Ліні�?",
InsertLine			: "В�?тавити горизонтальну лінію",
InsertSpecialCharLbl: "Спеціальний �?имвол",
InsertSpecialChar	: "В�?тавити �?пеціальний �?имвол",
InsertSmileyLbl		: "Смайлик",
InsertSmiley		: "В�?тавити �?майлик",
About				: "Про FCKeditor",
Bold				: "Жирний",
Italic				: "Кур�?ив",
Underline			: "Підкре�?лений",
StrikeThrough		: "Закре�?лений",
Subscript			: "Підр�?дковий індек�?",
Superscript			: "�?адр�?дковий индек�?",
LeftJustify			: "По лівому краю",
CenterJustify		: "По центру",
RightJustify		: "По правому краю",
BlockJustify		: "По ширині",
DecreaseIndent		: "Зменшити від�?туп",
IncreaseIndent		: "Збільшити від�?туп",
Undo				: "Повернути",
Redo				: "Повторити",
NumberedListLbl		: "�?умерований �?пи�?ок",
NumberedList		: "В�?тавити/Видалити нумерований �?пи�?ок",
BulletedListLbl		: "Маркований �?пи�?ок",
BulletedList		: "В�?тавити/Видалити маркований �?пи�?ок",
ShowTableBorders	: "Показати бордюри таблиці",
ShowDetails			: "Показати деталі",
Style				: "Стиль",
FontFormat			: "Форматуванн�?",
Font				: "Шрифт",
FontSize			: "Розмір",
TextColor			: "Колір тек�?ту",
BGColor				: "Колір фону",
Source				: "Джерело",
Find				: "Пошук",
Replace				: "Заміна",
SpellCheck			: "Перевірити орфографію",
UniversalKeyboard	: "Універ�?альна клавіатура",

Form			: "Форма",
Checkbox		: "Флагова кнопка",
RadioButton		: "Кнопка вибору",
TextField		: "Тек�?тове поле",
Textarea		: "Тек�?това обла�?ть",
HiddenField		: "Приховане поле",
Button			: "Кнопка",
SelectionField	: "Спи�?ок",
ImageButton		: "Кнопка із зображенн�?м",

// Context Menu
EditLink			: "В�?тавити по�?иланн�?",
InsertRow			: "В�?тавити �?троку",
DeleteRows			: "Видалити �?троки",
InsertColumn		: "В�?тавити колонку",
DeleteColumns		: "Видалити колонки",
InsertCell			: "В�?тавити комірку",
DeleteCells			: "Видалити комірки",
MergeCells			: "Об'єднати комірки",
SplitCell			: "Роз'єднати комірку",
CellProperties		: "Вла�?тиво�?ті комірки",
TableProperties		: "Вла�?тиво�?ті таблиці",
ImageProperties		: "Вла�?тиво�?ті зображенн�?",

AnchorProp			: "Вла�?тиво�?ті �?кор�?",
ButtonProp			: "Вла�?тиво�?ті кнопки",
CheckboxProp		: "Вла�?тиво�?ті флагової кнопки",
HiddenFieldProp		: "Вла�?тиво�?ті прихованого пол�?",
RadioButtonProp		: "Вла�?тиво�?ті кнопки вибору",
ImageButtonProp		: "Вла�?тиво�?ті кнопки із зображенн�?м",
TextFieldProp		: "Вла�?тиво�?ті тек�?тового пол�?",
SelectionFieldProp	: "Вла�?тиво�?ті �?пи�?ку",
TextareaProp		: "Вла�?тиво�?ті тек�?тової обла�?ті",
FormProp			: "Вла�?тиво�?ті форми",

FontFormats			: "�?ормальний;Форматований;�?дре�?а;Заголовок 1;Заголовок 2;Заголовок 3;Заголовок 4;Заголовок 5;Заголовок 6",

// Alerts and Messages
ProcessingXHTML		: "Обробка XHTML. Зачекайте, будь ла�?ка...",
Done				: "Зроблено",
PasteWordConfirm	: "Тек�?т, що ви хочете в�?тавити, �?хожий на копійований з Word. Ви хочете очи�?тити його перед в�?тавкою?",
NotCompatiblePaste	: "Ц�? команда до�?тупна дл�? Internet Explorer вер�?ії 5.5 або вище. Ви хочете в�?тавити без очищенн�??",
UnknownToolbarItem	: "�?евідомий елемент панелі ін�?трументів \"%1\"",
UnknownCommand		: "�?евідоме ім'�? команди \"%1\"",
NotImplemented		: "Команда не реалізована",
UnknownToolbarSet	: "Панель ін�?трументів \"%1\" не і�?нує",

// Dialogs
DlgBtnOK			: "ОК",
DlgBtnCancel		: "Ска�?увати",
DlgBtnClose			: "Зачинити",
DlgBtnBrowseServer	: "Передивити�?�? на �?ервері",
DlgAdvancedTag		: "Розширений",
DlgOpOther			: "&lt;Інше&gt;",

// General Dialogs Labels
DlgGenNotSet		: "&lt;не визначено&gt;",
DlgGenId			: "Ідентифікатор",
DlgGenLangDir		: "�?апр�?мок мови",
DlgGenLangDirLtr	: "Зліва на право (LTR)",
DlgGenLangDirRtl	: "Зправа на ліво (RTL)",
DlgGenLangCode		: "Мова",
DlgGenAccessKey		: "Гар�?ча клавіша",
DlgGenName			: "Им'�?",
DlgGenTabIndex		: "По�?лідовні�?ть переходу",
DlgGenLongDescr		: "Довгий опи�? URL",
DlgGenClass			: "Кла�? CSS",
DlgGenTitle			: "Заголовок",
DlgGenContType		: "Тип вмі�?ту",
DlgGenLinkCharset	: "Кодировка",
DlgGenStyle			: "Стиль CSS",

// Image Dialog
DlgImgTitle			: "Вла�?тиво�?ті зображенн�?",
DlgImgInfoTab		: "Інформаці�? про изображении",
DlgImgBtnUpload		: "�?аді�?лати на �?ервер",
DlgImgURL			: "URL",
DlgImgUpload		: "Закачати",
DlgImgAlt			: "�?льтернативний тек�?т",
DlgImgWidth			: "Ширина",
DlgImgHeight		: "Ви�?ота",
DlgImgLockRatio		: "Зберегти пропорції",
DlgBtnResetSize		: "Скинути розмір",
DlgImgBorder		: "Бордюр",
DlgImgHSpace		: "Горизонтальний від�?туп",
DlgImgVSpace		: "Вертикальний від�?туп",
DlgImgAlign			: "Вирівнюванн�?",
DlgImgAlignLeft		: "По лівому краю",
DlgImgAlignAbsBottom: "�?б�? по низу",
DlgImgAlignAbsMiddle: "�?б�? по �?ередині",
DlgImgAlignBaseline	: "По базовій лінії",
DlgImgAlignBottom	: "По низу",
DlgImgAlignMiddle	: "По �?ередині",
DlgImgAlignRight	: "По правому краю",
DlgImgAlignTextTop	: "Тек�?т на верху",
DlgImgAlignTop		: "По верху",
DlgImgPreview		: "Попередній перегл�?д",
DlgImgAlertUrl		: "Будь ла�?ка, введіть URL зображенн�?",
DlgImgLinkTab		: "По�?иланн�?",

// Link Dialog
DlgLnkWindowTitle	: "По�?иланн�?",
DlgLnkInfoTab		: "Інформаці�? по�?иланн�?",
DlgLnkTargetTab		: "Ціль",

DlgLnkType			: "Тип по�?иланн�?",
DlgLnkTypeURL		: "URL",
DlgLnkTypeAnchor	: "Якір на цю �?торінку",
DlgLnkTypeEMail		: "Эл. пошта",
DlgLnkProto			: "Протокол",
DlgLnkProtoOther	: "&lt;інше&gt;",
DlgLnkURL			: "URL",
DlgLnkAnchorSel		: "Оберіть �?кір",
DlgLnkAnchorByName	: "За ім'�?м �?кор�?",
DlgLnkAnchorById	: "За ідентифікатором елемента",
DlgLnkNoAnchors		: "&lt;�?емає �?корів до�?тупних в цьому документі&gt;",
DlgLnkEMail			: "�?дре�?а ел. пошти",
DlgLnkEMailSubject	: "Тема ли�?та",
DlgLnkEMailBody		: "Тіло повідомленн�?",
DlgLnkUpload		: "Закачати",
DlgLnkBtnUpload		: "Пере�?лати на �?ервер",

DlgLnkTarget		: "Ціль",
DlgLnkTargetFrame	: "&lt;фрейм&gt;",
DlgLnkTargetPopup	: "&lt;�?пливаюче вікно&gt;",
DlgLnkTargetBlank	: "�?ове вікно (_blank)",
DlgLnkTargetParent	: "Батьків�?ьке вікно (_parent)",
DlgLnkTargetSelf	: "Теж вікно (_self)",
DlgLnkTargetTop		: "�?айвище вікно (_top)",
DlgLnkTargetFrameName	: "Ім'�? целевого фрейма",
DlgLnkPopWinName	: "Ім'�? �?пливаючого вікна",
DlgLnkPopWinFeat	: "Вла�?тиво�?ті �?пливаючого вікна",
DlgLnkPopResize		: "Змінюєть�?�? в розмірах",
DlgLnkPopLocation	: "Панель локації",
DlgLnkPopMenu		: "Панель меню",
DlgLnkPopScroll		: "Поло�?и прокрутки",
DlgLnkPopStatus		: "Строка �?тату�?у",
DlgLnkPopToolbar	: "Панель ін�?трументів",
DlgLnkPopFullScrn	: "Повний екран (IE)",
DlgLnkPopDependent	: "Залежний (Netscape)",
DlgLnkPopWidth		: "Ширина",
DlgLnkPopHeight		: "Ви�?ота",
DlgLnkPopLeft		: "Позиці�? зліва",
DlgLnkPopTop		: "Позиці�? зверху",

DlnLnkMsgNoUrl		: "Будь ла�?ка, зане�?іть URL по�?иланн�?",
DlnLnkMsgNoEMail	: "Будь ла�?ка, зане�?іть адре�? �?л. почты",
DlnLnkMsgNoAnchor	: "Будь ла�?ка, оберіть �?кір",

// Color Dialog
DlgColorTitle		: "Оберіть колір",
DlgColorBtnClear	: "Очи�?тити",
DlgColorHighlight	: "Під�?вічений",
DlgColorSelected	: "Обраний",

// Smiley Dialog
DlgSmileyTitle		: "В�?тавити �?майлик",

// Special Character Dialog
DlgSpecialCharTitle	: "Оберіть �?пеціальний �?имвол",

// Table Dialog
DlgTableTitle		: "Вла�?тиво�?ті таблиці",
DlgTableRows		: "Строки",
DlgTableColumns		: "Колонки",
DlgTableBorder		: "Розмір бордюра",
DlgTableAlign		: "Вирівнюванн�?",
DlgTableAlignNotSet	: "<�?е в�?т.>",
DlgTableAlignLeft	: "Зліва",
DlgTableAlignCenter	: "По центру",
DlgTableAlignRight	: "Зправа",
DlgTableWidth		: "Ширина",
DlgTableWidthPx		: "пік�?елів",
DlgTableWidthPc		: "від�?отків",
DlgTableHeight		: "Ви�?ота",
DlgTableCellSpace	: "Проміжок (spacing)",
DlgTableCellPad		: "Від�?туп (padding)",
DlgTableCaption		: "Заголовок",

// Table Cell Dialog
DlgCellTitle		: "Вла�?тиво�?ті комірки",
DlgCellWidth		: "Ширина",
DlgCellWidthPx		: "пік�?елів",
DlgCellWidthPc		: "від�?отків",
DlgCellHeight		: "Ви�?ота",
DlgCellWordWrap		: "Згортанн�? тек�?та",
DlgCellWordWrapNotSet	: "<�?е в�?т.>",
DlgCellWordWrapYes	: "Так",
DlgCellWordWrapNo	: "�?і",
DlgCellHorAlign		: "Горизонтальне вирівнюванн�?",
DlgCellHorAlignNotSet	: "<�?е в�?т.>",
DlgCellHorAlignLeft	: "Зліва",
DlgCellHorAlignCenter	: "По центру",
DlgCellHorAlignRight: "Зправа",
DlgCellVerAlign		: "Вертикальное вирівнюванн�?",
DlgCellVerAlignNotSet	: "<�?е в�?т.>",
DlgCellVerAlignTop	: "Зверху",
DlgCellVerAlignMiddle	: "По�?ередині",
DlgCellVerAlignBottom	: "Знизу",
DlgCellVerAlignBaseline	: "По базовій лінії",
DlgCellRowSpan		: "Діапазон �?трок (span)",
DlgCellCollSpan		: "Діапазон колонок (span)",
DlgCellBackColor	: "Колір фона",
DlgCellBorderColor	: "Колір бордюра",
DlgCellBtnSelect	: "Оберіть...",

// Find Dialog
DlgFindTitle		: "Пошук",
DlgFindFindBtn		: "Пошук",
DlgFindNotFoundMsg	: "Вказаний тек�?т не знайдений.",

// Replace Dialog
DlgReplaceTitle			: "Замінити",
DlgReplaceFindLbl		: "Шукати:",
DlgReplaceReplaceLbl	: "Замінити на:",
DlgReplaceCaseChk		: "Учитывать реги�?тр",
DlgReplaceReplaceBtn	: "Замінити",
DlgReplaceReplAllBtn	: "Замінити в�?е",
DlgReplaceWordChk		: "Збіг цілих �?лів",

// Paste Operations / Dialog
PasteErrorPaste	: "�?а�?тройки безпеки вашого браузера не дозвол�?ють редактору автоматично виконувати операції в�?тавки. Будь ла�?ка, викори�?товуйте клавіатуру дл�? цього (Ctrl+V).",
PasteErrorCut	: "�?а�?тройки безпеки вашого браузера не дозвол�?ють редактору автоматично виконувати операції вирізуванн�?. Будь ла�?ка, викори�?товуйте клавіатуру дл�? цього (Ctrl+X).",
PasteErrorCopy	: "�?а�?тройки безпеки вашого браузера не дозвол�?ють редактору автоматично виконувати операції копіюванн�?. Будь ла�?ка, викори�?товуйте клавіатуру дл�? цього (Ctrl+C).",

PasteAsText		: "В�?тавити тільки тек�?т",
PasteFromWord	: "В�?тавити з Word",

DlgPasteMsg		: "Редактор не може автоматично виконати операцію в�?тавки  вна�?лідок <STRONG>налаштуванн�? безпеки</STRONG> вашого браузера.<BR> Будь ла�?ка, в�?тавте тек�?т в на�?тупне поле введенн�?, викори�?товуючи клавіатуру (<STRONG>Ctrl+V</STRONG>),  і нати�?ніть <STRONG>ОК</STRONG>.",

// Color Picker
ColorAutomatic	: "�?втоматичний",
ColorMoreColors	: "Кольори...",

// Document Properties
DocProps		: "Вла�?тиво�?ті документа",

// Anchor Dialog
DlgAnchorTitle		: "Вла�?тиво�?ті �?кор�?",
DlgAnchorName		: "Ім'�? �?кор�?",
DlgAnchorErrorName	: "Будь ла�?ка, зане�?іть ім'�? �?кор�?",

// Speller Pages Dialog
DlgSpellNotInDic		: "�?е має в �?ловнику",
DlgSpellChangeTo		: "Замінити на",
DlgSpellBtnIgnore		: "Ігнорувати",
DlgSpellBtnIgnoreAll	: "Ігнорувати в�?е",
DlgSpellBtnReplace		: "Замінити",
DlgSpellBtnReplaceAll	: "Замінити в�?е",
DlgSpellBtnUndo			: "�?азад",
DlgSpellNoSuggestions	: "- �?емає припущень -",
DlgSpellProgress		: "Виконуєть�?�? перевірка орфографії...",
DlgSpellNoMispell		: "Перевірку орфографії завершено: помилок не знайдено",
DlgSpellNoChanges		: "Перевірку орфографії завершено: жодне �?лово не змінено",
DlgSpellOneChange		: "Перевірку орфографії завершено: змінено одно �?лово",
DlgSpellManyChanges		: "Перевірку орфографії завершено: 1% �?лів змінено",

IeSpellDownload			: "Модуль перевірки орфографії не в�?тановлено. Бажаєтн завантажити його зараз?",

// Button Dialog
DlgButtonText	: "Тек�?т (Значенн�?)",
DlgButtonType	: "Тип",

// Checkbox and Radio Button Dialogs
DlgCheckboxName		: "Ім'�?",
DlgCheckboxValue	: "Значенн�?",
DlgCheckboxSelected	: "Обрана",

// Form Dialog
DlgFormName		: "Ім'�?",
DlgFormAction	: "Ді�?",
DlgFormMethod	: "Метод",

// Select Field Dialog
DlgSelectName		: "Ім'�?",
DlgSelectValue		: "Значенн�?",
DlgSelectSize		: "Розмір",
DlgSelectLines		: "лінії",
DlgSelectChkMulti	: "Дозволити обранн�? декількох позицій",
DlgSelectOpAvail	: "До�?тупні варіанти",
DlgSelectOpText		: "Тек�?т",
DlgSelectOpValue	: "Значенн�?",
DlgSelectBtnAdd		: "Добавити",
DlgSelectBtnModify	: "Змінити",
DlgSelectBtnUp		: "Вгору",
DlgSelectBtnDown	: "Вниз",
DlgSelectBtnSetValue : "В�?тановити �?к вибране значенн�?",
DlgSelectBtnDelete	: "Видалити",

// Textarea Dialog
DlgTextareaName	: "Ім'�?",
DlgTextareaCols	: "Колонки",
DlgTextareaRows	: "Строки",

// Text Field Dialog
DlgTextName			: "Ім'�?",
DlgTextValue		: "Значенн�?",
DlgTextCharWidth	: "Ширина",
DlgTextMaxChars		: "Мак�?. кіл-ть �?имволів",
DlgTextType			: "Тип",
DlgTextTypeText		: "Тек�?т",
DlgTextTypePass		: "Пароль",

// Hidden Field Dialog
DlgHiddenName	: "Ім'�?",
DlgHiddenValue	: "Значенн�?",

// Bulleted List Dialog
BulletedListProp	: "Вла�?тиво�?ті маркованого �?пи�?ка",
NumberedListProp	: "Вла�?тиво�?ті нумерованного �?пи�?ка",
DlgLstType			: "Тип",
DlgLstTypeCircle	: "Коло",
DlgLstTypeDisk		: "Ди�?к",
DlgLstTypeSquare	: "Квадрат",
DlgLstTypeNumbers	: "�?омери (1, 2, 3)",
DlgLstTypeLCase		: "Літери нижнього регі�?тра(a, b, c)",
DlgLstTypeUCase		: "Літери ВЕРХ�?ЬОГО РЕГІСТР�? (A, B, C)",
DlgLstTypeSRoman	: "Малі рим�?ькі літери (i, ii, iii)",
DlgLstTypeLRoman	: "Великі рим�?ькі літери (I, II, III)",

// Document Properties Dialog
DlgDocGeneralTab	: "Загальні",
DlgDocBackTab		: "Заднє тло",
DlgDocColorsTab		: "Кольори та від�?тупи",
DlgDocMetaTab		: "Мета дані",

DlgDocPageTitle		: "Заголовок �?торінки",
DlgDocLangDir		: "�?апр�?мок тек�?ту",
DlgDocLangDirLTR	: "Зліва на право (LTR)",
DlgDocLangDirRTL	: "Зправа на лево (RTL)",
DlgDocLangCode		: "Код мови",
DlgDocCharSet		: "Кодуванн�? набору �?имволів",
DlgDocCharSetOther	: "Інше кодуванн�? набору �?имволів",

DlgDocDocType		: "Заголовок типу документу",
DlgDocDocTypeOther	: "Інший заголовок типу документу",
DlgDocIncXHTML		: "Ввімкнути XHTML оголошенн�?",
DlgDocBgColor		: "Колір тла",
DlgDocBgImage		: "URL зображенн�? тла",
DlgDocBgNoScroll	: "Тло без прокрутки",
DlgDocCText			: "Тек�?т",
DlgDocCLink			: "По�?иланн�?",
DlgDocCVisited		: "Відвідане по�?иланн�?",
DlgDocCActive		: "�?ктивне по�?иланн�?",
DlgDocMargins		: "Від�?тупи �?торінки",
DlgDocMaTop			: "Верхній",
DlgDocMaLeft		: "Лівий",
DlgDocMaRight		: "Правий",
DlgDocMaBottom		: "�?ижній",
DlgDocMeIndex		: "Ключові �?лова документа (розділені комами)",
DlgDocMeDescr		: "Опи�? документа",
DlgDocMeAuthor		: "�?втор",
DlgDocMeCopy		: "�?втор�?ькі права",
DlgDocPreview		: "Попередній перегл�?д",

// Templates Dialog
Templates			: "Шаблони",
DlgTemplatesTitle	: "Шаблони змі�?ту",
DlgTemplatesSelMsg	: "Оберіть, будь ла�?ка, шаблон дл�? відкритт�? в редакторі<br>(поточний змі�?т буде втрачено):",
DlgTemplatesLoading	: "Завантаженн�? �?пи�?ку шаблонів. Зачекайте, будь ла�?ка...",
DlgTemplatesNoTpl	: "(�?е визначено жодного шаблону)",

// About Dialog
DlgAboutAboutTab	: "Про програму",
DlgAboutBrowserInfoTab	: "Інформаці�? браузера",
DlgAboutVersion		: "Вер�?і�?",
DlgAboutLicense		: "Ліцензовано згідно умовам GNU Lesser General Public License",
DlgAboutInfo		: "Додаткову інформацію дивіть�?�? на "
}