package com.bsb.hike.ui;

import com.bsb.hike.dto.AppState;
import com.bsb.hike.io.ClientConnectionHandler;
import com.bsb.hike.io.ConnectionFailedException;
import com.bsb.hike.ui.component.SignUpTextArea;
import com.bsb.hike.util.AppConstants;
import com.bsb.hike.util.AppResource;
import com.bsb.hike.util.BaseRetryable;
import com.bsb.hike.util.Log;
import com.bsb.hike.util.UIHelper;
import com.sun.lwuit.Button;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.plaf.Border;
import java.io.IOException;

/**
 * @author Puneet Agarwal
 * @author Ankit Yadav
 */
public class FormEnterPin extends FormHikeBase {

    private TextArea thanksMsgTextArea;
    private TextArea numberInput;
    private Command nextCommand;
    private Button callMe;
    private boolean clicked = false;
    private Label errorMsgTxtVw;
    private final static String TAG = "FormEnterPin";

    
    /**
     * Constructor of Enter Pin form. This form asks user for the PIN. Initializing and adding all components are done here.
     */
    public FormEnterPin() {

        initCommands();

        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        getStyle().setBgColor(ColorCodes.signupScreenBgColor, true);
        getStyle().setBgTransparency(255, true);
        
        callMe = new Button(LBL_CALL_ME);
        callMe.setEnabled(true);
        callMe.getStyle().setBorder(Border.createEmpty(), true);
        callMe.getSelectedStyle().setBorder(Border.createEmpty(), true);
        callMe.getPressedStyle().setBorder(Border.createEmpty(), true);
        callMe.getUnselectedStyle().setBorder(Border.createEmpty(), true);
        callMe.getStyle().setBgTransparency(0, true);
        callMe.getSelectedStyle().setBgTransparency(0, true);
        callMe.getPressedStyle().setBgTransparency(0, true);
        callMe.getUnselectedStyle().setBgTransparency(0, true);
        callMe.getStyle().setAlignment(Component.CENTER);
        callMe.getSelectedStyle().setAlignment(Component.CENTER);
        callMe.getPressedStyle().setAlignment(Component.CENTER);
        callMe.getUnselectedStyle().setAlignment(Component.CENTER);
        callMe.getStyle().setUnderline(true);
        callMe.getSelectedStyle().setUnderline(true);
        callMe.getPressedStyle().setUnderline(true);
        callMe.getUnselectedStyle().setUnderline(true);
        callMe.getStyle().setFont(Fonts.SMALL, true);
        callMe.getSelectedStyle().setFont(Fonts.SMALL, true);
        callMe.getPressedStyle().setFont(Fonts.SMALL, true);
        callMe.getUnselectedStyle().setFont(Fonts.SMALL, true);
        callMe.getStyle().setMargin(Component.LEFT, 40, true);
        callMe.getSelectedStyle().setMargin(Component.LEFT, 40, true);
        callMe.getPressedStyle().setMargin(Component.LEFT, 40, true);
        callMe.getUnselectedStyle().setMargin(Component.LEFT, 40, true);
        callMe.getStyle().setMargin(Component.RIGHT, 40, true);
        callMe.getSelectedStyle().setMargin(Component.RIGHT, 40, true);
        callMe.getPressedStyle().setMargin(Component.RIGHT, 40, true);
        callMe.getUnselectedStyle().setMargin(Component.RIGHT, 40, true);
        callMe.getStyle().setMargin(Component.TOP, 10, true);
        callMe.getSelectedStyle().setMargin(Component.TOP, 10, true);
        callMe.getPressedStyle().setMargin(Component.TOP, 10, true);
        callMe.getUnselectedStyle().setMargin(Component.TOP, 10, true);
        callMe.getStyle().setFgColor(ColorCodes.signupScreenWrongNumberTextColor, true);
        callMe.getSelectedStyle().setFgColor(ColorCodes.signupScreenWrongNumberTextColor, true);
        callMe.getPressedStyle().setFgColor(ColorCodes.signupScreenWrongNumberTextColor, true);
        callMe.getUnselectedStyle().setFgColor(ColorCodes.signupScreenWrongNumberTextColor, true);
        callMe.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent evt) {
       
                if (!clicked) {
                        clicked = true;
                        new Thread(new Runnable() {
                            public void run() {
                                BaseRetryable addressUpdateRetryable = new BaseRetryable() {
                                    public void retry() {
                                        Log.v(TAG, "Retrying...");
                                        DisplayStackManager.showProgressForm(true, null);
                                        try {
                                            //String pin = numberInput.getText();
                                            //Log.v(TAG, "pin: " + pin);
                                            String msisdn = AppState.getNumber();
                                            if (msisdn != null) {
                                              ClientConnectionHandler.callMe(msisdn);
                                              callMe.setVisible(false);
                                              callMe.setEnabled(false);
                                              // removeComponent(callMe);
                                              DisplayStackManager.showForm(DisplayStackManager.FORM_ENTER_PIN);
                                             
                                            } else {
                                                Log.v(TAG, "Entered into else block");
                                                DisplayStackManager.showForm(DisplayStackManager.FORM_ENTER_PIN);
                                            }
                                        } catch (Exception ex) {
                                            
                                            if (ex instanceof IOException || ex instanceof ConnectionFailedException) {
                                                errorForm.setErrorMessage(NO_NETWORK_TITLE);
                                            }
                                            errorForm.show();
                                        }
                                    }
                                };
                                addressUpdateRetryable.retry();
                                clicked = false;
                            }
                        }).start();
                   
            }
           }
        });
        
        thanksMsgTextArea = new TextArea(MSG_ENTER_PIN_CANVAS_1);
        thanksMsgTextArea.setEditable(false);
        thanksMsgTextArea.setRows(1);
        thanksMsgTextArea.setSingleLineTextArea(true);
        thanksMsgTextArea.getStyle().setFont(Fonts.LARGE, true);
        thanksMsgTextArea.getSelectedStyle().setFont(Fonts.LARGE, true);
        thanksMsgTextArea.getStyle().setPadding(Component.TOP, 20, true);
        thanksMsgTextArea.getSelectedStyle().setPadding(Component.TOP, 20, true);
        thanksMsgTextArea.getStyle().setPadding(Component.BOTTOM, 3, true);
        thanksMsgTextArea.getSelectedStyle().setPadding(Component.BOTTOM, 3, true);
        thanksMsgTextArea.getStyle().setAlignment(Component.CENTER, true);
        thanksMsgTextArea.getSelectedStyle().setAlignment(Component.CENTER, true);
        thanksMsgTextArea.getStyle().setFgColor(ColorCodes.signupScreenTopMsgColor, true);
        thanksMsgTextArea.getSelectedStyle().setFgColor(ColorCodes.signupScreenTopMsgColor, true);
        thanksMsgTextArea.getStyle().setBgColor(ColorCodes.signupScreenTopContainerBgColor, true);
        thanksMsgTextArea.getSelectedStyle().setBgColor(ColorCodes.signupScreenTopContainerBgColor, true);
        thanksMsgTextArea.getStyle().setBorder(Border.createEmpty(), true);
        thanksMsgTextArea.getSelectedStyle().setBorder(Border.createEmpty(), true);

        TextArea getSMSPinTextArea = new TextArea(MSG_ENTER_PIN_CANVAS_2);
        getSMSPinTextArea.setFocusable(false);
        getSMSPinTextArea.setEditable(false);
        getSMSPinTextArea.setRows(1);
        getSMSPinTextArea.setSingleLineTextArea(true);
        getSMSPinTextArea.getStyle().setFont(Fonts.LARGE, true);
        getSMSPinTextArea.getStyle().setAlignment(Component.CENTER, true);
        getSMSPinTextArea.getStyle().setPadding(Component.TOP, 0, true);
        getSMSPinTextArea.getSelectedStyle().setPadding(Component.TOP, 0, true);
        getSMSPinTextArea.getSelectedStyle().setAlignment(Component.CENTER, true);
        getSMSPinTextArea.getStyle().setFgColor(ColorCodes.signupScreenTopMsgColor, true);
        getSMSPinTextArea.getSelectedStyle().setFgColor(ColorCodes.signupScreenTopMsgColor, true);
        getSMSPinTextArea.getStyle().setBgColor(ColorCodes.signupScreenTopContainerBgColor, true);
        getSMSPinTextArea.getSelectedStyle().setBgColor(ColorCodes.signupScreenTopContainerBgColor, true);
        getSMSPinTextArea.getStyle().setBorder(Border.createEmpty(), true);
        getSMSPinTextArea.getSelectedStyle().setBorder(Border.createEmpty(), true);

        Label enterPinBelow = new Label(MSG_ENTER_PIN_BELOW_CANVAS);
        enterPinBelow.getStyle().setBgTransparency(0, true);
        enterPinBelow.getStyle().setFgColor(ColorCodes.signupScreenMiddleMsgColor, true);
        enterPinBelow.getStyle().setAlignment(Component.CENTER, true);
        enterPinBelow.getStyle().setPadding(Component.BOTTOM, 25, true);

        Container topContainer = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        topContainer.getStyle().setBorder(Border.createCompoundBorder(null, Border.createLineBorder(1, ColorCodes.signupScreenMiddleSeperator), null, null), true);
        topContainer.getStyle().setBgColor(ColorCodes.signupScreenTopContainerBgColor, true);
        topContainer.getStyle().setBgTransparency(255, true);
        topContainer.addComponent(thanksMsgTextArea);
        topContainer.addComponent(getSMSPinTextArea);
        topContainer.addComponent(enterPinBelow);

        numberInput = new SignUpTextArea(TextArea.NUMERIC | TextArea.NON_PREDICTIVE);
        
        errorMsgTxtVw = new Label(EMPTY_STRING);
        errorMsgTxtVw.getStyle().setAlignment(Component.CENTER, true);
        errorMsgTxtVw.getStyle().setPadding(Component.TOP, 10, true);
        errorMsgTxtVw.getStyle().setBgTransparency(0, true);
        errorMsgTxtVw.getStyle().setFont(Fonts.SMALL, true);
        errorMsgTxtVw.getStyle().setFgColor(ColorCodes.signupScreenMiddleMsgColor, true);
        errorMsgTxtVw.setVisible(false);

        Button wrongNumberEnteredButton = new Button(LBL_ENTERED_WRONG_NUMBER);
        wrongNumberEnteredButton.getStyle().setBorder(Border.createEmpty(), true);
        wrongNumberEnteredButton.getSelectedStyle().setBorder(Border.createEmpty(), true);
        wrongNumberEnteredButton.getPressedStyle().setBorder(Border.createEmpty(), true);
        wrongNumberEnteredButton.getUnselectedStyle().setBorder(Border.createEmpty(), true);
        wrongNumberEnteredButton.getStyle().setBgTransparency(0, true);
        wrongNumberEnteredButton.getSelectedStyle().setBgTransparency(0, true);
        wrongNumberEnteredButton.getPressedStyle().setBgTransparency(0, true);
        wrongNumberEnteredButton.getUnselectedStyle().setBgTransparency(0, true);
        wrongNumberEnteredButton.getStyle().setAlignment(Component.CENTER);
        wrongNumberEnteredButton.getSelectedStyle().setAlignment(Component.CENTER);
        wrongNumberEnteredButton.getPressedStyle().setAlignment(Component.CENTER);
        wrongNumberEnteredButton.getUnselectedStyle().setAlignment(Component.CENTER);
        wrongNumberEnteredButton.getStyle().setUnderline(true);
        wrongNumberEnteredButton.getSelectedStyle().setUnderline(true);
        wrongNumberEnteredButton.getPressedStyle().setUnderline(true);
        wrongNumberEnteredButton.getUnselectedStyle().setUnderline(true);
        wrongNumberEnteredButton.getStyle().setFont(Fonts.SMALL, true);
        wrongNumberEnteredButton.getSelectedStyle().setFont(Fonts.SMALL, true);
        wrongNumberEnteredButton.getPressedStyle().setFont(Fonts.SMALL, true);
        wrongNumberEnteredButton.getUnselectedStyle().setFont(Fonts.SMALL, true);
        wrongNumberEnteredButton.getStyle().setMargin(Component.TOP, 10, true);
        wrongNumberEnteredButton.getSelectedStyle().setMargin(Component.TOP, 10, true);
        wrongNumberEnteredButton.getPressedStyle().setMargin(Component.TOP, 10, true);
        wrongNumberEnteredButton.getUnselectedStyle().setMargin(Component.TOP, 10, true);
        wrongNumberEnteredButton.getStyle().setFgColor(ColorCodes.signupScreenWrongNumberTextColor, true);
        wrongNumberEnteredButton.getSelectedStyle().setFgColor(ColorCodes.signupScreenWrongNumberTextColor, true);
        wrongNumberEnteredButton.getPressedStyle().setFgColor(ColorCodes.signupScreenWrongNumberTextColor, true);
        wrongNumberEnteredButton.getUnselectedStyle().setFgColor(ColorCodes.signupScreenWrongNumberTextColor, true);
        wrongNumberEnteredButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                DisplayStackManager.showForm(DisplayStackManager.FORM_ENTER_NUMBER);
            }
        });

        addComponent(topContainer);
        addComponent(numberInput);
        addComponent(errorMsgTxtVw);
        addComponent(callMe);
        addComponent(wrongNumberEnteredButton);
    }

    
    /**
     * This method initializes all the commands which are there on the current form.
     */
    private void initCommands() {

        nextCommand = new Command(AppConstants.LBL_NXT, AppResource.getImageFromResource(sentInviteImage)) {
            public void actionPerformed(ActionEvent evt) {
                super.actionPerformed(evt);
                Log.v(TAG, "Pin Length is: " + numberInput.getText().trim().length());
                if (numberInput.getText().trim().length() > 3 && numberInput.getText().trim().length() <= 6) {
                    if (!clicked) {
                        clicked = true;
                        new Thread(new Runnable() {
                            public void run() {
                                BaseRetryable addressUpdateRetryable = new BaseRetryable() {
                                    public void retry() {
                                                
                                        Log.v(TAG, "Retrying...");
                                        DisplayStackManager.showProgressForm(true, MSG_VERIFYING_PIN_TXT);
                                        try {
                                            String pin = numberInput.getText();
                                                    
                                            Log.v(TAG, "pin: " + pin);
                                            String msisdn = ClientConnectionHandler.PostValidate(AppState.getNumber(), pin);
                                            if (msisdn != null) {
                                                         
                                                Log.v(TAG, "Entered into if block");
                                                AppState.setNumber(msisdn);
                                                DisplayStackManager.showForm(DisplayStackManager.FORM_SET_NAME);
                                            } else {
                                                         
                                                Log.v(TAG, "Entered into else block");       
                                                DisplayStackManager.showForm(DisplayStackManager.FORM_ENTER_PIN);
                                              
                                                UIHelper.runOnLcdUiThread(new Runnable() {
                                                    public void run() {
                                                         errorMsgTxtVw.setText(MSG_INVALID_PIN);
                                                         repaint();
                                                    }
                                                });
                                               
                                            }
                                        } catch (Exception ex) {
                                                    
                                            Log.v(TAG, "exception in verifying pin:" + ex.getClass().getName());
                                            if (ex instanceof IOException || ex instanceof ConnectionFailedException) {
                                                errorForm.setErrorMessage(NO_NETWORK_TITLE);
                                            }
                                            errorForm.show();
                                        }
                                    }
                                };
                                addressUpdateRetryable.retry();
                                clicked = false;
                            }
                        }).start();
                    }
                } else {
                            
                    Log.v(TAG, "Entered into Error block");
                    thanksMsgTextArea.requestFocus();
                    errorMsgTxtVw.setVisible(true);
                    errorMsgTxtVw.setText(MSG_INVALID_PIN);
                    repaint();
                }
            }
        };

        addCommand(nextCommand);
        setDefaultCommand(nextCommand);
    }

    
    /**
     * This delegated method is called just after showing the form. 
     */
    protected void onShow() {
        //call is befor super.onShow() to get previous form
        if (AppState.getForm() == DisplayStackManager.FORM_ENTER_NUMBER) {
            errorMsgTxtVw.setVisible(false);
            //make the call me button visible
            callMe.setVisible(true);
            callMe.setEnabled(true);
        } else {
            errorMsgTxtVw.setVisible(true);
        }
        thanksMsgTextArea.requestFocus();
        super.onShow();
        numberInput.setText(EMPTY_STRING);
    }
}
