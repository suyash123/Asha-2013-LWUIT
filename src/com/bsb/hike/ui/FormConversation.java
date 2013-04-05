package com.bsb.hike.ui;

import com.bsb.hike.Hike;
import com.bsb.hike.dto.AppState;
import com.bsb.hike.dto.ChatModel;
import com.bsb.hike.dto.ConversationList;
import com.bsb.hike.dto.ConversationModel;
import com.bsb.hike.dto.DataModel;
import com.bsb.hike.io.mqtt.MqttManager;
import com.bsb.hike.util.AppConstants;
import com.bsb.hike.util.AppResource;
import com.bsb.hike.util.CollectionUpdateEvent;
import com.bsb.hike.util.CollectionUpdateListener;
import com.bsb.hike.util.Log;
import com.bsb.hike.util.ModelUtils;
import com.bsb.hike.util.TextUtils;
import com.bsb.hike.util.UIHelper;
import com.nokia.mid.ui.PopupList;
import com.nokia.mid.ui.PopupListItem;
import com.nokia.mid.ui.PopupListListener;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Display;
import com.sun.lwuit.Graphics;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.geom.Dimension;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.list.ListCellRenderer;
import com.sun.lwuit.plaf.Border;
import java.util.Date;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

/**
 * @author Puneet Agarwal
 * @author Ranjit
 * @author Sudheer Keshav Bhat
 */
public class FormConversation extends FormHikeBase implements CollectionUpdateListener {

    private static final String TAG = "FormConversation";
    private Command mDeleteAllCmd;
    private Command mExitCmd;
    private Command mChatCmd;
    private Command mProfileCmd;
    private Command mInviteCmd;
    private Command mGroupChat;
    private Command rewardsCommand;
    private Image mLogoImg, mTapHereTip;
    private List list;
    private Timer mEventTimer;
    private EventTimeTracker mEventTracker;

