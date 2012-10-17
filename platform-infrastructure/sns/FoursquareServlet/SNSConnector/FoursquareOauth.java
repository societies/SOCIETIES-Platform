package SNSConnector;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Foursquare2Api;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

public class FoursquareOauth extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private String defaultApiKey = "LTNRV3JPEKSFUCMOF4HY05GZHW4BWIZ1Y2YGBJCLMGEXZFG4";
	private String defaultApiSecret = "2Y0YDIH5XQV13P2ZE3EWZDGEAIHXXQNMOUAEVU4XIWRYRBBS";
	OAuthService service = new ServiceBuilder().provider(Foursquare2Api.class)
			.apiKey(defaultApiKey)
			.apiSecret(defaultApiSecret)
			.callback("http://157.159.160.188:8080/examples/servlets/servlet/FoursquareOauth").build();
	private static final Token EMPTY_TOKEN = null;
	public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {

		String token = request.getQueryString();
		Verifier code = null;
		Token accessToken = null;
		if (token != null) {
			code = new Verifier(token.split("=")[1]);
			accessToken = service.getAccessToken(EMPTY_TOKEN, code);
		}
		
		JSONObject connector = new JSONObject();
		JSONObject societiesToken = new JSONObject();
		try {
			connector.put("from", "foursquare");
			connector.put("access_token", accessToken.getToken());
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
//				"<HEAD><TITLE>Societies Foursquare Connector</TITLE></HEAD>\n" +
//				"<BODY>\n" +
//				"<H1>Societies Foursquare Connector</H1>\n");
//		if (accessToken != null){
//			out.println("copy and paste this to generate the connector : <br><br>\n");
//			out.println(accessToken.getToken());
//			
//		}
//		else out.println("Oops !  Authentication server is down !");
//		out.println("</BODY></HTML>");
	}
}