/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.dto;

import java.util.Enumeration;

/**
 *
 * @author Ankit Yadav
 */
public class GroupList extends DataCollection {

    private final static GroupList list = new GroupList();

    private GroupList() {
    }

    /**
     * 
     * @return singleton instance of grouplist
     */
    public static GroupList getInstance() {
        return list;
    }

    /**
     * iterates and return GroupMembers by groupId
     * @param groupId
     * @return 
     */
    public GroupMembers getEntryByGroupId(String groupId) {
        if(groupId == null) {
            return null;
        }
        
        Enumeration enums = getInstance().elements();
        while (enums.hasMoreElements()){
            GroupMembers entry = (GroupMembers) enums.nextElement();
            if (groupId.equals(entry.getGroupId())) {
                return entry;
            }
        }
        return null;
    }
}
