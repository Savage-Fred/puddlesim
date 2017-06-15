package org.fog.utils;

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