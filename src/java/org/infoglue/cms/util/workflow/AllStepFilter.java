/**
 * $Id: AllStepFilter.java,v 1.1 2004/12/28 15:48:28 jed Exp $
 * Created by jed on Dec 28, 2004
 */
package org.infoglue.cms.util.workflow;

import com.opensymphony.workflow.spi.Step;

/**
 * No-op filter that allows all steps
 * @author jed
 * @version $Revision: 1.1 $ $Date: 2004/12/28 15:48:28 $
 */
public class AllStepFilter implements StepFilter
{
	public boolean isAllowed(Step step)
	{
		return true;
	}
}
