/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.dto;

import com.bsb.hike.util.Log;
import com.bsb.hike.util.ModelUtils;
import com.bsb.hike.util.TextUtils;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 *dto for group
 * @author Ankit Yadav
 */
public class GroupMembers implements DataModel {

    private final static String TAG = "GroupMembers";
    private String groupId;
    private String groupName;
    private String createrMsisdn;
    private boolean chatAlive = true;
    private Hashtable members; // key msisdn, value GroupMember
    private Vector memberList;

    public GroupMembers(String groupId, String createrMsisdn) {
        this.groupId = groupId;
        this.createrMsisdn = createrMsisdn;
        this.members = new Hashtable(MAX_GROUP_SIZE);
        this.memberList = new Vector(MAX_GROUP_SIZE);
        this.groupName = EMPTY_STRING;
    }

    /**
     * @return the groupId
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * @return the createrMsisdn
     */
    public String getCreaterMsisdn() {
        return createrMsisdn;
    }

    /**
     * @return the members has list
     */
    public Hashtable getMembers() {
        return members;
    }

    /**
     * 
     * @return the member's list
     */
    public Vector getMembersList() {
        return memberList;
    }

    /**
     * 
     * @param includeOwner
     * @return member's list those are currently active
     */
    public Vector getActiveMembersList(boolean includeOwner) {
        Vector membersList = new Vector(memberList.size() + 1);
        GroupMember owner = null;
        boolean meIncludedInOthersGC = false;
        for (int i = 0; i < memberList.size(); i++) {
            GroupMember aMember = (GroupMember) memberList.elementAt(i);
            
            Log.v(TAG, " Member in Group info: "+aMember);
            if (aMember.isActive()) {
                membersList.addElement(aMember);
            }
            if (aMember.getMsisdn().equals(createrMsisdn)) {
                owner = aMember;
            }
            if(aMember.getMsisdn().equals(AppState.getUserDetails().getMsisdn())) {
                meIncludedInOthersGC = true;
            }
        }
        if (owner == null && includeOwner) {
            //includes dummy data
            String name = AppState.getUserDetails().getMsisdn().equals(createrMsisdn) ? AppState.getUserDetails().getName() : createrMsisdn;
            membersList.insertElementAt(new GroupMember(createrMsisdn, name, true, true, null, GroupMember.MemberSource.SELF), 0);
        }
        
//        else if(owner != null && includeOwner) { //TODO check this logic again.
//            membersList.removeElement(owner);
//        }
        
        if(!meIncludedInOthersGC && !AppState.getUserDetails().getMsisdn().equals(createrMsisdn)) {
            String name = AppState.getUserDetails().getName() != null ? AppState.getUserDetails().getName() : AppState.getUserDetails().getMsisdn();
            membersList.addElement(new GroupMember(AppState.getUserDetails().getMsisdn(), name, true, true, null, GroupMember.MemberSource.SELF));
        }
        return membersList;
    }

