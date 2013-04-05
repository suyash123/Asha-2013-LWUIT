/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author Sudheer Keshav Bhat
 */
public class TextUtils implements AppConstants {

    private static final String TAG = "TextUtils";
    private static final String MONTHS[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private static final String CAPITALIZED_MONTHS[] = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
    private static final long diffNow = 60 * 1000;
    private static final long diffMin = 60 * 60 * 1000;
    private static final long diffHr = 24 * 60 * 60 * 1000;

    /**
     *format the phone number string
     * @param number
     * @return formatted phone number +XXXXX-XXX-XXXX or XXX-XXX-XXXX
     */
    public static String formatPhoneNumber(String number, boolean includeCountryCode) {
        if (number == null) {
            return EMPTY_STRING;
        } else if ((number.startsWith("+") && number.length() < 13) || (number.startsWith("+") && number.length() < 10)) {
            return number;
        }
        String firstThreeDigit = number.startsWith("+") && includeCountryCode ? number.substring(0, 6) : number.substring(3, 6);
        String middleThreeDigit = number.substring(6, 9);
        String lastFourDigit = number.substring(9);

        Log.v(TAG, firstThreeDigit + "-" + middleThreeDigit + "-" + lastFourDigit);
        return firstThreeDigit + "-" + middleThreeDigit + "-" + lastFourDigit;
    }

    /**
     * check whether the string is empty
     * @param string
     * @return 
     */
    public static boolean isEmpty(String string) {
        return string == null ? true : string.trim().length() == 0;
    }

    /**
     * creates and returns combined string of object array passed in, combines the toString response of each element separated by (, )
     * @param objects
     * @return 
     */
    public static String toString(Object[] objects) {
        StringBuffer buffer = new StringBuffer();
        if (objects != null) {
            for (int i = 0; i < objects.length; i++) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append(objects[i]);
            }
        }
        return buffer.toString();
    }

    /**
     * creates and returns combined string of vector passed in, combines the toString response of each element separated by new line
     * @param objects
     * @return 
     */
    public static String toString(Vector objects) {
        StringBuffer buffer = new StringBuffer();
        if (objects != null) {
            for (int i = 0; i < objects.size(); i++) {
                if (i > 0) {
                    buffer.append("\n");
                }
                buffer.append(objects.elementAt(i));
            }
        }
        return buffer.toString();
    }

    /**
     * creates and returns combined string of hashtable keys passed in, combines the toString response of each key separated by (, )
     * @param map
     * @return 
     */
    public static String toString(Hashtable map) {
        StringBuffer buffer = new StringBuffer();
        boolean first = true;
        if (map != null) {
            buffer.append("{");
            for (Enumeration e = map.keys(); e.hasMoreElements();) {
                Object key = e.nextElement();
                if (!first) {
                    buffer.append(", ");
                } else {
                    first = false;
                }
                buffer.append(key).append(":").append(map.get(key));
            }
            buffer.append("}");
        }
        return buffer.toString();
    }

    /**
     * parses the String with numeral as long
     * @param longStr
     * @return long value if string contains all numerals, 0 otherwise
     */
    public static long parseLong(String longStr) {
        long num = 0;
        try {
            num = Long.parseLong(longStr);
        } catch (NumberFormatException e) {
        }
        return num;
    }

    /**
     * parses the object as long
     * @param longStr
     * @return long value if object.toString contains all numerals, 0 otherwise
     */
    public static long parseLong(Object longStr) {
        return parseLong(String.valueOf(longStr));
    }

    /**
     * parses the String with numeral as int
     * @param intStr 
     * @return long value if string contains all numerals, 0 otherwise
     */
    public static int parseInt(String intStr) {
        int num = 0;
        try {
            num = Integer.parseInt(intStr);
        } catch (NumberFormatException e) {
        }
        return num;
    }

