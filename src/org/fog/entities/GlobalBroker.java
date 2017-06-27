package org.fog.entities;

import java.util.List;

public class GlobalBroker extends FogBroker {
	
	protected List<Integer> puddleHeadIds; 
	
	public GlobalBroker(String name) throws Exception {
		super(name);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the puddleHeadIds
	 */
	public List<Integer> getPuddleHeadIds() {
		return puddleHeadIds;
	}

	/**
	 * @param puddleHeadIds the puddleHeadIds to set
	 */
	public void setPuddleHeadIds(List<Integer> puddleHeadIds) {
		this.puddleHeadIds = puddleHeadIds;
	}

}
