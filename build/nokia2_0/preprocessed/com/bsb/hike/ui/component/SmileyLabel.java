/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.ui.component;

import com.bsb.hike.ui.helper.UIManager;
import com.bsb.hike.util.Log;
import com.sun.lwuit.Graphics;
import com.sun.lwuit.Label;

/**
 *
 * @author Sudheer Keshav Bhat
 */
public class SmileyLabel extends Label {
    
    public SmileyLabel(String text){
        super(text);
    }

    private static final String TAG = "Text";

    
    /**
     * Overrided method to give custom look and feel
     * @param g 
     */
    public void paint(Graphics g) {
                 
        Log.v(TAG, "paint()");
        UIManager.getInstance().getLookAndFeel().drawLabel(g, this);
    }
}
