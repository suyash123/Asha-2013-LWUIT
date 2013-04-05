/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.ui;

import com.bsb.hike.dto.AddressBookEntry;
import com.bsb.hike.dto.AddressBookList;
import com.bsb.hike.dto.AppState;
import com.bsb.hike.ui.component.PatchedTextArea;
import com.bsb.hike.util.Log;
import com.bsb.hike.util.TextUtils;
import com.bsb.hike.util.Validator;
import com.sun.lwuit.Command;
import com.sun.lwuit.Display;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import java.util.Enumeration;

/**
 *
 * @author Ranjith Yadav
 * @author Sudheer Keshav Bhat
 */
public class FormSelectContact extends FormSelectBase implements PatchedTextArea.TextChangedListener {

    private Command mBackCmd;
    private static final String TAG = "FormSelectContact";
    private StringBuffer mSearchBuffer = new StringBuffer();

    
    /**
     * Constructor of selecting contact screen. This form shows the list of user contacts.
     */
    public FormSelectContact() {

        //#if nokia2_0
//#         setTitle(LBL_NEW_CHAT);
        //#endif

        initCommands();
        mSearchTxtFld.setTextChangedListener(this);

        mFilteredContactLst.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                onContactSelected();
            }
        });
    }

    
    /**
     * This method is called when user selects a contact for one to one chat.
     */
    private void onContactSelected() {
        if (mFilteredContactLst != null && mFilteredContactLst.getSelectedIndex() >= 0) {

            Log.v(TAG, "index: " + mFilteredContactLst.getSelectedIndex());
            if (mFilteredAddressBook.size() > mFilteredContactLst.getSelectedIndex()) {
                AddressBookEntry entry = (AddressBookEntry) mFilteredAddressBook.elementAt(mFilteredContactLst.getSelectedIndex());
                if (entry.getMsisdn() != null && entry.getPhoneNumber() != null) {
                    if (entry.getId().equals(UNKNOWN_CONTACT_ID)) {
                        AppState.getUserDetails().setSelectedMsisdn(entry.getName());
                    } else {
                        AppState.getUserDetails().setSelectedMsisdn(entry.getMsisdn());
                    }
                    DisplayStackManager.showForm(DisplayStackManager.FORM_CHAT);
                    //TODO: To see if we can release resources from a container.
                }
            }
        }
    }

    
    /**
     * This method initializes all the commands which are there on the current form.
     */
    private void initCommands() {
        mBackCmd = new Command(LBL_BACK) {
            public void actionPerformed(ActionEvent evt) {
                if (AppState.workFlow == WORKFLOW_FORWARD) {
                    AppState.workFlow = WORKFLOW_BACK;
                    DisplayStackManager.showForm(DisplayStackManager.FORM_CHAT);
                } else {
                    DisplayStackManager.showForm(DisplayStackManager.FORM_CONVERSATION);
                }
            }
        };
        addCommand(mBackCmd);
        setBackCommand(mBackCmd);
    }

    
    /**
     * This method is called on onTextChangeListener() of the search box. This method will show only the filtered contacts to the user.
     * @param string 
     */
    private void searchContactlist(String string) {
        String searchString = string.trim();
        if (!TextUtils.isEmpty(searchString)) {
            mFilteredAddressBook.removeAllElements();
            String validatedNum = Validator.validatePhoneString(searchString);
            if (validatedNum != null) {
                boolean isValidLength = validatedNum.length() - (AppState.getUserDetails().getCountryCode().length() + 1) >= CharLimit.PHONE_NUMBER_MIN && validatedNum.length() <= CharLimit.PHONE_NUMBER_MAX;
                String msisdn = isValidLength ? UNKNOW_CONTACT_CHAT_MESSAGE : UNKNOWN_CONTACT_NAME;
                String name = isValidLength ? validatedNum : searchString;
                String phone = isValidLength ? validatedNum : null;
                AddressBookEntry addressBookentry = new AddressBookEntry(UNKNOWN_CONTACT_ID, phone, name, msisdn, false);
                if (AddressBookList.getInstance().getEntryByMsisdn(phone) == null) {
                    mFilteredAddressBook.addElement(addressBookentry);
                }
            }
            int length1 = AddressBookList.getInstance().size();
            for (int i = 0; i < length1; i++) {
                AddressBookEntry addressBookentry = (AddressBookEntry) AddressBookList.getInstance().elementAt(i);
                String contactName = addressBookentry.getName().toUpperCase();
                String contactNumber = addressBookentry.getMsisdn();
                searchString = searchString.toUpperCase();
                if (contactName.indexOf(searchString) >= 0 || contactNumber.indexOf(searchString) >= 0) {
                    mFilteredAddressBook.addElement(addressBookentry);
                }
            }
        } else {
            mFilteredAddressBook.removeAllElements();
            Enumeration enums = AddressBookList.getInstance().elements();
            while (enums.hasMoreElements()) {
                mFilteredAddressBook.addElement(enums.nextElement());
            }

        }
        //flushReplace();
        revalidate();
        mFilteredContactLst.setShouldCalcPreferredSize(true);
    }

    
    /**
     * This delegated method is called just after showing the form.
     */
    protected void onShow() {
        super.onShow();
        mFilteredContactLst.requestFocus();
        mSearchTxtFld.setText(EMPTY_STRING);
        mSearchBuffer.setLength(0);
        mFilteredAddressBook.removeAllElements();
        Enumeration enums = AddressBookList.getInstance().elements();
        while (enums.hasMoreElements()) {
            mFilteredAddressBook.addElement(enums.nextElement());
        }
        if (!mFilteredAddressBook.isEmpty()) {
            mFilteredContactLst.setSelectedIndex(0, true);
        }

        //flushReplace();
        revalidate();
    }

    
    /**
     * This is an overrided call of text change. This method gets called on every text change in the search box.
     * @param textArea
     * @param previousText
     * @param currentText 
     */
    public void onTextChange(PatchedTextArea textArea, String previousText, String currentText) {
        if (!(Display.getInstance().getCurrent() instanceof FormSelectContact)) {
            return;
        }
        
        Log.v(TAG, " ************* testing PatchedTextArea " + currentText);
        Log.v(TAG, " ************* String Length  " + currentText.length());
        int index = currentText.lastIndexOf(';');

        Log.v(TAG, " ************* Last Index " + index);
        if (index > 0) {
            searchContactlist(currentText.substring(index + 1));

            Log.v(TAG, " ************* Last Character " + currentText.substring(index));
        } else {
            searchContactlist(currentText);
        }
    }
}