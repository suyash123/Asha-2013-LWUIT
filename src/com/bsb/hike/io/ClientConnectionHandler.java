package com.bsb.hike.io;

import com.bsb.hike.Hike;
import com.bsb.hike.dto.AddressBookEntry;
import com.bsb.hike.dto.AddressBookList;
import com.bsb.hike.dto.AddressBookRequestEntry;
import com.bsb.hike.dto.AppState;
import com.bsb.hike.dto.DataModel;
import com.bsb.hike.dto.FileInfo;
import com.bsb.hike.dto.GroupList;
import com.bsb.hike.dto.JADInfoAttribute;
import com.bsb.hike.dto.UserDetails;
import com.bsb.hike.util.AppConstants;
import com.bsb.hike.util.Log;
import com.bsb.hike.util.TextUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Image;
import net.sf.jazzlib.GZIPInputStream;
import net.sf.jazzlib.GZIPOutputStream;
import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import util.base64.Base64;

/**
 *
 * @author Ankit Yadav.
 */
//TODO rename method names to follow coding standards.
public class ClientConnectionHandler implements AppConstants {
    
    private static final String TAG = "ClientConnectionHandler";

    /**
     * This method creates a HTTP connection with the specified parameters.
     * @param url The URL to which connection establishes
     * @param method The method ex. GET, POST
     * @param header The headers specific for a connection
     * @return
     * @throws ConnectionFailedException 
     */
    private static HttpConnection getConnection(String url, String method, Hashtable header) throws ConnectionFailedException {
        header.put(KeyUserAgent, ValueUserAgent);
        header.put(KeyAccept, ValueAccept);

        HttpConnection con;
        int retryCount = MaxRetryAttempt;
        while (retryCount-- > 0) {
            try {
                con = (HttpConnection) Connector.open(url, Connector.READ_WRITE);
                con.setRequestMethod(method);
                if (con != null) {
                    Enumeration keys = header.keys();
                    while (keys.hasMoreElements()) {
                        String key = keys.nextElement().toString();
                        String value = header.get(key).toString();
                        con.setRequestProperty(key, value);
                    }
                    return con;
                }
            } catch (IOException ex) {
                        
                Log.v(TAG, "connection could not be established." + ex.getClass().getName());
            } catch (SecurityException ex) {
                         
                Log.v(TAG, "user denied connection." + ex.getClass().getName());
            }
        }
        throw new ConnectionFailedException();
    }

    
    /**
     * This method reads the string out of the stream and returns it.
     * @param is
     * @return
     * @throws IOException 
     */
    private static String getStringFromStream(InputStream is) throws IOException {
        StringBuffer buffer = new StringBuffer();
        int ch;
        while ((ch = is.read()) != -1) {
            buffer.append((char) ch);
        }
        return buffer.toString();
    }

    
    /**
     * This method returns the MSISDN in response for auto-MSISDN authentication.
     * @return phone_no of SIM
     */
    public static String PostAccount() throws IOException, ConnectionFailedException {
        String response = null;
        InputStream is = null;
        HttpConnection con = null;
        try 
        {
            Hashtable header = new Hashtable();
            header.put(keyContentType, ValueContentJSON);
            con = getConnection(BaseURL + URIAccount, HttpConnection.POST, header);
            OutputStream os = con.openOutputStream();
            JSONObject object = new JSONObject();
            object.put(JSON_KEY_DEVICE_TYPE, VAL_DEV_TYPE);
            object.put(JSON_KEY_DEVICE_ID, TextUtils.processIMEIintoHash());
            object.put(JSON_KEY_DEVICE_VERSION, TextUtils.getDeviceModel());
            object.put(JSON_KEY_APP_VERSION, Hike.currentVer);
            writeObjectToOutputStream(os, object);
            is = con.openInputStream();
            response = getStringFromStream(is);

            Log.v(TAG, "PostAccount response String :" + response);
            JSONObject responseJson = new JSONObject(response);
            return fetchRegistrationInfo(responseJson, con.getHeaderField(KeySetCookie));
        } catch (JSONException e) {                     
            Log.v(TAG, "json exception: " + e.getMessage() + "\njson data: " + response);
        } finally {
            handleFinally(is, con, null);
        }
        return response;
    }

    
    /**
     * This method closes the connection and all I/O stream after network activity finishes.
     * @param is
     * @param con
     * @param os 
     */
    private static void handleFinally(InputStream is, HttpConnection con, OutputStream os) {
        try {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
            if (con != null) {
                con.close();
            }
        } catch (IOException ex) {
            //ignore
        }
    }

    
    /**
     * This method writes object to the output stream.
     * @param os
     * @param object
     * @throws IOException 
     */
    private static void writeObjectToOutputStream(OutputStream os, JSONObject object) throws IOException {
        String jsonString = object.toString();
        os.write(jsonString.getBytes(TEXT_ENCODING));
        os.flush();
    }

