-- $Id: cmContentCategory-column-patch.sql,v 1.1 2005/02/01 15:21:39 jed Exp $
-- Modifies cmContentCategory columns contentVersionId and categoryId so they
-- do not accept nulls.  This fixes an oversight in update-db-1.3-to-2.0.sql
-------------------------------------------------------------------------------
alter table cmContentCategory modify contentVersionId int(11) NOT NULL;
alter table cmContentCategory modify categoryId int(11) NOT NULL;
