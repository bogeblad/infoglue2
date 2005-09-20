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
package org.infoglue.deliver.taglib.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.infoglue.deliver.taglib.AbstractTag;

/**
 * This class implements the <common:url> tag, which creates an url
 * from a base url (user supplied or taken from the request), 
 * a query string (user supplied ot taken from the reuest) and
 * any number of parameters specified using nested <common:parameter/> tags.
 */
public class URLTag extends AbstractTag {
	/**
	 * The universal version identifier.
	 */
	private static final long serialVersionUID = -3691910169063207982L;

	/**
	 * The base url to use when constructing the url.
	 */
	private String baseURL;
	
	/**
	 * The query to use when constructing the url.
	 */
	private String query;
	
	/**
	 * The parameters to use when constructing the url.
	 */
	private Map parameters; 
	
	/**
	 * Default constructor.
	 */
	public URLTag()
	{
		super();
	}

	/**
	 * Initializes the parameters to make it accessible for the children tags (if any).
	 * 
	 * @return indication of whether to evaluate the body or not.
	 * @throws JspException if an error occurred while processing this tag.
	 */
	public int doStartTag() throws JspException 
	{
		System.out.println("start");
		return EVAL_BODY_INCLUDE;
	}
	
	/**
	 * Generates the url and either sets the result attribute or writes the url
	 * to the output stream. 
	 * 
	 * @return indication of whether to continue evaluating the JSP page.
	 * @throws JspException if an error occurred while processing this tag.
	 */
	public int doEndTag() throws JspException
    {
		addQueryParameters();
		produceResult(generateURL());
		parameters = null;
		return EVAL_PAGE;
    }

	/**
	 * Returns the parameters to use when constructing the url.
	 * 
	 * @return the parameters to use when constructing the url.
	 */
	protected final Map getParameters()
	{
		if(parameters == null)
		{
			parameters = new HashMap();
		}
		return parameters;
	}
	
	/**
	 * Returns the url attribute if present; otherwise the url is taken from the request.
	 * 
	 * @return the url attribute if present; otherwise the url is taken from the request.
	 */
	private String getBaseURL()
	{
		return (baseURL == null) ? getRequest().getRequestURL().toString() : baseURL;
	}
	
	/**
	 * Returns the query attribute if present; otherwise the query is taken from the request.
	 * 
	 * @return the query attribute if present; otherwise the query is taken from the request.
	 */
	private String getQuery()
	{
		String q = (query == null) ? getRequest().getQueryString() : query;
		if(q != null && (q.startsWith("?") || q.startsWith("&")))
		{
			return q.substring(1);
		}
		return q;
	}
	
	/**
	 * Returns the (http) request object.
	 * 
	 * @return the (http) request object.
	 */
	private final HttpServletRequest getRequest()
	{
		return (HttpServletRequest) pageContext.getRequest();
	}
	
	/**
	 * Adds the parameter from the query string to the parameters to use
	 * when constructing the url. If a parameter present in the query already
	 * exists in the parameters, the query parameter will be skipped.
	 * 
	 * @throws JspException if the format of the query string is illegal.
	 */
	private void addQueryParameters() throws JspException
	{
		if(getQuery() != null)
		{
			for(final StringTokenizer st = new StringTokenizer(getQuery(), "&"); st.hasMoreTokens(); )
			{
				final String token = st.nextToken();
				final StringTokenizer parameter = new StringTokenizer(token, "=");
				if(parameter.countTokens() != 2)
				{
					throw new JspTagException("Illegal query parameter [" + token + "].");
				}
				final String name  = parameter.nextToken();
				final String value = parameter.nextToken();
				if(!getParameters().containsKey(name))
				{
					getParameters().put(name, value);
				}
			}
		}
	}
	
	/**
	 * Generates the url string.
	 * 
	 * @return the url.
	 */
	public String generateURL() 
	{
		if(!getParameters().isEmpty()) 
		{
			StringBuffer sb = new StringBuffer();
			for(Iterator i = getParameters().keySet().iterator(); i.hasNext(); ) 
			{
				String name      = (String) i.next();
				String value     = (String) getParameters().get(name);
				String parameter = name + "=" + value;
				sb.append(parameter + (i.hasNext() ? "&" : ""));
			}
			return getBaseURL() + "?" + sb.toString();
		}
		return getBaseURL();
	}

	/**
	 * Sets the base url attribute.
	 * 
	 * @param baseURL the base url to use.
	 * @throws JspException if an error occurs while evaluating base url parameter.
	 */
	public void setBaseURL(final String baseURL) throws JspException
	{
		this.baseURL = evaluateString("url", "baseURL", baseURL);
	}

	/**
	 * Sets the query attribute.
	 * 
	 * @param query the query to use.
	 * @throws JspException if an error occurs while evaluating query parameter.
	 */
	public void setQuery(final String query) throws JspException
	{
		this.query = evaluateString("url", "query", query);
	}
}
