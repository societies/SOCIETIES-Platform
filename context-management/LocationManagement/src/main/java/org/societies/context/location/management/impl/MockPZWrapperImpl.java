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


import java.util.*;

public class MockPZWrapperImpl  {
	
	public MockPZWrapperImpl() {
		fillMockActiveZones();
		fillMockActiveEntitiesInZones();
		fillMockEntitiesLocations();
	}
	
	public static class Zone {
		public int getZoneId() {
			return zoneId;
		}
		public void setZoneId(int zoneId) {
			this.zoneId = zoneId;
		}

		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}

		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		
		@Override
		public String toString() {
			String result = "{" 
				+ "\"zoneId\":" + zoneId 
				+ ", "
				+ "\"name\":\"" + name + "\"" 
				+ ", "
				+ "\"type\":\"" + type + "\"" 
				+ ", "
				+ "\"description\":\"" + description + "\"" 
				+ "}"
				;
			return result;
		}
		
		private int zoneId;
		private String name;
		private String type;
		private String description;
	}
	
 	public static class ExZone extends Zone {
		public ExZone() {
			tags = new HashSet<String>();
		}

		public Set<String> getTags() {
			return tags;
		}

		public String getPersonalTag() {
			return personalTag;
		}
		public void setPersonalTag(String personalTag) {
			this.personalTag = personalTag;
		}
		
		@Override
		public String toString() {
			String result = "{" 
				+ "\"zoneId\":" + getZoneId() 
				+ ", "
				+ "\"name\":\"" + getName() + "\"" 
				+ ", "
				+ "\"type\":\"" + getType() + "\"" 
				+ ", "
				+ "\"description\":\"" + getDescription() + "\"" 
				+ ", "
				+ "\"tags\":["
				;
			int count = tags.size();
			for (String tag : tags) {
				count--;
				result += "\"" + tag + "\"";
				if (count > 0) {
					result += ", ";
				}
			}
			result += "]"
				+ ", "
				+ "\"personalTag\":\"" + personalTag + "\""
				+ "}"
				;
			return result;
		}
		
		private Set<String> tags;
		private String personalTag;
	}
	
	public static class Location {
		public Location() {
			zones = new ArrayList<MockPZWrapperImpl.ExZone>();
		}
		
		public List<ExZone> getZones() {
			return zones;
		}

		public Double getX() {
			return x;
		}
		public void setX(double x) {
			this.x = x;
		}

		public Double getY() {
			return y;
		}
		public void setY(double y) {
			this.y = y;
		}

		public Double getZ() {
			return z;
		}
		public void setZ(double z) {
			this.z = z;
		}
		
		@Override
		public String toString() {
			String result = "{" 
				+ "\"zones\":[" 
				;
			int count = zones.size();
			for (ExZone zone : zones) {
				count--;
				result += zone;
				if (count > 0) {
					result += ", ";
				}
			}
			result += "]"
				+ ", "
				+ "\"x\":" + x
				+ ", "
				+ "\"y\":" + y
				+ ", "
				+ "\"z\":" + z
				+ "}"
				;
			return result;
		}

		private List<ExZone> zones;
		private Double x;
		private Double y;
		private Double z;
	}

	/**************************************************************/
	/* http://ta-proj02:9082/QueriesGatewayREST/RT/allActiveZones */
	/**************************************************************/
	public Collection<Zone> getActiveZones() {
		return mockActiveZones.values();
	}

	/********************************************************************************/
	/* http://ta-proj02:9082/QueriesGatewayREST/RT/activeEntitiesIdsInZone/{zoneId} */
	/********************************************************************************/
	public Set<String> getActiveEntitiesIdsInZone(int zoneId) {
		Set<String> entities = mockActiveEntitiesIdsInZones.get(zoneId);
		if (entities == null) {
			entities = new HashSet<String>();
		}
		return entities;
	}

	/*******************************************************************************/
	/* http://ta-proj02:9082/QueriesGatewayREST/RT/location/full/entity/{entityId} */
	/*******************************************************************************/
	public Location getEntityFullLocation(String entityId) {
		Location location = mockEntitiesLocations.get(entityId);
		if (location == null) {
			location = new Location();
		}
		return location;
	}
	
	// Mock Data
	private HashMap<Integer, Zone> mockActiveZones;
	private HashMap<Integer, Set<String>> mockActiveEntitiesIdsInZones;
	private HashMap<String, Location> mockEntitiesLocations;
	private static final int DailyPlanet = 0;
	private static final int Earth = 1;
	private static final int LuthorCorp = 4;
	private static final int Metropolis = 5;
	private static final int Smallville = 6;
	private static final String LanaLang = "11:11:11:11:11:11";
	private static final String LoisLane = "22:22:22:22:22:22";
	private static final String LexLuthor = "ff:ff:ff:ff:ff:ff";
	
	private void fillMockActiveZones() {
		mockActiveZones = new HashMap<Integer, Zone>();
		
		Zone zone = new Zone();
		zone.setZoneId(DailyPlanet);
		zone.setType("building");
		zone.setName("Daily Planet");
		zone.setDescription("");
		mockActiveZones.put(zone.getZoneId(), zone);
		
		zone = new Zone();
		zone.setZoneId(Earth);
		zone.setType("planet");
		zone.setName("Earth");
		zone.setDescription("Planet Earth");
		mockActiveZones.put(zone.getZoneId(), zone);
		
		zone = new Zone();
		zone.setZoneId(LuthorCorp);
		zone.setType("building");
		zone.setName("Luthor Corp");
		zone.setDescription("");
		mockActiveZones.put(zone.getZoneId(), zone);
		
		zone = new Zone();
		zone.setZoneId(Metropolis);
		zone.setType("city");
		zone.setName("Metropolis");
		zone.setDescription("");
		mockActiveZones.put(zone.getZoneId(), zone);
		
		zone = new Zone();
		zone.setZoneId(Smallville);
		zone.setType("city");
		zone.setName("Smallville");
		zone.setDescription("");
		mockActiveZones.put(zone.getZoneId(), zone);
	}
	
	private void fillMockActiveEntitiesInZones() {
		mockActiveEntitiesIdsInZones = new HashMap<Integer, Set<String>>();

		Set<String> entities = new HashSet<String>();
		entities.add(LoisLane);
		int zoneId = DailyPlanet;
		mockActiveEntitiesIdsInZones.put(zoneId, entities);
		
		entities = new HashSet<String>();
		entities.add(LoisLane);
		entities.add(LanaLang);
		entities.add(LexLuthor);
		zoneId = Earth;
		mockActiveEntitiesIdsInZones.put(zoneId, entities);
		
		entities = new HashSet<String>();
		entities.add(LexLuthor);
		zoneId = LuthorCorp;
		mockActiveEntitiesIdsInZones.put(zoneId, entities);
		
		entities = new HashSet<String>();
		entities.add(LoisLane);
		entities.add(LexLuthor);
		zoneId = Metropolis;
		mockActiveEntitiesIdsInZones.put(zoneId, entities);
		
		entities = new HashSet<String>();
		entities.add(LanaLang);
		zoneId = Smallville;
		mockActiveEntitiesIdsInZones.put(zoneId, entities);
	}
	
	private void fillMockEntitiesLocations() {
		mockEntitiesLocations = new HashMap<String, Location>();

		Location location = new Location();
		String entityId = LoisLane;

		ExZone zone = new ExZone();
		zone.setZoneId(DailyPlanet);
		zone.setType("building");
		zone.setName("Daily Planet");
		zone.setDescription("");
		zone.getTags().add("offices");
		zone.setPersonalTag("work");
		location.getZones().add(zone);

		zone = new ExZone();
		zone.setZoneId(Earth);
		zone.setType("planet");
		zone.setName("Earth");
		zone.setDescription("Planet Earth");
		zone.getTags().add("planets");
		zone.setPersonalTag("home planet");
		location.getZones().add(zone);

		zone = new ExZone();
		zone.setZoneId(Metropolis);
		zone.setType("city");
		zone.setName("Metropolis");
		zone.setDescription("");
		zone.getTags().add("cities");
		zone.getTags().add("big-cities");
		zone.setPersonalTag("my city");
		location.getZones().add(zone);
		
		location.setX(100.5);
		location.setY(201.0);
		location.setZ(50.25);

		mockEntitiesLocations.put(entityId, location);

		location = new Location();
		entityId = LanaLang;

		zone = new ExZone();
		zone.setZoneId(Earth);
		zone.setType("planet");
		zone.setName("Earth");
		zone.setDescription("Planet Earth");
		zone.getTags().add("planets");
		zone.setPersonalTag("my dear planet");
		location.getZones().add(zone);

		zone = new ExZone();
		zone.setZoneId(Smallville);
		zone.setType("city");
		zone.setName("Smallville");
		zone.setDescription("");
		zone.getTags().add("cities");
		zone.getTags().add("small-cities");
		zone.setPersonalTag("my hometown");
		location.getZones().add(zone);
		
		location.setX(1000);
		location.setY(2000);
		location.setZ(0);

		mockEntitiesLocations.put(entityId, location);

		location = new Location();
		entityId = LexLuthor;

		zone = new ExZone();
		zone.setZoneId(Earth);
		zone.setType("planet");
		zone.setName("Earth");
		zone.setDescription("Planet Earth");
		zone.getTags().add("planets");
		zone.setPersonalTag("main target");
		location.getZones().add(zone);

		zone = new ExZone();
		zone.setZoneId(LuthorCorp);
		zone.setType("building");
		zone.setName("Luthor Corp");
		zone.setDescription("");
		zone.getTags().add("offices");
		zone.setPersonalTag("main office");
		location.getZones().add(zone);

		zone = new ExZone();
		zone.setZoneId(Metropolis);
		zone.setType("city");
		zone.setName("Metropolis");
		zone.setDescription("");
		zone.getTags().add("cities");
		zone.getTags().add("big-cities");
		zone.setPersonalTag("main city");
		location.getZones().add(zone);
		
		location.setX(110.5);
		location.setY(221.0);
		location.setZ(51.75);

		mockEntitiesLocations.put(entityId, location);
	}
}
