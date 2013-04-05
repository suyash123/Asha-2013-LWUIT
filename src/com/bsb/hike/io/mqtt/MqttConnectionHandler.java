package com.bsb.hike.io.mqtt;

import com.bsb.hike.mqtt.msg.MessageInputStream;
import com.bsb.hike.mqtt.msg.MessageOutputStream;
import com.bsb.hike.mqtt.msg.QoS;
import com.bsb.hike.util.AppConstants;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

/**
 * mqtt connection handler
 * @author Ankit Yadav
 */
public class MqttConnectionHandler implements AppConstants {

    private MessageInputStream in;
    private StreamConnection socket;
    private MessageOutputStream out;
    private InputStream is;
    private OutputStream os;
    private final static MqttConnectionHandler handler = new MqttConnectionHandler();
    private final static MqttListenerImplementation listener = new MqttListenerImplementation(handler);
    private static final String TAG = "MqttConnectionHandler";
    private boolean firstConnctionAfterRegistration = false;

    /**
     * 
     * @return singleton instance of MqttConnectionHandler
     */
    public static MqttConnectionHandler getMqttConnectionHandler() {
        return handler;
    }
    
    /**
     * 
     * @return check whether its 1st connection after registration
     */
    public boolean isfirstConnctionAfterRegistration(){
        return firstConnctionAfterRegistration;
    }
    
    /**
     * setter for flog of 1st connection after registration
     * @param firstConnctionAfterRegistration 
     */
    public void setfirstConnctionAfterRegistration(boolean firstConnctionAfterRegistration){
        this.firstConnctionAfterRegistration = firstConnctionAfterRegistration;
    }

    /**
     * async open request for mqtt connection
     */
    public void open() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    socket = (StreamConnection) Connector.open(MqttBase);

                    is = socket.openInputStream();
                    in = new MessageInputStream(is);

                    os = socket.openOutputStream();
                    out = new MessageOutputStream(os);

                    listener.connectionOpen();
                } catch (Exception ex) {
                    listener.handleFailure(ex);
                }
            }
        }).start();
    }

    /**
     * async close request for mqtt connection
     */
    public void close() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    in.close();
                    is.close();
                    os.close();
                    socket.close();
                    //listener.connectionClosed();
                } catch (Exception ex) {
                    //listener.connectionError(ex);
                } finally {
                    listener.connectionClosed();
                }
            }
        }).start();
    }

    /**
     * 
     * @return the MessageInputStream
     */
    public MessageInputStream getMessageInputStream() {
        return in;
    }

    /**
     * 
     * @return MessageOutputStream
     */
    public MessageOutputStream getMessageOutputStream() {
        return out;
    }

    /**
     * publish mqtt request, just a wrapper for writter's publish
     * @param topic
     * @param message
     * @param qos 
     */
    public void publish(String topic, String message, QoS qos) {
        this.publish(topic, message, qos, SYSTEM_MESSAGE);
    }
    
    /**
     * publish mqtt request, just a wrapper for writter's publish
     * @param topic
     * @param message
     * @param qos
     * @param id 
     */
    public void publish(String topic, String message, QoS qos, long id) {
        listener.getWriter().publish(topic, message, qos, id);
    }
}