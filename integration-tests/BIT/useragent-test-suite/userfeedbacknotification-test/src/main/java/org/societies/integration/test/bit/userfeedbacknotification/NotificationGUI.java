package org.societies.integration.test.bit.userfeedbacknotification;

//======================================================
//Source code generated by jvider v1.8 EVALUATION version.
//http://www.jvider.com/
//======================================================
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.WindowConstants;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
/**
 * @author  Administrator
 * @created October 31, 2013
 */
public class NotificationGUI extends JFrame 
{
	
	private Tester tester;
	static NotificationGUI theframe;

	JPanel pnPanel0;
	JButton btShowNot;
	JButton btAckNak;
	JButton btShowRadio;
	JButton btShowMany;
	JButton btPpnButon;
	JButton btUacBtn;
	/**
	 */
	public static void main( String args[] ) 
	{
		try 
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch ( ClassNotFoundException e ) 
		{
		}
		catch ( InstantiationException e ) 
		{
		}
		catch ( IllegalAccessException e ) 
		{
		}
		catch ( UnsupportedLookAndFeelException e ) 
		{
		}
		theframe = new NotificationGUI();
	} 

	public void setTester(Tester tester)
	{
		this.tester = tester;
	}
	
	public HashMap<JButton,Integer> getButtons()
	{
		HashMap<JButton, Integer> buttonList = new HashMap<JButton,Integer>();
		buttonList.put(btShowNot,1);
		buttonList.put(btAckNak,2);
		buttonList.put(btShowRadio,3);
		buttonList.put(btShowMany,4);
		buttonList.put(btPpnButon,5);
		buttonList.put(btUacBtn,6);
		return buttonList;
	}
	
	public void setActionListeners()
	{
		btShowNot.addActionListener(tester);
		btAckNak.addActionListener(tester);
		btShowRadio.addActionListener(tester);
		btShowMany.addActionListener(tester);
		btPpnButon.addActionListener(tester);
		btUacBtn.addActionListener(tester);
		
		addWindowListener(tester);
	}
	/**
	 */
	public NotificationGUI() 
	{
		super( "TITLE" );
		
		pnPanel0 = new JPanel();
		GridBagLayout gbPanel0 = new GridBagLayout();
		GridBagConstraints gbcPanel0 = new GridBagConstraints();
		pnPanel0.setLayout( gbPanel0 );

		btShowNot = new JButton( "Show Notification"  );
		gbcPanel0.gridx = 0;
		gbcPanel0.gridy = 8;
		gbcPanel0.gridwidth = 5;
		gbcPanel0.gridheight = 2;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( btShowNot, gbcPanel0 );
		pnPanel0.add( btShowNot );

		btAckNak = new JButton( "Show ACKNACK"  );
		btAckNak.setActionCommand( "" );
		gbcPanel0.gridx = 5;
		gbcPanel0.gridy = 8;
		gbcPanel0.gridwidth = 5;
		gbcPanel0.gridheight = 2;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( btAckNak, gbcPanel0 );
		pnPanel0.add( btAckNak );

		btShowRadio = new JButton( "Show Radio"  );
		btShowRadio.setActionCommand( "" );
		gbcPanel0.gridx = 10;
		gbcPanel0.gridy = 8;
		gbcPanel0.gridwidth = 5;
		gbcPanel0.gridheight = 2;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( btShowRadio, gbcPanel0 );
		pnPanel0.add( btShowRadio );

		btShowMany = new JButton( "Show SelectMany"  );
		btShowMany.setActionCommand( "" );
		gbcPanel0.gridx = 15;
		gbcPanel0.gridy = 8;
		gbcPanel0.gridwidth = 5;
		gbcPanel0.gridheight = 2;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( btShowMany, gbcPanel0 );
		pnPanel0.add( btShowMany );

		btPpnButon = new JButton( "Show PPN"  );
		btPpnButon.setActionCommand( "" );
		gbcPanel0.gridx = 2;
		gbcPanel0.gridy = 17;
		gbcPanel0.gridwidth = 5;
		gbcPanel0.gridheight = 2;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( btPpnButon, gbcPanel0 );
		pnPanel0.add( btPpnButon );

		btUacBtn = new JButton( "Show UAC"  );
		btUacBtn.setActionCommand( "" );
		gbcPanel0.gridx = 13;
		gbcPanel0.gridy = 17;
		gbcPanel0.gridwidth = 5;
		gbcPanel0.gridheight = 2;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( btUacBtn, gbcPanel0 );
		pnPanel0.add( btUacBtn );


		setContentPane( pnPanel0 );
		pack();
		setVisible( true );
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		
	} 
} 
