/*
 * This function returns the raw value of the elementId
 * regardless of what editor is active for the element.
 *
 */
function getValue(elementId)
{
	if(typeof syncToPlainEditor == 'function')
		syncToPlainEditor(elementId);

	var value = "";
	
	var allElem = document.editForm.elements;
  	for(i=0;i<allElem.length;i++)
	{
	    var element = allElem[i];
	    
	    if(element.name == elementId)
	    {
	    	if(element.type=='checkbox' || element.type=='radio')
	        {
	        	if(element.checked == true)
	          	{
	          		if(value == "")
		          		value += element.value;
					else
						value += ',' + element.value;				
	          	}
	        }
			else if(element.type=='select-multiple')
	        {
	        	var arr = jQuery.map($("#" + element.id + " :selected"), function(e) { return $(e).val(); })
				value = arr.join(",");
	        }
			else
			{
				var hiddenField = document.getElementById(elementId + "Hidden");
				if(hiddenField)
					fieldName = "document.editForm." + elementId + "Hidden";
				else
					fieldName = "document.editForm." + elementId;
				
				value = eval(fieldName).value;
			}
	    }      
	}	
								
	return value;
}
