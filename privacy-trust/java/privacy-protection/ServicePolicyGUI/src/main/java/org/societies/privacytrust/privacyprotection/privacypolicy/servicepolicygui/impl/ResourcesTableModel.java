/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske dru�be in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVA��O, SA (PTIN), IBM Corp., 
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
package org.societies.privacytrust.privacyprotection.privacypolicy.servicepolicygui.impl;

import java.io.PrintStream;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

public class ResourcesTableModel extends AbstractTableModel
{
	private Vector<Vector> data = new Vector(); private String[] columnNames = { "Resource", "Actions", "Conditions", "Optional" };

	public int getColumnCount()
	{
		return this.columnNames.length;
	}

	public int getRowCount()
	{
		return this.data.size();
	}
	public String getColumnName(int col) {
		return this.columnNames[col];
	}

	public Object getValueAt(int row, int col) {
		Vector rowVector = (Vector)this.data.get(row);
		return rowVector.get(col);
	}

	public Class getColumnClass(int c)
	{
		if (c == 4) {
			return Boolean.class;
		}
		return getValueAt(0, c).getClass();
	}

	public boolean isCellEditable(int row, int col)
	{
		if (col==3) {
			return true;
		}
		return false;
	}

	public void setValueAt(Object value, int row, int col)
	{
		Vector rowVector = (Vector)this.data.get(row);
		rowVector.set(col, value);
		fireTableCellUpdated(row, col);
	}

	public void addRow(Vector row)
	{
		this.data.add(row);
		for (int i = 0; i < row.size(); i++) {
			System.out.println(row.get(i));
		}

		fireTableDataChanged();
	}

	public void removeRow(int row) {
		this.data.remove(row);
		fireTableDataChanged();
	}
}

/* Location:           C:\Users\Eliza\java decompiler\
 * Qualified Name:     org.societies.privacytrust.privacyprotection.privacypolicy.servicepolicygui.impl.ResourcesTableModel
 * JD-Core Version:    0.6.2
 */