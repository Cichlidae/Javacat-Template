/**
 *   @(#)  SQLTransactionCommand.java	   0.1	 08/02/14
 *
 *   Copyright (C) 2006-2008 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.templ.dbs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.w3c.dom.Element;
import com.herocraft.javacat.dbs.DatabaseLogger;
import com.herocraft.javacat.dbs.DatabaseManager;
import com.herocraft.javacat.templ.foundation.Data;
import com.herocraft.javacat.templ.foundation.XMLData;
import com.herocraft.javacat.templ.foundation.TemplateException;

/*****************************************************************************
 * @author Amri (Anna A. Semyonova)
 * @version 0.1
 */

public class SQLTransactionCommand extends SQLCommand {

//class methods

/*****************************************************************************
 *  The constructor
 */

  public SQLTransactionCommand (DatabaseManager manager) {

    //code description

    super(manager);

  }

/*****************************************************************************
 *  Process the command
 */

  public Data process (Data data) throws TemplateException {

    //local variables

    Data result = data;
    
    //code description

    try {
      Element transaction = result.getAsXMLDOM().getDocumentElement();
      if (transaction != null) {
        result = new XMLData("transaction");
        result.getAsXMLDOM().getDocumentElement().setAttribute("id", transaction.getAttribute("id"));
        String dbid = transaction.getAttribute("dbid");
        ArrayList<SQLInfo> info = new ArrayList<SQLInfo>();
        ArrayList<String> SQLs = new ArrayList<String>();
        for (int i = 0; i < transaction.getChildNodes().getLength(); i ++) {
        	Element sql = (Element)transaction.getChildNodes().item(i);    
        	if ("jct:sql".equals(sql.getTagName()) || "jct:prepared-sql".equals(sql.getTagName())) {
        		SQLInfo sqlInfo = new SQLInfo(sql);
        		info.add(sqlInfo);
        		SQLs.add(sqlInfo.getSQL());        		
        	}        	
        }                
        ArrayList results = this.getDatabaseManager().executeTransaction((String[])SQLs.toArray(), dbid);
        for (int i = 0; i < results.size(); i++) {
          switch (info.get(i).getType()) {
            case SQLInfo.SELECT_TYPE: {
              ResultSet set = (ResultSet)results.get(i);
              result.getAsXMLDOM().getDocumentElement().appendChild(toJctXML(info.get(i), set));          
              set.close(); 	            	         
              break;
            }
            case SQLInfo.UPDATE_TYPE: {
            	int count = ((Integer)results.get(i)).intValue();
            	result.getAsXMLDOM().getDocumentElement().appendChild(toJctXML(info.get(i), count));            	            
                break;
            }
            case SQLInfo.XML_TYPE: {
            	ResultSet set = (ResultSet)results.get(i);      
            	result.getAsXMLDOM().getDocumentElement().appendChild(toXML(info.get(i), set));           
                break;
            }
          }
        }
      }
    }
    catch (SQLException err) {
    	DatabaseLogger.getLogger().error("SQL Transaction Command - processing error.", err);	
        throw new TemplateException(err);
    }
    return result;

  }

} // SQLTransactionCommand ends
