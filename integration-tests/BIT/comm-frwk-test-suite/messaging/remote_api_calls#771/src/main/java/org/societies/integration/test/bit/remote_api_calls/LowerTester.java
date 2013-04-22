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

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
package org.societies.integration.test.bit.remote_api_calls;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.example.fortunecookie.IWisdom;
import org.societies.api.schema.examples.fortunecookie.Cookie;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.example.calculator.ICalc;
import org.societies.api.schema.examples.calculatorbean.CalcBean;
import org.societies.api.schema.examples.calculatorbean.CalcBeanResult;
import org.societies.example.complexservice.IComplexService;
import org.societies.api.schema.examples.complexservice.ServiceAMsgBean;
import org.societies.api.schema.examples.complexservice.ServiceAMsgBeanResult;
import org.societies.api.schema.examples.complexservice.MyComplexBean;
import org.societies.api.schema.examples.fortunecookie.FortuneCookieBean;
import org.societies.api.schema.examples.fortunecookie.FortuneCookieBeanResult;
import org.societies.api.schema.examples.fortunecookie.MethodName;

public class LowerTester implements IFeatureServer {

	private static final List<String> NAMESPACES = Collections.unmodifiableList(
							  Arrays.asList("http://societies.org/api/schema/examples/calculatorbean"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
							  Arrays.asList("org.societies.api.schema.examples.calculatorbean"));
	
	//PRIVATE VARIABLES
	private ICommManager commManager;
	
	private static Logger LOG = LoggerFactory.getLogger(LowerTester.class);
	
	//PROPERTIES
	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
		
		try {
			getCommManager().register(this); 
			LOG.info("***1855... LowerTester getCommManager().register(this)");
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
	}

	
	//Constructor
	public LowerTester() {
	}
	
	
	@Override
	public List<String> getJavaPackages() {
		return PACKAGES;
	}
	

	@Override
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}
	
	/* Put your functionality here if there is NO return object, ie, VOID  */
	@Override
	public void receiveMessage(Stanza stanza, Object payload) {
		LOG.info("***1855... LowerTester receiveMessage:");
		//CHECK WHICH END BUNDLE TO BE CALLED THAT I MANAGE
		if (payload instanceof CalcBean)
		{ 
			CalcBean messageBean = (CalcBean)payload;
			LOG.debug("***1855 Message Recieved by ExampleCommMgr: " + messageBean.getMessage());
		}
	}

	/* Put your functionality here if there IS a return object */
	@Override
	public Object getQuery(Stanza stanza, Object payload) throws XMPPError {
		LOG.info("***1855... LowerTester getQuery:");
		
		//CHECK WHICH END BUNDLE TO BE CALLED THAT I MANAGE
		System.out.println("Generic query handler, doing nothing");
		if (payload instanceof CalcBean)
		{ 
			return this.getQuery(stanza, (CalcBean)payload);
		}		
		return null;
	}
	
	public Object getQuery(Stanza stanza, CalcBean payload) throws XMPPError {
		// --------- CALCULATOR BUNDLE ---------
		CalcBean calc = (CalcBean) payload;

		int result = 0;
		int a = 0;
		int b = 0;
		String text = "";
		Future<Integer> asyncResult = null;

		switch (calc.getMethod()) {
		// AddAsync() METHOD
		case ADD:
			a = calc.getA();
			b = calc.getB();
			asyncResult = CalcImpl.Add(a, b);
			//result = a+b;
			break;

		// Subtract() method
		case SUBTRACT:
			a = calc.getA();
			b = calc.getB();
			asyncResult = CalcImpl.Subtract(a, b);
			//result = a-b;
			break;
		}
		try {
			result = asyncResult.get(); // WAIT HERE TILL RESULT IS RETURNED.
										// PROCESSOR IS RELEASED!
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		text = a + "  " + calc.getMethod().toString() + " " + b + " = " + result;

		// GENERATE BEAN CONTAINING RETURN OBJECT
		CalcBeanResult calcRes = new CalcBeanResult();
		calcRes.setResult(result);
		calcRes.setText(text);
		return calcRes;
	}


	@Override
	public Object setQuery(Stanza arg0, Object arg1) throws XMPPError {
		return null;
	}
	
}
