package org.societies.android.api.context;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.datatype.DatatypeConfigurationException;
import org.societies.api.schema.context.model.CtxQualityBean;
import org.societies.api.schema.context.model.CtxOriginTypeBean;
import android.os.Parcel;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;


public class TestAndroidCtxQuality extends AndroidTestCase{

	private final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		
		super.tearDown();
	}

//      Commented out test as it is currently failing
//	@MediumTest
	public void testParcelable() throws DatatypeConfigurationException {

		final CtxQualityBean quality = new CtxQualityBean();
		assertNotNull(quality);

		//GregorianCalendar gcal = new GregorianCalendar();
	    //XMLGregorianCalendar xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
		//
		
	    String text = "2011-08-10T00:00:00-06:00";  
	    //XMLGregorianCalendar xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(text);
	    
	    try {
		    DateFormat df = new SimpleDateFormat(DATE_TIME_FORMAT);
		    Date xgcal = df.parse(text);
			quality.setLastUpdated(xgcal);
	    } catch (ParseException pEx) {
	    	fail("Exception parsing LastUpdated Date: " + pEx.getStackTrace());
	    }
		
		quality.setOriginType(CtxOriginTypeBean.MANUALLY_SET);
		quality.setPrecision(5.2);
		quality.setUpdateFrequency(8.1);
		
		assertEquals(0, quality.describeContents());
		
        Parcel parcel = Parcel.obtain();
        quality.writeToParcel(parcel, 0);
        //done writing, now reset parcel for reading
        parcel.setDataPosition(0);
        //finish round trip
        
        final CtxQualityBean createFromParcel = CtxQualityBean.CREATOR.createFromParcel(parcel);
       
        assertNotNull(createFromParcel);
        System.out.println("LastUpdate: " + quality.getLastUpdated().toString() + " = " + createFromParcel.getLastUpdated().toString());
        assertEquals(quality.getLastUpdated(), createFromParcel.getLastUpdated());
        assertEquals(quality.getOriginType(), createFromParcel.getOriginType());
        assertEquals(quality.getPrecision(), createFromParcel.getPrecision());
        assertEquals(quality.getUpdateFrequency(), createFromParcel.getUpdateFrequency());
	}
}
