# Machine Condition Monitoring
### Introduction
IBM Maximo Asset Management is a comprehensive solution for managing enterprise assets on an extensible platform. It offers “built in” mobile access, out-of-the box mapping, crew management, extensive reporting and analytical insights.
IBM IoT Foundation is a fully managed, cloud-hosted service that is designed to simplify and derive the value from IoT devices. 
Assets, managed through Maximo, can utilize sensor data, registered in IoT Foundation, for real-time monitoring. This is where Machine Condition Monitoring makes leveraging this information easy.
The Machine Condition Monitoring application runs in a Bluemix environment and can be used to integrate IBM IoT Foundation with Maximo Asset Manager. 
Use the Machine Condition Monitoring application to create mappings between:
1.	An organization in IBM IoT Foundation to and an organization in Maximo Asset Manager.
2.	A device in IBM IoT Foundation to and an asset in Maximo Asset Manager.

Use the Machine Condition Monitoring application to:
1.	Add business rules, in Machine Condition Monitoring, to set threshold for work order generation
2.	Generate a work order in Maximo, based on the information in a sensor event.

Use the ReST APIs that are provided with the Machine Condition Monitoring application to: 
1.	Create, read and delete the mappings between devices and assets, for a given organization.
2.	Create, read and delete the action items (such as Work-order generation) to be taken on receiving sensor events
 

---
### Version
0.0.2

---
### Requirements
#### Software Requirements
1.	IBM Bluemix account
2.	Java Cloudant Web starter boilerplate package
3.	Business Rules service (which has to be added to the above Java Cloudant Web starter boilerplate package)
4.	Internet of Things service (which has to be added to the above Java Cloudant Web starter boilerplate package)
5.	Node.js runtime application (Bind the exact services which have been added to the Java Cloudant Web Starter package – namely Business Rules service, Internet of Things service and also bind the Cloudant service.)
6.	Access to ReST service of the Maximo instance
---
### Physical components of Machine Condition Monitoring
The Machine Condition Monitoring application runs on IBM Bluemix and is made up of the following components which are all available on IBM Bluemix:
1.	Liberty for Java
2.	Cloudant DB service
3.	Business Rules Service
4.	Internet of Things Service
5.	Node.js runtime environment

---
### Installing and Configuring 
#### Prerequisites for installing Machine Condition Monitoring 
##### Bluemix
1. Create a web application in IBM Bluemix using the Java Cloudant Web starter boilerplate.
2. Add a Business Rules service to your application.
3. Add Internet of Things service to your application.
4. Make a note of all the VCAP credentials for every bound service. In this example the application has the following bound services:   
    1.	Cloudant
    2.	Business Rules
    3.	Internet of Things
Therefore there are 3 sets of VCAP credentials (one for each service).

##### Cloudant
Machine Condition Monitoring needs the following instances of Cloudant Databases in order to run:
1.	invokedrules
2.	Machine Condition Monitoringactionmappings
3.	Machine Condition Monitoringassetcentricmappings
4.	Machine Condition Monitoringdevicecentricmappings
5.	Machine Condition Monitoringrulecentricmappings

These databases are created by Machine Condition Monitoring, after the application is started.



---
##### Business Rules Service
Business Rules are created by using the Rules Designer. The Business Rules can then deployed on the rules services and made available on Bluemix.
1. Click the Business Rules Service that is bound to your Machine Condition Monitoring application. Make a note of the, URL, Username and password. You will need these for creating your Rule Execution Server in Eclipse IDE.
2. Install the Business Rules Service Rule Designer plugin in Eclipse IDE.
3. After installing, open the Rules perspective in Eclipse.
4. Right click your RuleApp project, and select RuleApp > Deploy. 
5. Select “Create a temporary Rule Execution Server configuration” and enter your Bluemix Business Rule Service credentials for URL, Username and password. 
6. Click Finish. 
7. Test your deployment by logging onto the Business Rules Server web page using your userid and password.
---
#### Installing Machine Condition Monitoring
To install the Machine Condition Monitoring web application, you need to push the machineconditionmonitoring-web.war file to your Bluemix account. 

