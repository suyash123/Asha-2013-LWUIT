/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.util;

import com.sun.lwuit.TextArea;

/**
 *
 * @author Sudheer Keshav Bhat
 */
public class ClipboardManager {
    
    private static final String TAG = "ClipboardManager";

    private static final ClipboardManager sInstance = new ClipboardManager();

    public static ClipboardManager getInstance() {
        return sInstance;
    }
    private String mCopiedText;

    public void copy(TextArea txtArea) {
        mCopiedText = txtArea.getText();
    }

    public void copy(String text) {
        mCopiedText = text;
    }

    public void paste(TextArea txtArea) {
        if (!TextUtils.isEmpty(mCopiedText)) {
            String currentTxt = txtArea.getText();
            StringBuffer currentTxtBuff = new StringBuffer(currentTxt.length() + mCopiedText.length());
            currentTxtBuff.append(currentTxt);
            int cursorPos = txtArea.getCursorPosition();
                     
            Log.v(TAG, "cursorPos:" + cursorPos);
            if (cursorPos >= 0 && cursorPos < currentTxt.length()) {
                currentTxtBuff.insert(cursorPos, mCopiedText);
            } else {
                currentTxtBuff.append(mCopiedText);
            }
            txtArea.setText(currentTxtBuff.toString());
        }
    }
    
    public boolean isClipboardContainsText() {
        if(!mCopiedText.trim().equals(AppConstants.EMPTY_STRING)) {
            return true;
        }
        return false;
    }
    
    public void clearClipboard() {
        mCopiedText = AppConstants.EMPTY_STRING;
    }

    public String paste() {
        return mCopiedText;
    }
}
