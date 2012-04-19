package xbneditor;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
  * This is a custom built dialog box that lets the user have three options
  * on whether they want to save, or not save, or cancel the operation.
  * The only difference between cancel and no is that no will continue with
  * the normal execution of the program and cancel will stop whatever process
  * that used the dialog.
  *
  * @author Laura Kruse
  * @version 1.1
  */
public class SaveDialog {
	private JDialog dialog;

	private JButton yes;
	private JButton no;
	private JButton cancel;

	private int choice;

	/**
	  * Creates a new dialog with a specific parent, file that is needing
	  * to be save, and the option of having a modal dialog or not.
	  *
	  * @param frame the parent frame that called this
	  * @param filename the name of the file that should be saved
	  * @param modal states whether the dialog must be closed before the
	  *	other windows can have focus back or not
	  */
	public SaveDialog(JFrame frame, String filename, boolean modal) {
		JLabel save;

		JPanel north;
		JPanel south;

		dialog = new JDialog(frame);

		yes = new JButton("Yes");
		no = new JButton("No");
		cancel = new JButton("Cancel");

		save = new JLabel("Save network " + filename + "?");

		north = new JPanel();
		south = new JPanel();

		dialog.setTitle("Bayesion Network Not Saved");
		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.setModal(modal);

		north.add(save);

		south.add(yes);
		yes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				choice = 1;
				dialog.dispose();
			}
		});

		south.add(no);
		no.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				choice = 2;
				dialog.dispose();
			}
		});

		south.add(cancel);
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				choice = 3;
				dialog.dispose();
			}
		});

		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				choice = 3;
				dialog.dispose();
			}
		});

		dialog.getContentPane().add(north, "North");
		dialog.getContentPane().add(south, "South");

		dialog.pack();

		// Centering the Dialog on it's parent frame.
		Point point;

		Dimension big;
		Dimension small;

		int x;
		int y;

		point  = frame.getLocationOnScreen();
		big = frame.getSize();
		small = dialog.getSize();

		x = point.x + (big.width - small.width) / 2;
		y = point.y + (big.height - small.height) / 2;

		dialog.setLocation(x, y);
	}

	/**
	  * Brings up the dialog to be seen.
	  */
	public void show() {
		dialog.show();
	}

	/**
	  * Returns an int value that represents what choice the user
	  * seleceted.
	  *
	  * @return choice - the users selection
	  */
	public int value() {
		return choice;
	}
}
