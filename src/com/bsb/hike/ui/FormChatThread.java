package com.bsb.hike.ui;

import com.bsb.hike.Hike;
import com.bsb.hike.dto.AppState;
import com.bsb.hike.dto.ChatEntity;
import com.bsb.hike.dto.ChatList;
import com.bsb.hike.dto.ChatModel;
import com.bsb.hike.dto.ConversationList;
import com.bsb.hike.dto.ConversationModel;
import com.bsb.hike.dto.DataModel;
import com.bsb.hike.dto.GroupMember;
import com.bsb.hike.dto.GroupMembers;
import com.bsb.hike.dto.MqttObjectModel.MessageType;
import com.bsb.hike.io.mqtt.MqttManager;
import com.bsb.hike.ui.component.ChatBubbleTextArea;
import com.bsb.hike.ui.component.SendContainer;
import com.bsb.hike.util.AppResource;
import com.bsb.hike.util.ClipboardManager;
import com.bsb.hike.util.CollectionUpdateEvent;
import com.bsb.hike.util.CollectionUpdateListener;
import com.bsb.hike.util.Log;
import com.bsb.hike.util.ModelUtils;
import com.bsb.hike.util.TextUtils;
import com.bsb.hike.util.UIHelper;
//#if nokia2_0
//# import com.nokia.mid.ui.KeyboardVisibilityListener;
//# import com.nokia.mid.ui.PopupList;
//# import com.nokia.mid.ui.PopupListItem;
//# import com.nokia.mid.ui.PopupListListener;
//# import com.nokia.mid.ui.VirtualKeyboard;
//#endif
import com.bsb.hike.util.Validator;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Display;
import com.sun.lwuit.Graphics;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.plaf.Border;
import java.util.Date;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import javax.microedition.io.ConnectionNotFoundException;

/**
 *
 * @author Puneet Agarwal
 * @author Ankit Yadav
 * @author Sudheer Keshav Bhat
 */
public class FormChatThread extends FormHikeBase {

    private Command mSendCmd;
    private Command mBackCmd;
    private Command mBlockCmd;
    private Command mLeaveGroupChatCmd;
    private Command mGroupInfoCmd;
    private Command mInviteCmd;
    private Command mCallCmd;
    private Command mFreeSMSCommand;
    private static final String TAG = "FormChatThread";
    private Container chatScreen = new Container(new BoxLayout(BoxLayout.Y_AXIS));
    private SendContainer sendBox = new SendContainer();
    private Hashtable mModelToListItemSDRs = new Hashtable();
    private Hashtable mModelToListItems = new Hashtable();
    private Border whiteBorder, blueBorder, greenBorder;
    private Vector blockList = AppState.getUserDetails().getBlocklist();
    private String mTitleTxt;
    private Container lastContainer;
    //#if nokia2_0
//#     private PopupList mCtxMenu;
//#     private PopupListItem mMenuItemGetFile;
    //#endif
    private ChatModel mLongPressedModel;
    private ChatEntity chatEntity;
    private Timer mEventTimer;
    private EventTimeTracker mEventTracker;
    private int index = 0;
    private Vector chatList;

