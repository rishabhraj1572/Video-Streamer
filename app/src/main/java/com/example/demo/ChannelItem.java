package com.example.demo;

public class ChannelItem {
    private String mChannelName;
    private String mChannelImage;
    private String mChannelLink,mType,mLicenseUrl,mOrigin,mReferer,mCookie,mUserAgent;

    public ChannelItem(String imgUrl, String channelName,String channelLink,String type,String licenseUrl,String origin,String referer,String cookie,String userAgent){
        mChannelName=channelName;
        mChannelImage=imgUrl;
        mChannelLink=channelLink;
        mType=type;
        mLicenseUrl=licenseUrl;
        mOrigin=origin;
        mReferer=referer;
        mCookie=cookie;
        mUserAgent=userAgent;
    }

    public String getImageUrl(){
        return mChannelImage;
    }
    public String getChannelName(){
        return  mChannelName;
    }

    public String getChannelLink() {
        return mChannelLink;
    }

    public String getType() {
        return mType;
    }

    public String getLicenseUrl() {
        return mLicenseUrl;
    }

    public String getOrigin() {
        return mOrigin;
    }

    public String getReferer() {
        return mReferer;
    }

    public String getCookie() {
        return mCookie;
    }

    public String getUserAgent() {
        return mUserAgent;
    }
}
