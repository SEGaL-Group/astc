package com.act.on.stc.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Date;

import com.act.on.stc.server.artifacts.CodeContext;
import com.act.on.stc.server.artifacts.Task;
import com.act.on.stc.server.artifacts.User;

public class PSQLWrapper {
	private Connection fDbc= null;

	/**
	 * Constructor
	 * 
	 * @param databaseConnection an already created connection to a database with a create layout
	 */
	public PSQLWrapper(Connection databaseConnection) {
		fDbc= databaseConnection;
	}
	
	/**
	 * Constructor that creates a database connection
	 * 
	 * @param databaseName
	 * @throws SQLException
	 */
	public PSQLWrapper(String databaseName) throws SQLException {
		fDbc= createConnection(databaseName, "localhost", null, null, 5432, false);
	}
	
	/**
	 * Constructor that creates a database connection
	 * 
	 * @param databaseName
	 * @param host dns or ip of the database sever 
	 * @throws SQLException
	 */
	public PSQLWrapper(String databaseName, String host) throws SQLException {
		fDbc= createConnection(databaseName, host, null, null, 5432, false);
	}
	
	/**
	 * Constructor that creates a database connection
	 * 
	 * @param databaseName
	 * @param host dns or ip of the database sever
	 * @param port port of the database sever
	 * @throws SQLException
	 */
	public PSQLWrapper(String databaseName, String host, int port) throws SQLException {
		fDbc= createConnection(databaseName, host, null, null, port, false);
	}
	
	/**
	 * Constructor that creates a database connection
	 * 
	 * @param databaseName
	 * @param host dns or ip of the database sever
	 * @param username
	 * @param password
	 * @param databasePort port of the database server
	 * @param ssl use ssl?
	 * @throws SQLException
	 */
	public PSQLWrapper(String databaseName, String host, String username, String password, int databasePort, boolean ssl) throws SQLException {
		fDbc= createConnection(databaseName, host, username, password, databasePort, ssl);
	}	
	
	/**
	 * stores a workitem interaction in the database
	 * 
	 * @param userId - the user id as it is used in the database, not the one used in jazz
	 * @param workingOnWorkItemId - the workitem id of the task currently worked on
	 * @param interactedWithWorkItemId - the workitem id of the workitem that has been interacted with
	 * @param timeStamp
	 * @param field - the field that was modified or selected, can be null
	 * @param wasEdited - true if the workitem was modified and false if it was jsut selected
	 * @throws SQLException
	 */
	public void storeWorkItemInteraction(int userId, int workingOnWorkItemId, int interactedWithWorkItemId, Timestamp timeStamp, String field, boolean wasEdited) throws SQLException {
		String query= "INSERT INTO " + PSQLDatabaseCreator.WORKITEMINTERACTIONTABLE + " VALUES ( " 
						+ userId + "," 
						+ workingOnWorkItemId + ","
						+ interactedWithWorkItemId + "," 
						+ (field == null ? "NULL" : "'" + field  + "'") + "," 
						+ "'" + timeStamp.toString() + "'" + ","
						+ "'" + (wasEdited ? "edit" : "select") + "'" +" )";
		
		executeQuery(query);
	}
	

	/**
	 * stores a code interaction in the database
	 * 
	 * @param userId - the user id as it is used in the database, not the one used in jazz
	 * @param workingOnWorkItemId - the workitem id of the task currently worked on as it is used in the database
	 * @param timeStamp
	 * @param fileName - name of the filed interacted with
	 * @param className - name of the class interacted with can be null
	 * @param methodName - name of the method interacted with can be null
	 * @param wasEdited - true if the code was modified false otherwise
	 * @throws SQLException 
	 */
	public void storeCodeInteraction(int userId, int workingOnWorkItemId, Timestamp timeStamp, String fileName, String className, String methodName, boolean wasEdited, String handle) throws SQLException {
		String query= "INSERT INTO " + PSQLDatabaseCreator.CODEINTERACTIONTABLE 
				+ "(userid,taskid,file,class,method,time,interactiontype,handle)" + " VALUES ( " 
				+ userId + "," 
				+ workingOnWorkItemId + ","
				+ "'" + fileName + "'" + "," 
				+ (className == null ? "NULL" : "'" + className + "'") + ","
				+ (methodName == null ? "NULL" : "'" + methodName + "'") + "," 
				+ "'" + timeStamp.toString() + "'" + ","
				+ "'" + (wasEdited ? "edit" : "select") + "'" + ","
				+ "'" + handle + "'" +" )";
		System.err.println(query);
		executeQuery(query);
	}