    /**
     * Constructor of Conversation form. This form shows all the previous conversations which user had. If there is no conversation, then there will a 
     * hike logo on the screen.
     */
    public FormConversation() {

        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        getStyle().setBgColor(ColorCodes.conversationBgGrey, true);

        list = new List(ConversationList.getInstance()) {
            private boolean dragging = false;
            int dy = 0;
            protected void longPointerPress(int x, int y) {
                super.longPointerPress(x, y);
            }

            public void pointerDragged(int x, int y) {
                super.pointerDragged(x, y);
                Log.v(TAG, "pointerDragged: A");
                dragging = true;
                if (mEventTimer != null && Math.abs(dy-y) > 5) {
                    System.out.println("onPointerReleased");
                    mEventTimer.cancel();
                }
            }

            public void pointerReleased(int x, int y) {
                super.pointerReleased(x, y);
                mEventTimer.cancel();
                        
                Log.v(TAG, "pointerReleased A: " + list.getSelectedIndex());
                if (!dragging && ConversationList.getInstance().size() - 1 >= list.getSelectedIndex()) {
                    ConversationModel bn = (ConversationModel) ConversationList.getInstance().elementAt(list.getSelectedIndex());
                    AppState.getUserDetails().setSelectedMsisdn(bn.getMsisdn());
                    //         Log.v(TAG, "Conversation index=" + list.getSelectedIndex());
                    DisplayStackManager.showForm(DisplayStackManager.FORM_CHAT);
                }
                dragging = false;

            }

            public void pointerHover(int[] x, int[] y) {
                super.pointerHover(x, y);
                         
                Log.v(TAG, "pointerHover: A");
            }

            public void pointerPressed(int x, int y) {
                super.pointerPressed(x, y);
                         
                Log.v(TAG, "pointerPressed: A");
                dragging = false;
                mEventTimer = new Timer();
                mEventTracker = new EventTimeTracker(y);
                mEventTimer.schedule(mEventTracker, EVENT_TRIGGER_TIME_OUT);
                
                dy =y;
            }

            public void pointerHoverPressed(int[] x, int[] y) {
                super.pointerHoverPressed(x, y);
                         
                Log.v(TAG, "pointerHoverPressed: A");
            }
        };
        list.setRenderer(new ConversationsRenderer());
        list.setSmoothScrolling(true);
        setScrollable(false);

        //#if nokia2_0
//#         list.getStyle().setBgColor(ColorCodes.conversationListBgGrey, true);
//#         list.getSelectedStyle().setBgColor(ColorCodes.conversationListBgGrey, true);
        //#elif nokia1_1
//#         list.getStyle().setBgColor(ColorCodes.white, true);
//#         list.getSelectedStyle().setBgColor(ColorCodes.white, true);
        //#endif

        list.getStyle().setBgTransparency(255, true);
        list.getSelectedStyle().setBgTransparency(255, true);
        list.getStyle().setPadding(Component.BOTTOM, 0, true);
        list.getSelectedStyle().setPadding(Component.BOTTOM, 0, true);
        list.getStyle().setPadding(Component.LEFT, 0, true);
        list.getSelectedStyle().setPadding(Component.LEFT, 0, true);
        list.getStyle().setPadding(Component.RIGHT, 0, true);
        list.getSelectedStyle().setPadding(Component.RIGHT, 0, true);
        list.getStyle().setBorder(Border.createCompoundBorder(null, Border.createEtchedRaised(ColorCodes.conversationSeperatorDarkGrey, ColorCodes.conversationSeperatorDropShadow), null, null), true);
        list.getSelectedStyle().setBorder(Border.createCompoundBorder(null, Border.createEtchedRaised(ColorCodes.conversationSeperatorDarkGrey, ColorCodes.conversationSeperatorDropShadow), null, null), true);

        //#if nokia1_1
//#         mLogoImg = AppResource.getImageFromResource(AppConstants.PATH_HOME_LOGO);
        //#elif nokia2_0
//#         mLogoImg = AppResource.getImageFromResource(AppConstants.PATH_HOME_LOGO_FULL_TOUCH);
        //#endif

        //#if nokia1_1
//#         mTapHereTip = AppResource.getImageFromResource(AppConstants.PATH_TAP_HERE_TIP);
        //#elif nokia2_0
//#         mTapHereTip = AppResource.getImageFromResource(AppConstants.PATH_TAP_HERE_TIP_FULL_TOUCH);
        //#endif

        initCommands();
        ConversationList.getInstance().addCollectionUpdateListener(this);
        addComponent(list);
    }

    
    /**
     * This delegated method is called when user drags a point on the screen for scrolling conversation list.
     * @param x
     * @param y 
     */
    public void pointerDragged(int x, int y) {
        if (!ConversationList.getInstance().isEmpty()) {
            super.pointerDragged(x, y);
        }
    }

    
    /**
     * This delegated method is called when user drags a point on the screen for scrolling conversation list.
     * @param x
     * @param y 
     */
    public void pointerDragged(int[] x, int[] y) {
        if (!ConversationList.getInstance().isEmpty()) {
            super.pointerDragged(x, y);
        }
    }

    
    /**
     * This method will draw hike logo on the screen if there is no conversation.
     * @param g 
     */
    public void paintBackground(Graphics g) {
        super.paintBackground(g);

        if (ConversationList.getInstance().isEmpty()) {
            g.drawImage(mTapHereTip, Display.getInstance().getDisplayWidth() / 2 - mTapHereTip.getWidth() / 2, 15);
            g.drawImage(mLogoImg, Display.getInstance().getDisplayWidth() / 2 - mLogoImg.getWidth() / 2, Display.getInstance().getDisplayHeight() / 2 - mLogoImg.getHeight() / 2);
        }
    }

    
    /**
     * This method initializes all the commands which are there on the current form.
     */
    private void initCommands() {
        rewardsCommand = new Command(LBL_REWARDS) {
            public void actionPerformed(ActionEvent evt) {
                super.actionPerformed(evt);
                if(AppState.getUserDetails() != null && AppState.getUserDetails().getAccountInfo() != null) {
                    if(AppState.getUserDetails().getAccountInfo().isShowReward(AppState.getUserDetails().getCountryCode())) {
                        AppState.fromScreen = DisplayStackManager.FORM_CONVERSATION;
                        DisplayStackManager.showForm(DisplayStackManager.FORM_REWARDS);
                    }
                }
            }
        };
        
        mDeleteAllCmd = new Command(LBL_DELETE_ALL) {
            public void actionPerformed(ActionEvent evt) {
                if (!ConversationList.getInstance().isEmpty()) {
                    showDeleteDialog();
                }
            }
        };

        //#if nokia2_0
//#         mChatCmd = new Command(LBL_CHAT, AppResource.getImageFromResource(PATH_NEW_CHAT)) {
//#             public void actionPerformed(ActionEvent evt) {
//#                 DisplayStackManager.showForm(DisplayStackManager.FORM_SELECT_CONTACT);
//#             }
//#         };
        //#elif nokia1_1
//#         mChatCmd = new Command(LBL_CHAT) {
//#             public void actionPerformed(ActionEvent evt) {
//#                 DisplayStackManager.getForm(DisplayStackManager.FORM_SELECT_CONTACT).show();
//#             }
//#         };
        //#endif

        mProfileCmd = new Command(LBL_PROFILE) {
            public void actionPerformed(ActionEvent evt) {
                DisplayStackManager.showForm(DisplayStackManager.FORM_PROFILE);
            }
        };

        mInviteCmd = new Command(LBL_INVITE_FRIENDS) {
            public void actionPerformed(ActionEvent evt) {
                AppState.fromScreen = DisplayStackManager.FORM_CONVERSATION;
                DisplayStackManager.showForm(DisplayStackManager.FORM_INVITE);
            }
        };

        mExitCmd = new Command(LBL_EXIT) {
            public void actionPerformed(ActionEvent evt) {
                showExitDialog();
            }
        };

        mGroupChat = new Command(LBL_GROUP_CHAT) {
            public void actionPerformed(ActionEvent evt) {
                super.actionPerformed(evt);
                AppState.fromScreen = DisplayStackManager.FORM_CONVERSATION;
                DisplayStackManager.showForm(DisplayStackManager.FORM_SELECT_GROUP_CONTACT);

            }
        };
        
        //Adding rewards command if conversation form is removed from the memory.
        addCommand(mProfileCmd);
        addCommand(mInviteCmd);
        addCommand(mExitCmd);
        addCommand(mGroupChat);
        addCommand(mChatCmd);
        addCommand(mDeleteAllCmd);
        setDefaultCommand(mChatCmd);
        setBackCommand(mExitCmd);
        if(AppState.getUserDetails() != null && AppState.getUserDetails().getAccountInfo() != null) {
            if(AppState.getUserDetails().getAccountInfo().isShowReward(AppState.getUserDetails().getCountryCode())) {
                removeCommand(rewardsCommand);
                addCommand(rewardsCommand, 4);
            }
        }
    }
    
    
    /**
     * This method adds rewards command on the menu options of conversation screen.
     * @param add 
     */
    public void addRewardCommand(boolean add) {
        removeCommand(rewardsCommand);
        if(add) {
            addCommand(rewardsCommand, 4);
        }
    }

    
    /**
     * This delegated method is called just after showing the form. This method cleans up mobile number of user from Enter number screen after signup
     * completes.
     */
    protected void onShow() {
        super.onShow();
        FormEnterNumber formenternumber = (FormEnterNumber) DisplayStackManager.getForm(DisplayStackManager.FORM_ENTER_NUMBER, false);
        if (formenternumber != null) {
            formenternumber.clearTextFieldWhenSignupCompletes();
        }
    }

    
    /**
     * Conversation List Renderer inflates all the conversations on the screen.
     */
    class ConversationsRenderer extends Container implements ListCellRenderer {

