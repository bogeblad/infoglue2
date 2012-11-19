/**
 * 
 */
package org.infoglue.deliver.taglib.content;

import javax.servlet.jsp.JspException;

import org.infoglue.deliver.taglib.component.ComponentLogicTag;

/**
 * @author Erik Stenbäcka <stenbacka@gmail.com>
 *
 */
public class ExternalBindingsTag extends ComponentLogicTag
{
	private static final long serialVersionUID = -7693066335269044674L;

	private String propertyName;
	private boolean useInheritance = true;
	private boolean useRepositoryInheritance = true;
    private boolean useStructureInheritance = true;

	public int doEndTag() throws JspException
    {
        setResultAttribute(getComponentLogic().getExternalBindings(propertyName, useInheritance, useRepositoryInheritance, useStructureInheritance));

		propertyName = null;
		useInheritance = true;
		useRepositoryInheritance = true;
	    useStructureInheritance = true;

		return EVAL_PAGE;
    }

	public void setPropertyName(String propertyName) throws JspException
	{
        this.propertyName = evaluateString("boundContents", "propertyName", propertyName);
	}

	public void setUseInheritance(boolean useInheritance)
	{
		this.useInheritance = useInheritance;
	}

    public void setUseRepositoryInheritance(boolean useRepositoryInheritance)
    {
        this.useRepositoryInheritance = useRepositoryInheritance;
    }

    public void setUseStructureInheritance(boolean useStructureInheritance)
    {
        this.useStructureInheritance = useStructureInheritance;
    }
}
