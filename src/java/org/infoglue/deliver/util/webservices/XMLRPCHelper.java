package org.infoglue.deliver.util.webservices;

import java.util.Vector;

import org.apache.xmlrpc.XmlRpc;
import org.apache.xmlrpc.XmlRpcClient;
import org.infoglue.cms.util.CmsLogger;

/**
 * @author Mattias Bogeblad
 *
 * This class helps using the popular XML-RPC way of accessing data.
 */

public class XMLRPCHelper
{
	private String serviceUrl = "";
	private String method = "";
	private Vector parameters = new Vector();
	private Object result = null;

	private String errorCode = "0";
	private String errorMessage = "Ok";

	/**
	 * The constructor for this class.
	 */

	public XMLRPCHelper()
	{
	}

	public void makeCall()
	{
		try
		{
			XmlRpcClient xmlrpc = new XmlRpcClient(serviceUrl);
			XmlRpc.setEncoding("ISO-8859-1");
			//Vector params = new Vector ();
			//params.addElement("7");
			//params.addElement("peew9yoop");
			//params.addElement("1");

			// this method returns a string
			this.result = xmlrpc.execute(this.method, this.parameters);

			CmsLogger.logInfo("result:" + result);
		}
		catch(Exception e)
		{
			this.errorCode = "1";
			this.errorMessage = "An error occurred:" + e.getMessage();
			CmsLogger.logWarning("An error occurred:" + e.getMessage(), e);
		}
	}

	public void setParameters(Vector parameters)
	{
		this.parameters = parameters;
	}

	public Vector getParameters()
	{
		return this.parameters;
	}

	public String getServiceUrl()
	{
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl)
	{
		this.serviceUrl = serviceUrl;
	}

	public Object getResult()
	{
		return this.result;
	}

	public String getErrorCode()
	{
		return this.errorCode;
	}

	public String getErrorMessage()
	{
		return this.errorMessage;
	}

	public void setErrorCode(String errorCode)
	{
		this.errorCode = errorCode;
	}

	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	public String getMethod()
	{
		return this.method;
	}

	public void setMethod(String method)
	{
		this.method = method;
	}

}