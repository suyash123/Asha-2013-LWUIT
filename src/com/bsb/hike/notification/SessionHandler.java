package com.bsb.hike.notification;

import com.bsb.hike.Hike;
import com.bsb.hike.io.ClientConnectionHandler;
import com.bsb.hike.io.ConnectionFailedException;
import com.bsb.hike.util.AppConstants;
import com.bsb.hike.util.Log;
import com.nokia.notifications.NotificationError;
import com.nokia.notifications.NotificationException;
import com.nokia.notifications.NotificationInfo;
import com.nokia.notifications.NotificationMessage;
import com.nokia.notifications.NotificationPayload;
import com.nokia.notifications.NotificationSession;
import com.nokia.notifications.NotificationSessionFactory;
import com.nokia.notifications.NotificationSessionListener;
import com.nokia.notifications.NotificationState;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

/**
 * Class that takes care of the application's Notification API related
 * functionality.
 *
 * @author Sudheer
 */
public class SessionHandler implements NotificationSessionListener {

    private NotificationSession session;
    private Hike midlet;
    private boolean opened = false;
    // By default use register() (default useNoa setting) and if enable/disable is defined then use register(boolean).
    private Boolean useNoa = null;
    private static final String TAG = "SessionHandler";
    private boolean isPushReceived = false;
    private String strPayload;
    //private static boolean isNotifRendering = false;

    public SessionHandler(Hike midlet) {
        this.midlet = midlet;
    }

    public boolean isOpened() {
        return opened;
    }

    public void close() {
        if (session != null) {
            session.close();
        }
    }

    public void openSession() throws NotificationException {
        session = NotificationSessionFactory.openSession(midlet, AppConstants.SERVICE_ID, AppConstants.APPLICATION_ID, this);
        opened = true;
    }

    public void closeSession() {
        close();
        opened = false;
    }

   /* public boolean isWakeUp0() throws NotificationException {
        return session.isWakeUp();
    }

    public void setWakeUp0(boolean enabled) throws NotificationException {
        session.setWakeUp(enabled);
    }*/

    public int getReceiveAtLatest0(boolean quiet) throws NotificationException {
        int ral = session.getReceiveAtLatest();
        if (!quiet) {
            showInfoText("getReceiveAtLatest: " + ral);
        }
        return ral;
    }

    public void setReceiveAtLatest(int value) throws NotificationException {
        showInfoText("setReceiveAtLatest: new value=" + value);
        session.setReceiveAtLatest(value);
    }

    public void register(Boolean useNoa) throws NotificationException {
        if (useNoa == null) {
            session.registerApplication();

        } else {
            session.registerApplication(useNoa.booleanValue());
        }
    }

    public void unregister0() throws NotificationException {
        session.unregisterApplication();
    }

    public void getNid0() throws NotificationException {
        session.getNotificationInformation();
    }

    /* (non-Javadoc)
     * @see com.nokia.notifications.NotificationSessionListener#messageReceived(com.nokia.notifications.NotificationMessage)
     */
    public void messageReceived(NotificationMessage message) {
        // Handle message information.
        isPushReceived = true;
        
        String from = message.getFrom();
        String sender = message.getSenderInformation();
        Date timestamp = message.getTimestamp();
        String title = message.getTitle();

        // Get the payload object from the message.
        NotificationPayload payload = message.getPayload();

        // Get the payload encoding and type information.
        String encoding = null;
        String type = null;

        if (payload != null) {
            encoding = payload.getEncoding();
            type = payload.getType();
        }

        StringBuffer buf = new StringBuffer();
        StringBuffer append = buf.append("Message received:").append("Message received:").append(" from: ").append(from).append(" sender: ").append(sender).append(" title: ").append(title).append(" timestamp: ").append(timestamp).append(" encoding: ").append(encoding).append(" type: ").append(type);

        showInfoText(append.toString());

        if (payload == null) {
            showInfoText("No payload");

        } else {

            if (NotificationPayload.TYPE_APPLICATION_OCTET_STREAM.equals(type)) {
                showInfoText("Message payload content is binary data.");
            }

            // Check the encoding of the payload.
            if (NotificationPayload.ENCODING_BASE64.equals(encoding)) {
                // Decode the Base64 encoded payload.
                try {
                    byte[] decodedPayload = payload.getBase64Data();

                    // Do something with the decoded data.
                    showInfoText("Message payload is base64 encoded. Length: " + decodedPayload.length);

                } catch (IOException e) {
                    showErrorText("Encoding is not base64.");
                }

            } else if (NotificationPayload.ENCODING_NONE.equals(encoding)
                    || NotificationPayload.ENCODING_STRING.equals(encoding)) {
                // Payload is not encoded. Handle it as plain text.
                strPayload = payload.getData();

                // Display the message, etc.
                showInfoText("Payload:");
                showInfoText(strPayload);
                //processMessage();
            } else {
                // Unknown custom encoding defined in the service.
                showInfoText("Message payload is encoded with application defined custom value: " + encoding);
            }
        }
    }

