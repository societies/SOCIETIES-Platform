package org.societies.service.provider.impl;

import org.societies.service.api.IMathService;

public class MathService implements IMathService {

	private int num_a;
	private int num_b;
	
	// to show how to input constructor para through bean
	public MathService(int arg1, int arg2, String msg1){
		this.num_a=arg1;
		this.num_b=arg2;		
		System.out.println("The numbers supplied as constructor arg are :" + num_a+", "+num_b);
		System.out.println("The String msg supplied : " + msg1);	
	}
	
	public int add(int a, int b) {
		// TODO Auto-generated method stub
		return a+b;
	}

	public int subtract(int a, int b) {
		// TODO Auto-generated method stub
		return a-b;
	}

	public int multiply(int a, int b) {
		// TODO Auto-generated method stub
		return a*b;
	}

}
