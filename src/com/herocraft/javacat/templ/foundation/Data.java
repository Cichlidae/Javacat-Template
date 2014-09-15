/**
 *   @(#)  Data.java	   0.1	 05/10/11
 *
 *   Copyright (C) 2005 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.templ.foundation;

import java.io.InputStream;
import org.w3c.dom.Document;

/*****************************************************************************
 * It is a data object which is used as input and output of a command. The data
 * in this object can be read as XMLDOM document, input stream, string or any
 * other undefined object. If implementation doesn't use some method it must
 * throw UnsupportedOperationException.
 *
 * @author Amri (Anna A. Semyonova)
 * @version 0.1
 */

public interface Data {

//interface methods

/*****************************************************************************
 *  Gets as XMLDOM object
 *
 *  @return XML Document in W3C notation
 */

  public Document getAsXMLDOM ();

/*****************************************************************************
 *  Gets as input stream object
 *
 *  @return an input stream representation of data
 */

  public InputStream getAsInputStream ();

/*****************************************************************************
 *  Gets as string
 *
 *  @return a string representation of data
 */

  public String getAsString ();

/*****************************************************************************
 *  Gets as byte array
 *
 *  @return data as array of bytes
 */

  public byte[] getAsBytes ();

/*****************************************************************************
 *  Gets as object
 *
 *  @return data as Object extention
 */

  public Object getAsObject ();

} // Data ends
