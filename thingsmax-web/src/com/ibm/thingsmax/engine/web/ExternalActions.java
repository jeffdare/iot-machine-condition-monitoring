package com.ibm.thingsmax.engine.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Random;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.ibm.json.java.JSONObject;
import com.ibm.thingsmax.bean.InvokedRule;
import com.ibm.thingsmax.storage.ThingsMaxInvokedRulesDAO;
import com.ibm.thingsmax.storage.ThingsMaxThingsActionDAO;

public class ExternalActions {

	/**
	 * This method can be used to invoke any action based on the rule engine
	 * recommendation.
	 * 
	 * @param ruleMessage
	 *            The Recommendation from the rule Engine
	 * @param deviceMapping
	 *            The device for which the rule was invoked and recommendation
	 *            was received
	 */

	public static void callAction(String ruleMessage, JSONObject deviceMapping) {

		System.out.println("called with rule : " + ruleMessage);

		CloseableHttpClient client = null;
		try {

			ThingsMaxThingsActionDAO dao = new ThingsMaxThingsActionDAO();

			//Currently the action is to create a WO in Maximo
			String maxDetails = dao.getThingsActionDetails("maximo");

			System.out.println("macixmo details : " + maxDetails);
			JSONObject maxDetailsJson = JSONObject.parse(maxDetails);

			String maximoUser = (String) maxDetailsJson
					.get("thingsActionUserId");
			String maximoPassword = (String) maxDetailsJson
					.get("thingsActionPassword");
			String maximoUrl = (String) maxDetailsJson.get("thingsActionURL");

			String endpointURI = MessageFormat
					.format(maximoUrl
							+ "/rest/os/mxwo?wonum={0}&worktype=EM&status=WSCH&assetnum={1}&siteid={2}&description={3}&_format=json&_lid={4}&_lpwd={5}",
							new Random().nextInt(1000000),
							deviceMapping.get("assetId"),
							deviceMapping.get("siteid"),
							URLEncoder.encode(ruleMessage, "UTF-8"),
							maximoUser, maximoPassword);

			SSLContextBuilder builder = new SSLContextBuilder();
			builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					builder.build(),
					SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			client = HttpClients.custom().setSSLSocketFactory(sslsf).build();

			HttpPost httpPost = new HttpPost(endpointURI);
			httpPost.addHeader("Content-Type", "application/json");
			CloseableHttpResponse response = client.execute(httpPost);
			try {
				if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
					throw new RuntimeException(
							"An error occured when invoking Maximo WO URL: " + endpointURI
									+ " Please verify :: "
									+ response.getStatusLine());
				} else {

					String result = EntityUtils.toString(response.getEntity());
					System.out.println("Response: " + result);

					JSONObject resp = JSONObject.parse(result);
					
					Long WONumber = (Long) ((JSONObject)((JSONObject)((JSONObject)((JSONObject)((JSONObject)resp.get("CreateMXWOResponse")).get("MXWOSet")).get("WORKORDER")).get("Attributes")).get("WORKORDERID")).get("content");
					
					InvokedRule rule = new InvokedRule((String)deviceMapping.get("assetId"), (String)deviceMapping.get("assetType"), (String)deviceMapping.get("deviceId"), (String)deviceMapping.get("deviceType"), WONumber.toString(), ruleMessage);

					System.out.println("the wonum is "+WONumber);
					//Add entry to the invokedRules database in Cloudant
					ThingsMaxInvokedRulesDAO ruleDao = new ThingsMaxInvokedRulesDAO();
					
					ruleDao.createAction(rule);
				}
			} finally {
				response.close();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
