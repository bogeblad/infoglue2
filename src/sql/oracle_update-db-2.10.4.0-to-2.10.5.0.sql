CREATE TABLE cmPageDeliveryMetaData (
  pageDeliveryMetaDataId number NOT NULL,
  siteNodeId number NOT NULL,
  languageId number NOT NULL,
  contentId number NOT NULL,
  lastModifiedDateTime date NOT NULL,
  selectiveCacheUpdateNotAppl number NOT NULL,
  lastModifiedTimeout date,
  PRIMARY KEY (pageDeliveryMetaDataId) 
);

CREATE SEQUENCE cmPageDeliveryMetaData_seq START WITH 1 INCREMENT BY 1;

CREATE  TABLE cmPageDeliveryMetaDataEnt (
  pageDeliveryMetaDataEntityId number NOT NULL,
  pageDeliveryMetaDataId number NOT NULL,
  siteNodeId number,
  contentId number,
  PRIMARY KEY (pageDeliveryMetaDataEntityId) 
);

CREATE SEQUENCE cmPageDeliveryMetaDataEnt_seq START WITH 1 INCREMENT BY 1;

create index pageDeliveryMetaDataIDX on cmPageDeliveryMetaData(siteNodeId, languageId, contentId);
