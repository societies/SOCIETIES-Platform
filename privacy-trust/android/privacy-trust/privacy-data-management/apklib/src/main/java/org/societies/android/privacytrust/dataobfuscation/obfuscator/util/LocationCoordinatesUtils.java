/**
 * 
 */
package org.societies.android.privacytrust.dataobfuscation.obfuscator.util;

import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.LocationCoordinates;




/**
 * @author Olivier Maridat Trialog
 * @date 2 sept. 2011
 */
public class LocationCoordinatesUtils {
//	private static final Logger LOG = Logger.getLogger(GeolocationUtils.class);
	
	public static double areaIntersection2Circles(double r1, double r2, double d) {
		double r12 = Math.pow(r1, 2);
    	double r22 = Math.pow(r2, 2);
    	double d2 = Math.pow(d, 2);
    	double alpha;
    	double gamma;
    	if (d == 0 || r1 == 0 || r2 == 0) {
        	alpha = 2*Math.PI;
        	gamma = 0;
    	}
    	if (d > r1+r2) {
    		alpha = 0;
        	gamma = 0;
    	}
    	else {
    		alpha = 2*Math.acos((r12+d2-r22)/(2*r1*d));
        	gamma = 2*Math.acos((r22+d2-r12)/(2*r2*d));
    	}
	
		double a1 = r12/2*(alpha-Math.sin(alpha))+r22/2*(gamma-Math.sin(gamma));
//		double a2 = r22*Math.acos(d/r2)-d*Math.sqrt(r22-d2);
//		double a3 = r12/2*alpha+r22/2*gamma-1/2*Math.sqrt((-d+r1+r2)*(d-r1+r2)*(d+r1-r2)*(d+r1+r2));
    	
//		LOG.info("Aire C1="+(Math.PI*r12)+"m², alpha="+Math.toDegrees(alpha)+"°");
//		LOG.info("Aire C2="+(Math.PI*r22)+"m², gamma="+Math.toDegrees(gamma)+"°");
//    	LOG.info("Aire C1 inter C2 methode 1="+a1+"m²");
    	return a1;
	}
	
	/**
	 * Arrondi d'un double avec n éléments après la virgule.
	 * @param a La valeur à convertir.
	 * @param n Le nombre de décimales à conserver.
	 * @return La valeur arrondi à n décimales.
	 */
	public static double floor(double a, int n) {
		double p = Math.pow(10.0, n);
		return Math.floor((a*p)+0.5) / p;
	}

	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */
	/* Vincenty Inverse Solution of Geodesics on the Ellipsoid (c) Chris Veness 2002-2010             */
	/* http://www.movable-type.co.uk/                                                                                             */
	/* from: Vincenty inverse formula - T Vincenty, "Direct and Inverse Solutions of Geodesics on the */
	/*       Ellipsoid with application of nested equations", Survey Review, vol XXII no 176, 1975    */
	/*       http://www.ngs.noaa.gov/PUBS_LIB/inverse.pdf
	/* adapted by: Olivier Maridat (Trialog, Societies Project) 2011                                  */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */
	/**
	 * Calculates geodetic distance between two points specified by latitude/longitude using 
	 * Vincenty inverse formula for ellipsoids
	 *
	 * @param   {Number} lat1, lon1: first point in decimal degrees
	 * @param   {Number} lat2, lon2: second point in decimal degrees
	 * @returns (Number} distance in metres between points
	 */
	public static double distVincenty(double lat1, double lon1, double lat2, double lon2) {
		// WGS-84 ellipsoid params
		double a = 6378137;
		double b = 6356752.314245;
		double f = 1/298.257223563;  
		
		double L = Math.toRadians(lon2-lon1);
		double U1 = Math.atan((1-f) * Math.tan(Math.toRadians(lat1)));
		double U2 = Math.atan((1-f) * Math.tan(Math.toRadians(lat2)));
		double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
		double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);
	  
		double lambda = L;
		double lambdaP;
		double iterLimit = 100;
		double cosSqAlpha;
		double cos2SigmaM;
		double sinSigma;
		double cosSigma;
		double sigma;
		double sinLambda;
		double cosLambda;
		
		do {
		    sinLambda = Math.sin(lambda);
		    cosLambda = Math.cos(lambda);
		    sinSigma = Math.sqrt((cosU2*sinLambda) * (cosU2*sinLambda) + 
		      (cosU1*sinU2-sinU1*cosU2*cosLambda) * (cosU1*sinU2-sinU1*cosU2*cosLambda));
		    if (sinSigma==0)
		    	return 0;  // co-incident points
		    cosSigma = sinU1*sinU2 + cosU1*cosU2*cosLambda;
		    sigma = Math.atan2(sinSigma, cosSigma);
		    double sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
		    cosSqAlpha = 1 - sinAlpha*sinAlpha;
		    cos2SigmaM = cosSigma - 2*sinU1*sinU2/cosSqAlpha;
//		    if (Integer.(cos2SigmaM))
//		    	cos2SigmaM = 0;  // equatorial line: cosSqAlpha=0 (§6)
		    double C = f/16*cosSqAlpha*(4+f*(4-3*cosSqAlpha));
		    lambdaP = lambda;
		    lambda = L + (1-C) * f * sinAlpha *
		      (sigma + C*sinSigma*(cos2SigmaM+C*cosSigma*(-1+2*cos2SigmaM*cos2SigmaM)));
		} while (Math.abs(lambda-lambdaP) > 1e-12 && --iterLimit>0);

