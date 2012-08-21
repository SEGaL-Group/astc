/**
 * 
 */
package com.act.on.stc.server.proximity;

import java.util.List;

import com.act.on.stc.server.artifacts.Task;

/**
 * @author NYhobbit
 *
 */
class ProximityDescriptor {
	private double preliminaryDistance = 0;
	private double scalingFactor = 1;
	private double scaledDistance = 0;
	private List<OverlapItem> overlappingItems;
	private List<Task> involvedTasks;
	
	/**
	 * @return the preliminaryDistance
	 */
	public double getPreliminaryDistance() {
		return preliminaryDistance;
	}
	/**
	 * @param preliminaryDistance the preliminaryDistance to set
	 */
	public void setPreliminaryDistance(double preliminaryDistance) {
		this.preliminaryDistance = preliminaryDistance;
	}
	
	/**
	 * @return the scaledDistance
	 */
	public double getScaledDistance() {
		return scaledDistance;
	}
	/**
	 * @param scaledDistance the scaledDistance to set
	 */
	public void setScaledDistance(double scaledDistance) {
		this.scaledDistance = scaledDistance;
	}
	
	/**
	 * @return the overlappingItems
	 */
	public List<OverlapItem> getOverlappingItems() {
		return overlappingItems;
	}
	/**
	 * @param overlappingItmess the overlappingEvents to set
	 */
	public void setOverlappingItems(List<OverlapItem> overlappingItems) {
		this.overlappingItems = overlappingItems;
	}
	
	/**
	 * @return the involvedTasks
	 */
	public List<Task> getInvolvedTasks() {
		return involvedTasks;
	}
	/**
	 * @param involvedTasks the involvedTasks to set
	 */
	public void setInvolvedTasks(List<Task> involvedTasks) {
		this.involvedTasks = involvedTasks;
	}
	
	/**
	 * @return the scalingFactor
	 */
	public double getScalingFactor() {
		return scalingFactor;
	}
	/**
	 * @param scalingFactor the scalingFactor to set
	 */
	public void setScalingFactor(double scalingFactor) {
		this.scalingFactor = scalingFactor;
	}
	
}