    /**
     * Constructor of Chat thread form. This form will show all the previous and current chat on the screen.
     */
    public FormChatThread() {

        getStyle().setBgColor(ColorCodes.chatScreenBgColor, true);
        getStyle().setBgTransparency(255, true);

        setLayout(new BorderLayout());
        setScrollable(false);
        chatScreen.getStyle().setPadding(Component.TOP, 10, true);
        chatScreen.setScrollableY(true);
        chatScreen.setScrollVisible(false);
        chatScreen.setSmoothScrolling(true);
        chatScreen.getStyle().setBgTransparency(0);
        chatScreen.getStyle().setPadding(Component.BOTTOM, 12, true);

        addComponent(BorderLayout.CENTER, chatScreen);
        addComponent(BorderLayout.SOUTH, sendBox);

        initContextMenu();

        ChatList.getInstance().addCollectionUpdateListener(new ChatListnerImpl());

        initCommands();

        Log.v(TAG, "FormChatThread()::end");
    }

    
    /**
     * This method will initialize context menu which comes on long press of chat bubble.
     */
    private void initContextMenu() {
        //#if nokia2_0
//#         mCtxMenu = new PopupList("context actions");
//#         mMenuItemGetFile = new PopupListItem(LBL_GET_FILE);
//#         mCtxMenu.appendItem(new PopupListItem(LBL_COPY));
//#         mCtxMenu.appendItem(new PopupListItem(LBL_FORWARD));
//#         mCtxMenu.appendItem(new PopupListItem(LBL_DELETE));
//#         mCtxMenu.setListener(new ChatPopupListenerImpl());
        //#endif
    }

    
    /**
     * This method adds all the chats to the screen of the current chat model.
     * @param model 
     */
    private synchronized void addChat(final ChatModel model) {
                 
        Log.v(TAG, "addChat()");
        final Container container;
        addTimeStampContainer(model);
        if (model.getType() == MessageType.SYSTEM) {
            container = new NotificationContainer(model);
            if (chatEntity.isGroupChat() && !chatEntity.isActive()) {
                sendBox.setSendEnabled(false, MSG_IN_CHATBOX_WHEN_GROUP_CHAT_ENDED);
                removeCommand(mGroupInfoCmd);
                removeCommand(mLeaveGroupChatCmd);
                removeCommand(mBlockCmd);
            }

                     
            Log.v(TAG, "model is system message" + model.getFromMsisdn() + model.getToMsisdn());
        } else {
            container = new ChatBubbleContainer(model);
        }
        mModelToListItems.put(model, container);
        chatScreen.addComponent(container);
        Log.v(TAG, "Chat buuble container width and height: "+"Width: "+ container.getWidth() + "Height" + container.getHeight());
        Log.v(TAG, "Chat buuble container preferred width and height: "+"Width: "+ container.getPreferredW() + "Height" + container.getPreferredH());
        Log.v(TAG, "Chat buuble container layout width and height: "+"Width: "+ container.getLayoutWidth() + "Height" + container.getLayoutHeight());
        
        if(!(chatEntity.isBlocked() || (chatEntity.isSmsChat() && AppState.getUserDetails().getSmsCredit() == 0))) {
            chatScreen.scrollRectToVisible(chatScreen.getWidth() - container.getWidth(), chatScreen.getLayoutHeight() - container.getHeight(), container.getWidth(), container.getHeight(), chatScreen);
        }
        lastContainer = container;
        revalidate();
        repaint();
        
                 
        Log.v(TAG, "addChat()::end");
    }

    
    /**
     * This method sets 9 patch image border to every chat bubble based on whether the chat is with hike user or SMS user.
     * @param ct
     * @param model 
     */
    private void set9PatchImageborder(ChatBubbleTextArea ct, ChatModel model) {
        if (ct == null || model == null) {
            return;
        }
        if (model.isMe()) {
            if (model.getType() == MessageType.HIKE) {
                if (blueBorder == null) {
                    blueBorder = Border.createImageBorder(AppResource.getImageFromResource(bTop), AppResource.getImageFromResource(bBottom), AppResource.getImageFromResource(bLeft), AppResource.getImageFromResource(bRight), AppResource.getImageFromResource(bTopleft), AppResource.getImageFromResource(bTopright), AppResource.getImageFromResource(bBottomleft), AppResource.getImageFromResource(bBottomright), AppResource.getImageFromResource(bBackground));
                }
                ct.getStyle().setBorder(blueBorder, true);
                ct.getSelectedStyle().setBorder(blueBorder, true);

            } else if (model.getType() == MessageType.SMS) {
                if (greenBorder == null) {
                    greenBorder = Border.createImageBorder(AppResource.getImageFromResource(gTop), AppResource.getImageFromResource(gBottom), AppResource.getImageFromResource(gLeft), AppResource.getImageFromResource(gRight), AppResource.getImageFromResource(gTopleft), AppResource.getImageFromResource(gTopright), AppResource.getImageFromResource(gBottomleft), AppResource.getImageFromResource(gBottomright), AppResource.getImageFromResource(gBackground));
                }
                ct.getStyle().setBorder(greenBorder, true);
                ct.getSelectedStyle().setBorder(greenBorder, true);
            }
            ct.getStyle().setPadding(8, 8, 8, 8);
            ct.getSelectedStyle().setPadding(8, 8, 8, 8);
        } else {
            if (whiteBorder == null) {
                whiteBorder = Border.createImageBorder(AppResource.getImageFromResource(wTop), AppResource.getImageFromResource(wBottom), AppResource.getImageFromResource(wLeft), AppResource.getImageFromResource(wRight), AppResource.getImageFromResource(wTopleft), AppResource.getImageFromResource(wTopright), AppResource.getImageFromResource(wBottomleft), AppResource.getImageFromResource(wBottomright), AppResource.getImageFromResource(wBackground));
            }
            ct.getStyle().setPadding(8, 8, 14, 8);
            ct.getSelectedStyle().setPadding(8, 8, 14, 8);
            ct.getStyle().setBorder(whiteBorder, true);
            ct.getSelectedStyle().setBorder(whiteBorder, true);
        }
    }

    
    /**
     * This method shows typing notification on chat screen.
     * @param model 
     */
    public void setTypingStatus(ConversationModel model) {
        if (!chatEntity.isGroupChat() || !chatEntity.isSmsChat()) {
            if (model.getConversationID() == chatEntity.getConversation().getConversationID()) {
                sendBox.setTypingStatus(model);
            }
        } else {
            sendBox.showSmsCount(false);
        }
    }

    
    /**
     * This delegate method is called just after showing chat form to the user. This method will call all other methods, like adding chat to the
     * screen, user is blocked or not, etc.
     */
    protected synchronized void onShow() {
        super.onShow();
        //#if nokia2_0
//#         VirtualKeyboard.setVisibilityListener(new KeyboardVisibilityListenerImpl());
        //#endif
                 
        Log.v(TAG, "onShow()");
        chatEntity = new ChatEntity(AppState.getUserDetails().getSelectedMsisdn());

        if (chatEntity.isGroupChat()) {
            if (chatEntity.getModel() == null) {
                //TODO 
                AppState.setForm(DisplayStackManager.FORM_CONVERSATION);
                ConversationList.getInstance().removeElement(ConversationList.getInstance().getEntryByMsisdn(chatEntity.getChatMsisdn()));
                DisplayStackManager.showForm(DisplayStackManager.FORM_ERROR);
            } else {
                sendBox.removeSMSContainerInGroupChat(true);
                AppState.getUserDetails().setCurrentGrp((GroupMembers) chatEntity.getModel());
            }
        } else {
            sendBox.removeSMSContainerInGroupChat(false);
        }
        
        Log.v(TAG, "Is SMS Chat " + chatEntity.isSmsChat() + ", User SMS credits " + AppState.getUserDetails().getSmsCredit());
        
        setChatTitle();
        initChatList();
        updateScreenForHikeStatus();
    }
    
    
    /**
     * This method removes all the component from the screen when deinitialize() called.
     */
    protected void deinitialize() {
        super.deinitialize();
        Log.v(TAG, "DeInitialize chat screen and remove all components");
        UIHelper.runOnLwuitUiThread(new Runnable() {

            public void run() {
                chatScreen.removeAll();
            }
        });
    }

    
    /**
     * This method is called when user opens chat form for a particular user. This method is used to copy message on the TextArea of chat form when 
     * user wants to forward the message.
     */
    private void processMsgForward() {
        if(AppState.workFlow == WORKFLOW_BACK) {
            AppState.workFlow = WORKFLOW_NONE;
            sendBox.setSendText(EMPTY_STRING);
        }else if (AppState.workFlow == WORKFLOW_FORWARD) {
            AppState.workFlow = WORKFLOW_NONE;
            String text = ClipboardManager.getInstance().paste();
                     
            Log.v(TAG, "forwarded msg: " + text);
            sendBox.setSendText(text);
        }
    }

    
    /**
     * This delegated method is called just after completion of onShow() method call. This will disable chat TextArea for 1 millisecond to control the 
     * visibility of the keyboard when chat thread opens.
     */
    protected void onShowCompleted() {
        super.onShowCompleted();
                
        Log.v(TAG, "---------------onShow completed");
        
//        String msg = chatEntity.isBlocked() ? MSG_IN_CHATBOX_FOR_BLOCKED_USER : (chatEntity.isGroupChat() && !chatEntity.isActive() ? MSG_IN_CHATBOX_WHEN_GROUP_CHAT_ENDED : EMPTY_STRING);
        sendBox.setSendEnabled(false);
        UIHelper.runOnLwuitUiThread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(1);
                } catch (Exception ex) {
                } finally {
                    setChatState();
                    processMsgForward();
                    repaint();
                    if (chatEntity.isBlocked()) {
                        initBlockScreen();
                    }
                }
            }
        });
    }

    
    /**
     * This method is for updating screen according to the status of the contact with whom user is chatting. 
     */
    public void updateScreenForHikeStatus() {
        if (chatEntity != null) {

            sendBox.showSmsCount(chatEntity.isSmsChat());

            removeCommand(mInviteCmd);
            removeCommand(mFreeSMSCommand);
            removeCommand(mCallCmd);
            removeCommand(mLeaveGroupChatCmd);
            removeCommand(mGroupInfoCmd);
            removeCommand(mBlockCmd);

            if (!chatEntity.isGroupChat()) {
                addCommand(mCallCmd);
            }
            if (chatEntity.isSmsChat()) {
                addCommand(mInviteCmd);
                if(AppState.getUserDetails().getCountryCode().equals(DEFAULT_COUNTRY_CODE)) {
                    addCommand(mFreeSMSCommand);
                }
            }

            Log.v(TAG, "updateCommand(): ChatEntity" + chatEntity);
           
            if (chatEntity.isGroupChat()) {
                if (chatEntity.isActive()) {
                    addCommand(mLeaveGroupChatCmd);
                    addCommand(mGroupInfoCmd);
                    addCommand(mBlockCmd);
                } else {
                    removeCommand(mBlockCmd);
                }
            } else {
                if (chatEntity.msisdnToBlock() != null) {
                    addCommand(mBlockCmd);
                }
            }
            
            if(!Validator.validatePhoneNum(chatEntity.getChatMsisdn())) {
                removeCommand(mBlockCmd);
                removeCommand(mCallCmd);
            }
                
            if (lastContainer != null && lastContainer instanceof ChatBubbleContainer) {
                ((ChatBubbleContainer) lastContainer).repaint9PatchBorder();
            }
        }
        repaint();
    }

    
    /**
     * This method initializes all the commands which are there on the current form.
     */
    private void initCommands() {

        mBlockCmd = new Command(LBL_BLOCK_USER) {
            public void actionPerformed(ActionEvent evt) {
                if (chatEntity.msisdnToBlock() != null && !chatEntity.isBlocked()) {

                    UIHelper.runOnLwuitUiThread(new Runnable() {
                        public void run() {
                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException ex) {
                            } finally {
                                setChatState();
                                MqttManager.blockUser(true, chatEntity.msisdnToBlock());
                                blockList.addElement(chatEntity.msisdnToBlock());
                                initBlockScreen();
                            }
                        }
                    });

                }
            }
        };

        mInviteCmd = new Command(LBL_INVITE) {
            public void actionPerformed(ActionEvent evt) {
                if (AppState.getUserDetails().getAccountInfo() != null) {
                    ModelUtils.addChat(AppState.getUserDetails().getAccountInfo().getInviteMessage(), new Date().getTime(), null, chatEntity.getChatMsisdn(), 0, ChatModel.MessageStatus.SENDING, MessageType.SMS, true);
                            
                    Log.v(TAG, "actionPerformed()5");
                }
            }
        };

        mBackCmd = new Command(LBL_BACK) {
            public void actionPerformed(ActionEvent evt) {
                AppState.getUserDetails().setCurrentGrp(null);
                DisplayStackManager.showForm(DisplayStackManager.FORM_CONVERSATION);
                sendBox.cancelTimerOnBack();
                AppResource.releaseSmileyImages();
                sendBox.prevMsgsBtn.setEnabled(false);
                sendBox.nextMsgsBtn.setEnabled(false);
                sendBox.setSendEnabled(false, EMPTY_STRING);
                chatList = null;
            }
        };

        mSendCmd = new Command(LBL_SEND, AppResource.getImageFromResource(sentInviteImage)) {
            boolean clicked = false;

            public void actionPerformed(ActionEvent evt) {
                         
                Log.v(TAG, "actionPerformed()");
                if (!clicked && !chatEntity.isBlocked()) {
                            
                    Log.v(TAG, "actionPerformed()2");
                    clicked = true;
                    if(!(chatEntity.isSmsChat() && AppState.getUserDetails().getLocalSMSCredits() == 0)) {
                        sendBox.sendAction(chatEntity);
                    }else {
                        initBlockScreenWhenSMSIsZero();
                    }
                    AppState.getUserDetails().setLocalSMSCredits(sendBox.noOfMessagesTyped);
                    clicked = false;
                }
                         
                Log.v(TAG, "actionPerformed()::end");
            }
        };

        mCallCmd = new Command(LBL_CALL) {
            public void actionPerformed(ActionEvent evt) {
                        
                Log.v(TAG, "Clicked on call label");
                makeACall();
            }
        };


        mLeaveGroupChatCmd = new Command(LBL_LEAVE_GRP_CHAT) {
            public void actionPerformed(ActionEvent evt) {
                ConversationList.getInstance().removeElement(ConversationList.getInstance().getEntryByMsisdn(chatEntity.getChatMsisdn()));
                MqttManager.leaveGroupChat(chatEntity.getChatMsisdn());
                AppState.getUserDetails().setCurrentGrp(null);
                DisplayStackManager.showForm(DisplayStackManager.FORM_CONVERSATION);
            }
        };

        mGroupInfoCmd = new Command(LBL_INFO) {
            public void actionPerformed(ActionEvent evt) {
                DisplayStackManager.showForm(DisplayStackManager.FORM_GROUP_INFO);
            }
        };

        mFreeSMSCommand = new Command(LBL_FREE_SMS) {
            public void actionPerformed(ActionEvent evt) {
                super.actionPerformed(evt);
                AppState.fromScreen = DisplayStackManager.FORM_CHAT;
                DisplayStackManager.showForm(DisplayStackManager.FORM_FREE_SMS);
            }
        };

        addCommand(mBackCmd);
        addCommand(mSendCmd);
        setDefaultCommand(mSendCmd);
        setBackCommand(mBackCmd);
    }

    
    /**
     * This method loads the next set of chat messages
     */
    public void getNextChat() {
        Log.v(TAG, "Get Next chat index = " + index + ":");
        if (index + MAX_CHAT_LIMIT < chatList.size()) {
            UIHelper.runOnLwuitUiThread(new Runnable() {

                public void run() {
                     Log.v(TAG, "If condition in getNextChat()");
                    sendBox.prevMsgsBtn.setEnabled(true);
                    chatScreen.removeAll();
                    Log.v(TAG, "after removing all elements from chat in getNextChat()");
                    index = index + MAX_CHAT_LIMIT;
                    Log.v(TAG, "before addElementsToChatScreen in getNextChat()");
                    addElementsToChatScreen(index);
                    if (index + MAX_CHAT_LIMIT >= chatList.size()) {
                        Log.v(TAG, "Nested If condition in getNextChat()");
                        sendBox.nextMsgsBtn.setEnabled(false);
                    }
                }
            });
        }
    }
    
    
    /**
     * This method loads the previous set of chat messages
     */
    public void getpreviousChat() {
        Log.v(TAG, "Get previous chat index" + index);
        if(index > 0) {
            UIHelper.runOnLwuitUiThread(new Runnable() {
                public void run() {
                    Log.v(TAG, "If condition in getpreviousChat()");
                    sendBox.nextMsgsBtn.setEnabled(true);
                    chatScreen.removeAll();
                    index = (index - MAX_CHAT_LIMIT) < 0 ? 0 : index - MAX_CHAT_LIMIT;
                    addElementsToChatScreen(index);
                    if (index <= 0) {
                        Log.v(TAG, "Nested If condition in getpreviousChat()");
                        sendBox.prevMsgsBtn.setEnabled(false);
                    }
                }
            });
        }
    }
    
    public int getIndex() {
        return index;
    }

    /**
     * @return the mTitleTxt
     */
    public String getTitleTxt() {
        return mTitleTxt;
    }

    
    /**
     * This method is called to set chat state of chat screen.
     */
    public void setChatState() {
        removeCommand(mBlockCmd);

        //Block and unblock command update with enable or disable chat box
        if (chatEntity.isBlocked()) {
            mBlockCmd.setCommandName(LBL_UNBLOCK_USER);
            sendBox.setSendEnabled(false);
        } else if (chatEntity.isGroupChat() && !chatEntity.isActive()) {
            mBlockCmd.setCommandName(LBL_BLOCK_USER);
            sendBox.setSendEnabled(false);
        } else if (chatEntity.isSmsChat() && AppState.getUserDetails().getSmsCredit() == 0) {
            //Disable chat box if user has zero SMS left
            initBlockScreenWhenSMSIsZero();
        } else {
            mBlockCmd.setCommandName(LBL_BLOCK_USER);
            sendBox.setSendEnabled(true);
        }
        if (chatEntity.msisdnToBlock() != null) {
            if (chatEntity.isGroupChat() && !chatEntity.isActive()) {
                removeCommand(mBlockCmd);
            } else {
                addCommand(mBlockCmd);
            }
        }
    }

    
    /**
     * This method is called to set the form title
     */
    private void setChatTitle() {
        mTitleTxt = chatEntity.getName();
        getTitleComponent().setIcon(null);
        getTitleComponent().getStyle().setMargin(Component.TOP, 6, true);
        setTitle(getTitleTxt());
    }

    
    /**
     * This method is called to initialize chat list on the screen. This will add all the chats one by one.
     */
    private synchronized void initChatList() {
                
        Log.v(TAG, "selection: " + chatEntity.getChatMsisdn());
        chatScreen.removeAll();
                
        Log.v(TAG, "chat screen cleared: ");
       
        if (chatEntity.getChatMsisdn() != null && chatEntity.getConversation() != null) {
            long converseId = chatEntity.getConversation().getConversationID();
            chatList = ChatList.getInstance().getChats(converseId);
            int count;
//          = chatList.size() > MAX_CHAT_LIMIT ? chatList.size() - (chatList.size() % MAX_CHAT_LIMIT == 0 ? MAX_CHAT_LIMIT: chatList.size() % MAX_CHAT_LIMIT) : 0;
            if(chatList.size() > MAX_CHAT_LIMIT) {
                if(chatList.size() % MAX_CHAT_LIMIT == 0) {
                    count = chatList.size() - MAX_CHAT_LIMIT;
                }else {
                    count = chatList.size() - chatList.size() % MAX_CHAT_LIMIT;
                }
            }else {
                count = 0;
            }
            index = count;
            Log.v(TAG, "index inside initChatList = " + index);
            Log.v(TAG, "chat list size inside initChatList = " + chatList.size());
            addElementsToChatScreen(count);
            if(chatList.size() > MAX_CHAT_LIMIT) {
                Log.v(TAG, "enabling previous button  inside initChatList = " + chatList.size());
                sendBox.prevMsgsBtn.setEnabled(true);
            }else{
                sendBox.prevMsgsBtn.setEnabled(false);
            }
        }else {
            Log.v(TAG, "Both pagination buttons are disabled");
            index = 0;
            sendBox.nextMsgsBtn.setEnabled(false);
            sendBox.prevMsgsBtn.setEnabled(false);
        }
    }

    private void addElementsToChatScreen(int count){
        int MAX = count + MAX_CHAT_LIMIT;
        Log.v(TAG, "index=" + index + "MAX=" + MAX);
        Vector chatModels = new Vector();
        while (chatList != null && count < chatList.size() && count < MAX) {
                ChatModel model = (ChatModel) chatList.elementAt(count);
                long convId = model.getConversationID();
                         
                Log.v(TAG, "ConvID=" + convId);
                
                if (canAdd(chatEntity.getConversation(), model)) {
                    addChat(model);
                    if (model.getType() == MessageType.SYSTEM) {
                        model.setMessageStatus(ChatModel.MessageStatus.READ);
                    } else {
                        chatModels.addElement(model);
                    }
                }
                count++;
            }
            if (!chatModels.isEmpty()) {
                MqttManager.markAsRead(chatModels);
            }
        }
    
    /**
     * This method is called to set the selected smiley on TextArea of chat screen.
     * @param imgStr 
     */
    public void setSelectedSmiley(String imgStr) {
        sendBox.setSelectedSmiley(imgStr);
    }

    
    /**
     * This method is called when user is blocked.
     */
    private void initBlockScreen() {
        ((FormUserBlocked) DisplayStackManager.getForm(DisplayStackManager.FORM_USER_BLOCKED, true)).setBlockedChat(chatEntity);
        DisplayStackManager.showForm(DisplayStackManager.FORM_USER_BLOCKED);
    }

    
    /**
     * This method is called when user doesn't have SMS credits left in his account.
     */
    public void initBlockScreenWhenSMSIsZero() {
        DisplayStackManager.showForm(DisplayStackManager.FORM_ZERO_SMS_LEFT);
    
    }

    
    /**
     * This method is called when user wants to make a call to the number to whom he started chat.
     * @return 
     */
    private boolean makeACall() {
        boolean isCalled = false;
        try {
            isCalled = Hike.sMidlet.platformRequest("tel:" + AppState.getUserDetails().getSelectedMsisdn());
        } catch (Exception e) {
            isCalled = false;
            //Handle the functionality for wrong format of number
        }
        return isCalled;
    }

    
    /**
     * This method is used to check line break in chat text.
     * @param input
     * @return 
     */
    private boolean checkforLineBreak(String input) {
        int index = input.indexOf("\n");
        if (index < 0) {
            return false;
        } else {
            return true;
        }
    }

    
    /**
     * This method is called to add timestamp to the chat screen.
     * @param model 
     */
    private synchronized void addTimeStampContainer(ChatModel model) {
        if (chatScreen.getComponentCount() == 0) {
            chatScreen.addComponent(new TimeStampContainer(model.getTimestamp()));
                    
            Log.v(TAG, "timestamp added to empty list");
        } else {
            Component last = chatScreen.getComponentAt(chatScreen.getComponentCount() - 1);
                     
            Log.v(TAG, "last container is: " + last.getClass().getName());
            if (last instanceof NotificationContainer) {
                long lastTime = ((NotificationContainer) last).getModel().getTimestamp();
                         
                Log.v(TAG, "delay is: " + (model.getTimestamp() - lastTime));
                
                if ((model.getTimestamp() - lastTime > TIME_STAMP_DELAY)) {
                    chatScreen.addComponent(new TimeStampContainer(model.getTimestamp()));
                             
                    Log.v(TAG, "timestamp added after ChatBubbleContainer");
                }
            } else if (last instanceof ChatBubbleContainer) {
                long lastTime = ((ChatBubbleContainer) last).getModel().getTimestamp();
                         
                Log.v(TAG, "delay is: " + (model.getTimestamp() - lastTime));
                if ((model.getTimestamp() - lastTime > TIME_STAMP_DELAY)) {
                    chatScreen.addComponent(new TimeStampContainer(model.getTimestamp()));
                             
                    Log.v(TAG, "timestamp added after ChatBubbleContainer");
                }
            }
        }
    }

    
    /**
     * ChatBubble container class returns the chat bubble and also handles long press events on it.
     */
    private class ChatBubbleContainer extends Container {

        ChatModel model;
        Container chatBubbleContainer;
        ChatBubbleTextArea msgTxtArea;
        String msgTxt;      
        public ChatBubbleContainer(final ChatModel model) {
            this.model = model;
            setLayout(new BorderLayout());         
            if (chatEntity.isGroupChat() && !model.isMe()) {
                GroupMember aMember = chatEntity.getModel() != null ? (GroupMember) ((GroupMembers) chatEntity.getModel()).getMembers().get(model.getFromMsisdn()) : null;
                String name = aMember != null ? aMember.getName() : model.getFromMsisdn();
                         
                Log.v(TAG, name);
                msgTxt = name + " - " + model.getMessage();
            } else {
                msgTxt = model.getMessage();
            }
            msgTxtArea = new ChatBubbleTextArea(msgTxt, CharLimit.CHAT);
            msgTxtArea.setPointerPressListener(new ChatBubbleTextArea.PointerPressListener() {
                int dy = 0;
                public void onPointerPressed(int x, int y) {
                    Log.v(TAG, "pointerPressed::");
                    mEventTimer = new Timer();
                    mEventTracker = new EventTimeTracker(y, model, msgTxt);
                    mEventTimer.schedule(mEventTracker, EVENT_TRIGGER_TIME_OUT);
                    
                    dy = y;
                }

                public void onPointerReleased(int x, int y) {
                    Log.v(TAG, "pointerReleased::");
                    mEventTimer.cancel();
                }

                public void onPointerDragged(int x, int y) {
                    Log.v(TAG, "pointerDragged: A");
                    if (mEventTimer != null && Math.abs(dy-y) > 5) {
                        System.out.println("onPointerDragged");
                        mEventTimer.cancel();
                    }
                }
            });

            if (checkforLineBreak(msgTxt)) {
                msgTxtArea.setRows(3);
                msgTxtArea.setColumns(TextUtils.maxCharInRow(msgTxt));
            }
            
            chatBubbleContainer = new Container(new BorderLayout());
            set9PatchImageborder(msgTxtArea, model);
            if (model.isMe()) {
                getStyle().setMargin(Component.RIGHT, 3, true);
                getSelectedStyle().setMargin(Component.RIGHT, 3, true);

                final Label sdrImgLbl = new Label();
                sdrImgLbl.getStyle().setBgTransparency(0, true);

                //#if nokia2_0
//#                     sdrImgLbl.getStyle().setPadding(Component.BOTTOM, 10, true);
                //#elif nokia1_1
//#                 sdrImgLbl.getStyle().setPadding(Component.BOTTOM, 3, true);
                //#endif

                sdrImgLbl.getStyle().setAlignment(Component.RIGHT, true);

                chatBubbleContainer.addComponent(BorderLayout.CENTER,sdrImgLbl);
                chatBubbleContainer.addComponent(BorderLayout.EAST, msgTxtArea);
                addComponent(BorderLayout.EAST, chatBubbleContainer);
                mModelToListItemSDRs.put(model, sdrImgLbl);
                Image sdr = null;
                if (model.getMessageStatus() == ChatModel.MessageStatus.SENDING) {
                    new Timer().schedule(new TimerTask() {
                        public void run() {
                            Image sdr = UIHelper.getMsgStatusIcon(model);
                            sdrImgLbl.setIcon(sdr);
                        }
                    }, CHAT_SENDING_ICN_DISPLAY_DELAY);
                } else {
                    sdr = UIHelper.getMsgStatusIcon(model);
                }
                sdrImgLbl.setIcon(sdr);
            } else {
                getStyle().setMargin(Component.LEFT, 3, true);
                getSelectedStyle().setMargin(Component.LEFT, 3, true);

                getStyle().setMargin(Component.RIGHT, 7, true);
                getSelectedStyle().setMargin(Component.RIGHT, 7, true);

                addComponent(BorderLayout.WEST, msgTxtArea);
            }
        }

        public void repaint9PatchBorder() {
            set9PatchImageborder(msgTxtArea, model);
        }

        public ChatModel getModel() {
            return model;
        }
      }
    
    
    
    /**
     * This class tracks the user for long press on a bubble.
     */
    private class EventTimeTracker extends TimerTask {

        private int mPosition;
        private ChatModel model;
        private String msgTxt;

        public EventTimeTracker(int y, ChatModel model, String msgTxt) {
            mPosition = y;
            this.model = model;
            this.msgTxt = msgTxt;
        }

        public void run() {
            mLongPressedModel = model;
            //#if nokia2_0
//#             String link = TextUtils.getLink(msgTxt);
//#             mCtxMenu.removeItem(mMenuItemGetFile);
//#             if (link != null) {
//#                 mCtxMenu.appendItem(mMenuItemGetFile);
//#             }
//#             mCtxMenu.setListYPos(mPosition);
//#             mCtxMenu.setVisible(true);
            //#endif

                     
            Log.v(TAG, " Event can be triggered");
            mEventTracker.cancel();
        }
    }
    
    
    /**
     * This class returns the timestamp container
     */
    private class TimeStampContainer extends Container {

        public TimeStampContainer(long timestamp) {
            setLayout(new BoxLayout(BoxLayout.X_AXIS));
            String timeString = TextUtils.toTimestamp(timestamp);
            Label timeStamp = new Label(timeString);
                    
            Log.v(TAG, timeString);
            timeStamp.getStyle().setFont(Fonts.SMALL, true);
            timeStamp.getSelectedStyle().setFont(Fonts.SMALL, true);
            timeStamp.getStyle().setBgTransparency(255, true);
            timeStamp.getStyle().setFgColor(ColorCodes.chatScreenTimeStampColor, true);
            timeStamp.getStyle().setBgColor(ColorCodes.chatScreenBgColor, true);
            timeStamp.getStyle().setAlignment(Component.CENTER);
            timeStamp.getStyle().setMargin(Component.LEFT, (Display.getInstance().getDisplayWidth() - timeStamp.getStyle().getFont().stringWidth(timeStamp.getText())) / 2, true);

            addComponent(timeStamp);
        }

        public void paintBackground(Graphics g) {
            super.paintBackground(g);

            g.setColor(ColorCodes.chatScreenTimeStampColor);
            g.drawLine(getX(), getY() + 12, getWidth(), getY() + 12);
        }
    }

    
    /**
     * This class returns Notification container when some notification arrives from the server.
     */
    private class NotificationContainer extends Container {

        ChatModel model;
        Label notificationIcon;
        TextArea notificationMsg;

        public NotificationContainer(ChatModel model) {
            this.model = model;
            setLayout(new BorderLayout());
            getStyle().setBgColor(ColorCodes.chatScreenNotificationBg, true);
            getStyle().setBgTransparency(255, true);
            getStyle().setAlignment(Component.CENTER, true);
            getStyle().setMargin(5, 5, 7, 5);
            getStyle().setBorder(Border.createRoundBorder(2, 2, false), true);
            String message = model.getMessage();
                     
            Log.v(TAG, "System Message: " + model.getMessage() + ", msisdn: " + model.getFromMsisdn());
            String iconPath;
            if (TextUtils.contains(message, SYSTEM_MSG_MEMBER_ADDED.trim())) {
                GroupMember member = chatEntity.isGroupChat() && chatEntity.getModel() != null ? ((GroupMembers) chatEntity.getModel()).getMember(model.getFromMsisdn()) : null;
                         
                Log.v(TAG, "group member: " + member);
                if (member != null && member.isOnHike()) {
                    iconPath = PATH_SYSTEM_ICON_HIKE;
                } else {
                    iconPath = PATH_SYSTEM_ICON_NON_HIKE;
                }
            } else if (TextUtils.contains(message, SYSTEM_MSG_WAITING_DND_USERS_PREFIX.trim()) || TextUtils.contains(message, SYSTEM_MSG_WAITING_DND_USERS_PREFIX_GC.trim())) {
                iconPath = PATH_SYSTEM_USER_WAITING;
            } else if (TextUtils.contains(message, SYSTEM_MSG_USER_CREDIT_PREFIX.trim())) {
                iconPath = PATH_SYSTEM_ICON_FREE_SMS;
            } else if (TextUtils.contains(message, SYSTEM_MSG_MEMBER_REMOVED.trim())) {
                iconPath = PATH_SYSTEM_ICON_LEFT;
            } else if (TextUtils.contains(message, SYSTEM_MSG_MEMBER_INVITED.trim())) {
                iconPath = PATH_SYSTEM_ICON_NON_HIKE;
            } else if (TextUtils.contains(message, SYSTEM_MSG_USER_JOINED.trim()) || TextUtils.contains(message, SYSTEM_MSG_USER_OPTED_IN.trim())) {
                iconPath = PATH_SYSTEM_ICON_JOINED;
            } else if (TextUtils.contains(message, SYSTEM_MSG_NO_INTERNATIONAL_SMS.trim())) {
                iconPath = PATH_SYSTEM_ICON_BIS;
            } else {
                iconPath = PATH_SYSTEM_ICON_JOINED;
            }
                     
            Log.v(TAG, "Image Path: " + iconPath);
            notificationIcon = new Label(AppResource.getImageFromResource(iconPath));
            notificationIcon.getStyle().setBgTransparency(0, true);
            notificationIcon.getStyle().setPadding(Component.LEFT, 4, true);
            notificationIcon.getStyle().setPadding(Component.TOP, 2, true);
            notificationIcon.getStyle().setPadding(Component.BOTTOM, 2, true);
            notificationIcon.setVerticalAlignment(Component.CENTER);

            notificationMsg = new TextArea();
            notificationMsg.setEditable(false);
            notificationMsg.getStyle().setPadding(Component.TOP, 2, true);
            notificationMsg.getSelectedStyle().setPadding(Component.TOP, 2, true);
            notificationMsg.getStyle().setPadding(Component.BOTTOM, 2, true);
            notificationMsg.getSelectedStyle().setPadding(Component.BOTTOM, 2, true);
            notificationMsg.getStyle().setPadding(Component.RIGHT, 3, true);
            notificationMsg.getSelectedStyle().setPadding(Component.RIGHT, 3, true);
            notificationMsg.getStyle().setPadding(Component.LEFT, 2, true);
            notificationMsg.getSelectedStyle().setPadding(Component.LEFT, 2, true);
            notificationMsg.getStyle().setBgTransparency(255, true);
            notificationMsg.getSelectedStyle().setBgTransparency(255, true);
            notificationMsg.getStyle().setBgColor(ColorCodes.chatScreenNotificationBg, true);
            notificationMsg.getSelectedStyle().setBgColor(ColorCodes.chatScreenNotificationBg, true);
            notificationMsg.getStyle().setBorder(Border.createEmpty(), true);
            notificationMsg.getSelectedStyle().setBorder(Border.createEmpty(), true);
            if (TextUtils.contains(message, SYSTEM_MSG_USER_OPTED_IN.trim())) {
                String gcmsg = message.substring(message.indexOf(SYSTEM_MSG_USER_OPTED_IN)) + SYSTEM_MSG_MEMBER_ADDED;
                notificationMsg.setText(gcmsg);
            } else {
                notificationMsg.setText(message);
            }
            
            notificationMsg.setText(message);
            if (notificationMsg.getStyle().getFont().stringWidth(notificationMsg.getText()) > Display.getInstance().getDisplayWidth() - notificationIcon.getIcon().getWidth()) {
                notificationMsg.setSingleLineTextArea(false);
                notificationMsg.setRows(2);
            }
            notificationMsg.getStyle().setFont(Fonts.SMALL, true);
            notificationMsg.getSelectedStyle().setFont(Fonts.SMALL, true);
            notificationMsg.getStyle().setFgColor(ColorCodes.chatScreenNotificationTextColor, true);
            notificationMsg.getSelectedStyle().setFgColor(ColorCodes.chatScreenNotificationTextColor, true);
            notificationMsg.getStyle().setAlignment(Component.CENTER, true);
            notificationMsg.getSelectedStyle().setAlignment(Component.CENTER, true);

            addComponent(BorderLayout.WEST, notificationIcon);
            addComponent(BorderLayout.CENTER, notificationMsg);
        }

        public ChatModel getModel() {
            return model;
        }
    }
    
    
    /**
     * This method checks whether the chat should be added to the screen or not.
     * @param conv
     * @param chat
     * @return 
     */
    private boolean canAdd(ConversationModel conv, ChatModel chat) {
            if (chat == null) {
                return false;
            }
            if (chat.getConversationID() == SYSTEM_CONVERSATION) {
                if (chatEntity.isGroupChat() && chatEntity.getModel() != null && ((GroupMembers) chatEntity.getModel()).hasMember(chat.getFromMsisdn())) {
                    return true;
                } else if (chatEntity.getChatMsisdn().equals(chat.getFromMsisdn())) {
                    return true;
                }
                return false;
            } else if (conv == null) {
                return (chat.getFromMsisdn() != null && chat.getFromMsisdn().equals(chatEntity.getChatMsisdn()))
                        || (chat.getToMsisdn() != null && chat.getToMsisdn().equals(chatEntity.getChatMsisdn()));
            } else if (conv.getConversationID() == chat.getConversationID()) {
                return true;
            } else {
                   return false;
            }
        }

    
    /**
     * This class keeps track of data which is coming from the server. As soon as the collectionUpdateListener notices data change, it will call 
     * modelAdded(), modelRemoved(), modelContentsUpdated() and updates the UI.
     */
    private class ChatListnerImpl implements CollectionUpdateListener {

        public void modelAdded(CollectionUpdateEvent event) {
                    
            Log.v(TAG, "modelAdded()");
            if (chatEntity.getChatMsisdn() == null || !(Display.getInstance().getCurrent() instanceof FormChatThread)) {
                return;
            }
                     
            Log.v(TAG, "selected contact: " + chatEntity.getChatMsisdn());
            DataModel affectedModel = event.getAffectedModel();
            if (affectedModel instanceof ChatModel) {
                        
                final ChatModel model = (ChatModel) affectedModel;
                ConversationModel convModel = ConversationList.getInstance().getEntryByMsisdn(chatEntity.getChatMsisdn());
                        
                Log.v(TAG, "modelAdded " + model + ", conv model: " + convModel + " conv List size: " + ConversationList.getInstance().size());
                if (canAdd(convModel, model)) {
                    if (chatList == null && chatEntity !=null && chatEntity.getConversation() != null) {
                        Log.v(TAG, "Initializing chat list modelAdded()");
                        chatList = ChatList.getInstance().getChats(chatEntity.getConversation().getConversationID());
                    }
                    Log.v(TAG, "modelAdded()6");
                    //if user is not on last page
                    //Log.v(TAG, "index===" + index + " chatList.size() " +chatList.size());
                    //if(chatList != null  && index + MAX_CHAT_LIMIT >= chatList.size()) {
                        addChat(model);
                    //}
                    
                    if(chatList != null) {
                        Log.v(TAG, "Chat list is not null, Chat list size is: " + chatList.size());
                        Log.v(TAG, "pagination modulus =" + chatList.size() % MAX_CHAT_LIMIT);
                       if (chatList.size() % MAX_CHAT_LIMIT >= 1){
                           Log.v(TAG, "Dynamic pagination modulus =" + chatList.size() % MAX_CHAT_LIMIT);
                           getNextChat();
                        } 
                    }
                    
                    if (model.isMe() && model.getType() != MessageType.SYSTEM) {
                        Log.v(TAG, "modelAdded()4");
                        MqttManager.sendMessage(chatEntity.getChatMsisdn(), model, chatEntity.isGroupChat());
                    } else {
                        Log.v(TAG, "modelAdded()5");
                        MqttManager.markAsRead(model);
                    }
                    
                }
            }
        }

               public void modelRemoved(final CollectionUpdateEvent event) {
            UIHelper.runOnLwuitUiThread(new Runnable() {
                public void run() {
                    Log.v(TAG, "Model removed called");
                    DataModel affectedModel = event.getAffectedModel();
                    if (affectedModel != null && affectedModel instanceof ChatModel && Display.getInstance().getCurrent() instanceof FormChatThread) {
                        Log.v(TAG, "Model removed affectedModel");
                        Container listItem = (Container) mModelToListItems.get(affectedModel);

                        Log.v(TAG, "listItem:" + listItem);
                        int chatIndex = -1;
                        if (listItem != null && listItem.getParent() == chatScreen) {
                            chatIndex = chatScreen.getComponentIndex(listItem);
                            chatScreen.removeComponent(listItem);
                        }


                        Log.v(TAG, "chatIndex:" + chatIndex + "chatScreen ComponentCount: " + chatScreen.getComponentCount());
                        // 0th will be timestamp
                        if (chatScreen.getComponentCount() > 1 && chatIndex <= chatScreen.getComponentCount()) {
                            //removed element was last
                            if (chatIndex == chatScreen.getComponentCount()) {
                                Component lastComponent = chatScreen.getComponentAt(chatIndex - 1);
                                if (lastComponent instanceof TimeStampContainer) {
                                    chatScreen.removeComponent(lastComponent);
                                }
                            } else { //removed element was middle element
                                Component lastComponent = chatScreen.getComponentAt(chatIndex - 1);
                                Component nextComponent = chatScreen.getComponentAt(chatIndex); //as the chat already removed
                                if (lastComponent instanceof TimeStampContainer && nextComponent instanceof TimeStampContainer) {
                                    chatScreen.removeComponent(lastComponent);
                                }
                            }
                        }
                        if (chatList.size() % MAX_CHAT_LIMIT == 0) {
                            getpreviousChat();
                        }
                        revalidate();
                        repaint();
                    }
                }
            });
        }

        public void modelUpdated(CollectionUpdateEvent event) {
            //TODO
        }

        public void modelContentsUpdated(DataModel model) {
            //TODO check whether the current form test is required
            if (model != null && model instanceof ChatModel && Display.getInstance().getCurrent() instanceof FormChatThread) {
                ChatModel chatModel = (ChatModel) model;
                Object icnLblObj = mModelToListItemSDRs.get(chatModel);
                if (icnLblObj != null && icnLblObj instanceof Label) {
                    Label label = (Label) icnLblObj;
                    Image sdr = UIHelper.getMsgStatusIcon(chatModel);
                    if (sdr != null) {
                        label.setIcon(sdr);
                    }
                }
                chatScreen.repaint();
            }            
        }
    }
    

    /**
     * This class gets callback of keyboard visibility change.
     */
    //#if nokia2_0
