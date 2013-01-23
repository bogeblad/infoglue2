package org.infoglue.deliver.taglib.content;

import java.util.List;

import javax.servlet.jsp.JspException;

import org.infoglue.deliver.taglib.TemplateControllerTag;

public class RelatedContentsTag extends TemplateControllerTag {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3761686771326530105L;
	
	private Integer contentId;
	private boolean onlyFirst;
	private String attributeName;
	private boolean checkHasVersion = true;
	private boolean checkHasAccess = true;

	public RelatedContentsTag()
	{
		super();
	}

    public int doEndTag() throws JspException
    {
		produceResult(getRelatedContents());
		
		this.checkHasVersion = true;
		this.checkHasAccess = true;
		
        return EVAL_PAGE;
    }

	private Object getRelatedContents() throws JspException
	{
		final List related = getController().getRelatedContents(contentId, attributeName, checkHasVersion, checkHasAccess);
		if(onlyFirst)
			return related.isEmpty() ? null : related.get(0);
		return related;
	}

    public void setContentId(String contentId) throws JspException
    {
        this.contentId = evaluateInteger("groupForContent", "contentId", contentId);
    }

    public void setOnlyFirst(boolean onlyFirst)
    {
        this.onlyFirst = onlyFirst;
    }

    public void setCheckHasVersion(boolean checkHasVersion)
    {
        this.checkHasVersion = checkHasVersion;
    }

    public void setCheckHasAccess(boolean checkHasAccess)
    {
        this.checkHasAccess = checkHasAccess;
    }

    public void setAttributeName(String attributeName)
    {
        this.attributeName = attributeName;
    }
}
