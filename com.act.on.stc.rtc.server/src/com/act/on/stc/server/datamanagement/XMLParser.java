package com.act.on.stc.server.datamanagement;

import java.io.InputStream;
import java.sql.Timestamp;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.act.on.stc.common.datamanagement.XMLConstants;
import com.act.on.stc.server.artifacts.CodeContext;
import com.act.on.stc.server.artifacts.IContext;
import com.act.on.stc.server.artifacts.Task;
import com.act.on.stc.server.artifacts.TaskContext;
import com.act.on.stc.server.artifacts.User;

public class XMLParser {
	private Task fTask= null;
	private User fUser= null;
	private IContext[] fContexts= null;

	public void parse(InputStream xmlContent) throws Exception {
		Document doc= DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlContent);
		fTask= this.parseTask(doc);
		fUser= this.parseUser(doc);
		fContexts= this.parseContext(doc);
	}
	
	private IContext[] parseContext(Document doc) {
		NodeList contextNodes= doc.getElementsByTagName(XMLConstants.CONTEXT_TAG);
		int nodeNumber= contextNodes.getLength();
		
		if (nodeNumber > 0) {
			IContext[] contexts= new IContext[nodeNumber];
		
			for (int i = 0; i < nodeNumber; i++) {
				Node contextNode= contextNodes.item(i);
				NamedNodeMap attributeMap= contextNode.getAttributes();
				if (attributeMap.getNamedItem(XMLConstants.TYPE).getNodeValue().equals(XMLConstants.TASK)) {
					contexts[i]= parseTaskContext(attributeMap);
				}
				if (attributeMap.getNamedItem(XMLConstants.TYPE).getNodeValue().equals(XMLConstants.CODE)) {
					contexts[i]= parseCodeContext(attributeMap);
				}
			}
			return contexts;
		}
		return null;
	}

	private IContext parseCodeContext(NamedNodeMap attributeMap) {
		String fileName= attributeMap.getNamedItem(XMLConstants.FILENAME).getNodeValue();
		boolean edited= Boolean.parseBoolean(attributeMap.getNamedItem(XMLConstants.EDITED).getNodeValue());
		Timestamp time= Timestamp.valueOf(attributeMap.getNamedItem(XMLConstants.TIME).getNodeValue());
		String handle= attributeMap.getNamedItem(XMLConstants.STRUCTURAL_HANDLE).getNodeValue();
		
		String className= null;
		if (attributeMap.getNamedItem(XMLConstants.CLASSNAME) != null) className= attributeMap.getNamedItem(XMLConstants.CLASSNAME).getNodeValue();
		String methodName= null;
		if (attributeMap.getNamedItem(XMLConstants.METHODNAME) != null) methodName= attributeMap.getNamedItem(XMLConstants.METHODNAME).getNodeValue();
		
		return new CodeContext(fileName, className, methodName, handle, edited, time);
	}

	private IContext parseTaskContext(NamedNodeMap attributeMap) {
		String uuid= attributeMap.getNamedItem(XMLConstants.UUID).getNodeValue();
		int taskId= Integer.parseInt(attributeMap.getNamedItem(XMLConstants.TASK_ID).getNodeValue());
		Timestamp time= Timestamp.valueOf(attributeMap.getNamedItem(XMLConstants.TIME).getNodeValue());

		String uri= null;
		if (attributeMap.getNamedItem(XMLConstants.URI) != null) uri= attributeMap.getNamedItem(XMLConstants.URI).getNodeValue();
		
		String field= null;
		if (attributeMap.getNamedItem(XMLConstants.FIELD) != null) field= attributeMap.getNamedItem(XMLConstants.FIELD).getNodeValue();
		
		boolean edited= Boolean.parseBoolean(attributeMap.getNamedItem(XMLConstants.EDITED).getNodeValue());
		
		return new TaskContext(new Task(uuid,uri,taskId), time, field, edited);
	}

	private User parseUser(Document doc) throws Exception {
		NodeList userNodes= doc.getElementsByTagName(XMLConstants.USER_TAG);
		
		if (userNodes.getLength() > 1) {
			throw new Exception("too many users " + userNodes.getLength());
		}
		if (userNodes.getLength() == 1) {
			NamedNodeMap attrs= userNodes.item(0).getAttributes();
			
			String usern= attrs.getNamedItem(XMLConstants.USERNAME).getNodeValue();
			String uuid= attrs.getNamedItem(XMLConstants.UUID).getNodeValue();
			
			return new User(usern, uuid);
		}
		return null;
	}

	private Task parseTask(Document doc) throws Exception {
		NodeList userNodes= doc.getElementsByTagName(XMLConstants.TASK_TAG);
		
		if (userNodes.getLength() > 1) {
			throw new Exception("too many users " + userNodes.getLength());
		}
		if (userNodes.getLength() == 1) {
			NamedNodeMap attrs= userNodes.item(0).getAttributes();
			
			int id= Integer.parseInt(attrs.getNamedItem(XMLConstants.ID).getNodeValue());
			String uuid= attrs.getNamedItem(XMLConstants.UUID).getNodeValue();
			String uri= attrs.getNamedItem(XMLConstants.URI).getNodeValue();
			
			return new Task(uuid, uri, id);
		}
		return null;	
	}

	public Task getTask() {
		return fTask;
	}

	public User getUser() {
		return fUser;
	}

	public IContext[] getContexts() {
		return fContexts;
	}

}