    public boolean isPushReceived() {
        return isPushReceived;
    }

    /* (non-Javadoc)
     * @see com.nokia.notifications.NotificationSessionListener#stateChanged(com.nokia.notifications.NotificationState)
     */
    public void stateChanged(NotificationState state) {
        int sessionState = state.getSessionState();
        int errorCode = state.getSessionError();

        showInfoText("State changed:");

        // Handle the state and the possible error.
        switch (errorCode) {
            case NotificationError.ERROR_NONE:
                // An asynchronous operation has finished successfully.
                showInfoText(" error code: ERROR_NONE");
                break;

            case NotificationError.ERROR_ENABLER_NOT_FOUND:
                showInfoText(" error code: ERROR_ENABLER_NOT_FOUND");
                break;

            case NotificationError.ERROR_NO_NETWORK:
                showInfoText(" error code: ERROR_NO_NETWORK");
                break;

            case NotificationError.ERROR_APPLICATION_ID_CONFLICT:
                showInfoText(" error code: ERROR_APPLICATION_ID_CONFLICT");
                break;

            case NotificationError.ERROR_NO_ACCOUNT:
                showInfoText(" error code: ERROR_NO_ACCOUNT");
                break;

            case NotificationError.ERROR_SERVICE_UNAVAILABLE:
                showInfoText(" error code: ERROR_SERVICE_UNAVAILABLE");
                break;

            case NotificationError.ERROR_REGISTER_FAILED:
                showInfoText(" error code: ERROR_REGISTER_FAILED");
                break;

            case NotificationError.ERROR_UNREGISTER_FAILED:
                showInfoText(" error code: ERROR_UNREGISTER_FAILED");
                break;

            case NotificationError.ERROR_INVALID_SERVICE_ID:
                showInfoText(" error code: ERROR_INVALID_SERVICE_ID");
                break;

            case NotificationError.ERROR_NOTIFICATION_ID_INVALIDATED:
                showInfoText(" error code: ERROR_NOTIFICATION_ID_INVALIDATED");
                break;

            case NotificationError.ERROR_DISABLED_BY_USER:
                showInfoText(" error code: ERROR_DISABLED_BY_USER");
                break;

            case NotificationError.ERROR_NOT_REGISTERED:
                showInfoText(" error code: ERROR_NOT_REGISTERED");
                break;

            case NotificationError.ERROR_ENABLER_EXITED:
                showInfoText(" error code: ERROR_ENABLER_EXITED");
                break;

            default:
                // An error occurred during an asynchronous operation.
                showErrorText("Error code: unknown [" + errorCode + "]");
                break;
        }

        if (sessionState == NotificationState.STATE_ONLINE) {
            // Application receives notifications because its state is online.
            showInfoText(" state: online");
            showInfoText("< Application may receive notifications from the service.");
            if (errorCode == NotificationError.ERROR_NONE) {
                showInfoText(" state: online && error code: ERROR_NONE");
                getNid();
            }
        } else if (sessionState == NotificationState.STATE_OFFLINE) {
            // Application does not receives notifications because its state is offline.
            showInfoText(" state: offline");
            showInfoText("< Application can not receive notifications from the service.");

        } else if (sessionState == NotificationState.STATE_CONNECTING) {
            // Application is connecting to receive notifications.                      
            showInfoText(" state: connecting");
            showInfoText("< Application can not receive notifications from the service yet.");

        } else {
            // Unknown state.
            showErrorText("State: UNKNOWN [" + sessionState + "]");
        }
    }

