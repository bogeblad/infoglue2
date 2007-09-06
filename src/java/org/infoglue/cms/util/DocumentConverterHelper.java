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

package org.infoglue.cms.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;
import org.infoglue.deliver.applications.databeans.ConvertedDocumentBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;

public class DocumentConverterHelper 
{
	private final static Logger logger = Logger.getLogger(DocumentConverterHelper.class.getName());

	public ConvertedDocumentBean convert(File aDocFile, String aTitle, String aMenuTextLength, List aCssList) 
	{
		ConvertedDocumentBean convertedDocument = new ConvertedDocumentBean();
		int menuMaxLength						= 20;
		
		logger.info("START");
		
		try
		{			
			if (aTitle == null || aTitle.trim().equals(""))
			{
				aTitle = "Innehållsförteckning";
			}
			
			if (aMenuTextLength == null || aMenuTextLength.trim().equals(""))
			{
				aMenuTextLength = "20";
			}
			
			try
			{
				menuMaxLength = new Integer(aMenuTextLength).intValue();
			}
			catch(NumberFormatException nfe)
			{
				// Do nothing. Use the default value of 20 instead.
			}
			
			OpenOfficeConnection connection = new SocketOpenOfficeConnection(8100);			
			
			//------------------------
			// Setup the output files
			//------------------------
			
			String fileName 		= aDocFile.getName().substring(0, aDocFile.getName().indexOf("."));
			String digitalAssetPath = CmsPropertyHandler.getDigitalAssetPath() + File.separator + fileName;
			
			String newFilePath 		= digitalAssetPath + File.separator + fileName;
						
			File pdfFile 			= new File(newFilePath + ".pdf");
			File odtFile 			= new File(newFilePath + ".odt");
			File htmlFile 			= new File(newFilePath + ".html");
			File contentXmlFile 	= new File(digitalAssetPath + File.separator + "content.xml");;
			
			//----------------------------------------------
			// Check if the doc has already been converted.
			// If it has, we don't need another conversion.
			//----------------------------------------------
			
			File documentDir = new File(digitalAssetPath);

			if (!documentDir.exists())
			{
				logger.info("Connecting to server...");								
				
				connection.connect();
				
				logger.info("Connection ok");
				
				logger.info("Conversion START");
	
				convertDocument(aDocFile, pdfFile, connection);
				convertDocument(aDocFile, htmlFile, connection);
				convertDocument(aDocFile, odtFile, connection);
				connection.disconnect();
				
				logger.info("Conversion END");			
				
				//------------------------------------------------
				// Extract the content.xml file from the ODT file
				// so we can parse the XML and generate the TOC
				//------------------------------------------------
				
				logger.info("Extracting content.xml...");
				
				String targetPath = odtFile.getAbsolutePath();
				targetPath = targetPath.substring(0, targetPath.lastIndexOf("\\") + 1);
	
				contentXmlFile = extractContentXml(odtFile, targetPath);
				
				logger.info("Done extracting content.xml");
				
				//--------------------------------------------------------
				// Insert the anchors, remove the TOC, remove CSS styles
				// and add a link to the CMS-CSS to the the HTML handbook
				// (Since we've just generated a new nav above)
				//--------------------------------------------------------
				
				logger.info("Updating handbook with extra info");
				
				adaptHandbook(htmlFile, aCssList);
				
				logger.info("Done updating handbook with extra info");
			}
			
			//--------------------------
			// Generate HTML TOC string
			//--------------------------
			
			logger.info("Generating TOC...");
			
			String htmlFileUrl 	= CmsPropertyHandler.getDigitalAssetBaseUrl() + "/" + fileName + "/" + htmlFile.getName();
			String pdfFileUrl	= CmsPropertyHandler.getDigitalAssetBaseUrl() + "/" + fileName + "/" + pdfFile.getName();
			String odtFileUrl	= CmsPropertyHandler.getDigitalAssetBaseUrl() + "/" + fileName + "/" + odtFile.getName();
			String tocString 	= generateHtmlToc(contentXmlFile, htmlFileUrl, aTitle, menuMaxLength);
			
			logger.info("Done generating TOC");
			
			convertedDocument.setHtmlFileUrl(htmlFileUrl);
			convertedDocument.setPdfFileUrl(pdfFileUrl);
			convertedDocument.setOdtFileUrl(odtFileUrl);
			convertedDocument.setTocString(tocString);
		}
		catch(Exception e)
		{
			logger.error("An error occurred when converting document:" + e.getMessage(), e);
		}
		
		logger.info("END");
		
		return convertedDocument;
	}
	
