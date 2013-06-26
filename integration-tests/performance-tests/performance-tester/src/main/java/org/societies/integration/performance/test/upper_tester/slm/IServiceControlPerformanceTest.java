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

import org.societies.integration.performance.test.lower_tester.PerformanceTestMgmtInfo;
import org.societies.integration.performance.test.upper_tester.slm.model.Install3pServiceParameters;
import org.societies.integration.performance.test.upper_tester.slm.model.InstallShared3pServiceParameters;
import org.societies.integration.performance.test.upper_tester.slm.model.Share3pServiceParameters;

/**
 * @author Olivier Maridat (Trialog
 */
public interface IServiceControlPerformanceTest {

	/**
	 * To test the installation of a third party service in a CSS
	 * @pre a 3P service archive (jar, war or apk) has to be available, it will installed in the given CSS container.
	 * @param performanceTestInfo MUST be in all performance test, this object groups some information sent by the Engine
	 * @param parameters an object containing parameters needed for this test  
	 */
	public void testInstall3pService(PerformanceTestMgmtInfo performanceTestMgmtInfo, Install3pServiceParameters parameters);

	/**
	 * To test the sharing of a third party service through a CIS
	 * @pre a CIS should has been created
	 * @pre the CSS has to be a member of this CIS
	 * @param performanceTestInfo MUST be in all performance test, this object groups some information sent by the Engine
	 * @param parameters an object containing parameters needed for this test  
	 */
	public void testShare3pService(PerformanceTestMgmtInfo performanceTestMgmtInfo, Share3pServiceParameters parameters);

	/**
	 * To test the installation of third party service shared through a CIS
	 * @pre a CIS should has been created
	 * @pre The service to install has to be shared through this CIS
	 * @pre all CSS have to be a member of this CIS
	 * @param performanceTestInfo MUST be in all performance test, this object groups some information sent by the Engine
	 * @param parameters an object containing parameters needed for this test  
	 */
	public void testInstallShared3pService(PerformanceTestMgmtInfo performanceTestMgmtInfo, InstallShared3pServiceParameters parameters);
}

