/**
 * XML reader to parse server proximity response xml content.
 * The reader shall yield a list of tuples:
 * 	-- <user, user, proximity>
 * or
 * 	-- <task, task, proximity>.
 * @author Arber Borici
 * 2011-11-07
 */

package com.act.on.stc.proxiscientia.visualization.datamanagement;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.act.on.stc.common.utilities.Pair;

public class XMLReader {
	private final String TUPLES = "Tuples";
	private final String TUPLE = "Tuple";
	private final String USER_NAME = "userName";
	private final String TASK_URI = "taskURI";
	private final String TASK_ID = "taskID";
	private final String PROXIMITY = "proximity";
	
	private final String xmlTuples;
	
	public XMLReader(String xmlInput){
		xmlTuples = xmlInput;
	}
	
	/**
	 * Retrieve list of tuples <user, user, proximity> from xml response.
	 * @return list of user-user proximity tuples
	 */
	public List<Pair<Pair<String, String>, Double>> getUserProxTuples() {
		List<Pair<Pair<String, String>, Double>> userTuples = new
				ArrayList<Pair<Pair<String, String>, Double>>();
		
		if (this.xmlTuples.isEmpty()) return userTuples;
		
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(xmlTuples.getBytes()));
			doc.getDocumentElement().normalize();
			
			String rootEl = doc.getDocumentElement().getNodeName();
			// root element should be Tuples
			if (!rootEl.equals(this.TUPLES)) return null;
			
			// collect all the <Tuple>...</Tuple> nodes:
			NodeList nodeLst = doc.getElementsByTagName(this.TUPLE);
		
			// add data to userTuples list:
			for (int s = 0; s < nodeLst.getLength(); s++) {
			    Node fstNode = nodeLst.item(s);
			    
			    if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
			    	// extract first user per their user name:
			    	Element fstElmnt = (Element) fstNode;
			    	NodeList fstNmElmntLst = fstElmnt.getElementsByTagName(this.USER_NAME);
			    	Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
			    	NodeList firstUser = fstNmElmnt.getChildNodes();
			    	String user1 = ((Node) firstUser.item(0)).getNodeValue();
			    	
			    	// extract second user per their user name:
			    	NodeList lstNmElmntLst = fstElmnt.getElementsByTagName(this.USER_NAME);
			    	Element lstNmElmnt = (Element) lstNmElmntLst.item(1);
			    	NodeList secondUser = lstNmElmnt.getChildNodes();
			    	String user2 = ((Node) secondUser.item(0)).getNodeValue();
			    	
			    	// extract proximity:
			    	NodeList prNmElmntLst = fstElmnt.getElementsByTagName(this.PROXIMITY);
			    	Element prNmElmnt = (Element) prNmElmntLst.item(0);
			    	NodeList proxim = prNmElmnt.getChildNodes();
			    	Double proximity = Double.parseDouble(((Node) proxim.item(0)).getNodeValue());

			    	// build the pairs:
			    	Pair<String, String> users = new Pair<String, String>(user1, user2);
			    	Pair<Pair<String, String>, Double> userTuple = new 
			    			Pair<Pair<String, String>, Double>(users, proximity);
			    	
			    	// store the tuple in the list:
			    	userTuples.add(userTuple);
			    }    
			}
		} 
		catch (Exception e) {
		    e.printStackTrace(); 
		}
		
		return userTuples;
	}
	
	/**
	 * Retrieve list of tuples <task, task, proximity> from xml response.
	 * @return list of task-task proximity tuples
	 */
	public List<Pair<Pair<String, String>, Double>> getTaskProxTuples() {
		List<Pair<Pair<String, String>, Double>> taskTuples = new
				ArrayList<Pair<Pair<String, String>, Double>>();
		
		if (this.xmlTuples.isEmpty()) return taskTuples;
		
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(xmlTuples.getBytes()));
			doc.getDocumentElement().normalize();
			
			String rootEl = doc.getDocumentElement().getNodeName();
			// root element should be Tuples
			if (!rootEl.equals(this.TUPLES)) return null;
			
			// collect all the <Tuple>...</Tuple> nodes:
			NodeList nodeLst = doc.getElementsByTagName(this.TUPLE);
		
			// add data to taskTuples list:
			for (int s = 0; s < nodeLst.getLength(); s++) {
			    Node fstNode = nodeLst.item(s);
			    
			    if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
			    	// extract first task per their task uri:
			    	Element fstElmnt = (Element) fstNode;
			    	NodeList fstNmElmntLst = fstElmnt.getElementsByTagName(this.TASK_ID);
			    	Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
			    	NodeList firstTask = fstNmElmnt.getChildNodes();
			    	String task1 = ((Node) firstTask.item(0)).getNodeValue();
			    	
			    	// extract second user per their user name:
			    	NodeList lstNmElmntLst = fstElmnt.getElementsByTagName(this.TASK_ID);
			    	Element lstNmElmnt = (Element) lstNmElmntLst.item(1);
			    	NodeList secondTask = lstNmElmnt.getChildNodes();
			    	String task2 = ((Node) secondTask.item(0)).getNodeValue();
			    	
			    	// extract proximity:
			    	NodeList prNmElmntLst = fstElmnt.getElementsByTagName(this.PROXIMITY);
			    	Element prNmElmnt = (Element) prNmElmntLst.item(0);
			    	NodeList proxim = prNmElmnt.getChildNodes();
			    	Double proximity = Double.parseDouble(((Node) proxim.item(0)).getNodeValue());

			    	// build the pairs:
			    	Pair<String, String> tasks = new Pair<String, String>(task1, task2);
			    	Pair<Pair<String, String>, Double> taskTuple = new 
			    			Pair<Pair<String, String>, Double>(tasks, proximity);
			    	
			    	// store the tuple in the list:
			    	taskTuples.add(taskTuple);
			    }    
			}
		} 
		catch (Exception e) {
		    e.printStackTrace(); 
		}
		
		return taskTuples;
	}
}
