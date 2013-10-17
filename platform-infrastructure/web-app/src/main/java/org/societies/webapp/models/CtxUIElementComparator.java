package org.societies.webapp.models;

import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.schema.context.model.CtxUIElement;

/**
 * @author Paolo
 *
 */
public class CtxUIElementComparator implements Comparator<CtxUIElement> {
	
	private static final Logger log = LoggerFactory.getLogger(CtxUIElementComparator.class);

	@Override
	public int compare(CtxUIElement o1, CtxUIElement o2) {
		
		if(o1 != null && o2 != null)
		{
			try
			{
				long id1 = Long.parseLong(o1.getDiplayId());
				long id2 = Long.parseLong(o2.getDiplayId());
				int val = id1 >= id2 ?  1 : -1;
				return val;
			}
			catch(NumberFormatException e)
			{
				log.error("Error parsing "+o1.getDiplayId()+"; "+o2.getDiplayId());
			}
		}
		return -1;
	}

}
