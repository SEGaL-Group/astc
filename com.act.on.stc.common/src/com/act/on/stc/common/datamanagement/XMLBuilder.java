package com.act.on.stc.common.datamanagement;

import java.io.StringWriter;
import java.net.URI;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

//import com.ibm.team.repository.common.IContributor;
//import com.ibm.team.repository.common.UUID;
//import com.ibm.team.workitem.common.model.IWorkItem;

public class XMLBuilder {
	private Document doc;
	private Element root;

	public XMLBuilder() throws ParserConfigurationException {
		doc= DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		root= doc.createElement(XMLConstants.PROXISCIENTIA);
		doc.appendChild(root);
	}

	public String build() throws TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError {
		Source xmlSource= new DOMSource(doc);
		StringWriter stw= new StringWriter(); 
		Result outputTarget= new StreamResult(stw);

		TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget); 

		return stw.toString(); 
	}

//	public void addUser(IContributor user) {
//
//		Element node= doc.createElement(XMLConstants.USER_TAG);
//
//		if (user == null) return;
//		
//		// NOTE: getUserId returns the username, which is what ProxiScientia requires
//		node.setAttribute(XMLConstants.USERNAME, user.getUserId());
//		node.setAttribute(XMLConstants.UUID, user.getStateId().getUuidValue());
//		root.appendChild(node);
//	}
//
//	public void addTask(IWorkItem wi, UUID uuid, URI uri) {
//		Element node= doc.createElement(XMLConstants.TASK_TAG);
//
//		if (wi == null) return;
//		
//		node.setAttribute(XMLConstants.ID, Integer.toString(wi.getId()));
//		node.setAttribute(XMLConstants.UUID, uuid.getUuidValue());
//		node.setAttribute(XMLConstants.URI, uri.toString());
//
//		root.appendChild(node);
//	}
}
