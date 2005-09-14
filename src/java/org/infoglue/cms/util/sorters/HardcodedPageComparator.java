package org.infoglue.cms.util.sorters;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.infoglue.cms.entities.structure.SiteNodeVO;

import org.infoglue.deliver.applications.databeans.WebPage;
import org.infoglue.deliver.controllers.kernel.impl.simple.BasicTemplateController;
import org.infoglue.deliver.controllers.kernel.impl.simple.TemplateController;

import java.util.Comparator;

/**
 * Sort on a particular property, using reflection to find the value
 *
 * @author Frank Febbraro (frank@phase2technology.com)
 */
public class HardcodedPageComparator implements Comparator
{
    private final static Logger logger = Logger.getLogger(HardcodedPageComparator.class.getName());

	private String sortProperty;
	private String sortOrder;
	private String namesInOrderString;
	
	private TemplateController templateController;

	public HardcodedPageComparator(String sortProperty, String sortOrder, String namesInOrderString, TemplateController templateController)
	{
		this.sortProperty = sortProperty;
		this.sortOrder = sortOrder;
		this.namesInOrderString = namesInOrderString;
		this.templateController = templateController;
	}

	public int compare(Object o1, Object o2)
	{
	    //System.out.println("sortOrder: " + sortOrder);

	    Comparable valueOne = getProperty(o1, sortProperty);
		Comparable valueTwo = getProperty(o2, sortProperty);
		
		if(valueOne == null)
		{
		    WebPage webPage1 = (WebPage)o1;
		    WebPage webPage2 = (WebPage)o2;
		    
		    Integer meta1Id = webPage1.getMetaInfoContentId(); //this.templateController.getMetaInformationContentId(webPage1.getSiteNodeId());
		    Integer meta2Id = webPage2.getMetaInfoContentId(); //this.templateController.getMetaInformationContentId(webPage2.getSiteNodeId());
		    
		    valueOne = this.templateController.getContentAttribute(meta1Id, this.templateController.getLanguageId(), sortProperty);
			valueTwo = this.templateController.getContentAttribute(meta2Id, this.templateController.getLanguageId(), sortProperty);
		}

		/*
	    if(sortOrder.equalsIgnoreCase("desc"))
		    return valueTwo.compareTo(valueOne);
		else
		    return valueOne.compareTo(valueTwo);
		*/
		
		if(after(valueOne, valueTwo))
		    return 1;
		else
		    return -1;
	}

	private boolean after(Comparable valueOne, Comparable valueTwo)
	{	    
	    int index1 = namesInOrderString.indexOf(valueOne.toString());
	    int index2 = namesInOrderString.indexOf(valueTwo.toString());
	    
	    if(index1 != -1 && index2 != -1)
	    {
	        if(index1 > index2)
	            return true;
	        else
	            return false;
	    }
	    else
	    {
	        if(index1 == -1 && index2 != -1)
	            return true;
	        else if(index2 == -1 && index1 != -1)
	            return false;
	        else
	        {
		        if(sortOrder.equalsIgnoreCase("desc"))
		        {
		            /*
		            if(valueOne.toString().equalsIgnoreCase("Nyheter"))
		            {
		                System.out.println("valueOne: " + valueOne);
		                System.out.println("valueTwo: " + valueTwo);
		                System.out.println("compare:" + valueTwo.compareTo(valueOne));
		            }
		            */
		            
		            if(valueTwo.compareTo(valueOne) < 0)
		                return false;
		            else
		                return true;
		        }
			    else
			    {
			        if(valueTwo.compareTo(valueOne) > 0)
				        return false;
		            else
		                return true;
			    }
	        }
	    }
	}
	
	private Comparable getProperty(Object o, String property)
	{
		try
		{
			Object propertyObject = PropertyUtils.getProperty(o, sortProperty);
			if(propertyObject instanceof String)
				return (Comparable)propertyObject.toString().toLowerCase();
			else
				return (Comparable)propertyObject;
		}
		catch (Exception e)
		{
			logger.info(getClass().getName() + " Error finding property " + property, e);
			return null;
		}
	}
}
