/**
 * Creates XML files per some predefined schema.
 * @author Arber Borici
 * 2011-10-28
 */

package com.act.on.stc.server.datamanagement;

import java.io.StringWriter;
import java.util.List;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.act.on.stc.common.utilities.Pair;
import com.act.on.stc.server.artifacts.Task;
import com.act.on.stc.server.artifacts.User;

public class XMLWriter {
	private final String TUPLES = "Tuples";
	private final String TUPLE = "Tuple";
	private final String USER_NAME = "userName";
	private final String TASK_URI = "taskURI";
	private final String TASK_ID = "taskID";
	private final String PROXIMITY = "proximity";
	private final String EMPTY = "";
	private final String NEW_LINE = "\n";
	private final String TAB = "\t";
	
	private StringWriter tuplesXML;

	public XMLWriter(){
		tuplesXML = new StringWriter();
	}

	/*
	 * Save the <user, user, proximity> tuples in an xml format
	 */
	public String saveUserProxTuples(List<Pair<Pair<User, User>, Double>> tuples) 
			throws Exception {
		
		String responseXML = null;
		
		// Create a XMLOutputFactory
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		
		// Create XMLEventWriter using the tuplesXML file
		XMLEventWriter eventWriter = outputFactory
				.createXMLEventWriter(this.tuplesXML);
		
		// Create an EventFactory
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		XMLEvent end = eventFactory.createDTD(this.NEW_LINE);
		
		// Create and write Start Tag
		StartDocument startDocument = eventFactory.createStartDocument();
		eventWriter.add(startDocument);

		// add new line:
		eventWriter.add(eventFactory.createDTD(this.NEW_LINE));
					
		// Create Tuples open tag: <Tuples> .... 
		StartElement configStartElement = 
				eventFactory.createStartElement(
						this.EMPTY, this.EMPTY, this.TUPLES);
		eventWriter.add(configStartElement);
		eventWriter.add(end);
		
		/* Iterate over the tuple list.
		 * For each tuple, write it as:
		 * 	<Tuple>
		 * 		<userName>...</userName>
		 * 		<userName>...</userName>
		 * 		<proximity>...</proximity>
		 * </Tuple> 
		 */
		for (Pair<Pair<User, User>, Double> tuple : tuples){
			
			// add tab:
			eventWriter.add(eventFactory.createDTD(this.TAB));
						
			// Create Tuple open tag: <Tuple> .... 
			StartElement tupleStartElement = 
					eventFactory.createStartElement(
							this.EMPTY, this.EMPTY, this.TUPLE);
			eventWriter.add(tupleStartElement);
			eventWriter.add(end);
			
			// write the individual tags: user, user, proximity:
			createNode(eventWriter, this.USER_NAME,
					tuple.getFirstElem().getFirstElem().getUserName());
			
			createNode(eventWriter, this.USER_NAME,
					tuple.getFirstElem().getSecondElem().getUserName());
			
			createNode(eventWriter, this.PROXIMITY, 
					Double.toString(tuple.getSecondElem()));
			
			// add tab:
			eventWriter.add(eventFactory.createDTD(this.TAB));
			
			// create Tuple end tag: .... </Tuple>
			eventWriter.add(eventFactory.createEndElement(
								this.EMPTY, this.EMPTY, this.TUPLE));
			
			// add new line:
			eventWriter.add(eventFactory.createDTD(this.NEW_LINE));
		}

		// create Tuples end tag: .... </Tuples>
		eventWriter.add(eventFactory.createEndElement(
				this.EMPTY, this.EMPTY, this.TUPLES));
		
		// create and write End Tag
		eventWriter.add(end);
		eventWriter.add(eventFactory.createEndDocument());
		eventWriter.close();
		
		responseXML = String.valueOf(tuplesXML);
		
		return responseXML;
	}

	/*
	 * Save the <task, task, proximity> tuples in an xml format
	 */
	public String saveTaskProxTuples(List<Pair<Pair<Task, Task>, Double>> tuples) 
			throws Exception {

		String responseXML = null;
		
		// Create a XMLOutputFactory
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		
		// Create XMLEventWriter using the tuplesXML file
		XMLEventWriter eventWriter = outputFactory
				.createXMLEventWriter(this.tuplesXML);
		
		// Create an EventFactory
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		XMLEvent end = eventFactory.createDTD(this.NEW_LINE);
		
		// Create and write Start Tag
		StartDocument startDocument = eventFactory.createStartDocument();
		eventWriter.add(startDocument);

		// add new line:
		eventWriter.add(eventFactory.createDTD(this.NEW_LINE));
					
		// Create Tuples open tag: <Tuples> .... 
		StartElement configStartElement = 
				eventFactory.createStartElement(
						this.EMPTY, this.EMPTY, this.TUPLES);
		eventWriter.add(configStartElement);
		eventWriter.add(end);
		
		/* Iterate over the tuple list.
		 * For each tuple, write it as:
		 * 	<Tuple>
		 * 		<userName>...</userName>
		 * 		<userName>...</userName>
		 * 		<proximity>...</proximity>
		 * </Tuple> 
		 */
		for (Pair<Pair<Task, Task>, Double> tuple : tuples){
			
			// add tab:
			eventWriter.add(eventFactory.createDTD(this.TAB));
						
			// Create Tuple open tag: <Tuple> .... 
			StartElement tupleStartElement = 
					eventFactory.createStartElement(
							this.EMPTY, this.EMPTY, this.TUPLE);
			eventWriter.add(tupleStartElement);
			eventWriter.add(end);
			
			// write the individual tags: task-id, task-id, proximity:
			createNode(eventWriter, this.TASK_ID,
					Integer.toString(tuple.getFirstElem().getFirstElem().getId()));
			
			createNode(eventWriter, this.TASK_ID,
					Integer.toString(tuple.getFirstElem().getSecondElem().getId()));
			
			createNode(eventWriter, this.PROXIMITY, 
					Double.toString(tuple.getSecondElem()));
			
			// add tab:
			eventWriter.add(eventFactory.createDTD(this.TAB));
			
			// create Tuple end tag: .... </Tuple>
			eventWriter.add(eventFactory.createEndElement(
								this.EMPTY, this.EMPTY, this.TUPLE));
			
			// add new line:
			eventWriter.add(eventFactory.createDTD(this.NEW_LINE));
		}

		// create Tuples end tag: .... </Tuples>
		eventWriter.add(eventFactory.createEndElement(
				this.EMPTY, this.EMPTY, this.TUPLES));
		
		// create and write End Tag
		eventWriter.add(end);
		eventWriter.add(eventFactory.createEndDocument());
		eventWriter.close();
		
		responseXML = String.valueOf(tuplesXML);
		
		return responseXML;
	}
	
	/*
	 * Create an xml node: <name>value</name>
	 */
	private void createNode(XMLEventWriter eventWriter, String name,
			String value) throws XMLStreamException {

		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		XMLEvent end = eventFactory.createDTD(this.NEW_LINE);
		XMLEvent tab = eventFactory.createDTD(this.TAB);
		
		// Create Start node
		StartElement sElement = eventFactory.createStartElement(
				this.EMPTY, this.EMPTY, name);
		eventWriter.add(tab);
		eventWriter.add(tab);
		eventWriter.add(sElement);
		
		// Create Content
		Characters characters = eventFactory.createCharacters(value);
		eventWriter.add(characters);
		
		// Create End node
		EndElement eElement = eventFactory.createEndElement(
				this.EMPTY, this.EMPTY, name);
		eventWriter.add(eElement);
		eventWriter.add(end);
	}

}