	/**
	 * storing workitem information that are worked on by users
	 * 
	 * @param workItemId - the id of the workitem
	 * @param workItemUrl - the url pointing to the workitem 
	 * @throws SQLException 
	 */
	public void storeWorkItem(int workItemId, String uuid, String workItemUrl) throws SQLException {
		String query= "INSERT INTO " + PSQLDatabaseCreator.WORKITEMTABLE 
				+ " (uuid, workitemid, url) VALUES ("
				+ "'" + uuid + "'" + ","
				+ workItemId + ","
				+ "'" + workItemUrl + "'" + ")";
		
		executeQuery(query);
	}
	
	/**
	 * storeinf user information
	 * 
	 * @param userName - the user nick
	 * @throws SQLException
	 */
	public void storeUser(String userName, String uuid) throws SQLException {
		String query= "INSERT INTO " + PSQLDatabaseCreator.USERTABLE 
				+ " (uuid, username) VALUES ("
				+ "'" + uuid + "'" + ","
				+ "'" + userName + "'" + ")";
		
		executeQuery(query);
	}
	
	/**
	 * 
	 * @param userId - the jazz user id
	 * @return - returns -1 if there is no userId found
	 * @throws SQLException 
	 */
	public int retrieveUserDatabaseId(String uuid) throws SQLException {
		String query= "SELECT id FROM " + PSQLDatabaseCreator.USERTABLE + " WHERE uuid='" + uuid + "'";
		
		return getIntIdFromDataBase(query);
	}
	
	/**
	 * 
	 * @param uuid
	 * @return
	 * @throws SQLException
	 */
	public int retrieveWorkItemDatabaseId(String uuid) throws SQLException {
		String query= "SELECT id FROM " + PSQLDatabaseCreator.WORKITEMTABLE + " WHERE uuid='" + uuid + "'";
		
		return getIntIdFromDataBase(query);
	}

	/**
	 * creates the database
	 * 
	 * @param overRide - should existing entries be droped
	 * @throws SQLException 
	 */
	public void createDatabase(boolean overRide) throws SQLException {
		PSQLDatabaseCreator dc= new PSQLDatabaseCreator(fDbc);
		dc.create(overRide);
	}
	
	private int getIntIdFromDataBase(String query) throws SQLException {
		Statement sql= fDbc.createStatement();
		ResultSet result= sql.executeQuery(query);
		
		int rowIndex= result.getRow();
		while (result.getRow() < 1) {
			result.next();
			if (result.getRow() == rowIndex) return -1;
			else rowIndex= result.getRow();
		}
		
		int id= result.getInt(1);
		result.close();
		sql.close();
		
		return id;
	}
	
	private void executeQuery(String query) throws SQLException {
		Statement sql= fDbc.createStatement();
		sql.execute(query);
		sql.close();
	}
	
	private Connection createConnection(String databaseName, String host, String username, String password, int databasePort, boolean ssl) throws SQLException {
		String url= "jdbc:postgresql://" + host + "/" + databaseName + ":" + databasePort;
		Properties props= new Properties();
		if (username != null && password != null) {
			props.setProperty("user", username);
			props.setProperty("password", password);
		}
		if (ssl) props.setProperty("ssl", null);
		
		return DriverManager.getConnection(url, props);
	}

	/**
	 * Return a list of users from the database
	 * @author Arber Borici
	 * 2011-10-20
	 */
	public List<User> retrieveUsers() throws SQLException {
		String qry = "SELECT * FROM " + PSQLDatabaseCreator.USERTABLE + ";";
		
		return getUserList(qry);
	}

