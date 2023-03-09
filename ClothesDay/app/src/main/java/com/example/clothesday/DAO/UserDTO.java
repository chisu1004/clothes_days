package com.example.clothesday.DAO;

public class UserDTO {
    private String ME_ID;
    private String ME_PW;
    private String ME_NICK;
    private String ME_PIC;
    private String ME_TEXT;


    public UserDTO() {
    }


    public String getME_ID() {
        return ME_ID;
    }
    public void setME_ID(String mE_ID) {
        ME_ID = mE_ID;
    }
    public String getME_PW() {
        return ME_PW;
    }
    public void setME_PW(String mE_PW) {
        ME_PW = mE_PW;
    }
    public String getME_NICK() {
        return ME_NICK;
    }
    public void setME_NICK(String ME_NICK) {
        this.ME_NICK = ME_NICK;
    }


    public String getME_PIC() {
        return ME_PIC;
    }

    public void setME_PIC(String mE_PIC) {
        ME_PIC = mE_PIC;
    }

    public String getME_TEXT() {
        return ME_TEXT;
    }

    public void setME_TEXT(String mE_TEXT) {
        ME_TEXT = mE_TEXT;
    }


}