package com.hkust.android.hack.flipped.core;

import com.hkust.android.hack.flipped.util.Common;

import java.util.Date;

/**
 * Created by rui on 14-4-26.
 */
public class ActivityMessage {
    private int id;
    private int type;
    private String authorAvatar;
    private String authorName;
    private String time;
    private String content;
    private String location;
    private int meetcnt;

    private Date datetime;

    public static final int MESSAGE_TYPE_MEET_PEOPLE = 1;
    public static final int MESSAGE_TYPE_MEET_STATUS = 1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getMeetcnt() {
        return meetcnt;
    }

    public void setMeetcnt(int meetcnt) {
        this.meetcnt = meetcnt;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void getDatetime(){
        datetime = Common.convertString2Date(this.time);
    }


    public int getHour() {
        if( datetime == null ) getDatetime();
        if( datetime == null ) return 0;
        return datetime.getHours();
    }
    public int getMin() {
        if( datetime == null ) getDatetime();
        if( datetime == null ) return 0;
        return datetime.getMinutes();
    }
}