	  if (iterLimit==0)
		  return 0;  // formula failed to converge

	  double uSq = cosSqAlpha * (a*a - b*b) / (b*b);
	  double A = 1 + uSq/16384*(4096+uSq*(-768+uSq*(320-175*uSq)));
	  double B = uSq/1024 * (256+uSq*(-128+uSq*(74-47*uSq)));
	  double deltaSigma = B*sinSigma*(cos2SigmaM+B/4*(cosSigma*(-1+2*cos2SigmaM*cos2SigmaM)-
	    B/6*cos2SigmaM*(-3+4*sinSigma*sinSigma)*(-3+4*cos2SigmaM*cos2SigmaM)));
	  double s = b*A*(sigma-deltaSigma);
	  
//	  s = s.toFixed(3); // round to 1mm precision
	  
	  // note: to return initial/final bearings in addition to distance, use something like:
//	  double fwdAz = Math.atan2(cosU2*sinLambda,  cosU1*sinU2-sinU1*cosU2*cosLambda);
//	  double revAz = Math.atan2(cosU1*sinLambda, -sinU1*cosU2+cosU1*sinU2*cosLambda);
//	  return { distance: s, initialBearing: fwdAz.toDeg(), finalBearing: revAz.toDeg() };
	  return s;
	}
	
	/**
	 * Calculates destination point given start point lat/long, angle (=direction) & distance of translation, 
	 * using Vincenty inverse formula for ellipsoids
	 *
	 * @param geolocation first point in decimal degrees
	 * @param direction direction of the translation in decimal degree
	 * @param distance distance along direction in meters
	 * @returns destination point
	 */
	public static LocationCoordinates4Obfuscation shitLatLgn(LocationCoordinates location, double direction, double distance) {
		// WGS-84 ellipsiod
		double a = 6378137;
		double b = 6356752.3142;
		double f = 1/298.257223563; 
		
		double alpha1 = Math.toRadians(direction);
		double sinAlpha1 = Math.sin(alpha1);
		double cosAlpha1 = Math.cos(alpha1);
		double tanU1 = (1-f) * Math.tan(Math.toRadians(location.getLatitude()));
		double cosU1 = 1 / Math.sqrt((1 + tanU1*tanU1)), sinU1 = tanU1*cosU1;
		double sigma1 = Math.atan2(tanU1, cosAlpha1);
		double sinAlpha = cosU1 * sinAlpha1;
		double cosSqAlpha = 1 - sinAlpha*sinAlpha;
		double uSq = cosSqAlpha * (a*a - b*b) / (b*b);
		double A = 1 + uSq/16384*(4096+uSq*(-768+uSq*(320-175*uSq)));
		double B = uSq/1024 * (256+uSq*(-128+uSq*(74-47*uSq)));
		double sigma = distance / (b*A);
		double sigmaP = 2*Math.PI;
		double cos2SigmaM = 0;
		double sinSigma = 0;
		double cosSigma = 0;
		double deltaSigma = 0;
		// Iterations until |sigma-sigmaP| > 1e-12
		while (Math.abs(sigma-sigmaP) > 1e-12) {
			cos2SigmaM = Math.cos(2*sigma1 + sigma);
			sinSigma = Math.sin(sigma);
			cosSigma = Math.cos(sigma);
			deltaSigma = B*sinSigma*(cos2SigmaM+B/4*(cosSigma*(-1+2*cos2SigmaM*cos2SigmaM)-
					B/6*cos2SigmaM*(-3+4*sinSigma*sinSigma)*(-3+4*cos2SigmaM*cos2SigmaM)));
			sigmaP = sigma;
			sigma = distance / (b*A) + deltaSigma;
		}

	  double tmp = sinU1*sinSigma - cosU1*cosSigma*cosAlpha1;
	  double lat2 = Math.atan2(sinU1*cosSigma + cosU1*sinSigma*cosAlpha1, 
	      (1-f)*Math.sqrt(sinAlpha*sinAlpha + tmp*tmp));
	  double lambda = Math.atan2(sinSigma*sinAlpha1, cosU1*cosSigma - sinU1*sinSigma*cosAlpha1);
	  double C = f/16*cosSqAlpha*(4+f*(4-3*cosSqAlpha));
	  double L = lambda - (1-C) * f * sinAlpha *
	      (sigma + C*sinSigma*(cos2SigmaM+C*cosSigma*(-1+2*cos2SigmaM*cos2SigmaM)));
	  double lon2 = (Math.toRadians(location.getLongitude())+L+3*Math.PI)%(2*Math.PI) - Math.PI;  // normalise to -180...+180
//	  double revAz = Math.atan2(sinAlpha, -tmp);  // final shiftAngle, if required

	  return new LocationCoordinates4Obfuscation(Math.toDegrees(lat2), Math.toDegrees(lon2), location.getAccuracy());
	}
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */

	
	public static double sexagecimal2decimal(double degree, double minute, double seconde) {
		return sexagecimal2decimal(degree, minute, seconde, false);
	}
	public static double sexagecimal2decimal(double degree, double minute, double seconde, boolean neg) {
		double decimal = degree+(minute/60.0)+(seconde/3600.0);
		return neg ? -decimal : decimal;
	}
}
