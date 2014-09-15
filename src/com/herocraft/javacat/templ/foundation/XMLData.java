/**
 *   @(#)  XMLData.java	   0.1	 08/02/21
 *
 *   Copyright (C) 2005-2008 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.templ.foundation;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/*****************************************************************************
 * Represents a XMLDOM data for commands & templates.
 *
 * @author Amri (Anna A. Semyonova)
 * @version 0.1
 */

public class XMLData implements Data {

//class variables

private Document document;

//class methods

/*****************************************************************************
 *  The constructor
 */

  public XMLData () {

    //code description

    try {
      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      this.document = builder.newDocument();      
    }
    catch (ParserConfigurationException err) {}

  }
 
/*****************************************************************************
 *  The constructor
 */  
  
  public XMLData (InputStream stream, InputStream schema) {
	  	
	  //local variables
	  
	  String type = XMLConstants.W3C_XML_SCHEMA_NS_URI;
	  
	  //code description
	  	
	  try {
	      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();		      	      	        	     
	      Document doc = builder.parse(stream);
	      if (schema != null) {
	    	  //провер€ем XML на валидность через указанную W3C .xsd XML-схему	    	  	    	
	    	  SchemaFactory factory = SchemaFactory.newInstance(type);	    				    			    	    			    
	    	  Validator validator = factory.newSchema(new StreamSource(schema)).newValidator();
	    	  validator.setErrorHandler(new ErrorHandler () {
				  	public void warning(SAXParseException exception) {
				  		TemplateLogger.getLogger().error("XML Data schema checking - WARNING: " + exception.toString());		        
			        }

			        public void error(SAXParseException exception) {
			        	TemplateLogger.getLogger().error("XML Data schema checking - ERROR: " + exception.toString());			         
			        }

			        public void fatalError(SAXParseException exception) {
			        	TemplateLogger.getLogger().error("XML Data schema checking - FATAL ERROR: " + exception.toString());			        
			        }		        
			  });
	    	  DOMResult result = new DOMResult();	    	  	    	  
	    	  validator.validate(new DOMSource(doc), result);  	 
	    	  this.document = (Document)result.getNode();
	      }	
	      else this.document = doc;
	  }
	  catch (Exception err) {
		  TemplateLogger.getLogger().error("XML Data - cannot create an XMLData instance from input stream.", err);
	  }
	  
  }
  
/*****************************************************************************
 *  The constructor
 */

  public XMLData (Document doc) {

    //code description

    if (doc == null) throw new NullPointerException();
    this.document = doc;

  }

/*****************************************************************************
 *  The constructor
 */

  public XMLData (String rootName) {

    //code description

    this();
    if (rootName == null) throw new NullPointerException();
    Element root = this.document.createElement(rootName);
    this.document.appendChild(root);

  }

/*****************************************************************************
 *  The constructor
 */

  public XMLData (Element rootElement) {

    //code description

    this();
    if (rootElement == null) throw new NullPointerException();
    Node root = this.document.importNode(rootElement, true);    
    this.document.appendChild(root);

  }

/*****************************************************************************
 *  Gets as XMLDOM object
 */

  public Document getAsXMLDOM () {

    //code description

    return document;

  }

/*****************************************************************************
 *  Gets as input stream object
 */

  public InputStream getAsInputStream () {

	 //code description
	  
	 if (this.document == null) return null;
	 return new ByteArrayInputStream(this.getAsBytes());
	    
  }

/*****************************************************************************
 *  Gets as string
 */

  public String getAsString () {

    //code description

	if (this.document == null) return null;  
    return new String(this.getAsBytes());

  }
  
/*****************************************************************************
 *  Gets as byte array
 */

  public byte[] getAsBytes (Properties p) {

    //code description
	  
	if (this.document == null) return null;
	try {
		ByteArrayOutputStream out = new ByteArrayOutputStream();	 	 
	    Transformer t = TransformerFactory.newInstance().newTransformer();
	    t.setOutputProperties(p);
	    t.transform(new DOMSource(this.document), new StreamResult(out));
	    return out.toByteArray();
	}
	catch (Exception err) {
		TemplateLogger.getLogger().error("XML Data - cannot write XMLData instance to byte array.", err);
	}
    return null;

  }
  
/*****************************************************************************
 *  Gets as byte array
 */
  
  public byte[] getAsBytes () {
	  
	  //code description
	  
	  Properties p = new Properties();	
	  return this.getAsBytes(p);
	  
  }

/*****************************************************************************
 *  Gets as object
 */

  public Object getAsObject () {

    //code description

    return document;

  }

} // XMLData ends
