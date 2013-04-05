package com.bsb.hike.ui;

import com.bsb.hike.Hike;
import com.bsb.hike.dto.AppState;
import com.bsb.hike.io.ClientConnectionHandler;
import com.bsb.hike.io.ConnectionFailedException;
import com.bsb.hike.util.AppConstants;
import com.bsb.hike.util.AppResource;
import com.bsb.hike.util.BaseRetryable;
import com.bsb.hike.util.Log;
import com.bsb.hike.util.TextUtils;
import com.bsb.hike.util.UIHelper;
import com.sun.lwuit.Button;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Graphics;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.plaf.Border;
import java.io.IOException;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;

public class FormEditProfile extends FormHikeBase {

    private static final String TAG = "FormEditProfile";
    private Command exitCommand, saveCommand;
    private TextArea userName, userPhnNumber, userEmail;
    private Button maleRadioButton;
    private Button femaleRadioButton;
    private boolean clicked = false;
    private Image selectedImage, unselectedImage;
    private static final int MAX_STRING_WIDTH = 70;
    private String mName ,mEmail ;
    private String mGender;

    
    /**
     * Constructor of Edit profile screen. This screen allows user to edit his name, gender and email. Initializing and adding all components are done 
     * in the constructor.
     */
    public FormEditProfile() {

        initCommands();

        getStyle().setBgColor(ColorCodes.editProfileScreenBg, true);

        //#if nokia2_0
//#         setTitle(LBL_EDIT_PROFILE);
        //#endif

        selectedImage = AppResource.getImageFromResource(AppConstants.PATH_GENDER_RADIOBUTTON_SELECTED);
        unselectedImage = AppResource.getImageFromResource(AppConstants.PATH_GENDER_RADIOBUTTON_UNSELECTED);

        Label nameText = new Label(LBL_NAME + LBL_SEMICOLON);
        nameText.setPreferredW(MAX_STRING_WIDTH);
        nameText.getStyle().setBgTransparency(0, true);
        nameText.getStyle().setFgColor(ColorCodes.editProfileScreenLabelColor);
        nameText.getSelectedStyle().setFgColor(ColorCodes.editProfileScreenLabelColor);
        userName = new TextArea(1, 20);
        userName.getStyle().setFgColor(ColorCodes.editProfileScreenDetailsLabelColor, true);
        userName.getSelectedStyle().setFgColor(ColorCodes.editProfileScreenDetailsLabelColor, true);
        //        TODO restrict the text limit
//        userName.setMaxSize(CharLimit.NAME);
        userName.setEnabled(true);
        userName.setText(AppState.getUserDetails().getName());
        userName.setGrowByContent(true);
        userName.getStyle().setBorder(Border.createEmpty(), true);
        userName.getSelectedStyle().setBorder(Border.createEmpty(), true);
        userName.getStyle().setBgColor(ColorCodes.editProfileScreenEachContainerBg, true);
        userName.getSelectedStyle().setBgColor(ColorCodes.editProfileScreenEachContainerBg, true);
        userName.setConstraint(TextArea.NON_PREDICTIVE);
        Container nameContainer = new Container(new BoxLayout(BoxLayout.X_AXIS));
        nameContainer.getStyle().setBorder(Border.createCompoundBorder(null, Border.createEtchedRaised(ColorCodes.editProfileScreenSeperator, ColorCodes.editProfileScreenSeperatorShadow), null, null), true);
        nameContainer.getStyle().setPadding(Component.TOP, 3, true);
        nameContainer.getStyle().setPadding(Component.BOTTOM, 3, true);
        nameContainer.addComponent(nameText);
        nameContainer.addComponent(userName);


        Label phoneText = new Label(LBL_PHONE + LBL_SEMICOLON);
        phoneText.setPreferredW(MAX_STRING_WIDTH);
        phoneText.getStyle().setBgTransparency(0, true);
        phoneText.getStyle().setFgColor(ColorCodes.editProfileScreenLabelColor);
        phoneText.getSelectedStyle().setFgColor(ColorCodes.editProfileScreenLabelColor);
        userPhnNumber = new TextArea(1, 2, TextArea.PHONENUMBER);
        userPhnNumber.getStyle().setFgColor(ColorCodes.editProfileScreenLabelColor, true);
        userPhnNumber.getSelectedStyle().setFgColor(ColorCodes.editProfileScreenLabelColor, true);
        userPhnNumber.setEditable(false);
        userPhnNumber.getStyle().setBorder(Border.createEmpty(), true);
        userPhnNumber.getSelectedStyle().setBorder(Border.createEmpty(), true);
        userPhnNumber.getStyle().setBgColor(ColorCodes.editProfileScreenEachContainerBg, true);
        userPhnNumber.getSelectedStyle().setBgColor(ColorCodes.editProfileScreenEachContainerBg, true);
        userPhnNumber.setFocusable(true);
        userPhnNumber.setWidth(getWidth());
        Container phoneNumContainer = new Container(new BoxLayout(BoxLayout.X_AXIS));
        phoneNumContainer.getStyle().setBorder(Border.createCompoundBorder(null, Border.createEtchedRaised(ColorCodes.editProfileScreenSeperator, ColorCodes.editProfileScreenSeperatorShadow), null, null), true);
        phoneNumContainer.getStyle().setPadding(Component.TOP, 3, true);
        phoneNumContainer.getStyle().setPadding(Component.BOTTOM, 3, true);
        phoneNumContainer.addComponent(phoneText);
        phoneNumContainer.addComponent(userPhnNumber);


        Label emailText = new Label(LBL_EMAIL + LBL_SEMICOLON);
        emailText.setPreferredW(MAX_STRING_WIDTH);
        emailText.getStyle().setBgTransparency(0, true);
        emailText.getStyle().setFgColor(ColorCodes.editProfileScreenLabelColor);
        emailText.getSelectedStyle().setFgColor(ColorCodes.editProfileScreenLabelColor);
        userEmail = new TextArea(1, 20, TextArea.EMAILADDR);
        //        TODO restrict the text limit
//        userEmail.setMaxSize(CharLimit.EMAIL);
        userEmail.getStyle().setFgColor(ColorCodes.editProfileScreenDetailsLabelColor, true);
        userEmail.getSelectedStyle().setFgColor(ColorCodes.editProfileScreenDetailsLabelColor, true);
        userEmail.getStyle().setBorder(Border.createEmpty(), true);
        userEmail.getSelectedStyle().setBorder(Border.createEmpty(), true);
        userEmail.getStyle().setBgColor(ColorCodes.editProfileScreenEachContainerBg, true);
        userEmail.getSelectedStyle().setBgColor(ColorCodes.editProfileScreenEachContainerBg, true);
        userEmail.setConstraint(TextArea.NON_PREDICTIVE | TextArea.EMAILADDR);
        Container emailContainer = new Container(new BoxLayout(BoxLayout.X_AXIS));
        emailContainer.getStyle().setBorder(Border.createCompoundBorder(null, Border.createEtchedRaised(ColorCodes.editProfileScreenSeperator, ColorCodes.editProfileScreenSeperatorShadow), null, null), true);
        emailContainer.getStyle().setPadding(Component.TOP, 3, true);
        emailContainer.getStyle().setPadding(Component.BOTTOM, 3, true);
        emailContainer.addComponent(emailText);
        emailContainer.addComponent(userEmail);


        Label genderText = new Label(LBL_GENDER + LBL_SEMICOLON);
        genderText.setPreferredW(MAX_STRING_WIDTH);
        genderText.getStyle().setBgTransparency(0, true);
        genderText.getStyle().setFgColor(ColorCodes.editProfileScreenLabelColor);
        genderText.getSelectedStyle().setFgColor(ColorCodes.editProfileScreenLabelColor);
        Label maleGenderText = new Label(LBL_MALE);
        maleGenderText.getStyle().setFgColor(ColorCodes.editProfileScreenDetailsLabelColor, true);
        maleGenderText.getStyle().setBgTransparency(0, true);
        maleGenderText.getStyle().setPadding(RIGHT, 0, true);
        Label femaleGenderText = new Label(LBL_FEMALE);
        femaleGenderText.getStyle().setFgColor(ColorCodes.editProfileScreenDetailsLabelColor, true);
        femaleGenderText.getStyle().setBgTransparency(0, true);
        femaleGenderText.getStyle().setPadding(RIGHT, 0, true);
        maleRadioButton = new Button();
        maleRadioButton.getStyle().setBgTransparency(255, true);
        maleRadioButton.getSelectedStyle().setBgTransparency(255, true);
        maleRadioButton.getPressedStyle().setBgTransparency(255, true);
        maleRadioButton.getStyle().setBorder(Border.createEmpty(), true);
        maleRadioButton.getSelectedStyle().setBorder(Border.createEmpty(), true);
        maleRadioButton.getPressedStyle().setBorder(Border.createEmpty(), true);
        maleRadioButton.getStyle().setBgColor(ColorCodes.editProfileScreenEachContainerBg, true);
        maleRadioButton.getSelectedStyle().setBgColor(ColorCodes.editProfileScreenEachContainerBg, true);
        maleRadioButton.getPressedStyle().setBgColor(ColorCodes.editProfileScreenEachContainerBg, true);
        maleRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                maleRadioButton.setIcon(selectedImage);
                femaleRadioButton.setIcon(unselectedImage);
            }
        });

        femaleRadioButton = new Button();
        femaleRadioButton.getStyle().setBgTransparency(255, true);
        femaleRadioButton.getSelectedStyle().setBgTransparency(255, true);
        femaleRadioButton.getPressedStyle().setBgTransparency(255, true);
        femaleRadioButton.getStyle().setBorder(Border.createEmpty(), true);
        femaleRadioButton.getSelectedStyle().setBorder(Border.createEmpty(), true);
        femaleRadioButton.getPressedStyle().setBorder(Border.createEmpty(), true);
        femaleRadioButton.getStyle().setBgColor(ColorCodes.editProfileScreenEachContainerBg, true);
        femaleRadioButton.getSelectedStyle().setBgColor(ColorCodes.editProfileScreenEachContainerBg, true);
        femaleRadioButton.getPressedStyle().setBgColor(ColorCodes.editProfileScreenEachContainerBg, true);
        femaleRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                femaleRadioButton.setIcon(selectedImage);
                maleRadioButton.setIcon(unselectedImage);
            }
        });
        Container genderContainer = new Container(new BoxLayout(BoxLayout.X_AXIS));
        genderContainer.getStyle().setPadding(Component.TOP, 7, true);
        genderContainer.getStyle().setPadding(Component.BOTTOM, 7, true);
        genderContainer.addComponent(genderText);
        genderContainer.addComponent(maleGenderText);
        genderContainer.addComponent(maleRadioButton);
        genderContainer.addComponent(femaleGenderText);
        genderContainer.addComponent(femaleRadioButton);


        Label arrowText = new Label();
        arrowText.getStyle().setFgColor(0x808080);
        arrowText.getSelectedStyle().setFgColor(0x808080);
        arrowText.getStyle().setMargin(Component.RIGHT, 10, true);
        arrowText.getSelectedStyle().setMargin(Component.RIGHT, 10, true);
        arrowText.setText(LBL_ARROW_TEXT);
        Button editPictureButton = new Button(LBL_EDIT_PICTURE);
        editPictureButton.getStyle().setFgColor(0x808080);
        editPictureButton.getSelectedStyle().setFgColor(0x808080);
        editPictureButton.getPressedStyle().setFgColor(0x808080);
        editPictureButton.getStyle().setAlignment(Component.LEFT);
        editPictureButton.getSelectedStyle().setAlignment(Component.LEFT);
        editPictureButton.getPressedStyle().setAlignment(Component.LEFT);
        editPictureButton.getStyle().setBorder(Border.createEmpty(), true);
        editPictureButton.getSelectedStyle().setBorder(Border.createEmpty(), true);
        editPictureButton.getPressedStyle().setBorder(Border.createEmpty(), true);
        editPictureButton.getStyle().setBgColor(0xF0F0EE, true);
        editPictureButton.getSelectedStyle().setBgColor(0xF0F0EE, true);
        editPictureButton.getPressedStyle().setBgColor(0xF0F0EE, true);
        editPictureButton.getStyle().setPadding(5, 5, 3, 0);
        editPictureButton.getSelectedStyle().setPadding(5, 5, 3, 0);
        editPictureButton.getPressedStyle().setPadding(5, 5, 3, 0);
        editPictureButton.getStyle().setMargin(5, 5, 3, 0);
        editPictureButton.getSelectedStyle().setMargin(5, 5, 3, 0);
        editPictureButton.getPressedStyle().setMargin(5, 5, 3, 0);
        editPictureButton.setFocusable(false);
        Container editPictureContainer = new Container(new BorderLayout()) {
            public void paintBackground(Graphics g) {
                g.setColor(0xe2e1e0);
                g.drawLine(getX(), getY(), getWidth(), getY());
            }
        };
        editPictureContainer.getStyle().setPadding(Component.TOP, 3, true);
        editPictureContainer.getStyle().setPadding(Component.BOTTOM, 3, true);
        editPictureContainer.addComponent(BorderLayout.WEST, editPictureButton);
        editPictureContainer.addComponent(BorderLayout.EAST, arrowText);

        setLayout(new BoxLayout(BoxLayout.Y_AXIS));

        Container editProfileContainer = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        editProfileContainer.getStyle().setBorder(Border.createCompoundBorder(null, Border.createEtchedRaised(ColorCodes.editProfileScreenBottomBorder, ColorCodes.editProfileScreenBottomBorderShadow), null, null), true);
        editProfileContainer.addComponent(nameContainer);
        editProfileContainer.addComponent(phoneNumContainer);
        editProfileContainer.addComponent(emailContainer);
        editProfileContainer.addComponent(genderContainer);
        //editProfileContainer.addComponent(editPictureContainer);
        editProfileContainer.getStyle().setMargin(TOP, 5, true);
        editProfileContainer.getStyle().setMargin(BOTTOM, 5, true);
        editProfileContainer.getStyle().setBgColor(ColorCodes.editProfileScreenEachContainerBg, true);
        editProfileContainer.getSelectedStyle().setBgColor(ColorCodes.editProfileScreenEachContainerBg, true);
        editProfileContainer.getStyle().setBgTransparency(255, true);

        //#if nokia1_1
