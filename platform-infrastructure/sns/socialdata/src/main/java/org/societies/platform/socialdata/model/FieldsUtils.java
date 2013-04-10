package org.societies.platform.socialdata.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.shindig.social.core.model.ListFieldImpl;
import org.apache.shindig.social.opensocial.model.ListField;


public class FieldsUtils {

    
    public static List <ListField> genList (String value[], String[] type, boolean[] primary) {

	
	List<ListField> list = new ArrayList<ListField>();
	
	for(int i =0; i<value.length; i++) {
	    list.add(genListField(value[i], type[i], primary[i]));
	}
	return list;
	
    }
    
    
    public static ListField genListField(String value, String type, boolean primary){
	ListField lf = new ListFieldImpl();
	lf.setPrimary(primary);
	lf.setType(type);
	lf.setValue(value);		
	return lf;
    }


    public static List<ListField> genList(String mail, String type, boolean primary) {
	return genList(new String[]{mail}, new String[]{type}, new boolean[]{primary});
	
    }
}
