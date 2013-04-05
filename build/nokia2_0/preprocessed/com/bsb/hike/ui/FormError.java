/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.ui;

import com.bsb.hike.dto.AppState;
import com.bsb.hike.util.AppConstants;
import com.bsb.hike.util.AppResource;
import com.bsb.hike.util.Log;
import com.bsb.hike.util.Retryable;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.plaf.Border;

/**
 * @author Puneet Agarwal
 * @author Ankit Yadav
 */
public class FormError extends FormHikeBase {
    private final static String TAG = "FormError";
    private Label errorDescription;
    private Command exitCommand, retryCommand;
    private Retryable retryableObj;
    private String errorMessage;

    
    /**
     * Constructor for Error screen. This form shows up when some IO error occurs or there is no network connection.
     */
    public FormError() {

        getStyle().setBgColor(ColorCodes.formErrorBgColor, true);
        getStyle().setBgTransparency(255, true);

        setLayout(new BoxLayout(BoxLayout.Y_AXIS));

        initCommands();

        TextArea errorMsg = new TextArea(AppConstants.MSG_ERROR);
        errorMsg.getStyle().setFont(Fonts.LARGE, true);
        errorMsg.getStyle().setFgColor(ColorCodes.formErrorErrorMsg, true);
        errorMsg.getStyle().setAlignment(Component.CENTER);
        errorMsg.getSelectedStyle().setAlignment(Component.CENTER);
        errorMsg.getStyle().setMargin(Component.TOP, 25, true);
        errorMsg.getSelectedStyle().setMargin(Component.TOP, 25, true);
        errorMsg.getStyle().setBorder(null, true);
        errorMsg.getSelectedStyle().setBorder(null, true);
        errorMsg.getStyle().setBgTransparency(0, true);
        errorMsg.getSelectedStyle().setBgTransparency(0, true);
        errorMsg.setFocusable(false);
        errorMsg.setEditable(false);
        errorMsg.setRows(2);

        errorDescription = new Label(MSG_NO_NETWORK_FOUND);
        errorDescription.getStyle().setPadding(Component.TOP, 10, true);
        errorDescription.getStyle().setPadding(Component.BOTTOM, 30, true);
        errorDescription.getStyle().setFgColor(ColorCodes.formErrorReason, true);
        errorDescription.getStyle().setBgTransparency(0, true);
        errorDescription.getStyle().setAlignment(Component.CENTER, true);

        Container upperContainer = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        upperContainer.getStyle().setBgTransparency(255, true);
        upperContainer.getStyle().setBgColor(ColorCodes.formErrorUpperContainerDarkGrey, true);
        upperContainer.getStyle().setBorder(Border.createCompoundBorder(null, Border.createLineBorder(1, ColorCodes.formErrorSeperator), null, null), true);
        upperContainer.addComponent(errorMsg);
        upperContainer.addComponent(errorDescription);

        addComponent(upperContainer);
    }

    
    /**
     * This method initializes all the commands which are there on the current form.
     */
    private void initCommands() {

        exitCommand = new Command(AppConstants.LBL_EXIT) {
            public void actionPerformed(ActionEvent evt) {
                super.actionPerformed(evt);
                DisplayStackManager.showForm(AppState.getForm());
            }
        };

        retryCommand = new Command(LBL_RETRY, AppResource.getImageFromResource(PATH_RETRY_COMMAND_ICON)) {
            public void actionPerformed(ActionEvent evt) {
                super.actionPerformed(evt);
                if (retryableObj != null) {
                    retryableObj.retry();
                } else {
                             
                    Log.v(TAG, "retry object is null");
                    DisplayStackManager.showForm(AppState.getForm());
                }
            }
        };

        addCommand(retryCommand);
        addCommand(exitCommand);
        setBackCommand(exitCommand);
        setDefaultCommand(retryCommand);
    }
    
    
    /**
     * This method is called from outside the class. This method assigns the value of error message to the local variable of this class.
     * @param errorMessage 
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * This method is called from outside the class. This method assigns the retryable object, so that if user gets some error, then he/she can retry 
     * the same request from retry button.
     * @param retryableObj 
     */
    public void setRetryableObj(Retryable retryableObj) {
        this.retryableObj = retryableObj;
    }

    
    /**
     * This delegated method is called just after showing the form. This method sets the error text message.
     */
    protected void onShow() {
        if(errorMessage == null){
            errorMessage = "Error occured.";
        }
        errorDescription.setText(errorMessage);
    }
}