//#         Label editProfileHeader = new Label(LBL_EDIT_PROFILE);
//#         editProfileHeader.getStyle().setPadding(Component.TOP, 10, true);
//#         editProfileHeader.getStyle().setBgTransparency(0, true);
//#         editProfileHeader.getStyle().setFgColor(0xE0E0E0, true);
//#         addComponent(editProfileHeader);
        //#endif

        addComponent(editProfileContainer);
    }

    
    /**
     * This delegated method is called just after showing the form. This method sets name, number, email and gender when user updates the profile.
     */
    protected void onShow() {
        super.onShow();

        userName.setText(AppState.getUserDetails().getName());
        userPhnNumber.setText(AppState.getUserDetails().getMsisdn());
        if (!TextUtils.isEmpty(AppState.getUserDetails().getEmail())) {
            userEmail.setText(AppState.getUserDetails().getEmail());
        }else {
            userEmail.setText(EMPTY_STRING);
        }

        if(AppState.getUserDetails().getGender().equals(GENDER_MALE)) {
            maleRadioButton.setIcon(selectedImage);
            femaleRadioButton.setIcon(unselectedImage);
        }else if(AppState.getUserDetails().getGender().equals(GENDER_FEMALE)) {
            femaleRadioButton.setIcon(selectedImage);
            maleRadioButton.setIcon(unselectedImage);
        }else if(AppState.getUserDetails().getGender().equals(GENDER_NOT_AVAILABLE)) {
            femaleRadioButton.setIcon(unselectedImage);
            maleRadioButton.setIcon(unselectedImage);
        }
        saveCurrentProfile();
    }

    
    /**
     * This method initializes all the commands which are there on the current form.
     */
    private void initCommands() {

        exitCommand = new Command(LBL_BACK) {
            public void actionPerformed(ActionEvent evt) {
                super.actionPerformed(evt);
                DisplayStackManager.showForm(DisplayStackManager.FORM_PROFILE);
            }
        };

        saveCommand = new Command(LBL_SAVE, AppResource.getImageFromResource(sentInviteImage)) {
            public void actionPerformed(ActionEvent evt) {
                super.actionPerformed(evt);
                if (!clicked) {
                    clicked = true;
                    new Thread(new Runnable() {
                        public void run() {

                            BaseRetryable addressUpdateRetryable = new BaseRetryable() {
                                public void retry() {
                                             
                                    Log.v(TAG, "Retrying...");
                                    try {
                                        boolean success = true;
                                        if (!userName.getText().trim().equals(EMPTY_STRING)) {
                                                     
                                            Log.v(TAG, "email empty: " + userEmail.getText().trim().equals(EMPTY_STRING) + "email validate: " + validateEmailID(userEmail.getText()));
                                            if (userEmail.getText().trim().equals(EMPTY_STRING) || validateEmailID(userEmail.getText())) {
                                                DisplayStackManager.showProgressForm(true, MSG_UPDATING_PROFILE_INFO);

                                                if (!(userName.getText().trim().equals(AppState.getUserDetails().getName()))) {
                                                    success = ClientConnectionHandler.PostName(userName.getText().trim());
                                                           
                                                    Log.v(TAG, "Post name response = " + success);
                                                }
                                                boolean gender = true;
                                                if (maleRadioButton.getIcon() == selectedImage) {
                                                    gender = true;
                                                } else {
                                                    gender = false;
                                                }
                                                success = success && ClientConnectionHandler.PostAccountInfo(userEmail.getText().trim(), gender);

                                                        
                                                Log.v(TAG, "Post Gender and Email response = " + success);
                                                if (success) {
                                                    DisplayStackManager.showForm(DisplayStackManager.FORM_PROFILE);
                                                } else {
                                                    DisplayStackManager.showForm(DisplayStackManager.FORM_ERROR);
                                                }
                                            } else {
                                                showErrorDialog();
                                            }
                                        } else {
                                            //TODO display name error
                                            DisplayStackManager.showForm(DisplayStackManager.FORM_PROFILE);
                                        }
                                    } catch (Exception ex) {
                                                
                                        Log.v(TAG, "exception in posting account info:" + ex.getClass().getName());
                                        if (ex instanceof IOException || ex instanceof ConnectionFailedException) {
                                            errorForm.setErrorMessage(NO_NETWORK_TITLE);
                                        }
                                        errorForm.show();
                                    }
                                }
                            };
                            
                            Log.v(TAG, "Profile changed : " + isProfileChanged());
                            if (isProfileChanged()) {
                                addressUpdateRetryable.retry();
                            } else {
                                DisplayStackManager.showForm(DisplayStackManager.FORM_PROFILE);
                            }
                            clicked = false;
                        }
                    }).start();
                }
            }
        };

        addCommand(exitCommand);
        addCommand(saveCommand);
        setDefaultCommand(saveCommand);
        setBackCommand(exitCommand);
    }

    
    /**
     * This method validates the email id which user has entered.
     * @param email
     * @return 
     */
    public static boolean validateEmailID(String email) {
        email = email.trim();
        String reverse = new StringBuffer(email).reverse().toString();
        if (email == null || email.length() == 0 || email.indexOf("@") == -1) {
            return false;
        }
        int emailLength = email.length();
        int atPosition = email.indexOf("@");
        int atDot = reverse.indexOf(".");

        String beforeAt = email.substring(0, atPosition);
        String afterAt = email.substring(atPosition + 1, emailLength);

        if (beforeAt.length() == 0 || afterAt.length() == 0) {
            return false;
        }
        for (int i = 0; email.length() - 1 > i; i++) {
            char i1 = email.charAt(i);
            char i2 = email.charAt(i + 1);
            if (i1 == '.' && i2 == '.') {
                return false;
            }
        }
        if (email.charAt(atPosition - 1) == '.' || email.charAt(0) == '.' || email.charAt(atPosition + 1) == '.' || afterAt.indexOf("@") != -1 || atDot < 2) {
            return false;
        }

        return true;
    }

    
    /**
     * This method displays the error dialog when user entered wrong email id.
     */
    private void showErrorDialog() {

        Alert editAlert = UIHelper.getSingleCommandAlert(LBL_ERROR, MSG_EMAIL_NOT_VALID_ERROR, LBL_OK, AlertType.ERROR);
        editAlert.setTimeout(1000);
        if (!editAlert.isShown()) {
            javax.microedition.lcdui.Display.getDisplay(Hike.sMidlet).setCurrent(editAlert, javax.microedition.lcdui.Display.getDisplay(Hike.sMidlet).getCurrent());
        }
    }
    
    
    /**
     * This method saves the current state of the profile when user opens this form.
     */
    private void saveCurrentProfile() {
      
        mName   = userName.getText();
        mEmail  = userEmail.getText();
        mGender = getGenderStatus();
    }
    
    
    /**
     * This method returns boolean value to show whether user has changed profile or not. If user change something, then it will return true else 
     * false.
     * @return 
     */
    private boolean isProfileChanged() {
        Log.v(TAG, "Current gender selected is " + mGender);
        Log.v(TAG, "Changed gender selected is " + getGenderStatus());
        if (!mName.equals(userName.getText()) || !mEmail.equals(userEmail.getText()) || !(mGender.equals(getGenderStatus()))) {
            return true;
        }
        return false;
       
    }

    
    /**
     * This method returns the gender status of the user while saving the profile.
     * @return 
     */
    private String getGenderStatus() {
        if (maleRadioButton.getIcon() == selectedImage) {
            return GENDER_MALE;
        }else if(femaleRadioButton.getIcon() == selectedImage) {
            return GENDER_FEMALE;
        }
       
        return GENDER_NOT_AVAILABLE;
    }

}
