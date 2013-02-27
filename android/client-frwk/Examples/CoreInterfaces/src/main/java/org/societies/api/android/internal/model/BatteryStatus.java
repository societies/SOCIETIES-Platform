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
package org.societies.api.android.internal.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Describe a battery status
 * @author Olivier (Trialog)
 *
 */
public class BatteryStatus implements Parcelable {
	private double level;
	private int scale;
	private double voltage;
	private double temperature;
	private int status;
	private int plugged;
	
	/**
	 * Constructor
	 * @param name
	 * @param enabled
	 */
	public BatteryStatus(double level, int scale, double voltage, double temperature, int status, int plugged) {
		super();
		this.level = level;
		this.scale = scale;
		this.voltage = voltage;
		this.temperature = temperature;
		this.status = status;
		this.plugged = plugged;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BatteryStatus [level=" + level + "%, scale=" + scale
				+ ", voltage=" + voltage + "V, temperature=" + temperature
				+ "°C, status=" + status + ", plugged=" + plugged + "]";
	}


	/**
	 * @return the level
	 */
	public double getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(double level) {
		this.level = level;
	}

	/**
	 * @return the scale
	 */
	public int getScale() {
		return scale;
	}

	/**
	 * @param scale the scale to set
	 */
	public void setScale(int scale) {
		this.scale = scale;
	}

	/**
	 * @return the voltage
	 */
	public double getVoltage() {
		return voltage;
	}

	/**
	 * @param voltage the voltage to set
	 */
	public void setVoltage(double voltage) {
		this.voltage = voltage;
	}

	/**
	 * @return the temperature
	 */
	public double getTemperature() {
		return temperature;
	}

	/**
	 * @param temperature the temperature to set
	 */
	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the plugged
	 */
	public int getPlugged() {
		return plugged;
	}

	/**
	 * @param plugged the plugged to set
	 */
	public void setPlugged(int plugged) {
		this.plugged = plugged;
	}
	

	/* ************************
	 * Parcelable Management
	 * ************************ */
	
	public BatteryStatus(Parcel in) {
		readFromParcel(in);
	}
	
	/*
	 * @see android.os.Parcelable#describeContents()
	 */
	public int describeContents() {
		return 0;
	}

	/*
	 * 
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	public void writeToParcel(Parcel dest, int flag) {
		dest.writeDouble(level);
		dest.writeInt(scale);
		dest.writeDouble(voltage);
		dest.writeDouble(temperature);
		dest.writeInt(status);
		dest.writeInt(plugged);
	}
	
	private void readFromParcel(Parcel in) {
		level = in.readDouble();
		scale = in.readInt();
		voltage = in.readDouble();
		temperature = in.readDouble();
		status = in.readInt();
		plugged = in.readInt();
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public BatteryStatus createFromParcel(Parcel in) {
			return new BatteryStatus(in);
		}

		public BatteryStatus[] newArray(int size) {
			return new BatteryStatus[size];
		}
	};
}
