package org.societies.android.platform.interfaces;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestServiceMethodTranslator {
	private static String methodsArray [] = {"getGreeting()", "getGreeting(String appendToMessage)", "getAnotherGreeting(String appendToMessage, int index)"};
	private static String METHOD_1 = "getGreeting";
	private static String METHOD_2 = "getAnotherGreeting";
	private static String METHOD_NON_EXIST = "doesNotExist()";
	private static String PARAM_1_NAME = "appendToMessage";
	private static String PARAM_2_NAME = "index";
	private static String PARAM_1_TYPE = "String";
	private static String PARAM_2_TYPE = "int";
	private static String PARAM_3_TYPE = "long";
	private static String PARAM_4_TYPE = "double";
	private static String PARAM_5_TYPE = "float";
	private static String PARAM_6_TYPE = "byte";
	private static String PARAM_7_TYPE = "char";
	private static String PARAM_8_TYPE = "boolean";
	private static String PARAM_9_TYPE = "short";
	
	private static String PARAM_1_TYPE_CAP = "String";
	private static String PARAM_2_TYPE_CAP = "Int";
	private static String PARAM_3_TYPE_CAP = "Long";
	private static String PARAM_4_TYPE_CAP = "Double";

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetMethodIndex() {
		assertEquals(0, ServiceMethodTranslator.getMethodIndex(methodsArray, methodsArray[0]));
		assertEquals(1, ServiceMethodTranslator.getMethodIndex(methodsArray, methodsArray[1]));
		assertEquals(2, ServiceMethodTranslator.getMethodIndex(methodsArray, methodsArray[2]));
		assertEquals(-1, ServiceMethodTranslator.getMethodIndex(methodsArray, METHOD_NON_EXIST));
	}

	@Test
	public void testGetMethodString() {
		assertEquals(METHOD_1, ServiceMethodTranslator.getMethodName(methodsArray, 0));
		assertEquals(METHOD_1, ServiceMethodTranslator.getMethodName(methodsArray, 1));
		assertEquals(METHOD_2, ServiceMethodTranslator.getMethodName(methodsArray, 2));
	}
	
	@Test(expected= org.societies.utilities.DBC.PreconditionException.class)
	public void testGetInvalidMethodString() {
		assertEquals(METHOD_2, ServiceMethodTranslator.getMethodName(methodsArray, 3));
	}
	
	@Test
	public void testGetParameterNumber() {
		assertEquals(0, ServiceMethodTranslator.getParameterNumber(methodsArray[0]));
		assertEquals(1, ServiceMethodTranslator.getParameterNumber(methodsArray[1]));
		assertEquals(2, ServiceMethodTranslator.getParameterNumber(methodsArray[2]));
	}

	
	@Test
	public void testGetMethodParameterName() {
		assertEquals(null, ServiceMethodTranslator.getMethodParameterName(methodsArray[0], 0));
		assertEquals(null, ServiceMethodTranslator.getMethodParameterName(methodsArray[0], 1));
		assertEquals(PARAM_1_NAME, ServiceMethodTranslator.getMethodParameterName(methodsArray[1], 0));
		assertEquals(null, ServiceMethodTranslator.getMethodParameterName(methodsArray[1], 1));
		assertEquals(PARAM_1_NAME, ServiceMethodTranslator.getMethodParameterName(methodsArray[2], 0));
		assertEquals(PARAM_2_NAME, ServiceMethodTranslator.getMethodParameterName(methodsArray[2], 1));
		assertEquals(null, ServiceMethodTranslator.getMethodParameterName(methodsArray[2], 2));
	}

	
	@Test
	public void testGetMethodParameterNames() {
		assertEquals(0, ServiceMethodTranslator.getMethodParameterNames(methodsArray[0]).length);
		assertEquals(1, ServiceMethodTranslator.getMethodParameterNames(methodsArray[1]).length);
		assertEquals(2, ServiceMethodTranslator.getMethodParameterNames(methodsArray[2]).length);
	}

	@Test(expected= org.societies.utilities.DBC.PreconditionException.class)
	public void testGetMethodParameterNamesMalFormed() {
		assertEquals(2, ServiceMethodTranslator.getMethodParameterNames(METHOD_1).length);
	}

	
	@Test
	public void testGetParameterClasses() {
		assertEquals(0, ServiceMethodTranslator.getParameterClasses(methodsArray[0]).length);
		assertEquals(1, ServiceMethodTranslator.getParameterClasses(methodsArray[1]).length);
		assertEquals(2, ServiceMethodTranslator.getParameterClasses(methodsArray[2]).length);
//		Class clazzes [] = ServiceMethodTranslator.getParameterClasses(methodsArray[2]);
//		for (int i = 0; i < clazzes.length; i++) {
//			System.out.println("Class: " + clazzes[i].getCanonicalName());
//		}
	}
	
	@Test
	public void testCapitaliseString() {
		assertEquals(PARAM_1_TYPE_CAP, ServiceMethodTranslator.capitaliseString(PARAM_1_TYPE));
		assertEquals(PARAM_2_TYPE_CAP, ServiceMethodTranslator.capitaliseString(PARAM_2_TYPE));
		assertEquals(PARAM_3_TYPE_CAP, ServiceMethodTranslator.capitaliseString(PARAM_3_TYPE));
		assertEquals(PARAM_4_TYPE_CAP, ServiceMethodTranslator.capitaliseString(PARAM_4_TYPE));
	}
	
	@Test
	public void testGetMethodParameterType() {
		assertEquals(PARAM_1_TYPE, ServiceMethodTranslator.getMethodParameterType(methodsArray[1], 0));
		assertEquals(PARAM_1_TYPE, ServiceMethodTranslator.getMethodParameterType(methodsArray[2], 0));
		assertEquals(PARAM_2_TYPE, ServiceMethodTranslator.getMethodParameterType(methodsArray[2], 1));

		assertEquals(null, ServiceMethodTranslator.getMethodParameterType(methodsArray[0], 0));
	}
	@Test
	public void testGetMethodParameterTypeCapitalised() {
		assertEquals(PARAM_1_TYPE_CAP, ServiceMethodTranslator.getMethodParameterTypeCapitalised(methodsArray[1], 0));
		assertEquals(PARAM_1_TYPE_CAP, ServiceMethodTranslator.getMethodParameterTypeCapitalised(methodsArray[2], 0));
		assertEquals(PARAM_2_TYPE_CAP, ServiceMethodTranslator.getMethodParameterTypeCapitalised(methodsArray[2], 1));

		assertEquals(null, ServiceMethodTranslator.getMethodParameterTypeCapitalised(methodsArray[0], 0));
	}
	@Test
	public void testGetMethodParameterTypes() {
		assertEquals(0, ServiceMethodTranslator.getMethodParameterTypes(methodsArray[0]).length);
		assertEquals(1, ServiceMethodTranslator.getMethodParameterTypes(methodsArray[1]).length);
		assertEquals(2, ServiceMethodTranslator.getMethodParameterTypes(methodsArray[2]).length);
//		String types [] = ServiceMethodTranslator.getMethodParameterTypes(methodsArray[2]);
//		for (int i = 0; i < types.length; i++) {
//			System.out.println("Types: " + types[i]);
//		}
	}
	@Test
	public void testGetMethodParameterTypesCapitalised() {
		assertEquals(0, ServiceMethodTranslator.getMethodParameterTypesCapitalised(methodsArray[0]).length);
		assertEquals(1, ServiceMethodTranslator.getMethodParameterTypesCapitalised(methodsArray[1]).length);
		assertEquals(2, ServiceMethodTranslator.getMethodParameterTypesCapitalised(methodsArray[2]).length);
	}
	
	@Test
	public void testArrayElement() {
		assertEquals(0, ServiceMethodTranslator.arrayElement(ServiceMethodTranslator.JAVA_PRIMITIVES, PARAM_2_TYPE));
		assertEquals(1, ServiceMethodTranslator.arrayElement(ServiceMethodTranslator.JAVA_PRIMITIVES, PARAM_3_TYPE));
		assertEquals(2, ServiceMethodTranslator.arrayElement(ServiceMethodTranslator.JAVA_PRIMITIVES, PARAM_4_TYPE));
	}
	
	@Test 
	public void testGetPrimitiveClass() {
		assertEquals(int.class, ServiceMethodTranslator.getPrimitiveClass(PARAM_2_TYPE));
		assertEquals(long.class, ServiceMethodTranslator.getPrimitiveClass(PARAM_3_TYPE));
		assertEquals(double.class, ServiceMethodTranslator.getPrimitiveClass(PARAM_4_TYPE));
		assertEquals(float.class, ServiceMethodTranslator.getPrimitiveClass(PARAM_5_TYPE));
		assertEquals(byte.class, ServiceMethodTranslator.getPrimitiveClass(PARAM_6_TYPE));
		assertEquals(char.class, ServiceMethodTranslator.getPrimitiveClass(PARAM_7_TYPE));
		assertEquals(boolean.class, ServiceMethodTranslator.getPrimitiveClass(PARAM_8_TYPE));
		assertEquals(short.class, ServiceMethodTranslator.getPrimitiveClass(PARAM_9_TYPE));
	}
}
