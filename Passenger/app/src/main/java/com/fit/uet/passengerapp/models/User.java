package com.fit.uet.passengerapp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bien-kun on 09/03/2017.
 */

public class User implements Parcelable {
    private String uid;
    private String name;
    private String phoneNum;
    private int point;
    private String coachUid;

    public User() {}

    public User(String uid, String name, String phoneNum, int point) {
        this.uid = uid;
        this.name = name;
        this.phoneNum = phoneNum;
        this.point = point;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getCoachUid() {
        return coachUid;
    }

    public void setCoachUid(String coachUid) {
        this.coachUid = coachUid;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return null;
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
