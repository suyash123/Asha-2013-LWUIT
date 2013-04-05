/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.ui;

import com.bsb.hike.dto.AddressBookEntry;
import com.bsb.hike.dto.AddressBookList;
import com.bsb.hike.dto.AppState;
import com.bsb.hike.dto.GroupMember;
import com.bsb.hike.io.mqtt.MqttManager;
import com.bsb.hike.ui.component.PatchedTextArea;
import com.bsb.hike.util.AppResource;
import com.bsb.hike.util.Log;
import com.bsb.hike.util.TextUtils;
import com.bsb.hike.util.UIHelper;
import com.bsb.hike.util.Validator;
import com.sun.lwuit.Command;
import com.sun.lwuit.Display;
import com.sun.lwuit.Image;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author Sree Kumar a.v
 *
 */
public class FormSelectGroupContact extends FormSelectBase implements PatchedTextArea.TextChangedListener{

    private Command mBackCmd;
    private Command mDoneCommand;
    private static final String TAG = "FormSelectContact";
    private Vector sGroupChatSelected = new Vector();
    private int count = 0;
    private int lastIndex = -1;
    private int SMS_USER_LIMIT = 5;
    private Vector mBaseAddressBook = null; ;
    private int displayWidth;

    /**
     * Constructor of selecting group contact screen. This form shows the list of user contacts.
     */
    public FormSelectGroupContact() {
        displayWidth = Display.getInstance().getDisplayWidth();
        mBaseAddressBook = new Vector();
        initCommands();
        mFilteredContactLst.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                onContactSelected();
            }
        });
    }

    /**
     * This method is called when user selects a contact for group chat.
     */
    private void onContactSelected() {
        if (mFilteredContactLst != null && mFilteredAddressBook.size() > mFilteredContactLst.getSelectedIndex()) {
                   
            Log.v(TAG, "index: " + mFilteredContactLst.getSelectedIndex());
               Log.v(TAG, "Address book size: " + mFilteredAddressBook.size());
            AddressBookEntry entry = (AddressBookEntry) mFilteredAddressBook.elementAt(mFilteredContactLst.getSelectedIndex());
            if (entry.getPhoneNumber() == null) {
                return;
            }
            if (canAddMore() && !sGroupChatSelected.contains(entry) && !entry.getMsisdn().equals(AppState.getUserDetails().getMsisdn())) {
                        
                Log.v(TAG, " Name Added is :******  :" + entry.getName());
//                if ((!entry.isOnHike()) && !canAddMoreSmsUser()) {
//                    UIHelper.showToast(MSG_SMSUSER_LIMIT);
//                    return;
//                }
                mFilteredAddressBook.removeElement(entry);
                if(entry.getId().equals(UNKNOWN_CONTACT_ID)){
                    entry = new AddressBookEntry(entry.getId(), entry.getPhoneNumber(), entry.getName(), entry.getName(), entry.isOnHike());
                }
                sGroupChatSelected.addElement(entry);
                //flushReplace();
                revalidate();
            }else if(entry.getMsisdn().equals(AppState.getUserDetails().getMsisdn())) {
                UIHelper.showToast(AppState.getUserDetails().getMsisdn() + MSG_OWN_NUMBER_WARNING);
                return;
            }
            mSearchTxtFld.setText(getSelectedGroupContactNames());
            lastIndex = mSearchTxtFld.getText().lastIndexOf(';');

        }

        if (count < 2) {
            removeCommand(mDoneCommand);
            setDoneCommand(AppResource.getImageFromResource(PATH_IC_TICK_MARK));
            addCommand(mDoneCommand);
            addCommand(mBackCmd);
            setDefaultCommand(mDoneCommand);
            setDefaultCommand(mBackCmd);
            count++;
        }
    }

    
    /**
     * This method initializes all the commands which are there on the current form.
     */
    private void initCommands() {
        mBackCmd = new Command(LBL_BACK) {
            public void actionPerformed(ActionEvent evt) {
                //mSearchTxtFld.setText(LBL_SEARCH);
                sGroupChatSelected.removeAllElements();
                mSearchTxtFld.setTextChangedListener(null);
                mSearchTxtFld.setText("");
                if (AppState.fromScreen == DisplayStackManager.FORM_GROUP_INFO) {
                    DisplayStackManager.showForm(DisplayStackManager.FORM_GROUP_INFO);
                } else if (AppState.fromScreen == DisplayStackManager.FORM_CONVERSATION) {
                    DisplayStackManager.showForm(DisplayStackManager.FORM_CONVERSATION);
                }
//                mFilteredAddressBook = null;
//                mFilteredContactLst = null;
            }
        };
        setDoneCommand(AppResource.getImageFromResource(sentInviteImage));
        addCommand(mBackCmd);
        addCommand(mDoneCommand);
        setBackCommand(mBackCmd);
        setDefaultCommand(mDoneCommand);
                 
        Log.v(TAG, "command initalizing Group**************** ");
    }

    
    /**
     * This method initializes the done command. User can navigate to chat form when minimum of 2 contacts is selected for group chat by
     * clicking on this command.
     * @param icon 
     */
    private void setDoneCommand(final Image icon) {
        mDoneCommand = new Command(LBL_DONE, icon) {
            boolean clicked = false;

            public void actionPerformed(ActionEvent evt) {
                if (!clicked && canFinishAdding() && !sGroupChatSelected.isEmpty()) {
                    clicked = true;
                    Hashtable list = getSelectedGroupContact();
                    sGroupChatSelected.removeAllElements();
                    mBaseAddressBook.removeAllElements();
                    String grpId = EMPTY_STRING;
                    switch (AppState.fromScreen) {
                        case DisplayStackManager.FORM_GROUP_INFO:
                                     
                            Log.v(TAG, AppState.getUserDetails().getCurrentGrp());
                            MqttManager.addUsersToGrpChat(AppState.getUserDetails().getCurrentGrp(), list);
                            grpId = AppState.getUserDetails().getCurrentGrp().getGroupId();
                            break;

                        case DisplayStackManager.FORM_CONVERSATION:
                            grpId = MqttManager.createGroupChat(list);
                            break;
                    }
                    AppState.getUserDetails().setSelectedMsisdn(grpId);
                    mSearchTxtFld.setText(EMPTY_STRING);
                    DisplayStackManager.showForm(DisplayStackManager.FORM_CHAT);
                    mSearchTxtFld.setTextChangedListener(null);
                    clicked = false;
//                    mFilteredAddressBook = null;
//                    mFilteredContactLst = null;
                }
            }
        };
    }

    
    /**
     * This method is called on onTextChangeListener() of the search box. This method will show only the filtered contacts to the user.
     * @param string 
     */
    private void searchContactlist(String string) {
        String searchString = string.trim();
        mFilteredAddressBook.removeAllElements();
        if (!TextUtils.isEmpty(searchString)) {
            String validatedNum = Validator.validatePhoneString(searchString);
            if (validatedNum != null) {
                boolean isValidLength = validatedNum.length() - (AppState.getUserDetails().getCountryCode().length()+1) >= CharLimit.PHONE_NUMBER_MIN && validatedNum.length() <= CharLimit.PHONE_NUMBER_MAX;
                String msisdn = isValidLength ? UNKNOW_CONTACT_GROUP_CHAT_MESSAGE : UNKNOWN_CONTACT_NAME;
                String name = isValidLength ? validatedNum : searchString;
                String phone = isValidLength ? validatedNum : null;
                AddressBookEntry addressBookentry = new AddressBookEntry(UNKNOWN_CONTACT_ID, phone, name, msisdn, false);
                if(AddressBookList.getInstance().getEntryByMsisdn(phone) == null && !containsMsisdn(phone) && !name.equals(AppState.getUserDetails().getMsisdn())) {
                    mFilteredAddressBook.addElement(addressBookentry);
                }
            }
            int length1 = mBaseAddressBook.size();
            for (int i = 0; i < length1; i++) {
                AddressBookEntry addressBookentry = (AddressBookEntry) mBaseAddressBook.elementAt(i);
                String contactName = addressBookentry.getName().toUpperCase();
                String contactNumber = addressBookentry.getMsisdn();
                searchString = searchString.toUpperCase();
                if (contactName.indexOf(searchString) >= 0 || contactNumber.indexOf(searchString) >= 0) {
                    mFilteredAddressBook.addElement(addressBookentry);
                }
            }
        } else {
            Enumeration enums = mBaseAddressBook.elements();
            while (enums.hasMoreElements()) {
                mFilteredAddressBook.addElement(enums.nextElement());
            }
        }
        Enumeration enums = sGroupChatSelected.elements();
        while (enums.hasMoreElements()) {
            mFilteredAddressBook.removeElement(enums.nextElement());
        }
        //flushReplace();
        revalidate();
        mFilteredContactLst.setShouldCalcPreferredSize(true);
    }
    
    
    /**
     * This method is to check whether the passed MSISDN is contained in the Address book or not.
     * @param msisdn
     * @return 
     */
    private boolean containsMsisdn(String msisdn) {
        
        Enumeration e = sGroupChatSelected.elements();
        while(e.hasMoreElements()) {
            AddressBookEntry addressBookEntry = (AddressBookEntry)e.nextElement();
            if(addressBookEntry.getMsisdn().equals(msisdn)) {
                return true;
            }
        }
        return false;
    }
 
    
    /**
     * This delegated method is called just after showing the form. This method removes all the contacts and adds them again in the vector.
     * This operation is necessary as we have to filter out the contacts which are already added in the group chat and user wants to add more
     * members in his group chat.
     */
    protected void onShow() {
        super.onShow();
        mFilteredContactLst.requestFocus();
        sGroupChatSelected.removeAllElements();
        mFilteredAddressBook.removeAllElements();
        mBaseAddressBook.removeAllElements();
        mSearchTxtFld.setText(EMPTY_STRING);
        mSearchTxtFld.setRows(1);
        mSearchTxtFld.setTextChangedListener(this);
        Enumeration enums = AddressBookList.getInstance().elements();
        while (enums.hasMoreElements()) {
            AddressBookEntry contact = (AddressBookEntry) enums.nextElement();
            if (!sGroupChatSelected.contains(contact) && ( AppState.getUserDetails().getCurrentGrp() == null || !AppState.getUserDetails().getCurrentGrp().getMembers().containsKey(contact.getMsisdn()) || !AppState.getUserDetails().getCurrentGrp().getMember(contact.getMsisdn()).isActive() )) {
                mFilteredAddressBook.addElement(contact);
                mBaseAddressBook.addElement(contact);
            }
          
        }
        if(!mFilteredAddressBook.isEmpty()) {
            mFilteredContactLst.setSelectedIndex(0, true);
        }
        enums = null;
        Runtime.getRuntime().gc();
        removeAllCommands();
        initCommands();

        switch (AppState.fromScreen) {
            case DisplayStackManager.FORM_GROUP_INFO:
                //#if nokia2_0
//#                 setTitle(LBL_ADD_TO_GROUP);
                //#endif
                break;

            case DisplayStackManager.FORM_CONVERSATION:
                //#if nokia2_0
//#                 setTitle(LBL_NEW_GROUP);
                //#endif
                break;
        }
        
        Log.v(TAG, "On Show In Group *********");
    }

    
    /**
     * This method is to get the name of selected contact which is used to show inside the search box.
     * @return 
     */
    public String getSelectedGroupContactNames() {
        StringBuffer selectedNames = new StringBuffer();
        Enumeration enums = sGroupChatSelected.elements();
        while (enums.hasMoreElements()) {
            AddressBookEntry entry = (AddressBookEntry) enums.nextElement();
            String contactName = (entry.getName()).equals("Tap here to send") ? entry.getMsisdn() : entry.getName();
            selectedNames.append(contactName);
            selectedNames.append("; ");
        }
        return selectedNames.toString();
    }

    
