package com.bsb.hike.dto;

import com.bsb.hike.util.AppConstants;
import com.bsb.hike.util.CollectionUpdateEvent;
import com.bsb.hike.util.CollectionUpdateListener;
import java.util.Vector;

/**
 * modified vector to listen changes in elements
 * @author Ankit Yadav.
 * @author Sudheer Keshav Bhat
 */
public abstract class DataCollection extends Vector implements AppConstants {
    private static final String TAG = "DataCollection";
    
    protected CollectionUpdateListener listener = null;

    public synchronized void setElementAt(DataModel obj, int index) {
        super.setElementAt(obj, index);
        if (listener != null) {
            listener.modelUpdated((new CollectionUpdateEvent(index, obj)));
        }
    }

    public synchronized void removeElementAt(int index) {
        super.removeElementAt(index);
        if (listener != null) {
            listener.modelRemoved(new CollectionUpdateEvent(index, null));
        }
    }

    public synchronized void insertElementAt(DataModel obj, int index) {        
        super.insertElementAt(obj, index);
        if (listener != null) {
            listener.modelAdded(new CollectionUpdateEvent(index, null));
        }
    }

    public synchronized void addElement(DataModel obj) {
        super.addElement(obj);
        if (listener != null) {
            listener.modelAdded(new CollectionUpdateEvent(-1, obj));
        }
    }

    public synchronized boolean removeElement(DataModel obj) {
        boolean removed = super.removeElement(obj);
        if (listener != null) {
            listener.modelRemoved(new CollectionUpdateEvent(-1, obj));
        }
        return removed;
    }

    public synchronized void removeAllElements() {
        super.removeAllElements();
        if (listener != null) {
            listener.modelRemoved((new CollectionUpdateEvent(-1, null)));
        }
    }
    
    // additional methods
    public void addCollectionUpdateListener(CollectionUpdateListener listener) {
        this.listener = listener;
    }

    public void removeCollectionUpdateListener() {
        listener = null;
    }
    
    public void refreshModel(DataModel model){
        if (listener != null) {
            listener.modelContentsUpdated(model);
        }
    }
}