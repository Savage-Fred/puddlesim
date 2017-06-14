package org.fog.utils;
import java.io.*;		/* Error Handling */

/**
 * Defines the area of coverage for a clusterhead 
 * Contains methods to check if a point is within the area of coverage for a clusterhead 
 * Works on any set of points that form a polygon. 
 */
public class Coverage {

	/**
	 * Nested class for Cartesian coordinates of a point
	 */ 
	private class Point {
		////////////// 	POINT FIELDS ///////////////////
		public double x;
		public double y;

		////////////// POINT CONSTRUCTORS //////////////
		public Point(double x, double y) {
			this.setx(x);
			this.sety(y);
		}

		////////////// POINT GETTERS AND SETTERS ///////
		public void 	setx(double input) {this.x = input;}
		public void 	sety(double input) {this.y = input;}
		public double 	getx() {return this.x;}
		public double 	gety() {return this.y;}
	}




	/**
	 * Array to hold the points of the polygon
	 * A collection of points defines the boundaries of a polygon 
	 */
	private final Point[] points; 

	private double[] xvals;
	private double[] yvals;

	/**
	 * Coverage Constructor 
	 * Fills an array with points that define the boundary of the polygon. 
	 * 
	 * @param xin double array of the x values 
	 * @param yin double array of the y values 
	 * 		note: xin[i],yin[i] become point i. The arrays must match
	 * @throws IllegalArgumentException if the arrays do not match 
	 */ 
	public Coverage(double[] xin, double[] yin) throws IOException{
		if (xin.length > yin.length) {
			throw new IllegalArgumentException ("Error: More x values than y values");
		}
		if (yin.length > xin.length) {
			throw new IllegalArgumentException ("Error: More y values than x values");
		}

		points = new Point[xin.length];
		for (int i = 0; i < xin.length;i++)
		{
			points[i] = new Point(xin[i], yin[i]);
		}
	}




	/**
	 * Method to check if a point is contined within a polygon 
	 * See: https://stackoverflow.com/questions/8721406/how-to-determine-if-a-point-is-inside-a-2d-convex-polygon
	 * See: https://wrf.ecse.rpi.edu//Research/Short_Notes/pnpoly.html
	 *
	 * @param test The point to check 
	 * @return True if the point is inside the polygon 
	 */
	public boolean contains(Point test) {
		int i;
		int j;
		boolean result = false;
		for (i = 0, j = points.length-1; i < points.length; j = i++) {
			if ((points[i].y > test.y) != (points[j].y > test.y) &&
				(test.x < (points[j].x - points[i].x) * (test.y - points[i].y) / (points[j].y -points[i].y) + points[i].x)) {
				result = !result;
			}
		}
		return result;
	}


}