package org.infoglue.cms.applications.workflowtool.function;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
public class ContentPopulator extends InfoglueFunction 
{
	/**
	 * 
	 */
	public static final String PROPERTYSET_CONTENT_PREFIX = "content_";

	/**
	 * 
	 */
	public static final String PROPERTYSET_CONTENT_VERSION_PREFIX = "contentversion_";
	
	/**
	 * 
	 */
	public static final String CONTENT_VALUES_PARAMETER = "contentValues";

	/**
	 * 
	 */
	public static final String CONTENT_VERSION_VALUES_PARAMETER = "contentVersionValues";
	
	/**
	 * 
	 */
	private ContentTypeDefinitionVO contentTypeDefinitionVO;

	
	
	/**
	 * 
	 */
	protected void doExecute(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException {
		populateContentValues(transientVars, ps);
		populateContentVersionValues(transientVars, ps);
	}

	/**
	 * 
	 */
	protected void initialize(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException {
		super.initialize(transientVars, args, ps);
		contentTypeDefinitionVO = (ContentTypeDefinitionVO) getParameter(transientVars, ContentTypeDefinitionProvider.CONTENT_TYPE_DEFINITION_PARAMETER);
	}
	
	/**
	 * 
	 */
	protected void populateContentValues(final Map transientVars, final PropertySet ps) throws WorkflowException {
		final ContentValues result = new ContentValues();
		
		result.setName(populate(transientVars, ps, PROPERTYSET_CONTENT_PREFIX + ContentValues.NAME));
		result.setPublishDateTime(populate(transientVars, ps, PROPERTYSET_CONTENT_PREFIX + ContentValues.PUBLISH_DATE_TIME));
		result.setExpireDateTime(populate(transientVars, ps, PROPERTYSET_CONTENT_PREFIX + ContentValues.EXPIRE_DATE_TIME));

		transientVars.put(CONTENT_VALUES_PARAMETER, result);
	}
	
	/**
	 * 
	 */
	protected void populateContentVersionValues(Map transientVars, PropertySet ps) throws WorkflowException {
		final ContentVersionValues result = new ContentVersionValues();
		final List contentTypeAttributes = getContentTypeAttributes();
		for(Iterator i=contentTypeAttributes.iterator(); i.hasNext(); ) {
			final ContentTypeAttribute attribute = (ContentTypeAttribute) i.next();
			result.set(attribute.getName(), populate(transientVars, ps, PROPERTYSET_CONTENT_VERSION_PREFIX + attribute.getName()));
		}
		transientVars.put(CONTENT_VERSION_VALUES_PARAMETER, result);
	}
	
	/**
	 * 
	 */
	private String populate(final Map transientVars, final PropertySet ps, final String name) throws WorkflowException {
		final PropertysetHelper psHelper = new PropertysetHelper(ps);
		if(transientVars.containsKey(name)) {
			psHelper.setData(name, TransientVarsHelper.getRequestValue(transientVars, name));
			getLogger().debug(name + " is found in the request; propertyset updated.");
		} else
			getLogger().debug(name + " is not found in the request; propertyset not updated.");
		return ps.exists(name) ? psHelper.getData(name) : "";
	}
	/**
	 * 
	 */
	private List getContentTypeAttributes() {
		return ContentTypeDefinitionController.getController().getContentTypeAttributes(contentTypeDefinitionVO.getSchemaValue());
	}
}
