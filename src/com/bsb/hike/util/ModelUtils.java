/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.util;

import com.bsb.hike.Hike;
import com.bsb.hike.dto.AccountInfo;
import com.bsb.hike.dto.AddressBookList;
import com.bsb.hike.dto.AppState;
import com.bsb.hike.dto.ChatList;
import com.bsb.hike.dto.ChatModel;
import com.bsb.hike.dto.ContactAvatar;
import com.bsb.hike.dto.ContactAvatarList;
import com.bsb.hike.dto.ConversationList;
import com.bsb.hike.dto.ConversationModel;
import com.bsb.hike.dto.GroupList;
import com.bsb.hike.dto.GroupMembers;
import com.bsb.hike.dto.MqttObjectModel;
import com.bsb.hike.dto.MqttObjectModel.MessageType;
import com.bsb.hike.dto.UserDetails;
import com.bsb.hike.dto.UserHikeStatus;
import com.bsb.hike.io.mqtt.MqttConnectionHandler;
import com.bsb.hike.io.mqtt.MqttManager;
import com.bsb.hike.ui.DisplayStackManager;
import com.bsb.hike.ui.FormChatThread;
import com.bsb.hike.ui.FormConversation;
import com.bsb.hike.ui.FormGroupInfo;
import com.bsb.hike.ui.FormSelectGroupContact;
import com.bsb.hike.ui.FormUserProfile;
import com.bsb.hike.ui.FreeSMSForm;
import com.sun.lwuit.Display;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.AlertType;
import org.json.me.JSONObject;

/**
 *
 * @author Sudheer Keshav Bhat
 */
public class ModelUtils implements AppConstants {

    private static final String TAG = "ModelUtils";

    /**
     * creates chatmodel and add to given conversation
     * @param msgTxt
     * @param timestamp
     * @param fromMsisdn
     * @param toMsisdn
     * @param mappedMsgId
     * @param msgStatus
     * @param type
     * @param isInvite 
     */
    public static void addChat(String msgTxt, long timestamp, String fromMsisdn, String toMsisdn, long mappedMsgId, int msgStatus, MessageType type, boolean isInvite) {
        ChatModel chatModel;
        String convMsisdn;
        boolean isGroupChat = isGroupChat(toMsisdn);
        if (isGroupChat) {
            convMsisdn = toMsisdn;
        } else {
            convMsisdn = fromMsisdn == null ? toMsisdn : fromMsisdn;
        }
        ConversationModel conversation = ConversationList.getInstance().getEntryByMsisdn(convMsisdn);

        long msgId = AppState.getNextMessageID(); // remove call in case returned without adding chat
        if (type == MessageType.SYSTEM) {
            //check for global notification, user joined, user optin, sms user credited
            if (TextUtils.contains(msgTxt, SYSTEM_MSG_USER_JOINED) || TextUtils.contains(msgTxt, SYSTEM_MSG_USER_OPTED_IN) || TextUtils.contains(msgTxt, SYSTEM_MSG_USER_CREDIT_PREFIX)) {
                chatModel = new ChatModel(SYSTEM_CONVERSATION, msgTxt, msgStatus, timestamp, msgId, mappedMsgId, toMsisdn, fromMsisdn, type);

                Log.v(TAG, "adding global chat to chat list" + chatModel);
                ChatList.getInstance().addElement(chatModel);
                return;
            }
        }

        if (isGroupChat && GroupList.getInstance().getEntryByGroupId(toMsisdn) == null) {
            return;
        }

        if (conversation == null) {
            if (type == MessageType.SYSTEM && msgTxt.endsWith(SYSTEM_MSG_GRP_CHAT_ENDED)) {
                return;
            }
            long convId = AppState.getNextConvID();
            chatModel = new ChatModel(convId, msgTxt, msgStatus, timestamp, msgId, mappedMsgId, toMsisdn, fromMsisdn, type);
            chatModel.setInvite(isInvite);

            Log.v(TAG, fromMsisdn);
            conversation = new ConversationModel(convId, convMsisdn, msgId);
            if (type == MessageType.HIKE) {
                conversation.setOnHike(true);
            }
            Log.v(TAG, "adding chat to chat list" + chatModel);
            ChatList.getInstance().addElement(chatModel);
            Log.v(TAG, "adding new conversation to conversation list" + chatModel);
            ConversationList.getInstance().addElement(conversation);
        } else {
            ChatModel last = conversation.getLastChatModel();
            if (last != null && last.getType() == MessageType.SYSTEM && last.getMessage().equals(msgTxt)) {
                return;
            }
            chatModel = new ChatModel(conversation.getConversationID(), msgTxt, msgStatus, timestamp, msgId, mappedMsgId, toMsisdn, fromMsisdn, type);
            chatModel.setInvite(isInvite);
            conversation.setMsgId(msgId);
            Log.v(TAG, "adding chat to chat list" + chatModel);
            ChatList.getInstance().addElement(chatModel);
        }
        VibratePhone(type, chatModel.isMe(), fromMsisdn, toMsisdn);
    }

