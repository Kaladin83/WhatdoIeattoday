package com.example.maratbe.whatdoieattoday.objects;

import android.support.design.circularreveal.CircularRevealHelper;

import java.util.ArrayList;

public class User {
    private int id;
    private int groupId;
    private String eMail;
    private String userName;
    private String groupName;
    private int admin;

    public User(String userName, String eMail, String groupName, int groupId, int admin) {
        this.eMail = eMail;
        this.userName = userName;
        this.groupName = groupName;
        this.groupId = groupId;
        this.admin = admin;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String group) {
        this.groupName = groupName;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getAdmin() {
        return admin;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }
}
