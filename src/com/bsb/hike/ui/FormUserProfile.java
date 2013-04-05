package com.bsb.hike.ui;

/**
 * 
 * @author Ranjith Yadav
 * @author Puneet Agarwal
 */
import com.bsb.hike.Hike;
import com.bsb.hike.dto.AppState;
import com.bsb.hike.dto.UserDetails;
import com.bsb.hike.util.AppConstants;
import com.bsb.hike.util.AppResource;
import com.bsb.hike.util.Log;
import com.bsb.hike.util.TextUtils;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Display;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.list.ListCellRenderer;
import com.sun.lwuit.plaf.Border;
import java.util.Vector;
import javax.microedition.io.ConnectionNotFoundException;

/**
 * @author Puneet Agarwal
 */

public class FormUserProfile extends FormHikeBase {
    private static final String TAG = "FormUserProfile";
    private String profileName, mailID;
    private Command backCommand;
    private Label madeWithLoveByBSB, appVersionLabel;
    private Vector settingsVector, profileDataVector, freeSMSDataVector;
    
    /**
     * Constructor of User profile form. This form provides option to user to update the profile, change notification settings, privacy settings,
     * Rewards options, and help feature.
     */
    public FormUserProfile() {
        
        profileDataVector = new Vector();
        freeSMSDataVector = new Vector();
        settingsVector = new Vector();
        
        Image madeWithLoveImage = AppResource.getImageFromResource(AppConstants.PATH_MADE_WITH_LOVE_BY_BSB);
        madeWithLoveByBSB = new Label(madeWithLoveImage);
        madeWithLoveByBSB.getStyle().setBgTransparency(0, true);
        madeWithLoveByBSB.getStyle().setPadding(Component.TOP, 15, true);
        madeWithLoveByBSB.getStyle().setPadding(Component.LEFT, Display.getInstance().getDisplayWidth()/2 - madeWithLoveImage.getWidth()/2, true);
        
        appVersionLabel = new Label();
        appVersionLabel.getStyle().setFgColor(ColorCodes.profileSettingsItemGrey, true);
        appVersionLabel.getStyle().setFont(Fonts.SMALL, true);
        appVersionLabel.getStyle().setBgTransparency(0, true);
        appVersionLabel.getStyle().setPadding(Component.TOP, 10, true);
        appVersionLabel.getStyle().setAlignment(Component.CENTER, true);
        
        setScrollable(true);
        setScrollableX(false);
        
        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        
        getStyle().setBgColor(ColorCodes.profileBgGrey, true);
        
        initCommands();
    }
    
    /**
     * This is a delegated method of the form. This method is called just after showing the form on the screen.
     */
    protected void onShow() {
        super.onShow();
        
        removeAll();
        createSettingsModeldata();
        initForm();
        userInfoContainer();
        settingsContainer();
        
        appVersionLabel.setText(LBL_VERSION + Hike.sMidlet.getAppProperty(LBL_MIDLET_VERSION));
        
        addComponent(madeWithLoveByBSB);
        addComponent(appVersionLabel);
        
    }
    
    /**
     * This method initializes the data, which we need to show on the form.
     */
    private void initForm() {
        UserDetails user = AppState.getUserDetails();
        if(user == null) {
            return;
        }
        
        profileName = user.getName();
        if(TextUtils.isEmpty(profileName)){
            profileName = EMPTY_STRING;
        }
        mailID = user.getEmail();
        if (TextUtils.isEmpty(mailID)) {
            mailID = EMPTY_STRING;
        }
        
        profileDataVector.removeAllElements();
        profileDataVector.addElement(new NameEmailIdPair(profileName, mailID, null));
    
        freeSMSDataVector.removeAllElements();
        freeSMSDataVector.addElement(new FreeSMSCountNImagePair(AppResource.getImageFromResource(freeSMSImage), LBL_FREE_SMS, Integer.toString(AppState.getUserDetails().getSmsCredit())));
    }
    
