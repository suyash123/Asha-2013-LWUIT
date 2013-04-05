/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.dto;

import com.bsb.hike.Hike;
import com.bsb.hike.io.ClientConnectionHandler;
import com.bsb.hike.io.ConnectionFailedException;
import com.bsb.hike.io.ContactHandler;
import com.bsb.hike.io.FileHandler;
import com.bsb.hike.notification.SessionHandler;
import com.bsb.hike.ui.DisplayStackManager;
import com.bsb.hike.util.AppConstants;
import com.bsb.hike.util.Log;
import com.bsb.hike.util.TextUtils;
import java.io.IOException;
import java.util.Date;
import java.util.Vector;
import javax.microedition.rms.RecordStore;
import org.json.me.JSONArray;
import org.json.me.JSONObject;

/**
 *dto for app state
 * @author Ankit Yadav
 */
public class AppState implements AppConstants {

    private static boolean onHike = false;
    private static int form;
    private static String number = null;
    private static boolean vibrationOn = true;
    private static boolean volumeOn = true;
    private static UserDetails user = null;
    private static long nextConvID = 1;
    private static long nextMessageID = 1;
    private final static String TAG = "AppState";
    public static transient int fromScreen;
    public static transient int workFlow;
    public static SessionHandler sessionHandler;
    private static int count = 1;
    private static boolean isPushReceived = false;
    //update variables
    public static JADInfoAttribute jad = new JADInfoAttribute(EMPTY_STRING, EMPTY_STRING, EMPTY_STRING);    
    private static byte updateCounter = 1;    
    public static long lastUpdated = 0;
    
