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
package org.societies.android.platform.phongegap;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.societies.android.api.common.ADate;
import org.societies.android.api.internal.privacytrust.trust.IInternalTrustClient;
import org.societies.android.api.utilities.ServiceMethodTranslator;
import org.societies.android.privacytrust.trust.TrustClientLocal;
import org.societies.android.privacytrust.trust.TrustClientLocal.TrustClientLocalBinder;
import org.societies.api.schema.privacytrust.trust.broker.TrustQueryBean;
import org.societies.api.schema.privacytrust.trust.model.ExtTrustRelationshipBean;
import org.societies.api.schema.privacytrust.trust.model.TrustEvidenceBean;
import org.societies.api.schema.privacytrust.trust.model.TrustEvidenceTypeBean;
import org.societies.api.schema.privacytrust.trust.model.TrustRelationshipBean;
import org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 2.0
 */
public class TrustPlugin extends Plugin {

	/** The logging tag */
	private static final String LOG_TAG = TrustPlugin.class.getName();
	
	/** A constant denoting an empty binary array. */
	private static final byte[] NULL_BINARY_ARRAY = new byte[] { Byte.MIN_VALUE };
	
	/** The start index in the IInternalTrustClient.methodsArray. */
	private static final int METHOD_IDX = 10;

	/**
	 * Actions required to bind and unbind to any Android service(s) 
	 * required by this plugin. It is imperative that dependent 
	 * services are binded to before invoking invoking methods.
	 */
	private static final String CONNECT_SERVICE = "connectService";
	private static final String DISCONNECT_SERVICE = "disconnectService";
	
	/** Required to match method calls with callbackIds */
	private Map<String, String> methodCallbacks;;

	private IInternalTrustClient trustClient;
    private boolean trustClientConnected = false;
    
    public TrustPlugin() {
    	
    	super();
    	this.methodCallbacks = new ConcurrentHashMap<String, String>();
    }

    /**
     *  IInternalTrustClient service connection
     */
    private ServiceConnection trustClientConnection = new ServiceConnection() {

    	/*
    	 * @see android.content.ServiceConnection#onServiceConnected(android.content.ComponentName, android.os.IBinder)
    	 */
    	@Override
    	public void onServiceConnected(ComponentName name, IBinder binder) {
    		
        	Log.d(LOG_TAG, "Connecting to IInternalTrustClient service");
        	
        	// Get local service binder
        	final TrustClientLocalBinder localBinder = (TrustClientLocalBinder) binder;
        	// Obtain the service's API
            TrustPlugin.this.trustClient = (IInternalTrustClient) localBinder.getService();
        	TrustPlugin.this.trustClientConnected = true;
            
            final String methodCallbackId = TrustPlugin.this.methodCallbacks.get(CONNECT_SERVICE);
			final PluginResult result = new PluginResult(PluginResult.Status.OK, "connected");
			result.setKeepCallback(false);
			TrustPlugin.this.success(result, methodCallbackId);
			
			Log.d(LOG_TAG, "Successfully connected to IInternalTrustClient service");
        }
    	
    	/*
    	 * @see android.content.ServiceConnection#onServiceDisconnected(android.content.ComponentName)
    	 */
    	@Override
        public void onServiceDisconnected(ComponentName name) {
        	
        	Log.d(LOG_TAG, "Disconnecting from IInternalTrustClient service");
        	TrustPlugin.this.trustClientConnected = false;
        }
    };
    
    /*
	 * @see org.apache.cordova.api.Plugin#onDestroy()
	 */
	@Override
	public void onDestroy() {
	
		Log.d(LOG_TAG, "onDestroy");
		this.disconnectServiceBinding();
    }
    
