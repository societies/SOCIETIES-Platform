package xbneditor;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
  * This class inherits all of the functionality of the Block
  * class.&nbsp;It is then specalized to be a probability node, and
  * thus it holds the names and values associated with different
  * probabilities.
  *
  * @author Laura Kruse
  * @version v1.3
  */
public class ChanceBlock extends Block {
	private LinkedList varnames = new LinkedList();
	private LinkedList varvalues = new LinkedList();
	private double[][] values;

	private int num;
	private int numcolumns;
	private int rows;
	private int columns;

	/**
	  * An empty constructor.
	  */
	public ChanceBlock() {
	}

	/**
	  * Constructs a new Chance block with a given name
	  * and location.
	  *
	  * @param name the name of this chance block
	  * @param x the inital x location of this block
	  * @param y the inital y location of this block
	  * @param innew a boolean that tells if this is a node that is
	  *	being loaded from a file or if it is one the user has
	  *	created
	  */
	public ChanceBlock(String name, double x, double y, boolean
isnew) {
		setBlockName(name);
		setType("nature");
		setCoordinates(x,y);
		values = new double[5][8];
		if(isnew) {
			num = 1;
			numcolumns = 1;
			values[0][0] = 0.0;
			varnames.add("state0");
			varvalues.add(new Double(0.0));
		} else {
			num = 0;
			numcolumns = 0;
		}
		rows = 5;
		columns = 8;
	}

	/**
	  * Returns the number of states this node has, it does the
	  * same thing that getRows does
	  *
	  * @return num - the number states this block is associated with
	  * @see ChanceBlock:getRows
	  */
	public int numAttributes() {
		return num;
	}

	/**
	  * Returns a LinkedList of the state names
	  *
	  * @return varnames - a LinkedList that contains the state names
	  */
	public LinkedList getAttributeNames() {
		return varnames;
	}

	/**
	  * Returns a LinkedList of the state values
	  *
	  * @return varvalues - a LinkeList that contains the state values
	  */
	public LinkedList getAttributeValues() {
		return varvalues;
	}

	/**
	  * Sets the name of a state at a specific location in the
	  * LinkedList of names.
	  *
	  * @param name the name to be set
	  * @param loc the location in the LinkedList to set the name to
	  */
	public void setAttributeName(String name, int loc) {
		varnames.set(loc,name);
	}

	/**
	  * Sets the value of a state at a specific location in the
	  * LinkedList of values.
	  *
	  * @param value the value to be set
	  * @param loc the location in the LinkedList to set the value to
	  */
	public void setAttributeValue(Double value, int loc) {
		varvalues.set(loc,value);
	}

	/**
	  * Adds a new state to the current node.
	  *
	  * @see add
	  */
	public void add() {
		add(new String("state"+num));
	}

	/**
	  * Adds a new state to the node.  This method is only called directly
	  * when a file is being loaded into the editor.
	  *
	  * @param name the name of the state that is being added
	  * @see add
	  */
	public void add(String name) {
		varnames.add(name);
		varvalues.add(new Double(0.0));

		if(num<rows) {
			values[num][0] = 0.0;
		} else {
			double tmp[][] = new double[2*rows][columns];
			for(int i=0;i<rows;i++) {
				for(int j=0;j<columns;j++) {
					tmp[i][j] = values[i][j];
				}
			}
			for(int z=0;z<numcolumns;z++) {
				tmp[num][z] = 0.0;
			}
			values = tmp;
			rows*=2;
		}

		num++;
	}

	/**
	  * Deletes the state at the corresponding location in the
	  * LinkedList.
 	  *
	  * @param loc the location of the state to delete
	  */
	public void delete(int loc) {
		varnames.remove(loc);
		varvalues.remove(loc);
		num--;
	}

	/**
	  * Stores all of the probabilites of the corresponding linked list
	  *
	  * @param values a two dimensinal array in which the rows
	  *	represent the number of attributes that this particular
	  *	node has, the columns represent the number of possible
	  *	combinations that these can be recongized in
	  * @param across the number of rows across in the array
	  */
	public void setValues(double[][] values, int columns) {
		this.values = values;
		this.columns = columns;
	}

	/**
	  * Gets the number of rows that the array of probabilities is using,
	  * this does the same thing as numAttributes.
	  *
	  * @return num - the number of attributes this node has
	  * @see ChanceBlock:numAttributes
	  */
	public int getRows() {
		return num;
	}

	/**
	  * Gets the number of columns that the array of probabilities
	  * is currently using.
	  *
	  * @return numcolumns - the number of columns that are filled in
	  */
	public int getColumns() {
		return numcolumns;
	}

	/**
	  * Returns the total number of columns in the array that this
	  * particular ChanceBlock has access to.
	  *
	  * @return columns - the number of columns in the array
	  */
	public int getTotalColumns() {
		return columns;
	}

	/**
	  * Changes how many columns in the probability table.
	  *
	  * @param columns the number that are currently being used
	  */
	public void setColumns(int numcolumns) {
		this.numcolumns = numcolumns;
	}

	/**
	  * Gets the corresponding value in the probability table
	  *
	  * @param irow the row the value is in
	  * @param icolumn the column the value is in
	  * @return values[irow][icolumn] - the value at this location
	  */
	public double getValue(int irow, int icolumn) {
		return values[irow][icolumn];
	}

	/**
	  * Sets the corresponding value in the probability table to
	  * the value that is being passed in.  This function is called
	  * when the Edit Panes recongizes that something has been updated.
	  *
	  * @param item the new value of the table
	  * @param irow the row that corresponds to the new value
	  * @param icolumn the column that corresponds to the new value
	  */
	public void setValue(double item, int irow, int icolumn) {
		if(icolumn >= columns) {
			//double tmp[][] = new double[rows][2*columns];
			double tmp[][] = new double[rows][2*icolumn];
			for(int i=0;i<rows;i++) {
				for(int j=0;j<columns;j++) {
					tmp[i][j] = values[i][j];
					//System.out.print(tmp[i][j] + "\t");
				}
				//System.out.println();
			}
			values = tmp;
			//columns*=2;
			columns = 2 * icolumn;
		}

		if(icolumn >= numcolumns) {
			numcolumns = icolumn + 1;
		}

		//System.out.println(irow + " " + icolumn);
		values[irow][icolumn] = item;
	}

	/**
	  * This function allows the tables to shrink when a node has
	  * been deleted.
	  *
	  * @param numattributes the number of attributes that need to be
	  *	collapsed into one
	  * @param offset the number that is inbetween these attributes
	  *	that need to be shrank
	  */
	public void shrinkTable(int numattributes, int offset) {
		// will probally need to do some modification to this
		// function to make it work correctly

		if(numattributes==1) return;
		double[][] tmp = new double[rows][columns];
		numcolumns /= numattributes;
		for(int i=0;i<numcolumns;i++) {
			for(int j=0;j<num;j++) {
				for(int k=0;k<numattributes;k++) {
					tmp[j][i]+=values[j][k*offset+i];
				}
			}
		}
		values = tmp;
	}
}
