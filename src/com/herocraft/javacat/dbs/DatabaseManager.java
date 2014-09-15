/**
 *   @(#)  DatabaseManager.java	   0.1	 08/02/08
 *
 *   Copyright (C) 2005-2008 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.dbs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/*****************************************************************************
 * The interface which has responsibility for interaction with database.
 * Returned by DatabaseManagerFactory methods. It should be used external classes
 * for access to dbs library functions.
 * <br><br>
 * <tt>DatabaseManager manager = DatabaseManagerFactory.getDatabaseManager();</tt>
 * <br>
 * <tt>String SQL = "SELECT * FROM testtabl;";</tt>
 * <br>
 * <tt>manager.executeQuery(SQL, "demodb");</tt>
 *
 * @author Amri (Anna A. Semyonova)
 * @version 0.1
 */

public interface DatabaseManager {

//interface methods

/*****************************************************************************
 *  Executes a query.
 *
 *  @param SQL the SQL selecting query
 *  @param id the unigue id of database the query is oriented to
 *
 *  @return the result set of db query executing
 *
 *  @exception SQLException throwed if a query is failed
 */

  public ResultSet executeQuery (String SQL, String id) throws SQLException;
  
/*****************************************************************************
 *  Executes a query.
 *
 *  @param SQL the SQL selecting query
 *
 *  @return the result set of db query executing
 *
 *  @exception SQLException throwed if a query is failed
 */

  public ResultSet executeQuery (String SQL, String id, int style) throws SQLException;    

/*****************************************************************************
 *  Executes a query.
 *
 *  @param SQL the SQL updating query
 *  @param id the unigue id of database the query is oriented to
 *
 *  @return the count of updated rows
 *
 *  @exception SQLException throwed if a query is failed
 */

  public int executeUpdates (String SQL, String id) throws SQLException;
  
/*****************************************************************************
 *  Executes a query.
 *
 *  @param SQL the SQL updating query
 *
 *  @return the count of updated rows
 *
 *  @exception SQLException throwed if a query is failed
 */

  public int executeUpdates (String SQL, String id, int style) throws SQLException;    

/*****************************************************************************
 *  Executes a query batch.
 *
 *  @param SQLs the SQLs queries
 *
 *  @return the result set of db query executing
 *
 *  @exception SQLException throwed if a query is failed
 */

  public ArrayList executeBatch (String[] SQLs, String id) throws SQLException;
  
/*****************************************************************************
 *  Executes a query batch.
 *
 *  @param SQLs the SQLs queries
 *
 *  @return the result set of db query executing
 *
 *  @exception SQLException throwed if a query is failed
 */

  public ArrayList executeBatch (String[] SQLs, String id, int style) throws SQLException;    

/*****************************************************************************
 *  Executes an array of queries in one transaction.
 *
 *  @param SQL the SQL selecting query
 *
 *  @return the result set of db query executing
 *
 *  @exception SQLException throwed if a query is failed
 */

  public ArrayList executeTransaction (String[] SQLs, String id) throws SQLException;
  
/*****************************************************************************
 *  Executes an array of queries in one transaction.
 *
 *  @param SQL the SQL selecting query
 *
 *  @return the result set of db query executing
 *
 *  @exception SQLException throwed if a query is failed
 */

  public ArrayList executeTransaction (String[] SQLs, String id, int style) throws SQLException;  
  
/*****************************************************************************
 *  Executes a prepared query.
 *
 *  @param SQL the SQL selecting query
 *
 *  @return the result set of db query executing
 *
 *  @exception SQLException throwed if a query is failed
 */
    
  public ResultSet executePreparedQuery (String name, PreparedParameter[] params, String id) throws SQLException;  
  
/*****************************************************************************
 *  Executes a prepared query.
 *
 *  @param SQL the SQL selecting query
 *
 *  @return the result set of db query executing
 *
 *  @exception SQLException throwed if a query is failed
 */
  
  public ResultSet executePreparedQuery (String name, PreparedParameter[] params, String id, int style) throws SQLException;
  
/*****************************************************************************
 *  Executes a prepared query.
 *
 *  @param SQL the SQL selecting query
 *
 *  @return the result set of db query executing
 *
 *  @exception SQLException throwed if a query is failed
 */
  
  public int executePreparedUpdates (String name, PreparedParameter[] params, String id, int style) throws SQLException;

/*****************************************************************************
 *  Executes a prepared query.
 *
 *  @param SQL the SQL selecting query
 *
 *  @return the result set of db query executing
 *
 *  @exception SQLException throwed if a query is failed
 */
    
  public int executePreparedUpdates (String name, PreparedParameter[] params, String id) throws SQLException;
    
/*****************************************************************************
 *  Releases db manager.
 */

  public void release () throws SQLException;

} // DatabaseManager ends
