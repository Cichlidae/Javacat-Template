/**
 *   @(#)  InstructionLocator.java	   0.1	 08/01/25
 *
 *   Copyright (C) 2008 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.templ.foundation;

import java.io.InputStream;

public interface DataLocator {

	public InputStream getDataAsStream (String name);
	
}
