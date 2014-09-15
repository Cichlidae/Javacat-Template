/**
 *   @(#)  DefaultDatabasePool.java	  0.3	   08/02/08
 *
 *   Copyright (C) 2005-2008 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.dbs;

import java.sql.*;
import java.util.*;

/*****************************************************************************
 * Simple connection pool organized manually. All database statements creates
 * temporarily for each input query.
 *
 * @author OpenSource, patched by Amri (Anna A. Semyonova)
 * @version 0.2
 */


class DefaultDatabasePool extends DatabasePool implements Runnable {

//class variables

private String id;
private String driver, url, username, password;
private int initConnections = 10;
private int maxConnections = 20;
private boolean waitIfBusy;
private Vector<Connection> availableConnections, busyConnections;
private Connection standaloneConnection;
private boolean connectionPending = false;
private Properties props = new Properties();
private String[] inst = new String[0];
private Map<String, PreparedStatement> preparedStatements = new HashMap<String, PreparedStatement>();
private int transactIsol = Connection.TRANSACTION_NONE;

private static final byte TRY_COUNT = 2;

//class methods

/*****************************************************************************
 *  Gets database pool id
 */

  public String getID () {
	  
	  //local variables
	  
	  return this.id;
	  
  }

/*****************************************************************************
 *  Creates new database connection pool.
 */

  public void create (DatabaseSettings settings) throws SQLException {

    //code description

    this.id = settings.getDatabaseId();
    this.driver = settings.getDatabaseDriver();
    this.url = settings.getDatabaseUrl();
    this.username = settings.getDatabaseUser();
    this.password = settings.getDatabasePassword();
    this.initConnections = settings.getDatabaseInitialConectionCount();
    this.maxConnections = settings.getDatabaseMaximumConnectionCount();
    if (settings.getDatabaseConnectionProperties() != null)
      this.props = settings.getDatabaseConnectionProperties();
    if (settings.getDatabaseConnectionInstructions() != null)
      this.inst = settings.getDatabaseConnectionInstructions();
    this.transactIsol = settings.getTransactionIsolation();
    this.waitIfBusy = true;
    if (initConnections > maxConnections) initConnections = maxConnections;
    availableConnections = new Vector<Connection>(initConnections);
    busyConnections = new Vector<Connection>();
    int i = 0;
    if (settings.standaloneConnectionAllowed()) {
    	this.standaloneConnection = makeNewStandaloneConnection(settings.getPreparedQueries());
    	i++;
    }
    while (i++ < initConnections) {
    	availableConnections.addElement(makeNewConnection());    
    }     

  }

/*****************************************************************************
 *  Makes the new connection
 */

  private Connection makeNewConnection() throws SQLException {

    //code description

    try {
      Class.forName(driver);
    }
    catch (Exception err) {
      DatabaseLogger.getLogger().fatal("Default Database Pool - no driver.", err);	   
      throw new SQLException("Default Database Pool - no driver.");
    }
    Properties prop = new Properties(this.props);
    if (username != null) prop.put("user",username);
    if (password != null) prop.put("password",password);
    Connection connection = DriverManager.getConnection(url, prop);
    if (this.transactIsol != Connection.TRANSACTION_NONE) {
      connection.setTransactionIsolation(this.transactIsol);
      connection.setAutoCommit(false);
    }
    if (this.inst.length > 0) {
      Statement stmt = connection.createStatement();
      for (int i = 0; i < this.inst.length; i++) {
        stmt.execute(this.inst[i]);
      }
      stmt.close();
    }
    return connection;

  }

/*****************************************************************************
 *  Makes new standalone connection
 */
  