    /**
     * adds members to this group and also adds corresponding  system notification
     * @param aMember 
     */
    private void addMember(GroupMember aMember) {

        if (!members.containsKey(aMember.getMsisdn())) {
            members.put(aMember.getMsisdn(), aMember);
            memberList.addElement(aMember);
            //for adding
            Log.v(TAG, " adding member notification " + aMember);
            if (aMember.getMemberSource() == GroupMember.MemberSource.SELF && chatAlive) {
                if (aMember.isOnHike()) {
                    ModelUtils.getSystemMessage(SYSTEM_MSG_MEMBER_ADDED, aMember.getName(), aMember.getMsisdn(), groupId);
                } else {
                    ModelUtils.getSystemMessage(SYSTEM_MSG_MEMBER_INVITED, aMember.getName(), aMember.getMsisdn(), groupId);
                }
            } else if (aMember.getMemberSource() == GroupMember.MemberSource.SENDER && chatAlive) {
                if (aMember.isOnHike()) {
                    ModelUtils.getSystemMessage(SYSTEM_MSG_MEMBER_ADDED, aMember.getName(), aMember.getMsisdn(), groupId);
                } else {
                    ModelUtils.getSystemMessage(SYSTEM_MSG_MEMBER_INVITED, aMember.getName(), aMember.getMsisdn(), groupId);
                    if (aMember.isDnD().equals(LBL_FALSE)) {
                        ModelUtils.getSystemMessage(SYSTEM_MSG_MEMBER_ADDED, aMember.getName(), aMember.getMsisdn(), groupId);
                    } else if (aMember.isDnD().equals(LBL_TRUE)) {
                        ModelUtils.getSystemMessage(SYSTEM_MSG_WAITING_DND_USERS_PREFIX_GC, aMember.getName(), aMember.getMsisdn(), groupId);
                    }
                }
            }
        } else {
            //rare but handled

            GroupMember member = (GroupMember) members.get(aMember.getMsisdn());
            if (aMember.getMemberSource() == GroupMember.MemberSource.SELF && chatAlive) {
                if (!member.isActive() && aMember.isActive()) {
                    if (aMember.isOnHike()) {
                        ModelUtils.getSystemMessage(SYSTEM_MSG_MEMBER_ADDED, aMember.getName(), aMember.getMsisdn(), groupId);
                    } else {
                        ModelUtils.getSystemMessage(SYSTEM_MSG_MEMBER_INVITED, aMember.getName(), aMember.getMsisdn(), groupId);
                    }
                }
            } else if (aMember.getMemberSource() == GroupMember.MemberSource.SENDER && chatAlive) {
                if (!member.isActive() && aMember.isActive()) {
                     if (aMember.isOnHike()) {
                    ModelUtils.getSystemMessage(SYSTEM_MSG_MEMBER_ADDED, aMember.getName(), aMember.getMsisdn(), groupId);
                } else {
                    ModelUtils.getSystemMessage(SYSTEM_MSG_MEMBER_INVITED, aMember.getName(), aMember.getMsisdn(), groupId);
                    if (aMember.isDnD().equals(LBL_FALSE)) {
                        ModelUtils.getSystemMessage(SYSTEM_MSG_MEMBER_ADDED, aMember.getName(), aMember.getMsisdn(), groupId);
                    } else if (aMember.isDnD().equals(LBL_TRUE)) {
                        ModelUtils.getSystemMessage(SYSTEM_MSG_WAITING_DND_USERS_PREFIX_GC, aMember.getName(), aMember.getMsisdn(), groupId);
                    }
                }
                }//both are active.
                else if (member.isActive() && aMember.isActive()) {
                    if (member.isDnD().equals(EMPTY_STRING) && aMember.isDnD().equals(LBL_TRUE)) {
                        addNotificationAccToHikeStatus(member, aMember,aMember.isDnD());
                    }
                    else if (member.isDnD().equals(EMPTY_STRING) && aMember.isDnD().equals(LBL_FALSE)){
                         addNotificationAccToHikeStatus(member, aMember,aMember.isDnD());
                    }
                    else  if (member.isDnD().equals(LBL_FALSE) && aMember.isDnD().equals(LBL_TRUE)) {
                        addNotificationAccToHikeStatus(member, aMember,aMember.isDnD());
                    }
                    else if (member.isDnD().equals(LBL_TRUE) && aMember.isDnD().equals(LBL_FALSE)){
                         addNotificationAccToHikeStatus(member, aMember,aMember.isDnD());
                    }
                }
            }
            member.setOnHike(aMember.isOnHike());
            member.setDnD(aMember.isDnD());
            member.setActive(true);
        }
    }
    
    /**
     * private utility class to add notification
     * @param member
     * @param aMember
     * @param isDnD 
     */
    private void addNotificationAccToHikeStatus(GroupMember member, GroupMember aMember, String isDnD) {
        if (isDnD.equals(LBL_TRUE)) {
            if (!member.isOnHike() && !aMember.isOnHike()) {
                ModelUtils.getSystemMessage(SYSTEM_MSG_WAITING_DND_USERS_PREFIX_GC, aMember.getName(), aMember.getMsisdn(), groupId);
            } else if (!member.isOnHike() && aMember.isOnHike()) {
                ModelUtils.getSystemMessage(SYSTEM_MSG_MEMBER_ADDED, aMember.getName(), aMember.getMsisdn(), groupId);
            }
        } else {
            if (!member.isOnHike() && !aMember.isOnHike()) {
                ModelUtils.getSystemMessage(SYSTEM_MSG_MEMBER_ADDED, aMember.getName(), aMember.getMsisdn(), groupId);
            } else if (!member.isOnHike() && aMember.isOnHike()) {
                ModelUtils.getSystemMessage(SYSTEM_MSG_MEMBER_ADDED, aMember.getName(), aMember.getMsisdn(), groupId);
            }
        }
    }

    /**
     * remove member from group, sets the status of member as inactive
     * @param msisdn 
     */
    public void removeMember(String msisdn) {
        getMember(msisdn).setActive(false);
        //for removal
        if (chatAlive) {
            ModelUtils.getSystemMessage(SYSTEM_MSG_MEMBER_REMOVED, getMember(msisdn).getName(), msisdn, groupId);
        }
    }