    public static void addChat(String msgTxt, long timestamp, String fromMsisdn, String toMsisdn, long mappedMsgId, int msgStatus, MessageType type) {
        addChat(msgTxt, timestamp, fromMsisdn, toMsisdn, mappedMsgId, msgStatus, type, false);
    }

    /**
     * updates user's hike status and and credits and make a callback for UI updation
     * @param user 
     */
    public static void updateUserHikeStatusAndCredits(UserHikeStatus user) {
        if (user == null) {
            return;
        }
        AddressBookList.getInstance().updateHikeStatus(user.getMsisdn(), user.isOnHike());

        if (user.getSms() > 0) {
            ChatModel chatModel = new ChatModel(SYSTEM_CONVERSATION, SYSTEM_MSG_USER_CREDIT_PREFIX + user.getSms() + SYSTEM_MSG_USER_CREDIT_SUFFIX, ChatModel.MessageStatus.READ, new Date().getTime(), AppState.getNextMessageID(), SYSTEM_MAPPED_MESSAGE, null, user.getMsisdn(), MessageType.SYSTEM);
            Log.v(TAG, "adding optin message to list" + chatModel);
            ChatList.getInstance().addElement(chatModel);
        }

        //to update last chat message
        ConversationModel conv = ConversationList.getInstance().getEntryByMsisdn(user.getMsisdn());
        if (conv != null) {
            long convId = conv.getConversationID();
            conv.setOnHike(user.isOnHike());
            ChatModel chat = ChatList.getInstance().getLastChatByConversationID(convId);
            if (chat != null && chat.getType() != MessageType.SYSTEM) {
                if (user.isOnHike()) {
                    chat.setType(MessageType.HIKE);
                } else {
                    chat.setType(MessageType.SMS);
                }
            } else {
                //TODO least probability
            }
            ChatList.getInstance().refreshModel(chat);
        }
    }

    
    /**
     * updates chat status for given msg id or sent chat
     * @param msgId
     * @param msgStatus
     * @return 
     */
    public static boolean updateSentChatByMsgId(long msgId, int msgStatus) {
        ChatModel chtMdlObj = ChatList.getInstance().getChatByMessageID(msgId);
        Log.v(TAG, "updateChatByMsgId()::update chat with msgId:" + chtMdlObj + " TO STATUS: " + msgStatus);
        if (chtMdlObj != null && chtMdlObj.isMe()) {
            updateChat(chtMdlObj, msgStatus);
            Log.v(TAG, "updated");
            return true;
        }
        Log.v(TAG, "not updated");
        return false;
    }

    /**
     * updates chat status for given msg id or received chat
     * @param msgId
     * @param msgStatus
     * @return 
     */
    public static boolean updateReceivedChatByMsgId(long msgId, int msgStatus) {
        Log.v(TAG, "updateReceivedChatByMsgId()");
        Object chtMdlsObj = MqttManager.msgIdToReceivedChatModel.get(new Long(msgId));
        if (chtMdlsObj != null && chtMdlsObj instanceof Vector) {
            Vector chatModels = (Vector) chtMdlsObj;
            for (int i = 0; i < chatModels.size(); i++) {
                ChatModel chatModel = (ChatModel) chatModels.elementAt(i);
                updateChat(chatModel, msgStatus);
                Log.v(TAG, "updateReceivedChatByMsgId()::update chat with status:" + chatModel + ":" + msgStatus);
            }
            return true;
        }
        return false;
    }

