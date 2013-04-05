/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.ui;

import com.bsb.hike.dto.AppState;
import com.bsb.hike.dto.GroupList;
import com.bsb.hike.dto.GroupMember;
import com.bsb.hike.dto.GroupMembers;
import com.bsb.hike.io.ClientConnectionHandler;
import com.bsb.hike.io.ConnectionFailedException;
import com.bsb.hike.io.mqtt.MqttManager;
import com.bsb.hike.ui.component.PatchedTextArea;
import com.bsb.hike.util.AppConstants.ColorCodes;
import com.bsb.hike.util.AppConstants.Fonts;
import com.bsb.hike.util.AppResource;
import com.bsb.hike.util.Log;
import com.bsb.hike.util.UIHelper;
import com.sun.lwuit.Button;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
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
import java.io.IOException;
import java.util.Vector;

/**
 *
 * @author Puneet Agarwal
 * 
 */
public class FormGroupInfo extends FormHikeBase {

    private PatchedTextArea groupTitleTextArea;
    private List participantsList;
    private Button inviteAllSMSUsersToHikeBtn;
    private Command addParticipantsCommand, saveCommand, backCommand;
    private static final String TAG = "FormGroupInfo";
    private Vector mNonHikeMemberMsisdns = new Vector();
    private GroupMembers groupMembers;

    
    /**
     * Constructor of Group info form. This form shows the group chat details and gives option to user to edit group name and add more members in
     * group chat.
     */
    public FormGroupInfo() {

        initCommands();

        //#if nokia2_0
//#         setTitle(LBL_GROUP_INFO);
        //#endif

        getStyle().setBgColor(ColorCodes.groupInfoBgGrey, true);
        setLayout(new BoxLayout(BoxLayout.Y_AXIS));

        Label groupNameLabel = new Label(LBL_GROUP_NAME);
        groupNameLabel.getStyle().setFgColor(ColorCodes.groupInfoHeadingText);
        groupNameLabel.getStyle().setBgColor(ColorCodes.white, true);
        groupNameLabel.getStyle().setBgTransparency(0, true);
        groupNameLabel.getStyle().setFont(Fonts.SMALL, true);
        groupNameLabel.getStyle().setPadding(Component.TOP, 10, true);

        groupTitleTextArea = new PatchedTextArea();
        groupTitleTextArea.getStyle().setPadding(Component.TOP, 0, true);
        groupTitleTextArea.getSelectedStyle().setPadding(Component.TOP, 0, true);
        groupTitleTextArea.getStyle().setFont(Fonts.MEDIUM, true);
        groupTitleTextArea.getSelectedStyle().setFont(Fonts.MEDIUM, true);
        groupTitleTextArea.setConstraint(TextArea.NON_PREDICTIVE);
        groupTitleTextArea.getStyle().setFgColor(ColorCodes.groupInfoNameGrey, true);
        groupTitleTextArea.getSelectedStyle().setFgColor(ColorCodes.groupInfoNameGrey, true);
        groupTitleTextArea.getStyle().setBgColor(ColorCodes.groupInfoHeadingNameBg, true);
        groupTitleTextArea.getSelectedStyle().setBgColor(ColorCodes.groupInfoHeadingNameBg, true);
        groupTitleTextArea.getStyle().setBgTransparency(255, true);
        groupTitleTextArea.getSelectedStyle().setBgTransparency(255, true);
        groupTitleTextArea.getStyle().setBorder(Border.createEmpty(), true);
        groupTitleTextArea.getSelectedStyle().setBorder(Border.createEmpty(), true);

        Container groupNameTitleContainer = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        groupNameTitleContainer.getStyle().setBgColor(ColorCodes.groupInfoHeadingNameBg, true);
        groupNameTitleContainer.getStyle().setBgTransparency(255, true);
        groupNameTitleContainer.getStyle().setBorder(Border.createCompoundBorder(null, Border.createLineBorder(1, ColorCodes.groupInfoTopContainerBottomBorderGrey), null, null), true);
        groupNameTitleContainer.getStyle().setPadding(Component.TOP, 5, true);
        groupNameTitleContainer.getStyle().setPadding(Component.BOTTOM, 5, true);
        groupNameTitleContainer.getStyle().setPadding(Component.LEFT, 3, true);
        groupNameTitleContainer.addComponent(groupNameLabel);
        groupNameTitleContainer.addComponent(groupTitleTextArea);

        Label participantsTitle = new Label();
        participantsTitle.setTextPosition(Component.RIGHT);
        participantsTitle.setText(LBL_PARTICIPANTS_TITLE);
        participantsTitle.getStyle().setBgTransparency(0, true);
        participantsTitle.getStyle().setFgColor(ColorCodes.groupInfoParticipantsTitleGrey, true);
        participantsTitle.getStyle().setPadding(Component.TOP, 10, true);
        participantsTitle.getStyle().setPadding(Component.LEFT, 5, true);

        inviteAllSMSUsersToHikeBtn = new Button();
        inviteAllSMSUsersToHikeBtn.setText(LBL_INVITE_SMS_USERS_TO_HIKE);
        inviteAllSMSUsersToHikeBtn.getStyle().setMargin(Component.TOP, 20, true);
        inviteAllSMSUsersToHikeBtn.getSelectedStyle().setMargin(Component.TOP, 20, true);
        inviteAllSMSUsersToHikeBtn.getPressedStyle().setMargin(Component.TOP, 20, true);
        inviteAllSMSUsersToHikeBtn.getStyle().setMargin(Component.LEFT, 15, true);
        inviteAllSMSUsersToHikeBtn.getSelectedStyle().setMargin(Component.LEFT, 15, true);
        inviteAllSMSUsersToHikeBtn.getPressedStyle().setMargin(Component.LEFT, 15, true);
        inviteAllSMSUsersToHikeBtn.getStyle().setMargin(Component.RIGHT, 15, true);
        inviteAllSMSUsersToHikeBtn.getSelectedStyle().setMargin(Component.RIGHT, 15, true);
        inviteAllSMSUsersToHikeBtn.getPressedStyle().setMargin(Component.RIGHT, 15, true);
        inviteAllSMSUsersToHikeBtn.getStyle().setFont(Fonts.SMALL, true);
        inviteAllSMSUsersToHikeBtn.getSelectedStyle().setFont(Fonts.SMALL, true);
        inviteAllSMSUsersToHikeBtn.getPressedStyle().setFont(Fonts.SMALL, true);
        inviteAllSMSUsersToHikeBtn.getStyle().setFgColor(ColorCodes.groupInfoInviteBtnTextGrey, true);
        inviteAllSMSUsersToHikeBtn.getSelectedStyle().setFgColor(ColorCodes.groupInfoInviteBtnTextGrey, true);
        inviteAllSMSUsersToHikeBtn.getPressedStyle().setFgColor(ColorCodes.groupInfoInviteBtnTextGrey, true);
        inviteAllSMSUsersToHikeBtn.getStyle().setAlignment(Component.CENTER, true);
        inviteAllSMSUsersToHikeBtn.getSelectedStyle().setAlignment(Component.CENTER, true);
        inviteAllSMSUsersToHikeBtn.getPressedStyle().setAlignment(Component.CENTER, true);
        inviteAllSMSUsersToHikeBtn.getStyle().setBgColor(ColorCodes.groupInfoHeadingNameBg, true);
        inviteAllSMSUsersToHikeBtn.getSelectedStyle().setBgColor(ColorCodes.groupInfoHeadingNameBg, true);
        inviteAllSMSUsersToHikeBtn.getPressedStyle().setBgColor(ColorCodes.groupInfoHeadingNameBg, true);
        inviteAllSMSUsersToHikeBtn.getStyle().setBgTransparency(255, true);
        inviteAllSMSUsersToHikeBtn.getSelectedStyle().setBgTransparency(255, true);
        inviteAllSMSUsersToHikeBtn.getPressedStyle().setBgTransparency(255, true);
        inviteAllSMSUsersToHikeBtn.getStyle().setBorder(Border.createEmpty(), true);
        inviteAllSMSUsersToHikeBtn.getSelectedStyle().setBorder(Border.createEmpty(), true);
        inviteAllSMSUsersToHikeBtn.getPressedStyle().setBorder(Border.createEmpty(), true);
        inviteAllSMSUsersToHikeBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                if(mNonHikeMemberMsisdns != null && mNonHikeMemberMsisdns.size() > 0){
                    MqttManager.invite(mNonHikeMemberMsisdns);
                             
                    Log.v(TAG, "All SMS Users invited");
                    //TODO move to PUBACK for this message.
                    UIHelper.showToast(MSG_INVITED);
                }
            }
        });

        addComponent(groupNameTitleContainer);
        addComponent(participantsTitle);
    }

    
     /**
     * This delegated method is called just after showing the form. This method is adding and removing components whenever this form opens up.
     */
    protected void onShow() {

        groupMembers = GroupList.getInstance().getEntryByGroupId(AppState.getUserDetails().getSelectedMsisdn());
        groupTitleTextArea.setText(groupMembers.getName());

        if (groupMembers != null) {
            if (participantsList != null) {
                removeComponent(participantsList);
            }

            participantsList = new List(groupMembers.getActiveMembersList(true));
            participantsList.getStyle().setPadding(Component.TOP, 5, true);
            participantsList.getSelectedStyle().setPadding(Component.TOP, 5, true);
            participantsList.getStyle().setPadding(Component.LEFT, 0, true);
            participantsList.getSelectedStyle().setPadding(Component.LEFT, 0, true);
            participantsList.getStyle().setPadding(Component.RIGHT, 0, true);
            participantsList.getSelectedStyle().setPadding(Component.RIGHT, 0, true);
            participantsList.setRenderer(new ParticipantsRenderer());
            addComponent(participantsList);
        }

        removeComponent(inviteAllSMSUsersToHikeBtn);
        addComponent(inviteAllSMSUsersToHikeBtn);

        addNonHikeMembers();        
    }

    
    /**
     * This method initializes all the commands which are there on the current form.
     */
    private void initCommands() {

        //#if nokia2_0
//#         addParticipantsCommand = new Command(ADD_PARTICIPANTS, AppResource.getImageFromResource(PATH_IC_SELECT_CONTACT_FOR_CHAT)) {
//# 
//#             public void actionPerformed(ActionEvent evt) {
//#                 super.actionPerformed(evt);
//#                 
//#                 Log.v(TAG, "FormGroupInfo action add participant" + evt);
//#                 Log.v(TAG, "Group count: " +AppState.getUserDetails().getCurrentGrp().getActiveMembersCount());
//#                 
//#                 if (AppState.getUserDetails().getCurrentGrp().getActiveMembersCount() >= 19) {
//#                     UIHelper.showToast(MSG_GCUSER_LIMIT);
//#                 } else {
//#                     AppState.fromScreen = DisplayStackManager.FORM_GROUP_INFO;
//#                      
//#                     Log.v(TAG, AppState.getUserDetails().getCurrentGrp());
//#                
//#                     DisplayStackManager.showForm(DisplayStackManager.FORM_SELECT_GROUP_CONTACT);
//#                 }
//# 
//#             }
//#         };
        //#elif nokia1_1
//#         addParticipantsCommand = new Command(ADD_PARTICIPANTS) {
//# 
//#             public void actionPerformed(ActionEvent evt) {
//#                 super.actionPerformed(evt);
//#                 /
//#                 AppState.fromScreen = DisplayStackManager.FORM_GROUP_INFO;
//#                 
//#                 DisplayStackManager.getForm(DisplayStackManager.FORM_SELECT_GROUP_CONTACT).show();
//# 
//#             }
//#         };
        //#endif

        backCommand = new Command(LBL_BACK) {

            public void actionPerformed(ActionEvent evt) {
                super.actionPerformed(evt);
                        
                Log.v(TAG, "onFormGroupInfoBack()");
                DisplayStackManager.showForm(DisplayStackManager.FORM_CHAT);
            }
        };

        saveCommand = new Command(LBL_SAVE) {

            public void actionPerformed(ActionEvent evt) {
                super.actionPerformed(evt);
                if(groupTitleTextArea.getText().length() <= GROUP_NAME_MAX_LIMIT) {
                    new Thread(new Runnable() {

                        public void run() {
                            DisplayStackManager.showProgressForm(true, MSG_UPDATING_GROUP_INFO);
                            try 
                            {
                                if (!groupTitleTextArea.getText().equals(EMPTY_STRING) && !groupTitleTextArea.getText().equals(groupMembers.getName())) {

                                    Log.v(TAG, "Group name update called in Group info");
                                    ClientConnectionHandler.postGroupName(AppState.getUserDetails().getCurrentGrp().getGroupId(), groupTitleTextArea.getText());
                                }
                            } catch (ConnectionFailedException ex) {
                                ex.printStackTrace();
                            } catch (IOException ioe) {
                                ioe.printStackTrace();
                            } finally {
                                DisplayStackManager.showForm(DisplayStackManager.FORM_CHAT);
                            }
                        }
                    }).start();
                }else {
                    UIHelper.showToast(MSG_MAX_GROUP_NAME_LIMIT_REACHED);
                }
            }
        };

        addCommand(backCommand);
        addCommand(addParticipantsCommand);
        addCommand(saveCommand);
        setBackCommand(backCommand);
        setDefaultCommand(addParticipantsCommand);
        
        
    }

    
    /**
     * Participants list Renderer inflates the participants list in particular group chat.
     */
    class ParticipantsRenderer extends Container implements ListCellRenderer {

        private Label mNameLbl = new Label("");
        private Label ownerLabel = new Label();
        private Image onHikeImage, notOnHikeImage, dndImage;
        private Image iconImage;

        public ParticipantsRenderer() {

            setLayout(new BorderLayout());

            onHikeImage = AppResource.getImageFromResource(PATH_GROUP_INFO_HIKE_PARTICIPANT_BLUE);
            notOnHikeImage = AppResource.getImageFromResource(PATH_GROUP_INFO_SMS_PARTICIPANT_GREEN);
            dndImage = AppResource.getImageFromResource(PATH_SYSTEM_USER_WAITING);
            
            Container listPane = new Container(new BoxLayout(BoxLayout.X_AXIS));
            listPane.getStyle().setBorder(Border.createCompoundBorder(null, Border.createLineBorder(1, ColorCodes.groupInfoListSeperator), null, null), true);
            listPane.getStyle().setPadding(Component.TOP, 2, true);
            listPane.getStyle().setPadding(Component.BOTTOM, 2, true);

            listPane.addComponent(mNameLbl);
            ownerLabel.setText(LBL_GROUP_OWNER);
            ownerLabel.getStyle().setBgTransparency(0, true);
            ownerLabel.getStyle().setFgColor(ColorCodes.groupInfoOwnerTextGrey, true);
            ownerLabel.getStyle().setFont(Fonts.SMALL);
            ownerLabel.getStyle().setPadding(Component.BOTTOM, 0, true);
            listPane.addComponent(ownerLabel);

            addComponent(BorderLayout.CENTER, listPane);
            setFocusable(true);
        }

        public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {
            if (value != null) {
                GroupMember member = (GroupMember) value;
                
                if(member.getMsisdn().equals(AppState.getUserDetails().getMsisdn())) {
                    mNameLbl.setText(AppState.getUserDetails().getName());
                }else {
                    mNameLbl.setText(member.getName());
                }
                
                if (member.isOnHike()) {
                    iconImage = onHikeImage;
                } else {
                    iconImage = notOnHikeImage;
                    if (member.isDnD().equals(LBL_TRUE)) {
                        iconImage = dndImage;
                    }
                }
                
                mNameLbl.setIcon(iconImage);
                mNameLbl.setGap(7);
                mNameLbl.getStyle().setBgTransparency(0, true);
                mNameLbl.getStyle().setPadding(Component.LEFT, 13, true);
                mNameLbl.getStyle().setFgColor(ColorCodes.white);
                mNameLbl.getStyle().setFont(Fonts.SMALL);

                ownerLabel.setVisible(member.getMsisdn().equals(groupMembers.getCreaterMsisdn()));
            } 
            return this;
        }

        public Component getListFocusComponent(List list) {
            return null;
        }
    }

    
    /**
     * This method adds all the SMS users which are active and is there in the current group chat. User can invite to all SMS user at once to join Hike.
     */
    private void addNonHikeMembers() {
        if (AppState.getUserDetails().getCurrentGrp() != null) {
            mNonHikeMemberMsisdns.removeAllElements();
            mNonHikeMemberMsisdns = AppState.getUserDetails().getCurrentGrp().getActiveSmsMembersMsisdn();
            
            Log.v(TAG, mNonHikeMemberMsisdns.toString());
        }
    }
}
