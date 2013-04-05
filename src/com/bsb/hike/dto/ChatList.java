package com.bsb.hike.dto;

import com.bsb.hike.util.Log;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class ChatList extends DataCollection {

    private final static String TAG = "ChatList";
    private final static ChatList list = new ChatList();
    private final static Hashtable cachedChat = new Hashtable();

    private ChatList() {
    }

    /**
     * 
     * @return sinleton instance of ChatList
     */
    public static ChatList getInstance() {
        return list;
    }

    public synchronized void addElement(DataModel model) {
        ChatModel chat = (ChatModel) model;
        ChatModel dup = chat.isMe() ? getChatByMessageID(chat.getMessageID()) :  getChatByMappedMessageID(chat.getFromMsisdn(), chat.getMappedMsgId());        
        if(chat.getType() == MqttObjectModel.MessageType.SYSTEM || (chat.getType() != MqttObjectModel.MessageType.SYSTEM && dup == null)){
            if (cachedChat.containsKey(EMPTY_STRING + chat.getConversationID())) {
                Vector clist = (Vector)cachedChat.get(EMPTY_STRING + chat.getConversationID());
                clist.addElement(chat);
            } else if (chat.getConversationID() != SYSTEM_CONVERSATION) {
                Vector clist = new Vector();
                clist.addElement(chat);
                cachedChat.put(Long.toString(chat.getConversationID()), clist);
            } else if (chat.getConversationID() == SYSTEM_CONVERSATION) {
                addGlobalSystemNotification(chat); //TODO update chat list
            }
            super.addElement(model);
        }
    }

    public synchronized boolean removeElement(DataModel obj) {
        ChatModel chat = (ChatModel) obj;
        if (cachedChat.containsKey(Long.toString(chat.getConversationID()))) {
            Vector clist = (Vector) cachedChat.get(Long.toString(chat.getConversationID()));
            clist.removeElement(chat);
            if (clist.isEmpty()) {
                ConversationList.getInstance().removeElement(ConversationList.getInstance().getEntryByConversationID(chat.getConversationID()));
                cachedChat.remove(Long.toString(chat.getConversationID()));
            }
        }
        return super.removeElement(obj);
    }  
    
    public synchronized void removeAllElements() {
        super.removeAllElements();
        cachedChat.clear();
    }  

    /**
     * adds system notification (note: it is a heavy operation, use wisely)
     * @param model 
     */
    private synchronized void addGlobalSystemNotification(ChatModel model) {
        String msisdn = model.getFromMsisdn();                 
        Log.v(TAG, "notification msisdn: " + msisdn + "msg: " + model.getMessage());
        if (model.getConversationID() == SYSTEM_CONVERSATION) {
            Enumeration enums = ConversationList.getInstance().elements();
            while (enums.hasMoreElements()) {
                ConversationModel conv = (ConversationModel) enums.nextElement();
                if (conv.hasParticipant(msisdn)) {
                    if (cachedChat.containsKey(EMPTY_STRING + conv.getConversationID())) {
                        Vector clist = (Vector) cachedChat.get(EMPTY_STRING + conv.getConversationID());
                        clist.addElement(model);
                        Log.v(TAG, "global notification added to existing conv: " + msisdn);
                    } else {
                        Vector clist = new Vector();
                        clist.addElement(model);
                        cachedChat.put(Long.toString(conv.getConversationID()), clist);
                        Log.v(TAG, "global notification added to new conv: " + msisdn);
                    }
                }
            }
        }
    }
    
    /**
     * iterates and returns chatmodel for given msgid
     * @param msgID
     * @return 
     */
    public synchronized ChatModel getChatByMessageID(long msgID) {
        Enumeration enums = getInstance().elements();
        while (enums.hasMoreElements()){
            ChatModel entry = (ChatModel) enums.nextElement();
            if (msgID == entry.getMessageID()) {
                return entry;
            }
        }
        return null;
    }

    /**
     * iterates and returns chatmodel for given mapped msgid
     * @param msisdn
     * @param msgID
     * @return 
     */
    public synchronized ChatModel getChatByMappedMessageID(String msisdn, long msgID) {
        Enumeration enums = getInstance().elements();
        while (enums.hasMoreElements()){
            ChatModel entry = (ChatModel) enums.nextElement();
            if (msgID == entry.getMappedMsgId() && msisdn.equals(entry.getFromMsisdn())) {
                return entry;
            }
        }
        return null;
    }

    /**
     * 
     * @param convId
     * @return chats that belong to given conversation
     */
    public synchronized Vector getChats(long convId) {
        return (Vector) cachedChat.get(EMPTY_STRING + convId);
    }

    /**
     * 
     * @param convID
     * @return last chat model in given conversation
     */
    public synchronized ChatModel getLastChatByConversationID(long convID) {
        return getChats(convID) == null? null : (ChatModel) getChats(convID).lastElement();
    }
}