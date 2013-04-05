/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.ui.component;

import com.bsb.hike.ui.helper.SmileyManager;
import com.bsb.hike.ui.helper.UIManager;
import com.bsb.hike.util.AppConstants.ColorCodes;
import com.bsb.hike.util.AppConstants.Fonts;
import com.bsb.hike.util.Log;
import com.sun.lwuit.Component;
import com.sun.lwuit.Display;
import com.sun.lwuit.Graphics;
import java.util.Vector;

/**
 *
 * @author Ankit Yadav
 */
public class ChatBubbleTextArea extends PatchedTextArea {

    private static final String TAG = "ChatBubbleTextArea";
    public Vector mRenderList;
    private PointerPressListener mPointerPressListener;
    public int mWidth = 0, mHeight = 0 , mRows = 0;
    
    public static interface PointerPressListener {
        void onPointerPressed(int x, int y);
        void onPointerReleased(int x, int y);
        void onPointerDragged(int x, int y);        
    }
    
    
    /**
     * This method returns message vector to render on the chat bubble.
     * @return 
     */
    public Vector getRenderList() {
        return mRenderList;
    }
    
    
    /**
     * This method sets the message vector 
     * @param renderList 
     */
//    public void setRenderList(Vector renderList){
//        mRenderList = renderList;
//    }

    
    /**
     * Parameterized Constructor for setting style, margin and padding. 
     * @param text
     * @param maxSize 
     */
    public ChatBubbleTextArea(String text, int maxSize) {
        super(text, maxSize);

        getStyle().setFont(Fonts.SMALL, true);
        getSelectedStyle().setFont(Fonts.SMALL, true);
        getPressedStyle().setFont(Fonts.SMALL, true);
        getUnselectedStyle().setFont(Fonts.SMALL, true);
        getStyle().setFgColor(ColorCodes.chatScreenChatBubbleTextColor, true);
        getSelectedStyle().setFgColor(ColorCodes.chatScreenChatBubbleTextColor, true);
        getPressedStyle().setFgColor(ColorCodes.chatScreenChatBubbleTextColor, true);
        getUnselectedStyle().setFgColor(ColorCodes.chatScreenChatBubbleTextColor, true);
        getStyle().setPadding(5, 5, 5, 8);
        getSelectedStyle().setPadding(5, 5, 5, 8);
        getPressedStyle().setPadding(5, 5, 5, 8);
        getUnselectedStyle().setPadding(5, 5, 5, 8);

        //int width = Display.getInstance().getDisplayWidth();
        //final int padding = 75;
        setGrowByContent(true);
        setEditable(false);
        getStyle().setMargin(Component.TOP, 5, true);
        getSelectedStyle().setMargin(Component.TOP, 5, true);
        getPressedStyle().setMargin(Component.TOP, 5, true);
        getUnselectedStyle().setMargin(Component.TOP, 5, true);
        getStyle().setMargin(Component.BOTTOM, 5, true);
        getSelectedStyle().setMargin(Component.BOTTOM, 5, true);
        getPressedStyle().setMargin(Component.BOTTOM, 5, true);
        getUnselectedStyle().setMargin(Component.BOTTOM, 5, true);
        Log.v(TAG, "getRenderList call start");
        SmileyManager.getInstance().getRenderList(getText(), this);
        Log.v(TAG, "getRenderList call end");
//        String textStr = getText();
//        if (getStyle().getFont().stringWidth(textStr) > (width - padding)) {
//            setSingleLineTextArea(false);
//            setRows(2);
//            setColumns((width - padding) / getStyle().getFont().charWidth('W'));
//        } else {
//            setSingleLineTextArea(true);
//        }
        
        setRows(mRows);
        setColumns(mWidth);
         Log.v(TAG, "getRenderList call width: "+mWidth);
    }

    
    /**
     * This is a overrided method of which will call custom method to draw text on chat bubble.
     * @param g 
     */
    public void paint(Graphics g) {
        UIManager.getInstance().getLookAndFeel().drawTextArea(g, this);
    }

    
    /**
     * THis method sets the text on chat bubble.
     * @param t 
     */
    public void setText(String t) {
        super.setText(t);
    }

    
    /**
     * Overrided method to take press event on chat buuble.
     * @param x
     * @param y 
     */
    public void pointerPressed(int x, int y) {
        super.pointerPressed(x, y);
        if (mPointerPressListener != null) {
            mPointerPressListener.onPointerPressed(x, y);
        }
    }

    
    /**
     * Overrided method to take release event on chat bubble.
     * @param x
     * @param y 
     */
    public void pointerReleased(int x, int y) {
        super.pointerReleased(x, y);
        if (mPointerPressListener != null) {
            mPointerPressListener.onPointerReleased(x, y);
        }

    }

    
    /**
     * Overrided method to take drag event on chat bubble.
     * @param x
     * @param y 
     */
    public void pointerDragged(int x, int y) {
        super.pointerDragged(x, y);
        if (mPointerPressListener != null) {
            mPointerPressListener.onPointerDragged(x, y);
        }
    }
  
    
    /**
     * This method initializes pointer press listener on chat bubble.
     * @param listener 
     */
    public void setPointerPressListener(PointerPressListener listener) {
        if (listener != null) {
            mPointerPressListener = listener;
        }
    }

    public int getPreferredW() {
        return mWidth+40;
    }
    
    public int getWidth() {
        return mWidth+40;
    }

    public int getPreferredH() {
        return ((mRows+1) * mHeight) + 20;
    }
    
    public int getHeight() {
        return ((mRows+1) * mHeight) + 20;
    }
    
}
