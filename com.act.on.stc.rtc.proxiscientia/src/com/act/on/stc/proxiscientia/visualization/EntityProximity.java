package com.act.on.stc.proxiscientia.visualization;

/**
 * This class will store the entity (user or task) names of the two entities (user or task) and their proximity.
 */
public class EntityProximity {
	private String firstEntityName;
	private String secondEntityName;
	private Double proximity;
	
	public EntityProximity(){
		
	}
	
	public EntityProximity(String one, String two, Double len){
		firstEntityName = one;
		secondEntityName = two;
		proximity = len;
	}
	
	public void setFirstEntityName(String firstName){
		firstEntityName = firstName;
	}
	
	public String getFirstEntityName(){
		return firstEntityName;
	}
	
	public String getSecondEntityName(){
		return secondEntityName;
	}
	
	public void setSecondEntityName(String secondUser){
		secondEntityName = secondUser;
	}
	
	public Double getProximityValue(){
		return proximity;
	}
	
	public void setProximityValue(Double prox){
		proximity = prox;
	}
	
}
