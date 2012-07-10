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
package org.societies.personalisation.PersonalisationGUI.impl.preferences.common;

import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.personalisation.preference.api.model.ContextPreferenceCondition;
import org.societies.personalisation.preference.api.model.IPreferenceCondition;
import org.societies.personalisation.preference.api.model.OperatorConstants;
/**
 * @author  Administrator
 * @created April 30, 2010
 */
public class ConditionCreatorDialog extends JDialog implements ActionListener
{
	static ConditionCreatorDialog theConditionCreatorDialog;

	JPanel pnPanel0;

	JPanel pnPanel1;
	JComboBox cmbContextTypes;
	JTextField txtValue;

	JPanel pnPanel2;
	JButton btOK;
	JButton btCancel;
	JLabel lbLabel0;
	JLabel lbLabel1;
	
	boolean ok = false;

	private final ICtxBroker broker;
	
	private List<CtxAttribute> ctxAttributes = new ArrayList<CtxAttribute>();

	private JLabel lbLabel2;

	private JComboBox cmbOperators;

	private IIdentity userIdentity;
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
		theConditionCreatorDialog = new ConditionCreatorDialog(null,  true, null, null);
	} 

	/**
	 */
	public ConditionCreatorDialog(JFrame frame,  boolean isModal, ICtxBroker broker, IIdentity userIdentity){
		super(frame, "Create new Condition", isModal );
		this.broker = broker;
		this.userIdentity = userIdentity;
		
		this.showGUI();
	}
	private void setLocationAndSize(){
		/*	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			    int maxX = screenSize.width ;
			    int maxY = screenSize.height ;
			    this.setLocation((maxX/3), (maxY/3));*/
				this.setLocation(GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint());
			    this.getContentPane().setSize(400, 300);
			}
	public void showGUI()
	{



		pnPanel0 = new JPanel();
		GridBagLayout gbPanel0 = new GridBagLayout();
		GridBagConstraints gbcPanel0 = new GridBagConstraints();
		pnPanel0.setLayout( gbPanel0 );

		pnPanel1 = new JPanel();
		pnPanel1.setBorder( BorderFactory.createTitledBorder( "Add Condition" ) );
		GridBagLayout gbPanel1 = new GridBagLayout();
		GridBagConstraints gbcPanel1 = new GridBagConstraints();
		pnPanel1.setLayout( gbPanel1 );

		
		cmbContextTypes = new JComboBox();
		gbcPanel1.gridx = 1;
		gbcPanel1.gridy = 0;
		gbcPanel1.gridwidth = 1;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 0;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbcPanel1.insets = new Insets( 0,0,10,0 );
		gbPanel1.setConstraints( cmbContextTypes, gbcPanel1 );
		pnPanel1.add( cmbContextTypes );

		txtValue = new JTextField( );
		txtValue.setText("");
		gbcPanel1.gridx = 1;
		gbcPanel1.gridy = 2;
		gbcPanel1.gridwidth = 1;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 0;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbPanel1.setConstraints( txtValue, gbcPanel1 );
		pnPanel1.add( txtValue );

		pnPanel2 = new JPanel();
		GridBagLayout gbPanel2 = new GridBagLayout();
		GridBagConstraints gbcPanel2 = new GridBagConstraints();
		pnPanel2.setLayout( gbPanel2 );

		btOK = new JButton( "OK"  );
		btOK.addActionListener(this);
		gbcPanel2.gridx = 1;
		gbcPanel2.gridy = 0;
		gbcPanel2.gridwidth = 1;
		gbcPanel2.gridheight = 1;
		gbcPanel2.fill = GridBagConstraints.BOTH;
		gbcPanel2.weightx = 1;
		gbcPanel2.weighty = 0;
		gbcPanel2.anchor = GridBagConstraints.NORTH;
		gbPanel2.setConstraints( btOK, gbcPanel2 );
		pnPanel2.add( btOK );

		btCancel = new JButton( "Cancel"  );
		btCancel.addActionListener(this);
		gbcPanel2.gridx = 0;
		gbcPanel2.gridy = 0;
		gbcPanel2.gridwidth = 1;
		gbcPanel2.gridheight = 1;
		gbcPanel2.fill = GridBagConstraints.BOTH;
		gbcPanel2.weightx = 1;
		gbcPanel2.weighty = 0;
		gbcPanel2.anchor = GridBagConstraints.NORTH;
		gbPanel2.setConstraints( btCancel, gbcPanel2 );
		pnPanel2.add( btCancel );
		gbcPanel1.gridx = 0;
		gbcPanel1.gridy = 4;
		gbcPanel1.gridwidth = 2;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 0;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbcPanel1.insets = new Insets( 5,0,0,0 );
		gbPanel1.setConstraints( pnPanel2, gbcPanel1 );
		pnPanel1.add( pnPanel2 );

		lbLabel0 = new JLabel( "Condition Type:"  );
		gbcPanel1.gridx = 0;
		gbcPanel1.gridy = 0;
		gbcPanel1.gridwidth = 1;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 1;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbPanel1.setConstraints( lbLabel0, gbcPanel1 );
		pnPanel1.add( lbLabel0 );

		lbLabel1 = new JLabel( "Value:"  );
		gbcPanel1.gridx = 0;
		gbcPanel1.gridy = 2;
		gbcPanel1.gridwidth = 1;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 1;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbPanel1.setConstraints( lbLabel1, gbcPanel1 );
		pnPanel1.add( lbLabel1 );

		lbLabel2 = new JLabel( "Operator:"  );
		gbcPanel1.gridx = 0;
		gbcPanel1.gridy = 1;
		gbcPanel1.gridwidth = 1;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 1;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbPanel1.setConstraints( lbLabel2, gbcPanel1 );
		pnPanel1.add( lbLabel2 );

		
		cmbOperators = new JComboBox( OperatorConstants.values() );
		gbcPanel1.gridx = 1;
		gbcPanel1.gridy = 1;
		gbcPanel1.gridwidth = 1;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 0;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbPanel1.setConstraints( cmbOperators, gbcPanel1 );
		pnPanel1.add( cmbOperators );
		gbcPanel0.gridx = 3;
		gbcPanel0.gridy = 6;
		gbcPanel0.gridwidth = 14;
		gbcPanel0.gridheight = 8;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbcPanel0.insets = new Insets( 2,2,2,2 );
		gbPanel0.setConstraints( pnPanel1, gbcPanel0 );
		pnPanel0.add( pnPanel1 );

		this.loadContextTypes();
		setDefaultCloseOperation( DISPOSE_ON_CLOSE );

		setContentPane( pnPanel0 );
		pack();
		this.setLocationAndSize();
		setVisible( true );
	} 
	
	public String getContextType(){
		int index = this.cmbContextTypes.getSelectedIndex();
		
		return this.ctxAttributes.get(index).getType();
	}
	
	public CtxIdentifier getContextID(){
		
		CtxAttribute attr = this.getCtxAttribute();
		return attr.getId();
/*		CtxIdentifier id = attr.getId();
		try{
			CtxIdentifier parsed = this.broker.parseIdentifier(id.toUriString());
			return parsed;
		}catch (CtxException ce){
			System.out.println(this.getClass().getName()+"WARNING: Unable to parse CtxIdentifier. Condition will possibly exceed 30KB and won't be stored in DB");
		}
		return id;*/
	}
	
	public CtxAttribute getCtxAttribute(){
		int index = this.cmbContextTypes.getSelectedIndex();
		
		return this.ctxAttributes.get(index);
	}
	
	private void calculateSizeOfObject(String message, Object p){
		ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(p);
			oos.flush(); 
			oos.close(); 
			bos.close();
			System.out.println(message+" "+bos.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	public IPreferenceCondition getConditionObject(){
		CtxAttributeIdentifier id = (CtxAttributeIdentifier) this.getContextID();
		//this.calculateSizeOfObject("ctxIdentifier size"+id.toUriString(), id);
		OperatorConstants op = this.getOperator();
		//this.calculateSizeOfObject("Operator size"+op.toString(), op);
		String value = this.getContextValue();
		//this.calculateSizeOfObject("Value size"+value, value);
		String type = this.getContextType();
		//this.calculateSizeOfObject("Type size"+type, type);
		ContextPreferenceCondition con = new ContextPreferenceCondition((CtxAttributeIdentifier) this.getContextID(), this.getOperator(), value, type);
		return con;
	}
	
	public OperatorConstants getOperator(){
		return (OperatorConstants) this.cmbOperators.getSelectedItem();
	}
	
	public String getContextValue(){
		return this.txtValue.getText();
	}
	
	public boolean getResult(){
		return this.ok;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(this.btCancel)){
			this.ok = false;
			this.dispose();
		}else{
			this.ok = true;
			if (txtValue.getText().trim().equals("")){
				JOptionPane.showMessageDialog(this, "Please enter a value in the textbox", "Missing value", JOptionPane.ERROR_MESSAGE);
			}else{
				if (!this.cmbOperators.getSelectedItem().equals(OperatorConstants.EQUALS)){
					try{
						int intValue = Integer.parseInt(this.txtValue.getText());
					}catch (NumberFormatException ex){
						JOptionPane.showMessageDialog(this,  txtValue.getText()+" is not a valid number\nInput a valid number or change the Operator to EQUALS", "Incorrect value", JOptionPane.ERROR_MESSAGE);
					}
				}else{
					this.dispose();
				}
			}
		}
	}
	
	private void loadContextTypes(){
		try {
			CtxEntity entity = broker.retrieveIndividualEntity(this.userIdentity).get();
			Set<CtxAttribute> attributes = entity.getAttributes();
			Iterator<CtxAttribute> it = attributes.iterator();
			while (it.hasNext()){
				CtxAttribute attr = it.next();
				this.ctxAttributes.add(attr);
				cmbContextTypes.addItem(attr.getType()+" "+attr.getId().toUriString());
			}
			
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
} 
