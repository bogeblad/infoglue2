package org.infoglue.cms.applications.workflowtool.function;

import java.util.Map;

import org.infoglue.cms.controllers.kernel.impl.simple.ContentController;
import org.infoglue.cms.entities.content.ContentVO;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * 
 *
 */
public class ContentMover extends ContentFunction 
{
	/**
	 * 
	 */
	public static final String DESTINATION_PARAMETER = "move_newParentFolder";
	
	/**
	 *
	 */
	public ContentMover() 
	{ 
		super(); 
	}

	/**
	 * 
	 */
	protected void doExecute(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException 
	{
		if(getContentVO() != null)
			move((ContentVO) getParameter(transientVars, DESTINATION_PARAMETER));
	}

	/**
	 * 
	 */
	private void move(final ContentVO destinationContentVO) throws WorkflowException 
	{
		try 
		{
			if(!getContentVO().getParentContentId().equals(destinationContentVO.getContentId()))
				ContentController.getContentController().moveContent(getContentVO(), destinationContentVO.getId(), getDatabase());
		} 
		catch(Exception e) 
		{
			throw new WorkflowException(e);
		}
	}
}
