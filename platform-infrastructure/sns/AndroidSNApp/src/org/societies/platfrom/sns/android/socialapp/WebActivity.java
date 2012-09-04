package org.societies.platfrom.sns.android.socialapp;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

public class WebActivity extends Activity {

	WebView w;
	LinearLayout loading;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.web);
        
        
        loading = (LinearLayout) findViewById(R.id.loading);
        String url = getIntent().getStringExtra(Constants.SSO_URL);
                       
        w = (WebView)findViewById(R.id.webView1);        
        w.setWebViewClient(client);
        w.getSettings().setJavaScriptEnabled(true);
        w.setWebChromeClient(chrome);
        w.loadUrl(url);
        
        
        WebSettings mWebSettings = w.getSettings();
        mWebSettings.setSavePassword(false);
        
        Log.v(Constants.DEBUG_TAG, "Loading URL: " + url);        
    
    
    
    }
    
    WebViewClient client = new WebViewClient() {
    
    	
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			
			
			loading.setVisibility(View.VISIBLE);
			return super.shouldOverrideUrlLoading(view, url);
//			Log.v(Constants.DEBUG_TAG, "Analizzo URL: " + url);
//			
//			if (url.indexOf("doconnect.php") != -1) {
//				
//				
////	            // do something
////				Log.v(Constants.DEBUG_TAG, ".. Pattern individuato");
////				
////				// "userSignedOn?username=openid"
////				
////				
////
////				try {
////					URL destination = new URL(url);
////					String[] qa = destination.getQuery().split("&");
////					
////					Intent resultData = new Intent("com.tilab.ca.android.intent.SIGNON");
////					
////					// signin
////					
////					Log.v(Constants.DEBUG_TAG, "Path: " + destination.getPath());
////					Log.v(Constants.DEBUG_TAG, "Host: " + destination.getHost());
////					
////					if (destination.getPath().indexOf("userSignedIn") >= 0)
////						resultData.putExtra("signin", true);
////					
////					for (int i=0; i < qa.length; i++) {
////						String[] kv = qa[i].split("=");
////						resultData.putExtra(kv[0], kv[1]);
////					}
////					
////					//resultData.putExtra("username", "openid");
////					
////					setResult(RESULT_OK, resultData);
////					finish();
////					
////				} catch (MalformedURLException e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				}
//
//				 return false;
//				 
//				 
//	        } 
//			else {
//	        	Log.v(Constants.DEBUG_TAG, ".. Pattern NON individuato in: " + url);		        	
//	            view.loadUrl(url);
//	        }
//
//	        return true;
		}
		
		public void onPageFinished(WebView view, String url) {
			loading.setVisibility(View.INVISIBLE);
			Log.v(Constants.DEBUG_TAG, "Finished");
			view.loadUrl("javascript:console.log('MAGIC'+document.getElementsByTagName('body')[0].innerHTML);");
		};
    
    };
    
    
    WebChromeClient chrome = new WebChromeClient() {
       
    	public boolean onConsoleMessage(ConsoleMessage cmsg)
        {
            // check secret prefix
            if (cmsg.message().startsWith("MAGIC"))
            {
                String msg = cmsg.message().substring(5); // strip off prefix
                Log.i("HTML","JSON:  "+msg);
                
                if (msg.indexOf("connector")>0) {
                	try {
                		
                		
                		JSONObject response = new JSONObject(msg);
                		JSONObject conn_resp  = response.getJSONObject("connector");
                		String token 	= conn_resp.getString(Constants.ACCESS_TOKEN);
                		String from		= conn_resp.getString(Constants.FROM);
                		String expires  = "";
                		if (conn_resp.has(Constants.TOKEN_EXPIRATION)) 
                			expires	= conn_resp.getString(Constants.TOKEN_EXPIRATION);
                		
                		
                		int code = Constants.FB_CODE;
                		if ("twitter".equalsIgnoreCase(from)) code = Constants.TW_CODE;
                		if ("foursquare".equalsIgnoreCase(from)) code = Constants.FQ_CODE;
                		
                		Intent intent = WebActivity.this.getIntent();
                		intent.putExtra(Constants.ACCESS_TOKEN, token);
                		intent.putExtra(Constants.TOKEN_EXPIRATION, expires);
                		setResult(code, intent);
					} 
                	
                	catch (JSONException e) {
						e.printStackTrace();
					}
                			
                	
                	finish();
                    
                }
                
                /* process HTML */

                return true;
            }

            return false;
        }
    };
    
   

        
}
