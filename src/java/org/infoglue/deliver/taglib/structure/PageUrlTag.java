package org.infoglue.deliver.taglib.structure;

import javax.servlet.jsp.JspException;

import org.infoglue.deliver.taglib.TemplateControllerTag;

public class PageUrlTag extends TemplateControllerTag {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4050485595074016051L;
	
	private Integer siteNodeId;
	private Integer languageId;
	private Integer contentId;

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
		return getController().getPageUrl(siteNodeId, languageId, contentId);
	}

	public void setSiteNodeId(final String siteNodeId) throws JspException
    {
        this.siteNodeId = evaluateInteger("groupForContent", "siteNodeId", siteNodeId);
    }

    public void setLanguageId(final String languageId) throws JspException
    {
        this.languageId = evaluateInteger("groupForContent", "languageId", languageId);
    }

    public void setContentId(final String contentId) throws JspException
    {
        this.contentId = evaluateInteger("groupForContent", "contentId", contentId);
    }
}
