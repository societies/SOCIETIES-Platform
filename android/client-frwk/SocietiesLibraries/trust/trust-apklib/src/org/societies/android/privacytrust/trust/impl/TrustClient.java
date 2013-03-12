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
package org.societies.android.privacytrust.trust.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.jivesoftware.smack.packet.IQ;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.comms.xmpp.ICommCallback;
import org.societies.android.api.comms.xmpp.Stanza;
import org.societies.android.api.comms.xmpp.XMPPError;
import org.societies.android.api.comms.xmpp.XMPPInfo;
import org.societies.android.api.css.manager.IServiceManager;
import org.societies.android.api.privacytrust.trust.ITrustClient;
import org.societies.android.api.privacytrust.trust.ADate;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.android.api.privacytrust.trust.TrustException;
import org.societies.api.schema.privacytrust.trust.model.TrustEvidenceTypeBean;
import org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean;
import org.societies.api.internal.schema.privacytrust.trust.evidence.collector.AddDirectEvidenceRequestBean;
import org.societies.api.internal.schema.privacytrust.trust.evidence.collector.MethodName;
import org.societies.api.internal.schema.privacytrust.trust.evidence.collector.TrustEvidenceCollectorRequestBean;
import org.societies.api.internal.schema.privacytrust.trust.evidence.collector.TrustEvidenceCollectorResponseBean;
import org.societies.identity.IdentityManagerImpl;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Android implementation of the {@link ITrustClient} service.
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.5
 */
public class TrustClient extends Service implements ITrustClient {
	
	private static final String TAG = TrustClient.class.getName();
	
	private static final List<String> ELEMENT_NAMES = Arrays.asList(
			"trustEvidenceCollectorRequestBean", "trustEvidenceCollectorResponseBean");
	
	private static final List<String> NAMESPACES = Arrays.asList(
            "http://societies.org/api/internal/schema/privacytrust/trust/evidence/collector",
            "http://societies.org/api/internal/schema/privacytrust/trust/model");
	
	private static final List<String> PACKAGES = Arrays.asList(
			"org.societies.api.internal.schema.privacytrust.trust.evidence.collector",
	        "org.societies.api.internal.schema.privacytrust.trust.model");
	
	// TODO Don't hardcode!
	private String cloudNodeJid = "jane.societies.local";
	
	private IIdentity cloudNodeId;
	
	/** The Client Comm Mgr service reference. */
	private ClientCommunicationMgr clientCommMgr;
	
	/** The exception from the Client Comm Mgr callback. */
	private Exception callbackException;
	
	/** The latch for waiting the Client Comm Mgr callback. */
	private CountDownLatch cdLatch;
	
	private boolean connectedToComms = false;
	
	/** The Client Comm Mgr callback. */
	// TODO move instantiation to constructor
	private final ICommCallback callback = new ICommCallback() {
		
		/*
		 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#getXMLNamespaces()
		 */
		public List<String> getXMLNamespaces() {
			
			return TrustClient.NAMESPACES;
		}

		/*
		 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#getJavaPackages()
		 */
		public List<String> getJavaPackages() {
			
			return TrustClient.PACKAGES;
		}

		/*
		 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveResult(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
		 */
		public void receiveResult(Stanza stanza, Object payload) {

			Log.d(TrustClient.TAG, "receiveResult:stanza="
					+ this.stanzaToString(stanza)
					+ ",payload.getClass=" 
					+ payload.getClass().getName());

			if (payload instanceof TrustEvidenceCollectorResponseBean) {

				TrustEvidenceCollectorResponseBean responseBean = 
						(TrustEvidenceCollectorResponseBean) payload;
				Log.d(TrustClient.TAG, 
						"receiveResult:payload.methodName=" 
								+ responseBean.getMethodName());
				switch (responseBean.getMethodName()) {

				case ADD_DIRECT_EVIDENCE:
					TrustClient.this.callbackException = null;
					break;
				default:
					TrustClient.this.callbackException = 
						new TrustClientException("Unsupported method in response bean: "
							+ responseBean.getMethodName());
				}
			} else {
				TrustClient.this.callbackException = 
						new TrustClientException("Unsupported payload type: "
								+ payload.getClass().getName());
			}
			TrustClient.this.cdLatch.countDown();
		}

		/*
		 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveError(org.societies.api.comm.xmpp.datatypes.Stanza, org.societies.api.comm.xmpp.exceptions.XMPPError)
		 */
		public void receiveError(Stanza stanza, XMPPError error) {
			
			Log.d(TrustClient.TAG, "receiveError:stanza="
					+ this.stanzaToString(stanza) 
					+ ",error=" + error);
			if (error != null)
				TrustClient.this.callbackException = error;
			else
				TrustClient.this.callbackException = 
					new TrustClientException("Unspecified XMPPError");
			TrustClient.this.cdLatch.countDown();
		}

		/*
		 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveInfo(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.String, org.societies.api.comm.xmpp.datatypes.XMPPInfo)
		 */
		public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
			
			Log.d(TrustClient.TAG, "receiveInfo with stanza "
					+ this.stanzaToString(stanza));
		}

