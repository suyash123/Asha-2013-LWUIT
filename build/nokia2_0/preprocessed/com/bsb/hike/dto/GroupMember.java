/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.dto;

import com.bsb.hike.util.AppConstants;
import com.bsb.hike.util.TextUtils;

/**
 *dto for single group member
 * @author Ankit Yadav
 */
public class GroupMember implements AppConstants{

    public interface MemberSource {
        byte RMS = 0;
        byte SELF = 1;
        byte SENDER = 2;
    }
    
    private String msisdn;
    private String name;
    private boolean onHike; //return only when user is not in addressbook db
    private String DnD;
    private boolean active;
    private byte source;
    private AddressBookEntry entry;

    public GroupMember(String msisdn, String name, boolean active, boolean onHike, String dnd, byte source) {
        this.msisdn = msisdn;
        this.name = name;
        this.onHike = onHike;
        this.DnD = dnd;
        this.active = active;
        this.source = source;
    }

    /**
     * @return the msisdn
     */
    public String getMsisdn() {
        if(msisdn !=null && msisdn.equals(NULL_STRING)){
            msisdn = null;
        }
        return msisdn;
    }

    /**
     * 
     * @return member source as RMS, sender or Self
     */
    public byte getMemberSource() {
        return source;
    }

    /**
     * @return the name
     */
    public String getName() {
        if (getEntry() != null) {
            return entry.getName();
        } else {
            return msisdn;
        }
    }

    /**
     * 
     * @return dnd status of member
     */
    public String isDnD() {
        return DnD == null ? EMPTY_STRING : DnD;
    }

    /**
     * setter for DnD status
     * @param DnD 
     */
    public void setDnD(String DnD) {
        this.DnD = DnD;
    }

    /**
     * 
     * @return first name of member
     */
    public String getFirstName() {
        return TextUtils.getFirstWord(getName());
    }

    /**
     * @return whether member is active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * sets the member as active or inactive
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * 
     * @return member's hike status
     */
    public boolean isOnHike() {
        return getEntry() == null ? onHike : entry.isOnHike();
    }

    /**
     * setter for member's hike status
     * @param onHike 
     */
    public void setOnHike(boolean onHike) {
        this.onHike = onHike;
    }

    /**
     * 
     * @return addressbookentry corresponding to member msisdn
     */
    private AddressBookEntry getEntry() {
        if (entry == null) {
            entry = AddressBookList.getInstance().getEntryByMsisdn(msisdn);
        }
        return entry;
    }

    public String toString() {
        return "GroupMember{msisdn:" + msisdn + ", name:" + name + ", active:" + active + ", onHike:" + onHike + "}";
    }
}
