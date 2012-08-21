/**
 * Implementation of Valetto et al.'s "proximity" algorithm
 * @author Arber Borici
 * 2011-10-20
 */

package com.act.on.stc.server.proximity.backup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Iterator;

import com.act.on.stc.server.artifacts.CodeContext;
import com.act.on.stc.server.artifacts.CodeContextComparators;

public class ProximityAlgorithm {
	// interaction weights (normalized)
	private final double EDIT_OVERLAP = 1.0;
	private final double MIXED_OVERLAP = 0.79;
	private final double SELECTION_OVERLAP = 0.59;
	private double actualOverlap;
	private double potentialOverlap;
	private double actualCount;

	@SuppressWarnings("unused")
	private double potentialCount;
	
	public ProximityAlgorithm() {
		actualOverlap = 0.0;
		potentialOverlap = 1.0; // to avoid div. by zero
	}	
	
	/**
	 * Given two working sets of edits/selects for two users or tasks,
	 * compute and return the proximity value at the File/Element granularity.
	 */
	public double computeProximity(List<CodeContext> workingSet1, 
			List<CodeContext> workingSet2) 
	{
		if (workingSet1 == null || workingSet2 == null) return 0.0;
		
		actualOverlap = getWSIntersection(workingSet1, workingSet2);
		if (actualOverlap == 0) return 0.0;
		
		potentialOverlap = getWSUnion(workingSet1, workingSet2);
		double roughProximity = actualOverlap/potentialOverlap;
		
		actualCount = getActualOverlapCount(workingSet1, workingSet2);
		actualCount = (actualCount == 0 ? 1 : actualCount);
		
//		potentialCount = getPotentialOverlapCount(workingSet1, workingSet2);
			// NOTE: potentialCount is currently not used anywhere -- oct. 31
			
	//	double scaledProximity = (actualCount/potentialCount)/avgCountAllPairs;
		
		// improvised scaling: multiply by the log of actual overlap counts
		// because the higher the actual overlap, the higher the chances that
		// any two developers are heavily working on the given work item
		return roughProximity * Math.log(actualCount); // scaled proximity by defn.
	}

	/**
	 * Gets the total number of interaction kinds in the actual overlap set.
	 * @param workingSet1
	 * @param workingSet2
	 * @return actual overlap count
	 */
	private int getActualOverlapCount(List<CodeContext> workingSet1,
			List<CodeContext> workingSet2) {
		
		List<CodeContext> processedItems = new ArrayList<CodeContext>();
		int sum = 1; // to avoid Math.Log(0) in the actualCount cal. on constructor
		
		for (CodeContext item1 : workingSet1){			
			/*
			 * Search for item1 in working set 2. If found,
			 * get the Math.Min of selections of item 1 and item 2
			 * get the Math.Min of edits of item 1 and item 2
			 * add the latter two to the sum.
			 * If not found, continue with next item in working set 1.
			 */
			if (!itemExistsIn(item1, processedItems))
				if (itemExistsIn(item1, workingSet2)){
					sum += Math.min(getSelectionCount(item1, workingSet1), 
							getSelectionCount(item1, workingSet2));
					sum += Math.min(getEditCount(item1, workingSet1), 
							getEditCount(item1, workingSet2));
					
					processedItems.add(item1);
				}
			
		}
		
		return sum;
	}
	
	/**
	 * Get the total of edit interactions of item in working set
	 * @param item
	 * @param workingSet
	 * @return
	 */
	private int getEditCount(CodeContext item, List<CodeContext> workingSet) {
		int sum = 0;
		
		for (CodeContext itm : workingSet){
			if (CodeContextComparators.FileNameComparator().compare(item, itm) == 0)
				sum += (itm.wasEdited() ? 1 : 0);
		}
		
		return sum;
	}

