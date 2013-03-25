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
package org.societies.context.location.management.coordinates.convertor;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.context.location.management.coordinates.convertor.Point;
import org.societies.context.location.management.coordinates.convertor.PointPair;

public class Pix2Geo {
	
	private double pzX1,pzY1;
	private double gpsX1,gpsY1;
	
	private double pzX2,pzY2;
	private double gpsX2,gpsY2;
	
	private double pzX3,pzY3;
	private double gpsX3,gpsY3;
	
	private double A,B,C,D,E,F;
	
	/** The logging facility. */
	private static final Logger log = LoggerFactory.getLogger(Pix2Geo.class);
	
	/**
	 * @pre pointPairArr.length()==4 
	 *      pontPairArr[0]->Lower-Left point
	 *      pontPairArr[1]->Lower-Right point
	 *      pontPairArr[2]->Upper-Left point
	 *      pontPairArr[3]->Upper-Right point
	 * @param pointPairArr
	 * @param mapName
	 * @throws Exception 
	 */
	public Pix2Geo(JSONObject jsonPoints) throws Exception {
		
		PointPair[] pointPairArr;
		try {
			pointPairArr = json2Array(jsonPoints);
			pzX1 = pointPairArr[0].getPixelPoint().getX();
			pzY1 = pointPairArr[0].getPixelPoint().getY();
			gpsX1 = pointPairArr[0].getGeoPoint().getX();
			gpsY1 = pointPairArr[0].getGeoPoint().getY();
			
			pzX2 = pointPairArr[1].getPixelPoint().getX();
			pzY2 = pointPairArr[1].getPixelPoint().getY();
			gpsX2 = pointPairArr[1].getGeoPoint().getX();
			gpsY2 = pointPairArr[1].getGeoPoint().getY();
			
			pzX3 = pointPairArr[2].getPixelPoint().getX();
			pzY3 = pointPairArr[2].getPixelPoint().getY();
			gpsX3 = pointPairArr[2].getGeoPoint().getX();
			gpsY3 = pointPairArr[2].getGeoPoint().getY();
			
			A = (-pzY3*gpsX1+pzY2*gpsX1-pzY2*gpsX3-gpsX2*pzY1+gpsX2*pzY3+gpsX3*pzY1)/
					(pzX3*pzY1-pzX2*pzY1+pzX2*pzY3-pzX1*pzY3+pzX1*pzY2-pzX3*pzY2);
			B = -(-pzX3*gpsX1+pzX1*gpsX3+pzX2*gpsX1-pzX2*gpsX3-pzX1*gpsX2+pzX3*gpsX2)/
					(pzX3*pzY1-pzX2*pzY1+pzX2*pzY3-pzX1*pzY3+pzX1*pzY2-pzX3*pzY2);
			C = (-gpsX3*pzX2*pzY1+pzY2*pzX1*gpsX3-pzY2*pzX3*gpsX1+gpsX2*pzX3*pzY1+pzY3*pzX2*gpsX1-pzY3*pzX1*gpsX2)/
					(pzX3*pzY1-pzX2*pzY1+pzX2*pzY3-pzX1*pzY3+pzX1*pzY2-pzX3*pzY2);
			D = (-pzY3*gpsY1+pzY2*gpsY1-pzY2*gpsY3-gpsY2*pzY1+gpsY2*pzY3+gpsY3*pzY1)/
					(pzX3*pzY1-pzX2*pzY1+pzX2*pzY3-pzX1*pzY3+pzX1*pzY2-pzX3*pzY2);
			E = -(-pzX3*gpsY1+pzX1*gpsY3+pzX2*gpsY1-pzX2*gpsY3-pzX1*gpsY2+pzX3*gpsY2)/
					(pzX3*pzY1-pzX2*pzY1+pzX2*pzY3-pzX1*pzY3+pzX1*pzY2-pzX3*pzY2);
			F = (-gpsY3*pzX2*pzY1+pzY2*pzX1*gpsY3-pzY2*pzX3*gpsY1+gpsY2*pzX3*pzY1+pzY3*pzX2*gpsY1-pzY3*pzX1*gpsY2)/
					(pzX3*pzY1-pzX2*pzY1+pzX2*pzY3-pzX1*pzY3+pzX1*pzY2-pzX3*pzY2);
			
			
			log.info("in Pix2Geo Ctor values are - A="+A+" ; B="+B+" ; C="+C+" ; D="+D+" ; E="+E+" ; F="+F);
			
		} catch (JSONException e) {
			log.error("JSONException in Ctor; Exception msg: "+e.getMessage()+"\t ; cause: "+e.getCause());
			throw new Exception(e);
		} catch (Exception e) {
			log.error("Exception in Ctor; Exception msg: "+e.getMessage()+"\t ; cause: "+e.getCause());
			throw new Exception(e);
		}
	}
	
	public Point convertPixToGeo(Point point){
		double convertedX = A*point.getX() + B*point.getY() + C;
		double convertedY = D*point.getX() + E*point.getY() + F;
		
		Point convertedPoint = new Point(convertedX,convertedY);
		
		log.debug("converting point x="+point.getX()+"\t y="+point.getY() + "\t -->  x="+convertedPoint.getX()+ "\t y="+convertedPoint.getY());
		return convertedPoint;
		
	}
	
	private  PointPair[] json2Array(JSONObject jsonPoint) throws JSONException {
		PointPair[] arr = new PointPair[4];
		arr[0] = new PointPair();
		arr[1] = new PointPair();
		arr[2] = new PointPair();
		arr[3] = new PointPair();

		arr[0].getPixelPoint().setX(jsonPoint.getDouble("pzX1"));
		arr[0].getPixelPoint().setY(jsonPoint.getDouble("pzY1"));
		arr[0].getGeoPoint().setX(jsonPoint.getDouble("gpsX1"));
		arr[0].getGeoPoint().setY(jsonPoint.getDouble("gpsY1"));
		arr[1].getPixelPoint().setX(jsonPoint.getDouble("pzX2"));
		arr[1].getPixelPoint().setY(jsonPoint.getDouble("pzY2"));
		arr[1].getGeoPoint().setX(jsonPoint.getDouble("gpsX2"));
		arr[1].getGeoPoint().setY(jsonPoint.getDouble("gpsY2"));
		arr[2].getPixelPoint().setX(jsonPoint.getDouble("pzX3"));
		arr[2].getPixelPoint().setY(jsonPoint.getDouble("pzY3"));
		arr[2].getGeoPoint().setX(jsonPoint.getDouble("gpsX3"));
		arr[2].getGeoPoint().setY(jsonPoint.getDouble("gpsY3"));
		arr[3].getPixelPoint().setX(jsonPoint.getDouble("pzX4"));
		arr[3].getPixelPoint().setY(jsonPoint.getDouble("pzY4"));
		arr[3].getGeoPoint().setX(jsonPoint.getDouble("gpsX4"));
		arr[3].getGeoPoint().setY(jsonPoint.getDouble("gpsY4"));
		
		String logMsg="Point Pair initialization\n";
		for (PointPair pointPair : arr){
			logMsg += pointPair.toString() +"\n";
		}
		log.info(logMsg);
		
		return arr;
	}
		
}
