/**
 * This method shows a hidden layer.
 */
 
function showDiv(id)
{
	document.getElementById(id).style.visibility = 'visible';
}

/**
 * This method hides a layer.
 */

function hideDiv(id)
{
	document.getElementById(id).style.visibility = 'hidden';
}

/**
 * This method moves a layer.
 */
 
function moveDiv(id, x, y)
{
	document.getElementById(id).style.left = x;
	document.getElementById(id).style.top = y;
}

/**
 * This method resizes a layer.
 */
 
function resizeDiv(id, width, height)
{
	document.getElementById(id).style.width = width;
	document.getElementById(id).style.height = height;
}

/**
 * This method submit a form.
 */

function submitForm(id)
{
	form = document.getElementById(id);
	alert("form:" + form.name);
	//document.getElementById(id).submit();
}

/**
 * This method returns the width of a window/frame
 */
 
function getWindowWidth()
{
	width = 640;
	if (window.innerWidth || window.innerHeight){ 
		width = window.innerWidth; 
	} 
	else if (document.body.clientWidth || document.body.clientHeight){ 
		width = document.body.clientWidth; 
	} 
	
	return width;
} 

/**
 * This method returns the height of a window/frame
 */

function getWindowHeight()
{
	height = 480;
	if (window.innerWidth || window.innerHeight){ 
		height = window.innerHeight; 
	} 
	else if (document.body.clientWidth || document.body.clientHeight){ 
		height = document.body.clientHeight; 
	} 

	return height;
} 
