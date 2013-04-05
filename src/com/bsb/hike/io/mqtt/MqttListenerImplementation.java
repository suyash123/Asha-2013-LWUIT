package com.bsb.hike.io.mqtt;

import com.bsb.hike.dto.AddressBookEntry;
import com.bsb.hike.dto.AddressBookList;
import com.bsb.hike.dto.AppState;
import com.bsb.hike.dto.ChatModel.MessageStatus;
import com.bsb.hike.dto.ConversationList;
import com.bsb.hike.dto.ConversationModel;
import com.bsb.hike.dto.MqttMessage;
import com.bsb.hike.dto.MqttObjectModel;
import com.bsb.hike.dto.MqttObjectModel.MessageType;
import com.bsb.hike.dto.MqttObjectModel.MqttType;
import com.bsb.hike.dto.UserHikeStatus;
import com.bsb.hike.mqtt.msg.ConnAckMessage;
import com.bsb.hike.mqtt.msg.Message;
import com.bsb.hike.mqtt.msg.PingRespMessage;
import com.bsb.hike.mqtt.msg.PubAckMessage;
import com.bsb.hike.mqtt.msg.PubCompMessage;
import com.bsb.hike.mqtt.msg.PubRecMessage;
import com.bsb.hike.mqtt.msg.PubRelMessage;
import com.bsb.hike.mqtt.msg.PublishMessage;
import com.bsb.hike.mqtt.msg.QoS;
import com.bsb.hike.mqtt.msg.SubAckMessage;
import com.bsb.hike.mqtt.msg.UnsubAckMessage;
import com.bsb.hike.ui.FormChatThread;
import com.bsb.hike.util.AppConstants;
import com.bsb.hike.util.Log;
import com.bsb.hike.util.ModelUtils;
import com.bsb.hike.util.TextUtils;
import com.sun.lwuit.Display;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import org.json.me.JSONException;

/**
 *implementaion of mqtt listener
 * @author Ankit Yadav
 * @author Sudheer Keshav Bhat
 */
public class MqttListenerImplementation implements MqttListener, AppConstants {

    private static final String TAG = "MqttListenerImplementation";
    private MqttConnectionHandler handler;
    private MqttReader reader;
    private final MqttWriter writer;
    private static Thread retryThread;
    private final Timer pingTimer = new Timer();
    private TimerTask pingTask;
    private final Timer pingAckTimer = new Timer();
    private TimerTask pingAckTask;

    public MqttListenerImplementation(MqttConnectionHandler handler) {
        this.handler = handler;
        writer = new MqttWriter(this);
    }

    public void handleMessage(Message msg) {
        long msgId = writer.removeMsgFromSentQueue(msg);
                
        Log.v(TAG, msg.toString());
        switch (msg.getType()) {
            case Message.Type.CONNACK:
                handleConnAck((ConnAckMessage) msg);
                break;
            case Message.Type.PINGRESP:
                handlePingResponse((PingRespMessage) msg);
                break;
            case Message.Type.PUBACK:
                handlePubAck((PubAckMessage) msg, msgId);
                break;
            case Message.Type.PUBCOMP:
                handlePubComp((PubCompMessage) msg);
                break;
            case Message.Type.PUBLISH:
                handlePublishMessage((PublishMessage) msg);
                break;
            case Message.Type.PUBREC:
                handlePubRec((PubRecMessage) msg);
                break;
            case Message.Type.PUBREL:
                handlePubRel((PubRelMessage) msg);
                break;
            case Message.Type.SUBACK:
                handleSubAck((SubAckMessage) msg);
                break;
            case Message.Type.UNSUBACK:
                handleUnSubAck((UnsubAckMessage) msg);
                break;
            default:
                break;
        }
    }

    private void handlePubRel(PubRelMessage msg) {
    }

    private void handlePubRec(PubRecMessage msg) {
    }

    private void handlePubComp(PubCompMessage msg) {
    }

    public void handleFailure(Throwable t) {
                 
        Log.v(TAG, "Socket Failure From Server: " + t.getClass().getName());
        if (reader != null) {
            reader.requestStop();
        }
        if (writer != null) {
            writer.requestStop();
        }
        if(pingTask !=null){
            pingTask.cancel();
        }

        if (!(t instanceof SecurityException)) {
            if(retryThread == null || !retryThread.isAlive()){
                retryThread = new ConnectionRetryThread();
                retryThread.start();
            }
        }
    }
    
