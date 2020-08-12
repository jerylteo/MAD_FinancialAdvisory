package edu.nyp.financialadvisory;

public class Record {

    private String type;
    private String category;
    private float amt;

    public Record(String t, String c, float a) {
        type = t;
        category = c;
        amt = a;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public float getAmt() {
        return amt;
    }

    public void setAmt(float amt) {
        this.amt = amt;
    }
}
