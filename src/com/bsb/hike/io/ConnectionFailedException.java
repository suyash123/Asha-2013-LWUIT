/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.io;

/**
 *
 * @author Ankit Yadav
 */
public class ConnectionFailedException extends Exception{

    public String toString() {
        return "Connection can not be established.";
    }   
}