    private class ConnectionRetryThread extends Thread {
                public void run() {
                    try {
                        Thread.sleep(MQTT_RETRY_TIMEOUT);
                    } catch (Exception ex) {
                                 
                        Log.v(TAG, "retry: " + ex.getClass().getName());
                    } finally {
                        handler.open();
                    }
                }
    }

    /**
     * handle connection ack, if success subscribe to topics and schedule ping
     * @param msg 
     */
    protected void handleConnAck(ConnAckMessage msg) {
        if (msg.getStatus() == ConnAckMessage.ConnectionStatus.ACCEPTED) {
                     
            Log.v(TAG, "Congrats, you are connected");
            try {
                writer.subscribe(AppState.getUserDetails().getUid() + Topics.APP, QoS.AT_LEAST_ONCE);
                writer.subscribe(AppState.getUserDetails().getUid() + Topics.SERVICE, QoS.AT_LEAST_ONCE);
                writer.subscribe(AppState.getUserDetails().getUid() + Topics.UI, QoS.AT_LEAST_ONCE);
                writer.processSendQueue();
                
                if(pingTask !=null){
                    pingTask.cancel();
                }
                
                if(pingAckTask !=null){
                    pingAckTask.cancel();
                }
                
                pingTask = new TimerTask() {
                    public void run() {
                        pingAckTask = new TimerTask() {
                            public void run() {
                                handleFailure(new Exception("ping timeout exception"));
                            }
                        };
                        pingAckTimer.schedule(pingAckTask, MQTT_PING_TIMEOUT);
                        writer.ping();
                    }
                };
                pingTimer.schedule(pingTask, MQTT_PING_TIMEOUT, MQTT_PING_TIMEOUT);
                if(handler.isfirstConnctionAfterRegistration()) {
                    MqttManager.requestAccountInfo(); // TODO move to more suitable/less frequnet place
                    handler.setfirstConnctionAfterRegistration(false);
                }
            } catch (Exception e) {
                         
                Log.v(TAG, "error while subscribing: " + e.getClass().getName());
            }
        } else if (msg.getStatus() == ConnAckMessage.ConnectionStatus.BAD_USERNAME_OR_PASSWORD) {
            new Thread(new Runnable() {
                public void run() {
                    ModelUtils.deleteUser(null);
                }
            }).start();
                     
            Log.v(TAG, "Wrong username/password");
        } else {
                    
            Log.v(TAG, "Something wrong, got status=" + msg.getStatus());
            
        }
    }
    
