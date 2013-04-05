/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.io.mqtt;

import com.bsb.hike.dto.AddressBookEntry;
import com.bsb.hike.dto.AppState;
import com.bsb.hike.dto.ChatModel;
import com.bsb.hike.dto.GroupList;
import com.bsb.hike.dto.GroupMembers;
import com.bsb.hike.dto.MqttMessage;
import com.bsb.hike.dto.MqttObjectModel;
import com.bsb.hike.dto.MqttObjectModel.MessageType;
import com.bsb.hike.dto.MqttObjectModel.MqttType;
import com.bsb.hike.mqtt.msg.QoS;
import com.bsb.hike.util.AppConstants;
import com.bsb.hike.util.Log;
import com.bsb.hike.util.TextUtils;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.json.me.JSONArray;

/**
 *
 * @author Sudheer Keshav Bhat
 */
public class MqttManager implements AppConstants {

    private static final String TAG = "MqttManager";
    public static final Hashtable/*<Integer, Vector<ChatModel>>*/ msgIdToReceivedChatModel = new Hashtable();
    public static final Hashtable/*<Integer, GroupMembers>*/ msgIdToGroupMembers = new Hashtable();

    /**
     * method creates message and publish to mqtt server
     * @param msisdn
     * @param model
     * @param isGroupChat 
     */
    public static void sendMessage(String msisdn, ChatModel model, boolean isGroupChat) {
        if (AppState.getUserDetails() == null || msisdn == null || model == null) {
            return;
        }
        long id = model.getMessageID();
        if (model.isInvite()) {
            Log.v(TAG, "sending invite: " + model + "to " + msisdn + " with msgID " + id + " " + isGroupChat);
            invite(msisdn, id);
        } else {
            Log.v(TAG, "sending message: " + model + "to " + msisdn + " with msgID " + id + " " + isGroupChat);
            String fromMsisdn = null;
            if (isGroupChat) {
                fromMsisdn = AppState.getUserDetails().getMsisdn();
            }

            Log.v(TAG, "sending from msisdn " + fromMsisdn);
            MqttMessage dataMsg = new MqttMessage(model.getType(), String.valueOf(id), TextUtils.replace(TextUtils.getMappedSmileys(),TextUtils.getOriginalSmileys(), model.getMessage()), model.getTimestamp() / 1000, null);
            MqttObjectModel mqttMessage = new MqttObjectModel(MqttType.MESSAGE, dataMsg, msisdn, fromMsisdn);
            String jsonString = mqttMessage.toJsonString();

            Log.v(TAG, "sending msg to MQTT" + jsonString);
            MqttConnectionHandler.getMqttConnectionHandler().publish(AppState.getUserDetails().getUid() + Topics.PUBLISH, jsonString, QoS.AT_LEAST_ONCE, id);
        }
    }

    /**
     * method creates invite and publish to mqtt server
     * @param msisdn
     * @param id 
     */
    public static void invite(String msisdn, long id) {
        if (AppState.getUserDetails() == null || AppState.getUserDetails().getAccountInfo() == null) {
            return;
        }
        String msg = AppState.getUserDetails().getAccountInfo().getInviteMessage();
        MqttMessage dataMsg = new MqttMessage(MessageType.SMS, String.valueOf(id), msg, new Date().getTime() / 1000, null);
        // invite would contain 'to' field only
        MqttObjectModel mqttMessage = new MqttObjectModel(MqttType.INVITE, dataMsg, msisdn, null);
        String jsonString = mqttMessage.toJsonString();

        Log.v(TAG, "sending invite request to MQTT" + jsonString);
        MqttConnectionHandler.getMqttConnectionHandler().publish(AppState.getUserDetails().getUid() + Topics.PUBLISH, jsonString, QoS.AT_LEAST_ONCE, id);
    }