        private Label nameLbl = new Label(EMPTY_STRING);
        private Label lastMsgLbl = new Label(EMPTY_STRING); //SmileyLabel(""); TODO
        private Label lastMsgTimestampLbl = new Label(EMPTY_STRING);
        private Container mainContainer, messageContainer;
        private Label avatarLabel = new Label(EMPTY_STRING);
        private Label onFocusLbl = new Label();

        public ConversationsRenderer() {

            messageContainer = new Container(new BorderLayout());
            messageContainer.getStyle().setPadding(Component.BOTTOM, 8, true);
            messageContainer.getStyle().setPadding(Component.LEFT, 2, true);
            messageContainer.getStyle().setBgTransparency(0, true);
            messageContainer.setPreferredSize(new Dimension(Display.getInstance().getDisplayWidth(), 65));

            mainContainer = new Container(new BoxLayout(BoxLayout.X_AXIS));
            mainContainer.getStyle().setBorder(Border.createCompoundBorder(null, Border.createEtchedRaised(ColorCodes.conversationSeperatorLightGrey, ColorCodes.conversationSeperatorDullWhiteDropShadow), null, null), true);
            mainContainer.getStyle().setBgColor(ColorCodes.conversationListBgGrey, true);

            onFocusLbl.getStyle().setBgTransparency(10, true);

            nameLbl.getStyle().setBgTransparency(0, true);
            nameLbl.getStyle().setPadding(Component.BOTTOM, 0, true);
            nameLbl.getStyle().setFgColor(ColorCodes.conversationListName, true);
            nameLbl.setPreferredW(Display.getInstance().getDisplayWidth() - 102);
            nameLbl.getStyle().setFont(Fonts.MEDIUM);

            lastMsgLbl.getStyle().setBgTransparency(0, true);
            lastMsgLbl.getStyle().setFont(Fonts.SMALL);
            lastMsgLbl.getStyle().setPadding(Component.TOP, 0, true);

            avatarLabel.getStyle().setBgTransparency(0, true);
            avatarLabel.getStyle().setPadding(Component.LEFT, 8, true);

            lastMsgTimestampLbl.getStyle().setFont(Fonts.SMALL);
            lastMsgTimestampLbl.getStyle().setFgColor(ColorCodes.conversationListTimeStampGrey);
            lastMsgTimestampLbl.getStyle().setPadding(Component.RIGHT, 0, true);
            lastMsgTimestampLbl.getStyle().setPadding(Component.TOP, 6, true);
            lastMsgTimestampLbl.getStyle().setBgTransparency(0, true);

            messageContainer.addComponent(BorderLayout.WEST, nameLbl);
            messageContainer.addComponent(BorderLayout.EAST, lastMsgTimestampLbl);
            messageContainer.addComponent(BorderLayout.SOUTH, lastMsgLbl);

            mainContainer.addComponent(avatarLabel);
            mainContainer.addComponent(messageContainer);
        }

