/**
 *   @(#)  FileInstruction.java	   0.1	 08/02/21
 *
 *   Copyright (C) 2008 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.templ.foundation;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/*****************************************************************************
 * JCT (jct:file) instruction of Javacat Templates
 *
 * @author Amri (Anna A. Semyonova)
 * @version 0.1
 */

public class FileInstruction implements Instruction {
	
	/* Processes jct:file instruction */
	public Element process (Element instruction) throws TemplateException {
					
		//local variables	   
		
		InputStream file, schema;
		Element result;			
		
		//code description
		
		if (!"jct:file".equals(instruction.getTagName())) throw new TemplateException("File Instruction - it's not a <jct:file> instruction.");
		try {
			file = Configuration.getDataLocator().getDataAsStream(instruction.getAttribute("location"));				
			schema = Configuration.getDataLocator().getDataAsStream(instruction.getAttribute("schema"));
			if (file == null) throw new FileNotFoundException(instruction.getAttribute("location"));																
			XMLData xml = new XMLData(file, schema);	
			if (xml != null && xml.getAsXMLDOM() != null) {						
				Element $result = xml.getAsXMLDOM().getDocumentElement();
				result = (Element)instruction.getOwnerDocument().importNode($result, true);
				Node parent = instruction.getParentNode();
				parent.removeChild(instruction);
				parent.appendChild(result);		
			}
			else throw new IOException();						
		}		
		catch (IOException err) {				
			boolean errcatch = "true".equals(instruction.getAttribute("error-catching")) ? true : false;
			if (errcatch) {
				//ловим ошибку и вместо исключения возвращаем её в виде XML-структуры
				result = this.getError(instruction.getOwnerDocument(), err.getClass().getName() + ":" + err.getMessage());
				TemplateLogger.getLogger().warn("File Instruction: error=" + err.getClass().getName() + ":" + err.getMessage());
			}
			else {
				//бросаем исключение дальше 
				TemplateLogger.getLogger().error("File Instruction: error=" + err.getClass().getName() + ":" + err.getMessage());
				throw new TemplateException(err);
			}			
		}	
		return result;
		
	}
	
	/* Forms error result if jct:file instruction handling not successful*/
	private Element getError (Document doc, String details) {
		
		//code description
		
		Element error = doc.createElement("error");
		error.setAttribute("details", details);
		return error;
		
	}
	
}