    /**
     * method for bulk invite 
     * @param addresses 
     */
    public static void invite(final Vector/*<AddressBookEntry OR msisdn>*/ addresses) {
        Enumeration enums = addresses.elements();
        Log.v(TAG, "Contacts to invite: " + addresses.size());
        while (enums.hasMoreElements()) {
            Object element = enums.nextElement();
            if (element instanceof AddressBookEntry) {
                AddressBookEntry entry = (AddressBookEntry) element;
                MqttManager.invite(entry.getMsisdn(), AppState.getNextMessageID());
            } else if (element instanceof String) {
                MqttManager.invite(element.toString(), AppState.getNextMessageID());
            }
        }
    }
    
    /**
     * method for requesting additional account info
     */
    public static void requestAccountInfo() {
        if (AppState.getUserDetails() == null) {
            return;
        }
        MqttType type = MqttType.REQUEST_ACCOUNT_INFO;
        MqttObjectModel mqttAccountInfo = new MqttObjectModel(type, null, null, null);

        Log.v(TAG, mqttAccountInfo.toJsonString());
        try {
            String accountInfo = mqttAccountInfo.toJsonString(); //        
            MqttConnectionHandler.getMqttConnectionHandler().publish(AppState.getUserDetails().getUid() + Topics.PUBLISH, accountInfo, QoS.AT_LEAST_ONCE);
        } catch (Exception ex) {
            Log.v(TAG, "error requesting account info: " + ex.getClass().getName());
        }
    }

    /**
     * method for blocking/unblocking a contact
     * @param block
     * @param msisdn 
     */
    public static void blockUser(boolean block, String msisdn) {
        if (AppState.getUserDetails() == null) {
            return;
        }
        MqttType type = MqttType.UNBLOCK_USER;
        if (block) {
            MqttManager.sendTyping(false, msisdn);
            type = MqttType.BLOCK_USER;
        }
        MqttObjectModel mqttBlock = new MqttObjectModel(type, msisdn, null, null);

        Log.v(TAG, mqttBlock.toJsonString());
        try {
            String blockMQTT = mqttBlock.toJsonString();
            MqttConnectionHandler.getMqttConnectionHandler().publish(AppState.getUserDetails().getUid() + Topics.PUBLISH, blockMQTT, QoS.AT_LEAST_ONCE);
        } catch (Exception ex) {
            Log.v(TAG, "error un/blocking account info: " + ex.getClass().getName());
        }
    }

    /**
     * method for sending read report
     * @param chatModel 
     */
    public static void markAsRead(ChatModel chatModel) {
        Vector chatModels = new Vector();
        chatModels.addElement(chatModel);
        markAsRead(chatModels);
    }

    /**
     * method for bulk read report
     * @param chatModels 
     */
    public static void markAsRead(Vector/*<ChatModel>*/ chatModels) {
        if (AppState.getUserDetails() == null || chatModels == null) {
            return;
        }
        long id = AppState.getNextMessageID();
        //TODO need to handle group chat case
        Vector unreadChats = new Vector(chatModels.size());
        Enumeration enums = chatModels.elements();
        while (enums.hasMoreElements()) {
            ChatModel chatModel = (ChatModel) enums.nextElement();
            if (chatModel.getMessageStatus() == ChatModel.MessageStatus.RECEIVED) {
                if (!chatModel.isMe()) {
                    unreadChats.addElement(chatModel);
                } else if (chatModel.getType() == MessageType.SYSTEM){
                    chatModel.setMessageStatus(ChatModel.MessageStatus.READ);
                }
            }
        }
        if (!unreadChats.isEmpty()) {
            msgIdToReceivedChatModel.put(new Long(id), unreadChats);
            String msisdn = ((ChatModel) unreadChats.elementAt(0)).getFromMsisdn();
            MqttObjectModel mqttModel = new MqttObjectModel(MqttType.MESSAGE_READ, getMppdMsgIds(unreadChats), msisdn, null);
            String jsonString = mqttModel.toJsonString();

            Log.v(TAG, "sending msg read request to MQTT" + jsonString);
            MqttConnectionHandler.getMqttConnectionHandler().publish(AppState.getUserDetails().getUid() + Topics.PUBLISH, jsonString, QoS.AT_LEAST_ONCE, id);
        }
    }

