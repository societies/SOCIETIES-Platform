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

package org.societies.useragent.feedback.guis;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class TimedGUI extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;

	JPanel jContentPane;
	JPanel proposalPane;
	JPanel abortPane;
	JButton abort;
	boolean feedback;
	boolean submitted;

	public TimedGUI(){
		feedback = false;		
		submitted = false;
	}

	public boolean displayGUI(String proposalText, int timeout){
		this.setTitle("TEST - Timed Abort Feedback GUI");
		this.setSize(300, 150);
		this.setLocation(getXCoord(this.getWidth()), getYCoord(this.getHeight()));
		this.setContentPane(getJContentPane(proposalText));
		this.setAlwaysOnTop(true);

		Timer timer = new Timer(timeout, this);
		this.setVisible(true);
		timer.start();
		
		while(!submitted){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return feedback;
	}

	private JPanel getJContentPane(String proposalText){
		if(jContentPane == null){
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();

			//add proposal pane
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 1;
			c.insets = new Insets(10,10,5,10);
			jContentPane.add(getProposalPane(proposalText), c);

			//add submit pane
			c.gridy = 1;
			c.weighty = 0;
			c.insets = new Insets(5,10,10,10);
			c.fill = GridBagConstraints.REMAINDER;
			jContentPane.add(getAbortPane(), c);
		}
		return jContentPane;
	}

	private JPanel getProposalPane(String proposalText){
		if(proposalPane == null){
			proposalPane = new JPanel();
			proposalPane.setLayout(new GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;
			JLabel text = new JLabel(proposalText);
			proposalPane.add(text, c);
		}
		return proposalPane;
	}

	private JPanel getAbortPane(){
		if(abortPane == null){
			abortPane = new JPanel();
			abortPane.setLayout(new GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;
			abort = new JButton("Abort");
			abort.addActionListener(this);
			abortPane.add(abort, c);
		}
		return abortPane;
	}

	/*
	 * Coordinate methods to position JDialog in bottom right of screen
	 */
	private int getXCoord(int width) {
		int xCoord = 0;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double screenWidth = screenSize.getWidth();
		Double tmp = (Double) screenWidth - width;
		xCoord = tmp.intValue();
		return xCoord;
	}

	private int getYCoord(int height) {
		int yCoord = 0;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double screenHeight = screenSize.getHeight();
		Double tmp = (Double) screenHeight - height;
		yCoord = tmp.intValue();
		return yCoord;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.setVisible(false);
		if(e.getSource() == abort){
			feedback = false;
		}else{
			feedback = true;
		}
		submitted = true;
	}

	public static void main(String[] args){
		TimedGUI gui = new TimedGUI();
		
		boolean result = gui.displayGUI("Starting service A", 5000);
		System.out.println("Returned feedback: "+result);
	}
}
