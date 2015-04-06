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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.ibm.thingsmax.bean.Asset;
import com.ibm.thingsmax.storage.ThingsMaxAssetDAO;
 
@Path("/assets")
public class AssetResource {
 
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAssets() {
    	ThingsMaxAssetDAO assetDao = new ThingsMaxAssetDAO();
        String assets = null;
 
        try {
        	assets = assetDao.getAssets();
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        return assets;
    }
 
    @GET
    @Path("{assetid}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAsset(@PathParam("assetid") String assetid) {
    	ThingsMaxAssetDAO assetDao = new ThingsMaxAssetDAO();
        String asset = null;
        try {
        	asset = assetDao.getAssetDetails(assetid);
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        return asset;
    }
    
    @POST
    @Consumes( MediaType.APPLICATION_JSON )
    public Response create( @Context UriInfo uriInfo, Asset asset ) {
    	try{
    		ThingsMaxAssetDAO assetDao = new ThingsMaxAssetDAO();	
	        Asset createdAsset = assetDao.createAsset( asset );
	        URI uri = uriInfo.getBaseUriBuilder().path( AssetResource.class )
	            .path( createdAsset.getAssetID()+"" ).build();
	
	        return Response.created( uri ).build();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }

    @DELETE
    @Path( "/{assetid}")
    public Response delete( @PathParam( "assetid" ) String id ) {
    	try{
    		ThingsMaxAssetDAO assetsDao = new ThingsMaxAssetDAO();
    		assetsDao.delete( id );
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
        return Response.status( Status.NO_CONTENT ).build();
    }
}