    /**
     * parses the object as int
     * @param intStr 
     * @return long value if object.toString contains all numerals, 0 otherwise
     */
    public static int parseInt(Object intStr) {
        return parseInt(String.valueOf(intStr));
    }

    public static String toTimestamp(long milliseconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(milliseconds));
        StringBuffer buffer = new StringBuffer(25);
        int date = calendar.get(Calendar.DATE);
        String month = CAPITALIZED_MONTHS[calendar.get(Calendar.MONTH)];
        int year = calendar.get(Calendar.YEAR) % 1000;
        int hr = calendar.get(Calendar.HOUR);
        int min = calendar.get(Calendar.MINUTE);
        String minFormatted = min < 10 ? "0" + min : EMPTY_STRING + min;
        String am_pm = calendar.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";
        if (am_pm.equals("PM") && hr == 0) {
            hr = 12;
        }
        return buffer.append(date).append(" ").append(month).append(" '").append(year).append(" AT ").append(hr)
                .append(":").append(minFormatted).append(" ").append(am_pm).toString();
    }

    /**
     * returns a formatted time difference of given time to current time
     * @param mTime
     * @return 
     */
    public static String toTimeDiff(long mTime) {
        Date currentDate = new Date();
        long presentTime = currentDate.getTime();
        long timeDifference = presentTime - mTime;
        String timeVal = EMPTY_STRING;
        if (timeDifference < diffNow) {
            timeVal = "now";
        } else if (timeDifference >= diffNow && timeDifference < (diffMin)) {
            String minTime = String.valueOf(timeDifference / (diffNow));
            timeVal = minTime + " " + "Min";
        } else if (timeDifference >= (diffMin) && timeDifference < (diffHr)) {
            String hrTime = String.valueOf(timeDifference / (diffMin));
            timeVal = hrTime + " " + "Hr";
        } else if (timeDifference >= (diffHr)) {
            Calendar c = Calendar.getInstance();
            c.setTime(new Date(mTime));
            int m = c.get(Calendar.MONTH);
            String daysTime = String.valueOf(c.get(Calendar.DATE)) + " " + MONTHS[m];
            timeVal = daysTime;
        }
        return timeVal;
    }

    /**
     * create a name string from vector of string based on number of elements
     * @param list
     * @return 
     */
    public static String toNames(Vector list) {
        if (list.size() < 1) {
            return null;
        } else if (list.size() == 1) {
            return list.elementAt(0).toString();
        } else if (list.size() == 2) {
            return list.elementAt(0) + " and " + list.elementAt(1);
        } else {
            String names = EMPTY_STRING;
            for (int i = 0; i < list.size() - 2; i++) {
                names += list.elementAt(i) + ", ";
            }
            names += list.elementAt(list.size() - 2) + " and ";
            names += list.elementAt(list.size() - 1);
            return names;
        }
    }

    /**
     * check whether 2nd parameter string is a part of 1st string
     * @param parent
     * @param subString
     * @return 
     */
    public static boolean contains(String parent, String subString) {
        if (parent == null || subString == null) {
            return false;
        }
        return parent.indexOf(subString) != -1;
    }

    /**
     * extracts a http:// link from text
     * @param text
     * @return 
     */
    public static String getLink(String text) {
        int index = text.indexOf(HTTP_ROOT);
        int breakIndex = text.indexOf(SINGLE_SPACE, index);
        if (index > -1) {
            if (breakIndex > -1) {
                return text.substring(index, breakIndex);
            } else {
                return text.substring(index);
            }
        }
        return null;
    }

    /**
     * returns 1st word from given String, can be used to depart first name
     * @param text
     * @return 
     */
    public static String getFirstWord(String text) {
        if (isEmpty(text)) {
            return text;
        } else if (contains(text, SINGLE_SPACE)) {
            return text.substring(0, text.indexOf(SINGLE_SPACE));
        } else {
            return text;
        }
    }

    public static int maxCharInRow(String source) {
        int max_char_count_in_row = 0;
        final String NEW_LINE = "\n";
        if (source == null) {
            return max_char_count_in_row;
        }
        int search_Index = 0;
        int newLineIndex;
        source = source + NEW_LINE;
        while ((newLineIndex = source.indexOf(NEW_LINE, search_Index)) != -1) {
            max_char_count_in_row = (max_char_count_in_row < newLineIndex - search_Index) ? (newLineIndex - search_Index) : max_char_count_in_row;
            search_Index = newLineIndex + NEW_LINE.length();
        }
        return max_char_count_in_row;
    }

    /**
     * replaces string with replacement string in text message
     * @param strToReplace String to replace
     * @param replacement
     * @param textMsg
     * @return 
     */
   public static String replace(Vector strToReplace, Vector replacement, String textMsg) {
       int size = strToReplace.size();
       if (size == replacement.size()) {
           for (int i = 0; i < size; i++) {
              textMsg = replaceOccurence(strToReplace.elementAt(i).toString(), replacement.elementAt(i).toString(), textMsg);
           }
       }
       return textMsg;
   }
   
    private static String replaceOccurence(String strToReplace, String replacement, String textMsg) {
        String result = EMPTY_STRING;
        int index = textMsg.indexOf(strToReplace);
        if (index == 0) {
            result = replacement + textMsg.substring(strToReplace.length());
            return replaceOccurence(strToReplace, replacement, result);
        } else if (index > 0) {
            result = textMsg.substring(0, index) + replacement + textMsg.substring(index + strToReplace.length());
            return replaceOccurence(strToReplace, replacement, result);
        } else {
            return textMsg;
        }
    }
    
    public static Vector getOriginalSmileys(){
        Vector originalSmileys = new Vector();
        originalSmileys.addElement(AppConstants.MSG_IDEA_SMILEY_ORIGINAL_STRING);
        originalSmileys.addElement(AppConstants.MSG_SWEET_ANGEL_SMILEY_ORIGINAL_STRING);
        originalSmileys.addElement(AppConstants.MSG_BOMM_ORIGINAL_STRING);
        return originalSmileys;
    }
    
    public static Vector getMappedSmileys(){
        Vector mappedSmileys = new Vector();
        mappedSmileys.addElement(AppConstants.MSG_IDEA_SMILEY_REPLCAED_STRING);
        mappedSmileys.addElement(AppConstants.MSG_SWEET_ANGEL_SMILEY_REPLCAED_STRING);
        mappedSmileys.addElement(AppConstants.MSG_BOMB_REPLACED_STRING);
        return mappedSmileys;
    }
    
    /**
     * get IMEI from device and create a hash of it with a given algorithm
     * @return 
     */
    public static String processIMEIintoHash() {
        String imeiCode = System.getProperty(DEVICE_IMEI);
        if(imeiCode != null) {
            byte[] array = imeiCode.getBytes();
            return PLATFORM_NAME + SEPARATOR + hashCode(array);
        }
        return null;
    }
    
    
    /**
     * takes byte[] and creates a hash from it
     * @param array
     * @return 
     */
    public static String hashCode(byte[] array) {
        String retString = EMPTY_STRING;
        double result = 19;
        for (int i = 0; i < array.length; i++) {
            result = 31 * result + (int) array[i];
        }
        retString = result + EMPTY_STRING;

        return retString;
    }
    
    
    /**
     * This method returns the device model number
     * @return 
     */
    public static String getDeviceModel(){
        String modelNumber = null;
        String platform = System.getProperty(PLATFORM_NAME_AND_VERSION);
        int firmwareSeparator = platform.indexOf(SLASH_FORWARD);
        if(firmwareSeparator != -1) {
            String value = platform.substring(0, firmwareSeparator + 1);
            modelNumber = value.substring(NOKIA_WORD_LENGTH, firmwareSeparator);
        }
        return modelNumber;
    }
} 