    /**
     * updates message status for given chatmodel
     * @param chatModel
     * @param msgStatus 
     */
    public static void updateChat(ChatModel chatModel, int msgStatus) {
        //update status
        chatModel.setMessageStatus(msgStatus);
        //to trigger listener callback..
        ChatList.getInstance().refreshModel(chatModel);
    }

    /**
     * updates message status based in given array of msgIds
     * @param msgIds
     * @param msgStatus 
     */
    public static void updateChatByMsgIds(long[] msgIds, int msgStatus) {
        for (int i = 0; i < msgIds.length; i++) {
            updateSentChatByMsgId(msgIds[i], msgStatus);
        }
    }

    /**
     * updates typing status for a given conversation
     * @param msisdn
     * @param typing 
     */
    public static void updateConversationByMsisdn(String msisdn, boolean typing) {
        ConversationModel conversation = ConversationList.getInstance().getEntryByMsisdn(msisdn);
        if (conversation != null) {
            conversation.setTyping(typing);
            ConversationList.getInstance().refreshModel(conversation);
        }
    }

    /**
     * sets group status as inactive when some other user has ended chat
     * @param groupId
     * @param endChat 
     */
    public static void setGroupChatStatus(String groupId, boolean endChat) {
        GroupList groups = GroupList.getInstance();
        GroupMembers group = groups.getEntryByGroupId(groupId);
        if (group != null) {
            group.setChatAlive(!endChat);
            groups.refreshModel(group);
            if ((Display.getInstance().getCurrent() instanceof FormGroupInfo || Display.getInstance().getCurrent() instanceof FormSelectGroupContact) && AppState.getUserDetails().getCurrentGrp().getGroupId().equals(groupId)) {
                DisplayStackManager.showForm(DisplayStackManager.FORM_CONVERSATION);
            }
        }
    }

    /**
     * updates group chat when new user details are received
     * @param groupId
     * @param members
     * @param creatorMsisdn 
     */
    public static void updateGroupChat(String groupId, Hashtable members, String creatorMsisdn) {
        try {

            Log.v(TAG, "group update started ");
            GroupMembers group = GroupList.getInstance().getEntryByGroupId(groupId);

            Log.v(TAG, "group member " + group);
            if (group != null) {
                group.addMembers(members);
                Log.v(TAG, "group member added " + group);
            } else {
                group = new GroupMembers(groupId, creatorMsisdn);
                Log.v(TAG, "group member created" + group);

                GroupList.getInstance().addElement(group);
                Log.v(TAG, "group added");

                group.addMembers(members);
                Log.v(TAG, "group member added" + group);
            }

            Log.v(TAG, "group update finish ");
        } catch (Exception ex) {

            Log.v(TAG, ex.getClass().getName());
        }
    }

    /**
     * remove a user from group chat
     * @param groupId
     * @param leavingUserMsisdn 
     */
    public static void removeUserFromGroupChat(String groupId, String leavingUserMsisdn) {
        GroupMembers group = GroupList.getInstance().getEntryByGroupId(groupId);
        if (group != null) {
            group.removeMember(leavingUserMsisdn);
        }
    }

    /**
     * check whether its a group chat if msisdn starts with + its i-to-1 else group chat
     * @param msisdn
     * @return 
     */
    public static boolean isGroupChat(String msisdn) {
        return msisdn != null && !msisdn.startsWith("+") ? true : false;
    }

    /**
     * sets group status as inactive when user receives ack of end chat request
     * @param msgId
     * @param active
     * @return 
     */
    public static boolean updateGroupStatus(long msgId, boolean active) {
        Log.v(TAG, "updateGroupStatus()");
        GroupMembers group = (GroupMembers) MqttManager.msgIdToGroupMembers.get(new Long(msgId));
        if (group != null) {
            Log.v(TAG, "updateGroupStatus():: found group:" + group);
            group.setChatAlive(active);
            //TODO refresh UI required?
            return true;
        }
        return false;
    }

