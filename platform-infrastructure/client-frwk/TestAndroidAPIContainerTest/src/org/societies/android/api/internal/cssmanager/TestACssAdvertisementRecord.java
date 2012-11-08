package org.societies.android.api.internal.cssmanager;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;
import org.societies.android.api.css.directory.ACssAdvertisementRecord;

import com.google.gson.Gson;

import android.os.Parcel;
import android.util.Log;

import junit.framework.TestCase;

public class TestACssAdvertisementRecord extends TestCase {

	private final static String ADVERT_ID_1 = "advertid1";
	private final static String ADVERT_NAME_1 = "advert_name1";
	private final static String ADVERT_URI_1 = "advert_URI1";
	
	private final static String ADVERT_ID_2 = "advertid2";
	private final static String ADVERT_NAME_2 = "advert_name2";
	private final static String ADVERT_URI_2 = "advert_URI2";
	
	protected void setUp() throws Exception {
		super.setUp();
		//after
	}

	protected void tearDown() throws Exception {
		//before
		super.tearDown();
	}

	public void testParcelable() throws Exception {
		ACssAdvertisementRecord aCssAdvert = new ACssAdvertisementRecord();
		aCssAdvert.setId(ADVERT_ID_1);
		aCssAdvert.setName(ADVERT_NAME_1);
		aCssAdvert.setUri(ADVERT_URI_1);
		
		assertEquals(0, aCssAdvert.describeContents());
		
        Parcel parcel = Parcel.obtain();
        aCssAdvert.writeToParcel(parcel, 0);
        //done writing, now reset parcel for reading
        parcel.setDataPosition(0);
        //finish round trip
        ACssAdvertisementRecord createFromParcel = ACssAdvertisementRecord.CREATOR.createFromParcel(parcel);
       
        assertEquals(aCssAdvert.getName(), createFromParcel.getName());
        assertEquals(aCssAdvert.getId(), createFromParcel.getId());
        assertEquals(aCssAdvert.getUri(), createFromParcel.getUri());
	}

	public void testJSONConversion() throws Exception {
		ACssAdvertisementRecord aCssAdvert_1 = new ACssAdvertisementRecord();
		aCssAdvert_1.setId(ADVERT_ID_1);
		aCssAdvert_1.setName(ADVERT_NAME_1);
		aCssAdvert_1.setUri(ADVERT_URI_1);
		
		ACssAdvertisementRecord aCssAdvert_2 = new ACssAdvertisementRecord();
		aCssAdvert_2.setId(ADVERT_ID_2);
		aCssAdvert_2.setName(ADVERT_NAME_2);
		aCssAdvert_2.setUri(ADVERT_URI_2);
		
		ACssAdvertisementRecord array [] = {aCssAdvert_1, aCssAdvert_2};
		
		assertEquals(2, array.length);
		
        JSONArray jArray = null;
		Gson gson = new Gson();
		try {
			jArray =  new JSONArray (new JSONTokener(gson.toJson(array)));
			Log.d("test", gson.toJson(array));
			assertEquals(2, jArray.length());
			
			assertEquals(ADVERT_ID_1, jArray.getJSONObject(0).get("id"));
			assertEquals(ADVERT_NAME_1, jArray.getJSONObject(0).get("name"));
			assertEquals(ADVERT_URI_1, jArray.getJSONObject(0).get("uri"));

			assertEquals(ADVERT_ID_2, jArray.getJSONObject(1).get("id"));
			assertEquals(ADVERT_NAME_2, jArray.getJSONObject(1).get("name"));
			assertEquals(ADVERT_URI_2, jArray.getJSONObject(1).get("uri"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		

	}
}
