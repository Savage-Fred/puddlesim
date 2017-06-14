package org.fog.utils;
import java.io.*;		/* Error Handling */

/**
 * <h1> Coverage Class </h1>
 * Defines the area of coverage for a clusterhead 
 * Contains methods to check if a point is within the area of coverage for a clusterhead 
 * Works on any set of points that form a polygon. 
 * 
 * @author 	William McCarty
 * @version 1.0.0
 * @since 	June 14, 2017
 */
public class Coverage {
	///////////////////////////////////////////////////////
	/////////////////// COVERAGE FIELDS 
	///////////////////////////////////////////////////////

	
	private final 	Point[]  points; 	/* A collection of points defines the boundaries of a polygon */
	private 		double[] xvals;		/* array of the x values that define the polygon */
	private 		double[] yvals; 	/* array of the y values that define the polygon */


	/////////////////////////////////////////////////////////////
	/////////////////// COVERAGE CONSTRUCTORS 
	/////////////////////////////////////////////////////////////
	/**
	 * <b>Coverage Constructor</b>
	 * Fills an array with points that define the boundary of the polygon. 
	 * 
	 * @param xin double array of the x values 
	 * @param yin double array of the y values 
	 * 		note: xin[i],yin[i] become point i. The arrays must match
	 * @throws IllegalArgumentException if the arrays do not match 
	 */ 
	public Coverage(double[] xin, double[] yin) throws IOException {
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
	 * <b>Coverage Constructor</b> 
	 * <p>Takes a 2d array that defines the cartesion coordinates as follows 
	 * <p>[x coordinate 1][y coordinate 1]
	 * <p>[x coordinate 2][y coordinate 2]...
	 *
	 * @param xyin 2d array of cartestion coordinates 
	 */ 
	public Coverage(double[][] xyin) {
		int numelements = xyin[0].length;
		points = new Point[numelements];
	}


	////////////////////////////////////////////////////////
	/////////////////// COVERAGE METHODS 
	////////////////////////////////////////////////////////

	/**
	 * <b>Contains</b>
	 * <p>Method to check if a point is contined within a polygon 
	 * <p>See: https://stackoverflow.com/questions/8721406/how-to-determine-if-a-point-is-inside-a-2d-convex-polygon
	 * <p>See: https://wrf.ecse.rpi.edu//Research/Short_Notes/pnpoly.html
	 *
	 * @param test the point to check 
	 * 
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

	/**
	 * <b>Contains</b>
	 * Overloaded contains method using x and y coordinates instead of a point object 
	 * 
	 * @param 	xin 	the x value of the test point 
	 * @param 	yin	the y value of the test point 
	 * 
	 * @return `true if the point is contained within the polygon. False otherwise
	 */ 
	public boolean contains(double xin, double yin)
	{
		return contains (new Point(xin, yin));
	}


}