    /**
     * initializes app state from rms, initializes session handler & session data if user is registered
     */
    public static void initializeState() {
        RecordStore recordStore = null;
        try {
            recordStore = RecordStore.openRecordStore(HIKE_STATE, true, RecordStore.AUTHMODE_PRIVATE, true);
            if (recordStore.getNumRecords() != 1) {
                Log.v(TAG, "setting user details for first launch");
                JSONObject data = new JSONObject();
                data.put(JsonKeyOnHike, false);
                data.put(JsonKeyForm, DisplayStackManager.FORM_GET_STARTED);
                byte[] bytes = data.toString().getBytes(TEXT_ENCODING);
                recordStore.addRecord(bytes, 0, bytes.length);
            } else {                         
                Log.v(TAG, "getting existing user details");
                byte[] rmsBytes = recordStore.getRecord(RMS_JsonID);
                         
                Log.v(TAG, "RMS details retrieved");
                String appData = new String(rmsBytes, TEXT_ENCODING);
                Log.v(TAG, "RMS USer details: " + appData);
                JSONObject appJson = new JSONObject(appData);
                form = appJson.optInt(JsonKeyForm, DisplayStackManager.FORM_GET_STARTED);
                         
                Log.v(TAG, "form: " + form);
                if (!DisplayStackManager.isSignUpForm(form)) {
                    form = DisplayStackManager.FORM_CONVERSATION;
                } else if (!(form == DisplayStackManager.FORM_ENTER_PIN || form == DisplayStackManager.FORM_SET_NAME)) {
                    form = DisplayStackManager.FORM_GET_STARTED;
                }
                        
                Log.v(TAG, "form: " + form);
                onHike = appJson.optBoolean(JsonKeyOnHike, false);
                number = appJson.optString(JsonKeyPhone, null);
                vibrationOn = appJson.optBoolean(JsonKeyVibrate, true);
                volumeOn = appJson.optBoolean(JsonKeyVolume, true);
                nextConvID = appJson.optLong(JsonKeyConvID, 1);
                nextMessageID = appJson.optLong(JsonKeyMessageID, 1);
                
                if (appJson.has(JsonKeyUser)) {
                             
                    Log.v(TAG, "RMS has user details");
                    JSONObject userJson = appJson.getJSONObject(JsonKeyUser);
                    boolean forceAddressBook = userJson.getBoolean(JsonKeyPostab);
                    String cookie = userJson.getString(JsonKeyCookie);
                    String msisdn = userJson.getString(JsonKeyMsisdn);
                    String userToken = userJson.getString(JsonKeyToken);
                    String userID = userJson.getString(JsonKeyUID);
                    String countryCode = userJson.getString(JsonKeyCountryCode);
                             
                    Log.v(TAG, "User Country code is " + countryCode);
                    String email = userJson.optString(JsonKeyEmail, "");
                    String name = userJson.optString(JsonKeyName, "");
                    int sms = userJson.optInt(JsonKeySmsCredit);

                    String inviteToken = userJson.optString(JsonKeyInviteToken);
                    String totalCredit = userJson.optString(JsonKeyTotalCredit);
                    String rewardToken = userJson.optString(JsonKeyRewardToken);
                    boolean showReward = userJson.optBoolean(JsonKeyShowRewards);

                    String selected = userJson.optString(JsonKeySelectedChatMsisdn);
                    String gender = userJson.optString(JsonKeyGender, GENDER_NOT_AVAILABLE);
                    JSONArray blocklist = userJson.getJSONArray(JsonKeyBlocklist);

                    byte[] avatar = null;
                    String avatarString = userJson.optString(JsonKeyAvatar, null);
                    if (avatarString != null) {
                        avatar = avatarString.getBytes();
                    }
                    if (selected == null) {
                        form = DisplayStackManager.FORM_CONVERSATION;
                    }
                    if (!(cookie != null && msisdn != null && userToken != null && userID != null)) {
                        throw new Exception() {
                            public String toString() {
                                return "user data could not retrieved.";
                            }
                        };
                    }
                    user = new UserDetails(cookie, msisdn, userToken, userID);
                    user.setForcePostAddressBook(forceAddressBook);
                    user.setEmail(email);
                    user.setName(name);
                    user.setCountryCode(countryCode);
                    user.setSelectedMsisdn(selected);
                    user.setGender(gender);
                    user.setAvatar(avatar);
                    user.setBlocklist(blocklist);
                    
                    Log.v(TAG, "user retrieved");
                    //This call should be made before any other method call. User has to be set before any flow starts.
                    AppState.setUserDetails(user);
                    
                    Log.v(TAG, "Invite token: " + inviteToken + ", Total Credit: " + totalCredit + ", Reward token: " + rewardToken + ", Show Reward: " + showReward);
                    AccountInfo acInfo = new AccountInfo(inviteToken, totalCredit);
                    user.setAccountInfo(acInfo);
                    acInfo.setRewardDetails(rewardToken, showReward);
                    
                    user.setSmsCredit(sms);
                             
                    //This code is only for app launch flow (Tested and working fine)*****************************************//
                    if (sessionHandler != null) {
                        try {
                            isPushReceived = sessionHandler.isPushReceived();
                            if(!isPushReceived && (new Date().getTime() - Hike.launchTime)  < PUSH_LAUNCH_WAIT) {
                                Thread.sleep(PUSH_LAUNCH_WAIT);
                            }
                            isPushReceived = sessionHandler.isPushReceived();
                            if (isPushReceived) {
                                String payload = sessionHandler.processMessage();
                                if (!TextUtils.isEmpty(payload)) {
                                    if (count == 1) {
                                        AppState.getUserDetails().setSelectedMsisdn(payload);
                                        form = DisplayStackManager.FORM_CHAT;
                                        count = count + 1;

                                        Log.v(TAG, "Received push message, opening chat thread");
                                    }
                                } else {
                                    form = DisplayStackManager.FORM_CONVERSATION;

                                    Log.v(TAG, "Received push message, opening conversation form if chat thread doesn't open");
                                }
                            } else {
                                if (!DisplayStackManager.isSignUpForm(form)) {
                                    form = DisplayStackManager.FORM_CONVERSATION;

                                    Log.v(TAG, "Not signup form, not received any push message, opening conversation form");
                                } else if (!(form == DisplayStackManager.FORM_ENTER_PIN || form == DisplayStackManager.FORM_SET_NAME)) {
                                    form = DisplayStackManager.FORM_GET_STARTED;

                                    Log.v(TAG, "Not received any push message, opening one of the signup form");
                                }
                            }
                        }catch (Exception ex){
                            Log.v(TAG, "exception while launching from push" + ex.getClass().getName());
                        }
                    }
                    //*******************************************************************************************************//    
                    Log.v(TAG, "set user details");
                    
                    //order of reading is important, chaging it might cause crash
                    FileHandler.readAddressBookFromRMS();
                    FileHandler.mergeAddressBookDiffFromRMS();                 
                    Log.v(TAG, "read from add");

                    FileHandler.readRecordsFromRMS(GroupFile);                 
                    Log.v(TAG, "read from groups");

                    FileHandler.readRecordsFromRMS(ConversationFile);                 
                    Log.v(TAG, "read from conv");

                    FileHandler.readRecordsFromRMS(ChatFile);                 
                    Log.v(TAG, "read from chat");

                    FileHandler.readRecordsFromRMS(PendingMessageFile);                 
                    Log.v(TAG, "read from pending messages");

                    FileHandler.readRecordsFromRMS(ThumbsFile);                 
                    Log.v(TAG, "read from thmbs");

                    Runtime.getRuntime().gc();

                    if (AppState.getUserDetails() != null) {
                        new Thread(){
                            public void run() {
                                try {   
                                    if (user.getForcePostAddressBook()) {
                                         Log.v(TAG, "force address book request");
                                         ClientConnectionHandler.PostAddressbook(ContactHandler.getAddressbookRequestArray(false), false);
                                         user.setForcePostAddressBook(false);
                                    } else {
                                         Log.v(TAG, "Details of Deleted contacts posting update request");
                                         ClientConnectionHandler.PostAddressbook(ContactHandler.getAddressbookRequestArray(true), true);
                                        FileHandler.writeAddressBookToRMS(AddressBookList.getInstance());
                                        Log.v(TAG, "Details of Deleted contacts posted update successfully");
                                    }
                                } catch (ConnectionFailedException ex) {
                                } catch (IOException ex) {
                                }
                            }                
                        }.start();                    
                    }
                } else {
                             
                    Log.v(TAG, "user details not in RMS");
                    nextConvID = 1;
                    nextMessageID = 1;
                }
            }

        } catch (Exception e) {                     
            Log.v(TAG, "user could not be retrieved: " + e.getClass().getName());
        } finally {
            if (recordStore != null) {
                try {
                    recordStore.closeRecordStore();
                } catch (Exception e) {
                    //ignore
                }
            }
        }
    }
    
