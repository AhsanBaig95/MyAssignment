package com.example.myassignment.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserStatus {
    @SerializedName("status_filename")
    @Expose
    private String statusFilename;
    @SerializedName("status_id")
    @Expose
    private Integer statusId;
    @SerializedName("status_type")
    @Expose
    private String statusType;
    @SerializedName("user_id")
    @Expose
    private Integer userId;

    @SerializedName("user_name")
    @Expose
    private String userName;

    public UserStatus(String statusFilename, Integer statusId, String statusType, String userName) {
        this.statusFilename = statusFilename;
        this.statusId = statusId;
        this.statusType = statusType;
        this.userName = userName;
    }

    public String getStatusFilename() {
        return statusFilename;
    }

    public void setStatusFilename(String statusFilename) {
        this.statusFilename = statusFilename;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public String getStatusType() {
        return statusType;
    }

    public void setStatusType(String statusType) {
        this.statusType = statusType;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
