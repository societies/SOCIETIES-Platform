/**
 * Copyright 2009 PERSIST consortium
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 */
/**
 * 
 */
package org.societies.display.server.gui;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

/**
 * @author Elizabeth
 *
 */
public class ConfigurationTableModel extends AbstractTableModel{
	private Vector<Vector> data;
	private String[] columnNames = {"ScreenID",
            "Screen Location", 
            "IPADDRESS"};

	public ConfigurationTableModel(){
		super();
		this.data = new Vector<Vector>();
	}
	 public int getColumnCount() {
            return columnNames.length;
        }


		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		@Override
		public int getRowCount() {
			// TODO Auto-generated method stub
			return data.size();
		}
        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
        	Vector rowVector = (Vector) data.get(row);
            return rowVector.get(col);
        }

        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
        public Class getColumnClass(int c) {
        	
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            return true;
        }

        /*
         * Don't need to implement this method unless your table's
         * data can change.
         */
        public void setValueAt(Object value, int row, int col) {
            Vector rowVector = (Vector) data.get(row);
            rowVector.set(col, value);
            fireTableCellUpdated(row, col);
        }
        
        public void addRow(Vector row){
        	
        	this.data.add(row);
        	for (int i=0;i<row.size(); i++){
        		System.out.println(row.get(i));
        	}
        	
        	this.fireTableDataChanged();
        }
        
        public void removeRow(int row){
        	if (this.data.size()>=row){
        		this.data.remove(row);
        		this.fireTableDataChanged();
        	}
        }
		public void addEmptyRow() {
			Vector v = new Vector();
			
			for (int i = 0; i < this.columnNames.length; i++){
				v.add("");
			}
			this.addRow(v);
		}
		
		public String getScreenId(int row){
			return (String) this.getValueAt(row, 0);
		}
	
		public String getLocation(int row){
			return (String) this.getValueAt(row, 1);
		}
		
		public String getIPAddress(int row){
			return (String) this.getValueAt(row, 2);
		}
}