    /**
     * opens record store and initialize update state
     */
    public static void initUpdateState(){
        RecordStore recordStore = null;
        try {
            recordStore = RecordStore.openRecordStore(UpdateStatus, true, RecordStore.AUTHMODE_PRIVATE, true);
            
            if (recordStore.getNumRecords() != 1) {
                JSONObject updateJson = new JSONObject();
                updateJson.put(JsonKeyLastUpdateDate, new Date().getTime());
                updateJson.put(JsonKeyUpdateCounter, 1);
                updateJson.put(JSON_APP_UPDATE_URL, jad.getJadURL());
                updateJson.put(JSON_APP_UPDATE_LATEST, jad.getJadLatestVersion());
                updateJson.put(JSON_APP_UPDATE_CRITICAL, jad.getJadCriticalVersion());                
                byte[] rmsBytes = updateJson.toString().getBytes(TEXT_ENCODING);
                recordStore.addRecord(rmsBytes, 0, rmsBytes.length);
            } else {
                byte[] bytes = recordStore.getRecord(RMS_JsonID);                         
                Log.v(TAG, "RMS details of Update retrieved");
                String appData = new String(bytes, TEXT_ENCODING);
                Log.v(TAG, "RMS details of Update: " +  appData);
                JSONObject appJson = new JSONObject(appData);                
                lastUpdated = Long.parseLong(appJson.optString(JsonKeyLastUpdateDate, EMPTY_STRING + 0));
                updateCounter = Byte.parseByte(appJson.optString(JsonKeyUpdateCounter, EMPTY_STRING + 1));
                String url = appJson.optString(JSON_APP_UPDATE_URL, EMPTY_STRING);
                String critical = appJson.optString(JSON_APP_UPDATE_CRITICAL, EMPTY_STRING);
                String latest = appJson.optString(JSON_APP_UPDATE_LATEST, EMPTY_STRING);
                jad = new JADInfoAttribute(url, critical, latest);
            }
        } catch (Exception ex) {                    
            Log.v(TAG, "exception while persisting state: " + ex.getClass().getName());
        } finally {
            if (recordStore != null) {
                try {
                    recordStore.closeRecordStore();
                } catch (Exception e) {
                    //ignore
                }
            }
        }
    }
    
    /**
     * store app state in rms
     */
    public static void persisteUpdateState(){             
        // Storing Update details in RMS
        RecordStore store = null;        
            try {                         
                Log.v(TAG, "Storing update details in RMS");
                store = RecordStore.openRecordStore(UpdateStatus, false, RecordStore.AUTHMODE_PRIVATE, true);
                JSONObject updateJson = new JSONObject();
                updateJson.put(JsonKeyUpdateCounter, ++updateCounter);
                updateJson.put(JsonKeyLastUpdateDate, new Date().getTime());
                updateJson.put(JSON_APP_UPDATE_URL, jad.getJadURL());
                updateJson.put(JSON_APP_UPDATE_LATEST, jad.getJadLatestVersion());
                updateJson.put(JSON_APP_UPDATE_CRITICAL, jad.getJadCriticalVersion());
                Log.v(TAG, "Stored app update values in JSON" + updateJson);
                byte[] rmsBytes = updateJson.toString().getBytes(TEXT_ENCODING);
                store.setRecord(RMS_JsonID, rmsBytes, 0, rmsBytes.length);
            } catch (Throwable ex) {
                         
                Log.v(TAG, "exception while persisting state: " + ex.getClass().getName());
            } finally {
                if (store != null) {
                    try {
                        store.closeRecordStore();
                    } catch (Exception e) {
                        //ignore
                    }
                }
            }
    }

