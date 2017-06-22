package org.fog.utils;
/**
 * This class contains the basic elements for a rectangle placed in a cartesian coordinate system.
 * 
 * @author AviRynderman
 * @since NSF REU 2017 - Parallel and Distributed Computing
 */
public class Rectangle {
	/**
	 * Width of the rectangle.
	 */
	private double width;
	/**
	 * Height of the rectangle.
	 */
	private double height;
	/**
	 * x value for bottom left corner of rectangle.
	 */
	private double x;
	/**
	 * y value for bottom left corner of rectangle.
	 */
	private double y;
	
	/**
	 * Constructor function for rectangle. Sets origin as (x,y), height as height, and width as width.
	 * 
	 * @param width is the width of the defined space.
	 * @param height is the height of the defined space.
	 * @param x x-coordinate for origin.
	 * @param y y-coordinate for origin.
	 */
	public Rectangle(double width, double height, double x, double y){
		if(width <= 0) width = 1;
		else if (height <= 0) height = 1;
		
		this.width = width;
		this.height = height;
		this.x = x;
		this.y = y;
	}
	/**
	 * Constructor function for rectangle. Sets origin as (0,0), height as height, and width as width.
	 * 
	 * @param width is the width of the defined space.
	 * @param height is the height of the defined space.
	 */
	public Rectangle(double width, double height){
		if(width <= 0) width = 1;
		else if (height <= 0) height = 1;
		
		this.width = width;
		this.height = height;
		this.x = 0;
		this.y = 0;
	}
	/**
	 * Checks whether a given point (x,y) is inside the rectangle. Note, landing on the
	 * boundary is considered inside the rectangle and will return true.
	 * @param x x-component of point.
	 * @param y y-component of point.
	 * @return false if the point is outside the rectangle, true if it's on or inside the rectangle.
	 */
	public boolean isInside(double x, double y){
		if(x > this.x + this.width || x < this.x)
			return false;
		if(y > this.y + this.height || y < this.y)
			return false;
		return true;
	}
	
	public double getWidth() {
		return width;
	}
	public void setWidth(double width) {
		this.width = width;
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
}
