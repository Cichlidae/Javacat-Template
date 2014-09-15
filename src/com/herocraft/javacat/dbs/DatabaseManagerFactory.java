/**
 *   @(#)  DatabaseManagerFactory.java	   0.1	 08/01/31
 *
 *   Copyright (C) 2005-2007 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.dbs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import javax.xml.XMLConstants;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/*****************************************************************************
 * This factory returns current DatabaseManager instance and must be initialized
 * at first in init() method. In DBS library version 1.0 the factory always uses
 * DatabaseManager defined by default.
 * <br><br>
 * <tt>DatabaseSettings settings = new DatabaseSettings () {</tt><br>
 * <tt>public String getDatabaseId () {return "demodb";}</tt><br>
 * <tt>public String getDatabaseUrl () {return "jdbc:odbc:demodb";}</tt><br>
 * <tt>};</tt><br>
 * <tt>DatabaseManagerFactory.init(settings);</tt><br>
 * <tt>DatabaseManager manager = DatabaseManagerFactory.getDatabaseManager();</tt><br>
 * <tt>... ...</tt><br>
 * <tt>DatabaseManagerFactory.destroy();</tt><br>
 *
 * @author Amri (Anna A. Semyonova)
 * @version 0.1
 */

public final class DatabaseManagerFactory {

//class variables

private static DatabaseManager manager;

private static class DefaultDatabaseSettings extends DatabaseSettings {
	
	//class variables
	
	String id;
	String url = "";
	String driver = "";
	String user = "";
	String password = "";
	int initial_connection_count = 0;
	int maximum_connection_count = 0;
	String database_pool = "";
	int transaction_isolation = 0;
	boolean standalone_connection_allowed = false;
	Properties connection_properties = new Properties();
	ArrayList<String> connection_instructions = new ArrayList<String>(1);
	Properties prepared_queries = new Properties();
		
	//class methods
	
	private DefaultDatabaseSettings () {
		
	}	
	public String getDatabaseId () {
		return id;
	}
	public String getDatabaseUrl () {
		return url;
	}
	public String getDatabaseDriver () {
		if (driver != null && !driver.trim().equals("")) return driver;
		else return super.getDatabaseDriver();
	}
	protected String getDatabaseUser () {
		if (user != null && !user.trim().equals("")) return user;
		else return super.getDatabaseUser();
	}
	protected String getDatabasePassword () {
		if (password != null && !password.trim().equals("")) return password;
		else return super.getDatabasePassword();
	}
	public int getDatabaseInitialConectionCount () {
		if (initial_connection_count > 0) return initial_connection_count;
		else return super.getDatabaseInitialConectionCount();
	}
	public int getDatabaseMaximumConnectionCount () {
		if (maximum_connection_count > 0) return maximum_connection_count;
		else return super.getDatabaseMaximumConnectionCount();
	}
	protected DatabasePool getDatabasePool () {
		if (database_pool == null || !database_pool.trim().equals("")) return super.getDatabasePool();
		try {
			DatabasePool pool = (DatabasePool)Class.forName(database_pool).newInstance();
			return pool;
		}
		catch (Exception err) {
			return super.getDatabasePool();
		}		
	}
	public int getTransactionIsolation () {
		if (transaction_isolation > 0) return transaction_isolation;
		else return super.getTransactionIsolation();
	}
	public boolean standaloneConnectionAllowed () {
		return standalone_connection_allowed;	
	}	
	public Properties getDatabaseConnectionProperties () {
		return connection_properties;
	}
	public String[] getDatabaseConnectionInstructions () {
		String[] instructions = new String[connection_instructions.size()];
		return connection_instructions.toArray(instructions);
	}
	public Properties getPreparedQueries () {
		return prepared_queries;
	}
}

//class methods

/*****************************************************************************
 *  The constructor
 */

  private DatabaseManagerFactory () {

  }

/*****************************************************************************
 *  Initializes the factory.
 *
 *  @param settings the settings for all databases which is planned to manage
 *  by DatabaseManager
 *
 *  @exception SQLException throwed if the current DatabaseManager cannot be initialized
 *  @exception IllegalStateException throwed if the current DatabaseManager is already initialized
 *  @exception NullPointerException throwed if settings parameter is null
*/  
  
