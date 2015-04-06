/**
 *****************************************************************************
 Copyright (c) 2015 IBM Corporation and other Contributors.
 All rights reserved. 
 Contributors:
 IBM - Initial Contribution
 *****************************************************************************
 * 
 */

package com.ibm.thingsmax.resources;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import com.ibm.thingsmax.bean.Device;
import com.ibm.thingsmax.bean.ThingsAction;
import com.ibm.thingsmax.storage.ThingsMaxDeviceDAO;
import com.ibm.thingsmax.storage.ThingsMaxThingsActionDAO;

@Path("/action")
public class ThingsActionResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getActions() {
    	ThingsMaxThingsActionDAO maximoDAO = new ThingsMaxThingsActionDAO();
        String maximo = null;
 
        try {
        	maximo = maximoDAO.getThingsAction();
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        return maximo;
    }
    
    @GET
    @Path("{actionId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAction(@PathParam("actionId") String actionId) {
    	ThingsMaxThingsActionDAO maximoDAO = new ThingsMaxThingsActionDAO();
        String action = null;
        try {
        	action = maximoDAO.getThingsActionDetails(actionId);
        } catch (Exception e) {
            e.printStackTrace();
        }
         return action;
    }
    
    @POST
    @Consumes( MediaType.APPLICATION_JSON )
    public Response create( @Context UriInfo uriInfo, ThingsAction action ) {
    	try{
    		ThingsMaxThingsActionDAO maximoDAO = new ThingsMaxThingsActionDAO();
	    	ThingsAction createdAction = maximoDAO.createAction(action);
	        URI uri = uriInfo.getBaseUriBuilder().path( ThingsActionResource.class ).path( createdAction.getActionId() +"" ).build();
	
	        return Response.created( uri ).build();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }
    
    @DELETE
    @Path( "/{actionId}")
    public Response delete( @PathParam( "actionId" ) String id ) {
    	try{
    		ThingsMaxThingsActionDAO actionDAO = new ThingsMaxThingsActionDAO();
    		actionDAO.delete(id);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
        return Response.status( Status.NO_CONTENT ).build();
    }    
}
