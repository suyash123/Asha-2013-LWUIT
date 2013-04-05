/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.ui;

import com.bsb.hike.dto.AppState;
import com.bsb.hike.io.HttpRequestHandler;
import com.bsb.hike.util.Log;
import com.sun.lwuit.Command;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.html.HTMLComponent;
import com.sun.lwuit.layouts.BoxLayout;

/**
 *
 * @author PuneethA
 */
public class FormRewards extends FormHikeBase {

    private Command backCommand;
    private Command inviteCommand;
    private Command homeCommand;
    private HTMLComponent rewardsWebView;
    private static final String TAG = "FormRewards";
    
    
    /**
     * Constructor of Rewards Form. This form shows user about all rewards detail.
     */
    public FormRewards() {
        
        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        getStyle().setBgColor(ColorCodes.conversationBgGrey, true);
        
        initCommands();
        
        HttpRequestHandler handler = new HttpRequestHandler();
        rewardsWebView = new HTMLComponent(handler);
        
        addComponent(rewardsWebView);
    }
    
    
    /**
     * This method initializes all the commands which are there on the current form.
     */
    private void initCommands() {
        backCommand = new Command(LBL_BACK) {
            public void actionPerformed(ActionEvent evt) {
                super.actionPerformed(evt);
                if(AppState.fromScreen == DisplayStackManager.FORM_CONVERSATION) {
                    DisplayStackManager.showForm(DisplayStackManager.FORM_CONVERSATION);
                }else if(AppState.fromScreen == DisplayStackManager.FORM_PROFILE) {
                    DisplayStackManager.showForm(DisplayStackManager.FORM_PROFILE);
                }else {
                    DisplayStackManager.showForm(DisplayStackManager.FORM_CONVERSATION);
                }
            }
        };
        
        inviteCommand = new Command(LBL_INVITE_FRIENDS) {

            public void actionPerformed(ActionEvent evt) {
                super.actionPerformed(evt);
                 AppState.fromScreen = DisplayStackManager.FORM_REWARDS;
                 DisplayStackManager.showForm(DisplayStackManager.FORM_INVITE);
            }
        };
        
        homeCommand = new Command(LBL_HOME) {

            public void actionPerformed(ActionEvent evt) {
                super.actionPerformed(evt);                
                DisplayStackManager.showForm(DisplayStackManager.FORM_CONVERSATION);
            }
        };
        
        addCommand(inviteCommand);
        addCommand(homeCommand);
        addCommand(backCommand);
        setBackCommand(backCommand);
    }

    
    /**
     * This delegated method is called just after showing the form. This method is called to open rewards URL with reward_token appended.
     */
    protected void onShow() {
        super.onShow();
        
        if(AppState.getUserDetails() != null && AppState.getUserDetails().getAccountInfo() != null) {
            Log.v(TAG, RewardsURL + PLATFORM_NAME + SLASH_FORWARD + AppState.getUserDetails().getAccountInfo().getRewardToken());
            rewardsWebView.setPage(RewardsURL + PLATFORM_NAME + SLASH_FORWARD + AppState.getUserDetails().getAccountInfo().getRewardToken());
        }
    }
}
