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
package org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Elizabeth
 *
 */
public class TimedNotificationGUI implements ActionListener{
	JFrame frame;
	JDialog dialog;
	Timer timer = new Timer(3000, this);

	private Logger logging = LoggerFactory.getLogger(this.getClass());
	boolean response = true;

	public void actionPerformed(ActionEvent e) {
		log(e.getActionCommand());
		if (e.getActionCommand().equals("timeout")){
			//log("getActioncommand==timeout");
			this.response = true;
		}else{
			//log("getActioncommand!=timeout");
			this.response = false;
		}
		dialog.dispose();
		timer.stop();
	}

	public static void main(String[] args) {
/*		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new TimedNotificationGUI().showGUI("SYMBOLIC_LOCATION");
			}
		});*/
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new TimedNotificationGUI().showGUI("Ignoring invalid policy", "Privacy Policy Negotiation Client");
			}
		});
			
	}

	
	public boolean showGUI(String contextType) {
		this.timer.setActionCommand("timeout");
		/*JOptionPane optionPane = new JOptionPane("Eggs are not supposed to be green.",
				JOptionPane.INFORMATION_MESSAGE);*/
		JOptionPane optionPane = new JOptionPane(
			    "Implementing Effect: PERMIT\n"
			    + "for resource:"+contextType+"\n"
			    + "Click Cancel to Abort",
			    JOptionPane.QUESTION_MESSAGE,
			    JOptionPane.YES_NO_OPTION);
		
		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(this);
		cancelBtn.setActionCommand("cancelled");
		optionPane.setOptions(new Object[]{cancelBtn});
		dialog = optionPane.createDialog("Privacy Preference Evaluation");
		
		timer.start();
		dialog.setVisible(true);

		//log("Returning: "+this.response);
		return this.response;
	}
	
	public void showGUI(String message, String title){
		this.timer.setActionCommand("timeout");
		JOptionPane optionPane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
		dialog = optionPane.createDialog(title);
		timer.start();
		dialog.setVisible(true);
	}
	private void log(String message){
		this.logging.info(this.getClass().getName()+" : "+message);
	}
}