	private static String generateHtmlToc(File aContentXmlFile, String aHtmlFileUrl, String aTitle, int aMenuMaxLength) throws Exception 
	{		
		Namespace officeNs 	= Namespace.getNamespace("office", "urn:oasis:names:tc:opendocument:xmlns:office:1.0");
		Namespace textNs 	= Namespace.getNamespace("text", "urn:oasis:names:tc:opendocument:xmlns:text:1.0");
		
		SAXBuilder builder 	= new SAXBuilder();
		Document doc 		= builder.build(aContentXmlFile);
		List tocElements	= null;
		
		//--------------------------------------------------------------
		// Find the element in the XML containing the Table Of Contents
		//--------------------------------------------------------------
		
		try
		{
			Element rootElement = doc.getRootElement();			
			Element bodyElement = rootElement.getChild("body", officeNs);
			Element textElement = bodyElement.getChild("text", officeNs);		
			Element tocElement 	= textElement.getChild("table-of-content", textNs);		 
			Element textIndexBodyElement = tocElement.getChild("index-body", textNs);		
			tocElements	= textIndexBodyElement.getChildren();					
		}
		catch(Exception e)
		{
			throw new Exception("The document structure is incorrect. Please create a document containing a table of contents.");
		}
		
		//------------------------------------
		// Generate the HTML for the TOC menu
		//------------------------------------
		
		if (tocElements != null)
		{
			StringBuffer htmlMenuSb = new StringBuffer();			
			generateHtml(tocElements.toArray(), htmlMenuSb, aHtmlFileUrl, aTitle, aMenuMaxLength);		
			return htmlMenuSb.toString();
		}
		else
		{
			throw new Exception ("The list of TOC elements was null.");
		}
	}

	private static StringBuffer generateHtml(Object[] aTocElements, StringBuffer aReturnSb, String aHtmlFileUrl, String aTitle, int aMenuMaxLength) throws Exception 
	{				
		Element child 					= null;
		String elementText				= "";		
		String headingNumber			= "";
		int startPosition 				= 0;
		String previousHeadingNumber 	= ""; 
		int level						= 0;
		int previousLevel				= 0;
		int numberOfEndUls 				= 0;
		StringTokenizer st				= null;
		int linkCounter					= 0;		
		String croppedElementText		= "";
		int cropCounter					= 0;
		boolean willCrop				= false;
		
		aReturnSb.append("<div id=\"submenu\" class=\"noprint\">");
		aReturnSb.append("<div class=\"menuheader\">" + aTitle + "</div>");
		aReturnSb.append("<ul>");
		
		while (startPosition < aTocElements.length)
		{				
			child 			= (Element)aTocElements[startPosition];
			elementText 	= child.getText();	
			
			if (elementText != null && !elementText.equals("") && elementText.indexOf(" ") != -1)
			{
				headingNumber 	= elementText.substring(0, elementText.indexOf(" "));
				st = new StringTokenizer(headingNumber, ".");
				level = st.countTokens();
				
				if (headingNumber.length() > previousHeadingNumber.length() && !previousHeadingNumber.equals(""))
				{									
					aReturnSb.append("<ul>");					
					level ++;
				}
				else if(headingNumber.length() < previousHeadingNumber.length() && !previousHeadingNumber.equals(""))
				{		
					numberOfEndUls = previousLevel - level;

					for (int i = 0; i < numberOfEndUls; i ++)
					{
						aReturnSb.append("</ul>");					
					}
				}
				
				//-----------------------------------------------------------------
				// If there is a page number at the end of the line, get rid of it
				//-----------------------------------------------------------------
				
				cropCounter = elementText.length();
				char myChar = elementText.charAt(cropCounter - 1);
				
				while (cropCounter > 0 && Character.isDigit(myChar))
				{		
					cropCounter --;
					myChar = elementText.charAt(cropCounter);
					willCrop = true;
				}
				
				if (willCrop)
				{
					cropCounter ++;
				}
				
				elementText = elementText.substring(0, cropCounter);
				
				//------------------------------------------------------------------------
				// Crop the text and add "..." if it's longer than the allowed max length
				//------------------------------------------------------------------------
				
				if (elementText.length() > aMenuMaxLength)
				{
					croppedElementText = elementText.substring(0, aMenuMaxLength) + "...";
				}
				else
				{
					croppedElementText = elementText;
				}
								
				aReturnSb.append("<li><p><a target=\"handbookFrame\" href=\"" + aHtmlFileUrl + "#link" + linkCounter + "\" title=\"" + elementText + "\">" + croppedElementText + "</a></p></li>");
				previousHeadingNumber = headingNumber;
				previousLevel = level;
				linkCounter ++;
			}			
			startPosition = startPosition + 1;
		}
		
		aReturnSb.append("</ul>");
		aReturnSb.append("</div>");

		return aReturnSb;		
	}
	
