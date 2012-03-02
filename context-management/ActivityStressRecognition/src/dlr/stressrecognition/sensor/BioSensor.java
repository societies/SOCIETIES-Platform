/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dlr.stressrecognition.sensor;

import dlr.stressrecognition.classifier.StressElicitationActivity;
import dlr.stressrecognition.logger.Logger;
import zephyr.android.BioHarnessBT.BTClient;
import zephyr.android.BioHarnessBT.ConnectListenerImpl;
import zephyr.android.BioHarnessBT.ConnectedEvent;
import zephyr.android.BioHarnessBT.PacketTypeRequest;
import zephyr.android.BioHarnessBT.ZephyrPacketArgs;
import zephyr.android.BioHarnessBT.ZephyrPacketEvent;
import zephyr.android.BioHarnessBT.ZephyrPacketListener;
import zephyr.android.BioHarnessBT.ZephyrProtocol;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * The BioSensor class handles the connection with the Zephyr BioHarness BT.
 * 
 * @author Michael Gross
 *
 */
public class BioSensor extends ConnectListenerImpl {   
	// Zephyr message identifier
	final int GP_MSG_ID = 0x20;
	final int BREATHING_MSG_ID = 0x21;
	final int ECG_MSG_ID = 0x22;
	final int RtoR_MSG_ID = 0x24;
	final int ACCEL_100mg_MSG_ID = 0x2A;
	final int SUMMARY_MSG_ID = 0x2B;

	private boolean logging = true;
	
	private Handler mHandler; 
	private Logger gpLogger;
	private Logger rrLogger;
	private Logger ecgLogger;
	private Logger brLogger;
	
	/*Creating the different Objects for different types of Packets*/
	private GeneralPacketInfo GPInfo = new GeneralPacketInfo();
	private ECGPacketInfo ECGInfoPacket = new ECGPacketInfo();
	private BreathingPacketInfo BreathingInfoPacket = new  BreathingPacketInfo();
	private RtoRPacketInfo RtoRInfoPacket = new RtoRPacketInfo();
	//private AccelerometerPacketInfo AccInfoPacket = new AccelerometerPacketInfo();
	//private SummaryPacketInfo SummaryInfoPacket = new SummaryPacketInfo();
    
	private PacketTypeRequest RqPacketType = new PacketTypeRequest();
	
	/**
	 * Constructor
	 * @param handler, to the main activity
	 * @param mHandler, to the main activity
	 */
	public BioSensor(Handler handler, Handler mHandler) {
		super(handler, null);
		this.mHandler = mHandler;
		
		// Create Logging Objects
		gpLogger = new Logger("General-Packet-Log");
		String[] gpColumns = {"HR", "BR", "Temp", "GSR", "BRAmplitude", "ECGAmplitude", "ECGNoise"};
		gpLogger.writeHeader(gpColumns);
		
		rrLogger = new Logger("RR-Packet-Log");
		String[] rrColumns = {"RtoR"};
		rrLogger.writeHeader(rrColumns);
		
		ecgLogger = new Logger("ECG-Log");
		String[] ecgColumns = {"ECG"};
		ecgLogger.writeHeader(ecgColumns);
		
		brLogger = new Logger("BR-Packet-Log");
		String[] brColumns = {"BR"};
		brLogger.writeHeader(brColumns);	
	}
	
