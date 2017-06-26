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

	protected List<Integer> puddleDevices;
	protected Map<Integer,FogDeviceCharacteristics> puddleDevicesCharacteristics;
	protected int parentId; 
	protected List<Integer> puddleBuddies; 
	protected List<Integer> childrenIds;
	private int level; 
	protected Polygon areaOfCoverage; 
	/**
	 * @param name
	 * @throws Exception
	 */
	public PuddleHead2(String name) throws Exception {
		super(name);
		// TODO Auto-generated constructor stub
		//initialize the lists and map
		setPuddleDevices(new ArrayList<Integer>());
		setPuddleDevicesCharacteristics(new HashMap<Integer, FogDeviceCharacteristics>());
		setPuddleBuddies(new ArrayList<Integer>()); 
		setChildrenIds(new ArrayList<Integer>());
	}
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

}