	public static void adaptHandbook(File aHandbookHtmlFile, List aCssList) throws Exception
	{
		String originalHtmlContent 		= readFileIntoString(aHandbookHtmlFile);
		String htmlContentWithoutToc	= removeTocFromHtml(originalHtmlContent);
		String htmlContentWithAnchors 	= insertAnchors(htmlContentWithoutToc);
		String htmlWithProperCss		= fixCss(htmlContentWithAnchors, aCssList);
		writeStringToFile(aHandbookHtmlFile, htmlWithProperCss);
	}
	
	private static String fixCss(String aHtmlContent, List aCssList)
	{		
		StringBuffer sb = new StringBuffer();
		
		int cssStartIndex = aHtmlContent.indexOf("<STYLE TYPE=\"text/css\">");
		
		String start = aHtmlContent.substring(0, cssStartIndex);
		String temp = aHtmlContent.substring(cssStartIndex + 1);
		String end = temp.substring(temp.indexOf("</STYLE>") + 8);
		
		StringBuffer linkedCssString = new StringBuffer();
		Iterator it = aCssList.iterator();
		String cssString = "";
		
		while (it.hasNext())
		{
			cssString = (String)it.next();
			cssString = "<link href=\"" + cssString + "\" rel=\"stylesheet\" type=\"text/css\" />" + '\n';
			linkedCssString.append(cssString);
		}
		
		//"<link href=\"/infoglueDeliverWorking/ViewPage.action?siteNodeId=38&amp;languageId=3&amp;contentId=-1\" rel=\"stylesheet\" type=\"text/css\" />" + 
		//	"<link href=\"/infoglueDeliverWorking/ViewPage.action?siteNodeId=101&amp;languageId=3&amp;contentId=-1\" rel=\"stylesheet\" type=\"text/css\" />";
		
		sb.append(start);
		sb.append(linkedCssString.toString());
		sb.append(end);

		return sb.toString();
	}
	
	private static String removeTocFromHtml(String aHtmlContent)
	{
		StringBuffer sb = new StringBuffer();
		
		int tocStartIndex = aHtmlContent.indexOf("<DIV ID=\"Table of Contents1\" DIR=\"LTR\">");
		
		String start = aHtmlContent.substring(0, tocStartIndex);
		String temp = aHtmlContent.substring(tocStartIndex + 1);
		String end = temp.substring(temp.indexOf("</DIV>") + 6);
		
		sb.append(start);
		sb.append(end);
		
		return sb.toString();
	}

	private static String insertAnchors(String aOriginalHtmlContent) 
	{		
		StringBuffer modifiedHtmlContent 	= new StringBuffer();
		StringTokenizer st 					= new StringTokenizer(aOriginalHtmlContent, System.getProperty("line.separator"));
		String lineString					= "";
		int linkCounter						= 0;
		
		while (st.hasMoreTokens())
		{
			lineString = st.nextToken();
			
			if (lineString.indexOf("<H1") > -1)
			{			
				lineString = lineString.replaceAll("<H1", "<A NAME=\"link" + linkCounter + "\"></A><H1");				
				linkCounter ++;	
			}
			else if (lineString.indexOf("<H2") > -1)
			{
				lineString = lineString.replaceAll("<H2", "<A NAME=\"link" + linkCounter + "\"></A><H2");				
				linkCounter ++;	
			}
			else if (lineString.indexOf("<H3") > -1)
			{
				lineString = lineString.replaceAll("<H3", "<A NAME=\"link" + linkCounter + "\"></A><H3");				
				linkCounter ++;	
			}
			else if (lineString.indexOf("<H4") > -1)
			{
				lineString = lineString.replaceAll("<H4", "<A NAME=\"link" + linkCounter + "\"></A><H4");				
				linkCounter ++;	
			}
			
			modifiedHtmlContent.append(lineString + System.getProperty("line.separator"));		
		}		
		return modifiedHtmlContent.toString();
	}

