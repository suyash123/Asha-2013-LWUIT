/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.io.mqtt;

import com.bsb.hike.dto.AppState;
import com.bsb.hike.dto.MqttPendingMessageList;
import com.bsb.hike.dto.UserDetails;
import com.bsb.hike.mqtt.msg.ConnectMessage;
import com.bsb.hike.mqtt.msg.DisconnectMessage;
import com.bsb.hike.mqtt.msg.Message;
import com.bsb.hike.mqtt.msg.MessageOutputStream;
import com.bsb.hike.mqtt.msg.PingReqMessage;
import com.bsb.hike.mqtt.msg.PubAckMessage;
import com.bsb.hike.mqtt.msg.PublishMessage;
import com.bsb.hike.mqtt.msg.QoS;
import com.bsb.hike.mqtt.msg.RetryableMessage;
import com.bsb.hike.mqtt.msg.SubscribeMessage;
import com.bsb.hike.mqtt.msg.UnsubscribeMessage;
import com.bsb.hike.util.AppConstants;
import com.bsb.hike.util.Log;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Vector;

/**
 *this class writes the mqtt packets to server
 * @author Ankit Yadav
 */
public class MqttWriter implements AppConstants {

    private final static MqttPendingMessageList mPublishedMessageList = MqttPendingMessageList.getInstance();
    private final static Hashtable mMessageMap = new Hashtable();
    public final static Hashtable mMqttMessageMap/*short mqttId, long messageId*/ = new Hashtable();

    private static final String TAG = "MqttWriter";
    private MqttListener listener;
    private static Thread messageDispatcher;
    private static Thread messageAckDispatcher;
    private static final Vector ackQueue = new Vector();
    private static boolean stop = false;
    private MessageOutputStream out;
    private static short mqttId = 1;

    public MqttWriter(MqttListener listener) {
        this.listener = listener;
    }

    /**
     * 
     * @return the next mqttid
     */
    public static synchronized short getMqttId() {
        
        Log.v(TAG, "mqtt id is " + mqttId);
        if (mqttId < 1) {
            mqttId = 1;
        }
        return mqttId++;
    }

    /**
     * connect request to mqtt server
     * @param out 
     */
    public void connect(MessageOutputStream out) {
        final UserDetails user = AppState.getUserDetails();
        if (user == null) {
                     
            Log.v(TAG, "User is null");
            return;
        }
        if (out == null) {
            listener.handleFailure(new Exception());
        } else {
            this.out = out;
        }

        ConnectMessage msg = new ConnectMessage(user.getMsisdn() + ":1", false, (byte) 60);
        msg.setCredentials(user.getUid(), user.getToken());
        try {
            out.writeMessage(msg);
            stop = false;
        } catch (Exception ex) {
                     
            Log.v(TAG, "connect message could not succeed");
            listener.handleFailure(ex);
        }
    }

    /**
     * sends ping request to mqtt server
     */
    public void ping() {
        new Thread(){
            public void run() {
                try {
                out.writeMessage(new PingReqMessage());
            } catch (Exception ex) {
                
                Log.v(TAG, "error writing message to mqtt server: " + ex.getMessage());
                listener.handleFailure(ex);
            }
            }
        }.start();    
    }

