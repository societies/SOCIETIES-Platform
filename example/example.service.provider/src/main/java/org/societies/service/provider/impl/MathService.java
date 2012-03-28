package org.societies.service.provider.impl;

import java.util.concurrent.Future;

import org.societies.service.api.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

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
		return a+b;
	}

	public int subtract(int a, int b) {
		return a-b;
	}

	@Async
	public Future<Integer> multiply(int a, int b) {
		return new AsyncResult<Integer>(a*b);
	}

	public boolean divise(int a, int b, IMathServiceCallBack callback) {
		if (b!=0) {
			callback.resultDivision(a/b);
			return true;
		} else {
			callback.error("division with 0 is not allowed in mathematics");
			return false;
		}
	}
}
