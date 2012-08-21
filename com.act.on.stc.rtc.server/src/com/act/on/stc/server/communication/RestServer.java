package com.act.on.stc.server.communication;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;

public class RestServer {
	public final static String APPLICATIONXML= "application/xml";
	
	private String fBaseUri= "http://localhost:9998/";
	private Thread fThread= null;
	private boolean fStop= false; 
	
	public RestServer() {}
	
	public RestServer(String baseUri) {
		fBaseUri= baseUri;
	}
	
	private synchronized void running() {
		System.out.println("Starting grizzly...");
		final Map<String, String> initParams= new HashMap<String, String>();
		initParams.put("com.sun.jersey.config.property.packages", "com.act.on.stc.server.communication.resources");
		
		SelectorThread threadSelector;
		try {
			threadSelector = GrizzlyWebContainerFactory.create(fBaseUri, initParams);
			while (!fStop) {
				wait(5);
			}
			System.out.print("Stoping grizzly ...");
			threadSelector.stopEndpoint();
			System.out.println(" stopped.");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			fStop= false;
		}
	}
	
	public void start() {
		if (fThread != null) return;
		
		fThread= new Thread() {
			public void run() {
				running();
			}
		};
		fThread.start();
	} 
	
	public void stop() {
		fStop= true;
	}
}
