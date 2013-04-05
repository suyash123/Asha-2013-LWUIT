package com.bsb.hike.util;

import com.bsb.hike.dto.DataModel;

public interface CollectionUpdateListener {

	/**
	 * 
	 * @param event
	 *            if event index is -1 and affectedModel is null model is added
	 *            at last, else index in set model is inserted to particular
	 *            index
	 * 
	 */
	void modelAdded(CollectionUpdateEvent event);

	/**
	 * 
	 * @param event
	 *            if event index is -1 and affectedModel is null all models are
	 *            removed, else if index in set model is removed from particular
	 *            index or if affectedModel is set, that model is removed
	 * 
	 */
	void modelRemoved(CollectionUpdateEvent event);

	/**
	 * 
	 * @param event
	 *            model is updated at particular index
	 * 
	 */
	void modelUpdated(CollectionUpdateEvent event);

	void modelContentsUpdated(DataModel affectedModel);
}
