/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.util;

/**
 *interface for retryable actions
 * @author Ankit Yadav
 */
public interface Retryable {

    public void retry();
    public void cancel();
    
    public void exit();
}
