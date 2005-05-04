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
 * File Name: pt-br.js
 * 	Brazilian Portuguese language file.
 * 
 * Version:  2.0 RC3
 * Modified: 2005-03-01 17:26:18
 * 
 * File Authors:
 * 		Carlos Alberto Tomatis Loth (carlos.loth@conectait.com.br)
 */

var FCKLang =
{
// Language direction : "ltr" (left to right) or "rtl" (right to left).
Dir					: "ltr",

ToolbarCollapse		: "Ocultar Barra de Ferramentas",
ToolbarExpand		: "Exibir Barra de Ferramentas",

// Toolbar Items and Context Menu
Save				: "Salvar",
NewPage				: "Novo",
Preview				: "Visualizar",
Cut					: "Recortar",
Copy				: "Copiar",
Paste				: "Colar",
PasteText			: "Colar como Texto sem Formatação",
PasteWord			: "Colar do Word",
Print				: "Imprimir",
SelectAll			: "Selecionar Tudo",
RemoveFormat		: "Remover Formatação",
InsertLinkLbl		: "Hiperlink",
InsertLink			: "Inserir/Editar Hiperlink",
RemoveLink			: "Remover Hiperlink",
Anchor				: "Inserir/Editar Âncora",
InsertImageLbl		: "Figura",
InsertImage			: "Inserir/Editar Figura",
InsertTableLbl		: "Tabela",
InsertTable			: "Inserir/Editar Tabela",
InsertLineLbl		: "Linha",
InsertLine			: "Inserir Linha Horizontal",
InsertSpecialCharLbl: "Caracteres Especiais",
InsertSpecialChar	: "Inserir Caractere Especial",
InsertSmileyLbl		: "Emoticon",
InsertSmiley		: "Inserir Emoticon",
About				: "Sobre FCKeditor",
Bold				: "Negrito",
Italic				: "Itálico",
Underline			: "Sublinhado",
StrikeThrough		: "Tachado",
Subscript			: "Subscrito",
Superscript			: "Sobrescrito",
LeftJustify			: "Alinhar Esquerda",
CenterJustify		: "Centralizar",
RightJustify		: "Alinhar Direita",
BlockJustify		: "Justificado",
DecreaseIndent		: "Diminuir Recuo",
IncreaseIndent		: "Aumentar Recuo",
Undo				: "Desfazer",
Redo				: "Refazer",
NumberedListLbl		: "Numeração",
NumberedList		: "Inserir/Remover Numeração",
BulletedListLbl		: "Marcadores",
BulletedList		: "Inserir/Remover Marcadores",
ShowTableBorders	: "Exibir Bordas da Tabela",
ShowDetails			: "Exibir Detalhes",
Style				: "Estilo",
FontFormat			: "Formatação",
Font				: "Fonte",
FontSize			: "Tamanho",
TextColor			: "Cor do Texto",
BGColor				: "Cor do Plano de Fundo",
Source				: "Código-Fonte",
Find				: "Localizar",
Replace				: "Substituir",
SpellCheck			: "Verificar Ortografia",
UniversalKeyboard	: "Teclado Universal",

Form			: "Formulário",
Checkbox		: "Caixa de Seleção",
RadioButton		: "Botão de Opção",
TextField		: "Caixa de Texto",
Textarea		: "�?rea de Texto",
HiddenField		: "Campo Oculto",
Button			: "Botão",
SelectionField	: "Caixa de Listagem",
ImageButton		: "Botão de Imagem",

// Context Menu
EditLink			: "Editar Hiperlink",
InsertRow			: "Inserir Linha",
DeleteRows			: "Remover Linhas",
InsertColumn		: "Inserir Coluna",
DeleteColumns		: "Remover Colunas",
InsertCell			: "Inserir Células",
DeleteCells			: "Remover Células",
MergeCells			: "Mesclar Células",
SplitCell			: "Dividir Célular",
CellProperties		: "Formatar Célula",
TableProperties		: "Formatar Tabela",
ImageProperties		: "Formatar Figura",

AnchorProp			: "Formatar Âncora",
ButtonProp			: "Formatar Botão",
CheckboxProp		: "Formatar Caixa de Seleção",
HiddenFieldProp		: "Formatar Campo Oculto",
RadioButtonProp		: "Formatar Botão de Opção",
ImageButtonProp		: "Formatar Botão de Imagem",
TextFieldProp		: "Formatar Caixa de Texto",
SelectionFieldProp	: "Formatar Caixa de Listagem",
TextareaProp		: "Formatar �?rea de Texto",
FormProp			: "Formatar Formulário",

FontFormats			: "Normal;Formatado;Endereço;Título 1;Título 2;Título 3;Título 4;Título 5;Título 6",	// 2.0: The last entry has been added.

// Alerts and Messages
ProcessingXHTML		: "Processando XHTML. Por favor, aguarde...",
Done				: "Pronto",
PasteWordConfirm	: "O texto que você deseja colar parece ter sido copiado do Word. Você gostaria de remover a formatação antes de colar?",
NotCompatiblePaste	: "Este comando está disponível para o navegador Internet Explorer 5.5 ou superior. Você gostaria de colar sem remover a formatação?",
UnknownToolbarItem	: "O item da barra de ferramentas \"%1\" não é reconhecido",
UnknownCommand		: "O comando \"%1\" não é reconhecido",
NotImplemented		: "O comando não foi implementado",
UnknownToolbarSet	: "A barra de ferramentas \"%1\" não existe",

// Dialogs
DlgBtnOK			: "OK",
DlgBtnCancel		: "Cancelar",
DlgBtnClose			: "Fechar",
DlgBtnBrowseServer	: "Localizar no Servidor",
DlgAdvancedTag		: "Avançado",
DlgOpOther			: "&lt;Outros&gt;",

// General Dialogs Labels
DlgGenNotSet		: "&lt;não ajustado&gt;",
DlgGenId			: "Id",
DlgGenLangDir		: "Direção do idioma",
DlgGenLangDirLtr	: "Esquerda para Direita (LTR)",
DlgGenLangDirRtl	: "Direita para Esquerda (RTL)",
DlgGenLangCode		: "Idioma",
DlgGenAccessKey		: "Chave de Acesso",
DlgGenName			: "Nome",
DlgGenTabIndex		: "�?ndice de Tabulação",
DlgGenLongDescr		: "Descrição da URL",
DlgGenClass			: "Classe de Folhas de Estilo",
DlgGenTitle			: "Título",
DlgGenContType		: "Tipo de Conteúdo",
DlgGenLinkCharset	: "Conjunto de Caracteres do Hiperlink",
DlgGenStyle			: "Estilos",

// Image Dialog
DlgImgTitle			: "Formatar Figura",
DlgImgInfoTab		: "Informações da Figura",
DlgImgBtnUpload		: "Enviar para o Servidor",
DlgImgURL			: "URL",
DlgImgUpload		: "Submeter",
DlgImgAlt			: "Texto Alternativo",
DlgImgWidth			: "Largura",
DlgImgHeight		: "Altura",
DlgImgLockRatio		: "Manter proporções",
DlgBtnResetSize		: "Redefinir para o Tamanho Original",
DlgImgBorder		: "Borda",
DlgImgHSpace		: "Horizontal",
DlgImgVSpace		: "Vertical",
DlgImgAlign			: "Alinhamento",
DlgImgAlignLeft		: "Esquerda",
DlgImgAlignAbsBottom: "Inferior Absoluto",
DlgImgAlignAbsMiddle: "Centralizado Absoluto",
DlgImgAlignBaseline	: "Baseline",
DlgImgAlignBottom	: "Inferior",
DlgImgAlignMiddle	: "Centralizado",
DlgImgAlignRight	: "Direita",
DlgImgAlignTextTop	: "Superior Absoluto",
DlgImgAlignTop		: "Superior",
DlgImgPreview		: "Visualização",
DlgImgAlertUrl		: "Por favor, digite o URL da figura.",

// Link Dialog
DlgLnkWindowTitle	: "Hiperlink",
DlgLnkInfoTab		: "Informações do hiperlink",
DlgLnkTargetTab		: "Informações de destino",

DlgLnkType			: "Tipo de hiperlink",
DlgLnkTypeURL		: "URL",
DlgLnkTypeAnchor	: "Âncora nesta página",
DlgLnkTypeEMail		: "E-Mail",
DlgLnkProto			: "Protocolo",
DlgLnkProtoOther	: "&lt;outro&gt;",
DlgLnkURL			: "URL do hiperlink",
DlgLnkAnchorSel		: "Selecione uma âncora",
DlgLnkAnchorByName	: "Pelo Nome da âncora",
DlgLnkAnchorById	: "Pelo Id do Elemento",
DlgLnkNoAnchors		: "&lt;Não há âncoras disponíveis neste documento&gt;",
DlgLnkEMail			: "Endereço E-Mail",
DlgLnkEMailSubject	: "Assunto da Mensagem",
DlgLnkEMailBody		: "Corpo da Mesagem",
DlgLnkUpload		: "Enviar ao Servidor",
DlgLnkBtnUpload		: "Enviar ao Servidor",

DlgLnkTarget		: "Destino",
DlgLnkTargetFrame	: "&lt;quadro&gt;",
DlgLnkTargetPopup	: "&lt;janela popup&gt;",
DlgLnkTargetBlank	: "Nova Janela (_blank)",
DlgLnkTargetParent	: "Janela Pai (_parent)",
DlgLnkTargetSelf	: "Mesma Janela (_self)",
DlgLnkTargetTop		: "Janela Superior (_top)",
DlgLnkTargetFrameName	: "Nome do Frame de Destino",
DlgLnkPopWinName	: "Nome da Janela Pop-up",
DlgLnkPopWinFeat	: "Atributos da Janela Pop-up",
DlgLnkPopResize		: "Redimensionável",
DlgLnkPopLocation	: "Barra de Endereços",
DlgLnkPopMenu		: "Barra de Menus",
DlgLnkPopScroll		: "Barras de Rolagem",
DlgLnkPopStatus		: "Barra de Status",
DlgLnkPopToolbar	: "Barra de Ferramentas",
DlgLnkPopFullScrn	: "Modo Tela Cheia (IE)",
DlgLnkPopDependent	: "Dependente (Netscape)",
DlgLnkPopWidth		: "Largura",
DlgLnkPopHeight		: "Altura",
DlgLnkPopLeft		: "Esquerda",
DlgLnkPopTop		: "Superior",

DlnLnkMsgNoUrl		: "Por favor, digite o endereço do Hiperlink",
DlnLnkMsgNoEMail	: "Por favor, digite o endereço de e-mail",
DlnLnkMsgNoAnchor	: "Por favor, selecione uma âncora",

// Color Dialog
DlgColorTitle		: "Selecione uma Cor",
DlgColorBtnClear	: "Limpar",
DlgColorHighlight	: "Visualização",
DlgColorSelected	: "Selecionada",

// Smiley Dialog
DlgSmileyTitle		: "Inserir Emoticon",

// Special Character Dialog
DlgSpecialCharTitle	: "Selecione um Caractere Especial",

// Table Dialog
DlgTableTitle		: "Formatar Tabela",
DlgTableRows		: "Linhas",
DlgTableColumns		: "Colunas",
DlgTableBorder		: "Borda",
DlgTableAlign		: "Alinhamento",
DlgTableAlignNotSet	: "<Não ajustado>",
DlgTableAlignLeft	: "Esquerda",
DlgTableAlignCenter	: "Centralizado",
DlgTableAlignRight	: "Direita",
DlgTableWidth		: "Largura",
DlgTableWidthPx		: "pixels",
DlgTableWidthPc		: "%",
DlgTableHeight		: "Altura",
DlgTableCellSpace	: "Espaçamento",
DlgTableCellPad		: "Enchimento",
DlgTableCaption		: "Legenda",

// Table Cell Dialog
DlgCellTitle		: "Formatar célula",
DlgCellWidth		: "Largura",
DlgCellWidthPx		: "pixels",
DlgCellWidthPc		: "%",
DlgCellHeight		: "Altura",
DlgCellWordWrap		: "Quebra de Linha",
DlgCellWordWrapNotSet	: "<Não ajustado>",
DlgCellWordWrapYes	: "Sim",
DlgCellWordWrapNo	: "Não",
DlgCellHorAlign		: "Alinhamento Horizontal",
DlgCellHorAlignNotSet	: "<Não ajustado>",
DlgCellHorAlignLeft	: "Esquerda",
DlgCellHorAlignCenter	: "Centralizado",
DlgCellHorAlignRight: "Direita",
DlgCellVerAlign		: "Alinhamento Vertical",
DlgCellVerAlignNotSet	: "<Não ajustado>",
DlgCellVerAlignTop	: "Superior",
DlgCellVerAlignMiddle	: "Centralizado",
DlgCellVerAlignBottom	: "Inferior",
DlgCellVerAlignBaseline	: "Baseline",
DlgCellRowSpan		: "Transpor Linhas",
DlgCellCollSpan		: "Transpor Colunas",
DlgCellBackColor	: "Cor do Plano de Fundo",
DlgCellBorderColor	: "Cor da Borda",
DlgCellBtnSelect	: "Selecionar...",

// Find Dialog
DlgFindTitle		: "Localizar...",
DlgFindFindBtn		: "Localizar",
DlgFindNotFoundMsg	: "O texto especificado não foi encontrado.",

// Replace Dialog
DlgReplaceTitle			: "Substituir",
DlgReplaceFindLbl		: "Procurar por:",
DlgReplaceReplaceLbl	: "Substituir por:",
DlgReplaceCaseChk		: "Coincidir Maiúsculas/Minúsculas",
DlgReplaceReplaceBtn	: "Substituir",
DlgReplaceReplAllBtn	: "Substituir Tudo",
DlgReplaceWordChk		: "Coincidir a palavra inteira",

// Paste Operations / Dialog
PasteErrorPaste	: "As configurações de segurança do seu navegador não permitem que o editor excute operações de colar automaticamente. Por favor, utilize o teclado para colar (Ctrl+V).",
PasteErrorCut	: "As configurações de segurança do seu navegador não permitem que o editor excute operações de recortar automaticamente. Por favor, utilize o teclado para recortar (Ctrl+X).",
PasteErrorCopy	: "As configurações de segurança do seu navegador não permitem que o editor excute operações de copiar automaticamente. Por favor, utilize o teclado para copiar (Ctrl+C).",

PasteAsText		: "Colar como Texto sem Formatação",
PasteFromWord	: "Colar do Word",

DlgPasteMsg		: "Não foi possível execurar o comando colar automaticamente devido às <STRONG>configurações de segurança</STRONG> seu navegador.<BR>Cole o conteúdo desejado dentro da seguinte caixa texto utilizando a tecla de atalho (<STRONG>Ctrl+V</STRONG>) e clique em <STRONG>OK</STRONG>.",

// Color Picker
ColorAutomatic	: "Automático",
ColorMoreColors	: "Mais Cores...",

// Document Properties
DocProps		: "Propriedades Documento",

// Anchor Dialog
DlgAnchorTitle		: "Formatar Âncora",
DlgAnchorName		: "Nome da Âncora",
DlgAnchorErrorName	: "Por favor, digite o nome da âncora",

// Speller Pages Dialog
DlgSpellNotInDic		: "Not in dictionary",	//MISSING
DlgSpellChangeTo		: "Alterar para",
DlgSpellBtnIgnore		: "Ignorar uma vez",
DlgSpellBtnIgnoreAll	: "Ignorar Todas",
DlgSpellBtnReplace		: "Alterar",
DlgSpellBtnReplaceAll	: "Alterar Todas",
DlgSpellBtnUndo			: "Desfazer",
DlgSpellNoSuggestions	: "-sem sugestões de ortografia-",
DlgSpellProgress		: "Verificação ortográfica em andamento...",
DlgSpellNoMispell		: "Verificação ortográfica encerrada: Não foram encontrados erros de ortografia",
DlgSpellNoChanges		: "Verificação ortográfica encerrada: Não houve alterações",
DlgSpellOneChange		: "Verificação ortográfica encerrada: Uma palavra foi alterada",
DlgSpellManyChanges		: "Verificação ortográfica encerrada: %1 foram alteradas",

IeSpellDownload			: "A verificação ortográfica não foi instalada. Você gostaria de realizar o download agora?",

// Button Dialog
DlgButtonText	: "Texto (Valor)",
DlgButtonType	: "Tipo",

// Checkbox and Radio Button Dialogs
DlgCheckboxName		: "Nome",
DlgCheckboxValue	: "Valor",
DlgCheckboxSelected	: "Selecionado",

// Form Dialog
DlgFormName		: "Nome",
DlgFormAction	: "Action",
DlgFormMethod	: "Método",

// Select Field Dialog
DlgSelectName		: "Nome",
DlgSelectValue		: "Valor",
DlgSelectSize		: "Tamanho",
DlgSelectLines		: "linhas",
DlgSelectChkMulti	: "Permitir múltiplas seleções",
DlgSelectOpAvail	: "Opções disponíveis",
DlgSelectOpText		: "Texto",
DlgSelectOpValue	: "Valor",
DlgSelectBtnAdd		: "Adicionar",
DlgSelectBtnModify	: "Modificar",
DlgSelectBtnUp		: "Para cima",
DlgSelectBtnDown	: "Para baixo",
DlgSelectBtnSetValue : "Definir como selecionado",
DlgSelectBtnDelete	: "Remover",

// Textarea Dialog
DlgTextareaName	: "Nome",
DlgTextareaCols	: "Colunas",
DlgTextareaRows	: "Linhas",

// Text Field Dialog
DlgTextName			: "Nome",
DlgTextValue		: "Valor",
DlgTextCharWidth	: "Comprimento (em caracteres)",
DlgTextMaxChars		: "Número Máximo de Caracteres",
DlgTextType			: "Tipo",
DlgTextTypeText		: "Texto",
DlgTextTypePass		: "Senha",

// Hidden Field Dialog
DlgHiddenName	: "Nome",
DlgHiddenValue	: "Valor",

// Bulleted List Dialog
BulletedListProp	: "Formatar Marcadores",
NumberedListProp	: "Formatar Numeração",
DlgLstType			: "Tipo",
DlgLstTypeCircle	: "Círculo",
DlgLstTypeDisk		: "Disco",
DlgLstTypeSquare	: "Quadrado",
DlgLstTypeNumbers	: "Números (1, 2, 3)",
DlgLstTypeLCase		: "Letras Minúsculas (a, b, c)",
DlgLstTypeUCase		: "Letras Maiúsculas (A, B, C)",
DlgLstTypeSRoman	: "Números Romanos Minúsculos (i, ii, iii)",
DlgLstTypeLRoman	: "Números Romanos Maiúsculos (I, II, III)",

// Document Properties Dialog
DlgDocGeneralTab	: "Geral",
DlgDocBackTab		: "Plano de Fundo",
DlgDocColorsTab		: "Cores e Margens",
DlgDocMetaTab		: "Meta Dados",

DlgDocPageTitle		: "Título da Página",
DlgDocLangDir		: "Direção do Idioma",
DlgDocLangDirLTR	: "Esquerda para Direita (LTR)",
DlgDocLangDirRTL	: "Direita para Esquerda (RTL)",
DlgDocLangCode		: "Código do Idioma",
DlgDocCharSet		: "Codificação de Caracteres",
DlgDocCharSetOther	: "Outra Codificação de Caracteres",

DlgDocDocType		: "Cabeçalho Tipo de Documento",
DlgDocDocTypeOther	: "Other Document Type Heading",
DlgDocIncXHTML		: "Incluir Declarações XHTML",
DlgDocBgColor		: "Cor do Plano de Fundo",
DlgDocBgImage		: "URL da Imagem de Plano de Fundo",
DlgDocBgNoScroll	: "Plano de Fundo Fixo",
DlgDocCText			: "Texto",
DlgDocCLink			: "Hiperlink",
DlgDocCVisited		: "Hiperlink Visitado",
DlgDocCActive		: "Hiperlink Ativo",
DlgDocMargins		: "Margens da Página",
DlgDocMaTop			: "Superior",
DlgDocMaLeft		: "Inferior",
DlgDocMaRight		: "Direita",
DlgDocMaBottom		: "Inferior",
DlgDocMeIndex		: "Palavras-chave de Indexação do Documento (separadas por vírgula)",
DlgDocMeDescr		: "Descrição do Documento",
DlgDocMeAuthor		: "Autor",
DlgDocMeCopy		: "Direitos Autorais",
DlgDocPreview		: "Visualizar",

// About Dialog
DlgAboutAboutTab	: "Sobre",
DlgAboutBrowserInfoTab	: "Informações do Navegador",
DlgAboutVersion		: "versão",
DlgAboutLicense		: "Licenciado sobre os termos da GNU Lesser General Public License",
DlgAboutInfo		: "Para maiores informações visite"
}