package com.bsb.hike.util;

import com.bsb.hike.dto.AddressBookEntry;
import com.bsb.hike.dto.AddressBookList;
import com.bsb.hike.dto.ChatList;
import com.bsb.hike.dto.ChatModel;
import com.bsb.hike.dto.ContactAvatar;
import com.bsb.hike.dto.ContactAvatarList;
import com.bsb.hike.dto.ConversationList;
import com.bsb.hike.dto.ConversationModel;
import com.bsb.hike.dto.DataCollection;
import com.bsb.hike.dto.GroupList;
import com.bsb.hike.dto.GroupMember;
import com.bsb.hike.dto.GroupMembers;
import com.bsb.hike.dto.MqttObjectModel.MessageType;
import com.bsb.hike.dto.MqttPendingMessageList;
import com.bsb.hike.io.mqtt.MqttWriter;
import com.bsb.hike.mqtt.msg.Message;
import com.bsb.hike.mqtt.msg.PublishMessage;
import com.bsb.hike.mqtt.msg.QoS;
import java.util.Enumeration;
import java.util.Hashtable;
import org.json.me.JSONArray;
import org.json.me.JSONObject;

public class DataCollectionJsonWrapper implements AppConstants {

    private static final String TAG = "DataCollectionJsonWrapper";

