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
package org.societies.android.privacytrust.dataobfuscation.obfuscator;

import java.util.ArrayList;
import java.util.List;

import org.societies.android.api.internal.privacytrust.privacy.model.dataobfuscation.LocationCoordinatesObfuscatorInfo;
import org.societies.android.api.privacytrust.privacy.model.PrivacyException;
import org.societies.android.privacytrust.dataobfuscation.obfuscator.util.LocationCoordinates4Obfuscation;
import org.societies.android.privacytrust.dataobfuscation.obfuscator.util.LocationCoordinatesUtils;
import org.societies.android.privacytrust.dataobfuscation.obfuscator.util.RandomBetween;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.ObfuscationLevelType;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.LocationCoordinates;

import android.util.Log;

/**
 * Obfuscator of location coordinates
 *
 * @author Olivier Maridat (Trialog)
 *
 */
public class LocationCoordinatesObfuscator extends DataObfuscator<LocationCoordinates> {
	private final static String TAG = LocationCoordinatesObfuscator.class.getSimpleName();

	/**
	 * Radius enlargement operation id
	 */
	public final static int OPERATION_E = 0;
	/**
	 * Radius reduction operation id
	 */
	public final static int OPERATION_R = 1;
	/**
	 * Shift operation id
	 */
	public final static int OPERATION_S = 2;
	/**
	 * Radius enlargement and then shift operation id
	 */
	public final static int OPERATION_ES = 3;
	/**
	 * Shift and then radius enlargement operation id
	 */
	public final static int OPERATION_SE = 4;
	/**
	 * Shift and then radius reduction operation id
	 */
	public final static int OPERATION_SR = 5;

	/**
	 * Random element for random computation
	 */
	private RandomBetween rand;
	public double step = 0.1;
	public double alpha0Max = 360;
	public double precisionMax = 0.0000000000000000001;
	public int nbMaxIteration = 30;