  public static void init (File dir) throws SQLException, Exception {
	
	  //local variables
	  
	  final ArrayList<DefaultDatabaseSettings> settings = new ArrayList<DefaultDatabaseSettings>(1);	 
	  
	  //code description
	  	
	  if (dir == null) {
		  DatabaseLogger.getLogger().error("JC Templates Database Manager - config directory is null.");
		  throw new NullPointerException();
	  }
	  File config = new File(dir, "dbs.xml");	
	  if (!config.exists()) { 
		  DatabaseLogger.getLogger().error("JC Templates Database Manager - config file not found in the specified directory: " + config.getPath());
		  throw new FileNotFoundException(config.getPath());		 
	  }		  
	  InputStream schema = DatabaseManagerFactory.class.getResourceAsStream("conf/dbs.xsd");
	  SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);	    				    			    	    			    
	  Validator validator = factory.newSchema(new StreamSource(schema)).newValidator();	  
	  validator.setErrorHandler(new ErrorHandler () {
		  	public void warning(SAXParseException exception) {
		  		DatabaseLogger.getLogger().error("JC Templates Database Manager - WARNING: " + exception.toString());		        
	        }

	        public void error(SAXParseException exception) {
	        	DatabaseLogger.getLogger().error("JC Templates Database Manager - ERROR: " + exception.toString());			         
	        }

	        public void fatalError(SAXParseException exception) {
	        	DatabaseLogger.getLogger().error("JC Templates Database Manager - FATAL ERROR: " + exception.toString());			        
	        }		        
	  });		  	 
	  DefaultHandler handler = new DefaultHandler () {
			  String path = "";
			  String tempval = "";
			  public void startElement (String uri, String localName, String qName, Attributes attrs) {
								
				  //code description
				  	
				  if ("dbs".equalsIgnoreCase(qName)) {					  
				  }
				  else if ("db".equalsIgnoreCase(qName)) {
					  if (path.endsWith(".dbs")) {
						  DefaultDatabaseSettings setting = new DefaultDatabaseSettings();
						  setting.id = attrs.getValue("id");
						  settings.add(setting);
						  DatabaseLogger.getLogger().debug("JC Templates Database Manager - read " + setting.id + " properties:");
					  }						  
				  }	
				  else {
					  DatabaseLogger.getLogger().debug("* property: " + qName);
					  if ("property".equalsIgnoreCase(qName)) {
						  if (path.endsWith(".db.connection-properties")) {						
							  tempval = attrs.getValue("key");						  						 
						  }					  
					  }
					  else if ("query".equalsIgnoreCase(qName)) {
						  if (path.endsWith(".db.prepared-queries")) {						
							  tempval = attrs.getValue("name");						  						 
						  }					  
					  }		
				  }
				  path += "." + qName;					  
				  
			  }
			  public void endElement (String uri, String localName, String qName) {
				  
				  //code description
				  
				  int index = path.lastIndexOf(".");
					if (index > -1) {
						path = path.substring(0, index);
				  }
				  
			  }			
			  public void characters(char[] ch, int start, int length) throws SAXException {
				  
				  //code description
				  
				  String value = new String(ch, start, length);				  
				  if (!Character.isISOControl(value.charAt(0))) {
					   DatabaseLogger.getLogger().debug("* characters <" + value + "> found in " + path);
				  }	
				  else return;					  				 
				  if (path.endsWith(".db.url")) {
					  DefaultDatabaseSettings setting = settings.get(settings.size() - 1);
					  setting.url = value;			
				  }	
				  else if (path.endsWith(".db.driver")) {
					  DefaultDatabaseSettings setting = settings.get(settings.size() - 1);
					  setting.driver = value;				  					  
				  }	
				  else if (path.endsWith(".db.user")) {
					  DefaultDatabaseSettings setting = settings.get(settings.size() - 1);
					  setting.user = value;					  					  
				  }	
				  else if (path.endsWith(".db.password")) {
					  DefaultDatabaseSettings setting = settings.get(settings.size() - 1);
					  setting.password = value;					  					  
				  }	
				  else if (path.endsWith(".db.initial_connection_count")) {
					  DefaultDatabaseSettings setting = settings.get(settings.size() - 1);	
					  try {
						  setting.initial_connection_count = Integer.parseInt(value);						  
					  }
					  catch (Exception err) {
						  DatabaseLogger.getLogger().warn("JC Templates Database Manager - error in reading of parameter 'initial_connection_count'.");						  
					  };
				  }	
				  else if (path.endsWith(".db.maximum-connection-count")) {
					  DefaultDatabaseSettings setting = settings.get(settings.size() - 1);	
					  try {
						  setting.maximum_connection_count = Integer.parseInt(value);
					  }
					  catch (Exception err) {
						  DatabaseLogger.getLogger().warn("JC Templates Database Manager - error in reading of parameter 'maximum_connection_count': " + new String(ch));
					  }
				  }
				  else if (path.endsWith(".db.database-pool")) {
					  DefaultDatabaseSettings setting = settings.get(settings.size() - 1);
					  setting.database_pool = value;					  					  
				  }
				  else if (path.endsWith(".db.transaction-isolation")) {
					  DefaultDatabaseSettings setting = settings.get(settings.size() - 1);	
					  try {
						  setting.transaction_isolation = Integer.parseInt(value);
					  }
					  catch (Exception err) {
						  DatabaseLogger.getLogger().warn("JC Templates Database Manager - error in reading of parameter 'transaction_isolation'.");
					  }
				  }
				  else if (path.endsWith(".db.standalone-connection-allowed")) {
					  DefaultDatabaseSettings setting = settings.get(settings.size() - 1);						 
					  setting.standalone_connection_allowed = "true".equals(value) ? true : false;					
				  }		
				  else if (path.endsWith(".db.connection-properties.property")) {
					  DefaultDatabaseSettings setting = settings.get(settings.size() - 1);
					  setting.connection_properties.setProperty(tempval, value);					  						  					
				  }	
				  else if (path.endsWith(".db.connection-instructions.instruction")) {
					  DefaultDatabaseSettings setting = settings.get(settings.size() - 1);
					  setting.connection_instructions.add(value);				  						  					
				  }
				  else if (path.endsWith(".db.prepared-queries.query")) {
					  DefaultDatabaseSettings setting = settings.get(settings.size() - 1);
					  setting.prepared_queries.setProperty(tempval, value);					  						  					
				  }	
			  }
	  };
	  SAXResult result = new SAXResult(handler);
	  try {
		 validator.validate(new SAXSource(new InputSource(new FileInputStream(config))), result);
	  }
	  catch (Exception err) {
		  DatabaseLogger.getLogger().fatal("JC Templates Database Manager - fatal error during initialization dbs.xml", err);			
		  throw err;
	  }	  		
	  DatabaseSettings[] dbsarray = new DatabaseSettings[settings.size()];
	  dbsarray = settings.toArray(dbsarray);
	  DatabaseManagerFactory.init(dbsarray);		  	
	  
  }
    
