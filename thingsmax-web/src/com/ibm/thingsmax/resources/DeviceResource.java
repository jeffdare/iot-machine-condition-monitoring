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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.ibm.thingsmax.bean.Device;
import com.ibm.thingsmax.storage.ThingsMaxDeviceDAO;
 
@Path("/devices")
public class DeviceResource {
 
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getDevices() {
    	ThingsMaxDeviceDAO deviceDao = new ThingsMaxDeviceDAO();
        String devices = null;
 
        try {
        	devices = deviceDao.getDevices();
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        return devices;
    }
 
    @GET
    @Path("{deviceid}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getDevice(@PathParam("deviceid") String deviceid) {
    	ThingsMaxDeviceDAO deviceDao = new ThingsMaxDeviceDAO();
        String device = null;
        try {
        	device = deviceDao.getDeviceDetails(deviceid);
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        return device;
    }
    
    @POST
    @Consumes( MediaType.APPLICATION_JSON )
    public Response create( @Context UriInfo uriInfo, Device device ) {
    	try{
    		ThingsMaxDeviceDAO deviceDao = new ThingsMaxDeviceDAO();	
	    	Device createdAsset = deviceDao.createDevice( device );
	        URI uri = uriInfo.getBaseUriBuilder().path( DeviceResource.class )
	            .path( createdAsset.getDeviceID()+"" ).build();
	
	        return Response.created( uri ).build();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }

    @DELETE
    @Path( "/{deviceid}")
    public Response delete( @PathParam( "deviceid" ) String id ) {
    	try{
    		ThingsMaxDeviceDAO devicesDao = new ThingsMaxDeviceDAO();
    		devicesDao.delete( id );
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
        return Response.status( Status.NO_CONTENT ).build();
    }
}