    /**
     * creates jsonobjects from given data collection
     * @param list
     * @return 
     */
    public static JSONObject toJsonArray(DataCollection list) {
        if (list == null) {
            return null;
        }
        JSONObject data = new JSONObject();
        JSONArray mainList = new JSONArray();
        try {

            if (list instanceof AddressBookList) {
                AddressBookList addressList = (AddressBookList) list;
                Enumeration enums = addressList.elements();
                while (enums.hasMoreElements()) {
                    try {
                        JSONArray record = new JSONArray();
                        AddressBookEntry addressEntry = (AddressBookEntry) enums.nextElement();
                        record.put(addressEntry.getId());
                        record.put(addressEntry.getName());
                        record.put(addressEntry.getMsisdn());
                        record.put(addressEntry.getPhoneNumber());
                        record.put(addressEntry.isOnHike());
                        mainList.put(record);
                    } catch (Exception ex) {
                        Log.v(TAG, "exception in toJsonArray(list)::: AddressBookList: " + ex.getClass().getName());
                    }
                }
                data.putOpt(DataBaseAddress, mainList);
            } else if (list instanceof ConversationList) {
                ConversationList convList = (ConversationList) list;
                Enumeration enums = convList.elements();
                while (enums.hasMoreElements()) {
                    try {
                        JSONObject record = new JSONObject();
                        JSONArray value = new JSONArray();
                        ConversationModel convEntry = (ConversationModel) enums.nextElement();
                        value.put(convEntry.getMsisdn());
                        value.put(convEntry.isOnHike());
                        value.put(convEntry.getMsgId());
                        record.put(EMPTY_STRING + convEntry.getConversationID(), value);
                        mainList.put(record);
                    } catch (Exception ex) {
                        Log.v(TAG, "exception in toJsonArray(list)::: ConversationList: " + ex.getClass().getName());
                    }
                }
                data.putOpt(DataBaseConversation, mainList);
            } else if (list instanceof ChatList) {
                ChatList convList = (ChatList) list;
                Enumeration enums = convList.elements();
                while (enums.hasMoreElements()) {
                    try {
                        JSONObject record = new JSONObject();
                        JSONArray value = new JSONArray();
                        ChatModel convEntry = (ChatModel) enums.nextElement();
                        value.put(convEntry.getConversationID());
                        value.put(convEntry.getMessage());
                        value.put(convEntry.getMessageStatus());
                        value.put(convEntry.getTimestamp());
                        value.put(convEntry.getMappedMsgId());
                        value.put(convEntry.getToMsisdn());
                        value.put(convEntry.getFromMsisdn());
                        value.put(convEntry.getType());
                        record.put(EMPTY_STRING + convEntry.getMessageID(), value);
                        mainList.put(record);
                    } catch (Exception ex) {
                        Log.v(TAG, "exception in toJsonArray(list)::: ChatList: " + ex.getClass().getName());
                    }
                }
                data.putOpt(DataBaseChat, mainList);
            } else if (list instanceof ContactAvatarList) {
                ContactAvatarList avatarList = (ContactAvatarList) list;
                Enumeration enums = avatarList.elements();
                while (enums.hasMoreElements()) {
                    try {
                        ContactAvatar avatarEntry = (ContactAvatar) enums.nextElement();
                        JSONObject entry = new JSONObject();
                        entry.putOpt(avatarEntry.getMsisdn(), avatarEntry.getThumb());
                        mainList.put(entry);
                    } catch (Exception ex) {
                        Log.v(TAG, "exception in toJsonArray(list)::: ContactAvatarList: " + ex.getClass().getName());
                    }
                }
                data.putOpt(DataBaseThumbs, mainList);
            } else if (list instanceof GroupList) {
                GroupList groupList = (GroupList) list;
                Enumeration enumslist = groupList.elements();
                while (enumslist.hasMoreElements()) {
                    try {
                        GroupMembers avatarEntry = (GroupMembers) enumslist.nextElement();
                        JSONObject entry = new JSONObject();
                        //creator msisdn, name, members-json
                        JSONArray groupInfo = new JSONArray();
                        groupInfo.put(avatarEntry.getCreaterMsisdn());
                        groupInfo.put(avatarEntry.getGroupName());
                        groupInfo.put(avatarEntry.isChatAlive());
                        //list of members
                        JSONArray membersJson = new JSONArray();
                        Hashtable members = avatarEntry.getMembers();
                        Enumeration enums = members.keys();
                        while (enums.hasMoreElements()) {
                            String key = enums.nextElement().toString();
                            GroupMember value = (GroupMember) members.get(key);
                            JSONArray amember = new JSONArray();
                            amember.put(value.getMsisdn()); // value.getMsisdn() = key
                            amember.put(value.getName());
                            amember.put(value.isActive());
                            amember.put(value.isOnHike());
                            amember.put(value.isDnD());
                            membersJson.put(amember);
                        }
                        groupInfo.put(membersJson);
                        entry.put(avatarEntry.getGroupId(), groupInfo);
                        mainList.put(entry);
                    } catch (Exception ex) {
                        Log.v(TAG, "exception in toJsonArray(list)::: GroupList: " + ex.getClass().getName());
                    }
                }
                data.putOpt(DataBaseGroups, mainList);
            } else if (list instanceof MqttPendingMessageList) {
                MqttPendingMessageList pendingList = (MqttPendingMessageList) list;
                Log.v(TAG, "MqttPendingMessageList: " + pendingList.size());
                Enumeration enums = pendingList.elements();
                while (enums.hasMoreElements()) {
                    try {
                        Message message = (Message) enums.nextElement();
                        if (message instanceof PublishMessage && message.getQos() != QoS.AT_MOST_ONCE) {
                            PublishMessage msg = (PublishMessage) message;
                            byte[] messagebytes = msg.getData();
                            String messageString = new String(messagebytes, TEXT_ENCODING);
                            mainList.put(messageString);
                        }
                    } catch (Exception ex) {
                        Log.v(TAG, "exception in toJsonArray(list)::: MqttPendingMessageList: " + ex.getClass().getName());
                    }
                }
                data.putOpt(DataBasePendingMessages, mainList);
            }
        } catch (Exception ex) {
            Log.v(TAG, "exception in toJsonArray(list) " + ex.getClass().getName());
        }
        return data;
    }

    /**
     * creates jsonobjects from given addressbook for given range
     * @param list
     * @param offset
     * @param range
     * @return 
     */
    public static JSONObject toJsonArray(AddressBookList list, int offset, int range) {
        JSONObject data = new JSONObject();
        JSONArray mainList = new JSONArray();
        try {
            if (list != null) {
                int size = Math.min(offset + range, list.size());
                for (int i = offset; i < size; i++) {
                    try {
                        JSONArray record = new JSONArray();
                        AddressBookEntry addressEntry = (AddressBookEntry) list.elementAt(i);
                        record.put(addressEntry.getId());
                        record.put(addressEntry.getName());
                        record.put(addressEntry.getMsisdn());
                        record.put(addressEntry.getPhoneNumber());
                        record.put(addressEntry.isOnHike());
                        mainList.put(record);
                    } catch (Exception ex) {
                        Log.v(TAG, "exception in toJsonArray(list, offset, range)-innerloop: " + ex.getClass().getName());
                    }
                }
                data.putOpt(DataBaseAddress, mainList);
            }
        } catch (Exception ex) {
            Log.v(TAG, "exception in toJsonArray(list, offset, range) " + ex.getClass().getName());
        }
        return data;
    }

