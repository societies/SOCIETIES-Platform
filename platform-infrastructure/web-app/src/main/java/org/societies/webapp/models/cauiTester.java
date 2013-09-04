package org.societies.webapp.models;

import java.util.List;

import org.societies.webapp.controller.CAUIController;

public class cauiTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new cauiTester();

	}

	cauiTester(){
		runTester();
	}


	public void runTester(){
		CAUIController cc = new CAUIController();
		cc.init();
		System.out.println(" tester: " +cc.getUserActionsList());
		List<CAUIAction> ls = cc.getUserActionsList(); 
		
		for( CAUIAction act : ls){
			System.out.println("a:" +act.getSourceAction());
			System.out.println("b:" +act.getTargetAction());
		}

	}

}
