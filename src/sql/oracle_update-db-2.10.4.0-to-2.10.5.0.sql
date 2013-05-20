CREATE TABLE cmPageDeliveryMetaData (
  pageDeliveryMetaDataId number NOT NULL,
  siteNodeId number NOT NULL,
  languageId number NOT NULL,
  contentId number NOT NULL,
  lastModifiedDateTime date NOT NULL,
  selectiveCacheUpdateNotAppl number NOT NULL,
  lastModifiedTimeout number default -1 NOT NULL,
  PRIMARY KEY (pageDeliveryMetaDataId) 
);

CREATE  TABLE cmPageDeliveryMetaDataEntity (
  pageDeliveryMetaDataEntityId number NOT NULL,
  pageDeliveryMetaDataId number NOT NULL,
  siteNodeId number NOT NULL,
  contentId number NOT NULL,
  PRIMARY KEY (pageDeliveryMetaDataEntityId) 
);

create index pageDeliveryMetaDataIDX on cmPageDeliveryMetaData(siteNodeId, languageId, contentId);
