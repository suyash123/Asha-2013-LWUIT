/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.ui.component;

import com.bsb.hike.util.AppConstants;
import com.sun.lwuit.plaf.Border;

/**
 *
 * @author Ankit Yadav
 */
public class SearchTextField extends PatchedTextArea  implements AppConstants{

    
    /**
     * Constructor of search TextField with styles and parameters.
     */
    public SearchTextField() {
        getStyle().setBgColor(AppConstants.ColorCodes.selectContactSearchFieldBg, true);
        getSelectedStyle().setBgColor(AppConstants.ColorCodes.selectContactSearchFieldBg, true);
        getSelectedStyle().setBgTransparency(255, true);
        getStyle().setBgTransparency(255, true);
        setFocusable(true);
        getStyle().setFgColor(AppConstants.ColorCodes.white, true);
        getSelectedStyle().setFgColor(AppConstants.ColorCodes.white, true);
        
        getStyle().setBorder(Border.createCompoundBorder(null, Border.createLineBorder(1, AppConstants.ColorCodes.selectContactSeperator), null, null), true);
        getSelectedStyle().setBorder(Border.createCompoundBorder(null, Border.createLineBorder(1, AppConstants.ColorCodes.selectContactSeperator), null, null), true);
        setScrollVisible(false);

        setColumns(30);
        setSingleLineTextArea(false);


//        setHint("Start typing...");
//        TODO restrict the limit
//        setMaxSize(AppConstants.CharLimit.DEFAULT_MAX);
    }

    
    /**
     * This method returns the text width on search TextField
     * @param source
     * @return 
     */
    public int getStringWidth(String source) {
        if (source != null) {
            return getStyle().getFont().stringWidth(source);
        }
        return 0;
    }
}
