/**
 *   @(#)  SQLInfo.java	   0.1	 08/02/18
 *
 *   Copyright (C) 2006-2008 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.templ.dbs;

import java.sql.SQLException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.herocraft.javacat.dbs.PreparedParameter;
import com.herocraft.javacat.templ.foundation.TemplateException;
import com.herocraft.javacat.templ.foundation.XMLData;

/*****************************************************************************
 * Holds an info about some SQL in JC TEMPLATES
 *
 * @author Amri (Anna A. Semyonova)
 * @version 0.1
 */

public class SQLInfo {

//class variables

private final String id;
private final int type;
private final String SQL;
private String resultSum = "";
private String resultGroup = "";

private PreparedParameter[] parameters = new PreparedParameter[0];

public static final int SELECT_TYPE = 0;
public static final int UPDATE_TYPE = 1;
public static final int XML_TYPE = 2;

private static final int[] TYPES = new int [] {SELECT_TYPE, UPDATE_TYPE, XML_TYPE};

//class methods

/*****************************************************************************
 *  The constructor
 */

  public SQLInfo (Element sql) throws TemplateException, SQLException {
	  
	  //code description
	  
	  if (sql == null) throw new NullPointerException();
	  this.id = sql.getAttribute("name");
	  if ("".equals(this.id.trim())) throw new IllegalArgumentException();  
	  if ("jct:prepared-sql".equals(sql.getTagName())) {
		  //это параметризованный запрос
		  NodeList text = sql.getElementsByTagName("jct:prepared-text");
		  if (text.getLength() == 0) throw new TemplateException("SQL Info - required 'jct:prepared-text' not found in 'jct:prepared-sql'.");
		  this.SQL = this.getSQL((Element)text.item(0));				  
		  this.parameters = this.getParameters(sql);
	  }
	  else {
		  //обычный запрос
		  this.SQL = this.getSQL(sql);  
	  }	  	  	    	  	
	  if (this.SQL.startsWith("SELECT")) {      
	      //выясним в каком виде ожидается результат: набор записей или XML-структур
	      String xml = sql.getAttribute("xml");
	      if ("true".equals(xml)) {
	    	  //набор XML-структур
	    	  this.type = SQLInfo.TYPES[SQLInfo.XML_TYPE];	    
	      }
	      else {
	    	  //набор записей:
	    	  //check if SQL template has other attributes:
	          //result-sum - the index of numeric field names the value sum of which
	          //must be calculated; result-group - the group by some field
	    	  this.setResultSumFieldsAttr(sql.getAttribute("result-sum"));
	    	  this.setResultGroupFieldsAttr(sql.getAttribute("result-group"));	 
	    	  this.type = SQLInfo.TYPES[SQLInfo.SELECT_TYPE];	 	    	
	      }           
	    }
	    else {
	      this.type = SQLInfo.TYPES[SQLInfo.UPDATE_TYPE];	   		  
	    }	  
	  
  }
  
/*****************************************************************************
 *  The constructor
 */

  protected void setResultSumFieldsAttr (String resultSum) {

    //code description

    if (resultSum != null) this.resultSum = resultSum; 

  }
  
/*****************************************************************************
 *    The constructor
 */

  protected void setResultGroupFieldsAttr (String resultGroup) {

	  //code description
   
      if (resultGroup != null) this.resultGroup = resultGroup;

  }  

/*****************************************************************************
 *  Gets SQL string from XML tag
 */

  protected String getSQL (Element sql) {

    //local variables

    String SQL = "";

    //code description
        
    NodeList textlist = sql.getChildNodes();
    for (int i = 0; i < textlist.getLength(); i++) {
      if (textlist.item(i).getNodeType() == Node.TEXT_NODE) {
        SQL += textlist.item(i).getNodeValue();
      }
    }
    return SQL;

  } 
  
/*****************************************************************************
 *  Получим параметры запроса
 */  
  
  protected PreparedParameter[] getParameters (Element sql) throws SQLException {
	  
	  //code description
	  
	  NodeList paramlist = sql.getElementsByTagName("jct:prepared-param");
	  PreparedParameter[] params = new PreparedParameter[paramlist.getLength()];
	  for (int i = 0; i < paramlist.getLength(); i++) {
		  Element parameter = (Element)paramlist.item(i);
		  String type = parameter.getAttribute("type");
		  String position = parameter.getAttribute("position");
		  String value = parameter.getAttribute("value");
		  if (parameter.getChildNodes().getLength() == 0 || (value != null && value.length() > 0)) {
			  //параметр ординарного типа
			  if (value != null) 
				  params[i] = new PreparedParameter(type, position, value);			  
			  else
				  params[i] = new PreparedParameter(type, position, "");
		  }
		  else {
			  //параметр бинарного типа
			  if (parameter.getChildNodes().getLength() > 0) {				  				  
				  XMLData xml = new XMLData((Element)parameter.getFirstChild());
				  params[i] = new PreparedParameter(type, position, xml.getAsBytes());
			  }
			  else {				  
				  params[i] = new PreparedParameter(type, position, new byte[0]);  
			  }			  
		  }		  		  	
	  }
	  return params;	  
	  
  }
  
/*****************************************************************************
 *  Gets sql id
 */

  public String getId () {

    //code description

    return this.id;

  }

/*****************************************************************************
 *  Gets sql type
 */

  public int getType () {

    //code description

    return this.type;

  }
  
/*****************************************************************************
 *  Gets SQL string
 */  

  public String getSQL () {
	  
	  //code description 
	  
	  return this.SQL;
	  
  }
  
/*****************************************************************************
 *  Gets SQL parameters
 */  
  
  public PreparedParameter[] getParameters () {
	  
	  //code description
	  
	  return this.parameters;
	  
  }
  
/*****************************************************************************
 *  Gets sql result sum attr
 */

  public String getResultSumFieldsAttr () {

    //code description

    return this.resultSum;

  }

/*****************************************************************************
 * Gets sql result group fields attr
 */

  public String getResultGroupFieldsAttr () {

    //code descriptiom

    return this.resultGroup;

  }

} // SQLInfo ends
