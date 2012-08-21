package com.act.on.stc.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

import com.act.on.stc.server.communication.RestServer;
import com.act.on.stc.server.database.PSQLWrapper;
import com.act.on.stc.server.datamanagement.DataManager;

public class Starter {
	private static final String STOP = "stop";
	private RestServer fServer;
	private String fFile = ".stopping";
	
	private synchronized void run() throws InterruptedException, IOException, SQLException {
		init();
		start();
		while (!needsToStop()) {
			wait(5);
		}
		File f= new File(fFile);
		f.delete();
		stop();
	}

	private void init() throws SQLException {
		PSQLWrapper dbc= new PSQLWrapper("astctest", "localhost", "jazz", "jazz", 5432, false);
		//PSQLWrapper dbc= new PSQLWrapper("agilefant-astc", "localhost", "jazz", "jazz", 5432, false);
		@SuppressWarnings("unused")
		DataManager dbm= new DataManager(dbc);
	}

	private boolean needsToStop() throws IOException {
		File f= new File(fFile);
		boolean doStop= false;
		if (f.exists()) {
			FileReader fr= new FileReader(f);
			BufferedReader br= new BufferedReader(fr);
		
			doStop= br.readLine().contains(STOP);
		}
		return doStop;
	}

	private void start() throws IOException {
		fServer= new RestServer();
		fServer.start();
		File f= new File(fFile);
		if (f.exists()) {
			f.delete();
		}
	}
	
	private void stop() {
		fServer.stop();		
	}

	public static void main(String[] args) throws IOException, InterruptedException, SQLException {
		Starter s= new Starter();
		s.run();
	}
}
