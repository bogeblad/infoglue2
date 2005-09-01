package org.infoglue.cms.applications.workflowtool.register;

import java.util.Map;

import org.apache.log4j.Logger;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentTypeDefinitionController;
import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;

import com.opensymphony.workflow.Register;
import com.opensymphony.workflow.WorkflowContext;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.spi.WorkflowEntry;

/**
 * 
 */
public class ContentType implements Register {
	/**
	 * 
	 */
    private final static Logger logger = Logger.getLogger(ContentType.class.getName());
	
	/**
	 * 
	 */
	public static final String TRANSIENT_VARIABLE_VARIABLE = "contentType";

	/**
	 * 
	 */
	private static final String ARGUMENT_CONTENT_TYPE_NAME = "contentTypeName";
	
	
	
	/**
	 * 
	 */
	public Object registerVariable(final WorkflowContext context, final WorkflowEntry entry, final Map args) throws WorkflowException {
		logger.debug("ContentType.registerVariable()");
		try {
		    final ContentTypeDefinitionVO contentTypeDefinitionVO = ContentTypeDefinitionController.getController().getContentTypeDefinitionVOWithName(getContentTypeName(args));
		    if(contentTypeDefinitionVO == null)
				throw new WorkflowException(ARGUMENT_CONTENT_TYPE_NAME + " is not a definied content type.");
			return contentTypeDefinitionVO;
		} catch(Exception e) {
			throw new WorkflowException("ContentTypeRegister.registerVariable() : " + e.getMessage());
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
