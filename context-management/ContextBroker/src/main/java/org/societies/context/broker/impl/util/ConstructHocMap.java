package org.societies.context.broker.impl.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConstructHocMap {

	ConstructHocMap(){

	}

	/*
	 * [[A, B, C], [aaa1, bbb1, ccc1], [aaa2, bbb2, ccc2], [aaa3, bbb3, ccc3]] -->
	 * {A = {aaa1,aaa2,aaa3}
	 *  B = {bbb1,bbb2,bbb3}
	 * ...
	 */
	public Map<String, List<String>> reconstructMap(List<List<String>> deconsturctedMapList, int max){

		Map<String, List<String>> result = new HashMap<String, List<String>>();

		//add key set
		List<String> keyList = deconsturctedMapList.get(0);
		for(String key : keyList){
			result.put(key, new ArrayList<String>());
		}
		
		// add values
		List<String> values = new ArrayList<String>();
		for(List<String> datalists: deconsturctedMapList){
			System.out.println("datalists "+datalists );
			
		}

		

		return result;
	}


	/*
	 * Return list will contain various number of lists, depending the size of the escorting attributes 
	 */
	public List<List<String>> deconstructMap(Map<String, List<String>> map){

		//List<String> keyList,  List<String> objectList 
		List<List<String>> result = new ArrayList<List<String>>();

		List<String> keyList = new ArrayList<String>(map.keySet());
		result.add(0,keyList);


		// [{a1,b1,c1},{a2,b2,c2},{a3,b3,c3,d4}]
		List<List<String>>  allEscortingValuesList = new ArrayList<List<String>>(map.values());
		System.out.println(" allEscortingValuesList : "+allEscortingValuesList );
		//get max number of escorting attributes 
		int max = 0;
		for(List<String> escortingValues : allEscortingValuesList){
			if(max < escortingValues.size()) max = escortingValues.size(); 	
		}

		// max number of escorting types should be created

		//iterate escorting attributes max time

		//System.out.println(" max : "+max );
		List<String> valuesPerType  = null; // one type list
		for( int i=0 ; i< max ; i++){
			valuesPerType  = new ArrayList<String>();
			//System.out.println(" i : "+i );

			for(List<String> tupleValues : allEscortingValuesList){

				//System.out.println(" i : "+i +" tuple value "+tupleValues);
				String value = "";
				if( tupleValues.size() > i){
					value = tupleValues.get(i);	
					//System.out.println(" value "+value);

				}
				valuesPerType.add(value);
				//System.out.println(" valuesPerType "+valuesPerType);

			}
			result.add(valuesPerType);
		}

		return result;
	}




	/**
	 * @param args
	 */
	public static void main(String[] args) {

		ConstructHocMap cm =  new ConstructHocMap();

		Map<String, List<String>> testMap = new HashMap<String, List<String>>();

		List<String> objectListA = new ArrayList<String>();
		objectListA.add("aaa1");
		objectListA.add("aaa2");
		objectListA.add("aaa3");
		testMap.put("A", objectListA);

		List<String> objectListB = new ArrayList<String>();
		objectListB.add("bbb1");
		objectListB.add("bbb2");
		objectListB.add("bbb3");
		testMap.put("B", objectListB);

		List<String> objectListC = new ArrayList<String>();
		objectListC.add("ccc1");
		objectListC.add("ccc2");
		objectListC.add("ccc3");
		objectListC.add("ccc4");
		testMap.put("C", objectListC);


		System.out.println("initial map : "+testMap );

		List<List<String>> deconstucted = cm.deconstructMap(testMap);
		System.out.println("deconstructed map to list: "+deconstucted );
		
		//Map<String, List<String>> reconstructed = cm.reconstructMap(deconstucted);
		//System.out.println("reconstructed list to map : "+reconstructed );
		

	}

}
