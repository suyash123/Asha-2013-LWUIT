/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.util;

import com.bsb.hike.dto.AppState;
import com.bsb.hike.ui.DisplayStackManager;
import com.bsb.hike.ui.FormError;

/**
 *basic implementaion of retryable interface
 * @author Sudheer Keshav Bhat
 */
public class BaseRetryable implements Retryable{   

    protected final static FormError errorForm = (FormError) DisplayStackManager.getForm(DisplayStackManager.FORM_ERROR, true);

    public BaseRetryable() {
        errorForm.setRetryableObj(this);
    }
    
    public void retry() {
        //TODO
    }
    
    public void cancel() {
        DisplayStackManager.getForm(AppState.getForm(), true).show();
    }

    public void exit() {
        //TODO
    }      
}
