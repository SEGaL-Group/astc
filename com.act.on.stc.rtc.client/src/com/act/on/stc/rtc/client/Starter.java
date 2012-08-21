package com.act.on.stc.rtc.client;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IStartup;

import com.act.on.stc.rtc.client.communication.PortForwardingL;
import com.act.on.stc.rtc.client.datamanagement.DataManager;
import com.act.on.stc.rtc.client.mylyn.MylynWrapper;
import com.act.on.stc.rtc.client.rtc.RTCStarter;
import com.jcraft.jsch.JSchException;

public class Starter implements IStartup {

	private static final String SERVER_URI = "http://localhost:9998/context/";
	private static final String PUSHUNIT_STORAGE_DIRECTORY = ".astc" + File.separator + "pushUnits";
	private static final int REMOTE_PORT = 9998;
	private static final int LOCAL_PORT = 9998;
	private static final String HOST = "ballroom.segal.uvic.ca";
	private static final String PASSWORD = "1a2s3t4c";
	private static final String USERNAME = "astc";

	//@Override
	public void earlyStartup() {
		PortForwardingL portForward= new PortForwardingL(USERNAME,PASSWORD,HOST,LOCAL_PORT,REMOTE_PORT);
		try {
			portForward.forwardPort();
		} catch (JSchException e2) {
			e2.printStackTrace();
		}
		
		MylynWrapper mylyn= new MylynWrapper();
		try {
			mylyn.startDefaultTask();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		while (ResourcesPlugin.getWorkspace().getRoot().getProjects().length == 0) waitMS(5);
		
		IPath path= ResourcesPlugin.getWorkspace().getRoot().getProjects()[0].getLocation().makeAbsolute();
		path= path.uptoSegment(path.segmentCount()-1);
		String pushUnitStoragePath= path.toString() + File.separator + PUSHUNIT_STORAGE_DIRECTORY;
		File f= new File(pushUnitStoragePath);
		if (!f.exists()) {
			f.mkdirs();
		}
		
		DataManager dm= new DataManager(mylyn, pushUnitStoragePath);
		try {
			dm.setPostToUri(new URI(SERVER_URI));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		RTCStarter rtcs= new RTCStarter(dm);
		rtcs.start();

		try {
			dm.start(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	private synchronized void waitMS(int i) {
		try {
			wait(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}
}
