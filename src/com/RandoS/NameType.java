package com.RandoS;

public class NameType {

    private String mName;
    private String mType;

    public NameType(String Name, String Type){
        mName = Name;
        mType = Type;
    }

    public String getName(){
        return mName;
    }

    public String getType(){
        return mType;
    }
}
