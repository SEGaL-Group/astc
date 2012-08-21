package com.act.on.stc.common.utilities;

public class Pair<T1, T2> {
	private T1 firstElem;
	private T2 secondElem;
	
	public Pair(T1 element1, T2 element2) {
		this.setFirstElem(element1);
		this.setSecondElem(element2);
	}

	/**
	 * @return the firstElem
	 */
	public T1 getFirstElem() {
		return firstElem;
	}

	/**
	 * @param firstElem the firstElem to set
	 */
	public void setFirstElem(T1 firstElem) {
		this.firstElem = firstElem;
	}

	/**
	 * @return the secondElem
	 */
	public T2 getSecondElem() {
		return secondElem;
	}

	/**
	 * @param secondElem the secondElem to set
	 */
	public void setSecondElem(T2 secondElem) {
		this.secondElem = secondElem;
	}

}
