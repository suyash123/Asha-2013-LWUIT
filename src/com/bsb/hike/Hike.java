package com.bsb.hike;

import com.bsb.hike.dto.AppState;
import com.bsb.hike.io.ClientConnectionHandler;
import com.bsb.hike.io.mqtt.MqttConnectionHandler;
import com.bsb.hike.ui.DisplayStackManager;
import com.bsb.hike.ui.FormHikeBase;
import com.bsb.hike.util.AppConstants;
import com.bsb.hike.util.Log;
import com.bsb.hike.util.TextUtils;
import com.sun.lwuit.Display;
import java.util.Date;
import javax.microedition.midlet.MIDlet;

/**
 *
 * @author Ankit Yadav
 * @author Sudheer Keshav Bhat
 * @author Puneet Agarwal
 */
public class Hike extends MIDlet implements AppConstants {

    public static Hike sMidlet;
    public static final long launchTime = new Date().getTime();
    private static final String TAG = "Hike";
    private String platform, value;
    private boolean isNokiaOldVerDev = false;
    private boolean isFirstLaunch = true;
    public static String currentVer;

    /**
     * Hike MIDlet constructor.
     */
    public Hike() {
        sMidlet = this;
    }

    
    /**
     * This method is called on launching the app. This delegated method is checking device model and version number of the device to check support
     * for Hike on the user's device. Also checking App update on the server for critical and latest updates and then finally open Mqtt connection 
     * if the user is already signed up on Hike.
     */
    public void startApp() {
        Display.init(this);
        DisplayStackManager.showProgressForm(false, MSG_PROGRESS_SCREEN_LOADING);
        Log.v(TAG, "form shown");
        if (isFirstLaunch) {
            Log.openLogFile();
            try {
                platform = System.getProperty(PLATFORM_NAME_AND_VERSION);
                if (platform != null) {
                    if (TextUtils.contains(platform, NOKIA_305_MODEL)) {
                        int firmwareSeparator = platform.indexOf(SLASH_FORWARD);
                        if (firmwareSeparator != -1) {
                            value = platform.substring(firmwareSeparator + 1);
                            if (Float.parseFloat(value) < 5.87) {
                                isNokiaOldVerDev = true;
                            }
                        }
                    } 
                }
            } catch (NumberFormatException nfe) {
                Log.v(TAG, "Cannot parse value, Value is " + value);
            } finally {
                isFirstLaunch = false;
            }
        }
        Log.v(TAG, "platform checked");
        
        currentVer = getAppProperty(LBL_MIDLET_VERSION);
        
        if (isNokiaOldVerDev) {
            DisplayStackManager.showForm(DisplayStackManager.FORM_OLD_VERSION);
        } else {
            new Thread(new Runnable() {
                public void run() {
                    Log.v(TAG, "start app update state");
                    AppState.initUpdateState();
                    Log.v(TAG, "end app update state");
                    if (AppState.shouldCheckForUpdate()) {
                        Log.v(TAG, "Eligible to check for update" + new Date().getTime());                        
                        DisplayStackManager.showProgressForm(false, MSG_CHECKING_FOR_UPDATES);
                        try {
                            ClientConnectionHandler.checkForUpdate(currentVer, PLATFORM_NAME);
                        } catch (Exception ex) {
                            Log.v(TAG, "exception while checking for update: " + ex.getClass().getName());
                        }
                        
                        if (AppState.jad.isCritical() || !TextUtils.isEmpty(AppState.jad.getJadLatestVersion())) {
                            Log.v(TAG, "Latest Version value " + AppState.jad.getJadLatestVersion());
                            if (!AppState.jad.isCritical()) {
                                AppState.initializeState();
                            }
                            DisplayStackManager.showForm(DisplayStackManager.FORM_HIKE_UPDATE);
                        } else {
                            Log.v(TAG, "Latest Version value in else condition " + AppState.jad.getJadLatestVersion());
                            AppState.initializeState();
                            FormHikeBase form = DisplayStackManager.getForm(AppState.getForm(), true);
                            if (!form.isSignUp()) {
                                MqttConnectionHandler.getMqttConnectionHandler().open();
                            }
                            form.show();
                        }
                    } else {
                        Log.v(TAG, "Not eligible to check for update");
                        AppState.initializeState();
                        Log.v(TAG, "app initialized");
                        FormHikeBase form = DisplayStackManager.getForm(AppState.getForm(), true);
                        if (!form.isSignUp()) {
                            MqttConnectionHandler.getMqttConnectionHandler().open();
                        }
                        form.show();
                        Log.v(TAG, "conversation form shown");
                    }
                }
            }).start();
        }
    }

    /**
     * This method is called when user exits the app. This delegated method will close the Mqtt connection, persist app state in RMS, and finally
     * releasing app resources from the memory.
     * @param unconditional 
     */
    public void destroyApp(boolean unconditional) {
        MqttConnectionHandler.getMqttConnectionHandler().close();
        AppState.persistState();
        AppState.persisteUpdateState();
        releaseResources();
        notifyDestroyed();
    }

    
    protected void pauseApp() {
        // Notice:
        // By default, the pauseApp method is never called, but it can be
        // enabled with the Nokia-proprietary JAD attributes Nokia-MIDlet-Background-Event
        // and Nokia-MIDlet-Flip-Close. Even if pauseApp is not called, it still needs to be
        // included in the MIDlet.
    }

    /**
     * This method closes the opened log file which opens at launch of the app.
     */
    private void releaseResources() {
        Log.closeLogFile();
    }
}