    /* (non-Javadoc)
     * @see com.nokia.notifications.NotificationSessionListener#infoReceived(com.nokia.notifications.NotificationInfo)
     */
    public void infoReceived(NotificationInfo info) {
        // Get the notification ID from the info.
        final String nid = info.getNotificationId();
        if (nid != null) {
            showInfoText("< NID received from Nokia Server:  " + nid);
            new Thread() {
                boolean sent = false;
                public void run() {
                    try {
                        sent = ClientConnectionHandler.sendPushNotifData(nid);
                        showInfoText("> NID sent to server.." + sent);
                        if (sent) {
//                            setWakeUp(true);
                        }
                    } catch (ConnectionFailedException ex) {
                    } catch (IOException ex) {
                    }
                    // Send the Notification ID to your service by using your own implementation.
                }
            }.start();
        } else {
            showInfoText("< NID not received from Nokia Server:  ");
        }
    }

    //from CommandController
    public void initialize() {
        // Check the availability of Notifications Enabler before trying to connect
//        NotificationsEnablerInstaller installer = InstallerFactory.getInstaller();
//        installer.checkAndUpdateNapiEnabler(this, null, midlet);
        Log.v(TAG, "Intialise ::: Register");
        open();
        register();
    }

    /**
     * @see
     * NotificationSessionFactory#openSession(javax.microedition.midlet.MIDlet,
     * String, String, NotificationSessionListener)
     */
    public void open() {

        showInfoText("> Open");

        if (!isOpened()) {
            try {
                showInfoText(" service ID: " + AppConstants.SERVICE_ID);
                showInfoText(" application ID: " + AppConstants.APPLICATION_ID);
                openSession();
                showInfoText(" Notification Session opened.");

            } catch (NotificationException e) {
                int reason = e.getReason();

                if (!isCommonNotificationException(reason)) {
                    showErrorText("Unknown NotificationException reason [" + reason + "]");
                }

            } catch (NullPointerException e) {
                showErrorText("Null parameter for open.");

            } catch (IllegalArgumentException e) {
                showErrorText("Unacceptable serviceId or applicationId.");

            } catch (Exception e) {
                showErrorText("Unknown exception: " + e);
            }
        }

        showInfoText("< Open");
    }

    private boolean isCommonNotificationException(int reason) {
        boolean common = false;

        if (reason == NotificationError.ERROR_OPERATION_FAILED) {
            showErrorText("Could not open session [ERROR_OPERATION_FAILED].");
            common = true;

        } else if (reason == NotificationError.ERROR_NO_ACCOUNT) {
            showErrorText("Could not open session [ERROR_NO_ACCOUNT].");
            common = true;

        } else if (reason == NotificationError.ERROR_NO_NETWORK) {
            showErrorText("Could not open session [ERROR_NO_NETWORK].");
            common = true;

        } else if (reason == NotificationError.ERROR_SERVICE_UNAVAILABLE) {
            showErrorText("Could not open session [ERROR_SERVICE_UNAVAILABLE].");
            common = true;

        } else if (reason == NotificationError.ERROR_ENABLER_NOT_FOUND) {
            showErrorText("Could not open session [ERROR_ENABLER_NOT_FOUND].");
            common = true;

        } else if (reason == NotificationError.ERROR_NOT_REGISTERED) {
            showErrorText("Could not open session [ERROR_NOT_REGISTERED].");
            common = true;
        }

        return common;
    }

