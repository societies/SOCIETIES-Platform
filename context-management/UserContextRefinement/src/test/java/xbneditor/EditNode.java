package xbneditor;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

/**
  * This class is responsible for the panel that displays the names
  * and values of states that correspond to each node.
  *
  * @author Laura Kruse
  * @version v1.5
  */
public class EditNode extends JPanel {
	private ChanceBlock tmp;

	private JTextField title;
	private JPanel attributes;
	private JTable table;

	private LinkedList varnames;
	private LinkedList varvalues;

	private int choice;

	/**
	  * Creates a new panel that contains all of the states of
	  * a corresponding block.
	  *
	  * @param tmp the node to be edited
	  * @param choice 0 = edit 1 = add state 2 = delete state
	  */
	public EditNode(Item item, int choice) {
		tmp = (ChanceBlock) item.getItem();
		this.choice = choice;

		if (choice==1) tmp.add();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		this.setLayout(gridbag);

		this.tmp = tmp;
		int l;
		if(tmp!=null) {
			varnames = tmp.getAttributeNames();
			varvalues = tmp.getAttributeValues();
			l = tmp.numAttributes();
		} else {
			l = 0;
		}

		String blockname = tmp.getBlockName();
		int len = blockname.length();
		if(len<5) len+=2;
		else if(len>10) len-=3;
		title = new JTextField(blockname,len);
		title.getDocument().addDocumentListener(new TextDocumentListener(tmp));

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(title,gbc);
		this.add(title);

		// Setting up the attribute list for each particular node.

		attributes = new JPanel(new GridLayout(item.numParents(),1));
		int num = setAttributeList(item,1);

		// The number of attributes that this particular Block
		// should have.

		if(num>=tmp.getTotalColumns()) {
			double array[][] = new double[tmp.getRows()][num*2];
			for(int i=0;i<tmp.getRows();i++) {
				for(int j=0;j<tmp.getColumns();j++) {
					array[i][j]=tmp.getValue(i,j);
				}
			}
			tmp.setValues(array,num*2);
			tmp.setColumns(num);
		} else if(num!=tmp.getColumns()) {
			tmp.setColumns(num);
		}

		if(attributes!=null) {
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gridbag.setConstraints(attributes,gbc);
			this.add(attributes);
		}

		// should do if table == null then create a new one,
		// otherwise should just manipulate the existing table

		if(choice==1 || choice==0) {
			// do some parent checking here
			table = new JTable(l,num+1);
		} else {
			table = new JTable(l,num+1);
		}

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(table,gbc);
		this.add(table);

		for(int i=0;i<l;i++) {
			table.setValueAt(varnames.get(i),i,0);
			for(int j=0;j<tmp.getColumns();j++) {
				table.setValueAt(new Double(tmp.getValue(i,j)),i,j+1);
			}
		}

		this.setVisible(true);
	}

	/**
	  * Checks to make sure that valid probabilities are entered
	  * as values for the states.  A probability over one is
	  * considered invalid.
	  */
	public void check() {
		if(tmp.getBlockName().length()>0) {
			int rows = tmp.getRows();
			int columns = tmp.getColumns();

			double tnum = 0;
			double cnum = 0;
			for(int i=0;i<rows;i++) {
				tmp.setAttributeName(table.getValueAt(i,0).toString(),i);
				for(int j=0;j<columns;j++) {
					// its j + 1 because all the columns are
					// shifted over one because the first
					// column contains the state name
					cnum = new Double(table.getValueAt(i,j+1).toString()).doubleValue();
					tmp.setValue(cnum,i,j);
					tnum+=cnum;
				}
			}

			tnum /= columns;

			//if(tnum>1.0 || tnum<1.0) {
			if(Math.abs(tnum - 1.0) / 1.0 > .05) {
				JOptionPane.showMessageDialog(null, "Probability of " + tmp.getBlockName() + " " + Math.abs(tnum - 1.0) / 1.0 + " does not equal one.  Data will not\ngive correct results.  Or enter wasn't pushed after\nentering new values." , "Error" , JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	  * Sets up the Panel of attribute names in the order corresponding to
	  * how they are read in.
	  *
	  * @param item the item that is currently being added
	  * @param level the level of nesting that the attributes are
	  *	begin added to
	  * @param num this / the numAttributes for a particular
	  * 	chance block tells you how many times to repeat the loop
	  */
	private int setAttributeList(Item item,int count) {
		for(int i=0;i<item.numParents();i++) {
			count=addRow(item.getParent(i),count);
		}
		return count;
	}

	/**
	  * Adds a row of labels stating what attribute that the probability
	  * depends on.
	  *
	  * @param item the item that is currently having its attributes
	  * 	displayed
	  * @param count the number of columns across
	  * @return count - an interger stating how many columns across the
	  *	the previous row of attributes was
	  */
	private int addRow(Item item, int count) {
		LinkedList states = ((ChanceBlock) item.getItem()).getAttributeNames();
		int num = ((ChanceBlock) item.getItem()).numAttributes();
		count*=num;

		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.ipadx = 4;
		constraints.fill = GridBagConstraints.BOTH;

		JPanel row = new JPanel(layout);

		//JTextField label = new JTextField(item.getItem().getBlockName(),6);
		//label.setEnabled(false);
		//layout.setConstraints(label,constraints);
		//row.add(label);

		JTable label;
		label = new JTable(1, 1);
		label.setCellSelectionEnabled(false);
		label.setRowSelectionAllowed(false);
		label.setColumnSelectionAllowed(false);
		layout.setConstraints(label, constraints);

		label.setValueAt(item.getItem().getBlockName(),0,0);
		row.add(label);

		constraints.weightx = 1;
		label = new JTable(1, count);
		label.setCellSelectionEnabled(false);
		label.setRowSelectionAllowed(false);
		label.setColumnSelectionAllowed(false);
		layout.setConstraints(label, constraints);

		for(int i=0;i<count;i++) {
			//label = new JTextField((String) states.get(i%num),6);
			//label.setEnabled(false);
			//layout.setConstraints(label,constraints);
			//row.add(label);

			label.setValueAt(states.get(i%num),0,i);
		}
		row.add(label);

		attributes.add(row);

		return count;
	}
}