    /**
     * compare versions and returns whther update is applicable
     * @param current
     * @param update
     * @return 
     */
    public static boolean compareVersions(String current, String update){
        Vector vecA = tokenize(current, '.');
        Vector vecB = tokenize(update, '.');
        int index = vecA.size() > vecB.size() ? vecA.size() : vecB.size();
        for (int i=0;i<index;i++){
            int curr = i < vecA.size() ?  Integer.parseInt(vecA.elementAt(i).toString()) : 0;
            int updt = i < vecB.size() ?  Integer.parseInt(vecB.elementAt(i).toString()) : 0;
            if (updt == curr){
                continue;
            } else {
                return updt > curr;
            }
        }
        return false;
    }
   
    /**
     * splits string from separators
     * @param version
     * @param seperator
     * @return 
     */
    public static Vector tokenize(String version, char seperator){
        Vector vector = new Vector();
        String sub = version;
        while (sub.indexOf(seperator) >=0){
            int index = sub.indexOf(seperator);
            String pre = sub.substring(0, index);
            String post = sub.substring(index+1);
            sub = post;
            if(pre.length() >0){
                vector.addElement(pre);
            }
        }
        if(sub.length() >0){
            vector.addElement(sub);
        }
        System.out.println(vector);
        return vector;       
    }
    
    /**
     * 
     * @return whether should check for update considering 3 factors, days, update counter's value and critical update
     */
    public static boolean shouldCheckForUpdate(){
        //check for time difference, launch count, critical update        
        long now = new Date().getTime();
        int days = (int) ((now - lastUpdated)) / (1000 * 60 * 60 * 24);
        Log.v(TAG, "Critical update " + jad.isCritical());
        Log.v(TAG, "Update counter " + updateCounter);
        Log.v(TAG, "Time Elapsed " + days);
        return (jad != null && jad.isCritical()) || ( updateCounter % MIN_UPDATE_COUNT == 0) || ( days > MIN_UPDATE_DATE_INTERVAL);
    }

    /**
     * persiste app state into rms
     */
    public static void persistState() {
        RecordStore store = null;
        try {
            store = RecordStore.openRecordStore(HIKE_STATE, false, RecordStore.AUTHMODE_PRIVATE, true);
            JSONObject appJson = new JSONObject();
            appJson.put(JsonKeyForm, form);
            appJson.put(JsonKeyOnHike, onHike);
            appJson.put(JsonKeyPhone, number);
            appJson.put(JsonKeyVibrate, vibrationOn);
            appJson.put(JsonKeyVolume, volumeOn);
            appJson.put(JsonKeyConvID, getNextConvID());
            appJson.put(JsonKeyMessageID, getNextMessageID());
            
            Log.v(TAG, "Saving app details");
            if (onHike) {
                         
                Log.v(TAG, "Saving user to json");
                JSONObject userJson = new JSONObject();
                userJson.put(JsonKeyPostab, user.getForcePostAddressBook());
                userJson.put(JsonKeyCookie, user.getCookie());
                userJson.put(JsonKeyMsisdn, user.getMsisdn());
                userJson.put(JsonKeyToken, user.getToken());
                userJson.put(JsonKeyUID, user.getUid());
                userJson.put(JsonKeyCountryCode, user.getCountryCode());
                
                         
                Log.v(TAG, "Saved basic user details");
                userJson.putOpt(JsonKeyEmail, user.getEmail());
                userJson.putOpt(JsonKeyName, user.getName());
                userJson.put(JsonKeyGender, user.getGender());
                         
                Log.v(TAG, "Saving user state");
                userJson.put(JsonKeySmsCredit, user.getSmsCredit());
                         
                Log.v(TAG, "Saving account info");
                if (user.getAccountInfo() != null) {
                    userJson.put(JsonKeyInviteToken, user.getAccountInfo().getInviteToken());
                    userJson.put(JsonKeyTotalCredit, user.getAccountInfo().getTotalCredit());
                    userJson.put(JsonKeyRewardToken, user.getAccountInfo().getRewardToken());
                    userJson.put(JsonKeyShowRewards, user.getAccountInfo().isShowReward(user.getCountryCode()));
                }
                userJson.putOpt(JsonKeySelectedChatMsisdn, null);
                         
                Log.v(TAG, "Saved user state");
                if (user.getAvatar() != null) {
                    userJson.putOpt(JsonKeyAvatar, new String(user.getAvatar()));
                } else {
                    userJson.putOpt(JsonKeyAvatar, null);
                }
                       
                Log.v(TAG, "Saved user name email avtar");
                userJson.put(JsonKeyBlocklist, user.getBlocklistAsJson());
                appJson.put(JsonKeyUser, userJson);
                
                try {
                    FileHandler.writeAddressBookDiffToRMS(AddressBookList.getInstance());

                    Log.v(TAG, "written in add");
                    FileHandler.writeRecordsToRMS(ConversationList.getInstance());

                    Log.v(TAG, "written in conv");
                    FileHandler.writeRecordsToRMS(ChatList.getInstance());


                    Log.v(TAG, "written in chat");
                    FileHandler.writeRecordsToRMS(ContactAvatarList.getInstance());

                    Log.v(TAG, "written in avatar");
                    FileHandler.writeRecordsToRMS(GroupList.getInstance());

                    Log.v(TAG, "written in groups");
                    FileHandler.writeRecordsToRMS(MqttPendingMessageList.getInstance());

                    Log.v(TAG, "written in message");
                }catch(Throwable e){
                }
            }
            byte[] rmsBytes = appJson.toString().getBytes(TEXT_ENCODING);
            store.setRecord(RMS_JsonID, rmsBytes, 0, rmsBytes.length);

        } catch (Throwable e) {
                     
            Log.v(TAG, "exception while persisting state: " + e.getClass().getName());
        } finally {
            if (store != null) {
                try {
                    store.closeRecordStore();
                } catch (Exception e) {
                    //ignore
                }
            }
        }
    }

