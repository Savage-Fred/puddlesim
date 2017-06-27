/**
 * 
 */
package org.fog.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fog.utils.Polygon;

/**
 * @author Jessica Knezha
 * @version PuddleSim 1.0
 * @since June 26, 2017
 *
 */
public class PuddleHead2 extends FogBroker {
	
	private static final String LOG_TAG = "PUDDLE_HEAD";

	protected List<Integer> puddleDevices;
	protected Map<Integer,FogDeviceCharacteristics> puddleDevicesCharacteristics;
	protected int parentId; 
	protected List<Integer> puddleBuddies; 
	protected List<Integer> childrenIds;
	private int level; 
	protected Polygon areaOfCoverage;
	protected double latitude; 
	protected double longitude; 
	
	/**
	 * @param name
	 * @throws Exception
	 */
	public PuddleHead2(String name) throws Exception {
		super(name);
		//initialize the lists and map
		setPuddleDevices(new ArrayList<Integer>());
		setPuddleDevicesCharacteristics(new HashMap<Integer, FogDeviceCharacteristics>());
		setPuddleBuddies(new ArrayList<Integer>()); 
		setChildrenIds(new ArrayList<Integer>());
	}
	
	//TODO look into this functionality for PuddleSim extension
	/**
	 * We may want to implement this for our own functionality in the future. 
	public void submitApplication(){
		
	}
	protected void deployApplication(){
		
	}
	*/
	
	
	
	
	///////////////////////Getters and Setters////////////////////////////////////////
	/**
	 * @return the puddleDevices
	 */
	public List<Integer> getPuddleDevices() {
		return puddleDevices;
	}
	/**
	 * @param puddleDevices the puddleDevices to set
	 */
	public void setPuddleDevices(List<Integer> puddleDevices) {
		this.puddleDevices = puddleDevices;
	}
	//add a node to the puddleDevices
	public void addPuddleDevice(int deviceId){
		puddleDevices.add(deviceId);
	}
	//remove a node from puddleDevices
	public void removePuddleDevice(int deviceId){
		puddleDevices.remove(deviceId);
	}
	//check if a node is one of my puddle devices
	public boolean isMyPuddleDevice(int deviceId){
		return puddleDevices.contains(deviceId);
	}
	/**
	 * @return the puddleDevicesCharacteristics
	 */
	public Map<Integer, FogDeviceCharacteristics> getPuddleDevicesCharacteristics() {
		return puddleDevicesCharacteristics;
	}
	/**
	 * @param puddleDevicesCharacteristics the puddleDevicesCharacteristics to set
	 */
	public void setPuddleDevicesCharacteristics(Map<Integer, FogDeviceCharacteristics> puddleDevicesCharacteristics) {
		this.puddleDevicesCharacteristics = puddleDevicesCharacteristics;
	}
	/**
	 * @return the parentId
	 */
	public int getParentId() {
		return parentId;
	}
	/**
	 * @param parentId the parentId to set
	 */
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	/**
	 * 
	 * @return the puddleBuddies
	 */
	public List<Integer> getPuddleBuddies() {
		return puddleBuddies;
	}
	/**
	 * @param puddleBuddies the puddleBuddies to set
	 */
	public void setPuddleBuddies(List<Integer> puddleBuddies) {
		this.puddleBuddies = puddleBuddies;
	}
	//add a node to the puddleBuddies
	public void addPuddleBuddy(int buddyId){
		puddleBuddies.add(buddyId);
	}
	//remove a node from puddleBuddies
	public void removePuddleBuddy(int buddyId){
		puddleBuddies.remove(buddyId);
	}
	//check if a node is a puddle buddy 
	public boolean isMyPuddleBuddy(int buddyId){
		return puddleBuddies.contains(buddyId);
	}
	/**
	 * @return the childrenIds
	 */
	public List<Integer> getChildrenIds() {
		return childrenIds;
	}
	/**
	 * @param childrenIds the childrenIds to set
	 */
	public void setChildrenIds(List<Integer> childrenIds) {
		this.childrenIds = childrenIds;
	}
	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}
	/**
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}
	/**
	 * @return the areaOfCoverage
	 */
	public Polygon getAreaOfCoverage() {
		return areaOfCoverage;
	}
	/**
	 * @param areaOfCoverage the areaOfCoverage to set
	 */
	public void setAreaOfCoverage(Polygon areaOfCoverage) {
		this.areaOfCoverage = areaOfCoverage;
	}
	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}
	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}
	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

}