    /**
     * handle various types of mqtt messages
     * @param msg 
     */
    protected void handlePublishMessage(PublishMessage msg) {
                 
        Log.v(TAG, "PUBLISH (" + msg.getTopic() + "): " + msg.getDataAsString());
        
        try {
            MqttObjectModel model = MqttJsonWrapper.toMqttObject(new String(msg.getData(), TEXT_ENCODING));
            if (model != null) {
                int id = model.type.id();
                if (MqttType.ACCOUNT_INFO.id() == id) {
                    ModelUtils.updateAccountDetails(model.data);
                } else if (MqttType.ANALYTICS_EVENT.id() == id) {
                    //TODO
                } else if (MqttType.BLOCK_USER.id() == id) {
                    //ignore as valid in outgoing message only.
                } else if (MqttType.DELIVERY_REPORT.id() == id) {
                    ModelUtils.updateSentChatByMsgId(Long.parseLong(model.data.toString()), MessageStatus.DELIVERED);
                } else if (MqttType.END_TYPING.id() == id) {
                    ModelUtils.updateConversationByMsisdn(model.fromMsisdn, false);
                } else if (MqttType.GROUP_CHAT_END.id() == id) {
                    ModelUtils.setGroupChatStatus(model.toMsisdn, true);
                } else if (MqttType.GROUP_CHAT_JOIN.id() == id) {
                             
                    Log.v(TAG, "grp-id: " + model.toMsisdn + " memebrs: " + ((Hashtable) model.data).size() + " from: " + model.fromMsisdn);
                   
                    ModelUtils.updateGroupChat(model.toMsisdn, (Hashtable) model.data, model.fromMsisdn);
                } else if (MqttType.GROUP_CHAT_LEAVE.id() == id) {
                    if(model.subType == MqttType.BIS){
                        ModelUtils.addChat(SYSTEM_MSG_NO_INTERNATIONAL_SMS, new Date().getTime(), model.fromMsisdn, model.toMsisdn, SYSTEM_MAPPED_MESSAGE, MessageStatus.READ, MessageType.SYSTEM);
                    }
                    ModelUtils.removeUserFromGroupChat(model.toMsisdn, (String) model.data);
                } else if (MqttType.GROUP_CHAT_NAME.id() == id) {
                    //TODO ::Anshuman
                    ModelUtils.updateGroupTitle((String) model.data, model.toMsisdn);
                } else if (MqttType.ICON.id() == id) {
                    ModelUtils.updateThumbsInAvatar(model.fromMsisdn, (String) model.data);
                } else if (MqttType.INVITE.id() == id) {
                    //ignore as valid in outgoing message only.
                } else if (MqttType.INVITEE_JOINED.id() == id) {
                    AddressBookList.getInstance().updateHikeStatus(model.data + EMPTY_STRING, true);
                    // which we are recieving from server on new message recieved.
                    ModelUtils.addChat(SYSTEM_MSG_INVITEE_JOINED, new Date().getTime(), model.data + EMPTY_STRING, model.toMsisdn, SYSTEM_MAPPED_MESSAGE, MessageStatus.RECEIVED, MessageType.SYSTEM);
                    // can remove a selective forms from show
                    if (Display.getInstance().getCurrent() != null) {
                        Display.getInstance().getCurrent().show();
                    }
                } else if (MqttType.INVITE_INFO.id() == id) {
                    AppState.getUserDetails().getAccountInfo().setTotalCredit((String) model.data);
                } else if (MqttType.MESSAGE.id() == id) {
                    if (model.data != null && model.data instanceof MqttMessage) {
                        MqttMessage mqttMsg = (MqttMessage) model.data;
                        mqttMsg.getTimeStamp().setTime(mqttMsg.getTimeStamp().getTime() * 1000);
                        if (mqttMsg != null) {
                            long mappedId = 0;
                                    
                            Log.v(TAG, "Message Direction: " + model.getMessageDirection());
                           
                            if (model.getMessageDirection() == MqttObjectModel.MessageDirection.IN) {
                                mappedId = Long.parseLong(mqttMsg.getMessageId());
                                 
                                Log.v(TAG, "mappedMsgID: " + mappedId);
                                
}                           // TODO to check and update the text for multiple users
                            if (mqttMsg.getDndNumbers() == null) {
                                ModelUtils.addChat(TextUtils.replace(TextUtils.getOriginalSmileys(), TextUtils.getMappedSmileys(), mqttMsg.getMessage()), mqttMsg.getTimeStamp().getTime(), model.fromMsisdn, model.toMsisdn, mappedId, MessageStatus.RECEIVED, mqttMsg.getType());
                            } else {
                                Vector list = mqttMsg.getDndNumbers();
                                Vector names = new Vector(list.size());
                                for (int i = 0; i < list.size(); i++) {
                                    AddressBookEntry entry = AddressBookList.getInstance().getEntryByMsisdn(list.elementAt(i).toString());
                                    String aName = (entry != null) ? entry.getName() : list.elementAt(i).toString();
                                             
                                    Log.v(TAG, "dnd users name: ------- >" + aName);
                                    names.addElement(aName);
                                }
                                String msgString = TextUtils.toNames(names);
                                ModelUtils.getSystemMessage(SYSTEM_MSG_WAITING_DND_USERS_PREFIX, msgString, model.fromMsisdn, model.toMsisdn);
                            }
                        }
                    }
                } else if (MqttType.MESSAGE_READ.id() == id) {
                    ModelUtils.updateChatByMsgIds((long[]) model.data, MessageStatus.READ);
                } else if (MqttType.REQUEST_ACCOUNT_INFO.id() == id) {
                    //TODO
                } else if (MqttType.SMS_CREDIT.id() == id) {
                    int sms = TextUtils.parseInt(model.data);
                    AppState.getUserDetails().setSmsCredit(TextUtils.parseInt(model.data));
                    if(Display.getInstance().getCurrent() instanceof FormChatThread && sms < 1) {
                        ((FormChatThread)Display.getInstance().getCurrent()).initBlockScreenWhenSMSIsZero();
                    }
                } else if (MqttType.START_TYPING.id() == id) {
                    ModelUtils.updateConversationByMsisdn(model.fromMsisdn, true);
                } else if (MqttType.UNBLOCK_USER.id() == id) {
                    //ignore as valid in outgoing message only.
                } else if (MqttType.UPDATE_AVAILABLE.id() == id) {
                    //TODO
                } else if (MqttType.USER_JOINED.id() == id || MqttType.USER_LEFT.id() == id || MqttType.USER_OPTIN.id() == id) {
                    UserHikeStatus userStatus = (UserHikeStatus) model.data;
                    // TODO user msisdn replace with name
                    AddressBookEntry address = AddressBookList.getInstance().getEntryByMsisdn(userStatus.getMsisdn());
                    String name = address == null ? userStatus.getMsisdn() : address.getName();
                    boolean addNotification = false;
                    if(address != null && !address.isOnHike()){                        
                        addNotification = true;
                    } else if (address == null) {
                        ConversationModel conv = ConversationList.getInstance().getEntryByMsisdn(userStatus.getMsisdn());
                        if (conv != null && !conv.isOnHike()) {
                            addNotification = true;
                        }
                    }
                    if(addNotification) {                        
                        if (MqttType.USER_JOINED.id() == id) {
                            ModelUtils.addChat(name + SYSTEM_MSG_USER_JOINED, new Date().getTime(), userStatus.getMsisdn(), model.toMsisdn, SYSTEM_MAPPED_MESSAGE, MessageStatus.RECEIVED, MessageType.SYSTEM);
                        } else if (MqttType.USER_OPTIN.id() == id) {
                            ModelUtils.addChat(name + SYSTEM_MSG_USER_OPTED_IN, new Date().getTime(), userStatus.getMsisdn(), model.toMsisdn, SYSTEM_MAPPED_MESSAGE, MessageStatus.RECEIVED, MessageType.SYSTEM);
                        }
                    }                    
                    
                    ModelUtils.updateUserHikeStatusAndCredits(userStatus);
                    if (Display.getInstance().getCurrent() instanceof FormChatThread) {
                        ((FormChatThread) Display.getInstance().getCurrent()).updateScreenForHikeStatus();
                    } else {
                        Display.getInstance().getCurrent().show();
                    }
                } else if (MqttType.BIS.id() == id) {
                    ModelUtils.addChat(SYSTEM_MSG_NO_INTERNATIONAL_SMS, new Date().getTime(), model.fromMsisdn, model.toMsisdn, SYSTEM_MAPPED_MESSAGE, MessageStatus.READ, MessageType.SYSTEM);
                } else if (MqttType.ACCOUNT.id() == id) {
                    ModelUtils.updateAccountDetails(model.data);
                }else if (MqttType.ACTION.id() == id) {
                    ModelUtils.setPostAddressBookFlag(model.data);
                } else {
                    //TODO default handle
  
                    Log.v(TAG, "handled unknown message type");

                }
            }
        } catch (UnsupportedEncodingException ex) {
  
            Log.v(TAG, ex.toString());

        } catch (JSONException ex) {
                     
            Log.v(TAG, ex.toString());
        }
    }

