/**
 * $Id: StepFilter.java,v 1.1 2004/12/28 15:48:28 jed Exp $
 * Created by jed on Dec 28, 2004
 */
package org.infoglue.cms.util.workflow;

import com.opensymphony.workflow.spi.Step;

/**
 * Provides a mechanism for filtering steps in a workflow based on the owner of the step and the current user
 * @author <a href="mailto:jedprentice@gmail.com">Jed Prentice</a>
 * @version $Revision: 1.1 $ $Date: 2004/12/28 15:48:28 $
 */
public interface StepFilter
{
	/**
	 * Indicates whether the given step is allowed by the filter
	 * @param step a workflow step
	 * @return true if the step is allowed, otherwise returns false
	 */
	boolean isAllowed(Step step);
}
