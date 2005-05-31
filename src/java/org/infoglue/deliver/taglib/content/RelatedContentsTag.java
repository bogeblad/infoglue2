package org.infoglue.deliver.taglib.content;

import java.util.List;

import javax.servlet.jsp.JspException;

import org.infoglue.deliver.taglib.TemplateControllerTag;

public class RelatedContentsTag extends TemplateControllerTag {
	private Integer contentId;
	private boolean onlyFirst;
	private String attributeName;

	public RelatedContentsTag()
	{
		super();
	}

    public int doEndTag() throws JspException
    {
		produceResult(getRelatedContents());
        return EVAL_PAGE;
    }

	private Object getRelatedContents() throws JspException
	{
		final List related = getController().getRelatedContents(contentId, attributeName);
		return (onlyFirst && !related.isEmpty()) ? related.get(0) : related;
	}

    public void setContentId(Integer contentId)
    {
        this.contentId = contentId;
    }

    public void setOnlyFirst(boolean onlyFirst)
    {
        this.onlyFirst = onlyFirst;
    }

    public void setAttributeName(String attributeName)
    {
        this.attributeName = attributeName;
    }
}
