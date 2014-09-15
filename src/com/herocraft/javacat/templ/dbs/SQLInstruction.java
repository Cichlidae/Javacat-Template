/**
 *   @(#)  SQLInstruction.java	   0.1	 06/10/17
 *
 *   Copyright (C) 2006 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.templ.dbs;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import com.herocraft.javacat.dbs.DatabaseManager;
import com.herocraft.javacat.dbs.DatabaseManagerFactory;
import com.herocraft.javacat.templ.foundation.Instruction;
import com.herocraft.javacat.templ.foundation.XMLData;
import com.herocraft.javacat.templ.foundation.TemplateException;

/*****************************************************************************
 * JCT (jct:sql) instruction of Javacat Templates
 *
 * @author Amri (Anna A. Semyonova)
 * @version 0.1
 */

public class SQLInstruction implements Instruction {
	
	/* Database manager needed to do SQL queries */
	private DatabaseManager manager;
				
	/* The constructor */
	public SQLInstruction () {
		
		//code description
		
		this.manager = DatabaseManagerFactory.getDatabaseManager();
		
	}
	
	/* Processes SQL instruction */
	public Element process (Element instruction) throws TemplateException {
		
		//code description
		
		if (!"jct:sql".equals(instruction.getTagName()) && !"jct:prepared-sql".equals(instruction.getTagName())) 
			throw new TemplateException("SQL Instruction - It's not a <jct:sql> or <jct:prepared-sql> instruction.");
		XMLData xml = new XMLData(instruction);
	    SQLCommand command = new SQLCommand(this.manager);
	    Element $result = command.process(xml).getAsXMLDOM().getDocumentElement();
	    Element result = (Element)instruction.getOwnerDocument().importNode($result, true);
	    Node parent = instruction.getParentNode();
	    parent.removeChild(instruction);
	    parent.appendChild(result);
	    return result;
		
	}

}
