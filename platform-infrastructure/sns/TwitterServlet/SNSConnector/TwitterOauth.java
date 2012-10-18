package SNSConnector;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.json.*;

public class TwitterOauth extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private String defaultApiKey = "cLD3W6l4bfXs8cwlzXGmRQ";
	private String defaultApiSecret = "IN6Oo79VnduEt5HRI9IQY07SpW86xkcN4UICuFg1zA0";
	OAuthService service = new ServiceBuilder().provider(TwitterApi.class)
			.apiKey(defaultApiKey)
			.apiSecret(defaultApiSecret)
			.callback("http://157.159.160.188:8080/examples/servlets/servlet/TwitterOauth").build();


	public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		//	String url = request.getRequestURI().toString();
		String token = request.getQueryString();   // d=789
		Token oauth_token = null;
		Verifier oauth_verifier = null;
		Token accessToken = null;
		if (token != null) {
			oauth_token = new Token(token.split("=")[1].split("&")[0],"");
			oauth_verifier = new Verifier(token.split("=")[2]);
			accessToken = service.getAccessToken(oauth_token, oauth_verifier);
		}

		JSONObject connector = new JSONObject();
		JSONObject societiesToken = new JSONObject();
		try {
			connector.put("from", "twitter");
			connector.put("access_token", accessToken.getToken()+","+accessToken.getSecret());
			connector.put("expires", "");
			societiesToken.put("connector", connector);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PrintWriter out = response.getWriter();
		out.println(societiesToken.toString());
//		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 " +
//				"Transitional//EN\">\n" +
//				"<HTML>\n" +
//				"<HEAD><TITLE>Societies Twitter Connector</TITLE></HEAD>\n" +
//				"<BODY>\n" +
//				"<H1>Societies Twitter Connector</H1>\n");
//		if (accessToken != null){
//			out.println("copy and paste this to generate the connector : <br><br>\n");
//			out.println(accessToken.getToken()+","+accessToken.getSecret());
//			
//		}
//		else out.println("Oops !  Authentication server is down !");
//		out.println("</BODY></HTML>");
	}
}