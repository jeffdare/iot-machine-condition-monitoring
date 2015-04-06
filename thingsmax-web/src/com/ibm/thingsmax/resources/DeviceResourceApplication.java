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

import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import java.util.HashSet;
import java.util.Set;
 
@javax.ws.rs.ApplicationPath("deviceMappings")
@Path("/devices")

public class DeviceResourceApplication extends Application {
 
	@Context
    private UriInfo context;

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(DeviceResource.class);
 
        return classes;
    }
}