    /**
     * This method posts mobile number of a user in case of auto-MSISDN fails.
     * @param request
     * @return msisdn of account posted
     */
    public static String PostAccount(String request) throws IOException, ConnectionFailedException {
        Hashtable header = new Hashtable();
        InputStream is = null;

        header.put(keyContentType, ValueContentJSON);

        HttpConnection con = getConnection(BaseURL + URIValidate + ValidateAccountDigit, HttpConnection.POST, header);
        OutputStream os = con.openOutputStream();
        try {
            JSONObject object = new JSONObject();
            object.put(JsonKeyPhone, request);
            writeObjectToOutputStream(os, object);
            is = con.openInputStream();
                     
            Log.v(TAG, "PostAccount Response Code :" + con.getResponseCode());
            String responseStr = getStringFromStream(is);
                      
            Log.v(TAG, "PostAccount response String :" + responseStr);
            JSONObject response = new JSONObject(responseStr);
            return response.getString(JsonKeyMsisdn);
        } catch (JSONException ex) {
                     
            Log.v(TAG, "json exception: " + ex.getMessage());
        } finally {
            handleFinally(is, con, os);
        }
        return null;
    }

    
    /**
     * This method sends delete account request to server.
     * @return
     * @throws IOException
     * @throws ConnectionFailedException 
     */
    public static boolean DeleteAccount() throws IOException, ConnectionFailedException {
        Hashtable header = new Hashtable();
        OutputStream os = null;

        header.put(keyContentType, ValueContentJSON);
        
        HttpConnection con = getConnection(BaseURL + URIAccountDelete, HttpConnection.POST, header);
        String cookie = AppState.getUserDetails().getCookie();
        con.setRequestProperty(KeyCookie, cookie);
        con.setRequestProperty(JsonKeyUID, AppState.getUserDetails().getUid());
        OutputStream outputStream = con.openOutputStream();
        outputStream.flush();
        InputStream is = con.openInputStream();
                 
        Log.v(TAG, "Delete Account response code ========================= " + con.getResponseCode());
        String responseString = getStringFromStream(is);
                 
        Log.v(TAG, "Response string on delete accont" + responseString);
        try {
            JSONObject jResponse = new JSONObject(responseString);
            String stat = jResponse.getString(JsonKeyStat);
            if (stat.trim().toLowerCase().equals("ok")) {
                return true;
            }
        } catch (JSONException e) {
                     
            Log.v(TAG, "json exception: " + e.getMessage() + "\njson data: " + responseString);
        } finally {
            handleFinally(is, con, os);
        }
        return false;
    }

    
    /**
     * This method validates account through PIN
     * @param msisdn
     * @param pin
     * @return
     * @throws IOException
     * @throws ConnectionFailedException 
     */
    public static String PostValidate(String msisdn, String pin) throws IOException, ConnectionFailedException {
        if (msisdn == null || pin == null) {
            return null;
        }
        Hashtable header = new Hashtable();
        InputStream is = null;

        header.put(keyContentType, ValueContentJSON);

        HttpConnection con = getConnection(BaseURL + URIAccount, HttpConnection.POST, header);
        OutputStream os = con.openOutputStream();
        try {
            JSONObject object = new JSONObject();
            object.put(JsonKeyMsisdn, msisdn);
            object.put(JsonKeyPin, pin);
            object.put(JSON_KEY_DEVICE_TYPE, VAL_DEV_TYPE);
            object.put(JSON_KEY_DEVICE_ID, TextUtils.processIMEIintoHash());
            object.put(JSON_KEY_DEVICE_VERSION, TextUtils.getDeviceModel());
            object.put(JSON_KEY_APP_VERSION, Hike.currentVer);
            
            Log.v(TAG, "sending: " + object.toString());
            writeObjectToOutputStream(os, object);
            is = con.openInputStream();
            JSONObject responseJson = new JSONObject(getStringFromStream(is));
                    
            Log.v(TAG, "recieving: " + responseJson.toString());            
            return fetchRegistrationInfo(responseJson, con.getHeaderField(KeySetCookie));
        } catch (JSONException e) {                    
            Log.v(TAG, "json exception: " + e.getMessage());
        } finally {
            handleFinally(is, con, os);
        }
        return null;
    }
    
    
    /**
     * This method fetches user information from validation response.
     * @param responseJson
     * @param cookie
     * @return
     * @throws JSONException 
     */
    private static String fetchRegistrationInfo(JSONObject responseJson, String cookie) throws JSONException{
        if (!responseJson.has(JsonKeyError)) {
            String uid = responseJson.getString(JsonKeyUID);
            String msidn = responseJson.getString(JsonKeyMsisdn);
            String token = responseJson.getString(JsonKeyToken);
            int smsCredit = responseJson.getInt(JsonKeySmsCredit);
            String countryCode = responseJson.getString(JsonKeyCountryCode);
            UserDetails user = new UserDetails(cookie, msidn, token, uid);
            user.setSmsCredit(smsCredit);
            user.setCountryCode(countryCode);

            Log.v(TAG, user.toString());
            AppState.setUserDetails(user);
            return msidn;
        }
        return null;
    }

    
      // also removes the contact from AddressbookList
     private static JSONArray fetchDetailsOfDeletedContacts(Vector addressBookRequestEntries) {

        JSONArray removedContactsArray = new JSONArray();
        
        //list all existing ids
        Vector removedId = new Vector();
        Enumeration enumsRmsList = AddressBookList.getInstance().elements();
        while (enumsRmsList.hasMoreElements()) {
            AddressBookEntry entry = (AddressBookEntry) enumsRmsList.nextElement();
            String id = entry.getId();
            if(!removedId.contains(id)) {
                removedId.addElement(id);
            }
        }
        
        //remove ids that are also present in request
        Enumeration enumsRequestList = addressBookRequestEntries.elements();
        while (enumsRequestList.hasMoreElements()) {
            AddressBookRequestEntry entry = (AddressBookRequestEntry) enumsRequestList.nextElement();
            String id = entry.getId();
            if(removedId.contains(id)) {
                removedId.removeElement(id);
            }
        }
        
         for (int i = 0; i < removedId.size(); i++) {
             removedContactsArray.put(removedId.elementAt(i));
         }
        
        return removedContactsArray;
    }

