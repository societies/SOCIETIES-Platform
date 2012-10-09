/**
 * Copyright (c) 2011, SOCIETIES Consortium
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
package org.societies.example;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * @author Olivier Maridat
 *
 */
public class GsonTest {
	private static Logger LOG = LoggerFactory.getLogger(GsonTest.class.getSimpleName());

	private static Gson gsonManager;
	
	@BeforeClass
	public static void setUpClass() {
		gsonManager = new Gson();
	}


	@Test
	public void testFromJsonToJson() {
		String testTitle = new String("testFromJsonToJson");
		String testDescription = new String("Create a Java object from JSON formatted string, and then generate a JSON formatted string from this Java object.");
		LOG.info("[TEST "+testTitle+"] "+testDescription);
		
		String complexJsonData = "{\"id\":1,\"type\":2,\"data\":[\"yeah\",\"yoh ho!\"]}";
		Data complexData = gsonManager.fromJson(complexJsonData, Data.class);
		String complexJsonDataRetrieved = gsonManager.toJson(complexData);
		LOG.info("JSON: "+complexJsonData);
		LOG.info("Retrieved from JSON: "+complexData.toString());
		LOG.info("Retrieved JSON: "+complexJsonDataRetrieved);
		assertEquals("From Json to Java and then Json not the same", complexJsonData, complexJsonDataRetrieved);
	}
}
