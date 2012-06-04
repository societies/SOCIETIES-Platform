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
package org.societies.context.location.management.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Set;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			int option = 0;
			while (option != OptionExit) {
				System.out.println("****************************");
				System.out.println("Menu");
				System.out.println("----");
				System.out.println("1. Active Zones");
				System.out.println("2. Active Entities IDs In Zone");
				System.out.println("3. Entity Full Location");
				System.out.println("4. Exit");
				System.out.print("Select Option: ");
				String line = br.readLine();
				if (line.length() != 1) {
					System.out.println("ERROR - Illegal Option: " + line);
					continue;
				}
				try {
					option = Integer.parseInt(line);
				} catch (Exception dummy) {
					System.out.println("ERROR - Illegal Option: " + line);
					continue;
				}
				switch (option) {
				case OptionActiveZones:
					activeZones();
					break;
				case OptionActiveEntitiesIdsInZone:
					activeEntitiesIdsInZone();
					break;
				case OptionEntityFullLocation:
					entityFullLocation();
					break;
				case OptionExit:
					break;
				default:
					System.out.println("ERROR - Illegal Option: " + line);
				}
			}
			System.out.println("****************************");
			System.out.println("Bye!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static final int OptionActiveZones = 1;
	private static final int OptionActiveEntitiesIdsInZone = 2;
	private static final int OptionEntityFullLocation = 3;
	private static final int OptionExit = 4;

	private static MockPZWrapperImpl pz = new MockPZWrapperImpl();
	private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	
	private static void activeZones() {
		System.out.println("****************************");
		System.out.println("Active Zones");
		System.out.println("------------");
		/*
		Collection<MockPZWrapperImpl.Zone> zones = pz.getActiveZones();
		System.out.println("[");
		int count = zones.size();
		for (MockPZWrapperImpl.Zone zone : zones) {
			count--;
			System.out.println(zone + (count > 0 ? ", " : ""));
		}
		System.out.println("]");*/
	}
	
	private static void activeEntitiesIdsInZone() throws IOException {
		/*
		System.out.println("****************************");
		System.out.println("Active Entities In Zone");
		System.out.println("-----------------------");
		System.out.print("Enter Zone: ");
		String line = br.readLine();
		int zoneId;
		try {
			zoneId = Integer.parseInt(line);
		} catch (Exception dummy) {
			System.out.println("ERROR - Illegal Zone ID: " + line);
			return;
		}
		Set<String> entitiesIds = pz.getActiveEntitiesIdsInZone(zoneId);
		System.out.println("[");
		int count = entitiesIds.size();
		for (String entityId : entitiesIds) {
			count--;
			System.out.println("\"" + entityId + "\"" + (count > 0 ? ", " : ""));
		}
		System.out.println("]");
		*/
	}
	
	private static void entityFullLocation() throws IOException {
		System.out.println("****************************");
		System.out.println("Entity Full Location");
		System.out.println("--------------------");
		System.out.print("Enter Entity ID: ");
		String entityId = br.readLine();
		//MockPZWrapperImpl.Location location = pz.getEntityFullLocation(entityId);
		//System.out.println(location);
	}

}
