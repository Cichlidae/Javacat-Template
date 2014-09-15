/**
 *   @(#)  JCTCommand.java	   0.1	 08/02/01
 *
 *   Copyright (C) 2005-2008 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.templ.foundation;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

/*****************************************************************************
 * JCT processor handles Javacat Templates
 *
 * @author Amri (Anna A. Semyonova)
 * @version 0.1
 */

public class JCTCommand implements Command {

//class variables

private Document doc = null;
private String id = "unknown";

//class methods

/*****************************************************************************
 *  The constructors
 */

  public JCTCommand (String filename) throws TemplateException {
	  
	  //code description
	  
	  try {
	      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	      this.doc = builder.parse(Configuration.getTemplateLocator().getTemplateAsStream(filename));
	      this.id = filename;
	    }
	    catch (Exception err) {
	      TemplateLogger.getLogger().error("JCT Command: file reading error '" + filename + "' (DOM)", err); 	    	
	      throw new TemplateException(err.getMessage());
	    }

  }
  
  public JCTCommand () {
	  
  }

/*****************************************************************************
 *  Process XMLDOM structure
 */

  public Data process (Data data) throws TemplateException {

    //code description
	 	 
	if (data != null) this.doc = data.getAsXMLDOM();		
	if (this.doc == null) {
		TemplateLogger.getLogger().error("JCT Command: file for handling not matched."); 	    	
	    throw new TemplateException("JCT Command: file for handling not matched.");
	}	
    Element element = doc.getDocumentElement();    
    try {
    	return this.process(element);
    }
    catch (Exception err) {
    	TemplateLogger.getLogger().error("JCT Command: template transformation error '" + this.id + "' (JCT)", err); 		
        throw new TemplateException(err.getMessage());
    }

  }

/*****************************************************************************
 *  Process XMLDOM structure
 */

   private Data process (Element element) throws TemplateException {
    
     //code decription

     Instruction instruction = InstructionFactory.getInstruction(element.getTagName());      
     if (instruction != null && instruction instanceof AtomicInstruction) { 
    	 TemplateLogger.getLogger().debug("JCT Command: atomic instruction " + instruction.getClass());
    	 instruction.process(element);
     }              
     else {
       NodeList children = element.getChildNodes();
       if (children.getLength() > 0) {
    	   Node[] nodes = new Node[children.getLength()];
    	   for (int i = 0; i < children.getLength(); i++) {
    		   nodes[i] = children.item(i);
    	   }
    	   for (int i = 0; i < nodes.length; i++) {
    		   if (nodes[i].getNodeType() != Node.ELEMENT_NODE) continue;
    		   this.process((Element)nodes[i]);
    	   }
       } 
       if (instruction != null) {
    	   TemplateLogger.getLogger().debug("JCT Command: instruction " + instruction.getClass());
    	   instruction.process(element);     
       }
     }
     this.doc.getDocumentElement().removeAttribute("xmlns:jct");
     return new XMLData(this.doc);

   } 
  
} // DefaultProcessor ends
