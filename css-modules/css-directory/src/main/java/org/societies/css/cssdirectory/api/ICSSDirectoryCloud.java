package org.societies.css.cssdirectory.api;

import java.util.Collection;

public interface ICSSDirectoryCloud {
	
	void addCSS(Object css);
	
	void deleteCSS(Object css);
	
	void updateCSS(Object css, Object update);
	
	Collection<Object> findForAllCSS();
	
	Collection<Object> findForAllCSS(Object cisgroup);
	
	Object findCSS(Object searchinfo);

}
