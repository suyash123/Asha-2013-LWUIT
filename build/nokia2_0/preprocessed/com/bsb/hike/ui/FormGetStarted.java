package com.bsb.hike.ui;

import com.bsb.hike.Hike;
import com.bsb.hike.dto.AppState;
import com.bsb.hike.io.ClientConnectionHandler;
import com.bsb.hike.io.ConnectionFailedException;
import com.bsb.hike.util.AppResource;
import com.bsb.hike.util.BaseRetryable;
import com.bsb.hike.util.Log;
import com.bsb.hike.util.UIHelper;
import com.sun.lwuit.Button;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Label;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.plaf.Border;
import java.io.IOException;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

/**
 *
 * @author Ankit Yadav
 * @author Sudheer Keshav Bhat
 * @author Puneet Agarwal
 */
public class FormGetStarted extends FormHikeBase {

    private Command mNxtCmd;
    private Command mExitCmd;
    private String mMsisdn;
    private BaseRetryable mAutoMsisdnRetryable;
    private final static String TAG = "FormGetStarted";

    
    /**
     * Constructor of Get started form. This form is displayed when user launches the application for the first time. This form will open again if user
     * has not signed up or deleted his account.
     */
    public FormGetStarted() {
        
        initCommands();
        initComponents();
        initRetryables();
    }

    
    /**
     * This method initializes and add all the components on the form.
     */
    private void initComponents() {

        getStyle().setBgColor(ColorCodes.getStartedScreenBgColor, true);
        getStyle().setBgTransparency(255, true);

        Label mLogoLbl = new Label(AppResource.getImageFromResource(getStarted));
        mLogoLbl.getStyle().setBgTransparency(0, true);
        mLogoLbl.getStyle().setPadding(Component.BOTTOM, 15, true);
        mLogoLbl.getStyle().setAlignment(Component.CENTER);

        Button mTermsBtn = new Button(LBL_TERMS_N_PRIVACY);
        mTermsBtn.getStyle().setBorder(Border.createEmpty(), true);
        mTermsBtn.getSelectedStyle().setBorder(Border.createEmpty(), true);
        mTermsBtn.getPressedStyle().setBorder(Border.createEmpty(), true);
        mTermsBtn.getUnselectedStyle().setBorder(Border.createEmpty(), true);
        mTermsBtn.getStyle().setBgTransparency(0, true);
        mTermsBtn.getSelectedStyle().setBgTransparency(0, true);
        mTermsBtn.getPressedStyle().setBgTransparency(0, true);
        mTermsBtn.getUnselectedStyle().setBgTransparency(0, true);
        mTermsBtn.getStyle().setAlignment(Component.CENTER);
        mTermsBtn.getSelectedStyle().setAlignment(Component.CENTER);
        mTermsBtn.getPressedStyle().setAlignment(Component.CENTER);
        mTermsBtn.getUnselectedStyle().setAlignment(Component.CENTER);
        mTermsBtn.getStyle().setUnderline(true);
        mTermsBtn.getSelectedStyle().setUnderline(true);
        mTermsBtn.getPressedStyle().setUnderline(true);
        mTermsBtn.getUnselectedStyle().setUnderline(true);
        mTermsBtn.getStyle().setFont(Fonts.SMALL, true);
        mTermsBtn.getSelectedStyle().setFont(Fonts.SMALL, true);
        mTermsBtn.getPressedStyle().setFont(Fonts.SMALL, true);
        mTermsBtn.getUnselectedStyle().setFont(Fonts.SMALL, true);
        mTermsBtn.getStyle().setMargin(Component.BOTTOM, 70, true);
        mTermsBtn.getSelectedStyle().setMargin(Component.BOTTOM, 70, true);
        mTermsBtn.getPressedStyle().setMargin(Component.BOTTOM, 70, true);
        mTermsBtn.getUnselectedStyle().setMargin(Component.BOTTOM, 70, true);
        mTermsBtn.getStyle().setFgColor(ColorCodes.getStartedScreenTermsnPrivacyColor, true);
        mTermsBtn.getSelectedStyle().setFgColor(ColorCodes.getStartedScreenTermsnPrivacyColor, true);
        mTermsBtn.getPressedStyle().setFgColor(ColorCodes.getStartedScreenTermsnPrivacyColor, true);
        mTermsBtn.getUnselectedStyle().setFgColor(ColorCodes.getStartedScreenTermsnPrivacyColor, true);
        mTermsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {

                showTnPAlert();
            }
        });

        setLayout(new BorderLayout());

        addComponent(BorderLayout.CENTER, mLogoLbl);
        addComponent(BorderLayout.SOUTH, mTermsBtn);

        setScrollable(false);
    }

    
    /**
     * This method opens a alert LCDUI dialog when user clicks on terms and privacy link on this form.
     */
    private void showTnPAlert() {

        Alert warningAlert = UIHelper.getAlertDialog(LBL_WARNING, MSG_LAUNCH_TNA_BROWSER_WARN, LBL_OK, LBL_CANCEL, AlertType.WARNING);
        warningAlert.setCommandListener(new CommandListener() {
            public void commandAction(javax.microedition.lcdui.Command c, Displayable d) {
                if (c.getLabel().equals(LBL_OK)) {

                    DisplayStackManager.showForm(AppState.getForm());

                    try {
                        Hike.sMidlet.platformRequest(TERMS_URL);
                    } catch (ConnectionNotFoundException ex) {
                        Log.v(TAG, "network error while sending platform request.");
                    }
                }
                if (c.getLabel().equals(LBL_CANCEL)) {

                    DisplayStackManager.showForm(AppState.getForm());
                }
            }
        });

        Display.getDisplay(Hike.sMidlet).setCurrent(warningAlert, Display.getDisplay(Hike.sMidlet).getCurrent());
    }

    
    /**
     * This method initializes all the commands which are there on the current form.
     */
    private void initCommands() {

        mNxtCmd = new Command(LBL_NXT, AppResource.getImageFromResource(sentInviteImage)) {
            public void actionPerformed(ActionEvent evt) {
                mAutoMsisdnRetryable.retry();
            }
        };

        mExitCmd = new Command(LBL_EXIT) {
            public void actionPerformed(ActionEvent evt) {
                super.actionPerformed(evt);
                showExitDialog();
            }
        };

        addCommand(mNxtCmd);
        addCommand(mExitCmd);
        setDefaultCommand(mNxtCmd);
        setBackCommand(mExitCmd);
    }

    
    /**
     * This method initializes retry interface for auto MSISDN auth.
     */
    private void initRetryables() {

        mAutoMsisdnRetryable = new BaseRetryable() {
            public void retry() {
                new Thread(new Runnable() {
                    public void run() {                                
                        Log.v(TAG, "Retrying...");
                        DisplayStackManager.showProgressForm(true, null);
                        try {
                            mMsisdn = ClientConnectionHandler.PostAccount();
                            if (mMsisdn == null) {
                                DisplayStackManager.showForm(DisplayStackManager.FORM_ENTER_NUMBER);
                            } else {
                                AppState.setNumber(mMsisdn);
                                DisplayStackManager.showForm(DisplayStackManager.FORM_SET_NAME);
                            }
                        } catch (Exception ex) {
                                     
                            Log.v(TAG, "exception in auto msisdn:" + ex.getClass().getName());
                            if (ex instanceof IOException || ex instanceof ConnectionFailedException) {
                                errorForm.setErrorMessage(NO_NETWORK_TITLE);
                            }
                            errorForm.show();
                        }
                    }
                }).start();
            }
        };
    }
    
    
    /**
     * This method is called when user presses back key to exit the app. This will open up a confirmation dialog.
     */
    private void showExitDialog() {

        Alert deleteAlert = UIHelper.getAlertDialog(APP_TITLE, MSG_CLOSE_APPLICATION, LBL_YES, LBL_NO, AlertType.CONFIRMATION);
        deleteAlert.setCommandListener(new CommandListener() {
            public void commandAction(javax.microedition.lcdui.Command c, Displayable d) {
                if (c.getLabel().equals(LBL_YES)) {
                    Hike.sMidlet.destroyApp(true);
                }
                if (c.getLabel().equals(LBL_NO)) {
                    DisplayStackManager.showForm(AppState.getForm());
                }
            }
        });

        javax.microedition.lcdui.Display.getDisplay(Hike.sMidlet).setCurrent(deleteAlert, javax.microedition.lcdui.Display.getDisplay(Hike.sMidlet).getCurrent());
    }

    
    /**
     * This delegated method is called just after showing the form.
     */
    protected void onShow() {
        super.onShow();
        
    }
}