    public static boolean PostAddressbook(Vector/*<AddressbookRequestEntry>*/ requestEntries, boolean update) throws IOException, ConnectionFailedException {
        Log.v(TAG, "PostAddressbook::before:requestEntries.size():" + requestEntries.size());
         Log.v(TAG, "Address book list size before fetchDetailsOfDeletedContacts:-"+AddressBookList.getInstance().size());
        JSONArray removedArray = update ? fetchDetailsOfDeletedContacts(requestEntries) : null;   
         Log.v(TAG, "Address book list size after fetchDetailsOfDeletedContacts:-"+AddressBookList.getInstance().size());
        Log.v(TAG, "PostAddressbook::after:requestEntries.size():" + requestEntries.size());
        int requestSize = requestEntries.size();
        int chunkCount = requestSize % ADDRESSBOOK_CHUNK_SIZE == 0 ? requestSize / ADDRESSBOOK_CHUNK_SIZE : requestSize / ADDRESSBOOK_CHUNK_SIZE + 1;
                 
        Log.v(TAG, "Chunk Size: " + chunkCount);
        OutputStream os = null;
        HttpConnection con = null;
        InputStream is = null;
        AddressBookList list = AddressBookList.getInstance();
        int listSize = list.size();
        int count = 0;
        
        Log.v(TAG, "PostAddressbook::before sync:AddressBookList.size():" + list.size());
        list.setSortOnChange(false);
        
        if (!update) {
            Log.v(TAG, "PostAddressbook::before removng all list element:" + list.size());
            list.removeAllElements();
        }
        
        for (int j = 0; j < chunkCount; j++) {
            try {
                JSONObject chunk = new JSONObject();
                int updatedRequestSize = requestEntries.size();
               // int offset = j * ADDRESSBOOK_CHUNK_SIZE;
                         
                //Log.v(TAG, "Starting offset: " + (offset));
               // int maxCap = offset + ADDRESSBOOK_CHUNK_SIZE > requestSize ? requestSize : offset + ADDRESSBOOK_CHUNK_SIZE;
                  int maxCap = ADDRESSBOOK_CHUNK_SIZE > updatedRequestSize ? updatedRequestSize : ADDRESSBOOK_CHUNK_SIZE;
                for (int i = 0; i < maxCap; i++) {
                    AddressBookRequestEntry entry = (AddressBookRequestEntry) requestEntries.elementAt(i);
                    JSONArray jArray;
                    if (chunk.has(entry.getId())) {
                        jArray = chunk.getJSONArray(entry.getId());
                    } else {
                        jArray = new JSONArray();
                    }
                    JSONObject jEntry = new JSONObject();
                    //jEntry.put(JsonKeyUID, AppState.getUserDetails().getUid());
                    jEntry.put(JsonKeyPhone, entry.getPhoneNumber());
                    jEntry.put(JsonKeyName, entry.getName());
                    jArray.put(jEntry);
                    Log.v(TAG, "contact request json: "+jEntry.toString());
                    chunk.put(entry.getId(), jArray);
                }
                
                //removing processed chunk
               for (int i = 0; i < maxCap; i++) {
                   requestEntries.removeElementAt(0);
               }
                
                boolean updateRequest = j == 0 && update;
                Hashtable header = new Hashtable();
                header.put(keyContentType, ValueContentJSON);
                header.put("Accept-Encoding", "gzip");
                header.put("Content-Encoding", "gzip");
                String url = BaseURL + (updateRequest ? URIAdressbookUpdate : URIAdressbook);
                con = getConnection(url, HttpConnection.POST, header);
                String cookie = AppState.getUserDetails().getCookie();
                con.setRequestProperty(KeyCookie, cookie);
                con.setRequestProperty(JsonKeyUID, AppState.getUserDetails().getUid());
                Log.v(TAG, "Contact Synchronization Request started: " + j + updateRequest);
                os = con.openDataOutputStream();
                if (!updateRequest) {
                    JSONObject main = chunk;
                    byte[] compress1 = compress(main.toString());
                    os.write(compress1, 0, compress1.length);
                    os.close();
                } else {
                    JSONObject main = new JSONObject();
                    JSONObject object = chunk;
                    main.put(JsonKeyUpdate, object);
                    main.putOpt(JsonKeyRemove, removedArray);
                    byte[] compress1 = compress(main.toString());
                    os.write(compress1, 0, compress1.length);
                    os.close();
                }                       
                Log.v(TAG, "Contact Synchronization Request finished: " + j);   
                String responseString;
                is = con.openInputStream();
                
                if ("gzip".equals(con.getEncoding())) {
                    responseString = deCompress(is);
                } else {
                    responseString = getStringFromStream(is);
                }
                
                Vector chunkResponse = getAddressBookResponseFromJson(responseString);
                        
                Log.v(TAG, "Processed chunk:" + j + " response: " + responseString);
                for (int k = 0; k < chunkResponse.size(); k++) {
                    if (update && !list.isEmpty() && count < listSize) {
                        Log.v(TAG, "PostAddressbook::before removing first element of list:AddressBookList.size():" + list.size());
                        list.removeElementAt(0);
                    }
                    list.addElement((DataModel)chunkResponse.elementAt(k));
                    count++;
                }
                Log.v(TAG, "PostAddressbook::after processig all the chunks :AddressBookList.size():" + list.size());
                chunkResponse.removeAllElements();
                System.gc();
            } catch (JSONException e) {
                         
                Log.v(TAG, "json exception: " + e.getMessage());
                throw  new ConnectionFailedException();
            } finally {
                handleFinally(is, con, os);
            }
        }
        
        if (update && listSize > requestSize) {
            for (int i = 0; i < listSize - requestSize && !list.isEmpty(); i++) {
                Log.v(TAG, "No of diff in local" + (listSize - requestSize));
                list.removeElementAt(0);
            }
        }
//        if (!diffContacts.isEmpty()) {
//            for (int i = 0; i < diffContacts.size(); i++) {
//                list.addElement(diffContacts.elementAt(i));
//            }
//        } 
//        requestEntries.removeAllElements();
        
        Log.v(TAG, "PostAddressbook::after sync:AddressBookList.size():" + list.size());
        list.sort();
        list.setSortOnChange(true);
        Log.v(TAG, "Post Address Book update completed:AddressBookList.size():"+ list.size());
        
        Runtime.getRuntime().gc();
        return false;
    }

    
    /**
     * This method extracts address book entries from post address book response.
     * @param response
     * @throws JSONException 
     */
     private static Vector/*<AddressbookEntry>*/ getAddressBookResponseFromJson(String response) throws JSONException {
        Vector responseAddressbook = new Vector();
        if (response != null) {
            JSONObject responseJson = new JSONObject(response);
            if (responseJson.has(JsonKeyAddressbook)) {
                JSONObject responseAddresses = responseJson.getJSONObject(JsonKeyAddressbook);
                Enumeration keys = responseAddresses.keys();

                while (keys.hasMoreElements()) {
                    String idString = keys.nextElement().toString();
                    JSONArray jArray = responseAddresses.getJSONArray(idString);
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject jEntry = (JSONObject) jArray.getJSONObject(i);                        
                        String msisdn = jEntry.getString(JsonKeyMsisdn);
                        if (!TextUtils.isEmpty(msisdn) && msisdn.startsWith("+")) {
                            responseAddressbook.addElement(new AddressBookEntry(idString, jEntry.getString(JsonKeyPhone), jEntry.getString(JsonKeyName), jEntry.getString(JsonKeyMsisdn), jEntry.getBoolean(JsonKeyOnHike)));
                        }
                    }
                }
            }
            if (responseJson.has(JsonKeyBlocklist)) {
                AppState.getUserDetails().setBlocklist(responseJson.optJSONArray(JsonKeyBlocklist));
                Log.v(TAG, "blocked list contains" + responseJson.optJSONArray(JsonKeyBlocklist));
            }
            responseJson = null;
        }
        return responseAddressbook;
    }

    
    /**
     * This method posts user name on the server.
     * @param name
     * @return
     * @throws IOException
     * @throws ConnectionFailedException 
     */
    public static boolean PostName(String name) throws IOException, ConnectionFailedException {
        Hashtable header = new Hashtable();
        InputStream is = null;

        header.put(keyContentType, ValueContentJSON);

        HttpConnection con = getConnection(BaseURL + URIName, HttpConnection.POST, header);
                 
        Log.v(TAG, "retrieving cookie");
        String cookie = AppState.getUserDetails().getCookie();
                 
        Log.v(TAG, "cookie retrieved");
        con.setRequestProperty(KeyCookie, cookie);
        con.setRequestProperty(JsonKeyUID, AppState.getUserDetails().getUid());
        OutputStream os = con.openOutputStream();
        try {
            JSONObject object = new JSONObject();
            //object.put(JsonKeyUID, AppState.getUserDetails().getUid());
            object.put(JsonKeyName, name);
            Log.v(TAG, "Post name request json: "+object.toString());
            writeObjectToOutputStream(os, object);
                     
            Log.v(TAG, "response code ========================= " + con.getResponseCode());
            is = con.openInputStream();
            String responseString = getStringFromStream(is);
            JSONObject jResponse = new JSONObject(responseString);
                     
            Log.v(TAG, jResponse.toString());
            String stat = jResponse.getString(JsonKeyStat);
            if (stat.trim().toLowerCase().equals(JsonKeyStatOk)) {
                         
                Log.v(TAG, "user details in set name: " + AppState.getUserDetails());
                AppState.getUserDetails().setName(name);
                return true;
            }
        } catch (JSONException e) {
                     
            Log.v(TAG, "json exception: " + e.getMessage());
        } finally {
            handleFinally(is, con, os);
        }
        return false;
    }
    

    /**
     * This method posts group name on the server of the specified group.
     * @param groupId
     * @param name
     * @return
     * @throws IOException
     * @throws ConnectionFailedException 
     */
    public static boolean postGroupName(String groupId, String name) throws IOException, ConnectionFailedException {
        Hashtable header = new Hashtable();
        OutputStream os = null;
        InputStream is = null;

        header.put(keyContentType, ValueContentJSON);
        final String finalUrl = BaseURL + URIGroup + "/" + groupId + URIGroupName;
        HttpConnection con = getConnection(finalUrl, HttpConnection.POST, header);
                 
        Log.v(TAG, "postGroupName()::finalUrl" + finalUrl);
        
        if (con != null) {
            try {
                String cookie = AppState.getUserDetails().getCookie();
                con.setRequestProperty(KeyCookie, cookie);
                con.setRequestProperty(JsonKeyUID, AppState.getUserDetails().getUid());
                os = con.openOutputStream();
                JSONObject object = new JSONObject();
                //object.put(JsonKeyUID, AppState.getUserDetails().getUid());
                object.put(JsonKeyName, name);
                writeObjectToOutputStream(os, object);

                       
                Log.v(TAG, "postGroupName()::responseCode: " + con.getResponseCode());
                is = con.openInputStream();
                String responseString = getStringFromStream(is);
                JSONObject jResponse = new JSONObject(responseString);
                        
                Log.v(TAG, "postGroupName()::response: " + jResponse.toString());
                String stat = jResponse.getString(JsonKeyStat);
                if (stat.trim().toLowerCase().equals(JsonKeyStatOk)) {
                    GroupList.getInstance().getEntryByGroupId(groupId).setGroupName(name);
                }
                return true;
            } catch (JSONException e) {
                         
                Log.v(TAG, "error posting group name: " + e.getClass().getName());
            } finally {
                handleFinally(is, con, os);
            }
        }
        return false;
    }

    
    /**
     * This method posts email and gender of user on the server.
     * @param email
     * @param male
     * @return
     * @throws IOException
     * @throws ConnectionFailedException 
     */
    public static boolean PostAccountInfo(String email, boolean male) throws IOException, ConnectionFailedException {
        Hashtable header = new Hashtable();
        InputStream is = null;
        header.put(keyContentType, ValueContentJSON);

        HttpConnection con = getConnection(BaseURL + URIPii, HttpConnection.POST, header);
                
        Log.v(TAG, "retrieving cookie");
        String cookie = AppState.getUserDetails().getCookie();
                 
        Log.v(TAG, "cookie retrieved");
        con.setRequestProperty(KeyCookie, cookie);
        con.setRequestProperty(JsonKeyUID, AppState.getUserDetails().getUid());
        OutputStream os = con.openOutputStream();
        try {
            JSONObject object = new JSONObject();
            if (email != null && !email.trim().equals(EMPTY_STRING)) {
                object.put(JsonKeyEmail, email);
            }
            String gender = male ? GENDER_MALE : GENDER_FEMALE;
            object.put(JsonKeyGender, gender);
            //object.put(JsonKeyUID, AppState.getUserDetails().getUid());
            writeObjectToOutputStream(os, object);
                     
            Log.v(TAG, "account info json" + object);
            Log.v(TAG, "response code ========================= " + con.getResponseCode());
            is = con.openInputStream();
            String responseString = getStringFromStream(is);
            JSONObject jResponse = new JSONObject(responseString);
                    
            Log.v(TAG, jResponse.toString());
            String stat = jResponse.getString(JsonKeyStat);
            if (stat.trim().toLowerCase().equals(JsonKeyStatOk)) {
                if (email != null) {
                    AppState.getUserDetails().setEmail(email);
                }
                AppState.getUserDetails().setGender(gender);
                return true;
            }
        } catch (JSONException e) {
                     
            Log.v(TAG, "json exception: " + e.getMessage());
        } finally {
            handleFinally(is, con, os);
        }
        return false;
    }
    

   /**
    * This method posts avatar image of user on the server.
    * @param path
    * @return
    * @throws IOException
    * @throws ConnectionFailedException 
    */
    public static boolean PostAvatar(String path) throws IOException, ConnectionFailedException {
        if (path == null || path.length() < 1) {
            return false;
        }
        Hashtable header = new Hashtable();
        header.put(keyContentType, ValueContentBinary);

        HttpConnection con = getConnection(BaseURL + URIAvatar, HttpConnection.POST, header);

        String cookie = AppState.getUserDetails().getCookie();
        con.setRequestProperty(KeyCookie, cookie);
        con.setRequestProperty(JsonKeyUID, AppState.getUserDetails().getUid());
        OutputStream os = con.openOutputStream();

        FileConnection fc = (FileConnection) Connector.open(path, Connector.READ);

        InputStream input = fc.openInputStream();
        byte[] image = new byte[input.available()];
        input.read(image);
        if (image.length > 0 && Image.createImage(image, 0, image.length) != null) {
            os.write(image);
            os.flush();
        } else {
            return false;
        }

        InputStream is = con.openInputStream();
        String responseString = getStringFromStream(is);
        try {
            JSONObject jResponse = new JSONObject(responseString);
            String stat = jResponse.getString(JsonKeyStat);
            if (stat.trim().toLowerCase().equals("ok")) {
                return true;
            }
        } catch (JSONException e) {
                   
            Log.v(TAG, "json exception: " + e.getMessage() + "\njson data: " + responseString);
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
                if (fc != null) {
                    fc.close();
                }
            } catch (IOException ex) {
            }
            handleFinally(is, con, os);
        }
        return false;
    }

    
    /**
     * This method posts group avatar image of specified group on the server.
     * @param groupId
     * @param path
     * @return
     * @throws IOException
     * @throws ConnectionFailedException 
     */
    public static boolean PostGroupAvatar(String groupId, String path) throws IOException, ConnectionFailedException {
        if (path == null || path.length() < 1) {
            return false;
        }
        Hashtable header = new Hashtable();
        InputStream is = null;
        header.put(keyContentType, ValueContentBinary);

        HttpConnection con = getConnection(BaseURL + URIGroup + "/" + groupId + URIGroupAvatar, HttpConnection.POST, header);

        String cookie = AppState.getUserDetails().getCookie();
        con.setRequestProperty(KeyCookie, cookie);
        con.setRequestProperty(JsonKeyUID, AppState.getUserDetails().getUid());
        OutputStream os = con.openOutputStream();
        FileConnection fc = (FileConnection) Connector.open(path, Connector.READ);
        InputStream input = fc.openInputStream();
        byte[] image = new byte[input.available()];
        input.read(image);
        if (image.length > 0 && Image.createImage(image, 0, image.length) != null) {
            os.write(image);
            os.flush();
        } else {
            return false;
        }

        is = con.openInputStream();
        String responseString = getStringFromStream(is);
        try {
            JSONObject jResponse = new JSONObject(responseString);
            String stat = jResponse.getString(JsonKeyStat);
            if (stat.trim().toLowerCase().equals("ok")) {
                return true;
            }

        } catch (JSONException e) {
                     
            Log.v(TAG, "json exception: " + e.getMessage() + "\njson data: " + responseString);
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
                if (fc != null) {
                    fc.close();
                }
            } catch (IOException ex) {
            }
            handleFinally(is, con, os);
        }
        return false;
    }

    
    /**
     * This method posts file using HTTP POST on the server.
     * @param path
     * @return
     * @throws IOException
     * @throws ConnectionFailedException 
     */
    public static FileInfo PostFile(String path) throws IOException, ConnectionFailedException {
        if (path == null || path.length() < 1) {
            return null;
        }
        Hashtable header = new Hashtable();
        OutputStream os = null;
        InputStream is = null;

        String filename = path;
        //TODO hardcoding..
        if (path.indexOf('/') != -1) {
            filename = path.substring(path.lastIndexOf('/') + 1);
        }

        header.put(keyContentName, filename);
        header.put(keyContentType, getContentTypeForInputFile(path));

        HttpConnection con = getConnection(BaseURL + URIFileTransfer, HttpConnection.POST, header);
        String cookie = AppState.getUserDetails().getCookie();
        con.setRequestProperty(KeyCookie, cookie);
        con.setRequestProperty(JsonKeyUID, AppState.getUserDetails().getUid());
        FileConnection fc = (FileConnection) Connector.open(path, Connector.READ);
        InputStream input = fc.openInputStream();
        byte[] file = new byte[input.available()];
        input.read(file);

        Hashtable params = new Hashtable();
        HttpMultipartRequest req = new HttpMultipartRequest(BaseURL + URIFileTransfer, params, "upload", filename, getContentTypeForInputFile(path), file);

        byte[] response = req.send(con);
        String responseString = new String(response);
        try {
            JSONObject jResponse = new JSONObject(responseString);
            String stat = jResponse.getString(JsonKeyStat);

            //TODO hardcoding..
            if (stat.trim().toLowerCase().equals("ok")) {
                JSONObject jFile = jResponse.getJSONObject(JsonKeyData);
                String fk = jFile.getString(JsonKeyFileKey);
                String fn = jFile.getString(JsonKeyFileName);
                String ct = jFile.getString(JsonKeyFileType);
                FileInfo datafile = new FileInfo(fk, fn, ct);
                return datafile;
            }
        } catch (JSONException e) {
                     
            Log.v(TAG, "json exception: " + e.getMessage() + "\njson data: " + responseString);
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
                if (fc != null) {
                    fc.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            handleFinally(is, con, os);
        }
        return null;
    }

    
    /**
     * This method posts file using HTTP PUT on the server.
     * @param path
     * @return
     * @throws IOException
     * @throws ConnectionFailedException 
     */
    public static FileInfo PutFile(String path) throws IOException, ConnectionFailedException {
        Hashtable header = new Hashtable();
        OutputStream os = null;
        InputStream is = null;

        String filename = path;
        if (path.indexOf('/') != -1) {
            filename = path.substring(path.lastIndexOf('/') + 1);
        }

        header.put(keyContentName, filename);
        header.put(keyContentType, getContentTypeForInputFile(path));

        HttpConnection con = getConnection(BaseURL + URIFileTransfer, "PUT", header);
        String cookie = AppState.getUserDetails().getCookie();
        con.setRequestProperty(KeyCookie, cookie);
        con.setRequestProperty(JsonKeyUID, AppState.getUserDetails().getUid());
        os = con.openOutputStream();

        FileConnection fc = (FileConnection) Connector.open(path, Connector.READ);
        InputStream input = fc.openInputStream();
        byte[] file = new byte[input.available()];
        input.read(file);

        os.write(file);
        os.flush();

        is = con.openInputStream();

        String responseString = getStringFromStream(is);
        try {
            JSONObject jResponse = new JSONObject(responseString);
            String stat = jResponse.getString(JsonKeyStat);

            //TODO hardcoding..
            if (stat.trim().toLowerCase().equals("ok")) {
                JSONObject jFile = jResponse.getJSONObject(JsonKeyData);
                String fk = jFile.getString(JsonKeyFileKey);
                String fn = jFile.getString(JsonKeyFileName);
                String ct = jFile.getString(JsonKeyFileType);
                FileInfo datafile = new FileInfo(fk, fn, ct);
                return datafile;
            }
        } catch (JSONException e) {
                     
            Log.v(TAG, "json exception: " + e.getMessage() + "\njson data: " + responseString);
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
                if (fc != null) {
                    fc.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            handleFinally(is, con, os);
        }
        return null;
    }

    
    
    /**
     * This method fetches file data in base-64 format.
     * @param fid
     * @return
     * @throws IOException
     * @throws ConnectionFailedException 
     */
    public static String GetFile(String fid) throws IOException, ConnectionFailedException {
        Hashtable header = new Hashtable();

        HttpConnection con = getConnection(BaseURL + URIFileTransfer + ParamFileTransfer + fid, HttpConnection.GET, header);

        String cookie = AppState.getUserDetails().getCookie();
        con.setRequestProperty(KeyCookie, cookie);
        con.setRequestProperty(JsonKeyUID, AppState.getUserDetails().getUid());
        InputStream is = con.openInputStream();
        byte[] file = new byte[is.available()];
        is.read(file);
        byte[] decoded = Base64.decode(file);
        return decoded.toString();
    }

    
    /**
     * This method returns list of files uploaded previously by the user
     
     * @return {@link String} array
     */
    public static FileInfo[] GetFileList() throws IOException, ConnectionFailedException {
        Hashtable header = new Hashtable();
        HttpConnection con = getConnection(BaseURL + URIFileTransfer, HttpConnection.GET, header);
        String cookie = AppState.getUserDetails().getCookie();
        con.setRequestProperty(KeyCookie, cookie);
        con.setRequestProperty(JsonKeyUID, AppState.getUserDetails().getUid());
        InputStream is = con.openInputStream();
        String responseString = getStringFromStream(is);
        try {
            JSONObject jResponse = new JSONObject(responseString);
            JSONArray files = jResponse.getJSONArray(JsonKeyFiles);
            FileInfo[] filesArray = new FileInfo[files.length()];
            for (int i = 0; i < files.length(); i++) {
                JSONObject file = files.getJSONObject(i);
                String fk = file.getString(JsonKeyFileKey);
                String fn = file.getString(JsonKeyFileName);
                String ct = file.getString(JsonKeyFileType);
                filesArray[i] = new FileInfo(fk, fn, ct);
            }
            return filesArray;
        } catch (JSONException e) {
                     
            Log.v(TAG, "json exception: " + e.getMessage() + "\njson data: " + responseString);
        } finally {
            handleFinally(is, con, null);
        }
        return null;
    }

    
    /**
     * This method returns the content-type of the specified file.
     * @param file
     * @return 
     */
    private static String getContentTypeForInputFile(String file) {
        String contentType = ValueContentBinary;
        if (file.endsWith("png")) {
            contentType = "image/png";
        } else if (file.endsWith("jpeg") || file.endsWith("jpg")) {
            contentType = "image/jpg";
        } else if (file.endsWith("txt")) {
            contentType = "text/plain";
        }
        return contentType;
    }

    
    /**
     * This method sends push notification data on the server.
     * @param nid
     * @return
     * @throws IOException
     * @throws ConnectionFailedException 
     */
    public static boolean sendPushNotifData(String nid) throws IOException, ConnectionFailedException {
        boolean success = false;
        Hashtable header = new Hashtable();
        InputStream is = null;

        header.put(keyContentType, ValueContentJSON);
        final String finalUrl = BaseURL + URI_ACCOUNT_DEVICE;
        HttpConnection con = getConnection(finalUrl, HttpConnection.POST, header);
                 
        Log.v(TAG, "retrieving cookie");
        String cookie = AppState.getUserDetails().getCookie();
                 
        Log.v(TAG, "cookie retrieved");
        con.setRequestProperty(KeyCookie, cookie);
        con.setRequestProperty(JsonKeyUID, AppState.getUserDetails().getUid());
        OutputStream os = con.openOutputStream();
        try {
            JSONObject object = new JSONObject();
            object.put(JSON_KEY_DEV_TYPE, VAL_DEV_TYPE);
            object.put(JSON_KEY_DEV_TOKEN, nid);
                    
            Log.v(TAG, "Sending NID to Hike server--------" + object.toString());
            writeObjectToOutputStream(os, object);

                     
            Log.v(TAG, "response code ========================= " + con.getResponseCode());
            is = con.openInputStream();
            String responseString = getStringFromStream(is);
            JSONObject jResponse = new JSONObject(responseString);
            String stat = jResponse.getString(JsonKeyStat);
            if (VAL_PASS.equals(stat)) {
                //TODO save to RMS for later..
                success = true;
            }
        } catch (JSONException e) {
                     
            Log.v(TAG, "json exception: " + e.getMessage());
        } finally {
            handleFinally(is, con, os);
        }
        return success;
    }

    
    /**
     * This method checks for app update on the server.
     * @param currentVer
     * @param DeviceId
     * @throws ConnectionFailedException
     * @throws IOException 
     */
    public static void checkForUpdate(String currentVer, String DeviceId) throws ConnectionFailedException, IOException {
        Hashtable header = new Hashtable();
        HttpConnection con = null;
        InputStream is = null;

        con = getConnection(DownloadURL + URI_APP_Update, HttpConnection.GET, header);
        Log.v(TAG, "Update URL: " + DownloadURL + URI_APP_Update);
        is = con.openInputStream();
        String responseString = getStringFromStream(is);
        try {
            JSONObject jResponse = new JSONObject(responseString);            
            Log.v(TAG, "App Update JSON Response " + jResponse.toString());
            // check if the new version equals the current version
            String newVer = jResponse.optString(JSON_APP_UPDATE_LATEST);
            String criticalVer = jResponse.optString(JSON_APP_UPDATE_CRITICAL);
            String url = jResponse.optString(JSON_APP_UPDATE_URL);   
            AppState.jad = new JADInfoAttribute(url, criticalVer, newVer);
        } catch (JSONException e) {                     
            Log.v(TAG, "json exception: " + e.getMessage());             
        } finally {
            handleFinally(is, con, null);
        }
    }
    
    
    /**
     * This method requests a call for PIN verification.
     * @param normalizedMSISDN
     * @throws ConnectionFailedException
     * @throws IOException 
     */
    public static void callMe(String normalizedMSISDN) throws ConnectionFailedException, IOException {
              
        Hashtable header = new Hashtable();
        InputStream is = null;

        header.put(keyContentType, ValueContentJSON);
        final String finalUrl = BaseURL + URI_CALL_ME;

        HttpConnection con = getConnection(finalUrl, HttpConnection.POST, header);
        OutputStream os = con.openOutputStream();
        try {
            JSONObject object = new JSONObject();
            object.put(JsonKeyMsisdn, normalizedMSISDN);

            writeObjectToOutputStream(os, object);
            is = con.openInputStream();
            String responseString = getStringFromStream(is);
        } catch (JSONException e) {
            Log.v(TAG, "json exception: " + e.getMessage());
        } finally {
            handleFinally(is, con, null);
        }
        // return null;
    }
    
      /**
     * This method sends unlink account request to server.
     *
     * @return
     * @throws IOException
     * @throws ConnectionFailedException
     */
    public static boolean UnlinkAccount() throws IOException, ConnectionFailedException {
        Hashtable header = new Hashtable();
        OutputStream os = null;

        header.put(keyContentType, ValueContentJSON);

        HttpConnection con = getConnection(BaseURL + URIAccountUnlink, HttpConnection.POST, header);
        String cookie = AppState.getUserDetails().getCookie();
        con.setRequestProperty(KeyCookie, cookie);
        OutputStream outputStream = con.openOutputStream();
        outputStream.flush();
        InputStream is = con.openInputStream();
        //#ifdef DEBUG         
        //#         Log.v(TAG, "Delete Account response code ========================= " + con.getResponseCode());
        //#endif
        String responseString = getStringFromStream(is);
        //#ifdef DEBUG         
        //#         Log.v(TAG, "Response string on delete accont" + responseString);
        //#endif
        try {
            JSONObject jResponse = new JSONObject(responseString);
            String stat = jResponse.getString(JsonKeyStat);
            if (stat.trim().toLowerCase().equals("ok")) {
                return true;
            }
        } catch (JSONException e) {
            //#ifdef DEBUG         
            //#             Log.v(TAG, "json exception: " + e.getMessage() + "\njson data: " + responseString);
            //#endif
        } finally {
            handleFinally(is, con, os);
        }
        return false;
    }
    
    /**
     * Compress the data received as a String message.
     * @param message <code>String</code> to be compressed
     * @param out OutputStream to write compressed data to.
     *  It is not closed in this method.
     * @throws IOException if there is any error during writing
     */
    public static byte[] compress(String message) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gz = new GZIPOutputStream(out);

        gz.write(message.getBytes("UTF-8"));
        gz.flush();
        gz.close();

        out.flush();
        byte[] array = out.toByteArray();
        out.close();

        return array;

    }

    
    /**
     * Compress the data received as a String message.
     *
     * @param message <code>String</code> to be compressed
     * @param out OutputStream to write compressed data to. It is not closed in
     * this method.
     * @throws IOException if there is any error during writing
     */
    public static void compress(String message, OutputStream out)
            throws IOException {
        GZIPOutputStream gz = new GZIPOutputStream(out);
        byte[] bytes = message.getBytes("UTF-8");
        gz.write(bytes, 0, bytes.length);
        gz.close();
      
    }

    
    
    /**
     * Decompress the data received in the Stream. The stream is not closed in
     * this method.
     *
     * @param in <code>InputStream</code> of the connection where the compressed
     * data is received.
     * @return <code>String</code> representing the data decompressed
     * @throws IOException if there is any error during reading
     */
    public static String deCompress(InputStream in) throws IOException {
        StringBuffer sb = new StringBuffer();

        GZIPInputStream gz = new GZIPInputStream(in);
        int c = 0;
        while ((c = gz.read()) != -1) {
            sb.append((char) c);
        }

        gz.close();

        return sb.toString();
    }

    
    /**
     * Decompress the data received in the array of bytes.
     *
     * @param bytes data array compressed
     * @return <code>String</code> representing the data decompressed
     * @throws IOException if there is any error during reading
     */
    public static String deCompress(byte[] bytes) throws IOException {
        StringBuffer sb = new StringBuffer();

        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        GZIPInputStream gz = new GZIPInputStream(in);
        int c = 0;
        while ((c = gz.read()) != -1) {
            sb.append((char) c);
        }

        gz.close();

        return sb.toString();
    }
}
