package SNSConnector;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Foursquare2Api;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

public class FoursquareLoginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private String defaultApiKey = "LTNRV3JPEKSFUCMOF4HY05GZHW4BWIZ1Y2YGBJCLMGEXZFG4";
	private String defaultApiSecret = "2Y0YDIH5XQV13P2ZE3EWZDGEAIHXXQNMOUAEVU4XIWRYRBBS";
	OAuthService service = new ServiceBuilder().provider(Foursquare2Api.class)
			.apiKey(defaultApiKey)
			.apiSecret(defaultApiSecret)
			.callback("http://157.159.160.188:8080/examples/servlets/servlet/FoursquareOauth").build();
	private static final Token EMPTY_TOKEN = null;
	
	public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
	    
		String url = service.getAuthorizationUrl(EMPTY_TOKEN);
		response.sendRedirect(response.encodeRedirectURL(url));
	}

}