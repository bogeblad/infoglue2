package org.infoglue.cms.util.sorters;

import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.infoglue.deliver.applications.databeans.WebPage;
import org.infoglue.deliver.controllers.kernel.impl.simple.TemplateController;

/**
 * Sort on a particular property, using reflection to find the value
 *
 * @author Frank Febbraro (frank@phase2technology.com)
 */
public class HardcodedPageComparator implements Comparator<Object>
{
	private final static Logger logger = Logger.getLogger(HardcodedPageComparator.class);

	private String sortProperty;
	private String sortOrder;
	private boolean numberOrder;
	private String nameProperty;
	private String namesInOrderString;
	private Collator collation = Collator.getInstance();

	private TemplateController templateController;

	private static Map<String,Boolean> classUndefinedProperties = new HashMap<String,Boolean>();

	public HardcodedPageComparator(String sortProperty, String sortOrder, boolean numberOrder, String nameProperty, String namesInOrderString, TemplateController templateController)
	{
		this.sortProperty = sortProperty;
		this.sortOrder = sortOrder;
		this.numberOrder = numberOrder;
		this.nameProperty = nameProperty;
		if(namesInOrderString != null)
			this.namesInOrderString = namesInOrderString.toLowerCase();
		this.templateController = templateController;
		try
		{
			if(templateController.getLocale() != null)
				this.collation = Collator.getInstance(templateController.getLocale());
		}
		catch (Exception e)
		{
			logger.warn("No good locale: " + e.getMessage());
		}

		if (logger.isInfoEnabled())
		{
			StringBuilder sb = new StringBuilder();
			sb.append("Constructed comparator with settings.");
			sb.append("[");
			sb.append("sortProperty: ").append(this.sortProperty).append(", ");
			sb.append("sortOrder: ").append(this.sortOrder).append(", ");
			sb.append("numberOrder: ").append(this.numberOrder).append(", ");
			sb.append("nameProperty: ").append(this.nameProperty).append(", ");
			sb.append("namesInOrderString: ").append(this.namesInOrderString).append(", ");
			sb.append("]");
			logger.info(sb);
		}
	}

	public int compare(Object o1, Object o2)
	{
		Comparable valueOne = getProperty(o1, sortProperty, this.numberOrder);
		Comparable valueTwo = getProperty(o2, sortProperty, this.numberOrder);

		Comparable valueOneName = getProperty(o1, nameProperty, false);
		Comparable valueTwoName = getProperty(o2, nameProperty, false);

		if(valueOne == null)
		{
			WebPage webPage1 = (WebPage)o1;
			WebPage webPage2 = (WebPage)o2;

			valueOne = this.templateController.getPageMetaData(webPage1.getSiteNodeId(), this.templateController.getLanguageId(), sortProperty);
			valueTwo = this.templateController.getPageMetaData(webPage2.getSiteNodeId(), this.templateController.getLanguageId(), sortProperty);

			if(valueOneName == null)
			{
				valueOneName = this.templateController.getPageMetaData(webPage1.getSiteNodeId(), this.templateController.getLanguageId(), nameProperty);
				valueTwoName = this.templateController.getPageMetaData(webPage2.getSiteNodeId(), this.templateController.getLanguageId(), nameProperty);
			}

			if(this.numberOrder)
			{
				try
				{
					if(valueOne != null && !valueOne.equals(""))
						valueOne = new Long(valueOne.toString());
					else
					{
						if(sortOrder.equalsIgnoreCase("desc"))
							valueOne = new Long(Long.MIN_VALUE);
						else
							valueOne = new Long(Long.MAX_VALUE);
					}
				}
				catch(Exception e)
				{
					logger.info("Not a number..." + e.getMessage());
				}

				try
				{
					if(valueTwo != null && !valueTwo.equals(""))
						valueTwo = new Long(valueTwo.toString());
					else
					{
						if(sortOrder.equalsIgnoreCase("desc"))
							valueTwo = new Long(Long.MIN_VALUE);
						else
							valueTwo = new Long(Long.MAX_VALUE);
					}
				}
				catch(Exception e)
				{
					logger.info("Not a number..." + e.getMessage());
				}
			}
		}

		return after(valueOne, valueTwo, valueOneName, valueTwoName);
	}

	@SuppressWarnings("unchecked")
	private int after(Comparable valueOne, Comparable valueTwo, Comparable valueOneName, Comparable valueTwoName)
	{
		int index1 = namesInOrderString.indexOf(valueOneName.toString());
		int index2 = namesInOrderString.indexOf(valueTwoName.toString());

		if(index1 != -1 && index2 != -1)
		{
			int result = index1 - index2;
			// If the items are not the same we return, otherwise we look at the other property
			if (result > 0)
			{
				return result;
			}
		}

		if(index1 == -1 && index2 != -1)
			return 1;
		else if(index2 == -1 && index1 != -1)
			return -1;
		else
		{
			int result;
			if (sortOrder.equalsIgnoreCase("desc"))
			{
				if((valueOne != null && !valueOne.toString().equalsIgnoreCase("")) && (valueTwo == null || valueTwo.toString().equalsIgnoreCase("")))
					result = -1;
				if((valueTwo != null && !valueTwo.toString().equalsIgnoreCase("")) && (valueOne == null || valueOne.toString().equalsIgnoreCase("")))
					result = 1;

				if(valueOne instanceof String && valueTwo instanceof String)
					result = collation.compare(valueTwo, valueOne);
				else
					result = valueTwo.compareTo(valueOne);
			}
			else
			{
				if((valueOne != null && !valueOne.toString().equalsIgnoreCase("")) && (valueTwo == null || valueTwo.toString().equalsIgnoreCase("")))
					result = -1;
				if((valueTwo != null && !valueTwo.toString().equalsIgnoreCase("")) && (valueOne == null || valueOne.toString().equalsIgnoreCase("")))
					result = 1;

				try
				{
					if(valueOne instanceof String && valueTwo instanceof String)
						result = collation.compare(valueOne, valueTwo);
					else
						result = valueOne.compareTo(valueTwo);
				}
				catch (Exception e)
				{
					logger.warn("Error comparing [" + valueOne + "] and [" + valueTwo + "] for property:" + sortProperty);
					result = valueOne.compareTo(valueTwo);
				}
			}

			return result;
		}
	}

	private Comparable<?> getProperty(Object o, String property, boolean useNumberOrder)
	{
		try
		{
			if(o == null || classUndefinedProperties.get("" + o.getClass().getName() + "_" + property) != null)
			{
				return null;
			}

			Object propertyObject = PropertyUtils.getProperty(o, property);

			if(propertyObject instanceof String)
			{
				if(useNumberOrder)
				{
					try
					{
						return new Long(propertyObject.toString());
					}
					catch(Exception e)
					{
						logger.debug("Not a number..." + e.getMessage());
					}
				}

				return (Comparable<?>)propertyObject.toString().toLowerCase();
			}
			else
			{
				return (Comparable<?>)propertyObject;
			}
		}
		catch (Exception e)
		{
			logger.info("Error finding property " + property + " on " + o.getClass() + ". Caching this.", e);
			classUndefinedProperties.put("" + o.getClass().getName() + "_" + property, new Boolean(false));
			return null;
		}
	}

}
