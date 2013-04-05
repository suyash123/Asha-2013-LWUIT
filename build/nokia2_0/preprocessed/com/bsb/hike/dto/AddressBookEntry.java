package com.bsb.hike.dto;

import com.bsb.hike.util.TextUtils;
import java.util.Vector;

/**
 * dto for addressbook entry
 * @author Ankit Yadav
 */
public class AddressBookEntry implements DataModel {

    private String id;
    private String phoneNumber;
    private String name;
    private String msisdn;
    private boolean invited;
    private boolean onHike;
    //lazy reference
    private Vector blocklist;
    private boolean dirty;

    /**
     * set the dirty or modified state of entry
     * @param dirty 
     */
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    /**
     * getter for dirty or modified state of entry
     * @return 
     */
    public boolean isDirty() {
        return dirty;
    }

    public AddressBookEntry(String id, String phoneNumber, String name, String msisdn, boolean onHike) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.msisdn = msisdn;
        this.onHike = onHike;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @return the name
     */
    public String getName() {
        return !TextUtils.isEmpty(name) ? name:getMsisdn();
    }

    /**
     * @return the msisdn
     */
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * @return the statusHike
     */
    public boolean isOnHike() {
        return onHike;
    }

    /**
     * setter for onhike status
     * @param onHike 
     */
    public void setOnHike(boolean onHike) {
        this.onHike = onHike;
        dirty = true;
    }

    /**
     * @return the blocked
     */
    public boolean isBlocked() {
        if (blocklist == null) {
            blocklist = AppState.getUserDetails().getBlocklist();
        }
        return blocklist.contains(getMsisdn());
    }

    /**
     * setter for name
     * @param inName 
     */
    public void setName(String inName) {
        this.name = inName;
    }

    public String toString() {
        return "AddressBookEntry{id:" + id  + ", name:" + name + ", phoneNumber:" + phoneNumber + ", msisdn:" + msisdn + ", onHike:" + onHike + "}";
    }

    /**
     * @return the invited
     */
    public boolean isInvited() {
        return invited;
    }

    /**
     * @param invited the invited to set
     */
    public void setInvited(boolean invited) {
        this.invited = invited;
    }
}