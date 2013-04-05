/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.ui;

/**
 *
 * @author Puneet Agarwal
 * @author Ranjith Yadav
 */
import com.bsb.hike.dto.AddressBookEntry;
import com.bsb.hike.dto.AddressBookList;
import com.bsb.hike.dto.AppState;
import com.bsb.hike.io.mqtt.MqttManager;
import com.bsb.hike.ui.component.PatchedTextArea;
import com.bsb.hike.ui.component.SearchTextField;
import com.bsb.hike.util.AppConstants;
import com.bsb.hike.util.AppResource;
import com.bsb.hike.util.Collections;
import com.bsb.hike.util.Log;
import com.bsb.hike.util.TextUtils;
import com.bsb.hike.util.UIHelper;
import com.bsb.hike.util.Validator;
import com.sun.lwuit.Button;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Display;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.geom.Dimension;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.list.ListCellRenderer;
import com.sun.lwuit.plaf.Border;
import java.util.Enumeration;
import java.util.Vector;

public class FormInvitation extends FormHikeBase implements PatchedTextArea.TextChangedListener{

    private Command backCommand;
    private SearchTextField searchTextArea = new SearchTextField();
    private Vector inviteVector = new Vector();
    private List inviteList;
    private static final String TAG = "FormInvitation";

    
    /**
     * Constructor of Invitation form. This form displays all the non-hike users and allow user to send invitation to those users who are not on hike.
     */
    public FormInvitation() {

        getStyle().setBgColor(ColorCodes.selectContactBgGrey, true);

        //#if nokia2_0
//#         setTitle(LBL_INVITE_FRIENDS);
        //#endif

        setLayout(new BorderLayout());

        setScrollable(false);

        addComponent(BorderLayout.NORTH, searchTextArea);
        
        initCommands();

        searchTextArea.setTextChangedListener(this);

        inviteList = new List(inviteVector);
        inviteList.getStyle().setPadding(Component.LEFT, 0, true);
        inviteList.getSelectedStyle().setPadding(Component.LEFT, 0, true);
        inviteList.getStyle().setPadding(Component.RIGHT, 0, true);
        inviteList.getSelectedStyle().setPadding(Component.RIGHT, 0, true);
        inviteList.getStyle().setPadding(Component.BOTTOM, 0, true);
        inviteList.getSelectedStyle().setPadding(Component.BOTTOM, 0, true);
        inviteList.setRenderer(new InviteListRenderer());
        inviteList.addActionListener(new ActionListener() {
            boolean clicked = false;
            AddressBookEntry entry;

            public void actionPerformed(ActionEvent evt) {
                if (!clicked) {
                    entry = (AddressBookEntry) inviteList.getSelectedItem();                    
                    if(entry.getMsisdn()== null || entry.getPhoneNumber() == null) {
                        return ;
                    } 
                    clicked = true;
                    if (entry.getId().equals(UNKNOWN_CONTACT_ID)){
                        MqttManager.invite(entry.getName(), AppState.getNextMessageID());
                    } else{
                        MqttManager.invite(entry.getMsisdn(), AppState.getNextMessageID());
                    }
                    clicked = false;
                    entry.setInvited(true);
                    //flushReplace();
                    revalidate();
                }
            }
        });

        addComponent(BorderLayout.CENTER, inviteList);
    }

