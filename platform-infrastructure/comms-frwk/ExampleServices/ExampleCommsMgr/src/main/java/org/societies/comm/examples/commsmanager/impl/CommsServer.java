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
import org.societies.comm.examples.calculator.ICalc;
import org.societies.comm.examples.calculatorbean.CalcBean;
import org.societies.comm.examples.calculatorbean.CalcBeanResult;
import org.societies.comm.examples.fortunecookie.api.IWisdom;
import org.societies.comm.examples.fortunecookie.datatypes.Cookie;
import org.societies.comm.examples.fortunecookiebean.FortuneCookieBean;
import org.societies.comm.examples.fortunecookiebean.FortuneCookieBeanResult;
import org.societies.comm.examples.fortunecookiebean.MethodName;
import org.societies.comm.xmpp.datatypes.Stanza;
import org.societies.comm.xmpp.exceptions.CommunicationException;
import org.societies.comm.xmpp.interfaces.CommManager;
import org.societies.comm.xmpp.interfaces.FeatureServer;
import org.societies.example.complexservice.IComplexService;
import org.societies.example.complexservice.schema.MyComplexBean;
import org.societies.example.complexservice.schema.ServiceAMsgBean;
import org.societies.example.complexservice.schema.ServiceAMsgBeanResult;

public class CommsServer implements FeatureServer {

	private static final List<String> NAMESPACES = Collections.unmodifiableList(
							  Arrays.asList("http://societies.org/comm/examples/calculatorbean",
									  		"http://societies.org/comm/examples/fortunecookiebean",
									  		"http://societies.org/example/complexservice/schema"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
							  Arrays.asList("org.societies.comm.examples.calculatorbean",
											"org.societies.comm.examples.fortunecookiebean",
											"org.societies.example.complexservice.schema"));
	
	//PRIVATE VARIABLES
	private CommManager commManager;
	private ICalc calcService;
	private IWisdom fcGenerator;
	private IComplexService complexSvc;
	
	private static Logger LOG = LoggerFactory.getLogger(CommsServer.class);
	
	//PROPERTIES
	public CommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(CommManager commManager) {
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
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
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
	
	/* Put your functionality here if there is NO return object, ie, VOID 
	 */
	@Override
	public void receiveMessage(Stanza stanza, Object payload) {
		//CHECK WHICH END BUNDLE TO BE CALLED THAT I MANAGE
		
	}

	/* Put your functionality here if there IS a return object
	 */
	@Override
	public Object receiveQuery(Stanza stanza, Object payload) {
		//CHECK WHICH END BUNDLE TO BE CALLED THAT I MANAGE
		
		// --------- CALCULATOR BUNDLE ---------
		if (payload.getClass().equals(CalcBean.class)) {
			
			CalcBean calc = (CalcBean) payload;
			
			int result=0; int a = 0; int b = 0;
			String text = ""; 
			switch (calc.getMethod()) {
			
			//Add() METHOD
			case ADD:
				a = calc.getA();
				b = calc.getB();
				result = calcService.Add(a, b);
				text = a + " + " + b + " = " + result;
				break;

			//Subtract() method
			case SUBTRACT:
				a = calc.getA();
				b = calc.getB();
				result = calcService.Subtract(a, b);
				text = a + " - " + b + " = " + result;
				break;
				
			//AddAsync() METHOD
			case ADD_ASYNC:
				a = calc.getA();
				b = calc.getB();
				Future<Integer> asyncResult = calcService.AddAsync(a, b);
				
				try {
					result = asyncResult.get();		//WAIT HERE TILL RESULT IS RETURNED. PROCESSOR IS RELEASED!
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}				
				text = a + " + " + b + " = " + result;
				break;
			}
			//GENERATE BEAN CONTAINING RETURN OBJECT 
			CalcBeanResult calcRes = new CalcBeanResult();
			calcRes.setResult(result);
			calcRes.setText(text);
			return calcRes;
		}
		
		// -------- FORTUNE COOKIE BUNDLE ---------
		else if (payload.getClass().equals(FortuneCookieBean.class)) {
			FortuneCookieBean fcBean = (FortuneCookieBean) payload;
			
			if (fcBean.getMethod().equals(MethodName.GET_COOKIE)) {
				//NO PARAMETERS FOR THIS METHOD
				Cookie fortune = fcGenerator.getCookie();
				
				//GENERATE BEAN CONTAINING RETURN OBJECT 
				FortuneCookieBeanResult fcRes = new FortuneCookieBeanResult();
				fcRes.setId(fortune.getId());
				fcRes.setValue(fortune.getValue());
				return fcRes;
			}
		}
		
		// -------- COMPLEX SERVICE BUNDLE ---------
		else if (payload.getClass().equals(ServiceAMsgBean.class)) {
			ServiceAMsgBean complexBean = (ServiceAMsgBean) payload;
			
			MyComplexBean paramBean = (MyComplexBean) complexBean.getComplexBean();
			Future<MyComplexBean> returnBean = null;
			ServiceAMsgBeanResult complexRes = new ServiceAMsgBeanResult(); //GENERATE BEAN CONTAINING RETURN OBJECT 
			
			switch (complexBean.getMethod()) {
			
			//DO_SOMETHING() METHOD
			case DO_SOMETHING:
				//CALL ACTUAL SERVICE
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
				return complexRes;

			//DO_SOMETHING_ELSE() METHOD
			case DO_SOMETHING_ELSE:
				//CALL ACTUAL SERVICE
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
				return complexRes;
			}
		}
		//TODO: Better error handling, ie, if there is no match on the received Message Bean
		return null;
	}
	
	
}
