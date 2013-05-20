CREATE  TABLE cmPageDeliveryMetaData (
  pageDeliveryMetaDataId INT NOT NULL AUTO_INCREMENT ,
  siteNodeId INT NOT NULL ,
  languageId INT NOT NULL ,
  contentId INT NOT NULL ,
  lastModifiedDateTime TIMESTAMP NOT NULL ,
  selectiveCacheUpdateNotApplicable TINYINT NOT NULL DEFAULT 0 ,
  lastModifiedTimeout INT NOT NULL DEFAULT -1 ,
  PRIMARY KEY (pageDeliveryMetaDataId) )
ENGINE = MyISAM;

CREATE  TABLE cmPageDeliveryMetaDataEntity (
  pageDeliveryMetaDataEntityId INT NOT NULL AUTO_INCREMENT ,
  pageDeliveryMetaDataId INT NOT NULL ,
  siteNodeId INT NULL ,
  contentId INT NULL ,
  PRIMARY KEY (pageDeliveryMetaDataEntityId) )
ENGINE = MyISAM;

create index pageDeliveryMetaDataIDX on cmPageDeliveryMetaData(siteNodeId, languageId, contentId);