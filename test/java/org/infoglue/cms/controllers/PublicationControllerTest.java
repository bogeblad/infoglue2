/* ===============================================================================
 *
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
 *
 * $Id: PublicationControllerTest.java,v 1.1 2005/02/28 23:47:53 frank Exp $
 */
package org.infoglue.cms.controllers;

import java.util.List;
import java.util.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.entities.management.CategoryAttribute;
import org.infoglue.cms.entities.publishing.PublicationVO;
import org.infoglue.cms.entities.publishing.PublicationDetailVO;
import org.infoglue.cms.entities.publishing.impl.simple.PublicationImpl;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentTypeDefinitionController;
import org.infoglue.cms.controllers.kernel.impl.simple.PublicationController;
import org.infoglue.cms.util.ResourceHelper;
import org.infoglue.cms.util.InfoGlueTestCase;
import org.infoglue.cms.exception.SystemException;

/**
 * @author Frank Febbraro (frank@phase2technology.com)
 */
public class PublicationControllerTest extends InfoGlueTestCase
{
	private PublicationVO testEdition;

	ArrayList testEditions = new ArrayList();
	ArrayList testDetails = new ArrayList();

	protected void setUp() throws Exception
	{
		super.setUp();

		//testEdition = createEdition(10);
	}

	protected void tearDown() throws Exception
	{
		for (Iterator iter = testEditions.iterator(); iter.hasNext();)
		{
			PublicationVO publicationVO = (PublicationVO)iter.next();
			PublicationController.deleteEntity(PublicationImpl.class, publicationVO.getId());
			assertRemoved(publicationVO.getId());
		}

	}

	public void testGetEditions() throws Exception
	{
		System.out.println("TEST!");
	}

	private PublicationVO createEdition(String name, Date publicationDate, int detailCount) throws SystemException
	{
		PublicationVO edition = new PublicationVO();
		edition.setRepositoryId(getRepoId());
		edition.setName(name);
		edition.setDescription(getName() + " description");
		edition.setPublicationDateTime(publicationDate);
		edition.setPublisher(getName());

		for (int i = 0; i < detailCount; i++)
		{
			PublicationDetailVO detail = new PublicationDetailVO();
			detail.setName("TestPublicationDetail");
			detail.setEntityClass("TEST-CLASS");
			detail.setEntityId(new Integer(-99));
			detail.setCreationDateTime(new Date());
			detail.setCreator(getName());
			detail.setTypeId(PublicationDetailVO.PUBLISH);
			edition.getPublicationDetails().add(detail);
		}

		PublicationVO savedEdition = PublicationController.create(edition);
		testEditions.add(savedEdition);
		return savedEdition;
	}

	// Make sure it was removed from the DB
	private void assertRemoved(Integer id) throws Exception
	{
		try
		{
			PublicationController.getVOWithId(PublicationImpl.class, id);
			fail("The Publication was not deleted");
		}
		catch(Exception e)
		{
			// Exception is expected
		}
	}
}
