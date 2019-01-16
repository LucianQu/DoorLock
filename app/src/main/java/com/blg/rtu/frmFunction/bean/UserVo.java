package com.blg.rtu.frmFunction.bean;

import java.io.Serializable;

public class UserVo implements Serializable {
    private int birthday;
    private String countryCode;
    private long createDate;
    private int gender;
    private String imageUrl;
    private String loginName;
    private int loginType;
    private int notice;
    private int noticeMode;
    private String phone;
    private String remark;
    private long tid;
    private String userName;

    public int getBirthday()
    {
        return this.birthday;
    }

    public String getCountryCode()
    {
        return this.countryCode;
    }

    public long getCreateDate()
    {
        return this.createDate;
    }

    public int getGender()
    {
        return this.gender;
    }

    public String getImageUrl()
    {
        return this.imageUrl;
    }

    public String getLoginName()
    {
        return this.loginName;
    }

    public int getLoginType()
    {
        return this.loginType;
    }

    public int getNotice()
    {
        return this.notice;
    }

    public int getNoticeMode()
    {
        return this.noticeMode;
    }

    public String getPhone()
    {
        return this.phone;
    }

    public String getRemark()
    {
        return this.remark;
    }

    public long getTid()
    {
        return this.tid;
    }

    public String getUserName()
    {
        return this.userName;
    }

    public void setBirthday(int paramInt)
    {
        this.birthday = paramInt;
    }

    public void setCountryCode(String paramString)
    {
        this.countryCode = paramString;
    }

    public void setCreateDate(long paramLong)
    {
        this.createDate = paramLong;
    }

    public void setGender(int paramInt)
    {
        this.gender = paramInt;
    }

    public void setImageUrl(String paramString)
    {
        this.imageUrl = paramString;
    }

    public void setLoginName(String paramString)
    {
        this.loginName = paramString;
    }

    public void setLoginType(int paramInt)
    {
        this.loginType = paramInt;
    }

    public void setNotice(int paramInt)
    {
        this.notice = paramInt;
    }

    public void setNoticeMode(int paramInt)
    {
        this.noticeMode = paramInt;
    }

    public void setPhone(String paramString)
    {
        this.phone = paramString;
    }

    public void setRemark(String paramString)
    {
        this.remark = paramString;
    }

    public void setTid(long paramLong)
    {
        this.tid = paramLong;
    }

    public void setUserName(String paramString)
    {
        this.userName = paramString;
    }
}
