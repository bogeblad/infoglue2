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

DROP SEQUENCE cmAccessRightUser_seq;

CREATE SEQUENCE cmAccessRightUser_seq START WITH 100000 INCREMENT BY 1;

DROP TABLE cmAccessRightUser;

CREATE TABLE cmAccessRightUser (
  accessRightUserId number NOT NULL,
  accessRightId number NOT NULL,
  userName varchar2(150) NOT NULL,
  PRIMARY KEY  (accessRightUserId)
);
