/**
 *   @(#)  Instruction.java	   0.1	 06/10/17
 *
 *   Copyright (C) 2006 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.templ.foundation;

import org.w3c.dom.Element;

/*****************************************************************************
 * JCT instruction of Javacat Templates
 *
 * @author Amri (Anna A. Semyonova)
 * @version 0.1
 */

public interface Instruction {
	
	public Element process (Element instruction) throws TemplateException;

}