    protected void handlePingResponse(PingRespMessage msg) {
        if(pingAckTask !=null){
            pingAckTask.cancel();
        }
                
        Log.v(TAG, "PING response came at " + System.currentTimeMillis());
    }

    protected void handlePubAck(PubAckMessage msg, long msgId) {
        
                
        Log.v(TAG, "PubAck came for msgId=" + msgId);
        if (!ModelUtils.updateSentChatByMsgId(msgId, MessageStatus.SENT)) {
            if (!ModelUtils.updateReceivedChatByMsgId(msgId, MessageStatus.READ)) {
                if (!ModelUtils.updateGroupStatus(msgId, false)) {
                    //TODO handle ACK for other requests.                
                }
            }
        }
    }

    protected void handleSubAck(SubAckMessage msg) {
                
        Log.v(TAG, "SubAck came for msgId=" + msg.getMessageId() + " " + msg.getGrantedQoSs().toString());
    }

    protected void handleUnSubAck(UnsubAckMessage msg) {
                 
        Log.v(TAG, "UnSubAck came for msgId=" + msg.getMessageId());
    }

    public void connectionOpen() {
        if (reader != null) {
            reader.requestStop();
        }
        reader = new MqttReader(handler.getMessageInputStream(), this, writer);
        reader.start();

        writer.connect(handler.getMessageOutputStream());
    }

    public void connectionClosed() {
        if (reader != null) {
            reader.requestStop();
        }
        if (writer != null) {
            writer.requestStop();
        }
        if (pingAckTask !=null){
            pingAckTask.cancel();
        }
        if (pingTask !=null){
            pingTask.cancel();
        }
    }

    public MqttWriter getWriter() {
        return writer;
    }
}
