/**
 * $Id: StepFilter.java,v 1.2 2004/12/28 22:40:07 jed Exp $
 * Created by jed on Dec 28, 2004
 */
package org.infoglue.cms.util.workflow;

import org.infoglue.cms.entities.mydesktop.WorkflowStepVO;

/**
 * Provides a mechanism for filtering steps in a workflow based on the owner of the step and the current user
 * @author <a href="mailto:jedprentice@gmail.com">Jed Prentice</a>
 * @version $Revision: 1.2 $ $Date: 2004/12/28 22:40:07 $
 */
public interface StepFilter
{
	/**
	 * Indicates whether the given step is allowed by the filter
	 * @param step a workflow step
	 * @return true if the step is allowed, otherwise returns false
	 */
	boolean isAllowed(WorkflowStepVO step);
}