    /*
     * @see org.apache.cordova.api.Plugin#execute(java.lang.String, org.json.JSONArray, java.lang.String)
     */
	@Override
	public PluginResult execute(String action, JSONArray data, String callbackId) {
		
		Log.d(LOG_TAG, "execute: action=" + action + ", data=" + data 
				+ ", callbackId=" + callbackId);
		PluginResult result = null;
		
		if (CONNECT_SERVICE.equals(action)) {

			if (!this.trustClientConnected) {
				// Add action-callbackId mapping
				this.methodCallbacks.put(action, callbackId);
				result = new PluginResult(PluginResult.Status.NO_RESULT);
	            result.setKeepCallback(true);
				this.initialiseServiceBinding();
			} else {
				// Already connected
	            result = new PluginResult(PluginResult.Status.OK, "connected");
	            result.setKeepCallback(false);
			}           
			
		} else if (DISCONNECT_SERVICE.equals(action)) {

			this.disconnectServiceBinding();
            result = new PluginResult(PluginResult.Status.OK, "disconnected");
            result.setKeepCallback(false);
            
		} else if (this.trustClientConnected && this.isValidAction(action)) {
			
			// Add action-callbackId mapping
			this.methodCallbacks.put(action, callbackId);
			
			try {
				// IInternalTrustClient methods
				if (action.equals(ServiceMethodTranslator.getMethodName(IInternalTrustClient.methodsArray, 10))) {
					// retrieveTrustRelationships(client, query)
					this.trustClient.retrieveTrustRelationships(data.getString(0), 
							this.toTrustQuery(data.getJSONObject(1)));

				} else if (action.equals(ServiceMethodTranslator.getMethodName(IInternalTrustClient.methodsArray, 13))) {
					// retrieveExtTrustRelationships(client, query)
					this.trustClient.retrieveExtTrustRelationships(data.getString(0), 
							this.toTrustQuery(data.getJSONObject(1)));
				
				} else if (action.equals(ServiceMethodTranslator.getMethodName(IInternalTrustClient.methodsArray, 15))) {
					// addDirectTrustEvidence(client, subjectId, objectId, type, timestamp, Serializable info)
					final TrustEvidenceTypeBean evidenceType = 
							TrustEvidenceTypeBean.fromValue(data.getString(3)); 	
					final Serializable info;
					if (data.get(5) instanceof Serializable) {
						if (TrustEvidenceTypeBean.RATED == evidenceType && data.get(5) instanceof Number) {
							info = new Double(((Number) data.get(5)).doubleValue());
						} else {
							info = (Serializable) data.get(5);
						}
					} else {
						info = null;
					}
					this.trustClient.addDirectTrustEvidence(data.getString(0), 
							this.toTrustedEntityId(data.getJSONObject(1)),
							this.toTrustedEntityId(data.getJSONObject(2)),
							evidenceType,
							new ADate(new Date(data.getLong(4))),
							info);
				} else {
					// Action did not match any of the IInternalTrustClient methods
					Log.e(LOG_TAG, "Unexpected action received: " + action);
					// Remove action-callbackId mapping
					this.methodCallbacks.remove(action);
					// Return result with ERROR status
					result = new PluginResult(PluginResult.Status.ERROR);
		            result.setKeepCallback(false);
		            
		            return result;
				}
				
				// Async action successfully sent to IInternalTrustClient.
				// Don't return any result now, since status results 
				// will be sent when events come in from the broadcast receiver
	            result = new PluginResult(PluginResult.Status.NO_RESULT);
	            result.setKeepCallback(true);
				
			} catch (Exception e) {
				
				Log.e(LOG_TAG, "Could not execute action '" + action + "': " 
						+ e.getLocalizedMessage(), e);
				// Remove action-callbackId mapping
				this.methodCallbacks.remove(action);
				// Return result with ERROR status
				result = new PluginResult(PluginResult.Status.ERROR);
	            result.setKeepCallback(false);
			}
			
		} else {
			// Not connected to IInternalTrustClient service or invalid action
			Log.e(LOG_TAG, "Could not execute action '" + action + "': " 
					+ ((this.trustClientConnected) 
							? "Invalid action"
							: "TrustClient service not connected"));
            result = new PluginResult(PluginResult.Status.ERROR);
            result.setKeepCallback(false);
		}
		
		return result;	
	}

