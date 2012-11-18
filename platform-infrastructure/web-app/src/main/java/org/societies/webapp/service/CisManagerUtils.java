package org.societies.webapp.service;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICis;


public class CisManagerUtils {
	
	private static Logger LOG = LoggerFactory.getLogger(CisManagerUtils.class);

	public static boolean isJidOnCommunityCollection(Collection<ICis> listOfIcis, String jid) {
		LOG.debug("cisManagerUtilsCalled");
		for (ICis temp : listOfIcis) {
			if(temp.getCisId().equalsIgnoreCase(jid))
				return true;
		}
		return false;
	  }

}
