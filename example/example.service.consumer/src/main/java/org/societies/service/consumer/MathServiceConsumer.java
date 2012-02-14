package org.societies.service.consumer;

import org.societies.service.api.IMathService;

public class MathServiceConsumer{

	private int num_a;
	private int num_b;
	
	private IMathService mathService;
	
	
	public MathServiceConsumer(int a, int b){
		this.num_a=a;
		this.num_b=b;
		System.out.println("numbers from property file :" +num_a+" "+num_b);
	}
	
	// Start Bean mandatory methods 
	public IMathService getMathService() {
		return mathService;
	}
	
	public void setMathService(IMathService mathService) {
		this.mathService = mathService;
	}
	//End Bean mandatory methods
		
	// used to demonstrate how starting an execution of a bundle process
	public void initMethod() throws Exception {		
	System.out.println("add result is : "+ 	getMathService().add(num_a, num_b));
	System.out.println("multiply result is : "+ 	getMathService().multiply(num_a, num_b));
	System.out.println("subtract result is : "+ 	getMathService().subtract(num_a, num_b));
	}
	
	// used to demonstrate how to test a method calling an interface of another bundle
	public void collaborationCall(int num_a, int num_b) {
		System.out.println("add "+ num_a + "+" +num_b);
		
		System.out.println("add result is : "+ 	getMathService().add(num_a, num_b));
		/*
		System.out.println("multiply result is : "+ 	getMathService().multiply(num_a, num_b));
		System.out.println("subtract result is : "+ 	getMathService().subtract(num_a, num_b));
		*/
	}
	
	//
}
