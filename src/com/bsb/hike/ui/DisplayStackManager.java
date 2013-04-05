package com.bsb.hike.ui;

import com.bsb.hike.util.AppConstants;
import com.bsb.hike.util.Log;
import java.lang.ref.WeakReference;
import java.util.Hashtable;

/**
 * @author Puneet Agarwal
 * @author Ankit Yadav
 */
public class DisplayStackManager implements AppConstants {

    // signup form
    public static final byte FORM_ERROR = -1;
    public static final byte FORM_GET_STARTED = 0;
    public static final byte FORM_ENTER_NUMBER = 1;
    public static final byte FORM_ENTER_PIN = 2;
    public static final byte FORM_SET_NAME = 3; // last signup form
    // user form
    public static final byte FORM_CONVERSATION = 5;
    public static final byte FORM_PROFILE = 6;
    public static final byte FORM_NOTIFICATION = 7;
    public static final byte FORM_PRIVACY = 8;
    public static final byte FORM_EDIT_PROFILE = 9;
    public static final byte FORM_CHAT = 10;
    public static final byte FORM_SELECT_CONTACT = 11;
    public static final byte FORM_SELECT_GROUP_CONTACT = 12;
    public static final byte FORM_INVITE = 13;
    public static final byte FORM_GROUP_INFO = 14;
    public static final byte FORM_FREE_SMS = 15;
    public static final byte FORM_USER_BLOCKED = 16;
    public static final byte FORM_ZERO_SMS_LEFT = 17;
    // non state forms
    public static final byte FORM_SMILEY_PANE = 18;
    public static final byte FORM_PROGRESS_INDICATOR = 19;
    public static final byte FORM_HIKE_UPDATE = 20;
    public static final byte FORM_OLD_VERSION = 21;
    public static final byte FORM_REWARDS = 22;
    //
    private static final Hashtable forms = new Hashtable();
    private static String TAG = "DisplayStackManager";

    
    /**
     * This method returns the static instance of the form.
     * @param form The form which is requested.
     * @param forcecreate 
     * @return 
     */
    public static FormHikeBase getForm(int form, boolean forcecreate) {
        FormHikeBase current = null;
        if (forms.containsKey(EMPTY_STRING + form) && ((WeakReference) forms.get(EMPTY_STRING + form)).get() != null) {
            WeakReference ref = (WeakReference) forms.get(EMPTY_STRING + form);
            current = (FormHikeBase) ref.get();
            current.setPageNumber(form);
            return current;
        } else if (!forcecreate) {
            return null;
        } else {
            switch (form) {
                case FORM_ERROR:
                    current = new FormError();
                    break;
                case FORM_GET_STARTED:
                    current = new FormGetStarted();
                    break;
                case FORM_ENTER_NUMBER:
                    current = new FormEnterNumber();
                    break;
                case FORM_CONVERSATION:
                    current = new FormConversation();
                    break;
                case FORM_PROFILE:
                    current = new FormUserProfile();
                    break;
                case FORM_NOTIFICATION:
                    current = new FormNotification();
                    break;
                case FORM_PRIVACY:
                    current = new FormPrivacy();
                    break;
                case FORM_EDIT_PROFILE:
                    current = new FormEditProfile();
                    break;
                case FORM_SET_NAME:
                    current = new FormSetName();
                    break;
                case FORM_ENTER_PIN:
                    current = new FormEnterPin();
                    break;
                case FORM_CHAT:
                    current = new FormChatThread();
                    break;
                case FORM_PROGRESS_INDICATOR:
                    current = new FormProgressindicator();
                    break;
                case FORM_SELECT_CONTACT:
                    current = new FormSelectContact();
                    break;
                case FORM_SELECT_GROUP_CONTACT:
                    current = new FormSelectGroupContact();
                    break;
                case FORM_INVITE:
                    current = new FormInvitation();
                    break;
                case FORM_GROUP_INFO:
                    current = new FormGroupInfo();
                    break;
                case FORM_FREE_SMS:
                    current = new FreeSMSForm();
                    break;
                case FORM_USER_BLOCKED:
                    current = new FormUserBlocked();
                    break;
                case FORM_SMILEY_PANE:
                    current = new FormSmileyPane();
                    break;
                case FORM_ZERO_SMS_LEFT:
                    current = new FormBlockOnZeroSMS();
                    break;
                case FORM_HIKE_UPDATE:
                    current = new FormHikeUpdate();
                    break;
                case FORM_OLD_VERSION:
                    current = new FormOldVersion();
                    break;
                case FORM_REWARDS:
                    current = new FormRewards();
                    break;
            }
            forms.put(EMPTY_STRING + form, new WeakReference(current));
            current.setPageNumber(form);
            return current;
        }
    }
    
    
    /**
     * This method calls getForm() method internally and displays the requested form on the screen
     * @param form 
     */
    public static void showForm(int form){
        Log.v(TAG, "form requested to show " + form);
        getForm(form, true).show();
        Log.v(TAG, "form shown");
    }
    
    
    /**
     * This method is called to show progress screen.
     * @param animate Whether to show progress animation or not. If true, animation starts, else no animation on false.
     * @param message Progress text to be displayed on progress screen.
     */
    public static void showProgressForm(boolean animate, String message){
        Log.v(TAG, "progress form called with animation " + animate);
        if (message == null) {
            ((FormProgressindicator)getForm(FORM_PROGRESS_INDICATOR, true)).show(animate);
        } else {
            ((FormProgressindicator)getForm(FORM_PROGRESS_INDICATOR, true)).show(animate, message);
        }
        Log.v(TAG, "progress form shown" + animate);
    }

    
    /**
     * This method is used to check whether the form is signup form
     * @param form The form which needs to be check as signup form or not.
     * @return 
     */
    public static boolean isSignUpForm(int form) {
        return form <= DisplayStackManager.FORM_SET_NAME;
    }
}