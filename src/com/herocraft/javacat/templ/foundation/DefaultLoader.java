/**
 *   @(#) DefaultLoader.java	   0.1	 08/02/01
 *
 *   Copyright (C) 2005-2008 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.templ.foundation;

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

class DefaultLoader implements TemplateLoader {

//class methods

/*****************************************************************************
 *  The constructor
 */

  DefaultLoader () {

  }

/*****************************************************************************
 *  Create Template instance
 */

  public Template loadTemplate (String name, String filename) throws TemplateException {

 	//local variables
	  
	  DefaultTemplate template = null;  
	  Command command = null;
	  
    //code description

	try {  
		//создаем экземпляр шаблона:
		//если имя файла шаблона не задано отдельно, то пробуем создать его на основе его собственного имени
		if (filename != null) command = this.createCommand(filename);
		else command = this.createCommand(name);					
		template = new DefaultTemplate(command);	
	}
	catch (TemplateException err) {
		TemplateLogger.getLogger().error("Default Loader - cannot load template '" + name + "'.", err);
		throw err;
	}
    return template;

  }
  
  /*****************************************************************************
   *  Sets tdom
   */

    protected Command createCommand (String filename) throws TemplateException {

      //code description

      if (filename == null) throw new TemplateException("Default Loader - cannot create command -> template filename is null.");
      if (filename.endsWith("xml")) {
    	//команда, обрабатывающая XML-структуры  
    	TemplateLogger.getLogger().debug("Loading XML template '" + filename + "'");  
        return new XMLCommand(filename);
      }
      else if (filename.endsWith("xsl")) {
    	//команда, обрабатывающая XSLT-структуры  
    	TemplateLogger.getLogger().debug("Loading XSL template '" + filename + "'"); 
        return new XSLCommand(filename);
      }
      else if (filename.endsWith("jct")) {
    	//команда, обрабатывающая JCT-структуры
    	TemplateLogger.getLogger().debug("Loading JCT template '" + filename + "'"); 
    	return new JCTCommand(filename);      	      	      
      }
      else throw new TemplateException("Default Loader - cannot create command -> illegal template filename.");

    }


} // DefaultLoader ends
