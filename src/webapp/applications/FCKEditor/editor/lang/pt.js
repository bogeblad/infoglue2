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
 * File Name: pt.js
 * 	Portuguese language file.
 * 
 * File Authors:
 * 		Francisco Pereira (fjpereira@netcabo.pt)
 */

var FCKLang =
{
// Language direction : "ltr" (left to right) or "rtl" (right to left).
Dir					: "ltr",

ToolbarCollapse		: "Fechar Barra",
ToolbarExpand		: "Expandir Barra",

// Toolbar Items and Context Menu
Save				: "Guardar",
NewPage				: "Nova Página",
Preview				: "Pré-visualizar",
Cut					: "Cortar",
Copy				: "Copiar",
Paste				: "Colar",
PasteText			: "Colar como texto não formatado",
PasteWord			: "Colar do Word",
Print				: "Imprimir",
SelectAll			: "Seleccionar Tudo",
RemoveFormat		: "Eliminar Formato",
InsertLinkLbl		: "Hiperligação",
InsertLink			: "Inserir/Editar Hiperligação",
RemoveLink			: "Eliminar Hiperligação",
Anchor				: " Inserir/Editar Âncora",
InsertImageLbl		: "Imagem",
InsertImage			: "Inserir/Editar Imagem",
InsertTableLbl		: "Tabela",
InsertTable			: "Inserir/Editar Tabela",
InsertLineLbl		: "Linha",
InsertLine			: "Inserir Linha Horizontal",
InsertSpecialCharLbl: "Caracter Especial",
InsertSpecialChar	: "Inserir Caracter Especial",
InsertSmileyLbl		: "Emoticons",
InsertSmiley		: "Inserir Emoticons",
About				: "Acerca do FCKeditor",
Bold				: "Negrito",
Italic				: "Itálico",
Underline			: "Sublinhado",
StrikeThrough		: "Rasurado",
Subscript			: "Superior à Linha",
Superscript			: "Inferior à Linha",
LeftJustify			: "Alinhar à Esquerda",
CenterJustify		: "Alinhar ao Centro",
RightJustify		: "Alinhar à Direita",
BlockJustify		: "Justificado",
DecreaseIndent		: "Diminuir Avanço",
IncreaseIndent		: "Aumentar Avanço",
Undo				: "Anular",
Redo				: "Repetir",
NumberedListLbl		: "Numeração",
NumberedList		: "Inserir/Eliminar Numeração",
BulletedListLbl		: "Marcas",
BulletedList		: "Inserir/Eliminar Marcas",
ShowTableBorders	: "Mostrar Limites da Tabelas",
ShowDetails			: "Mostrar Parágrafo",
Style				: "Estilo",
FontFormat			: "Formato",
Font				: "Tipo de Letra",
FontSize			: "Tamanho",
TextColor			: "Cor do Texto",
BGColor				: "Cor de Fundo",
Source				: "Fonte",
Find				: "Procurar",
Replace				: "Substituir",
SpellCheck			: "Verificação Ortográfica",
UniversalKeyboard	: "Teclado Universal",

Form			: "Formulário",
Checkbox		: "Caixa de Verificação",
RadioButton		: "Botão de Opção",
TextField		: "Campo de Texto",
Textarea		: "�?rea de Texto",
HiddenField		: "Campo Escondido",
Button			: "Botão",
SelectionField	: "Caixa de Combinação",
ImageButton		: "Botão de Imagem",

// Context Menu
EditLink			: "Editar Hiperligação",
InsertRow			: "Inserir Linha",
DeleteRows			: "Eliminar Linhas",
InsertColumn		: "Inserir Coluna",
DeleteColumns		: "Eliminar Coluna",
InsertCell			: "Inserir Célula",
DeleteCells			: "Eliminar Célula",
MergeCells			: "Unir Células",
SplitCell			: "Dividir Célula",
CellProperties		: "Propriedades da Célula",
TableProperties		: "Propriedades da Tabela",
ImageProperties		: "Propriedades da Imagem",

AnchorProp			: "Propriedades da Âncora",
ButtonProp			: "Propriedades do Botão",
CheckboxProp		: "Propriedades da Caixa de Verificação",
HiddenFieldProp		: "Propriedades do Campo Escondido",
RadioButtonProp		: "Propriedades do Botão de Opção",
ImageButtonProp		: " Propriedades do Botão de imagens",
TextFieldProp		: "Propriedades do Campo de Texto",
SelectionFieldProp	: "Propriedades da Caixa de Combinação",
TextareaProp		: "Propriedades da �?rea de Texto",
FormProp			: "Propriedades do Formulário",

FontFormats			: "Normal;Formatado;Endereço;Título 1;Título 2;Título 3;Título 4;Título 5;Título 6",

// Alerts and Messages
ProcessingXHTML		: "A Processar XHTML. Por favor, espere...",
Done				: "Concluído",
PasteWordConfirm	: "O texto que deseja parece ter sido copiado do Word. Deseja limpar a formatação antes de colar?",
NotCompatiblePaste	: "Este comando só está disponível para Internet Explorer versão 5.5 ou superior. Deseja colar sem limpar a formatação?",
UnknownToolbarItem	: "Item de barra desconhecido \"%1\"",
UnknownCommand		: "Nome de comando desconhecido \"%1\"",
NotImplemented		: "Comando não implementado",
UnknownToolbarSet	: "Nome de barra \"%1\" não definido",

// Dialogs
DlgBtnOK			: "OK",
DlgBtnCancel		: "Cancelar",
DlgBtnClose			: "Fechar",
DlgBtnBrowseServer	: "Navegar no Servidor",
DlgAdvancedTag		: "Avançado",
DlgOpOther			: "&lt;Outro&gt;",

// General Dialogs Labels
DlgGenNotSet		: "&lt;Não definido&gt;",
DlgGenId			: "Id",
DlgGenLangDir		: "Orientação de idioma",
DlgGenLangDirLtr	: "Esquerda à Direita (LTR)",
DlgGenLangDirRtl	: "Direita a Esquerda (RTL)",
DlgGenLangCode		: "Código de Idioma",
DlgGenAccessKey		: "Chave de Acesso",
DlgGenName			: "Nome",
DlgGenTabIndex		: "�?ndice de Tubulação",
DlgGenLongDescr		: "Descrição Completa do URL",
DlgGenClass			: "Classes de Estilo de Folhas Classes",
DlgGenTitle			: "Título",
DlgGenContType		: "Tipo de Conteúdo",
DlgGenLinkCharset	: "Fonte de caracteres vinculado",
DlgGenStyle			: "Estilo",

// Image Dialog
DlgImgTitle			: "Propriedades da Imagem",
DlgImgInfoTab		: "Informação da Imagem",
DlgImgBtnUpload		: "Enviar para o Servidor",
DlgImgURL			: "URL",
DlgImgUpload		: "Carregar",
DlgImgAlt			: "Texto Alternativo",
DlgImgWidth			: "Largura",
DlgImgHeight		: "Altura",
DlgImgLockRatio		: "Proporcional",
DlgBtnResetSize		: "Tamanho Original",
DlgImgBorder		: "Limite",
DlgImgHSpace		: "Esp.Horiz",
DlgImgVSpace		: "Esp.Vert",
DlgImgAlign			: "Alinhamento",
DlgImgAlignLeft		: "Esquerda",
DlgImgAlignAbsBottom: "Abs inferior",
DlgImgAlignAbsMiddle: "Abs centro",
DlgImgAlignBaseline	: "Linha de base",
DlgImgAlignBottom	: "Fundo",
DlgImgAlignMiddle	: "Centro",
DlgImgAlignRight	: "Direita",
DlgImgAlignTextTop	: "Topo do texto",
DlgImgAlignTop		: "Topo",
DlgImgPreview		: "Pré-visualizar",
DlgImgAlertUrl		: "Por favor introduza o URL da imagem",
DlgImgLinkTab		: "Hiperligação",

// Link Dialog
DlgLnkWindowTitle	: "Hiperligação",
DlgLnkInfoTab		: "Informação de Hiperligação",
DlgLnkTargetTab		: "Destino",

DlgLnkType			: "Tipo de Hiperligação",
DlgLnkTypeURL		: "URL",
DlgLnkTypeAnchor	: "Referência a esta página",
DlgLnkTypeEMail		: "E-Mail",
DlgLnkProto			: "Protocolo",
DlgLnkProtoOther	: "&lt;outro&gt;",
DlgLnkURL			: "URL",
DlgLnkAnchorSel		: "Seleccionar una referência",
DlgLnkAnchorByName	: "Por Nome de Referência",
DlgLnkAnchorById	: "Por ID de elemento",
DlgLnkNoAnchors		: "&lt;Não há referências disponíveis no documento&gt;",
DlgLnkEMail			: "Endereço de E-Mail",
DlgLnkEMailSubject	: "Título de Mensagem",
DlgLnkEMailBody		: "Corpo da Mensagem",
DlgLnkUpload		: "Carregar",
DlgLnkBtnUpload		: "Enviar ao Servidor",

DlgLnkTarget		: "Destino",
DlgLnkTargetFrame	: "&lt;Frame&gt;",
DlgLnkTargetPopup	: "&lt;Janela de popup&gt;",
DlgLnkTargetBlank	: "Nova Janela(_blank)",
DlgLnkTargetParent	: "Janela Pai (_parent)",
DlgLnkTargetSelf	: "Mesma janela (_self)",
DlgLnkTargetTop		: "Janela primaria (_top)",
DlgLnkTargetFrameName	: "Nome do Frame Destino",
DlgLnkPopWinName	: "Nome da Janela de Popup",
DlgLnkPopWinFeat	: "Características de Janela de Popup",
DlgLnkPopResize		: "Ajustável",
DlgLnkPopLocation	: "Barra de localização",
DlgLnkPopMenu		: "Barra de Menu",
DlgLnkPopScroll		: "Barras de deslocamento",
DlgLnkPopStatus		: "Barra de Estado",
DlgLnkPopToolbar	: "Barra de Ferramentas",
DlgLnkPopFullScrn	: "Janela Completa (IE)",
DlgLnkPopDependent	: "Dependente (Netscape)",
DlgLnkPopWidth		: "Largura",
DlgLnkPopHeight		: "Altura",
DlgLnkPopLeft		: "Posição Esquerda",
DlgLnkPopTop		: "Posição Direita",

DlnLnkMsgNoUrl		: "Por favor insira a hiperligação URL",
DlnLnkMsgNoEMail	: "Por favor insira o endereço de e-mail",
DlnLnkMsgNoAnchor	: "Por favor seleccione uma referência",

// Color Dialog
DlgColorTitle		: "Seleccionar Cor",
DlgColorBtnClear	: "Nenhuma",
DlgColorHighlight	: "Destacado",
DlgColorSelected	: "Seleccionado",

// Smiley Dialog
DlgSmileyTitle		: "Inserir um Emoticon",

// Special Character Dialog
DlgSpecialCharTitle	: "Seleccione um caracter especial",

// Table Dialog
DlgTableTitle		: "Propriedades da Tabela",
DlgTableRows		: "Linhas",
DlgTableColumns		: "Colunas",
DlgTableBorder		: "Tamanho do Limite",
DlgTableAlign		: "Alinhamento",
DlgTableAlignNotSet	: "<Não definido>",
DlgTableAlignLeft	: "Esquerda",
DlgTableAlignCenter	: "Centrado",
DlgTableAlignRight	: "Direita",
DlgTableWidth		: "Largura",
DlgTableWidthPx		: "pixeis",
DlgTableWidthPc		: "percentagem",
DlgTableHeight		: "Altura",
DlgTableCellSpace	: "Esp. e/células",
DlgTableCellPad		: "Esp. interior",
DlgTableCaption		: "Título",

// Table Cell Dialog
DlgCellTitle		: "Propriedades da Célula",
DlgCellWidth		: "Largura",
DlgCellWidthPx		: "pixeis",
DlgCellWidthPc		: "percentagem",
DlgCellHeight		: "Altura",
DlgCellWordWrap		: "Moldar Texto",
DlgCellWordWrapNotSet	: "<Não definido>",
DlgCellWordWrapYes	: "Sim",
DlgCellWordWrapNo	: "Não",
DlgCellHorAlign		: "Alinhamento Horizontal",
DlgCellHorAlignNotSet	: "<Não definido>",
DlgCellHorAlignLeft	: "Esquerda",
DlgCellHorAlignCenter	: "Centrado",
DlgCellHorAlignRight: "Direita",
DlgCellVerAlign		: "Alinhamento Vertical",
DlgCellVerAlignNotSet	: "<Não definido>",
DlgCellVerAlignTop	: "Topo",
DlgCellVerAlignMiddle	: "Médio",
DlgCellVerAlignBottom	: "Fundi",
DlgCellVerAlignBaseline	: "Linha de Base",
DlgCellRowSpan		: "Unir Linhas",
DlgCellCollSpan		: "Unir Colunas",
DlgCellBackColor	: "Cor do Fundo",
DlgCellBorderColor	: "Cor do Limite",
DlgCellBtnSelect	: "Seleccione...",

// Find Dialog
DlgFindTitle		: "Procurar",
DlgFindFindBtn		: "Procurar",
DlgFindNotFoundMsg	: "O texto especificado não foi encontrado.",

// Replace Dialog
DlgReplaceTitle			: "Substituir",
DlgReplaceFindLbl		: "Texto a Procurar:",
DlgReplaceReplaceLbl	: "Substituir por:",
DlgReplaceCaseChk		: "Maiúsculas/Minúsculas",
DlgReplaceReplaceBtn	: "Substituir",
DlgReplaceReplAllBtn	: "Substituir Tudo",
DlgReplaceWordChk		: "Coincidir com toda a palavra",

// Paste Operations / Dialog
PasteErrorPaste	: "A configuração de segurança do navegador não permite a execução automática de operações de colar. Por favor use o teclado (Ctrl+V).",
PasteErrorCut	: "A configuração de segurança do navegador não permite a execução automática de operações de cortar. Por favor use o teclado (Ctrl+X).",
PasteErrorCopy	: "A configuração de segurança do navegador não permite a execução automática de operações de copiar. Por favor use o teclado (Ctrl+C).",

PasteAsText		: "Colar como Texto Simples",
PasteFromWord	: "Colar do Word",

DlgPasteMsg		: "O editor não pode executar automaticamente o colar devido à <STRONG>configuração de segurança</STRONG> do navegador.<BR>Por favor cole dentro do seguinte quadro usando o teclado (<STRONG>Ctrl+V</STRONG>) e pressione <STRONG>OK</STRONG>.",

// Color Picker
ColorAutomatic	: "Automático",
ColorMoreColors	: "Mais Cores...",

// Document Properties
DocProps		: "Propriedades do Documento",

// Anchor Dialog
DlgAnchorTitle		: "Propriedades da Âncora",
DlgAnchorName		: "Nome da Âncora",
DlgAnchorErrorName	: "Por favor, introduza o nome da âncora",

// Speller Pages Dialog
DlgSpellNotInDic		: "Não está num directório",
DlgSpellChangeTo		: "Mudar para",
DlgSpellBtnIgnore		: "Ignorar",
DlgSpellBtnIgnoreAll	: "Ignorar Tudo",
DlgSpellBtnReplace		: "Substituir",
DlgSpellBtnReplaceAll	: "Substituir Tudo",
DlgSpellBtnUndo			: "Anular",
DlgSpellNoSuggestions	: "- Sem sugestões -",
DlgSpellProgress		: "Verificação ortográfica em progresso…",
DlgSpellNoMispell		: "Verificação ortográfica completa: não foram encontrados erros",
DlgSpellNoChanges		: "Verificação ortográfica completa: não houve alteração de palavras",
DlgSpellOneChange		: "Verificação ortográfica completa: uma palavra alterada",
DlgSpellManyChanges		: "Verificação ortográfica completa: %1 palavras alteradas",

IeSpellDownload			: " Verificação ortográfica não instalada. Quer descarregar agora?",

// Button Dialog
DlgButtonText	: "Texto (Valor)",
DlgButtonType	: "Tipo",

// Checkbox and Radio Button Dialogs
DlgCheckboxName		: "Nome",
DlgCheckboxValue	: "Valor",
DlgCheckboxSelected	: "Seleccionado",

// Form Dialog
DlgFormName		: "Nome",
DlgFormAction	: "Acção",
DlgFormMethod	: "Método",

// Select Field Dialog
DlgSelectName		: "Nome",
DlgSelectValue		: "Valor",
DlgSelectSize		: "Tamanho",
DlgSelectLines		: "linhas",
DlgSelectChkMulti	: "Permitir selecções múltiplas",
DlgSelectOpAvail	: "Opções Possíveis",
DlgSelectOpText		: "Texto",
DlgSelectOpValue	: "Valor",
DlgSelectBtnAdd		: "Adicionar",
DlgSelectBtnModify	: "Modificar",
DlgSelectBtnUp		: "Para cima",
DlgSelectBtnDown	: "Para baixo",
DlgSelectBtnSetValue : "Definir um valor por defeito",
DlgSelectBtnDelete	: "Apagar",

// Textarea Dialog
DlgTextareaName	: "Nome",
DlgTextareaCols	: "Colunas",
DlgTextareaRows	: "Linhas",

// Text Field Dialog
DlgTextName			: "Nome",
DlgTextValue		: "Valor",
DlgTextCharWidth	: "Tamanho do caracter",
DlgTextMaxChars		: "Nr. Máximo de Caracteres",
DlgTextType			: "Tipo",
DlgTextTypeText		: "Texto",
DlgTextTypePass		: "Palavra-chave",

// Hidden Field Dialog
DlgHiddenName	: "Nome",
DlgHiddenValue	: "Valor",

// Bulleted List Dialog
BulletedListProp	: "Propriedades da Marca",
NumberedListProp	: "Propriedades da Numeração",
DlgLstType			: "Tipo",
DlgLstTypeCircle	: "Circulo",
DlgLstTypeDisk		: "Disco",
DlgLstTypeSquare	: "Quadrado",
DlgLstTypeNumbers	: "Números (1, 2, 3)",
DlgLstTypeLCase		: "Letras Minúsculas (a, b, c)",
DlgLstTypeUCase		: "Letras Maiúsculas (A, B, C)",
DlgLstTypeSRoman	: "Numeração Romana em Minúsculas (i, ii, iii)",
DlgLstTypeLRoman	: "Numeração Romana em Maiúsculas (I, II, III)",

// Document Properties Dialog
DlgDocGeneralTab	: "Geral",
DlgDocBackTab		: "Fundo",
DlgDocColorsTab		: "Cores e Margens",
DlgDocMetaTab		: "Meta Data",

DlgDocPageTitle		: "Título da Página",
DlgDocLangDir		: "Orientação de idioma",
DlgDocLangDirLTR	: "Esquerda à Direita (LTR)",
DlgDocLangDirRTL	: "Direita à Esquerda (RTL)",
DlgDocLangCode		: "Código de Idioma",
DlgDocCharSet		: "Codificação de Caracteres",
DlgDocCharSetOther	: "Outra Codificação de Caracteres",

DlgDocDocType		: "Tipo de Cabeçalho do Documento",
DlgDocDocTypeOther	: "Outro Tipo de Cabeçalho do Documento",
DlgDocIncXHTML		: "Incluir Declarações XHTML",
DlgDocBgColor		: "Cor de Fundo",
DlgDocBgImage		: "Caminho para a Imagem de Fundo",
DlgDocBgNoScroll	: "Fundo Fixo",
DlgDocCText			: "Texto",
DlgDocCLink			: "Hiperligação",
DlgDocCVisited		: "Hiperligação Visitada",
DlgDocCActive		: "Hiperligação Activa",
DlgDocMargins		: "Margem das Páginas",
DlgDocMaTop			: "Topo",
DlgDocMaLeft		: "Esquerda",
DlgDocMaRight		: "Direita",
DlgDocMaBottom		: "Fundo",
DlgDocMeIndex		: "Palavras de Indexação do Documento (separadas por virgula)",
DlgDocMeDescr		: "Descrição do Documento",
DlgDocMeAuthor		: "Autor",
DlgDocMeCopy		: "Direitos de Autor",
DlgDocPreview		: "Pré-visualizar",

// Templates Dialog
Templates			: "Modelos",
DlgTemplatesTitle	: "Modelo de Conteúdo",
DlgTemplatesSelMsg	: "Por favor, seleccione o modelo a abrir no editor<br>(o conteúdo actual será perdido):",
DlgTemplatesLoading	: "A carregar a lista de modelos. Aguarde por favor...",
DlgTemplatesNoTpl	: "(Sem modelos definidos)",

// About Dialog
DlgAboutAboutTab	: "Acerca",
DlgAboutBrowserInfoTab	: "Informação do Nevegador",
DlgAboutVersion		: "versão",
DlgAboutLicense		: "Licenciado segundo os términos de GNU Lesser General Public License",
DlgAboutInfo		: "Para mais informações por favor dirija-se a"
}