    /**
     * send typing status
     * @param typing
     * @param to 
     */
    public static void sendTyping(boolean typing, String to) {
        if (AppState.getUserDetails() == null || to == null) {
            return;
        }
        MqttObjectModel mqttModel = new MqttObjectModel(typing ? MqttType.START_TYPING : MqttType.END_TYPING, null, to, null);
        String jsonString = mqttModel.toJsonString();

        Log.v(TAG, "sending typing request to MQTT" + jsonString);
        MqttConnectionHandler.getMqttConnectionHandler().publish(AppState.getUserDetails().getUid() + Topics.PUBLISH, jsonString, QoS.AT_MOST_ONCE);
    }
    
    /**
     * group chat leave request for given group id
     * @param groupId 
     */
    public static void leaveGroupChat(String groupId) {
        if (AppState.getUserDetails() == null || groupId == null) {
            return;
        }
        long id = AppState.getNextMessageID();
        GroupMembers group = GroupList.getInstance().getEntryByGroupId(groupId);
        if (group != null) {
            Log.v(TAG, "group:" + group);
            msgIdToGroupMembers.put(new Long(id), group);
            String groupCreatorMsisdn = group.getCreaterMsisdn();
            MqttObjectModel mqttModel = new MqttObjectModel(MqttType.GROUP_CHAT_LEAVE, AppState.getUserDetails().getMsisdn(), groupId, groupCreatorMsisdn);
            String jsonString = mqttModel.toJsonString();

            Log.v(TAG, "sending leaveGroupChat request to MQTT" + jsonString);
            MqttConnectionHandler.getMqttConnectionHandler().publish(AppState.getUserDetails().getUid() + Topics.PUBLISH, jsonString, QoS.AT_LEAST_ONCE);
            GroupList.getInstance().removeElement(group);
        }
    }

    /**
     * create group chat
     * @param list
     * @return 
     */
    public static String createGroupChat(Hashtable list) {
        if (AppState.getUserDetails() == null) {
            return null;
        }
        String grpId = AppState.getUserDetails().getUid() + AppConstants.SEPARATOR + new Date().getTime();
        GroupMembers group = new GroupMembers(grpId, AppState.getUserDetails().getMsisdn());
        GroupList.getInstance().addElement(group);
        addUsersToGrpChat(group, list);
        return grpId;
    }

    /**
     * add more users to group chat
     * @param group
     * @param list 
     */
    public static void addUsersToGrpChat(GroupMembers group, Hashtable list) {
        if (AppState.getUserDetails() == null || group == null || list == null) {
            return;
        }
        try {
            Log.v(TAG, "addUsersToGrpChat()");
            String grpId = group.getGroupId();

            Log.v(TAG, "group id : " + grpId);
            group.addMembers(list);

            Log.v(TAG, "members added : " + list.size());
            MqttObjectModel mqttObj = new MqttObjectModel(MqttObjectModel.MqttType.GROUP_CHAT_JOIN, list, grpId, group.getCreaterMsisdn());
            String blockMQTT = MqttJsonWrapper.toJSON(mqttObj).toString();

            Log.v(TAG, EMPTY_STRING + blockMQTT);

            Log.v(TAG, "addUsersToGrpChat()::2");
            MqttConnectionHandler.getMqttConnectionHandler().publish(AppState.getUserDetails().getUid() + Topics.PUBLISH, blockMQTT, QoS.AT_LEAST_ONCE);
        } catch (Exception ex) {
            Log.v(TAG, "some exception while creating/updating group chat" + ex.getClass().getName());
        }
    }

    /**
     * gets json array out of list of chat models
     * @param chatModels
     * @return 
     */
    private static JSONArray getMppdMsgIds(Vector/*<ChatModel>*/ chatModels) {
        JSONArray jsonArr = new JSONArray();
        Enumeration enums = chatModels.elements();
        while (enums.hasMoreElements()) {
            ChatModel chatModel = (ChatModel) enums.nextElement();
            jsonArr.put(EMPTY_STRING + chatModel.getMappedMsgId());
        }
        return jsonArr;
    }
}
