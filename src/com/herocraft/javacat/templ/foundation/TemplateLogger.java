/**
 *   @(#)  TemplateLogger.java	   0.1	 07/11/15
 *
 *   Copyright (C) 2007 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.templ.foundation;

import org.apache.log4j.Logger;

/******************************************************************
 * JC Templates log4j logging
 * 
 * @author Nadi Shakti
 *
 */

public class TemplateLogger {
	
public static final String LOGGER_NAME = "com.herocraft.javacat.templ";

/*******************************************************************
 *  The constructor
 */

  private TemplateLogger () {
	  
  }

/*******************************************************************
 *  Returns a log4j logger
 */
	
  public static Logger getLogger () {
	  
	  //code description
	  	  
	  return Logger.getLogger(LOGGER_NAME);
	  
  }
  
} //TemplateLogger ends
