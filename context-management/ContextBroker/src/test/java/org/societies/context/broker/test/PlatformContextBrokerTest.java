package org.societies.context.broker.test;

import org.societies.api.context.broker.IUserCtxBrokerCallback;
import org.societies.context.broker.impl.PlatformContextBroker;
import org.societies.api.mock.EntityIdentifier;

public class PlatformContextBrokerTest {

	private static PlatformContextBroker platformCtxBroker = null;
	//private static IUserCtxDBMgr userDB;
	
	//Constructor
	PlatformContextBrokerTest(){
		
		//here the user DB of the broker should be set using the setter: setUserDB(IUserCtxDBMgr userDB)
		//setUserDB(userDB);
		System.out.println("-- start of testing --");
		testCreateCtxEntity();
		testCreateCtxAssociation();
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new PlatformContextBrokerTest();
	}

	
	private void testCreateCtxEntity(){

		System.out.println("---- test CreateCtxEntity external");
		IUserCtxBrokerCallback exCallback = null;
		EntityIdentifier identifier = null;
		platformCtxBroker.createEntity(identifier, "person", exCallback);
		
		System.out.println("---- test CreateCtxEntity internal");
		org.societies.api.internal.context.broker.IUserCtxBrokerCallback inCallback = null;
		platformCtxBroker.createEntity("person", inCallback);
		
	}
	
	private void testCreateCtxAssociation(){
		
		System.out.println("---- test testCreateCtxAssociation external");
		IUserCtxBrokerCallback exCallback = null;
		EntityIdentifier identifier = null;
		platformCtxBroker.createAssociation(identifier, "person", exCallback);
		
		System.out.println("---- test testCreateCtxAssociation internal");
		org.societies.api.internal.context.broker.IUserCtxBrokerCallback inCallback = null;
		platformCtxBroker.createAssociation("person", inCallback);
		
	}

}
