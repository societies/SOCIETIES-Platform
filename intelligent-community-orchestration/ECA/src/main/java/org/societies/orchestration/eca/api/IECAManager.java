package org.societies.orchestration.eca.api;

import java.util.List;

import org.societies.api.context.model.CtxAttribute;

public interface IECAManager {
	
	public void checkLocalContext();
	
	public void getRelatedCSS();
	
	public void addUsersContext(List<CtxAttribute> attributes);
	
	public void setLocalContext(CtxAttribute ctxAtt);
	
}
