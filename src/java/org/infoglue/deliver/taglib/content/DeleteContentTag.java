package org.infoglue.deliver.taglib.content;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.infoglue.cms.controllers.kernel.impl.simple.UserControllerProxy;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.cms.util.DateHelper;
import org.infoglue.cms.webservices.elements.RemoteAttachment;
import org.infoglue.deliver.taglib.AbstractTag;
import org.infoglue.deliver.taglib.TemplateControllerTag;
import org.infoglue.deliver.util.webservices.DynamicWebservice;

/**
 * This tag helps update a content in the cms from the delivery application.
 */

public class DeleteContentTag extends InfoGlueWebServiceTag
{
    /**
     * The universal version identifier.
     */
    private static final long serialVersionUID = -1904980538720103871L;

    /**
     *  
     */
    private String operationName = "deleteContent";

    /**
     * The map containing the content that should be updated.
     */

    private Map content = new HashMap();

	private Integer contentId;

    /**
     *  
     */
    private InfoGluePrincipal principal;

    /**
     *  
     */
    public DeleteContentTag()
    {
        super();
    }

    /**
     * Initializes the parameters to make it accessible for the children tags
     * (if any).
     * 
     * @return indication of whether to evaluate the body or not.
     * @throws JspException
     *             if an error occurred while processing this tag.
     */
    public int doStartTag() throws JspException
    {
        return EVAL_BODY_INCLUDE;
    }

    /**
     *  
     */
    public int doEndTag() throws JspException
    {
        try
        {
            if(this.contentId != null)
                content.put("contentId", this.contentId);
                
            this.invokeOperation("content", content);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new JspTagException(e.getMessage());
        }

        content.clear();
        this.contentId = null;
        
        return EVAL_PAGE;
    }

    /**
     *  
     */
    public void setOperationName(final String operationName)
    {
        this.operationName = operationName;
    }

    /**
     *  
     */
    public void setPrincipal(final String principalString) throws JspException
    {
        this.principal = (InfoGluePrincipal) this.evaluate("remoteContentService", "principal", principalString, InfoGluePrincipal.class);
    }

    public void setContentId(String contentId) throws JspException
    {
        this.contentId = evaluateInteger("deleteContent", "contentId", contentId);
    }

    public String getOperationName()
    {
        return this.operationName;
    }
}