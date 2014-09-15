/**
 *   @(#)  TemplateLocator.java	   0.1	 05/11/30
 *
 *   Copyright (C) 2005 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.templ.foundation;

import java.io.InputStream;

/*****************************************************************************
 * Creates Template instances
 *
 * @author Amri (Anna A. Semyonova)
 * @version 0.1
 */

public interface TemplateLocator {

//interface methods

/*****************************************************************************
 *  Get a template as input stream
 */

  public InputStream getTemplateAsStream (String name);

} // TemplateLocator ends
