/**
 *   @(#)  DefaultTemplate.java	   0.1	 08/02/01
 *    
 *   Copyright (C) 2005-2008 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.templ.foundation;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

public class XMLCommand implements Command {

//class variables	
	
  private Document document;  
  
//class methods  

  public XMLCommand (String filename) throws TemplateException {

    try {
      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      this.document = builder.parse(Configuration.getTemplateLocator().getTemplateAsStream(filename));
    }
    catch (Exception err) {
      TemplateLogger.getLogger().error("XML Command: file reading error '" + filename + "' (DOM)", err); 	    	
      throw new TemplateException(err.getMessage());
    }

  }
  
  protected XMLCommand () {
	  
  }

  public Data process (Data data) {

	//просто возвращаем загруженную XML-структуру в неизменном виде  
    return new XMLData(this.document);

  }

}

