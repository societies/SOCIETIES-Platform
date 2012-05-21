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


package org.societies.context.community.estimation.impl;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

public class ConvexHull {
									// Convex Hull pseudocode
									// for all points p in S
									// 	for all points q in S
									// 		if p != q
									// 			draw a line from p to q
									// 			if all points in S except p and q lie to the left of the line
									//			 add the directed vector pq to the solution set


//Input = a set S of n points
//    Assume that there are at least 2 points in the input set S of points
//
//QuickHull (S)
//{
//    // Find convex hull from the set S of n points
//
//    Convex Hull := {}
//    Find left and right most points, say A & B, and add A & B to convex hull
//    Segment AB divides the remaining (n-2) points into 2 groups S1 and S2
//        where S1 are points in S that are on the right side of the oriented line from A to B,
//        and S2 are points in S that are on the right side of the oriented line from B to A
//    FindHull (S1, A, B)
//    FindHull (S2, B, A)
//} 	

	public ArrayList<Point> qHull(ArrayList<Point> points) {			
		
		
		//Constants declaration
		ArrayList<Point> convexHullSet = new ArrayList<Point>();
		int minX= Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int minPoint = -1;
		int maxPoint = -1;
		ArrayList<Point> leftPointsSet = new ArrayList<Point>();
		ArrayList<Point> rightPointsSet = new ArrayList<Point>();

		if (points.size()<3){
			return points;
		}
		
		for (int i=0; i<points.size(); ++i){
			if (points.get(i).x < minX){
				minX=points.get(i).x;
				minPoint = i;
			}
			if (points.get(i).x > maxX){
				maxX=points.get(i).x;
				maxPoint =i;
			}
		}
		
		Point minP = points.get(minPoint);
		Point maxP = points.get(maxPoint);	
		Point p = new Point();
		convexHullSet.add(minP);
		convexHullSet.add(maxP);
		points.remove(minP);
		points.remove(maxP);
		
		for (int i=0; i<convexHullSet.size(); ++i){
			System.out.println("ConvexHull Point"+i+"is"+convexHullSet.get(i));
		}
		
		// Separate Left Or Right Points
		for (int i=0; i<points.size(); ++i){
			p = points.get(i);
			int pL = pointLocation(minP, maxP, p);
			if (pL == 1){
				leftPointsSet.add(p);
			}else{
				rightPointsSet.add(p);
			}
		}
		for (int i=0; i<leftPointsSet.size(); ++i){
			System.out.println("LeftSet Point"+i+"is"+leftPointsSet.get(i));
		}
		
		for (int i=0; i<rightPointsSet.size(); ++i){
			System.out.println("RightSet Point"+i+"is"+rightPointsSet.get(i));
		}
		for (int i=0; i<convexHullSet.size(); ++i){
			System.out.println("Convex Hull Element "+i+"is"+convexHullSet.get(i));
		}
						
		findHullSet(rightPointsSet,minP,maxP,convexHullSet);
		findHullSet(leftPointsSet,maxP,minP,convexHullSet);
		System.out.println("HIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII"+convexHullSet.size());
//		for (int z=0; z<convexHullSet.size();++z){
//			System.out.println("ConvexHull["+z+"] = "+convexHullSet.get(z));
//		}
		return convexHullSet;		
	}
	

//	FindHull (Sk, P, Q)
//	{
//	    // Find points on convex hull from the set Sk of points
//	    // that are on the right side of the oriented line from P to Q
//
//	   If Sk has no point,
//	        then  return.
//	    From the given set of points in Sk, find farthest point, say C, from segment PQ
//	    Add point C to convex hull at the location between P and Q
//	    Three points P, Q, and C partition the remaining points of Sk into 3 subsets: S0, S1, and S2
//	        where S0 are points inside triangle PCQ, S1 are points on the right side of the oriented
//	        line from  P to C, and S2 are points on the right side of the oriented line from C to Q.
//	    FindHull(S1, P, C)
//	    FindHull(S2, C, Q)
//	}
//
//	Output = convex hull 

	//******************************************************************************************************
		//******************************************************************************************************
	
