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
package org.societies.android.platform.utilities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.societies.android.platform.interfaces.ICoreServiceMonitor;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class RemoteServiceHandler extends Handler {
	private static final String LOG_TAG = RemoteServiceHandler.class.getName();
	private Class container;
	private Object containerObject;
		
	public RemoteServiceHandler(Class container, Object containerObject) {
		super();
		this.container = container;
		this.containerObject = containerObject;
	}

	@Override
	public void handleMessage(Message message) {
		String targetMethod = ServiceMethodTranslator.getMethodSignature(
				ICoreServiceMonitor.methodsArray, message.what);

		if (targetMethod != null) {
			try {
				Log.d(LOG_TAG, "Target method: " + targetMethod);

				Class parameterClasses[] = ServiceMethodTranslator
						.getParameterClasses(targetMethod);
				for (Class element : parameterClasses) {
					Log.d(LOG_TAG,
							"Target method param types: " + element.getName());

				}

				Method method = this.container.getMethod(
								ServiceMethodTranslator.getMethodName(
										ICoreServiceMonitor.methodsArray,
										message.what), parameterClasses);
				Log.d(LOG_TAG, "Found method: " + method.getName());
				try {
					Object params[] = new Object[ServiceMethodTranslator
							.getParameterNumber(targetMethod)];
					Log.d(LOG_TAG, "Number of parameters: " + params.length);

					String paramTypeList[] = ServiceMethodTranslator
							.getMethodParameterTypesCapitalised(targetMethod);

					for (String type : paramTypeList) {
						Log.d(LOG_TAG, "Parameter type: " + type);
					}

					String paramNameList[] = ServiceMethodTranslator
							.getMethodParameterNames(targetMethod);

					// unless the class loader is set bad things happen
					Bundle bundle = message.getData();
					bundle.setClassLoader(this.container.getClassLoader());

					for (int i = 0; i < paramTypeList.length; i++) {
						Class bundleParam[] = { String.class };
						Log.d(LOG_TAG, "param list: " + paramNameList[i]);
						Object bundleValue[] = { paramNameList[i] };

						Method bundleMethod = null;

						if (implementsParcelable(parameterClasses[i])) {
							Log.d(LOG_TAG, "Class: " + parameterClasses[i]
									+ " is an instance of Parcelable");
							bundleMethod = Bundle.class.getMethod(
									"getParcelable", bundleParam);
						} else {
							bundleMethod = Bundle.class.getMethod("get"
									+ paramTypeList[i], bundleParam);
						}
						Log.d(LOG_TAG,
								"Method invoked: " + bundleMethod.getName());

						params[i] = bundleMethod.invoke(bundle, bundleValue);
						Log.d(LOG_TAG, "parameter i = " + i + " value: "
								+ params[i]);
					}
					method.invoke((this.container.cast(this.containerObject)) , params);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Determine if a class implements the Parcelable interface
	 * 
	 * @param clazz
	 * @return boolean
	 */
	private boolean implementsParcelable(Class clazz) {
		boolean retValue = false;

		Class interfaces[] = clazz.getInterfaces();
		for (Class interfaze : interfaces) {
			Log.d(LOG_TAG, "interface: " + interfaze.getSimpleName());
			if (interfaze.getSimpleName().equals("Parcelable")) {
				retValue = true;
				break;
			}
		}

		return retValue;
	}
}


