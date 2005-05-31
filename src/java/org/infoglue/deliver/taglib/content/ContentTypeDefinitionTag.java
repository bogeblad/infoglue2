package org.infoglue.deliver.taglib.content;

import javax.servlet.jsp.JspException;

import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.deliver.taglib.TemplateControllerTag;

public class ContentTypeDefinitionTag extends TemplateControllerTag {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3257002150969421873L;
	
	private Integer contentId;

	public ContentTypeDefinitionTag() 
	{
		super();
	}

    public int doEndTag() throws JspException
    {
		produceResult(getContentTypeDefinition());
        return EVAL_PAGE;
    }

	private ContentTypeDefinitionVO getContentTypeDefinition() throws JspException
	{
		return getController().getContentTypeDefinitionVO(contentId);
	}

    public void setContentId(Integer contentId)
    {
        this.contentId = contentId;
    }
}
