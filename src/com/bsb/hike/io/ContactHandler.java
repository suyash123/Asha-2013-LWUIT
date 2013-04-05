package com.bsb.hike.io;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import com.bsb.hike.dto.AddressBookRequestEntry;
import com.bsb.hike.util.AppConstants;
import com.bsb.hike.util.Log;
import com.bsb.hike.util.Validator;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.pim.Contact;
import javax.microedition.pim.ContactList;
import javax.microedition.pim.PIM;

/**
 *
 * @author Shreyas
 */
public class ContactHandler implements AppConstants {

    /**
     * Connection to Contacts Server
     */
//    private static ContactsConnection contactsConn = null;
//    private static ContactsConnection contactsConnSub = null;
    private static ContactList contactList = null;
    /**
     * Keep track of change in contact. Used in events notifications.
     */
//    private byte change = -1;
    /**
     * The data sources to use for a contact operation.
     */
//    private ContactSource[] src;
    /**
     * Possible value for contacts source
     */
    public static int SIM = 1;
    /**
     * Possible value for contacts source
     */
    public static int PHONE = 2;
    /**
     * Possible value for contacts source
     */
    public static int ACCORDING_TO_USER_SETTING = 3;
    private static String TAG = "ContactHandler";

    public ContactHandler() {
        // Initialize contactsVector used to display in midlet

        // filter = new Filter();
        // initialize contacts Settings connection
        openContactsConnection();
        //contactsVector = getContacts();
    }

    /**
     * Open the Contacts server connection or uses fake connection to test UI
     * functionality
     */
    private void openContactsConnection() {
        try {
            /*contactsConn = ContactsConnection.openConnection();
            contactsConnSub = ContactsConnection.openConnection();
            contactsConnSub.subscribe(this);*/

            contactList = (ContactList)PIM.getInstance().openPIMList(PIM.CONTACT_LIST, PIM.READ_WRITE);
        } catch (Exception e) {
                     
            Log.v(TAG, "Opening Contacts connection failed - ");// + e.getClass().getName() + " contacts conn is: " + contactsConn + " contacts conn sub is: " + contactsConnSub);
        }
    }

    /**
     * Called when user presses 'Get First' soft key to display contacts from
     * top of Contact list.
     */
    private Vector/*<AddressBookRequestEntry>*/ getContacts() {
        Vector contactsVector = new Vector(100, 50);
        try {
                     
            Log.v(TAG, "Starting fetching Contacts");


            Enumeration contactEnum = contactList.items();
            while(contactEnum.hasMoreElements()){
                Contact contact = (Contact)contactEnum.nextElement();
                addContact(contact, contactsVector);
                contact = null;
            }
            /*src = new ContactSource[2];
            src[0] = new ContactSource();
            src[0].setContactSource(SIM);
            src[1] = new ContactSource();
            src[1].setContactSource(PHONE);
            //  while un-commenting below lines, make sure to set array size 3
            //  src[2] = new ContactSource();
            //  src[2].setContactSource(ACCORDING_TO_USER_SETTING);

            Contact[] contactsArrayFirst = contactsConn.getFirst(src, 1, null);
            if (contactsArrayFirst != null) {
                Contact nextContact = contactsArrayFirst[0];

                for (int j = 0; j < 200; j++) {
                    try {
                        Contact[] contactsArray = contactsConn.getNext(src, nextContact, null, true, 10);
                        if (contactsArray != null && contactsArray.length > 0) {
                             
                            Log.v("got next" + j, contactsArray.length + EMPTY_STRING);
                            if (contactsArray.length == 1) {
                                addContact(contactsArray[0], contactsVector);
                                break;
                            }
                            for (int i = 0; i < contactsArray.length - 1; i++) {
                                addContact(contactsArray[i], contactsVector);
                            }
                            nextContact = contactsArray[contactsArray.length - 1];
                        } else {
                            break;
                        }
                        contactsArray = null;
                    } catch (Throwable e) {
                     
                        Log.v(TAG, e.getClass().getName() + " Exception handled");
                    }
                }
                nextContact = null;
            }
            contactsArrayFirst = null;
            src[0] = null;
            src[1] = null;
            src = null;*/
        } catch (Exception ex) {
            ex.printStackTrace();
        }
              Runtime.getRuntime().gc();
        return contactsVector;
    }