    /**
     * add members to group
     * @param list 
     */
    public void addMembers(Hashtable list) {
        if(list == null) {
            return;
        }
        Enumeration enums = list.keys();
                 
        Log.v(TAG, "size is " + list.size());
        while (enums.hasMoreElements()) {
            String key = enums.nextElement().toString();
                     
            Log.v(TAG, "Key is " + key + " && instance of" + (list.get(key)).getClass().getName());
            addMember((GroupMember) list.get(key));
        }
    }

    /**
     * get member entry for given msisdn
     * @param msisdn
     * @return 
     */
    public GroupMember getMember(String msisdn) {
            return (GroupMember) members.get(msisdn);
    }

    /**
     * 
     * @param msisdn
     * @return whether group has given member
     */
    public boolean hasMember(String msisdn) {
        return msisdn != null && (members.containsKey(msisdn) || createrMsisdn.equals(msisdn));
    }

    /**
     * 
     * @return group name if set else dynamic name based on number of active member 
     */
    public String getName() {
        if (!TextUtils.isEmpty(groupName)) {
            return groupName;
        } else {
            int size = getActiveMembersCount();
            UserDetails selfDtls = AppState.getUserDetails();
            String myName = selfDtls.getName();
            if (size == 0) {
                return myName;
            }else if(size == 1) {
                String groupMemberName = ((GroupMember) getActiveMembersList(false).elementAt(0)).getFirstName();
                return groupMemberName;
            }else if(size == 2) {
                String groupMemberName1 = ((GroupMember) getActiveMembersList(false).elementAt(0)).getFirstName();
                String groupMemberName2 = ((GroupMember) getActiveMembersList(false).elementAt(1)).getFirstName();
                return groupMemberName1 + " & " + groupMemberName2;
            }else {
                String groupMemberName = ((GroupMember) getActiveMembersList(false).elementAt(0)).getFirstName();
                if (groupMemberName == null) {
                    groupMemberName = createrMsisdn;
                }
                return groupMemberName + " & " + (size - 1) + " others";
            }
        }
    }

    /**
     * @return the groupName if set else null
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * @param groupName the groupName to set
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * @return whether chat is active
     */
    public boolean isChatAlive() {
        return chatAlive;
    }

    /**
     * setter for chat state
     * @param chatAlive the chatAlive to set
     */
    public void setChatAlive(boolean chatAlive) {
        this.chatAlive = chatAlive;
        if (this.chatAlive && !chatAlive) {
            ModelUtils.addChat(SYSTEM_MSG_GRP_CHAT_ENDED, new Date().getTime(), groupId, null, 0, ChatModel.MessageStatus.RECEIVED, MqttObjectModel.MessageType.SYSTEM);
        }
    }

    /**
     * 
     * @return count of members active in group
     */
    public int getActiveMembersCount() {
        //This method does not count own number
        boolean hasMe = false;
        int count = 0;
        Hashtable _members = getMembers();
        Enumeration enums = _members.keys();
        while (enums.hasMoreElements()) {
            GroupMember member = (GroupMember) _members.get(enums.nextElement());
            if (member.isActive()) {
                count++;
            }
            if(member.getMsisdn().equals(AppState.getUserDetails().getMsisdn())) {
                hasMe = true;
            }
        }
        return hasMe ? count-1 : count;
    }

    /**
     * 
     * @return count of active sms members in group
     */
    public int getActiveSmsMembersCount() {
        int count = 0;
        Hashtable _members = getMembers();
        Enumeration enums = _members.keys();
        while (enums.hasMoreElements()) {
            GroupMember member = (GroupMember) _members.get(enums.nextElement());
            if (member.isActive() && !member.isOnHike()) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * 
     * @return list of active sms members in group
     */
    public Vector getActiveSmsMembersMsisdn() {
        Vector activeSmsUserList = new Vector();
        Hashtable _members = getMembers();
        Enumeration enums = _members.keys();
        while (enums.hasMoreElements()) {
            GroupMember member = (GroupMember) _members.get(enums.nextElement());
            if (member.isActive() && !member.isOnHike()) {
                activeSmsUserList.addElement(member.getMsisdn());
            }
        }
        return activeSmsUserList;
    }

    public String toString() {
        return "GroupMembers{groupId:" + groupId + ", groupName:" + groupName + ", createrMsisdn:" + createrMsisdn + ", chatAlive:" + chatAlive + ", members:" + TextUtils.toString(members) + "}";
    }
}