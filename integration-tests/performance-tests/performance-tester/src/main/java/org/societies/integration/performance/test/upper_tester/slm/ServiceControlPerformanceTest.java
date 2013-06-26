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
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.servicecontrol.ResultMessage;
import org.societies.api.schema.servicelifecycle.servicecontrol.ServiceControlResult;
import org.societies.api.services.ServiceUtils;
import org.societies.integration.performance.test.lower_tester.PerformanceLowerTester;
import org.societies.integration.performance.test.lower_tester.PerformanceTestMgmtInfo;
import org.societies.integration.performance.test.lower_tester.PerformanceTestResult;
import org.societies.integration.performance.test.upper_tester.slm.model.Install3pServiceParameters;
import org.societies.integration.performance.test.upper_tester.slm.model.InstallShared3pServiceParameters;
import org.societies.integration.performance.test.upper_tester.slm.model.Share3pServiceParameters;
import org.societies.integration.performance.test.upper_tester.slm.model.Uninstall3pServiceParameters;

/**
 * @author Olivier Maridat (Trialog)
 */
public class ServiceControlPerformanceTest implements IServiceControlPerformanceTest {
	private static final Logger LOG = LoggerFactory.getLogger(ServiceControlPerformanceTest.class);

	private ICommManager commManager;
	private IServiceControl serviceControl;
	private IServiceDiscovery serviceDiscovery;


