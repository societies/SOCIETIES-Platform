package org.personalsmartspace.spm.servicepolicygui.impl;

import javax.swing.JFrame;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.WindowConstants;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.personalsmartspace.spm.negotiation.api.platform.RequestItem;
import org.personalsmartspace.spm.preference.api.platform.Action;
import org.personalsmartspace.spm.preference.api.platform.Condition;
import org.personalsmartspace.spm.preference.api.platform.Resource;
import org.personalsmartspace.spm.preference.api.platform.constants.ActionConstants;
import org.personalsmartspace.spm.preference.api.platform.constants.ConditionConstants;

public class RequestItemEditor extends JFrame
{
	private JPanel pnPanel0;
	private JPanel resourcePanel;
	private JLabel resourceLabel;
	private JTextField resourceTypeTxtField;
	private JPanel actionsPanel;
	//private JList actionsModel;
	private ActionsTableModel actionsModel;
	private JTable actionsTable;
	private JButton addActionBtn;
	private JButton removeActionBtn;
	private JPanel conditionsPanel;
	private JTable conditionsTable;
	private JButton addConditionBtn;
	private JButton removeConditionBtn;
	private JButton btnSave;
	private JButton btnDiscard;
	private ConditionsTableModel conditionsModel;

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
		RequestItemEditor theRequestItemEditor = new RequestItemEditor(null);
	} 

	/**
	 */
	public RequestItemEditor(ActionListener listener) 
	{
		super( "Requested Items Editor" );

		pnPanel0 = new JPanel();
		pnPanel0.setBorder( BorderFactory.createTitledBorder( "Resource Editor" ) );
		GridBagLayout gbPanel0 = new GridBagLayout();
		GridBagConstraints gbcPanel0 = new GridBagConstraints();
		pnPanel0.setLayout( gbPanel0 );

		resourcePanel = new JPanel();
		GridBagLayout gbPanel2 = new GridBagLayout();
		GridBagConstraints gbcPanel2 = new GridBagConstraints();
		resourcePanel.setLayout( gbPanel2 );

		resourceLabel = new JLabel( "ResourceType"  );
		gbcPanel2.gridx = 0;
		gbcPanel2.gridy = 0;
		gbcPanel2.gridwidth = 1;
		gbcPanel2.gridheight = 1;
		gbcPanel2.fill = GridBagConstraints.BOTH;
		gbcPanel2.weightx = 1;
		gbcPanel2.weighty = 1;
		gbcPanel2.anchor = GridBagConstraints.NORTH;
		gbPanel2.setConstraints( resourceLabel, gbcPanel2 );
		resourcePanel.add( resourceLabel );

		resourceTypeTxtField = new JTextField( );
		gbcPanel2.gridx = 1;
		gbcPanel2.gridy = 0;
		gbcPanel2.gridwidth = 1;
		gbcPanel2.gridheight = 1;
		gbcPanel2.fill = GridBagConstraints.BOTH;
		gbcPanel2.weightx = 1;
		gbcPanel2.weighty = 0;
		gbcPanel2.anchor = GridBagConstraints.NORTH;
		gbPanel2.setConstraints( resourceTypeTxtField, gbcPanel2 );
		resourcePanel.add( resourceTypeTxtField );
		gbcPanel0.gridx = 0;
		gbcPanel0.gridy = 0;
		gbcPanel0.gridwidth = 2;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.HORIZONTAL;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 1;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbcPanel0.insets = new Insets( 3,3,3,3 );
		gbPanel0.setConstraints( resourcePanel, gbcPanel0 );
		pnPanel0.add( resourcePanel );

		actionsPanel = new JPanel();
		actionsPanel.setBorder( BorderFactory.createTitledBorder( "Actions" ) );
		GridBagLayout gbActionsPanel = new GridBagLayout();
		GridBagConstraints gbcActionsPanel = new GridBagConstraints();
		actionsPanel.setLayout( gbActionsPanel );

		List<ActionConstants> actions = new ArrayList<ActionConstants>();
		//actionsModel = new JList( actions.toArray() );
		actionsModel = new ActionsTableModel();
		actionsTable = new JTable(actionsModel);
		JScrollPane scpList0 = new JScrollPane( actionsTable );
		gbcActionsPanel.gridx = 0;
		gbcActionsPanel.gridy = 0;
		gbcActionsPanel.gridwidth = 2;
		gbcActionsPanel.gridheight = 1;
		gbcActionsPanel.fill = GridBagConstraints.BOTH;
		gbcActionsPanel.weightx = 1;
		gbcActionsPanel.weighty = 1;
		gbcActionsPanel.anchor = GridBagConstraints.NORTH;
		gbActionsPanel.setConstraints( scpList0, gbcActionsPanel );
		actionsPanel.add( scpList0 );

		addActionBtn = new JButton( "Add Action"  );
		addActionBtn.setActionCommand("addAction");
		addActionBtn.addActionListener(listener);
		gbcActionsPanel.gridx = 0;
		gbcActionsPanel.gridy = 1;
		gbcActionsPanel.gridwidth = 1;
		gbcActionsPanel.gridheight = 1;
		gbcActionsPanel.fill = GridBagConstraints.BOTH;
		gbcActionsPanel.weightx = 1;
		gbcActionsPanel.weighty = 0;
		gbcActionsPanel.anchor = GridBagConstraints.NORTH;
		gbActionsPanel.setConstraints( addActionBtn, gbcActionsPanel );
		actionsPanel.add( addActionBtn );

		removeActionBtn = new JButton( "Remove Action"  );
		removeActionBtn.addActionListener(listener);
		removeActionBtn.setActionCommand("removeAction");
		gbcActionsPanel.gridx = 1;
		gbcActionsPanel.gridy = 1;
		gbcActionsPanel.gridwidth = 1;
		gbcActionsPanel.gridheight = 1;
		gbcActionsPanel.fill = GridBagConstraints.BOTH;
		gbcActionsPanel.weightx = 1;
		gbcActionsPanel.weighty = 0;
		gbcActionsPanel.anchor = GridBagConstraints.NORTH;
		gbActionsPanel.setConstraints( removeActionBtn, gbcActionsPanel );
		actionsPanel.add( removeActionBtn );
		gbcPanel0.gridx = 0;
		gbcPanel0.gridy = 1;
		gbcPanel0.gridwidth = 1;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( actionsPanel, gbcPanel0 );
		pnPanel0.add( actionsPanel );

		conditionsPanel = new JPanel();
		conditionsPanel.setBorder( BorderFactory.createTitledBorder( "Conditions" ) );
		GridBagLayout gbConditionsPanel = new GridBagLayout();
		GridBagConstraints gbcConditionsPanel = new GridBagConstraints();
		conditionsPanel.setLayout( gbConditionsPanel );

		this.conditionsModel = new ConditionsTableModel();
		
		
		conditionsTable = new JTable(this.conditionsModel);
		JScrollPane jsp = new JScrollPane(conditionsTable);
		gbcConditionsPanel.gridx = 0;
		gbcConditionsPanel.gridy = 0;
		gbcConditionsPanel.gridwidth = 2;
		gbcConditionsPanel.gridheight = 1;
		gbcConditionsPanel.fill = GridBagConstraints.BOTH;
		gbcConditionsPanel.weightx = 1;
		gbcConditionsPanel.weighty = 1;
		gbcConditionsPanel.anchor = GridBagConstraints.NORTH;
		gbConditionsPanel.setConstraints( jsp, gbcConditionsPanel );
		
		conditionsPanel.add( jsp );

		addConditionBtn = new JButton( "Add Condition"  );
		addConditionBtn.addActionListener(listener);
		addConditionBtn.setActionCommand("addCondition");
		gbcConditionsPanel.gridx = 0;
		gbcConditionsPanel.gridy = 1;
		gbcConditionsPanel.gridwidth = 1;
		gbcConditionsPanel.gridheight = 1;
		gbcConditionsPanel.fill = GridBagConstraints.BOTH;
		gbcConditionsPanel.weightx = 1;
		gbcConditionsPanel.weighty = 0;
		gbcConditionsPanel.anchor = GridBagConstraints.NORTH;
		gbConditionsPanel.setConstraints( addConditionBtn, gbcConditionsPanel );
		conditionsPanel.add( addConditionBtn );

		removeConditionBtn = new JButton( "Remove Condition"  );
		removeConditionBtn.addActionListener(listener);
		removeConditionBtn.setActionCommand("removeCondition");
		gbcConditionsPanel.gridx = 1;
		gbcConditionsPanel.gridy = 1;
		gbcConditionsPanel.gridwidth = 1;
		gbcConditionsPanel.gridheight = 1;
		gbcConditionsPanel.fill = GridBagConstraints.BOTH;
		gbcConditionsPanel.weightx = 1;
		gbcConditionsPanel.weighty = 0;
		gbcConditionsPanel.anchor = GridBagConstraints.NORTH;
		gbConditionsPanel.setConstraints( removeConditionBtn, gbcConditionsPanel );
		conditionsPanel.add( removeConditionBtn );
		gbcPanel0.gridx = 1;
		gbcPanel0.gridy = 1;
		gbcPanel0.gridwidth = 1;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( conditionsPanel, gbcPanel0 );
		pnPanel0.add( conditionsPanel );

		btnSave = new JButton( "Save"  );
		btnSave.addActionListener(listener);
		btnSave.setActionCommand("saveResource");
		gbcPanel0.gridx = 1;
		gbcPanel0.gridy = 2;
		gbcPanel0.gridwidth = 1;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( btnSave, gbcPanel0 );
		pnPanel0.add( btnSave );

		btnDiscard = new JButton( "Discard"  );
		btnDiscard.setActionCommand("discard");
		btnDiscard.addActionListener(listener);
		gbcPanel0.gridx = 0;
		gbcPanel0.gridy = 2;
		gbcPanel0.gridwidth = 1;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( btnDiscard, gbcPanel0 );
		pnPanel0.add( btnDiscard );

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		setContentPane( pnPanel0 );
		pack();
		setVisible( true );
	}

	public void addAction(ActionConstants action, Boolean optional){
		Vector row = new Vector();
		row.add(action);
		row.add(optional);
		this.actionsModel.addRow(row);
		this.actionsTable.setModel(actionsModel);
	
		/*
		ListModel model = this.actionsModel.getModel();
		ArrayList<ActionConstants> actions = new ArrayList<ActionConstants>();
		actions.add(action);
		for (int i=0; i<model.getSize(); i++){
			actions.add((ActionConstants) model.getElementAt(i));
			
		}
		this.actionsModel.setListData(actions.toArray());
		*/
	}

	public void removeSelectedAction(){
		int index = this.actionsTable.getSelectedRow();
		if (index >=0){
			this.actionsModel.removeRow(index);
		}else{
			JOptionPane.showMessageDialog(this, "Select an Action to remove");
		}
		/*
		int index = this.actionsModel.getSelectedIndex();
		System.out.println("Action index: "+index);
		if (index>=0){
			ListModel model = this.actionsModel.getModel();
			ArrayList<ActionConstants> actions = new ArrayList<ActionConstants>();
			for (int i=0; i<model.getSize(); i++){
				ActionConstants element  = (ActionConstants) model.getElementAt(i);
				if (element.compareTo((ActionConstants) model.getElementAt(index))!=0){
					System.out.println("if element: "+element.toString()+" compareTo "+model.getElementAt(index));
					actions.add(element);
				}
			}
			this.actionsModel.setListData(actions.toArray());
		}else{
			JOptionPane.showMessageDialog(this, "Select an Action to remove");
		}*/
	}
	public void addCondition(ConditionConstants condition, String value, Boolean b){
		Vector row = new Vector();
		row.add(condition);
		row.add(value);
		row.add(b);
		this.conditionsModel.addRow(row);
		this.conditionsTable.setModel(conditionsModel);
	}
	
	public void removeSelectedCondition(){
		int index = this.conditionsTable.getSelectedRow();
		if (index>=0){
			this.conditionsModel.removeRow(index);
		}else{
			JOptionPane.showMessageDialog(this, "Select a Condition to remove");
		}
	
	}
	public RequestItem getRequestItem(){
		String resourceType = this.resourceTypeTxtField.getText().trim();
		if (resourceType==null){
			JOptionPane.showMessageDialog(this, "Please enter a resource type");
			return null;
		}
		if (resourceType.equals("")){
			JOptionPane.showMessageDialog(this, "Please enter a resource type");
			return null;
		}
		
		ArrayList<Action> actions = new ArrayList<Action>();
		for (int i = 0; i<this.actionsModel.getRowCount(); i++){
			ActionConstants ac = (ActionConstants) this.actionsModel.getValueAt(i, 0);
			Boolean optional = (Boolean) this.actionsModel.getValueAt(i,1);
			Action a = new Action(ac,optional.booleanValue());
			actions.add(a);
		}
		/*
		ListModel actionModel = this.actionsModel.getModel();
		if (actionModel.getSize()==0){
			JOptionPane.showMessageDialog(this, "Please add at least one Action");
			return null;
		}
		
		
		ArrayList<Action> actions = new ArrayList<Action>();
		for (int i=0; i<actionModel.getSize(); i++){
			ActionConstants ac = (ActionConstants) actionModel.getElementAt(i);
			actions.add(new Action(ac));
		}*/
		Resource resource = new Resource(resourceType);
		
		ArrayList<Condition> conditions = new ArrayList<Condition>();
		for (int i=0; i<this.conditionsModel.getRowCount(); i++){
			ConditionConstants cc = (ConditionConstants) this.conditionsModel.getValueAt(i, 0);
			String value = (String) this.conditionsModel.getValueAt(i,1);
			Boolean optional = (Boolean) this.conditionsModel.getValueAt(i,2);
			Condition condition = new Condition(cc,value, optional);
			conditions.add(condition);
		}
		RequestItem item = new RequestItem(resource, actions, conditions);
		return item;
	
	}
	
/*	public Action getSelectedAction(){
		int index = this.actionsModel.getSelectedIndex();
		ActionConstants ac = (ActionConstants) this.actionsModel.getModel().getElementAt(index);
		return new Action(ac);
		
	}
	
*/
} 
