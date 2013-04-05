/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.util;

import com.bsb.hike.Hike;
import com.bsb.hike.dto.ChatModel;
import com.bsb.hike.dto.ChatModel.MessageStatus;
import com.bsb.hike.dto.MqttObjectModel.MessageType;
import com.sun.lwuit.Display;
import com.sun.lwuit.Image;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;

/**
 *
 * @author Sudheer Keshav Bhat
 */
public class UIHelper implements AppConstants {

    private static final String TAG = "UIHelper";
    
    /**
     * returns image as per the message status of chatmodel
     * @param model
     * @return 
     */
    public static Image getMsgStatusIcon(ChatModel model) {
        if (model == null) {
            return null;
        }
        int msgStatus = model.getMessageStatus();
        if (model.getType() == MessageType.SYSTEM) {
            return null;
        } else if (!model.isMe() && model.getMessageStatus() == MessageStatus.RECEIVED) {
            return AppResource.getImageFromResource(PATH_UNREAD_MSG_BLUE_ICON);
        } else if (msgStatus == MessageStatus.DELIVERED) {
            return AppResource.getImageFromResource(PATH_IC_DELIVERED);
        } else if (model.isMe() && msgStatus == MessageStatus.READ) {
            return AppResource.getImageFromResource(PATH_IC_READ);
        } else if (msgStatus == MessageStatus.SENT) {
            return AppResource.getImageFromResource(PATH_IC_SENT);
        } else if (msgStatus == MessageStatus.SENDING) {
            return AppResource.getImageFromResource(PATH_IC_SENDING);
        } else {
            return null;
        }
    }

    public static Alert getAlertDialog(String title, String alertText, String positiveButton, String negetiveButton, AlertType alertType) {

        Alert alertDialog = new Alert(title, alertText, null, alertType);
        alertDialog.addCommand(new javax.microedition.lcdui.Command(positiveButton, javax.microedition.lcdui.Command.OK, 1));
        alertDialog.addCommand(new javax.microedition.lcdui.Command(negetiveButton, javax.microedition.lcdui.Command.CANCEL, 1));

        return alertDialog;
    }

    /**
     * create and shows a toast
     * @param message 
     */
    public static void showToast(String message) {
        Alert toast = new Alert(null, message, null, AlertType.INFO);
        toast.setTimeout(1000);
        javax.microedition.lcdui.Display display = javax.microedition.lcdui.Display.getDisplay(Hike.sMidlet);
        //TODO isShown condition is of no use, as every new object will be always not shown : Ankit
        if (!toast.isShown() && (display != null && !(display.getCurrent() instanceof Alert))) {
            display.setCurrent(toast);
        }
    }

    /**
     * create and returns alert object 
     * @param title
     * @param alertText
     * @param positiveButton
     * @param alertType
     * @return 
     */
    public static Alert getSingleCommandAlert(String title, String alertText, String positiveButton, AlertType alertType) {
        Alert singleCommandAlert = new Alert(title, alertText, null, alertType);
        return singleCommandAlert;
    }

    /**
     * method to dispatch a task to LWUIT-UI thread
     * @param runnable 
     */
    public static void runOnLwuitUiThread(Runnable runnable) {
        Display.getInstance().callSerially(runnable);
    }

    /**
     * method to dispatch a task to LCDUI thread
     * @param runnable 
     */
    public static void runOnLcdUiThread(Runnable runnable) {
        javax.microedition.lcdui.Display.getDisplay(Hike.sMidlet).callSerially(runnable);
    }
}
