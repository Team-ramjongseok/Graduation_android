package com.example.graduation_android;

public class RecyclerViewItem {
    private int mCafeId;
    private String mCafeName;
    private String mEmptyseat;

    public int getCafeId() {
        return mCafeId;
    }
    public void setCafeId(int cafeId) {
        this.mCafeId = cafeId;
    }

    public String getCafeName() {
        return mCafeName;
    }
    public void setCafeName(String cafeName) {
        this.mCafeName = cafeName;
    }

    public String getEmptyseat() {
        return mEmptyseat;
    }
    public void setEmptyseat(String emptyseat) {
        this.mEmptyseat = emptyseat;
    }
}
