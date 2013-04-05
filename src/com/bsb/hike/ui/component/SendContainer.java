/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.ui.component;

import com.bsb.hike.dto.AppState;
import com.bsb.hike.dto.ChatEntity;
import com.bsb.hike.dto.ChatModel;
import com.bsb.hike.dto.ConversationModel;
import com.bsb.hike.io.mqtt.MqttManager;
import com.bsb.hike.ui.DisplayStackManager;
import com.bsb.hike.ui.FormChatThread;
import com.bsb.hike.ui.FormSmileyPane;
import com.bsb.hike.util.AppConstants;
import com.bsb.hike.util.AppResource;
import com.bsb.hike.util.ClipboardManager;
import com.bsb.hike.util.Log;
import com.bsb.hike.util.ModelUtils;
import com.bsb.hike.util.TextUtils;
import com.bsb.hike.util.UIHelper;
import com.nokia.mid.ui.PopupList;
import com.nokia.mid.ui.PopupListItem;
import com.nokia.mid.ui.PopupListListener;
import com.sun.lwuit.Button;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Display;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.plaf.Border;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Ankit Yadav
 */
public class SendContainer extends Container implements AppConstants, PatchedTextArea.TextChangedListener {

    private Button smiley;
    private Container chatBoxNSmileyContainer;
    private PatchedTextArea mMsgTxtArea;
    private int displayWidth;
    private static final String TAG = "SendContainer";
    private Label smsCount;
    private Label mTypingIcn;
    private Container smsCountContainer;
    private int noOfCharactersTyped;
    public int noOfMessagesTyped;
    private boolean maxRowFactor = false;
    //#if nokia2_0
//#     private PopupList mCtxMenu;
    //#endif
    private Timer timer, pasteTextTimer;
    private TypingNotificationTimerTask typingNotificationTimerTask;
    private ChatPasteEventTimeTracker pasteEventTimeTracker;
    public Button prevMsgsBtn, nextMsgsBtn;

    
    /**
     * Constructor of send container. This class provides all the functionality to the chat box
     */
    public SendContainer() {

        setLayout(new BorderLayout());

        getStyle().setBorder(Border.createEmpty(), true);
        getSelectedStyle().setBorder(Border.createEmpty(), true);
        getPressedStyle().setBorder(Border.createEmpty(), true);
        getUnselectedStyle().setBorder(Border.createEmpty(), true);

        displayWidth = Display.getInstance().getDisplayWidth();

        mMsgTxtArea = new PatchedTextArea(EMPTY_STRING, CharLimit.CHAT);
        mMsgTxtArea.setRows(1);
        mMsgTxtArea.getStyle().setBgColor(ColorCodes.chatScreenSendContainerBg, true);
        mMsgTxtArea.getSelectedStyle().setBgColor(ColorCodes.chatScreenSendContainerBg, true);
        mMsgTxtArea.getDisabledStyle().setBgColor(ColorCodes.chatScreenSendContainerDisabled, true);
        mMsgTxtArea.getStyle().setFgColor(ColorCodes.chatScreenChatMessageTextColor, true);
        mMsgTxtArea.getSelectedStyle().setFgColor(ColorCodes.chatScreenChatMessageTextColor, true);
        mMsgTxtArea.getDisabledStyle().setFgColor(ColorCodes.chatScreenChatMessageTextColorDisabled, true);
        mMsgTxtArea.getStyle().setBorder(Border.createEmpty(), true);
        mMsgTxtArea.getSelectedStyle().setBorder(Border.createEmpty(), true);
        mMsgTxtArea.getDisabledStyle().setBorder(Border.createEmpty(), true);
        mMsgTxtArea.getStyle().setPadding(Component.RIGHT, 0, true);
        mMsgTxtArea.getSelectedStyle().setPadding(Component.RIGHT, 0, true);
        mMsgTxtArea.getDisabledStyle().setPadding(Component.RIGHT, 0, true);
        mMsgTxtArea.getStyle().setPadding(Component.TOP, 15, true);
        mMsgTxtArea.getSelectedStyle().setPadding(Component.TOP, 15, true);
        mMsgTxtArea.getDisabledStyle().setPadding(Component.TOP, 15, true);
        mMsgTxtArea.getStyle().setMargin(Component.BOTTOM, 0, true);
        mMsgTxtArea.getSelectedStyle().setMargin(Component.BOTTOM, 0, true);
        mMsgTxtArea.getDisabledStyle().setMargin(Component.BOTTOM, 0, true);
        mMsgTxtArea.getStyle().setFont(Fonts.MEDIUM, true);
        mMsgTxtArea.getSelectedStyle().setFont(Fonts.MEDIUM, true);
        mMsgTxtArea.getDisabledStyle().setFont(Fonts.MEDIUM, true);
        mMsgTxtArea.setColumns(10);
        mMsgTxtArea.setTextChangedListener(this);
        mMsgTxtArea.setLongPressListener(new PatchedTextArea.LongPressListener() {

            public void onPointerPressed(int x, int y) {
                if (pasteEventTimeTracker != null) {
                    pasteEventTimeTracker.cancel();
                    pasteEventTimeTracker = null;
                }
                pasteTextTimer = new Timer();
                pasteEventTimeTracker = new ChatPasteEventTimeTracker(y);
                pasteTextTimer.schedule(pasteEventTimeTracker, EVENT_TRIGGER_TIME_OUT);
            }

            public void onPointerReleased(int x, int y) {
                Log.v(TAG, "pointerReleased::");
                if (pasteTextTimer != null) {
                    System.out.println("onPointerReleased");
                    pasteTextTimer.cancel();
                    pasteTextTimer = null;
                }
            }

            public void onPointerDragged(int x, int y) {
                
            }
        });
        
        smsCountContainer = new Container(new BorderLayout());
        smsCountContainer.setPreferredH(20);
        smsCountContainer.getStyle().setBgTransparency(255, true);
        smsCountContainer.getStyle().setBgColor(ColorCodes.chatscreenSMSStripBg, true);

        smsCount = new Label();
        smsCount.setPreferredW(80);
        smsCount.getStyle().setPadding(Component.RIGHT, 2, true);
        smsCount.getStyle().setFont(Fonts.SMALL, true);
        smsCount.getStyle().setBgTransparency(0, true);
        smsCount.getStyle().setFgColor(ColorCodes.white, true);

        mTypingIcn = new Label(AppResource.getImageFromResource(PATH_TYPING));
        mTypingIcn.getStyle().setPadding(Component.LEFT, 10, true);
        mTypingIcn.getStyle().setFont(Fonts.SMALL, true);
        mTypingIcn.getStyle().setBgTransparency(0, true);

        smsCountContainer.addComponent(BorderLayout.WEST, mTypingIcn);
        smsCountContainer.addComponent(BorderLayout.EAST, smsCount);

        smiley = new Button(AppResource.getImageFromResource(PATH_CHAT_BOX_SMILEY));
        smiley.getStyle().setBorder(Border.createEmpty(), true);
        smiley.getSelectedStyle().setBorder(Border.createEmpty(), true);
        smiley.getPressedStyle().setBorder(Border.createEmpty(), true);
        smiley.getUnselectedStyle().setBorder(Border.createEmpty(), true);
        smiley.getDisabledStyle().setBorder(Border.createEmpty(), true);
        smiley.getStyle().setBgColor(AppConstants.ColorCodes.chatScreenSendContainerBg, true);
        smiley.getSelectedStyle().setBgColor(AppConstants.ColorCodes.chatScreenSendContainerBg, true);
        smiley.getPressedStyle().setBgColor(AppConstants.ColorCodes.chatScreenSendContainerBg, true);
        smiley.getUnselectedStyle().setBgColor(AppConstants.ColorCodes.chatScreenSendContainerBg, true);
        smiley.getDisabledStyle().setBgColor(ColorCodes.chatScreenSendContainerDisabled, true);

        //#if nokia2_0
//#         smiley.getStyle().setPadding(Component.LEFT, 5, true);
//#         smiley.getSelectedStyle().setPadding(Component.LEFT, 5, true);
//#         smiley.getPressedStyle().setPadding(Component.LEFT, 5, true);
//#         smiley.getUnselectedStyle().setPadding(Component.LEFT, 5, true);
//#         smiley.getDisabledStyle().setPadding(Component.LEFT, 5, true);
        //#elif nokia1_1
//#         smiley.getStyle().setPadding(Component.LEFT, 0, true);
//#         smiley.getSelectedStyle().setPadding(Component.LEFT, 0, true);
//#         smiley.getPressedStyle().setPadding(Component.LEFT, 0, true);
//#         smiley.getUnselectedStyle().setPadding(Component.LEFT, 0, true);
//#         smiley.getDisabledStyle().setPadding(Component.LEFT, 0, true);
        //#endif

        //#if nokia2_0
//#         smiley.getStyle().setMargin(Component.BOTTOM, 5, true);
//#         smiley.getSelectedStyle().setMargin(Component.BOTTOM, 5, true);
//#         smiley.getPressedStyle().setMargin(Component.BOTTOM, 5, true);
//#         smiley.getUnselectedStyle().setMargin(Component.BOTTOM, 5, true);
//#         smiley.getDisabledStyle().setMargin(Component.BOTTOM, 5, true);
        //#elif nokia1_1
//#         smiley.getStyle().setMargin(Component.BOTTOM, 0, true);
//#         smiley.getSelectedStyle().setMargin(Component.BOTTOM, 0, true);
//#         smiley.getPressedStyle().setMargin(Component.BOTTOM, 0, true);
//#         smiley.getUnselectedStyle().setMargin(Component.BOTTOM, 0, true);
//#         smiley.getDisabledStyle().setMargin(Component.BOTTOM, 0, true);
        //#endif

        smiley.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                UIHelper.runOnLwuitUiThread(new Runnable() {
                    public void run() {
                        ((FormSmileyPane) DisplayStackManager.getForm(DisplayStackManager.FORM_SMILEY_PANE, true)).setCurrentChatText(mMsgTxtArea.getText());
                        DisplayStackManager.showForm(DisplayStackManager.FORM_SMILEY_PANE);
                    }
                });
            }
        });

        //Adding SMS count Label in onShow() Method (Showing only when sending to SMS user)
        chatBoxNSmileyContainer = new Container(new BorderLayout());
        chatBoxNSmileyContainer.getStyle().setBorder(Border.createCompoundBorder(Border.createLineBorder(1, ColorCodes.chatScreenSendContainerTopShadow), null, null, null), true);
        chatBoxNSmileyContainer.getSelectedStyle().setBorder(Border.createCompoundBorder(Border.createLineBorder(1, ColorCodes.chatScreenSendContainerTopShadow), null, null, null), true);
        chatBoxNSmileyContainer.getDisabledStyle().setBorder(Border.createCompoundBorder(Border.createLineBorder(1, ColorCodes.chatScreenSendContainerTopShadow), null, null, null), true);
        chatBoxNSmileyContainer.getStyle().setBgColor(ColorCodes.chatScreenSendContainerBg, true);
        chatBoxNSmileyContainer.getSelectedStyle().setBgColor(ColorCodes.chatScreenSendContainerBg, true);
        chatBoxNSmileyContainer.getPressedStyle().setBgColor(ColorCodes.chatScreenSendContainerBg, true);
        chatBoxNSmileyContainer.getUnselectedStyle().setBgColor(ColorCodes.chatScreenSendContainerBg, true);
        chatBoxNSmileyContainer.getDisabledStyle().setBgColor(ColorCodes.chatScreenSendContainerDisabled, true);
        chatBoxNSmileyContainer.getStyle().setBgTransparency(255, true);
        chatBoxNSmileyContainer.getSelectedStyle().setBgTransparency(255, true);
        chatBoxNSmileyContainer.getPressedStyle().setBgTransparency(255, true);
        chatBoxNSmileyContainer.getUnselectedStyle().setBgTransparency(255, true);
        chatBoxNSmileyContainer.getDisabledStyle().setBgTransparency(255, true);
        chatBoxNSmileyContainer.addComponent(BorderLayout.CENTER, mMsgTxtArea);
        //#if nokia2_0
