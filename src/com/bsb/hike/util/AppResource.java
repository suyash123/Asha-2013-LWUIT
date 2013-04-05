/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.util;

import com.sun.lwuit.Image;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Hashtable;

/**
 *Abstract class to store and access static resources
 * @author Ankit Yadav
 */
public abstract class AppResource implements AppConstants {
    private static final String TAG = "AppResource";
    private final static Hashtable imageTable = new Hashtable();
    private final static Hashtable smiliesTable = new Hashtable();

    /**
     * creates and returns image from given path, maintains single instance for images, and keeps smilies and other images separate
     * @param path
     * @return 
     */
    public static synchronized Image getImageFromResource(String path) {
        Hashtable table;
        if (path.startsWith("/smileys")) {
            table = smiliesTable;
        } else {
            table = imageTable;
        }
        if (table.containsKey(path) && ((WeakReference)table.get(path)).get() != null) {
            return (Image) ((WeakReference)table.get(path)).get();
        } else {
            InputStream imgStream = "String".getClass().getResourceAsStream(path);                     
            Log.v(TAG, "Input Stream of file: " + path + "= " + imgStream);
            Image img = null;
            if (imgStream != null) {
                try {
                    img = Image.createImage(imgStream);
                    table.put(path, new WeakReference(img));
                             
                    Log.v(TAG, "image table now contains image: " + path);
                } catch (Exception ex) {                             
                    Log.v(TAG, "Image creation failed for: " + path + "= " + imgStream);
                }
            }
            return img;
        }
    }
    
    /**
     * clears images from smilies cache
     */
    public static synchronized void releaseSmileyImages() {
        smiliesTable.clear();   
        Runtime.getRuntime().gc();
        Log.v(TAG, "smilies released from hashtable");
    }
}
