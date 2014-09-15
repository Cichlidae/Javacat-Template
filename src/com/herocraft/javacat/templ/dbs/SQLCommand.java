/**
 *   @(#)  SQLCommand.java	   0.1	 08/02/14
 *
 *   Copyright (C) 2005-2008 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.templ.dbs;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Iterator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import com.herocraft.javacat.dbs.DatabaseManager;
import com.herocraft.javacat.dbs.DatabaseLogger;
import com.herocraft.javacat.templ.foundation.Command;
import com.herocraft.javacat.templ.foundation.Data;
import com.herocraft.javacat.templ.foundation.XMLData;
import com.herocraft.javacat.templ.foundation.TemplateException;

/*****************************************************************************
 * @author Amri (Anna A. Semyonova)
 * @version 0.1
 */

public class SQLCommand implements Command {

//class variables

private DatabaseManager manager;

//class methods

/*****************************************************************************
 *  The constructor
 */

  public SQLCommand (DatabaseManager manager) {

    //code description

    if (manager == null) throw new NullPointerException();
    this.manager = manager;

  }

/*****************************************************************************
 *  Gets db manager used
 */

  public DatabaseManager getDatabaseManager () {

    //code description

    return this.manager;

  }

/*****************************************************************************
 *  Process the command
 */

  public Data process (Data data) throws TemplateException {

    //local variables

    Data result = data;
    Node node = null;

    //code description

    try {
      Element sql = result.getAsXMLDOM().getDocumentElement();
      if (sql != null) {          
          String dbid = sql.getAttribute("dbid");        
          SQLInfo info = new SQLInfo(sql);          
          DatabaseLogger.getLogger().debug("SQL Command - SQL: " + info.getSQL());          
          switch (info.getType()) {
            case SQLInfo.SELECT_TYPE: {
              ResultSet set = null;
              if (info.getParameters().length > 0) 
            	  set = manager.executePreparedQuery(info.getSQL(), info.getParameters(), dbid); 
              else 
            	  set = manager.executeQuery(info.getSQL(), dbid);
              node = toJctXML(info, set);
              set.close();            	         
              break;
            }
            case SQLInfo.UPDATE_TYPE: {
              int count = 0;
              if (info.getParameters().length > 0)  {               
            	  count = manager.executePreparedUpdates(info.getSQL(), info.getParameters(), dbid);
              }	  
              else 	
            	  count = manager.executeUpdates(info.getSQL(), dbid);
              node = toJctXML(info, count);            	       
              break;
            }
            case SQLInfo.XML_TYPE: {
              ResultSet set = null;	
              if (info.getParameters().length > 0)	
            	  set = manager.executePreparedQuery(info.getSQL(), info.getParameters(), dbid);
              else
            	  set = manager.executeQuery(info.getSQL(), dbid); 	            	
              node = toXML(info, set);
              break;
            }
          }
          if (node != null) {
            result = new XMLData((Element)node);
          }
      }
    }
    catch (SQLException err) {
      DatabaseLogger.getLogger().error("SQL Command - processing error.", err);	
      throw new TemplateException(err);
    }
    return result;

  }

/*****************************************************************************
 *  Prepares result of updates in xml format
 */

  protected Node toJctXML (SQLInfo info, int resultCount) {

    //code description

    XMLData xmlData = new XMLData(info.getId());
    Element root = xmlData.getAsXMLDOM().getDocumentElement();
    root.setAttribute("updated-records", String.valueOf(resultCount));
    return xmlData.getAsXMLDOM().getDocumentElement();

  }

/*****************************************************************************
 *  Prepares result sets representing it in xml format
 */
  
  protected Node toXML (SQLInfo info, ResultSet resultSet) throws SQLException {
	  
	//local variables
	  
	XMLData xmlData = new XMLData(info.getId());
	  
	//code description
	  	
	Document doc = xmlData.getAsXMLDOM();
    Element root = doc.getDocumentElement();
	while (resultSet.next()) {
		//берем только первый столбец (подразумевается, что задан только один столбец в запросе)
		XMLData data = new XMLData(resultSet.getBinaryStream(1), null);
		Element $result = data.getAsXMLDOM().getDocumentElement();
		Element result = (Element)doc.importNode($result, true);				
		root.appendChild(result);				 
	}	  
	return xmlData.getAsXMLDOM().getDocumentElement();
	  
  }
  
/*****************************************************************************
 *  Prepares result sets representing it in xml format
 */

  protected Node toJctXML (SQLInfo info, ResultSet resultSet) throws SQLException {

    //local variables

    XMLData xmlData = new XMLData(info.getId());
    HashMap<String, Integer> sum = new HashMap<String, Integer>();
    int groupIndex = -1;
    String sumFields = info.getResultSumFieldsAttr();
    String groupFields = info.getResultGroupFieldsAttr();

    //code description
   
    ResultSetMetaData metadata = resultSet.getMetaData();
    //at first we need to result-sum flags up
    StringTokenizer tokenizer = new StringTokenizer(sumFields, ";");
    while (tokenizer.hasMoreTokens()) {
        String token = tokenizer.nextToken();
        sum.put(token, new Integer(0));
    }
    for (int i = 0; i < metadata.getColumnCount(); i++) {
        String column = metadata.getColumnName(i + 1);
        if (column.equals(groupFields)) groupIndex = i;
    }
    //prepare current group record
    Element group = null;
    //form result set xml
    Document doc = xmlData.getAsXMLDOM();
    Element root = doc.getDocumentElement();
    while (resultSet.next()) {
        Element record = doc.createElement("record");
        for (int j = 0; j < metadata.getColumnCount(); j++) {
          String value = resultSet.getString(metadata.getColumnName(j + 1));
          record.setAttribute(metadata.getColumnName(j + 1), value);
          if (sum.containsKey(metadata.getColumnName(j + 1))) {
            //calculate summas of result-sum field values
            int counter = ( (Integer) sum.get(metadata.getColumnName(j + 1))).
                intValue();
            counter += resultSet.getInt(metadata.getColumnName(j + 1));
            sum.put(metadata.getColumnName(j + 1), new Integer(counter));
          }
          if (j == groupIndex) {
            //we need to group this record
            if (group == null || !group.getAttribute("id").equals(value)) {
              //change group
              group = doc.createElement("group");
              group.setAttribute("id", value);
              root.appendChild(group);
            }
          }
        }
        if (groupIndex == -1) root.appendChild(record);
        else group.appendChild(record);
    }
    if (root.hasChildNodes()) {
        Iterator sums = sum.keySet().iterator();
        while (sums.hasNext()) {
          String field = (String) sums.next();
          int summa = ( (Integer) sum.get(field)).intValue();
          root.setAttribute(field, String.valueOf(summa));
        }
    }   
    return xmlData.getAsXMLDOM().getDocumentElement();

  }

} // SQLCommand ends
