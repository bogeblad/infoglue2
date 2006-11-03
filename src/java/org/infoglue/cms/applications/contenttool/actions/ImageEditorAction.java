/* ===============================================================================
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

package org.infoglue.cms.applications.contenttool.actions;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.infoglue.cms.applications.common.VisualFormatter;
import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;
import org.infoglue.cms.applications.databeans.AssetKeyDefinition;
import org.infoglue.cms.controllers.kernel.impl.simple.CastorDatabaseService;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentTypeDefinitionController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentVersionController;
import org.infoglue.cms.controllers.kernel.impl.simple.DigitalAssetController;
import org.infoglue.cms.controllers.kernel.impl.simple.GroupPropertiesController;
import org.infoglue.cms.controllers.kernel.impl.simple.LanguageController;
import org.infoglue.cms.controllers.kernel.impl.simple.RolePropertiesController;
import org.infoglue.cms.controllers.kernel.impl.simple.SiteNodeController;
import org.infoglue.cms.controllers.kernel.impl.simple.SiteNodeTypeDefinitionController;
import org.infoglue.cms.controllers.kernel.impl.simple.SiteNodeVersionController;
import org.infoglue.cms.controllers.kernel.impl.simple.UserPropertiesController;
import org.infoglue.cms.entities.content.DigitalAssetVO;
import org.infoglue.cms.entities.management.AvailableServiceBindingVO;
import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.entities.management.GroupProperties;
import org.infoglue.cms.entities.management.GroupPropertiesVO;
import org.infoglue.cms.entities.management.RoleProperties;
import org.infoglue.cms.entities.management.RolePropertiesVO;
import org.infoglue.cms.entities.management.SiteNodeTypeDefinitionVO;
import org.infoglue.cms.entities.management.UserProperties;
import org.infoglue.cms.entities.management.UserPropertiesVO;
import org.infoglue.cms.entities.structure.SiteNodeVO;
import org.infoglue.cms.entities.structure.SiteNodeVersionVO;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.cms.util.ConstraintExceptionBuffer;
import org.infoglue.deliver.applications.databeans.DatabaseWrapper;
import org.infoglue.deliver.controllers.kernel.impl.simple.BasicTemplateController;
import org.infoglue.deliver.controllers.kernel.impl.simple.IntegrationDeliveryController;
import org.infoglue.deliver.controllers.kernel.impl.simple.NodeDeliveryController;
import org.infoglue.deliver.util.BrowserBean;

import webwork.action.ActionContext;
import webwork.multipart.MultiPartRequestWrapper;
import com.mullassery.imaging.Imaging;
import com.mullassery.imaging.ImagingFactory;

/**
 * @author Mattias Bogeblad
 * @version 1.0
 * @since InfoglueCMS 2.4
 * 
 */

public class ImageEditorAction extends InfoGlueAbstractAction
{
    private final static Logger logger = Logger.getLogger(ImageEditorAction.class.getName());

	private static final long serialVersionUID = 1L;

	private DigitalAssetVO digitalAssetVO = null;
	private String modifiedFileUrl = "";
	private int xpos1, ypos1, xpos2, ypos2 = 0;
	private Integer contentVersionId = null;
	private Integer digitalAssetId   = null;

	private String digitalAssetKey   = null;
	private boolean isUpdated       = false;
	private String reasonKey;
	private ContentTypeDefinitionVO contentTypeDefinitionVO;
	//private DigitalAssetVO updatedDigitalAssetVO = null;
	private String closeOnLoad;
	private Integer contentTypeDefinitionId;
	
	private ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();
	private Imaging imaging = ImagingFactory.createImagingInstance(ImagingFactory.AWT_LOADER, ImagingFactory.JAVA2D_TRANSFORMER);
        	
    public String doExecute() throws Exception
    {
    	ceb.throwIfNotEmpty();
	
    	this.digitalAssetVO = DigitalAssetController.getDigitalAssetVOWithId(this.digitalAssetId);

        String filePath = DigitalAssetController.getDigitalAssetFilePath(this.digitalAssetVO.getDigitalAssetId());
        System.out.println("filePath:" + filePath);
    	BufferedImage original = javax.imageio.ImageIO.read(new File(filePath));

    	File outputFile = new File(CmsPropertyHandler.getDigitalAssetPath() + File.separator + "temp_" + this.getInfoGluePrincipal().getName() + "_" + digitalAssetVO.getDigitalAssetId() + ".png");
		javax.imageio.ImageIO.write(original, "PNG", outputFile);
		this.modifiedFileUrl = CmsPropertyHandler.getWebServerAddress() + "/" + CmsPropertyHandler.getDigitalAssetBaseUrl() + "/temp_" + this.getInfoGluePrincipal().getName() + "_" + digitalAssetVO.getDigitalAssetId() + ".png";
		System.out.println("modifiedFileUrl:" + modifiedFileUrl);

        return "success";
    }    

