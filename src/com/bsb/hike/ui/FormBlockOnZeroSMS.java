/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.ui;

import com.bsb.hike.util.AppConstants;
import com.bsb.hike.util.AppResource;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.plaf.Border;

/**
 *
 * @author Puneet Agarwal
 */
public class FormBlockOnZeroSMS extends FormHikeBase {
    
    private Command backCommand;
    private Image blockedIcn;
    private static final String TAG = "FormBlockOnZeroSMS";
    
    
    /**
     * Constructor of Block on zero SMS form. This form comes up when user doesn't have SMS credits left in his account and user try to send SMS
     * to a Non-Hike user.
     */
    public FormBlockOnZeroSMS() {

        setTitle(LBL_NO_CREDITS);
        
        initCommands();
        
        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        
        getStyle().setAlignment(Component.CENTER, true);
        getStyle().setBgColor(ColorCodes.commonBgColor, true);
        getStyle().setBgTransparency(100, true);
        
        blockedIcn = AppResource.getImageFromResource(AppConstants.PATH_ICN_BLOCKED_USER);
        
        Label blockedIcnLabel = new Label();
        blockedIcnLabel.setFocusable(false);
        blockedIcnLabel.setIcon(blockedIcn);
        blockedIcnLabel.getStyle().setPadding(Component.TOP, 100, true);
        blockedIcnLabel.getStyle().setAlignment(Component.CENTER, true);
        blockedIcnLabel.getSelectedStyle().setAlignment(Component.CENTER, true);
        blockedIcnLabel.getStyle().setBgTransparency(0, true);
        blockedIcnLabel.getSelectedStyle().setBgTransparency(0, true);
        
        TextArea msgZeroSMSLeft = getBlockedMsgTextArea();
        msgZeroSMSLeft.setText(MSG_NO_SMS_CREDITS_LEFT);
        msgZeroSMSLeft.getStyle().setPadding(Component.RIGHT, 0, true);
        msgZeroSMSLeft.getSelectedStyle().setPadding(Component.RIGHT, 0, true);
        msgZeroSMSLeft.getStyle().setMargin(Component.TOP, 10, true);
        msgZeroSMSLeft.getSelectedStyle().setMargin(Component.TOP, 10, true);
        msgZeroSMSLeft.getStyle().setPadding(Component.BOTTOM, 0, true);
        msgZeroSMSLeft.getSelectedStyle().setPadding(Component.BOTTOM, 0, true);
        msgZeroSMSLeft.getStyle().setMargin(Component.BOTTOM, 0, true);
        msgZeroSMSLeft.getSelectedStyle().setMargin(Component.BOTTOM, 0, true);
        msgZeroSMSLeft.getStyle().setFont(Fonts.SMALL, true);
        msgZeroSMSLeft.getSelectedStyle().setFont(Fonts.SMALL, true);
        
        TextArea msgInviteToContinue = getBlockedMsgTextArea();
        msgInviteToContinue.setText(MSG_INVITE_TO_CONTINUE);
        msgInviteToContinue.getStyle().setPadding(Component.TOP, 0, true);
        msgInviteToContinue.getSelectedStyle().setPadding(Component.TOP, 0, true);
        msgInviteToContinue.getStyle().setMargin(Component.TOP, 0, true);
        msgInviteToContinue.getSelectedStyle().setMargin(Component.TOP, 0, true);
        msgInviteToContinue.getStyle().setPadding(Component.RIGHT, 0, true);
        msgInviteToContinue.getSelectedStyle().setPadding(Component.RIGHT, 0, true);
        msgInviteToContinue.getStyle().setFont(Fonts.SMALL, true);
        msgInviteToContinue.getSelectedStyle().setFont(Fonts.SMALL, true);
        
        addComponent(blockedIcnLabel);
        addComponent(msgZeroSMSLeft);
        addComponent(msgInviteToContinue);
        
        setScrollable(false);
        setScrollVisible(false);
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
     * This method returns TextArea with all the styles needed except margin and padding on the component. 
     * @return 
     */
    private TextArea getBlockedMsgTextArea() {
        
        TextArea message = new TextArea();
        message.setEditable(false);
        message.setFocusable(false);
        message.setFocus(true);
        message.setSingleLineTextArea(false);
        message.setTextEditorEnabled(false);
        message.getStyle().setAlignment(Component.CENTER, true);
        message.getSelectedStyle().setAlignment(Component.CENTER, true);
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
