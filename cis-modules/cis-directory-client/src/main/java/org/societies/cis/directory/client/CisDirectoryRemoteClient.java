package org.societies.cis.directory.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.api.cis.directory.ICisDirectoryCallback;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;

public class CisDirectoryRemoteClient implements ICisDirectoryCallback{
	
	List<CisAdvertisementRecord> resultList;

	private final long TIMEOUT = 5;

	private BlockingQueue<List<CisAdvertisementRecord>> returnList;		
	
	private static Logger logger = LoggerFactory.getLogger(CisDirectoryRemoteClient.class);
	
	public CisDirectoryRemoteClient(){
		
		logger.info("CIS DIRECTORY CisDirectoryRemoteClient called ###########: ");
		//logger.info("CIS DIRECTORY CALLBACK cisDirectoryClient = @@@@@@@@@@: "+cisDirectoryClient);
		
		returnList = new ArrayBlockingQueue<List<CisAdvertisementRecord>>(1);
	}
	
	public void getResult(List<CisAdvertisementRecord> records) {
		resultList = new ArrayList<CisAdvertisementRecord>();
		
		for (int i = 0; i < records.size(); i++)
		{
			resultList.add(records.get(i));
		}
		
		setResultList(resultList);

	}


	/**
	 * @return the resultList
	 */
	public List<CisAdvertisementRecord> getResultList() {
		try {
			return returnList.poll(TIMEOUT, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * @param resultList the resultList to set
	 */
	public void setResultList(List<CisAdvertisementRecord> resultList) {
		try {
			returnList.put(resultList);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
