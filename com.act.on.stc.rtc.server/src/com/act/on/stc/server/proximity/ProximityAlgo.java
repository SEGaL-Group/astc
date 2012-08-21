package com.act.on.stc.server.proximity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.act.on.stc.common.utilities.Pair;
import com.act.on.stc.server.artifacts.CodeContext;
import com.act.on.stc.server.artifacts.Task;
import com.act.on.stc.server.artifacts.User;

public class ProximityAlgo implements IPairProximity, IEgoProximity {

	private enum ProximityGranularity {
		FILE_LEVEL,
		JAVA_ELEMENT_LEVEL; //finer-grained: method or attribute 
	}
	/**
	 * indicates the granularity at which proximity is computed
	 */
	private ProximityGranularity granularity = ProximityGranularity.FILE_LEVEL;
	
	/**
	 * precision of the proximity scores
	 */
	private final int DECIMALS = 2;
	
	public ProximityGranularity getGranularity() { return granularity; }
	public void setGranularity(ProximityGranularity pg) { this.granularity = pg; }
	
	/*
	 * Computation of Proximity for pair of items
	 * @see com.act.on.stc.server.proximity.IPairProximity
	 */
	@Override
	public ProximityDescriptor computeProximity(User u1, User u2) {
		// TODO Auto-generated method stub
		ProximityDescriptor pd = new ProximityDescriptor();
		return pd;
	}

	@Override
	public ProximityDescriptor computeProximity(Task wi1, Task wi2) {
		List<CodeContext> c1 = getContextEvents(wi1);
		List<CodeContext> c2 = getContextEvents(wi2);
		int totaItems = 0;
		int overlappingItems = 0;
		Hashtable<String, OverlapItem> allElements = new Hashtable<String, OverlapItem>();	
		OverlapItem oi;
		String elementID;
		
		ProximityDescriptor pd = new ProximityDescriptor();
		ArrayList<Task> relatedTasks = new ArrayList<Task>();
		
		//go through the 2 working sets one by one: first one ...
		relatedTasks.add(wi1);
		for(CodeContext eventC1 : c1) {
			if(granularity == ProximityGranularity.FILE_LEVEL)
				elementID = eventC1.getFileName();
			else
				elementID = eventC1.getHandle();
			
			if (allElements.containsKey(elementID) == false) {
				oi = new OverlapItem(elementID);
				allElements.put(elementID, oi);
			}
			else 
				oi = allElements.get(elementID);
			
			if(eventC1.wasEdited())
				oi.setEdit1(oi.getEdit1()+1);
			else
				oi.setSelection1(oi.getSelection1()+1);						
		}
		// ... and now second one
		relatedTasks.add(wi2);
		for(CodeContext eventC2 : c2) {
			if(granularity == ProximityGranularity.FILE_LEVEL)
				elementID = eventC2.getFileName();
			else
				elementID = eventC2.getHandle();
			
			if (allElements.containsKey(elementID) == false) {
				oi = new OverlapItem(elementID);
				allElements.put(elementID, oi);
			}
			else 
				oi = allElements.get(elementID);
			
			if(eventC2.wasEdited())
				oi.setEdit1(oi.getEdit1()+1);
			else
				oi.setSelection1(oi.getSelection1()+1);
		}
		
		totaItems = allElements.keySet().size();
		List<OverlapItem> actualOverlaps = new ArrayList<OverlapItem>();
		for (OverlapItem overlap : allElements.values()) {
			if(overlap.isActualOverlap())
				actualOverlaps.add(overlap);
		}
		overlappingItems = actualOverlaps.size();
		pd.setOverlappingItems(actualOverlaps);		
		pd.setPreliminaryDistance(computeRawProximity(actualOverlaps));
		pd.setInvolvedTasks(relatedTasks);
		
		return pd;
	}
	
	private double computeRawProximity(List<OverlapItem> overlaps) {
		double potential = 0;
		double actual = 0;
		
		for (OverlapItem item : overlaps) {
			if (item.getEdit1() > 0 && item.getEdit2() > 0) {
				potential += ProximityWeight.EDIT_OVERLAP.weight();
				actual += ProximityWeight.EDIT_OVERLAP.weight();
			} else if (item.getEdit1() > 0 || item.getEdit2() > 0) {
				potential += ProximityWeight.EDIT_OVERLAP.weight();
				if (item.getSelection1() > 0 && item.getSelection2() > 0)
					actual += ProximityWeight.MIXED_OVERLAP.weight();
			} else {
				potential += ProximityWeight.SELECTION_OVERLAP.weight();
				if (item.getSelection1() > 0 && item.getSelection2() > 0)
					actual += ProximityWeight.SELECTION_OVERLAP.weight();
			}	
		}
		
		return roundProximityScore(actual/potential);
	}
	

	private double roundProximityScore(double score) {
		String theFormat = "#";
		
		if (this.DECIMALS > 0) {
			theFormat += ".";
			for (int i = 0; i < DECIMALS; i++)
				theFormat += "#";
		}
		
		DecimalFormat df = new DecimalFormat(theFormat);
		return Double.valueOf(df.format(score));
	}
	
	private int countOverlappingEvents(List<OverlapItem> overlaps) {
		int overlapCount = 0;
		//calculate number of overlapping events
		for (OverlapItem oi : overlaps) {
			overlapCount = overlapCount + Math.min(oi.getEdit1(), oi.getEdit2())
					+ Math.min(oi.getSelection1(), oi.getSelection2());
		}

		return overlapCount;
	}
	
	@Override
	public ProximityDescriptor computeProximity(CodeContext ctx1, CodeContext ctx2) {
		// TODO Auto-generated method stub
		ProximityDescriptor pd = new ProximityDescriptor();
		return pd;
	}

	/*
	 * Computation of proximity from an ego-network perspective
	 * @see com.act.on.stc.server.proximity.IEgoProximity
	 */
	@Override
	public List<Pair<CodeContext, Double>> computeProximity(CodeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ProximityDescriptor> computeProximity(Task wi) {
		// TODO must retrieve FULL set of open Tasks somehow
		// (directly from the DB)
		List<Task> relatedTasks = new ArrayList<Task>();
		List<ProximityDescriptor> BinaryProximities = new ArrayList<ProximityDescriptor>();
		int totalEvents = 0;
		
		for(Task t : relatedTasks) {
			if (!(t.getUUID().equals(wi.getUUID()))) { // TODO: is this the criterion for equals() between Tasks ??
				ProximityDescriptor pairProximity = computeProximity(wi, t);
				totalEvents += countOverlappingEvents(pairProximity.getOverlappingItems());
				BinaryProximities.add(pairProximity);
			}
		}
		
		double averageEvents = totalEvents / relatedTasks.size();
		
		for(ProximityDescriptor pd: BinaryProximities) {
			double scalingFactor = pd.getOverlappingItems().size() / averageEvents;
			pd.setScalingFactor(scalingFactor);
			pd.setScaledDistance(roundProximityScore(pd.getPreliminaryDistance() * scalingFactor));
		}
		
		return BinaryProximities;
	}

	@Override
	public List<Pair<User, Double>> computeProximity(User u) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private List<CodeContext> getContextEvents(Task t) {
		List<CodeContext> ret = new ArrayList<CodeContext>();
		// TODO must retrieve the CodeContexts associated to Task t somehow 
		// (directly from the DB?)
		
		return ret;
	}
}