	/**
	 * Get the total of selection interactions of item in working set
	 * @param item
	 * @param workingSet
	 * @return
	 */
	private int getSelectionCount(CodeContext item,
			List<CodeContext> workingSet) {
		int sum = 0;
		
		for (CodeContext itm : workingSet){		
			if (CodeContextComparators.FileNameComparator().compare(item, itm) == 0)
				sum += (itm.wasEdited() ? 0 : 1);
		}
		
		return sum;
	}

	/**
	 * Check if item exists in the given working set
	 * @param item
	 * @param workingSet
	 * @return true if item exists in the given working set
	 */
	private boolean itemExistsIn(CodeContext item,
			List<CodeContext> workingSet) {
		
		for (CodeContext item2 : workingSet){			
			if (CodeContextComparators.FileNameComparator().compare(item, item2) == 0) 
				return true;
		}
		
		return false;
	}

	/**
	 * Gets the total number of interaction kinds in the potential overlap set.
	 * @param workingSet1
	 * @param workingSet2
	 * @return potential overlap count
	 */
	@SuppressWarnings("unused")
	private int getPotentialOverlapCount(List<CodeContext> workingSet1,
			List<CodeContext> workingSet2) {
	
		List<CodeContext> processedItems = new ArrayList<CodeContext>();
		int sum = 0;
		
		for (CodeContext item1 : workingSet1){			
			/*
			 * Search for item1 in working set 2. If not found,
			 * count all selects/edits of that item. If found,
			 * get the Math.Max of selections of item 1 and item 2
			 * get the Math.Max of edits of item 1 and item 2
			 * add the latter two to the sum.
			 * Add all processed items to tmpWS.
			 * When done, check if workingSet2 has any unprocessed items,
			 * by looking at tmpWS.
			 */
			if (!itemExistsIn(item1, processedItems)){
				if (itemExistsIn(item1, workingSet2)){
					sum += Math.max(getSelectionCount(item1, workingSet1), 
							getSelectionCount(item1, workingSet2));
					sum += Math.max(getEditCount(item1, workingSet1), 
							getEditCount(item1, workingSet2));
				}
				else {
					sum += getSelectionCount(item1, workingSet1) + 
							getEditCount(item1, workingSet1);
				}
				
				processedItems.add(item1);
			}
			
		}
		
		/* check for unprocessed items in ws2 */
		for (CodeContext item : workingSet2){
			if (!itemExistsIn(item, processedItems)){
				sum += getSelectionCount(item, workingSet2) + 
						getEditCount(item, workingSet2);
				
				processedItems.add(item);
			}
		}
		
		return sum;
	}


	/**
	 * Computes the actual overlap of the intersection
	 * of the task contexts of the two working sets
	 */
	private double getWSIntersection(List<CodeContext> workingSet1,
			List<CodeContext> workingSet2) {
		
		double actualOverlap = 0.0;
		
		/*
		 * Collapse records based on interaction-type redundancy:
		 */
		List<CodeContext> wSet1 = collapseSimilar(workingSet1);
		List<CodeContext> wSet2 = collapseSimilar(workingSet2);
		
		/*
		 * Given a handle in the working set, if it comprises
		 * both an edit and a selection interaction type, then
		 * remove the selection entry from the hash set since
		 * it is subsumed by the edit interaction.
		 */
		wSet1 = removeSubsumedInteraction(wSet1);
		wSet2 = removeSubsumedInteraction(wSet2);
		
		/*
		 * Iterate over ALL items of ws1 and ALL items of ws2.
		 * Determine which ones are common through their handles.
		 * For those in common:
		 * 	if both are "edit" then we have edit overlap = 1
		 * 	if one is "edit" and the other is "select", mixed overlap = .79
		 *  if both are select, selection overlap = .59
		 */
		for (CodeContext ws1 : wSet1){
			for (CodeContext ws2: wSet2){				
				if (CodeContextComparators.FileNameComparator().compare(ws1, ws2) == 0){
					if (ws1.wasEdited() && ws2.wasEdited())
						actualOverlap += this.EDIT_OVERLAP;
					else if (!ws1.wasEdited() && !ws2.wasEdited()) 
						actualOverlap += this.SELECTION_OVERLAP;
					else
						actualOverlap += this.MIXED_OVERLAP;	
				}
			}
		}

		return actualOverlap;
	}

