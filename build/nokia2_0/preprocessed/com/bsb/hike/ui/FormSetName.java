/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.ui;

import com.bsb.hike.dto.AddressBookList;
import com.bsb.hike.dto.AppState;
import com.bsb.hike.io.ClientConnectionHandler;
import com.bsb.hike.io.ConnectionFailedException;
import com.bsb.hike.io.ContactHandler;
import com.bsb.hike.io.FileHandler;
import com.bsb.hike.io.mqtt.MqttConnectionHandler;
import com.bsb.hike.ui.component.SignUpTextArea;
import com.bsb.hike.util.AppConstants;
import com.bsb.hike.util.AppResource;
import com.bsb.hike.util.BaseRetryable;
import com.bsb.hike.util.Log;
import com.bsb.hike.util.TextUtils;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.plaf.Border;
import java.io.IOException;

/**
 * @author Puneet Agarwal
 * @author Ankit Yadav
 */
public class FormSetName extends FormHikeBase {

    private TextArea allSetMsgTextArea, onNumberMsgTextArea;
    private TextArea inputName;
    private Command nextCommand;
    private boolean clicked = false;
    private Label errorMsgTxtVw;
    private static final String TAG = "FormSetName";

    
    /**
     * Constructor of set name screen. This form asks user to input name on signup. Initializing and adding of all components takes place 
     * in constructor.
     */
    public FormSetName() {

        initCommands();

        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        getStyle().setBgColor(ColorCodes.signupScreenBgColor, true);
        getStyle().setBgTransparency(255, true);

        allSetMsgTextArea = new TextArea(MSG_ENTER_NAME_CANVAS_1);
        allSetMsgTextArea.setEditable(false);
        allSetMsgTextArea.setRows(1);
        allSetMsgTextArea.setSingleLineTextArea(true);
        allSetMsgTextArea.getStyle().setFont(Fonts.LARGE, true);
        allSetMsgTextArea.getSelectedStyle().setFont(Fonts.LARGE, true);
        allSetMsgTextArea.getStyle().setPadding(Component.TOP, 20, true);
        allSetMsgTextArea.getSelectedStyle().setPadding(Component.TOP, 20, true);
        allSetMsgTextArea.getStyle().setPadding(Component.BOTTOM, 3, true);
        allSetMsgTextArea.getSelectedStyle().setPadding(Component.BOTTOM, 3, true);
        allSetMsgTextArea.getStyle().setAlignment(Component.CENTER, true);
        allSetMsgTextArea.getSelectedStyle().setAlignment(Component.CENTER, true);
        allSetMsgTextArea.getStyle().setFgColor(ColorCodes.signupScreenTopMsgColor, true);
        allSetMsgTextArea.getSelectedStyle().setFgColor(ColorCodes.signupScreenTopMsgColor, true);
        allSetMsgTextArea.getStyle().setBgColor(ColorCodes.signupScreenTopContainerBgColor, true);
        allSetMsgTextArea.getSelectedStyle().setBgColor(ColorCodes.signupScreenTopContainerBgColor, true);
        allSetMsgTextArea.getStyle().setBorder(Border.createEmpty(), true);
        allSetMsgTextArea.getSelectedStyle().setBorder(Border.createEmpty(), true);

        onNumberMsgTextArea = new TextArea();
        onNumberMsgTextArea.setFocusable(false);
        onNumberMsgTextArea.setEditable(false);
        onNumberMsgTextArea.setRows(1);
        onNumberMsgTextArea.setSingleLineTextArea(true);
        onNumberMsgTextArea.getStyle().setFont(Fonts.LARGE, true);
        onNumberMsgTextArea.getStyle().setAlignment(Component.CENTER, true);
        onNumberMsgTextArea.getStyle().setPadding(Component.TOP, 0, true);
        onNumberMsgTextArea.getSelectedStyle().setPadding(Component.TOP, 0, true);
        onNumberMsgTextArea.getSelectedStyle().setAlignment(Component.CENTER, true);
        onNumberMsgTextArea.getStyle().setFgColor(ColorCodes.signupScreenTopMsgColor, true);
        onNumberMsgTextArea.getSelectedStyle().setFgColor(ColorCodes.signupScreenTopMsgColor, true);
        onNumberMsgTextArea.getStyle().setBgColor(ColorCodes.signupScreenTopContainerBgColor, true);
        onNumberMsgTextArea.getSelectedStyle().setBgColor(ColorCodes.signupScreenTopContainerBgColor, true);
        onNumberMsgTextArea.getStyle().setBorder(Border.createEmpty(), true);
        onNumberMsgTextArea.getSelectedStyle().setBorder(Border.createEmpty(), true);

        Label whatsYourNameLabel = new Label(MSG_ENTER_NAME_BELOW_CANVAS);
        whatsYourNameLabel.getStyle().setBgTransparency(0, true);
        whatsYourNameLabel.getStyle().setFgColor(ColorCodes.signupScreenMiddleMsgColor, true);
        whatsYourNameLabel.getStyle().setAlignment(Component.CENTER, true);
        whatsYourNameLabel.getStyle().setPadding(Component.BOTTOM, 25, true);

        Container topContainer = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        topContainer.getStyle().setBorder(Border.createCompoundBorder(null, Border.createLineBorder(1, ColorCodes.signupScreenMiddleSeperator), null, null), true);
        topContainer.getStyle().setBgColor(ColorCodes.signupScreenTopContainerBgColor, true);
        topContainer.getStyle().setBgTransparency(255, true);
        topContainer.addComponent(allSetMsgTextArea);
        topContainer.addComponent(onNumberMsgTextArea);
        topContainer.addComponent(whatsYourNameLabel);

        inputName = new SignUpTextArea(TextArea.NON_PREDICTIVE);

        errorMsgTxtVw = new Label(EMPTY_STRING);
        errorMsgTxtVw.getStyle().setAlignment(Component.CENTER, true);
        errorMsgTxtVw.getStyle().setPadding(Component.TOP, 10, true);
        errorMsgTxtVw.getStyle().setBgTransparency(0, true);
        errorMsgTxtVw.getStyle().setFont(Fonts.SMALL, true);
        errorMsgTxtVw.getStyle().setFgColor(ColorCodes.signupScreenMiddleMsgColor, true);
        errorMsgTxtVw.setVisible(false);


        addComponent(topContainer);
        addComponent(inputName);
        addComponent(errorMsgTxtVw);
    }

    
    /**
     * This method initializes all the commands which are there on the current form.
     */
    private void initCommands() {
        nextCommand = new Command(AppConstants.LBL_NXT, AppResource.getImageFromResource(sentInviteImage)) {
            public void actionPerformed(ActionEvent evt) {
                super.actionPerformed(evt);
                if (!inputName.getText().trim().equals(EMPTY_STRING)) {
                    if (!clicked) {
                        clicked = true;
                        new Thread(new Runnable() {
                            public void run() {
                                BaseRetryable addressUpdateRetryable = new BaseRetryable() {
                                    public void retry() {
                                                 
                                        Log.v(TAG, "Retrying...");
                                        DisplayStackManager.showProgressForm(true, null);
                                        try {
                                            boolean success = ClientConnectionHandler.PostName(inputName.getText().trim());
                                            if (success) {
                                                DisplayStackManager.showProgressForm(true, MSG_SCANNING_CONTACT_PROGRESS);
                                                BaseRetryable addressUpdateRetryable = new BaseRetryable() {

                                                    public void retry() {
                                                                 
                                                        Log.v(TAG, "Retrying...");
                                                        DisplayStackManager.showProgressForm(true, null);
                                                        try {
                                                            if (!ClientConnectionHandler.PostAddressbook(ContactHandler.getAddressbookRequestArray(false), false)) {
                                                                //DisplayStackManager.getForm(DisplayStackManager.FORM_ERROR).show();
                                                            }
                                                            new Thread(){
                                                                public void run() {
                                                                    FileHandler.writeAddressBookToRMS(AddressBookList.getInstance());
                                                                }
                                                            }.start();
                                                        } catch (Exception ex) {
                                                            Log.v(TAG, "exception in sending addressbook:" + ex.getClass().getName());
                                                            if (ex instanceof IOException || ex instanceof ConnectionFailedException) {
                                                                errorForm.setErrorMessage(NO_NETWORK_TITLE);
                                                            }
                                                            errorForm.show();
                                                            return ;                                                          
                                                        }
                                                        MqttConnectionHandler.getMqttConnectionHandler().setfirstConnctionAfterRegistration(true);
                                                        MqttConnectionHandler.getMqttConnectionHandler().open();
                                                        DisplayStackManager.showForm(DisplayStackManager.FORM_CONVERSATION);
                                                                 
                                                        Log.v(TAG, "addressbook update finish");
                                                    }
                                                };
                                                addressUpdateRetryable.retry();
                                            } else {
                                                DisplayStackManager.showForm(DisplayStackManager.FORM_ERROR);
                                            }
                                            clicked = false;
                                        } catch (Exception ex) {
                                                     
                                            Log.v(TAG, "exception in set name account:" + ex.getClass().getName());
                                            if (ex instanceof IOException || ex instanceof ConnectionFailedException) {
                                                errorForm.setErrorMessage(NO_NETWORK_TITLE);
                                            }
                                            errorForm.show();
                                        }
                                    }
                                };
                                addressUpdateRetryable.retry();
                            }
                        }).start();
                    }
                } else {
                             
                    Log.v(TAG, "Entered into Error block");
                    errorMsgTxtVw.setVisible(true);
                    errorMsgTxtVw.setText(MSG_INVALID_NAME);
                    repaint();
                }

            }
        };

        addCommand(nextCommand);
        setDefaultCommand(nextCommand);
    }

    
    /**
     * This delegated method is called just after showing the form on the screen. This method is used to set the text (user phone number) after 
     * formatting in a particular manner.
     */
    protected void onShow() {
        allSetMsgTextArea.requestFocus();
        super.onShow();
        onNumberMsgTextArea.setText(TextUtils.formatPhoneNumber(AppState.getNumber(), true) + EXCLAIMATION);
        inputName.setText(EMPTY_STRING);
    }
}
