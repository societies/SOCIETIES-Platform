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

package org.societies.integration.performance.test.upper_tester.rafik;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.integration.performance.test.lower_tester.PerformanceLowerTester;
import org.societies.integration.performance.test.lower_tester.PerformanceTestMgmtInfo;
import org.societies.integration.performance.test.lower_tester.PerformanceTestResult;
import org.societies.integration.performance.test.upper_tester.rafik.cismgmt.CisManagerClientCallback;
import org.societies.integration.performance.test.upper_tester.rafik.cismgmt.CreateCisParameters;
import org.societies.integration.performance.test.upper_tester.rafik.cismgmt.ICisMgmtPerformanceTest;
import org.societies.integration.performance.test.upper_tester.rafik.cismgmt.JoinCisParameters;


public class PerformanceTestImpl implements ICisMgmtPerformanceTest {

	private static Logger LOG = LoggerFactory.getLogger(PerformanceTestImpl.class);

	private ICisManager cisManager;
	private CisManagerClientCallback cisManagerClientCallback;

	private ICommManager commManager;

	private PerformanceLowerTester performanceLowerTester;
	private PerformanceTestResult performanceTestResult;

	public PerformanceTestImpl(){	
	}

	public void setCisManager(ICisManager cisManager) {
		if (null != cisManager) 
		{
			LOG.info("### [PerformanceTestImpl] cisManager injected");
			this.cisManager = cisManager;
		}
	}

	public ICisManager getCisManager()
	{
		if (null != cisManager) {
			LOG.info("### [PerformanceTestImpl] this.cisManager non null");
			return cisManager;
		}
		else {
			LOG.info("### [PerformanceTestImpl] this.cisManager null");
			return null;
		}
	}



	public void setCommManager(ICommManager commManager) {
		if (null != commManager) 
		{
			LOG.info("### [PerformanceTestImpl] commManager injected");
			this.commManager = commManager;
		}
	}

	public ICommManager getCommManager()
	{
		if (null != commManager) {
			LOG.info("### [PerformanceTestImpl] this.commManager non null");
			return commManager;
		}
		else {
			LOG.info("### [PerformanceTestImpl] this.commManager null");
			return null;
		}
	}


	@Override
	public void joinCisTest(PerformanceTestMgmtInfo performanceTestMgmtInfo, JoinCisParameters joinCisParameters) 
	{
		if (null != performanceTestMgmtInfo && null != joinCisParameters)
		{
			String cssOwnerId = joinCisParameters.getCssOwnerId();
			String cisId = joinCisParameters.getCisId();
			
			//The following 2 lines are mandatory in the beginning of the test 
			performanceLowerTester = new PerformanceLowerTester(performanceTestMgmtInfo);
			performanceLowerTester.testStart(this.getClass().getName(), getCommManager());	

			LOG.info("### [PerformanceTestImpl] cssOwnerId: " + cssOwnerId + "  cisId: "+ cisId);

			if (null != cssOwnerId && null != cisId && !"".equals(cssOwnerId) && !"".equals(cisId)) 
			{
				CisAdvertisementRecord adv = new CisAdvertisementRecord();

				adv.setCssownerid(cssOwnerId);
				adv.setId(cisId);

				if(null != getCisManager()) 
				{
					//If the result will be provided by the a callback, the performanceLowerTester instance MUST be given to callback implementation so that this latter can return a result. 
					cisManagerClientCallback = new CisManagerClientCallback(performanceLowerTester);

					getCisManager().joinRemoteCIS(adv, cisManagerClientCallback);	
				}
				else
				{
					performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "The CIS Manager instance is null!",
																	PerformanceTestResult.ERROR_STATUS);
					
					performanceLowerTester.testFinish(performanceTestResult);
				}
			}
			else
			{
				performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "The cssOwnerId and/or cisId are null or empty!",
																PerformanceTestResult.ERROR_STATUS);
				performanceLowerTester.testFinish(performanceTestResult);
			}
		}
		else
		{
			performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "The performanceTestMgmtInfo and/or joinCisParameters are null!",
																	PerformanceTestResult.ERROR_STATUS);
			performanceLowerTester.testFinish(performanceTestResult);
		}
	}

	
	/* (non-Javadoc)
	 * @see org.societies.integration.performance.test.upper_tester.rafik.cismgmt.ICisMgmtPerformanceTest#createCisTest(org.societies.integration.performance.test.lower_tester.PerformanceTestMgmtInfo, org.societies.integration.performance.test.upper_tester.rafik.cismgmt.CreateCisParameters)
	 */
	@Override
	public void createCisTest(PerformanceTestMgmtInfo performanceTestInfo,
			CreateCisParameters createCisParameters) {
		// TODO Auto-generated method stub
		
	}
}
