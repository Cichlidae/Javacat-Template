/**
 *   @(#)  Configuration.java	   0.1	 08/02/21
 *
 *   Copyright (C) 2007-2008 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.templ.foundation;

import java.util.Properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/* Holds the configuration data */

final public class Configuration {
	
/* the configuration map of 'id-value' pairs */
private static Properties confmap = new Properties();

/* template, instruction & data locators by default */
private static TemplateLocator templateLocator;
private static InstructionLocator instructionLocator;
private static DataLocator dataLocator;

/**************************************************************************************************************
 *  The constructor
 */

  private Configuration () {
	  
  }

/**************************************************************************************************************
 *  Inits configuration
 */

  public static void init (File confdir, TemplateLocator templateLocator, 
		                   InstructionLocator instructionLocator, DataLocator dataLocator) throws Exception {
	  
	  //code description
	  
	  //инициализируем классы, отвечающие за загрузку локальных данных
	  if (templateLocator == null) throw new NullPointerException();
	  Configuration.templateLocator = templateLocator;
	  Configuration.instructionLocator = instructionLocator;
	  Configuration.dataLocator = dataLocator;	
	  if (confdir != null) {
		  //считываем глобальные конфигурационные параметры, к которым можно обращаться из шаблонов (jct:conf)
		  File conf = new File(confdir, "config.xml");
		  if (!conf.exists()) { 
			  TemplateLogger.getLogger().error("JC Templates Configuration - config file not found in the specified directory: " + conf.getPath());
			  throw new FileNotFoundException(conf.getPath());		 
		  }	
		  TemplateLogger.getLogger().debug("JC Templates Configuration file:" + conf.getPath());
		  InputStream schema = Configuration.class.getResourceAsStream("conf/config.xsd");
		  SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);	    				    			    	    			    
		  Validator validator = factory.newSchema(new StreamSource(schema)).newValidator();	  
		  validator.setErrorHandler(new ErrorHandler () {
			  public void warning(SAXParseException exception) {
				  TemplateLogger.getLogger().error("JC Templates Configuration - WARNING: " + exception.toString());		        
		      }

		      public void error(SAXParseException exception) {
		    	  TemplateLogger.getLogger().error("JC Templates Configuration - ERROR: " + exception.toString());			         
		      }

		      public void fatalError(SAXParseException exception) {
		    	  TemplateLogger.getLogger().error("JC Templates Configuration - FATAL ERROR: " + exception.toString());			        
		      }		        
		  });	
		  DefaultHandler handler = new DefaultHandler () {
			  String path = "";			
			  public void startElement (String uri, String localName, String qName, Attributes attrs) {
								
				  //code description
				  	
				  if ("config".equalsIgnoreCase(qName)) {	
					  TemplateLogger.getLogger().debug("JC Templates Configuration properties:");
				  }				 
				  else if ("param".equalsIgnoreCase(qName)) {					  		
					  if (path.endsWith(".config")) {		
						  TemplateLogger.getLogger().debug("* property: " + attrs.getValue("id") + "=" + attrs.getValue("value"));		
						  Configuration.setConfigData(attrs.getValue("id"), attrs.getValue("value"));									  						
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
			 validator.validate(new SAXSource(new InputSource(new FileInputStream(conf))), result);
		  }
		  catch (Exception err) {
			  TemplateLogger.getLogger().fatal("JC Templates Configuration - fatal error during initialization config.xml", err);			
			  throw err;
		  }	  			  		  
	  }	  	  	 
	  TemplateLogger.getLogger().info("JC Templates Configuration initialized sucessfully.");
	  
  }
  
  public static void init (TemplateLocator templateLocator) throws Exception {
	  
	  //code description
	  
	  Configuration.init(null, templateLocator, null, null);
	  
  }
  
/**************************************************************************************************************
 *  Sets a 'id-value' pair
 */
	
  static void setConfigData (String id, String value) {
	  
	  //code description
	  
	  confmap.setProperty(id, value);
	  
  }
  
/**************************************************************************************************************
 *  Gets a 'id-value' pair
 */  
	
  public static String getStringConfigData (String id) {
	  
	  //code description
	  
	  String value = confmap.getProperty(id);
	  return value == null ? "" : value;
	  
  }
 
/**************************************************************************************************************
 *  Gets a 'id-value' pair
 */  
  	
  public static boolean getBooleanConfigData (String id) {
  	  
	  //code description
  	  
	  return "true".equals(Configuration.getStringConfigData(id)) ? true : false;  	
  	  
  }  
 
/**************************************************************************************************************
 *  Gets a 'id-value' pair
 */  
    	
  public static int getIntConfigData (String id) {
    	  
	  //code description
    	  
	  try {
		  return Integer.parseInt(Configuration.getStringConfigData(id));
	  }
	  catch (Exception err) {
		  return Integer.MIN_VALUE;
	  }
	        	
  }   
  
/**************************************************************************************************************
 *  Gets a 'id-value' pair
 */  
      	
  public static long getLongConfigData (String id) {
      	  
	  //code description
      	  
  	  try {
  		  return Long.parseLong(Configuration.getStringConfigData(id));
  	  }
  	  catch (Exception err) {
  		  return Long.MIN_VALUE;
  	  }
  	        	
  }     
  
/**************************************************************************************************************
 *  Gets a 'id-value' pair
 */  
      	
  public static double getDoubleConfigData (String id) {
      	  
	  //code description
      	  
  	  try {
  		  return Double.parseDouble(Configuration.getStringConfigData(id));
  	  }
  	  catch (Exception err) {
  		  return Double.MIN_VALUE;
  	  }
  	        	
  }  
  
/* Gets locators */
  
  public static TemplateLocator getTemplateLocator () {
	  
	  //code description
	  
	  return Configuration.templateLocator;
	  
  }
  
  public static InstructionLocator getInstructionLocator () {
	  
	  //code description
	  
	  return Configuration.instructionLocator;
	  
  }
  
  public static DataLocator getDataLocator () {
	  
	  //code description
	  
	  return Configuration.dataLocator;
	  
  }
  
  public static TemplateLoader getTemplateLoader () {
	  
	  //code description
	  
	  return new DefaultLoader();
	  
  }  

}
