package com.RandoS;

public class Error {

    private String errorType;
    private int tokenNumber;

    Error(String errorType, int tokenNumber){
        this.errorType = errorType;
        this.tokenNumber = tokenNumber;
    }

    String getErrorType(){
        return errorType;
    }

    int getTokenNumber(){
        return tokenNumber;
    }
}
