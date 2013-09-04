package org.societies.platfrom.sns.android.socialapp;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.builder.api.Foursquare2Api;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class WebActivity extends Activity{


	private static final String TAG = "WebActivity";

	private String socialNetworkRequested = null;
	private OAuthService service;
	private LinearLayout progressBar;
	private WebView webView;

	// facebook
	private static final Token EMPTY_TOKEN = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ssolib_web);        
		// read parameters
		socialNetworkRequested = getIntent().getStringExtra(Constants.SSO_URL);
		Log.d(TAG, "[onCreate] socialNetworkRequested="+socialNetworkRequested);

		progressBar = (LinearLayout) findViewById(R.id.loading);
		progressBar.setVisibility(View.GONE);

		webView = (WebView) findViewById(R.id.webview);
		webView.setVisibility(View.GONE);

		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setLoadsImagesAutomatically(true);
		settings.setSupportMultipleWindows(true);
		settings.setDomStorageEnabled(true);
		settings.setUserAgent(0);
		settings.setSaveFormData(true);
		settings.setLoadWithOverviewMode(true);
		
		if(socialNetworkRequested != null){
			this.doConnectToSocialNetwork(socialNetworkRequested);
		}
	}

	private void doConnectToSocialNetwork(String socialNetworkRequested){

		if(socialNetworkRequested == null){
			//no socialnetwork to connect
			finish();
			return;
		}
		
		TokenListener tokenListener = new TokenListener(socialNetworkRequested);

		if(socialNetworkRequested.equals(Constants.FB_URL)){
			Log.d(Constants.DEBUG_TAG, "[onClick] fb_connector");
			progressBar.setVisibility(View.VISIBLE);

			service = new ServiceBuilder()
			.provider(FacebookApi.class)
			.apiKey(Constants.FB_CLIENT_ID)
			.apiSecret(Constants.FB_CLIENT_SECRET)
			.callback(Constants.FB_CALLBACK_URL)
			.scope(Constants.FB_SCOPES)
			.build();

			String fbauthURL = service.getAuthorizationUrl(EMPTY_TOKEN);
			Log.d(Constants.DEBUG_TAG, "[onClick] fbauthURL="+fbauthURL);

			webView.setVisibility(View.VISIBLE);
			webView.setWebViewClient(new InnerWebViewClient(EMPTY_TOKEN, tokenListener)); 				
			//send user to authorization page
			webView.loadUrl(fbauthURL);

		} else if(socialNetworkRequested.equals(Constants.FQ_URL)){
			Log.d(Constants.DEBUG_TAG, "[onClick] fq_connector");
			progressBar.setVisibility(View.VISIBLE);

			service = new ServiceBuilder()
			.provider(Foursquare2Api.class)
			.apiKey(Constants.FQ_CLIENT_ID)
			.apiSecret(Constants.FQ_CLIENT_SECRET)
			.callback(Constants.FQ_CALLBACK_URL)
			.build();

			String fqauthURL = service.getAuthorizationUrl(EMPTY_TOKEN);
			Log.d(Constants.DEBUG_TAG, "[onClick] fqauthURL="+fqauthURL);

			webView.setVisibility(View.VISIBLE);
			webView.setWebViewClient(new InnerWebViewClient(EMPTY_TOKEN, tokenListener)); 				
			//send user to authorization page
			webView.loadUrl(fqauthURL);		
		} else if(socialNetworkRequested.equals(Constants.TW_URL)){
			Log.d(Constants.DEBUG_TAG, "[onClick] tw_connector");
			progressBar.setVisibility(View.VISIBLE);

			service = new ServiceBuilder()
			.provider(TwitterApi.class)
			.apiKey(Constants.TW_CLIENT_ID)
			.apiSecret(Constants.TW_CLIENT_SECRET)
			.callback(Constants.TW_CALLBACK_URL)
			.build();

			Token requestToken = service.getRequestToken();
			String twauthURL = service.getAuthorizationUrl(requestToken);

			webView.setVisibility(View.VISIBLE);
			webView.setWebViewClient(new InnerWebViewClient(requestToken, tokenListener)); 
			//send user to authorization page
			webView.loadUrl(twauthURL);
		} else if(socialNetworkRequested.equals(Constants.LK_URL)){
			Log.d(Constants.DEBUG_TAG, "[onClick] lk_connector");
			progressBar.setVisibility(View.VISIBLE);

			service = new ServiceBuilder()
			.provider(LinkedInApi.class)
			.apiKey(Constants.LK_CLIENT_ID)
			.apiSecret(Constants.LK_CLIENT_SECRET)
			.callback(Constants.LK_CALLBACK_URL)
			.build();

			Token lkrequestToken = service.getRequestToken();
			String lkurl = service.getAuthorizationUrl(lkrequestToken);
			Log.d(Constants.DEBUG_TAG, "[onClick] lkurl="+lkurl);

			webView.setVisibility(View.VISIBLE);
			webView.setWebViewClient(new InnerWebViewClient(lkrequestToken, tokenListener)); 

			//send user to authorization page
			webView.loadUrl(lkurl);	
		}
	}
	
	private class InnerWebViewClient extends WebViewClient {

		private static final String InnerTAG = "InnerWebViewClient";

		private Token requestToken = null;
		private SocialNetworkTokenListener listener;

		public InnerWebViewClient(Token requestToken, SocialNetworkTokenListener listener){
			this.requestToken = requestToken;
			this.listener = listener;
		}		

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.d(InnerTAG, "[shouldOverrideUrlLoading] view="+view+", url="+url);

			//check for our custom callback protocol otherwise use default behavior
			if(url.startsWith(Constants.TW_CALLBACK_URL)){
				//authorization complete hide webview for now.
				webView.setVisibility(View.GONE);

				Uri uri = Uri.parse(url);
				String verifier = uri.getQueryParameter("oauth_verifier");
				
				Token accessToken = null;
				
				if(verifier != null){
					Verifier v = new Verifier(verifier);
					//save this token for practical use.
					accessToken = service.getAccessToken(requestToken, v);
				}				

				if(listener != null){
					listener.onTokenAvailable(accessToken);
				}  
				return true;
			} else if(url.startsWith(Constants.FB_CALLBACK_URL)){
				//authorization complete hide webview for now.
				webView.setVisibility(View.GONE);
				
				Log.d(InnerTAG, "[shouldOverrideUrlLoading] FB_CALLBACK_URL="+Constants.FB_CALLBACK_URL);
				
				Uri uri = Uri.parse(url);
				String verifier = uri.getQueryParameter("code");
				
				Token accessToken = null;
				
				if(verifier != null){
					Log.d(InnerTAG, "[shouldOverrideUrlLoading] verifier="+verifier);
					Verifier v = new Verifier(verifier);
					
					accessToken = service.getAccessToken(EMPTY_TOKEN, v);
				}
				
				if(listener != null){
					listener.onTokenAvailable(accessToken);
				}  
				return true;
			} else if(url.startsWith(Constants.FQ_CALLBACK_URL)){
				//authorization complete hide webview for now.
				webView.setVisibility(View.GONE);
				
				Log.d(InnerTAG, "[shouldOverrideUrlLoading] FQ_CALLBACK_URL="+Constants.FQ_CALLBACK_URL);
				
				Uri uri = Uri.parse(url);
				String verifier = uri.getQueryParameter("code");
				
				Token accessToken = null;
				
				if(verifier != null){
					Log.d(InnerTAG, "[shouldOverrideUrlLoading] verifier="+verifier);
					Verifier v = new Verifier(verifier);					
					accessToken = service.getAccessToken(EMPTY_TOKEN, v);
				}				
				
				if(listener != null){
					listener.onTokenAvailable(accessToken);
				}  
				return true;
			} else if(url.startsWith(Constants.LK_CALLBACK_URL)){
				//authorization complete hide webview for now.
				webView.setVisibility(View.GONE);
				
				Log.d(InnerTAG, "[shouldOverrideUrlLoading] LK_CALLBACK_URL="+Constants.LK_CALLBACK_URL);
				
				//http://127.0.0.1:8080/societies-test/doConnect.html?type=lk&oauth_token=8b2e784d-78f4-44fd-9d98-e7fed60b5cbc&oauth_verifier=49273
				Uri uri = Uri.parse(url);
				String verifier = uri.getQueryParameter("oauth_verifier");
				
				Token accessToken = null;
				
				if(verifier != null){
					Verifier v = new Verifier(verifier);
					//save this token for practical use.
					accessToken = service.getAccessToken(requestToken, v);
				}				

				if(listener != null){
					listener.onTokenAvailable(accessToken);
				}  
				return true;
			}
			return super.shouldOverrideUrlLoading(view, url);    	
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			Log.d(InnerTAG, "[onPageFinished] view="+view+", url="+url);
			if(progressBar != null){
				progressBar.setVisibility(View.GONE);
			}
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			Log.e(InnerTAG, "[onReceivedError] errorCode="+errorCode+", description="+description+", failingUrl="+failingUrl);
			if(progressBar != null){
				progressBar.setVisibility(View.GONE);
			}
			if(listener != null){
				listener.onTokenAvailable(null);
			}  
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			Log.d(InnerTAG, "[onReceivedSslError] view="+view+", handler="+handler+", error="+error);
			handler.proceed(); // Ignore SSL certificate errors
		}

		@Override
		public void onLoadResource(WebView view, String url) {
			super.onLoadResource(view, url);
			Log.d(InnerTAG, "[onLoadResource] view="+view+", url="+url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			Log.d(InnerTAG, "[onPageStarted] view="+view+", url="+url+", favicon="+favicon);
		}

		@Override
		public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
			super.onReceivedHttpAuthRequest(view, handler, host, realm);
			Log.d(InnerTAG, "[onReceivedHttpAuthRequest] view="+view+", handler="+handler+", host="+host+", realm="+realm);
		}

		@Override
		public void onTooManyRedirects(WebView view, Message cancelMsg, Message continueMsg) {
			Log.d(InnerTAG, "[onTooManyRedirects] view="+view+", cancelMsg="+cancelMsg+", continueMsg="+continueMsg);
			super.onTooManyRedirects(view, cancelMsg, continueMsg);
		}

		@Override
		public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
			Log.d(InnerTAG, "[onUnhandledKeyEvent] view="+view+", event="+event);
			super.onUnhandledKeyEvent(view, event);				
		}
		
		@Override
		public void onFormResubmission(WebView view, Message dontResend, Message resend) {
			Log.d(InnerTAG, "[onFormResubmission] view="+view+", dontResend="+dontResend+", resend="+resend);
			super.onFormResubmission(view, dontResend, resend);
		}
	}
	
	private int getCodeFromSnRequested(String from){
		int code = Constants.FB_CODE;
		if ("twitter".equalsIgnoreCase(from)) code = Constants.TW_CODE;
		if ("foursquare".equalsIgnoreCase(from)) code = Constants.FQ_CODE;
		if ("linkedin".equalsIgnoreCase(from)) code = Constants.LK_CODE;
		if ("facebook".equalsIgnoreCase(from)) code = Constants.FB_CODE;
		if ("googleplus".equalsIgnoreCase(from)) code = Constants.GP_CODE;
		
		return code;
	}
	
	private class TokenListener implements SocialNetworkTokenListener{
		
		private String snRequested = null;
		
		public TokenListener(String snRequested){
			this.snRequested = snRequested;
		}

		@Override
		public void onTokenAvailable(Token accessToken) {
			
			String finalAccessToken = "";
			int tokenExpiration = -1;
			
			if(accessToken != null){
				tokenExpiration = 3600;
				if(socialNetworkRequested.equals(Constants.FB_URL) || socialNetworkRequested.equals(Constants.FQ_URL)){
					// oauth1
					finalAccessToken = accessToken.getToken();
				} else if(socialNetworkRequested.equals(Constants.TW_URL) || socialNetworkRequested.equals(Constants.LK_URL)){
					// oauth2
					finalAccessToken = accessToken.getToken()+","+accessToken.getSecret();
				}
			} 
			
			Intent intent = WebActivity.this.getIntent();
    		intent.putExtra(Constants.ACCESS_TOKEN, finalAccessToken);
    		intent.putExtra(Constants.TOKEN_EXPIRATION, tokenExpiration);
    		setResult(getCodeFromSnRequested(snRequested), intent);
    		finish();
		}
	}
}
