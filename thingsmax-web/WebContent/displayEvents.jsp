<%@page import="java.io.PrintWriter"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="java.util.*" %>  

<%@ page import="java.util.concurrent.*" %>
<%@ page import="com.ibm.json.java.*" %>

<%@ page import="org.apache.http.*" %>
<%@ page import="org.apache.http.client.*" %>
<%@ page import="org.apache.http.client.methods.*" %>
<%@ page import="org.apache.http.entity.*" %>
<%@ page import="org.apache.http.impl.client.*" %>

<%@ page import="com.ibm.iotf.*" %>
<%@ page import="com.ibm.thingsmax.engine.web.*"  %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%!
%>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Event Dashboard</title>

</head>
<body>
<%

		com.ibm.thingsmax.engine.web.IoTFAgent agent = (com.ibm.thingsmax.engine.web.IoTFAgent)application.getAttribute("iotFAgent");
//		response.setIntHeader("Refresh", 20);
        List<String> messages = new LinkedList<String>();
        
        String deviceType = null;
        String deviceId = null;
        String eventType = null;
        String formatType = null;
        String qoS = null;

        String originalDeviceType = null;
        String originalDeviceId = null;
        String originalEventType = null;
        String originalFormatType = null;
        
        boolean changed = false;
        
        if(request.getParameter("type") == null) {
			deviceType = agent.getDeviceType() != null ? agent.getDeviceType() : "+";
		} else {
			deviceType = request.getParameter("type") ;
			originalDeviceType = agent.getDeviceType();
			agent.setDeviceType(deviceType);
			changed = true;
		}
		
		if(request.getParameter("id") == null){
			deviceId = agent.getDeviceId() != null ? agent.getDeviceId() : "+";
		} else {
			deviceId = request.getParameter("id");
			originalDeviceId = agent.getDeviceId();
			agent.setDeviceId(deviceId);
			changed = true;			
		}
		
		if(request.getParameter("event") == null){
			eventType = agent.getEventType() != null ? agent.getEventType() : "+";
		} else {
			eventType = request.getParameter("event");
			originalEventType = agent.getEventType();
			agent.setEventType(eventType);
			changed = true;			
		}
		
		if(request.getParameter("format") == null){
			formatType = agent.getFormatType() != null ? agent.getFormatType() : "+";
		} else {
			formatType = request.getParameter("format");
			originalFormatType = agent.getFormatType();
			agent.setFormatType(formatType);
			changed = true;			
		}
//        agent.client.subscribeToDeviceCommands(deviceType, deviceId, eventType, "json", 0);
		System.out.println("it comes before changed unsubscribe");
		
		/*  Set subscriptionSet =  agent.client.getSubscriptions().entrySet();
		Iterator<Map.Entry<String, Integer>> iter = subscriptionSet.iterator();
		while(iter.hasNext()) {
			Map.Entry<String, Integer > key = iter.next();
			System.out.println(key.getKey() +" :: "+ key.getValue());
		}  */
		
        if(changed) {
        	System.out.println("it comes before changed unsubscribe");
        	
        	/* Set subscriptionSet1 =  agent.client.getSubscriptions().entrySet();
			Iterator<Map.Entry<String, Integer >> iter1 = subscriptionSet1.iterator();
		while(iter1.hasNext()) {
			Map.Entry<String, Integer > key1 = iter1.next();
			System.out.println(key1.getKey() +" :: "+ key1.getValue());
		}  */
			System.out.println("DeviceType: " + deviceType + "DeviceId: " + deviceId + "EventType: " + eventType + "FormatType: " + formatType);
			agent.client.unsubscribeFromDeviceEvents(originalDeviceType, originalDeviceId, originalEventType, originalFormatType, 0);
			System.out.println("it comes after changed unsubscribe");
			System.out.println("DeviceType: " + deviceType + "DeviceId: " + deviceId + "EventType: " + eventType + "FormatType: " + formatType);
			
			
			/* Set subscriptionSet2 =  agent.client.getSubscriptions().entrySet();
			Iterator<Map.Entry<String, Integer >> iter2 = subscriptionSet2.iterator();
		while(iter2.hasNext()) {
			Map.Entry<String, Integer > key2 = iter2.next();
			System.out.println(key2.getKey() +" :: "+ key2.getValue());
		} */
			try {
				Thread.sleep(10);
			} catch(InterruptedException ex) {
			
			}
			System.out.println("it comes before changed subscribe");
			
			/* Set subscriptionSet3 =  agent.client.getSubscriptions().entrySet();
			Iterator<Map.Entry<String, Integer >> iter3 = subscriptionSet3.iterator();
		while(iter3.hasNext()) {
			Map.Entry<String, Integer > key3 = iter3.next();
			System.out.println(key3.getKey() +" :: "+ key3.getValue());
		} */
			agent.client.subscribeToDeviceEvents(deviceType, deviceId, eventType, formatType, 0); 
			System.out.println("it comes after changed subscribe");       
			try {
				Thread.sleep(10);
			} catch(InterruptedException ex) {
			
			}
		}
        
%>
		<table class="TFtable" align="center">
			<tr>
				<th align="right" > Property</th>
				<th></th>
				<th align="left" > Value</th>
			</tr>
			<tr>
				<td align="right" > Organization Id </td>
				<td></td>
				<td><%= agent.client.getOrgId() %></td>
			</tr>
			<tr>
				<td align="right">Device Type</td>
				<td></td>
				<td><%= deviceType %></td>
			</tr>
			<tr>
				<td align="right">Device Id</td>
				<td></td>
				<td><%= deviceId%></td>
			</tr>						
			<tr>
				<td align="right">Event Type</td>
				<td></td>
				<td><%= eventType%></td>
			</tr>
			<tr>
				<td align="right">Format Type</td>
				<td></td>
				<td><%= formatType%></td>
			</tr>
									
			<tr>
				<td align="right"  valign="top">Events</td>
				<td></td>	
											
<%
//		out.println("Connected successfully - <br>Your device ID is " + tc.client.getAppId());
//		out.println("<br>Organization: " + tc.client.getOrgId() + " (" + tc.client.getAuthToken() + ")");
		agent.messages.drainTo(messages);
//		out.println("Messages received");
		
		Iterator iterator = messages.iterator();
		
%>
<td>
<% 		
 		while(iterator.hasNext()) {
			out.println(iterator.next() + "<br>");
		} 
%>		
		</td>
		</tr>						

		</table> 
</body>
</html>