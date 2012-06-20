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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class CheckBoxGUI{

	List<String> feedback;
	JFrame frame;
	JPanel jContentPane;
	JPanel proposalPane;
	JPanel optionsPane;
	JPanel submitPane;
	JButton submit;
	boolean submitted;

	public CheckBoxGUI(){
		feedback = new ArrayList<String>();
		submitted = false;
	}

	public List<String> displayGUI(String proposalText, String[] options){
		frame = new JFrame();
		frame.setTitle("TEST - Checkbox Feedback GUI");
		frame.setSize(300, 300);
		frame.setLocation(getXCoord(frame.getWidth()), getYCoord(frame.getHeight()));
		frame.setContentPane(getJContentPane(proposalText, options));
		frame.setVisible(true);
		while(!submitted){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return feedback;
	}

	private JPanel getJContentPane(String proposalText, String[] options){
		if(jContentPane == null){
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();

			//add proposal pane
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.insets = new Insets(10,10,5,10);
			jContentPane.add(getProposalPane(proposalText), c);

			//add options pane
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridy = 1;
			c.weighty = 1;
			jContentPane.add(getOptionsPane(options), c);

			//add submit pane
			c.gridy = 2;
			c.weighty = 0;
			c.insets = new Insets(5,10,10,10);
			c.fill = GridBagConstraints.REMAINDER;
			jContentPane.add(getSubmitPane(), c);
		}
		return jContentPane;
	}

	private JPanel getProposalPane(String proposalText){
		if(proposalPane == null){
			proposalPane = new JPanel();
			proposalPane.setLayout(new GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();
			JLabel text = new JLabel(proposalText);
			proposalPane.add(text, c);
		}
		return proposalPane;
	}

	private JPanel getOptionsPane(String[] options){
		if(optionsPane == null){
			optionsPane = new JPanel();
			optionsPane.setLayout(new GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();
			c.weightx = 1;
			c.fill = GridBagConstraints.BOTH;
			for(int i=0; i<options.length; i++){
				JCheckBox checkbox = new JCheckBox(options[i]);
				checkbox.setActionCommand(options[i]);
				checkbox.addItemListener(new CheckboxListener());
				c.gridy = i;
				optionsPane.add(checkbox, c);
			}	
		}
		return optionsPane;
	}

	private JPanel getSubmitPane(){
		if(submitPane == null){
			submitPane = new JPanel();
			submitPane.setLayout(new GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();
			submit = new JButton("OK");
			submit.addActionListener(new ButtonListener());
			submitPane.add(submit, c);
		}
		return submitPane;
	}


	private int getXCoord(int width)
	{
		int xCoord = 0;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double screenWidth = screenSize.getWidth();
		Double middle = (Double)screenWidth/2;
		Double tmp = middle - (width/2);
		xCoord = tmp.intValue();
		return xCoord;
	}

	private int getYCoord(int height)
	{
		int yCoord = 0;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double screenHeight = screenSize.getHeight();
		Double middle = (Double)screenHeight/2;
		Double tmp = middle - (height/2);
		yCoord = tmp.intValue();
		return yCoord;
	}

	public class CheckboxListener implements ItemListener{
		@Override
		public void itemStateChanged(ItemEvent e) {
			String item = ((AbstractButton) e.getItemSelectable()).getActionCommand();
			if(e.getStateChange() == ItemEvent.SELECTED){
				feedback.add(item); 
			}else if(e.getStateChange() == ItemEvent.DESELECTED){
				feedback.remove(item);
			}
		}
	}

	public class ButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource().equals(submit)){
				frame.setVisible(false);
				submitted = true;
			}
		}
	}

	public static void main(String[] args){
		String[] options = {"yes", "no", "maybe", "ask again later"};
		CheckBoxGUI gui = new CheckBoxGUI();

		List<String> results = gui.displayGUI("Are you OK??", options);
		System.out.println("Selected feedback: ");
		for(String nextResult : results){
			System.out.println(nextResult);
		}
	}
}
