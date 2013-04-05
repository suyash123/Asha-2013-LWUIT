/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.ui;

import com.bsb.hike.Hike;
import com.sun.lwuit.Button;
import com.sun.lwuit.Component;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.plaf.Border;

/**
 *
 * @author PuneethA
 */
public class FormOldVersion extends FormHikeBase {

    
    /**
     * Constructor of Old device version form. This form shows up when device doesn't support services like Push notification.
     */
    public FormOldVersion() {

        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        getStyle().setBgColor(ColorCodes.commonBgColor, true);
        getStyle().setBgTransparency(255, true);

        TextArea txtOldVersion = new TextArea(OLD_VERSION_MSG);
        txtOldVersion.setEditable(false);
        txtOldVersion.setRows(2);
        txtOldVersion.getStyle().setAlignment(Component.CENTER, true);
        txtOldVersion.getSelectedStyle().setAlignment(Component.CENTER, true);
        txtOldVersion.getStyle().setFont(Fonts.MEDIUM, true);
        txtOldVersion.getSelectedStyle().setFont(Fonts.MEDIUM, true);
        txtOldVersion.getStyle().setBgColor(ColorCodes.commonBgColor, true);
        txtOldVersion.getSelectedStyle().setBgColor(ColorCodes.commonBgColor, true);
        txtOldVersion.getStyle().setBgTransparency(255, true);
        txtOldVersion.getSelectedStyle().setBgTransparency(255, true);
        txtOldVersion.setSingleLineTextArea(true);
        txtOldVersion.getStyle().setFgColor(ColorCodes.signupScreenTopMsgColor, true);
        txtOldVersion.getSelectedStyle().setFgColor(ColorCodes.signupScreenTopMsgColor, true);
        txtOldVersion.getStyle().setBorder(Border.createEmpty(), true);
        txtOldVersion.getSelectedStyle().setBorder(Border.createEmpty(), true);
        txtOldVersion.getStyle().setMargin(Component.TOP, 65, true);
        txtOldVersion.getSelectedStyle().setMargin(Component.TOP, 65, true);

        Button closeBtn = new Button(LBL_EXIT);
        closeBtn.getStyle().setFont(Fonts.SMALL, true);
        closeBtn.getStyle().setFgColor(ColorCodes.white, true);
        closeBtn.getSelectedStyle().setFgColor(ColorCodes.white, true);
        closeBtn.getPressedStyle().setFgColor(ColorCodes.white, true);
        closeBtn.getSelectedStyle().setFont(Fonts.SMALL, true);
        closeBtn.getPressedStyle().setFont(Fonts.SMALL, true);
        closeBtn.getStyle().setBgColor(ColorCodes.freeSMSScreenInviteBtnBgBlue, true);
        closeBtn.getSelectedStyle().setBgColor(ColorCodes.freeSMSScreenInviteBtnBgBlue, true);
        closeBtn.getPressedStyle().setBgColor(ColorCodes.freeSMSScreenInviteBtnBgBlue, true);
        closeBtn.getStyle().setBorder(Border.createEmpty(), true);
        closeBtn.getSelectedStyle().setBorder(Border.createEmpty(), true);
        closeBtn.getPressedStyle().setBorder(Border.createEmpty(), true);
        closeBtn.getStyle().setBgTransparency(255, true);
        closeBtn.getSelectedStyle().setBgTransparency(255, true);
        closeBtn.getPressedStyle().setBgTransparency(255, true);
        closeBtn.getStyle().setAlignment(Component.CENTER, true);
        closeBtn.getSelectedStyle().setAlignment(Component.CENTER, true);
        closeBtn.getPressedStyle().setAlignment(Component.CENTER, true);
        closeBtn.getStyle().setMargin(Component.TOP, 10, true);
        closeBtn.getSelectedStyle().setMargin(Component.TOP, 10, true);
        closeBtn.getPressedStyle().setMargin(Component.TOP, 10, true);
        closeBtn.getStyle().setMargin(Component.RIGHT, 30, true);
        closeBtn.getSelectedStyle().setMargin(Component.RIGHT, 30, true);
        closeBtn.getPressedStyle().setMargin(Component.RIGHT, 30, true);
        closeBtn.getStyle().setMargin(Component.LEFT, 30, true);
        closeBtn.getSelectedStyle().setMargin(Component.LEFT, 30, true);
        closeBtn.getPressedStyle().setMargin(Component.LEFT, 30, true);
        closeBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                Hike.sMidlet.destroyApp(true);
            }
        });
        
        addComponent(txtOldVersion);
        addComponent(closeBtn);
    }
}