    /**
     * creates JSONArray from given AddressBookEntry
     * @param addressEntry
     * @return 
     */
    public static JSONArray toJsonArray(AddressBookEntry addressEntry) {
        JSONArray record = new JSONArray();
        record.put(addressEntry.getId());
        record.put(addressEntry.getName());
        record.put(addressEntry.getMsisdn());
        record.put(addressEntry.getPhoneNumber());
        record.put(addressEntry.isOnHike());
        return record;
    }

    /**
     * creates data collection from given jsonobjects
     * @param json
     * @return 
     */
    public static DataCollection toDataCollection(JSONObject json) {
        if (json == null || json.names() == null || json.names().length() < 1) {
            return null;
        }

        try {
            String key = json.names().getString(0);
            JSONArray array = json.getJSONArray(key);
            if (key.equals(DataBaseAddress)) {
                AddressBookList list = AddressBookList.getInstance();
                list.removeAllElements();
                list.setSortOnChange(false);
                for (int i = 0; i < array.length(); i++) {
                    try {
                        JSONArray record = array.getJSONArray(i);
                        Log.v(TAG, "id: " + record.getString(0));
                        Log.v(TAG, "phone: " + record.getString(3));
                        Log.v(TAG, "name: " + record.getString(1));
                        Log.v(TAG, "msisdn: " + record.getString(2));
                        Log.v(TAG, "onhike" + record.getBoolean(4));
                        AddressBookEntry entry = new AddressBookEntry(record.getString(0), record.getString(3), record.getString(1), record.getString(2), record.getBoolean(4));
                        list.addElement(entry);
                    } catch (Exception ex) {
                        Log.v(TAG, "Exception in toDataCollection()::: DataBaseAddress: " + ex.getClass().getName());
                    }
                }
                list.sort();
                list.setSortOnChange(true);
                return list;
            } else if (key.equals(DataBaseConversation)) {
                ConversationList list = ConversationList.getInstance();
                list.removeAllElements();
                for (int i = 0; i < array.length(); i++) {
                    try {
                        JSONObject oneObject = array.getJSONObject(i);
                        String convID = oneObject.keys().nextElement().toString();
                        JSONArray conversation = oneObject.getJSONArray(convID);
                        Log.v(TAG, "convid: " + Long.parseLong(convID));
                        Log.v(TAG, "msisdn: " + conversation.getString(0));
                        Log.v(TAG, "msgid: " + conversation.getString(2));
                        Log.v(TAG, "onhike: " + conversation.getBoolean(1));
                        ConversationModel model = new ConversationModel(Long.parseLong(convID), conversation.getString(0), Long.parseLong(conversation.getString(2)));
                        model.setOnHike(conversation.getBoolean(1));
                        list.addElement(model);
                    } catch (Exception ex) {
                        Log.v(TAG, "Exception in toDataCollection()::: DataBaseConversation: " + ex.getClass().getName());
                    }
                }
                return list;
            } else if (key.equals(DataBaseChat)) {
                ChatList list = ChatList.getInstance();
                list.removeAllElements();
                for (int i = 0; i < array.length(); i++) {
                    try {
                        JSONObject oneObject = array.getJSONObject(i);
                        String chatID = oneObject.keys().nextElement().toString();
                        JSONArray chat = oneObject.getJSONArray(chatID);
                        Log.v(TAG, "convid: " + Long.parseLong(chat.getString(0)));
                        Log.v(TAG, "msg: " + chat.getString(1));
                        Log.v(TAG, "msgstatus: " + Integer.parseInt(chat.getString(2)));
                        Log.v(TAG, "timestamp: " + Long.parseLong(chat.getString(3)));
                        Log.v(TAG, "msgid: " + Long.parseLong(chatID));
                        Log.v(TAG, "mappedmsgid: " + Long.parseLong(chat.getString(4)));
                        Log.v(TAG, "tomsisdn: " + chat.getString(5));
                        Log.v(TAG, "frommsisdn: " + chat.getString(6));
                        Log.v(TAG, "sent as: " + chat.getString(7));

                        String typeString = chat.getString(7);
                        MessageType type = typeString.equals(MessageType.HIKE.toString()) ? MessageType.HIKE : typeString.equals(MessageType.SMS.toString()) ? MessageType.SMS : MessageType.SYSTEM;
                        ChatModel model = new ChatModel(Long.parseLong(chat.getString(0)), chat.getString(1), Integer.parseInt(chat.getString(2)), Long.parseLong(chat.getString(3)), Long.parseLong(chatID), Long.parseLong(chat.getString(4)), chat.getString(5), chat.getString(6), type);
                        list.addElement(model);
                    } catch (Exception ex) {
                        Log.v(TAG, "Exception in toDataCollection()::: DataBaseChat: " + ex.getClass().getName());
                    }
                }
                return list;
            } else if (key.equals(DataBaseThumbs)) {
                ContactAvatarList list = ContactAvatarList.getInstance();
                list.removeAllElements();
                for (int i = 0; i < array.length(); i++) {
                    try {
                        JSONObject oneObject = array.getJSONObject(i);
                        String msisdn = oneObject.keys().nextElement().toString();
                        String thumb = oneObject.getString(msisdn);
                        ContactAvatar avatar = new ContactAvatar(msisdn, thumb);
                        list.addElement(avatar);
                    } catch (Exception ex) {
                        Log.v(TAG, "Exception in toDataCollection()::: DataBaseThumbs: " + ex.getClass().getName());
                    }
                }
                return list;
            } else if (key.equals(DataBaseGroups)) {
                GroupList list = GroupList.getInstance();
                list.removeAllElements();
                for (int i = 0; i < array.length(); i++) {
                    try {
                        JSONObject oneObject = array.getJSONObject(i);
                        String groupId = oneObject.keys().nextElement().toString();
                        Log.v(TAG, "groupId: " + groupId);
                        JSONArray details = oneObject.getJSONArray(groupId);
                        String creatorMsisdn = details.getString(0);
                        Log.v(TAG, "creatorMsisdn: " + creatorMsisdn);
                        String name = details.optString(1, EMPTY_STRING);
                        Log.v(TAG, "name: " + name);
                        boolean alive = details.optBoolean(2, true);
                        Log.v(TAG, "alive: " + alive);
                        JSONArray members = details.getJSONArray(3);
                        Log.v(TAG, "members: " + members.length());
                        Hashtable table = new Hashtable();
                        for (int j = 0; j < members.length(); j++) {
                            try {
                                JSONArray aMember = members.getJSONArray(j);
                                Log.v(TAG, "memberMsisdn: " + aMember.getString(0));
                                Log.v(TAG, "memberName: " + aMember.getString(1));
                                Log.v(TAG, "memberActive: " + aMember.getBoolean(2));
                                Log.v(TAG, "memberOnHike: " + aMember.getBoolean(2));
                                Log.v(TAG, "memberDnD: " + aMember.getBoolean(2));

                                String memberMsisdn = aMember.getString(0);
                                String memberName = aMember.getString(1);
                                boolean memberActive = aMember.getBoolean(2);
                                boolean memberOnHike = aMember.getBoolean(3);
                                String memberDnD = aMember.getString(4);

                                GroupMember member = new GroupMember(memberMsisdn, memberName, memberActive, memberOnHike, memberDnD, GroupMember.MemberSource.RMS);
                                table.put(memberMsisdn, member);
                            } catch (Exception ex) {
                                Log.v(TAG, "Exception in toDataCollection()::: DataBaseGroupMembers: " + ex.getClass().getName());
                            }
                        }
                        GroupMembers group = new GroupMembers(groupId, creatorMsisdn);
                        group.setGroupName(name);
                        group.addMembers(table);
                        group.setChatAlive(alive);
                        list.addElement(group);
                    } catch (Exception ex) {
                        Log.v(TAG, "Exception in toDataCollection()::: DataBaseGroups: " + ex.getClass().getName());
                    }
                }
                return list;
            } else if (key.equals(DataBasePendingMessages)) {
                MqttPendingMessageList list = MqttPendingMessageList.getInstance();
                list.removeAllElements();
                for (int i = 0; i < array.length(); i++) {
                    try {
                        String oneObject = array.getString(i);
                        Log.v(TAG, "Message from RMS: " + oneObject);
                        PublishMessage msg = new PublishMessage(Topics.PUBLISH, oneObject, QoS.AT_LEAST_ONCE);
                        msg.setMessageId(MqttWriter.getMqttId());
                        long id = ModelUtils.getMessageIdfromMqttJson(oneObject);
                        if(id != SYSTEM_MESSAGE) {
                            MqttWriter.mMqttMessageMap.put(EMPTY_STRING + msg.getMessageId(), EMPTY_STRING + id);
                        }
                        Log.v(TAG, "Message as publish: " + msg.getDataAsString());
                        list.addElement(msg);
                    } catch (Exception ex) {
                        Log.v(TAG, "Exception in toDataCollection()::: DataBaseGroups: " + ex.getClass().getName());
                    }
                }
                return list;
            }
        } catch (Exception ex) {
            Log.v(TAG, "Exception in toDataCollection()" + ex.getClass().getName());
        }
        return null;
    }

