package com.bsb.hike.io.mqtt;

import com.bsb.hike.mqtt.msg.Message;

/**
 *listener for mqtt events
 * @author ankit yadav
 */
public interface MqttListener {
    public void handleMessage(Message msg);
    public void handleFailure(Throwable t);
    public void connectionOpen();
    public void connectionClosed();
}