    /**
     * Broadcast receiver to receive intent return values from service method calls
     */
	private class ServiceBroadcastReceiver extends BroadcastReceiver  {
		
		/*
		 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
		 */
		@Override
		public void onReceive(Context context, Intent intent) {
			
			Log.d(LOG_TAG, "onReceive: action=" + intent.getAction());
			
			final String methodCallbackKey;
			final String methodCallbackId;
			
			// IInternalTrustClient methods
			if (IInternalTrustClient.RETRIEVE_TRUST_RELATIONSHIPS.equals(intent.getAction())) {
				
				methodCallbackKey = ServiceMethodTranslator.getMethodName(
						IInternalTrustClient.methodsArray, 10);
				methodCallbackId = TrustPlugin.this.methodCallbacks.get(methodCallbackKey);
				if (methodCallbackId != null) {					
					// Unmarshall intent
					final String exceptionMessage = intent.getStringExtra(
							IInternalTrustClient.INTENT_EXCEPTION_KEY);
					if (exceptionMessage != null) {
						Log.e(LOG_TAG, "Failed to retrieve trust relationships: " + exceptionMessage);
						PluginResult result = new PluginResult(PluginResult.Status.ERROR);
						result.setKeepCallback(false);
						TrustPlugin.this.error(result, methodCallbackId);
						TrustPlugin.this.methodCallbacks.remove(methodCallbackKey);
						return;
					}
					PluginResult result = null;
					try {
						final Parcelable[] pTrustRelationships = (Parcelable[]) 
								intent.getParcelableArrayExtra(IInternalTrustClient.INTENT_RETURN_VALUE_KEY);
						final TrustRelationshipBean[] trustRelationships;
						if (pTrustRelationships != null && pTrustRelationships.length > 0) {
							trustRelationships = new TrustRelationshipBean[pTrustRelationships.length];
							System.arraycopy(pTrustRelationships, 0, 
									trustRelationships, 0, pTrustRelationships.length);
						} else {
							trustRelationships = new TrustRelationshipBean[0]; 
						}
						// Return result as JSONArray
						result = new PluginResult(PluginResult.Status.OK, 
								TrustPlugin.this.toTrustRelationshipJSONArray(trustRelationships));
						result.setKeepCallback(false);
						TrustPlugin.this.success(result, methodCallbackId);
					} catch (Exception e) {
						Log.e(LOG_TAG, "Failed to handle result of retrieve trust relationships: " 
								+ e.getLocalizedMessage(), e);
						result = new PluginResult(PluginResult.Status.ERROR);
						result.setKeepCallback(false);
						TrustPlugin.this.error(result, methodCallbackId);
					}
					
				} else {
					Log.e(LOG_TAG, "Could not find matching methodCallbackId for action '"
							+ intent.getAction() + "'");
					return;
				}
				
			} else if (IInternalTrustClient.RETRIEVE_EXT_TRUST_RELATIONSHIPS.equals(intent.getAction())) {
				
				methodCallbackKey = ServiceMethodTranslator.getMethodName(
						IInternalTrustClient.methodsArray, 13);
				methodCallbackId = TrustPlugin.this.methodCallbacks.get(methodCallbackKey);
				if (methodCallbackId != null) {					
					// Unmarshall intent
					final String exceptionMessage = intent.getStringExtra(
							IInternalTrustClient.INTENT_EXCEPTION_KEY);
					if (exceptionMessage != null) {
						Log.e(LOG_TAG, "Failed to retrieve extended trust relationships: " + exceptionMessage);
						PluginResult result = new PluginResult(PluginResult.Status.ERROR);
						result.setKeepCallback(false);
						TrustPlugin.this.error(result, methodCallbackId);
						TrustPlugin.this.methodCallbacks.remove(methodCallbackKey);
						return;
					}
					PluginResult result = null;
					try {
						final Set<ExtTrustRelationshipBean> trustRelationships = 
								new LinkedHashSet<ExtTrustRelationshipBean>();
						final Parcelable[] pTrustRelationships = (Parcelable[]) 
								intent.getParcelableArrayExtra(IInternalTrustClient.INTENT_RETURN_VALUE_KEY);
						for (final Parcelable pTrustRelationship : pTrustRelationships) {
							if (pTrustRelationship instanceof ExtTrustRelationshipBean) {
								final ExtTrustRelationshipBean extTrustRelationship = 
										(ExtTrustRelationshipBean) pTrustRelationship;
								final Iterator<TrustEvidenceBean> evidenceIter = 
										extTrustRelationship.getTrustEvidence().iterator();
								while (evidenceIter.hasNext()) {
									final TrustEvidenceBean evidence = evidenceIter.next();
									if (TrustEvidenceTypeBean.NULL == evidence.getType()) {
										// Remove evidence of type NULL
										evidenceIter.remove();
									} else {
										// Nullify evidence info of type NULL_BINARY_ARRAY
										if (Arrays.equals(NULL_BINARY_ARRAY, evidence.getInfo())) {
											evidence.setInfo(null);
										}
									}
								}
								trustRelationships.add(extTrustRelationship);
							}
						}
						// Return result as JSONArray
						result = new PluginResult(PluginResult.Status.OK, 
								TrustPlugin.this.toExtTrustRelationshipJSONArray(trustRelationships));
						result.setKeepCallback(false);
						TrustPlugin.this.success(result, methodCallbackId);
					} catch (Exception e) {
						Log.e(LOG_TAG, "Failed to handle result of retrieve extended trust relationships: " 
								+ e.getLocalizedMessage(), e);
						result = new PluginResult(PluginResult.Status.ERROR);
						result.setKeepCallback(false);
						TrustPlugin.this.error(result, methodCallbackId);
					}
				} else {
					Log.e(LOG_TAG, "Could not find matching methodCallbackId for action '"
							+ intent.getAction() + "'");
					return;
				}
				
			} else if (IInternalTrustClient.ADD_DIRECT_TRUST_EVIDENCE.equals(intent.getAction())) {
				
				methodCallbackKey = ServiceMethodTranslator.getMethodName(
						IInternalTrustClient.methodsArray, 15);
				methodCallbackId = TrustPlugin.this.methodCallbacks.get(methodCallbackKey);
				if (methodCallbackId != null) {					
					// Unmarshall intent
					final String exceptionMessage = intent.getStringExtra(
							IInternalTrustClient.INTENT_EXCEPTION_KEY);
					if (exceptionMessage != null) {
						Log.e(LOG_TAG, "Failed to add trust evidnece: " + exceptionMessage);
						PluginResult result = new PluginResult(PluginResult.Status.ERROR);
						result.setKeepCallback(false);
						TrustPlugin.this.error(result, methodCallbackId);
						TrustPlugin.this.methodCallbacks.remove(methodCallbackKey);
						return;
					}	
					// Return result
					PluginResult result = new PluginResult(PluginResult.Status.OK, 
							"addedTrustEvidence");
					result.setKeepCallback(false);
					TrustPlugin.this.success(result, methodCallbackId);
					// Show our love
					Toast.makeText(TrustPlugin.this.ctx.getContext(), 
							"Thanks for your feedback - We love you!", Toast.LENGTH_LONG).show();
				} else {
					Log.e(LOG_TAG, "Could not find matching methodCallbackId for action '"
							+ intent.getAction() + "'");
					return;
				}
				
			} else {
				// Unexpected action
				Log.e(LOG_TAG, "Received unexpected action: " + intent.getAction());
				return;
			}
			
			// Remove callback ID for given method invocation
			Log.d(LOG_TAG, "Removing methodCallbackId '" + methodCallbackId + "'");
			TrustPlugin.this.methodCallbacks.remove(methodCallbackKey);
		}
	};
    