//    /**
//     * This method constraints the SMS user limit to maximum 5. This method check whether user is allowed to add more SMS user in group chat, if 5 SMS 
//     * users are not added in the group.
//     * @return 
//     */
//    private boolean canAddMoreSmsUser() {
//        //will check only sms user limit not the max total limit
//        int selectedSmsUser = 0;
//        Enumeration enums = sGroupChatSelected.elements();
//        while (enums.hasMoreElements()) {
//            AddressBookEntry entry = (AddressBookEntry) enums.nextElement();
//            if (!entry.isOnHike()) {
//                selectedSmsUser++;
//                if (selectedSmsUser >= SMS_USER_LIMIT) {
//                    return false;
//                }
//            }
//        }
//        int current = AppState.getUserDetails().getCurrentGrp() != null ? AppState.getUserDetails().getCurrentGrp().getActiveSmsMembersCount() : 0;
//        return current + selectedSmsUser < SMS_USER_LIMIT ? true : false;
//    }

    
    /**
     * This method deletes the last selected contact from the list. 
     */
    public void deleteLastGroupContact() {
        if (!sGroupChatSelected.isEmpty()) {
            Object last = sGroupChatSelected.lastElement();
            sGroupChatSelected.removeElement(last);
            mFilteredAddressBook.removeElement(last);
        }
        //flushReplace();
        revalidate();
    }

    
    /**
     * This method is called when user clicks on done command to start a group chat. This method  returns all the members which user has selected.
     * @return 
     */
    public Hashtable getSelectedGroupContact() {
        Hashtable contacts = new Hashtable();
        for (int index = 0; index < sGroupChatSelected.size(); index++) {
            AddressBookEntry entry = (AddressBookEntry) sGroupChatSelected.elementAt(index);
            if (entry != null) {
                GroupMember aMember = new GroupMember(entry.getMsisdn(), entry.getName(), true, entry.isOnHike(), null, GroupMember.MemberSource.SELF);
                contacts.put(entry.getMsisdn(), aMember);
                        
                Log.v(TAG, "name : " + contacts.get(entry.getMsisdn()));
            }
        }
        return contacts;
    }

    
    /**
     * This method is used to check whether the maximum group chat members limit reached or not. 
     * @return 
     */
    private boolean canAddMore() {
        final boolean underLimit;
        int current = AppState.getUserDetails().getCurrentGrp() != null ? AppState.getUserDetails().getCurrentGrp().getActiveMembersCount() : 0;
        underLimit = current + sGroupChatSelected.size() < 19 ? true : false;
        if (!underLimit) {
            UIHelper.showToast(MSG_GCUSER_LIMIT);
        }
        return underLimit;
    }

    
    /**
     * This method returns boolean value when user finished adding members in group chat.
     * @return 
     */
    private boolean canFinishAdding() {
        int current = AppState.getUserDetails().getCurrentGrp() != null ? AppState.getUserDetails().getCurrentGrp().getMembers().size() : 0;
        boolean canadd = current + sGroupChatSelected.size() < 2 ? false : true;
                 
        Log.v(TAG, "can finish adding: "+ canadd);
        return canadd;
    }
    
    
    /**
     * This is an overrided call of text change. This method gets called on every text change in the search box.
     * @param textArea
     * @param previousText
     * @param currentText 
     */
    public void onTextChange(PatchedTextArea textArea, String previousText, final String currentText) {
        if (!(Display.getInstance().getCurrent() instanceof FormSelectGroupContact)) {
            return;
        }
        
        if (currentText.length() == 0) {
            sGroupChatSelected.removeAllElements();                    
        }
        final int index = currentText.lastIndexOf(';') + 1;              
        if (index > 0) {
            UIHelper.runOnLwuitUiThread(new Runnable() {
                public void run() {
                    searchContactlist(currentText.substring(index));
                }
            });                 
        } else {
            UIHelper.runOnLwuitUiThread(new Runnable() {
                public void run() {
                    searchContactlist(currentText);
                }
            });
        }
        if (lastIndex == currentText.length()) {
            deleteLastGroupContact();
            mSearchTxtFld.setText(getSelectedGroupContactNames());
            lastIndex = currentText.lastIndexOf(';') + 1;                  
        }

        int CharWidth = mSearchTxtFld.getStringWidth(currentText);
        if (CharWidth < (displayWidth - TRIM_SIZE)) {
            mSearchTxtFld.setRows(MIN_ROW_FACTOR);
            return;
        }
        int rowSizeFactor = CharWidth / (displayWidth - TRIM_SIZE);
        if (rowSizeFactor >= MAX_ROW_SIZE) {
            return;
        }
        mSearchTxtFld.setRows(rowSizeFactor + MIN_ROW_FACTOR);
    }
}
