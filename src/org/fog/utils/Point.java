package org.fog.utils;

/**
 * <h1> Point Class </h1>
 * Defines a point in cartesion space 
 *
 * @author 	William McCarty
 * @version 1.0.0
 * @since 	June 14, 2017
 */ 
class Point {
	//////////////////////////////////////////
	////////////// 	FIELDS 
	//////////////////////////////////////////
	public double x;
	public double y;
	

	//////////////////////////////////////////
	////////////// CONSTRUCTORS 
	//////////////////////////////////////////

	/**
	 * <b> Basic constructor for Point </b>
	 *
	 * @param x x coordinate 
	 * @param y y coordinate
	 */
	public Point(double x, double y) {
		this.setx(x);
		this.sety(y);
	}


	//////////////////////////////////////////
	////////////// GETTERS AND SETTERS 
	//////////////////////////////////////////
	public void 	setx(double input) {this.x = input;}
	public void 	sety(double input) {this.y = input;}

	public double 	getx() {return this.x;}
	public double 	gety() {return this.y;}
}