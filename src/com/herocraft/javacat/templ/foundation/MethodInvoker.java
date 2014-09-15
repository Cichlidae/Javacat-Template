/**
 *   @(#)  MethodInvoker.java	   0.1	 05/10/21
 *
 *   Copyright (C) 2005 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.templ.foundation;

import java.lang.reflect.*;

/*****************************************************************************
 * @author Amri (Anna A. Semyonova)
 * @version 0.1
 */

class MethodInvoker {

//class methods

/*****************************************************************************
 *  The constructor
 */

  MethodInvoker () {

  }

/*****************************************************************************
 *   Invokes methods by Reflection API
 */

  final Object invoke (Class c, String method, Object[] args) throws TemplateException {

    //local variables

    Class[] parameters = new Class[args.length];

    //code description

    for (int i = 0; i < args.length; i++) parameters[i] = args[i].getClass();
    try {
      Method $method = c.getMethod(method, parameters);
      Object result = $method.invoke(null, args);
      return result;
    }
    catch (Exception err) {
      throw new TemplateException(err);
    }

  }

} // MethodInvoker ends
