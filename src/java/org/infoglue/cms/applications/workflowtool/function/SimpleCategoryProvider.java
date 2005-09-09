package org.infoglue.cms.applications.workflowtool.function;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.infoglue.cms.applications.workflowtool.util.PropertysetHelper;
import org.infoglue.cms.controllers.kernel.impl.simple.CategoryController;
import org.infoglue.cms.entities.management.CategoryVO;


import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public class SimpleCategoryProvider extends CategoryProvider 
{
	/**
	 * 
	 */
	private static final String PROPERTYSET_CATEGORY_PREFIX = "category_";

	/**
	 * 
	 */
	private static final String ARGUMENT_ATTRIBUTE_NAME = "attributeName";

	/**
	 * 
	 */
	private static final String	ARGUMENT_ROOT_CATEGORY_NAME = "rootCategory";

	/**
	 * 
	 */
	private CategoryVO rootCategory;

	/**
	 * 
	 */
	private String attributeName;
		
	
	
	/**
	 * 
	 */
	public SimpleCategoryProvider() 
	{ 
		super();	
	}

	/**
	 * 
	 */
	protected void doExecute(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException 
	{
		cleanPropertySet(ps);
		populate(transientVars, ps);	
	}
	
	/**
	 * 
	 */
	private void populate(final Map transientVars, final PropertySet ps) throws WorkflowException 
	{
		List result = new ArrayList();
		for(Iterator i = rootCategory.getChildren().iterator(); i.hasNext();) 
		{
			final CategoryVO categoryVO = (CategoryVO) i.next();
			if(transientVars.containsKey(getCategoryKey(categoryVO))) 
			{
				new PropertysetHelper(ps).setData(getCategoryKey(categoryVO), "1");
				result.add(categoryVO);
			}
		}
		getCategories().put(attributeName, result);
	}
	
	/**
	 * 
	 */
	private void cleanPropertySet(final PropertySet ps) 
	{
		new PropertysetHelper(ps).removeKeys(getBaseKey());
	}
	
	/**
	 * 
	 */
	protected void initializeArguments(final Map args) throws WorkflowException 
	{
		super.initializeArguments(args);
		attributeName = (String) args.get(ARGUMENT_ATTRIBUTE_NAME);
		rootCategory  = getRootCategory((String) args.get(ARGUMENT_ROOT_CATEGORY_NAME));
	}
	
	/**
	 * 
	 */
	private CategoryVO getRootCategory(final String path) throws WorkflowException 
	{
		try 
		{
			final CategoryVO categoryVO = CategoryController.getController().findByPath(path, getDatabase());
			return CategoryController.getController().findWithChildren(categoryVO.getId(), getDatabase());
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
			throw new WorkflowException("SimpleCategoryProvider.getRootCategory() : " + e);
		}
	}

	/**
	 * 
	 */
	private String getBaseKey() 
	{ 
		return PROPERTYSET_CATEGORY_PREFIX + attributeName + "_";	
	}
	
	/**
	 * 
	 */
	private String getCategoryKey(final CategoryVO categoryVO) 
	{ 
		return getBaseKey() + categoryVO.getName(); 
	}
}
