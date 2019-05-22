package com.example.maratbe.whatdoieattoday.objects;

import android.view.View;

import java.util.ArrayList;

public class Group {
    private int groupId;
    private String groupName;
    private String adminName;

    public Group(int groupId, String groupName, String adminName) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.adminName = adminName;
    }

    public int getGroupId() {
        return groupId;

    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }
}