    /**
     * This method initializes the components on the screen with their styles and their action listeners.
     */
    private void userInfoContainer() {
        
        final List profileList = new List(profileDataVector);
        profileList.setRenderer(new ProfileDataRenderer());
        profileList.getStyle().setBorder(Border.createCompoundBorder(null, Border.createLineBorder(1, ColorCodes.profileLightGreySeperator), null, null), true);
        profileList.getSelectedStyle().setBorder(Border.createCompoundBorder(null, Border.createLineBorder(1, ColorCodes.profileLightGreySeperator), null, null), true);
        profileList.getStyle().setPadding(Component.TOP, 0, true);
        profileList.getStyle().setPadding(Component.BOTTOM, 0, true);
        profileList.getStyle().setBgColor(ColorCodes.profileScreenNameEmailBgGrey, true);
        profileList.getStyle().setBgTransparency(255, true);
        profileList.setWidth(getWidth());
        profileList.setFocusable(false);
        profileList.setFocus(false);
        profileList.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent evt) {
                if (profileList.getSelectedIndex() == 0) {
                    DisplayStackManager.showForm(DisplayStackManager.FORM_EDIT_PROFILE);
                }
            }
        });
        
        final List freeSMSList = new List(freeSMSDataVector);
        freeSMSList.setRenderer(new FreeSMSDataRenderer());
        freeSMSList.getStyle().setPadding(Component.TOP, 0, true);
        freeSMSList.getStyle().setPadding(Component.BOTTOM, 0, true);
        freeSMSList.getStyle().setPadding(Component.LEFT, 0, true);
        freeSMSList.getStyle().setPadding(Component.RIGHT, 0, true);
        freeSMSList.setFocus(false);
        freeSMSList.setFocusable(false);
        freeSMSList.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                if(freeSMSList.getSelectedIndex() == 0) {
                    AppState.fromScreen = DisplayStackManager.FORM_PROFILE;
                    DisplayStackManager.showForm(DisplayStackManager.FORM_FREE_SMS);
                }
            }
        });
        
        Container userInfocontainer = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        userInfocontainer.addComponent(profileList);
        if(AppState.getUserDetails().getCountryCode().equals(DEFAULT_COUNTRY_CODE)) {
            userInfocontainer.addComponent(freeSMSList);
        }
        userInfocontainer.getStyle().setBgColor(ColorCodes.profileBgGrey, true);
        userInfocontainer.getStyle().setBgTransparency(255, true);
        
        addComponent(userInfocontainer);
    }

    /**
     * This method initializes the components on the screen with their styles and their action listeners.
     */
    private void settingsContainer() {

        final List settingList = new List(settingsVector);
        settingList.setRenderer(new SettingsRenderer());
        settingList.setFocus(false);
        settingList.setFocusable(false);
        settingList.getStyle().setBgColor(ColorCodes.profileBgGrey, true);
        settingList.getSelectedStyle().setBgColor(ColorCodes.profileBgGrey, true);
        settingList.getStyle().setPadding(Component.BOTTOM, 0, true);
        settingList.getStyle().setPadding(Component.LEFT, 0, true);
        settingList.getStyle().setPadding(Component.RIGHT, 0, true);
        settingList.getStyle().setBgTransparency(255, true);
        settingList.getSelectedStyle().setBgTransparency(255, true);
        settingList.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                
                if (((TitleImagePair)settingList.getModel().getItemAt(settingList.getSelectedIndex())).getTitle().equals(LBL_REWARDS)) {
                    if(AppState.getUserDetails() != null && AppState.getUserDetails().getAccountInfo() != null) {
                        if(AppState.getUserDetails().getAccountInfo().isShowReward(AppState.getUserDetails().getCountryCode())) {
                            AppState.fromScreen = DisplayStackManager.FORM_PROFILE;
                            DisplayStackManager.showForm(DisplayStackManager.FORM_REWARDS);
                        }
                    }
                }
                if (((TitleImagePair)settingList.getModel().getItemAt(settingList.getSelectedIndex())).getTitle().equals(LBL_NOTIFICATIONS)) {
                    DisplayStackManager.showForm(DisplayStackManager.FORM_NOTIFICATION);
                }
                if(((TitleImagePair)settingList.getModel().getItemAt(settingList.getSelectedIndex())).getTitle().equals(LBL_PRIVACY)) {
                    DisplayStackManager.showForm(DisplayStackManager.FORM_PRIVACY);
                }
                if(((TitleImagePair)settingList.getModel().getItemAt(settingList.getSelectedIndex())).getTitle().equals(LBL_HELP)) {
                    try 
                    {
                        Hike.sMidlet.platformRequest(HelpURL);
                    } catch (ConnectionNotFoundException ex) {
                        Log.v(TAG, ex.getClass().getName());
                    }
                }
            }
        });
            
        Container settingsContainer = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        settingsContainer.addComponent(settingList);
        
        addComponent(settingsContainer);
    }

    /**
     * This method initializes all the commands which are there on the current form.
     */
    private void initCommands() {
        
        backCommand = new Command(LBL_BACK) {
            
            public void actionPerformed(ActionEvent evt) {
                super.actionPerformed(evt);
                DisplayStackManager.showForm(DisplayStackManager.FORM_CONVERSATION);
            }
        };
        
        addCommand(backCommand);
        setBackCommand(backCommand);
    }

    
    /**
     * This method creates data set for settings list.
     */
    public void createSettingsModeldata() {
        settingsVector.removeAllElements();
        if(AppState.getUserDetails() != null && AppState.getUserDetails().getAccountInfo() != null) {
            if(AppState.getUserDetails().getAccountInfo().isShowReward(AppState.getUserDetails().getCountryCode())) {
                settingsVector.addElement(new TitleImagePair(LBL_REWARDS, AppResource.getImageFromResource(rewardsImage)));
            }
        }
        settingsVector.addElement(new TitleImagePair(LBL_NOTIFICATIONS, AppResource.getImageFromResource(notificImage)));
        settingsVector.addElement(new TitleImagePair(LBL_PRIVACY, AppResource.getImageFromResource(privacyImage)));
        settingsVector.addElement(new TitleImagePair(LBL_HELP, AppResource.getImageFromResource(helpImage)));
    }
    
    
    /**
     * This method is called from outside the class to update the SMS count UI.
     * @param updatedSMSCount 
     */
    public void updateSmsCountInUserProfile(int updatedSMSCount) {
        if(!freeSMSDataVector.isEmpty() && freeSMSDataVector.size()>0){
            FreeSMSCountNImagePair pair =  (FreeSMSCountNImagePair) freeSMSDataVector.elementAt(0);
            if(pair!=null){
                pair.setSmsCount(Integer.toString(updatedSMSCount));
            }
        }
    }
    
    /**
     * Settings Renderer class which creates a list for user to change their settings.
     */
    private class SettingsRenderer extends Container implements ListCellRenderer {
        
         private Label settingsItemName;
         private Label arrowIconLabel;
         private Image arrowImage;
         private Container settingsContainer;

        public SettingsRenderer() {
        
            arrowImage = AppResource.getImageFromResource(PATH_LIST_ARROW_ICON);
        }
         
        public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {
            
            TitleImagePair pair = (TitleImagePair) value;
            settingsContainer = new Container(new BorderLayout());
            settingsContainer.getStyle().setPadding(Component.TOP, 6, true);
            settingsContainer.getStyle().setPadding(Component.BOTTOM, 6, true);
            settingsContainer.getStyle().setBorder(Border.createCompoundBorder(null, Border.createEtchedRaised(ColorCodes.profileSettingsSeperator, ColorCodes.profileSettingsSeperatorShadow), null, null), true);
              
            settingsItemName = new Label();
            settingsItemName.getStyle().setBgTransparency(0, true);
            settingsItemName.getStyle().setFgColor(ColorCodes.profileSettingsItemGrey, true);
            settingsItemName.getStyle().setFont(Fonts.SMALL, true);
            settingsItemName.getStyle().setPadding(Component.LEFT, 8, true);
            
            arrowIconLabel = new Label();
            arrowIconLabel.getStyle().setPadding(Component.RIGHT, 10, true);
            arrowIconLabel.getStyle().setBgTransparency(0, true);
            
            settingsContainer.addComponent(BorderLayout.WEST, settingsItemName);
            settingsContainer.addComponent(BorderLayout.EAST, arrowIconLabel);
            
            settingsItemName.setText(pair.getTitle());
            settingsItemName.setGap(10);
            settingsItemName.setIcon(pair.getImage());
            arrowIconLabel.setIcon(arrowImage);
            
            return settingsContainer;
        }

        public Component getListFocusComponent(List list) {
            return null;
        }
    }
    
    /**
     * Model class for title image pair.
     */
    private class TitleImagePair {
        
        private String title;
        private Image image;

        public TitleImagePair(String title, Image image) {
            this.title = title;
            this.image = image;
        }

        public String getTitle() {
            return title;
        }

        public Image getImage() {
            return image;
        }
    }
    
    
    /**
     * Profile Renderer class for creating user profile options.
     */
    private class ProfileDataRenderer extends Container implements ListCellRenderer {
        
         private TextArea username;
         private TextArea emailId;
         //private Label profileImage;
         private Container profileContainer, nameEmailIdContainer;
         
        public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {
            
            profileContainer = new Container(new BoxLayout(BoxLayout.X_AXIS));
            profileContainer.getStyle().setBgColor(ColorCodes.profileScreenNameEmailBgGrey, true);
            profileContainer.getStyle().setBgTransparency(255, true);
            profileContainer.getStyle().setPadding(Component.TOP, 8, true);
            profileContainer.getStyle().setPadding(Component.BOTTOM, 8, true);
            
            NameEmailIdPair pair = (NameEmailIdPair) value;
            nameEmailIdContainer = new Container(new BoxLayout(BoxLayout.Y_AXIS));
              
            //profileImage = new Label();
            username = getNameTextArea();
            
            nameEmailIdContainer.addComponent(username);
            
            if(!pair.getEmailId().equals("")) {
                emailId = getEmailIdLabel();
                nameEmailIdContainer.addComponent(emailId);
                emailId.setText(pair.getEmailId());
            } 
            
            username.setText(pair.getName());
            
            //profileContainer.addComponent(profileImage);
            profileContainer.addComponent(nameEmailIdContainer);
            
            return profileContainer;
        }

        public Component getListFocusComponent(List list) {
            return null;
        }
    }
    
    /**
     * Model class for Name and email id.
     */
    private class NameEmailIdPair {
        
        private String name;
        private String emailID;
        private Image profileImage;

        public NameEmailIdPair(String name, String email, Image profileImage) {
            this.name = name;
            this.emailID = email;
            this.profileImage = profileImage;
        }

        public String getName() {
            return name;
        }

        public String getEmailId() {
            return emailID;
        }
        
        public Image getImage() {
            return profileImage;
        }
    }
    
    /**
     * Returns Textarea for displaying name on the screen.
     * @return 
     */
    private TextArea getNameTextArea() {
        
        TextArea nameTextArea = new TextArea();
        nameTextArea.getStyle().setFont(Fonts.LARGE, true);
        nameTextArea.getSelectedStyle().setFont(Fonts.LARGE, true);
        nameTextArea.getStyle().setFgColor(ColorCodes.profileNameGrey, true);
        nameTextArea.getSelectedStyle().setFgColor(ColorCodes.profileNameGrey, true);
        nameTextArea.setConstraint(TextArea.LEFT);
        nameTextArea.getStyle().setAlignment(Component.LEFT);
        nameTextArea.setEditable(false);
        nameTextArea.getStyle().setPadding(Component.BOTTOM, 0, true);
        nameTextArea.getSelectedStyle().setPadding(Component.BOTTOM, 0, true);
        if (nameTextArea.getText().length() > 15) {
            nameTextArea.setRows(2);
        } else {
            nameTextArea.setRows(1);
        }
        nameTextArea.setRowsGap(0);
        nameTextArea.setFocusable(false);
        nameTextArea.getStyle().setBgTransparency(0, true);
        nameTextArea.getStyle().setBorder(null, true);
        
        return nameTextArea;
    }
    
    
    /**
     * Returns Textarea for displaying Email-id on the screen.
     * @return 
     */
    private TextArea getEmailIdLabel() {
        
        TextArea emailIdTextArea = new TextArea();
        emailIdTextArea.setConstraint(TextArea.LEFT);
        emailIdTextArea.getStyle().setAlignment(Component.LEFT);
        emailIdTextArea.getStyle().setFgColor(ColorCodes.profileEmailId);
        emailIdTextArea.getSelectedStyle().setFgColor(ColorCodes.profileEmailId);
        emailIdTextArea.getStyle().setPadding(Component.TOP, 0, true);
        emailIdTextArea.getSelectedStyle().setPadding(Component.TOP, 0, true);
        emailIdTextArea.getStyle().setFont(Fonts.SMALL, true);
        emailIdTextArea.getSelectedStyle().setFont(Fonts.SMALL, true);
        emailIdTextArea.setEditable(false);
        emailIdTextArea.setRows(1);
        emailIdTextArea.getStyle().setBgTransparency(0, true);
        emailIdTextArea.setFocusable(false);
        emailIdTextArea.getStyle().setBorder(null, true);
        
        return emailIdTextArea;
    }
    
    
    /**
     * Free SMS data Renderer class for showing free SMS credits on profile page.
     */
    private class FreeSMSDataRenderer extends Container implements ListCellRenderer {
        
         private Label freeSMS;
         private Label mSmsCountLbl;
         private Container smsCounterpane;

        public FreeSMSDataRenderer() {
            
            freeSMS = new Label();
            freeSMS.getStyle().setFont(Fonts.SMALL, true);
            freeSMS.getStyle().setBgTransparency(0, true);
            freeSMS.getStyle().setFgColor(ColorCodes.profileSettingsItemGrey, true);
            freeSMS.getStyle().setPadding(Component.LEFT, 8, true);
            freeSMS.setTextPosition(Component.RIGHT);

            mSmsCountLbl = new Label();
            mSmsCountLbl.getStyle().setFont(Fonts.SMALL, true);
            mSmsCountLbl.getStyle().setBgTransparency(0, true);
            mSmsCountLbl.getStyle().setFgColor(ColorCodes.profileSMSCountBlue, true);
            mSmsCountLbl.getStyle().setPadding(Component.RIGHT, 8, true);
            mSmsCountLbl.getStyle().setAlignment(Component.CENTER, true);
            mSmsCountLbl.setTextPosition(Component.LEFT);
            
            smsCounterpane = new Container(new BorderLayout());
            smsCounterpane.getStyle().setBorder(Border.createCompoundBorder(null, Border.createEtchedRaised(ColorCodes.profileSettingsSeperator, ColorCodes.profileSettingsSeperatorShadow), null, null), true);
            smsCounterpane.getStyle().setPadding(Component.TOP, 8, true);
            smsCounterpane.getStyle().setPadding(Component.BOTTOM, 8, true);
            smsCounterpane.getStyle().setBgColor(ColorCodes.white, true);
            smsCounterpane.addComponent(BorderLayout.CENTER, freeSMS);
            smsCounterpane.addComponent(BorderLayout.EAST, mSmsCountLbl);
        }
         
        public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {
            if(value != null) {                
                FreeSMSCountNImagePair freeSMSCountNImagePair = (FreeSMSCountNImagePair)value;
                freeSMS.setText(freeSMSCountNImagePair.getFreeSmsText());
                freeSMS.setGap(10);
                freeSMS.setIcon(freeSMSCountNImagePair.getSmsCountImage());
                mSmsCountLbl.setText(freeSMSCountNImagePair.getSmsCount());
            }
            return smsCounterpane;
        }

        public Component getListFocusComponent(List list) {
            return null;
        }
    }
    
    
    /**
     * Data Model class for count and image.
     */
    private class FreeSMSCountNImagePair {
        
        private String freeSmsText;
        private String smsCount;
        private Image smsCountImage;

        public FreeSMSCountNImagePair(Image smsCountImage, String freeSmsText, String smsCount) {
            this.smsCountImage = smsCountImage;
            this.freeSmsText = freeSmsText;
            this.smsCount = smsCount;
        }
        
        public String getFreeSmsText() {
            return freeSmsText;
        }

        public String getSmsCount() {
            return smsCount;
        }

        public void setSmsCount(String count) {
            smsCount = count;
        }
        public Image getSmsCountImage() {
            return smsCountImage;
        }
    }
}
    
