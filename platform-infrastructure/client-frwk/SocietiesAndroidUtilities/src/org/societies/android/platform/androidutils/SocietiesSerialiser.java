///**
// * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
// * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
// * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
// * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
// * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
// * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
// * All rights reserved.
// *
// * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
// * conditions are met:
// *
// * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
// *
// * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
// *    disclaimer in the documentation and/or other materials provided with the distribution.
// *
// * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
// * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
// * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
// * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// */
//package org.societies.android.platform.androidutils;
//
//import java.io.ByteArrayOutputStream;
//
//import org.simpleframework.xml.Serializer;
//import org.simpleframework.xml.convert.Registry;
//import org.simpleframework.xml.convert.RegistryStrategy;
//import org.simpleframework.xml.core.Persister;
//import org.simpleframework.xml.strategy.Strategy;
//import org.societies.maven.converters.URIConverter;
//
//import android.util.Log;
//
///**
// * Describe your class here...
// *
// * @author aleckey
// *
// */
//public class SocietiesSerialiser {
//
//	private static final String LOG_TAG = SocietiesSerialiser.class.getName();
//	private static Serializer s;
//	
//	public SocietiesSerialiser() {
//		Registry registry = new Registry();
//		Strategy strategy = new RegistryStrategy(registry);
//		
//		try {
//			registry.bind(java.net.URI.class, URIConverter.class);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		s = new Persister(strategy);
//	}
//	
//	public String Write(Object payload) throws Exception {
//		Log.d(LOG_TAG, "Serialising payload: " + payload.getClass().getName());
//		ByteArrayOutputStream os = new ByteArrayOutputStream();
//		s.write(payload, os);
//		return os.toString();
//	}
//	
//	public Object Read(Class<? extends Object> c, String xml) throws Exception {
//		//GET CLASS FIRST
//		Object payload = s.read(c, xml);		
//		return payload;
//	}
//	
//}
//
