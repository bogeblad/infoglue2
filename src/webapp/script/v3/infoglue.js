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
	}
	else
	{
		setTimeout("resizeInlineTabDivs()", 100);
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
