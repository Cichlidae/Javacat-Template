/**
 *   @(#)  XSLCommand.java	   0.1	 08/02/01
 *
 *   Copyright (C) 2005-2008 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.templ.foundation;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.Templates;
import org.w3c.dom.Document;

/*****************************************************************************
 * A command of processing XSLT Transformation. The transformation is
 * DOM to DOM transformation. The method getAsXMLDOM () must be supported
 * by Data implementation.
 *
 * @author Amri (Anna A. Semyonova)
 * @version 0.1
 */

public class XSLCommand implements Command {

//class variables

private Templates templ;
private String id = null;

//class methods

/*****************************************************************************
 *  The constructor
 */

  public XSLCommand (String filename) throws TemplateException {

    //code description

    if (filename == null) this.templ = null;
    else try {
    	this.id = filename;
    	//загружаем XSL-шаблон, по которому будет осуществляться трансформация данных
    	StreamSource xsl = new StreamSource(Configuration.getTemplateLocator().getTemplateAsStream(filename)); 	
    	this.templ = TransformerFactory.newInstance().newTemplates(xsl);    	
    }
    catch (Exception err) {
    	TemplateLogger.getLogger().error("XSL Command: incorrect template format '" + this.id + "' (XSLT)", err); 	    	
    	throw new TemplateException(err.getMessage());
    }

  }

/*****************************************************************************
 *  The constructor
 */

  public XSLCommand () throws TemplateException {

    //code description

    this.templ = null;

  }

/*****************************************************************************
 *  Process the command
 */

  public Data process (Data data) throws TemplateException {

    //local variables

    Data databack = null;
    Transformer transformer = null;

    //code description

    try {
      TemplateLogger.getLogger().debug(data.getAsString());
      //производим XSLT-трансформацию входных XML-данных на основе указанного шаблона
      //если шаблон не указан, структура остается без изменений
      if (this.templ != null) transformer = this.templ.newTransformer();
      else transformer = TransformerFactory.newInstance().newTransformer();
      DOMResult result = new DOMResult();
      transformer.transform(new DOMSource(data.getAsXMLDOM()), result);    
      databack = new XMLData((Document)result.getNode());    
    }
    catch (Exception err) {
      TemplateLogger.getLogger().error("XSL Command: template transformation error '" + this.id + "' (XSLT)", err);       
      throw new TemplateException(err.getMessage());
    }    
    if (databack != null) {
    	if (databack.getAsXMLDOM().getDocumentElement() == null) {    	
    		TemplateLogger.getLogger().warn("XSL Command: result is empty (" + this.id + ")! Default result got.");
    		databack = DefaultManager.applyServices("default");
    	}
    }
    return databack;

  }

} // XSLCommand ends
