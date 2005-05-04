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
 * File Name: hu.js
 * 	Hungarian language file.
 * 
 * Version:  2.0 RC3
 * Modified: 2005-03-01 17:26:17
 * 
 * File Authors:
 * 		Varga Zsolt (meridian@netteszt.hu)
 */

var FCKLang =
{
// Language direction : "ltr" (left to right) or "rtl" (right to left).
Dir					: "ltr",

ToolbarCollapse		: "Egyszerû eszköztár",
ToolbarExpand		: "Bõvített eszköztár",

// Toolbar Items and Context Menu
Save				: "Mentés",
NewPage				: "Új oldal",
Preview				: "Elõnézet",
Cut					: "Kivágás",
Copy				: "Másolás",
Paste				: "Beillesztés",
PasteText			: "Beillesztés formázatlan szövegként",
PasteWord			: "Beillesztés Wordbõl",
Print				: "Nyomtatás",
SelectAll			: "Minden kijelölése",
RemoveFormat		: "Formázás törlése",
InsertLinkLbl		: "Hivatkozás",
InsertLink			: "Hivatkozás beillesztése/módosítása",
RemoveLink			: "Hivatkozás törlése",
Anchor				: "Horgony beillesztése/szerkesztése",
InsertImageLbl		: "Kép",
InsertImage			: "Kép beillesztése/módosítása",
InsertTableLbl		: "Táblázat",
InsertTable			: "Táblázat beillesztése/módosítása",
InsertLineLbl		: "Vonal",
InsertLine			: "Elválasztóvonal beillesztése",
InsertSpecialCharLbl: "Speciális karakter",
InsertSpecialChar	: "Speciális karakter beillesztése",
InsertSmileyLbl		: "Hangulatjelek",
InsertSmiley		: "Hangulatjelek beillesztése",
About				: "FCKeditor névjegy",
Bold				: "Félkövér",
Italic				: "Dõlt",
Underline			: "Aláhúzott",
StrikeThrough		: "�?thúzott",
Subscript			: "Alsó index",
Superscript			: "Felsõ index",
LeftJustify			: "Balra",
CenterJustify		: "Középre",
RightJustify		: "Jobbra",
BlockJustify		: "Sorkizárt",
DecreaseIndent		: "Behúzás csökkentése",
IncreaseIndent		: "Behúzás növelése",
Undo				: "Visszavonás",
Redo				: "Ismétlés",
NumberedListLbl		: "Számozás",
NumberedList		: "Számozás beillesztése/törlése",
BulletedListLbl		: "Felsorolás",
BulletedList		: "Felsorolás beillesztése/törlése",
ShowTableBorders	: "Táblázat szegély mutatása",
ShowDetails			: "Részletek mutatása",
Style				: "Stílus",
FontFormat			: "Formátum",
Font				: "Betûtipus",
FontSize			: "Méret",
TextColor			: "Betûszín",
BGColor				: "Háttérszín",
Source				: "Forráskód",
Find				: "Keresés",
Replace				: "Csere",
SpellCheck			: "Helyesírásellenőrzés",
UniversalKeyboard	: "�?ltalános billentyűzet",

Form			: "Űrlap",
Checkbox		: "Jelölőnégyzet",
RadioButton		: "Választógomb",
TextField		: "Szövegmező",
Textarea		: "Szövegterület",
HiddenField		: "Rejtettmező",
Button			: "Gomb",
SelectionField	: "Választómező",
ImageButton		: "Képgomb",

// Context Menu
EditLink			: "Hivatkozás módosítása",
InsertRow			: "Sor beszúrása",
DeleteRows			: "Sor(ok) törlése",
InsertColumn		: "Oszlop beszúrása",
DeleteColumns		: "Oszlop(ok) törlése",
InsertCell			: "Cella beszúrása",
DeleteCells			: "Cellák törlése",
MergeCells			: "Cellák egyesítése",
SplitCell			: "Cellák szétválasztása",
CellProperties		: "Cellák tulajdonsága",
TableProperties		: "Táblázat tulajdonsága",
ImageProperties		: "Kép tulajdonsága",

AnchorProp			: "Horgony(ok) tulajdonsága(i)",
ButtonProp			: "Gomb(ok) tulajdonsága(i) ",
CheckboxProp		: "Jelölőnégyzet(ek) tulajdonsága(i)",
HiddenFieldProp		: "Rejtettmező(k) tulajdonsága(i)",
RadioButtonProp		: "Választógomb(ok) tulajdonsága(i)",
ImageButtonProp		: "Képgomb(ok) tulajdonsága(i)",
TextFieldProp		: "Szövegmező(k) tulajdonsága(i)",
SelectionFieldProp	: "Választómező(k) tulajdonsága(i)",
TextareaProp		: "Szövegterület(ek) tulajdonsága(i)",
FormProp			: "Űrlap(ok) tulajdonsága(i)",

FontFormats			: "Normál;Formázott;Címsor;Fejléc 1;Fejléc 2;Fejléc 3;Fejléc 4;Fejléc 5;Fejléc 6;Bekezdés (DIV)",	// 2.0: The last entry has been added.

// Alerts and Messages
ProcessingXHTML		: "XHTML feldolgozása. Kérem várjon...",
Done				: "Kész",
PasteWordConfirm	: "A szöveg amit be szeretnél illeszteni úgy néz ki Word-bõl van másolva. Do you want to clean it before pasting?",
NotCompatiblePaste	: "Ez a parancs csak Internet Explorer 5.5 verziótól használható (Firefox rulez). Do you want to paste without cleaning?",
UnknownToolbarItem	: "Ismeretlen eszköztár elem \"%1\"",
UnknownCommand		: "Ismeretlen parancs \"%1\"",
NotImplemented		: "A parancs nincs beágyazva",
UnknownToolbarSet	: "Eszközkészlet beállítás \"%1\" nem létezik",

// Dialogs
DlgBtnOK			: "OK",
DlgBtnCancel		: "Mégsem",
DlgBtnClose			: "Bezárás",
DlgBtnBrowseServer	: "Szerver tallózása",
DlgAdvancedTag		: "Haladó",
DlgOpOther			: "Egyéb",

// General Dialogs Labels
DlgGenNotSet		: "&lt;nincs beállítva&gt;",
DlgGenId			: "Azonosító",
DlgGenLangDir		: "Nyelv útmutató",
DlgGenLangDirLtr	: "Balról jobbra",
DlgGenLangDirRtl	: "Jobbról balra",
DlgGenLangCode		: "Nyelv kód",
DlgGenAccessKey		: "Elérési kulcs",
DlgGenName			: "Név",
DlgGenTabIndex		: "Tabulátor index",
DlgGenLongDescr		: "Hosszú URL",
DlgGenClass			: "Stíluskészlet",
DlgGenTitle			: "Advisory Title",
DlgGenContType		: "Advisory Content Type",
DlgGenLinkCharset	: "Hivatkozott kódlap készlet",
DlgGenStyle			: "Stílus",

// Image Dialog
DlgImgTitle			: "Kép tulajdonsága",
DlgImgInfoTab		: "Kép információ",
DlgImgBtnUpload		: "Küldés a szervernek",
DlgImgURL			: "URL",
DlgImgUpload		: "Feltöltés",
DlgImgAlt			: "Buborék szöveg",
DlgImgWidth			: "Szélesség",
DlgImgHeight		: "Magasság",
DlgImgLockRatio		: "Arány megtartása",
DlgBtnResetSize		: "Eredeti méret",
DlgImgBorder		: "Keret",
DlgImgHSpace		: "Vízsz. táv",
DlgImgVSpace		: "Függ. táv",
DlgImgAlign			: "Igazítás",
DlgImgAlignLeft		: "Bal",
DlgImgAlignAbsBottom: "Legaljára",
DlgImgAlignAbsMiddle: "Közepére",
DlgImgAlignBaseline	: "Baseline",
DlgImgAlignBottom	: "Aljára",
DlgImgAlignMiddle	: "Középre",
DlgImgAlignRight	: "Jobbra",
DlgImgAlignTextTop	: "Szöveg tetjére",
DlgImgAlignTop		: "Tetejére",
DlgImgPreview		: "Elõnézet",
DlgImgAlertUrl		: "Töltse ki a kép URL-ét",

// Link Dialog
DlgLnkWindowTitle	: "Hivatkozás",
DlgLnkInfoTab		: "Hivatkozás információ",
DlgLnkTargetTab		: "Cél",

DlgLnkType			: "Hivatkozás tipusa",
DlgLnkTypeURL		: "URL",
DlgLnkTypeAnchor	: "Horgony az oldalon",
DlgLnkTypeEMail		: "E-Mail",
DlgLnkProto			: "Protokoll",
DlgLnkProtoOther	: "&lt;más&gt;",
DlgLnkURL			: "URL",
DlgLnkAnchorSel		: "Horgony választása",
DlgLnkAnchorByName	: "Horgony név szerint",
DlgLnkAnchorById	: "Azonosító szerint elõsorban ",
DlgLnkNoAnchors		: "&lt;Nincs horgony a dokumentumban&gt;",
DlgLnkEMail			: "E-Mail cím",
DlgLnkEMailSubject	: "Üzenet tárgya",
DlgLnkEMailBody		: "Üzenet",
DlgLnkUpload		: "Feltöltés",
DlgLnkBtnUpload		: "Küldés a szerverhez",

DlgLnkTarget		: "Cél",
DlgLnkTargetFrame	: "&lt;keret&gt;",
DlgLnkTargetPopup	: "&lt;felugró ablak&gt;",
DlgLnkTargetBlank	: "Új ablak (_blank)",
DlgLnkTargetParent	: "Szülõ ablak (_parent)",
DlgLnkTargetSelf	: "Azonos ablak (_self)",
DlgLnkTargetTop		: "Legfelsõ ablak (_top)",
DlgLnkTargetFrameName	: "Cél frame neve",
DlgLnkPopWinName	: "Felugró ablak neve",
DlgLnkPopWinFeat	: "Felugró ablak jellemzõi",
DlgLnkPopResize		: "Méretezhetõ",
DlgLnkPopLocation	: "Location Bar",
DlgLnkPopMenu		: "Menü sor",
DlgLnkPopScroll		: "Gördítõsáv",
DlgLnkPopStatus		: "�?llapotsor",
DlgLnkPopToolbar	: "Eszköztár",
DlgLnkPopFullScrn	: "Teljes képernyõ (IE)",
DlgLnkPopDependent	: "Netscape sajátosság",
DlgLnkPopWidth		: "Szélesség",
DlgLnkPopHeight		: "Magasság",
DlgLnkPopLeft		: "Bal pozíció",
DlgLnkPopTop		: "Felsõ pozíció",

DlnLnkMsgNoUrl		: "Adja meg a hivatkozás URL-ét",
DlnLnkMsgNoEMail	: "Adja meg az e-mail címet",
DlnLnkMsgNoAnchor	: "Válasszon egy horgonyt",

// Color Dialog
DlgColorTitle		: "Szinválasztás",
DlgColorBtnClear	: "Törlés",
DlgColorHighlight	: "Világos rész",
DlgColorSelected	: "Választott",

// Smiley Dialog
DlgSmileyTitle		: "Hangulatjel beszúrása",

// Special Character Dialog
DlgSpecialCharTitle	: "Speciális karakter választása",

// Table Dialog
DlgTableTitle		: "Táblázat tulajdonságai",
DlgTableRows		: "Sorok",
DlgTableColumns		: "Oszlopok",
DlgTableBorder		: "Szegélyméret",
DlgTableAlign		: "Igazítás",
DlgTableAlignNotSet	: "<Nincs beállítva>",
DlgTableAlignLeft	: "Bal",
DlgTableAlignCenter	: "Közép",
DlgTableAlignRight	: "Jobb",
DlgTableWidth		: "Szélesség",
DlgTableWidthPx		: "képpontok",
DlgTableWidthPc		: "százalék",
DlgTableHeight		: "Magasság",
DlgTableCellSpace	: "Cell spacing",
DlgTableCellPad		: "Cell padding",
DlgTableCaption		: "Felirat",

// Table Cell Dialog
DlgCellTitle		: "Cella tulajdonságai",
DlgCellWidth		: "Szélesség",
DlgCellWidthPx		: "képpontok",
DlgCellWidthPc		: "százalék",
DlgCellHeight		: "Height",
DlgCellWordWrap		: "Sortörés",
DlgCellWordWrapNotSet	: "&lt;Nincs beállítva&gt;",
DlgCellWordWrapYes	: "Igen",
DlgCellWordWrapNo	: "Nem",
DlgCellHorAlign		: "Vízszintes igazítás",
DlgCellHorAlignNotSet	: "&lt;Nincs beállítva&gt;",
DlgCellHorAlignLeft	: "Bal",
DlgCellHorAlignCenter	: "Közép",
DlgCellHorAlignRight: "Jobb",
DlgCellVerAlign		: "Függõleges igazítás",
DlgCellVerAlignNotSet	: "&lt;Nincs beállítva&gt;",
DlgCellVerAlignTop	: "Tetejére",
DlgCellVerAlignMiddle	: "Középre",
DlgCellVerAlignBottom	: "Aljára",
DlgCellVerAlignBaseline	: "Egyvonalba",
DlgCellRowSpan		: "Sorok egyesítése",
DlgCellCollSpan		: "Oszlopok egyesítése",
DlgCellBackColor	: "Háttérszín",
DlgCellBorderColor	: "Szegélyszín",
DlgCellBtnSelect	: "Kiválasztás...",

// Find Dialog
DlgFindTitle		: "Keresés",
DlgFindFindBtn		: "Keresés",
DlgFindNotFoundMsg	: "A keresett szöveg nem található.",

// Replace Dialog
DlgReplaceTitle			: "Csere",
DlgReplaceFindLbl		: "Keresendõ:",
DlgReplaceReplaceLbl	: "Cserélendõ:",
DlgReplaceCaseChk		: "Találatok",
DlgReplaceReplaceBtn	: "Csere",
DlgReplaceReplAllBtn	: "Összes cseréje",
DlgReplaceWordChk		: "Egész dokumentumban",

// Paste Operations / Dialog
PasteErrorPaste	: "A böngészõ biztonsági beállításai nem engedélyezik a szerkesztõnek, hogy végrehatjsa a beillesztés mûveletet.Használja az alábbi billentyûzetkombinációt (Ctrl+V).",
PasteErrorCut	: "A böngészõ biztonsági beállításai nem engedélyezik a szerkesztõnek, hogy végrehatjsa a kivágás mûveletet.Használja az alábbi billentyûzetkombinációt (Ctrl+X).",
PasteErrorCopy	: "A böngészõ biztonsági beállításai nem engedélyezik a szerkesztõnek, hogy végrehatjsa a másolás mûveletet.Használja az alábbi billentyûzetkombinációt (Ctrl+X).",

PasteAsText		: "Beillesztés formázatlan szövegként",
PasteFromWord	: "Beillesztés Wordbõl",

DlgPasteMsg		: "A szerkesztõ nem képes végrehajtani a beillesztési mûveletet a bõngészõ <STRONG>biztonsági beállításai</STRONG> miatt.<BR>Másolja be az alábbi mezõbe a (<STRONG>Ctrl+V</STRONG>) billentyûzetkombinációval és kattintson <STRONG>OK</STRONG> gombra.",

// Color Picker
ColorAutomatic	: "Automatikus",
ColorMoreColors	: "Több szín...",

// Document Properties
DocProps		: "Dokumentum tulajdonsága",

// Anchor Dialog
DlgAnchorTitle		: "Horgony tulajdonsága",
DlgAnchorName		: "Horgony neve",
DlgAnchorErrorName	: "Kérem adja meg a horgony nevét",

// Speller Pages Dialog
DlgSpellNotInDic		: "Nincs a könyvtárban",
DlgSpellChangeTo		: "�?tváltás",
DlgSpellBtnIgnore		: "Kihagyja",
DlgSpellBtnIgnoreAll	: "Összeset kihagyja",
DlgSpellBtnReplace		: "Csere",
DlgSpellBtnReplaceAll	: "Összes cseréje",
DlgSpellBtnUndo			: "Visszavonás",
DlgSpellNoSuggestions	: "Nincs feltevés",
DlgSpellProgress		: "Helyesírásellenőrzés folyamatban...",
DlgSpellNoMispell		: "Helyesírásellenőrzés kész: Nem találtam hibát",
DlgSpellNoChanges		: "Helyesírásellenőrzés kész: Nincs változtatott szó",
DlgSpellOneChange		: "Helyesírásellenőrzés kész: Egy szó cserélve",
DlgSpellManyChanges		: "Helyesírásellenőrzés kész: %1 szó cserélve",

IeSpellDownload			: "A helyesírásellenőrző nincs telepítve. Szeretné letölteni most?",

// Button Dialog
DlgButtonText	: "Szöveg (Érték)",
DlgButtonType	: "Típus",

// Checkbox and Radio Button Dialogs
DlgCheckboxName		: "Név",
DlgCheckboxValue	: "Érték",
DlgCheckboxSelected	: "Választott",

// Form Dialog
DlgFormName		: "Név",
DlgFormAction	: "Esemény",
DlgFormMethod	: "Metódus",

// Select Field Dialog
DlgSelectName		: "Név",
DlgSelectValue		: "Érték",
DlgSelectSize		: "Méret",
DlgSelectLines		: "sorok",
DlgSelectChkMulti	: "Engedi a többszörös kiválasztást",
DlgSelectOpAvail	: "Elérhető opciók",
DlgSelectOpText		: "Szöveg",
DlgSelectOpValue	: "Érték",
DlgSelectBtnAdd		: "Bővít",
DlgSelectBtnModify	: "Módosít",
DlgSelectBtnUp		: "Fel",
DlgSelectBtnDown	: "Le",
DlgSelectBtnSetValue : "Beállítja a kiválasztott értéket",
DlgSelectBtnDelete	: "Töröl",

// Textarea Dialog
DlgTextareaName	: "Név",
DlgTextareaCols	: "Oszlopok",
DlgTextareaRows	: "Sorok",

// Text Field Dialog
DlgTextName			: "Név",
DlgTextValue		: "Érték",
DlgTextCharWidth	: "Karakter szélesség",
DlgTextMaxChars		: "Maximum karakterek",
DlgTextType			: "Típus",
DlgTextTypeText		: "Szöveg",
DlgTextTypePass		: "Jelszó",

// Hidden Field Dialog
DlgHiddenName	: "Név",
DlgHiddenValue	: "Érték",

// Bulleted List Dialog
BulletedListProp	: "Felsorolás tulajdonságai",
NumberedListProp	: "Számozás tulajdonságai",
DlgLstType			: "Típus",
DlgLstTypeCircle	: "Ciklus",
DlgLstTypeDisk		: "Lemez",
DlgLstTypeSquare	: "Négyzet",
DlgLstTypeNumbers	: "Számok (1, 2, 3)",
DlgLstTypeLCase		: "Kisbetűs (a, b, c)",
DlgLstTypeUCase		: "Nagybetűs (a, b, c)",
DlgLstTypeSRoman	: "Kis római számok (i, ii, iii)",
DlgLstTypeLRoman	: "Nagy római számok (I, II, III)",

// Document Properties Dialog
DlgDocGeneralTab	: "�?ltalános",
DlgDocBackTab		: "Háttér",
DlgDocColorsTab		: "Színek és margók",
DlgDocMetaTab		: "Meta adatok",

DlgDocPageTitle		: "Oldalcím",
DlgDocLangDir		: "Nyelv utasítás",
DlgDocLangDirLTR	: "Balról jobbra (LTR)",
DlgDocLangDirRTL	: "Jobbról balra (RTL)",
DlgDocLangCode		: "Nyelv kód",
DlgDocCharSet		: "Karakterkódolás",
DlgDocCharSetOther	: "Más karakterkódolás",

DlgDocDocType		: "Dokumentum címsor típus",
DlgDocDocTypeOther	: "Más dokumentum címsor típus",
DlgDocIncXHTML		: "XHTML elemeket tartalmaz",
DlgDocBgColor		: "Háttérszín",
DlgDocBgImage		: "Háttérkép cím",
DlgDocBgNoScroll	: "Nem gördíthető háttér",
DlgDocCText			: "Szöveg",
DlgDocCLink			: "Cím",
DlgDocCVisited		: "Látogatott cím",
DlgDocCActive		: "Aktív cím",
DlgDocMargins		: "Oldal margók",
DlgDocMaTop			: "Felső",
DlgDocMaLeft		: "Bal",
DlgDocMaRight		: "Jobb",
DlgDocMaBottom		: "Felül",
DlgDocMeIndex		: "Dokumentum keresőszavak (vesszővel elválasztva)",
DlgDocMeDescr		: "Dokumentum leírás",
DlgDocMeAuthor		: "Szerző",
DlgDocMeCopy		: "Szerzői jog",
DlgDocPreview		: "Előnézet",

// About Dialog
DlgAboutAboutTab	: "About",
DlgAboutBrowserInfoTab	: "Böngésző információ",
DlgAboutVersion		: "verzió",
DlgAboutLicense		: "GNU Lesser General Public License szabadalom alá tartozik",
DlgAboutInfo		: "További információkért menjen"
}