/*****************************************************************************
 *  Initializes the factory.
 *
 *  @param settings the settings for all databases which is planned to manage
 *  by DatabaseManager
 *
 *  @exception SQLException throwed if the current DatabaseManager cannot be initialized
 *  @exception IllegalStateException throwed if the current DatabaseManager is already initialized
 *  @exception NullPointerException throwed if settings parameter is null
 */

  public static void init (DatabaseSettings[] settings) throws SQLException {

    //code description

	if (settings == null) throw new NullPointerException();  
	if (DatabaseManagerFactory.isInitialized()) DatabaseManagerFactory.release();   
	try {
		manager = new DefaultDatabaseManager(settings);
	}
	catch (SQLException err) {
		DatabaseLogger.getLogger().fatal("JC Templates Database Manager - fatal error during initialization manager instance.", err);
		throw err;
	}
	DatabaseLogger.getLogger().info("JC Templates Database Manager initialized sucessfully.");

  }

/*****************************************************************************
 *  Destroys the factory.
 */

  public static void release () {

    //code description

    if (manager != null) {
      try {	
    	  manager.release();
    	  DatabaseLogger.getLogger().info("JC Templates Database Manager released sucessfully.");
      }
      catch (SQLException err) {};
      manager = null;
    }    

  }

/*****************************************************************************
 *  Gets the database manager instance.
 *
 *  @return the database handler only instance to work with
 *
 *  @exception IllegalStateException throwed if a current DatabaseManager is not initialized
 */

  public static DatabaseManager getDatabaseManager () {

    //code description

    if (manager == null) {
    	RuntimeException e = new IllegalStateException("JC Templates Instruction Database Manager not initialized.");
		DatabaseLogger.getLogger().fatal("JC Templates Instruction Database Manager not initialized.", e);
		throw e;	    	    
    }
    return manager;

  }

/*****************************************************************************
 *  Check if manager is initialized
 */

  public static boolean isInitialized () {

    //code description

    if (manager == null) return false;
    else return true;

  }

} // DatabaseManagerFactory ends
