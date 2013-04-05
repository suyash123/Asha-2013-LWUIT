package com.bsb.hike.dto;

import com.bsb.hike.util.Log;
import com.bsb.hike.util.ModelUtils;
import java.util.Vector;
import org.json.me.JSONArray;

/**
 * dto for details of registered user
 * @author Ankit Yadav
 */
public class UserDetails implements DataModel {

    private String msisdn;
    private String token;
    private String uid;
    private String cookie;
    private String email;
    private String name;
    private int localSMSCredits;
    private int smsCredit;
    private AccountInfo accountInfo;
    private String selectedMsisdn;
    private String countryCode;
    private String gender = GENDER_NOT_AVAILABLE;
    private byte[] avatar;
    private final Vector blocklist;
    private transient GroupMembers currentGrp;
    private boolean forcePostAddressBook;

    private final static String TAG = "UserDetails";

    public UserDetails(String cookie, String msisdn, String token, String uid) {
        this.cookie = cookie;
        this.msisdn = msisdn;
        this.token = token;
        this.uid = uid;
        blocklist = new Vector() {
            public synchronized void addElement(Object obj) {
                if (!this.contains(obj)) {
                    super.addElement(obj);
                }
            }
        };
    }

    /**
     * 
     * @return country code without +
     */
    public String getCountryCode() {
        String code = countryCode == null ? DEFAULT_COUNTRY_CODE : this.countryCode;
        return code.startsWith("+") ? code.substring(1) : code;
    }

    /**
     * setter for country code
     * @param countryCode 
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * @return the msisdn
     */
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * @return the uid
     */
    public String getUid() {
        return uid;
    }

    /**
     *
     * @return the cookie
     */
    public String getCookie() {
        return cookie;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * 
     * @return byte[] of user avatar
     */
    public byte[] getAvatar() {
        return avatar;
    }

    /**
     * @return the blocklist
     */
    public Vector getBlocklist() {
        return blocklist;
    }

    /**
     * 
     * @return the blocklist in jsonarray format
     */
    public JSONArray getBlocklistAsJson() {
        JSONArray array = new JSONArray();
        for (int i = 0; i < blocklist.size(); i++) {
            array.put(blocklist.elementAt(i));
        }
        return array;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * sets the name
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * sets the gender
     * @param gender the gender to set
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * sets the avatar
     * @param avatar the avatar to set
     */
    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    /**
     * sets blocklist
     * @param blocklist the blocklist to set
     */
    public void setBlocklist(JSONArray blocklist) {
        this.blocklist.removeAllElements();
        for (int i = 0; i < blocklist.length(); i++) {
            String element = blocklist.optString(i);
            if (element != null) {
                this.blocklist.addElement(element);
            }
        }
    }

    /**
     * sets blocklist
     * @param blocklist the blocklist to set
     */
    public void setBlocklist(Vector blocklist) {
        this.blocklist.removeAllElements();
        for (int i = 0; i < blocklist.size(); i++) {
            this.blocklist.addElement(blocklist.elementAt(i));
        }
    }

    /**
     * @return the smsCredit
     */
    public int getSmsCredit() {
        return smsCredit;
    }
    
    /**
     * 
     * @return local sms credit
     */
    public int getLocalSMSCredits() {
        if(this.localSMSCredits > 0) {
            return this.localSMSCredits;
        }
        return 0;
    }

    /**
     * sets the local sms credit
     * @param noOfMessagesSent 
     */
    public void setLocalSMSCredits(int noOfMessagesSent) {
        this.localSMSCredits -= noOfMessagesSent;
    }

    
    /**
     * @param smsCredit the smsCredit to set
     */
    public void setSmsCredit(int smsCredit) {
        this.smsCredit = smsCredit;
        this.localSMSCredits = smsCredit;
        ModelUtils.updateSmsCreditInUI(smsCredit);
        //TODO When SMS credits updates from the server and if it is greater than zero then we need to remove the block screen dynamically.
        Log.v(TAG, "SMS Changed in AppState and modelUtils called");        
    }

    /**
     * @return the selectedMsisdn
     */
    public String getSelectedMsisdn() {
        return selectedMsisdn;
    }

    /**
     * setter for selected msisdn in chat list
     * @param aSelectedMsisdn the selectedMsisdn to set
     */
    public void setSelectedMsisdn(String aSelectedMsisdn) {
        selectedMsisdn = aSelectedMsisdn;
    }

    /**
     * 
     * @return current selected group
     */
    public GroupMembers getCurrentGrp() {
        return currentGrp;
    }

    /**
     * setter for current group
     * @param currentGrp 
     */
    public void setCurrentGrp(GroupMembers currentGrp) {
        this.currentGrp = currentGrp;
    }

    /**
     * setter for account info
     * @param accountInfo 
     */
    public void setAccountInfo(AccountInfo accountInfo) {
        this.accountInfo = accountInfo;
    }

    /**
     * 
     * @return account info
     */
    public AccountInfo getAccountInfo() {
        return accountInfo;
    }
    
    public boolean getForcePostAddressBook() {
        return forcePostAddressBook;
    }

    public void setForcePostAddressBook(boolean forcePostAddressBook) {
        this.forcePostAddressBook = forcePostAddressBook;
    }

    public String toString() {
        return "UserDetails{cookie:" + cookie + ", msisdn:" + msisdn + ", token:" + token + ", uid:" + uid + "}";
    }
}