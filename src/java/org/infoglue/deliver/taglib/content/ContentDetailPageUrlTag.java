/* ===============================================================================
*
* Part of the InfoGlue Content Management Platform (www.infoglue.org)
*
* ===============================================================================
*
*  Copyright (C)
* 
* This program is free software; you can redistribute it and/or modify it under
* the terms of the GNU General Public License version 2, as published by the
* Free Software Foundation. See the file LICENSE.html for more information.
* 
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY, including the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License along with
* this program; if not, write to the Free Software Foundation, Inc. / 59 Temple
* Place, Suite 330 / Boston, MA 02111-1307 / USA.
*
* ===============================================================================
*/

package org.infoglue.deliver.taglib.content;

import java.util.List;

import javax.servlet.jsp.JspException;

import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.structure.SiteNodeVO;
import org.infoglue.deliver.taglib.component.ComponentLogicTag;

public class ContentDetailPageUrlTag extends ComponentLogicTag 
{
	private static final long serialVersionUID = 4050206323348354355L;

	private Integer siteNodeId;
	private Integer contentId;
	private String propertyName;
    private boolean useInheritance = true;
	private boolean useRepositoryInheritance = true;
    private boolean useStructureInheritance = true;

	public int doEndTag() throws JspException
    {
        ContentVO contentVO = getContent();
        	
        if(contentVO != null)
        {
	        if(contentVO.getExtraProperties().get("detailSiteNodeId") != null)
			{
				Integer detailSiteNodeId = (Integer)contentVO.getExtraProperties().get("detailSiteNodeId");
				setResultAttribute(getController().getPageUrl(detailSiteNodeId, getController().getLanguageId(), new Integer(-1)));
			}
			else
			{
				List referencingSiteNodeVOList = getController().getReferencingPages(contentVO.getId(), 50, true);
				if(referencingSiteNodeVOList.size() > 0)
				{
					SiteNodeVO detailSiteNodeVO = (SiteNodeVO)referencingSiteNodeVOList.get(0);
					setResultAttribute(getController().getPageUrl(detailSiteNodeVO.getId(), getController().getLanguageId(), new Integer(-1)));				
				}
				else
				{
					setResultAttribute(null);
				}
			}
        }
        else
        {
        	setResultAttribute(null);
        }
        
	    return EVAL_PAGE;
    }
    
	private ContentVO getContent() throws JspException
	{	
	    if(this.contentId != null)
	    {
			if(this.contentId.intValue() < 1)
				return null;

	    	return this.getController().getContent(this.contentId);
	    }
	    else if(this.propertyName != null)
	    {
	        if(this.siteNodeId != null)
	            return this.getComponentLogic().getBoundContentWithDetailSiteNodeId(siteNodeId, propertyName, useInheritance);
	        else
	            return this.getComponentLogic().getBoundContentWithDetailSiteNodeId(propertyName, useInheritance, useRepositoryInheritance, useStructureInheritance);
	    }
	    else if(this.getController().getContentId() != null && this.getController().getContentId().intValue() > -1)
	    {
	    	return this.getController().getContent();
	    }
	    else
	    {
	    	return null;
	    }
	}

    public void setContentId(String contentId) throws JspException
    {
        this.contentId = evaluateInteger("content", "contentId", contentId);
    }

    public void setSiteNodeId(String siteNodeId) throws JspException
    {
        this.siteNodeId = evaluateInteger("content", "siteNodeId", siteNodeId);
    }

    public void setPropertyName(String propertyName) throws JspException
    {
        this.propertyName = evaluateString("ContentDetailPageUrlTag", "propertyName", propertyName);
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