	private void findHullSet(ArrayList<Point> pointsSet, Point minPoint, Point maxPoint, ArrayList<Point> convexHullSet) {
		
		//Constants
		Point fP = new Point();
		
		int distance_min = Integer.MIN_VALUE;
		int dis = 0;
		int furthestPoint = -1;
		int insertPosition = convexHullSet.indexOf(maxPoint);
		
		ArrayList<Point> set1 = new ArrayList<Point>();
		ArrayList<Point> set2 = new ArrayList<Point>();		
		
		if (pointsSet.size()==0){
			return ;
		}
		if (pointsSet.size()==1){
			Point p = pointsSet.get(0);
			pointsSet.remove(p);
			convexHullSet.add(insertPosition, p);
			return;
		}
		System.out.println("A point is"+minPoint.x +""+minPoint.y);
		System.out.println("B point is"+maxPoint.x+""+maxPoint.y);
//		System.out.println("THIS IS THE"+convexHullSet.size()+" TIME");
		
	
	
		//Calculate  the farthest point of minPoint maxPoint
		
		for (int i=0; i<pointsSet.size(); ++i){	
			Point m =pointsSet.get(i);								
			dis=distance(minPoint, maxPoint, m);
			//	System.out.print("Point's "+i+" ["+m.x+"]"+"["+m.y+"]"+"distance ="+dis);

			if (dis > distance_min){
				//System.out.println("this is time number.........."+i);
				//System.out.print("Dis="+dis+" distance_min="+distance_min);
				distance_min =dis;
				//					System.out.println("MIN_DISTANCE="+distance_min);
				//					System.out.println("DIS="+dis);
				furthestPoint=i;		
			}
	
		}
		
		fP=pointsSet.get(furthestPoint);
		convexHullSet.add(insertPosition,fP);
		pointsSet.remove(furthestPoint);
		//	System.out.print("Farthest Point is "+fP);
		System.out.println("The size of Points Set is " + pointsSet.size());
		//System.out.print("FINAL FARTHEST POINT ISSSSSSSSSSS :) "+fP);
		//pointsSet.remove(furthestPoint);
		
		
		// Find points lying in the right of a,fP
		for (int i=0; i<pointsSet.size(); ++i){
			Point rP = pointsSet.get(i);
			int pL = pointLocation(minPoint, fP, rP);
			if (pL == -1){
				set1.add(rP);
			}	
		}
		
		// Find points lying in the right of fP,b
		for (int i=0; i<pointsSet.size(); ++i){
			Point rP = pointsSet.get(i);
			int pL = pointLocation(fP, maxPoint, rP);
			if (pL == -1){
				set2.add(rP);
			}
//		for (int y=0; y<set1.size();++y){
//			System.out.println("Set1 stoixeio  "+i+" = "+set1.get(i));
//		}
//		
//		for (int z=0; z<set2.size();++z){
//			System.out.println("RSET stoixeio  "+i+" = "+set2.get(i));
//		}
//		System.out.println("READDY TO RUN findHull");
		System.out.println("set1 size is"+set1.size());
//		System.out.println("The element is "+set1.get(0));
		System.out.println("set2 size is"+set2.size());
//		System.out.println("The element is ");
		findHullSet(set1, minPoint, fP, convexHullSet);
		findHullSet(set2, fP, maxPoint, convexHullSet);
	}
		for (int y=0; y<set1.size();++y){
		System.out.println("Set1 stoixeio  "+y+" = "+set1.get(y));
	}
	
	for (int z=0; z<set2.size();++z){
		System.out.println("set2 stoixeio  "+z+" = "+set2.get(z));
	}

	}
	
	

	
	
	
	
	
	
//&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^	
//&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^&^		
	
	
	
	
	
	
	private void hullSet(Point a, Point b, ArrayList<Point> set, ArrayList<Point> hull) {
		System.out.println("I am at the hullSet method");
		int insertPosition = hull.indexOf(b);
		if (set.size()==0)
			return;
		if (set.size()==1){
			Point p = set.get(0);
			set.remove(p);
			hull.add(insertPosition, p);
			return;
		}
		//Calculate the furthest point from segment AB, set it in the hull set and remove it from the set of points
		int dist = Integer.MIN_VALUE;
		int furthestPoint = -1;
		for (int i=0; i<set.size();++i){
			Point p = set.get(i);
			int distance = distance(a, b, p);
			if (distance > dist){
				dist = distance;
				furthestPoint = i;
			}
		}
		
		Point p = set.get(furthestPoint);
		set.remove(furthestPoint);
		hull.add(insertPosition, p);
		
		//Find who's on the left of segment AP
		ArrayList<Point> leftSetAP	= new ArrayList<Point>();
		for (int i=0; i<set.size(); ++i){
			Point m = set.get(i);
			if (pointLocation(a, p, m)==1){
				leftSetAP.add(m);
			}
			
		}
		System.out.println("The size of leftSetAP is "+leftSetAP.size());
		//Find who's on the left of segment PB
		ArrayList<Point> leftSetPB	= new ArrayList<Point>();
		for (int i=0; i<set.size(); ++i){
			Point m = set.get(i);
			if (pointLocation(p, b, m)==1){
				leftSetPB.add(m);
			}
		}
		System.out.println("The size of leftSetPB is "+leftSetPB.size());

		//Divide and conquer. Run the same method again this time for the segment AP and PB
		hullSet(a, p, leftSetPB, hull);
		hullSet(p, b, leftSetAP, hull);
		
		
	}

