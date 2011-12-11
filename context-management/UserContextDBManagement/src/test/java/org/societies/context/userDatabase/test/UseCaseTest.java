package org.societies.context.userDatabase.test;



import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.internal.context.user.db.IUserCtxDBMgrCallback;
import org.societies.context.userDatabase.impl.UserContextDBManagement;

public class UseCaseTest {

	static UserContextDBManagement userCDB ;
	static CtxAttribute attribute;
	
	
	UseCaseTest(){
		userCDB = new UserContextDBManagement();
		System.out.println("start testing");
		testCreateEntitySynch();	
		//testCreateIndividualCtxEntity();
		testCreateAttribute();
		testRetrieveAttribute();
		testUpdateAttributeValue();
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		UseCaseTest ucTests = new UseCaseTest(); 
	}



	
/*
	private void testCreateIndividualCtxEntity(){
		
		System.out.println("---- testCreateIndividualCtxEntity");
		IUserCtxDBMgrCallback callback = null;
		userCDB.createIndividualCtxEntity("person",null);
		
		//add more when callback is ready
	}
*/

	private void testCreateEntitySynch(){

		System.out.println("---- testCreateEntitySynch");
	
		CtxEntity ctxEnt1 = userCDB.createEntitySynch("sensor", null);
		CtxEntity ctxEnt2 = userCDB.createEntitySynch("sensor", null);

		System.out.println("Created Sensor Entity 1 "+ ctxEnt1.getId());
		System.out.println("Created Sensor Entity 2 "+ ctxEnt2.getId());

		CtxEntity ctxEntRetrieved1 = (CtxEntity) userCDB.retrieveSynch(ctxEnt1.getId());
		System.out.println("Retieve entity from repository");
		if (ctxEntRetrieved1.getId().equals(ctxEntRetrieved1.getId())) System.out.println("Retrieved Sensor Entities are equal");
	
	}


	private void testCreateAttribute(){
		
		System.out.println("---- testCreateAttribute");
		CtxEntity ctxEnt3 = userCDB.createEntitySynch("sensor", null);
		attribute = userCDB.createAttributeSynch(ctxEnt3.getId(), "Temperature");
		attribute.setIntegerValue(5);

		System.out.println("attribute id: "+attribute.getId() +" type:"+attribute.getType()+" value:"+attribute.getIntegerValue());	
	}
	
	
	private void testRetrieveAttribute(){
		System.out.println("---- testRetrieveAttribute");
		CtxAttribute ctxAttrRetrieved = (CtxAttribute) userCDB.retrieveSynch(attribute.getId());
		System.out.println("ctxAttrRetrieved id: "+ctxAttrRetrieved.getId() +" type:"+ctxAttrRetrieved.getType()+" ctxAttrRetrieved:"+attribute.getIntegerValue());	
		if (attribute.getId().equals(ctxAttrRetrieved.getId())) System.out.println("Retrieved attributes are equal");

	}
	
	private void testUpdateAttributeValue(){
		System.out.println("---- testUpdateAttributeValue");
		CtxAttribute ctxAttrRetrieved = (CtxAttribute) userCDB.retrieveSynch(attribute.getId());
		System.out.println("ctxAttrRetrieved id: "+ctxAttrRetrieved.getId() +" type:"+ctxAttrRetrieved.getType()+" ctxAttrRetrieved:"+ctxAttrRetrieved.getIntegerValue());	
		ctxAttrRetrieved.setIntegerValue(10);
		userCDB.update(ctxAttrRetrieved,null);
		
		CtxAttribute ctxAttrUpdated = (CtxAttribute) userCDB.retrieveSynch(attribute.getId());
		System.out.println("ctxAttrUpdated id: "+ctxAttrUpdated.getId() +" type:"+ctxAttrUpdated.getType()+" ctxAttrUpdated:"+ctxAttrUpdated.getIntegerValue());	
	}
		
}