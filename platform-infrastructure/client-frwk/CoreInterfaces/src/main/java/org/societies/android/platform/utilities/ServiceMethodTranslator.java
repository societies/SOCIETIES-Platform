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

import java.util.ArrayList;
import java.util.List;

import org.societies.utilities.DBC.Dbc;

/**
 * This class provides a set of static methods to help make the Service Messenger API calling mechanism
 * as simple as possible while allowing the service and the consumer to share a common service API. 
 * 
 * TODO parameter arrays 
 */

public class ServiceMethodTranslator {
	
	private static final String ParameterStart = "(";
	private static final String ParameterEnd = ")";
	private static final String PACKAGE_SEPARATOR = ".";
	private static final String ParameterDelimiter = ",";
	private static final String ParameterSeparator = " ";
	public static final String JAVA_LANG_PREFIX = "java.lang.";
	public static final String ANDROID_OS_PREFIX = "android.os.";
	
	public static final String JAVA_PRIMITIVES [] = {"int", "long", "double", "float", "byte", "char", "boolean", "short"};
	public static final String JAVA_LANG_CLASSES [] = {"String"};
	
	/**
	 * Determine whether a method signature exists and if it does what is its
	 * index number.
	 *  
	 * @param methodsArray
	 * @param methodName
	 * @return int index of method in interface list of methods. -1 indicates that a
	 * method does not exist.
	 */
	public static int getMethodIndex(String [] methodsArray, String methodName) {
		Dbc.require("An array of method(s) must be supplied", methodsArray != null && methodsArray.length > 0);
		Dbc.require("A method must be specified", methodName != null && methodName.length() > 0);
		Dbc.require("A method must have " + ParameterStart + " and " + ParameterEnd + " characters", methodName.contains(ParameterStart) && methodName.contains(ParameterEnd));
		
		int retValue = -1;
		for (int i = 0; i < methodsArray.length; i++) {
			if (methodsArray[i].equals(methodName)) {
				retValue = i;
				break;
			}
		}
		return retValue;
	}
	
	/**
	 * Determine the corresponding method name for a given index
	 * 
	 * @param methodsArray
	 * @param index
	 * @return String method signature
	 */
	public static String getMethodName(String [] methodsArray, int index) {
		Dbc.require("An array of method(s) must be supplied", methodsArray != null && methodsArray.length > 0);
		Dbc.require("Method index must be 0 or greater", index >= 0 && index < methodsArray.length);
		
		return parseMethod(methodsArray[index]);
	}
	/**
	 * Determine the corresponding method signature for a given index
	 * 
	 * @param methodsArray
	 * @param index
	 * @return String method signature
	 */
	public static String getMethodSignature(String [] methodsArray, int index) {
		Dbc.require("An array of method(s) must be supplied", methodsArray != null && methodsArray.length > 0);
		Dbc.require("Method index must be 0 or greater", index >= 0 && index < methodsArray.length);
		
		return methodsArray[index];
	}
	
	/**
	 * How many parameters does a method signature contain
	 * 
	 * @param methodName
	 * @return int number of parameters
	 */
	public static int getParameterNumber(String methodName) {
		Dbc.require("A method must be specified", methodName != null && methodName.length() > 0);
		Dbc.require("A method must have " + ParameterStart + " and " + ParameterEnd + " characters", methodName.contains(ParameterStart) && methodName.contains(ParameterEnd));
		
		return getMethodParameterTypes(methodName).length;
	}
	
	/**
	 * Determine the parameter type for a given parameter index
	 * 
	 * @param methodName
	 * @param parameter
	 * @return String parameter type
	 */
	public static String getMethodParameterType(String methodName, int parameter) {
		Dbc.require("A method must be specified", methodName != null && methodName.length() > 0);
		Dbc.require("A method must have " + ParameterStart + " and " + ParameterEnd + " characters", methodName.contains(ParameterStart) && methodName.contains(ParameterEnd));
		Dbc.require("Method index must be 0 or greater", parameter >= 0);

		String retValue = null;
		
		String paramTypes [] = getMethodParameterTypes(methodName);
		
		if (paramTypes.length > 0 && parameter < paramTypes.length) {
			retValue = paramTypes[parameter];
		}

		return retValue;
	}
	
