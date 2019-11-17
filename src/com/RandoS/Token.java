package com.RandoS;

public class Token {

    private String mValuePart;
    private String mClassPart;
    private int mLineNumber;

    //Constructor
    Token(String valuePart, String classPart, int lineNumber){

        mValuePart = valuePart;
        mClassPart = classPart;
        mLineNumber = lineNumber;
    }

    //Getter Method
    String getValuePart(){
        return  mValuePart;
    }

    String getClassPart(){
        return mClassPart;
    }

    int getLineNumber(){
        return mLineNumber;
    }

}
