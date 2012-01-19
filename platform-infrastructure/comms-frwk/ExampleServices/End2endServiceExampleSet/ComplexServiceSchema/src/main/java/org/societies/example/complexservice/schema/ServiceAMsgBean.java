package org.societies.example.complexservice.schema;

public class ServiceAMsgBean {

	public enum methodName {doSomething, doSomethingElse};
	private methodName method;
	private MyComplexBean complexBean;

	public methodName getMethod() {
		return this.method;
	}
	
	public void setMethod(methodName method) {
		this.method = method;
	}

	public MyComplexBean getComplexBean() {
		return complexBean;
	}

	public void setComplexBean(MyComplexBean complexBean) {
		this.complexBean = complexBean;
	}
	
}