        public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {
//            //         Log.v(TAG, "cell index: " + index);
            if (value != null) {
                ConversationModel conversation = (ConversationModel) value;
                ChatModel lastChat = conversation.getLastChatModel();

                Image icon = UIHelper.getMsgStatusIcon(lastChat);

                nameLbl.setText(conversation.getName());
                lastMsgLbl.setGap(5);
                lastMsgLbl.setIcon(icon);

                //set the cached avatar image label
                //the search inside getContact Avtar shoudlnt happen again and again.
                avatarLabel.setIcon(conversation.getContactAvtar());

                if (lastChat != null) {
                    lastMsgLbl.setText(lastChat.getMessage());
                    lastMsgTimestampLbl.setText(TextUtils.toTimeDiff(lastChat.getTimestamp()));
                    if (lastChat.getMessageStatus() == ChatModel.MessageStatus.RECEIVED) {
                        lastMsgLbl.getStyle().setFgColor(ColorCodes.conversationListNewUnreadMsg, true);
                    } else {
                        lastMsgLbl.getStyle().setFgColor(ColorCodes.conversationListLastMsgGrey, true);
                    }
                } else {
                    lastMsgLbl.setText(EMPTY_STRING);
                    lastMsgTimestampLbl.setText(TextUtils.toTimeDiff(new Date().getTime()));
                    lastMsgLbl.getStyle().setFgColor(ColorCodes.conversationListLastMsgGrey, true);
                }
            }

            return mainContainer;
        }

        public Component getListFocusComponent(List list) {

            return onFocusLbl;
        }
    }

    
    /**
     * This class is extending TimerTask class which is used to open context menu  on the screen, when user tap and hold on particular conversation.
     */
    private class EventTimeTracker extends TimerTask {

        private int mPosition;

        public EventTimeTracker(int y) {
            mPosition = y;
        }