    private void addContact(Contact contact, Vector/*<AddressBookRequestEntry>*/ contactsVector) {
        String [] names = null;
        if (contactList.isSupportedField(Contact.NAME))
            names = contact.getStringArray(Contact.NAME, Contact.ATTR_NONE);
        String name = "";
        if (contactList.isSupportedArrayElement(Contact.NAME, Contact.NAME_GIVEN) && names != null)
             name = names[Contact.NAME_GIVEN];//contact.getDisplayName();
        if (contactList.isSupportedArrayElement(Contact.NAME, Contact.NAME_FAMILY) && names != null){
            if(!name.equals(""))
                name = " " + names[Contact.NAME_FAMILY];//contact.getDisplayName();
            else
                name =names[Contact.NAME_FAMILY];
        }

        String id = String.valueOf(contact.getInt(Contact.UID, 0));//contact.getContactID();
        
        Vector numbers = new Vector();
        if (contactList.isSupportedAttribute(Contact.TEL, Contact.ATTR_MOBILE))
            numbers.addElement(contact.getString(Contact.TEL, Contact.ATTR_MOBILE));
        if (contactList.isSupportedAttribute(Contact.TEL, Contact.ATTR_HOME))
            numbers.addElement(contact.getString(Contact.TEL, Contact.ATTR_HOME));
        if (contactList.isSupportedAttribute(Contact.TEL, Contact.ATTR_OTHER))
            numbers.addElement(contact.getString(Contact.TEL, Contact.ATTR_OTHER));
        if (contactList.isSupportedAttribute(Contact.TEL, Contact.ATTR_PAGER))
            numbers.addElement(contact.getString(Contact.TEL, Contact.ATTR_PAGER));
        if (contactList.isSupportedAttribute(Contact.TEL, Contact.ATTR_FAX))
            numbers.addElement(contact.getString(Contact.TEL, Contact.ATTR_FAX));
//        PhoneNumber[] numbers = contact.getNumbers();
        if (numbers != null) {
            for (int k = 0; k < numbers.size(); k++) {
                String number = (String)numbers.elementAt(k);//numbers[k].getNumber();
                if (Validator.validatePhoneNum(number)) {
                    AddressBookRequestEntry entry = new AddressBookRequestEntry(id, number, name);
                    contactsVector.addElement(entry);
                    entry = null;
                }
                number = null;
            }
        }
        name = null;
        id = null;
        numbers = null;
    }

    /**
     * The client can use the getNumEntries message to get the number of
     * contacts
     */
    /*private int getNumEntries() {
        int noOfContacts = 10;
        try {
            if (contactsConn != null) {
                noOfContacts = contactsConn.getNumEntries(src, null);
            }
        } catch (Exception ce) {
            ce.printStackTrace();
                     
            Log.v("Error in fetching no of contacts now will return default", ce.getClass().getName());
        }
        return noOfContacts;
    }*/

    public static Vector/*<AddressbookRequestEntry>*/ getAddressbookRequestArray(boolean update) {
        Vector resultAll = null;
        if (!AppConstants.LOCAL) {
             
            Log.v(TAG, "fetching Device contacts");
            ContactHandler handler = new ContactHandler();
            resultAll = handler.getContacts();
            Runtime.getRuntime().gc();
        } else {
            resultAll = new Vector();
             
            Log.v(TAG, "fetching dummy contacts");
            
            resultAll.addElement(new AddressBookRequestEntry("00510", "8088005119", "shekhar"));
            resultAll.addElement(new AddressBookRequestEntry("00507", "9663758619", "Puneet"));
            resultAll.addElement(new AddressBookRequestEntry("00509", "9686123998", "sudheer"));
            resultAll.addElement(new AddressBookRequestEntry("00587", "9663547386", "Ankit"));
            resultAll.addElement(new AddressBookRequestEntry("00511", "9590770395", "Ankit rel"));
            resultAll.addElement(new AddressBookRequestEntry("00512", "8800950759", "Puneet Ag"));
            resultAll.addElement(new AddressBookRequestEntry("00513", "9538417586", "sharon"));
            resultAll.addElement(new AddressBookRequestEntry("00514", "9448283881", "bsnl"));
            resultAll.addElement(new AddressBookRequestEntry("00516", "9611135458", "test"));
            resultAll.addElement(new AddressBookRequestEntry("00518", "9611135743", "Ankit Kumar"));
            resultAll.addElement(new AddressBookRequestEntry("00522", "9770350246", "Ankit ind2"));
            resultAll.addElement(new AddressBookRequestEntry("00523", "9770350856", "Ankit ind3"));
            resultAll.addElement(new AddressBookRequestEntry("00524", "9844386777", "robo"));
            resultAll.addElement(new AddressBookRequestEntry("00508", "8750564839 ", "Idea"));
            resultAll.addElement(new AddressBookRequestEntry("00525", "9818789836", "Airtel1"));
            resultAll.addElement(new AddressBookRequestEntry("00526", "9818789836", "Airtel2"));
            resultAll.addElement(new AddressBookRequestEntry("00528", "9844386777", "Nokia"));
            resultAll.addElement(new AddressBookRequestEntry("00529", "8375975893", "SMS user"));
            resultAll.addElement(new AddressBookRequestEntry("00508", "9961300771", "Jacqueline Benjamin Angelica Michael"));
            resultAll.addElement(new AddressBookRequestEntry("00506", "7353980930", "ShreeKumar"));
            resultAll.addElement(new AddressBookRequestEntry("00515", "9901152525", "airtel52525"));
            resultAll.addElement(new AddressBookRequestEntry("00577", "9900291939", "robo-at"));
            resultAll.addElement(new AddressBookRequestEntry("00543", "7676560906", "Murtuzaa Saifee"));
                    
            Log.v(TAG, "fetched dummy contacts: " + resultAll.size());
        }
        return resultAll;
    }
}