	/**
	 * @param eventArgs 
	 */
	public void Connected(ConnectedEvent<BTClient> eventArgs) {
		System.out.println(String.format("Connected to BioHarness %s.", eventArgs.getSource().getDevice().getName()));
		/*Use this object to enable or disable the different Packet types*/
		RqPacketType.GP_ENABLE = true;
		RqPacketType.ECG_ENABLE = true;
		RqPacketType.RtoR_ENABLE = true;
		RqPacketType.BREATHING_ENABLE = true;
		RqPacketType.LOGGING_ENABLE = true;
		
		//Creates a new ZephyrProtocol object and passes it the BTComms object
		ZephyrProtocol _protocol = new ZephyrProtocol(eventArgs.getSource().getComms(), RqPacketType);
		//ZephyrProtocol _protocol = new ZephyrProtocol(eventArgs.getSource().getComms(), );
		_protocol.addZephyrPacketEventListener(new ZephyrPacketListener() {
			public void ReceivedPacket(ZephyrPacketEvent eventArgs) {
				ZephyrPacketArgs msg = eventArgs.getPacket();
				/*
				byte CRCFailStatus;
				byte RcvdBytes;
				CRCFailStatus = msg.getCRCStatus();
				RcvdBytes = msg.getNumRvcdBytes() ;
				*/
				int MsgID = msg.getMsgID();
				byte [] DataArray = msg.getBytes();	
				switch (MsgID)
				{
				case GP_MSG_ID:
					//***************Displaying the Heart Rate********************************
					int HRate =  GPInfo.GetHeartRate(DataArray);
					Message message = mHandler.obtainMessage(StressElicitationActivity.HEART_RATE);
					Bundle b = new Bundle();
					b.putDouble("HeartRate",HRate);
					//message.setData(b);
					//mHandler.sendMessage(message);
					//System.out.println("Heart Rate is "+ HRate);

					//***************Displaying the Respiration Rate********************************
					double RespRate = GPInfo.GetRespirationRate(DataArray);
					//message = mHandler.obtainMessage(StressElicitationActivity.RESPIRATION_RATE);
					b.putDouble("RespirationRate", RespRate);
					//message.setData(b);
					//mHandler.sendMessage(message);
					//System.out.println("Respiration Rate is "+ RespRate);
					
					//***************Displaying the Skin Temperature*******************************
					double SkinTempDbl = GPInfo.GetSkinTemperature(DataArray);
					//message = mHandler.obtainMessage(StressElicitationActivity.SKIN_TEMPERATURE);
					b.putDouble("SkinTemperature", SkinTempDbl);
					//mHandler.sendMessage(text1);
					message.setData(b);
					mHandler.sendMessage(message);
					
					//System.out.println("Skin Temperature is "+ SkinTempDbl);
					
					//*************** Displaying the GSR ******************************************
					double GSRvalue = GPInfo.GetGSR(DataArray);
					//message = mHandler.obtainMessage(StressElicitationActivity.GSR);
					//b.putString("GSR", String.valueOf(GSRvalue));
					//message.setData(b);
					//mHandler.sendMessage(text1);
					
					//*************** Displaying the BR Amplitude *********************************
					double BRAmplitude = GPInfo.GetBreathingWaveAmplitude(DataArray);
					//message = mHandler.obtainMessage(StressElicitationActivity.BRAMPLITUDE);
					//b.putString("BR-Amplitude", String.valueOf(BRAmplitude));
					//message.setData(b);
					//mHandler.sendMessage(text1);
					
					//*************** Displaying the ECG Amplitude *********************************
					double ECGAmplitude = GPInfo.GetECGAmplitude(DataArray);
					//message = mHandler.obtainMessage(StressElicitationActivity.ECGAMPLITUDE);
					//b.putString("ECG-Amplitude", String.valueOf(ECGAmplitude));
					//message.setData(b);
					//mHandler.sendMessage(text1);
					
					//*************** Displaying the ECG Noise *********************************
					double ECGNoise = GPInfo.GetECGNoise(DataArray);
					//message = mHandler.obtainMessage(StressElicitationActivity.ECGNOISE);
					//b.putString("ECG-Noise", String.valueOf(ECGNoise));
					//message.setData(b);
					//mHandler.sendMessage(text1);
					
					String[] data  = {String.valueOf(HRate), String.valueOf(RespRate), String.valueOf(SkinTempDbl), String.valueOf(GSRvalue), String.valueOf(BRAmplitude), String.valueOf(ECGAmplitude), String.valueOf(ECGNoise)};
					if(logging)
						gpLogger.write(data);
					
					/***************Displaying the Posture******************************************					
					*+++++ Ignored for the stress recognition system +++++
					*
					*int PostureInt = GPInfo.GetPosture(DataArray);
					*text1 = mHandler.obtainMessage(POSTURE);
					*b1.putString("Posture", String.valueOf(PostureInt));
					*text1.setData(b1);
					*mHandler.sendMessage(text1);
					*System.out.println("Posture is "+ PostureInt);	
					/***************Displaying the Peak Acceleration*********************************
					*+++++ Ignored for the stress recognition system +++++
					*
					*double PeakAccDbl = GPInfo.GetPeakAcceleration(DataArray);
					*text1 = mHandler.obtainMessage(PEAK_ACCLERATION);
					*b1.putString("PeakAcceleration", String.valueOf(PeakAccDbl));
					*text1.setData(b1);
					*mHandler.sendMessage(text1);
					*System.out.println("Peak Acceleration is "+ PeakAccDbl);	
					*
					*byte ROGStatus = GPInfo.GetROGStatus(DataArray);
					*System.out.println("ROG Status is "+ ROGStatus);
					*
					*********************************************************************************/
					break;
					
				case BREATHING_MSG_ID:
					//Bundle breathingBundle = new Bundle();
					short[] breathingData = BreathingInfoPacket.GetBreathingSamples(DataArray);
					//breathingBundle.putShortArray("Breathing", breathingData);
					//Message breathingMsg = mHandler.obtainMessage(StressElicitationActivity.RESPIRATION);
					//breathingMsg.setData(breathingBundle);
					//mHandler.sendMessage(breathingMsg);
					//System.out.println("Breathing Packet Sequence Number is "+BreathingInfoPacket.GetSeqNum(DataArray));
					if(logging) {
						for(int i=0; i < breathingData.length; i++) {
							brLogger.write(String.valueOf(breathingData[i]));
						}
					}
					break;
				case ECG_MSG_ID:
					//Bundle ecgBundle = new Bundle();
					short[] ecgData = ECGInfoPacket.GetECGSamples(DataArray);
					//ecgBundle.putShortArray("ECG", ecgData);
					//Message ecgMsg = mHandler.obtainMessage(StressElicitationActivity.ECG);
					//ecgMsg.setData(ecgBundle);
					//mHandler.sendMessage(ecgMsg);
					//System.out.println("ECG Packet Sequence Number is "+ECGInfoPacket.GetSeqNum(DataArray));
					if(logging) {
						for(int i=0; i < ecgData.length; i++) {
							ecgLogger.write(Short.toString(ecgData[i]));
						}
					}
					break;
				case RtoR_MSG_ID:
					//Bundle rrBundle = new Bundle();
					int[] rrData = RtoRInfoPacket.GetRtoRSamples(DataArray);
					//rrBundle.putIntArray("RtoR", rrData);
					//Message rrMsg = mHandler.obtainMessage(StressElicitationActivity.RR);
					//rrMsg.setData(rrBundle);
					for(int i=0; i < rrData.length; i++) {
						Bundle rrBundle = new Bundle();
						Message rrMsg = mHandler.obtainMessage(StressElicitationActivity.RR);
						rrMsg.setData(rrBundle);
						rrBundle.putInt("RtoR", rrData[i]);
						mHandler.sendMessage(rrMsg);
					}	
					//mHandler.sendMessage(rrMsg);
					//System.out.println("R to R Packet Sequence Number is "+RtoRInfoPacket.GetSeqNum(DataArray));
					if(logging) {
						for(int i=0; i < rrData.length; i++) {
							rrLogger.write(Integer.toString(rrData[i]));
						}	
					}
					break;
				/*case ACCEL_100mg_MSG_ID:
					Ignored for the stress recognition system
					System.out.println("Accelerometry Packet Sequence Number is "+AccInfoPacket.GetSeqNum(DataArray));
					break;
				case SUMMARY_MSG_ID:
					Do what you want. Printing Sequence Number for now
					System.out.println("Summary Packet Sequence Number is "+SummaryInfoPacket.GetSeqNum(DataArray));
					break;
				*/			
				}
			}
		});
	}

	public void setLogging(boolean logging) {
		this.logging = logging;
	}

	public boolean isLogging() {
		return logging;
	}  
}
