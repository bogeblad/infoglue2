package org.infoglue.deliver.taglib.structure;

import javax.servlet.jsp.JspException;

import org.infoglue.deliver.taglib.TemplateControllerTag;

public class PageUrlTag extends TemplateControllerTag {
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

	public void setSiteNodeId(Integer siteNodeId)
    {
        this.siteNodeId = siteNodeId;
    }

    public void setLanguageId(Integer languageId)
    {
        this.languageId = languageId;
    }

    public void setContentId(Integer contentId)
    {
        this.contentId = contentId;
    }
}
