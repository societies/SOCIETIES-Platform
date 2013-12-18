package org.societies.integration.test.bit.userfeedbacknotification;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.ExpProposalType;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.AccessControlResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;
import org.societies.api.schema.useragent.feedback.ExpFeedbackResultBean;
import org.societies.api.schema.useragent.feedback.ImpFeedbackResultBean;
import org.societies.api.schema.useragent.feedback.UserFeedbackBean;
import org.societies.useragent.api.model.UserFeedbackEventTopics;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.JButton;

public class Tester implements ActionListener, WindowListener{

	private static final Logger log = LoggerFactory.getLogger(Tester.class);

	private IUserFeedback userFeedback;
	private ICommManager commManager;
	private IIdentity identity;
	private Requestor requestor;
	private HashMap<JButton,Integer> buttonList;
	private NotificationGUI gui;
	private HashSet<Integer> buttonClick;
	private HashSet<Boolean> windowExit;

	private static final int SHOW_NOT = 1;
	private static final int ACK_NAK = 2;
	private static final int SHOW_RADIO = 3;
	private static final int SHOW_MANY = 4;
	private static final int SHOW_PPN = 5;
	private static final int SHOW_UAC = 6;  

	public Tester() {
		log.debug("Tester constructor");
	}

	@Before
	public void setUp() {
		this.userFeedback = TestUserFeedback.getUserFeedback();
		this.commManager = TestUserFeedback.getCommManager();
		this.identity = commManager.getIdManager().getThisNetworkNode();
		this.requestor = new Requestor(this.identity);

		this.buttonClick = new HashSet<Integer>();

		this.gui = new NotificationGUI();
		gui.setTester(this);
		this.buttonList = gui.getButtons();

		gui.setActionListeners();

	}

	@After
	public void tearDown() {
		this.gui.dispose();
	}

	@Test
	public void waitForButtonClick()
	{
		while(true)
		{
			synchronized(this.buttonClick)
			{
				try {
					this.buttonClick.wait();
				} catch (InterruptedException e) {
					Assert.fail();
					break;
				}
				log.debug("I have been notified!");
				try {

					int eventNumber = this.buttonClick.iterator().next();
					log.debug("The number got:" + eventNumber);
					if(eventNumber>0)
					{
						log.debug("Going to UF method");
						getUF(eventNumber);
					}
					else
					{
						log.debug("Disposing of GUI then finishing");
						gui.dispose();
						break;
					}
				} catch (InterruptedException e) {
					Assert.fail();
					break;
				} catch (ExecutionException e) {
					Assert.fail();
					break;
				}
			}
		}
		//	wait for action
		//while(true)
		//{
		//	try {
		//		log.debug("About to wait..");


		//	} catch (InterruptedException e) {
		//		log.error("Wait exception");
		//		Assert.fail();
		//	}
		//	log.debug("Going to UF");
		//	try {
		//		getUF();
		//	} catch (InterruptedException e) {
		//		// TODO Auto-generated catch block
		//		e.printStackTrace();
		//	} catch (ExecutionException e) {
		//		// TODO Auto-generated catch block
		//		e.printStackTrace();
		//	}



		//	}


	}

	public void getUF(int eventNumber) throws InterruptedException, ExecutionException
	{
		switch(eventNumber)
		{
		//Normal Show Notification
		case 1:this.userFeedback.showNotification("This is a notification");
		break;
		//ACK_NAK
		case 2:String[] list = new String[2];
		list[0]="Yes";
		list[1]="No";
		ExpProposalContent content = new ExpProposalContent("Here are your options", list);
		List<String> reply = this.userFeedback.getExplicitFB(org.societies.api.internal.useragent.model.ExpProposalType.ACKNACK, content).get();
		log.debug("Got: " + reply.get(0));
		break;
		//RADIO
		case 3:	String[] list2 = new String[2];
		list2[0]="Yes";
		list2[1]="No";
		ExpProposalContent content2 = new ExpProposalContent("Here are your options", list2);
		List<String> reply2 = this.userFeedback.getExplicitFB(org.societies.api.internal.useragent.model.ExpProposalType.RADIOLIST, content2).get();
		log.debug("Got: " + reply2.get(0));break;
		//SELECT MANY
		case 4 : String[] list3 = new String[]{"Yes", "No", "Maybe"};
		ExpProposalContent content3 = new ExpProposalContent("Here are your options", list3);
		List<String> reply3 = this.userFeedback.getExplicitFB(org.societies.api.internal.useragent.model.ExpProposalType.CHECKBOXLIST, content3).get();
		log.debug("Got: " + reply3);break;


		//PPN
		case 5:
			ResponsePolicy rp = new ResponsePolicy();
			NegotiationDetailsBean b = new NegotiationDetailsBean();
			RequestorBean rb = new RequestorBean();
			rb.setRequestorId("test.id.societies");
			b.setRequestor(rb);

			ResponsePolicy rp2 = this.userFeedback.getPrivacyNegotiationFB(rp, b).get();
			log.debug("Got response of PPN: " + rp2.getNegotiationStatus());
			break;
			//UAC
		case 6:
			AccessControlResponseItem item = new AccessControlResponseItem();
			RequestItem r = new RequestItem();
			//r.s
			//item.setRequestItem(value);
		//	this.userFeedback.getAccessControlFB(this.requestor, items)
			break;
		}

	}

	public void notifyWait(Integer button)
	{
		synchronized(this.buttonClick)
		{
			this.buttonClick.clear();
			this.buttonClick.add(button);
			this.buttonClick.notifyAll();
		}
	}

	public void stopTest()
	{
		synchronized(this.buttonClick)
		{
			log.debug("Notifiying thread to stop!");
			this.buttonClick.clear();
			this.buttonClick.add(0);
			this.notifyAll();
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		notifyWait(this.buttonList.get((JButton)arg0.getSource()));
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		//	stopTest();

	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		synchronized(this.buttonClick)
		{
			this.buttonClick.clear();
			this.buttonClick.add(0);
			this.buttonClick.notifyAll();
		}

	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}




}
