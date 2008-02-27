package org.infoglue.cms.controllers.kernel.impl.simple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.LockNotGrantedException;
import org.exolab.castor.jdo.TransactionAbortedException;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.content.ContentVersion;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.entities.content.DigitalAsset;
import org.infoglue.cms.entities.content.DigitalAssetVO;
import org.infoglue.cms.entities.kernel.BaseEntityVO;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.security.InfoGluePrincipal;

@SuppressWarnings({"unused", "unchecked"})
public class ContentCleanerController  extends BaseController
{
    private final static Logger logger                                     = Logger.getLogger(ContentCleanerController.class);
    private static final LanguageController languageController             = LanguageController.getController();
    private static final ContentController contentController               = ContentController.getContentController();
    private static final ContentVersionController contentVersionController = ContentVersionController.getContentVersionController();
    private static final DigitalAssetController digitalAssetController     = DigitalAssetController.getController();    
    public static final int FACTOR_KB                                      = 1024,
                            FACTOR_MB                                      = 1048576, 
                            FACTOR_GB                                      = 1073741824;    
    private float recoveredDiskSpaceCnt                                    = 0.0f;
    private Long elapsedTime                                               = 0l;
    private Integer deletedContentVersionsCnt                              = 0, 
                    deletedDigitalAssetsCnt                                = 0;
    
    public static ContentCleanerController getContentCleanerController()
    {
        return new ContentCleanerController();
    }
    
    private ContentCleanerController() {}
    
    public void cleanSweep(final int hitSize2Retain) throws Exception
    {
        try {
            final Database db = CastorDatabaseService.getDatabase();
            beginTransaction(db);
            final List<LanguageVO> languageVOList = languageController.getLanguageVOList(db);
            final List<ContentVO> contentVOList = contentController.getContentVOList();             
            commitTransaction(db);
            for (ContentVO contentVO : contentVOList)
            {                   
                clean(contentVO, hitSize2Retain, languageVOList);
            }           
        }   
        catch(Exception e)
        {
            e.printStackTrace();
            //logger.error(e);
        }       
    }
    
    public void clean(final Integer contentId, final int hitSize2Retain) throws Exception
    {
        final Database db = CastorDatabaseService.getDatabase();
        beginTransaction(db);
        final ContentVO contentVO = contentController.getContentVOWithId(contentId);                
        final List<LanguageVO> languageVOList = languageController.getLanguageVOList(db);
        commitTransaction(db);
        clean(contentVO, hitSize2Retain, languageVOList);
    }
    
    public void clean(final Integer contentId, final int hitSize2Retain, final List<LanguageVO> languageVOList) throws Exception
    {
        final Database db = CastorDatabaseService.getDatabase();
        beginTransaction(db);
        final ContentVO contentVO = contentController.getContentVOWithId(contentId);                
        commitTransaction(db);
        clean(contentVO, hitSize2Retain, languageVOList);
    }
    
    public void clean(final ContentVO contentVO, final int hitSize2Retain, final List<LanguageVO> languageVOList) throws Exception
    {
        // Recursive clean for branches
        if (contentVO.getIsBranch())
        {
            final List<ContentVO> childs = contentController.getContentChildrenVOList(contentVO.getContentId());
            for (final ContentVO child : childs)
            {
                clean(child, hitSize2Retain, languageVOList);
            }
        }
        // Start cleaning content
        for (LanguageVO languageVO : languageVOList)
        {
            clean(contentVO, languageVO, hitSize2Retain);
        }                   
    }
    
    private void clean(final ContentVO contentVO, final LanguageVO languageVO, final int hitSize2Retain) throws Exception
    {
        final List<ContentVersion> contentVersionList2Retain = 
            collectContentVersionList2Retain(contentVO, languageVO, hitSize2Retain);
        // Do this only when we have a sufficient number of content versions, if less there is no need to do this.
        // The list of content versions can hold both the <hitSize> quantity and one copy of the latest published content version,
        // this happens if none of the content versions found was the last published.  
        if (contentVersionList2Retain.size() >= hitSize2Retain)
        {
            cleanContent(contentVO, languageVO, contentVersionList2Retain);
        }
    }
    
