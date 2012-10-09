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


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.context.location.management.PZWrapper;
import org.societies.context.location.management.PzPropertiesReader;
import org.societies.context.location.management.api.ITag;
import org.societies.context.location.management.api.IUserLocation;
import org.societies.context.location.management.api.IZone;
import org.societies.context.location.management.api.IZoneId;
import org.societies.context.location.management.api.impl.CoordinateImpl;
import org.societies.context.location.management.api.impl.TagImpl;
import org.societies.context.location.management.api.impl.UserLocationImpl;
import org.societies.context.location.management.api.impl.ZoneIdImpl;
import org.societies.context.location.management.api.impl.ZoneImpl;


public class MockPZWrapperImpl implements PZWrapper {
	/** The logging facility. */
	private static final Logger log = LoggerFactory.getLogger(MockPZWrapperImpl.class);
	
	private final Timer timer = new Timer();
	
	
	
	public MockPZWrapperImpl() {
		fillMockActiveZones();
		fillMockActiveEntitiesInZones();
		fillMockEntitiesLocations();
	}
	
	
	@SuppressWarnings("unused")
	private void init(){
		int generateNewLocationsCycle = PzPropertiesReader.instance().getPzMockGenerateLocationsCycle();
		try{
			timer.schedule(new CleanTimerTask(), generateNewLocationsCycle, generateNewLocationsCycle);
		}catch (Exception e) {
			log.error("couldn't create timer task in MockPZWrapperImpl",e);
		}
	}
	
	@SuppressWarnings("unused")
	private void cleanup(){
		try{
			if (timer != null){
				timer.cancel();
			}
		}catch (Exception e) {
			log.error("couldn't stop timer task in MockPZWrapperImpl",e);
		}
	}
	
	private class CleanTimerTask extends TimerTask{

		@Override
		public void run() {
			try{
				synchronized (this) {
					mockEntitiesLocations.clear();
				}
			}catch (Exception e) {
				log.error("failed to clear mockEntitiesLocations object",e);
			}
		}
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
	
 	private class ExZone extends Zone {
		public ExZone(Zone zone){
			this.setDescription(zone.getDescription());
			this.setName(zone.getName());
			this.setType(zone.getType());
			this.setZoneId(zone.getZoneId());
		}
 		
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
		
		private Set<String> tags = new HashSet<String>();
		private String personalTag;
	}
	
	private class Location {
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
	public Collection<IZone> getActiveZones() {
		Collection<MockPZWrapperImpl.ExZone> mockZones;
		synchronized (this) {
			mockZones = mockActiveZones.values();	
		}
		
		Collection<IZone> zones = new ArrayList<IZone>();
		
		IZone zone;
		for (MockPZWrapperImpl.Zone mockZone : mockZones){
			zone = convert(mockZone);
			zones.add(zone);
		}
		return zones;
	}

	/********************************************************************************/
	/* http://ta-proj02:9082/QueriesGatewayREST/RT/activeEntitiesIdsInZone/{zoneId} */
	/********************************************************************************/
	public Set<String> getActiveEntitiesIdsInZone(IZoneId zoneId) {
		Set<String> entities;
		synchronized (this) {
			entities = mockActiveEntitiesIdsInZones.get(zoneId.getId());	
		}
		if (entities == null) {
			entities = new HashSet<String>();
		}
		return entities;
	}

	
	
	/*******************************************************************************/
	/* http://ta-proj02:9082/QueriesGatewayREST/RT/location/full/entity/{entityId} */
	/*******************************************************************************/
	public IUserLocation getEntityFullLocation(String entityId) {
		Location location;
		synchronized (this) {
			location = mockEntitiesLocations.get(entityId);	
			if (location == null) {
				location = new Location();
				location = generateRandomLocation(location);
				mockEntitiesLocations.put(entityId,location);	
			}
		}
		
		IUserLocation userLocation = new UserLocationImpl();
		List<IZone> zones = convert(location.getZones());
		userLocation.setZones(zones);
		userLocation.setXCoordinate(new CoordinateImpl(location.getX()));
		userLocation.setYCoordinate(new CoordinateImpl(location.getY()));
		userLocation.setId(entityId);
		return userLocation;
	}
	
	
	/*
	 * Helpers methods
	 * 
	 */
	
