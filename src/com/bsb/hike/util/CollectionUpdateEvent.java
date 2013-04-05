package com.bsb.hike.util;

import com.bsb.hike.dto.DataModel;

public class CollectionUpdateEvent {
	private int affectedIndex = -1;
	private DataModel affectedModel = null;

	public CollectionUpdateEvent(int affectedIndex, DataModel affectedModel) {
		this.affectedIndex = affectedIndex;
		this.affectedModel = affectedModel;
	}

	/**
	 * @return the affectedIndex
	 */
	public int getAffectedIndex() {
		return affectedIndex;
	}

	/**
	 * @return the affectedModel
	 */
	public DataModel getAffectedModel() {
		return affectedModel;
	}
}
