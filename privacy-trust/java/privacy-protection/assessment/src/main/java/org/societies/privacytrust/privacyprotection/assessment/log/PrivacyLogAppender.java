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
package org.societies.privacytrust.privacyprotection.assessment.log;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.ChannelType;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.DataAccessLogEntry;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.DataTransmissionLogEntry;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.IPrivacyLogAppender;
import org.societies.privacytrust.privacyprotection.assessment.logger.CommsFwTestBean;
import org.societies.privacytrust.privacyprotection.assessment.util.ServiceResolver;
import org.societies.privacytrust.privacyprotection.assessment.util.StackParser;

/**
 * 
 *
 * @author Mitja Vardjan
 *
 */
public class PrivacyLogAppender implements IPrivacyLogAppender {

	private static Logger LOG = LoggerFactory.getLogger(PrivacyLogAppender.class);

	private CommsFwTestBean testBean;
	private PrivacyLog privacyLog;
	private ServiceResolver serviceResolver;

	public PrivacyLogAppender() {
		LOG.info("Constructor");
	}

	public void init() {
		LOG.info("init()");
		
		//ApplicationContext appContext = new ClassPathXmlApplicationContext(new String[] { "META-INF/spring/bundle-context.xml" });
		//LOG.debug("init(): 1");
		//CommsFwTestBean aopBean = (CommsFwTestBean) appContext.getBean("aopBeanProxy");
		
		
//		LOG.debug("init(): 2");
//		String aspectName = testBean.getAspectName();
//		LOG.debug("init(): 3 aspectName = " + aspectName);
//		testBean.setAspectName("ahoj");
//		aspectName = testBean.getAspectName();
//		LOG.debug("init(): 4 aspectName = " + aspectName);
		
//		try {
//			LOG.debug("init(): 1");
//			commMgr.sendIQGet(null, null, null);
//			LOG.debug("init(): 2");
//		} catch (CommunicationException e) {
//			LOG.warn("init()", e);
//		}
		
//		logStack();
	}
	
	/**
	 * @return the serviceResolver
	 */
	public ServiceResolver getServiceResolver() {
		return serviceResolver;
	}

	/**
	 * @param serviceResolver the serviceResolver to set
	 */
	public void setServiceResolver(ServiceResolver serviceResolver) {
		this.serviceResolver = serviceResolver;
	}

	private String getInvokerClass() {

		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		StackParser stackParser = new StackParser(stack);
		String invoker = stackParser.getInvokerOfInvoker();
		//LOG.debug("Context / Comms: {}", stackParser.getInvoker());  // Costly call, use for debugging only!
		if (LOG.isDebugEnabled()) {
			LOG.debug("Invoked by {}", invoker);
		}
		
		return invoker;
	}

	private List<String> getInvokerClasses() {

		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		StackParser stackParser = new StackParser(stack);
		List<String> classes = stackParser.getAllClasses();
		if (LOG.isDebugEnabled()) {
			LOG.debug("Invoked by {}", classes);
		}
		
		return classes;
	}

	// Getters and setters for beans
	public PrivacyLog getPrivacyLog() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("getPrivacyLog()");
		}
		return privacyLog;
	}
	public void setPrivacyLog(PrivacyLog privacyLog) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("setPrivacyLog()");
		}
		this.privacyLog = privacyLog;
	}
	public CommsFwTestBean getTestBean() {
		LOG.debug("getTestBean()");
		return testBean;
	}
	public void setTestBean(CommsFwTestBean testBean) {
		LOG.debug("setTestBean()");
		this.testBean = testBean;
	}

	/* (non-Javadoc)
	 * @see IPrivacyLogAppender#logCommsFw(IIdentity, IIdentity, Object)
	 */
	// The type parameter in comms fw is not type of data. It is not even used at the moment.
	// The only option is to call getClass() on the payload. For any more info the Object payload
	// should be typecasted and parsed (not feasible).
	// implementation: sentToGroup: true if receiver is CIS
	// implementation: channelId = XMPP
	@Override
	public boolean logCommsFw(IIdentity sender, IIdentity receiver, Object payload) {
		
		//LOG.debug("logCommsFw()");
		if (LOG.isDebugEnabled()) {
			LOG.debug("logCommsFw({}, {}, ...)", sender, receiver);
		}

		String dataType;
		String invokerClass = getInvokerClass();
		List<String> invokerClasses = getInvokerClasses();
		List<String> invokerBundles = serviceResolver.getBundleSymbolicName(invokerClass);
		
		if (payload != null) {
			dataType = payload.getClass().getSimpleName();
		}
		else {
			dataType = null;
		}
		DataTransmissionLogEntry logEntry = new DataTransmissionLogEntry(
				dataType,
				new Date(),
				receiver,
				sender,
				invokerClass,
				invokerClasses,
				invokerBundles,
				-1,
				ChannelType.XMPP);
		
		privacyLog.append(logEntry);
		
		return true;
	}
	
	@Override
	public void logContext(Requestor requestor, IIdentity owner) {
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("logContext()");
		}
		
		String invokerClass = getInvokerClass();
		List<String> invokerClasses = getInvokerClasses();
		
		logContext(requestor, owner, -1, invokerClass, invokerClasses);
	}

	@Override
	public void logContext(Requestor requestor, IIdentity owner, int dataSize) {
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("logContext({})", dataSize);
		}

		String invokerClass = getInvokerClass();
		List<String> invokerClasses = getInvokerClasses();
		
		logContext(requestor, owner, dataSize, invokerClass, invokerClasses);
	}
	
	private void logContext(Requestor requestor, IIdentity owner, int dataSize, String invokerClass, List<String> invokerClasses) {

		IIdentity requestorId;
		
		if (requestor == null) {
			requestorId = null;
		}
		else {
			requestorId = requestor.getRequestorId();
		}
		List<String> invokerBundle = serviceResolver.getBundleSymbolicName(invokerClass);
		
		DataAccessLogEntry logEntry = new DataAccessLogEntry(
				new Date(), requestorId, invokerClass, invokerClasses, invokerBundle, owner, dataSize);
		privacyLog.getDataAccess().add(logEntry);
	}

	/* (non-Javadoc)
	 * @see IPrivacyLogAppender#logSN(String, Date, boolean, IIdentity, IIdentity, ChannelType)
	 */
//	@Override
//	public boolean logSN(String dataType, Date time, boolean sentToGroup, IIdentity sender,
//			IIdentity receiver, ChannelType channelId) {
//		
//		LOG.debug("logSN()");
//		LOG.warn("logSN(): not implemented yet");
//
//		return true;
//	}

	/* (non-Javadoc)
	 * @see IPrivacyLogAppender#log(LogEntry)
	 */
	@Override
	public boolean log(DataTransmissionLogEntry entry) {
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("log(DataTransmissionLogEntry)");
		}

		privacyLog.append(entry);

		return true;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.IPrivacyLogAppender#log(org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.DataAccessLogEntry)
	 */
	@Override
	public void log(DataAccessLogEntry entry) {

		if (LOG.isDebugEnabled()) {
			LOG.debug("log(DataAccessLogEntry)");
		}

		privacyLog.getDataAccess().add(entry);
	}
}
