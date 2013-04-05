package com.bsb.hike.io;

import com.bsb.hike.dto.AddressBookEntry;
import com.bsb.hike.dto.AddressBookList;
import com.bsb.hike.dto.ChatList;
import com.bsb.hike.dto.ContactAvatarList;
import com.bsb.hike.dto.ConversationList;
import com.bsb.hike.dto.DataCollection;
import com.bsb.hike.dto.GroupList;
import com.bsb.hike.dto.MqttPendingMessageList;
import com.bsb.hike.util.AppConstants;
import com.bsb.hike.util.DataCollectionJsonWrapper;
import com.bsb.hike.util.Log;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import org.json.me.JSONArray;
import org.json.me.JSONObject;

public class FileHandler implements AppConstants {

    private static final String TAG = "FileHandler";

    /**
     * creates and returns connection to given RMS
     * @param file
     * @return 
     */
    public static FileConnection getFileConnection(String file) {
        String privateDir = System.getProperty("fileconn.dir.private");
        try {
            FileConnection filecon = (FileConnection) Connector.open(privateDir + file, Connector.READ_WRITE);
            if (!filecon.exists()) {
                filecon.create();
            }
            if (filecon.canWrite()) {
                return filecon;
            }
        } catch (Exception e) {
                     
            Log.v(TAG, e.getClass().getName() + " while creating file connection: " + file);
            return null;
        }
        return null;
    }

    /**
     * write records of type data collection in rms
     * @param list
     * @return 
     */
    public static boolean writeRecordsToRMS(DataCollection list) {
        boolean retVal = false;
        String file = null;
        if (list instanceof AddressBookList) {
            file = ContactFile;
        } else if (list instanceof ConversationList) {
            file = ConversationFile;
        } else if (list instanceof ChatList) {
            file = ChatFile;
        } else if (list instanceof ContactAvatarList) {
            file = ThumbsFile;
        } else if (list instanceof GroupList) {
            file = GroupFile;
        } else if (list instanceof MqttPendingMessageList) {
            file = PendingMessageFile;
        }

        RecordStore store = null;
        try {
                     
            Log.v(TAG, " started writing to " + file);
            store = RecordStore.openRecordStore(file, true, RecordStore.AUTHMODE_PRIVATE, true);
            String records = DataCollectionJsonWrapper.toJsonArray(list).toString();
            Log.v(TAG, " app data: " + records);
            byte[] rmsBytes = records.getBytes(TEXT_ENCODING);
            if (store.getNumRecords() != 1) {
                store.addRecord(rmsBytes, 0, rmsBytes.length);
            } else {
                store.setRecord(RMS_JsonID, rmsBytes, 0, rmsBytes.length);
            }
            retVal = true;
        } catch (Exception e) {                     
            Log.v(TAG, e.getClass().getName() + " while writing to RMS: " + file);
        } finally {
            if (store != null) {
                try {
                    store.closeRecordStore();
                } catch (Exception e) {
                    //ignore
                }
            }
        }
          Runtime.getRuntime().gc();
        return retVal;
    }

    /**
     * write records of type AddressBookList in rms
     * @param list 
     */
    public static void writeAddressBookToRMS(AddressBookList list) {
        int size = list.size();
        int modulo = size % RMS_ENTRY_CHUNK_SIZE;
        int diviser = size / RMS_ENTRY_CHUNK_SIZE;
        int chunkSize = modulo == 0 ? diviser : (diviser + 1);

        String[] recordStores = RecordStore.listRecordStores(); 
        
        if(recordStores != null){
            for(int i= 0;i< recordStores.length ; i++){
                if(recordStores[i].startsWith(ContactFile + ".")){
                    try {
                        RecordStore.deleteRecordStore(recordStores[i]);
                    } catch (RecordStoreException ex) {
                        Log.v(TAG, "RecordStoreException while deleting RMS: " + recordStores[i]);
                    }
                }
            }
        }
        
        for (int i = 0; i < chunkSize; i++) {
            RecordStore store = null;
            String file = null;
            try {
                file = ContactFile + "." + i;
                         
                Log.v(TAG, " started writing to " );
                store = RecordStore.openRecordStore(file, true, RecordStore.AUTHMODE_PRIVATE, true);
                String records = DataCollectionJsonWrapper.toJsonArray(list, i * RMS_ENTRY_CHUNK_SIZE, RMS_ENTRY_CHUNK_SIZE).toString();
                         
                Log.v(TAG, " records fetched " );
                byte[] rmsBytes = records.getBytes(TEXT_ENCODING);
                if (store.getNumRecords() != 1) {
                    store.addRecord(rmsBytes, 0, rmsBytes.length);
                } else {
                    store.setRecord(RMS_JsonID, rmsBytes, 0, rmsBytes.length);
                }
                         
                Log.v(TAG, "records: " );
            } catch (Exception e) {
                         
                Log.v(TAG, e.getClass().getName() + " while writing to RMS: " + file);
            } finally {
                if (store != null) {
                    try {
                        store.closeRecordStore();
                    } catch (Exception e) {
                        //ignore
                    }
                }
            }
        }
      Runtime.getRuntime().gc();
    }

