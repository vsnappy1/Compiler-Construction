package com.RandoS;

import java.util.ArrayList;

class LexicalAnalyser {

    //Constants
    private static final String KEYWORD     = "KEYWORD";
    private static final String IDENTIFIER  = "ID";
    private static final String PUNCTUATION = "PUNCTUATION";
    private static final String OPERATOR    = "OPERATOR";
    public static final String INVALID_LEXEME = "--INVALID LEXEME--";
    public static final String CONSTANT_STRING    = "String_Constant";
    public static final String CONSTANT_CHAR      = "char_Constant";
    public static final String CONSTANT_FLOAT     = "float_Constant";
    public static final String CONSTANT_INTEGER   = "int_Constant";
    public static final String CONSTANT_BOOLEAN   = "bool_Constant";

    //This ArrayList will contain Tokens with there Class, Value & LineNumber
    private static ArrayList<Token> Token_list;
    private static ArrayList<Error> Error_list;

    //Character which is being read by LA
    private char mCharacter;
    // For Storing the lexemes
    private String mTemporary = "";
    // Line Number and Character Number
    private int lineNumber = 0 , charNumber = 0;
    // The this contains the Text from file
    private String sourceCode;
    // Check whether number is float or not
    private boolean isFloat = false;

    // Check whether error occurred or not
    private boolean hasErrorOccurred = false;

    LexicalAnalyser(String sourceCode){

        this.sourceCode = sourceCode;

        Token_list = new ArrayList<>();
        Error_list = new ArrayList<>();

        breakWords(sourceCode);

    }

    ArrayList<Token> getToken_list(){
        return Token_list;
    }

    void displayTokens(){
        displayArrayList(Token_list);
    }

