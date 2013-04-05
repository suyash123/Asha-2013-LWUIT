/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.ui.component;

import com.bsb.hike.util.AppConstants;
import com.sun.lwuit.TextArea;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Patched version of <code>TextArea</code> with fixes to missing or buggy functionality.
 * @author Sudheer Keshav Bhat
 */
public class PatchedTextArea extends TextArea implements AppConstants {

    private String mPreviusText;
    private Timer mTextChngListenerTimer;
    private TimerTask mTextChngListenerTimerTask;
    private LongPressListener mLongPressListener;
    private TextChangedListener mTextChangedListener;
    
    public static interface LongPressListener {
        void onPointerPressed(int x, int y);
        void onPointerReleased(int x, int y);
        void onPointerDragged(int x, int y);      
    }
    
    
    /**
     * Overrided method to take press event on patched TextArea.
     * @param x
     * @param y 
     */
    public void pointerPressed(int x, int y) {
        super.pointerPressed(x, y);
        if (mLongPressListener != null) {
            mLongPressListener.onPointerPressed(x, y);
        }
    }

    
    /**
     * Overrided method to take release event on patched TextArea.
     * @param x
     * @param y 
     */
    public void pointerReleased(int x, int y) {
        super.pointerReleased(x, y);
        if (mLongPressListener != null) {
            mLongPressListener.onPointerReleased(x, y);
        }

    }

    
    /**
     * Overrided method to take drag event on patched TextArea.
     * @param x
     * @param y 
     */
    public void pointerDragged(int x, int y) {
        super.pointerDragged(x, y);
        if (mLongPressListener != null) {
            mLongPressListener.onPointerDragged(x, y);
        }
    }
    
    
    /**
     * This method initializes long press listener on patched TextArea.
     * @param listener 
     */
    public void setLongPressListener(LongPressListener longPressListener) {
        if(longPressListener != null) {
            mLongPressListener = longPressListener;
        }
    }
    
    public static interface TextChangedListener {
        void onTextChange(PatchedTextArea textArea, String previousText, String currentText);
    }
    
    public PatchedTextArea(int rows, int columns) {
        super(rows, columns);
    }

    public PatchedTextArea(int rows, int columns, int constraint) {
        super(rows, columns, constraint);
    }

    public PatchedTextArea(String text, int rows, int columns) {
        super(text, rows, columns);
    }

    public PatchedTextArea(String text, int rows, int columns, int constraint) {
        super(text, rows, columns, constraint);
    }

    public PatchedTextArea(String text, int maxSize) {
        super(text, maxSize);
    }

    public PatchedTextArea(String text) {
        super(text);
    }

    public PatchedTextArea() {
        super();
    }
    
    
    /**
     * This method sets text change listener.
     * @param textChangedListener 
     */
    public void setTextChangedListener(TextChangedListener textChangedListener) {
        this.mTextChangedListener = textChangedListener;
        if(textChangedListener != null) {
            initTextWatcher();
        } else {
            destroyTextWatcher();
        }
    }
    
    
    /**
     * This method is called to initialize text watcher and detects when there is any text change on patched TextArea
     */
    private void initTextWatcher() {        
        mPreviusText = getText() != null ? getText() : EMPTY_STRING;
        mTextChngListenerTimer = new Timer();
        mTextChngListenerTimerTask = new TimerTask() {            
            public void run() {
                if (mTextChangedListener != null && !mPreviusText.equals(getText())) {
                    String text = mPreviusText;
                    mPreviusText = getText();
                    mTextChangedListener.onTextChange(PatchedTextArea.this, text, getText());
                }
            }
        };
        mTextChngListenerTimer.schedule(mTextChngListenerTimerTask, TEXT_WATCHER_PERIOD, TEXT_WATCHER_PERIOD);
    }
    
    
    /**
     * This method cancels all the timer events which detects text change on patched TextArea
     */
    private void destroyTextWatcher() {
        mPreviusText = null;
        if (mTextChngListenerTimer != null) {
            mTextChngListenerTimer.cancel();
            mTextChngListenerTimer = null;
        }
        if (mTextChngListenerTimerTask != null) {
            mTextChngListenerTimerTask.cancel();
            mTextChngListenerTimerTask = null;
        }
    }
}