//#         chatBoxNSmileyContainer.addComponent(BorderLayout.WEST, smiley);
        //#else 
        chatBoxNSmileyContainer.addComponent(BorderLayout.EAST, smiley);
        //#endif
        
        Container paginationContainer = new Container(new BoxLayout(BoxLayout.X_AXIS));
        
        prevMsgsBtn = getPaginationActionBtns(LBL_PREVIOUS);
        prevMsgsBtn.getStyle().setMargin(1, 2, 3, 1);
        prevMsgsBtn.getSelectedStyle().setMargin(1, 2, 3, 1);
        prevMsgsBtn.getPressedStyle().setMargin(1, 2, 3, 1);
        prevMsgsBtn.getDisabledStyle().setMargin(1, 2, 3, 1);
        
        nextMsgsBtn = getPaginationActionBtns(LBL_NEXT);
        nextMsgsBtn.getStyle().setMargin(1, 2, 1, 2);
        nextMsgsBtn.getSelectedStyle().setMargin(1, 2, 1, 2);
        nextMsgsBtn.getPressedStyle().setMargin(1, 2, 1, 2);
        nextMsgsBtn.getDisabledStyle().setMargin(1, 2, 1, 2);
        nextMsgsBtn.setEnabled(false);
        
        paginationContainer.addComponent(prevMsgsBtn);
        paginationContainer.addComponent(nextMsgsBtn);
        
        prevMsgsBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                Log.v(TAG, "Previous msgs btn clicked");
                ((FormChatThread)DisplayStackManager.getForm(DisplayStackManager.FORM_CHAT, false)).getpreviousChat();
            }
        });
        
        nextMsgsBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                Log.v(TAG, "Next msgs btn clicked");
                ((FormChatThread)DisplayStackManager.getForm(DisplayStackManager.FORM_CHAT, false)).getNextChat();
            }
        });

        addComponent(BorderLayout.NORTH, smsCountContainer);
        addComponent(BorderLayout.CENTER, paginationContainer);
        addComponent(BorderLayout.SOUTH, chatBoxNSmileyContainer);

        initContextMenu();
    }

    
    /**
     * This method returns pagination action buttons with styles and margin.
     * @param btnLabel
     * @return 
     */
    private Button getPaginationActionBtns(String btnLabel) {
        
        Button paginationActionBtn = new Button(btnLabel);
        paginationActionBtn.getStyle().setFont(Fonts.SMALL, true);
        paginationActionBtn.getSelectedStyle().setFont(Fonts.SMALL, true);
        paginationActionBtn.getPressedStyle().setFont(Fonts.SMALL, true);
        paginationActionBtn.getDisabledStyle().setFont(Fonts.SMALL, true);
        paginationActionBtn.getStyle().setFgColor(ColorCodes.white, true);
        paginationActionBtn.getSelectedStyle().setFgColor(ColorCodes.white, true);
        paginationActionBtn.getPressedStyle().setFgColor(ColorCodes.white, true);
        paginationActionBtn.getDisabledStyle().setFgColor(ColorCodes.paginationBtnsFgColor, true);
        paginationActionBtn.getStyle().setBgColor(ColorCodes.paginationBtnsBgColor, true);
        paginationActionBtn.getSelectedStyle().setBgColor(ColorCodes.paginationBtnsBgColor, true);
        paginationActionBtn.getPressedStyle().setBgColor(ColorCodes.paginationBtnsBgColor, true);
        paginationActionBtn.getDisabledStyle().setBgColor(ColorCodes.paginationBtnsBgColor, true);
        paginationActionBtn.getStyle().setBorder(Border.createEmpty(), true);
        paginationActionBtn.getSelectedStyle().setBorder(Border.createEmpty(), true);
        paginationActionBtn.getPressedStyle().setBorder(Border.createEmpty(), true);
        paginationActionBtn.getDisabledStyle().setBorder(Border.createEmpty(), true);
        paginationActionBtn.getStyle().setBgTransparency(255, true);
        paginationActionBtn.getSelectedStyle().setBgTransparency(255, true);
        paginationActionBtn.getPressedStyle().setBgTransparency(255, true);
        paginationActionBtn.getDisabledStyle().setBgTransparency(255, true);
        paginationActionBtn.getStyle().setAlignment(Component.CENTER, true);
        paginationActionBtn.getSelectedStyle().setAlignment(Component.CENTER, true);
        paginationActionBtn.getPressedStyle().setAlignment(Component.CENTER, true);
        paginationActionBtn.getDisabledStyle().setAlignment(Component.CENTER, true);
        paginationActionBtn.getStyle().setPadding(0, 0, 0, 0);
        paginationActionBtn.getSelectedStyle().setPadding(0, 0, 0, 0);
        paginationActionBtn.getPressedStyle().setPadding(0, 0, 0, 0);
        paginationActionBtn.getDisabledStyle().setPadding(0, 0, 0, 0);
        paginationActionBtn.setPreferredW(Display.getInstance().getDisplayWidth()/2);
        paginationActionBtn.setPreferredH(30);
        
        return paginationActionBtn;
    }
    
    
    /**
     * This method removes SMS container from chat box if the chat is group chat.
     * @param isGroupChat 
     */
    public void removeSMSContainerInGroupChat(boolean isGroupChat) {
        if (isGroupChat) {

            Log.v(TAG, "SMS Count container removed in case of group chat");
            removeComponent(smsCountContainer);
        } else {
            if (smsCountContainer.getParent() == null) {

                Log.v(TAG, "SMS Count container added in case of one to one chat");
                addComponent(BorderLayout.NORTH, smsCountContainer);
            }
        }
    }

    
    /**
     * This method decides whether to show SMS strip on chat box or not based on the type of chat (Hike or SMS)
     * @param show 
     */
    public void showSmsCount(boolean show) {
        if (show) {
            noOfMessagesTyped = 0;
            smsCountContainer.getStyle().setBgTransparency(255, true);
            mTypingIcn.setVisible(false);
            smsCount.setVisible(true);
            smsCount.setText(((TextArea) mMsgTxtArea).getText().length() + SLASH_FORWARD + SMS_MAX_CAP + "  " + POUND + noOfMessagesTyped);

        } else {
            timer = new Timer();
            smsCountContainer.getStyle().setBgTransparency(0, true);
            mTypingIcn.setVisible(false);
            smsCount.setVisible(false);
        }
    }

    
    /**
     * This method enables or disables the chat box based on some conditions
     * @param enabled
     * @param msgText 
     */
    public void setSendEnabled(boolean enabled, String msgText) {
        setSendEnabled(enabled);
        mMsgTxtArea.setText(msgText);
    }

    
    /**
     * This method enables or disables the chat box based on some conditions
     * @param enabled 
     */
    public void setSendEnabled(boolean enabled) {
        mMsgTxtArea.setEnabled(enabled);
        mMsgTxtArea.setFocus(enabled);
        mMsgTxtArea.setFocusable(enabled);
        smiley.setEnabled(enabled);
        chatBoxNSmileyContainer.setEnabled(enabled);
        repaint();
    }

    
    /**
     * This method sets the smiley on chat box selected by user.
     * @param imgStr 
     */
    public void setSelectedSmiley(String imgStr) {
        mMsgTxtArea.setText(mMsgTxtArea.getText() + imgStr);
    }

    
    /**
     * This method initializes context menu for chat box on long press.
     */
    private void initContextMenu() {
        //#if nokia2_0
//#         mCtxMenu = new PopupList("context actions");
//#         mCtxMenu.appendItem(new PopupListItem(LBL_PASTE));
//#         mCtxMenu.setListener(new PopupListListener() {
//#             public void itemSelected(PopupList list, PopupListItem item) {
//# 
//#                 Log.v(TAG, "Context menu::itemSelected()");
//#                 
//#                 String title = item.getText();
//#                 if (LBL_PASTE.equals(title)) {
//#                     ClipboardManager.getInstance().paste(mMsgTxtArea);
//#                 }
//#                 list.setVisible(false);
//#             }
//# 
//#             public void listDismissed(PopupList list) {
//#                 list.setVisible(false);
//#             }
//#         });
        //#endif
    }

    
    /**
     * This method changes visibility of typing notification on chat box.
     * @param affectedModel 
     */
    public void setTypingStatus(ConversationModel affectedModel) {

        Log.v(TAG, "modelContentsUpdated()" + ":" + affectedModel);
        if (((ConversationModel) affectedModel).isTyping()) {
            Log.v(TAG, "Typing Notification started - True");
            mTypingIcn.setVisible(true);
            repaint();
            typingNotificationTimerTask = new TypingNotificationTimerTask();
            timer.schedule(typingNotificationTimerTask, 10000);
        } else {
            typingNotificationTimerTask.cancel();
            Log.v(TAG, "Typing Notification - False - Inside else condition before 10 seconds");
            mTypingIcn.setVisible(false);
            repaint();
        }
    }

    
    /**
     * This class extends TimerTask class for showing typing notification based on some conditions.
     */
    private class TypingNotificationTimerTask extends TimerTask {

        public void run() {

            mTypingIcn.setVisible(false);
            repaint();
            Log.v(TAG, "Typing Notification - False - Inside timer task after 10 seconds");
            typingNotificationTimerTask.cancel();
        }
    }

    
    /**
     * This method cancels timer for typing notification, when user presses back on chat thread. 
     */
    public void cancelTimerOnBack() {
        if (typingNotificationTimerTask != null) {
            typingNotificationTimerTask.cancel();

            Log.v(TAG, "Typing timer task cancelled");
        }

        if (timer != null) {
            timer.cancel();

            Log.v(TAG, "Typing timer cancelled");
        }
    }

    
    /**
     * This method sets text on chat box.
     * @param text 
     */
    public void setSendText(String text) {
        mMsgTxtArea.setText(text);
    }

    
    /**
     * This method is called when user presses send button to send message on other side.
     * @param chatEntity 
     */
    public void sendAction(ChatEntity chatEntity) {
        String chatTxt = mMsgTxtArea.getText();

        Log.v(TAG, "filter Check: " + "text is empty - " + TextUtils.isEmpty(chatTxt) + chatEntity);
        Log.v(TAG, AppState.getUserDetails().getLocalSMSCredits()+"");
        if ((!TextUtils.isEmpty(chatTxt) && !chatEntity.isBlocked() && chatEntity.isActive())) {

            Log.v(TAG, "actionPerformed::Send text not empty: " + chatTxt);
            String from = chatEntity.isGroupChat() ? AppState.getUserDetails().getMsisdn() : null;
            ModelUtils.addChat(chatTxt, new Date().getTime(), from, chatEntity.getChatMsisdn(), 0, ChatModel.MessageStatus.SENDING, chatEntity.sendAs());

            Log.v(TAG, "actionPerformed()5");
        }

        if (chatEntity.isActive()) {
            mMsgTxtArea.setText(EMPTY_STRING);
            mMsgTxtArea.setRows(1);
        }
        
        noOfCharactersTyped = 0;
        noOfMessagesTyped = 0;
    }

    
    /**
     * This method detects text change on chat box and calculates rows required to display chat message when user types.
     * @param textArea
     * @param previousText
     * @param currentText 
     */
    public void onTextChange(PatchedTextArea textArea, String previousText, String currentText) {
        if (!(Display.getInstance().getCurrent() instanceof FormChatThread)) {
            return;
        }
        //Used for sending typing notification
        MqttManager.sendTyping(!currentText.trim().equals(EMPTY_STRING), AppState.getUserDetails().getSelectedMsisdn());

        //Logic for calculating character count
        noOfCharactersTyped = currentText.length();
        int charCount = noOfCharactersTyped % SMS_MAX_CAP;
        if (charCount != 0) {
            noOfMessagesTyped = (noOfCharactersTyped / SMS_MAX_CAP) + 1;
        } else if (noOfCharactersTyped != 0) {
            noOfMessagesTyped = noOfCharactersTyped / SMS_MAX_CAP;
        }
        smsCount.setText(charCount + SLASH_FORWARD + SMS_MAX_CAP + "  " + POUND + noOfMessagesTyped);

        //Logic for growing textarea
        int CharWidth = ((TextArea) mMsgTxtArea).getStyle().getFont().stringWidth(currentText);

        Log.v(TAG, "CharWidth" + CharWidth);
        Log.v(TAG, "textWidth" + displayWidth);

        if ((maxRowFactor && mMsgTxtArea.getRows() == 1)) {
            chatBoxNSmileyContainer.revalidate();
            maxRowFactor = false;
        }

        if (CharWidth < (displayWidth - 80)) {
            mMsgTxtArea.setRows(MIN_ROW_FACTOR);
            return;
        }

        int rowSizeFactor = CharWidth / (displayWidth - 80);
        if (rowSizeFactor >= MAX_ROW_SIZE) {
            return;
        }
        mMsgTxtArea.setRows(rowSizeFactor + MAX_ROW_SIZE);
        chatBoxNSmileyContainer.revalidate();
        maxRowFactor = true;

        Log.v(TAG, "Row Factor is " + rowSizeFactor);
    }
    
    
    /**
     * This class extends TimerTask class for taking long press events on chat box.
     */
    private class ChatPasteEventTimeTracker extends TimerTask {

        private int mPosition;
        
        public ChatPasteEventTimeTracker(int y) {
            mPosition = y;
        }

        public void run() {
            //#if nokia2_0
//#             mCtxMenu.setListYPos(mPosition);
//#             mCtxMenu.setVisible(true);
            //#endif

            Log.v(TAG, " Event can be triggered");
            pasteEventTimeTracker.cancel();
        }
    }
}