    /**
     * parses json object to get contacts and adds it to AddressbookList
     * @param json 
     */
    public static void toAddressBookList(JSONObject json) {
        if (json == null || json.names() == null || json.names().length() < 1) {
            return;
        }
        Log.v(TAG, "json to addresbook conversion started");
        try {
            String key = json.names().getString(0);
            JSONArray array = json.getJSONArray(key);
            for (int i = 0; i < array.length(); i++) {
                try {
                    JSONArray record = array.getJSONArray(i);
                    Log.v(TAG, "id: " + record.getString(0));
                    Log.v(TAG, "phone: " + record.getString(3));
                    Log.v(TAG, "name: " + record.getString(1));
                    Log.v(TAG, "msisdn: " + record.getString(2));
                    Log.v(TAG, "onhike" + record.getBoolean(4));
                    AddressBookEntry entry = new AddressBookEntry(record.getString(0), record.getString(3), record.getString(1), record.getString(2), record.getBoolean(4));
                    AddressBookList.getInstance().addElement(entry);
                } catch (Exception ex) {
                    Log.v(TAG, "exception while merging addresbook-innerloop: " + ex.getClass().getName());
                }
            }
        } catch (Exception ex) {
            Log.v(TAG, "exception while merging addresbook: " + ex.getClass().getName());
        }
    }

