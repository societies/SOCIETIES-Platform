/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.security.digsig.sign;

import java.lang.ref.WeakReference;

import org.societies.security.digsig.api.Verify;
import org.societies.security.digsig.apiinternal.RestServer;
import org.societies.security.digsig.utility.RandomString;
import org.societies.security.digsig.utility.UrlParamName;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

/**
 * Service to be used by other processes (other apps).
 *
 * @author mitjav
 *
 */
public class SignServiceRemote extends Service {

	private static final String TAG = SignServiceRemote.class.getSimpleName();
	
	/**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler(this));

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        return mMessenger.getBinder();
    }

    /**
     * Handler of incoming messages from clients.
     */
    static class IncomingHandler extends Handler {
    	
    	private final WeakReference<SignServiceRemote> mService;
    	
    	IncomingHandler(SignServiceRemote service) {
    		mService = new WeakReference<SignServiceRemote>(service);
    	}
    	
        @Override
        public void handleMessage(Message msg) {
        	
            Log.i(TAG, "handleMessage: msg.what = " + msg.what + ", replyTo = " + msg.replyTo);
            
            switch (msg.what) {
                case Verify.Methods.GET_CERTIFICATE:
                	mService.get().getCertificate();
                    break;
                case Verify.Methods.VERIFY:
                	mService.get().verify();
                    break;
                case Verify.Methods.GENERATE_URIS:
                	Message message = Message.obtain(null, Verify.Methods.GENERATE_URIS, 0, 0);
                	message.setData(mService.get().generateUris());
                	try {
                		msg.replyTo.send(message);
                	} catch (RemoteException e) {
                		Log.i(TAG, "handleMessage: sending return message", e);
                	}
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private void getCertificate() {
    	Log.w(TAG, "getCertificate: Not yet implemented");
    	// TODO
    }

    private void verify() {
    	Log.w(TAG, "verify: Not yet implemented");
    	// TODO
    }

    private Bundle generateUris() {
    	
    	Log.i(TAG, "generateUris");
    	
    	String host = "http://192.168.1.92";
    	String resourceName = RandomString.getRandomNumberString();
    	String cert = "certificate";  // TODO
    	String notificationEndpoint = "http://192.168.1.92/community-signature/notification-endpoint";  // TODO
    	String signature = "signature";  // TODO
    	
    	String uploadUri = uriForFileUpload(host, resourceName, cert, notificationEndpoint);
    	String downloadUri = uriForFileDownload(host, resourceName, signature);
    	
    	Bundle bundle = new Bundle();
    	bundle.putString(Verify.Params.UPLOAD_URI, uploadUri);
    	bundle.putString(Verify.Params.DOWNLOAD_URI, downloadUri);

    	return bundle;
    }
	
	/**
	 * URI for file download and file merge
	 */
	private String uriForFileDownload(String host, String path, String signature) {
		
		String uriStr;
		
		Log.d(TAG, "uriForFileDownload: host = " + host + ", path = " + path);
		
		uriStr = host + RestServer.BASE + RestServer.PATH_XML_DOCUMENTS + "/" + path.replaceAll(".*/", "") +
				"?" + RestServer.URL_PARAM_SIGNATURE + "=" + signature;

		Log.d(TAG, "uriForFileDownload(): uri = " + uriStr);
		return uriStr;
	}
	
	private String uriForFileUpload(String host, String path, String cert, String notificationEndpoint) {
		
		String uriStr;

		Log.d(TAG, "uriForFileUpload: host = " + host + ", path = " + path);

		cert = UrlParamName.base64ToUrl(cert);
		
		uriStr = host + RestServer.BASE + RestServer.PATH_XML_DOCUMENTS + "/" + path.replaceAll(".*/", "") +
				"?" + RestServer.URL_PARAM_CERT + "=" + cert +
				"&" + RestServer.URL_PARAM_NOTIFICATION_ENDPOINT + "=" + notificationEndpoint +
				"&" + RestServer.URL_PARAM_NUM_SIGNERS_THRESHOLD + "=" + 2; 

		Log.d(TAG, "uriForFileUpload(): uri = " + uriStr);
		return uriStr;
	}
}
