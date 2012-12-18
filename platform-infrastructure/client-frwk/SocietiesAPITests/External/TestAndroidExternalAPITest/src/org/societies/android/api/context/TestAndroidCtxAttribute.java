package org.societies.android.api.context;

import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.societies.api.schema.context.model.CtxAttributeIdentifierBean;
import org.societies.api.schema.context.model.CtxAttributeBean;

import android.os.Parcel;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;



public class TestAndroidCtxAttribute extends AndroidTestCase{

	private static final String ATTRIBUTE_ID = "scope/ATTRIBUTE/type/6";
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		
		super.tearDown();
	}

	@MediumTest
	public void testParcelable() {

		final CtxAttributeBean attribute = new CtxAttributeBean();	
		assertNotNull(attribute);
		
	    byte[] binVal = new byte[]{3,2,5,4,1};
		attribute.setBinaryValue(binVal);
		double doubleVal = 1.0;
		attribute.setDoubleValue(doubleVal);
		boolean histRec = true;
		attribute.setHistoryRecorded(histRec);
		CtxAttributeIdentifierBean attrId = new CtxAttributeIdentifierBean();
		attrId.setString(ATTRIBUTE_ID);
		attribute.setId(attrId);
		int integerVal = 14;
		attribute.setIntegerValue(integerVal);
		XMLGregorianCalendar testDate = null;
		attribute.setLastModified(testDate);
//		attribute.setQuality()
		attribute.setSourceId(ATTRIBUTE_ID);
		String strVal = "test";
		attribute.setStringValue(strVal);
		attribute.setValueMetric(strVal);
//		attribute.setValueType();
		assertEquals(ATTRIBUTE_ID, attribute.getSourceId());
		
		assertEquals(0, attribute.describeContents());
		
        Parcel parcel = Parcel.obtain();
        attribute.writeToParcel(parcel, 0);
        //done writing, now reset parcel for reading
        parcel.setDataPosition(0);
        //finish round trip
        
        final CtxAttributeBean createFromParcel = CtxAttributeBean.CREATOR.createFromParcel(parcel);
       
        assertNotNull(createFromParcel);
        assertEquals(attribute.getSourceId(), createFromParcel.getSourceId());
	}
}