	/**
	 * Determine the capitalised parameter type for a given parameter index
	 * 
	 * @param methodName
	 * @param parameter
	 * @return String parameter type capitalised
	 */
	public static String getMethodParameterTypeCapitalised(String methodName, int parameter) {
		Dbc.require("A method must be specified", methodName != null && methodName.length() > 0);
		Dbc.require("A method must have " + ParameterStart + " and " + ParameterEnd + " characters", methodName.contains(ParameterStart) && methodName.contains(ParameterEnd));
		Dbc.require("Method index must be 0 or greater", parameter >= 0);

		return capitaliseString(getMethodParameterType(methodName, parameter));
	}
	/**
	 * Compile an array of capitalised parameter types for a given method signature
	 * 
	 * @param methodName
	 * @return String array of parameter types capitalised
	 */
	public static String [] getMethodParameterTypesCapitalised(String methodName) {
		Dbc.require("A method must be specified", methodName != null && methodName.length() > 0);
		Dbc.require("A method must have " + ParameterStart + " and " + ParameterEnd + " characters", methodName.contains(ParameterStart) && methodName.contains(ParameterEnd));
	
		String types [] = getMethodParameterTypes(methodName);
		String capitalTypes [] = new String [types.length];
		for (int i = 0; i < types.length; i++) {
			capitalTypes[i] = capitaliseString(types[i]);
		}
		return capitalTypes;
	}

	/**
	 * Compile an array of parameter types for a given method signature
	 * 
	 * @param methodName
	 * @return String array of parameter types
	 */
	public static String [] getMethodParameterTypes(String methodName) {
		Dbc.require("A method must be specified", methodName != null && methodName.length() > 0);
		Dbc.require("A method must have " + ParameterStart + " and " + ParameterEnd + " characters", methodName.contains(ParameterStart) && methodName.contains(ParameterEnd));

		List <String> paramTypes = new ArrayList<String> ();
		int paramStart = methodName.indexOf(ParameterStart) + 1;
		int paramEnd = methodName.indexOf(ParameterEnd);
		
		if (paramStart == paramEnd) {
			return new String [0];
		} else {
			String params = methodName.substring(paramStart, paramEnd);

			String paramArray [] = params.split(ParameterDelimiter);

			for (int i = 0; i < paramArray.length; i++) {
				String paramType = paramArray[i].trim();
				
				paramTypes.add(paramType.substring(0, paramType.indexOf(ParameterSeparator)));
			}

			return paramTypes.toArray(new String [paramTypes.size()]);
		}
			
	}
	/**
	 * Determine the parameter name for a given parameter index
	 * 
	 * @param methodName
	 * @param parameter
	 * @return String parameter name
	 */
	public static String getMethodParameterName(String methodName, int parameter) {
		Dbc.require("A method must be specified", methodName != null && methodName.length() > 0);
		Dbc.require("A method must have " + ParameterStart + " and " + ParameterEnd + " characters", methodName.contains(ParameterStart) && methodName.contains(ParameterEnd));
		Dbc.require("Method index must be 0 or greater", parameter >= 0);

		String retValue = null;
		
		String methodNames [] = getMethodParameterNames(methodName);
		
		if (methodNames.length > 0 && parameter < methodNames.length) {
			retValue = methodNames[parameter];
		}
		return retValue;
	}
	
	
	/**
	 * Compile an array of parameter names for a given method signature
	 * 
	 * @param methodName
	 * @return String array of parameter names
	 */
	public static String [] getMethodParameterNames(String methodName) {
		Dbc.require("A method must be specified", methodName != null && methodName.length() > 0);
		Dbc.require("A method must have " + ParameterStart + " and " + ParameterEnd + " characters", methodName.contains(ParameterStart) && methodName.contains(ParameterEnd));

		List <String> paramNames = new ArrayList<String> ();
		
		int paramStart = methodName.indexOf(ParameterStart) + 1;
		int paramEnd = methodName.indexOf(ParameterEnd);

		if (paramStart == paramEnd) {
			return new String [0];
		} else {
			String params = methodName.substring(paramStart, paramEnd);
			String paramArray [] = params.split(ParameterDelimiter);
	
			for (int i = 0; i < paramArray.length; i++) {
				String paramName = paramArray[i].trim();
				paramNames.add(paramName.substring(paramName.indexOf(ParameterSeparator) + 1, paramName.length()));
			}
			return paramNames.toArray(new String [paramNames.size()]);
			
		}

	}
	