    private void breakWords(String sourceCode) {

        // Parse each character
        for (charNumber = 0; charNumber < sourceCode.length(); charNumber++) {

            // Get i'th character
            mCharacter = sourceCode.charAt(charNumber);

            if (mCharacter == '$') {
                mTemporary = String.valueOf(mCharacter);
                addInList(KEYWORD);
                charNumber++;
                continue;
            }

            // Increment the lineNumber if it is a Line Break
            if (mCharacter == '\r') {
                lineNumber++;
            }

            hasErrorOccurred = false;

            // Do further processing if character is not space
            if (mCharacter != ' ') {

                // For Operators
                if (isOPERATOR(mCharacter)) {

                    mTemporary = String.valueOf(mCharacter);
                    // For Double Operators
                    if (shouldCheckNextWord() && sourceCode.charAt(charNumber + 1) == '=') {
                        mTemporary = mCharacter + "=";
                        charNumber++;
                    }
                    // For Increment
                    else if (shouldCheckNextWord() && mCharacter == '+' && sourceCode.charAt(charNumber + 1) == '+') {
                        mTemporary = "++";
                        charNumber++;
                    }
                    // For Decrement
                    else if (shouldCheckNextWord() && mCharacter == '-' && sourceCode.charAt(charNumber + 1) == '-') {
                        mTemporary = "--";
                        charNumber++;
                    }
                    // For Signed Numbers
                    else if (shouldCheckNextWord() && (mCharacter == '+' || mCharacter == '-') && Character.isDigit(sourceCode.charAt(charNumber + 1))) {

                        if (!Token_list.get(Token_list.size() - 1).getClassPart().equals(IDENTIFIER) &&
                                !Token_list.get(Token_list.size() - 1).getClassPart().equals(CONSTANT_INTEGER) &&
                                !Token_list.get(Token_list.size() - 1).getClassPart().equals(CONSTANT_FLOAT)) {
                            addNumber();
                        }
                    }
                    else if (shouldCheckNextWord() && mCharacter == '&' && sourceCode.charAt(charNumber + 1) == '&') {
                        mTemporary = "&&";
                        charNumber++;
                    }
                    else if (shouldCheckNextWord() && mCharacter == '|' && sourceCode.charAt(charNumber + 1) == '|') {
                        mTemporary = "&&";
                        charNumber++;
                    }
                    // For Comments
                    else if (mCharacter == '/') {
                        // For single line
                        if (sourceCode.charAt(charNumber + 1) == '/') {
                            mTemporary = "";
                            while (shouldCheckNextWord() && sourceCode.charAt(charNumber + 1) != '\r') {
                                charNumber++;
                            }
                            if (sourceCode.charAt(charNumber + 1) == '\r') {
                                lineNumber++;
                                charNumber++;
                            }
                        }
                        // For Multiline
                        else if (sourceCode.charAt(charNumber + 1) == '*') {
                            mTemporary = "";
                            charNumber++;
                            while (shouldCheckNextWord() && sourceCode.charAt(charNumber + 1) != '*' && sourceCode.charAt(charNumber + 2) != '/') {
                                if (sourceCode.charAt(charNumber + 1) == '\r') {
                                    lineNumber++;
                                    charNumber++;
                                }
                                charNumber++;
                            }
                            charNumber += 2;
                        }
                    }
                    addInList(OPERATOR);
                }

                // For Punctuations
                else if (isPUNCTUATION(mCharacter)) {

                    mTemporary = String.valueOf(mCharacter);

                    // For float that start with .
                    if (mCharacter == '.' && !Character.isLetter(sourceCode.charAt(charNumber + 1))) {
                        addNumber();
                    }

                    // For Char Constant
                    if (mCharacter == '\'') {
                        mTemporary = "";
                        if (shouldCheckNextWord()) {
                            mTemporary += sourceCode.charAt(++charNumber);
                            if (shouldCheckNextWord() && sourceCode.charAt(charNumber + 1) == '\'') {
                                ++charNumber;
                            } else if (shouldCheckNextWord() && sourceCode.charAt(charNumber + 2) == '\'') { //for \n,\r,\\ etc..
                                mTemporary += sourceCode.charAt(++charNumber);
                                ++charNumber;
                            } else {
                                addError("close quote missing");
                                addInList(INVALID_LEXEME);
                                continue;
                            }
                        } else {
                            addError("empty literal");
                            addInList(INVALID_LEXEME);
                            continue;
                        }
                        addInList(CONSTANT_CHAR);
                    }

                    // For String Constant
                    else if (mCharacter == '"') {
                        boolean isError = false;
                        mTemporary = "";
                        while (shouldCheckNextWord() && sourceCode.charAt(charNumber + 1) != '"') {

                            if (sourceCode.charAt(charNumber + 1) == '\r') {
                                addError("closing quote missing");
                                isError = true;
                                addInList(INVALID_LEXEME);
                                hasErrorOccurred = true;
                                lineNumber++;
                                break;
                            }

                            addInTemporary(sourceCode.charAt(++charNumber));
                        }
                        charNumber++;


                        addInList(CONSTANT_STRING);
                        if (isError) {
                            addError(INVALID_LEXEME);
                        }

                    }

                    //For Other cases
                    else {
                        addInList(PUNCTUATION);
                    }
                }

                //For Numbers
                else if (Character.isDigit(mCharacter)) {

                    addNumber();
                }


                // For Keyword and Identifiers
                else if (Character.isAlphabetic(mCharacter) || mCharacter == '_') {

                    boolean isID = true;

                    // For checking true and false
                    if (mCharacter == 't') {
                        if (sourceCode.charAt(charNumber + 1) == 'r')
                            if (sourceCode.charAt(charNumber + 2) == 'u')
                                if (sourceCode.charAt(charNumber + 3) == 'e') {
                                    charNumber += 3;
                                    mTemporary = "true";
                                    addInList(CONSTANT_BOOLEAN);
                                    isID = false;
                                    mCharacter = '\n';
                                }
                    }
                    else if (mCharacter == 'f') {
                        if (sourceCode.charAt(charNumber + 1) == 'a')
                            if (sourceCode.charAt(charNumber + 2) == 'l')
                                if (sourceCode.charAt(charNumber + 3) == 's')
                                    if (sourceCode.charAt(charNumber + 4) == 'e') {
                                        charNumber += 4;
                                        mTemporary = "false";
                                        addInList(CONSTANT_BOOLEAN);
                                        isID = false;
                                        mCharacter = '\n';
                                    }
                    }

                    if (isID) {
                        addCharacterToVariable(mCharacter);
                        while (shouldCheckNextWord() && !isWordBreaker(sourceCode.charAt(charNumber + 1))) {

                            addCharacterToVariable(sourceCode.charAt(++charNumber));
                        }

                        if (mTemporary.length() != 0) {

                            //For identifier and keywords
                            if (isKEY_WORD(mTemporary)) {
                                addInList(KEYWORD);
                            } else if (Character.isAlphabetic(mTemporary.charAt(0))) {
                                if (validateIdentifier(mTemporary)) {
                                    addInList(IDENTIFIER);
                                } else {
                                    addError("Incorrect Identifier");
                                    addInList(INVALID_LEXEME);
                                }

                            } else if (mTemporary.charAt(0) == '_' && Character.isLetterOrDigit(mTemporary.charAt(1))) {
                                addInList(IDENTIFIER);
                            } else {
                                addError("Identifier Error");
                                addInList(INVALID_LEXEME);
                            }
                        }
                    } else {
                        if (mCharacter != '\n' && mCharacter != '\r' && mCharacter != '\t') {
                            addCharacterToVariable(mCharacter);
                            addError("unknown literal");
                            addInList(INVALID_LEXEME);
                        }
                    }
                }
            }
        }
    }

