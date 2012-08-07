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
package org.societies.api.internal.logging;


/**
 * Represents access to a log file line.
 * @author Patrick Robertson, DLR
 *
 */
public interface IPerformanceMessage {

	public final static int Delay = 0;
	public final static int Memory = 1;
	public final static int Accuracy = 2;
	public final static int OtherQuantitative = 3;
	public final static int Quanitative = 4;
	
	public static final String ComponentTypeStr = "Component";
	public static final String OperationTypeStr = "OperationType";
	public static final String PerformanceTypeStr = "PerformanceType";
	public static final String TestContextStr = "TestContext";
	public static final String D82TestTableNameStr = "D82TestTableName";
	public static final String perfNameValueStr = "perfNameValue";
	public static final String ModifiedlastStr = "ModifiedLast";
	
	
	/**
	 * Write the message to the log
	 * @throws IllegalStateException
	 */ 
	public void writeToLog() throws IllegalStateException;
	
	/**
	 * Set the type of test
	 * Choose from PerformanceMessage.Delay, PerformanceMessage.Memory
	 * or PerformanceMessage.Accuracy
	 * @param performanceType type of test: choose from PerformanceMessage.Delay, PerformanceMessage.Memory
	 * or PerformanceMessage.Accuracy
	 */
	public void setPerformanceType(int performanceType); 
	
	/**
	 * Set the Component that got the test result as a free String
	 * (i.e. the component under test)
	 * e.g. "HWU_Learning_Algorithm_XYZ_16"
	 * @param component the Component that got the test result (i.e. the component under test)
	 */
	public void setSourceComponent(String component);
	

	/**
	 * Set the type of test which was done as a free String
	 * e.g. "LearningFromCSSCreationHistory"
	 * @param opType type of test which was done
	 */
	public void setOperationType(String opType);
	
	/**
	 * Set the context in which the test was done as a freely chosen String
	 * e.g. "LearningDataSet194.CSS.Campus2"
	 * @param testContext context in which the test was done
	 * 
	 */
	public void setTestContext(String testContext);
	
	/**
	 * Set the Name value pair of the performance metric as a freely chosen String
	 * e.g. "LearningAccuracy.Percent=70"
	 * @param perfNameValue name value pair of the performance metric
	 */
	public void setPerformanceNameValue(String perfNameValue);
	
	/**
	 * See D82v2
	 * @param testTableIndex  the test case name
	 */
	public void setD82TestTableName(String testTableName);	
	
	/**
	 * @return the Component that got the test result (i.e. the component under test)
	 */
	public String getSourceComponent();

	/**
	 * @return the test case name
	 */
	public String getD82TestTableName();

	/**
	 * @return type of test which was done
	 */
	public String getOperationType() ;

	/**
	 * @return name value pair of the performance metric
	 */
	public String getPerformanceNameValue();


	/**
	 * @return context in which the test was done
	 */
	public String getTestContext();

	/**
	 * @return type of test which was done
	 */
	public int getPerformanceType();
	
}