    /**
     * write records of modified AddressBookList into rms
     * @param list 
     */
    public static void writeAddressBookDiffToRMS(AddressBookList list) {
        int size = list.size();
        JSONArray jsonList = new JSONArray();
        RecordStore store = null;
        String file = ContactDiffFile;
        try {
            for (int i = 0; i < size; i++) {
                AddressBookEntry entry = (AddressBookEntry) list.elementAt(i);
                if (entry.isDirty()) {
                    JSONArray entryJson = DataCollectionJsonWrapper.toJsonArray(entry);
                    jsonList.put(entryJson);
                    entry.setDirty(false);
                }
            }
            JSONObject data = new JSONObject();
            data.putOpt(DataBaseAddressDiff, jsonList);
            store = RecordStore.openRecordStore(file, true, RecordStore.AUTHMODE_PRIVATE, true);
            byte[] rmsBytes = data.toString().getBytes(TEXT_ENCODING);
            if (store.getNumRecords() != 1) {
                store.addRecord(rmsBytes, 0, rmsBytes.length);
            } else {
                store.setRecord(RMS_JsonID, rmsBytes, 0, rmsBytes.length);
            }
        } catch (Exception e) {
             
            Log.v(TAG, e.getClass().getName() + " while writing to RMS: " + file);
        } finally {
            if (store != null) {
                try {
                    store.closeRecordStore();
                } catch (Exception e) {
                    //ignore
                }
            }
        }
    }

    /**
     * read records from rms of given file into type of data collection
     * @param file
     * @return 
     */
    public static DataCollection readRecordsFromRMS(String file) {
        DataCollection list = null;
        RecordStore recordStore = null;
        try {
            recordStore = RecordStore.openRecordStore(file, true, RecordStore.AUTHMODE_PRIVATE, true);
            if (recordStore.getNumRecords() != 1) {
                //TODO not required
            } else {
                byte[] rmsBytes = recordStore.getRecord(RMS_JsonID);
                String appData = new String(rmsBytes, TEXT_ENCODING);
                Log.v(TAG, " app data: " + appData);
                list = DataCollectionJsonWrapper.toDataCollection(new JSONObject(appData));
            }
        } catch (Exception e) {                     
            Log.v(TAG, e.getClass().getName() + " while reading from RMS: " + file);
        } finally {
            if (recordStore != null) {
                try {
                    recordStore.closeRecordStore();
                } catch (Exception e) {
                    //ignore
                }
            }
        }
        Runtime.getRuntime().gc();
        return list;
    }

    /**
     * read records from rms of addressbook 
     */
    public static void readAddressBookFromRMS() {
        AddressBookList list = AddressBookList.getInstance();
        list.removeAllElements();
        list.setSortOnChange(false);
        for (int i = 0; i < 10; i++) {
            String file = ContactFile + "." + i;
            RecordStore recordStore = null;
            try {
                recordStore = RecordStore.openRecordStore(file, true, RecordStore.AUTHMODE_PRIVATE, true);
                if (recordStore.getNumRecords() != 1) {
                    //TODO not required
                } else {
                    byte[] rmsBytes = recordStore.getRecord(RMS_JsonID);
                    String appData = new String(rmsBytes, TEXT_ENCODING);
                    DataCollectionJsonWrapper.toAddressBookList(new JSONObject(appData));
                }
            } catch (Exception e) {
                         
                Log.v(TAG, e.getClass().getName() + " while reading from RMS: " + file);
           } finally {
                if (recordStore != null) {
                    try {
                        recordStore.closeRecordStore();
                    } catch (Exception e) {
                        //ignore
                    }
                }
            }
        }
//            System.out.println("Before: " + TextUtils.toString(list));
        list.sort();
//            System.out.println("After: " + TextUtils.toString(list));
        list.setSortOnChange(true);
  
        Log.v(TAG, "list.size::::" + list.size());
        Runtime.getRuntime().gc();
    }

    /**
     * merge addressbook diff
     */
    public static void mergeAddressBookDiffFromRMS() {
        String file = ContactDiffFile;
        RecordStore recordStore = null;
        try {
            recordStore = RecordStore.openRecordStore(file, true, RecordStore.AUTHMODE_PRIVATE, true);
            if (recordStore.getNumRecords() != 1) {
                //TODO not required
            } else {
                byte[] rmsBytes = recordStore.getRecord(RMS_JsonID);
                String appData = new String(rmsBytes, TEXT_ENCODING);
                DataCollectionJsonWrapper.mergeAddressBookList(new JSONObject(appData));
            }
        } catch (Exception e) {             
            Log.v(TAG, e.getClass().getName() + " while reading from RMS: " + file);
        } finally {
            if (recordStore != null) {
                try {
                    recordStore.closeRecordStore();
                } catch (Exception e) {
                    //ignore
                }
            }
        }
        Runtime.getRuntime().gc();
    }
}