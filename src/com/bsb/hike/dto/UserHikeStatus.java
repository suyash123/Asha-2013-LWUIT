/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.dto;

/**
 *Class to represent user's hike status, when user opted-in/out, joined/left the hike
 * @author Ankit Yadav
 */
public class UserHikeStatus {

    private String msisdn;
    private int sms = 0;
    private boolean onHike;

    public UserHikeStatus(String msisdn, int smsCredit, boolean onHike) {
        this.msisdn = msisdn;
        this.sms = smsCredit;
        this.onHike = onHike;
    }

    /**
     * 
     * @return msisdn of contact
     */
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * 
     * @return sms credited to account
     */
    public int getSms() {
        return sms;
    }

    /**
     * 
     * @return user's new hike status
     */
    public boolean isOnHike() {
        return onHike;
    }
}
