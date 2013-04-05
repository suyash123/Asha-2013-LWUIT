package com.bsb.hike.dto;

import java.util.Enumeration;

/**
 * list for ContactAvatar
 * @author Ankit Yadav
 */
public class ContactAvatarList extends DataCollection {

    private static final ContactAvatarList list = new ContactAvatarList();
    private static final String TAG = "ContactAvtarList";

    private ContactAvatarList() {
    }

    /**
     * 
     * @return singleton instance of Avatarlist
     */
    public static ContactAvatarList getInstance() {
        return list;
    }

    /**
     * iterates and return contactavatar by msisdn
     * @param msisdn
     * @return 
     */
    public static ContactAvatar getContactAvtarByMSISDN(String msisdn) {

        Enumeration enums = list.elements();
        while (enums.hasMoreElements()) {
            ContactAvatar chat = (ContactAvatar) enums.nextElement();
            if (chat.getMsisdn().equals(msisdn)) {
                return chat;
            }
        }
        return null;
    }
}