    /**
     *update thumbnails when recieved for particular msisdn
     * @param fromMSISDN
     * @param thumbs
     */
    public static void updateThumbsInAvatar(String fromMSISDN, String thumbs) {
        ContactAvatar avatar = new ContactAvatar(fromMSISDN, thumbs);
        ContactAvatarList.getInstance().addElement(avatar);
        ConversationModel model = ConversationList.getInstance().getEntryByMsisdn(fromMSISDN);
        if (model != null) {
            model.setCachedContactAvatar(avatar);
            if (Display.getInstance().getCurrent() instanceof FormConversation) {
                DisplayStackManager.showForm(DisplayStackManager.FORM_CONVERSATION);
            }
        }
    }

    /**
     * anshuman:: adding support to update group title
     *
     * @param name String
     * @param fromMsisdn String
     *
     */
    public static void updateGroupTitle(String name, String toMSISDN) {
        Log.v(TAG, "Got group name" + name);
        GroupMembers group = GroupList.getInstance().getEntryByGroupId(toMSISDN);
        if (group != null) {
            Log.v(TAG, "found group to update" + toMSISDN);
            group.setGroupName(name);
        }
        FormChatThread formchat = (FormChatThread) DisplayStackManager.getForm(DisplayStackManager.FORM_CHAT, false);
        if(formchat != null) {
            formchat.setTitle(name);
        }
    }

    /**
     * condition check for vibration and ringer, alerts only where and when required
     * @param type
     * @param me
     * @param fromMsisdn
     * @param toMsisdn 
     */
    private static void VibratePhone(MessageType type, boolean me, String fromMsisdn, String toMsisdn) {
        if (Display.getInstance().getCurrent() instanceof FormChatThread && AppState.getUserDetails() != null) {
            if (AppState.getUserDetails().getCurrentGrp() != null) {
                // Currently in Group chat: 
                if (isGroupChat(toMsisdn)) {
                    // receving group chat message
                    if (MessageType.HIKE == type && !me && !AppState.getUserDetails().getCurrentGrp().getGroupId().equals(toMsisdn)) {
                        vibeOrRingPhone();
                    }
                } else {
                    if (MessageType.HIKE == type && !me) {
                        vibeOrRingPhone();
                    }
                }
            } else {
                // currently in One to One chat 
                if (isGroupChat(toMsisdn)) { // check incomming is Groupchat
                    if (MessageType.HIKE == type && !me) {
                        vibeOrRingPhone();
                    }
                } else {
                    // incomming is One to one chat
                    if (MessageType.HIKE == type && !me && !AppState.getUserDetails().getSelectedMsisdn().equals(fromMsisdn)) {
                        vibeOrRingPhone();
                    }
                }
            }
        } else if (Display.getInstance().getCurrent() instanceof FormConversation) {
            // currently in conversation
            if (MessageType.HIKE == type) {
                vibeOrRingPhone();
            }
        } else {
            vibeOrRingPhone();
        }
    }

    /**
     * vibrates and/or ring phone based on user settings
     */
    private static void vibeOrRingPhone() {
        if (AppState.isVibrationOn()) {
            javax.microedition.lcdui.Display.getDisplay(Hike.sMidlet).vibrate(VIBRATE_DURATION);
            Log.v(TAG, "Vibrating :::");
        }
        if (AppState.isVolumeOn()) {
            Log.v(TAG, "Playing Tone:::");
            AlertType.INFO.playSound(javax.microedition.lcdui.Display.getDisplay(Hike.sMidlet));
        }
    }

    /**
     * reflects the change in UI based on updated sms-credits
     * @param smsCredit 
     */
    public static void updateSmsCreditInUI(int smsCredit) {
        FreeSMSForm formsms = (FreeSMSForm) DisplayStackManager.getForm(DisplayStackManager.FORM_FREE_SMS, false);
        if (formsms != null) {
            formsms.updateSmsCountInFreeSMSForm(smsCredit);
            Log.v(TAG, "credit change reflected in FreeSMSForm");
        }
        FormUserProfile formuserprofile = (FormUserProfile) DisplayStackManager.getForm(DisplayStackManager.FORM_PROFILE, false);
        if (formuserprofile != null) {
            formuserprofile.updateSmsCountInUserProfile(smsCredit);
            Log.v(TAG, "credit change reflected in FormUserProfile");
        }
    }

