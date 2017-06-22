package org.fog.utils;
import java.io.*;       /* Error Handling */
import java.util.*;
import java.util.Arrays;
import java.util.List;
/**
 * <h1> Polygon Class </h1>
 * Defines the area of Polygon for a clusterhead.
 * Contains methods to check if a point is within the area of Polygon for a clusterhead.
 * Works on any set of points that form a polygon. 
 * 
 * @author  William McCarty
 * @version 1.0.0
 * @since   June 14, 2017
 */
public class Polygon {
    ///////////////////////////////////////////////////////
    /////////////////// Polygon FIELDS 
    ///////////////////////////////////////////////////////
    private Point[] points;     /* A collection of points defines the boundaries of a polygon */
    private int     numberOfPoints = 0;
    private String[] polygonTypes = {"Point", "Line", "Triangle", "Quadrilateral", "Pentagon", "Hexagon", "Heptagon", "Octagon", "Nonagon", "Decagon"};
    
    /////////////////////////////////////////////////////////////
    /////////////////// Polygon CONSTRUCTORS 
    /////////////////////////////////////////////////////////////
    /**
     * <b>Polygon Constructor</b>
     * <p>Fills an array with points that define the boundary of the polygon. 
     * 
     * @param xin double array of the x values 
     * @param yin double array of the y values 
     *      <p><i>note: xin[i],yin[i] become point i. The arrays must match</i>
     * @throws IllegalArgumentException if the arrays do not match 
     */ 
    public 
    Polygon(double[] xin, double[] yin) throws IOException 
    {
        int sizex = xin.length;
        int sizey = yin.length;

        if (sizex <= 2 || sizey <= 2) {
            throw new IllegalArgumentException ("Error: Not enough points to be a polygon");
        }

        if (sizex > sizey) {
            throw new IllegalArgumentException ("Error: More x values than y values");
        }
        if (sizey > sizex) {
            throw new IllegalArgumentException ("Error: More y values than x values");
        }

        points = new Point[sizex];
        for (int i = 0; i < sizex;i++)
        {
            points[i] = new Point(xin[i], yin[i]);
        }

        numberOfPoints = sizex;
    }

    /**
     * <b>Polygon Constructor</b> 
     * <p>Takes a 2d array that defines the cartesion coordinates as follows:
     * <p>[x coordinate 1][y coordinate 1]
     * <p>[x coordinate 2][y coordinate 2]...
     *
     * @param xyin 2d array of cartestion coordinates 
     * @throws IllegalArgumentException Not a polygon
     */ 
    public 
    Polygon(double[][] xyin) throws IOException 
    {
    	double []temp = xyin[0];
        numberOfPoints = xyin[0].length;
        if (numberOfPoints <= 2)
            throw new IllegalArgumentException ("Error: A polygon must contain at least 3 points");
        points = new Point[numberOfPoints];
        //Polygon(xyin[0],xyin[1]);
    }
    /**
     * <b>Polygon Constructor</b> 
     *
     * @param points_in array of points
     * @throws IllegalArgumentException Not a polygon
     */
    public 
    Polygon(Point[] points_in) throws IOException
    {
        numberOfPoints = count(points_in);
        if (numberOfPoints <= 2) 
            throw new IllegalArgumentException ("Error: A polygon must contain at least 3 points");
        setPoints(points_in);
    }

    ////////////////////////////////////////////////////////
    /////////////////// GETTERS AND SETTERS
    /////////////////////////////////////////////////////////


	public void setPoints(Point[] points_in)    { this.points = Arrays.copyOf(points_in, points_in.length); }
    public int  getNumberOfPoints()             { return numberOfPoints; }
    public void setNumberOfPoints(int c)        {this.numberOfPoints = c;}   

    ////////////////////////////////////////////////////////
    /////////////////// Polygon METHODS 
    ////////////////////////////////////////////////////////

    /**
     * <b>Contains</b>
     * <p>Method to check if a point is contained within a polygon 
     * <p>See: https://stackoverflow.com/questions/8721406/how-to-determine-if-a-point-is-inside-a-2d-convex-polygon
     * <p>See: https://wrf.ecse.rpi.edu//Research/Short_Notes/pnpoly.html
     *
     * @param test the point to check 
     * @return True if the point is inside the polygon 
     */
    public 
    boolean contains(Point test) 
    {
        int i;
        int j;
        boolean result = false;
        for (i = 0, j = numberOfPoints-1; i < numberOfPoints; j = i++) {
            if ((points[i].y > test.y) != (points[j].y > test.y) &&
                (test.x < (points[j].x - points[i].x) * (test.y - points[i].y) / (points[j].y -points[i].y) + points[i].x)) {
                    result = !result;
                }
        }
        return result;
    }

    /**
     * <b>Contains</b>
     * <p>Overloaded contains method using x and y coordinates instead of a point object 
     * 
     * @param   xin     the x value of the test point 
     * @param   yin the y value of the test point 
     * 
     * @return `true if the point is contained within the polygon. False otherwise
     */ 
    public 
    boolean contains(double xin, double yin)
    {
        return contains (new Point(xin, yin));
    }

    /**
     * <b>Contains</b>
     * <p>Overloaded method to check within a different polygon
     * 
     * @param   test    Point value to be checked 
     * @param   points  new polygon to be checked 
     * 
     * @return `true if the point is contained within the polygon. False otherwise
     */ 
    public 
    boolean contains(Point test, Point[] points)
    {
        setPoints(points);
        return contains(test);
    }

    /**
     * count
     * <p> Method to count the number of elements in an array 
     * @param xin of objects
     * @return the number of nonNull elements in the array
     */
    private 
    int count(Object[] xin) 
    {
        ArrayList<Object> list = new ArrayList<Object>(Arrays.asList(xin));
        list.trimToSize();
        return list.size();
        
    }


    /**
     * toString
     * <p>Simple method to output the polygon to a string. Gives the polygon type and its points
     * @return formatted string 
     */
    @Override 
    public 
    String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Shape: ");
        if (numberOfPoints <= 10)
            buffer.append(polygonTypes[numberOfPoints] + "\n");
        else if (numberOfPoints > 10)
            buffer.append(numberOfPoints + "-gon \n");
        buffer.append("Points: ");
        for (Point p: points)
        {
            buffer.append("{" + p.toString() + " } ");
        }

        return buffer.toString();

    }
}