package com.act.on.stc.server.communication.resources;

import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.act.on.stc.server.communication.RestServer;
import com.act.on.stc.server.datamanagement.DataManager;

@Path(TaskVisualizationResource.PATH)
public class TaskVisualizationResource {
	// path where request for task-task proximity is made, given a user
	public static final String PATH= "/taskVisualization";
	private DataManager fDm;
	
	public TaskVisualizationResource() throws Exception {
		fDm= DataManager.getDataManager();
	}
	
	@POST
	@Produces(RestServer.APPLICATIONXML)
	public Response receivePOST(InputStream xmlContent) {
		return fDm.processGetTaskDistanceRequest(xmlContent);
	}
	
	@GET
	@Produces(RestServer.APPLICATIONXML)
	public Response receiveGET(InputStream xmlContent) {
		return fDm.processGetTaskDistanceRequest(xmlContent);
	}
}
