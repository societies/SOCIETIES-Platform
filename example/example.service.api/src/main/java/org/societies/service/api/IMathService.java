package org.societies.service.api;

import java.util.concurrent.Future;

public interface IMathService {
	
	public int add(int a, int b);
	
	public int subtract(int a, int b);
	
	public  Future<Integer> multiply(int a, int b);
	
	public boolean divise(int a, int b, IMathServiceCallBack callback);

}
