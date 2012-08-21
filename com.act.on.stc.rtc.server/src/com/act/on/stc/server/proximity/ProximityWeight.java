/**
 * 
 */
package com.act.on.stc.server.proximity;

/**
 * @author NYhobbit
 *
 */
enum ProximityWeight {
	SELECTION_OVERLAP(0.59),
	MIXED_OVERLAP(0.79),
	EDIT(0.7),
	SELECTION(1.0),
	EDIT_OVERLAP(1.0);
	
	private final double weightValue;
	
	ProximityWeight (double d) {
		this.weightValue = d;
	}

	/**
	 * @return the weightValue
	 */
	public double weight() {
		return this.weightValue;
	}
	
}