    /**
     * parses json object to get contacts and adds it to AddressbookList
     * @param json 
     */
    public static void mergeAddressBookList(JSONObject json) {
        if (json == null || json.names() == null || json.names().length() < 1) {
            return;
        }
        Log.v(TAG, "merging addresbook started");
        try {
            String key = json.names().getString(0);
            JSONArray array = json.getJSONArray(key);
            for (int i = 0; i < array.length(); i++) {
                try {
                    JSONArray record = array.getJSONArray(i);
                    Log.v(TAG, "id: " + record.getString(0));
                    Log.v(TAG, "onhike: " + record.getBoolean(4));

                    Enumeration enums = AddressBookList.getInstance().elements();
                    while (enums.hasMoreElements()) {
                        AddressBookEntry entry = (AddressBookEntry) enums.nextElement();
                        if (entry.getId().equals(record.getString(0))) {
                            entry.setOnHike(record.getBoolean(4));
                        }
                    }
                } catch (Exception ex) {
                    Log.v(TAG, "exception while merging addresbook-innerloop: " + ex.getClass().getName());
                }
            }
        } catch (Exception ex) {
            Log.v(TAG, "exception while merging addresbook: " + ex.getClass().getName());
        }
        Log.v(TAG, "merging addresbook completed");
    }
}