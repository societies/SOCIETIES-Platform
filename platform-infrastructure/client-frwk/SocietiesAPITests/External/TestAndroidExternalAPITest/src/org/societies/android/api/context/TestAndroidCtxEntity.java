package org.societies.android.api.context;

import org.societies.api.schema.context.model.CtxEntityBean;
import org.societies.api.schema.context.model.CtxEntityIdentifierBean;

import android.os.Parcel;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;



public class TestAndroidCtxEntity extends AndroidTestCase{

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
		
//		ACtxEntity entity = new ACtxEntity(entityId);
		CtxEntityBean entity = new CtxEntityBean();
		entity.setId(entityId);
		
		assertNotNull(entityId);
		assertNotNull(entity);
		
		assertEquals(0, entityId.describeContents());
		assertEquals(0, entity.describeContents());
		
        Parcel parcel = Parcel.obtain();
//        entityId.writeToParcel(parcel, 0);
        entity.writeToParcel(parcel, 0);
        //done writing, now reset parcel for reading
        parcel.setDataPosition(0);
        //finish round trip
//        CtxEntityIdentifierBean createFromParcel = CtxEntityIdentifierBean.CREATOR.createFromParcel(parcel);
        CtxEntityBean createFromParcelEntity = CtxEntityBean.CREATOR.createFromParcel(parcel);
       
        assertNotNull(createFromParcelEntity);
//        assertEquals(entityId.getString(), createFromParcel.getString());
        assertEquals(entity.getId(), createFromParcelEntity.getId());
	}
}
