/**
 *   @(#)  DatabasePool.java	   0.3	 06/04/24
 *
 *   Copyright (C) 2005-2006 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.dbs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/*****************************************************************************
 * This interface allows to execute selecting & updating queries by manipulating
 * with pooled database conections. Represents only database pool.
 *
 * @author Amri (Anna A. Semyonova)
 * @version 0.2
 */

public abstract class DatabasePool {

//interface variables
	
public static final int TEMPORARY = 0;
public static final int STANDALONE = 1;
	
//interface methods

/*****************************************************************************
 *  Creates new database connection pool.
 */

  public abstract void create (DatabaseSettings settings) throws SQLException;

/*****************************************************************************
 *  Executes a query.
 *
 *  @param SQL the SQL selecting query
 *
 *  @return the result set of db query executing
 *
 *  @exception SQLException throwed if a query is failed
 */

  public abstract ResultSet executeQuery (String SQL) throws SQLException;

 /*****************************************************************************
  *  Executes a query.
  *
  *  @param SQL the SQL selecting query
  *
  *  @return the result set of db query executing
  *
  *  @exception SQLException throwed if a query is failed
  */

   public abstract ResultSet executeQuery (String SQL, int style) throws SQLException; 
   
/*****************************************************************************
 *  Executes a query.
 *
 *  @param SQL the SQL selecting query
 *
 *  @return the result set of db query executing
 *
 *  @exception SQLException throwed if a query is failed
 */

  public abstract ResultSet executePreparedQuery (String name, PreparedParameter[] params, int style) throws SQLException;    
  
/*****************************************************************************
 *  Executes a query.
 *
 *  @param SQL the SQL selecting query
 *
 *  @return the result set of db query executing
 *
 *  @exception SQLException throwed if a query is failed
 */

  public abstract ResultSet executePreparedQuery (String name, PreparedParameter[] params) throws SQLException;    
    
/*****************************************************************************
 *  Executes a query.
 *
 *  @param SQL the SQL updating query
 *
 *  @return the count of updated rows
 *
 *  @exception SQLException throwed if a query is failed
 */

  public abstract int executeUpdates (String SQL) throws SQLException;
  
 /*****************************************************************************
  *  Executes a query.
  *
  *  @param SQL the SQL updating query
  *
  *  @return the count of updated rows
  *
  *  @exception SQLException throwed if a query is failed
  */

   public abstract int executeUpdates (String SQL, int style) throws SQLException;  
   
/*****************************************************************************
 *  Executes a query.
 *
 *  @param SQL the SQL updating query
 *
 *  @return the count of updated rows
 *
 *  @exception SQLException throwed if a query is failed
 */

   public abstract int executePreparedUpdates (String name, PreparedParameter[] params, int style) throws SQLException;    

/*****************************************************************************
 *  Executes a query.
 *
 *  @param SQL the SQL updating query
 *
 *  @return the count of updated rows
 *
 *  @exception SQLException throwed if a query is failed
 */

   public abstract int executePreparedUpdates (String name, PreparedParameter[] params) throws SQLException;    
      
/*****************************************************************************
 *  Executes a query batch.
 *
 *  @param SQLs the SQLs queries
 *
 *  @return the result set of db query executing
 *
 *  @exception SQLException throwed if a query is failed
 */

  public abstract ArrayList executeBatch (String[] SQLs) throws SQLException;

 /*****************************************************************************
  *  Executes a query batch.
  *
  *  @param SQLs the SQLs queries
  *
  *  @return the result set of db query executing
  *
  *  @exception SQLException throwed if a query is failed
  */

   public abstract ArrayList executeBatch (String[] SQLs, int style) throws SQLException;  
  
/*****************************************************************************
 *  Executes an array of queries in one transaction.
 *
 *  @param SQL the SQL selecting query
 *
 *  @return the result set of db query executing
 *
 *  @exception SQLException throwed if a query is failed
 */

  public abstract ArrayList executeTransaction (String[] SQLs) throws SQLException;

/*****************************************************************************
  *  Executes an array of queries in one transaction.
  *
  *  @param SQL the SQL selecting query
  *
  *  @return the result set of db query executing
  *
  *  @exception SQLException throwed if a query is failed
  */

  public abstract ArrayList executeTransaction (String[] SQLs, int style) throws SQLException;  
  
/*****************************************************************************
 *  Releases the connection pool.
 *
 *  @exception SQLException throwed if a pool cannot be released correctly
 */

  public abstract void release () throws SQLException;

} // DatabasePool ends
