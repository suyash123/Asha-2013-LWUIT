/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.dto;

import com.bsb.hike.dto.MqttObjectModel.MessageType;
import com.bsb.hike.util.Log;
import com.bsb.hike.util.ModelUtils;
import com.bsb.hike.util.Validator;

/**
 * it is a bridge between groupMembers and Individual chat
 *
 * @author Ankit Yadav
 */
public final class ChatEntity implements DataModel {

    private static final String TAG = "ChatEntity";
    private String chatMsisdnOrGroupId;
    AddressBookEntry address;
    GroupMembers group;
    ConversationModel model;

    public ChatEntity(String selectedMsisdn) {
        chatMsisdnOrGroupId = selectedMsisdn;
        if (isGroupChat()) {
            group = GroupList.getInstance().getEntryByGroupId(selectedMsisdn);
        } else {
            address = AddressBookList.getInstance().getEntryByMsisdn(selectedMsisdn);
        }
    }
    
    /**
     * 
     * @return conversation that belong to particular chat entity
     */
    public ConversationModel getConversation(){
        if(model == null){
            model = ConversationList.getInstance().getEntryByMsisdn(chatMsisdnOrGroupId);
        }
        return model;
    }

    /**
     * 
     * @return whether chat is group chat
     */
    public boolean isGroupChat() {
        return ModelUtils.isGroupChat(chatMsisdnOrGroupId);
    }

    /**
     * 
     * @return whether the user this chat belongs to is blocked
     */
    public boolean isBlocked() {
        return AppState.getUserDetails().getBlocklist().contains(msisdnToBlock());
    }

    /**
     * 
     * @return msisdn of user to block, in case of gc it returns msisdn of creator, if the msisdn belongs to user itself then returns null
     */
    public String msisdnToBlock() {
        String msisdn = null;
        if (isGroupChat()) {
            if(group != null) {
                msisdn = group.getCreaterMsisdn();
            }
        } else if (Validator.validatePhoneString(chatMsisdnOrGroupId) != null){
            msisdn = chatMsisdnOrGroupId;
        }
        return msisdn == null || AppState.getUserDetails().getMsisdn().equals(msisdn) ? null : msisdn;
    }

    /**
     * useful only for groupchat
     * @return whether the group is active
     */
    public boolean isActive() {
        if (isGroupChat() && group != null) {
            return group.isChatAlive();
        }
        return true;
    }

    /**
     * 
     * @return effective name for chat, user name in case of 1 to 1 or group name incase of gc
     */
    public String getName() {
        ConversationModel selected = ConversationList.getInstance().getEntryByMsisdn(chatMsisdnOrGroupId);
        if (selected != null) {
            return selected.getName();
        } else {
            if (isGroupChat()) {
                return group != null ? group.getName() : LBL_GROUP_CHAT;
            } else {
                return address != null ? address.getName() : chatMsisdnOrGroupId;
            }
        }
    }

    /**
     * 
     * @return owner of the chat, user name in case of 1 to 1 or creator name in case of gc
     */
    public String getOwnerName(){
        if (isGroupChat()) {
            String creator = group.getCreaterMsisdn();            
            AddressBookEntry entry = AddressBookList.getInstance().getEntryByMsisdn(creator);
            if(entry != null){
                return entry.getName();
            }else{
               return creator; 
            }
        } else {
            return address != null ? address.getName() : chatMsisdnOrGroupId;
        }
    }
    
    /**
     * 
     * @return message type applicable for user, be it SMS or HIKE
     */
    public MessageType sendAs() {
        MessageType type = MessageType.SMS;
        if (isGroupChat()) {
            type = MessageType.HIKE;
        } else if (address != null) {
                     
            Log.v(TAG, "sending as ------- address entry exists");
            type = address.isOnHike() ? MessageType.HIKE : MessageType.SMS;
        } else if (address == null) {
            ConversationModel model = ConversationList.getInstance().getEntryByMsisdn(chatMsisdnOrGroupId);
            if (model != null) {
                type = model.isOnHike() ? MessageType.HIKE : MessageType.SMS;
                         
                Log.v(TAG, "sending as ------- conversation hike status" + model.isOnHike());
            }
        }
                 
        Log.v(TAG, "sending as ------- " + type);
        return type;
    }

    /**
     * 
     * @return data model appropriate for chat, address entry or group entry
     */
    public DataModel getModel() {
        if (isGroupChat()) {
            return group;
        } else {
            return address;
        }
    }

    /**
     * 
     * @return whthter it is chat with non-hike user
     */
    public boolean isSmsChat() {
        if (isGroupChat()) {
            return false;
        } else {
            if (address != null) {
                return !address.isOnHike();
            } else if (getConversation() != null) {
                return !getConversation().isOnHike();
            } else {
                return true;
            }
        }
    }

    /**
     * 
     * @return msisdn (or groupid) to chat with
     */
    public String getChatMsisdn() {
        return chatMsisdnOrGroupId;
    }

    public String toString() {
        return "chatEntity: blocked - " + isBlocked() + " groupChat - " + isGroupChat() + " number - " + getChatMsisdn() + " group active - " + isActive() + " send as - " + sendAs();
    }
}
