/**
 *   @(#)  ConfigInstruction.java	   0.1	 08/07/01
 *
 *   Copyright (C) 2007-2008 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.templ.foundation;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ConfigInstruction implements AtomicInstruction {

	public Element process(Element instruction) throws TemplateException {
		
		//local variables
		
		Element element = null;
		boolean inline = false;
		
		//code description
		
		if (!"jct:conf".equals(instruction.getTagName())) throw new TemplateException("Config Instruction - it's not a <jct:conf> instruction.");
		String id = instruction.getAttribute("id");
		if (id == null || "".equals(id.trim())) throw new TemplateException("Config Instruction - <jct:conf>: 'id' attribute is required!");		
		String result = instruction.getAttribute("name");		
	    String name = instruction.getAttribute("target");
	    String suffix = instruction.getAttribute("value");
	    if (name == null || "".equals(name.trim())) name = id;
	    if (result == null || "".equals(result.trim())) {	    	
	    	result = id;
	    	inline = true;	    	    	  			
	    }
		String value = Configuration.getStringConfigData(id);
		if (value == null) value = "";
		if (suffix != null) value += suffix; 
		if (inline) {
			if (instruction.getParentNode() instanceof Element) {
				//результирующее значение подставляется как атрибут в родительский тэг инструкции
				Element parent = (Element)instruction.getParentNode();
				parent.setAttribute(name, value);	
				parent.removeChild(instruction);
			}
			else inline = false;			
		}
		if (!inline) {
			//результирующее значение подставляется отдельным тэгом
			element = instruction.getOwnerDocument().createElement(result);
			element.setAttribute("id", name);
			element.setAttribute("value", value);	
			Node parent = instruction.getParentNode();
		    parent.removeChild(instruction);
		    parent.appendChild(element);			
		}		
		return element;
		
	}

}
