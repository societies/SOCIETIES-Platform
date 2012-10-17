package SNSConnector;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

public class TwitterLoginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private String defaultApiKey = "cLD3W6l4bfXs8cwlzXGmRQ";
	private String defaultApiSecret = "IN6Oo79VnduEt5HRI9IQY07SpW86xkcN4UICuFg1zA0";
	OAuthService service = new ServiceBuilder().provider(TwitterApi.class)
			.apiKey(defaultApiKey)
			.apiSecret(defaultApiSecret)
			.callback("http://157.159.160.188:8080/examples/servlets/servlet/TwitterOauth").build();
	
	public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
	    
		Token requestToken = service.getRequestToken();
		String url = service.getAuthorizationUrl(requestToken);
		response.sendRedirect(response.encodeRedirectURL(url));
	}

}