    /**
     * @return current form
     */
    public static int getForm() {
        return form;
    }

    /**
     * @param form the form to set
     */
    public static void setForm(int form) {
        AppState.form = form;
    }

    /**
     * @return the phone entered for registration number
     */
    public static String getNumber() {
        return number;
    }

    /**
     * @param number phone number entered for registration
     */
    public static void setNumber(String number) {
        AppState.number = number;
    }

    /**
     * @return whether vibration is enabled
     */
    public static boolean isVibrationOn() {
        return vibrationOn;
    }

    /**
     * enables or disables vibration status
     * @param vibrationStatus the vibrationStatus to set
     */
    public static void setVibrationOn(boolean vibrationOn) {
        AppState.vibrationOn = vibrationOn;
    }

    /**
     * @return whether volume is enabled
     */
    public static boolean isVolumeOn() {
        return volumeOn;
    }

    /**
     * enables or disables volume status
     * @param volumeStatus the volumeStatus to set
     */
    public static void setVolumeOn(boolean volumeOn) {
        AppState.volumeOn = volumeOn;
    }

    /**
     * @return user details
     */
    public static UserDetails getUserDetails() {
        return user;
    }

    /**
     * setter for userdetails
     * @param user the user to set
     */
    public static void setUserDetails(UserDetails user) {
        AppState.user = user;
        if (user == null) {
            AppState.onHike = false;
        } else {
            AppState.onHike = true;
            if (!LOCAL) {
                AppState.initPushNotif();
            }
            //MqttConnectionHandler.getMqttConnectionHandler().open();
        }
    }

    /**
     * @return the nextConvID
     */
    public static long getNextConvID() {
        return nextConvID++;
    }

    /**
     * @return the nextMessageID
     */
    public static long getNextMessageID() {
        return nextMessageID++;
    }

    /**
     * initialize push notification session
     */
    public static void initPushNotif() {
                 
        Log.v(TAG, "initializing push notification..");

        if (sessionHandler == null) {
            sessionHandler = new SessionHandler(Hike.sMidlet);
        }
        sessionHandler.initialize();
    }

    
    /**
     * deletes the user details and data locally and unregisters the push service
     */
    public static void deleteUser() {
        if (!LOCAL) {
            if (sessionHandler != null) {
                sessionHandler.unregister();
            }
        }
        AppState.setUserDetails(null);
        AppState.setForm(DisplayStackManager.FORM_GET_STARTED);
        AppState.setNumber(null);

        nextConvID = 0;
        nextMessageID = 0;

        AddressBookList.getInstance().removeAllElements();
        ConversationList.getInstance().removeAllElements();
        ChatList.getInstance().removeAllElements();
        GroupList.getInstance().removeAllElements();
        ContactAvatarList.getInstance().removeAllElements();
        MqttPendingMessageList.getInstance().removeAllElements();
                 
        Log.v(TAG, "Signout User ------> Completed");
    }
}
