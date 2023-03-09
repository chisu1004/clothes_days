package com.example.clothesday.DAO;

import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.Statement;


public class SearchDTO {
    int SE_ID;
    String SE_CON;
    String SE_DA;
    String SE_ME_ID;


    public int getSE_ID() {
        return SE_ID;
    }
    public void setSE_ID(int sE_ID) {
        SE_ID = sE_ID;
    }
    public String getSE_CON() {
        return SE_CON;
    }
    public void setSE_CON(String sE_CON) {
        SE_CON = sE_CON;
    }
    public String getSE_DA() {
        return SE_DA;
    }
    public void setSE_DA(String sE_DA) {
        SE_DA = sE_DA;
    }
    public String getSE_ME_ID() {
        return SE_ME_ID;
    }
    public void setSE_ME_ID(String sE_ME_ID) {
        SE_ME_ID = sE_ME_ID;
    }


}
