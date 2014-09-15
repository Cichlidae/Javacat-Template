/**
 *   @(#)  Template.java	   0.1	 05/10/11
 *
 *   Copyright (C) 2005 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.templ.foundation;

/*****************************************************************************
 * Represents a template of handling. It transforms input data array to the
 * output data array in the manner defined in implementations.
 *
 * @author Amri (Anna A. Semyonova)
 * @version 0.1
 */

public interface Template {

//interface methods

/*****************************************************************************
 *  Applies a template to all the input data in the array. The result array
 *  not strongly need to be the same size as input one.
 *
 *  @param data the input data
 *
 *  @return the output data
 *
 *  @throws TemplateException throwed if hard error occuring while appliing a template
 */

  public Data apply (String name, Data data) throws TemplateException;

} // Template ends