	/**
	 * Computes the potential overlap of the union
	 * of the task contexts of the two working sets
	 */
	private double getWSUnion(List<CodeContext> workingSet1,
			List<CodeContext> workingSet2) {
		
		double potentialOverlap = 0.0;

		/*
		 * Collapse records based on interaction-type redundancy:
		 */
		List<CodeContext> wSet1 = collapseSimilar(workingSet1);
		List<CodeContext> wSet2 = collapseSimilar(workingSet2);	
		
		/*
		 * Iterate over ALL items of ws1 and ALL items of ws2.
		 * If a task context in ws1 is both edit/select, then check
		 * if it exists in ws2 and count as EDIT_OVERLAP.
		 * If a task context in ws1 is only edit, then check if it exists
		 * in ws2. If in ws2 it is edit, then edit overlap, else, mixed.
		 * Same for selection...
		 */
		List<CodeContext> finalWSet = new ArrayList<CodeContext>();
		for (CodeContext ws1 : wSet1){
			
			if (finalWSet.contains(ws1)) continue;
			
			boolean isMatched = false;
			boolean isPairWs1 = false;
			boolean isPairWs2 = false;
			CodeContext ws2 = null;
			CodeContext ws1Pair = null;
			CodeContext ws2Pair = null;
			
			// check if ws1 has another counterpart:
			for (CodeContext wsTemp : wSet1) {				
				if ((CodeContextComparators.FileNameComparator().compare(ws1, wsTemp) == 0) &&
					wsTemp.wasEdited() != ws1.wasEdited()) {
					isPairWs1 = true;
					ws1Pair = wsTemp;
					
					break;
				}
			}
			
			for (CodeContext wsTemp : wSet2){				
				// check if ws1 is in wSet2:
				if (CodeContextComparators.FileNameComparator().compare(ws1, wsTemp) == 0) {
					isMatched = true;
					ws2 = wsTemp;
					
					// check if ws2 has another counterpart:
					for (CodeContext wsTmp : wSet2) {						
						if ((CodeContextComparators.FileNameComparator().compare(ws2, wsTmp) == 0) &&
								wsTmp.wasEdited() != ws2.wasEdited()) {
							isPairWs2 = true;
							ws2Pair = wsTmp;
							
							break;
						}
					}
					
					break;
				}
			}
			
			if (isMatched){
				if (isPairWs1 || isPairWs2)
					potentialOverlap += this.EDIT_OVERLAP;
				else if (!ws1.wasEdited() && !ws2.wasEdited()) 
					potentialOverlap += this.SELECTION_OVERLAP;
				else
					potentialOverlap += this.MIXED_OVERLAP;
				
				// mark ws1 and ws2 and their counterparts as processed
				finalWSet.add(ws1);
				if (ws1Pair != null) finalWSet.add(ws1Pair);
				finalWSet.add(ws2);
				if (ws2Pair != null) finalWSet.add(ws2Pair);

			}
		}
		
		// check the two lists to see if there are unique elements:
		// i.e. if an element has been added to list finalWSet, then
		// it has already been processed.
		
		wSet1 = removeSubsumedInteraction(wSet1);
		wSet2 = removeSubsumedInteraction(wSet2);
		for (CodeContext ws : wSet1){
			if (!finalWSet.contains(ws)){
				if (ws.wasEdited()) potentialOverlap += this.EDIT_OVERLAP;
				else potentialOverlap += this.SELECTION_OVERLAP;
			}
		}
		
		for (CodeContext ws : wSet2){
			if (!finalWSet.contains(ws)){
				if (ws.wasEdited()) potentialOverlap += this.EDIT_OVERLAP;
				else potentialOverlap += this.SELECTION_OVERLAP;
			}
		}	
		
		return potentialOverlap;
	}


