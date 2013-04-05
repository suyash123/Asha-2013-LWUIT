/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.dto;

import com.bsb.hike.Hike;
import com.bsb.hike.util.AppConstants;
import com.bsb.hike.util.TextUtils;

/**
 *dto for update jad information
 * @author PuneethA
 */
public class JADInfoAttribute implements AppConstants {

    private String criticalVersion;
    private String latestVersion;
    private String url;

    public JADInfoAttribute(String url, String criticalVersion, String latestVersion) {
        this.criticalVersion = criticalVersion;
        this.latestVersion = latestVersion;
        this.url = url;
    }

    /**
     * 
     * @return jad url
     */
    public String getJadURL() {
        return url;
    }

    /**
     * 
     * @return jad url of last critical update
     */
    public String getJadCriticalVersion() {
        return AppState.compareVersions(Hike.sMidlet.getAppProperty(LBL_MIDLET_VERSION), criticalVersion) ? criticalVersion : EMPTY_STRING;
    }

    /**
     * 
     * @return jad url of latest version available
     */
    public String getJadLatestVersion() {
        return latestVersion;
    }

    /**
     * 
     * @return whether update is critical
     */
    public boolean isCritical() {
        return !TextUtils.isEmpty(criticalVersion);
    }
}
