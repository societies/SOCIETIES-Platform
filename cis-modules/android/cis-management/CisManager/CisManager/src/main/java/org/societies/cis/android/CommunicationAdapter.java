/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.cis.android;


/**
 * In charge of setting up communication with the CisManager cloud.
 * 
 * TODO: This should be generalized later for different types of connections.
 * 
 * @author Babak.Farshchian@sintef.no
 *
 */
class CommunicationAdapter {

//    private boolean online = false;
//	
//    // Get an identifier for log messages:
//    private static final String LOG_TAG = CommunicationAdapter.class.getName();
//    //CIS manager relevant messages:
//    private static final List<String> ELEMENT_NAMES = Arrays.asList("communities", "subscribedTo",
//	    	"community-manager", "community", "create", "configure", "search-cis", "list", "delete");
//    //Specify the XCCom name spaces you understand:
//    private static final List<String> NAME_SPACES = Arrays.asList(
//	    		"http://societies.org/api/schema/cis/manager",
//	    		"http://societies.org/api/schema/cis/community");
//    // TODO: What does this mean?
//    private static final List<String> PACKAGES = Arrays.asList(
//			"org.societies.api.schema.cis.manager",
//			"org.societies.api.schema.cis.community");
//    //TODO: Address of the cloud node? I thought this was set in the comms manager?
//    private static final String DESTINATION = "xcmanager.jabber.sintef9013.com";
//
//    private final IIdentity toXCManager;
//    private final ICommCallback callback;
//    private ClientCommunicationMgr ccm;
//    private Context context;
//
//    public CommunicationAdapter(Context _context){
//	context = _context;
//	//Create a callback class that will handle incoming messages:
//	callback = new CommunicationCallback(context, NAME_SPACES,PACKAGES);
//	//Get a JID-compatible identity for the XCManager in the cloud node:
//	// TODO: Why can't I use DESTINATIO directly?
//    	try {
//    	    toXCManager = IdentityManagerImpl.staticfromJid(DESTINATION);
//    	    } catch (InvalidFormatException e) {
//    		Log.e(LOG_TAG, e.getMessage(), e);
//		throw new RuntimeException(e);
//		}     
//    }
//	    
//    /**
//     * TODO: Need to implement this. It is going to be either done through a presence
//     * value or through rela XMPP login.
//     * @return
//     */
//    boolean isOnline(){
//	return online;
//    }
//    /**
//     * When CommunicationAdapter is created it does not go online automatically
//     * You have to call this method explicitly
//     * 
//     * @return:
//     * 0 means not even tried
//     * 1 means I am online
//     * 2 means no network available on device
//     * 3 means service not responding
//     * 4 means authentication error
//     */
//    int goOnline(){
//	//TODO: log in to network.
//	
//	return 0;
//    }
//    
//    /**
//     * @return
//     */
//    int goOffline(){
//	online = false;
//	// TODO: clean up network
//	return 0;
//    }
//    /**
//     * Starts an asynch task to go out in the network and create a group
//     * in the CisManager cloud.
//     * 
//     * TODO This method should return possible error message code
//     * @param _record
//     */
//    public void createGroup(ICisRecord _record){
//	CreateGroupTask task = new CreateGroupTask(context);
//	task.execute(_record);
//	
//    }
//    
//    //public ICisRecord getGroupInfo(String _name){
//	//GetGroupInfoTask task = new GetGroupInfoTask(context);
//	//task.execute(_name);
//    //}
//    /**
//     * Parameters to generic AsynchTask:
//     * 1- ICisRecord (Params): This is the group to be created. Param to execute method.
//     * 2- Integer (Progress): The type of progress units used to check task progress.
//     * 3- Integer (Result): The type of the result returned by the task i.e. by doInBackground.
//     * 
//     * @author Babak.Farshchian@sintef.no
//     *
//     */
//    private class CreateGroupTask extends AsyncTask<ICisRecord, Integer, Integer> {
//
//    	private Context context;
//    	private ICisRecord group;
//    	
//    	public CreateGroupTask(Context _context) {
//    		context = _context;
//    	}
//
//    	protected Integer doInBackground(ICisRecord... args) {
//    		ccm = new ClientCommunicationMgr(context);
//    		//We create only one group:
//    		group = args[0];
//    		//Create bean to send over:
//    		Create messageBean = new Create();
//    		//Populate the data from provided CisRecord:
//    		messageBean.setCommunityName(group.getName());
//    		messageBean.setOwnerJid(group.getOwnerId());
//    		
//    		Stanza stanza = new Stanza(toXCManager);
//    		//Try to send the message:
//    		try {
//    			ccm.register(ELEMENT_NAMES, callback);
//    			ccm.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
//    			Log.d(LOG_TAG, "Send stanza");
//    			} catch (Exception e) {
//    			    Log.e(this.getClass().getName(), e.getMessage());
//    			    }
//            return null;
//    	}
//    }
// 
//    /**
//     * Parameters to generic AsynchTask:
//     * 1- String (Params): This is the ID to look for.
//     * 2- Integer (Progress): The type of progress units used to check task progress.
//     * 3- Integer (Result): The type of the result returned by the task i.e. by doInBackground.
//     * 
//     * @author Babak.Farshchian@sintef.no
//     *
//     */
//    private class GetGroupInfoTask extends AsyncTask<String, Integer, ICisRecord> {
//
//    	private Context context;
//    	private ICisRecord group;
//    	
//    	public GetGroupInfoTask(Context _context) {
//    		context = _context;
//    	}
//
//    	protected ICisRecord doInBackground(String... args) {
//    		ccm = new ClientCommunicationMgr(context);
//    		//We create only one group:
//    		String groupNames = args[0];
//    		//Create bean to send over:
//    		Create messageBean = new Create();
//    		//Populate the data from provided CisRecord:
//    		messageBean.setCommunityName(group.getName());
//    		messageBean.setOwnerJid(group.getOwnerId());
//    		
//    		Stanza stanza = new Stanza(toXCManager);
//    		//Try to send the message:
//    		try {
//    			ccm.register(ELEMENT_NAMES, callback);
//    			ccm.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
//    			Log.d(LOG_TAG, "Send stanza");
//    			} catch (Exception e) {
//    			    Log.e(this.getClass().getName(), e.getMessage());
//    			    }
//            return null;
//    	}
//    }
}