	/**
	 * Convert result set into a List of users
	 * @author Arber Borici
	 * 2011-10-20
	 */
	private List<User> getUserList(String qry) throws SQLException {
		Statement sql= fDbc.createStatement();
		ResultSet result= sql.executeQuery(qry);
		
		List<User> users = new ArrayList<User>();

		while (result.next()) {
			User usr = new User(result.getString(3),result.getString(2));
			users.add(usr);
		}
		
		result.close();
		sql.close();
		
		return (users.size() > 0 ? users : null);
	}

	/**
	 * Gets a set of code contexts from table codeinteraction
	 * given a user and a task
	 * @author Arber Borici
	 * 2011-10-20
	 * @throws SQLException, ParseException 
	 */
	public List<CodeContext> retrieveCodeContexts(Task task, User user) 
	throws SQLException, ParseException
	{
		// get the "edit" interaction types
		// then the "select" interaction types
		// and merge the two results into one hashset.
		String qry = "SELECT * FROM " + 
			PSQLDatabaseCreator.CODEINTERACTIONTABLE 
			+ " WHERE userid = " 
			+ this.retrieveUserDatabaseId(user.getUUID())
			+ " AND taskid = " 
			+ this.retrieveWorkItemDatabaseId(task.getUUID())
			+ " AND interactiontype = 'edit';";
		
		List<CodeContext> lstEditSelect = new ArrayList<CodeContext>(); 		
		lstEditSelect = getCodeContextSet(qry);

		if (lstEditSelect == null) return null;
		
		qry = "SELECT * FROM " + 
				PSQLDatabaseCreator.CODEINTERACTIONTABLE 
				+ " WHERE userid = " 
				+ this.retrieveUserDatabaseId(user.getUUID())
				+ " AND taskid = " 
				+ this.retrieveWorkItemDatabaseId(task.getUUID())
				+ " AND interactiontype = 'select';";
		
		List<CodeContext> lstSelect = new ArrayList<CodeContext>();  
		lstSelect = getCodeContextSet(qry);
		
		// append lstSelect to lstEditSelect and return the resulting list
		if (lstSelect != null) 
			lstEditSelect.addAll(lstSelect);		
		
		return lstEditSelect;
	}

	// invoked by retrieveCodeContexts(task, user)
	private List<CodeContext> getCodeContextSet(String qry)
	throws SQLException, ParseException 
	{
		Statement sql= fDbc.createStatement();
		ResultSet result= sql.executeQuery(qry);
		
		List<CodeContext> codeContexts = new ArrayList<CodeContext>();

		while (result.next()) {
			boolean isEdit = result.getString(7).equalsIgnoreCase("edit");
			
			SimpleDateFormat formatDate = 
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date formatTimeStamp = formatDate.parse(result.getString(6));				
			Timestamp ts = new Timestamp(formatTimeStamp.getTime());
			
			// db rows 3-8 contain the relevant code context fields.
			CodeContext codeContext = new CodeContext(result.getString(3),
							result.getString(4),
							result.getString(5),
							result.getString(8),
							isEdit,
							ts);
			
			codeContexts.add(codeContext);
		}		
		
		return (codeContexts.size() > 0 ? codeContexts : null);
	}

	/*
	 * Return a list of tasks from the database
	 * Arber Borici
	 * 2011-10-20
	 */
	public List<Task> retrieveTasks() throws SQLException {
		String qry = "SELECT * FROM " + PSQLDatabaseCreator.WORKITEMTABLE + " WHERE workitemid > 0;";
		
		return getTaskList(qry);
	}

	/*
	 * Return all the tasks from the database
	 */
	private List<Task> getTaskList(String qry) throws SQLException {
		Statement sql= fDbc.createStatement();
		ResultSet result= sql.executeQuery(qry);
		
		List<Task> tasks = new ArrayList<Task>();

		while (result.next()) {
			Integer taskID = Integer.parseInt(result.getString(3));
			Task task = new Task(result.getString(2),result.getString(4),
					taskID);
			
			tasks.add(task);
		}
		
		result.close();
		sql.close();
		
		return (tasks.size() > 0 ? tasks : null);
	}
}
