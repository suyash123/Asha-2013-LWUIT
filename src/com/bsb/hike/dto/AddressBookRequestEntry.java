package com.bsb.hike.dto;

/**
 * dto for addressbook request 
 * @author Ankit Yadav
 */
public class AddressBookRequestEntry implements DataModel {

    private String id;
    private String phoneNumber;
    private String name;

    public AddressBookRequestEntry(String id, String phoneNumber, String name) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.name = name;
    }

    /**
     * 
     * @return contact id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    public String toString() {
        return "AddressBookRequestEntry{id:" + id + ", phoneNumber:" + phoneNumber + ", name:" + name + "}";
    }
}
