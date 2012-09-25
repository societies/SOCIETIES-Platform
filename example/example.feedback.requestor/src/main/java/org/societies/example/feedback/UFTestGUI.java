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

package org.societies.example.feedback;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.ExpProposalType;
import org.societies.api.internal.useragent.model.ImpProposalContent;
import org.societies.api.internal.useragent.model.ImpProposalType;

public class UFTestGUI extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JButton radioRequest;
	private JButton checkRequest;
	private JButton ackRequest;
	private JButton abortRequest;
	private JButton notificationRequest;

	private IUserFeedback feedback;
	private int i;

	public UFTestGUI(){
	}

	public void initialiseExampleFeedbackRequestor(){
		this.setTitle("User Feedback test GUI");
		this.setSize(1000, 200);
		this.setLayout(new GridLayout());
		this.setContentPane(getJContentPane());
		this.setVisible(true);
		
		i = 1;
	}

	private JPanel getJContentPane(){
		if(contentPane == null){
			contentPane = new JPanel();
			contentPane.setLayout(new GridLayout());
		}

		radioRequest = new JButton("Request Radio GUI");
		radioRequest.addActionListener(this);
		checkRequest = new JButton("Request Checkbox GUI");
		checkRequest.addActionListener(this);
		ackRequest = new JButton("Request Ack/Nack GUI");
		ackRequest.addActionListener(this);
		abortRequest = new JButton("Request Abort GUI");
		abortRequest.addActionListener(this);
		notificationRequest = new JButton("Request Notification GUI");
		notificationRequest.addActionListener(this);
		contentPane.add(radioRequest);
		contentPane.add(checkRequest);
		contentPane.add(ackRequest);
		contentPane.add(abortRequest);
		contentPane.add(notificationRequest);

		return contentPane;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == radioRequest){
			int requestId = i;
			i++;
			String proposalText = "Request "+requestId+" -> Please choose your favourite cuisine:";
			String[] options = {"Scottish", "Indian", "Chinese", "Mexican", "Italian", "Spanish", "French", "American"};
			ExpProposalContent content = new ExpProposalContent(proposalText, options);
			try {
				List<String> answer = feedback.getExplicitFB(ExpProposalType.RADIOLIST, content).get();
				System.out.println("Request "+requestId+" -> Favourite cuisine is:");
				for(String next: answer){
					System.out.println(next);
				}
				System.out.println();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				e1.printStackTrace();
			}
		}else if(e.getSource() == checkRequest){
			int requestId = i;
			i++;
			String proposalText = "Request "+requestId+" -> Please choose your favourite colours:";
			String[] options = {"RED", "WHITE", "GREEN", "BLUE", "BLACK", "YELLOW", "Purple", "Gold", "Pink", "Silver"};			
			ExpProposalContent content = new ExpProposalContent(proposalText, options);
			try {
				List<String> answer = feedback.getExplicitFB(ExpProposalType.CHECKBOXLIST, content).get();
				System.out.println("Request "+requestId+" -> Favourite colours are:");
				for(String next: answer){
					System.out.println(next);
				}
				System.out.println();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				e1.printStackTrace();
			}
		}else if (e.getSource() == ackRequest){
			int requestId = i;
			i++;
			String proposalText = "Request "+requestId+" -> Is it raining?";
			String[] options = {"Yes", "No", "Don't know"};
			ExpProposalContent content = new ExpProposalContent(proposalText, options);
			try {
				List<String> answer = feedback.getExplicitFB(ExpProposalType.ACKNACK, content).get();
				System.out.println("Request "+requestId+" -> Answer is:");
				for(String next: answer){
					System.out.println(next);
				}
				System.out.println();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				e1.printStackTrace();
			}
		}else if (e.getSource() == abortRequest){
			int requestId = i;
			i++;
			String proposalText = "Request "+requestId+" -> Starting service X for you...";
			ImpProposalContent content = new ImpProposalContent(proposalText, 5000);
			try {
				Boolean answer = feedback.getImplicitFB(ImpProposalType.TIMED_ABORT, content).get();
				System.out.println("Request "+requestId+" -> "+answer.toString());
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				e1.printStackTrace();
			}
		}else if (e.getSource() == notificationRequest){
			int requestId = i;
			i++;
			feedback.showNotification("Request "+requestId+" -> It rains a lot in Scotland");
		}else{
			System.out.println("Error - did not recognise event source");
		}
	}

	public void setFeedback(IUserFeedback feedback){
		this.feedback = feedback;
	}
}
