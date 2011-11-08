public interface ICommunityLifecycleManagement {
	
	public ICommunityLifecycleManagement();
	
	public void createCISs();
	
	public void configureCISs();
	
	public void deleteCISs();
	
	public void processPreviousLongTimeCycle();
	
	public void processPreviousShortTimeCycle();
	
	public void loop();
	
	public void stimulusForCommunityCreationDetected();
	
	public void stimulusForCommunityDeletionDetected();
}