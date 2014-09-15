/**
 * 
 */

package com.herocraft.javacat.dbs;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.sql.SQLException;

/**
 * @author Nadi Shakti
 *
 */

public class PreparedParameter {
	
	/* The parameter description of prepared statement */
	
	private int TYPE;
	private int POSITION;
	private Object VALUE;		
	
	/*The constructors*/	
	private PreparedParameter (String position) throws SQLException {
		
		//code description
		
		if (position == null) throw new NullPointerException();
		try {		
			this.POSITION = Integer.parseInt(position);
		}
		catch (NumberFormatException err) {
			throw new SQLException("Prepared Parameter - incorrect position of parameter.");
		};	
		
	}
	
	public PreparedParameter (String type, String position, String value) throws SQLException {
		
		//code description
			
		this(position);
		if (type == null || value == null) throw new NullPointerException();
		if ("BIGINT".equals(type)) {
			this.TYPE = Types.BIGINT;	
			this.VALUE = new Long(value);
		}
		else if ("BOOLEAN".equals(type)) {
			this.TYPE = Types.BOOLEAN;	
			this.VALUE = new Boolean(value);
		}
		else if ("DOUBLE".equals(type)) {
			this.TYPE = Types.DOUBLE;
			this.VALUE = new Double(value);
		}
		else if ("FLOAT".equals(type)) {
			this.TYPE = Types.FLOAT;
			this.VALUE = new Float(value);
		}
		else if ("INTEGER".equals(type)) {
			this.TYPE = Types.INTEGER;	
			this.VALUE = new Integer(value);
		}
		else if ("SMALLINT".equals(type)) {
			this.TYPE = Types.SMALLINT;	
			this.VALUE = new Short(value);
		}
		else if ("TINYINT".equals(type)) {
			this.TYPE = Types.TINYINT;
			this.VALUE = new Byte(value);
		}
		else if ("VARCHAR".equals(type)) {
			this.TYPE = Types.VARCHAR;	
			this.VALUE = value;
		}		
		else {
			this.TYPE = Types.OTHER;
			this.VALUE = value;
		}			
		
	}
	
	public PreparedParameter (String type, String position, byte[] value) throws SQLException {
		
		//code description
		
		this(position);
		if (type == null || value == null) throw new NullPointerException();
		if ("BLOB".equals(type)) {
			this.TYPE = Types.BLOB;
			this.VALUE = value;
		}
		else if ("BINARY".equals(type)) {
			this.TYPE = Types.BINARY;
			this.VALUE = value;
		}
		else {
			this.TYPE = Types.OTHER;
			this.VALUE = value;
		}		
		
	}
	
	public int getType () {
		
		//code description
		
		return this.TYPE;
		
	}
	
	public int getPosition () {
		
		//code description
		
		return this.POSITION;
		
	}
	
	public Object getValue () {
		
		//code description
		
		return this.VALUE;
		
	}
	
	protected void putInto (PreparedStatement ps) throws SQLException {
		
		//code description
		
		switch (this.TYPE) {
	  	case Types.BIGINT: ps.setLong(this.POSITION, ((Long)this.VALUE).longValue()); break;
		case Types.BOOLEAN: ps.setBoolean(this.POSITION, ((Boolean)this.VALUE).booleanValue()); break;
		case Types.DOUBLE: ps.setDouble(this.POSITION, ((Double)this.VALUE).doubleValue()); break;
		case Types.FLOAT: ps.setFloat(this.POSITION, ((Float)this.VALUE).floatValue()); break;
		case Types.INTEGER: ps.setInt(this.POSITION, ((Integer)this.VALUE).intValue()); break;
		case Types.SMALLINT: ps.setShort(this.POSITION, ((Short)this.VALUE).shortValue()); break;
		case Types.TINYINT: ps.setByte(this.POSITION, ((Byte)this.VALUE).byteValue()); break;
		case Types.VARCHAR:	ps.setString(this.POSITION, (String)this.VALUE); break;
		case Types.BLOB:
		case Types.BINARY: {			
			ps.setBytes(this.POSITION, (byte[])this.VALUE); 
			break;
		}
		default: ps.setObject(this.POSITION, this.VALUE);
	  }		
		
	}

}