    private void addNumber(){
        isFloat = false;
        mTemporary = String.valueOf(mCharacter);

        if(mCharacter == '.'){
            isFloat = true;
        }

        while (charNumber+1 < sourceCode.length() && (!isWordBreaker(sourceCode.charAt(charNumber+1)) || !isFloat)){

            //For Float
            if(sourceCode.charAt(charNumber+1) == '.'){
                if( Character.isDigit(sourceCode.charAt(charNumber+2))){
                    isFloat=true;
                }
                else {
                    break;
                }
            }

            else if(isWordBreaker(sourceCode.charAt(charNumber+1))){
                break;
            }
            addCharacterToVariable(sourceCode.charAt(++charNumber));
        }

        validateNumber(mTemporary);

        if(isFloat){
            addInList(CONSTANT_FLOAT);
        }
        else {
            addInList(CONSTANT_INTEGER);
        }
    }

    private void addInTemporary(char c){

        if(c != '\n' && c != '\r' && c != '\t'){
            mTemporary += c;
        }

    }

    private boolean validateIdentifier(String str){

        for(int i = 0; i < str.length() ; i++){
            if(str.charAt(i) != '_' && !Character.isLetterOrDigit(str.charAt(i))){
                return false;
            }
        }
        return true;
    }

    private boolean validateNumber(String str){
        isFloat = false;
        boolean isInteger = true;
        hasErrorOccurred = false;
        boolean isSignedNumber = false;

        for (int i = 0; i< str.length() ; i++){


            if(str.charAt(i) == '.' && isFloat){
                addError("incorrect float");
                return false;
            }
            else if(str.charAt(i) == '.'){
                isFloat = true;
                isInteger = false;
            } else if (str.charAt(i) == '+' || str.charAt(i) == '-') {
                isSignedNumber = true;

            } else if (!Character.isDigit(str.charAt(i))) {
                hasErrorOccurred = true;
            }
        }

        if(hasErrorOccurred){
            if(isFloat)
                addError("incorrect float");
            else if(isInteger)
                addError("incorrect integer");
            return false;
        }

        return true;
    }

    private void addInList(String ClassPart){

        if(mTemporary.length()  != 0 ){

            if(ClassPart.equals(KEYWORD) || ClassPart.equals(PUNCTUATION) || ClassPart.equals(OPERATOR)){
                Token_list.add(new Token(mTemporary,getClassPart(mTemporary),lineNumber));
            }
            else {
                Token_list.add(new Token(mTemporary,ClassPart,lineNumber));
            }
            mTemporary = "";

        }
    }

    private boolean shouldCheckNextWord(){

        return charNumber + 1 != sourceCode.length();
    }

    private void addCharacterToVariable(char c){

        if(c != '\n' && c != '\r' && c != '\t'){
            mTemporary += c;
        }
    }

    private boolean isWordBreaker(char c){

        return isPUNCTUATION(c) || isOPERATOR(c) || c == ' ' || c == '\r';
    }

