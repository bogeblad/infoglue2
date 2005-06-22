package org.infoglue.deliver.taglib.structure;

import javax.servlet.jsp.JspException;

import org.infoglue.deliver.taglib.TemplateControllerTag;
import org.infoglue.deliver.taglib.component.ComponentLogicTag;

public class PageUrlTag extends ComponentLogicTag {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4050485595074016051L;
	
	private String propertyName;
	private boolean useInheritance = true;

	private Integer siteNodeId;
	private Integer languageId;
	private Integer contentId = new Integer(-1);

	private String extraParameters;
	
	public PageUrlTag() 
	{
		super();
	}

    public int doEndTag() throws JspException
    {
        produceResult(getPageUrl());
        return EVAL_PAGE;
    }

	private String getPageUrl() throws JspException
	{
	    if(this.languageId == null)
	        this.languageId = getController().getLanguageId();
	    
	    if(this.propertyName != null)
	        return getComponentLogic().getPageUrl(propertyName, contentId, languageId, useInheritance);
	    else
	        return getController().getPageUrl(siteNodeId, languageId, contentId);
	}

	public void setSiteNodeId(final String siteNodeId) throws JspException
    {
        this.siteNodeId = evaluateInteger("pageUrl", "siteNodeId", siteNodeId);
    }

    public void setLanguageId(final String languageId) throws JspException
    {
        this.languageId = evaluateInteger("pageUrl", "languageId", languageId);
    }

    public void setContentId(final String contentId) throws JspException
    {
        this.contentId = evaluateInteger("pageUrl", "contentId", contentId);
    }
    
    public void setPropertyName(String propertyName)
    {
        this.propertyName = propertyName;
    }
    
    public void setUseInheritance(boolean useInheritance)
    {
        this.useInheritance = useInheritance;
    }
    
    public void setExtraParameters(String extraParameters)
    {
        this.extraParameters = extraParameters;
    }
}
