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

