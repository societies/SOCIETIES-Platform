package org.societies.service.consumer;

import org.societies.service.api.*;

public class MathServiceConsumer{

	private int num_a;
	private int num_b;
	
	private IMathService mathService;
	private IMathServiceCallBack divisionCallBack;
	
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
	
	public void setMathServiceCallBack(IMathServiceCallBack mathServiceCallBack) {
		this.divisionCallBack = mathServiceCallBack;
	}
		
	// used to demonstrate how starting an execution of a bundle process
	public void initMethod() throws Exception {		
	System.out.println("add result is : "+ 	getMathService().add(num_a, num_b));
	System.out.println("multiply result is : "+ 	getMathService().multiply(num_a, num_b));
	System.out.println("subtract result is : "+ 	getMathService().subtract(num_a, num_b));
	}
	
	// used to demonstrate how to test a method calling an interface of another bundle
	public void collaborationCall(int num_a, int num_b) {
		System.out.println("add result is : "+ 	getMathService().add(num_a, num_b));
	}
	
	//use to demonstrate how to test a method which uses a callback */
		public void callDivisionWithCallBack(int num_a, int num_b) {
			System.out.println("step1");
			boolean r = getMathService().divise(num_a, num_b, divisionCallBack);
			
			System.out.println("step2");
			if (r) {
				System.out.println("have a look on the callback");
			} else {
				System.out.println("an error occurs in the call");
			}
		}

}