	private Location generateRandomLocation(Location location){
		location.setX(generateRand());
		location.setY(generateRand());
		location.setZ(generateRand());
		
		int zoneID = (int) Math.ceil( (Math.random()*100)%4); 
		ExZone zone = mockActiveZones.get(Integer.valueOf(zoneID));
		ExZone exZone = new ExZone(zone);
		exZone.setPersonalTag("Personal Tag"+zoneID);
		exZone.getTags().addAll(zone.getTags());
		location.getZones().add(exZone);
		return location;
	}
	
	private double generateRand(){
		double rand = Math.random()*1000;
		return Math.ceil(rand);
	}
	
	private List<IZone> convert(List<ExZone> mockZones){
		List<IZone> zones = new ArrayList<IZone>();
		
		IZone zone;
		for (ExZone exZone : mockZones){
			zone = convert(exZone);
			zones.add(zone);
		}
		return zones;
	}
	
	private IZone convert(MockPZWrapperImpl.ExZone mockZone){
		IZone zone = convert((MockPZWrapperImpl.Zone)mockZone);
		zone.setPersonalTag(new TagImpl(mockZone.getPersonalTag()));
		
		List<ITag> tags = new ArrayList<ITag>();
		for (String tag : mockZone.getTags()){
			tags.add(new TagImpl(tag));
		}
		zone.setTags(tags);
		
		return zone;
		
	}
	
	private IZone convert(MockPZWrapperImpl.Zone mockZone){
		IZone zone = new ZoneImpl();
		
		IZoneId zoneId = new ZoneIdImpl(); 
		zoneId.setId(mockZone.getZoneId());
		
		zone.setDescription(mockZone.getDescription());
		zone.setName(mockZone.getName());
		zone.setType(mockZone.getType());
		zone.setId(zoneId);
		return zone;
	}
	
	// Mock Data
	private HashMap<Integer, ExZone> mockActiveZones;
	private HashMap<Integer, Set<String>> mockActiveEntitiesIdsInZones;
	private HashMap<String, Location> mockEntitiesLocations;
	private static final int DailyPlanet = 0;
	private static final int Earth = 1;
	private static final int LuthorCorp = 2;
	private static final int Metropolis = 3;
	private static final int Smallville = 4;
	private static final String LanaLang = "11:11:11:11:11:11";
	private static final String LoisLane = "22:22:22:22:22:22";
	private static final String LexLuthor = "ff:ff:ff:ff:ff:ff";
	
	private void fillMockActiveZones() {
		mockActiveZones = new HashMap<Integer, ExZone>();
		
		ExZone zone = new ExZone();
		zone.setZoneId(DailyPlanet);
		zone.setType("building");
		zone.setName("Daily Planet");
		zone.setDescription("");
		zone.getTags().add("Office Planet");
		mockActiveZones.put(zone.getZoneId(), zone);
		
		zone = new ExZone();
		zone.setZoneId(Earth);
		zone.setType("planet");
		zone.setName("Earth");
		zone.setDescription("Planet Earth");
		zone.getTags().add("earth Zone");
		mockActiveZones.put(zone.getZoneId(), zone);
		
		zone = new ExZone();
		zone.setZoneId(LuthorCorp);
		zone.setType("building");
		zone.setName("Luthor Corp");
		zone.setDescription("");
		zone.getTags().add("office Luthor");
		mockActiveZones.put(zone.getZoneId(), zone);
		
		zone = new ExZone();
		zone.setZoneId(Metropolis);
		zone.setType("city");
		zone.setName("Metropolis");
		zone.setDescription("");
		zone.getTags().add("office Metropolis");
		mockActiveZones.put(zone.getZoneId(), zone);
		
		zone = new ExZone();
		zone.setZoneId(Smallville);
		zone.setType("city");
		zone.setName("Smallville");
		zone.setDescription("");
		zone.getTags().add("office Smallville");
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
