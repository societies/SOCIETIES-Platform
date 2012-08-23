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
package org.societies.privacytrust.privacyprotection.privacypreferencemanager.gui.privacy.PPN;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPreferenceDetails;

public class PPNPreferenceDetailsTable extends AbstractTableModel{

	private List<PPNPreferenceDetails> details = new ArrayList<PPNPreferenceDetails>();
	private String[] columnNames = {"Context Type", "ICtxIdentifier", "Provider Identity", "CIS ID or ServiceID"};
	public PPNPreferenceDetailsTable(List<PPNPreferenceDetails> initDetails){
		super();
		
		for (PPNPreferenceDetails d: initDetails){
			this.addRow(d);
		}
	}
	@Override
	public String getColumnName(int column) {
		return this.columnNames[column];
	}
	
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return details.size();
	}

	@Override
	public Class getColumnClass(int c){
		return String.class;
	}
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		PPNPreferenceDetails d = this.details.get(rowIndex);
		
		if (columnIndex==0){
			return d.getDataType();
		}
		
		if (columnIndex == 1){
			if (d.getAffectedDataId()!=null){
				return d.getAffectedDataId();
			}else{
				return "";
			}
		}
		
		if (columnIndex==2){
			if (d.getRequestor().getRequestorId()!=null){
				return d.getRequestor().getRequestorId();
			}
			else{
				return "";
			}
		}
		
		if (columnIndex==3){
			Requestor requestor = d.getRequestor();
			if (requestor instanceof RequestorService){
				return ((RequestorService) requestor).getRequestorServiceId();
			}
			
			if (requestor instanceof RequestorCis){
				return ((RequestorCis) requestor).getCisRequestorId();
			}
				return "";
		}
		return new String("");
	}

	public void setValueAt(PPNPreferenceDetails value, int row, int col){
		PPNPreferenceDetails d = this.details.get(row);
		
		if (col==0){
			d.setDataType(value.getDataType());
			fireTableCellUpdated(row, col);
		}
		
		if (col==1){
			if (value.getAffectedDataId()!=null){
				d.setAffectedDataId(value.getAffectedDataId());
				fireTableCellUpdated(row, col);
			}
		}
		
		if (col==2){
			if (value.getRequestor()!=null){
				d.setRequestor(value.getRequestor());
				fireTableCellUpdated(row, col);
			}
		}
		
		if (col==3){
			if (value.getRequestor()!=null){
				d.setRequestor(value.getRequestor());
				fireTableCellUpdated(row, col);
			}
		}
	}
    public void addRow(PPNPreferenceDetails d){
    	//System.out.println("addRow("+d.toString()+")");
    	this.details.add(d);
    	this.fireTableDataChanged();
    }
    public void removeRow(int row){
    	this.details.remove(row);
    	this.fireTableDataChanged();
    }
    
    public PPNPreferenceDetails getRow(int row){
    	return this.details.get(row);
    }
}
