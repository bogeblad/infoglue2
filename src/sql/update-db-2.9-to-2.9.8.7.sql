ALTER TABLE cmSubscription change entityName entityName varchar(100) DEFAULT NULL;
ALTER TABLE cmSubscription change entityId entityName varchar(200) DEFAULT NULL;

ALTER TABLE cmDigAsset MODIFY ( assetContentType VARCHAR2(255) );

ALTER TABLE cmSiteNodeVersion ADD COLUMN forceProtocolChange TINYINT(4) UNSIGNED NOT NULL DEFAULT 2;