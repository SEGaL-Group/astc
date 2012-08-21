/**
 * Collection of granularity comparators for task contexts.
 * @author Arber Borici
 * 2011-11-04
 */

package com.act.on.stc.server.artifacts;

import java.util.Comparator;

public class CodeContextComparators {
	
	// compare at the file name granularity:
	public static Comparator<CodeContext> FileNameComparator() {
		return new CodeContextComparator
				(CodeContextComparator.GranularityLevel.File);
	}
	
	// compare at the class name granularity:
	public static Comparator<CodeContext> ClassNameComparator() {
		return new CodeContextComparator
				(CodeContextComparator.GranularityLevel.Class);
	}
	
	// compare at the method name granularity:
	public static Comparator<CodeContext> MethodNameComparator() {
		return new CodeContextComparator
				(CodeContextComparator.GranularityLevel.Method);
	}
	
}