	/**
     * Creates a JSONArray representation of the specified trust relationships.
     * 
     * @param trustRelationships
     *            the trust relationships to represent as a JSONArray.
     * @return the specified trust relationships as a JSONArray.
	 * @throws JSONException 
     */
    private JSONArray toTrustRelationshipJSONArray(
    		final TrustRelationshipBean[] trustRelationships) throws JSONException {
    	
    	return (JSONArray) new JSONTokener(new Gson().toJson(
    			trustRelationships)).nextValue();
    }
    
    /**
     * Creates a JSONArray representation of the specified extended trust 
     * relationships.
     * 
     * @param trustRelationships
     *            the extended trust relationships to represent as a JSONArray.
     * @return the specified extended trust relationships as a JSONArray
	 * @throws JSONException 
     */
    private JSONArray toExtTrustRelationshipJSONArray(
    		final Set<ExtTrustRelationshipBean> trustRelationships) throws JSONException {
    	
    	final Set<AExtTrustRelationship> aetrs = 
    			new LinkedHashSet<TrustPlugin.AExtTrustRelationship>();
    	for (final ExtTrustRelationshipBean etr : trustRelationships) {
    		aetrs.add(new AExtTrustRelationship(etr));
    	}
    	return (JSONArray) new JSONTokener(new Gson().toJson(
    			aetrs)).nextValue();
    }
    
