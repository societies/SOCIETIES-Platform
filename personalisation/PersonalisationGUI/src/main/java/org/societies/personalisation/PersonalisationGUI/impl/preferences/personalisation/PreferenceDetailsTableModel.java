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
package org.societies.personalisation.PersonalisationGUI.impl.preferences.personalisation;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.societies.api.internal.personalisation.model.PreferenceDetails;


public class PreferenceDetailsTableModel extends AbstractTableModel{

	private List<PreferenceDetails> details = new ArrayList<PreferenceDetails>();;
	private String[] columnNames = {"Service Type",
            "Service ID", 
            "Preference Name"};
	public PreferenceDetailsTableModel(List<PreferenceDetails> initDetails){
		super();
		
		for (PreferenceDetails d: initDetails){
			this.addRow(d);
		}
	}
	
	public String getColumnName(int column) {
		return this.columnNames[column];
	}
	public PreferenceDetailsTableModel(){
		super();
	}
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return this.details.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		PreferenceDetails d = this.details.get(rowIndex);
		
		if (columnIndex==0){
			if (d.getServiceType()==null){
				return "";
			}else{
				return d.getServiceType();
			}
		}
		
		if (columnIndex==1){
			if (d.getServiceID()==null){
				return "";
			}else{
				return d.getServiceID();
			}
		}
		
		if (columnIndex==2){
			return d.getPreferenceName();
		}
		
		return null;
	}
	
    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    public Class getColumnClass(int c) {
    	System.out.println("JavaSWING Asking for class of column: "+c);
/*    	if (c==1){
    		return IServiceIdentifier.class;
    	}*/
        return String.class;
    }

    public void addRow(PreferenceDetails d){
    	System.out.println("addRow("+d.toString()+")");
    	this.details.add(d);
    	this.fireTableDataChanged();
    }
    
    public void setValueAt(PreferenceDetails value, int row, int col) {
    	System.out.println("SetVAlueAt(row:"+row+",col:"+col+")");
    	PreferenceDetails d = this.details.get(row);
    	
    	if (col==0){
    		if (value.getServiceType()!=null){
    			d.setServiceType(value.getServiceType());
    			fireTableCellUpdated(row, col);
    		}
    		
    	}
    	
    	if (col==1){
    		if (value.getServiceID()!=null){
    			d.setServiceID(value.getServiceID());
    			fireTableCellUpdated(row, col);
    		}
    		
    	}
    	
    	if (col==2){
    		d.setPreferenceName(value.getPreferenceName());
    		fireTableCellUpdated(row, col);
    	}
        
        
    }
    
    
    public void removeRow(int row){
    	this.details.remove(row);
    	this.fireTableDataChanged();
    }
    
    public PreferenceDetails getRow(int row){
    	return this.details.get(row);
    }
	
}
