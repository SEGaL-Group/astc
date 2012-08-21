package com.act.on.stc.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class PSQLDatabaseCreator {
	public static final String CREATETABLE= "CREATE TABLE";
	public static final String OPEN= "(";
	public static final String CLOSE= ")";
	
	public static final String USERTABLE= "users";
	public static final String WORKITEMTABLE= "workitem";
	public static final String CODEINTERACTIONTABLE= "codeinteraction";
	public static final String WORKITEMINTERACTIONTABLE= "workiteminteraction";
	
	private String fDatabaseName= null;
	private String fHost= "localhost";
	private int fPort= 5432;
	private boolean fSsl= false;

	private String fUserName= null;
	private String fPassword= null;

	private Connection fConn= null;
	
	public PSQLDatabaseCreator(Connection databaseConnection) {
		fConn= databaseConnection;
	}
	
	public PSQLDatabaseCreator(String databaseName) {
		fDatabaseName= databaseName;
	}
	
	public PSQLDatabaseCreator(String databaseName, String host) {
		fDatabaseName= databaseName;
		fHost= host;
	}
	
	public PSQLDatabaseCreator(String databaseName, String host, int databasePort) {
		fDatabaseName= databaseName;
		fHost= host;
		fPort= databasePort;
	}
	
	public PSQLDatabaseCreator(String databaseName, String host, String username, String password, int databasePort, boolean ssl) {
		fPort= databasePort;
		fHost= host;
		fUserName= username;
		fPassword= password;
		fSsl= ssl;
		fDatabaseName= databaseName;
	}
	
	public void create(boolean overRide) throws SQLException {
		Connection conn= openConnection();
		
		if (!isCreated(conn) || overRide) {
			if (overRide) dropTables(conn);
			
			Statement sql= conn.createStatement();
			// Table containing user information
			sql.execute(CREATETABLE + " " + USERTABLE + " " + OPEN + " id serial, uuid varchar(255), username varchar(255) " + CLOSE);
			sql.execute("CREATE INDEX " + USERTABLE + "_id ON " + USERTABLE + " (id)");
			// Table containing workitem information
			sql.execute(CREATETABLE + " " + WORKITEMTABLE + " " + OPEN + " id serial, uuid varchar(255), workitemid int, url varchar(255), UNIQUE(uuid) " + CLOSE);
			sql.execute("CREATE INDEX " + WORKITEMTABLE + "_id ON " + WORKITEMTABLE + " (id)");
			// Table containing interactions on the source code level
			sql.execute(CREATETABLE + " " + CODEINTERACTIONTABLE + " " + OPEN + " userid int, taskid int, file varchar(255), class varchar(255), method varchar(255), time timestamp, interactiontype varchar(255), handle text" + CLOSE);
			// Table containing interactions with workitems
			sql.execute(CREATETABLE + " " + WORKITEMINTERACTIONTABLE + " " + OPEN + " userid int, taskid int, workitemid int, field varchar(255), time timestamp, interactiontype varchar(255) " + CLOSE);
			sql.close();
		}
		
		closeConnection(conn);
	}

	public void dropTables(Connection conn) throws SQLException {
		Statement sql= conn.createStatement();
		sql.execute("DROP TABLE IF EXISTS " + USERTABLE);
		sql.execute("DROP TABLE IF EXISTS " + WORKITEMTABLE);
		sql.execute("DROP TABLE IF EXISTS " + CODEINTERACTIONTABLE);
		sql.execute("DROP TABLE IF EXISTS " + WORKITEMINTERACTIONTABLE);
		sql.close();
	}

	/**
	 * Just testing if the USER table exists to determine whether everything was already created
	 * 
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private boolean isCreated(Connection conn) throws SQLException {		
		Statement sql= conn.createStatement();
		ResultSet results= sql.executeQuery("SELECT COUNT(relname) FROM pg_class WHERE relname='" + USERTABLE + "'");

		if (!results.isFirst()) results.next();
		int count= results.getInt(1);
		results.close();
		sql.close();
		
		return count > 0;
	}

	private void closeConnection(Connection conn) throws SQLException {
		if (fConn == null) conn.close(); 		
	}

	private Connection openConnection() throws SQLException {
		if (fConn != null) {
			return fConn;
		}
		
		String url= "jdbc:postgresql://" + fHost + "/" + fDatabaseName + ":" + fPort;
		Properties props= new Properties();
		if (fUserName != null && fPassword != null) {
			props.setProperty("user", fUserName);
			props.setProperty("password", fPassword);
		}
		if (fSsl) props.setProperty("ssl", String.valueOf(fSsl));
		
		return DriverManager.getConnection(url, props);
	}
}
