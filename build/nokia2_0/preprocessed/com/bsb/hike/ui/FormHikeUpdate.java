/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.ui;

import com.bsb.hike.Hike;
import com.bsb.hike.dto.AppState;
import com.bsb.hike.util.Log;
import com.sun.lwuit.Button;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.plaf.Border;
import javax.microedition.io.ConnectionNotFoundException;

/**
 *
 * @author Puneet Agarwal
 */
public class FormHikeUpdate extends FormHikeBase {

    private static final String TAG = "FormHikeUpdate";
    private Label criticalUpdateLbl;

    /**
     * Constructor of Hike update form. This form gets open when there is update available on the server and app is eligible to check update
     * at some particular conditions.
     */
    public FormHikeUpdate() {

        setTitle(LBL_UPDATE_HIKE_FORM_TITLE);
        
        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        getStyle().setBgColor(ColorCodes.commonBgColor, true);
        getStyle().setBgTransparency(255, true);
        
        criticalUpdateLbl = new Label(LBL_CRITICAL_UPDATE);
        criticalUpdateLbl.getStyle().setAlignment(Component.CENTER, true);
        criticalUpdateLbl.getSelectedStyle().setAlignment(Component.CENTER, true);
        criticalUpdateLbl.getStyle().setFont(Fonts.MEDIUM_BOLD, true);
        criticalUpdateLbl.getSelectedStyle().setFont(Fonts.MEDIUM_BOLD, true);
        criticalUpdateLbl.getStyle().setBgColor(ColorCodes.commonBgColor, true);
        criticalUpdateLbl.getSelectedStyle().setBgColor(ColorCodes.commonBgColor, true);
        criticalUpdateLbl.getStyle().setBgTransparency(255, true);
        criticalUpdateLbl.getSelectedStyle().setBgTransparency(255, true);
        criticalUpdateLbl.getStyle().setFgColor(ColorCodes.signupScreenTopMsgColor, true);
        criticalUpdateLbl.getSelectedStyle().setFgColor(ColorCodes.signupScreenTopMsgColor, true);
        criticalUpdateLbl.getStyle().setBorder(Border.createEmpty(), true);
        criticalUpdateLbl.getSelectedStyle().setBorder(Border.createEmpty(), true);
        criticalUpdateLbl.getStyle().setMargin(Component.TOP, 65, true);
        criticalUpdateLbl.getSelectedStyle().setMargin(Component.TOP, 65, true);
                
        TextArea txtUpdateText = new TextArea(LBL_UPDATE_AVAILABLE);
        txtUpdateText.setEditable(false);
        txtUpdateText.setRows(2);
        txtUpdateText.getStyle().setAlignment(Component.CENTER, true);
        txtUpdateText.getSelectedStyle().setAlignment(Component.CENTER, true);
        txtUpdateText.getStyle().setFont(Fonts.MEDIUM, true);
        txtUpdateText.getSelectedStyle().setFont(Fonts.MEDIUM, true);
        txtUpdateText.getStyle().setBgColor(ColorCodes.commonBgColor, true);
        txtUpdateText.getSelectedStyle().setBgColor(ColorCodes.commonBgColor, true);
        txtUpdateText.getStyle().setBgTransparency(255, true);
        txtUpdateText.getSelectedStyle().setBgTransparency(255, true);
        txtUpdateText.setSingleLineTextArea(true);
        txtUpdateText.getStyle().setFgColor(ColorCodes.signupScreenTopMsgColor, true);
        txtUpdateText.getSelectedStyle().setFgColor(ColorCodes.signupScreenTopMsgColor, true);
        txtUpdateText.getStyle().setBorder(Border.createEmpty(), true);
        txtUpdateText.getSelectedStyle().setBorder(Border.createEmpty(), true);
        
        Label currentVersionLbl = new Label();
        currentVersionLbl.setText(LBL_CURRENT_VERSION + Hike.sMidlet.getAppProperty(LBL_MIDLET_VERSION));
        currentVersionLbl.getStyle().setMargin(Component.TOP, 10, true);
        currentVersionLbl.getStyle().setPadding(Component.BOTTOM, 0, true);
        currentVersionLbl.getStyle().setFont(Fonts.SMALL, true);
        currentVersionLbl.getStyle().setBgTransparency(0, true);
        currentVersionLbl.getStyle().setFgColor(0xFFFFFF, true);
        currentVersionLbl.getStyle().setAlignment(Component.CENTER, true);
        
        Label updateVersionLbl = new Label();
        updateVersionLbl.setText(LBL_UPDATE_VERSION + AppState.jad.getJadLatestVersion());
        updateVersionLbl.getStyle().setPadding(Component.TOP, 0, true);
        updateVersionLbl.getStyle().setFont(Fonts.SMALL, true);
        updateVersionLbl.getStyle().setBgTransparency(0, true);
        updateVersionLbl.getStyle().setFgColor(0xFFFFFF, true);
        updateVersionLbl.getStyle().setAlignment(Component.CENTER, true);

        Container updateContainer = new Container(new BoxLayout(BoxLayout.X_AXIS));
        updateContainer.getStyle().setMargin(Component.TOP, 10, true);
        
        Button cancelBtn = getUpdateActionBtns(LBL_CANCEL);
        cancelBtn.getStyle().setMargin(Component.LEFT, 50, true);
        cancelBtn.getSelectedStyle().setMargin(Component.LEFT, 50, true);
        cancelBtn.getPressedStyle().setMargin(Component.LEFT, 50, true);
        
        Button updateBtn = getUpdateActionBtns(LBL_UPDATE_HIKE_FORM_TITLE);
        
        updateContainer.addComponent(cancelBtn);
        updateContainer.addComponent(updateBtn);
        
        Log.v(TAG, "App Update JAD URL " + AppState.jad.getJadURL());
        
        updateBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                
                Log.v(TAG, "Update clicked");
                try {
                    Hike.sMidlet.platformRequest(AppState.jad.getJadURL());                   
                    Hike.sMidlet.destroyApp(true);
                } catch (ConnectionNotFoundException ex) {
                    Log.v(TAG, "network error while checking for update.");
                }
            }
        });

        cancelBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                
                if (AppState.jad.isCritical()) {
                    Hike.sMidlet.destroyApp(true);
                } else {
                    
                    Log.v(TAG, "Update cancelled");
                    DisplayStackManager.showForm(AppState.getForm());
                }
            }
        });
        
        addComponent(criticalUpdateLbl);
        addComponent(txtUpdateText);
        addComponent(updateContainer);
        addComponent(currentVersionLbl);
        addComponent(updateVersionLbl);
    }
    
    
    /**
     * This method returns update action or cancel action buttons with styles and margin.
     * @param btnLabel
     * @return 
     */
    private Button getUpdateActionBtns(String btnLabel) {
        
        Button updateActionBtn = new Button(btnLabel);
        updateActionBtn.getStyle().setFont(Fonts.SMALL, true);
        updateActionBtn.getStyle().setFgColor(ColorCodes.white, true);
        updateActionBtn.getSelectedStyle().setFgColor(ColorCodes.white, true);
        updateActionBtn.getPressedStyle().setFgColor(ColorCodes.white, true);
        updateActionBtn.getSelectedStyle().setFont(Fonts.SMALL, true);
        updateActionBtn.getPressedStyle().setFont(Fonts.SMALL, true);
        updateActionBtn.getStyle().setBgColor(ColorCodes.freeSMSScreenInviteBtnBgBlue, true);
        updateActionBtn.getSelectedStyle().setBgColor(ColorCodes.freeSMSScreenInviteBtnBgBlue, true);
        updateActionBtn.getPressedStyle().setBgColor(ColorCodes.freeSMSScreenInviteBtnBgBlue, true);
        updateActionBtn.getStyle().setBorder(Border.createEmpty(), true);
        updateActionBtn.getSelectedStyle().setBorder(Border.createEmpty(), true);
        updateActionBtn.getPressedStyle().setBorder(Border.createEmpty(), true);
        updateActionBtn.getStyle().setBgTransparency(255, true);
        updateActionBtn.getSelectedStyle().setBgTransparency(255, true);
        updateActionBtn.getPressedStyle().setBgTransparency(255, true);
        updateActionBtn.getStyle().setAlignment(Component.CENTER, true);
        updateActionBtn.getSelectedStyle().setAlignment(Component.CENTER, true);
        updateActionBtn.getPressedStyle().setAlignment(Component.CENTER, true);
        updateActionBtn.getStyle().setMargin(Component.RIGHT, 10, true);
        updateActionBtn.getSelectedStyle().setMargin(Component.RIGHT, 10, true);
        updateActionBtn.getPressedStyle().setMargin(Component.RIGHT, 10, true);
        
        return updateActionBtn;
    }

    
    /**
     * This delegated method is called just after showing the form.
     */
    protected void onShow() {
        
        if(!AppState.jad.isCritical()) {
            criticalUpdateLbl.setText(EMPTY_STRING);
        }else {
            criticalUpdateLbl.setText(LBL_CRITICAL_UPDATE);
        }
    }
}
