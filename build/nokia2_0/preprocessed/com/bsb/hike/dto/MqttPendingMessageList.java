/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.dto;

/**
 *list for pending mqtt messages
 * @author Ankit Yadav
 */
public class MqttPendingMessageList extends DataCollection {

    private final static MqttPendingMessageList list = new MqttPendingMessageList();

    private MqttPendingMessageList() {
    }

    public static MqttPendingMessageList getInstance() {
        return list;
    }
}
