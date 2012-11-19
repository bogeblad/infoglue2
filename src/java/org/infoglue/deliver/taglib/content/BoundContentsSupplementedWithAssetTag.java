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
public class BoundContentsSupplementedWithAssetTag extends ComponentLogicTag
{
	private static final long serialVersionUID = -2082014468249286368L;

	private String propertyName;
	private Integer siteNodeId;
	private boolean useInheritance = true;
	private boolean useRepositoryInheritance = true;
    private boolean useStructureInheritance = true;

	public int doEndTag() throws JspException
    {
        if(siteNodeId == null)
        {
        	setResultAttribute(getComponentLogic().getBoundContentSupplementedWithAsset(propertyName, useInheritance, useRepositoryInheritance, useStructureInheritance));
        }
        else
        {
        	//setResultAttribute(getComponentLogic().getBoundContents(siteNodeId, propertyName, useInheritance));
        }

		propertyName = null;
		siteNodeId = null;
		useInheritance = true;
		useRepositoryInheritance = true;
	    useStructureInheritance = true;

		return EVAL_PAGE;
    }

	public void setSiteNodeId(String siteNodeId) throws JspException
	{
        this.siteNodeId = evaluateInteger("boundContents", "siteNodeId", siteNodeId);
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
