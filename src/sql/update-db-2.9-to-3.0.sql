ALTER TABLE cmSubscription change entityName entityName varchar(100) DEFAULT NULL;
ALTER TABLE cmSubscription change entityId entityName varchar(200) DEFAULT NULL;


ALTER TABLE cmDigAsset MODIFY ( assetContentType VARCHAR2(255) );

alter table cmDigAsset modify assetContentType 


--ALTER TABLE cmSiteNode ADD COLUMN sortOrder INTEGER NOT NULL DEFAULT '-1';
--ALTER TABLE cmSiteNode ADD COLUMN isHidden TINYINT NOT NULL DEFAULT 0;

ALTER TABLE cmSiteNodeVersion ADD COLUMN sortOrder INT(10) NOT NULL DEFAULT -1;
ALTER TABLE cmSiteNodeVersion ADD COLUMN isHidden TINYINT UNSIGNED NOT NULL DEFAULT 0;

ALTER TABLE cmSiteNode ADD COLUMN isDeleted TINYINT NOT NULL DEFAULT 0;
ALTER TABLE cmContent ADD COLUMN isDeleted TINYINT NOT NULL DEFAULT 0;
ALTER TABLE cmRepository ADD COLUMN isDeleted TINYINT NOT NULL DEFAULT 0;

OBS::::::: add index på alla isDeleted, sortOrder och isHidden
