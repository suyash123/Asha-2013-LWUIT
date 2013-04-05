package com.bsb.hike.dto;

import com.bsb.hike.util.AppConstants;
import com.bsb.hike.util.AppResource;
import com.bsb.hike.util.Log;
import com.bsb.hike.util.ModelUtils;
import com.sun.lwuit.Image;

public class ConversationModel implements DataModel {

    private static final String TAG = "ConversationModel";
    private long conversationID; // primary key, auto increment
    private String msisdnOrGroupId; //(or group id ) unique
    private boolean onHike = false;
    private transient boolean typing;
    private String name;
    private long messageId;
    //volatile
    private ChatModel chatModel;
    private ContactAvatar cachedContactAvatar;
    
    public ConversationModel(long conversationID, String msisdnOrGroupId, long msgId) {
        this.conversationID = conversationID;
        this.msisdnOrGroupId = msisdnOrGroupId;
        this.messageId = msgId;
    }
    
    public ContactAvatar getCachedContactAvatar() {
        return cachedContactAvatar;
    }

    public void setCachedContactAvatar(ContactAvatar avatar) {

        this.cachedContactAvatar = avatar;
    }

    public boolean isTyping() {
        return typing;
    }

    public void setTyping(boolean typing) {
        this.typing = typing;
    }

    /**
     * @return the conversationID
     */
    public long getConversationID() {
        return conversationID;
    }

    /**
     * @return the msisdn
     */
    public String getMsisdn() {
        if(msisdnOrGroupId !=null && msisdnOrGroupId.equals(NULL_STRING)){
            msisdnOrGroupId = null;
        }
        return msisdnOrGroupId;
    }

    public String getName() {
        if (ModelUtils.isGroupChat(getMsisdn())) {
            GroupMembers group = GroupList.getInstance().getEntryByGroupId(getMsisdn());
            if (group != null) {
                return group.getName();
            } else {
                return getMsisdn();
            }
        } else if (name == null) {
            AddressBookEntry entry = AddressBookList.getInstance().getEntryByMsisdn(getMsisdn());
            name = entry != null ? entry.getName() : getMsisdn();
        }
        return name;
    }

    //anshuman:
    public void setName(String str)
    {
        
        if (ModelUtils.isGroupChat(getMsisdn())) {
            GroupMembers group = GroupList.getInstance().getEntryByGroupId(getMsisdn());
            if (group != null) {
                group.setGroupName(str);
            } 
        }
    }

    public void setMsgId(long msgId) {
        this.chatModel = null;
        this.messageId = msgId;
    }

    public long getMsgId() {
        return messageId;
    }

    public ChatModel getLastChatModel() {
        if (chatModel == null || messageId == SYSTEM_MESSAGE || chatModel.getMessageID() != messageId) {
            chatModel = ChatList.getInstance().getChatByMessageID(messageId);
            if (chatModel == null) {
                chatModel = ChatList.getInstance().getLastChatByConversationID(conversationID);
            }
        }
        Log.v(TAG, "Last ChatModel is " + chatModel);
        return chatModel;
    }

    public boolean hasParticipant(String msisdnOrGroupId) {
        if (ModelUtils.isGroupChat(this.msisdnOrGroupId)) {
            GroupMembers group = GroupList.getInstance().getEntryByGroupId(this.msisdnOrGroupId);
            return group != null && group.hasMember(msisdnOrGroupId);
        } else {
            return this.msisdnOrGroupId.equals(msisdnOrGroupId);
        }
    }

    /**
     * @return the onHike
     */
    public boolean isOnHike() {
        //use only if user is not in contact book
        return onHike;
    }

    /**
     * @param onHike the onHike to set
     */
    public void setOnHike(boolean onHike) {
        this.onHike = onHike;
    }

    /**
     * 
     * TODO:: add caching here for the image, so that this is not called again and again from Form Conversation
     * Leads to a big perf bug otherwise
     * @return 
     */
    public Image getContactAvtar() {
        
        if (cachedContactAvatar != null) {
            return Image.createImage(cachedContactAvatar.getThumb().getBytes(), 0, cachedContactAvatar.getThumb().getBytes().length).scaled(38, 38);
        } else {
            if (ModelUtils.isGroupChat(this.msisdnOrGroupId)) {
                return AppResource.getImageFromResource(AppConstants.PATH_GROUP_AVATAR_ICON_GROUP);
            } else {
                return AppResource.getImageFromResource(AppConstants.PATH_INDIVIDUAL_AVATAR_ICON_DEFAULT);
            }
        }

    }

    public String toString() {
        return "ConversationModel{"+getLastChatModel()+"}";
    }
}