	/**
	 * Remove selection interaction-type task contexts iff
	 * the same task context comprises an edit interaction type.
	 */
	private List<CodeContext> removeSubsumedInteraction(
			List<CodeContext> workingSet) {
		
		// ws will contain the contexts we want to retain
		List<CodeContext> ws = new ArrayList<CodeContext>();
		
		Iterator<CodeContext> lstItr = workingSet.iterator();
		
		CodeContext curr = null, next = null;
		if (lstItr.hasNext()) curr = lstItr.next();
		if (lstItr.hasNext()) next = lstItr.next();	
		while (curr != null && next != null){			
			if (CodeContextComparators.FileNameComparator().compare(curr, next) == 0) {
				// if current is edit, then remove next, else remove current
				if (curr.wasEdited()) {
					ws.add(curr);
					
					if (lstItr.hasNext())
						curr = lstItr.next();
					else
						curr = null;
					
					if (lstItr.hasNext())
						next = lstItr.next();
					else {
						if (curr != null) ws.add(curr);
						next = null;
					}
				}
				else {
					ws.add(next);
					
					if (lstItr.hasNext())
						curr = lstItr.next();
					else
						curr = null;
					
					if (lstItr.hasNext())
						next = lstItr.next();
					else {
						if (curr != null) ws.add(curr);
						next = null;
					}
				}
			}
			else {
				ws.add(curr);
				curr = next;
				
				if (lstItr.hasNext())
					next = lstItr.next();
				else {
					if (curr != null) ws.add(curr);
					next = null;
				}
			}
			
		}

		return ws;
	}
	
	/**
	 * Remove interaction-type redundancy from the working sets.
	 * The collapseSimilar algorithm requires one precondition:
	 * 	the list is sorted lexicographically per the FileName/Element field.
	 */
	private List<CodeContext> collapseSimilar(
			List<CodeContext> workingSet) {
		
		// ws will store the collapsed working set
		List<CodeContext> ws = new ArrayList<CodeContext>();
		CodeContext taskContextEvent = null;

		// sort the list lexicographically based on the chosen granularity:			
		Collections.sort(workingSet, CodeContextComparators.FileNameComparator());
		
		
		// iterate over all workingSet elements
		Iterator<CodeContext> lstItr = workingSet.iterator();
		taskContextEvent = lstItr.next();
		ws.add(taskContextEvent);
		while (lstItr.hasNext()){
			CodeContext next = lstItr.next();
			
			// same handle?
			if (CodeContextComparators.FileNameComparator().compare(next, taskContextEvent) == 0){
				// if same handle, then collapse interaction types
				if ((next.wasEdited() && !taskContextEvent.wasEdited()) ||
					(!next.wasEdited() && taskContextEvent.wasEdited())){
					taskContextEvent = next;
					
					/*
					 * The following mitigates this scenario:
					 * 	Suppose you have three task context sequentially as:
					 * 		"e.java"  edit
					 * 		"e.java"  select
					 * 		"e.java"  edit
					 * Then, the collapse will compare the first two and
					 * add them both, then it will compare the last two and
					 * add the third as well. This is because we sort
					 * lexicographically ONLY per the FileName, rather than
					 * per the interaction types as well.
					 * NOTE: TimeStamp may make similar contexts distinct...
					 * 
					 */
					boolean taskIsContained = false;
					for (CodeContext cs : ws) {						
						if ((CodeContextComparators.FileNameComparator().compare(cs, taskContextEvent) == 0)
							&& cs.wasEdited() == taskContextEvent.wasEdited()){
							taskIsContained = true;
							break;
						}		
					}
					
					if (!taskIsContained) ws.add(taskContextEvent);
				}
			}
			else {
				taskContextEvent = next;
				ws.add(taskContextEvent);
			}
		}
		
		return ws;
	}

}