    private boolean isPUNCTUATION(char c){

        switch (c){
            case '.':
            case '(':
            case ')':
            case '{':
            case '}':
            case '[':
            case ']':
            case ';':
            case '"':
            case '\'':
            case ',':
                return true;
            default:
                return false;
        }
    }

    private boolean isOPERATOR(char c){

        switch (c) {
            case '>':
            case '<':
            case '=':
            case '+':
            case '-':
            case '*':
            case '/':
            case '!':
            case '&':
            case '|':
                return true;
            default:
                return false;
        }

    }

    private boolean isKEY_WORD(String str){

        switch (str){
            case "true":
            case "false":
            case "void":
            case "main":
            case "class":
            case "interface":
            case "continue":
            case "break":
            case "return":
            case "private":
            case "public":
            case "protected":
            case "extends":
            case "abstract":
            case "sealed":
            case "final":
            case "new":
            case "static":
            case "int":
            case "float":
            case "char":
            case "String":
            case "for":
            case "if":
            case "else":
            case "bool":
                return true;
            default:
                return false;
        }
    }

    private static void displayArrayList(ArrayList<Token> list){
        System.out.println("Tokens : " + Token_list.size() +  "\t Errors : " + Error_list.size());
        String error;
        int errorNumber = 0;

        System.out.println("Line#           Token           Class");
        for (int i = 0 ; i < list.size() ; i++){

            error = "";
            if(  errorNumber < Error_list.size() &&  i == Error_list.get(errorNumber).getTokenNumber() ){
                error = Error_list.get(errorNumber).getErrorType();
                errorNumber++;
            }

            System.out.println(
                    list.get(i).getLineNumber()+"\t\t\t\t\t"+
                            list.get(i).getValuePart() +"\t\t\t\t\t" +
                            list.get(i).getClassPart() +"\t\t\t\t\t" +
                            error);

        }
    }

    private void addError(String msg){
        // System.out.println("\n"+"ERROR Line#:"+lineNumber+ "   "+msg+"\n");
        // isErrorOccurred = true;
        Error_list.add(new Error("*"+msg+"*",Token_list.size()));
        hasErrorOccurred = true;
    }

    private String getClassPart(String Token){

        switch (Token){

            // End marker
            case "$":
                return "$";

            //KEYWORDS
            case "public":
            case "private":
            case "protected":
                return "AM";

            case "int":
                return "int";
            case "float":
                return "float";
            case "char":
                return "char";
            case "bool":
                return "bool";

            case "interface":
                return "INTERFACE";

            case "class":
                return "CLASS";

            case "for":
                return "FOR";

            case "abstract":
                return "ABSTRACT";

            case "sealed":
                return "SEALED";

            case "static":
                return "STATIC";

            case "extends":
                return "INH";

            case "return":
                return "RETURN";

            case "if":
                return "IF";

            case "else":
                return "ELSE";

            case "break":
                return "BREAK";

            case "continue":
                return "CONTINUE";

            case "void":
                return "VOID";

            case "main":
                return "MAIN";

            case "final":
                return "FINAL";

            case "array":
                return "ARRAY";

            case "new":
                return "NEW";

            case "String":
                return "String";

            case "override":
                return "OVERRIDE";

            //OPERATORS
            case "=":
            case "+=":
            case "-=":
            case "*=":
            case "/=":
                return "ASGN_OP";

            case ">":
            case "<":
            case ">=":
            case "<=":
            case "==":
            case "!=":
                return "RELATIONAL_OP";

            case "&&":
                return "&&";
            case "||":
                return "||";
            case "!":
                return "!";

            case "+":
            case "-":
                return "PM";

            case "*":
            case "/":
                return "MD";

            case "++":
            case "--":
                return "PPMM";


            //PUNCTUATIONS
            case ".":
                return ".";

            case "(":
                return "(";

            case ")":
                return ")";

            case "{":
                return "{";

            case "}":
                return "}";

            case "[":
                return "[";

            case "]":
                return "]";

            case ";":
                return ";";

            case ",":
                return ",";


            //ERROR
            default:
                return "UNKNOWN LITERAL";
        }
    }
}