    private void cleanContent(final ContentVO contentVO, final LanguageVO languageVO, 
            final List<ContentVersion> contentVersionList2Retain) throws Exception
    {
        final long startTime = System.currentTimeMillis();
        final Database db = CastorDatabaseService.getDatabase();
        beginTransaction(db);
        // Retrive all conten verisons for this content
        final List<ContentVersion> contentVerisionList = 
            contentVersionController.getContentVersionsWithParentAndLanguage(
                    contentVO.getContentId(), languageVO.getLanguageId(), db);      
        for (final ContentVersion contentVersion : contentVerisionList)
        {
            // Should this content version be retained?
            if (!isRetainedContentVersion(contentVersion, contentVersionList2Retain))
            {
                logger.info("Listing digital assets for ContentVersion:" + contentVersion.getContentVersionId());
                // Retrive all digital assets for this content version
                final ArrayList<DigitalAsset> items = new ArrayList(contentVersion.getDigitalAssets());
                deletedContentVersionsCnt += items.size();
                for (final DigitalAsset digitalAsset : items) 
                {
                    recoveredDiskSpaceCnt += digitalAsset.getAssetFileSize();
                    final Integer digitalAssetId = digitalAsset.getDigitalAssetId();
                    logger.info("\tDead Digital Asset: " + digitalAsset.getAssetFileName());
                    // Delete all digital assets and their references that belongs to this content version
                    contentVersionController.deleteDigitalAssetRelation(contentVersion.getContentVersionId(), digitalAsset, db);
                    digitalAssetController.delete(digitalAssetId, db);
                }
                // Delete the content version as well
                deletedContentVersionsCnt += 1;
                contentVersionController.delete(contentVersion, db, true);
                logger.info("ContentVersion: " + contentVersion.getContentVersionId() + " Is Dead Meat.");
                
            }
        }
        commitTransaction(db);
        elapsedTime += System.currentTimeMillis() - startTime;
    }
    
    private boolean isRetainedContentVersion(final ContentVersion contentVersion, 
            final List<ContentVersion> contentVerisionList2Retain)
    {
        for (final ContentVersion retainedContentVersion : contentVerisionList2Retain)
        {
            if (contentVersion.getContentVersionId().intValue() == 
                retainedContentVersion.getContentVersionId().intValue())
            {
                return true;
            }
        }
        return false;
    }
    
    private List<ContentVersion> collectContentVersionList2Retain(final ContentVO contentVO, final LanguageVO languageVO, 
            final int hitSize) throws Exception
    {
        final Integer contentId = contentVO.getContentId(), languageId = languageVO.getLanguageId();
        final Database db = CastorDatabaseService.getDatabase();
        beginTransaction(db);
        // Retrive the latest content versions for hitSize
        final List<ContentVersion> contentVerisionsList2Retain = 
            contentVersionController.getLatestContentVersionsForHitSize(contentId, languageId, hitSize, db);
        // If none of them is published, fetch the latest published to this list also
        if (!hasState(contentVerisionsList2Retain, ContentVersionVO.PUBLISHED_STATE))
        {
            final ContentVersion latestPublished = 
                contentVersionController.getLatestPublishedContentVersion(contentId, languageId, db);
            if (latestPublished != null)
            {
                contentVerisionsList2Retain.add(latestPublished);
            }
        }
        commitTransaction(db);
        return contentVerisionsList2Retain;
    }
    
    private boolean hasState(final List<ContentVersion> contentVerisionsList2Retain, final Integer state)
    {
        for (final ContentVersion contentVersion : contentVerisionsList2Retain)
        {
            if (contentVersion.getStateId().intValue() == state.intValue())
            {
                return true;
            }
        }
        return false;
    }
    
    public float getCDSFactor(final int factor)
    {
        return recoveredDiskSpaceCnt / factor;
    }
    
    public Integer getDeletedDigitalAsstesCounter()
    {
        return deletedDigitalAssetsCnt;
    }
    
    public Integer getDeletedContentVersionsCounter()
    {
        return deletedContentVersionsCnt;
    }
    
    public Long getElapsedTime()
    {
        return elapsedTime;
    }
    
    @Override
    public BaseEntityVO getNewVO() {
        return null;
    }
    
    public static void main(String[] args) {
        final ContentCleanerController controller = ContentCleanerController.getContentCleanerController();
        try {
            controller.cleanSweep(1);
            //controller.clean(325, 1);
            logger.info("Removed " + controller.getDeletedContentVersionsCounter() + " content versions.");
            logger.info("Removed " + controller.getDeletedDigitalAsstesCounter() + " digital assets.");
            logger.info("Recovered " + controller.getCDSFactor(ContentCleanerController.FACTOR_MB) + " mb disk space.");
            logger.info("Time elapsed: " + ((controller.getElapsedTime()/1000) / 60) + " min.");            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