    /**
     * publish message to mqtt server
     * @param topic
     * @param message
     * @param qos
     * @param id 
     */
    public void publish(String topic, String message, QoS qos, long id) {
        try {
            PublishMessage msg = new PublishMessage(topic, message.getBytes(TEXT_ENCODING), qos);
            submitMessage(msg);
            if(id != SYSTEM_MESSAGE) {
                mMqttMessageMap.put(EMPTY_STRING + msg.getMessageId(), EMPTY_STRING + id);
            }
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * send acknowledgement for given id
     * @param messageId 
     */
    public void acknowledge(short messageId) {
        
        Log.v(TAG, "acknowledging message id: " + messageId);
        ackQueue.addElement(new PubAckMessage(messageId));
        
        if (messageAckDispatcher == null || !messageAckDispatcher.isAlive()) {
            
            Log.v(TAG, "ack dispatcher running");
            messageAckDispatcher = new Thread() {
                public void run() {
                    
                    Log.v(TAG, "ack dispatcher started");
                    while (!ackQueue.isEmpty() && !stop) {
                        
                        Log.v(TAG, "ack dispatcher has something to post");
                        try {
                            PubAckMessage message = (PubAckMessage) ackQueue.firstElement();
                            out.writeMessage(message);
                            
                            Log.v(TAG, "ack posted by dispatcher for msg id: " + message.getMessageId());
                            ackQueue.removeElement(message);
                        } catch (Exception ex) {
                            
                            Log.v(TAG, "error writing ack to mqtt server: " + ex.getMessage());
                            listener.handleFailure(ex);
                        }
                    }
                    
                    Log.v(TAG, "ack dispatcher ended");
                }
            };
            messageAckDispatcher.start();
        }
    }

    /**
     * send subscription request to given topic
     * @param topic
     * @param qos 
     */
    public void subscribe(String topic, QoS qos) {
        SubscribeMessage msg = new SubscribeMessage(topic, qos);
        submitMessage(msg);
    }

    /**
     * send unsubscription request to given topic
     * @param topic 
     */
    public void unsubscribe(String topic) {
        UnsubscribeMessage msg = new UnsubscribeMessage(topic);        
        submitMessage(msg);
    }

    /**
     * send disconnect request to mqtt server
     */
    public void disconnect() {
        DisconnectMessage msg = new DisconnectMessage();
        submitMessage(msg);
    }

    /**
     * submit publish messages to write queue
     * @param message 
     */
    private void submitMessage(Message message) {
        if (!mPublishedMessageList.contains(message)) {
            if((message instanceof PublishMessage || message instanceof SubscribeMessage || message instanceof UnsubscribeMessage) && message.getQos() != QoS.AT_MOST_ONCE){
                ((RetryableMessage)message).setMessageId(getMqttId());
            }
            mPublishedMessageList.addElement(message);
        }
        processSendQueue();
    }

    /**
     * start processing pending messages
     */
    public void processSendQueue() {
        if (messageDispatcher == null || !messageDispatcher.isAlive()) {
                     
            Log.v(TAG, "message dispatcher running");
            messageDispatcher = new Thread() {
                public void run() {
                             
                    Log.v(TAG, "message dispatcher started");
                    while (!mPublishedMessageList.isEmpty() && !stop) {
                                 
                        Log.v(TAG, "message dispatcher has something to post");
                        try {
                            Message message = (Message) mPublishedMessageList.firstElement();
                            if (message == null) {
                                continue;
                            }
                            if(message instanceof PublishMessage){                                         
                                Log.v(TAG, "message dispatcher has publish message " + ((PublishMessage)message).getDataAsString());
                            }
                            RetryableMessage pubMsg = (RetryableMessage) message;
                            mMessageMap.put(EMPTY_STRING + pubMsg.getMessageId(), pubMsg);
                            out.writeMessage(message);
                                     
                            Log.v(TAG, "message posted by dispatcher" + message.getClass().getName() + " message: " + message);
                            mPublishedMessageList.removeElement(message);
                        } catch (Exception ex) {                                   
                            Log.v(TAG, "error writing message to mqtt server: " + ex.getMessage());
                            listener.handleFailure(ex);
                        }
                    }
                             
                    Log.v(TAG, "message dispatcher ended");
                }
            };
            messageDispatcher.start();
        }
    }

    /**
     * remove acknowledged messages from queue
     * @param msg
     * @return 
     */
    public long removeMsgFromSentQueue(Message msg) {
        if (msg instanceof RetryableMessage) {
            RetryableMessage retMsg = (RetryableMessage) msg;
            
            Log.v(TAG, "removing ACKed messages from sent queue");
            if (mMessageMap.containsKey(EMPTY_STRING + retMsg.getMessageId())) {
                Object value = mMessageMap.get(EMPTY_STRING + retMsg.getMessageId());
                boolean removed;
                Log.v(TAG, "obj to remove: " + value);
                if (value != null && value instanceof PublishMessage) {
                    short msgIdString = ((PublishMessage) value).getMessageId();
                    if(mMqttMessageMap.containsKey(EMPTY_STRING + msgIdString)){
                        long msgId = Long.parseLong(mMqttMessageMap.get(EMPTY_STRING + msgIdString).toString());
                        
                        Log.v(TAG, "msgId: " + msgId);
                        removed = mPublishedMessageList.removeElement(mMessageMap.get(EMPTY_STRING + msgId));
                        
                        Log.v(TAG, "PublishedMessageList ACK removed: " + removed);
                        return msgId;
                    }
                }
            }
        }
        return SYSTEM_MESSAGE;
    }

    /**
     * request stop writter thread
     */
    public void requestStop() {
        stop = true;
    }
}
