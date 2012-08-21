/**
 * Implementation of a granularity comparator for task contexts.
 * @author Arber Borici
 * 2011-11-04
 */

package com.act.on.stc.server.artifacts;

import java.util.Comparator;

final class CodeContextComparator implements Comparator<CodeContext> {
	public enum GranularityLevel{File, Class, Method};
	private GranularityLevel granularity;
	
	CodeContextComparator(GranularityLevel granularityLevel){
		setGranularityLevel(granularityLevel);
	}
	
	public void setGranularityLevel(GranularityLevel granularityLevel){
		this.granularity = granularityLevel;
	}
	
	@Override
	/**
	 * Returns 0 if granularity levels match; -1/1 otherwise.
	 */
	public int compare(CodeContext wi1, CodeContext wi2){
		
		if (granularity == GranularityLevel.File)
			return wi1.getFileName().compareTo(wi2.getFileName());
		else if (granularity == GranularityLevel.Class)
			return wi1.getClassName().compareTo(wi2.getClassName());
		else if (granularity == GranularityLevel.Method)
			return wi1.getMethodName().compareTo(wi2.getMethodName());

		return -1;
	}
}