    /**
     * This delegated method is called just after showing the form. This method removes all the contacts from the list and add them again on onShow().
     */
    protected void onShow() {
        super.onShow();

        inviteVector.removeAllElements();
        Vector list = AddressBookList.getInstance().getInviteList();
        Log.v(TAG, "Invite lsit size: "+list.size());
        Enumeration enums = list.elements();
        while (enums.hasMoreElements()) {
            AddressBookEntry entry = (AddressBookEntry) enums.nextElement();
            inviteVector.addElement(entry);
        }
        Collections.sort(inviteVector, mAlphabeticalComparator);
        //flushReplace();
        revalidate();
    }

    
    /**
     * This method is called on onTextChangeListener() of the search box. This method will show only the filtered contacts to the user.
     * @param string 
     */
    private void searchContactlist(String string) {
        String searchString = string.trim();
        if (!TextUtils.isEmpty(searchString)) {
            inviteVector.removeAllElements();
            String validatedNum = Validator.validatePhoneString(searchString);
            if (validatedNum != null) {
                boolean isValidLength = validatedNum.length() - (AppState.getUserDetails().getCountryCode().length()+1) >= CharLimit.PHONE_NUMBER_MIN && validatedNum.length() <= CharLimit.PHONE_NUMBER_MAX;
                String msisdn = isValidLength ? UNKNOWN_CONTACT_INVITE_MESSAGE : UNKNOWN_CONTACT_NAME;
                String name= isValidLength ? validatedNum : searchString;
                String phone = isValidLength ? validatedNum : null;
                AddressBookEntry addressBookentry = new AddressBookEntry(UNKNOWN_CONTACT_ID, phone, name, msisdn, false);
                inviteVector.addElement(addressBookentry);
            }
            
            Vector list = AddressBookList.getInstance().getInviteList();
            Enumeration enums = list.elements();
            while (enums.hasMoreElements()) {
                AddressBookEntry entry = (AddressBookEntry) enums.nextElement();
                String contactName = entry.getName().toUpperCase();
                String contactNumber = entry.getMsisdn();
                searchString = searchString.toUpperCase();
                if (contactName.indexOf(searchString) >= 0 || contactNumber.indexOf(searchString) >= 0) {
                    inviteVector.addElement(entry);
                }
            }
        } else {
            inviteVector.removeAllElements();
            Vector list = AddressBookList.getInstance().getInviteList();
            Enumeration enums = list.elements();
            while (enums.hasMoreElements()) {
                AddressBookEntry entry = (AddressBookEntry) enums.nextElement();
                inviteVector.addElement(entry);
            }
        }
        //flushReplace();
        revalidate();
        inviteList.setShouldCalcPreferredSize(true);
    }

    
    /**
     * This method initializes all the commands which are there on the current form.
     */
    private void initCommands() {
        backCommand = new Command(LBL_BACK) {
            public void actionPerformed(ActionEvent evt) {
                searchTextArea.setText("");
                if(AppState.fromScreen == DisplayStackManager.FORM_REWARDS) {
                    DisplayStackManager.showForm(DisplayStackManager.FORM_REWARDS);
                }else if(AppState.fromScreen == DisplayStackManager.FORM_CONVERSATION) {
                DisplayStackManager.showForm(DisplayStackManager.FORM_CONVERSATION);
                }else if(AppState.fromScreen == DisplayStackManager.FORM_FREE_SMS){
                    AppState.fromScreen = DisplayStackManager.FORM_PROFILE;
                    DisplayStackManager.showForm(DisplayStackManager.FORM_FREE_SMS);
                }else {
                    DisplayStackManager.showForm(DisplayStackManager.FORM_CONVERSATION);
                }
            }
        };
        addCommand(backCommand);
        setBackCommand(backCommand);
    }
    
    
    /**
     * This is an overrided call of text change. This method gets called on every text change in the search box.
     * @param textArea
     * @param previousText
     * @param currentText 
     */
    public void onTextChange(PatchedTextArea textArea, String previousText, final String currentText) {
        if (!(Display.getInstance().getCurrent() instanceof FormInvitation)) {
            return;
        }
        Log.v(TAG, " ************* testing PatchedTextArea " + currentText);
        Log.v(TAG, " ************* String Length  " + currentText.length());
        final int index = currentText.lastIndexOf(';');

        Log.v(TAG, " ************* Last Index " + index);
        if (index > 0) {
            UIHelper.runOnLwuitUiThread(new Runnable() {
                  public void run() {
                  searchContactlist(currentText.substring(index + 1));
                }
            });

            Log.v(TAG, " ************* Last Character " + currentText.substring(index));
        } else {
             UIHelper.runOnLwuitUiThread(new Runnable() {
                  public void run() {
                  searchContactlist(currentText);
               }
            });
        }

        if(!inviteVector.isEmpty()) {
            inviteList.setSelectedIndex(0);
            inviteList.repaint();
        }
    }

    
    /**
     * Invite List Renderer class inflates all the user contacts which are not on hike.
     */
    private class InviteListRenderer extends Container implements ListCellRenderer {

        private Image inviteImage, sentInviteImage;
        private Button inviteButton;
        private Label tickMark;
        private AddressBookEntry addressEntry;
        private Container mainContainer;
        private Label onFocusLbl = new Label();

        public InviteListRenderer() {
        
            inviteImage = AppResource.getImageFromResource(AppConstants.inviteImage);
            sentInviteImage = AppResource.getImageFromResource(AppConstants.sentInviteImage);
        }
        
