package org.infoglue.cms.applications.workflowtool.function;

import java.util.Map;

import org.infoglue.cms.controllers.kernel.impl.simple.ContentController;
import org.infoglue.cms.entities.content.ContentVO;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public class ContentProvider extends InfoglueFunction {
	/**
	 * 
	 */
	public static final String TRANSIENT_VARS_VARIABLE = "content";
	
	/**
	 * 
	 */
	public static final String RESULTSET_CONTENT_ID = ContentPopulator.PROPERTYSET_CONTENT_PREFIX + "contentID";
	
	
	
	/**
	 * 
	 */
	protected void doExecute(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException {
		populate(transientVars, ps);
	}

	/**
	 * 
	 */
	private void populate(final Map transientVars, final PropertySet ps) throws WorkflowException {
		if(ps.exists(RESULTSET_CONTENT_ID))
			try {
				final Integer contentID = new Integer(ps.getString(RESULTSET_CONTENT_ID));
				final ContentVO content = ContentController.getContentController().getContentVOWithId(contentID, getDatabase());
				transientVars.put(TRANSIENT_VARS_VARIABLE, content);
			} catch(Exception e) {
				getLogger().warn("Non-existing contentId found; removing from the resultset.");
				ps.remove(RESULTSET_CONTENT_ID);
			}
	}
}
