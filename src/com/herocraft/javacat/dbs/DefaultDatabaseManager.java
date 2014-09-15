/**
 *   @(#)  DefaultDatabaseManager.java	   0.3	 08/02/08
 *
 *   Copyright (C) 2004-2008 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.dbs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

/*****************************************************************************
 * The class which has responsibility for interaction with database. The
 * only prime task of this class is to load database pools specified by their ids
 * and to deliver them the queries.
 *
 * @author Amri (Anna A. Semyonova)
 * @version 0.2
 */

class DefaultDatabaseManager implements DatabaseManager {

//class variables

private HashMap<String, DatabasePool> pools = new HashMap<String, DatabasePool>();

//class methods

/*****************************************************************************
 *  The constructor
 *
 *  @exception DBHandlerException
 */

  DefaultDatabaseManager (DatabaseSettings[] settings) throws SQLException {

    //local variables

    DatabasePool pool = null;

    //code description

    if (settings == null) throw new NullPointerException();
    if (settings.length == 0) throw new IllegalArgumentException();
    for (int i = 0; i < settings.length; i++) {
      pool = settings[i].getDatabasePool();
      pool.create(settings[i]);
      pools.put(settings[i].getDatabaseId(), pool);  
    }

  }

/*****************************************************************************
 *  Executes a query
 *
 *  @param SQL
 *
 *  @return the result set of db query executing
 *
 *  @exception SQLException
 */

  public ResultSet executeQuery (String SQL, String id) throws SQLException {

    //code description

    DatabasePool pool = (DatabasePool)pools.get(id);
    return pool.executeQuery(SQL);

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

  public ResultSet executeQuery (String SQL, String id, int style) throws SQLException {
	  
	  //code description

	  DatabasePool pool = (DatabasePool)pools.get(id);
	  return pool.executeQuery(SQL, style);
	  
  }
  
/*****************************************************************************
 *  Executes a query
 *
 *  @param SQL
 *
 *  @return the result set of db query executing
 *
 *  @exception SQLException
 */

  public int executeUpdates (String SQL, String id) throws SQLException {

    //code description

    DatabasePool pool = (DatabasePool)pools.get(id);
    return pool.executeUpdates(SQL);

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

  public int executeUpdates (String SQL, String id, int style) throws SQLException {
	  
	  	//code description

	    DatabasePool pool = (DatabasePool)pools.get(id);
	    return pool.executeUpdates(SQL, style);
	  
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

  public ArrayList executeBatch (String[] SQLs, String id) throws SQLException {

    //code description

    DatabasePool pool = (DatabasePool)pools.get(id);
    return pool.executeBatch(SQLs);

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

  public ArrayList executeBatch (String[] SQLs, String id, int style) throws SQLException {
	  
	  	//code description

	    DatabasePool pool = (DatabasePool)pools.get(id);
	    return pool.executeBatch(SQLs, style);
	  
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

  public ArrayList executeTransaction (String[] SQLs, String id) throws SQLException {

    //code description

    DatabasePool pool = (DatabasePool)pools.get(id);
    return pool.executeTransaction(SQLs);

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

  public ArrayList executeTransaction (String[] SQLs, String id, int style) throws SQLException {
	  
	  	//code description

	    DatabasePool pool = (DatabasePool)pools.get(id);
	    return pool.executeTransaction(SQLs, style);
	  
  }
  
/*****************************************************************************
 *  Executes a prepared query.
 *
 *  @param SQL the SQL selecting query
 *
 *  @return the result set of db query executing
 *
 *  @exception SQLException throwed if a query is failed
 */
    
  public ResultSet executePreparedQuery (String name, PreparedParameter[] params, String id, int style) throws SQLException {
	  
	  //code description
	  
	  DatabasePool pool = (DatabasePool)pools.get(id);
	  return pool.executePreparedQuery(name, params, style);
	  
  }
    
/*****************************************************************************
 *  Executes a prepared query.
 *
 *  @param SQL the SQL selecting query
 *
 *  @return the result set of db query executing
 *
 *  @exception SQLException throwed if a query is failed
 */
      
  public ResultSet executePreparedQuery (String name, PreparedParameter[] params, String id) throws SQLException {
  	  
	  //code description
  	  
  	  DatabasePool pool = (DatabasePool)pools.get(id);
  	  return pool.executePreparedQuery(name, params);
  	  
  }  
  
/*****************************************************************************
 *  Executes a prepared query.
 *
 *  @param SQL the SQL selecting query
 *
 *  @return the result set of db query executing
 *
 *  @exception SQLException throwed if a query is failed
 */
    
  public int executePreparedUpdates (String name, PreparedParameter[] params, String id, int style) throws SQLException {
	  
	  //code description
	  	  
	  DatabasePool pool = (DatabasePool)pools.get(id);
	  return pool.executePreparedUpdates(name, params, style);
	  
  }
  
/*****************************************************************************
 *  Executes a prepared query.
 *
 *  @param SQL the SQL selecting query
 *
 *  @return the result set of db query executing
 *
 *  @exception SQLException throwed if a query is failed
 */
      
  public int executePreparedUpdates (String name, PreparedParameter[] params, String id) throws SQLException {
  	  
	  //code description
  	  	  
  	  DatabasePool pool = (DatabasePool)pools.get(id);
  	  return pool.executePreparedUpdates(name, params);
  	  
  }  
  
/*****************************************************************************
 *  Releases db handler
 */

  public void release () throws SQLException {

    //code description

    Iterator pools = this.pools.keySet().iterator();
    while (pools.hasNext()) {
      String id = (String)pools.next();
      DatabasePool pool = (DatabasePool)this.pools.get(id);
      pool.release();
    }

  }

} // DefaultDatabaseManager ends