        public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {
            if(value != null) {
                addressEntry = (AddressBookEntry) value;
                         
                Log.v(TAG, "AddressBookEntry: "+addressEntry);   
                
                Log.v(TAG, "Index is:"+index);
                onFocusLbl.getStyle().setBgTransparency(20, true);
                
                Container namephnumContainer = new Container(new BoxLayout(BoxLayout.Y_AXIS));
                namephnumContainer.getStyle().setPadding(Component.LEFT, 3, true);
                namephnumContainer.getStyle().setPadding(Component.TOP, 5, true);
                namephnumContainer.getStyle().setPadding(Component.BOTTOM, 8, true);

                Label contactName = new Label(addressEntry.getName());
                contactName.getStyle().setBgTransparency(0, true);
                contactName.getStyle().setFgColor(ColorCodes.selectContactNameGrey, true);
                contactName.getStyle().setPadding(Component.BOTTOM, 0, true);

                contactName.getSelectedStyle().setFont(Fonts.MONO_MEDIUM, true);
                contactName.getStyle().setFont(Fonts.MONO_MEDIUM, true);
                contactName.getSelectedStyle().setBorder(null, true);
                contactName.setPreferredSize(new Dimension((Display.getInstance().getDisplayWidth() - inviteImage.getWidth() - 25), 30));

                Label phoneNum = new Label(addressEntry.getMsisdn());
                phoneNum.getStyle().setBgTransparency(0, true);
                phoneNum.getStyle().setPadding(Component.TOP, 0, true);
                phoneNum.getSelectedStyle().setBorder(null, true);
                phoneNum.getSelectedStyle().setFont(Fonts.MONO_SMALL, true);
                phoneNum.getStyle().setFont(Fonts.MONO_SMALL, true);
                phoneNum.getStyle().setFgColor(ColorCodes.selectContactNumberGrey);

                namephnumContainer.addComponent(contactName);
                namephnumContainer.addComponent(phoneNum);

                inviteButton = new Button(inviteImage);
                inviteButton.getStyle().setBgTransparency(0, true);
                inviteButton.getSelectedStyle().setBgTransparency(0, true);
                inviteButton.getPressedStyle().setBgTransparency(0, true);
                inviteButton.getPressedStyle().setBorder(null, true);
                inviteButton.getStyle().setBorder(null, true);
                inviteButton.getSelectedStyle().setBorder(null, true);
                inviteButton.getStyle().setAlignment(CENTER);

                tickMark = new Label(sentInviteImage);
                tickMark.getStyle().setAlignment(Component.CENTER);
                tickMark.getStyle().setBgTransparency(0, true);
                tickMark.getSelectedStyle().setBgTransparency(0, true);
                tickMark.getPressedStyle().setBgTransparency(0, true);
                tickMark.getPressedStyle().setBorder(null, true);
                tickMark.getStyle().setBorder(null, true);
                tickMark.getSelectedStyle().setBorder(null, true);
                tickMark.getStyle().setAlignment(CENTER);
                tickMark.getStyle().setPadding(Component.RIGHT, 30, true);

                mainContainer = new Container(new BorderLayout());
                mainContainer.getStyle().setBorder(Border.createCompoundBorder(null, Border.createEtchedRaised(ColorCodes.selectContactSeperator, ColorCodes.selectContactSeperatorShadow), null, null), true);

                mainContainer.addComponent(BorderLayout.WEST, namephnumContainer);
                if (inviteButton.getParent() == null) {
                    mainContainer.addComponent(BorderLayout.EAST, inviteButton);
                }

                getStyle().setPadding(Component.RIGHT, 0, true);
                getUnselectedStyle().setPadding(Component.RIGHT, 0, true);
                getPressedStyle().setPadding(Component.RIGHT, 0, true);
                getSelectedStyle().setPadding(Component.RIGHT, 0, true);

                if (addressEntry.isInvited()) {
                    mainContainer.removeComponent(inviteButton);
                    mainContainer.addComponent(BorderLayout.EAST, tickMark);
                }
            }
            
            return mainContainer;
        }

        public Component getListFocusComponent(List list) {
            return onFocusLbl;
        }
    }
    
    private Collections.Comparator mAlphabeticalComparator = new Collections.Comparator() {
        public int compare(Object o1, Object o2) {
            AddressBookEntry address1 = (AddressBookEntry) o1;
            AddressBookEntry address2 = (AddressBookEntry) o2;
            if(address1 == null || address2==null){
                return 0;
            }
            return address1.getName().toLowerCase().compareTo(address2.getName().toLowerCase());
        }
    };
}