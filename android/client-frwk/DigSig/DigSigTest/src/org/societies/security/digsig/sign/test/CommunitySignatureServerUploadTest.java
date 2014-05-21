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
package org.societies.security.digsig.sign.test;

import java.net.URI;

import org.societies.security.digsig.sign.SignActivity;
import org.societies.security.digsig.utility.Net;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

/**
 * Android test case for uploading an XML document to the REST server, then downloading it,
 * signing it and sending it back to the server.
 *
 * @author Mitja Vardjan
 *
 */
public class CommunitySignatureServerUploadTest extends ActivityInstrumentationTestCase2<SignActivity> {

	private static final String TAG = CommunitySignatureServerUploadTest.class.getSimpleName();
	
	protected static final String uploadUri = "http://research.setcce.si:8081/rest/xmldocs/794760269315621?cert=MIIFfTCCBGWgAwIBAgIEQLKKPDANBgkqhkiG9w0BAQUFADA%2BMQswCQYDVQQGEwJzaTEbMBkGA1UE%0AChMSc3RhdGUtaW5zdGl0dXRpb25zMRIwEAYDVQQLEwlzaXRlc3QtY2EwHhcNMDgxMjEwMTE0NjQ5%0AWhcNMTMxMjEwMTIxNjQ5WjBuMQswCQYDVQQGEwJzaTEbMBkGA1UEChMSc3RhdGUtaW5zdGl0dXRp%0Ab25zMRIwEAYDVQQLEwlTSVRFU1QtQ0ExGTAXBgNVBAsTEGNlcnRpZmljYXRlcy13ZWIxEzARBgNV%0ABAMTCk1ha3MgUm9taWgwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDXs88gaClpvjjV%0AY56ntw4CqQCjFpqdUPN6z%2F7wyDmIwIqcBxl8V0226rY0OQz2UbehKld7THFM%2FEnmSQYTmlIuv0CF%0Ai00%2BJrjbWjmZzMGZIl0Hj%2FpDnXjudE0qZOo24c%2BvUBuv31Sa74TNgz8xqgVe6HzQ8TEn%2FFFMW90p%0AsGTqAUP6Tdpggj1FxWChrp9ghXgcj62SXzyrO9Z7FQDNzatK2oY3DRos5JOP4HT6q4nxXuGEknQu%0AKvgh8SwpW4HzAILJpop6O9e1Hj22sEF6F54BVbVnbMq9wwCmdTnB%2FF4MV%2BwM%2BGs6nh3wsvJXrbW1%0ApumFhe0XR5BI1drg8JMCKMTVAgMBAAGjggJRMIICTTALBgNVHQ8EBAMCBaAwKwYDVR0QBCQwIoAP%0AMjAwODEyMTAxMTQ2NDlagQ8yMDEzMTIxMDEyMTY0OVowEQYJYIZIAYb4QgEBBAQDAgWgMCkGCWCG%0ASAGG%2BEIBAgQcFhpodHRwczovL1NJVEVfTkFNRS9jZGEtY2dpLzBIBglghkgBhvhCAQMEOxY5Y2xp%0AZW50Y2dpLmV4ZT9hY3Rpb249Y2hlY2tSZXZvY2F0aW9uJiZDUkw9Y249Q1JMMiZzZXJpYWw9MBwG%0AA1UdEQQVMBOBEW1ha3Mucm9taWhAZ292LnNpMIIBAwYDVR0fBIH7MIH4MFWgU6BRpE8wTTELMAkG%0AA1UEBhMCc2kxGzAZBgNVBAoTEnN0YXRlLWluc3RpdHV0aW9uczESMBAGA1UECxMJc2l0ZXN0LWNh%0AMQ0wCwYDVQQDEwRDUkwyMIGeoIGboIGYhmVsZGFwOi8veDUwMC5nb3Yuc2kvb3U9c2l0ZXN0LWNh%0ALG89c3RhdGUtaW5zdGl0dXRpb25zLGM9c2k%2Fb3U9c2l0ZXN0LWNhLG89c3RhdGUtaW5zdGl0dXRp%0Ab25zLGM9c2k%2FYmFzZYYvaHR0cDovL3d3dy5zaWdlbi1jYS5zaS9jcmwvc2l0ZXN0L3NpdGVzdC1j%0AYS5jcmwwHwYDVR0jBBgwFoAUVEkHRofPHYmdyq5yp6igoGm3IecwHQYDVR0OBBYEFCDTjILeTbcQ%0ATaSx3DtjAIzvuASYMAkGA1UdEwQCMAAwGQYJKoZIhvZ9B0EABAwwChsEVjcuMQMCA6gwDQYJKoZI%0AhvcNAQEFBQADggEBAIJ1eQawmdsZ%2FUsPSVg3dRGISw5IFHcJ56Za00U%2BDVLyuU3HOrSRYfQBuOJ5%0AjBjRK8UPAz7kmb1HKAHA86RTVFsiKJx5ibEW%2BXG%2FiIDyNX6cgCAHX4Pl00ycZXf%2FdPFpigbuQvZy%0AUDa6frt1TOkRXnsORq0U23jcx4NY1pfq8X4aaZPjCzgSkxuob0aa%2BadqUntdBW9tu8IFeCpa7S8Q%0ArMWAV2vJY4s4IGfXdCtySK8MouWWaZnzo8WBQxRY7uQm%2B%2BdXtdIq0TOW4xD3hBkHOpcHR3MYrzGL%0AKJfkMZjtgJULhk4eHsYhIXCQgwKKH%2BJ9H0Lg9R7F7XaS%2BQBSZhSof6g%3D%0A&endpoint=http%3A%2F%2F192.168.1.92%2Fsocieties%2Fcommunity-signature%2Fnotify&minnumsig=2";
	protected static final String downloadUri = "http://research.setcce.si:8081/rest/xmldocs/794760269315621?sig=A8864353EE88B14F284C3B13FF2BD59089CC3D2422BBF5A6DF7F06AAC1F7BF6F21AB5C297A18CF96C0AD52AA856C51E553BF7FF004F8F7981474C425F9313DEA3A2E878B03F7B290E98A59917A83435EB482EBEED2D03B69BF41B9C96FCF5AEA17B9955B5461E9CDF018F824BFAA4959E05140574F94CFE077F4FCAC2FDFCB8B4FEA1D58000453148D70D2590AC588798A565D7C52CB194C4B6A9BE027B17BA5E18AED0C35CBF595BDF1C25ACB0005F99F7678E6380559882C52671FA6DF86A665A6F2BBEE2E142008197161522B1E31D9F2665D431EE43BE7578AA80DE8760E07649F4785DA6B10D2E5B7CAFC0D1A840C732730ACBE3AC10E045A17FC69A704";
	private static final String sourceUri = "http://192.168.1.73/tmp/societies/doc.xml";
//	private static final String sourceUri = "http://192.168.1.73/tmp/societies/doc-large.xml";
	
	private Activity mActivity;
	
	public CommunitySignatureServerUploadTest() {
		super(SignActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		
		Log.i(TAG, "setUp");
		
		// Required by JUnit
		super.setUp();

		setActivityInitialTouchMode(false);
		mActivity = getActivity();
	}
	
	public void testPreConditions() {
		assertNotNull(mActivity);
	}

	@MediumTest
	public void testInitialDocumentUpload() throws Exception {
		
		Log.i(TAG, "testInitialDocumentUpload");

		Net source = new Net(new URI(sourceUri));
		Net upload = new Net(new URI(uploadUri));
		Net download = new Net(new URI(downloadUri));

		String contents = source.getString();
		
		download.delete();
		assertTrue(upload.put(contents));
//		download.getString();
		
		Log.i(TAG, "testInitialDocumentUpload: uploaded successfully");
	}
}
