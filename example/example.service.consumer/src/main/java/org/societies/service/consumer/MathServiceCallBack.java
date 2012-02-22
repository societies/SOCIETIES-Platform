package org.societies.service.consumer;

import org.societies.service.api.IMathServiceCallBack;

public class MathServiceCallBack implements IMathServiceCallBack {

	public void resultDivision(float r) {
		System.out.println("divison result is " + r);
	}
	
	public void error(String s) {
		System.out.println(s);
	}
}