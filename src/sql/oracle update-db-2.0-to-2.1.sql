-- ===============================================================================
--
-- Part of the InfoGlue Content Management Platform (www.infoglue.org)
--
-- ===============================================================================
--
--  Copyright (C)
--
-- This program is free software; you can redistribute it and/or modify it under
-- the terms of the GNU General Public License version 2, as published by the
-- Free Software Foundation. See the file LICENSE.html for more information.
--
-- This program is distributed in the hope that it will be useful, but WITHOUT
-- ANY WARRANTY, including the implied warranty of MERCHANTABILITY or FITNESS
-- FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
--
-- You should have received a copy of the GNU General Public License along with
-- this program; if not, write to the Free Software Foundation, Inc. / 59 Temple
-- Place, Suite 330 / Boston, MA 02111-1307 / USA.
--
-- ===============================================================================
--
-- $Id: update-db-1.3-to-2.0.sql,v 1.14 2005/06/29 08:52:46 mattias Exp $
--
-- This script contains the database updates required to go from 2.0 to 2.1.
----------------------------------------------------------------------------------
-- Updates OSWorkflow tables so caller and owner can be longer strings.
----------------------------------------------------------------------------------

alter table OS_CURRENTSTEP MODIFY OWNER varchar(255);
alter table OS_HISTORYSTEP MODIFY OWNER varchar(255);
alter table OS_CURRENTSTEP MODIFY CALLER varchar(255);
alter table OS_HISTORYSTEP MODIFY CALLER varchar(255);

DROP INDEX "OS_CURRENTSTEP_OWNERINDEX";
DROP INDEX "OS_CURRENTSTEP_OWNERCALLER";
DROP INDEX "OS_HISTORYSTEP_OWNERINDEX";
DROP INDEX "OS_HISTORYSTEP_CALLERINDEX";

CREATE INDEX "OS_CURRENTSTEP_OWNERINDEX" ON OS_CURRENTSTEP(OWNER);
CREATE INDEX "OS_CURRENTSTEP_OWNERCALLER" ON OS_CURRENTSTEP(CALLER);
CREATE INDEX "OS_HISTORYSTEP_OWNERINDEX" ON OS_HISTORYSTEP(OWNER);
CREATE INDEX "OS_HISTORYSTEP_CALLERINDEX" ON OS_HISTORYSTEP(CALLER);

DROP INDEX referencingEntityName ON cmRegistry;
DROP INDEX referencingEntityId ON cmRegistry;
DROP INDEX entityName ON cmRegistry;
DROP INDEX entityId ON cmRegistry;
DROP INDEX referencingEntityComplName ON cmRegistry;
DROP INDEX referencingEntityComplId ON cmRegistry;
DROP INDEX categoryContVersionId ON cmContentCategory;
DROP INDEX contVerDigAssetDigAssId ON cmContentVersionDigitalAsset;
DROP INDEX contVerDigAssetContVerId ON cmContentVersionDigitalAsset;

CREATE INDEX referencingEntityName ON cmRegistry(referencingEntityName);
CREATE INDEX referencingEntityId ON cmRegistry(referencingEntityId);
CREATE INDEX entityName ON cmRegistry(entityName);
CREATE INDEX entityId ON cmRegistry(entityId);
CREATE INDEX referencingEntityComplName ON cmRegistry(referencingEntityComplName);
CREATE INDEX referencingEntityComplId ON cmRegistry(referencingEntityComplId);
CREATE INDEX categoryContVersionId ON cmContentCategory(contentVersionId);
CREATE INDEX contVerDigAssetDigAssId ON cmContentVersionDigitalAsset(digitalAssetId);
CREATE INDEX contVerDigAssetContVerId ON cmContentVersionDigitalAsset(contentVersionId);

----------------------------------------------------------------------------------
-- Add new interception point for workflows				                        --
----------------------------------------------------------------------------------
INSERT INTO cmInterceptionPoint (interceptionPointId, category, name, description, usesExtraDataForAccessControl) VALUES 
  (31,'Workflow','Workflow.Create','This point checks access to creating a new workflow',1);
INSERT INTO cmInterceptionPointInterceptor (interceptionPointId, interceptorId) VALUES
  (31, 1);

-- ---------------------------------------------------------------------------- --
-- Adding new column to siteNodeVersion for special pageCacheKey                       --
-- ---------------------------------------------------------------------------- --
alter table cmSiNoVer add pageCacheKey varchar(255);
