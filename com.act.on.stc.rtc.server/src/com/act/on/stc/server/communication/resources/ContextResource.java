package com.act.on.stc.server.communication.resources;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.act.on.stc.server.communication.RestServer;
import com.act.on.stc.server.datamanagement.DataManager;

@Path(ContextResource.PATH)
public class ContextResource {
	public static final String PATH= "/context";
	private DataManager fDm;
	
	public ContextResource() throws Exception {
		fDm= DataManager.getDataManager();
	}
	
	@GET
	@Produces(RestServer.APPLICATIONXML)
	public Response recieveGET() {
		return null; //fDm.processGetDistanceRequest(xmlContent);
	}
	
	@PUT
	@Consumes(RestServer.APPLICATIONXML)
	public Response recievePUT(InputStream xmlContent) {
		return fDm.processPutContextRequest(xmlContent);
	}
	
	@POST
	@Consumes(RestServer.APPLICATIONXML)
	public Response recievePOST(InputStream xmlContent) {
		return fDm.processPostContextRequest(xmlContent);
	}
}
