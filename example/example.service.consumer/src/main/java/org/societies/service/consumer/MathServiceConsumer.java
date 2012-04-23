package org.societies.service.consumer;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.societies.service.api.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

public class MathServiceConsumer implements IConsumer {

	private int num_a;
	private int num_b;

	private IMathService mathService;
	private IMathServiceCallBack divisionCallBack;

	private int hiddenState;

	public MathServiceConsumer(int a, int b) {
		this.num_a = a;
		this.num_b = b;
		System.out.println("numbers from property file :" + num_a + " " + num_b);
	}

	// Start Bean mandatory methods
	public IMathService getMathService() {
		return mathService;
	}

	public void setMathService(IMathService mathService) {
		this.mathService = mathService;
	}

	// End Bean mandatory methods

	public void setMathServiceCallBack(IMathServiceCallBack mathServiceCallBack) {
		this.divisionCallBack = mathServiceCallBack;
	}

	public int getHiddenState() {
		return hiddenState;
	}

	public void setHiddenState(int hiddenState) {
		this.hiddenState = hiddenState;
	}

	// used to demonstrate how starting an execution of a bundle process
	public void initMethod() throws Exception {
		System.out.println("add result is : "
				+ getMathService().add(num_a, num_b));
		System.out.println("multiply result is : "
				+ getMathService().multiply(num_a, num_b));
		System.out.println("subtract result is : "
				+ getMathService().subtract(num_a, num_b));
	}

	// used to demonstrate how to test a method calling an interface of another
	// bundle
	public int collaborationCall(int num_a, int num_b) {
		return getMathService().add(num_a, num_b);
	}

	// used to demonstrate how the mock can manage the Asynch Interface.
	public boolean collaborationAsynchronousCall(int num_a, int num_b, int result) throws InterruptedException, ExecutionException {
		Future<Integer> res = null;
		res = getMathService().multiply(num_a, num_b);
		if (result == res.get()) {
			return true;
		} else {
			return false;
		}
		// System.out.println("valeur de res  " + res);
		// return true;
	}

	// used to demonstrate how to test a method which uses a callback */
	public void callDivisionWithCallBack(int num_a, int num_b) {
		boolean r = getMathService().divise(num_a, num_b, divisionCallBack);

		if (r) {
			System.out.println("have a look on the callback");
		} else {
			System.out.println("an error occurs in the call");
		}
	}

	// new coded added for stateful test
	public void callStatefulMethod() {
		if (this.hiddenState > 10) {
			this.mathService.add(num_a, num_b);
		} else {
			this.mathService.multiply(num_a, num_b);
		}
	}

	@Override
	public int barycenter(int a, int b, int c){
		// barycenter of a and b is RES with RES = (a * b) / c
		Future<Integer> futureAb = getMathService().multiply(a, b);

		int ab = 0;
		try {
			ab = futureAb.get();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			e1.printStackTrace();
		}

		if (0 != c) 
		{
			int result = ab/c;

			return result;
		}
		return -1;
	}

	@Override
	public void asyncBarycenter(int a, int b, int c, IConsumerCallback consumerCallback) {

		System.out.println("*** MathServiceConsumer asyncBarycenter");

		Calendar calendar = Calendar.getInstance();
		long start = calendar.getTimeInMillis();

		try {
			Thread.sleep(10*1000);
		} catch (InterruptedException e) {
			System.out.println("*** MathServiceConsumer asyncBarycenter InterruptedException: " + e.getMessage());
			e.printStackTrace();
		}

		calendar = Calendar.getInstance();
		long end = calendar.getTimeInMillis();
		System.out.println("*** MathServiceConsumer asyncBarycenter delay: " + ((end-start)/1000));


		// barycenter of a and b is RES with RES = (a * b) / c
		Future<Integer> futureAb = getMathService().multiply(a, b);

		int ab = 0;
		try {
			ab = futureAb.get();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			e1.printStackTrace();
		}

		if (0 != c)
		{
			int result = ab/c;
			consumerCallback.sendResult(result);
		}
		else
		{
			consumerCallback.sendResult(-1);
		}

	}


	@Async
	@Override
	public Future<Integer> futureBarycenter(int a, int b, int c) {

		System.out.println("*** MathServiceConsumer futureBarycenter");

		Calendar calendar = Calendar.getInstance();
		long start = calendar.getTimeInMillis();
		
		try {
			Thread.sleep(10*1000);
		} catch (InterruptedException e) {
			System.out.println("*** MathServiceConsumer futureBarycenter InterruptedException: " + e.getMessage());
			e.printStackTrace();
		}

		calendar = Calendar.getInstance();
		long end = calendar.getTimeInMillis();
		System.out.println("*** MathServiceConsumer futureBarycenter delay: " + ((end-start)/1000));

		Future<Integer> futureAb = getMathService().multiply(a, b);

		int ab = 0;
		try {
			ab = futureAb.get();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			e1.printStackTrace();
		}

		if (0 != c)
		{
			int result = ab/c;
			return new AsyncResult<Integer>(result);
		}
		return new AsyncResult<Integer>(0);
	}
}
