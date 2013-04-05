package com.bsb.hike.dto;

public class ContactAvatar implements DataModel {

    private String msisdn;
    private String thumb;
    
    public ContactAvatar(String msisdn, String thumb) {
        this.msisdn = msisdn;
        this.thumb = thumb;
    }

    /**
     * 
     * @param sets the thumb
     */
    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
    
    /**
     * @return the msisdn
     */
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * @return the thumb as string
     */
    public String getThumb() {
        return thumb;
    }
}
