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
 * File Name: hr.js
 * 	Croatian language file.
 * 
 * File Authors:
 * 		Alex Varga (avarga@globaldizajn.hr)
 */

var FCKLang =
{
// Language direction : "ltr" (left to right) or "rtl" (right to left).
Dir					: "ltr",

ToolbarCollapse		: "Smanji trake s alatima",
ToolbarExpand		: "Proširi trake s alatima",

// Toolbar Items and Context Menu
Save				: "Snimi",
NewPage				: "Nova stranica",
Preview				: "Pregledaj",
Cut					: "Izreži",
Copy				: "Kopiraj",
Paste				: "Zalijepi",
PasteText			: "Zalijepi kao �?isti tekst",
PasteWord			: "Zalijepi iz Worda",
Print				: "Ispiši",
SelectAll			: "Odaberi sve",
RemoveFormat		: "Ukloni formatiranje",
InsertLinkLbl		: "Link",
InsertLink			: "Ubaci/promjeni link",
RemoveLink			: "Ukloni link",
Anchor				: "Ubaci/promjeni sidro",
InsertImageLbl		: "Slika",
InsertImage			: "Ubaci/promjeni sliku",
InsertTableLbl		: "Tablica",
InsertTable			: "Ubaci/promjeni tablicu",
InsertLineLbl		: "Linija",
InsertLine			: "Ubaci vodoravnu liniju",
InsertSpecialCharLbl: "Posebni karakteri",
InsertSpecialChar	: "Ubaci posebne karaktere",
InsertSmileyLbl		: "Smješko",
InsertSmiley		: "Ubaci smješka",
About				: "O FCKeditoru",
Bold				: "Podebljaj",
Italic				: "Ukosi",
Underline			: "Podcrtano",
StrikeThrough		: "Precrtano",
Subscript			: "Subscript",
Superscript			: "Superscript",
LeftJustify			: "Lijevo poravnanje",
CenterJustify		: "Središnje poravnanje",
RightJustify		: "Desno poravnanje",
BlockJustify		: "Blok poravnanje",
DecreaseIndent		: "Pomakni ulijevo",
IncreaseIndent		: "Pomakni udesno",
Undo				: "Poništi",
Redo				: "Ponovi",
NumberedListLbl		: "Broj�?ana lista",
NumberedList		: "Ubaci/ukloni broj�?anu listu",
BulletedListLbl		: "Obi�?na lista",
BulletedList		: "Ubaci/ukloni obi�?nu listu",
ShowTableBorders	: "Prikaži okvir tablice",
ShowDetails			: "Prikaži detalje",
Style				: "Stil",
FontFormat			: "Format",
Font				: "Font",
FontSize			: "Veli�?ina",
TextColor			: "Boja teksta",
BGColor				: "Boja pozadine",
Source				: "K&ocirc;d",
Find				: "Pronađi",
Replace				: "Zamijeni",
SpellCheck			: "Provjeri pravopis",
UniversalKeyboard	: "Univerzalna tipkovnica",

Form			: "Form",
Checkbox		: "Checkbox",
RadioButton		: "Radio Button",
TextField		: "Text Field",
Textarea		: "Textarea",
HiddenField		: "Hidden Field",
Button			: "Button",
SelectionField	: "Selection Field",
ImageButton		: "Image Button",

// Context Menu
EditLink			: "Promjeni link",
InsertRow			: "Ubaci red",
DeleteRows			: "Izbriši redove",
InsertColumn		: "Ubaci kolonu",
DeleteColumns		: "Izbriši kolone",
InsertCell			: "Ubaci ćelije",
DeleteCells			: "Izbriši ćelije",
MergeCells			: "Spoji ćelije",
SplitCell			: "Razdvoji ćelije",
CellProperties		: "Svojstva ćelije",
TableProperties		: "Svojstva tablice",
ImageProperties		: "Svojstva slike",

AnchorProp			: "Svojstva sidra",
ButtonProp			: "Image Button svojstva",
CheckboxProp		: "Checkbox svojstva",
HiddenFieldProp		: "Hidden Field svojstva",
RadioButtonProp		: "Radio Button svojstva",
ImageButtonProp		: "Image Button svojstva",
TextFieldProp		: "Text Field svojstva",
SelectionFieldProp	: "Selection svojstva",
TextareaProp		: "Textarea svojstva",
FormProp			: "Form svojstva",

FontFormats			: "Normal;Formatirano;Adresa;Heading 1;Heading 2;Heading 3;Heading 4;Heading 5;Heading 6",

// Alerts and Messages
ProcessingXHTML		: "Obrađujem XHTML. Molimo pri�?ekajte...",
Done				: "Završio",
PasteWordConfirm	: "Tekst koji želite zalijepiti �?ini se da je kopiran iz Worda. Želite li prije o�?istiti tekst?",
NotCompatiblePaste	: "Ova naredba je dostupna samo u Internet Exploreru 5.5 ili novijem. Želite li nastaviti bez �?išćenja?",
UnknownToolbarItem	: "Nepoznati �?lan trake s alatima \"%1\"",
UnknownCommand		: "Nepoznata naredba \"%1\"",
NotImplemented		: "Naredba nije implementirana",
UnknownToolbarSet	: "Traka s alatima \"%1\" ne postoji",

// Dialogs
DlgBtnOK			: "OK",
DlgBtnCancel		: "Poništi",
DlgBtnClose			: "Zatvori",
DlgBtnBrowseServer	: "Pretraži server",
DlgAdvancedTag		: "Napredno",
DlgOpOther			: "&lt;Drugo&gt;",

// General Dialogs Labels
DlgGenNotSet		: "&lt;nije postavljeno&gt;",
DlgGenId			: "Id",
DlgGenLangDir		: "Smjer jezika",
DlgGenLangDirLtr	: "S lijeva na desno (LTR)",
DlgGenLangDirRtl	: "S desna na lijevo (RTL)",
DlgGenLangCode		: "K&ocirc;d jezika",
DlgGenAccessKey		: "Pristupna tipka",
DlgGenName			: "Naziv",
DlgGenTabIndex		: "Tab Indeks",
DlgGenLongDescr		: "Duga�?ki opis URL",
DlgGenClass			: "Stylesheet klase",
DlgGenTitle			: "Advisory naslov",
DlgGenContType		: "Advisory vrsta sadržaja",
DlgGenLinkCharset	: "Linked Resource Charset",
DlgGenStyle			: "Stil",

// Image Dialog
DlgImgTitle			: "Svojstva slika",
DlgImgInfoTab		: "Info slike",
DlgImgBtnUpload		: "Pošalji na server",
DlgImgURL			: "URL",
DlgImgUpload		: "Pošalji",
DlgImgAlt			: "Alternativni tekst",
DlgImgWidth			: "Širina",
DlgImgHeight		: "Visina",
DlgImgLockRatio		: "Zaklju�?aj odnos",
DlgBtnResetSize		: "Obriši veli�?inu",
DlgImgBorder		: "Okvir",
DlgImgHSpace		: "HSpace",
DlgImgVSpace		: "VSpace",
DlgImgAlign			: "Poravnaj",
DlgImgAlignLeft		: "Lijevo",
DlgImgAlignAbsBottom: "Abs dolje",
DlgImgAlignAbsMiddle: "Abs sredina",
DlgImgAlignBaseline	: "Bazno",
DlgImgAlignBottom	: "Dolje",
DlgImgAlignMiddle	: "Sredina",
DlgImgAlignRight	: "Desno",
DlgImgAlignTextTop	: "Vrh teksta",
DlgImgAlignTop		: "Vrh",
DlgImgPreview		: "Pregledaj",
DlgImgAlertUrl		: "Unesite URL slike",
DlgImgLinkTab		: "Link",

// Link Dialog
DlgLnkWindowTitle	: "Link",
DlgLnkInfoTab		: "Link Info",
DlgLnkTargetTab		: "Meta",

DlgLnkType			: "Link vrsta",
DlgLnkTypeURL		: "URL",
DlgLnkTypeAnchor	: "Sidro na ovoj stranici",
DlgLnkTypeEMail		: "E-Mail",
DlgLnkProto			: "Protokol",
DlgLnkProtoOther	: "&lt;drugo&gt;",
DlgLnkURL			: "URL",
DlgLnkAnchorSel		: "Odaberi sidro",
DlgLnkAnchorByName	: "Po nazivu sidra",
DlgLnkAnchorById	: "Po Id elementa",
DlgLnkNoAnchors		: "&lt;Nema dostupnih sidra&gt;",
DlgLnkEMail			: "E-Mail adresa",
DlgLnkEMailSubject	: "Naslov",
DlgLnkEMailBody		: "Sadržaj poruke",
DlgLnkUpload		: "Pošalji",
DlgLnkBtnUpload		: "Pošalji na server",

DlgLnkTarget		: "Meta",
DlgLnkTargetFrame	: "&lt;okvir&gt;",
DlgLnkTargetPopup	: "&lt;popup prozor&gt;",
DlgLnkTargetBlank	: "Novi prozor (_blank)",
DlgLnkTargetParent	: "Roditeljski prozor (_parent)",
DlgLnkTargetSelf	: "Isti prozor (_self)",
DlgLnkTargetTop		: "Vršni prozor (_top)",
DlgLnkTargetFrameName	: "Ime ciljnog okvira",
DlgLnkPopWinName	: "Naziv popup prozora",
DlgLnkPopWinFeat	: "Mogućnosti popup prozora",
DlgLnkPopResize		: "Promjenjljive veli�?ine",
DlgLnkPopLocation	: "Traka za lokaciju",
DlgLnkPopMenu		: "Izborna traka",
DlgLnkPopScroll		: "Scroll traka",
DlgLnkPopStatus		: "Statusna traka",
DlgLnkPopToolbar	: "Traka s alatima",
DlgLnkPopFullScrn	: "Cijeli ekran (IE)",
DlgLnkPopDependent	: "Ovisno (Netscape)",
DlgLnkPopWidth		: "Širina",
DlgLnkPopHeight		: "Visina",
DlgLnkPopLeft		: "Lijeva pozicija",
DlgLnkPopTop		: "Gornja pozicija",

DlnLnkMsgNoUrl		: "Molimo upišite URL link",
DlnLnkMsgNoEMail	: "Molimo upišite e-mail adresu",
DlnLnkMsgNoAnchor	: "Molimo odaberite sidro",

// Color Dialog
DlgColorTitle		: "Odaberite boju",
DlgColorBtnClear	: "Obriši",
DlgColorHighlight	: "Osvijetli",
DlgColorSelected	: "Odaberi",

// Smiley Dialog
DlgSmileyTitle		: "Ubaci smješka",

// Special Character Dialog
DlgSpecialCharTitle	: "Odaberite posebni karakter",

// Table Dialog
DlgTableTitle		: "Svojstva tablice",
DlgTableRows		: "Redova",
DlgTableColumns		: "Kolona",
DlgTableBorder		: "Veli�?ina okvira",
DlgTableAlign		: "Poravnanje",
DlgTableAlignNotSet	: "<nije postavljeno>",
DlgTableAlignLeft	: "Lijevo",
DlgTableAlignCenter	: "Središnje",
DlgTableAlignRight	: "Desno",
DlgTableWidth		: "Širina",
DlgTableWidthPx		: "piksela",
DlgTableWidthPc		: "postotaka",
DlgTableHeight		: "Visina",
DlgTableCellSpace	: "Prostornost ćelija",
DlgTableCellPad		: "Razmak ćelija",
DlgTableCaption		: "Naslov",

// Table Cell Dialog
DlgCellTitle		: "Svojstva ćelije",
DlgCellWidth		: "Širina",
DlgCellWidthPx		: "piksela",
DlgCellWidthPc		: "postotaka",
DlgCellHeight		: "Visina",
DlgCellWordWrap		: "Word Wrap",
DlgCellWordWrapNotSet	: "<nije postavljeno>",
DlgCellWordWrapYes	: "Da",
DlgCellWordWrapNo	: "Ne",
DlgCellHorAlign		: "Vodoravno poravnanje",
DlgCellHorAlignNotSet	: "<nije postavljeno>",
DlgCellHorAlignLeft	: "Lijevo",
DlgCellHorAlignCenter	: "Središnje",
DlgCellHorAlignRight: "Desno",
DlgCellVerAlign		: "Okomito poravnanje",
DlgCellVerAlignNotSet	: "<nije postavljeno>",
DlgCellVerAlignTop	: "Gornje",
DlgCellVerAlignMiddle	: "Srednišnje",
DlgCellVerAlignBottom	: "Donje",
DlgCellVerAlignBaseline	: "Bazno",
DlgCellRowSpan		: "Spajanje redova",
DlgCellCollSpan		: "Spajanje kolona",
DlgCellBackColor	: "Boja pozadine",
DlgCellBorderColor	: "Boja okvira",
DlgCellBtnSelect	: "Odaberi...",

// Find Dialog
DlgFindTitle		: "Pronađi",
DlgFindFindBtn		: "Pronađi",
DlgFindNotFoundMsg	: "Traženi tekst nije pronađen.",

// Replace Dialog
DlgReplaceTitle			: "Zamijeni",
DlgReplaceFindLbl		: "Pronađi:",
DlgReplaceReplaceLbl	: "Zamijeni sa:",
DlgReplaceCaseChk		: "Usporedi mala/velika slova",
DlgReplaceReplaceBtn	: "Zamijeni",
DlgReplaceReplAllBtn	: "Zamijeni sve",
DlgReplaceWordChk		: "Usporedi cijele rije�?i",

// Paste Operations / Dialog
PasteErrorPaste	: "Sigurnosne postavke Vašeg pretraživa�?a ne dozvoljavaju operacije automatskog ljepljenja. Molimo koristite kraticu na tipkovnici (Ctrl+V).",
PasteErrorCut	: "Sigurnosne postavke Vašeg pretraživa�?a ne dozvoljavaju operacije automatskog izrezivanja. Molimo koristite kraticu na tipkovnici (Ctrl+X).",
PasteErrorCopy	: "Sigurnosne postavke Vašeg pretraživa�?a ne dozvoljavaju operacije automatskog kopiranja. Molimo koristite kraticu na tipkovnici (Ctrl+C).",

PasteAsText		: "Zalijepi kao �?isti tekst",
PasteFromWord	: "Zalijepi iz Worda",

DlgPasteMsg		: "Editor nije mogao automatski zalijepiti zbog  <STRONG>sigurnosnih postavki</STRONG> Vašeg pretraživa�?a.<BR>Molimo zalijepite unutar sljedeće kocke koristeći tipkovnicu (<STRONG>Ctrl+V</STRONG>) i pritisnite na <STRONG>OK</STRONG>.",

// Color Picker
ColorAutomatic	: "Automatski",
ColorMoreColors	: "Više boja...",

// Document Properties
DocProps		: "Svojstva dokumenta",

// Anchor Dialog
DlgAnchorTitle		: "Svojstva sidra",
DlgAnchorName		: "Ime sidra",
DlgAnchorErrorName	: "Molimo unesite ime sidra",

// Speller Pages Dialog
DlgSpellNotInDic		: "Nije u rje�?niku",
DlgSpellChangeTo		: "Promjeni u",
DlgSpellBtnIgnore		: "Zanemari",
DlgSpellBtnIgnoreAll	: "Zanemari sve",
DlgSpellBtnReplace		: "Zamijeni",
DlgSpellBtnReplaceAll	: "Zamijeni sve",
DlgSpellBtnUndo			: "Vrati",
DlgSpellNoSuggestions	: "-Nema preporuke-",
DlgSpellProgress		: "Provjera u tijeku...",
DlgSpellNoMispell		: "Provjera završena: Nema greaka",
DlgSpellNoChanges		: "Provjera završena: Nije napravljena promjena",
DlgSpellOneChange		: "Provjera završena: Jedna rije�? promjenjena",
DlgSpellManyChanges		: "Provjera završena: Promjenjeno %1 rije�?i",

IeSpellDownload			: "Provjera pravopisa nije instalirana. Želite li skinuti provjeru pravopisa?",

// Button Dialog
DlgButtonText	: "Tekst (vrijednost)",
DlgButtonType	: "Vrsta",

// Checkbox and Radio Button Dialogs
DlgCheckboxName		: "Ime",
DlgCheckboxValue	: "Vrijednost",
DlgCheckboxSelected	: "Odabrano",

// Form Dialog
DlgFormName		: "Ime",
DlgFormAction	: "Akcija",
DlgFormMethod	: "Metoda",

// Select Field Dialog
DlgSelectName		: "Ime",
DlgSelectValue		: "Vrijednost",
DlgSelectSize		: "Veli�?ina",
DlgSelectLines		: "linija",
DlgSelectChkMulti	: "Dozvoli višestruki odabir",
DlgSelectOpAvail	: "Dostupne opcije",
DlgSelectOpText		: "Tekst",
DlgSelectOpValue	: "Vrijednost",
DlgSelectBtnAdd		: "Dodaj",
DlgSelectBtnModify	: "Promjeni",
DlgSelectBtnUp		: "Gore",
DlgSelectBtnDown	: "Dolje",
DlgSelectBtnSetValue : "Postavi kao odabranu vrijednost",
DlgSelectBtnDelete	: "Obriši",

// Textarea Dialog
DlgTextareaName	: "Ime",
DlgTextareaCols	: "Kolona",
DlgTextareaRows	: "Redova",

// Text Field Dialog
DlgTextName			: "Ime",
DlgTextValue		: "Vrijednost",
DlgTextCharWidth	: "irina",
DlgTextMaxChars		: "Najviše karaktera",
DlgTextType			: "Vrsta",
DlgTextTypeText		: "Tekst",
DlgTextTypePass		: "Šifra",

// Hidden Field Dialog
DlgHiddenName	: "Ime",
DlgHiddenValue	: "Vrijednost",

// Bulleted List Dialog
BulletedListProp	: "Svojstva liste",
NumberedListProp	: "Svojstva broj�?ane liste",
DlgLstType			: "Vrsta",
DlgLstTypeCircle	: "Krug",
DlgLstTypeDisk		: "Disk",
DlgLstTypeSquare	: "Kvadrat",
DlgLstTypeNumbers	: "Brojevi (1, 2, 3)",
DlgLstTypeLCase		: "Mala slova (a, b, c)",
DlgLstTypeUCase		: "Velika slova (A, B, C)",
DlgLstTypeSRoman	: "Male rimske brojke (i, ii, iii)",
DlgLstTypeLRoman	: "Velike rimske brojke (I, II, III)",

// Document Properties Dialog
DlgDocGeneralTab	: "Općenito",
DlgDocBackTab		: "Pozadina",
DlgDocColorsTab		: "Boje i margine",
DlgDocMetaTab		: "Meta Data",

DlgDocPageTitle		: "Naslov stranice",
DlgDocLangDir		: "Smjer jezika",
DlgDocLangDirLTR	: "S lijeva na desno",
DlgDocLangDirRTL	: "S desna na lijevo",
DlgDocLangCode		: "K&ocirc;d jezika",
DlgDocCharSet		: "Enkodiranje znakova",
DlgDocCharSetOther	: "Ostalo enkodiranje znakova",

DlgDocDocType		: "Zaglavlje vrste dokumenta",
DlgDocDocTypeOther	: "Ostalo zaglavlje vrste dokumenta",
DlgDocIncXHTML		: "Ubaci XHTML deklaracije",
DlgDocBgColor		: "Boja pozadine",
DlgDocBgImage		: "URL slike pozadine",
DlgDocBgNoScroll	: "Pozadine se ne pomi�?e",
DlgDocCText			: "Tekst",
DlgDocCLink			: "Link",
DlgDocCVisited		: "Posjećeni link",
DlgDocCActive		: "Aktivni link",
DlgDocMargins		: "Margine stranice",
DlgDocMaTop			: "Vrh",
DlgDocMaLeft		: "Lijevo",
DlgDocMaRight		: "Desno",
DlgDocMaBottom		: "Dolje",
DlgDocMeIndex		: "Klju�?ne rije�?i dokumenta (odvojene zarezom)",
DlgDocMeDescr		: "Opis dokumenta",
DlgDocMeAuthor		: "Autor",
DlgDocMeCopy		: "Autorska prava",
DlgDocPreview		: "Pregledaj",

// Templates Dialog
Templates			: "Predlošci",
DlgTemplatesTitle	: "Predlošci sadržaja",
DlgTemplatesSelMsg	: "Molimo odaberite predložak koji želite otvoriti<br>(stvarni sadržaj će biti izgubljen):",
DlgTemplatesLoading	: "U�?itavam listu predložaka. Molimo pri�?ekajte...",
DlgTemplatesNoTpl	: "(Nema definiranih predložaka)",

// About Dialog
DlgAboutAboutTab	: "O FCKEditoru",
DlgAboutBrowserInfoTab	: "Podaci o pretraživa�?u",
DlgAboutVersion		: "ina�?ica",
DlgAboutLicense		: "Licencirano pod uvjetima GNU Lesser General Public License",
DlgAboutInfo		: "Za više informacija posjetite"
}