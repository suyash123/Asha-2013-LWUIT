package com.bsb.hike.dto;

import com.bsb.hike.util.CollectionUpdateEvent;
import com.bsb.hike.util.Collections;
import com.bsb.hike.util.Collections.Comparator;
import com.bsb.hike.util.Log;
import java.util.Enumeration;

/**
 * list for Conversations
 * @author Ankit Yadav
 * @author Sudheer Keshav Bhat
 */
public class ConversationList extends DataCollection {

    private final static ConversationList list = new ConversationList();
    private final static String TAG = "ConversationList";
    
    private ConversationList() {
    }
    private boolean mSortOnChange = true;

    private Comparator mDescendingComparator = new Comparator() {
        public int compare(Object o1, Object o2) {
            ConversationModel cm1 = (ConversationModel) o1;
            ConversationModel cm2 = (ConversationModel) o2;
            if(cm1 == null || cm2==null || cm1.getLastChatModel() == null || cm2.getLastChatModel() == null){
                return 0;
            }
            return cm1.getLastChatModel().getTimestamp() > cm2.getLastChatModel().getTimestamp() ? -1 : (cm1.getLastChatModel().getTimestamp() < cm2.getLastChatModel().getTimestamp() ? 1 : 0);
        }
    };

    /**
     * 
     * @return singleton instance for Conversation list
     */
    public static ConversationList getInstance() {
        return list;
    }

    public synchronized void addElement(DataModel model) {
        ConversationModel convModel = (ConversationModel) model;
        ConversationModel existing = getEntryByConversationID(convModel.getConversationID());
        Runtime.getRuntime().gc();
        
        Log.v(TAG, "Model Is Added :: " + convModel.getName() + "mSortOnChange :" + mSortOnChange );
        if (mSortOnChange) {
            if (existing == null) {
                super.addElement(model);
                sort();
                if (listener != null) {
                    listener.modelAdded(new CollectionUpdateEvent(-1, model));
                }
            } else {
                int index = indexOf(existing);
                setElementAt(model, index);
                sort();
                if (listener != null) {
                    listener.modelUpdated((new CollectionUpdateEvent(index, model)));
                }
            }
        } else {
            if (existing == null) {
                super.addElement(model);
            } else {
                super.setElementAt(model, indexOf(existing));
            }
        }
    }

    /**
     * iterates and return ConversationModel by ConversationID
     * @param convId
     * @return
     */
    public synchronized ConversationModel getEntryByConversationID(long convId) {
        Enumeration enums = list.elements();
        while (enums.hasMoreElements()) {
            ConversationModel entry = (ConversationModel) enums.nextElement();
            if (convId == entry.getConversationID()) {
                return entry;
            }
        }
        return null;
    }

    /**
     * iterates and return ConversationModel by msisdn
     * @param msisdn
     * @return 
     */
    public synchronized ConversationModel getEntryByMsisdn(String msisdn) {
        if (msisdn == null) {
            return null;
        }
        Enumeration enums = list.elements();
        while (enums.hasMoreElements()) {
            ConversationModel entry = (ConversationModel) enums.nextElement();
            if (entry.getMsisdn().equals(msisdn)) {
                return entry;
            }
        }
        return null;
    }

    public synchronized void removeElementAt(int index) {
        ConversationModel model = (ConversationModel) getInstance().elementAt(index);
        long id = model.getConversationID();
        super.removeElementAt(index);
        
        ChatList chats = ChatList.getInstance();
        Enumeration enums = chats.elements();
        while (enums.hasMoreElements()) {
            ChatModel chat = (ChatModel) enums.nextElement();
            if (chat.getConversationID() == id) {
                chats.removeElement(chat);
            }
        }
    }
        
    public synchronized void removeAllElements() {
        super.removeAllElements();
        ChatList.getInstance().removeAllElements();
    }

    public void refreshModel(DataModel model) {
        if (mSortOnChange) {
            sort();
            if (listener != null) {
                listener.modelContentsUpdated(model);
            }
        } else {
            super.refreshModel(model);
        }
    }

    /**
     * sorts the list
     */
    public synchronized void sort() {        
        Log.v(TAG, "Sorting is called ");
        Collections.sort(this, mDescendingComparator);
    }
}
