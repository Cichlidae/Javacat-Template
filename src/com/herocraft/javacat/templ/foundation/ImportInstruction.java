/**
 *   @(#)  ImportInstruction.java	   0.1	 06/10/17
 *
 *   Copyright (C) 2006 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.templ.foundation;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/*****************************************************************************
 * JCT (jct:import) instruction of Javacat Templates
 *
 * @author Amri (Anna A. Semyonova)
 * @version 0.1
 */

public class ImportInstruction implements Instruction {
		
	/* Processes SQL instruction */
	public Element process (Element instruction) throws TemplateException {
		
		//local variables

	    String template = instruction.getAttribute("template");
		
		//code description
		
		if (!"jct:import".equals(instruction.getTagName())) throw new TemplateException("Import Instruction - it's not a <jct:import> instruction.");
		Template templ = Configuration.getTemplateLoader().loadTemplate(template, null);
	    XMLData xml = new XMLData(instruction);
	    Element $result = templ.apply(template, xml).getAsXMLDOM().getDocumentElement();
	    Element result = (Element)instruction.getOwnerDocument().importNode($result, true);
	    Node parent = instruction.getParentNode();
	    parent.removeChild(instruction);
	    parent.appendChild(result);
	    return result;
		
	}

}
