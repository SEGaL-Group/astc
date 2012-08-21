package com.act.on.stc.server.communication.resources;

import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.act.on.stc.server.communication.RestServer;
import com.act.on.stc.server.datamanagement.DataManager;

@Path(UserVisualizationResource.PATH)
public class UserVisualizationResource {
	// path where request for user-user proximity is made, given a task
	public static final String PATH= "/userVisualization";
	private DataManager fDm;
	
	public UserVisualizationResource() throws Exception {
		fDm= DataManager.getDataManager();
	}
	
	@POST
	@Produces(RestServer.APPLICATIONXML)
	public Response receivePOST(InputStream xmlContent) {
		return fDm.processGetUserDistanceRequest(xmlContent);
	}
	
	@GET
	@Produces(RestServer.APPLICATIONXML)
	public Response receiveGET(InputStream xmlContent) {
		return fDm.processGetUserDistanceRequest(xmlContent);
	}
}
