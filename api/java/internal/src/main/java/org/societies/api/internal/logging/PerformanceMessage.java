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

import java.util.Date;

/**
 * A PerformanceMessage contains all parameters in a logfile line.
 * @see org.IPerformanceMessage.log.impl.IPersistPerformanceMessage 
 * @author Patrick Robertson, DLR
 *
 */
public class PerformanceMessage implements IPerformanceMessage {

	private String testContext;
	private String component;
	private int performanceType;
	private String perfNameValue;
	private String opType;
	private String d82TestTableName;
	protected long whenModified;
	
	public static final String DelimsTime = "[=]";
	public static final String DelimNameValue = "[=]";

	/**
	 * Construct a PerformanceMessage with all parameters.
	 * @see societies.IPerformanceMessage 
	 * @param testContext context in which the test was done
	 * @param component the Component that got the test result (i.e. the component under test)
	 * @param testType testType type of test: choose from PerformanceMessage.Delay, PerformanceMessage.Memory
	 * or PerformanceMessage.Accuracy
	 * @param perfNameValue name value pair of the performance metric
	 * @param opType type of test which was done
	 * @param d82TestTableName the test case name of D82 test table
	 */
	public PerformanceMessage(String testContext, String component, int performanceType, 
			String perfNameValue, String opType, String d82TestTableName) {
		super();
		this.testContext = testContext;
		this.component = component;
		this.performanceType = performanceType;
		this.perfNameValue = perfNameValue;
		this.opType = opType;
		this.d82TestTableName = d82TestTableName;
		this.whenModified = System.currentTimeMillis();
	}

	
	/**
	 * Construct a PerformanceMessage with no parameters. You must then manually call all
	 * the setters.
	 * @see societies.IPerformanceMessage 
	 */
	public PerformanceMessage() {
		super();
	}
	
	
	
	/**
	 * Construct a PerformanceMessage from a String as from the log file single line entry.
	 * @see societies.IPerformanceMessage 
	 */
	public PerformanceMessage(String logLine) {
		super();
		importFromString(logLine);
	}

	public void importFromString(String logLine) {
		String delims = "[,]";
		String delimsInner = "[:]";
		String[] tokens = logLine.split(delims);
		for (int i = 0; i < tokens.length; i++) {
		 //   System.out.println("Token " + tokens[i]);
		    String[] tokensInner = tokens[i].split(delimsInner);
		    if (ComponentTypeStr.equals(tokensInner[0])) this.component=tokensInner[1].trim();
		    else if (OperationTypeStr.equals(tokensInner[0])) this.opType=tokensInner[1].trim();
		    else if (PerformanceTypeStr.equals(tokensInner[0])) this.performanceType=Integer.parseInt(tokensInner[1].trim());
		    else if (TestContextStr.equals(tokensInner[0])) this.testContext=tokensInner[1].trim();
		    else if (D82TestTableNameStr.equals(tokensInner[0])) this.d82TestTableName=tokensInner[1].trim();
		    else if (perfNameValueStr.equals(tokensInner[0])) this.perfNameValue=tokensInner[1].trim();
		    else if (ModifiedlastStr.equals(tokensInner[0]))
				{
		    		String[] tokensTimr = tokensInner[1].split(DelimsTime);
		    		this.whenModified = Long.parseLong(tokensTimr[0].trim());
				}
		    else {
		    	System.err.println("Illegal token: " + tokensInner[0]);
		    }
		}
	}

	/**
	 * Construct a PerformanceMessage from an existing one (deep copy).
	 * @see societies.IPerformanceMessage 
	 * @param input the PerformanceMessage which to take
	 */
	public PerformanceMessage(PerformanceMessage input) {
		super();
		this.testContext = new String(input.testContext);
		this.component = new String(input.component);
		this.performanceType = input.performanceType;
		this.perfNameValue = new String(perfNameValue);
		this.opType = new String(opType);
		this.d82TestTableName = input.d82TestTableName;
		this.whenModified = System.currentTimeMillis();
	}
	

	/* 
	 * @see societies.IPerformanceMessage#getSourceComponent()
	 */
	public String getSourceComponent() {
		return component;
	}




	/*
	 * @see societies.IPerformanceMessage#getD82TestTableName()
	 */
	public String getD82TestTableName() {
		return this.d82TestTableName;
	}



	/* 
	 * @see societies.IPerformanceMessage#getOperationType()
	 */
	public String getOperationType() {
		return opType;
	}
	