	/**
	 * Compile a Class array of the parameter types for a method signature
	 * 
	 * @param methodName
	 * @return Class array of parameter types
	 * TODO: work required for type arrays
	 */
	public static Class [] getParameterClasses(String methodName) {
		Dbc.require("A method must be specified", methodName != null && methodName.length() > 0);
		Dbc.require("A method must have " + ParameterStart + " and " + ParameterEnd + " characters", methodName.contains(ParameterStart) && methodName.contains(ParameterEnd));

		String paramTypes [] = getMethodParameterTypes(methodName);
		Class paramClasses [] = new Class [paramTypes.length];
		for (int i = 0; i < paramTypes.length; i++) {
			try {
				if (arrayContains(JAVA_PRIMITIVES, paramTypes[i])) {
					paramClasses[i] = getPrimitiveClass(paramTypes[i]);
				} else if (arrayContains(JAVA_LANG_CLASSES, paramTypes[i])) {
					paramClasses[i] = Class.forName(JAVA_LANG_PREFIX + paramTypes[i]);
				} else {
					paramClasses[i] = Class.forName(paramTypes[i]);
				}
					
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return paramClasses;
	}
	/**
	 * Determine the method name for a given method signature
	 * 
	 * @param methodSignature
	 * @return String method name
	 */
	static String parseMethod(String methodSignature) {
		Dbc.require("A method must be specified", methodSignature != null && methodSignature.length() > 0);
		Dbc.require("A method must have " + ParameterStart + " and " + ParameterEnd + " characters", methodSignature.contains(ParameterStart) && methodSignature.contains(ParameterEnd));
		return methodSignature.substring(0, methodSignature.indexOf(ParameterStart));
	}

	/**
	 * Capitalise a string value
	 * Does not capitalise parameter type if it contains a fully qualified class
	 * 
	 * @param value
	 * @return String capitalised version
	 */
	static String capitaliseString(String value) {
		String retValue = value;
		
		if (!value.contains(PACKAGE_SEPARATOR)) {
			StringBuffer newValue = new StringBuffer();
			
			if (value != null && value.length() > 0) {
				newValue.append(value.substring(0, 1).toUpperCase());
				
				newValue.append(value.substring(1));
				retValue = newValue.toString();
			}
		}
		return retValue;

	}
	
	/**
	 * Determine if a String value is contained in a String array
	 * @param array
	 * @param value
	 * @return boolean true if value is contained in array
	 */
	static boolean arrayContains(String [] array, String value) {
		boolean retValue = false;
		
		for (String element : array) {
			if (element.equals(value)) {
				retValue = true;
				break;
			}
		}
		return retValue;
	}
	/**
	 * Determine if a String value is contained in a String array
	 * @param array
	 * @param value
	 * @return boolean true if value is contained in array
	 */
	static int arrayElement(String [] array, String value) {
		int retValue = -1;
		
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(value)) {
				retValue = i;
				break;
			}
		}
		return retValue;
	}
	
	/**
	 * Obtain a Class for a Java primitive
	 * @param parameter
	 * @return Class
	 */
	static Class getPrimitiveClass(String parameter) {
		Class clazz = null;
		
		switch (arrayElement(JAVA_PRIMITIVES, parameter)) {
			case 0: 
				clazz = int.class;
				break;
			case 1: 
				clazz = long.class;
				break;
			case 2: 
				clazz = double.class;
				break;
			case 3: 
				clazz = float.class;
				break;
			case 4: 
				clazz = byte.class;
				break;
			case 5: 
				clazz = char.class;
				break;
			case 6: 
				clazz = boolean.class;
				break;
			case 7: 
				clazz = short.class;
				break;
			default:
		}
		return clazz;
	}
}
