//-----------------------------------------------
// Dirty handling in forms 
//-----------------------------------------------

var dirty = false;

function setDirty()
{
	dirty=true;
}
function getDirty()
{
	return dirty;
}

function getWindowHeight()
{
	var y;
	if (self.innerHeight) // all except Explorer
	{
		y = self.innerHeight;
	}
	else if (document.documentElement && document.documentElement.clientHeight)
		// Explorer 6 Strict Mode
	{
		y = document.documentElement.clientHeight;
	}
	else if (document.body) // other Explorers
	{
		y = document.body.clientHeight;
	}
	return y;
}

function getWindowWidth()
{
	var x;
	if (self.innerHeight) // all except Explorer
	{
		x = self.innerWidth;
	}
	else if (document.documentElement && document.documentElement.clientHeight)
		// Explorer 6 Strict Mode
	{
		x = document.documentElement.clientWidth;
	}
	else if (document.body) // other Explorers
	{
		x = document.body.clientWidth;
	}
	return x;
}

function resizeInlineTabDivs()
{
	var dimensionsWidth = $(window).width();
	var dimensionsHeight = $(window).height();
  	//alert("dimensionsWidth:" + dimensionsWidth);
  	if(dimensionsWidth != 0)
  	{
		$(".inlineTabDiv").css("height", dimensionsHeight - 160);
  		$(".inlineTabDiv").css("width", dimensionsWidth - 30);
		$(".inlineSubTabDiv").css("height", dimensionsHeight - 220);
		$(".inlineSubTabDiv").css("width", dimensionsHeight - 40);
	}
	else
	{
		setTimeout("resizeInlineTabDivs()", 100);
	}
}

function resizeInlineTabDivsWithAmount(height, width)
{
	var dimensionsWidth = $(window).width();
	var dimensionsHeight = $(window).height();
  	//alert("dimensionsWidth:" + dimensionsWidth);
	//alert("height:" + height);
  	if(dimensionsWidth != 0)
  	{
		//$(".inlineTabDiv").css("height", dimensionsHeight - height);
  		//$(".inlineTabDiv").css("width", dimensionsWidth - width);
	}
	else
	{
		setTimeout("resizeInlineTabDivsWithAmount(" + height + "," + width + ")", 500);
	}
}

function resizeScrollArea()
{
	var dimensionsWidth = $(window).width();
	var dimensionsHeight = $(window).height();
  	//alert("dimensionsHeight:" + (dimensionsHeight - 78));
  	if(dimensionsWidth != 0)
  	{
		$(".igScrollArea").css("height", dimensionsHeight - 78);
		//$(".igScrollArea").css("width", dimensionsWidth);
	}
	else
	{
		setTimeout("resizeScrollArea()", 100);
	}
}

function checkAllBoxes(element)
{
	if(element)
	{
		var length = element.length;
	  	if(length == null)
	  	{
	  		element.checked = true;
	  		rowId = element.getAttribute("rowId");
			listRowMarked(document.getElementById(rowId));
	  	}
	  	else
	  	{	
		 	var field = element;
		 	for (i = 0; i < field.length; i++)
			{
				field[i].checked = true;
				rowId = field[i].getAttribute("rowId");
				listRowMarked(document.getElementById(rowId));
			}
		}
	}
}

function uncheckAllBoxes(element)
{
	if(element)
	{
		var length = element.length;
	  	if(length == null)
	  	{
	  		element.checked = false;
	  		rowId = element.getAttribute("rowId");
			listRowUnMarked(document.getElementById(rowId));
	  	}
	  	else
	  	{	
		 	var field = element;
		 	for (i = 0; i < field.length; i++)
			{
				field[i].checked = false;
				rowId = field[i].getAttribute("rowId");
				listRowUnMarked(document.getElementById(rowId));
			}
		}
	}
}

function listRowMarked(rowEl)
{
	return;
	/*
	if (rowEl.className.slice(0,6) != "marked")
		rowEl.className = "marked"+rowEl.className;
	*/	
	lastRow = rowEl;
}


function listRowUnMarked(rowEl)
{
	var rowClass = rowEl.className;
	if(rowClass.length > 6)
	{
		if (rowClass.slice(0,6) == "marked")
		{
			rowEl.className = rowClass.slice(6, rowClass.length);
		}
	}
			
	lastRow = rowEl;
}

function CheckUncheck(row,chkbox)
{
	var rowEl=document.getElementById(row);
	if (chkbox.checked)
	{
		listRowMarked(rowEl);
	}
	else
	{
		listRowUnMarked(rowEl);		
	}
}

//-----------------------------------------------
//This function opens up a new location in a 
//restriced popup 
//-----------------------------------------------
function openPopup(url, name, details)
{
	newWin=window.open(url, name, details);
	newWin.focus();
}

//-----------------------------------------------
//This function opens up a new location in a 
//restriced popup 
//-----------------------------------------------
function openPopupWithOptionalParameter(url, name, details, question, parameter)
{
	if(confirm(question))
		newWin=window.open(url + "&" + parameter, name, details);
	else
		newWin=window.open(url, name, details);
	
	newWin.focus();
}
