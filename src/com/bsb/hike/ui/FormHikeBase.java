package com.bsb.hike.ui;

import com.bsb.hike.dto.AppState;
import com.bsb.hike.util.AppConstants;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;

/**
 *
 * @author Ankit Yadav
 */
public class FormHikeBase extends Form implements AppConstants {

    private Label title;
    private int pageNumber;
    private static final String TAG = "FormHikeBase";
    
    
    /**
     * Constructor of parent class of all UI classes.
     */
    public FormHikeBase() {

        title = new Label();
        //#if nokia2_0
//#         title.setText(APP_TITLE);
//#         getStyle().setBgColor(ColorCodes.commonBgColor, true);
//#         getStyle().setBgTransparency(255, true);
        //#else
        getStyle().setBgImage(null);
        //#endif

        setTitleComponent(title);
    }

    /**
     * This delegated method is called just after showing the form. When this method called this will save the page number of the current form.
     */
    protected void onShow() {
        super.onShow();
        AppState.setForm(pageNumber);
    }

    
    /**
     * This method is called from outside the class. This method initializes the page number when called.
     * @param pageNumber 
     */
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    
    /**
     * This method returns the current form page number.
     * @return 
     */
    public int getPageNumber() {
        return pageNumber;
    }
    
    
    /**
     * This method checks whether the current page is signup form or not. If it is signup form, then it will return true else false.
     * @return 
     */
    public boolean isSignUp(){
        return this.pageNumber <= DisplayStackManager.FORM_SET_NAME;
    }
}
