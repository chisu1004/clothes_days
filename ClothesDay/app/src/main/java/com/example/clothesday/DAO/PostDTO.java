package com.example.clothesday.DAO;



public class PostDTO {

    private int PO_ID;

    private String PO_CON;
    private String PO_ME_ID;
    private int PO_LIKE;
    private String PO_TAG;
    private String PO_CATE;
    private String PO_PIC;
    private String PO_REG_DA;


    public PostDTO() {
    }




    public String getPO_CON() {
        return PO_CON;
    }

    public void setPO_CON(String pO_CON) {
        PO_CON = pO_CON;
    }

    public String getPO_ME_ID() {
        return PO_ME_ID;
    }

    public void setPO_ME_ID(String pO_ME_ID) {
        PO_ME_ID = pO_ME_ID;
    }

    public int getPO_LIKE() {
        return PO_LIKE;
    }

    public void setPO_PIC(String pO_PIC) {
        PO_PIC = pO_PIC;
    }

    public String getPO_PIC() {
        return PO_PIC;
    }

    public void setPO_LIKE(int pO_LIKE) {
        PO_LIKE = pO_LIKE;
    }

    public String getPO_TAG() {
        return PO_TAG;
    }

    public void setPO_TAG(String pO_TAG) {
        PO_TAG = pO_TAG;
    }

    public String getPO_CATE() {
        return PO_CATE;
    }

    public void setPO_CATE(String pO_CATE) {
        PO_CATE = pO_CATE;
    }

    public  int getPO_ID() {
        return PO_ID;
    }

    public void setPO_ID( int pO_ID) {
        PO_ID = pO_ID;
    }

    public String getPO_REG_DA() {
        return PO_REG_DA;
    }

    public void setPO_REG_DA(String pO_REG_DA) {
        PO_REG_DA = pO_REG_DA;
    }

}