//#     private class KeyboardVisibilityListenerImpl implements KeyboardVisibilityListener {
//# 
//#         public void showNotify(int keyboardCategory) {
//#             if (Display.getInstance().getCurrent() instanceof FormChatThread) {
//#                 if (lastContainer != null) {
//#                     new Thread() {
//#                         public void run() {
//#                             try {
//#                                 Thread.sleep(1000);
//#                             } catch (InterruptedException ie) {
//#                             } finally {
//#                                 chatScreen.scrollRectToVisible(chatScreen.getWidth() - lastContainer.getWidth(), chatScreen.getLayoutHeight() - lastContainer.getHeight(), lastContainer.getWidth(), lastContainer.getHeight(), chatScreen);
//#                             }
//# 
//#                         }
//#                     }.start();
//#                 }
//#                 revalidate();
//#                 repaint();
//#             }
//#         }
//# 
//#         public void hideNotify(int keyboardCategory) {
//#             if (Display.getInstance().getCurrent() instanceof FormChatThread) {
//#                 sendBox.setVisible(false);
//#                 UIHelper.runOnLwuitUiThread(new Runnable() {
//#                     public void run() {
//#                         chatScreen.setShouldCalcPreferredSize(true);
//#                         chatScreen.revalidate();
//#                         chatScreen.repaint();
//#                         revalidate();
//#                         repaint();
//#                         new Thread() {
//#                             public void run() {
//#                                 try {
//#                                     Thread.sleep(1);
//#                                 } catch (Exception ex) {
//#                                 } finally {
//#                                     UIHelper.runOnLwuitUiThread(new Runnable() {
//#                                         public void run() {
//#                                             sendBox.setVisible(true);
//#                                             repaint();
//#                                         }
//#                                     });
//#                                 }
//#                             }
//#                         }.start();
//#                     }
//#                 });
//#             }
//#         }
//#     }
//# 
//#     
//#     /**
//#      * This class creates popup list for forward, copy, delete functionality.
//#      */
//#     private class ChatPopupListenerImpl implements PopupListListener {
//# 
//#         public void itemSelected(PopupList list, PopupListItem item) {
//#                      
//#             Log.v(TAG, "Context menu::itemSelected()");
//#             
//#             String title = item.getText();
//#             if (LBL_COPY.equals(title)) {
//#                 ClipboardManager.getInstance().copy(mLongPressedModel.getMessage());
//#             } else if (LBL_FORWARD.equals(title)) {
//#                 ClipboardManager.getInstance().copy(mLongPressedModel.getMessage());
//#                          
//#                 Log.v(TAG, "forwarding msg: " + ClipboardManager.getInstance().paste());
//#                 AppState.workFlow = WORKFLOW_FORWARD;
//#                 DisplayStackManager.showForm(DisplayStackManager.FORM_SELECT_CONTACT);
//#             } else if (LBL_DELETE.equals(title)) {
//#                          
//#                 Log.v(TAG, "Context menu::delete");
//#                 list.setVisible(false);
//#                          
//#                 Log.v(TAG, "deleting message " + mLongPressedModel.getMessage());
//#                 ConversationList.getInstance().getEntryByConversationID(mLongPressedModel.getConversationID()).setMsgId(SYSTEM_MESSAGE);
//#                 ChatList.getInstance().removeElement(mLongPressedModel);
//#             } else if (LBL_GET_FILE.equals(title)) {
//#                 try {
//#                     Hike.sMidlet.platformRequest(TextUtils.getLink(mLongPressedModel.getMessage()));
//#                 } catch (ConnectionNotFoundException ex) {
//#                     ex.printStackTrace();
//#                 }
//#             }
//#             list.setVisible(false);
//#         }
//# 
//#         public void listDismissed(PopupList list) {
//#         }
//#     }
    //#endif
}
