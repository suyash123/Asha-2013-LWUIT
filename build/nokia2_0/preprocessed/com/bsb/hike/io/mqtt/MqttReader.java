/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.io.mqtt;

import com.bsb.hike.mqtt.msg.Message;
import com.bsb.hike.mqtt.msg.MessageInputStream;
import com.bsb.hike.mqtt.msg.PublishMessage;
import com.bsb.hike.mqtt.msg.QoS;
import com.bsb.hike.util.AppConstants;
import com.bsb.hike.util.Log;
import java.util.Date;

/**
 *
 * @author Ankit Yadav
 */
public class MqttReader extends Thread implements AppConstants {

    private static final String TAG = "MqttReader";
    private boolean stop = false;
    private MqttListenerImplementation listener;
    private MessageInputStream in;
    private MqttWriter writer;
    private final int MESSAGE_LIMIT = 5 ;
    private final long TIME_SPAN = 1000 * 1;
    private final long SLEEP_TIME = 1000 * 1;
    private long currentTime = new Date().getTime();
    private int messageCount = 0;

    public MqttReader(MessageInputStream in, MqttListenerImplementation listener, MqttWriter writer) {
        this.in = in;
        this.listener = listener;
        this.writer = writer;
    }

    /**
     * read mqtt messages
     */
    public void run() {
                
        Log.v(TAG, "Reader thread started");
        while (!stop) {
            if(messageCount % MESSAGE_LIMIT == 0 && new Date().getTime() - currentTime < TIME_SPAN){
                try{
                    Thread.sleep(SLEEP_TIME);
                    Log.v(TAG, "Reader thread sleeping to delay read");
                }catch (Exception ex){
                    
                }
            }else {
                Log.v(TAG, "reading data...");
                try {
                    if (in == null) {
                        listener.handleFailure(new Exception("Input Stream to read is null"));
                    }
                    Message msg = in.readMessage();
                    if (msg == null) {
                        continue;
                    }
                    messageCount++;
                    currentTime = new Date().getTime();
                    Log.v(TAG, "message recieved of type " + msg.getClass().getName());
                    listener.handleMessage(msg);
                    if (msg instanceof PublishMessage && msg.getQos() != QoS.AT_MOST_ONCE)  {

                        writer.acknowledge(((PublishMessage) msg).getMessageId());
                    }
                } catch (Exception e) {

                    Log.v(TAG, "exception in reader thread: " + e.getClass().getName());
                    listener.handleFailure(e);
                }
            }
        }
                 
        Log.v(TAG, "reader thread terminated.");
    }

    /**
     * request stop reader thread
     */
    public void requestStop() {
        stop = true;
    }
}
