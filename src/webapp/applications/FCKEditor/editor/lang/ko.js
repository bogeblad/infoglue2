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
 * File Name: ko.js
 * 	Danish language file.
 * 
 * Version:  2.0 RC3
 * Modified: 2005-03-01 17:26:17
 * 
 * File Authors:
 * 		Taehwan Kwag (thkwag@nate.com)
 */

var FCKLang =
{
// Language direction : "ltr" (left to right) or "rtl" (right to left).
Dir					: "ltr",

ToolbarCollapse		: "툴바 �?추기",
ToolbarExpand		: "툴바 보�?�기",

// Toolbar Items and Context Menu
Save				: "저장하기",
NewPage				: "새 문서",
Preview				: "미리보기",
Cut					: "잘�?�내기",
Copy				: "복사하기",
Paste				: "붙여넣기",
PasteText			: "�?스트로 붙여넣기",
PasteWord			: "MS Word 형�?�?서 붙여넣기",
Print				: "�?�쇄하기",
SelectAll			: "전체선�?",
RemoveFormat		: "�?�맷 지우기",
InsertLinkLbl		: "�?�?�",
InsertLink			: "�?�?� 삽입/변경",
RemoveLink			: "�?�?� 삭제",
Anchor				: "책갈피 삽입/변경",
InsertImageLbl		: "�?�미지",
InsertImage			: "�?�미지 삽입/변경",
InsertTableLbl		: "표",
InsertTable			: "표 삽입/변경",
InsertLineLbl		: "수�?�선",
InsertLine			: "수�?�선 삽입",
InsertSpecialCharLbl: "특수문�? 삽입",
InsertSpecialChar	: "특수문�? 삽입",
InsertSmileyLbl		: "아�?�콘",
InsertSmiley		: "아�?�콘 삽입",
About				: "FCKeditor�? 대하여",
Bold				: "진하게",
Italic				: "�?�텔릭",
Underline			: "밑줄",
StrikeThrough		: "취소선",
Subscript			: "아래 첨�?",
Superscript			: "위 첨�?",
LeftJustify			: "왼쪽 정렬",
CenterJustify		: "가운�?� 정렬",
RightJustify		: "오른쪽 정렬",
BlockJustify		: "양쪽 맞춤",
DecreaseIndent		: "내어쓰기",
IncreaseIndent		: "들여쓰기",
Undo				: "취소",
Redo				: "재실행",
NumberedListLbl		: "순서있는 목�?",
NumberedList		: "순서있는 목�?",
BulletedListLbl		: "순서없는 목�?",
BulletedList		: "순서없는 목�?",
ShowTableBorders	: "표 테�?리 보기",
ShowDetails			: "문서기호 보기",
Style				: "스타�?�",
FontFormat			: "�?�맷",
Font				: "�?�트",
FontSize			: "글�? �?�기",
TextColor			: "글�? 색�?",
BGColor				: "배경 색�?",
Source				: "소스",
Find				: "찾기",
Replace				: "바꾸기",
SpellCheck			: "철�?검사",
UniversalKeyboard	: "다국어 입력기",

Form			: "�?�",
Checkbox		: "체�?�박스",
RadioButton		: "�?�디오버튼",
TextField		: "입력필드",
Textarea		: "입력�?역",
HiddenField		: "숨김필드",
Button			: "버튼",
SelectionField	: "펼침목�?",
ImageButton		: "�?�미지버튼",

// Context Menu
EditLink			: "�?�?� 수정",
InsertRow			: "가로줄 삽입",
DeleteRows			: "가로줄 삭제",
InsertColumn		: "세로줄 삽입",
DeleteColumns		: "세로줄 삭제",
InsertCell			: "셀 삽입",
DeleteCells			: "셀 삭제",
MergeCells			: "셀 합치기",
SplitCell			: "셀 나누기",
CellProperties		: "셀 �?성",
TableProperties		: "표 �?성",
ImageProperties		: "�?�미지 �?성",

AnchorProp			: "책갈피 �?성",
ButtonProp			: "버튼 �?성",
CheckboxProp		: "체�?�박스 �?성",
HiddenFieldProp		: "숨김필드 �?성",
RadioButtonProp		: "�?�디오버튼 �?성",
ImageButtonProp		: "�?�미지버튼 �?성",
TextFieldProp		: "입력필드 �?성",
SelectionFieldProp	: "펼침목�? �?성",
TextareaProp		: "입력�?역 �?성",
FormProp			: "�?� �?성",

FontFormats			: "Normal;Formatted;Address;Heading 1;Heading 2;Heading 3;Heading 4;Heading 5;Heading 6",	// 2.0: The last entry has been added.

// Alerts and Messages
ProcessingXHTML		: "XHTML 처리중. 잠시만 기다려주십시요.",
Done				: "완료",
PasteWordConfirm	: "붙여넣기 할 �?스트는 MS Word�?서 복사한 것입니다. 붙여넣기 전�? MS Word �?�멧�?� 삭제하시겠습니까?",
NotCompatiblePaste	: "�?� 명령�?� �?�터넷�?�스플로러 5.5 버전 �?��?�?서만 작�?�합니다. �?�멧�?� 삭제하지 않고 붙여넣기 하시겠습니까?",
UnknownToolbarItem	: "알수없는 툴바입니다. : \"%1\"",
UnknownCommand		: "알수없는 기능입니다. : \"%1\"",
NotImplemented		: "기능�?� 실행�?�지 않았습니다.",
UnknownToolbarSet	: "툴바 설정�?� 없습니다. : \"%1\"",

// Dialogs
DlgBtnOK			: "예",
DlgBtnCancel		: "아니오",
DlgBtnClose			: "닫기",
DlgBtnBrowseServer	: "서버 보기",
DlgAdvancedTag		: "�?세히",
DlgOpOther			: "&lt;기타&gt;",

// General Dialogs Labels
DlgGenNotSet		: "&lt;설정�?�지 않�?�&gt;",
DlgGenId			: "ID",
DlgGenLangDir		: "쓰기 방향",
DlgGenLangDirLtr	: "왼쪽�?서 오른쪽 (LTR)",
DlgGenLangDirRtl	: "오른쪽�?서 왼쪽 (RTL)",
DlgGenLangCode		: "언어 코드",
DlgGenAccessKey		: "엑세스 키",
DlgGenName			: "Name",
DlgGenTabIndex		: "탭 순서",
DlgGenLongDescr		: "URL 설명",
DlgGenClass			: "Stylesheet Classes",
DlgGenTitle			: "Advisory Title",
DlgGenContType		: "Advisory Content Type",
DlgGenLinkCharset	: "Linked Resource Charset",
DlgGenStyle			: "Style",

// Image Dialog
DlgImgTitle			: "�?�미지 설정",
DlgImgInfoTab		: "�?�미지 정보",
DlgImgBtnUpload		: "서버로 전송",
DlgImgURL			: "URL",
DlgImgUpload		: "업로드",
DlgImgAlt			: "�?�미지 설명",
DlgImgWidth			: "너비",
DlgImgHeight		: "높�?�",
DlgImgLockRatio		: "비율 유지",
DlgBtnResetSize		: "�?래 �?�기로",
DlgImgBorder		: "테�?리",
DlgImgHSpace		: "수�?�여백",
DlgImgVSpace		: "수�?여백",
DlgImgAlign			: "정렬",
DlgImgAlignLeft		: "왼쪽",
DlgImgAlignAbsBottom: "줄아래(Abs Bottom)",
DlgImgAlignAbsMiddle: "줄중간(Abs Middle)",
DlgImgAlignBaseline	: "기준선",
DlgImgAlignBottom	: "아래",
DlgImgAlignMiddle	: "중간",
DlgImgAlignRight	: "오른쪽",
DlgImgAlignTextTop	: "글�?위(Text Top)",
DlgImgAlignTop		: "위",
DlgImgPreview		: "미리보기",
DlgImgAlertUrl		: "�?�미지 URL�?� 입력하십시요",

// Link Dialog
DlgLnkWindowTitle	: "�?�?�",
DlgLnkInfoTab		: "�?�?� 정보",
DlgLnkTargetTab		: "타겟",

DlgLnkType			: "�?�?� 종류",
DlgLnkTypeURL		: "URL",
DlgLnkTypeAnchor	: "책갈피",
DlgLnkTypeEMail		: "�?�메�?�",
DlgLnkProto			: "프로토콜",
DlgLnkProtoOther	: "&lt;기타&gt;",
DlgLnkURL			: "URL",
DlgLnkAnchorSel		: "책갈피 선�?",
DlgLnkAnchorByName	: "책갈피 �?�름",
DlgLnkAnchorById	: "책갈피 ID",
DlgLnkNoAnchors		: "&lt;문서�? 책갈피가 없습니다.&gt;",
DlgLnkEMail			: "�?�메�?� 주소",
DlgLnkEMailSubject	: "제목",
DlgLnkEMailBody		: "내용",
DlgLnkUpload		: "업로드",
DlgLnkBtnUpload		: "서버로 전송",

DlgLnkTarget		: "타겟",
DlgLnkTargetFrame	: "&lt;프레임&gt;",
DlgLnkTargetPopup	: "&lt;�?업창&gt;",
DlgLnkTargetBlank	: "새 창 (_blank)",
DlgLnkTargetParent	: "부모 창 (_parent)",
DlgLnkTargetSelf	: "현재 창 (_self)",
DlgLnkTargetTop		: "최 �?위 창 (_top)",
DlgLnkTargetFrameName	: "타겟 프레임 �?�름",
DlgLnkPopWinName	: "�?업창 �?�름",
DlgLnkPopWinFeat	: "�?업창 설정",
DlgLnkPopResize		: "�?�기조정",
DlgLnkPopLocation	: "주소표시줄",
DlgLnkPopMenu		: "메뉴바",
DlgLnkPopScroll		: "스�?�롤바",
DlgLnkPopStatus		: "�?태바",
DlgLnkPopToolbar	: "툴바",
DlgLnkPopFullScrn	: "전체화면 (IE)",
DlgLnkPopDependent	: "Dependent (Netscape)",
DlgLnkPopWidth		: "너비",
DlgLnkPopHeight		: "높�?�",
DlgLnkPopLeft		: "왼쪽 위치",
DlgLnkPopTop		: "윗쪽 위치",

DlnLnkMsgNoUrl		: "�?�?� URL�?� 입력하십시요.",
DlnLnkMsgNoEMail	: "�?�메�?�주소를 입력하십시요.",
DlnLnkMsgNoAnchor	: "책갈피명�?� 입력하십시요.",

// Color Dialog
DlgColorTitle		: "색�? 선�?",
DlgColorBtnClear	: "지우기",
DlgColorHighlight	: "현재",
DlgColorSelected	: "선�?�?�",

// Smiley Dialog
DlgSmileyTitle		: "아�?�콘 삽입",

// Special Character Dialog
DlgSpecialCharTitle	: "특수문�? 선�?",

// Table Dialog
DlgTableTitle		: "표 설정",
DlgTableRows		: "가로줄",
DlgTableColumns		: "세로줄",
DlgTableBorder		: "테�?리 �?�기",
DlgTableAlign		: "정렬",
DlgTableAlignNotSet	: "<설정�?�지 않�?�>",
DlgTableAlignLeft	: "왼쪽",
DlgTableAlignCenter	: "가운�?�",
DlgTableAlignRight	: "오른쪽",
DlgTableWidth		: "너비",
DlgTableWidthPx		: "픽셀",
DlgTableWidthPc		: "�?�센트",
DlgTableHeight		: "높�?�",
DlgTableCellSpace	: "셀 간격",
DlgTableCellPad		: "셀 여백",
DlgTableCaption		: "캡션",

// Table Cell Dialog
DlgCellTitle		: "셀 설정",
DlgCellWidth		: "너비",
DlgCellWidthPx		: "픽셀",
DlgCellWidthPc		: "�?�센트",
DlgCellHeight		: "높�?�",
DlgCellWordWrap		: "워드랩",
DlgCellWordWrapNotSet	: "<설정�?�지 않�?�>",
DlgCellWordWrapYes	: "예",
DlgCellWordWrapNo	: "아니오",
DlgCellHorAlign		: "수�?� 정렬",
DlgCellHorAlignNotSet	: "<설정�?�지 않�?�>",
DlgCellHorAlignLeft	: "왼쪽",
DlgCellHorAlignCenter	: "가운�?�",
DlgCellHorAlignRight: "오른쪽",
DlgCellVerAlign		: "수�? 정렬",
DlgCellVerAlignNotSet	: "<설정�?�지 않�?�>",
DlgCellVerAlignTop	: "위",
DlgCellVerAlignMiddle	: "중간",
DlgCellVerAlignBottom	: "아래",
DlgCellVerAlignBaseline	: "기준선",
DlgCellRowSpan		: "세로 합치기",
DlgCellCollSpan		: "가로 합치기",
DlgCellBackColor	: "배경 색�?",
DlgCellBorderColor	: "테�?리 색�?",
DlgCellBtnSelect	: "선�?",

// Find Dialog
DlgFindTitle		: "찾기",
DlgFindFindBtn		: "찾기",
DlgFindNotFoundMsg	: "문�?열�?� 찾�?� 수 없습니다.",

// Replace Dialog
DlgReplaceTitle			: "바꾸기",
DlgReplaceFindLbl		: "찾�?� 문�?열:",
DlgReplaceReplaceLbl	: "바꿀 문�?열:",
DlgReplaceCaseChk		: "대소문�? 구분",
DlgReplaceReplaceBtn	: "바꾸기",
DlgReplaceReplAllBtn	: "모�? 바꾸기",
DlgReplaceWordChk		: "온전한 단어",

// Paste Operations / Dialog
PasteErrorPaste	: "브�?�우저�?� 보안설정때문�? 붙여넣기 기능�?� 실행할 수 없습니다. 키보드 명령�?� 사용하십시요. (Ctrl+V).",
PasteErrorCut	: "브�?�우저�?� 보안설정때문�? 잘�?�내기 기능�?� 실행할 수 없습니다. 키보드 명령�?� 사용하십시요. (Ctrl+X).",
PasteErrorCopy	: "브�?�우저�?� 보안설정때문�? 복사하기 기능�?� 실행할 수 없습니다. 키보드 명령�?� 사용하십시요.  (Ctrl+C).",

PasteAsText		: "�?스트로 붙여넣기",
PasteFromWord	: "MS Word 형�?�?서 붙여넣기",

DlgPasteMsg		: "브�?�우저�?� <STRONG>보안설정/STRONG> 때문�? 붙여넣기 할 수 없습니다. <BR>키보드 명령(<STRONG>Ctrl+V</STRONG>)�?� �?�용하여 붙여넣�?� 다�?� <STRONG>예</STRONG>버튼�?� �?�릭하십시요.",

// Color Picker
ColorAutomatic	: "기본색�?",
ColorMoreColors	: "색�?선�?...",

// Document Properties
DocProps		: "문서 �?성",

// Anchor Dialog
DlgAnchorTitle		: "책갈피 �?성",
DlgAnchorName		: "책갈피 �?�름",
DlgAnchorErrorName	: "책갈피 �?�름�?� 입력하십시요.",

// Speller Pages Dialog
DlgSpellNotInDic		: "사전�? 없는 단어",
DlgSpellChangeTo		: "변경할 단어",
DlgSpellBtnIgnore		: "건너뜀",
DlgSpellBtnIgnoreAll	: "모�? 건너뜀",
DlgSpellBtnReplace		: "변경",
DlgSpellBtnReplaceAll	: "모�? 변경",
DlgSpellBtnUndo			: "취소",
DlgSpellNoSuggestions	: "- 추천단어 없�?� -",
DlgSpellProgress		: "철�?검사를 진행중입니다...",
DlgSpellNoMispell		: "철�?검사 완료: 잘못�?� 철�?가 없습니다.",
DlgSpellNoChanges		: "철�?검사 완료: 변경�?� 단어가 없습니다.",
DlgSpellOneChange		: "철�?검사 완료: 단어가 변경�?�었습니다.",
DlgSpellManyChanges		: "철�?검사 완료: %1 단어가 변경�?�었습니다.",

IeSpellDownload			: "철�? 검사기가 철치�?�지 않았습니다. 지금 다운로드하시겠습니까?",

// Button Dialog
DlgButtonText	: "버튼글�?(값)",
DlgButtonType	: "버튼종류",

// Checkbox and Radio Button Dialogs
DlgCheckboxName		: "�?�름",
DlgCheckboxValue	: "값",
DlgCheckboxSelected	: "선�?�?�",

// Form Dialog
DlgFormName		: "�?��?�름",
DlgFormAction	: "실행경로(Action)",
DlgFormMethod	: "방법(Method)",

// Select Field Dialog
DlgSelectName		: "�?�름",
DlgSelectValue		: "값",
DlgSelectSize		: "세로�?�기",
DlgSelectLines		: "줄",
DlgSelectChkMulti	: "여러항목 선�? 허용",
DlgSelectOpAvail	: "선�?옵션",
DlgSelectOpText		: "�?�름",
DlgSelectOpValue	: "값",
DlgSelectBtnAdd		: "추가",
DlgSelectBtnModify	: "변경",
DlgSelectBtnUp		: "위로",
DlgSelectBtnDown	: "아래로",
DlgSelectBtnSetValue : "선�?�?�것으로 설정",
DlgSelectBtnDelete	: "삭제",

// Textarea Dialog
DlgTextareaName	: "�?�름",
DlgTextareaCols	: "칸수",
DlgTextareaRows	: "줄수",

// Text Field Dialog
DlgTextName			: "�?�름",
DlgTextValue		: "값",
DlgTextCharWidth	: "글�? 너비",
DlgTextMaxChars		: "최대 글�?수",
DlgTextType			: "종류",
DlgTextTypeText		: "문�?열",
DlgTextTypePass		: "비밀번호",

// Hidden Field Dialog
DlgHiddenName	: "�?�름",
DlgHiddenValue	: "값",

// Bulleted List Dialog
BulletedListProp	: "순서없는 목�? �?성",
NumberedListProp	: "순서있는 목�? �?성",
DlgLstType			: "종류",
DlgLstTypeCircle	: "�?(Circle)",
DlgLstTypeDisk		: "둥근�?(Disk)",
DlgLstTypeSquare	: "네모�?(Square)",
DlgLstTypeNumbers	: "번호 (1, 2, 3)",
DlgLstTypeLCase		: "소문�? (a, b, c)",
DlgLstTypeUCase		: "대문�? (A, B, C)",
DlgLstTypeSRoman	: "로마�? 수문�? (i, ii, iii)",
DlgLstTypeLRoman	: "로마�? 대문�? (I, II, III)",

// Document Properties Dialog
DlgDocGeneralTab	: "�?�반",
DlgDocBackTab		: "배경",
DlgDocColorsTab		: "색�? �? 여백",
DlgDocMetaTab		: "메타�?��?�터",

DlgDocPageTitle		: "페�?�지명",
DlgDocLangDir		: "문�? 쓰기방향",
DlgDocLangDirLTR	: "왼쪽�?서 오른쪽 (LTR)",
DlgDocLangDirRTL	: "오른쪽�?서 왼쪽 (RTL)",
DlgDocLangCode		: "언어코드",
DlgDocCharSet		: "�?릭터셋 �?�코딩",
DlgDocCharSetOther	: "다른 �?릭터셋 �?�코딩",

DlgDocDocType		: "문서 헤드",
DlgDocDocTypeOther	: "다른 문서헤드",
DlgDocIncXHTML		: "XHTML 문서정�?� �?�함",
DlgDocBgColor		: "배경색�?",
DlgDocBgImage		: "배경�?�미지 URL",
DlgDocBgNoScroll	: "스�?�롤�?�지않는 배경",
DlgDocCText			: "�?스트",
DlgDocCLink			: "�?�?�",
DlgDocCVisited		: "방문한 �?�?�(Visited)",
DlgDocCActive		: "활성화�?� �?�?�(Active)",
DlgDocMargins		: "페�?�지 여백",
DlgDocMaTop			: "위",
DlgDocMaLeft		: "왼쪽",
DlgDocMaRight		: "오른쪽",
DlgDocMaBottom		: "아래",
DlgDocMeIndex		: "문서 키워드 (콤마로 구분)",
DlgDocMeDescr		: "문서 설명",
DlgDocMeAuthor		: "작성�?",
DlgDocMeCopy		: "저작권",
DlgDocPreview		: "미리보기",

// About Dialog
DlgAboutAboutTab	: "About",
DlgAboutBrowserInfoTab	: "브�?�우저 정보",
DlgAboutVersion		: "버전",
DlgAboutLicense		: "Licensed under the terms of the GNU Lesser General Public License",
DlgAboutInfo		: "For further information go to"
}