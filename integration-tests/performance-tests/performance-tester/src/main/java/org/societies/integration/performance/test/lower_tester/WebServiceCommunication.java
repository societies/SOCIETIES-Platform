package org.societies.integration.performance.test.lower_tester;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class manages all web services requests
 * @author rafik
 * @date 17/01/2013
 * 
 */
public class WebServiceCommunication {

	private static Logger LOG = LoggerFactory.getLogger(WebServiceCommunication.class);

	private static String URL = "";
	
	/**
	 * 
	 * @param URL
	 * @param startTestResponse
	 * @return
	 */
	public static String sendStartResponse(String host, String startTestResponse)
	{	
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("start_test_response", startTestResponse));
		
		URL = host+"/start-test";
		
		LOG.info("### [WebServiceCommunication] sendStartResponse URL: " + URL);
		
		String response = HttpClient.sendHttpPost(URL, postParameters);

		if (response != null)
		{
			LOG.info("### [WebServiceCommunication] sendStartResponse response: " + response);
			return response;
		}
		else
		{
			LOG.info("### [WebServiceCommunication] sendStartResponse null response");
			return null;
		}
	}

	
	/**
	 * 
	 * @param URL
	 * @param finishTestResponse
	 * @return
	 */
	public static String sendFinishResponse(String host, String finishTestResponse)
	{
		
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("end_test_response", finishTestResponse));
		
		URL = host + "/end-test";
		
		LOG.info("### [WebServiceCommunication] sendFinishResponse URL: " + URL);
		
		String response = HttpClient.sendHttpPost(URL, postParameters);

		if (response != null)
		{
			LOG.info("### [WebServiceCommunication] sendFinishResponse response: " + response);
			return response;
		}
		else
		{
			LOG.info("### [WebServiceCommunication] sendFinishResponse null response");
			return null;
		}
	}
	
}
