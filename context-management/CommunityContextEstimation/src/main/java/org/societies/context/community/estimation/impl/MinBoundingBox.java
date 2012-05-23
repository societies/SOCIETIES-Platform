import java.awt.Point;
import java.util.ArrayList;

public class MinBoundingBox {
	public Point[] minBBox(ArrayList<Point> points) {

	      Point[] minBB = new Point[2];
	// Arkoun to pano aristera shmeio kai to kato deksia shmeio tou tetragonou gia na to orisoun.


	      int minX= Integer.MAX_VALUE;
	      int maxX = Integer.MIN_VALUE;
	      int minY= Integer.MAX_VALUE;
	      int maxY = Integer.MIN_VALUE;


	// Ekfylismenh periptosh exoume an einai mono ena shmeio ... allios kanonika ...

	  
	      for (int i=0; i<points.size(); ++i){
	         if (points.get(i).x < minX){
	            minX=points.get(i).x;
	         }
	         if (points.get(i).x > maxX){
	            maxX=points.get(i).x;
	         }
	         if (points.get(i).x < minX){
	         minX=points.get(i).x;
	         }
	         if (points.get(i).x > maxX){ 
	         maxX=points.get(i).x;
	         }
	         if (points.get(i).y < minY){
	            minY=points.get(i).y;
	         }
	         if (points.get(i).y > maxY){
	            maxY=points.get(i).y;
	         }
	         if (points.get(i).y < minY){
	         minY=points.get(i).y;
	         }
	         if (points.get(i).y > maxY){ 
	         maxY=points.get(i).y;
	         }
	      }

	      Point topLeft = new Point(minX,maxY);
	      Point bottomRight = new Point(maxX,minY);


	      return {topLeft,bottomRight};
	   }

}
