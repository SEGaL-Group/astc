package com.act.on.stc.server.proximity;

/**
 * @author NYhobbit
 * Utility class for proximity calculation 
 * Captures a single artifact that is overlapping in a pair of working sets
 * and accounts for the number of interactions recorded in those two workin sets
 * 
 */

class OverlapItem {
	
/**
 * an identifier for the artifact
 */
private String itemName;

/**
 * number of selection interactions recorded in the first working set
 */
private int selection1;

/**
 * number of editing interactions recorded in the first working set
 */
private int edit1;

/**
 * number of selection interactions recorded in the second working set
 */
private int selection2;

/**
 * number of edtiing interactions recorded in the first working set
 */
private int edit2;


	OverlapItem (String name) {
		setItemName(name);
		setSelection1(0);
		setEdit1(0);
		setSelection2(0);
		setEdit2(0);
	}

	OverlapItem(String name, int s1, int e1, int s2, int e2) {
		setItemName(name);
		setSelection1(s1);
		setEdit1(e1);
		setSelection2(s2);
		setEdit2(e2);
	}

	public boolean isActualOverlap() {
		return ((selection1 > 0 || edit1 > 0) && (selection2 > 0 || edit2 > 0));
	}
	
	/**
	 * @return the itemName
	 */
	public String getItemName() {
		return itemName;
	}


	/**
	 * @param itemName the itemName to set
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}


	/**
	 * @return the selection1
	 */
	public int getSelection1() {
		return selection1;
	}


	/**
	 * @param selection1 the selection1 to set
	 */
	public void setSelection1(int selection1) {
		this.selection1 = selection1;
	}


	/**
	 * @return the edit1
	 */
	public int getEdit1() {
		return edit1;
	}


	/**
	 * @param edit1 the edit1 to set
	 */
	public void setEdit1(int edit1) {
		this.edit1 = edit1;
	}


	/**
	 * @return the selection2
	 */
	public int getSelection2() {
		return selection2;
	}


	/**
	 * @param selection2 the selection2 to set
	 */
	public void setSelection2(int selection2) {
		this.selection2 = selection2;
	}


	/**
	 * @return the edit2
	 */
	public int getEdit2() {
		return edit2;
	}


	/**
	 * @param edit2 the edit2 to set
	 */
	public void setEdit2(int edit2) {
		this.edit2 = edit2;
	}
}
