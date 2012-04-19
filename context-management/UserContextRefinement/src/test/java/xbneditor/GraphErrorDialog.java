package xbneditor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
  * This is the class that displays a dialog which tells the user how many
  * addition, deletion, and reversal errors have been found in the graph
  * compared to the GoldStandard graph.
  *
  * @author Laura Kruse
  * @version v1.0
  */
public class GraphErrorDialog {
	private JDialog dialog;

	private JButton ok;

	private JLabel addition;
	private JLabel deletion;
	private JLabel reversal;

	/**
	  * Creates a new graph, with a specifed parent frame.
	  *
	  * @param frame the parent fram that is displaying this dialog
	  * @param modal a boolean stating whether the dialog is modal or not
	  */
	public GraphErrorDialog(JFrame frame, boolean modal) {
		dialog = new JDialog(frame);

		ok = new JButton("Ok");

		addition = new JLabel();
		deletion = new JLabel();
		reversal = new JLabel();

		dialog.setTitle("Graph Errors");
		dialog.getContentPane().setLayout(new GridLayout(4, 1));
		dialog.setModal(modal);

		dialog.getContentPane().add(addition);
		dialog.getContentPane().add(deletion);
		dialog.getContentPane().add(reversal);
		dialog.getContentPane().add(ok);

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});

		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dialog.dispose();
			}
		});
	}

	/**
	  * Sets the string that would represent the number of addition
	  * erros that this graph contains.
	  *
	  * @param errors the string stating the number of addition errors
	  * 	that this graph had.
	  */
	public void setAddition(String errors) {
		addition.setText("Addition:  " + errors);
	}

	/**
	  * Sets the string that would represent the number of deletion
	  * errors that this graph contains.
	  *
	  * @param errors the string stating the number of deletion errors
	  * 	that this graph had.
	  */
	public void setDeletion(String errors) {
		deletion.setText("Deletion:  " + errors);
	}

	/**
	  * Sets the string that would represent the number of reversal
	  * errors that this graph contains.
	  *
	  * @param errors the string stating the number of reversal errors
	  * 	that this graph had.
	  */
	public void setReversal(String errors) {
		reversal.setText("Reversal:  " + errors);
	}

	/**
	  * Makes the dialog visible.
	  */
	public void show() {
		dialog.pack();
		dialog.show();
	}
}