		/*
		 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveMessage(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
		 */
		public void receiveMessage(Stanza stanza, Object payload) {
			
			Log.d(TrustClient.TAG, "receiveMessage with stanza "
					+ this.stanzaToString(stanza));
		}

		/*
		 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveItems(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.String, java.util.List)
		 */
		public void receiveItems(Stanza stanza, String node, List<String> items) {
			
			Log.d(TrustClient.TAG, "receiveItems with stanza "
					+ this.stanzaToString(stanza)
					+ ", node " + node
					+ " and items: ");
			for(String item : items)
				Log.d(TrustClient.TAG, item);
		}
		
		private String stanzaToString(Stanza stanza) {
			
			final StringBuilder sb = new StringBuilder();
			sb.append("[");
			sb.append("id=" + stanza.getId());
			sb.append(",");
			sb.append("from=" + stanza.getFrom());
			sb.append(",");
			sb.append("to=" + stanza.getTo());
			sb.append("]");
			
			return sb.toString();
		}
	};
	
	private final IBinder binder = new LocalBinder();

	/*
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
	
		return this.binder;
	}
	
	/*
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate () {
		
		Log.i(TAG, "Starting");
		try {
			if (this.cloudNodeId == null)
				this.cloudNodeId = IdentityManagerImpl.staticfromJid(cloudNodeJid);
			Log.d(TAG, "Hardcoded cloud node IIdentity " + this.cloudNodeId);
			if (this.clientCommMgr == null)
				this.clientCommMgr = new ClientCommunicationMgr(this, true);
		} catch (InvalidFormatException ife) {
			final String errorText = "Failed to create cloud node IIdentity: " + ife.getLocalizedMessage();
			Log.e(TAG, errorText, ife);
			Toast.makeText(this, errorText, Toast.LENGTH_LONG).show();
			throw new RuntimeException(ife);
		}
	}
	
	/*
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		
		Log.i(TAG, "Stopping");
		this.cloudNodeId = null;
		if (this.clientCommMgr != null) {
			//this.clientCommMgr.unregister(ELEMENT_NAMES, this.callback);
			this.clientCommMgr = null;
		}
	}

	public boolean startService() {
    	if (!connectedToComms) {
        	//NOT CONNECTED TO COMMS SERVICE YET
        	Log.d(TAG, "TrustClient startService binding to comms");
	        this.clientCommMgr.bindCommsService(new IMethodCallback() {	
				@Override
				public void returnAction(boolean resultFlag) {
					Log.d(TAG, "Connected to comms: " + resultFlag);
					if (resultFlag) {
						connectedToComms = true;
						//REGISTER NAMESPACES
						clientCommMgr.register(ELEMENT_NAMES, NAMESPACES, PACKAGES, new IMethodCallback() {
							@Override
							public void returnAction(boolean resultFlag) {
								Log.d(TAG, "Namespaces registered: " + resultFlag);
								//SEND INTENT WITH SERVICE STARTED STATUS
				        		Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
				        		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, resultFlag);
				        		TrustClient.this.getApplicationContext().sendBroadcast(intent);
							}
							@Override
							public void returnAction(String result) { }
						});
					} else {
						Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
			    		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, false);
			    		TrustClient.this.getApplicationContext().sendBroadcast(intent);
					}
				}	
				@Override
				public void returnAction(String result) { }
			});
        }
    	else {
    		Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
    		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, true);
    		TrustClient.this.getApplicationContext().sendBroadcast(intent);
    	}
		return true;
    }
    
    public boolean stopService() {
    	if (connectedToComms) {
        	//UNREGISTER AND DISCONNECT FROM COMMS
        	Log.d(TAG, "TrustClient stopService unregistering namespaces");
        	clientCommMgr.unregister(ELEMENT_NAMES, NAMESPACES, new IMethodCallback() {
				@Override
				public void returnAction(boolean resultFlag) {
					Log.d(TAG, "Unregistered namespaces: " + resultFlag);
					connectedToComms = false;
					
					clientCommMgr.unbindCommsService();
					//SEND INTENT WITH SERVICE STOPPED STATUS
	        		Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STOPPED_STATUS);
	        		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, true);
	        		TrustClient.this.getApplicationContext().sendBroadcast(intent);
				}	
				@Override
				public void returnAction(String result) { }
			});
        }
    	else {
    		Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STOPPED_STATUS);
    		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, true);
    		TrustClient.this.getApplicationContext().sendBroadcast(intent);
    	}
    	return true;
    }
    
    /**
	 * @param client
	 */
	private void broadcastServiceNotStarted(String client, String method) {
		if (client != null) {
			Intent intent = new Intent(method);
			intent.putExtra(IServiceManager.INTENT_NOTSTARTED_EXCEPTION, true);
			intent.setPackage(client);
			TrustClient.this.getApplicationContext().sendBroadcast(intent);
		}
	}
	
	@Override
	public void retrieveTrust(final String client, final TrustedEntityIdBean trustorId, final TrustedEntityIdBean trusteeId) {
		
	}
	
	@Override
	public void addTrustEvidence(final String client, 
			final TrustedEntityIdBean subjectId, final TrustedEntityIdBean objectId,
			final TrustEvidenceTypeBean type, final ADate timestamp, 
			final Serializable info) {
		/*
		if (client == null)
			throw new NullPointerException("client can't be null");
		if (subjectId == null)
			throw new NullPointerException("subjectId can't be null");
		if (objectId == null)
			throw new NullPointerException("objectId can't be null");
		if (type == null)
			throw new NullPointerException("type can't be null");
		if (timestamp == null)
			throw new NullPointerException("timestamp can't be null");
		
		final StringBuilder sb = new StringBuilder();
		sb.append("Adding direct trust evidence:");
		sb.append("client=");
		sb.append(client);
		sb.append(", subjectId=");
		sb.append(subjectId);
		sb.append(", objectId=");
		sb.append(objectId);
		sb.append(", type=");
		sb.append(type);
		sb.append(", timestamp=");
		sb.append(timestamp);
		sb.append(", info=");
		sb.append(info);
		Log.d(TAG, sb.toString());

		try {
			final AddDirectEvidenceRequestBean addEvidenceBean = 
					new AddDirectEvidenceRequestBean();
			// 1. subjectId
			addEvidenceBean.setSubjectId(subjectId);
			// 2. objectId
			addEvidenceBean.setObjectId(objectId);
			// 3. type
			addEvidenceBean.setType(type);
			// 4. timestamp
			final GregorianCalendar gregCal = new GregorianCalendar();
			gregCal.setTime(timestamp.getDate());
			addEvidenceBean.setTimestamp(
					new DatatypeFactoryImpl().newXMLGregorianCalendar(gregCal));
			// 5. info
			if (info != null)
				addEvidenceBean.setInfo(serialise(info));

			final TrustEvidenceCollectorRequestBean requestBean = 
					new TrustEvidenceCollectorRequestBean();
			requestBean.setMethodName(MethodName.ADD_DIRECT_EVIDENCE);
			requestBean.setAddDirectEvidence(addEvidenceBean);

			final Stanza stanza = new Stanza(this.cloudNodeId);
			this.clientCommMgr.sendIQ(stanza, IQ.Type.GET, requestBean, this.callback);
			this.cdLatch = new CountDownLatch(1);
			this.cdLatch.await();
			//if (this.callbackException != null)
			//	throw this.callbackException;
			
		} catch (IOException ioe) {

			final String errorMessage = 
					"Could not add trust evidence for subject '" + subjectId
					+ "' and object '" + objectId + "' of type '" + type
					+ ": Could not serialise info object into byte[]: " 
					+ ioe.getLocalizedMessage();
			Log.e(TAG, errorMessage);
			//throw new TrustClientException(errorMessage, ioe);
			
		} catch (Exception e) {
			
			final String errorMessage =
					"Could not add direct trust evidence for subject '" 
					+ subjectId	+ "' and object '" + objectId + "' of type '"
					+ type + ": " + e.getLocalizedMessage();
			Log.e(TAG, errorMessage);
			//throw new TrustClientException(errorMessage, e);
		}*/
	}
	
	public class LocalBinder extends Binder {
		
		public ITrustClient getService() {
			return TrustClient.this;
		}
	}
	
	/**
	 * Serialises the specified object into a byte array
	 * 
	 * @param object
	 *            the object to serialise
	 * @return a byte array of the serialised object
	 * @throws IOException if the serialisation of the specified object fails
	 */
	private static byte[] serialise(Serializable object) throws IOException {

		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(object);
		
		return baos.toByteArray();
	}
}