  private Connection makeNewStandaloneConnection (Properties psSQLs) throws SQLException {
	  
	  //local variables
	  
	  PreparedStatement ps;
	  
	  //code description
	  	  
	  Connection con = makeNewConnection();	  
	  if (psSQLs != null) {		 
		  Enumeration names = psSQLs.keys();
		  while (names.hasMoreElements()) {
			  String name = (String)names.nextElement();
			  String SQL = psSQLs.getProperty(name);
			  if (SQL.toUpperCase().startsWith("SELECT")) {
				  ps = con.prepareStatement(SQL, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			  }
			  else {
				  ps = con.prepareStatement(SQL, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE); 
			  }
			  this.preparedStatements.put(name, ps);
		  }		  
	  }
	  return con;
	  
  }
  
/*****************************************************************************
 *  Gets free connection
 */

  private synchronized Connection getConnection() throws SQLException {

    //code description

    if (!availableConnections.isEmpty()) {
      Connection existingConnection = (Connection)availableConnections.lastElement();
      int lastIndex = availableConnections.size() - 1;
      availableConnections.removeElementAt(lastIndex);
      if (existingConnection.isClosed()) {
        notifyAll();
        return getConnection();
      }
      else {
        busyConnections.addElement(existingConnection);
        return existingConnection;
      }
    }
    else {
      if ((totalConnections() < maxConnections) && !connectionPending) {
        makeBackgroundConnection();
      }
      else if (!waitIfBusy) {
    	DatabaseLogger.getLogger().error("Default Database Pool - connection limit reached.");      
        throw new SQLException("Default Database Pool - connection limit reached.");
      }
      try {
        wait();
      }
      catch (InterruptedException ie) {}
      return getConnection();
    }

  }    

/*****************************************************************************
 *  Makes a background connection
 */

  private void makeBackgroundConnection () {

    //code description

    connectionPending = true;
    try {
      Thread connectThread = new Thread(this);
      connectThread.start();
    }
    catch (Exception e)  {}

  }

/*****************************************************************************
 *  Runs a backgroung connection making procedure
 */

  public final void run () {

    //code description

    try {
      Connection connection = makeNewConnection();
      synchronized (this) {
        availableConnections.addElement(connection);
        connectionPending = false;
        notifyAll();
      }
    }
    catch (Exception e) {}

  }

/*****************************************************************************
 *  Makes a busy connection free
 */

  private synchronized void free (Connection connection) {

    //code description

    busyConnections.removeElement(connection);
    availableConnections.addElement(connection);
    notifyAll();

  }

/*****************************************************************************
 *  Returns the total number of connections
 */

  private synchronized int totalConnections () {

	//code description  
	  
    int count = availableConnections.size() + busyConnections.size();
    return standaloneConnection != null ? count++ : count;

  }

/*****************************************************************************
 *  Releases the connection pool
 *
 *  @exception SQLException throwed if a pool cannot be released correctly
 */

  public synchronized void release () throws SQLException {

    //code description
	  
    if (!availableConnections.isEmpty()) {
      Enumeration e = availableConnections.elements();
      while (e.hasMoreElements()) {
        ((Connection)e.nextElement()).close();
      }
    }
    availableConnections.removeAllElements();
    availableConnections = null;
    if (!busyConnections.isEmpty()) {
      Enumeration e = busyConnections.elements();
      while (e.hasMoreElements()) {
        ((Connection)e.nextElement()).close();
      }
    }
    busyConnections.removeAllElements();
    busyConnections = null;
    if (standaloneConnection != null) {
    	standaloneConnection.close();
    	standaloneConnection = null;
    }

  }

/*****************************************************************************
 *  Executes a query
 *
 *  @param SQL - the SQL selecting query
 *
 *  @return the result set of db query executing
 *
 *  @exception SQLException throwed if a query is failed
 */

  public ResultSet executeQuery (String SQL) throws SQLException {

	  //code description
	  
	  return this.executeQuery(SQL, TEMPORARY);

  }

 /*****************************************************************************
  *  Executes a query.
  *
  *  @param SQL the SQL selecting query
  *
  *  @return the result set of db query executing
  *
  *  @exception SQLException throwed if a query is failed
  */

   public ResultSet executeQuery (String SQL, int style) throws SQLException {
	   
	   //local variables

	   Connection con = null;
	   ResultSet result = null;
	   int counter = 0;
	   SQLException ex = null;
	   
	   //code description
	   	
	   while (counter < TRY_COUNT) {
	     try {
	       ex = null;
	       switch (style) {
	       		case STANDALONE: {
	       			synchronized (this.standaloneConnection) {
	       				result = this.standaloneConnection.createStatement().executeQuery(SQL);
	       			}
	       			break;
	       		}
	       		default: {
	       			con = getConnection();
	       			result = con.createStatement().executeQuery(SQL);	       				       			
	       		}
	       }	       	       
	       break;
	     }
	     catch (SQLException err) {
	       DatabaseLogger.getLogger().error("Default Database Pool - cannot do SQL:" + SQL, err);
	       counter++;
	       ex = err;
	     }
	     finally {
	       if (con != null) {
	         free(con);
	       }
	     }
	   }
	   if (ex != null) {
	     throw ex;
	   }
	   return result; 	    	    	  
	   
   }
    
/*****************************************************************************
 *  Executes a query.
 *
 *  @param SQL the SQL selecting query
 *
 *  @return the result set of db query executing
 *
 *  @exception SQLException throwed if a query is failed
 */

  public ResultSet executePreparedQuery (String name, PreparedParameter[] params) throws SQLException {
	  
	  //code description
	  
	  return this.executePreparedQuery(name, params, TEMPORARY);
	  
  }
   
/*****************************************************************************
 *  Executes a query.
 *
 *  @param SQL the SQL selecting query
 *
 *  @return the result set of db query executing
 *
 *  @exception SQLException throwed if a query is failed
 */

  public ResultSet executePreparedQuery (String name, PreparedParameter[] params, int style) throws SQLException {
	  	 	
	  //local variables
	  
	  Connection con = null;
	  PreparedStatement ps = null;
	  int counter = 0;
	  SQLException ex = null;
	  ResultSet result = null;
	  
	  //code description
	  	
	  while (counter < TRY_COUNT) {
		  try {
			  ex = null;		  		  			  			  
			  switch (style) {
	       		case STANDALONE: {
	       			//в случае статического коннекта к серверу тексты prepared-запросов должны быть заданы
	       			//в файле конфигурации, поэтому, здесь мы обращаемся к запросу по имени
	       			ps = this.preparedStatements.get(name);
	       			if (ps == null) {
	       				counter = TRY_COUNT;
	       				throw new SQLException("Default Database Pool - prepared statement" +  name + " not found.");		       				
	       			}
	       			if (ps.getResultSetConcurrency() != ResultSet.CONCUR_READ_ONLY) {
	       				counter = TRY_COUNT;
	       				throw new SQLException("Default Database Pool - this is (" + name + ") not a 'select' statement.");				  
	  			    }	 	       				       			
	       			synchronized (this.standaloneConnection) {
	       				for (int i = 0; i < params.length; i++) params[i].putInto(ps);	       			       					       	
	       				result = ps.executeQuery();	  
	       			}
	       			break;
	       		}
	       		default: {
	       			//в случае множества временных соединений, мы создаем каждый раз новый prepared-запрос
	       			con = getConnection();
	       			ps = con.prepareStatement(name, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
	       			for (int i = 0; i < params.length; i++) params[i].putInto(ps);	       			       					       	
       				result = ps.executeQuery();		       				       			
	       		}
			  }	       	       
			  break;			  			  			  
		  }		  		  	  
		  catch (SQLException err) {
			  DatabaseLogger.getLogger().error("Default Database Pool - cannot do SQL: " + name, err);			  		  
		  	  counter++;
		  	  ex = err;
		  }		
		  finally {
			  if (con != null) {
			     free(con);
			  }
		  }
  	  }
	  if (ex != null) {
	     throw ex;
	  }
	  return result;
	  
  }
  
/*****************************************************************************
 *  Executes a query.
 *
 *  @param SQL the SQL updating query
 *
 *  @return the count of updated rows
 *
 *  @exception SQLException throwed if a query is failed
 */

  public int executePreparedUpdates (String name, PreparedParameter[] params, int style) throws SQLException {
  	  
  	  //local variables
  	  
	  Connection con = null;
  	  PreparedStatement ps = null;
  	  int counter = 0;
  	  int result = 0;
  	  SQLException ex = null;	  
  	  
  	  //code description
  	  	
  	  while (counter < TRY_COUNT) {
  		  try {
  			  ex = null;		  		    			    			 
  			  switch (style) {
  	       		case STANDALONE: {
  	       			//в случае статического коннекта к серверу тексты prepared-запросов должны быть заданы
	       			//в файле конфигурации, поэтому, здесь мы обращаемся к запросу по имени
  	       			ps = this.preparedStatements.get(name);
  	       			if (ps == null) {
  	       				counter = TRY_COUNT;
  	       				throw new SQLException("Default Database Pool - prepared statement" +  name + " not found.");		       				
  	       			}
  	       			if (ps.getResultSetConcurrency() != ResultSet.CONCUR_READ_ONLY) {
  	       				counter = TRY_COUNT;
  	       				throw new SQLException("Default Database Pool - this is (" + name + ") not a 'select' statement.");				  
  	       			}	  	       			
  	       			synchronized (this.standaloneConnection) {
  	       				for (int i = 0; i < params.length; i++) params[i].putInto(ps);	   
  	       				result = ps.executeUpdate();	  
  	       			}
  	       			break;
  	       		}
  	       		default: {
  	       			//в случае множества временных соединений, мы создаем каждый раз новый prepared-запрос
	       			con = getConnection();
	       			ps = con.prepareStatement(name, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
	       			for (int i = 0; i < params.length; i++) params[i].putInto(ps);	       			       					       	
	       			result = ps.executeUpdate();	   	       			  	       			  	       			  	       		
  	       		}
  			  }	       	       
  			  break;			  			  			  
  		  }		  		  	  
  		  catch (SQLException err) {
  			  DatabaseLogger.getLogger().error("Default Database Pool - cannot do SQL: " + name, err); 
  		  	  counter++;
  		  	  ex = err;
  		  }
  		  finally {
  			  if (con != null) {
			     free(con);
			  }
  		  }
      }
  	  if (ex != null) {
  	     throw ex;
  	  }
  	  return result;	  	  	  	
  	  
  }       

/*****************************************************************************
 *  Executes a query.
 *
 *  @param SQL the SQL updating query
 *
 *  @return the count of updated rows
 *
 *  @exception SQLException throwed if a query is failed
 */

  public int executePreparedUpdates (String name, PreparedParameter[] params) throws SQLException {
	  
	  //code description
	  
	  return this.executePreparedUpdates(name, params, TEMPORARY);
	  
  }
  
/*****************************************************************************
 *  Executes a query
 *
 *  @param SQL - the SQL updating query
 *
 *  @return the count of updated rows
 *
 *  @exception SQLException throwed if a query is failed
 */

  public int executeUpdates (String SQL, int style) throws SQLException {

    //local variables

    Connection con = null;
    int result = 0;
    int counter = 0;
    SQLException ex = null;

    //code description

    while (counter < TRY_COUNT) {
      try {
        ex = null;
        switch (style) {
   			case STANDALONE: {
   				synchronized (this.standaloneConnection) {
   					result = this.standaloneConnection.createStatement().executeUpdate(SQL);
   				}
   				break;
   			}
   			default: {
   				con = getConnection();
   				result = con.createStatement().executeUpdate(SQL);  				       			
   			}
        }	               
        break;
      }
      catch (SQLException err) {
    	DatabaseLogger.getLogger().error("Default Database Pool - cannot do SQL:" + SQL, err);
        counter++;
        ex = err;
      }
      finally {
        if (con != null) {
          free(con);
        }
      }
    }
    if (ex != null) {
      throw ex;
    }
    return result;

  }

/*****************************************************************************
  *  Executes a query.
  *
  *  @param SQL the SQL updating query
  *
  *  @return the count of updated rows
  *
  *  @exception SQLException throwed if a query is failed
  */

   public int executeUpdates (String SQL) throws SQLException {
	   
	   //code description
	   
	   return this.executeUpdates(SQL, TEMPORARY);
	   
   }

/*****************************************************************************
 *  Handles batch
 */   
   
  private ArrayList handleBatch (String[] SQLs, Connection con, int counter) throws SQLException {
	  
	  //local variables
	  
	  ArrayList<Object> list = new ArrayList<Object>();	
	  
	  //code description
	  
	  Statement st = con.createStatement();
      for (int i = 0; i < SQLs.length; i++) {
        if (counter == 0) {
        	DatabaseLogger.getLogger().debug(SQLs[i]);
        }
        st.addBatch(SQLs[i]);
      }
      st.executeBatch();
      while (true) {
        boolean isSet = st.getMoreResults(Statement.KEEP_CURRENT_RESULT);
        if (!isSet && st.getUpdateCount() == -1) break;
        if (isSet) {
          list.add(st.getResultSet());
        }
        else {
          list.add(new Integer(st.getUpdateCount()));
        }
      }
      return list;
	  
  }
   
/*****************************************************************************
 *  Executes a query batch.
 *
 *  @param SQLs the SQLs queries
 *
 *  @return the result set of db query executing
 *
 *  @exception SQLException throwed if a query is failed
 */

  public ArrayList executeBatch (String[] SQLs, int style) throws SQLException {

    //local variables

    Connection con = null;
    ArrayList list = new ArrayList();
    int counter = 0;
    SQLException ex = null;

    //code description

    if (SQLs == null) return list;
    while (counter < TRY_COUNT) {
      try {
        ex = null;
        switch (style) {
			case STANDALONE: {
				synchronized (this.standaloneConnection) {
					list = this.handleBatch(SQLs, this.standaloneConnection, counter);
				}
				break;
			}
			default: {
				con = getConnection();
				list = this.handleBatch(SQLs, con, counter);								
			}
        }	                            
        break;
      }
      catch (SQLException err) {    
    	  DatabaseLogger.getLogger().error("Default Database Pool - cannot do SQL batch.", err);
        counter++;
        ex = err;
      }
      finally {
        if (con != null) {
          free(con);
        }
      }
    }
    if (ex != null) {
      throw ex;
    }
    return list;

  }

/*****************************************************************************
 *  Executes a query batch.
 *
 *  @param SQLs the SQLs queries
 *
 *  @return the result set of db query executing
 *
 *  @exception SQLException throwed if a query is failed
 */

  public ArrayList executeBatch (String[] SQLs) throws SQLException {
	  
	  //code description
	   
	   return this.executeBatch(SQLs, TEMPORARY);
	  
  }
  
/*****************************************************************************
 *  Handles transaction
 */  
  
  private ArrayList handleTransaction (String[] SQLs, Connection con, int counter) throws SQLException {
	  
	  //local variables
	  
	  ArrayList<Object> list = new ArrayList<Object>();
	  
	  //code description
	  
	  for (int i = 0; i < SQLs.length; i++) {
		Statement st = con.createStatement();
		if (counter == 0) {
			DatabaseLogger.getLogger().debug(SQLs[i]);
		}
        st.execute(SQLs[i]);
        if (st.getMoreResults()) {
          if (st.getUpdateCount() > -1) {
            list.add(new Integer[st.getUpdateCount()]);
          }
          else {
            list.add(st.getResultSet());
          }
        }
      }
	  return list;
	  
  }
  
/*****************************************************************************
 *  Executes an array of queries in one transaction.
 *
 *  @param SQL the SQL selecting query
 *
 *  @return the result set of db query executing
 *
 *  @exception SQLException throwed if a query is failed
 */

  public ArrayList executeTransaction (String[] SQLs, int style) throws SQLException {

    //local variables

    Connection con = null;
    ArrayList list = new ArrayList();
    int counter = 0;
    SQLException ex = null;

    //code description

    if (SQLs == null) return list;
    while (counter < TRY_COUNT) {
      try {
        ex = null;
        switch (style) {
			case STANDALONE: {
				synchronized (this.standaloneConnection) {
					try {
						list = this.handleTransaction(SQLs, this.standaloneConnection, counter);
						this.standaloneConnection.commit();
					}
					catch (SQLException err) {
						this.standaloneConnection.rollback();
						DatabaseLogger.getLogger().error("Default Database Pool - cannot do SQL transaction.", err);
				        counter++;
				        ex = err;
					}		
					catch (Exception err) {
						this.standaloneConnection.rollback();
						DatabaseLogger.getLogger().error("Default Database Pool - cannot do SQL transaction.", err);
				        ex = new SQLException(err.getMessage());
					}
				}
				break;
			}
			default: {
				con = getConnection();
				list = this.handleTransaction(SQLs, con, counter);	
				con.commit();
			}
        }	                             
        break;
      }
      catch (SQLException err) {
    	if (con != null) {
    		con.rollback();    
    	}
    	DatabaseLogger.getLogger().error("Default Database Pool - cannot do SQL transaction.", err);
        counter++;
        ex = err;
      }
      catch (Exception err) {
    	if (con != null) {  
    		con.rollback();     
    	}
    	DatabaseLogger.getLogger().error("Default Database Pool - cannot do SQL transaction.", err);
        counter = TRY_COUNT;
        ex = new SQLException(err.getMessage());
      }
      finally {
        if (con != null) {
          free(con);
        }
      }
    }
    if (ex != null) {
      throw ex;
    }
    return list;

  }

/*****************************************************************************
 *  Executes an array of queries in one transaction.
 *
 *  @param SQL the SQL selecting query
 *
 *  @return the result set of db query executing
 *
 *  @exception SQLException throwed if a query is failed
 */

  public ArrayList executeTransaction (String[] SQLs) throws SQLException {
	  
	  //code description
	   
	   return this.executeTransaction(SQLs, TEMPORARY);
	  
  }
  
}//the end of DefaultDatabasePool
