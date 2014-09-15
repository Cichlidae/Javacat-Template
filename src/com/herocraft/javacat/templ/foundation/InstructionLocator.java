/**
 *   @(#)  InstructionLocator.java	   0.1	 067/11/14
 *
 *   Copyright (C) 2005-2006 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.templ.foundation;

import java.io.InputStream;

public interface InstructionLocator {

	public InputStream getInstructionAsStream (String name);
	
}
