/* 
 * Created on 2005-apr-27
 *
 */
package org.infoglue.cms.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.XStream;

/**
 * @author Stefan Sik
 * 
 */
public class XMLNotificationWriter implements NotificationListener
{
    private String boundary = "";
    private Writer out = null;
    private Thread workingThread = null;
    private XMLWriter xmlWriter = null;
    private XStream xStream = null;

    public XMLNotificationWriter(Writer out, String encoding, String boundary, Thread workingThread, boolean compact, boolean supressDecl)
    {
        this.out = out;
        this.boundary = boundary;
        this.workingThread = workingThread;
        
	    OutputFormat format = compact ? OutputFormat.createCompactFormat() : OutputFormat.createPrettyPrint();
        format.setSuppressDeclaration(supressDecl);
		format.setEncoding(encoding);
		format.setExpandEmptyElements(!compact);
		xmlWriter = new XMLWriter(out, format);
		xStream = new XStream();
    }
    
    public void setContextParameters(Map map)
    {
    }

    /*
     * @see org.infoglue.cms.util.NotificationListener#notify(org.infoglue.cms.util.NotificationMessage)
     */
    public void notify(NotificationMessage message)
    {
        try
        {
            xmlWriter.write(DocumentHelper.parseText(xStream.toXML(message)));
            xmlWriter.flush();
            out.write(("\r\n" + boundary + "\r\n"));
            out.flush();

        }
        catch (DocumentException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            if(workingThread != null) workingThread.interrupt();
        }
    }
}
