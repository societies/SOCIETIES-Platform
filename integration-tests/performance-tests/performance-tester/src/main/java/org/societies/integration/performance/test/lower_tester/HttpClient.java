package org.societies.integration.performance.test.lower_tester;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;



import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;

public class HttpClient {


	public static String sendHttpPost(String URL, ArrayList<NameValuePair> postParameters) 
	{
		try 
		{
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPostRequest = new HttpPost(URL);

			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters);
			httpPostRequest.setEntity(formEntity);

			HttpResponse response = (HttpResponse) httpclient.execute(httpPostRequest);

			StatusLine statusLine = response.getStatusLine();

			if(statusLine.getStatusCode() == HttpStatus.SC_OK)
			{
				//Get hold of the response entity (-> the data):
				HttpEntity entity = response.getEntity();				
				
				if (entity != null) {
					// convert content stream to a String
					String resultString= convertStreamToString(entity.getContent());
					return resultString;
				}
				return null;
			} 
			else
			{
				//Closes the connection.
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}	
		return null;
	}

	//	public static String sendHttpGet(String URL) 
	//	{
	//		try 
	//		{
	//			DefaultHttpClient httpclient = new DefaultHttpClient();
	//			
	//			HttpGet httpGet = new HttpGet(URL);
	//			
	//			HttpResponse response = (HttpResponse) httpclient.execute(httpGet, httpContext);
	//			
	//			StatusLine statusLine = response.getStatusLine();
	//
	//			HttpEntity entity = response.getEntity();				
	//			
	//			if (entity != null) 
	//			{
	//				// convert content stream to a String
	//				String resultString= convertStreamToString(entity.getContent());
	//				
	//				return resultString;
	//			}
	//			else
	//			{
	//				System.out.println("null entity");
	//				return null;
	//			}
	//		}
	//		catch (IOException e)
	//		{
	//			e.printStackTrace();
	//			return null;
	//		}		
	//	}


	private static String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		try {
			return reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}