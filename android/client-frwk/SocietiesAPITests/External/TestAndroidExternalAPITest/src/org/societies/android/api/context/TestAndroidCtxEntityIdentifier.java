package org.societies.android.api.context;

import org.societies.api.schema.context.model.CtxEntityIdentifierBean;
import org.societies.api.schema.context.model.CtxIdentifierBean;

import android.os.Parcel;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;



public class TestAndroidCtxEntityIdentifier extends AndroidTestCase{

	private static final String ENTITY_ID = "ownerId/ENTITY/type/15";
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		
		super.tearDown();
	}

	@MediumTest
	public void testParcelable() {

		CtxIdentifierBean entityId;
//		final CtxEntityIdentifierBean entityId = new CtxEntityIdentifierBean();	
		entityId = new CtxEntityIdentifierBean();
		assertNotNull(entityId);
		
		entityId.setString(ENTITY_ID);
		assertEquals(ENTITY_ID, entityId.getString());
		
		assertEquals(0, entityId.describeContents());
		
        Parcel parcel = Parcel.obtain();
        entityId.writeToParcel(parcel, 0);
        //done writing, now reset parcel for reading
        parcel.setDataPosition(0);
        //finish round trip
        
        final CtxEntityIdentifierBean createFromParcel = CtxEntityIdentifierBean.CREATOR.createFromParcel(parcel);
       
        assertNotNull(createFromParcel);
        assertEquals(entityId.getString(), createFromParcel.getString());
	}
}
