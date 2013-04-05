package com.bsb.hike.dto;

import com.bsb.hike.dto.MqttObjectModel.MessageType;

/**
 * @author Ankit Yadav
 * @author Sudheer Keshav Bhat
 */
public class ChatModel implements DataModel {

    public static interface MessageStatus {

        int SENDING = 0;
        int SENT = 1;
        int DELIVERED = 2;
        int RECEIVED = 3;
        int READ = 4;
    }
    private long conversationID;
    private String message;
    private int messageStatus;
    private long timestamp;
    private long messageID; // primary key auto increment
    private long mappedMsgId;
    private String toMsisdn;
    private String fromMsisdn;
    private MessageType type;
    private boolean invite; // volatile

    public ChatModel(long conversationID, String message, int messageStatus, long timestamp, long messageID, long mappedMsgId, String toMsisdn, String fromMsisdn, MessageType type) {
        this.conversationID = conversationID;
        this.message = message;
        this.messageStatus = messageStatus;
        this.timestamp = timestamp;
        this.messageID = messageID;
        this.mappedMsgId = mappedMsgId;
        this.toMsisdn = toMsisdn;
        this.fromMsisdn = fromMsisdn;
        this.type = type;
    }
    
    /**
     * @return the conversationID chat belongs to
     */
    public long getConversationID() {
        return conversationID;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the messageStatus of chat
     */
    public int getMessageStatus() {
        return messageStatus;
    }

    /**
     * @return the timestamp of message
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @return the messageID
     */
    public long getMessageID() {
        //TODO store nextMessageID
        return messageID;
    }

    /**
     * @return the mappedMsgId
     */
    public long getMappedMsgId() {
        return mappedMsgId;
    }

    /**
     * sets message status as s,d,r
     * @param messageStatus 
     */
    public void setMessageStatus(int messageStatus) {
        if (this.messageStatus < messageStatus) {
            this.messageStatus = messageStatus;
        }
    }

    /**
     * 
     * @return whether this chat created by user
     */
    public boolean isMe() {
        if (mappedMsgId == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return the to msisdn
     */
    public String getToMsisdn() {
        if(toMsisdn !=null && toMsisdn.equals(NULL_STRING)){
            toMsisdn = null;
        }
        return toMsisdn;
    }

    /**
     * 
     * @return the from msisdn
     */
    public String getFromMsisdn() {
        if(fromMsisdn !=null && fromMsisdn.equals(NULL_STRING)){
            fromMsisdn = null;
        }
        return fromMsisdn;
    }

    /**
     * @return the message type
     */
    public MessageType getType() {
        return type;
    }

    /**
     * @param type the message type to set
     */
    public void setType(MessageType type) {
        this.type = type;
    }    

    /**
     * @return whether the  invite
     */
    public boolean isInvite() {
        return invite;
    }

    /**
     * @param invite set message as invite
     */
    public void setInvite(boolean invite) {
        this.invite = invite;
    }
    

    public String toString() {
        return "ChatModel{conversationID:" + conversationID + ", message:" + message + ", messageStatus:" + messageStatus + ", timestamp:" + timestamp + ", messageID:"
                + messageID + ", mappedMsgId:" + mappedMsgId + ", toMsisdn:" + toMsisdn + ", fromMsisdn:" + fromMsisdn + ", type:" + type + "}";
    }
}
