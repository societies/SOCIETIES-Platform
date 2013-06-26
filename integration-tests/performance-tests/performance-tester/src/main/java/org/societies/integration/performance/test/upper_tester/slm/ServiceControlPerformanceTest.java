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
package org.societies.integration.performance.test.upper_tester.slm;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.servicelifecycle.IServiceControl;
import org.societies.api.schema.servicelifecycle.servicecontrol.ResultMessage;
import org.societies.api.schema.servicelifecycle.servicecontrol.ServiceControlResult;
import org.societies.api.services.ServiceUtils;
import org.societies.integration.performance.test.lower_tester.PerformanceLowerTester;
import org.societies.integration.performance.test.lower_tester.PerformanceTestMgmtInfo;
import org.societies.integration.performance.test.lower_tester.PerformanceTestResult;
import org.societies.integration.performance.test.upper_tester.slm.model.Install3pServiceParameters;
import org.societies.integration.performance.test.upper_tester.slm.model.InstallShared3pServiceParameters;
import org.societies.integration.performance.test.upper_tester.slm.model.Share3pServiceParameters;

/**
 * @author Olivier Maridat (Trialog)
 */
public class ServiceControlPerformanceTest implements IServiceControlPerformanceTest {
	private static final Logger LOG = LoggerFactory.getLogger(ServiceControlPerformanceTest.class);

	private ICommManager commManager;
	private IServiceControl serviceControl;


	@Override
	public void testInstall3pService(PerformanceTestMgmtInfo performanceTestMgmtInfo, Install3pServiceParameters parameters) {
		// -- Init
		PerformanceLowerTester performanceLowerTester = new PerformanceLowerTester(performanceTestMgmtInfo);
		performanceLowerTester.testStart(this.getClass().getName(), commManager);
		PerformanceTestResult performanceTestResult = null;

		// -- Verify
		if (null == parameters) {
			performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "Install3pServiceParameters are null!", PerformanceTestResult.ERROR_STATUS);
		}
		if (null == parameters.getServicePath() || "".equals(parameters.getServicePath())) {
			performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "Install3pServiceParameters::servicePath is empty!", PerformanceTestResult.ERROR_STATUS);
		}

		// -- Parameters
		URL serviceBundleUrl = this.getClass().getClassLoader().getResource(parameters.getServicePath());

		// -- Launch the test
		try {
			ServiceControlResult result = serviceControl.installService(serviceBundleUrl, parameters.getCssOwnerId()).get();
			boolean success = (null != result && ResultMessage.SUCCESS.equals(result.getMessage()));
			String resultMsg = "Result: "+(success ? ServiceUtils.serviceResourceIdentifierToString(result.getServiceId())+" is installed" : "service can't be installed+")+"! ("+(null != result ? result.getMessage() : "result is empty")+")";
			LOG.info(resultMsg);
			performanceTestResult = new PerformanceTestResult(this.getClass().getName(), resultMsg, success ? PerformanceTestResult.SUCCESS_STATUS : PerformanceTestResult.FAILED_STATUS);
		}
		catch (Exception e) {
			LOG.error("["+e.getMessage()+"] Ouch, can't install this 3p service.", e);
			performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "Ouch, can't install this 3p service \""+serviceBundleUrl.toString()+"\"", PerformanceTestResult.ERROR_STATUS);
		}

		// -- Send result
		performanceLowerTester.testFinish(performanceTestResult);
	}

	@Override
	public void testShare3pService(PerformanceTestMgmtInfo performanceTestMgmtInfo, Share3pServiceParameters parameters) {

	}

	@Override
	public void testInstallShared3pService(PerformanceTestMgmtInfo performanceTestMgmtInfo, InstallShared3pServiceParameters parameters) {

	}
	
	
	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}
	public void setServiceControl(IServiceControl serviceControl) {
		this.serviceControl = serviceControl;
	}
}

