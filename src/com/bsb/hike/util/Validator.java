/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.util;

import com.bsb.hike.dto.AppState;

/**
 *Class containing methods related to various validations
 * @author Sudheer Keshav Bhat
 */
public class Validator implements AppConstants{

    /**
     * test whether entered phone number is valid
     * @param phoneNum
     * @return true if number is valid, false otherwise
     */
    public static boolean validatePhoneNum(String phoneNum) {
        if (!TextUtils.isEmpty(phoneNum)) {
            if (phoneNum.startsWith("+0") || phoneNum.startsWith("000")) {
                return false;
            } else if (phoneNum.startsWith("+")) {           
                phoneNum = phoneNum.substring(1, phoneNum.length());
            } else if (phoneNum.startsWith("00")) {           
                phoneNum = phoneNum.substring(2, phoneNum.length());
            } else if (phoneNum.startsWith("0")) {           
                phoneNum = phoneNum.substring(1, phoneNum.length());
            }
            if(isValidNumericString(phoneNum)){
                phoneNum = String.valueOf(Long.parseLong(phoneNum));
                if (phoneNum.length() >= CharLimit.PHONE_NUMBER_MIN && phoneNum.length() <= CharLimit.PHONE_NUMBER_MAX) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * returns validated string of phone number entered. it appends country code if not included
     * @param text
     * @return valid phone number string including country code
     */
    public static String validatePhoneString(String text) {
        //does not check for length
        String countryCode = AppState.getUserDetails().getCountryCode();
        if (TextUtils.isEmpty(text)) {
            return null;
        } else {
            String num = text;
            if (num.startsWith("+")) {
                num = num.length() == 1 ? "" : num.substring(1);
            } else if (num.startsWith("+0") || num.startsWith("000")) {
                return null;
            }
            if (isValidNumericString(num)) {
                if (text.startsWith("+")) {
                    return text;
                } else {                    
                    return "+" + countryCode + Long.parseLong(num);
                }
            } else {
                return null;
            }
        }
    }

    /**
     * check whether the given string is all numeral
     * @param numeric
     * @return 
     */
    public static boolean isValidNumericString(String numeric) {
        boolean valid = false;
        if (!TextUtils.isEmpty(numeric)) {
            try {
                Long.parseLong(numeric);
                valid = true;
            } catch (NumberFormatException nEx) {
                valid = false;
            }
        }
        return valid;
    }
}
