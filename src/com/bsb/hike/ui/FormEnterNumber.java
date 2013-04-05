package com.bsb.hike.ui;

import com.bsb.hike.dto.AppState;
import com.bsb.hike.io.ClientConnectionHandler;
import com.bsb.hike.io.ConnectionFailedException;
import com.bsb.hike.ui.component.SignUpTextArea;
import com.bsb.hike.util.AppResource;
import com.bsb.hike.util.BaseRetryable;
import com.bsb.hike.util.Log;
import com.bsb.hike.util.Validator;
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
 * @author Sudheer
 */
public class FormEnterNumber extends FormHikeBase {

    private TextArea couldntPullMsgTextAreaTop;
    private TextArea numberInput;
    private Command nextCommand;
    private boolean clicked = false;
    private static final String TAG = "FormEnterNumber";
    private Label errorMsgTxtVw;

    
    /**
     * Constructor of Enter number screen. This form asks user to enter his mobile number.
     */
    public FormEnterNumber() {
        
        initCommands();

        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        getStyle().setBgColor(ColorCodes.signupScreenBgColor, true);
        getStyle().setBgTransparency(255, true);

        couldntPullMsgTextAreaTop = new TextArea(MSG_ENTER_NUMBER_CANVAS1);
        couldntPullMsgTextAreaTop.setEditable(false);
        couldntPullMsgTextAreaTop.setRows(1);
        couldntPullMsgTextAreaTop.setSingleLineTextArea(true);
        couldntPullMsgTextAreaTop.getStyle().setFont(Fonts.LARGE, true);
        couldntPullMsgTextAreaTop.getSelectedStyle().setFont(Fonts.LARGE, true);
        couldntPullMsgTextAreaTop.getStyle().setPadding(Component.TOP, 20, true);
        couldntPullMsgTextAreaTop.getSelectedStyle().setPadding(Component.TOP, 20, true);
        couldntPullMsgTextAreaTop.getStyle().setPadding(Component.BOTTOM, 3, true);
        couldntPullMsgTextAreaTop.getSelectedStyle().setPadding(Component.BOTTOM, 3, true);
        couldntPullMsgTextAreaTop.getStyle().setAlignment(Component.CENTER, true);
        couldntPullMsgTextAreaTop.getSelectedStyle().setAlignment(Component.CENTER, true);
        couldntPullMsgTextAreaTop.getStyle().setFgColor(ColorCodes.signupScreenTopMsgColor, true);
        couldntPullMsgTextAreaTop.getSelectedStyle().setFgColor(ColorCodes.signupScreenTopMsgColor, true);
        couldntPullMsgTextAreaTop.getStyle().setBgColor(ColorCodes.signupScreenTopContainerBgColor, true);
        couldntPullMsgTextAreaTop.getSelectedStyle().setBgColor(ColorCodes.signupScreenTopContainerBgColor, true);
        couldntPullMsgTextAreaTop.getStyle().setBorder(Border.createEmpty(), true);
        couldntPullMsgTextAreaTop.getSelectedStyle().setBorder(Border.createEmpty(), true);

        TextArea couldntPullMsgTextAreaMiddle = new TextArea(MSG_ENTER_NUMBER_CANVAS2);
        couldntPullMsgTextAreaMiddle.setFocusable(false);
        couldntPullMsgTextAreaMiddle.setEditable(false);
        couldntPullMsgTextAreaMiddle.setRows(1);
        couldntPullMsgTextAreaMiddle.setSingleLineTextArea(true);
        couldntPullMsgTextAreaMiddle.getStyle().setFont(Fonts.LARGE, true);
        couldntPullMsgTextAreaMiddle.getStyle().setAlignment(Component.CENTER, true);
        couldntPullMsgTextAreaMiddle.getStyle().setPadding(Component.TOP, 0, true);
        couldntPullMsgTextAreaMiddle.getSelectedStyle().setPadding(Component.TOP, 0, true);
        couldntPullMsgTextAreaMiddle.getStyle().setPadding(Component.BOTTOM, 3, true);
        couldntPullMsgTextAreaMiddle.getSelectedStyle().setPadding(Component.BOTTOM, 3, true);
        couldntPullMsgTextAreaMiddle.getSelectedStyle().setAlignment(Component.CENTER, true);
        couldntPullMsgTextAreaMiddle.getStyle().setFgColor(ColorCodes.signupScreenTopMsgColor, true);
        couldntPullMsgTextAreaMiddle.getSelectedStyle().setFgColor(ColorCodes.signupScreenTopMsgColor, true);
        couldntPullMsgTextAreaMiddle.getStyle().setBgColor(ColorCodes.signupScreenTopContainerBgColor, true);
        couldntPullMsgTextAreaMiddle.getSelectedStyle().setBgColor(ColorCodes.signupScreenTopContainerBgColor, true);
        couldntPullMsgTextAreaMiddle.getStyle().setBorder(Border.createEmpty(), true);
        couldntPullMsgTextAreaMiddle.getSelectedStyle().setBorder(Border.createEmpty(), true);
        
        TextArea couldntPullMsgTextAreaLast = new TextArea(MSG_ENTER_NUMBER_CANVAS3);
        couldntPullMsgTextAreaLast.setFocusable(false);
        couldntPullMsgTextAreaLast.setEditable(false);
        couldntPullMsgTextAreaLast.setRows(1);
        couldntPullMsgTextAreaLast.setSingleLineTextArea(true);
        couldntPullMsgTextAreaLast.getStyle().setFont(Fonts.LARGE, true);
        couldntPullMsgTextAreaLast.getStyle().setAlignment(Component.CENTER, true);
        couldntPullMsgTextAreaLast.getStyle().setPadding(Component.TOP, 0, true);
        couldntPullMsgTextAreaLast.getSelectedStyle().setPadding(Component.TOP, 0, true);
        couldntPullMsgTextAreaLast.getSelectedStyle().setAlignment(Component.CENTER, true);
        couldntPullMsgTextAreaLast.getStyle().setFgColor(ColorCodes.signupScreenTopMsgColor, true);
        couldntPullMsgTextAreaLast.getSelectedStyle().setFgColor(ColorCodes.signupScreenTopMsgColor, true);
        couldntPullMsgTextAreaLast.getStyle().setBgColor(ColorCodes.signupScreenTopContainerBgColor, true);
        couldntPullMsgTextAreaLast.getSelectedStyle().setBgColor(ColorCodes.signupScreenTopContainerBgColor, true);
        couldntPullMsgTextAreaLast.getStyle().setBorder(Border.createEmpty(), true);
        couldntPullMsgTextAreaLast.getSelectedStyle().setBorder(Border.createEmpty(), true);
        
        TextArea addCountryCodeTextarea = new TextArea(MSG_ENTER_COUNTRY_CODE);
        addCountryCodeTextarea.setFocusable(false);
        addCountryCodeTextarea.setEditable(false);
        addCountryCodeTextarea.setRows(2);
        addCountryCodeTextarea.setSingleLineTextArea(false);
        addCountryCodeTextarea.getStyle().setFont(Fonts.SMALL, true);
        addCountryCodeTextarea.getStyle().setAlignment(Component.CENTER, true);
        addCountryCodeTextarea.getStyle().setPadding(Component.TOP, 0, true);
        addCountryCodeTextarea.getSelectedStyle().setPadding(Component.TOP, 0, true);
        addCountryCodeTextarea.getSelectedStyle().setAlignment(Component.CENTER, true);
        addCountryCodeTextarea.getStyle().setFgColor(ColorCodes.signupScreenWrongNumberTextColor, true);
        addCountryCodeTextarea.getSelectedStyle().setFgColor(ColorCodes.signupScreenWrongNumberTextColor, true);
        addCountryCodeTextarea.getStyle().setBgTransparency(255, true);
        addCountryCodeTextarea.getSelectedStyle().setBgTransparency(255, true);
        addCountryCodeTextarea.getStyle().setPadding(Component.TOP, 20, true);
        addCountryCodeTextarea.getSelectedStyle().setPadding(Component.TOP, 20, true);
        addCountryCodeTextarea.getStyle().setBgColor(ColorCodes.signupScreenBgColor, true);
        addCountryCodeTextarea.getSelectedStyle().setBgColor(ColorCodes.signupScreenBgColor, true);
        addCountryCodeTextarea.getSelectedStyle().setBgTransparency(255, true);
        addCountryCodeTextarea.getStyle().setBorder(Border.createEmpty(), true);
        addCountryCodeTextarea.getSelectedStyle().setBorder(Border.createEmpty(), true);

//        Label enterNumberMsgLbl = new Label(MSG_ENTER_NUMBER_BELOW_CANVAS);
//        enterNumberMsgLbl.getStyle().setBgTransparency(0, true);
//        enterNumberMsgLbl.getStyle().setFgColor(ColorCodes.signupScreenMiddleMsgColor, true);
//        enterNumberMsgLbl.getStyle().setAlignment(Component.CENTER, true);
//        enterNumberMsgLbl.getStyle().setPadding(Component.BOTTOM, 25, true);

        Container topContainer = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        topContainer.getStyle().setBorder(Border.createCompoundBorder(null, Border.createLineBorder(1, ColorCodes.signupScreenMiddleSeperator), null, null), true);
        topContainer.getStyle().setBgColor(ColorCodes.signupScreenTopContainerBgColor, true);
        topContainer.getStyle().setBgTransparency(255, true);
        topContainer.addComponent(couldntPullMsgTextAreaTop);
        topContainer.addComponent(couldntPullMsgTextAreaMiddle);
        topContainer.addComponent(couldntPullMsgTextAreaLast);

        numberInput = new SignUpTextArea(TextArea.PHONENUMBER);
 
        errorMsgTxtVw = new Label(EMPTY_STRING);
        errorMsgTxtVw.getStyle().setAlignment(Component.CENTER, true);
        errorMsgTxtVw.getStyle().setPadding(Component.TOP, 10, true);
        errorMsgTxtVw.getStyle().setBgTransparency(0, true);
        errorMsgTxtVw.getStyle().setFont(Fonts.SMALL, true);
        errorMsgTxtVw.getStyle().setFgColor(ColorCodes.signupScreenMiddleMsgColor, true);
        errorMsgTxtVw.setVisible(false);

        addComponent(topContainer);
        addComponent(numberInput);
        addComponent(errorMsgTxtVw);
        addComponent(addCountryCodeTextarea);
    }

    
    /**
     * This method initializes all the commands which are there on the current form.
     */
    private void initCommands() {

        nextCommand = new Command(LBL_NXT, AppResource.getImageFromResource(sentInviteImage)) {
            public void actionPerformed(ActionEvent evt) {
                super.actionPerformed(evt);
                final String number = numberInput.getText();
                         
                Log.v(TAG, "you entered number: " + number);
                if (!clicked && Validator.validatePhoneNum(number)) {
                    couldntPullMsgTextAreaTop.requestFocus();
                    clicked = true;
                    new Thread(new Runnable() {
                        public void run() {
                            errorMsgTxtVw.setVisible(false);
                            BaseRetryable addressUpdateRetryable = new BaseRetryable() {
                                public void retry() {
                                            
                                    Log.v(TAG, "Retrying...");
                                    DisplayStackManager.showProgressForm(true, null);
                                    try {
                                        String phoneNo = ClientConnectionHandler.PostAccount(number);
                                                 
                                        Log.v(TAG, phoneNo);
                                        if (phoneNo != null) {
                                            AppState.setNumber(phoneNo);
                                            DisplayStackManager.showForm(DisplayStackManager.FORM_ENTER_PIN);
                                        } else {
                                            DisplayStackManager.showForm(DisplayStackManager.FORM_ERROR);
                                        }
                                    } catch (Exception ex) {
                                                
                                        Log.v(TAG, "exception in posting number:" + ex.getClass().getName());
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
                } else {
                            
                    Log.v(TAG, "Invalid nummber !!!" );    
                    errorMsgTxtVw.setVisible(true);
                    errorMsgTxtVw.setText(MSG_INVALID_NUMBER);
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
        
        couldntPullMsgTextAreaTop.requestFocus();
        super.onShow();
        //numberInput.setText(EMPTY_STRING);
    }
    
    
    /**
     * This method clears up the number from TextArea after completing signup.
     */
    public void clearTextFieldWhenSignupCompletes() {
        if(numberInput != null) {
            numberInput.setText(EMPTY_STRING);
        }
    }
}
