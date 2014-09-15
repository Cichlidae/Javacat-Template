/**
 *   @(#)  DefaultManager.java	   0.1	 08/02/01
 *
 *   Copyright (C) 2005-2008 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.templ.foundation;

import java.util.HashMap;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/*****************************************************************************
 * It's responsible for general line of template applying. It has only one
 * static instance which is able by the method 'getTemplateManager()'. Its main
 * functions is to load template configurations from 'querydesc.xml' and
 * 'updatesdesc.xml' files and to delegate client queries to a template specified
 * by 'settings.xml' or, in the special case, to a template which defined in
 * a configuration file directly.
 *
 * @author Amri (Anna A. Semyonova)
 * @version 0.1
 */

public final class DefaultManager {

//class variables

private static final HashMap<String, DefaultTemplate> TEMPLATES = new HashMap<String, DefaultTemplate>();
static final HashMap<String, DefaultTemplate> SERVICES = new HashMap<String, DefaultTemplate>();

//class methods

/*****************************************************************************
 *  The constructor
 */

  private DefaultManager () throws TemplateException, Exception {

    //code description

    try {  
      //инициализируем фабрику инструкций <jct:*>	
      if (!InstructionFactory.isInitialized()) InstructionFactory.init();
      //загружаем описания пользовательских (template) и служебных (service) шаблонов из файла templates.xml
      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document doc = builder.parse(Configuration.getTemplateLocator().getTemplateAsStream("templates.xml"));
      NodeList templates = doc.getElementsByTagName("templates").item(0).getChildNodes();      
      for (int i = 0; i < templates.getLength(); i++) {
        if (templates.item(i).getNodeType() != Node.ELEMENT_NODE) continue;
        Element template = (Element)templates.item(i);
        HashMap<String, DefaultTemplate> storage = null;
        if ("template".equals(template.getTagName())) {          
          storage = TEMPLATES;
        }
        else if ("service".equals(template.getTagName())) {
          storage = SERVICES;
        }
        if (storage != null) {   
          //создаем объект указанного шаблона и сохраняем его в хэш
          String name = template.getAttribute("type");
          String filename = template.getAttribute("file");
          DefaultTemplate tImpl = (DefaultTemplate)Configuration.getTemplateLoader().loadTemplate(name, filename);          
          storage.put(name, tImpl);
        }
      }  
    }
    catch (TemplateException err) {
    	TemplateLogger.getLogger().fatal("Default Manager - initializing error.");	
        throw err;
    }
    catch (Exception err) {
    	TemplateLogger.getLogger().fatal("Default Manager - initializing error.", err);	
    	throw err;
    }

  }
  
 /*****************************************************************************
  *  Gets the template manager
  *
  *  @return the only instance of the template manager to work with
  */

    public static void init () throws Exception {
     
      //code description
        
      new DefaultManager();    
      TemplateLogger.getLogger().info("JC Templates Manager (Default Manager) initialized sucessfully.");

    }  
    
/*****************************************************************************
 *  Destroies the manager
 */    
    
  public static void release () {
	  
	  //code description
	  
	  if (InstructionFactory.isInitialized()) InstructionFactory.release();   
	  TemplateLogger.getLogger().info("JC Templates Manager (Default Manager) released sucessfully.");
	  
  }
    
/*****************************************************************************
 *  Applies templates to input XML queries
 *
 *  @param queries
 *
 *  @return an array of answers accordingly input queries
 *
 *  @exception TemplateException
 */

  public static Data applyTemplates (Data data) throws TemplateException {

    //local variables

    Template template = null;

    //code description
 
    Element query = data.getAsXMLDOM().getDocumentElement();
    String name = query.getAttribute("type");
    template = (Template)TEMPLATES.get(name);
    if (template == null) {
    	TemplateLogger.getLogger().warn("Default Manager - template '" + name + "' not registered.");  
        template = DefaultManager.getService("unknown");
        if (template == null) throw new TemplateException("Default Manager - template '" + name + "' not found.");
    }
    return template.apply(name, data);  

  }
  
  public static Data applyServices (String name) throws TemplateException {
	  
	  //code description
	  
	  Template t = DefaultManager.getService(name);
	  return t.apply(name, null); 
	  
  }
  
  public static Template getService (String name) {
	  
	  //code description
	  
	  return (Template)SERVICES.get(name);
	  
  }

} // DefaultManager ends