The minimum amount of disk-space required on the IBM Bluemix, for this application, is 1GB. The minimum amount of memory required is 512MB although you might want to set the amount of memory higher than this – for example 2GB. The value that you assign to disk-space and memory will depend on your environment. 
The above stated values (amount of disk-space and memory) can be modified using the manifest.yml file.
Ensure that you have the latest version of the cf tool installed. Complete the following steps to install the cf tool, in case you don’t have the latest.
1.	You can find the latest version on github.com at the following location - https://github.com/cloudfoundry/cli/releases.
2.	Choose the appropriate installer for your platform and download the installer.
3.	Unzip the installer and run the executable code.
Complete the following steps to install the Machine Condition Monitoring application:
1.	Connect to Bluemix by using the following command, on the command prompt of your local system.
    ```
    cf api https://api.ng.bluemix.net
    ```
2.	Login to Bluemix by using the following command. 
    ```
    cf login –u {Bluemix userid} –o {Bluemix organization} –s {Bluemix-space} 
    ```
3.	Download the source code, for Machine Condition Monitoring from the following location – 
https://github.com/amitmangalvedkar/iot-machine-condition-monitoring/tree/master/thingsmax-web
4.	Build a war file from the source code, in an eclipse IDE, and name it as machineconditionmonitor-web.war.
5.	Push the web application to your IBM Bluemix account, by providing a unique application name (in the below case I have provided machineconditionmonitor-web)
    ```
    cf push machineconditionmonitor-web
    ```
6.	Download the “server.xml” file from Bluemix.
7.	Create a folder called “defaultServer” in your local system.
8.	Add the “server.xml” file in this folder and add the following entry in the server.xml file.
    ```
    <webContainer deferServletLoad="false"/>
    ```
Change the name of the app, in server.xml, to the one that you have provided.
9.	The above steps prevent lazy loading in the liberty on Bluemix.
10.	Create a folder called apps and add the machineconditionmonitoring-web.war file to that folder.
11.	Push the war file back to IBM Bluemix, by providing the unique application name as was given previously (in the below case I have provided machineconditionmonitor-web)
    ```
    cf push machineconditionmonitor-web –p defaultServer
    ```
#### Installing the Machine Condition Monitoring UI
To install the Machine Condition Monitoring UI, you need to push the Machine Condition Monitoring Node.js application to your Bluemix account. 

Complete the following steps to install the Machine Condition Monitoring UI:
This step assumes that you have already the ‘cf’ tool, as documented in steps 1- 3 of Installing Machine Condition Monitoring
1.	From the Catalog section in IBM Bluemix, select SDK for Node.js in the Runtimes Section. 
2.	Enter a unique application name, say ThingsMaxUI and click Create.
3.	After the application is created, bind(not add) the services that you created earlier:
    1.	Business Rules Service
    2.	Cloudant NoSQL DB Service
    3.	Internet of Things Service
4.	Create a user-defined environment variable for the URL of the Machine Condition Monitoring-web application
    1.	Select Environment Variables tab
    2 .	Click User-Defined
    3.	Add an environmental variable called “runtimeapp” with the URL of your Machine Condition Monitoring-web application. The following link http://libMachine Condition Monitoring.mybluemix.net is the URL for the Machine Condition Monitoring-web application.
5.	Push the Machine Condition Monitoring-GUI code from (where) by using the following command from the cf command line.

6.	Connect to Bluemix by using the following command, on the command prompt of your local system.
    ```  
    cf api https://api.ng.bluemix.net
    ```
7.	Login to Bluemix by using the following command. 
    ```
    cf login –u {Bluemix userid} –o {Bluemix organization} –s {Bluemix-space} 
    ```
8.	Build the Machine Condition Monitoring from the following github location
https://github.com/amitmangalvedkar/iot-machine-condition-monitoring/tree/master/ThingsMax-GUI

9.	Push your Node.js application to your Bluemix account, , by providing a unique application name (in the below case I have provided machineconditionmonitor-GUI)
    ```
    cf push machineconditionmonitoring-GUI
    ```
---
### Flow Testing
1.	Send a device event from a registered device which has event parameter that is set higher than the maximum permissible threshold so that a work order is generated.
2.	The work-order is generated and can be seen in the Machine Condition Monitoring UI.

---
###License
 - Apache License

