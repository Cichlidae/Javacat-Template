/**
 *   @(#)  DefaultTemplate.java	   0.1	 08/02/01
 *
 *   Copyright (C) 2005-2008 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.templ.foundation;

/*****************************************************************************
 * Represents a template of handling. It transforms input data array to the
 * output data array in the manner defined in implementations.
 *
 * @author Amri (Anna A. Semyonova)
 * @version 0.1
 */

public class DefaultTemplate implements Template {

//class variables

private Command command = null;

//class methods

/*****************************************************************************
 *  The constructor
 */

  public DefaultTemplate (Command command) throws TemplateException {
	  
	  //code description
	  
	  if (command == null) throw new TemplateException("Default Template - needs a command to execute.");
	  this.command = command;
	  
  }

/*****************************************************************************
 *  Applies a template to all the input data in the array. The result array
 *  not strongly need to be the same size as input one.
 *
 *  @param data the input data
 *
 *  @return the output data
 *
 *  @throws TemplateException throwed if hard error occuring while appliing a template
 */

  public Data apply (String name, Data data) throws TemplateException {

    //local variables

    Data result = null;

    //code description

    //сначала осуществляем преобразование XML-данных на основе команды, сопоставленной объекту шаблона
    result = command.process(data);
    if (command instanceof XSLCommand) {
      //после XSLT-трансформации необходимо запустить JCT-обработчик инструкций JC Templates 	
      JCTCommand processor = new JCTCommand();
      result = processor.process(result);
    }
    return result;

  }

} // DefaultTemplate ends