	//******************************************************************************************************
		//******************************************************************************************************	

	
	public ArrayList<Point> quickHull (ArrayList<Point> points){
		
		ArrayList<Point> convexHull = new ArrayList<Point>();
		if (points.size() < 3) 
			return (ArrayList<Point>) points.clone();
	
	int minPoint = -1, maxPoint = -1;
	int minX = Integer.MAX_VALUE;
	int maxX = Integer.MIN_VALUE;
	
	//From all the points in the points set I select the min and the max ones
	for (int i = 0; i<points.size(); ++i){
		if (points.get(i).x < minX){
			minX =points.get(i).x;
			minPoint = i;
		}
		if (points.get(i).x > maxX){
			maxX =points.get(i).x;
			maxPoint = i;
		}		
	}
	
	Point A = points.get(minPoint);
	Point B = points.get(maxPoint);
	
	//I add to the conveHull the points that belong to it and remove them from the points set
	convexHull.add(A);
	convexHull.add(B);
	points.remove(A);
	points.remove(B);
	
	//I have to separate the points in two sets, left and right compared to a given segment declared by AB
	ArrayList<Point> leftSet = new ArrayList<Point>();
	ArrayList<Point> rightSet = new ArrayList<Point>();
	
	for (int i=0; i<points.size(); ++i){
		Point p = points.get(i);
		if (pointLocation(A, B, p) == -1) 
			leftSet.add(p);
			else
				rightSet.add(p);
		}
	//Here we have a segment AB, two sets of points one at the left and one at the right of the segment, 
	//and one set of points containing the points belonging on the hull. 
	//We will run the hullSet method for these two sets and the hull set
	hullSet(A,B,rightSet,convexHull);
	//hullSet(B,A, leftSet,convexHull);
	
	return convexHull;
	
	}
	
	//******************************************************************************************************
		//******************************************************************************************************

	//Based on Cross Product function, if the crossProduct is negative then point P is on the right of AB. Otherwise is on the left
	public int pointLocation(Point A, Point B, Point P){
		int crossProduct = (B.x-A.x)*(P.y-A.y) - (B.y-A.y)*(P.x-A.x);
		return (crossProduct>0 ?1 :-1);
	}
	
	//******************************************************************************************************
		//******************************************************************************************************

