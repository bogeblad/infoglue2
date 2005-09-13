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
 */
package org.infoglue.cms.applications.workflowtool.function.email;

import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

import javax.mail.BodyPart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.infoglue.cms.applications.workflowtool.function.InfoglueFunction;
import org.infoglue.cms.util.mail.MailService;
import org.infoglue.cms.util.mail.MailServiceFactory;

import com.opensymphony.workflow.WorkflowException;

/**
 * Note! This is a stub; will be finished soon...
 */
public class EmailFunction extends InfoglueFunction {
	/**
	 * 
	 */
	private static final String ENCODING = "UTF-8";
	
	/**
	 * 
	 */
	private static final String ADDRESS_DELIMITER = ",";
	
	/**
	 * 
	 */
	private static final String EMAIL_PARAMETER_PREFIX = "email_";
	
	/**
	 * 
	 */
	public static final String TO_PARAMETER = EMAIL_PARAMETER_PREFIX + "to";
	
	/**
	 * 
	 */
	public static final String FROM_PARAMETER = EMAIL_PARAMETER_PREFIX + "from";

	/**
	 * 
	 */
	public static final String BODY_PARAMETER = EMAIL_PARAMETER_PREFIX + "body";
	
	/**
	 * 
	 */
	private static final String TO_ARGUMENT = "to";

	/**
	 * 
	 */
	private static final String FROM_ARGUMENT = "from";

	/**
	 * 
	 */
	private static final String SUBJECT_ARGUMENT = "subject";

	/**
	 * 
	 */
	private static final String BODY_ARGUMENT = "body";

	/**
	 * 
	 */
	private static final String BODY_TYPE_ARGUMENT = "type";
	
	/**
	 * 
	 */
	private MailService service;
	
	/**
	 * 
	 */
	private MimeMessage message;
	
	/**
	 * 
	 */
	private MimeMultipart multipart;
	
	
	
	/**
	 * 
	 */
	public EmailFunction() 
	{
		super();
	}

	/**
	 * 
	 */
	protected void execute() throws WorkflowException 
	{
		initializeMailService();
		createMessage();
		sendMessage();
	}
	
	/**
	 * 
	 */
	private void createMessage() throws WorkflowException
	{
		getLogger().debug("Creating message.");
		initializeMessage();
		initializeMultipart();
		createMainBodyPart();
		createAttachments();
	}

	/**
	 * 
	 */
	private void initializeMessage() throws WorkflowException
	{
		getLogger().debug("Initializing message.");
		//message = service.createMessage();
		initializeTo();
		initializeFrom();
		initializeSubject();
	}
	
	/**
	 * 
	 */
	private void initializeMultipart() throws WorkflowException
	{
		getLogger().debug("Initializing multipart.");
		try 
		{
			multipart = new MimeMultipart();
			message.setContent(multipart);
		}
		catch(Exception e)
		{
			throwException(e);
		}
	}
	
	/**
	 * 
	 */
	private void initializeTo() throws WorkflowException
	{
		getLogger().debug("Initializing to.");
	}
	
	/**
	 * 
	 */
	private void initializeFrom() throws WorkflowException
	{
		getLogger().debug("Initializing from.");
		InternetAddress[] from = null;
		if(argumentExists(FROM_ARGUMENT))
		{
			getLogger().debug("Fetching 'from' from arguments.");
			from = createAddresses(getArgument(FROM_ARGUMENT)); // TODO : translateVariables
		}
		else
		{
			getLogger().debug("Fetching 'from' from parameters.");
			from = (InternetAddress[]) getParameter(FROM_PARAMETER);
		}
		try 
		{
			message.addFrom(from);
		}
		catch(Exception e)
		{
			throwException(e);
		}
		
	}
	
	/**
	 * 
	 */
	private void initializeSubject() throws WorkflowException
	{
		getLogger().debug("Initializing subject.");
		try 
		{
			message.setSubject(getArgument(SUBJECT_ARGUMENT), ENCODING); // TODO : translateVariables
		}
		catch(Exception e)
		{
			throwException(e);
		}
	}
	
	/**
	 * 
	 */
	private void createMainBodyPart() throws WorkflowException
	{
		getLogger().debug("Initializing main body part.");
		try 
		{
			final BodyPart part = new MimeBodyPart();
			part.setContent(getBody(), getArgument(BODY_TYPE_ARGUMENT));
			multipart.addBodyPart(part);
		}
		catch(Exception e)
		{
			throwException(e);
		}
	}
	
	/**
	 * 
	 */
	private String getBody() throws WorkflowException
	{
		if(argumentExists(BODY_ARGUMENT))
		{
			getLogger().debug("Fetching 'body' from arguments.");
			return getParameter(BODY_ARGUMENT).toString();
		}
		getLogger().debug("Fetching 'body' from parameters.");
		return getParameter(BODY_PARAMETER).toString();  // TODO : translateVariables
	}
	
	/**
	 * 
	 */
	private void createAttachments() throws WorkflowException
	{
		
	}
	
	/**
	 * 
	 */
	private InternetAddress[] createAddresses(final String s) throws WorkflowException
	{
		final Collection addresses = new ArrayList();
		for(final StringTokenizer st = new StringTokenizer(s, ADDRESS_DELIMITER); st.hasMoreTokens(); )
		{
			addresses.add(createAddress(st.nextToken()));
		}
		return (InternetAddress[]) addresses.toArray();
	}
	
	/**
	 * 
	 */
	private InternetAddress createAddress(final String email) throws WorkflowException
	{
		InternetAddress address = null;
		try 
		{
			address = new InternetAddress(email);
		}
		catch(Exception e)
		{
			throwException(e);
		}
		return address;
	}
	
	/**
	 * 
	 */
	private void initializeMailService() throws WorkflowException
	{
		getLogger().debug("Initializing mail service.");
		try 
		{
			service = MailServiceFactory.getService();
		}
		catch(Exception e)
		{
			throwException(e);
		}
	}
	
	/**
	 * 
	 */
	private void sendMessage() throws WorkflowException
	{
		getLogger().debug("Sending message.");
		try 
		{
			//service.send(message);
		}
		catch(Exception e)
		{
			throwException(e);
		}
	}
}
