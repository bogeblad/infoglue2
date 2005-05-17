package org.infoglue.cms.util.sorters;

import org.apache.commons.beanutils.PropertyUtils;
import org.exolab.castor.jdo.Database;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentVersionController;
import org.infoglue.cms.controllers.kernel.impl.simple.LanguageController;
import org.infoglue.cms.controllers.kernel.impl.simple.SiteNodeController;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.content.ContentVersion;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.entities.management.Language;
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
	private Database db;
	public long extractTime = 0;

	public ContentComparator(String sortProperty, String sortOrder, BasicTemplateController basicTemplateController)
	{
		this.sortProperty = sortProperty;
		this.sortOrder = sortOrder;
		this.basicTemplateController = basicTemplateController;
		extractTime = 0;
	}

    public int compare(Object o1, Object o2)
	{		
		ContentVO contentVO1 = (ContentVO)o1;
		ContentVO contentVO2 = (ContentVO)o2;
	
		Comparable valueOne = (String)contentVO1.getExtraProperties().get(sortProperty);
		Comparable valueTwo = (String)contentVO2.getExtraProperties().get(sortProperty);
		
        long previousTime = System.currentTimeMillis();

		if(valueOne == null)
		{
	        valueOne = getProperty(o1, sortProperty);
			valueTwo = getProperty(o2, sortProperty);
		}

		if(valueOne == null && this.basicTemplateController != null)
		{
		    valueOne = this.basicTemplateController.getContentAttribute(contentVO1.getId(), this.basicTemplateController.getLanguageId(), sortProperty);
			valueTwo = this.basicTemplateController.getContentAttribute(contentVO2.getId(), this.basicTemplateController.getLanguageId(), sortProperty);
		}
		
		int result;
		
		if(sortOrder.equalsIgnoreCase("desc"))
		    result = valueTwo.compareTo(valueOne);
		else
		    result = valueOne.compareTo(valueTwo);

		long elapsedTime = System.currentTimeMillis() - previousTime;

		extractTime = extractTime + elapsedTime;

		return result;
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
