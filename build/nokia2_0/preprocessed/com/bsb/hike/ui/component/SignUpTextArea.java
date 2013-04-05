/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.ui.component;

import com.bsb.hike.util.AppConstants;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.plaf.Border;

/**
 *
 * @author Ankit Yadav
 */
public class SignUpTextArea extends TextArea implements AppConstants{

    
    /**
     * Constructor of signup TextArea. This initializes all the styles needed for TextArea on signup screens.
     * @param constraints 
     */
    public SignUpTextArea(int constraints) {
        getStyle().setBgTransparency(255, true);
        getSelectedStyle().setBgTransparency(255, true);
        getStyle().setFgColor(AppConstants.ColorCodes.signupScreenTextAreaTextColor, true);
        getSelectedStyle().setFgColor(AppConstants.ColorCodes.signupScreenTextAreaTextColor, true);
        getStyle().setBorder(Border.createCompoundBorder(Border.createEtchedRaised(AppConstants.ColorCodes.signupScreenTextAreaBorderShadow, AppConstants.ColorCodes.signupScreenTextAreaBorder), Border.createEtchedRaised(AppConstants.ColorCodes.signupScreenTextAreaBorder, AppConstants.ColorCodes.signupScreenTextAreaBorderShadow), Border.createEtchedRaised(AppConstants.ColorCodes.signupScreenTextAreaBorderShadow, AppConstants.ColorCodes.signupScreenTextAreaBorder), Border.createEtchedRaised(AppConstants.ColorCodes.signupScreenTextAreaBorder, AppConstants.ColorCodes.signupScreenTextAreaBorderShadow)), true);
        getSelectedStyle().setBorder(Border.createCompoundBorder(Border.createEtchedRaised(AppConstants.ColorCodes.signupScreenTextAreaBorderShadow, AppConstants.ColorCodes.signupScreenTextAreaBorder), Border.createEtchedRaised(AppConstants.ColorCodes.signupScreenTextAreaBorder, AppConstants.ColorCodes.signupScreenTextAreaBorderShadow), Border.createEtchedRaised(AppConstants.ColorCodes.signupScreenTextAreaBorderShadow, AppConstants.ColorCodes.signupScreenTextAreaBorder), Border.createEtchedRaised(AppConstants.ColorCodes.signupScreenTextAreaBorder, AppConstants.ColorCodes.signupScreenTextAreaBorderShadow)), true);
        setSingleLineTextArea(true);
        setColumns(14);
        setFocusable(true);
        requestFocus();
        
        // TODO restrict the text limit
        // setMaxSize(CharLimit.NAME);
        setText(EMPTY_STRING);
        setConstraint(constraints);
        getStyle().setAlignment(Component.CENTER, true);
        getSelectedStyle().setAlignment(Component.CENTER, true);
        getStyle().setPadding(Container.TOP, 12, true);
        getSelectedStyle().setPadding(Container.TOP, 12, true);
        getStyle().setPadding(Container.BOTTOM, 12, true);
        getSelectedStyle().setPadding(Container.BOTTOM, 12, true);
        getStyle().setMargin(Container.TOP, 18, true);
        getSelectedStyle().setMargin(Container.TOP, 18, true);
        getStyle().setMargin(Container.RIGHT, 18, true);
        getSelectedStyle().setMargin(Container.RIGHT, 18, true);
        getStyle().setMargin(Container.LEFT, 18, true);
        getSelectedStyle().setMargin(Container.LEFT, 18, true);
        getStyle().setBgColor(AppConstants.ColorCodes.signupScreenTextAreaBgColor, true);
        getSelectedStyle().setBgColor(AppConstants.ColorCodes.signupScreenTextAreaBgColor, true);
    }    
}
