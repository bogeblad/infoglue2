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

package org.infoglue.cms.treeservice;

import org.infoglue.cms.net.*;
import org.infoglue.cms.util.CmsLogger;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.net.*;

import java.util.Hashtable;

public abstract class JServiceBuilder extends HttpServlet
{
	protected HttpServletRequest request;
	 
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
	}

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		this.request = request;
		Hashtable inHash = requestToHashtable(request);
		CmsLogger.logInfo("Got request...");
		
		PrintWriter out = null;
        
        try
        {  
            
            
            // get an input stream from the applet
	        out = response.getWriter();
	        
	        CommunicationEnvelope requestEnvelope = deserializeEnvelope(inHash);
	        CommunicationEnvelope reponseEnvelope = execute(requestEnvelope); 
			String responseString = toEncodedString(serializeEnvelope(reponseEnvelope));
	        	        
	        // send back a confirmation message to the applet
            CmsLogger.logInfo("Sending the string to the applet:" + responseString);
	        out.println(responseString);
	            
	        out.flush();	        
	        out.close();
	        CmsLogger.logInfo("Complete.");
        }
        catch (Exception e)
        {
			e.printStackTrace();    
        }
	}


    /**
	 * Encodes a hash table to an URL encoded string.
	 * 
	 * @param inHash The hash table you would like to encode
	 * @return A URL encoded string.
	 */
		
	private String toEncodedString(Hashtable inHash) throws Exception
	{
	    StringBuffer buffer = new StringBuffer();
	    Enumeration names = inHash.keys();
	    while(names.hasMoreElements())
	    {
	        String name = names.nextElement().toString();
	        String value = inHash.get(name).toString();
	        buffer.append(URLEncoder.encode(name, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8"));
			if(names.hasMoreElements())
	        {
	            buffer.append("&");
	        }
	    }
	    return buffer.toString();
	}
	
	
	private CommunicationEnvelope deserializeEnvelope(Hashtable hash)
	{
		CommunicationEnvelope communicationEnvelope = new CommunicationEnvelope();
		communicationEnvelope.setAction("" + hash.get("action"));
		communicationEnvelope.setStatus("" + hash.get("status"));
		CmsLogger.logInfo("Action:" + communicationEnvelope.getAction());
		CmsLogger.logInfo("Status:" + communicationEnvelope.getStatus());
		
		List nodes = new ArrayList();
		int i = 0;
		String id = (String)hash.get("nodeList." + i + ".id");
		while(id != null)
		{
			Node n = new Node();
			n.setId(new Integer(id));
			n.setName((String)hash.get("nodeList." + i + ".name"));
			n.setIsBranch(new Boolean((String)hash.get("nodeList." + i + ".isBranch")));
			nodes.add(n);
			CmsLogger.logInfo("Node:" + n);
			i++;
			id = (String)hash.get("nodeList." + i + ".id");
		}	
		communicationEnvelope.setNodes(nodes);
				
		return communicationEnvelope;		
	}


	private Hashtable serializeEnvelope(CommunicationEnvelope requestEnvelope)
	{
		Hashtable hash = new Hashtable();
		CmsLogger.logInfo("Serializing:" + requestEnvelope);
		hash.put("action", requestEnvelope.getAction());
		hash.put("status", requestEnvelope.getStatus());
		
		List nodes = requestEnvelope.getNodes();
		int i = 0;
		Iterator iterator = nodes.iterator();
		while(iterator.hasNext())
		{
			Node n = (Node)iterator.next();
			hash.put("nodeList." + i + ".id", "" + n.getId());
			hash.put("nodeList." + i + ".name", "" + n.getName());
			hash.put("nodeList." + i + ".isBranch", "" + n.getIsBranch());
			i++;
		}	
				
		return hash;		
	}

	public Hashtable requestToHashtable(HttpServletRequest req) 
	{	
        Hashtable input = new Hashtable();
				
	    for (Enumeration e = req.getParameterNames(); e.hasMoreElements() ;) 
	    {		        
	        String name = (String)e.nextElement();
	        String value = (String)req.getParameter(name);
            input.put(name, value);
	    }
        
        return input;	
		
	}


    public abstract CommunicationEnvelope execute(CommunicationEnvelope envelope);
}