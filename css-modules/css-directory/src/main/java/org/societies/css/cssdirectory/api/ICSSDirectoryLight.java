package org.societies.css.cssdirectory.api;

import java.util.Collection;

public interface ICSSDirectoryLight {
	
	void addCSS(Object css);
	
	void deleteCIS(Object css);
	
	void updateCIS(Object css, Object update);
	
	Collection<Object> findForAllCSS();
	
	Collection<Object> findForAllCSS(Object cisgroup);
	
	Object findCSS(Object searchinfo);


}
