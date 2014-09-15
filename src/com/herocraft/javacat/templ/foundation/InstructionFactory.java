/**
 *   @(#)  InstructionFactory.java	   0.1	 08/02/12
 *
 *   Copyright (C) 2005-2008 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.templ.foundation;

import java.io.InputStream;
import java.util.Properties;
import javax.xml.XMLConstants;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;
import org.xml.sax.InputSource;
import org.xml.sax.ErrorHandler;

public final class InstructionFactory {

//class variables
	
private static Properties classnames = null;	
	
//class methods

/******************************************************************
 *  The constructor
 */

  private InstructionFactory () {
	  
  }
  
/******************************************************************
 *  Inits the factory
 */  

  public static void init () throws Exception {
	  
	  //code description
	  
	  if (InstructionFactory.isInitialized()) InstructionFactory.release();
	  InstructionFactory.classnames = new Properties();
	  InstructionLocator locator = Configuration.getInstructionLocator();
	  InputStream xml = null;
	  if (locator != null) xml = locator.getInstructionAsStream("instructions.xml");	  
	  if (xml == null) xml = InstructionFactory.class.getResourceAsStream("conf/instructions.xml");	 
	  if (xml != null) {		
		  InputStream schema = InstructionFactory.class.getResourceAsStream("conf/instructions.xsd");
		  SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);	    				    			    	    			    
		  Validator validator = factory.newSchema(new StreamSource(schema)).newValidator();
		  validator.setErrorHandler(new ErrorHandler () {
			  	public void warning(SAXParseException exception) {
			  		TemplateLogger.getLogger().error("JC Templates Instruction Factory - WARNING: " + exception.toString());		        
		        }

		        public void error(SAXParseException exception) {
		        	TemplateLogger.getLogger().error("JC Templates Instruction Factory - ERROR: " + exception.toString());			         
		        }

		        public void fatalError(SAXParseException exception) {
		        	TemplateLogger.getLogger().error("JC Templates Instruction Factory - FATAL ERROR: " + exception.toString());			        
		        }		        
		  });			  		 		
		  DefaultHandler handler = new DefaultHandler () {
				  String path = "";
				  public void startElement (String uri, String localName, String qName, Attributes attrs) {
					
					  //code description
					  
					  if ("instruction".equalsIgnoreCase(qName)) {
						  if (path.endsWith(".instructions")) {
							  InstructionFactory.classnames.setProperty(attrs.getValue("tagname"), attrs.getValue("classname"));
							  TemplateLogger.getLogger().info("Additional instruction found: <" + attrs.getValue("tagname") + ">");	
						  }						  
					  }
					  path += "." + qName;						
					  
				  }
				  public void endElement (String uri, String localName, String qName) {
					  
					  //code description
					  
					  int index = path.lastIndexOf(".");
						if (index > -1) {
							path = path.substring(0, index);
					  }
					  
				  }				  				  
		};
		SAXResult result = new SAXResult(handler);
		try {
			validator.validate(new SAXSource(new InputSource(xml)), result);
		}
		catch (Exception err) {
			TemplateLogger.getLogger().fatal("JC Templates Instruction Factory - fatal error during initialization.", err);
			InstructionFactory.classnames = null;
		    throw err;
		}
	  }				 	
	  TemplateLogger.getLogger().info("JC Templates Instruction Factory initialized sucessfully.");
	  	  	  
  }
  
/******************************************************************
 *  Gets an instruction
 */
  
  public static Instruction getInstruction (String name) throws TemplateException {
	  
	  //local variables
	  
	  Instruction instruction = new DefaultInstruction();
	  
	  //code description
	  
	  if (!InstructionFactory.isInitialized()) {
		  TemplateException e = new TemplateException("JC Templates Instruction Factory not initialized.");
		  TemplateLogger.getLogger().fatal("JC Templates Instruction Factory not initialized.", e);
		  throw e;		  
	  }
	  if ("jct:url".equals(name)) instruction = new UrlInstruction();		  	  
	  else if ("jct:import".equals(name)) instruction = new ImportInstruction();
      else if ("jct:invoke".equals(name)) instruction = new InvokeInstruction();
      else if ("jct:conf".equals(name)) instruction = new ConfigInstruction();
      else if ("jct:file".equals(name)) instruction = new FileInstruction();
      else if ("jct:url".equals(name)) instruction = new UrlInstruction();
      else if (InstructionFactory.classnames.containsKey(name)) {
    	String classname = InstructionFactory.classnames.getProperty(name);
    	try {
    		//jct:sql, jct:sql-transaction, etc
    		instruction = (Instruction)Class.forName(classname).newInstance();
    	}
    	catch (Exception errx) {
    		TemplateLogger.getLogger().error("Instruction Factory - cannot load instruction class'" + classname + "'.", errx);
    		throw new TemplateException(errx.getMessage());
    	};    	  	 
      }	  	  
	  return instruction;
	  
  }
  
/******************************************************************
 *  Destroies the factory
 */  
  
  public static void release () {
	  
	  //code description
	  
	  if (InstructionFactory.classnames != null) {
		  InstructionFactory.classnames.clear();
		  InstructionFactory.classnames = null;
	  }
	  TemplateLogger.getLogger().info("JC Templates Instruction Factory released sucessfully.");
	  
  }
  
/******************************************************************
 *  Checks if the factory already initialized
 */  
  
  public static boolean isInitialized () {
	  
	  //code description
	  
	  if (InstructionFactory.classnames != null) return true;
	  else return false;
	  
  }
  
} // InstructionFactory ends
