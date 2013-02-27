package org.societies.android.api.context;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.societies.api.schema.context.model.CtxEntityBean;
import org.societies.api.schema.context.model.CtxEntityIdentifierBean;

import android.os.Parcel;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;



public class TestAndroidCtxEntity extends AndroidTestCase{
	private final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ssZ"; 
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		
		super.tearDown();
	}

	@MediumTest
	public void testParcelable() {
		CtxEntityIdentifierBean entityId = new CtxEntityIdentifierBean();	
		entityId.setString("ownerId/ENTITY/type/15");
		
		CtxEntityBean entity = new CtxEntityBean();
		try {
			String text = "2011-08-10 00:00:00-06:00";
		    DateFormat df = new SimpleDateFormat(DATE_TIME_FORMAT);
		    Date testDate = df.parse(text);
		    entity.setLastModified(testDate);
		} catch (ParseException pEx) {
		   	fail("Exception parsing LastUpdated Date: " + pEx.getStackTrace());
		}
		entity.setId(entityId);
		
		assertNotNull(entityId);
		assertNotNull(entity);
		assertEquals(0, entityId.describeContents());
		assertEquals(0, entity.describeContents());
		
        Parcel parcel = Parcel.obtain();
        entity.writeToParcel(parcel, 0);

        //done writing, now reset parcel for reading
        parcel.setDataPosition(0);
        CtxEntityBean createFromParcelEntity = CtxEntityBean.CREATOR.createFromParcel(parcel);
       
        assertNotNull(createFromParcelEntity);
        assertEquals(entity.getId().getString(), createFromParcelEntity.getId().getString());
        assertEquals(entity.getLastModified() , createFromParcelEntity.getLastModified());
	}
}