	//Distance calculation between a point P and a segment AB
	public int distance(Point A, Point B, Point P){
		int ABx=B.x-A.x;
		int ABy =B.y-A.y;
		int num = ABx*(A.y-P.y)-ABy*(A.x-P.x);
		if (num<0)
			num = -num;
		return num;
	}	
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	private static float ccw( float[] p1, float[] p2, float[] p3 )
//	{
//		return (p2[0] - p1[0])*(p3[1] - p1[1]) - (p2[1] - p1[1])*(p3[0] - p1[0]);
//	}
//
//	public static float[][] findConvexHull(float[][] pts)
//	{
//		if ( pts.length < 3 )
//		{
//			return pts;
//		}
//		float[] minY = {Float.MAX_VALUE, Float.MAX_VALUE};
//		int ixMinY = -1;
//		for ( int i = 0; i < pts.length; i++ )
//		{
//			float[] pt = pts[i];
//			if ( pt[1] < minY[1] )
//			{
//				minY = pt;
//				ixMinY = i;
//			}
//			else if ( pt[1] == minY[1] && pt[0] < minY[0] )
//			{
//				minY = pt;
//				ixMinY = i;
//			}
//		}
//
//		pts[ixMinY] = pts[0];
//		pts[0] = minY;
//
//		Arrays.sort(pts, 1, pts.length, new AngleComparator(minY));
//		Deque<float[]> ps = new LinkedList<float[]>();
//		ps.push(pts[0]);
//		ps.push(pts[1]);
//		for ( int i = 2; i < pts.length; i++ )
//		{
//			float[] p0 = ps.pop();
//			float[] p1 = ps.pop();
//			float ccw = ccw(p0, p1, pts[i]);
//			ps.push(p1);
//			ps.push(p0);
//			if ( ccw == 0 )
//			{
//				ps.pop();
//				ps.push(pts[i]);
//			}
//			else if ( ccw < 0 )
//			{
//				ps.push(pts[i]);
//			}
//			else
//			{
//				while ( (ccw >= 0) && ps.size() > 2 )
//				{
//					ps.pop();
//					p0 = ps.pop();
//					p1 = ps.pop();
//					ccw = ccw(p0, p1, pts[i]);
//					ps.push(p1);
//					ps.push(p0);
//				}
//				ps.push(pts[i]);
//			}
//		}
//		return ps.toArray(new float[ps.size()][]);
//	}
//
//	private static class AngleComparator implements Comparator<float[]>
//	{
//		private final float[] pt;
//
//		public AngleComparator(float[] pt)
//		{
//			this.pt = pt;
//		}
//
//		public int compare(float[] m, float[] n)
//		{
//			// cos t = a/h
//					// a = m.x - pt.x
//					// h = sqrt((m.x-pt.x)^2 + (m.y - pt.y)^2)
//					float cm = (m[0] - pt[0])/
//					(float)Math.sqrt((m[0] - pt[0])*(m[0] - pt[0]) + (m[1] - pt[1])*(m[1] - pt[1]));
//			cm = 1 - cm;
//			float cn = (n[0] - pt[0])/
//					(float)Math.sqrt((n[0] - pt[0])*(n[0] - pt[0]) + (n[1] - pt[1])*(n[1] - pt[1]));
//			cn = 1 - cn;
//			if ( cm < cn )
//			{
//				return -1;
//			}
//			else if ( cm > cn )
//			{
//				return 1;
//			}
//			else
//			{
//				return 0;
//			}
//		}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


		//	Point point1 = new Point(1,4);
		//	Point point2 = new Point(2,2);
		//	Point point3 = new Point(2,1);
		//	Point point4 = new Point(3,7);
		//	Point point5 = new Point(6,9);
		//
		//	ArrayList<Point> pSet = new ArrayList();
		//	pSet.;
		//	pSet.add(point2);
		//	pSet.add(point3);
		//	pSet.add(point4);
		//	pSet.add(point5);
		//
		//	// for (Point p:pSet){
		//	// if (pSet.iterator().hasNext()){
		//	// Point q = pSet.iterator().next();
		//	// for (Point q:pSet){
		//	// if (p!=q){
		//	// Point a = pSet.iterator().next();
		//	// checkPointsPosition(p,q,a);
		//	// }
		//	// }
		//	// }
		//	//
		//	// }




		//private ArrayList<Point> checkPointsPosition(Point a, Point b, Point p) {
		//	// TODO Auto-generated method stub
		//	ArrayList<Point> solutionSet = new ArrayList();
		//	if ((b.x-a.x)*(p.y-a.y)-(b.y-a.y)*(p.x-a.x)>0){
		//		solutionSet.add(p);
		//	}
		//	return solutionSet;


	}

