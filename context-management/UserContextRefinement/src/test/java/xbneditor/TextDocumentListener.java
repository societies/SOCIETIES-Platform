package xbneditor;

import javax.swing.text.*;
import javax.swing.event.*;

/**
  * This class is an extension of a DocumentListener.  It is specifically
  * used to recongize when a text area has been changed and the updates
  * with the corresponding changes that have been made.
  *
  * @author Laura Kruse
  * @version v1.0
  */
public class TextDocumentListener implements DocumentListener {
	private Block block;

	/**
	  * Constructs a new TextDocumentListener that is listening for
	  * changes to a specific Block name.
	  *
	  * @param block the block that might have its name changed
	  */
	public TextDocumentListener(Block block) {
		this.block = block;
	}

	/**
	  * This is an overriden method that is inherited from the
	  * DocumentListener interface.  It reconcgizes when something
	  * has been inserted into the string.
	  *
	  * @param e the DocumentEvent that has occured
	  */
	public void insertUpdate(DocumentEvent e) {
		updateNodeName(e);
	}

	/**
	  * This is an overriden method that is inherited from the
	  * DocumentListener interface.  It reconcgizes when something
	  * has been deleted from the string.
	  *
	  * @param e the DocumentEvent that has occured
	  */
	public void removeUpdate(DocumentEvent e) {
		updateNodeName(e);
	}

	/**
	  * Another method that is overridden, this function is
	  * never called in the course of this program.
	  *
	  * @param e the DocumentEvent that has occured
	  */
	public void changedUpdate(DocumentEvent e) {
		// This is never called because we are dealing with
		// only a simple text field.
	}

	/**
	  * This function is called when the string has been modified in
	  * some way, either by insertions or deletions.  It then will
	  * updated the Block name.
	  *
	  * @param e the DocumentEvent that has occured
	  */
	private void updateNodeName(DocumentEvent e) {
		Document doc = e.getDocument();
		int length = doc.getLength();
		try {
			block.setBlockName(doc.getText(0,length));
		} catch (BadLocationException be) {
			be.printStackTrace();
		}
	}
}
