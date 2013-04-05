/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.ui;

import com.bsb.hike.dto.AppState;
import com.bsb.hike.dto.ChatEntity;
import com.bsb.hike.io.mqtt.MqttManager;
import com.bsb.hike.util.AppConstants;
import com.bsb.hike.util.AppResource;
import com.bsb.hike.util.Log;
import com.sun.lwuit.Button;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Display;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.geom.Dimension;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.plaf.Border;

/**
 *
 * @author Puneet Agarwal
 */
public class FormUserBlocked extends FormHikeBase {
    
    private TextArea blockedMsgFirst, blockedMsgName, blockedMsgBottomLast;
    private Button unblockBtn;
    private Label blockedIcnLabel;
    private Command backCommand;
    private Image blockedIcn;
    private static final String TAG = "FormUserBlocked";
    private ChatEntity chatEntity;
    private Container blockedMsgTopContainer;

    /**
     * Constructor of User blocked screen. This form comes up when user has blocked someone and tries to open chat screen with the same user whom the 
     * user has blocked.
     */
    public FormUserBlocked() {
        
        setTitle(LBL_USER_BLOCKED);
        
        initCommands();
        
        getStyle().setBgColor(ColorCodes.commonBgColor, true);
        getStyle().setBgTransparency(100, true);
        
        blockedIcn = AppResource.getImageFromResource(AppConstants.PATH_ICN_BLOCKED_USER);
        
        blockedIcnLabel = new Label();
        blockedIcnLabel.setFocusable(false);
        blockedIcnLabel.setIcon(blockedIcn);
        blockedIcnLabel.getStyle().setPadding(Component.TOP, 60, true);
        blockedIcnLabel.getStyle().setAlignment(Component.CENTER, true);
        blockedIcnLabel.getSelectedStyle().setAlignment(Component.CENTER, true);
        blockedIcnLabel.getStyle().setBgTransparency(0, true);
        blockedIcnLabel.getSelectedStyle().setBgTransparency(0, true);
        
        blockedMsgFirst = getBlockedMsgTextArea();
        blockedMsgFirst.getStyle().setPadding(Component.RIGHT, 0, true);
        blockedMsgFirst.getSelectedStyle().setPadding(Component.RIGHT, 0, true);
        blockedMsgFirst.getStyle().setMargin(Component.RIGHT, 0, true);
        blockedMsgFirst.getSelectedStyle().setMargin(Component.RIGHT, 0, true);
        blockedMsgFirst.getStyle().setFont(Fonts.SMALL, true);
        blockedMsgFirst.getSelectedStyle().setFont(Fonts.SMALL, true);
        
        blockedMsgName = getBlockedMsgTextArea();
        blockedMsgName.getStyle().setPadding(Component.LEFT, 0, true);
        blockedMsgName.getSelectedStyle().setPadding(Component.LEFT, 0, true);
        blockedMsgName.getStyle().setMargin(Component.LEFT, 0, true);
        blockedMsgName.getSelectedStyle().setMargin(Component.LEFT, 0, true);
        blockedMsgName.getStyle().setFont(Fonts.SMALL_BOLD, true);
        blockedMsgName.getSelectedStyle().setFont(Fonts.SMALL_BOLD, true);
        
        blockedMsgBottomLast = getBlockedMsgTextArea();
        blockedMsgBottomLast.getStyle().setAlignment(Component.CENTER, true);
        blockedMsgBottomLast.getSelectedStyle().setAlignment(Component.CENTER, true);
        blockedMsgBottomLast.getStyle().setFont(Fonts.SMALL, true);
        blockedMsgBottomLast.getSelectedStyle().setFont(Fonts.SMALL, true);
        
        blockedMsgTopContainer = new Container(new BoxLayout(BoxLayout.X_AXIS));
        blockedMsgTopContainer.getStyle().setPadding(Component.TOP, 15, true);
        blockedMsgTopContainer.getStyle().setBgTransparency(0, true);
        blockedMsgTopContainer.getStyle().setAlignment(Component.CENTER, true);
        blockedMsgTopContainer.addComponent(blockedMsgFirst);
        blockedMsgTopContainer.addComponent(blockedMsgName);
        
        Container blockedMsgContainer = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        blockedMsgContainer.getStyle().setBgTransparency(0, true);
        blockedMsgContainer.getStyle().setAlignment(Component.CENTER, true);
        blockedMsgContainer.addComponent(blockedMsgTopContainer);
        blockedMsgContainer.addComponent(blockedMsgBottomLast);
        
        unblockBtn = new Button(LBL_UNBLOCK_USER);
        unblockBtn.getStyle().setFont(Fonts.SMALL, true);
        unblockBtn.getStyle().setFgColor(ColorCodes.white, true);
        unblockBtn.getSelectedStyle().setFgColor(ColorCodes.white, true);
        unblockBtn.getPressedStyle().setFgColor(ColorCodes.white, true);
        unblockBtn.getSelectedStyle().setFont(Fonts.SMALL, true);
        unblockBtn.getPressedStyle().setFont(Fonts.SMALL, true);
        unblockBtn.getStyle().setBgColor(ColorCodes.freeSMSScreenInviteBtnBgBlue, true);
        unblockBtn.getSelectedStyle().setBgColor(ColorCodes.freeSMSScreenInviteBtnBgBlue, true);
        unblockBtn.getPressedStyle().setBgColor(ColorCodes.freeSMSScreenInviteBtnBgBlue, true);
        unblockBtn.getStyle().setBorder(Border.createEmpty(), true);
        unblockBtn.getSelectedStyle().setBorder(Border.createEmpty(), true);
        unblockBtn.getPressedStyle().setBorder(Border.createEmpty(), true);
        unblockBtn.getStyle().setBgTransparency(255, true);
        unblockBtn.getSelectedStyle().setBgTransparency(255, true);
        unblockBtn.getPressedStyle().setBgTransparency(255, true);
        unblockBtn.getStyle().setAlignment(Component.CENTER, true);
        unblockBtn.getSelectedStyle().setAlignment(Component.CENTER, true);
        unblockBtn.getPressedStyle().setAlignment(Component.CENTER, true);
        unblockBtn.getStyle().setMargin(Component.LEFT, 70, true);
        unblockBtn.getSelectedStyle().setMargin(Component.LEFT, 70, true);
        unblockBtn.getPressedStyle().setMargin(Component.LEFT, 70, true);
        unblockBtn.getStyle().setMargin(Component.RIGHT, 70, true);
        unblockBtn.getSelectedStyle().setMargin(Component.RIGHT, 70, true);
        unblockBtn.getPressedStyle().setMargin(Component.RIGHT, 70, true);
        unblockBtn.getStyle().setMargin(Component.TOP, 30, true);
        unblockBtn.getSelectedStyle().setMargin(Component.TOP, 30, true);
        unblockBtn.getPressedStyle().setMargin(Component.TOP, 30, true);
        unblockBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                         
                Log.v(TAG, "user unblock request sent");
                if (chatEntity.msisdnToBlock() != null) {
                    AppState.getUserDetails().getBlocklist().removeElement(chatEntity.msisdnToBlock());
                    MqttManager.blockUser(false, chatEntity.msisdnToBlock());
                }
                ((FormChatThread)DisplayStackManager.getForm(DisplayStackManager.FORM_CHAT, true)).setChatState(); 
                DisplayStackManager.showForm(DisplayStackManager.FORM_CHAT);
                         
                Log.v(TAG, "User unblocked");
            }
        });
        
        setScrollable(false);
        setScrollVisible(false);
        
        Container blockedContainer = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        blockedContainer.setPreferredSize(new Dimension(Display.getInstance().getDisplayWidth(), Display.getInstance().getDisplayHeight()));
        blockedContainer.getStyle().setBgColor(ColorCodes.commonBgColor, true);
        blockedContainer.getStyle().setBgTransparency(100, true);
        blockedContainer.addComponent(blockedIcnLabel);
        blockedContainer.addComponent(blockedMsgContainer);
        blockedContainer.addComponent(unblockBtn);
        
        addComponent(blockedContainer);
    }

    public void pointerDragged(int x, int y) {
        
    }

    public void pointerDragged(int[] x, int[] y) {
        
    }

    public void pointerHover(int[] x, int[] y) {
        
    }

    public void pointerHoverPressed(int[] x, int[] y) {
        
    }

    public void pointerHoverReleased(int[] x, int[] y) {
       
    }

    /**
     * This delegate method calls on pressing a point on device screen.
     * @param x
     * @param y 
     */
    public void pointerPressed(int x, int y) {
        super.pointerPressed(x, y);
        getStyle().setBgColor(ColorCodes.commonBgColor, true);
        getStyle().setBgTransparency(0, true);
    }

    
    /**
     * This delegate method calls on pressing a point on device screen.
     * @param x
     * @param y 
     */
    public void pointerPressed(int[] x, int[] y) {
        super.pointerPressed(x, y);
        getStyle().setBgColor(ColorCodes.commonBgColor, true);
        getStyle().setBgTransparency(0, true);
    }

    
    /**
     * This delegate method calls on releasing a point from device screen.
     * @param x
     * @param y 
     */
    public void pointerReleased(int x, int y) {
        super.pointerReleased(x, y);
        getStyle().setBgColor(ColorCodes.commonBgColor, true);
        getStyle().setBgTransparency(0, true);
    }

    
    /**
     * This delegate method calls on releasing a point from device screen.
     * @param x
     * @param y 
     */
    public void pointerReleased(int[] x, int[] y) {
        super.pointerReleased(x, y);
        getStyle().setBgColor(ColorCodes.commonBgColor, true);
        getStyle().setBgTransparency(0, true);
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
     * This method is called from outside the class to initialize chat entity variable in this class.
     * @param chatEntity 
     */
    public void setBlockedChat(ChatEntity chatEntity) {
        this.chatEntity = chatEntity;
    }

    
    /**
     * This delegated method is called just after showing the form on the screen. In this method all the alignment calculation is done according 
     * to the name length.
     */
    protected void onShow() {
        super.onShow();        
        blockedMsgFirst.setText(MSG_BLOCK_DIALOG_1);
        String blockedUserName = chatEntity.getOwnerName();
        blockedMsgName.setText(blockedUserName + FULL_STOP);
        blockedMsgBottomLast.setText(MSG_BLOCK_DIALOG_2);    
        int displayWidth = Display.getInstance().getDisplayWidth();
        int firstStringWidth = blockedMsgFirst.getStyle().getFont().stringWidth(MSG_BLOCK_DIALOG_1);
        int leftPadding = displayWidth/2 - firstStringWidth/2 - blockedMsgName.getStyle().getFont().stringWidth(blockedUserName)/2;
        if(leftPadding < 0) {
            leftPadding = 0;
            blockedMsgFirst.getStyle().setAlignment(Component.CENTER, true);
            blockedMsgFirst.getSelectedStyle().setAlignment(Component.CENTER, true);
            if(blockedMsgName.getStyle().getFont().stringWidth(blockedMsgName.getText()) > displayWidth) {
                blockedMsgName.setRows(2);
                blockedMsgName.setSingleLineTextArea(false);
            }else {
                blockedMsgName.setRows(1);
                blockedMsgName.setSingleLineTextArea(true);
            }
            blockedMsgName.getStyle().setAlignment(Component.CENTER, true);
            blockedMsgName.getSelectedStyle().setAlignment(Component.CENTER, true);
            blockedMsgTopContainer.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        }else {
            blockedMsgFirst.getStyle().setAlignment(Component.LEFT, true);
            blockedMsgFirst.getSelectedStyle().setAlignment(Component.LEFT, true);
            blockedMsgName.setRows(1);
            blockedMsgName.setSingleLineTextArea(true);
            blockedMsgName.getStyle().setAlignment(Component.LEFT, true);
            blockedMsgName.getSelectedStyle().setAlignment(Component.LEFT, true);
            blockedMsgTopContainer.setLayout(new BoxLayout(BoxLayout.X_AXIS));
        }
        blockedMsgTopContainer.getStyle().setPadding(Component.LEFT, leftPadding, true);
    }

    
    /**
     * This method returns TextArea with styles and padding for creating text on the screen for a blocked user.
     * @return 
     */
    private TextArea getBlockedMsgTextArea() {
        
        TextArea message = new TextArea();
        message.setEditable(false);
        message.setFocusable(false);
        message.setFocus(true);
        message.setFlatten(true);
        message.setTextEditorEnabled(false);
        message.setSingleLineTextArea(true);
        message.getStyle().setPadding(Component.TOP, 0, true);
        message.getSelectedStyle().setPadding(Component.TOP, 0, true);
        message.getStyle().setPadding(Component.LEFT, 0, true);
        message.getSelectedStyle().setPadding(Component.LEFT, 0, true);
        message.getStyle().setPadding(Component.RIGHT, 0, true);
        message.getSelectedStyle().setPadding(Component.RIGHT, 0, true);
        message.getStyle().setPadding(Component.BOTTOM, 0, true);
        message.getSelectedStyle().setPadding(Component.BOTTOM, 0, true);
        message.getStyle().setBorder(Border.createEmpty(), true);
        message.getSelectedStyle().setBorder(Border.createEmpty(), true);
        message.getStyle().setBgColor(ColorCodes.commonBgColor, true);
        message.getSelectedStyle().setBgColor(ColorCodes.commonBgColor, true);
        message.getPressedStyle().setBgColor(ColorCodes.commonBgColor, true);
        message.getDisabledStyle().setBgColor(ColorCodes.commonBgColor, true);
        message.getUnselectedStyle().setBgColor(ColorCodes.commonBgColor, true);
        message.getStyle().setFgColor(ColorCodes.white, true);
        message.getSelectedStyle().setFgColor(ColorCodes.white, true);
        message.getStyle().setBgTransparency(0, true);
        message.getSelectedStyle().setBgTransparency(0, true);
        message.getPressedStyle().setBgTransparency(0, true);
        message.getDisabledStyle().setBgTransparency(0, true);
        message.getUnselectedStyle().setBgTransparency(0, true);
        
        return message;
    }
}
