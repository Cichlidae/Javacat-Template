/**
 *   @(#)  InvokeInstruction.java	   0.1	 06/10/17
 *
 *   Copyright (C) 2006 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.templ.foundation;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*****************************************************************************
 * JCT (jct:invoke) instruction of Javacat Templates
 *
 * @author Amri (Anna A. Semyonova)
 * @version 0.1
 */

public class InvokeInstruction implements Instruction {
	
	/* Processes INVOKE instruction */
	public Element process (Element instruction) throws TemplateException {
					
		//local variables

	    MethodInvoker invoker = new MethodInvoker();
		
		//code description
		
		if (!"jct:invoke".equals(instruction.getTagName())) throw new TemplateException("Invoke Instruction - it's not a <jct:invoke> instruction.");
		String operator = instruction.getAttribute("class");
	    String method = instruction.getAttribute("name");
	    NodeList arglist = instruction.getElementsByTagName("jct:param");
	    Object[] args = new Object[arglist.getLength()];
	    try {
	      for (int i = 0; i < args.length; i++) {
	        args[i] = new String(((Element)arglist.item(i)).getAttribute("value"));
	      }
	      String result = (String)invoker.invoke(Class.forName(operator), method, args);
	      Element element = instruction.getOwnerDocument().createElement(method);
	      element.setAttribute("type", "string");
	      if (result != null) {
	        element.setAttribute("result", result);
	      }
	      Node parent = instruction.getParentNode();
	      parent.removeChild(instruction);
	      parent.appendChild(element);
	      return element;
	    }
	    catch (Exception err) {
	      throw new TemplateException(err);
	    }
		
	}

}
