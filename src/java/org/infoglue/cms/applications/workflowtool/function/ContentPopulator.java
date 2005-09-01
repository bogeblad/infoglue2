package org.infoglue.cms.applications.workflowtool.function;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.infoglue.cms.applications.workflowtool.register.ContentType;
import org.infoglue.cms.applications.workflowtool.util.ContentValues;
import org.infoglue.cms.applications.workflowtool.util.ContentVersionValues;
import org.infoglue.cms.applications.workflowtool.util.PropertysetHelper;
import org.infoglue.cms.applications.workflowtool.util.TransientVarsHelper;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentTypeDefinitionController;
import org.infoglue.cms.entities.management.ContentTypeAttribute;
import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;


import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public class ContentPopulator extends InfoglueFunction {
	/**
	 * 
	 */
	public static final String PROPERTYSET_CONTENT_PREFIX = "content.";

	/**
	 * 
	 */
	public static final String PROPERTYSET_CONTENT_VERSION_PREFIX = "contentversion.";
	
	/**
	 * 
	 */
	public static final String TRANSIENT_VARS_CONTENT_VARIABLE = "contentAttributes";

	/**
	 * 
	 */
	public static final String TRANSIENT_VARS_CONTENT_VERSION_VARIABLE = "contentVersionAttributes";
	
	/**
	 * 
	 */
	private ContentTypeDefinitionVO contentTypeDefinitionVO;

	
	
	/**
	 * 
	 */
	protected void doExecute(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException {
		populate(transientVars, ps);
	}

	/**
	 * 
	 */
	protected void initialize(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException {
		super.initialize(transientVars, args, ps);
		contentTypeDefinitionVO = (ContentTypeDefinitionVO) getParameter(transientVars, ContentType.TRANSIENT_VARIABLE_VARIABLE);
	}
	
	/**
	 * 
	 */
	private void populate(Map transientVars, PropertySet ps) throws WorkflowException {
		populateContentValues(transientVars, ps);
		populateContentVersionValues(transientVars, ps);
	}

	/**
	 * 
	 */
	protected void populateContentValues(Map transientVars, PropertySet ps) throws WorkflowException {
		final ContentValues result = new ContentValues();
		
		final String publishDate = TransientVarsHelper.getRequestValue(transientVars, PROPERTYSET_CONTENT_PREFIX + ContentValues.PUBLISH_DATE_TIME);
		final String expireDate  = TransientVarsHelper.getRequestValue(transientVars, PROPERTYSET_CONTENT_PREFIX + ContentValues.EXPIRE_DATE_TIME);
		final String name        = TransientVarsHelper.getRequestValue(transientVars, PROPERTYSET_CONTENT_PREFIX + ContentValues.NAME);
		
		result.setName(name);
		result.setPublishDateTime(publishDate);
		result.setExpireDateTime(expireDate);

		populatePropertySet(ps, PROPERTYSET_CONTENT_PREFIX + ContentValues.PUBLISH_DATE_TIME, publishDate);
		populatePropertySet(ps, PROPERTYSET_CONTENT_PREFIX + ContentValues.EXPIRE_DATE_TIME,  expireDate);
		populatePropertySet(ps, PROPERTYSET_CONTENT_PREFIX + ContentValues.NAME,  name);

		transientVars.put(TRANSIENT_VARS_CONTENT_VARIABLE, result);
	}

	/**
	 * 
	 */
	protected void populateContentVersionValues(Map transientVars, PropertySet ps) throws WorkflowException {
		final ContentVersionValues result = new ContentVersionValues();
		final List contentTypeAttributes = getContentTypeAttributes();
		for(Iterator i=contentTypeAttributes.iterator(); i.hasNext(); ) {
			final ContentTypeAttribute attribute = (ContentTypeAttribute) i.next();

			final String name  = PROPERTYSET_CONTENT_VERSION_PREFIX + attribute.getName();
			final String value = TransientVarsHelper.getRequestValue(transientVars, name);
			populatePropertySet(ps, name, value);
			if(value != null)
				result.set(attribute.getName(), value);
		}
		transientVars.put(TRANSIENT_VARS_CONTENT_VERSION_VARIABLE, result);
	}
	
	/**
	 * 
	 */
	private void populatePropertySet(PropertySet ps, String name, String value) throws WorkflowException {
		if(value != null)
			new PropertysetHelper(ps).setData(name, value);
		else if(ps.exists(name)) 
			ps.remove(name);
	}

	/**
	 * 
	 */
	private void populatePropertySet(PropertySet ps, String name, Date value) throws WorkflowException {
		populatePropertySet(ps, name, (value == null) ? null : value.toString());
	}

	/**
	 * 
	 */
	private List getContentTypeAttributes() {
		return ContentTypeDefinitionController.getController().getContentTypeAttributes(contentTypeDefinitionVO.getSchemaValue());
	}
}
