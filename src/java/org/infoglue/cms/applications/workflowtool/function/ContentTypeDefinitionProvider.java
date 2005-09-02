package org.infoglue.cms.applications.workflowtool.function;

import java.util.Map;

import org.infoglue.cms.controllers.kernel.impl.simple.ContentTypeDefinitionController;
import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

public class ContentTypeDefinitionProvider extends InfoglueFunction {
	
	/**
	 * 
	 */
	public static final String CONTENT_TYPE_DEFINITION_PARAMETER = "contentTypeDefinition";

	/**
	 * 
	 */
	private static final String ARGUMENT_CONTENT_TYPE_NAME = "contentTypeDefinitionName";

	/**
	 * 
	 */
	public ContentTypeDefinitionProvider() { super(); }

	/**
	 * 
	 */
	protected void doExecute(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException {
		populate(transientVars, args, ps);
	}
	
	/**
	 * 
	 */
	private void populate(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException {
			try {
			    final ContentTypeDefinitionVO contentTypeDefinitionVO = ContentTypeDefinitionController.getController().getContentTypeDefinitionVOWithName(getContentTypeName(args), getDatabase());
			    if(contentTypeDefinitionVO == null)
					throw new WorkflowException(ARGUMENT_CONTENT_TYPE_NAME + " is not a definied content type.");
				transientVars.put(CONTENT_TYPE_DEFINITION_PARAMETER, contentTypeDefinitionVO);
			} catch(Exception e) {
				e.printStackTrace();
				throw new WorkflowException(e);
			}
	}
	
	/**
	 * 
	 */
	private String getContentTypeName(final Map args) throws WorkflowException {
		if(!args.containsKey(ARGUMENT_CONTENT_TYPE_NAME))
			throw new WorkflowException("No " + ARGUMENT_CONTENT_TYPE_NAME + " argument specified.");
		return (String) args.get(ARGUMENT_CONTENT_TYPE_NAME);
	}
}
