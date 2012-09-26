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
package org.societies.privacytrust.privacyprotection.assessment.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses stack trace for service invocation in Virgo + Spring environment.
 *
 * @author Mitja Vardjan
 *
 */
public class StackParser {
	
	private static Logger LOG = LoggerFactory.getLogger(StackParser.class);

	private static final String PROXY_CLASS = "org.springframework.aop.framework.JdkDynamicAopProxy";
	private static final String PROXY_METHOD = "invoke";
	
	private final StackTraceElement[] stack;
	private int k = 0;
	
	/**
	 * Constructor
	 * 
	 * @param stack Stack trace
	 */
	public StackParser(StackTraceElement[] stack) {
		this.stack = stack;
	}
	
	public void log() {
		
		for (int k = 0; k < stack.length; k++) {
			
			// Full class name
			LOG.debug("STACK[{}] class: {}", k, stack[k].getClassName());
			
			// Java file name without path
			LOG.debug("STACK[{}] file: {}", k, stack[k].getFileName());
			
			// Method name without class name or parameters
			LOG.debug("STACK[{}] method: {}", k, stack[k].getMethodName());
			
			// Full class name + method + file name + line number
			LOG.debug("STACK[{}] toString: {}", k, stack[k].toString());
			
			// false for all classes of interest, true only for sun.reflect.NativeMethodAccessorImpl
			LOG.debug("STACK[{}] isNative: {}", k, stack[k].isNativeMethod());
		}
	}
	
	public String getInvoker() {
		k = 0;
		return getNextInvoker();
	}
	
	public String getInvokerOfInvoker() {
		k = 0;
		getNextInvoker();
		return getNextInvoker();
	}
	
	public String[] getAllInvokers() {
		k = 0;
		List<String> invokers = new ArrayList<String>();
		String next;

		while ((next = getNextInvoker()) != null) {
			invokers.add(next);
		}
		return (String[]) invokers.toArray();
	}

	public String getNextInvoker() {
		
		LOG.debug("getNextInvoker()");
		
		for (; k < stack.length; k++) {
			
			if (PROXY_METHOD.equals(stack[k].getMethodName()) &&
					PROXY_CLASS.equals(stack[k].getClassName())) {
				
				if (k+2 >= stack.length) {
					LOG.warn("Unexpected end of stack at {}", k);
					return null;
				}
				
				if (stack[k+1].getFileName() == null) {
					String className = stack[k+2].getClassName();
					LOG.debug("getNextInvoker(): {}", className);
					k += 3;
					return className;
				}
			}
		}
		
		LOG.debug("getNextInvoker(): No invoker found");
		return null;
	}
}
