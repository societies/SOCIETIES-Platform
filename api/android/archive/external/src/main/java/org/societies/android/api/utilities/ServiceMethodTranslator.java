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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.societies.utilities.DBC.Dbc;

import android.os.Parcelable;
import android.util.Log;


/**
 * This class provides a set of static methods to help make the Service Messenger API calling mechanism
 * as simple as possible while allowing the service and the consumer to share a common service API. 
 * 
 * Parameter arrays of String and primitives now supported but only one-dimensional
 */

public class ServiceMethodTranslator {
	
	private static final String ParameterStart = "(";
	private static final String ParameterEnd = ")";
	private static final String PACKAGE_SEPARATOR = ".";
	private static final String ParameterDelimiter = ",";
	private static final String ParameterSeparator = " ";
	public static final String JAVA_LANG_PREFIX = "java.lang.";
	public static final String JAVA_LANG_PREFIX_ARRAY = "[Ljava.lang.";
	public static final String JAVA_LANG_SUFFIX_ARRAY = ";";
	public static final String JAVA_ARRAY = "[]";
	public static final String JAVA_ARRAY_START = "[";
	public static final String JAVA_ARRAY_FINISH = "]";
	public static final String ANDROID_OS_PREFIX = "android.os.";
	
	public static final String JAVA_PRIMITIVES [] = {"int", "long", "double", "float", "byte", "char", "boolean", "short"};
	
	public static final String JAVA_PRIMITIVES_ARRAYS_CLASS_TYPE []    = {"[I", "[L", "[D", "[F", "[B", "[C", "[Z", "[S"};
	public static final String ANDROID_PRIMITIVES_ARRAYS_CLASS_TYPE [] = {"[I", "[J", "[D", "[F", "[B", "[C", "[Z", "[S"};
	public static final String JAVA_PRIMITIVE_ARRAYS [] = {"int[]", "long[]", "double[]", "float[]", "byte[]", "char[]", "boolean[]", "short[]"};
	public static final String JAVA_LANG_CLASSES [] = {"String"};
	public static final String JAVA_LANG_CLASSES_ARRAYS [] = {"String[]"};
	
	private static final String LOG_TAG = ServiceMethodTranslator.class.getName();
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
				
				String attemptedType = paramType.substring(0, paramType.indexOf(ParameterSeparator));
						
				paramTypes.add(arrayTypeCheck(paramArray[i], attemptedType));
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

				paramNames.add(removeArraySymbols(paramName.substring(paramName.indexOf(ParameterSeparator) + 1, paramName.length())));
			}
			return paramNames.toArray(new String [paramNames.size()]);
			
		}

	}
	
	
	/**
	 * Retrieve the Bundle "getter" method for this type of data
	 * get<simple type> for simple types
	 * or getSerializable or Parcelable for complex types
	 * @param paramType
	 * @return the name of the bundle getter method
	 */
	public static String getGetMethodFromParameter(String paramType) {
		StringBuffer result = new StringBuffer("get");
		if (arrayContains(JAVA_PRIMITIVES, paramType)) {
			result.append(capitaliseString(getPrimitiveClass(paramType).getSimpleName()));
		} else if (arrayContains(JAVA_LANG_CLASSES, paramType)) {
			result.append(capitaliseString(paramType));
		} else {
			Class<?> paramClassType = null;
			try {
				Class<?>[] implementedInterfaces = Class.forName(paramType).getInterfaces();

				for (int i = 0; i < implementedInterfaces.length; i++) {
					if (implementedInterfaces[i].equals(Parcelable.class)) {
						result.append("Parcelable");
						break;
					} else if (implementedInterfaces[i].equals(Serializable.class)) {
						result.append("Serializable");
						break;
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			//Causes a problem when testing:
//			[INFO] EXCEPTION FROM SIMULATION:
//			[INFO] local variable type mismatch: attempt to set or access a value of type java.lang.Class 
//			using a local variable of type java.lang.Class[]. 
//			This is symptomatic of .class transformation tools that ignore local variable information.

//			for(Class implementedInterface : implementedInterfaces) {
//				if (implementedInterface.equals(Parcelable.class)) {
//					result.append("Parcelable");
//					break;
//				}
//				else if (implementedInterface.equals(Serializable.class)) {
//					result.append("Serializable");
//					break;
//				}
//			}
		}
		return result.toString();
	}
	
	/**
	 * Create an array of the interface method signature
	 * @param usedInterface
	 * @return
	 */
	public static String[] getMethodsArrayFromInterface(Class usedInterface) {
		int length = usedInterface.getMethods().length;
		String[] methods = new String[length];
		int methodNumber = 0;
		for(Method method : usedInterface.getMethods()) {
			StringBuffer func = new StringBuffer(method.getName()+"(");
			int parameterNumber = 0;
			for(Class<?> parameter : method.getParameterTypes()) {
				func.append((parameterNumber != 0 ? ", " : "")+parameter.getName()+" param"+parameterNumber);
				parameterNumber++;
			}
			func.append(")");
			methods[methodNumber] = func.toString();
			methodNumber++;
		}
		return methods;
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
//				Log.d(LOG_TAG, "Parameter type: " + paramTypes[i]);
				
				if (arrayContains(JAVA_PRIMITIVE_ARRAYS, paramTypes[i])) {
					paramClasses[i] = getPrimitiveClassArray(paramTypes[i]);
					
				} else if (arrayContains(JAVA_PRIMITIVES, paramTypes[i])) {
					paramClasses[i] = getPrimitiveClass(paramTypes[i]);
					
				} else if (arrayContains(JAVA_LANG_CLASSES_ARRAYS, paramTypes[i])) {
					paramClasses[i] = Class.forName(JAVA_LANG_PREFIX_ARRAY + removeArraySymbols(paramTypes[i]) + JAVA_LANG_SUFFIX_ARRAY);
					
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
	
	/**
	 * Determine the class of a given primitive array type
	 * 
	 * @param parameter
	 * @return Class of primitive array
	 */
	static Class getPrimitiveClassArray(String parameter) {
		Class clazz = null;
		String classType = null;
		
		for (int i  = 0; i < JAVA_PRIMITIVE_ARRAYS.length; i++) {
			if (parameter.equals(JAVA_PRIMITIVE_ARRAYS[i])) {
				classType = ANDROID_PRIMITIVES_ARRAYS_CLASS_TYPE[i];
				break;
			}
		}
		if (null != classType) {
			try {
				clazz = Class.forName(classType);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		return clazz;
	}

	/**
	 * Check if the parameter is an array type
	 * @param fullParam
	 * @param paramType
	 * @return String param type
	 */
	private static String arrayTypeCheck(String fullParam, String paramType) {
		StringBuffer name = new StringBuffer(removeArraySymbols(paramType));
		
		if (fullParam.contains(JAVA_ARRAY_START)) {
			name.append(JAVA_ARRAY);
		}
		return name.toString();
	}
	
	/**
	 * Remove Java array symbols from parameter name
	 * 
	 * @param paramName
	 * @return String with Java array symbols removed
	 */
	private static String removeArraySymbols(String paramName) {
		String retValue = null;
		
		String firstPass = paramName.replace(JAVA_ARRAY_START, "");
		String secondPass = firstPass.replace(JAVA_ARRAY_FINISH, "");
		retValue = secondPass.trim();
		
		return retValue;
	}
		
}