    /**
     * Creates a TrustQueryBean instance from the specified JSONObject
     * representation.
     * 
     * @param jtQuery 
     *            JSONObject representation of a TrustQueryBean.
     * @return a TrustQueryBean instance from the specified JSONObject
     *         representation. 
     * @throws JSONException 
     */
    private TrustQueryBean toTrustQuery(JSONObject jtQuery) 
    		throws JSONException {
    	
    	return new Gson().fromJson(jtQuery.toString(), TrustQueryBean.class);
    }
    
    /**
     * Creates a TrustedEntityIdBean instance from the specified JSONObject
     * representation.
     * 
     * @param jTeid 
     *            JSONObject representation of a TrustedEntityIdBean.
     * @return a TrustedEntityIdBean instance from the specified JSONObject
     *         representation. 
     * @throws JSONException 
     */
    private TrustedEntityIdBean toTrustedEntityId(JSONObject jTeid) 
    		throws JSONException {
    	
    	return new Gson().fromJson(jTeid.toString(), TrustedEntityIdBean.class);
    }
	
	/**
     * Bind to the target service(s)
     */
    private void initialiseServiceBinding() {
    	
    	// Create intent to bind to IInternalTrustClient service
    	final Intent trustClientIntent = new Intent(this.ctx.getContext(),
    			TrustClientLocal.class);
    	this.ctx.getContext().bindService(trustClientIntent, 
    			this.trustClientConnection, Context.BIND_AUTO_CREATE);
    	
    	// Register broadcast receiver to receive service return values 
    	final IntentFilter intentFilter = new IntentFilter();
    	// IInternalTrustClient service actions
    	intentFilter.addAction(IInternalTrustClient.RETRIEVE_TRUST_RELATIONSHIPS); 
    	intentFilter.addAction(IInternalTrustClient.RETRIEVE_TRUST_RELATIONSHIP);
    	intentFilter.addAction(IInternalTrustClient.RETRIEVE_TRUST_VALUE);
    	intentFilter.addAction(IInternalTrustClient.RETRIEVE_EXT_TRUST_RELATIONSHIPS);
    	intentFilter.addAction(IInternalTrustClient.ADD_DIRECT_TRUST_EVIDENCE);
    	Log.d(LOG_TAG, "initialiseServiceBinding: intentFilter=" + intentFilter);
    	this.ctx.getContext().registerReceiver(new ServiceBroadcastReceiver(), intentFilter);
    }
    
    /**
     * Unbind from service(s)
     */
    private void disconnectServiceBinding() {
    	
    	if (this.trustClientConnected) {
    		this.ctx.getContext().unbindService(this.trustClientConnection);
    	}
    }
	
