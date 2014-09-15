/**
 *   @(#)  Command.java	   0.1	 05/10/11
 *
 *   Copyright (C) 2005 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.templ.foundation;

/*****************************************************************************
 * A command which is processed in a template. It translates input Data object
 * to the output Data object.
 *
 * @author Amri (Anna A. Semyonova)
 * @version 0.1
 */

public interface Command {

//interface methods

/*****************************************************************************
 *  Process the command.
 *
 *  @param data the input data for command
 *
 *  @return the output data receiving by processing
 *
 *  @throws CommandException throwed if error occuring during command processing.
 */

  public Data process (Data data) throws TemplateException;

} // Command ends
