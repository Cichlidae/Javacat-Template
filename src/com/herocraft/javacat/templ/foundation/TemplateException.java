/**
 *   @(#)  TemplateException.java	   0.1	 05/10/11
 *
 *   Copyright (C) 2005 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.templ.foundation;

/*****************************************************************************
 * Throwed if errors with template handling occur.
 *
 * @author Amri (Anna A. Semyonova)
 * @version 0.1
 */

public class TemplateException extends Exception {

//class variables
	
static final long serialVersionUID = 20070126L; 
		
//class methods

/****************************************************************************
 *  The constructor.
 *
 *  @param cause the underliing exception
 */

  public TemplateException (Throwable cause) {

    super(cause.getMessage(), cause);

  }

/****************************************************************************
 *  The constructor.
 *
 *  @param message the message of exception
 */

  public TemplateException (String message) {

    super(message);

  }

} // TemplateException ends
