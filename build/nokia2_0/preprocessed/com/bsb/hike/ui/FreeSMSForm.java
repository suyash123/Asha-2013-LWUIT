/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.ui;

import com.bsb.hike.dto.AppState;
import com.bsb.hike.util.Log;
import com.sun.lwuit.Button;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Label;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.plaf.Border;

/**
 *
 * @author Puneet Agarwal
 */
public class FreeSMSForm extends FormHikeBase {

    private Label freeSMSCountLabel;
    private Command backCommand;
    private static final String TAG = "FreeSMSForm"; 
    
    /**
     *  Constructor of Free SMS form. This form provides the information about SMS credits to the user. Adding and initializing all the components 
     * are done here.
     */
    public FreeSMSForm() {
        
        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        
        getStyle().setBgColor(ColorCodes.freeSMSScreenPartialBgGrey);
        getStyle().setBgTransparency(255, true);
        
        initCommands();
        
        Label freeSMSLeftLabel = new Label(LBL_FREE_SMS_LEFT);
        freeSMSLeftLabel.getStyle().setFgColor(ColorCodes.freeSMSScreenHeaderTextBlack, true);
        freeSMSLeftLabel.getStyle().setPadding(Component.TOP, 15, true);
        freeSMSLeftLabel.getStyle().setFont(Fonts.MEDIUM, true);
        freeSMSLeftLabel.getStyle().setBgTransparency(0, true);
        freeSMSLeftLabel.getStyle().setAlignment(Component.CENTER, true);
        
        freeSMSCountLabel = new Label(Integer.toString(AppState.getUserDetails().getSmsCredit()));
        freeSMSCountLabel.getStyle().setFgColor(ColorCodes.freeSMSScreenCountBlue, true);
        freeSMSCountLabel.getStyle().setPadding(Component.TOP, 10, true);
        freeSMSCountLabel.getStyle().setFont(Fonts.LARGE_BOLD, true);
        freeSMSCountLabel.getStyle().setBgTransparency(0, true);
        freeSMSCountLabel.getStyle().setAlignment(Component.CENTER, true);
        
        Container topContainer = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        topContainer.getStyle().setBorder(Border.createCompoundBorder(null, Border.createEtchedRaised(ColorCodes.freeSMSScreenUpperContainerBorder, ColorCodes.freeSMSScreenUpperContainerBorderShadow), null, null), true);
        topContainer.getSelectedStyle().setBorder(Border.createCompoundBorder(null, Border.createEtchedRaised(ColorCodes.freeSMSScreenUpperContainerBorder, ColorCodes.freeSMSScreenUpperContainerBorderShadow), null, null), true);
        topContainer.addComponent(freeSMSLeftLabel);
        topContainer.addComponent(freeSMSCountLabel);
        
        Label maxPerMonthLabel = new Label(LBL_MAX_PER_MONTH);
        maxPerMonthLabel.getStyle().setFgColor(ColorCodes.freeSMSScreenMaxPerMonthLightGrey, true);
        maxPerMonthLabel.getStyle().setPadding(Component.RIGHT, 0, true);
        maxPerMonthLabel.getStyle().setMargin(Component.RIGHT, 0, true);
        maxPerMonthLabel.getStyle().setFont(Fonts.SMALL, true);
        maxPerMonthLabel.getStyle().setBgTransparency(0, true);
        
        String credits = AppState.getUserDetails().getAccountInfo() == null? DEFAULT_USER_CREDITS : AppState.getUserDetails().getAccountInfo().getTotalCredit();
        Label maxPerMonthCountLabel = new Label(EMPTY_STRING + credits);
        maxPerMonthCountLabel.getStyle().setFgColor(ColorCodes.freeSMSScreenMaxPerMonthDarkGrey, true);
        maxPerMonthCountLabel.getStyle().setPadding(Component.LEFT, 0, true);
        maxPerMonthCountLabel.getStyle().setFont(Fonts.SMALL, true);
        maxPerMonthCountLabel.getStyle().setBgTransparency(0, true);
        
        Container maxSMSContainer = new Container(new BoxLayout(BoxLayout.X_AXIS));
        maxSMSContainer.getStyle().setPadding(Component.TOP, 20, true);
        maxSMSContainer.getStyle().setPadding(Component.BOTTOM, 10, true);
        maxSMSContainer.getStyle().setPadding(Component.LEFT, 53, true);
        maxSMSContainer.addComponent(maxPerMonthLabel);
        maxSMSContainer.addComponent(maxPerMonthCountLabel);
        topContainer.addComponent(maxSMSContainer);
        
        Label freeSMSRefillLabel = new Label(LBL_FREE_SMS_REFILL_INFO);
        freeSMSLeftLabel.getStyle().setBorder(Border.createCompoundBorder(Border.createEtchedRaised(ColorCodes.conversationSeperatorLightGrey, ColorCodes.conversationSeperatorDullWhiteDropShadow), Border.createEmpty(), Border.createEmpty(), Border.createEmpty()), true);
        freeSMSRefillLabel.getStyle().setFgColor(ColorCodes.freeSMSScreenCountInfoLightGrey, true);
        freeSMSRefillLabel.getStyle().setFont(Fonts.SMALL, true);
        freeSMSRefillLabel.getStyle().setBgTransparency(0, true);
        freeSMSRefillLabel.getStyle().setAlignment(Component.CENTER, true);
        topContainer.addComponent(freeSMSRefillLabel);
        
        Label inviteFriendsLabel = new Label(LBL_INVITE_FRIENDS_EARN_REWARDS);
        inviteFriendsLabel.getStyle().setFgColor(ColorCodes.freeSMSScreenInviteFriends, true);
        inviteFriendsLabel.getStyle().setPadding(Component.TOP, 10, true);
        inviteFriendsLabel.getStyle().setFont(Fonts.SMALL_BOLD, true);
        inviteFriendsLabel.getStyle().setBgTransparency(0, true);
        inviteFriendsLabel.getStyle().setAlignment(Component.CENTER, true);
        
        Label getFiftySMSInfoTopLabel = new Label(LBL_FIFTY_FREE_SMS_TEXT_1);
        getFiftySMSInfoTopLabel.getStyle().setFgColor(ColorCodes.freeSMSScreenBonusInfoLineGrey, true);
        getFiftySMSInfoTopLabel.getStyle().setPadding(Component.BOTTOM, 0, true);
        getFiftySMSInfoTopLabel.getStyle().setMargin(Component.BOTTOM, 0, true);
        getFiftySMSInfoTopLabel.getStyle().setFont(Fonts.SMALL, true);
        getFiftySMSInfoTopLabel.getStyle().setBgTransparency(0, true);
        getFiftySMSInfoTopLabel.getStyle().setAlignment(Component.CENTER, true);
        
        Label getFiftySMSInfoBottomLeftLabel = new Label(LBL_FIFTY_FREE_SMS_TEXT_2);
        getFiftySMSInfoBottomLeftLabel.getStyle().setFgColor(ColorCodes.freeSMSScreenBonusInfoLineGrey, true);
        getFiftySMSInfoBottomLeftLabel.getStyle().setPadding(Component.LEFT, 28, true);
        getFiftySMSInfoBottomLeftLabel.getStyle().setPadding(Component.RIGHT, 0, true);
        getFiftySMSInfoBottomLeftLabel.getStyle().setFont(Fonts.SMALL, true);
        getFiftySMSInfoBottomLeftLabel.getStyle().setBgTransparency(0, true);
        getFiftySMSInfoBottomLeftLabel.getStyle().setAlignment(Component.CENTER, true);
        
        Label getFiftySMSInfoBottomBlueLabel = new Label("50 Free SMS");
        getFiftySMSInfoBottomBlueLabel.getStyle().setFgColor(ColorCodes.freeSMSScreenBonusMiddleBlue, true);
        getFiftySMSInfoBottomBlueLabel.getStyle().setPadding(Component.LEFT, 0, true);
        getFiftySMSInfoBottomBlueLabel.getStyle().setPadding(Component.RIGHT, 0, true);
        getFiftySMSInfoBottomBlueLabel.getStyle().setFont(Fonts.SMALL, true);
        getFiftySMSInfoBottomBlueLabel.getStyle().setBgTransparency(0, true);
        getFiftySMSInfoBottomBlueLabel.getStyle().setAlignment(Component.CENTER, true);
        
        Label getFiftySMSInfoBottomRightLabel = new Label(LBL_FIFTY_FREE_SMS_TEXT_3);
        getFiftySMSInfoBottomRightLabel.getStyle().setFgColor(ColorCodes.freeSMSScreenBonusInfoLineGrey, true);
        getFiftySMSInfoBottomRightLabel.getStyle().setPadding(Component.LEFT, 0, true);
        getFiftySMSInfoBottomRightLabel.getStyle().setFont(Fonts.SMALL, true);
        getFiftySMSInfoBottomRightLabel.getStyle().setBgTransparency(0, true);
        getFiftySMSInfoBottomRightLabel.getStyle().setAlignment(Component.CENTER, true);
        
        Container fiftyFreeSmsBottomContainer = new Container(new BoxLayout(BoxLayout.X_AXIS));
        fiftyFreeSmsBottomContainer.getStyle().setPadding(Component.TOP, 0, true);
        fiftyFreeSmsBottomContainer.addComponent(getFiftySMSInfoBottomLeftLabel);
        fiftyFreeSmsBottomContainer.addComponent(getFiftySMSInfoBottomBlueLabel);
        fiftyFreeSmsBottomContainer.addComponent(getFiftySMSInfoBottomRightLabel);
        
        Button inviteButton = new Button(LBL_INVITE_SMS_FRIENDS);
        inviteButton.getStyle().setFont(Fonts.SMALL, true);
        inviteButton.getSelectedStyle().setFont(Fonts.SMALL, true);
        inviteButton.getPressedStyle().setFont(Fonts.SMALL, true);
        inviteButton.getDisabledStyle().setFont(Fonts.SMALL, true);
        inviteButton.getStyle().setFgColor(ColorCodes.white, true);
        inviteButton.getSelectedStyle().setFgColor(ColorCodes.white, true);
        inviteButton.getPressedStyle().setFgColor(ColorCodes.white, true);
        inviteButton.getDisabledStyle().setFgColor(ColorCodes.white, true);
        inviteButton.getStyle().setBgColor(ColorCodes.freeSMSScreenInviteBtnBgBlue, true);
        inviteButton.getSelectedStyle().setBgColor(ColorCodes.freeSMSScreenInviteBtnBgBlue, true);
        inviteButton.getPressedStyle().setBgColor(ColorCodes.freeSMSScreenInviteBtnBgBlue, true);
        inviteButton.getDisabledStyle().setBgColor(ColorCodes.freeSMSScreenInviteBtnBgBlue, true);
        inviteButton.getStyle().setBorder(Border.createEmpty(), true);
        inviteButton.getSelectedStyle().setBorder(Border.createEmpty(), true);
        inviteButton.getPressedStyle().setBorder(Border.createEmpty(), true);
        inviteButton.getDisabledStyle().setBorder(Border.createEmpty(), true);
        inviteButton.getStyle().setBgTransparency(255, true);
        inviteButton.getSelectedStyle().setBgTransparency(255, true);
        inviteButton.getPressedStyle().setBgTransparency(255, true);
        inviteButton.getDisabledStyle().setBgTransparency(255, true);
        inviteButton.getStyle().setAlignment(Component.CENTER, true);
        inviteButton.getSelectedStyle().setAlignment(Component.CENTER, true);
        inviteButton.getPressedStyle().setAlignment(Component.CENTER, true);
        inviteButton.getDisabledStyle().setAlignment(Component.CENTER, true);
        inviteButton.getStyle().setMargin(Component.LEFT, 50, true);
        inviteButton.getSelectedStyle().setMargin(Component.LEFT, 50, true);
        inviteButton.getPressedStyle().setMargin(Component.LEFT, 50, true);
        inviteButton.getDisabledStyle().setMargin(Component.LEFT, 50, true);
        inviteButton.getStyle().setMargin(Component.RIGHT, 50, true);
        inviteButton.getSelectedStyle().setMargin(Component.RIGHT, 50, true);
        inviteButton.getPressedStyle().setMargin(Component.RIGHT, 50, true);
        inviteButton.getDisabledStyle().setMargin(Component.RIGHT, 50, true);
        inviteButton.getStyle().setMargin(Component.TOP, 12, true);
        inviteButton.getSelectedStyle().setMargin(Component.TOP, 12, true);
        inviteButton.getPressedStyle().setMargin(Component.TOP, 12, true);
        inviteButton.getDisabledStyle().setMargin(Component.TOP, 12, true);
        inviteButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                AppState.fromScreen = DisplayStackManager.FORM_FREE_SMS;
                DisplayStackManager.showForm(DisplayStackManager.FORM_INVITE);
            }
        });
        
        Container upperHalfContainer = new Container(new BorderLayout());
        upperHalfContainer.getStyle().setBorder(Border.createCompoundBorder(null, Border.createLineBorder(1, ColorCodes.freeSMSScreenUpperHalfContainerBottomBorder) , null, null), true);
        upperHalfContainer.getSelectedStyle().setBorder(Border.createCompoundBorder(null, Border.createLineBorder(1, ColorCodes.freeSMSScreenUpperHalfContainerBottomBorder) , null, null), true);
        upperHalfContainer.getStyle().setBgColor(ColorCodes.freeSMSScreenUpperHalfDarkGrey, true);
        upperHalfContainer.getStyle().setBgTransparency(255, true);
        upperHalfContainer.addComponent(BorderLayout.CENTER, topContainer);
        upperHalfContainer.setPreferredH(160);
        
        Label hikeToSMSOnlyWorkInIndia = new Label(MSG_HIKE_TO_SMS_ONLY_IN_INDIA);
        hikeToSMSOnlyWorkInIndia.getStyle().setFgColor(ColorCodes.freeSMSScreenBonusInfoLineGrey, true);
        hikeToSMSOnlyWorkInIndia.getStyle().setPadding(Component.LEFT, 0, true);
        hikeToSMSOnlyWorkInIndia.getStyle().setPadding(Component.TOP, 15, true);
        hikeToSMSOnlyWorkInIndia.getStyle().setFont(Fonts.SMALL, true);
        hikeToSMSOnlyWorkInIndia.getStyle().setBgTransparency(0, true);
        hikeToSMSOnlyWorkInIndia.getStyle().setAlignment(Component.CENTER, true);
        
        addComponent(upperHalfContainer);
        addComponent(inviteFriendsLabel);
        addComponent(getFiftySMSInfoTopLabel);
        addComponent(fiftyFreeSmsBottomContainer);
        addComponent(inviteButton);
        addComponent(hikeToSMSOnlyWorkInIndia);
    }
    
    
    /**
     * This method initializes all the commands which are there on the current form.
     */
    private void initCommands() {
        
        backCommand = new Command(LBL_BACK) {

            public void actionPerformed(ActionEvent evt) {
                super.actionPerformed(evt);
                if(AppState.fromScreen == DisplayStackManager.FORM_CHAT) {
                    DisplayStackManager.showForm(DisplayStackManager.FORM_CHAT);
                
                }else if(AppState.fromScreen == DisplayStackManager.FORM_PROFILE){
                    DisplayStackManager.showForm(DisplayStackManager.FORM_PROFILE);
                }
            }
        };
        
        addCommand(backCommand);
        setBackCommand(backCommand);
    }
    
    /**
     * This method is called from outside the class to update the SMS count UI.
     * @param updatedSMSCount 
     */
    public void updateSmsCountInFreeSMSForm(int updatedSMSCount) {                 
        Log.v("FreeSMSForm", updatedSMSCount+ EMPTY_STRING);
        freeSMSCountLabel.setText(Integer.toString(updatedSMSCount));
    }
}