	// -- CONSTRUCTOR
	/**
	 * @param data
	 */
	public LocationCoordinatesObfuscator(DataWrapper dataWrapper) {
		super(dataWrapper);
		obfuscatorInfo = new LocationCoordinatesObfuscatorInfo();
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.android.api.internal.privacytrust.model.dataobfuscation.obfuscator.IDataObfuscator#obfuscateData(double)
	 */
	public DataWrapper obfuscateData(double obfuscationLevel)
			throws PrivacyException {
		// -- Init
		rand = new RandomBetween();
		// -- Algorithm
		Integer obfuscationOperation = -1;
		Double middleObfuscationLevel = (double) -1;
		Double theta = (double) -1;
		LocationCoordinates obfuscatedLocationCoordinates = obfuscateLocation(data, obfuscationLevel, obfuscationOperation, middleObfuscationLevel, theta);

		// -- Return
		DataWrapper dataWrapper = new DataWrapper();
		dataWrapper.setData(obfuscatedLocationCoordinates);
		return dataWrapper;
	}

	/**
	 * Location obfuscation algorithm
	 * @param geolocation Location to obfuscate
	 * @param obfuscationLevel Obfuscation level
	 * @return obfuscated location
	 */
	private LocationCoordinates obfuscateLocation(LocationCoordinates geolocation, double obfuscationLevel, int obfuscationOperation, double middleObfuscationLevel, double theta) {
		/* ALGORITHM
		 * Select randomly an algorithm
		 * And apply it
		 */
		LocationCoordinates4Obfuscation obfuscatedLocationCoordinates = null;
		int operation;
		// Select a random operation
		if (-1 == obfuscationOperation) {
			operation = rand.nextInt(6);
		}
		// Use a defined operation
		else {
			operation = obfuscationOperation;
		}
		//		operation = 5;
		switch(operation) {
		case OPERATION_E:
			obfuscatedLocationCoordinates = EObfuscation(geolocation, obfuscationLevel);
			obfuscatedLocationCoordinates.setObfuscationAlgorithm(OPERATION_E);
			break;
		case OPERATION_R:
			obfuscatedLocationCoordinates = RObfuscation(geolocation, obfuscationLevel);
			obfuscatedLocationCoordinates.setObfuscationAlgorithm(OPERATION_R);
			break;
		case OPERATION_S:
			// SObfuscation with a random direction theta
			if (-1 == theta) {
				obfuscatedLocationCoordinates = SObfuscation(geolocation, obfuscationLevel);
			}
			// SObfuscation with a defined direction theta
			else {
				obfuscatedLocationCoordinates = SObfuscation(geolocation, obfuscationLevel, theta);
			}
			obfuscatedLocationCoordinates.setObfuscationAlgorithm(OPERATION_S);
			break;
		case OPERATION_ES:
			// ESObfuscation with a random direction theta and a random middleObfuscationLevel
			if (-1 == theta && -1 == middleObfuscationLevel) {
				obfuscatedLocationCoordinates = ESObfuscation(geolocation, obfuscationLevel);
			}
			// ESObfuscation with a random direction theta and a defined middleObfuscationLevel
			else if (-1 == theta && -1 != middleObfuscationLevel) {
				obfuscatedLocationCoordinates = ESObfuscation(geolocation, obfuscationLevel, middleObfuscationLevel);
			}
			// ESObfuscation with a defined direction theta and a random middleObfuscationLevel
			else if (-1 != theta && -1 == middleObfuscationLevel) {
				obfuscatedLocationCoordinates = ESObfuscation(geolocation, obfuscationLevel, theta);
			}
			// ESObfuscation with a defined direction theta and a middleObfuscationLevel
			else {
				obfuscatedLocationCoordinates = ESObfuscation(geolocation, obfuscationLevel, middleObfuscationLevel, theta);
			}
			obfuscatedLocationCoordinates.setObfuscationAlgorithm(OPERATION_ES);
			break;
		case OPERATION_SE:
			// SEObfuscation with a random direction theta and a random middleObfuscationLevel
			if (-1 == theta && -1 == middleObfuscationLevel) {
				obfuscatedLocationCoordinates = SEObfuscation(geolocation, obfuscationLevel);
			}
			// SEObfuscation with a defined direction theta and a middleObfuscationLevel
			else {
				obfuscatedLocationCoordinates = SEObfuscation(geolocation, obfuscationLevel, middleObfuscationLevel, theta);
			}
			obfuscatedLocationCoordinates.setObfuscationAlgorithm(OPERATION_SE);
			break;
		case OPERATION_SR:
			// SRObfuscation with a random direction theta and a random middleObfuscationLevel
			if (-1 == theta && -1 == middleObfuscationLevel) {
				obfuscatedLocationCoordinates = SRObfuscation(geolocation, obfuscationLevel);
			}
			// SRObfuscation with a defined direction theta and a middleObfuscationLevel
			else {
				obfuscatedLocationCoordinates = SRObfuscation(geolocation, obfuscationLevel, middleObfuscationLevel, theta);
			}
			obfuscatedLocationCoordinates.setObfuscationAlgorithm(OPERATION_SR);
			break;
		}
		obfuscatedLocationCoordinates.setObfuscationLevel(obfuscationLevel);
		return obfuscatedLocationCoordinates;
	}

	/**
	 * Location obfuscation algorithm
	 * by enlarging the radius
	 * @param geolocation Location to obfuscate
	 * @param obfuscationLevel Obfuscation level
	 * @preconditions obfuscationLevel > 0
	 * @return obfuscated location
	 */
	private LocationCoordinates4Obfuscation EObfuscation(LocationCoordinates geolocation, double obfuscationLevel) {
		return new LocationCoordinates4Obfuscation(geolocation.getLatitude(), geolocation.getLongitude(), geolocation.getAccuracy()/((double) Math.sqrt(obfuscationLevel)));
	}
	/**
	 * Location obfuscation algorithm
	 * by reducing the radius
	 * @param geolocation Location to obfuscate
	 * @param obfuscationLevel Obfuscation level
	 * @return obfuscated location
	 */
	private LocationCoordinates4Obfuscation RObfuscation(LocationCoordinates geolocation, double obfuscationLevel) {
		return new LocationCoordinates4Obfuscation(geolocation.getLatitude(), geolocation.getLongitude(), geolocation.getAccuracy()*((double) Math.sqrt(obfuscationLevel)));
	}
	/**
	 * Location obfuscation algorithm
	 * by shifting the centre of the geolocation circle
	 * @param geolocation Location to obfuscate
	 * @param obfuscationLevel Obfuscation level
	 * @return obfuscated location
	 */
	private LocationCoordinates4Obfuscation SObfuscation(LocationCoordinates geolocation, double obfuscationLevel) {
		// Select a random direction for the shifting
		double theta = rand.nextDouble()*360;
		return SObfuscation(geolocation, obfuscationLevel, theta);
	}
	/**
	 * Location obfuscation algorithm
	 * by shifting the centre of the geolocation circle
	 * @param geolocation Location to obfuscate
	 * @param obfuscationLevel Obfuscation level
	 * @param theta Direction of shifting
	 * @return obfuscated location
	 */
	private LocationCoordinates4Obfuscation SObfuscation(LocationCoordinates geolocation, double obfuscationLevel, double theta) {
		LocationCoordinates4Obfuscation obfuscatedLocationCoordinates = null;
		/*
		 * Resolve following system:
		 * alpha - sin(alpha) = pi*obfuscationLevel
		 * d = 2*horizontalAccuracy*cos(alpha/2)
		 */
		double alpha = solveXMoinsSinxMoinsC(Math.PI*obfuscationLevel);
		double d = 2*geolocation.getAccuracy()*Math.cos(alpha/2);
		// Shift the geolocation center by distance d and angle theta
		/*
		 * /!\ Latitude/longitude are angles, not cartesian coordinates!
		 * new latitude != latitude+d*sin(alpha)
		 * new longitude != longitude+d*cos(alpha)
		 */
		obfuscatedLocationCoordinates = LocationCoordinatesUtils.shitLatLgn(geolocation, theta, d);
		obfuscatedLocationCoordinates.setShiftDirection(theta);
		obfuscatedLocationCoordinates.setShiftDistance(d);
		obfuscatedLocationCoordinates.setShiftAlpha(alpha);

		return obfuscatedLocationCoordinates;
	}
	/**
	 * Location obfuscation algorithm
	 * by enlarging the radius and then
	 * shifting the centre of the geolocation circle
	 * @param geolocation Location to obfuscate
	 * @param obfuscationLevel Obfuscation level
	 * @return obfuscated location
	 */
	private LocationCoordinates4Obfuscation ESObfuscation(LocationCoordinates geolocation, double obfuscationLevel) {
		// Select an intermediate obfuscation level > obfuscation level
		double middleObfuscationLevel = rand.nextDoubleBetween(obfuscationLevel, 1);
		// Select a random direction for the shifting
		double theta = rand.nextDouble()*360;
		return ESObfuscation(geolocation, obfuscationLevel, middleObfuscationLevel, theta);
	}
	/**
	 * Location obfuscation algorithm
	 * by enlarging the radius and then
	 * shifting the centre of the geolocation circle
	 * @param geolocation Location to obfuscate
	 * @param obfuscationLevel Obfuscation level
	 * @param middleObfuscationLevel Middle Obfuscation level for E operation
	 * @return obfuscated location
	 */
	private LocationCoordinates4Obfuscation ESObfuscation(LocationCoordinates geolocation, double obfuscationLevel, double middleObfuscationLevel) {
		// Select a random direction for the shifting
		double theta = rand.nextDouble()*360;
		return ESObfuscation(geolocation, obfuscationLevel, middleObfuscationLevel, theta);
	}
	/**
	 * Location obfuscation algorithm
	 * by enlarging the radius and then
	 * shifting the centre of the geolocation circle
	 * @param geolocation Location to obfuscate
	 * @param obfuscationLevel Obfuscation level
	 * @param middleObfuscationLevel Obfuscation level for the enlargement
	 * @param theta Direction of shifting
	 * @return obfuscated location
	 */
	private LocationCoordinates4Obfuscation ESObfuscation(LocationCoordinates geolocation, double obfuscationLevel, double middleObfuscationLevel, double theta) {
		LocationCoordinates4Obfuscation finalObfuscatedLocationCoordinates = null;
		LocationCoordinates4Obfuscation middleObfuscatedLocationCoordinates = null;

		// -- Enlarge
		middleObfuscatedLocationCoordinates = EObfuscation(geolocation, middleObfuscationLevel);
		middleObfuscatedLocationCoordinates.setObfuscationLevel(middleObfuscationLevel);
		Log.d(TAG, middleObfuscatedLocationCoordinates.toJSONString()+",");

		// -- Shift
		/* Solve the following system
		 * ri*sin(alpha/2) - rf*sin(gamma/2) = 0
		 * ri^2*(alpha-sin(alpha))+rf^2*(gamma-sin(gamma))-2*PI*rf^2*obfuscationLevel=0
		 * ri*cos(alpha/2) + rf*cos(gamma/2) - d = 0
		 */
		// Compute angles alpha and gamma, and distance d
		List<Double> solutions = solveAlphaGammaDByNewton(geolocation, middleObfuscatedLocationCoordinates, obfuscationLevel);
		double d = solutions.get(2);
		// Shift the geolocation center by distance d and angle theta
		/*
		 * /!\ Latitude/longitude are angles, not cartesian coordinates!
		 * new latitude != latitude+d*sin(alpha)
		 * new longitude != longitude+d*cos(alpha)
		 */
		finalObfuscatedLocationCoordinates = LocationCoordinatesUtils.shitLatLgn(middleObfuscatedLocationCoordinates, theta, d);
		finalObfuscatedLocationCoordinates.setObfuscationLevel(obfuscationLevel);
		return finalObfuscatedLocationCoordinates;
	}
	/**
	 * Location obfuscation algorithm
	 * by shifting the centre of the geolocation circle
	 * and then enlarging the radius
	 * @param geolocation Location to obfuscate
	 * @param obfuscationLevel Obfuscation level
	 * @return obfuscated location
	 */
	private LocationCoordinates4Obfuscation SEObfuscation(LocationCoordinates geolocation, double obfuscationLevel) {
		// Select an intermediate obfuscation level > obfuscation level
		double middleObfuscationLevel = rand.nextDoubleBetween(obfuscationLevel, 1);
		// Select a random direction for the shifting
		double theta = rand.nextDouble()*360;
		return SEObfuscation(geolocation, obfuscationLevel, middleObfuscationLevel, theta);
	}
	/**
	 * Location obfuscation algorithm
	 * by shifting the centre of the geolocation circle
	 * and then enlarging the radius
	 * @param geolocation Location to obfuscate
	 * @param obfuscationLevel Obfuscation level
	 * @param middleObfuscationLevel Obfuscation level for the shifting
	 * @param theta Direction of shifting
	 * @return obfuscated location
	 */
	private LocationCoordinates4Obfuscation SEObfuscation(LocationCoordinates geolocation, double obfuscationLevel, double middleObfuscationLevel, double theta) {
		LocationCoordinates4Obfuscation finalObfuscatedLocationCoordinates = null;
		LocationCoordinates4Obfuscation middleObfuscatedLocationCoordinates = null;

		// -- Shift
		middleObfuscatedLocationCoordinates = SObfuscation(geolocation, middleObfuscationLevel, theta);
		middleObfuscatedLocationCoordinates.setObfuscationLevel(middleObfuscationLevel);
		Log.d(TAG, middleObfuscatedLocationCoordinates.toJSONString()+",");

		// -- Enlarge
		/* Solve the following system
		 * ri*sin(alpha/2) - rf*sin(gamma/2) = 0
		 * ri^2/2*(alpha-sin(alpha))+rf^2/2*(gamma-sin(gamma))-PI*rf^2*obfuscationLevel=0
		 * ri*cos(alpha/2) + rf*cos(gamma/2) - d = 0
		 * 
		 * obfuscationLevel, ri and d are well-known
		 */
		double rf = solveAlphaGammaRfByNewton(geolocation, middleObfuscatedLocationCoordinates, obfuscationLevel, true);
		finalObfuscatedLocationCoordinates = new LocationCoordinates4Obfuscation(middleObfuscatedLocationCoordinates.getLatitude(), middleObfuscatedLocationCoordinates.getLongitude(), rf);
		finalObfuscatedLocationCoordinates.setObfuscationLevel(obfuscationLevel);
		return finalObfuscatedLocationCoordinates;
	}
	/**
	 * Location obfuscation algorithm
	 * by shifting the centre of the geolocation circle
	 * and then reducing the radius
	 * @param geolocation Location to obfuscate
	 * @param obfuscationLevel Obfuscation level
	 * @return obfuscated location
	 */
	private LocationCoordinates4Obfuscation SRObfuscation(LocationCoordinates geolocation, double obfuscationLevel) {
		// Select an intermediate obfuscation level > obfuscation level
		double middleObfuscationLevel = rand.nextDoubleBetween(obfuscationLevel, 1);
		// Select a random direction for the shifting
		double theta = rand.nextDouble()*360;
		return SRObfuscation(geolocation, obfuscationLevel, middleObfuscationLevel, theta);
	}
	/**
	 * Location obfuscation algorithm
	 * by shifting the centre of the geolocation circle
	 * and then reducing the radius
	 * @param geolocation Location to obfuscate
	 * @param obfuscationLevel Obfuscation level
	 * @param middleObfuscationLevel Obfuscation level for the shifting
	 * @param theta Direction of shifting
	 * @return obfuscated location
	 */
	private LocationCoordinates4Obfuscation SRObfuscation(LocationCoordinates geolocation, double obfuscationLevel, double middleObfuscationLevel, double theta) {
		LocationCoordinates4Obfuscation finalObfuscatedLocationCoordinates = null;
		LocationCoordinates4Obfuscation middleObfuscatedLocationCoordinates = null;

		// -- Shift
		middleObfuscatedLocationCoordinates = SObfuscation(geolocation, middleObfuscationLevel, theta);
		middleObfuscatedLocationCoordinates.setObfuscationLevel(middleObfuscationLevel);
		Log.d(TAG, middleObfuscatedLocationCoordinates.toJSONString()+",");

		// -- Reduce
		/* Solve the following system
		 * ri*sin(alpha/2) - rf*sin(gamma/2) = 0
		 * ri^2/2*(alpha-sin(alpha))+rf^2/2*(gamma-sin(gamma))-PI*rf^2*obfuscationLevel=0
		 * ri*cos(alpha/2) + rf*cos(gamma/2) - d = 0
		 * 
		 * obfuscationLevel, ri and d are well-known
		 */
		double rf = solveAlphaGammaRfByNewton(geolocation, middleObfuscatedLocationCoordinates, obfuscationLevel, false);
		finalObfuscatedLocationCoordinates = new LocationCoordinates4Obfuscation(middleObfuscatedLocationCoordinates.getLatitude(), middleObfuscatedLocationCoordinates.getLongitude(), rf);
		finalObfuscatedLocationCoordinates.setObfuscationLevel(obfuscationLevel);
		return finalObfuscatedLocationCoordinates;
	}

	/**
	 * Solve x-sin(x)-C=0 with Newton's Method
	 * @param obfuscationLevel
	 * @return x
	 */
	private double solveXMoinsSinxMoinsC(double C) {
		// -- Find x in x-sin(x)-C=0
		/* Computation algorithm
		We use Newton Method
		f(x)=x-sin(x)-C
		f'(x)=1-cos(x)
		xn = xnmoins - f(x)/f'(x)

		The difficulty is initialization, but :
		A sign study show that f is growing
		And f(PI/2)=-1.62, and f(PI)=0.9
		So, we choose a value between PI/2 and PI, for example: 2
		 */
		double xn = 2;
		double precision = xn;
		double precisionMax = 0.00000000000000000001;
		int nbMaxIteration = 10;
		int i = 0;
		while(i<nbMaxIteration && precision > precisionMax) {
			double xnmoins1 = xn;
			if (0 == xnmoins1) {
				xnmoins1 = 0.0000000001;
			}
			//			LOG.info("xn"+i+"="+xn+" (precision = "+precision+")");
			xn = xnmoins1-((-C+xnmoins1-Math.sin(xnmoins1))/(1-Math.cos(xnmoins1)));
			precision = Math.abs(xn-xnmoins1);
			i++;
		}	
		//		LOG.info("xnfinal="+xn+" ou "+Math.toDegrees(xn)+"°");
		return xn;
	}
	/**
	 * Solve alpha, gamma, d using Newton's method:
	 * ri*sin(x/2) - rf*sin(y/2) = 0
	 * ri^2*(x-sin(x))+rf^2*(y-sin(y))-2*PI*rf^2*obfuscationLevel=0
	 * @param initialLocation
	 * @param middleLocation
	 * @param obfuscationLevel
	 * @return
	 */
	private List<Double> solveAlphaGammaDByNewton(LocationCoordinates initialLocation, LocationCoordinates4Obfuscation middleLocation, double obfuscationLevel) {
		double ri = initialLocation.getAccuracy();
		double rf = middleLocation.getAccuracy();
		double ri2 = Math.pow(ri, 2);
		double rf2 = Math.pow(rf, 2);
		double C = Math.PI*obfuscationLevel*rf2;
		if (ri > rf) {
			C = Math.PI*obfuscationLevel*ri2;
		}
		double gammaMax = 2*Math.asin(ri/rf);
		double gamma0Max = gammaMax;

		// Initialize
		double alpha0 = 1;
		double gamma0 = 1;

		double alphan;
		double gamman;
		double precisionAlpha;
		double precisionGamma;		
		boolean restart;
		// While correct values have not been computed
		do {
			restart = false;

			// Values n-1 = initialization values
			alphan = alpha0;
			gamman = gamma0;
			precisionAlpha = alpha0;
			precisionGamma = gamma0;

			// While a good precision have been reached
			int i = 0;
			while(i<nbMaxIteration && (precisionAlpha > precisionMax || precisionGamma > precisionMax)) {
				// Save precedent values
				double alphanmoins1 = alphan;
				double gammanmoins1 = gamman;
				//				LOG.info("alphan"+i+"="+alphan+" ("+precisionAlpha+"), gamman"+i+"="+gamman+" ("+precisionGamma+")");
				// Compute functions and their derivates
				double f = ri*Math.sin(alphanmoins1/2)-rf*Math.sin(gammanmoins1/2);
				double dfByAlpha = ri/2*Math.cos(alphanmoins1/2);
				double dfByGamma = -rf/2*Math.cos(gammanmoins1/2);
				double g = ri2/2*(alphanmoins1-Math.sin(alphanmoins1))+rf2/2*(gammanmoins1-Math.sin(gammanmoins1))-C;
				double dgByAlpha = ri2*(1-Math.cos(alphanmoins1));
				double dgByGamma = rf2*(1-Math.cos(gammanmoins1));

				// Compute new values
				/* Algorithm
				 * alphan = alphan-1 - (f(alphan-1)*dgByGamma-g(alphan-1)*dfByGamma)/(dfByAlpha*dgByGamma-dfByGamma*dgByAlpha)
				 * gamman = gamman-1 - (g(gamman-1)*dfByAlpha-f(gamman-1)*dgByAlpha)/(dfByAlpha*dgByGamma-dfByGamma*dgByAlpha)
				 */
				double delta = dfByAlpha*dgByGamma-dfByGamma*dgByAlpha;
				alphan = alphanmoins1 - (f*dgByGamma-g*dfByGamma)/delta;
				gamman = gammanmoins1 - (g*dfByAlpha-f*dgByAlpha)/delta;
				//				/* Alternative algorithm
				//				 * Xn = (alphan gamman)
				//				 * Xn = Xn-1 - F(Xn-1)/J_F(Xn-1)
				//				 */
				//				double [][] valuesF = {{f}, {g}};
				//				double [][] valuesJ_F = {{dfByAlpha, dfByGamma}, {dgByAlpha, dgByGamma}};
				//		        RealMatrix F = new Array2DRowRealMatrix(valuesF);
				//		        RealMatrix J_F = new Array2DRowRealMatrix(valuesJ_F);
				//		        RealMatrix J_FinverseTimeF = new LUDecompositionImpl(J_F).getSolver().getInverse().multiply(F);
				//				alphan = alphanmoins1 - J_FinverseTimeF.getEntry(0,0);
				//				gamman = gammanmoins1 - J_FinverseTimeF.getEntry(1,0);

				// Compute precision
				precisionAlpha = Math.abs(alphan-alphanmoins1);
				precisionGamma = Math.abs(gamman-gammanmoins1);
				i++;
			}

			// Check if computed values are correct, else: restart
			//			LOG.info("Restart");
			//			LOG.info("alpha0="+alpha0+", alphanfinal="+alphan+" ou "+Math.toDegrees(alphan)+"°");
			//			LOG.info("gamma0="+gamma0+", gammanfinal="+gamman+" ou "+Math.toDegrees(gamman)+"°");
			if (alpha0 < alpha0Max && (alphan <= 0 || alphan >= 2*Math.PI)) {
				restart = true;
				alpha0 += step;
			}
			if (gamma0 < gamma0Max && (gamman <= 0 || Math.toDegrees(gamman) >= gammaMax)) {
				restart = true;
				gamma0 += step;
			}
		}
		while(restart);

		double d = ri*Math.cos(alphan/2)+rf*Math.cos(gamman/2);

		List<Double> solutions = new ArrayList<Double>();
		solutions.add(alphan);
		solutions.add(gamman);
		solutions.add(d);
		return solutions;
	}
	/**
	 * Solve alpha, gamma, rf
	 * ri*sin(alpha/2) - rf*sin(gamma/2) = 0
	 * ri^2*(alpha-sin(alpha))+rf^2*(gamma-sin(gamma))-2*PI*rf^2*obfuscationLevel=0
	 * ri*cos(alpha/2) + rf*cos(gamma/2) - d = 0
	 * @param initialLocation
	 * @param middleLocation
	 * @param obfuscationLevel
	 * @return
	 */
	private double solveAlphaGammaRfByNewton(LocationCoordinates initialLocation, LocationCoordinates4Obfuscation middleLocation, double obfuscationLevel, boolean enlargement) {
		// Rename some variables
		double ri = initialLocation.getAccuracy();
		double ri2 = Math.pow(ri, 2);
		double d = middleLocation.getShiftDistance();
		double d2 = Math.pow(d, 2);
		double rf0Max = 100*ri;

		// Initialization
		double rf0 = ri;
		double alpha;
		double gamma;
		double rf;	
		double precision = 0.001;
		boolean restart;
		double maxH = 0;
		double minH = 0;
		//		int nbTour = 0;

		// While correct values have not been computed
		do {
			restart = false;

			// - Select rf
			rf = rf0;
			// - Compute alpha and gamma
			double rf2 = Math.pow(rf, 2);
			// Ci are in Cf or they are similar
			if (d == 0 || rf == 0 || rf >= (ri+d)) {
				alpha = 2*Math.PI;
				gamma = 0;
			}
			// Cf are in Ci
			else if ((d <= ri && ri >= (d+rf)) || ri == 0) {
				alpha = 0;
				gamma = 2*Math.PI;
			}
			// Circles are disjoints
			else if (d > ri+rf) {
				alpha = 0;
				gamma = 0;
			}
			// Normal case
			else {
				alpha = 2*Math.acos((ri2+d2-rf2)/(2*ri*d));
				gamma = 2*Math.acos((rf2+d2-ri2)/(2*rf*d));
			}

			// - Compute the function h that must be equals to 0
			double Aintersection = ri2/2*(alpha-Math.sin(alpha))+rf2/2*(gamma-Math.sin(gamma));
			double Atotal = Math.PI*ri2;
			if (rf > ri) {
				Atotal = Math.PI*rf2;
			}
			double h = Aintersection/Atotal-obfuscationLevel;

			// ( If h != 0 : restart
			if (rf0 > 0 && rf0 < rf0Max && (h <= -precision ||  h >= precision)) {
				// - Compute the step, to go faster when we are far away of the solution
				// Select maxH
				if (0 == maxH || maxH < Math.abs(h)) {
					maxH = Math.abs(h);
				}
				if (0 == minH || minH > Math.abs(h)) {
					minH = Math.abs(h);
				}
				/* ALGO
				 * We want a function  f: 
				 * lim (x -> 0) f = 0
				 * lim (x -> infinity) f = m
				 * arctan (with x > 0 and < m) is a good function 
				 */
				double m = 2;
				double step = 2*m/Math.PI*Math.atan(Math.abs(h));
				//				LOG.info(Math.abs(h)+" "+step);
				// We continy only if rf0 can be > 0
				if (rf0 > step) {
					restart = true;
					if (enlargement) {
						rf0 += step;
					}
					else {
						rf0 -= step;
					}
				}
			}

			//			nbTour++;
			//			if (!restart) {
			//				LOG.info("Soluce "+nbTour+" : " +
			//						"alpha="+Math.toDegrees(alpha)+"°, " +
			//						"| gamma="+Math.toDegrees(gamma)+"°, " +
			//						"| rf="+rf+" meters,"+
			//						"| ri="+ri+" meters, "+
			//						"| d="+d+" meters, "+
			//						"h="+h+" (maxH="+maxH+", minH="+minH+")");
			//			}
		}
		while(restart);
		return (double) rf;
	}

	// -- GETTER / SETTER
}