	@Override
	public void testInstall3pService(PerformanceTestMgmtInfo performanceTestMgmtInfo, Install3pServiceParameters parameters) {
		// -- Init
		PerformanceLowerTester performanceLowerTester = initResult(performanceTestMgmtInfo);
		PerformanceTestResult performanceTestResult = null;

		// -- Verify
		if (null == parameters) {
			performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "Install3pServiceParameters are null!", PerformanceTestResult.ERROR_STATUS);
		}
		if (null == parameters.getServicePath() || "".equals(parameters.getServicePath())) {
			performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "Install3pServiceParameters::servicePath is empty!", PerformanceTestResult.ERROR_STATUS);
		}

		// -- Parameters
		if (null == parameters.getServicePath() || "".equals(parameters.getCssOwnerId())) {
			parameters.setCssOwnerId(commManager.getIdManager().getThisNetworkNode().getJid());	
		}
		URL serviceBundleUrl = this.getClass().getClassLoader().getResource(parameters.getServicePath());

		// -- Launch the test
		try {
			ServiceControlResult result = serviceControl.installService(serviceBundleUrl, parameters.getCssOwnerId()).get();
			performanceTestResult = generateResult(result, "", "installed");
		}
		catch (Exception e) {
			performanceTestResult = generateErrorResult(e, serviceBundleUrl.toString(), "install");
		}

		// -- Send result
		performanceLowerTester.testFinish(performanceTestResult);
	}

	@Override
	public void testShare3pService(PerformanceTestMgmtInfo performanceTestMgmtInfo, Share3pServiceParameters parameters) {
		// -- Init
		PerformanceLowerTester performanceLowerTester = initResult(performanceTestMgmtInfo);
		PerformanceTestResult performanceTestResult = null;

		// -- Verify
		if (null == parameters) {
			performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "Share3pServiceParameters are null!", PerformanceTestResult.ERROR_STATUS);
		}
		if (null == parameters.getServiceId() || "".equals(parameters.getServiceId())) {
			performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "Share3pServiceParameters::serviceId is empty!", PerformanceTestResult.ERROR_STATUS);
		}

		// -- Parameters
		ServiceResourceIdentifier serviceResourceId = ServiceUtils.generateServiceResourceIdentifierFromString(parameters.getServiceId());
		if (null == serviceResourceId) {
			performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "Can't retrieve a ServiceResourceIdentifier from this 3p service id: \""+parameters.getServiceId()+"\"", PerformanceTestResult.ERROR_STATUS);
		}
		
		// -- Launch the test
		try {
			Service service = serviceDiscovery.getService(serviceResourceId).get();
			if (null == service) {
				performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "Can't find this 3p service: \""+parameters.getServiceId()+"\"", PerformanceTestResult.ERROR_STATUS);
			}
			ServiceControlResult result = serviceControl.shareService(service, parameters.getCisId()).get();
			performanceTestResult = generateResult(result, "", "shared");
		}
		catch (Exception e) {
			performanceTestResult = generateErrorResult(e, parameters.getServiceId(), "share");
		}

		// -- Send result
		performanceLowerTester.testFinish(performanceTestResult);
	}

	@Override
	public void testUnshare3pService(PerformanceTestMgmtInfo performanceTestMgmtInfo, Share3pServiceParameters parameters) {
		// -- Init
		PerformanceLowerTester performanceLowerTester = initResult(performanceTestMgmtInfo);
		PerformanceTestResult performanceTestResult = null;

		// -- Verify
		if (null == parameters) {
			performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "Share3pServiceParameters are null!", PerformanceTestResult.ERROR_STATUS);
		}
		if (null == parameters.getServiceId() || "".equals(parameters.getServiceId())) {
			performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "Share3pServiceParameters::serviceId is empty!", PerformanceTestResult.ERROR_STATUS);
		}

		// -- Parameters
		ServiceResourceIdentifier serviceResourceId = ServiceUtils.generateServiceResourceIdentifierFromString(parameters.getServiceId());

		// -- Launch the test
		try {
			Service service = serviceDiscovery.getService(serviceResourceId).get();
			ServiceControlResult result = serviceControl.unshareService(service, parameters.getCisId()).get();
			performanceTestResult = generateResult(result, "", "unshared");
		}
		catch (Exception e) {
			performanceTestResult = generateErrorResult(e, parameters.getServiceId(), "unshare");
		}

		// -- Send result
		performanceLowerTester.testFinish(performanceTestResult);
	}

	@Override
	public void testInstallShared3pService(PerformanceTestMgmtInfo performanceTestMgmtInfo, InstallShared3pServiceParameters parameters) {
		// -- Init
		PerformanceLowerTester performanceLowerTester = initResult(performanceTestMgmtInfo);
		PerformanceTestResult performanceTestResult = null;

		// -- Verify
		if (null == parameters) {
			performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "Share3pServiceParameters are null!", PerformanceTestResult.ERROR_STATUS);
		}
		if (null == parameters.getServiceId() || "".equals(parameters.getServiceId())) {
			performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "Share3pServiceParameters::serviceId is empty!", PerformanceTestResult.ERROR_STATUS);
		}

		// -- Parameters
		ServiceResourceIdentifier serviceResourceId = ServiceUtils.generateServiceResourceIdentifierFromString(parameters.getServiceId());
		if (null == serviceResourceId) {
			performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "Can't retrieve a ServiceResourceIdentifier from this 3p service id: \""+parameters.getServiceId()+"\"", PerformanceTestResult.ERROR_STATUS);
		}

		// -- Launch the test
		try {
			Service service = serviceDiscovery.getService(serviceResourceId).get();
			if (null == service) {
				performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "Can't find this 3p service: \""+parameters.getServiceId()+"\"", PerformanceTestResult.ERROR_STATUS);
			}
			ServiceControlResult result = serviceControl.installService(service, commManager.getIdManager().getThisNetworkNode().getJid()).get();
			performanceTestResult = generateResult(result, "", "installed (shared)");
		}
		catch (Exception e) {
			performanceTestResult = generateErrorResult(e, parameters.getServiceId(), "install (shared)");
		}

		// -- Send result
		performanceLowerTester.testFinish(performanceTestResult);
	}

	@Override
	public void testUninstall3pService(PerformanceTestMgmtInfo performanceTestMgmtInfo, Uninstall3pServiceParameters parameters) {
		// -- Init
		PerformanceLowerTester performanceLowerTester = initResult(performanceTestMgmtInfo);
		PerformanceTestResult performanceTestResult = null;

		// -- Verify
		if (null == parameters) {
			performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "Uninstall3pServiceParameters are null!", PerformanceTestResult.ERROR_STATUS);
		}
		if (null == parameters.getServiceId() || "".equals(parameters.getServiceId())) {
			performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "Uninstall3pServiceParameters::serviceId is empty!", PerformanceTestResult.ERROR_STATUS);
		}

		// -- Parameters
		ServiceResourceIdentifier serviceResourceId = ServiceUtils.generateServiceResourceIdentifierFromString(parameters.getServiceId());

		// -- Launch the test
		try {
			ServiceControlResult result = serviceControl.uninstallService(serviceResourceId).get();
			performanceTestResult = generateResult(result, "", "uninstalled");
		}
		catch (Exception e) {
			performanceTestResult = generateErrorResult(e, parameters.getServiceId(), "uninstall");
		}

		// -- Send result
		performanceLowerTester.testFinish(performanceTestResult);
	}

	// --- Private Methods
	private PerformanceLowerTester initResult(PerformanceTestMgmtInfo performanceTestMgmtInfo) {
		PerformanceLowerTester performanceLowerTester = new PerformanceLowerTester(performanceTestMgmtInfo);
		performanceLowerTester.testStart(this.getClass().getName(), commManager);
		return performanceLowerTester;
	}

	private PerformanceTestResult generateResult(ServiceControlResult result, String data, String action) {
		boolean success = (null != result && ResultMessage.SUCCESS.equals(result.getMessage()));
		if (null != result && null != result.getServiceId() && !"".equals(result.getServiceId())) {
			data = ServiceUtils.serviceResourceIdentifierToString(result.getServiceId())+" "+data;
		}
		String resultMsg = "Result: "+(success ? data+" is "+action : "service can't be "+action)+"! ("+(null != result ? result.getMessage() : "result is empty")+")";
		LOG.info(resultMsg);
		return new PerformanceTestResult(this.getClass().getName(), resultMsg, success ? PerformanceTestResult.SUCCESS_STATUS : PerformanceTestResult.FAILED_STATUS);
	}

	private PerformanceTestResult generateErrorResult(Exception e, String data, String action) {
		String resultMsg = "["+e+"] Ouch, can't "+action+" this 3p service  \""+data+"\".";
		LOG.error(resultMsg, e);
		return new PerformanceTestResult(this.getClass().getName(), resultMsg, PerformanceTestResult.ERROR_STATUS);
	}


	// --- Dependency Injection
	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}
	public void setServiceControl(IServiceControl serviceControl) {
		this.serviceControl = serviceControl;
	}
	public void setServiceDiscovery(IServiceDiscovery serviceDiscovery) {
		this.serviceDiscovery = serviceDiscovery;
	}
}

