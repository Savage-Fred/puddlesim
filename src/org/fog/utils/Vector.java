package org.fog.utils;

import java.util.Random;

/**
 * This class is used to hold a 2D vector.   
 * 
 * @author Avi Rynderman
 * @since NSF REU 2017 - Parallel and Distributed Computing
 */

public class Vector {
	/**
	 * x-component of the vector.
	 */
	private double xComponent;
	/**
	 * y-component of the vector.
	 */
	private double yComponent;
	/**
	 * public constructor.
	 * @param xComponent 
	 * @param yComponent
	 */
	public Vector(double xComponent, double yComponent){
		this.xComponent = xComponent;
		this.yComponent = yComponent;
	}
	/**
	 * Creates a random vector.
	 * @param scalar is the user chosen magnitude of the vector.
	 * @return a vector with x and y components.
	 */
	public Vector(double scalar){
		Random rand = new Random();
		this.xComponent = rand.nextInt(3)-1;
		this.yComponent = rand.nextInt(3)-1;
		double magnitude = Math.sqrt(xComponent*xComponent + yComponent*yComponent);
		if(magnitude == 0){
			xComponent = 0;
			yComponent = 0;
		}
		else {
			xComponent = scalar * xComponent / magnitude;
			yComponent = scalar * yComponent / magnitude;
		}
	}
	/**
	 * Creates a random unit vector. 
	 * @return a unit vector with x and y components.
	 */
	public Vector(){
		Random rand = new Random();
		this.xComponent = rand.nextInt(3)-1;
		this.yComponent = rand.nextInt(3)-1;
		double magnitude = Math.sqrt(xComponent*xComponent + yComponent*yComponent);
		if(magnitude == 0){
			xComponent = 0;
			yComponent = 0;
		}
		else {
			xComponent = xComponent / magnitude;
			yComponent = yComponent / magnitude;
		}
	}
	
	/**
	 * This function first turns the vector into a unit vector, then multiplies the components the the scalar.
	 * @param scalar is the new scalar.
	 */
	public void rescaleVector(double scalar){
		double magnitude = Math.sqrt(xComponent*xComponent + yComponent*yComponent);
		if(magnitude == 0)
			return;
		xComponent = scalar * xComponent / magnitude;
		yComponent = scalar * yComponent / magnitude;
	}
	/**
	 * Calculates the magnitude of the vector.
	 * @return Magnitude of the vector.
	 */
	public double getMagnitude() {
		double magnitude;
		magnitude = Math.sqrt(this.xComponent*this.xComponent + this.yComponent*this.yComponent);
		return magnitude;
	}
	/**
	 * Calculates the angle at which the vector is pointing.
	 * @return The direction of movement as a radian unit. 
	 */
	public double getAngleRad() {
		return Math.atan2(xComponent, yComponent);
	}
	public double getxComponent() {
		return this.xComponent;
	}
	public void setxComponent(double xComponent) {
		this.xComponent= xComponent;
	}
	public double getyComponent() {
		return this.yComponent;
	}
	public void setyComponent(double yComponent) {
		this.yComponent = yComponent;
	}
}