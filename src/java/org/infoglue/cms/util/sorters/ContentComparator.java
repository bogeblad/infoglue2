package org.infoglue.cms.util.sorters;

import org.apache.commons.beanutils.PropertyUtils;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentVersionController;
import org.infoglue.cms.controllers.kernel.impl.simple.LanguageController;
import org.infoglue.cms.controllers.kernel.impl.simple.SiteNodeController;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.entities.structure.SiteNodeVO;
import org.infoglue.cms.util.CmsLogger;
import org.infoglue.deliver.controllers.kernel.impl.simple.BasicTemplateController;

import java.util.Comparator;

/**
 * Sort on a particular property, using reflection to find the value
 *
 * @author Frank Febbraro (frank@phase2technology.com)
 */
public class ContentComparator implements Comparator
{
	private String sortProperty;
	private String sortOrder;
	private BasicTemplateController basicTemplateController;

	public ContentComparator(String sortProperty, String sortOrder, BasicTemplateController basicTemplateController)
	{
		this.sortProperty = sortProperty;
		this.sortOrder = sortOrder;
		this.basicTemplateController = basicTemplateController;
	}


    public int compare(Object o1, Object o2)
	{
	    Comparable valueOne = getProperty(o1, sortProperty);
		Comparable valueTwo = getProperty(o2, sortProperty);
		
		if(valueOne == null)
		{
			ContentVO contentVO1 = (ContentVO)o1;
			ContentVO contentVO2 = (ContentVO)o2;
			
		    valueOne = this.basicTemplateController.getContentAttribute(contentVO1.getId(), this.basicTemplateController.getLanguageId(), sortProperty);
			valueTwo = this.basicTemplateController.getContentAttribute(contentVO2.getId(), this.basicTemplateController.getLanguageId(), sortProperty);
		}

		if(sortOrder.equalsIgnoreCase("desc"))
		    return valueTwo.compareTo(valueOne);
		else
		    return valueOne.compareTo(valueTwo);
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
			CmsLogger.logInfo(getClass().getName() + " Error finding property " + property, e);
			return null;
		}
	}
}