	public static String readFileIntoString(File aFile) throws Exception
	{
		StringBuffer contents 	= new StringBuffer();
	    BufferedReader input 	= null;
	    try 
	    {	     
	    	input 				= new BufferedReader( new FileReader(aFile) );
	    	String line 		= null;
	      
	    	while (( line = input.readLine()) != null)
	    	{
	    		contents.append(line);
	    		contents.append(System.getProperty("line.separator"));
	    	}
	    }
	    catch (Exception e) 
	    {
	    	throw (e);
	    }	  
	    finally 
	    {
	    	if (input!= null) 
    		{	         
    			input.close();
    		}		   
	    }
	    return contents.toString();
	}
	
	public static void writeStringToFile(File aFile, String aContents) throws FileNotFoundException, IOException
	{		
		if (aFile == null) 
		{
			throw new IllegalArgumentException("File should not be null.");
		}
		if (!aFile.exists()) 
		{
			throw new FileNotFoundException ("File does not exist: " + aFile);
		}
		if (!aFile.isFile()) 
		{
			throw new IllegalArgumentException("Should not be a directory: " + aFile);
		}
		if (!aFile.canWrite()) 
		{
			throw new IllegalArgumentException("File cannot be written: " + aFile);
		}
		
		Writer output = null;
		
		try 
		{		
			output = new BufferedWriter( new FileWriter(aFile) );
			output.write( aContents );
		}
		finally 
		{		
			if (output != null)
			{
				output.close();
			}
		}		
	}
	
	public static void listChildren(Element aParentElement)
	{
		if (aParentElement == null)
		{
			logger.info("The supplied Element was null");
		}
		else
		{
			logger.info("Children to " + aParentElement.getNamespacePrefix() + ":" + aParentElement.getName() + ": ");
			logger.info("---------------------------------");
			List children 		= aParentElement.getChildren();		
			Iterator it 		= children.iterator();		
			while(it.hasNext())
			{
				Element child = (Element)it.next();
				logger.info(child.getNamespacePrefix() + ":" + child.getName() + "=" + child.getText());
			}
			logger.info("---------------------------------");
		}
	}

	public static void convertDocument(File aInputFile, File aOutputFile, OpenOfficeConnection aConnection)
	{							
		String fileType = "";
		fileType = aOutputFile.getAbsolutePath().substring(aOutputFile.getAbsolutePath().lastIndexOf(".") + 1);
		
		//--------------------------------------------
		// Connect to server and create the converter
		//--------------------------------------------
		
		DocumentConverter converter = new OpenOfficeDocumentConverter(aConnection); 
				
		//---------------------------------------------
		// Convert the document to the selected format
		//---------------------------------------------
		
		logger.info("Converting document to " + fileType + "...");
		
		converter.convert(aInputFile, aOutputFile);
		
		logger.info("Conversion complete");
	}
	
	public static File extractContentXml(File aOdtFile, String aTargetDirectory) throws ZipException, IOException
	{
		File contentXmlFile = null;
		ZipFile  zipFile 	= new ZipFile(aOdtFile);		
		Enumeration entries = zipFile.entries();

		while(entries.hasMoreElements()) 
		{
			ZipEntry entry = (ZipEntry)entries.nextElement();
		
			if (entry.getName().equals("content.xml"))
			{	
				contentXmlFile = new File(aTargetDirectory + entry.getName());					
				copyInputStream(zipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(contentXmlFile)));
				break;
			}			
		}	
		zipFile.close();
		
		return(contentXmlFile);
	}
	
	public static final void copyInputStream(InputStream in, OutputStream out) throws IOException
	{
		byte[] buffer = new byte[1024];
	    int len;

	    while((len = in.read(buffer)) >= 0)
	    {
	    	out.write(buffer, 0, len);
	    }
	    
	    in.close();
	    out.close();
	}
}