        public void run() {
            //#if nokia2_0
//#             final PopupList popupList = new PopupList("context actions");
//#             final ConversationModel selected = (ConversationModel) list.getSelectedItem();
//#             final String selectedMsisdn = selected.getMsisdn();
//#             boolean isGroupChat = ModelUtils.isGroupChat(selectedMsisdn);
//#             final PopupListItem deleteItm = new PopupListItem(LBL_DELETE);
//#             popupList.appendItem(deleteItm);
//#             if (isGroupChat) {
//#                 deleteItm.setText(LBL_DELETE_GRP_CHAT);
//#             }
//#             popupList.setListener(new PopupListListener() {
//#                 public void itemSelected(PopupList popList, PopupListItem item) {
//#                     if (item == deleteItm) {
//#                         ConversationList.getInstance().removeElement(ConversationList.getInstance().getEntryByMsisdn(selected.getMsisdn()));
//#                         DisplayStackManager.showForm(AppState.getForm());
//#                         if (ModelUtils.isGroupChat(selectedMsisdn)) {
//#                             MqttManager.leaveGroupChat(selectedMsisdn);
//#                         }
//#                     }
//#                     popList.setVisible(false);
//#                 }
//# 
//#                 public void listDismissed(PopupList list) {
//#                 }
//#             });
//#             popupList.setListYPos(mPosition);
//#             popupList.setVisible(true);
            //#endif

            //         Log.v(TAG, " Event can be triggered");
            mEventTracker.cancel();
        }
    }

    
    /**
     * This method is called when a new conversation model is added.
     * @param event 
     */
    public void modelAdded(CollectionUpdateEvent event) {
        //         Log.v(TAG, "model added to list");
        list.setShouldCalcPreferredSize(true);
        repaint();
    }

    
    /**
     * This method is called when old conversation model is removed. 
     * @param event 
     */
    public void modelRemoved(CollectionUpdateEvent event) {
        //         Log.v(TAG, "model removed from list");
        list.repaint();
    }

    
    /**
     * This method is called when conversation model is updated.
     * @param event 
     */
    public void modelUpdated(CollectionUpdateEvent event) {
        //         Log.v(TAG, "model updated");
        list.repaint();
    }

    
    /**
     * This method is called when the contents of model gets updated, when new conversation arrives on the device.
     * @param affectedModel 
     */
    public void modelContentsUpdated(DataModel affectedModel) {
        //         Log.v(TAG, "model contents updated");
        if (affectedModel != null && affectedModel instanceof ConversationModel) {
            ConversationModel conversation = (ConversationModel) affectedModel;
            ConversationModel currentConversation = ConversationList.getInstance().getEntryByMsisdn(AppState.getUserDetails().getSelectedMsisdn());
            if (currentConversation == conversation) {
                if (Display.getInstance().getCurrent() instanceof FormChatThread) {
                    FormChatThread _chat = (FormChatThread) Display.getInstance().getCurrent();
                    _chat.setTypingStatus(conversation);
                }
            }
        }
        if (Display.getInstance().getCurrent() instanceof FormConversation) {
            // TO:Do No need to Load form again to refresh the List.
            // DisplayStackManager.getForm(DisplayStackManager.FORM_CONVERSATION).show(); 
            list.repaint();
        }
    }

    
    /**
     * This method shows LCDUI alert to user when user selects some conversation to delete.
     */
    private void showDeleteDialog() {
        Alert deleteAlert = UIHelper.getAlertDialog(LBL_CONFIRM, MSG_DELETE_CONVERSATION, LBL_DELETE, LBL_CANCEL, AlertType.CONFIRMATION);
        deleteAlert.setCommandListener(new CommandListener() {
            public void commandAction(javax.microedition.lcdui.Command c, Displayable d) {
                if (c.getLabel().equals(LBL_DELETE)) {
                    DisplayStackManager.showForm(AppState.getForm());
                    Enumeration enums = ConversationList.getInstance().elements();
                    while (enums.hasMoreElements()) {
                        ConversationModel model = (ConversationModel) enums.nextElement();
                        if (ModelUtils.isGroupChat(model.getMsisdn())) {
                            MqttManager.leaveGroupChat(model.getMsisdn());
                        }
                    }
                    ConversationList.getInstance().removeAllElements();
                }
                if (c.getLabel().equals(LBL_CANCEL)) {

                    DisplayStackManager.showForm(AppState.getForm());
                }
            }
        });

        javax.microedition.lcdui.Display.getDisplay(Hike.sMidlet).setCurrent(deleteAlert, javax.microedition.lcdui.Display.getDisplay(Hike.sMidlet).getCurrent());
    }

    
    /**
     * This method opens up a LCDUI alert dialog when user presses back button on conversation list and confirm user to exit the app.
     */
    private void showExitDialog() {
        Alert deleteAlert = UIHelper.getAlertDialog(APP_TITLE, MSG_CLOSE_APPLICATION, LBL_YES, LBL_NO, AlertType.CONFIRMATION);
        deleteAlert.setCommandListener(new CommandListener() {
            public void commandAction(javax.microedition.lcdui.Command c, Displayable d) {
                if (c.getLabel().equals(LBL_YES)) {
                    Hike.sMidlet.destroyApp(true);
                }
                if (c.getLabel().equals(LBL_NO)) {
                    DisplayStackManager.showForm(AppState.getForm());
                }
            }
        });
        javax.microedition.lcdui.Display.getDisplay(Hike.sMidlet).setCurrent(deleteAlert, javax.microedition.lcdui.Display.getDisplay(Hike.sMidlet).getCurrent());
    }
}
