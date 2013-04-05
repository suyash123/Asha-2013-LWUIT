package com.bsb.hike.io.mqtt;

import com.bsb.hike.dto.AccountInfo;
import com.bsb.hike.dto.AppState;
import com.bsb.hike.dto.GroupMember;
import com.bsb.hike.dto.MqttMessage;
import com.bsb.hike.dto.MqttObjectModel;
import com.bsb.hike.dto.MqttObjectModel.MessageDirection;
import com.bsb.hike.dto.MqttObjectModel.MqttType;
import com.bsb.hike.dto.UserHikeStatus;
import com.bsb.hike.util.AppConstants;
import com.bsb.hike.util.Log;
import java.util.Enumeration;
import java.util.Hashtable;
import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import util.base64.Base64;

/**
 * utility class to covert Mqtt object to Json object and vice versa
 * @author Ankit Yadav
 * @author Sudheer Keshav Bhat
 */
public class MqttJsonWrapper implements AppConstants {

    private static final String TAG = "MqttJsonWrapper";

    /**
     * creates and return JSONObject from MqttObjectModel
     * @param object
     * @return
     * @throws JSONException 
     */
    public static JSONObject toJSON(MqttObjectModel object) throws JSONException {
        JSONObject message = new JSONObject();
        MqttType type = object.type;
        message.put(JsonKeyMqttType, type.toString());
        if (type == MqttType.MESSAGE || type == MqttType.INVITE) {
            MqttMessage data = (MqttMessage) object.data;
            message.put(MessageDirection.OUT.toString(), object.toMsisdn);
            JSONObject dataJson = new JSONObject();
            dataJson.put(JsonKeyMqttTimeStamp, data.getTimeStamp().getTime());
            dataJson.put(JsonKeyMqttMessageId, data.getMessageId());
            dataJson.put(data.getType().toString(), data.getMessage());
            message.put(JsonKeyMqttData, dataJson);
        } else if (type == MqttType.DELIVERY_REPORT) {
            // data is message id
            message.put(JsonKeyMqttData, object.data.toString());
        } else if (type == MqttType.START_TYPING || type == MqttType.END_TYPING) {
            message.put(MessageDirection.OUT.toString(), object.toMsisdn);
        } else if (type == MqttType.BLOCK_USER || type == MqttType.UNBLOCK_USER) {
            message.put(JsonKeyMqttData, object.data);
        } else if (type == MqttType.MESSAGE_READ) {
            message.put(JsonKeyMqttData, object.data);
            message.put(JsonKeyMqttTo, object.toMsisdn);
        } else if (type == MqttType.GROUP_CHAT_LEAVE) {
            message.put(JsonKeyMqttData, object.data.toString());
            message.put(MessageDirection.OUT.toString(), object.toMsisdn);
        } else if (type == MqttType.GROUP_CHAT_JOIN) {
            //data is hash table of user, key is msisdn and value is name
            Hashtable joinList = (Hashtable) object.data;
            JSONArray listJson = new JSONArray();
            Enumeration keys = joinList.keys();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement().toString();
                String value = ((GroupMember) joinList.get(key)).getName();
                JSONObject pair = new JSONObject();
                pair.put(JsonKeyMsisdn, key);
                pair.put(JsonKeyName, value);
                //onhike status not required to send
                //pair.put(JsonKeyOnHike, true);
                listJson.put(pair);
            }
            message.put(JsonKeyMqttData, listJson);
            message.put(MessageDirection.OUT.toString(), object.toMsisdn);
        } else {
            //default handle    
        }
        //assuming only sending cases are converted to json 
        if (object.isGroupChat()) {
            message.put(MessageDirection.IN.toString(), object.fromMsisdn);
        }
        // sms_credit, user_left, user_joined, invitee_joined, icon, group_chat_end,  group_chat_name, not sent by app so need not handle them 
        return message;
    }

    /**
     * creates parse and return mqttobject from json
     * @param json
     * @return
     * @throws JSONException 
     */
    public static MqttObjectModel toMqttObject(JSONObject json) throws JSONException {
        String typeString = json.getString(JsonKeyMqttType);
        //
        MqttType type = null;
        MqttType subType = null;
        Object data = null;
        String toMsisdn = null;
        String fromMsisdn = null;

        if (json.has(JsonKeyMqttFrom)) {
            fromMsisdn = json.getString(JsonKeyMqttFrom);
        }
        if (json.has(JsonKeyMqttTo)) {
            toMsisdn = json.getString(JsonKeyMqttTo);
        }

        if (MqttType.ACCOUNT_INFO.equals(typeString)) {
            type = MqttType.ACCOUNT_INFO;
            JSONObject dataObj = json.getJSONObject(JsonKeyMqttData);
            String token = dataObj.getString(JsonKeyInviteToken);
            String credit = dataObj.getString(JsonKeyTotalCredit);
            AccountInfo info = new AccountInfo(token, credit);
            AppState.getUserDetails().setAccountInfo(info);
            data = dataObj.getJSONObject(JsonKeyAccount);
            Log.v(TAG, "Account info: " + info);
        } else if (MqttType.ANALYTICS_EVENT.equals(typeString)) {
            type = MqttType.ANALYTICS_EVENT;
            //TODO
        } else if (MqttType.DELIVERY_REPORT.equals(typeString)) {
            type = MqttType.DELIVERY_REPORT;
            data = json.getString(JsonKeyMqttData);
        } else if (MqttType.END_TYPING.equals(typeString)) {
            type = MqttType.END_TYPING;
        } else if (MqttType.GROUP_CHAT_END.equals(typeString)) {
            type = MqttType.GROUP_CHAT_END;
        } else if (MqttType.GROUP_CHAT_JOIN.equals(typeString)) {
            type = MqttType.GROUP_CHAT_JOIN;
            Hashtable joinList = new Hashtable();
            JSONArray listJson = json.getJSONArray(JsonKeyMqttData);
                     
            Log.v(TAG, "recieved member list contains " + listJson.length());
            for (int i = 0; i < listJson.length(); i++) {
                         
                Log.v(TAG, "index--------->" + i);
                JSONObject entry = listJson.getJSONObject(i);
                String key = entry.getString(JsonKeyMsisdn);
                String value = entry.getString(JsonKeyName);
                boolean onHike = entry.getBoolean(JsonKeyOnHike);
                String dnd = entry.getString(JsonKeyDnD);
                GroupMember aMember = new GroupMember(key, value, true, onHike, dnd,  GroupMember.MemberSource.SENDER);
                joinList.put(key, aMember);
            }
                     
            Log.v(TAG, "size--------->" + joinList.size());
            data = joinList;
        } else if (MqttType.GROUP_CHAT_LEAVE.equals(typeString)) {
            type = MqttType.GROUP_CHAT_LEAVE;
            data = json.getString(JsonKeyMqttData);
            if(json.has(JsonKeyMqttSubType)){
                String subTypeString = json.getString(JsonKeyMqttSubType);
                if(MqttType.BIS.equals(subTypeString)){
                    subType = MqttType.BIS;
                }
            }
        } else if (MqttType.GROUP_CHAT_NAME.equals(typeString)) {
            type = MqttType.GROUP_CHAT_NAME;
            data = json.getString(JsonKeyMqttData);
        } else if (MqttType.ICON.equals(typeString)) {
            type = MqttType.ICON;
            String base64Icon = json.getString(JsonKeyMqttData);
            byte[] bytes = Base64.decode(base64Icon);
            data = new String(bytes);
        } else if (MqttType.INVITEE_JOINED.equals(typeString)) {
            type = MqttType.INVITEE_JOINED;
            data = json.getString(JsonKeyMqttData);
        } else if (MqttType.INVITE_INFO.equals(typeString)) {
            type = MqttType.INVITE_INFO;
            JSONObject jsonData = new JSONObject(json.getString(JsonKeyMqttData));
            data = jsonData.getString(JsonKeyTotalCredit);
        } else if (MqttType.MESSAGE.equals(typeString)) {
            type = MqttType.MESSAGE;
            data = new MqttMessage(json.getJSONObject(JsonKeyMqttData));
        } else if (MqttType.MESSAGE_READ.equals(typeString)) {
            type = MqttType.MESSAGE_READ;
            data = extractMsgIds(json);
        } else if (MqttType.REQUEST_ACCOUNT_INFO.equals(typeString)) {
            type = MqttType.REQUEST_ACCOUNT_INFO; //contains only type
        } else if (MqttType.SMS_CREDIT.equals(typeString)) {
            type = MqttType.SMS_CREDIT;
            data = json.getString(JsonKeyMqttData);
        } else if (MqttType.START_TYPING.equals(typeString)) {
            type = MqttType.START_TYPING;
        } else if (MqttType.UPDATE_AVAILABLE.equals(typeString)) {
            type = MqttType.UPDATE_AVAILABLE;
            //TODO
        } else if (MqttType.USER_JOINED.equals(typeString)) {
            type = MqttType.USER_JOINED;
            JSONObject dataObj = json.getJSONObject(JsonKeyMqttData);
            data = new UserHikeStatus(dataObj.getString(JsonKeyMsisdn), dataObj.optInt(JsonKeyCredit, 0), true);
        } else if (MqttType.USER_LEFT.equals(typeString)) {
            type = MqttType.USER_LEFT;
            JSONObject dataObj = json.getJSONObject(JsonKeyMqttData);
            data = new UserHikeStatus(dataObj.getString(JsonKeyMsisdn), dataObj.optInt(JsonKeyCredit, 0), false);
        } else if (MqttType.USER_OPTIN.equals(typeString)) {
            type = MqttType.USER_OPTIN;
            JSONObject dataObj = json.getJSONObject(JsonKeyMqttData);
            data = new UserHikeStatus(dataObj.getString(JsonKeyMsisdn), dataObj.optInt(JsonKeyCredit, 0), false);
        } else if (MqttType.BIS.equals(typeString)) {
            type = MqttType.BIS;            
        } else if (MqttType.ACCOUNT.equals(typeString)) {
            type = MqttType.ACCOUNT;    
            //returning data as json only, to avoid overwritting fields in case of absent keys
            data = json.getJSONObject(JsonKeyMqttData);
        }else if (MqttType.ACTION.equals(typeString)) {
            type = MqttType.ACTION;    
            data = json.getJSONObject(JsonKeyMqttData);
        } 
        else {
            //default handle
            type = MqttType.UNKNOWN;
        }
        MqttObjectModel message = new MqttObjectModel(type, data, toMsisdn, fromMsisdn, subType);

        //invite, block, unblock not recieved by app so need not handle them
        return message;
    }

    /**
     * creates and return mqttobject from json string
     * @param jsonString
     * @return
     * @throws JSONException 
     */
    public static MqttObjectModel toMqttObject(String jsonString) throws JSONException {
        return toMqttObject(new JSONObject(jsonString));
    }

    /**
     * @param json
     * @return extracts msgid from mqtt message/invite packet
     * @throws JSONException 
     */
    private static long[] extractMsgIds(JSONObject json) throws JSONException {
        JSONArray jsonArr = json.getJSONArray(JsonKeyMqttData);
        long[] msgIds = new long[jsonArr.length()];
        for (int i = 0; i < msgIds.length; i++) {
            msgIds[i] = Long.parseLong(jsonArr.getString(i));
        }
        return msgIds;
    }
}