    /**
     * @see NotificationSession#registerApplication()
     */
    public void register() {
        showInfoText("> Register");

        try {
            // This starts an asynchronous operations.
            // When registering is ready, 
            // stateChanged function is called.
            // See, how the flow continues there.
            showInfoText("registerApplication");
            register(useNoa);

        } catch (NotificationException e) {
            int reason = e.getReason();

            if (!isCommonNotificationException(reason)) {

                if (reason == NotificationError.ERROR_SESSION_CLOSED) {
                    showErrorText("Could not register [ERROR_SESSION_CLOSED].");

                } else if (reason == NotificationError.ERROR_APPLICATION_ID_CONFLICT) {
                    showErrorText("Could not register [ERROR_APPLICATION_ID_CONFLICT].");

                } else if (reason == NotificationError.ERROR_SERVICE_ID_CONFLICT) {
                    showErrorText("Could not register [ERROR_SERVICE_ID_CONFLICT].");

                } else if (reason == NotificationError.ERROR_REGISTER_FAILED) {
                    showErrorText("Could not register [ERROR_REGISTER_FAILED].");

                } else {
                    showErrorText("Unknown NotificationException reason [" + reason + "]");
                }
            }

        } catch (Exception e) {
            showErrorText("Unknown exception: " + e);
        }
    }

    /**
     * @see NotificationSession#unregisterApplication()
     */
    public void unregister() {
        showInfoText("> Unregister");

        try {
            // This starts an asynchronous operations.
            // When unregistering is ready, 
            // stateChanged function is called.
            // See, how the flow continues there.
            unregister0();

        } catch (NotificationException e) {
            int reason = e.getReason();

            if (!isCommonNotificationException(reason)) {

                if (reason == NotificationError.ERROR_SESSION_CLOSED) {
                    showErrorText("Could not unregister [ERROR_SESSION_CLOSED].");

                } else if (reason == NotificationError.ERROR_UNREGISTER_FAILED) {
                    showErrorText("Could not unregister [ERROR_UNREGISTER_FAILED].");

                } else {
                    showErrorText("Unknown NotificationException reason [" + reason + "]");
                }
            }

        } catch (Exception e) {
            showErrorText("Unknown exception: " + e);
        }
    }

    /**
     * @see NotificationSession#getNotificationInformation()
     */
    public void getNid() {
        showInfoText("> Getting NID");

        try {
            // This starts an asynchronous operations.
            // When operation is ready, 
            // infoReceived function is called.
            // See, how the flow continues there.
            getNid0();

        } catch (NotificationException e) {
            int reason = e.getReason();

            if (!isCommonNotificationException(reason)) {

                if (reason == NotificationError.ERROR_NOT_ALLOWED) {
                    showErrorText("Session is not online [ERROR_NOT_ALLOWED].");

                } else if (reason == NotificationError.ERROR_SESSION_CLOSED) {
                    showErrorText("Session is closed [ERROR_SESSION_CLOSED].");

                } else {
                    showErrorText("Unknown NotificationException reason [" + reason + "]");
                }
            }

        } catch (Exception e) {
            showErrorText("Unknown exception:  " + e);
        }
    }

    /**
     * @see NotificationSession#isWakeUp()
     */
    /*public void isWakeUp() {
        showInfoText("> Is wake-up");

        try {
            boolean state = isWakeUp0();
            showInfoText(" wakeup state: " + state);

        } catch (NotificationException e) {
            int reason = e.getReason();

            if (reason == NotificationError.ERROR_SESSION_CLOSED) {
                showErrorText("Session is closed [ERROR_SESSION_CLOSED].");

            } else if (reason == NotificationError.ERROR_OPERATION_FAILED) {
                showErrorText("Operation could not be completed [ERROR_OPERATION_FAILED].");

            } else {
                showErrorText("Unknown NotificationException reason [" + reason + "]");
            }

        } catch (Exception e) {
            showErrorText("Unknown exception: " + e);
        }

        showInfoText("< Is wake-up");
    }*/

