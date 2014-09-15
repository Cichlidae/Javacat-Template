/**
 *   @(#)  DatabaseSettings.java   0.3	 08/02/08
 *
 *   Copyright (C) 2005-2008 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.dbs;

import java.sql.Connection;
import java.util.Properties;

/*****************************************************************************
 * It is a database access settings descriptor: driver, url, user, password,
 * pool initial parameters. It's used by a database manager to make a database pool.
 *
 * @author Amri (Anna A. Semyonova)
 * @version 0.2
 */

public abstract class DatabaseSettings {

//interface methods

/****************************************************************************
 * Gets a database name.
 *
 * @return a database unique id in order to give a database manager the chance
 * to distinguish one database from another
 */

  public abstract String getDatabaseId ();

/****************************************************************************
 * Gets a database path.
 *
 * @return a database URL
 */

  public abstract String getDatabaseUrl ();

/****************************************************************************
 * Gets a database driver.
 *
 * @return a database driver
 */

  public String getDatabaseDriver () {

    return "sun.jdbc.odbc.JdbcOdbcDriver";

  }

/****************************************************************************
 * Gets a database user.
 *
 * @return a database user login
 */

  protected String getDatabaseUser () {

    return null;

  }

/****************************************************************************
 * Gets a database password.
 *
 * @return a database password
 */

  protected String getDatabasePassword () {

    return null;

  }

/****************************************************************************
 * Gets a database initial connection count.
 *
 * @return the initial count of connections
 */

   public int getDatabaseInitialConectionCount () {

     return 1;

  }

/****************************************************************************
 * Gets a database maximum connection count.
 *
 * @return the maximum count of connections
 */

   public int getDatabaseMaximumConnectionCount () {

     return 1;

   }

/****************************************************************************
 *  Gets database connection properties
 */

  public Properties getDatabaseConnectionProperties () {

    return new Properties();

  }

/****************************************************************************
 *  Gets database connection instructions
 */

  public String[] getDatabaseConnectionInstructions () {

      return new String[0];

  }

/****************************************************************************
 * Gets a pool handler instance for a describing database.
 *
 * @return a database pool instanse
 */

   protected DatabasePool getDatabasePool () {

     return new DefaultDatabasePool();

   }

/****************************************************************************
 *  Gets the transaction isolation level for db connections. If method returns
 *  a value different from TRANSACTION_NONE it automatically means that
 *  auto-commit mode of connection must be set in false.
 */

  public int getTransactionIsolation () {

    //code description

    return Connection.TRANSACTION_NONE;

  }
  
/******************************************************************************
 *  Returns true if allowed the standalone connection
 */  
  
   public boolean standaloneConnectionAllowed () {
	   
	   //code description
	   
	   return false;
	   
   }

/******************************************************************************
 *  Returns prepared query map
 */   
   
   public Properties getPreparedQueries () {
	   
	   //code description
	   
	   return null;
	   
   }
   
} // the end of DatabaseSettings class
