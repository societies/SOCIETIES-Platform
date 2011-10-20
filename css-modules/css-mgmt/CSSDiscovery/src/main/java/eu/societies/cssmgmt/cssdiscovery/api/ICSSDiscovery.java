package eu.societies.cssmgmt.cssdiscovery.api;

import java.util.Collection;
import java.util.List;

public interface ICSSDiscovery {
	
	public boolean setCSSDiscoveryDomain(Object[] CSSDomain);
	
	public Collection<Object> searchAllCSS();
	
	public Collection<Object> searchAllCSS(Object[] cisGroup);
	
	public boolean registerForJoinCSSAlert(Object handler, Object[] cisGroupFilter);
	
	public boolean registerForLeftCSSAlert(Object handler, Object[] cisGroupFilter);
	
	public boolean unregisterJoinCSSAlert(Object handler, Object[] cisGroupFilter);
	
	public boolean unregisterLeftCSSAlert(Object handler, Object[] cisGroupFilter);
	
	public Object findCSS(Object CSSID);
	
	public boolean isCSSExists();
	
	public List<Object> getCISMembership(Object CSSID);  

}
