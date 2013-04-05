/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.dto;

import com.bsb.hike.ui.DisplayStackManager;
import com.bsb.hike.ui.FormConversation;
import com.bsb.hike.ui.FormRewards;
import com.bsb.hike.ui.FormUserProfile;
import com.bsb.hike.util.AppConstants;
import com.bsb.hike.util.Log;
import com.bsb.hike.util.TextUtils;
import com.sun.lwuit.Display;

/**
 *DTO class for Additional info of registered user
 * @author Ankit Yadav
 */
public class AccountInfo implements DataModel {

    private String inviteToken;
    private String totalCredit;
    private String rewardToken;
    private boolean showReward;

    public AccountInfo(String inviteToken, String totalCredit) {
        this.totalCredit = totalCredit;
        this.inviteToken = inviteToken;
    }

    /**
     * returns invite token
     * @return 
     */
    public String getInviteToken() {
        return inviteToken;
    }

    /**
     * getter for totalcredit permonth
     * @return 
     */
    public String getTotalCredit() {
        return totalCredit;
    }

    /**
     * setter for total credit
     * @param totalCredit 
     */
    public void setTotalCredit(String totalCredit) {
        this.totalCredit = totalCredit;
    }

    /**
     * getter for custom invite message
     * @return 
     */
    public String getInviteMessage() {
        return MSG_INVITE_PREFIX + DownloadURL + MSG_INVITE_SUFFIX;
    }

    /**
     * getter for reward token
     * @return 
     */
    public String getRewardToken() {
        return rewardToken;
    }

    /**
     * setter for reward details, also reflects the changes in UI
     * @param rewardToken
     * @param showReward 
     */
    public void setRewardDetails(String rewardToken, boolean showReward) {
        if (rewardToken != null) {
            this.rewardToken = rewardToken;
        }
        if (this.showReward != showReward) {
            FormConversation formconv = (FormConversation) DisplayStackManager.getForm(DisplayStackManager.FORM_CONVERSATION, false);
            if (formconv !=null) {
                formconv.addRewardCommand(showReward);
            }
            FormUserProfile formprof = (FormUserProfile) DisplayStackManager.getForm(DisplayStackManager.FORM_PROFILE, false);
            if (formprof !=null) {
                formprof.createSettingsModeldata();
                if (Display.getInstance().getCurrent() instanceof FormRewards && !showReward) {
                    formprof.show();
                }
            }
        }
        this.showReward = showReward;
    }

    /**
     * getter to check whether reward should show
     * @return 
     */
    public boolean isShowReward(String countrycode) {
        return (TextUtils.isEmpty(rewardToken) || !countrycode.equals(AppConstants.DEFAULT_COUNTRY_CODE)) ? false : showReward;
    }

    public String toString() {
        return "inviteToken: " + inviteToken + "totalCredit: " + totalCredit + "rewardToken: " + rewardToken + "showReward: " + showReward;
    }
}
