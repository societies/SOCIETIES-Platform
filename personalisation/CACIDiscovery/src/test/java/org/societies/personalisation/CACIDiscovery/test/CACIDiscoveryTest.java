package org.societies.personalisation.CACIDiscovery.test;


import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.societies.personalisation.CACIDiscovery.impl.CACIDiscovery;
import org.societies.personalisation.CACIDiscovery.impl.UIModelSimilarityEval;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CAUI.api.model.UserIntentModelData;

public class CACIDiscoveryTest {

	CACIDiscovery discovery = null;
	private static IUserIntentAction mockUserActionA = mock(IUserIntentAction.class);
	private static IUserIntentAction mockUserActionB = mock(IUserIntentAction.class);
	private static IUserIntentAction mockUserActionC = mock(IUserIntentAction.class);
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

		discovery = new CACIDiscovery();
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testGenerateVariousUserModels() {
	}

	@Test
	public void mergeTargetMaps(){
	
		HashMap<IUserIntentAction,Double> mapAnew = new HashMap<IUserIntentAction,Double>(); 
		mapAnew.put(mockUserActionA,0.5);
		mapAnew.put(mockUserActionB,0.5);
		
		HashMap<IUserIntentAction,Double> mapBexisting = new HashMap<IUserIntentAction,Double>();
		mapBexisting.put(mockUserActionC,1.0);
		
		System.out.println(" out A "+ mapAnew);
		System.out.println(" out B "+ mapBexisting);
		
		HashMap<IUserIntentAction,Double> merged = discovery.mergeTargetMaps(mapAnew, mapBexisting);
		
		
		System.out.println("merged:"+ merged);
		
		
	}

		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
}