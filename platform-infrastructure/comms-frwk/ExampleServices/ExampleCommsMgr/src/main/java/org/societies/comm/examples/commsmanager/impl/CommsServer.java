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
package org.societies.comm.examples.commsmanager.impl;

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

public class CommsServer implements IFeatureServer {

	private static final List<String> NAMESPACES = Collections.unmodifiableList(
							  Arrays.asList("http://societies.org/api/schema/examples/calculatorbean",
									  		"http://societies.org/api/schema/examples/fortunecookie",
									  		"http://societies.org/api/schema/examples/complexservice"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
							  Arrays.asList("org.societies.api.schema.examples.calculatorbean",
											"org.societies.api.schema.examples.fortunecookie",
											"org.societies.api.schema.examples.complexservice"));
	
	//PRIVATE VARIABLES
	private ICommManager commManager;
	private ICalc calcService;
	private IWisdom fcGenerator;
	private IComplexService complexSvc;
	
	private static Logger LOG = LoggerFactory.getLogger(CommsServer.class);
	
	//PROPERTIES
	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}

	public ICalc getCalcService() {
		return calcService;
	}

	public void setCalcService(ICalc calcService) {
		this.calcService = calcService;
	}

	public IWisdom getFcGenerator() {
		return fcGenerator;
	}

	public void setFcGenerator(IWisdom fcGenerator) {
		this.fcGenerator = fcGenerator;
	}

	public IComplexService getComplexSvc() {
		return complexSvc;
	}

	public void setComplexSvc(IComplexService complexSvc) {
		this.complexSvc = complexSvc;
	}
	
	//METHODS
	public CommsServer() {
	}
	
	public void InitService() {
		//REGISTER OUR ServiceManager WITH THE XMPP Communication Manager
		try {
			getCommManager().register(this); 
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.FeatureServer#getJavaPackages()
	 */
	@Override
	public List<String> getJavaPackages() {
		return PACKAGES;
	}
	
	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.FeatureServer#getXMLNamespaces()
	 */
	@Override
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}
	
	/* Put your functionality here if there is NO return object, ie, VOID  */
	@Override
	public void receiveMessage(Stanza stanza, Object payload) {
		//CHECK WHICH END BUNDLE TO BE CALLED THAT I MANAGE
		if (payload instanceof CalcBean){ 
			CalcBean messageBean = (CalcBean)payload;
			LOG.debug("*** Message Recieved by ExampleCommMgr: " + messageBean.getMessage());
		}
	}

	/* Put your functionality here if there IS a return object */
	@Override
	public Object getQuery(Stanza stanza, Object payload) throws XMPPError {
		//CHECK WHICH END BUNDLE TO BE CALLED THAT I MANAGE
		System.out.println("Generic query handler, doing nothing");
		if (payload instanceof ServiceAMsgBean){ return this.getQuery(stanza, (ServiceAMsgBean)payload);}
		if (payload instanceof FortuneCookieBean){ return this.getQuery(stanza, (FortuneCookieBean)payload);}
		if (payload instanceof CalcBean){ return this.getQuery(stanza, (CalcBean)payload);}		
		return null;
	}
	
	public Object getQuery(Stanza stanza, ServiceAMsgBean payload) throws XMPPError {

		ServiceAMsgBean complexBean = (ServiceAMsgBean) payload;
		MyComplexBean paramBean = (MyComplexBean) complexBean.getComplexBean();
		Future<MyComplexBean> returnBean = null;
		// GENERATE BEAN CONTAINING RETURN OBJECT
		ServiceAMsgBeanResult complexRes = new ServiceAMsgBeanResult(); 

		switch (complexBean.getMethod()) {
		// DO_SOMETHING() METHOD
		case DO_SOMETHING:
			// CALL ACTUAL SERVICE
			returnBean = complexSvc.doSomething(paramBean);

			try {
				complexRes.setComplexBean(returnBean.get());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// DO_SOMETHING_ELSE() METHOD
		case DO_SOMETHING_ELSE:
			// CALL ACTUAL SERVICE
			returnBean = complexSvc.doSomethingElse(paramBean);

			try {
				complexRes.setComplexBean(returnBean.get());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return complexRes;
	}
	
	public Object getQuery(Stanza stanza, FortuneCookieBean payload) throws XMPPError {
		FortuneCookieBean fcBean = (FortuneCookieBean) payload;
		// NO PARAMETERS FOR THIS METHOD
		Future<Cookie> fortune = fcGenerator.getCookie();
		Cookie cookie = null;
		try {
			cookie = fortune.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		// GENERATE BEAN CONTAINING RETURN OBJECT
		FortuneCookieBeanResult fcRes = new FortuneCookieBeanResult();
		fcRes.setCookie(cookie);
		return fcRes;
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
			asyncResult = calcService.Add(a, b);
			break;

		// Subtract() method
		case SUBTRACT:
			a = calc.getA();
			b = calc.getB();
			asyncResult = calcService.Subtract(a, b);
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

	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.FeatureServer#setQuery(org.societies.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public Object setQuery(Stanza arg0, Object arg1) throws XMPPError {
		// TODO Auto-generated method stub
		return null;
	}
	
}
