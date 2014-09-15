/**
 *   @(#)  TemplateLoader.java	   0.1	 08/02/01
 *
 *   Copyright (C) 2005-2008 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.templ.foundation;

/*****************************************************************************
 * Creates Template instances
 *
 * @author Amri (Anna A. Semyonova)
 * @version 0.1
 */

public interface TemplateLoader {

//interface methods

/*****************************************************************************
 *  Create Template instance
 */

  public Template loadTemplate (String name, String filename) throws TemplateException;

} // TemplateLoader ends
