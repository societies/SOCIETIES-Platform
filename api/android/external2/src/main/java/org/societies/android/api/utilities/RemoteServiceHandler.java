/**
Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 

(SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp (IBM),
INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
ITALIA S.p.a.(TI), TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.android.api.utilities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class RemoteServiceHandler extends Handler {
	private static final String BUNDLE_GET_METHOD_PREFIX = "get";
	private static final String BUNDLE_ARRAY_GET_METHOD_SUFFIX = "Array";
	private static final String BUNDLE_GET_PARCELABLE_METHOD = "getParcelable";
	public static final String JAVA_ARRAY = "[]";
	public static final String JAVA_OBJECT_CLASSNAME = "java.lang.Object";
	
	//It was originally possible to change the Android logging behaviour via an APK manifest. This 
	//no longer happens and consequently flag required to control class's logging behaviour
	private static final boolean DEBUG_LOGGING = false;

	private static final String LOG_TAG = RemoteServiceHandler.class.getName();
	private Class <?> container;
	private Object containerObject;
	private String [] methodsArray;
	
		
	/**
	 * Default constructor 
	 * @param container class of object which will be using the handler
	 * @param containerObject object which will be using the handler
	 * @param methodsArray methods array from interface being implemented by object
	 */
	public RemoteServiceHandler(Class <?> container, Object containerObject, String [] methodsArray) {
		super();
		this.container = container;
		this.containerObject = containerObject;
		this.methodsArray = methodsArray;
	}

	@Override
	public void handleMessage(Message message) {
		String targetMethod = ServiceMethodTranslator.getMethodSignature(
				this.methodsArray, message.what);

		if (targetMethod != null) {
			try {
				if (DEBUG_LOGGING){
					Log.d(LOG_TAG, "Target method: " + targetMethod);
				};

				Class <?> parameterClasses[] = ServiceMethodTranslator.getParameterClasses(targetMethod);
				
				for (Class <?> element : parameterClasses) {
					if (DEBUG_LOGGING){
						Log.d(LOG_TAG,"Target method param types: " + element.getName());
					};
				}

				Method method = this.container.getMethod(ServiceMethodTranslator.getMethodName(this.methodsArray,message.what), parameterClasses);
				
				if (DEBUG_LOGGING){
					Log.d(LOG_TAG, "Found method: " + method.getName());
				};
				try {
					Object params[] = new Object[ServiceMethodTranslator
							.getParameterNumber(targetMethod)];
					if (DEBUG_LOGGING){
						Log.d(LOG_TAG, "Number of parameters: " + params.length);
					};

					String paramTypeList[] = ServiceMethodTranslator.getMethodParameterTypesCapitalised(targetMethod);

					for (String type : paramTypeList) {
						if (DEBUG_LOGGING){
							Log.d(LOG_TAG, "Parameter type: " + type);
						};
					}

					String paramNameList[] = ServiceMethodTranslator.getMethodParameterNames(targetMethod);

					// unless the class loader is set bad things happen
					Bundle bundle = message.getData();
					bundle.setClassLoader(this.container.getClassLoader());

					for (int i = 0; i < paramTypeList.length; i++) {
						Class <?> bundleParam[] = { String.class };
						if (DEBUG_LOGGING){
							Log.d(LOG_TAG, "param list: " + paramNameList[i]);
						};
						Object bundleValue[] = { paramNameList[i] };

						Method bundleMethod = null;

						if (implementsParcelable(parameterClasses[i])) {
							if (DEBUG_LOGGING){
								Log.d(LOG_TAG, "Class: " + parameterClasses[i] + " is an instance of Parcelable");
							};
							bundleMethod = Bundle.class.getMethod(BUNDLE_GET_PARCELABLE_METHOD, bundleParam);
						} else if (parameterClasses[i].isArray()) {
							if (DEBUG_LOGGING){
								Log.d(LOG_TAG, "Class: " + parameterClasses[i] + " is an array");
							};
							bundleMethod = Bundle.class.getMethod(createBundleArrayMethod(paramTypeList[i]), bundleParam);
						} else {
							bundleMethod = Bundle.class.getMethod(BUNDLE_GET_METHOD_PREFIX + paramTypeList[i], bundleParam);
						}
						if (DEBUG_LOGGING){
							Log.d(LOG_TAG,"Method invoked: " + bundleMethod.getName());
						};

						params[i] = bundleMethod.invoke(bundle, bundleValue);
						if (DEBUG_LOGGING){
							Log.d(LOG_TAG, "parameter i = " + i + " value: " + params[i]);
						};
					}
					method.invoke((this.container.cast(this.containerObject)) , params);
				} catch (IllegalArgumentException e) {
					Log.e(LOG_TAG, "Illegal arguement", e);
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					Log.e(LOG_TAG, "Illegal access", e);
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					Log.e(LOG_TAG, "Method invocation exception", e);
					e.printStackTrace();
				}
			} catch (SecurityException e) {
				Log.e(LOG_TAG, "Security exception", e);
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				Log.e(LOG_TAG, "No such method", e);
				e.printStackTrace();
			}
		}
	}

	/**
	 * Determine if a class implements the Parcelable interface
	 * The Java Object is also treated as a being a Parcelable
	 * 
	 * @param clazz
	 * @return boolean
	 */
	private boolean implementsParcelable(Class <?> clazz) {
		boolean retValue = false;

		Class <?> interfaces[] = clazz.getInterfaces();
		if (clazz.getName().equals(JAVA_OBJECT_CLASSNAME)) {
			retValue = true;
		} else {
			for (Class <?> interfaze : interfaces) {
				if (DEBUG_LOGGING){
					Log.d(LOG_TAG, "interface: " + interfaze.getSimpleName());
				};
				if (interfaze.getSimpleName().equals("Parcelable")) {
					retValue = true;
					break;
				}
			}
		}

		return retValue;
	}
	/**
	 * Get the relevant Android Bundle get array method for a given array parameter type
	 * 
	 * @param parameterType
	 * @return String method
	 */
	private String createBundleArrayMethod(String parameterType) {
		StringBuffer method = new StringBuffer();
		
		if (parameterType.contains(JAVA_ARRAY)) {
			method.append(BUNDLE_GET_METHOD_PREFIX);
			method.append(parameterType.replace(JAVA_ARRAY, ""));
			method.append(BUNDLE_ARRAY_GET_METHOD_SUFFIX);
		}
		return method.toString();
	}
}
