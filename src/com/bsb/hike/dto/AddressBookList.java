package com.bsb.hike.dto;

import com.bsb.hike.util.CollectionUpdateEvent;
import com.bsb.hike.util.Collections;
import com.bsb.hike.util.Collections.Comparator;
import com.bsb.hike.util.Log;
import com.bsb.hike.util.TextUtils;
import java.util.Enumeration;
import java.util.Vector;

/**
 * @author Ankit Yadav
 * @author Sudheer Keshav Bhat - Added sortability.
 */
public class AddressBookList extends DataCollection {

    private final static Vector inviteList = new Vector();
    private final static AddressBookList list = new AddressBookList();
    private static final String TAG = "AddressBookList";

    private AddressBookList() {
    }
    private boolean mSortOnChange = true;

    /**
     * setter to enable/disable sorting on change
     * @param sortOnChange 
     */
    public void setSortOnChange(boolean sortOnChange) {
        mSortOnChange = sortOnChange;
    }
    
    private Comparator mAlphabeticalComparator = new Comparator() {
        public int compare(Object o1, Object o2) {
            AddressBookEntry address1 = (AddressBookEntry) o1;
            AddressBookEntry address2 = (AddressBookEntry) o2;
            if(address1 == null || address2==null){
                return 0;
            }
            return address1.getName().toLowerCase().compareTo(address2.getName().toLowerCase());
        }
    };

    /**
     * 
     * @return singleton instance
     */
    public static AddressBookList getInstance() {
        return list;
    }

    /**
     * iterate over whole list to find a match by msisdn
     * @param msisdn
     * @return 
     */
    public AddressBookEntry getEntryByMsisdn(String msisdn) {
        if (!TextUtils.isEmpty(msisdn)) {
            Enumeration enums = list.elements();
            while (enums.hasMoreElements()) {
                AddressBookEntry entry = (AddressBookEntry) enums.nextElement();
                if (msisdn.equalsIgnoreCase(entry.getMsisdn())) {
                    return entry;
                }
            }
        }
        return null;
    }

    public void addElement(DataModel model) {
        Log.v(TAG, "Addressbook add element called");
        if (model instanceof AddressBookEntry && !((AddressBookEntry) model).isOnHike()) {
            Log.v(TAG, "element added in invite list");
            inviteList.addElement(model);
        }
        if (mSortOnChange) {
            Log.v(TAG, "Addressbook add element called sorted");
            super.addElement(model);
            if (model instanceof AddressBookEntry && !((AddressBookEntry) model).isOnHike()) {
                Log.v(TAG, "element added in invite list during sort");
                inviteList.addElement(model);
            }
            sort();
            if (listener != null) {
                listener.modelAdded(new CollectionUpdateEvent(-1, model));
            }
        } else {
            Log.v(TAG, "Addressbook add element called not sorted");
            super.addElement(model);
        }
    }

    public boolean removeElement(DataModel obj) {
        if (obj instanceof AddressBookEntry && !((AddressBookEntry) obj).isOnHike()) {
            inviteList.removeElement(obj);
        }
        return super.removeElement(obj);
    }

    public synchronized void removeAllElements() {
        inviteList.removeAllElements();
        super.removeAllElements();
    }

    public void refreshModel(DataModel model) {
        if (mSortOnChange) {
            sort();
            if (listener != null) {
                listener.modelContentsUpdated(model);
            }
        } else {
            super.refreshModel(model);
        }
    }

    /**
     * sorts this vector
     */
    public void sort() {
        Collections.sort(this, mAlphabeticalComparator);
    }

    /**
     * 
     * @return list of all non hike users
     */
    public Vector getInviteList() {
        return inviteList;
    }

    /**
     * updates hike status of given msisdn
     * @param msisdn
     * @param onHike 
     */
    public synchronized void updateHikeStatus(String msisdn, boolean onHike) {
        Enumeration enums = getInstance().elements();
        while (enums.hasMoreElements()) {
            AddressBookEntry entry = (AddressBookEntry) enums.nextElement();
            if (entry != null && entry.getMsisdn().equals(msisdn)) {
                entry.setOnHike(onHike);
                if (onHike) {
                    inviteList.removeElement(entry);
                } else {
                    inviteList.addElement(entry);
                }
                refreshModel(entry);
                break;
            }
        }
    }
}