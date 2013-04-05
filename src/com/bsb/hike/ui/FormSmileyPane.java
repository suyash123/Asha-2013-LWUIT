/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.ui;

import com.bsb.hike.ui.helper.SmileyManager;
import com.bsb.hike.util.AppConstants;
import com.bsb.hike.util.AppResource;
import com.bsb.hike.util.Log;
import com.sun.lwuit.Button;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Display;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.layouts.GridLayout;
import com.sun.lwuit.plaf.Border;
import com.sun.lwuit.table.TableLayout;

/**
 *
 * @author Puneet Agarwal
 */
public class FormSmileyPane extends FormHikeBase {

    private static final String TAG = "FormSmileyPane";
    private Button yellowSmileyBtn, yolkSmileyBtn, popSmileyBtn;
    private Image yellowSmiley, yolkSmiley, popSmiley;
    private int CURRENT_SMILEY = 0;
    private Container yellowSmileyContainer, yolkSmileyContainer, popSmileyContainer;
    private Container dummySendContainer, mainContainer;
    private Label mMsgTxtArea;
    private String currentChatText;
    
    
    /**
     * Constructor of Smiley Form. This form shows all the smilies in three tabs and shows the chat text to the user which user has entered.
     */
    public FormSmileyPane() {
        
        setTitle(LBL_ADD_EMOTIONS);
        
        getStyle().setBgColor(ColorCodes.smileyPaneBgColor, true);
        getStyle().setBgTransparency(0, true);
        
        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        
        yellowSmiley = AppResource.getImageFromResource(PATH_SMILEY_YELLOW);
        yolkSmiley = AppResource.getImageFromResource(PATH_SMILEY_YOLK);
        popSmiley = AppResource.getImageFromResource(PATH_SMILEY_POP);
        
        CURRENT_SMILEY = YOLK_SMILEY;
        
        yellowSmileyBtn = new Button(yellowSmiley);
        yolkSmileyBtn = new Button(yolkSmiley);
        popSmileyBtn = new Button(popSmiley);
        
        initListeners();
        
        yolkSmileyContainer = createSmileyForTabPane(YOLKS_SMILEY_BEGIN, YOLKS_SMILEY_END);
        
        Container smileyTabs = new Container(new GridLayout(1, 3));
        smileyTabs.addComponent(createSmileyButton(yolkSmileyBtn));
        smileyTabs.addComponent(createSmileyButton(popSmileyBtn));
        smileyTabs.addComponent(createSmileyButton(yellowSmileyBtn));
        
        resetSmileyBg();
        
        dummySendContainer = dummySendContainerForBackNavigation();
        
        mainContainer = new Container(new BorderLayout());
        mainContainer.setPreferredH(Display.getInstance().getDisplayHeight());
        mainContainer.getStyle().setBgColor(ColorCodes.smileyPaneBgColor, true);
        mainContainer.getStyle().setBgTransparency(255, true);
        
        mainContainer.addComponent(BorderLayout.NORTH, yolkSmileyContainer);
        mainContainer.addComponent(BorderLayout.CENTER, smileyTabs);
        mainContainer.addComponent(BorderLayout.SOUTH, dummySendContainer);
        
        addComponent(mainContainer);
    }
    
    
    /**
     * This method creates a grid view of smilies for each tab in smiley form.
     * @param startIndex
     * @param lastIndex
     * @return 
     */
    private Container createSmileyForTabPane(int startIndex, int lastIndex) {
        
        Log.v(TAG, lastIndex / 5 + lastIndex % 5 + EMPTY_STRING);
        Container smiliesPane = new Container(new TableLayout(lastIndex / 5 + lastIndex % 5, 5));
        smiliesPane.setScrollVisible(true);
        smiliesPane.setPreferredH(Display.getInstance().getDisplayHeight() - 100);
        smiliesPane.getStyle().setBgColor(ColorCodes.smileyPaneBgColor);
        smiliesPane.setScrollableY(true);
        final SmileyManager mgr = SmileyManager.getInstance();
        for (int i = startIndex; i < lastIndex; i++) {
            final String imgPath = mgr.smileys[i][1];
            final String imgString = mgr.smileys[i][0];
                     
            Log.v(TAG, "Smiley pair: " + imgPath + "  :  " + imgString);
            Button smileyBtn = new Button(AppResource.getImageFromResource(imgPath));
            //TODO test whether single border can be attached to different UI component
            Border border = Border.createEmpty();
            smileyBtn.getSelectedStyle().setBorder(border);
            smileyBtn.getUnselectedStyle().setBorder(border);
            smileyBtn.getPressedStyle().setBorder(border);
            
            smileyBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                             
                    Log.v(TAG, "Smiley selected: " + imgPath + "  :  " + imgString);
                    ((FormChatThread)DisplayStackManager.getForm(DisplayStackManager.FORM_CHAT, true)).setSelectedSmiley(imgString);
                    DisplayStackManager.showForm(DisplayStackManager.FORM_CHAT);
                }
            });
            smiliesPane.addComponent(smileyBtn);
        }
        return smiliesPane;
    }
    
    
    /**
     * This method returns the clickable button for each tab.
     * @param smiley
     * @return 
     */
    private Button createSmileyButton(Button smiley) {
        
        smiley.getStyle().setPadding(0, 0, 0, 0);
        smiley.getSelectedStyle().setPadding(0, 0, 0, 0);
        smiley.getPressedStyle().setPadding(0, 0, 0, 0);
        smiley.getUnselectedStyle().setPadding(0, 0, 0, 0);
        smiley.getStyle().setMargin(0, 0, 0, 0);
        smiley.getSelectedStyle().setMargin(0, 0, 0, 0);
        smiley.getPressedStyle().setMargin(0, 0, 0, 0);
        smiley.getUnselectedStyle().setMargin(0, 0, 0, 0);
        smiley.getStyle().setBgTransparency(255, true);
        smiley.getSelectedStyle().setBgTransparency(255, true);
        smiley.getPressedStyle().setBgTransparency(255, true);
        smiley.getUnselectedStyle().setBgTransparency(255, true);
        smiley.setPreferredH(40);
        
        return smiley;
    }
    
    /**
     * This method initializes action listeners for all three tabs. Each action listener is used to change the smiley tab and doing operations 
     * on components like addComponent() or removeComponent().
     */
    private void initListeners() {
        
        yolkSmileyBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                if (CURRENT_SMILEY != YOLK_SMILEY) {
                    CURRENT_SMILEY = YOLK_SMILEY;
                    resetSmileyBg();
                    if(yellowSmileyContainer != null) {
                        mainContainer.removeComponent(yellowSmileyContainer);
                        yellowSmileyContainer = null;
                    }
                    if(popSmileyContainer != null) {
                        mainContainer.removeComponent(popSmileyContainer);
                        popSmileyContainer = null;
                    }
                    yolkSmileyContainer = createSmileyForTabPane(YOLKS_SMILEY_BEGIN, YOLKS_SMILEY_END);
                    mainContainer.addComponent(BorderLayout.NORTH, yolkSmileyContainer);
                    revalidate();
                }
            }
        });
        
        
        yellowSmileyBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                if (CURRENT_SMILEY != YELLOW_SMILEY) {
                    CURRENT_SMILEY = YELLOW_SMILEY;
                    resetSmileyBg();
                    if(yolkSmileyContainer != null) {
                        mainContainer.removeComponent(yolkSmileyContainer);
                        yolkSmileyContainer = null;
                    }
                    if(popSmileyContainer != null) {
                        mainContainer.removeComponent(popSmileyContainer);
                        popSmileyContainer = null;
                    }
                    yellowSmileyContainer = createSmileyForTabPane(YELLOW_SMILEY_BEGIN, YELLOW_SMILEY_END);
                    mainContainer.addComponent(BorderLayout.NORTH, yellowSmileyContainer);
                    revalidate();
                }
            }
        });
        
        popSmileyBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                if (CURRENT_SMILEY != POP_SMILEY) {
                    CURRENT_SMILEY = POP_SMILEY;
                    resetSmileyBg();
                    if(yellowSmileyContainer != null) {
                        mainContainer.removeComponent(yellowSmileyContainer);
                        yellowSmileyContainer = null;
                    }
                    if(yolkSmileyContainer != null) {
                        mainContainer.removeComponent(yolkSmileyContainer);
                        yolkSmileyContainer = null;
                    }
                    popSmileyContainer = createSmileyForTabPane(POPO_SMILEY_BEGIN, POPO_SMILEY_END);
                    mainContainer.addComponent(BorderLayout.NORTH, popSmileyContainer);
                    revalidate();
                }
            }
        });
    }
    
    /**
     * This method is just to create border around each tab when user changes the tab.
     */
    private void resetSmileyBg() {
        switch (CURRENT_SMILEY) {
            
           case YOLK_SMILEY:
                yolkSmileyBtn.getStyle().setBgColor(ColorCodes.smileyPaneBgColor, true);
                yolkSmileyBtn.getSelectedStyle().setBgColor(ColorCodes.smileyPaneBgColor, true);
                yolkSmileyBtn.getPressedStyle().setBgColor(ColorCodes.smileyPaneBgColor, true);
                yolkSmileyBtn.getUnselectedStyle().setBgColor(ColorCodes.smileyPaneBgColor, true);
                yolkSmileyBtn.getStyle().setBorder(Border.createCompoundBorder(Border.createEmpty(), Border.createEmpty(), Border.createEmpty(), Border.createLineBorder(1, ColorCodes.smileyBorderColor)), true);
                yolkSmileyBtn.getSelectedStyle().setBorder(Border.createCompoundBorder(Border.createEmpty(), Border.createEmpty(), Border.createEmpty(), Border.createLineBorder(1, ColorCodes.smileyBorderColor)), true);
                yolkSmileyBtn.getPressedStyle().setBorder(Border.createCompoundBorder(Border.createEmpty(), Border.createEmpty(), Border.createEmpty(), Border.createLineBorder(1, ColorCodes.smileyBorderColor)), true);
                yolkSmileyBtn.getUnselectedStyle().setBorder(Border.createCompoundBorder(Border.createEmpty(), Border.createEmpty(), Border.createEmpty(), Border.createLineBorder(1, ColorCodes.smileyBorderColor)), true);
                popSmileyBtn.getStyle().setBgColor(ColorCodes.smileyPaneCloseButtonBgColor, true);
                popSmileyBtn.getSelectedStyle().setBgColor(ColorCodes.smileyPaneCloseButtonBgColor, true);
                popSmileyBtn.getPressedStyle().setBgColor(ColorCodes.smileyPaneCloseButtonBgColor, true);
                popSmileyBtn.getUnselectedStyle().setBgColor(ColorCodes.smileyPaneCloseButtonBgColor, true);
                popSmileyBtn.getStyle().setBorder(Border.createCompoundBorder(Border.createLineBorder(1, ColorCodes.smileyBorderColor), Border.createEmpty(), Border.createEmpty(), Border.createEmpty()), true);
                popSmileyBtn.getSelectedStyle().setBorder(Border.createCompoundBorder(Border.createLineBorder(1, ColorCodes.smileyBorderColor), Border.createEmpty(), Border.createEmpty(), Border.createEmpty()), true);
                popSmileyBtn.getPressedStyle().setBorder(Border.createCompoundBorder(Border.createLineBorder(1, ColorCodes.smileyBorderColor), Border.createEmpty(), Border.createEmpty(), Border.createEmpty()), true);
                popSmileyBtn.getUnselectedStyle().setBorder(Border.createCompoundBorder(Border.createLineBorder(1, ColorCodes.smileyBorderColor), Border.createEmpty(), Border.createEmpty(), Border.createEmpty()), true);
                yellowSmileyBtn.getStyle().setBgColor(ColorCodes.smileyPaneCloseButtonBgColor, true);
                yellowSmileyBtn.getSelectedStyle().setBgColor(ColorCodes.smileyPaneCloseButtonBgColor, true);
                yellowSmileyBtn.getPressedStyle().setBgColor(ColorCodes.smileyPaneCloseButtonBgColor, true);
                yellowSmileyBtn.getUnselectedStyle().setBgColor(ColorCodes.smileyPaneCloseButtonBgColor, true);
                yellowSmileyBtn.getStyle().setBorder(Border.createCompoundBorder(Border.createLineBorder(1, ColorCodes.smileyBorderColor), Border.createEmpty(), Border.createEmpty(), Border.createEmpty()), true);
                yellowSmileyBtn.getSelectedStyle().setBorder(Border.createCompoundBorder(Border.createLineBorder(1, ColorCodes.smileyBorderColor), Border.createEmpty(), Border.createEmpty(), Border.createEmpty()), true);
                yellowSmileyBtn.getPressedStyle().setBorder(Border.createCompoundBorder(Border.createLineBorder(1, ColorCodes.smileyBorderColor), Border.createEmpty(), Border.createEmpty(), Border.createEmpty()), true);
                yellowSmileyBtn.getUnselectedStyle().setBorder(Border.createCompoundBorder(Border.createLineBorder(1, ColorCodes.smileyBorderColor), Border.createEmpty(), Border.createEmpty(), Border.createEmpty()), true);
                break;

           case YELLOW_SMILEY:
                popSmileyBtn.getStyle().setBgColor(ColorCodes.smileyPaneCloseButtonBgColor, true);
                popSmileyBtn.getSelectedStyle().setBgColor(ColorCodes.smileyPaneCloseButtonBgColor, true);
                popSmileyBtn.getPressedStyle().setBgColor(ColorCodes.smileyPaneCloseButtonBgColor, true);
                popSmileyBtn.getUnselectedStyle().setBgColor(ColorCodes.smileyPaneCloseButtonBgColor, true);
                popSmileyBtn.getStyle().setBorder(Border.createCompoundBorder(Border.createLineBorder(1, ColorCodes.smileyBorderColor), Border.createEmpty(), Border.createEmpty(), Border.createEmpty()), true);
                popSmileyBtn.getSelectedStyle().setBorder(Border.createCompoundBorder(Border.createLineBorder(1, ColorCodes.smileyBorderColor), Border.createEmpty(), Border.createEmpty(), Border.createEmpty()), true);
                popSmileyBtn.getPressedStyle().setBorder(Border.createCompoundBorder(Border.createLineBorder(1, ColorCodes.smileyBorderColor), Border.createEmpty(), Border.createEmpty(), Border.createEmpty()), true);
                popSmileyBtn.getUnselectedStyle().setBorder(Border.createCompoundBorder(Border.createLineBorder(1, ColorCodes.smileyBorderColor), Border.createEmpty(), Border.createEmpty(), Border.createEmpty()), true);
                yolkSmileyBtn.getStyle().setBgColor(ColorCodes.smileyPaneCloseButtonBgColor, true);
                yolkSmileyBtn.getSelectedStyle().setBgColor(ColorCodes.smileyPaneCloseButtonBgColor, true);
                yolkSmileyBtn.getPressedStyle().setBgColor(ColorCodes.smileyPaneCloseButtonBgColor, true);
                yolkSmileyBtn.getUnselectedStyle().setBgColor(ColorCodes.smileyPaneCloseButtonBgColor, true);
                yolkSmileyBtn.getStyle().setBorder(Border.createCompoundBorder(Border.createLineBorder(1, ColorCodes.smileyBorderColor), Border.createEmpty(), Border.createEmpty(), Border.createEmpty()), true);
                yolkSmileyBtn.getSelectedStyle().setBorder(Border.createCompoundBorder(Border.createLineBorder(1, ColorCodes.smileyBorderColor), Border.createEmpty(), Border.createEmpty(), Border.createEmpty()), true);
                yolkSmileyBtn.getPressedStyle().setBorder(Border.createCompoundBorder(Border.createLineBorder(1, ColorCodes.smileyBorderColor), Border.createEmpty(), Border.createEmpty(), Border.createEmpty()), true);
                yolkSmileyBtn.getUnselectedStyle().setBorder(Border.createCompoundBorder(Border.createLineBorder(1, ColorCodes.smileyBorderColor), Border.createEmpty(), Border.createEmpty(), Border.createEmpty()), true);
                yellowSmileyBtn.getStyle().setBgColor(ColorCodes.smileyPaneBgColor, true);
                yellowSmileyBtn.getSelectedStyle().setBgColor(ColorCodes.smileyPaneBgColor, true);
                yellowSmileyBtn.getPressedStyle().setBgColor(ColorCodes.smileyPaneBgColor, true);
                yellowSmileyBtn.getUnselectedStyle().setBgColor(ColorCodes.smileyPaneBgColor, true);
                yellowSmileyBtn.getStyle().setBorder(Border.createCompoundBorder(Border.createEmpty(), Border.createEmpty(), Border.createLineBorder(1, ColorCodes.smileyBorderColor), Border.createEmpty()), true);
                yellowSmileyBtn.getSelectedStyle().setBorder(Border.createCompoundBorder(Border.createEmpty(), Border.createEmpty(), Border.createLineBorder(1, ColorCodes.smileyBorderColor), Border.createEmpty()), true);
                yellowSmileyBtn.getPressedStyle().setBorder(Border.createCompoundBorder(Border.createEmpty(), Border.createEmpty(), Border.createLineBorder(1, ColorCodes.smileyBorderColor), Border.createEmpty()), true);
                yellowSmileyBtn.getUnselectedStyle().setBorder(Border.createCompoundBorder(Border.createEmpty(), Border.createEmpty(), Border.createLineBorder(1, ColorCodes.smileyBorderColor), Border.createEmpty()), true);
                break;
                
           case POP_SMILEY:
                yolkSmileyBtn.getStyle().setBgColor(ColorCodes.smileyPaneCloseButtonBgColor, true);
                yolkSmileyBtn.getSelectedStyle().setBgColor(ColorCodes.smileyPaneCloseButtonBgColor, true);
                yolkSmileyBtn.getPressedStyle().setBgColor(ColorCodes.smileyPaneCloseButtonBgColor, true);
                yolkSmileyBtn.getUnselectedStyle().setBgColor(ColorCodes.smileyPaneCloseButtonBgColor, true);
                yolkSmileyBtn.getStyle().setBorder(Border.createCompoundBorder(Border.createLineBorder(1, ColorCodes.smileyBorderColor), Border.createEmpty(), Border.createEmpty(), Border.createEmpty()), true);
                yolkSmileyBtn.getSelectedStyle().setBorder(Border.createCompoundBorder(Border.createLineBorder(1, ColorCodes.smileyBorderColor), Border.createEmpty(), Border.createEmpty(), Border.createEmpty()), true);
                yolkSmileyBtn.getPressedStyle().setBorder(Border.createCompoundBorder(Border.createLineBorder(1, ColorCodes.smileyBorderColor), Border.createEmpty(), Border.createEmpty(), Border.createEmpty()), true);
                yolkSmileyBtn.getUnselectedStyle().setBorder(Border.createCompoundBorder(Border.createLineBorder(1, ColorCodes.smileyBorderColor), Border.createEmpty(), Border.createEmpty(), Border.createEmpty()), true);
                popSmileyBtn.getStyle().setBgColor(ColorCodes.smileyPaneBgColor, true);
                popSmileyBtn.getSelectedStyle().setBgColor(ColorCodes.smileyPaneBgColor, true);
                popSmileyBtn.getPressedStyle().setBgColor(ColorCodes.smileyPaneBgColor, true);
                popSmileyBtn.getUnselectedStyle().setBgColor(ColorCodes.smileyPaneBgColor, true);
                popSmileyBtn.getStyle().setBorder(Border.createCompoundBorder(Border.createEmpty(), Border.createEmpty(), Border.createLineBorder(1, ColorCodes.smileyBorderColor), Border.createLineBorder(1, ColorCodes.smileyBorderColor)), true);
                popSmileyBtn.getSelectedStyle().setBorder(Border.createCompoundBorder(Border.createEmpty(), Border.createEmpty(), Border.createLineBorder(1, ColorCodes.smileyBorderColor), Border.createLineBorder(1, ColorCodes.smileyBorderColor)), true);
                popSmileyBtn.getPressedStyle().setBorder(Border.createCompoundBorder(Border.createEmpty(), Border.createEmpty(), Border.createLineBorder(1, ColorCodes.smileyBorderColor), Border.createLineBorder(1, ColorCodes.smileyBorderColor)), true);
                popSmileyBtn.getUnselectedStyle().setBorder(Border.createCompoundBorder(Border.createEmpty(), Border.createEmpty(), Border.createLineBorder(1, ColorCodes.smileyBorderColor), Border.createLineBorder(1, ColorCodes.smileyBorderColor)), true);
                yellowSmileyBtn.getStyle().setBgColor(ColorCodes.smileyPaneCloseButtonBgColor, true);
                yellowSmileyBtn.getSelectedStyle().setBgColor(ColorCodes.smileyPaneCloseButtonBgColor, true);
                yellowSmileyBtn.getPressedStyle().setBgColor(ColorCodes.smileyPaneCloseButtonBgColor, true);
                yellowSmileyBtn.getUnselectedStyle().setBgColor(ColorCodes.smileyPaneCloseButtonBgColor, true);
                yellowSmileyBtn.getStyle().setBorder(Border.createCompoundBorder(Border.createLineBorder(1, ColorCodes.smileyBorderColor), Border.createEmpty(), Border.createEmpty(), Border.createEmpty()), true);
                yellowSmileyBtn.getSelectedStyle().setBorder(Border.createCompoundBorder(Border.createLineBorder(1, ColorCodes.smileyBorderColor), Border.createEmpty(), Border.createEmpty(), Border.createEmpty()), true);
                yellowSmileyBtn.getPressedStyle().setBorder(Border.createCompoundBorder(Border.createLineBorder(1, ColorCodes.smileyBorderColor), Border.createEmpty(), Border.createEmpty(), Border.createEmpty()), true);
                yellowSmileyBtn.getUnselectedStyle().setBorder(Border.createCompoundBorder(Border.createLineBorder(1, ColorCodes.smileyBorderColor), Border.createEmpty(), Border.createEmpty(), Border.createEmpty()), true);
                break;
        }
    }

    
    /**
     * This delegated method is called just after showing the form. This method is called to set the chat text in bottom of the smiley form.
     */
    protected void onShow() {
        super.onShow();
        
        mMsgTxtArea.setText(currentChatText);
    }
    
    /**
     * This method is called from outside the class for initializing current chat text which user see in the bottom of this form.
     * @param currentChatText 
     */
    public void setCurrentChatText(String currentChatText) {
        this.currentChatText = currentChatText;
    }
    
    
    /**
     * This method creates a dummy container, which makes the smiley form floating on the screen and is also used for back navigation.
     * @return 
     */
    private Container dummySendContainerForBackNavigation() {
        
        mMsgTxtArea = new Label();
        mMsgTxtArea.getStyle().setBgColor(ColorCodes.chatScreenSendContainerBg, true);
        mMsgTxtArea.getStyle().setPadding(Component.BOTTOM, 15, true);
        mMsgTxtArea.getStyle().setMargin(Component.LEFT, 5, true);
        mMsgTxtArea.getStyle().setFgColor(0xA8A8A8, true);
        mMsgTxtArea.getStyle().setBorder(Border.createEmpty(), true);
        mMsgTxtArea.getStyle().setPadding(Component.RIGHT, 0, true);
        mMsgTxtArea.getStyle().setPadding(Component.TOP, 15, true);
        mMsgTxtArea.getStyle().setMargin(Component.BOTTOM, 0, true);
        mMsgTxtArea.getStyle().setFont(Fonts.MEDIUM, true);
        
        Button smiley = new Button(AppResource.getImageFromResource(PATH_CHAT_BOX_SMILEY));
        smiley.getStyle().setBorder(Border.createEmpty(), true);
        smiley.getSelectedStyle().setBorder(Border.createEmpty(), true);
        smiley.getPressedStyle().setBorder(Border.createEmpty(), true);
        smiley.getUnselectedStyle().setBorder(Border.createEmpty(), true);
        smiley.getStyle().setBgColor(AppConstants.ColorCodes.chatScreenSendContainerBg, true);
        smiley.getSelectedStyle().setBgColor(AppConstants.ColorCodes.chatScreenSendContainerBg, true);
        smiley.getPressedStyle().setBgColor(AppConstants.ColorCodes.chatScreenSendContainerBg, true);
        smiley.getUnselectedStyle().setBgColor(AppConstants.ColorCodes.chatScreenSendContainerBg, true);
        
        //#if nokia2_0
//#         smiley.getStyle().setPadding(Component.LEFT, 5, true);
//#         smiley.getSelectedStyle().setPadding(Component.LEFT, 5, true);
//#         smiley.getPressedStyle().setPadding(Component.LEFT, 5, true);
//#         smiley.getUnselectedStyle().setPadding(Component.LEFT, 5, true);
//#         smiley.getStyle().setMargin(Component.BOTTOM, 5, true);
//#         smiley.getSelectedStyle().setMargin(Component.BOTTOM, 5, true);
//#         smiley.getPressedStyle().setMargin(Component.BOTTOM, 5, true);
//#         smiley.getUnselectedStyle().setMargin(Component.BOTTOM, 5, true);
        //#elif nokia1_1
//#         smiley.getStyle().setPadding(Component.LEFT, 0, true);
//#         smiley.getSelectedStyle().setPadding(Component.LEFT, 0, true);
//#         smiley.getPressedStyle().setPadding(Component.LEFT, 0, true);
//#         smiley.getUnselectedStyle().setPadding(Component.LEFT, 0, true);
//#         smiley.getDisabledStyle().setPadding(Component.LEFT, 0, true);
//#         smiley.getStyle().setMargin(Component.BOTTOM, 0, true);
//#         smiley.getSelectedStyle().setMargin(Component.BOTTOM, 0, true);
//#         smiley.getPressedStyle().setMargin(Component.BOTTOM, 0, true);
//#         smiley.getUnselectedStyle().setMargin(Component.BOTTOM, 0, true);
//#         smiley.getDisabledStyle().setMargin(Component.BOTTOM, 0, true);
        //#endif

        smiley.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               DisplayStackManager.showForm(DisplayStackManager.FORM_CHAT);
            }
        });
        
        Container chatBoxNSmileyContainer = new Container(new BorderLayout());
        chatBoxNSmileyContainer.getStyle().setBorder(Border.createCompoundBorder(Border.createLineBorder(1, ColorCodes.chatScreenSendContainerTopShadow), null, null, null), true);
        chatBoxNSmileyContainer.getSelectedStyle().setBorder(Border.createCompoundBorder(Border.createLineBorder(1, ColorCodes.chatScreenSendContainerTopShadow), null, null, null), true);
        chatBoxNSmileyContainer.getStyle().setBgColor(ColorCodes.chatScreenSendContainerBg, true);
        chatBoxNSmileyContainer.getSelectedStyle().setBgColor(ColorCodes.chatScreenSendContainerBg, true);
        chatBoxNSmileyContainer.getPressedStyle().setBgColor(ColorCodes.chatScreenSendContainerBg, true);
        chatBoxNSmileyContainer.getUnselectedStyle().setBgColor(ColorCodes.chatScreenSendContainerBg, true);
        chatBoxNSmileyContainer.getStyle().setBgTransparency(255, true);
        chatBoxNSmileyContainer.getSelectedStyle().setBgTransparency(255, true);
        chatBoxNSmileyContainer.getPressedStyle().setBgTransparency(255, true);
        chatBoxNSmileyContainer.getUnselectedStyle().setBgTransparency(255, true);
        chatBoxNSmileyContainer.addComponent(BorderLayout.CENTER, mMsgTxtArea);
        //#if nokia2_0
//#         chatBoxNSmileyContainer.addComponent(BorderLayout.WEST, smiley);
        //#else 
chatBoxNSmileyContainer.addComponent(BorderLayout.EAST, smiley);
        //#endif
        
        return chatBoxNSmileyContainer;
    }
}
