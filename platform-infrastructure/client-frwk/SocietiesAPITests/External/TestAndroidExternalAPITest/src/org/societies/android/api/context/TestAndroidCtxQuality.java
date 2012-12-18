package org.societies.android.api.context;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.societies.api.schema.context.model.CtxQualityBean;
import org.societies.api.schema.context.model.CtxOriginTypeBean;

import android.os.Parcel;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;



public class TestAndroidCtxQuality extends AndroidTestCase{

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		
		super.tearDown();
	}

	@MediumTest
	public void testParcelable() throws DatatypeConfigurationException {

		final CtxQualityBean quality = new CtxQualityBean();
		assertNotNull(quality);

/*		GregorianCalendar gcal = new GregorianCalendar();
	      XMLGregorianCalendar xgcal = DatatypeFactory.newInstance()
	            .newXMLGregorianCalendar(gcal);
	*/      
	      String text = "2011-08-10T00:00:00-06:00";  
	      XMLGregorianCalendar xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(text);  
	      
		quality.setLastUpdated(xgcal);
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
        assertEquals(quality.getLastUpdated(), createFromParcel.getLastUpdated());
        assertEquals(quality.getOriginType(), createFromParcel.getOriginType());
        assertEquals(quality.getPrecision(), createFromParcel.getPrecision());
        assertEquals(quality.getUpdateFrequency(), createFromParcel.getUpdateFrequency());
	}
}
