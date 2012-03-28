package org.societies.css.devicemgmt.DeviceDriverSimulator;



public class LightSimulator extends Thread {
	/* This thread is created to simulate the change of temperature.
	 * we decide to inform the first listener */
	 
	public boolean run = true;
	private LightSensor lightSensor ;
	private long delay;
	private int lsId;

	public LightSimulator (Object d, int id)
	{
		System.out.println("start Light Simul");
		lightSensor = (LightSensor) d;
		lsId=id;
		delay = id * 6000;
		this.start();	
	}
	public synchronized void stopSimul ()
	{
		System.out.println ("Stop Light Simul");
		run = false;
	}
	
	public void run ()
	{   
		while (run == true)
		{				
			
			/* fisrt delay to start the simulators at different time */ 
			try {
				Thread.sleep(delay);
			}catch(Exception e){
				System.out.println("Erreur Thread:"+e);
				}
			
			lightSensor.setLightLevel();

			System.out.println ("**************************************************");
			System.out.println ("<-- Driver : New Light received : "+Double.toString(lightSensor.getLightValue()) + " from sensor number " + lsId);
			System.out.println ("**************************************************\n\n");
			
			try {
				Thread.sleep(10000);
			}catch(Exception e){
				System.out.println("Erreur Thread:"+e);
				}
		}
	}
	public void startSimul() {
		System.out.println ("Start Simul");
	}
}
