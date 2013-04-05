package com.bsb.hike.dto;

import com.bsb.hike.dto.MqttObjectModel.MessageType;
import com.bsb.hike.util.AppConstants;
import com.bsb.hike.util.Log;
import java.util.Date;
import java.util.Vector;
import org.json.me.JSONArray;
import org.json.me.JSONObject;

/**
 * represents a mqtt message or invitation
 *
 * @author Ankit Yadav
 * @author Sudheer Keshav Bhat
 *
 */
public class MqttMessage implements AppConstants {

    private String messageId;
    private Date timeStamp;
    private String message;
    private MessageType type;
    private Vector dndNumbers;
    private static final String TAG = "MqttMessage";

    public MqttMessage(MessageType type, String messageId, String message, long timeStamp, Vector dndNumbers) {
        this.type = type;
        this.messageId = messageId;
        this.message = message;
        this.timeStamp = new Date(timeStamp);
        this.dndNumbers = dndNumbers;
    }

    public MqttMessage(JSONObject dataJson) {
        try {
            this.timeStamp = new Date(dataJson.getInt(JsonKeyMqttTimeStamp));
            this.messageId = dataJson.optString(JsonKeyMqttMessageId);
            this.type = dataJson.has(MessageType.SMS.toString()) ? MessageType.SMS : MessageType.HIKE;
            this.message = dataJson.getString(this.type.toString());
            JSONObject dndKey = dataJson.optJSONObject(JsonKeyMetaData);
            if (dndKey != null && dndKey.has(JsonKeyDndNumbers)) {
                JSONArray list = dndKey.getJSONArray(JsonKeyDndNumbers);
                if (list.length() > 0) {
                    dndNumbers = new Vector(list.length());
                    for (int i = 0; i < list.length(); i++) {
                        dndNumbers.addElement(list.getString(i));
                    }
                }
            }

        } catch (Exception jex) {
            Log.v(TAG, "exception while creating mqtt-message");
        }
    }

    /**
     * @return the messageId
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * @return the timeStamp
     */
    public Date getTimeStamp() {
        return timeStamp;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the type
     */
    public MessageType getType() {
        return type;
    }

    public String toString() {
        return "MqttMessage{messageId:" + messageId + ", timeStamp:" + timeStamp + ", message:" + message + ", type:" + type + "}";
    }

    /**
     * @return the dndNumbers
     */
    public Vector getDndNumbers() {
        return dndNumbers;
    }
}
