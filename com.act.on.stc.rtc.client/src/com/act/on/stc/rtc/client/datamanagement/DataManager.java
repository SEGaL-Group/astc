package com.act.on.stc.rtc.client.datamanagement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.xml.sax.SAXException;

import com.act.on.stc.rtc.client.communication.RestClient;
import com.act.on.stc.rtc.client.datamanagement.PushUnitFactory.PushUnit;
import com.act.on.stc.rtc.client.mylyn.MylynWrapper;
import com.act.on.stc.rtc.client.rtc.RTCWrapper;
import com.ibm.team.repository.common.IContributor;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.common.model.IWorkItem;
import com.sun.jersey.api.client.ClientResponse;

public class DataManager {
	private static final String PUSHING_DATA_JOB = "pushingData";
	private URI fPostToUri;
	private IWorkItem fCurrentTask= DummyWorkItem.getInstance();
	private URI fTaskUri;
	private DataPushJob fDataPushJob= null;
	private MylynWrapper fMylyn= null;
	private List<PushUnit> fPushUnits= new ArrayList<PushUnit>();
	private String fPushUnitDirectory;
	private Date fLastEventDate= new Date(0);

	public DataManager(MylynWrapper mylyn, String pushUnitDirectory) {
		fMylyn= mylyn;
		fPushUnitDirectory= pushUnitDirectory;
		try {
			fTaskUri= new URI(DummyWorkItem.DUMMY_STRING_URI);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public void start(int ms) throws InterruptedException {
		if (fDataPushJob == null) {
			loadPushUnits();

			fDataPushJob= new DataPushJob(PUSHING_DATA_JOB, ms);
			fDataPushJob.setPriority(Job.LONG);
			fDataPushJob.setSystem(true);
			fDataPushJob.schedule(ms * 10);
		}
	}

	private void loadPushUnits() {
		File directory= new File(fPushUnitDirectory);
		File[] files= directory.listFiles();
		if (files != null) {
			PushUnitFactory puf= new PushUnitFactory(fPushUnitDirectory);
			for (File f : files) {
				if (f.isFile() && f.getName().matches("\\.pu")) {
					try {
						PushUnit pu= puf.parseFile(f);
						fPushUnits.add(pu);
						if (pu.getLastDate().after(fLastEventDate)) {
							fLastEventDate= pu.getLastDate();
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (URISyntaxException e) {
						e.printStackTrace();
					} catch (SAXException e) {
						e.printStackTrace();
					} catch (ParserConfigurationException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void setPostToUri(URI postToUri) {
		fPostToUri= postToUri;
	}

	public void setCurrentTask(IWorkItem wi) {
		fCurrentTask= wi;
	}

	public void setCurrentTaskURI(URI uri) {
		fTaskUri= uri;
	}

	public void changeWorkItem(IWorkItem wi, URI uri) throws TransformerConfigurationException, ParserConfigurationException, TransformerException, TransformerFactoryConfigurationError, IOException, TeamRepositoryException {
		pushContext(fMylyn.getRecordedContext());
		fTaskUri= uri;
		fCurrentTask= wi;
	}

	public synchronized boolean pushContext(IInteractionContext ctx) throws ParserConfigurationException, TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError, IOException, TeamRepositoryException {
		IContributor user= RTCWrapper.getUser();

		if (ctx != null && ctx.getInteractionHistory().size() > 0) {
			PushUnitFactory puf= new PushUnitFactory(fPushUnitDirectory);
			PushUnit pu= puf.parseMylynXML(ctx, fPostToUri, fLastEventDate, null, null, fCurrentTask, fTaskUri);
			if (pu != null) { 
				fPushUnits.add(pu);
				pu.save();
				if (fLastEventDate.before(pu.getLastDate())) fLastEventDate= pu.getLastDate();
			}
		}

		ArrayList<PushUnit> pus= new ArrayList<PushUnit>();
		boolean success= true;
		for (PushUnit pu : fPushUnits) {
			if (pu.needsUpdate()) {
				if (user != null)
				pu.updateUser(user.getUserId(), user.getItemId());
				if (RTCWrapper.hasWorkItem(pu.getWorkItemUUID())) {
					pu.updateWorkItem(RTCWrapper.getWorkItemURI(pu.getWorkItemUUID()), RTCWrapper.getWorkItem(pu.getWorkItemUUID()).getId());
				}
			}
			
			if (!pu.needsUpdate()) {
				ClientResponse response= RestClient.POST(pu.getURI(), MediaType.APPLICATION_XML, pu.toXML());
				if ( !(response.getStatus() == ClientResponse.Status.OK.getStatusCode() || response.getStatus() == ClientResponse.Status.NO_CONTENT.getStatusCode()) ) {
					pus.add(pu);
				} else {
					if (fLastEventDate.before(pu.getLastDate())) fLastEventDate= pu.getLastDate();
					pu.delete();
					success= false;
				}
			}
		}

		fPushUnits= pus;

		return success;
	}

	class DataPushJob extends Job {
		private int fInterval;

		public DataPushJob(String name, int interval) {
			super(name);
			fInterval= interval;
		}

		protected IStatus run(IProgressMonitor mon) {
			try {
				pushContext(fMylyn.getRecordedContext());
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			} catch (TransformerFactoryConfigurationError e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TeamRepositoryException e) {
				e.printStackTrace();
			} finally {
				schedule(fInterval); 
			}

			return Status.OK_STATUS;
		}
	}
}
