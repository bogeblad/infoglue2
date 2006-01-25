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

public class UpdateContentVersionTag extends InfoGlueWebServiceTag implements ContentVersionParameterInterface
{
    /**
     * The universal version identifier.
     */
    private static final long serialVersionUID = -1904980538720103871L;

    /**
     *  
     */
    private String operationName = "updateContentVersion";

    /**
     * The map containing the content that should be updated.
     */

    private Map contentVersion = new HashMap();

	private Integer contentVersionId;
    private Integer stateId;
    private String versionComment;
	private Integer languageId;
   	private Integer contentId;
	private String versionValue;

    /**
     *  
     */
    private InfoGluePrincipal principal;

    /**
     *  
     */
    public UpdateContentVersionTag()
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
            if(this.contentVersionId != null)
                contentVersion.put("contentVersionId", this.contentVersionId);
            if(this.contentId != null)
                contentVersion.put("contentId", this.contentId);
            if(this.languageId != null)
                contentVersion.put("languageId", this.languageId);
            if(this.stateId != null)
                contentVersion.put("stateId", this.stateId);
            if(this.versionComment != null)
                contentVersion.put("versionComment", this.versionComment);
            if(this.versionValue != null)
                contentVersion.put("versionValue", this.versionValue);
                
            this.invokeOperation("contentVersion", contentVersion);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new JspTagException(e.getMessage());
        }

        contentVersion.clear();
        this.contentId = null;
        this.contentVersionId = null;
        this.languageId = null;
        this.versionComment = null;
        this.versionValue = null;
        this.stateId = null;
        
        return EVAL_PAGE;
    }

	/**
	 * Adds the content version attribute to the contentVersion Value.
	 * 
	 * @throws JspException if the ancestor tag isn't a url tag.
	 */
	public void addContentVersionAttribute(String name, String value) throws JspException
	{
	    Map contentVersionAttributes = (Map)this.contentVersion.get("contentVersionAttributes");
	    if(contentVersionAttributes == null)
	    {
	        contentVersionAttributes = new HashMap();
	        this.contentVersion.put("contentVersionAttributes", contentVersionAttributes);
	    }

	    contentVersionAttributes.put(name, value);
	}

	/**
	 * Adds the content version attribute to the contentVersion Value.
	 * 
	 * @throws JspException if the ancestor tag isn't a url tag.
	 */
	public void addDigitalAsset(RemoteAttachment remoteAttachment) throws JspException
	{
	    List digitalAssets = (List)this.contentVersion.get("digitalAssets");
	    if(digitalAssets == null)
	    {
	        digitalAssets = new ArrayList();
	        this.contentVersion.put("digitalAssets", digitalAssets);
	    }

	    digitalAssets.add(remoteAttachment);
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

    public void setContentVersionId(String contentVersionId) throws JspException
    {
        this.contentVersionId = evaluateInteger("updateContentVersion", "contentVersionId", contentVersionId);
    }

    public void setContentId(String contentId) throws JspException
    {
        this.contentId = evaluateInteger("updateContentVersion", "contentId", contentId);
    }

    public void setLanguageId(String languageId) throws JspException
    {
        this.languageId = evaluateInteger("updateContentVersion", "languageId", languageId);
    }

    public void setStateId(String stateId) throws JspException
    {
        this.stateId = evaluateInteger("updateContentVersion", "stateId", stateId);
    }

    public void setVersionComment(String versionComment) throws JspException
    {
        this.versionComment = evaluateString("updateContentVersion", "versionComment", versionComment);
    }

    public void setVersionValue(String versionValue) throws JspException
    {
        this.versionValue = evaluateString("updateContentVersion", "versionValue", versionValue);
    }

    public String getOperationName()
    {
        return this.operationName;
    }
}