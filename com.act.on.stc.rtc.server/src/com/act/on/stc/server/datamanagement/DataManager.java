package com.act.on.stc.server.datamanagement;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.act.on.stc.common.utilities.Pair;
import com.act.on.stc.server.artifacts.CodeContext;
import com.act.on.stc.server.artifacts.IContext;
import com.act.on.stc.server.artifacts.Task;
import com.act.on.stc.server.artifacts.TaskContext;
import com.act.on.stc.server.artifacts.User;
import com.act.on.stc.server.database.PSQLWrapper;
import com.act.on.stc.server.proximity.backup.ProximityAlgorithm;

public class DataManager {
	private static DataManager fDm= null;
	private PSQLWrapper fDb;
	
	public DataManager(PSQLWrapper dataBaseWrapper) {
		fDb= dataBaseWrapper;
		fDm= this;
	}
	
	public static DataManager getDataManager() throws Exception {
		if (fDm == null) throw new Exception("has not been instantiated");
		return fDm;
	}

	public Response processPostContextRequest(InputStream xmlContent){
		Response response= null;
			
		try {
			XMLParser xmlp= new XMLParser();
			xmlp.parse(xmlContent);
			int wid = getWorkItemDatabaseId(xmlp.getTask());
			int userid = getUserDatabaseId(xmlp.getUser());
			
			IContext[] contexts= xmlp.getContexts();
			if (contexts != null)
				for (IContext context : contexts) {
					storeContext(wid, userid, context);
				}
			
			response = Response.ok().build();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return response;		
	}
	
	public Response processPutContextRequest(InputStream xmlContent) {
		return Response.serverError().entity("No Put Support Implemented").build();
	}

	private int getUserDatabaseId(User user) throws SQLException {
		int userid= fDb.retrieveUserDatabaseId(user.getUUID());
		if (userid == -1) {
			fDb.storeUser(user.getUserName(), user.getUUID());
			userid= fDb.retrieveUserDatabaseId(user.getUUID());
		}
		return userid;
	}

	private int getWorkItemDatabaseId(Task task) throws SQLException {
		int wid= fDb.retrieveWorkItemDatabaseId(task.getUUID());
		if (wid == -1) {
			fDb.storeWorkItem(task.getId(), task.getUUID(), task.getUri());
			wid= fDb.retrieveWorkItemDatabaseId(task.getUUID());
		}
		return wid;
	}

	private void storeContext(int workItemId, int userId, IContext context) throws SQLException {
		if (context.getType() == IContext.CODE_CONTEXT_TYPE) {
			CodeContext c= (CodeContext) context;
			fDb.storeCodeInteraction(userId, workItemId, c.getTimeStamp(), c.getFileName(), c.getClassName(), c.getMethodName(), c.wasEdited(), c.getHandle());
		}
		if (context.getType() == IContext.TASK_CONTEXT_TYPE) {
			TaskContext c= (TaskContext) context;
			int wid= getWorkItemDatabaseId(c.getTask());
			fDb.storeWorkItemInteraction(userId, workItemId, wid, c.getTimeStamp(), c.getField(), c.wasEdited());
		}
	}

	/*
	 * Returns user-user proximity response, given client's TASK request
	 * Arber Borici
	 * 2011-10-20
	 * @param xmlContent: to extract user/task data
	 */
	public Response processGetUserDistanceRequest(InputStream xmlContent) {
		Response response = null;
		
		try {
			XMLParser xmlPrs = new XMLParser();
			xmlPrs.parse(xmlContent);
			
			// extract user from the client's request
			User user = xmlPrs.getUser();
			
			// extract task from the client's request
			Task task = xmlPrs.getTask();	
			
			// get a list of users from the database:
			List<User> users = getUsers();
			
			if (users == null) return null;
			
			ProximityAlgorithm proxemics = new ProximityAlgorithm();
			
			List<Pair<Pair<User, User>, Double>> tuples = 
					new ArrayList<Pair<Pair<User, User>, Double>>();
			/*
			 * For each pair of <user, user2>, compute proximity 
			 */
		//	for (User user1 : users){
				// get all code contexts from db for user1, given the task
		//		List<CodeContext> workingSet1 = getCodeContexts(task, user1);
			
			// get all code contexts from db for user, given the task
			List<CodeContext> workingSet1 = getCodeContexts(task, user);
			for (User user2 : users){
				
				// if user2 is the same user, skip to next:
				if (user2.getUUID().equals(user.getUUID())) continue;
				
				// get all code contexts from db for user2, given the task
				List<CodeContext> workingSet2 = 
						getCodeContexts(task, user2);
				
				// compute proximity for the two developers given
				// the two corresponding code contexts, if not null
				double proximity = 
					proxemics.computeProximity(workingSet1, workingSet2);
				
				Pair<User, User> userPair = new Pair<User, User>(user, user2);
				Pair<Pair<User, User>, Double> userProxTuple = 
						new Pair<Pair<User, User>, Double>(userPair, proximity);
				tuples.add(userProxTuple); // list of tuples <user, user, distance>
			}
		//	}
			
			/*
			 * The response xml string must abide by the following schema:
			 * <Tuples ...>
			 * 		<Tuple>
			 * 			<userName>...</userName>
			 *  		<userName>...</userName>
			 *  		<proximity>...</proximity>
			 * 		</Tuple>
			 * 		...
			 * </Tuples> 
			 */
			
			/* write the tuples in an xml file "tupleXML" */
			
			
			
			/* build the response from the tupleXML file */
			String tupleXML;
			
			XMLWriter xmlWriter = new XMLWriter();
			
			tupleXML = xmlWriter.saveUserProxTuples(tuples);
			
			response = Response.ok()
						.type(MediaType.APPLICATION_XML)
						.entity(tupleXML)
						.build();
			
			/*
			 * Or, the following response is more succinct:
			 */
			//return Response.ok(tupleXML, MediaType.APPLICATION_XML).build();
			
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return response;
	}

	/*
	 * Get a list of the code contexts from the database
	 * for the given user and task
	 * Arber Borici
	 * 2011-10-20
	 */
	private List<CodeContext> getCodeContexts(Task task, User user) 
	throws SQLException, ParseException 
	{
		return fDb.retrieveCodeContexts(task, user);
	}

	/*
	 * Return a list of users from the database
	 * Arber Borici
	 * 2011-10-20
	 */
	private List<User> getUsers() throws SQLException {
		return fDb.retrieveUsers();
	}

	/*
	 * Returns task-task proximity response, given client's USER request
	 * Arber Borici
	 * 2011-10-20
	 */
	public Response processGetTaskDistanceRequest(InputStream xmlContent) {
		Response response = null;
		
		try {
			XMLParser xmlPrs = new XMLParser();
			xmlPrs.parse(xmlContent);
			
			// extract user from
			User user = xmlPrs.getUser();

			// extract task from the client's request
			Task task = xmlPrs.getTask();	
			
			// get a list of tasks from the database
			List<Task> tasks = getTasks();
			
			if (tasks == null) return null;
				
			ProximityAlgorithm proxemics = new ProximityAlgorithm();
			
			List<Pair<Pair<Task, Task>, Double>> tuples = 
					new ArrayList<Pair<Pair<Task, Task>, Double>>();
			/*
			 * For each pair of tasks, compute proximity 
			 */
		//	for (Task task1 : tasks){
			
			// get all code contexts from db for task1, given the user
			List<CodeContext> workingSet1 = getCodeContexts(task, user);
			for (Task task2 : tasks){
				
				if (task2.getUUID().equals(task.getUUID())) continue;
				
				// get all code contexts from db for user2, given the task
				List<CodeContext> workingSet2 = 
						getCodeContexts(task2, user);
				
				// compute proximity for the two developers given
				// the two corresponding code contexts:
				double proximity = 
					proxemics.computeProximity(workingSet1, workingSet2);
				
				Pair<Task, Task> taskPair = new Pair<Task, Task>(task, task2);
				Pair<Pair<Task, Task>, Double> taskProxTuple = 
						new Pair<Pair<Task, Task>, Double>(taskPair, proximity);
				tuples.add(taskProxTuple); // list of tuples <task, task, distance>
			}
		//	}
			
			/* build the response from the tupleXML file */
			String tupleXML;
			
			XMLWriter xmlWriter = new XMLWriter();
			
			tupleXML = xmlWriter.saveTaskProxTuples(tuples);
			
			response = Response.ok()
						.type(MediaType.APPLICATION_XML)
						.entity(tupleXML)
						.build();
			
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return response;
	}

	/*
	 * Return a list of tasks from the database
	 * Arber Borici
	 * 2011-10-20
	 */
	private List<Task> getTasks() throws SQLException {
		return fDb.retrieveTasks();
	}


}