    public String doResize() throws Exception
    {
    	ceb.throwIfNotEmpty();

    	this.digitalAssetVO = DigitalAssetController.getDigitalAssetVOWithId(this.digitalAssetId);

    	File file = new File(CmsPropertyHandler.getDigitalAssetPath() + File.separator + "temp_" + this.getInfoGluePrincipal().getName() + "_" + digitalAssetVO.getDigitalAssetId() + ".png");
        //String filePath = DigitalAssetController.getDigitalAssetFilePath(this.digitalAssetVO.getDigitalAssetId());
    	//System.out.println("filePath:" + filePath);
    	BufferedImage original = javax.imageio.ImageIO.read(file);

    	//BufferedImage original = loadImageResource(getDigitalAssetUrl()); 
		
    	//scaleXY()
    	//BufferedImage image = imaging.scale(original, 1.2f, 0.6f);
    	
    	//scaleEqually() {
    	BufferedImage image = imaging.scale(original, 0.5f);

    	//resizeToFit()
    	//BufferedImage image = imaging.resize(original, 200, 800, true);

    	File outputFile = file;
		javax.imageio.ImageIO.write(image, "PNG", outputFile);
		this.modifiedFileUrl = CmsPropertyHandler.getWebServerAddress() + "/" + CmsPropertyHandler.getDigitalAssetBaseUrl() + "/temp_" + this.getInfoGluePrincipal().getName() + "_" + digitalAssetVO.getDigitalAssetId() + ".png";
		System.out.println("modifiedFileUrl:" + modifiedFileUrl);
		
        return "success";
    }    

    public String doCrop() throws Exception
    {
    	ceb.throwIfNotEmpty();

    	this.digitalAssetVO = DigitalAssetController.getDigitalAssetVOWithId(this.digitalAssetId);

    	File file = new File(CmsPropertyHandler.getDigitalAssetPath() + File.separator + "temp_" + this.getInfoGluePrincipal().getName() + "_" + digitalAssetVO.getDigitalAssetId() + ".png");
    	BufferedImage original = javax.imageio.ImageIO.read(file);

    	BufferedImage image = imaging.crop(original, xpos1, ypos1, xpos2 - xpos1, ypos2 - ypos1);

    	File outputFile = file;
		javax.imageio.ImageIO.write(image, "PNG", outputFile);
		this.modifiedFileUrl = CmsPropertyHandler.getWebServerAddress() + "/" + CmsPropertyHandler.getDigitalAssetBaseUrl() + "/temp_" + this.getInfoGluePrincipal().getName() + "_" + digitalAssetVO.getDigitalAssetId() + ".png";
		System.out.println("modifiedFileUrl:" + modifiedFileUrl);
		
        return "success";
    }    

    public String doSave() throws Exception
    {
    	ceb.throwIfNotEmpty();

    	this.digitalAssetVO = DigitalAssetController.getDigitalAssetVOWithId(this.digitalAssetId);

    	File file = new File(CmsPropertyHandler.getDigitalAssetPath() + File.separator + "temp_" + this.getInfoGluePrincipal().getName() + "_" + digitalAssetVO.getDigitalAssetId() + ".png");
   		System.out.println("saving file:" + file.getAbsolutePath());
   		
    	DigitalAssetVO newAsset = new DigitalAssetVO();
		newAsset.setAssetContentType(digitalAssetVO.getAssetContentType());
		newAsset.setAssetKey(digitalAssetVO.getAssetKey() + "_new");
		newAsset.setAssetFileName(digitalAssetVO.getAssetFileName());
		newAsset.setAssetFilePath(digitalAssetVO.getAssetFilePath());
		newAsset.setAssetFileSize(new Integer(new Long(file.length()).intValue()));
		InputStream is = new FileInputStream(file);
		
		if(this.contentVersionId != null)
		    digitalAssetVO = DigitalAssetController.create(newAsset, is, this.contentVersionId);
 		//else
		//    digitalAssetVO = DigitalAssetController.create(newAsset, is, this.entity, this.entityId);

		file.delete();
		
        return "success";
    }    

    public void setDigitalAssetKey(String digitalAssetKey)
	{
		this.digitalAssetKey = digitalAssetKey;
	}
		
	/**
	 * This method fetches the blob from the database and saves it on the disk.
	 * Then it returnes a url for it
	 */
	
	public String getDigitalAssetUrl() throws Exception
	{
		String imageHref = null;
		try
		{
       		imageHref = DigitalAssetController.getDigitalAssetUrl(this.digitalAssetVO.getDigitalAssetId());
		}
		catch(Exception e)
		{
		    logger.warn("We could not get the url of the digitalAsset: " + e.getMessage(), e);
		}
		
		return imageHref;
	}
	    
	public Integer getDigitalAssetId()
	{
		return digitalAssetId;
	}

	public void setDigitalAssetId(Integer digitalAssetId)
	{
		this.digitalAssetId = digitalAssetId;
	}

	public String getDigitalAssetKey()
	{
		return digitalAssetKey;
	}

	public boolean getIsUpdated()
	{
		return isUpdated;
	}

	public Integer getContentVersionId()
	{
		return contentVersionId;
	}

	public void setContentVersionId(Integer contentVersionId)
	{
		this.contentVersionId = contentVersionId;
	}
    
    public String getCloseOnLoad()
    {
        return closeOnLoad;
    }
    
    public void setCloseOnLoad(String closeOnLoad)
    {
        this.closeOnLoad = closeOnLoad;
    }

	public String getModifiedFileUrl() 
	{
		return modifiedFileUrl;
	}

	public int getXpos1() 
	{
		return xpos1;
	}

	public void setXpos1(int xpos1) 
	{
		this.xpos1 = xpos1;
	}

	public int getXpos2() 
	{
		return xpos2;
	}

	public void setXpos2(int xpos2) 
	{
		this.xpos2 = xpos2;
	}

	public int getYpos1() 
	{
		return ypos1;
	}

	public void setYpos1(int ypos1) 
	{
		this.ypos1 = ypos1;
	}

	public int getYpos2() 
	{
		return ypos2;
	}

	public void setYpos2(int ypos2) 
	{
		this.ypos2 = ypos2;
	}
    
}
