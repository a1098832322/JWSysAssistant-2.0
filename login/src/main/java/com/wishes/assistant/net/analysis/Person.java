package com.wishes.assistant.net.analysis;

import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by 10988 on 2017/4/27.
 */

public class Person {
    private static Person person = null;

    //学号，姓名，打卡次数，
    private String mNumber;
    private String mName;
    private String mcount;
    private String mTall;
    private String mWeight;
    private String mLung;
    private String m50;
    private String mJump;
    private String mLongRun;
    private String mFlexion;
    private String mUp;

    public String getFinalScoller() {
        return finalScoller;
    }

    public void setFinalScoller(String finalScoller) {
        this.finalScoller = finalScoller;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    //总成绩和等级
    private String finalScoller;
    private String level;

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getMcount() {
        return mcount;
    }

    public void setMcount(String mcount) {
        Log.e(TAG, "setMcount: 打卡次数：" + mcount);
        this.mcount = mcount;
    }

    public String getmTall() {
        return mTall;
    }

    public void setmTall(String mTall) {
        this.mTall = mTall;
    }

    public String getmWeight() {
        return mWeight;
    }

    public void setmWeight(String mWeight) {
        this.mWeight = mWeight;
    }

    public String getmLung() {
        return mLung;
    }

    public void setmLung(String mLung) {
        this.mLung = mLung;
    }

    public String getM50() {
        return m50;
    }

    public void setM50(String m50) {
        this.m50 = m50;
    }

    public String getmJump() {
        return mJump;
    }

    public void setmJump(String mJump) {
        this.mJump = mJump;
    }

    public String getmLongRun() {
        return mLongRun;
    }

    public void setmLongRun(String mLongRun) {
        this.mLongRun = mLongRun;
    }

    public String getmFlexion() {
        return mFlexion;
    }

    public void setmFlexion(String mFlexion) {
        this.mFlexion = mFlexion;
    }

    public String getmUp() {
        return mUp;
    }

    public void setmUp(String mUp) {
        this.mUp = mUp;
    }


    public String getmNumber() {
        return mNumber;
    }

    public void setmNumber(String mNumber) {
        this.mNumber = mNumber;
    }


    public static Person getPerson() {
        return person;
    }

    public static void setPerson(Person person) {
        Person.person = person;
    }

    public static Person getInstance() {
        if (person == null) {
            person = new Person();
        }
        return person;
    }
}
