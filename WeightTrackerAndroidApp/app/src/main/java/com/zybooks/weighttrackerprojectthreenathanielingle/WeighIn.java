package com.zybooks.weighttrackerprojectthreenathanielingle;

public class WeighIn {
    private long mId;
    private String mDate;
    private float mWeight;

    public WeighIn() {
        mId = -1;
        mDate = "";
        mWeight = -1;
    }

    public WeighIn(long id, String date, float weight) {
        mId = id;
        mDate = date;
        mWeight = weight;
    }

    public long getId() {
        return mId;
    }

    public String getDate() {
        return mDate;
    }

    public float getWeight() {
        return mWeight;
    }

    public void setWeight(float weight) {
        mWeight = weight;
    }
}