    /**
     * deletes user locally after closing mqtt connection
     * @param message 
     */
    public static void deleteUser(String message) {
        MqttConnectionHandler.getMqttConnectionHandler().close();
        DisplayStackManager.showProgressForm(true, message);
        AppState.deleteUser();
        DisplayStackManager.showForm(AppState.getForm());
    }

    /**
     * generate a system message based on type string passed
     * @param type
     * @param name
     * @param fromMsisdn
     * @param toMsisdn 
     */
    public static void getSystemMessage(String type, String name, String fromMsisdn, String toMsisdn) {
        if (TextUtils.contains(type, SYSTEM_MSG_MEMBER_REMOVED)) {
            ModelUtils.addChat(name + type, new Date().getTime(), fromMsisdn, toMsisdn, 0, ChatModel.MessageStatus.RECEIVED, MqttObjectModel.MessageType.SYSTEM);
        } else if (TextUtils.contains(type, SYSTEM_MSG_MEMBER_ADDED)) {
            ModelUtils.addChat(name + type, new Date().getTime(), fromMsisdn, toMsisdn, 0, ChatModel.MessageStatus.RECEIVED, MqttObjectModel.MessageType.SYSTEM);
        } else if (TextUtils.contains(type, SYSTEM_MSG_WAITING_DND_USERS_PREFIX)) {
            ModelUtils.addChat(name + SYSTEM_MSG_WAITING_DND_USERS_PREFIX + name + SYSTEM_MSG_WAITING_DND_USERS_SUFFIX, new Date().getTime(), fromMsisdn, toMsisdn, 0, ChatModel.MessageStatus.RECEIVED, MqttObjectModel.MessageType.SYSTEM);
        } else if (TextUtils.contains(type, SYSTEM_MSG_WAITING_DND_USERS_PREFIX_GC)) {
            ModelUtils.addChat(SYSTEM_MSG_WAITING_DND_USERS_PREFIX_GC + name + SYSTEM_MSG_WAITING_DND_USERS_SUFFIX_GC, new Date().getTime(), fromMsisdn, toMsisdn, 0, ChatModel.MessageStatus.RECEIVED, MqttObjectModel.MessageType.SYSTEM);
        } else if (TextUtils.contains(type, SYSTEM_MSG_MEMBER_INVITED)) {
            ModelUtils.addChat(name + type, new Date().getTime(), fromMsisdn, toMsisdn, 0, ChatModel.MessageStatus.RECEIVED, MqttObjectModel.MessageType.SYSTEM);
        }
    }

    /**
     * updates user account details
     * @param data 
     */
    public static void updateAccountDetails(Object data) {
        if (AppState.getUserDetails() == null || data == null) {
            return;
        } 
        if (data instanceof JSONObject) {
            AccountInfo info = AppState.getUserDetails().getAccountInfo();
            if (info != null) {
                JSONObject account = (JSONObject) data;
                Log.v(TAG, "json for account details: " + account);
                String rewardtoken = account.optString(JsonKeyRewardToken, null);
                boolean showreward = account.optBoolean(JsonKeyShowRewards, false);
                info.setRewardDetails(rewardtoken, showreward);
            }
        } else if (data instanceof AccountInfo) {
            //not being used
            AppState.getUserDetails().setAccountInfo((AccountInfo)data);
        }
    }
    
    public static void setPostAddressBookFlag(Object data){
         if (AppState.getUserDetails() == null || data == null) {
            return;
        } 
        if (data instanceof JSONObject) {
            UserDetails userDetails = AppState.getUserDetails();
            if (userDetails != null) {
                 JSONObject postAddressBook = (JSONObject) data;
                 Log.v(TAG, "json for force post address book: " + postAddressBook.toString());
                 boolean forceAddressBook = postAddressBook.optBoolean(JsonKeyPostab, false);
                 userDetails.setForcePostAddressBook(forceAddressBook);
            }
        }
    }
    
    /**
     * gets messageid from message json of type i & m
     * @param json
     * @return 
     */
    public static long getMessageIdfromMqttJson(String json) {
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONObject jsondata = jsonObj.optJSONObject(JsonKeyMqttData); 
            if (jsondata != null) {
                String id = jsondata.optString(JsonKeyMqttMessageId, EMPTY_STRING + SYSTEM_MESSAGE);
                return Long.parseLong(id);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }        
        return SYSTEM_MESSAGE;
    }
}