    /**
     * Determines if the Javascript action is a valid.
     * 
     * N.B. Assumes that the Javascript method name is the exact same as the 
     * Java implementation. 
     * 
     * @param action
     * @return boolean
     */
    private boolean isValidAction(String action) {
    	
    	// Check IInternalTrustClient methods
    	for (int i = TrustPlugin.METHOD_IDX; i < IInternalTrustClient.methodsArray.length; ++i) {
        	if (action.equals(ServiceMethodTranslator.getMethodName(IInternalTrustClient.methodsArray, i))) {
        		return true;
        	}
    	}
    	
    	return false;
    }
    
    /**
	 * Deserialises an object from the specified byte array
	 * 
	 * @param objectData
	 *            the object to deserialise
	 * @param classLoader
	 *            the <code>ClassLoader</code> to use for deserialisation
	 * @return the deserialised object
	 * @throws IOException if the deserialisation of the specified byte array fails
	 * @throws ClassNotFoundException if the class of the deserialised object cannot be found
	 */
	public Serializable deserialise(byte[] objectData,
			ClassLoader classLoader) throws IOException, ClassNotFoundException {

		final Serializable result;
		CustomObjectInputStream ois = null;
		try {
			ois = new CustomObjectInputStream(
					new ByteArrayInputStream(objectData), classLoader);

			result = (Serializable) ois.readObject();
		} finally {
			if (ois != null)
				ois.close();
		}

		return result;
	}
	
	/**
	 * Credits go to jboss/hibernate for the inspiration
	 */
	private static final class CustomObjectInputStream extends ObjectInputStream {

		// The ClassLoader to use for deserialisation
		private ClassLoader classLoader;

		public CustomObjectInputStream(InputStream is, ClassLoader cl)
				throws IOException {
			super(is);
			this.classLoader = cl;
		}

		protected Class<?> resolveClass(ObjectStreamClass clazz)
				throws IOException, ClassNotFoundException {

			String className = clazz.getName();
			Class<?> resolvedClass = null;

			try {
				resolvedClass = this.classLoader.loadClass(className);
			} catch (ClassNotFoundException e) {
				resolvedClass = super.resolveClass(clazz);
			}

			return resolvedClass;
		}
	}
	
	private class AExtTrustRelationship extends TrustRelationshipBean {
		
		private static final long serialVersionUID = 2100124754703639674L;
		
		private final Set<ATrustEvidence> trustEvidence;
		
		private AExtTrustRelationship(ExtTrustRelationshipBean etr) {
			
			super.setTrustorId(etr.getTrustorId());
			super.setTrusteeId(etr.getTrusteeId());
			super.setTrustValueType(etr.getTrustValueType());
			super.setTrustValue(etr.getTrustValue());
			super.setTimestamp(etr.getTimestamp());
			this.trustEvidence = new LinkedHashSet<TrustPlugin.ATrustEvidence>();
			for (final TrustEvidenceBean evidence : etr.getTrustEvidence()) {
				this.trustEvidence.add(new ATrustEvidence(evidence));
			}
		}
	}
	
	private class ATrustEvidence {
		
		@SuppressWarnings("unused")
		private final TrustedEntityIdBean subjectId;
		
		@SuppressWarnings("unused")
		private final TrustedEntityIdBean objectId;
		
		@SuppressWarnings("unused")
		private final TrustEvidenceTypeBean type;
		
		@SuppressWarnings("unused")
		private final Date timestamp;
		
		@SuppressWarnings("unused")
		private Serializable info;
		
		@SuppressWarnings("unused")
		private final TrustedEntityIdBean sourceId;
		
		private ATrustEvidence(TrustEvidenceBean te) {
			
			this.subjectId = te.getSubjectId();
			this.objectId = te.getObjectId();
			this.type = te.getType();
			this.timestamp = te.getTimestamp();
			if (te.getInfo() != null) {
				try {
					this.info = TrustPlugin.this.deserialise(
							te.getInfo(), this.getClass().getClassLoader());
				} catch (Exception e) {
					Log.e(LOG_TAG, "Could not deserialise trust evidence info: "
							+ e.getLocalizedMessage(), e);
				}
			} else {
				this.info = null;
			}
			this.sourceId = te.getSourceId();
		}
	}
}