    /**
     * @param wakeUp If true then the application is woken up in the event of
     * notifications.
     * @see NotificationSession#setWakeUp(boolean)
     */
    /*public void setWakeUp(boolean wakeUp) {
        showInfoText("> Set wake-up: " + wakeUp);

        try {
            setWakeUp0(wakeUp);

        } catch (NotificationException e) {
            int reason = e.getReason();

            if (reason == NotificationError.ERROR_SESSION_CLOSED) {
                showErrorText("Session is closed [ERROR_SESSION_CLOSED].");

            } else if (reason == NotificationError.ERROR_OPERATION_FAILED) {
                showErrorText("Operation could not be completed [ERROR_OPERATION_FAILED].");

            } else {
                showErrorText("Unknown NotificationException reason [" + reason + "]");
            }

        } catch (Exception e) {
            showErrorText("Unknown exception: " + e);
        }

        showInfoText("< Set wake-up");
    }*/

    private void setReceiveAtLatest() {
        showInfoText("> Set receive at latest");

        try {
            setReceiveAtLatest(100);

        } catch (NotificationException e) {
            int reason = e.getReason();

            if (reason == NotificationError.ERROR_SESSION_CLOSED) {
                showErrorText("Session is closed [ERROR_SESSION_CLOSED].");

            } else if (reason == NotificationError.ERROR_OPERATION_FAILED) {
                showErrorText("Operation could not be completed [ERROR_OPERATION_FAILED].");

            } else {
                showErrorText("Unknown NotificationException reason [" + reason + "]");
            }

        } finally {
            showInfoText("< Set receive at latest");
        }
    }

    private int getReceiveAtLatest(boolean silent) {
        if (!silent) {
            showInfoText("> Get receive at latest");
        }

        int ral = -1;

        try {
            ral = getReceiveAtLatest0(silent);

        } catch (NotificationException e) {
            int reason = e.getReason();

            if (reason == NotificationError.ERROR_SESSION_CLOSED) {
                showErrorText("Session is closed [ERROR_SESSION_CLOSED].");

            } else if (reason == NotificationError.ERROR_OPERATION_FAILED) {
                showErrorText("Operation could not be completed [ERROR_OPERATION_FAILED].");

            } else {
                showErrorText("Unknown NotificationException reason [" + reason + "]");
            }

        } finally {
            if (!silent) {
                showInfoText("< Get receive at latest");
            }
        }

        return ral;
    }

    /**
     * @see NotificationSessionFactory#getNotificationsEnablerVersion()
     */
    public void getVersion() {
        showInfoText("> Get version");

        String version = NotificationSessionFactory.getNotificationsEnablerVersion();
        showInfoText(" Notification Enabler version: " + version);

        showInfoText("< Get version");
    }

    private void showInfoText(String text) {
                 
        Log.v(TAG, text);
    }

    private void showErrorText(String text) {
                
        Log.v(TAG, "ERROR> " + text);
    }

    /* (non-Javadoc)
     * @see com.nokia.notifications.installer.InstallListener#installResult(int)
     */
    //@Override
   /* public void installResult(int result) {
        // 
        switch (result) {
            case InstallListener.ALREADY_EXISTS:
            case InstallListener.INSTALLATION_OK:
                // NNAPI enabler should now be available
                open();
                register();
                break;

            case InstallListener.DEVICE_NOT_SUPPORTED:
                // No support - exit app
                exit();
                break;

            case InstallListener.CANCELLED_BY_USER:
                // Not available, but install canceled by user - exit app
                exit();
                break;

            case InstallListener.INSTALLATION_FAILED:
                // Installation failed for some reason, can't continue - exit app
                exit();
                break;

            case InstallListener.MEMORY_FULL:
                // Memory is full, can't continue - exit app
                exit();
                break;
        }
    }*/

    private void exit() {
        closeSession();
    }

    public String processMessage() {
       
        return strPayload;
    }
    
}