	/**
	 * Return the value stored in the name value pair of this performance message.
	 * The respective name must match "name"
	 * @param name the name of the NV pair
	 * @return the value of the NV pair
	 */
	public String getValue(String name) {
		String[] res = this.perfNameValue.split(DelimNameValue);
		if (res.length<1) return null;
		if (name!=null && ! res[0].equals(name)) return null;
		return res[1].trim();
	}

	/**
	 * Return the value stored in the name value pair of this performance message.
	 * @return the value of the NV pair
	 */
	public String getValue() {
		return this.getValue(null);
	}

	/* 
	 * @see societies.IPerformanceMessage#getPerformanceNameValue()
	 */
	public String getPerformanceNameValue() {
		return perfNameValue;
	}


	
	/* 
	 * @see societies.IPerformanceMessage#getTestContext()
	 */
	public String getTestContext() {
		return testContext;
	}



	/* 
	 * @see societies.IPerformanceMessage#getPerformanceType()
	 */
	public int getPerformanceType() {
		return performanceType;
	}


	

	/* 
	 * @see societies.IPerformanceMessage#setD82TestTableName(java.lang.String)
	 */
	public void setD82TestTableName(String d82TestTableName) {
		this.d82TestTableName = d82TestTableName;
		this.whenModified = System.currentTimeMillis();
	}


	/* 
	 * @see societies.IPerformanceMessage#setOperationType(java.lang.String)
	 */
	public void setOperationType(String opType) {
		this.opType = opType;
		this.whenModified = System.currentTimeMillis();
	}


	/** 
	 * @see societies.IPerformanceMessage#setPerformanceNameValue(java.lang.String)
	 */
	public void setPerformanceNameValue(String perfNameValue) {
		this.perfNameValue=perfNameValue;
		this.whenModified = System.currentTimeMillis();
	}


	public void setPerformanceType(int performanceType) {
		this.performanceType = performanceType;
		this.whenModified = System.currentTimeMillis();
	}


	/** 
	 * @see societies.IPerformanceMessage#setSourceComponent(java.lang.String)
	 */
	public void setSourceComponent(String component) {
		this.component = component;
		this.whenModified = System.currentTimeMillis();
	}


	/**
	 * @see societies.IPerformanceMessage#setTestContext(java.lang.String)
	 */
	public void setTestContext(String testContext) {
		this.testContext = testContext;
		this.whenModified = System.currentTimeMillis();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new String(ComponentTypeStr+":" + this.component + ","+OperationTypeStr+":" + this.getOperationType() +
		","+PerformanceTypeStr+":" + this.performanceType + 
		","+TestContextStr +":" + this.testContext + "," + D82TestTableNameStr + ":" + this.d82TestTableName +
		this.generateNameValueRep() + "," + ModifiedlastStr + ":" + this.whenModified + "=" + 
		(this.whenModified>0 ? new Date(this.whenModified) : "null"));
	}
	
	public String generateNameValueRep() {
		return ","+perfNameValueStr+":"+ this.perfNameValue;
	}
	
	public static void main(String[] argv) {
		IPerformanceMessage m = new PerformanceMessage();
		m.setTestContext("DemoContext1");
		m.setSourceComponent("org.personalsmartspace.demopackage.DemoClass");
		m.setPerformanceType(IPerformanceMessage.Delay);
		m.setPerformanceNameValue("Delay=1");
		m.setOperationType("DemoOpFromContext");
		m.setD82TestTableName("S11");
		System.out.println(m);
	}


	public static String ReturnPerformanceTypeString(int performanceType) {
		if (performanceType == IPerformanceMessage.Accuracy) return "Accuracy";
		if (performanceType == IPerformanceMessage.Delay) return "Delay";
		if (performanceType == IPerformanceMessage.Memory) return "Memory";
		if (performanceType == IPerformanceMessage.OtherQuantitative) return "OtherQuantitative";
		if (performanceType == IPerformanceMessage.Quanitative) return "Qualitative";

		return "Unspecified";
	}


	/**
	 * @see societies.IPerformanceMessage#writeToLog()
	 */
	public void writeToLog() throws IllegalStateException {
		if (this.whenModified==0) throw new IllegalStateException("Unable to log a performance Message that has not been even partially configured.");
		// TODO add the log write
		String logString = this.toString();
	}
	
}
