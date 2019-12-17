package com.RandoS;

import java.util.ArrayList;

public class SyntaxAnalyser {

    private static int TokenNumber;
    private ArrayList<Token> TokenSet;
    private boolean isFirstTime = true;

    public SyntaxAnalyser(ArrayList<Token> TokenSet) {

        TokenNumber = 0;
        this.TokenSet = TokenSet;

        start();
    }

    boolean start() {
        if (TokenSet.get(TokenNumber).getClassPart().equals("CLASS")) {
            TokenNumber++;
            if (TokenSet.get(TokenNumber).getClassPart().equals("ID")) {
                TokenNumber++;
                if (INH()) {
                    if (TokenSet.get(TokenNumber).getClassPart().equals("{")) {
                        TokenNumber++;
                        if (Class_Body()) {
                            if (TokenSet.get(TokenNumber).getClassPart().equals("MAIN")) {
                                TokenNumber++;
                                if (TokenSet.get(TokenNumber).getClassPart().equals("(")) {
                                    TokenNumber++;
                                    if (TokenSet.get(TokenNumber).getClassPart().equals(")")) {
                                        TokenNumber++;
                                        if (TokenSet.get(TokenNumber).getClassPart().equals("{")) {
                                            TokenNumber++;
                                            if (MST()) {
                                                if (TokenSet.get(TokenNumber).getClassPart().equals("}")) {
                                                    TokenNumber++;
                                                    if (Class_Body()) {
                                                        if (TokenSet.get(TokenNumber).getClassPart().equals("}")) {
                                                            TokenNumber++;
                                                            if (Defs()) {
                                                                return true;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return returnFalseReportError();
    }

    private boolean Defs() {
        if (TokenSet.get(TokenNumber).getClassPart().matches("ABSTRACT|STATIC||SEALED|CLASS|INTERFACE")) {
            if (Cat()) {
                if (Class_Interface_Def())
                    if (Defs()) {
                        return true;
                    }
            }
        } else if (TokenSet.get(TokenNumber).getClassPart().equals("$")) {
            TokenNumber++;
            return true;
        }
        return false;
    }

    private boolean Class_Interface_Def() {
        if (TokenSet.get(TokenNumber).getClassPart().equals("CLASS")) {
            TokenNumber++;
            if (TokenSet.get(TokenNumber).getClassPart().equals("ID")) {
                TokenNumber++;
                if (INH()) {
                    if (TokenSet.get(TokenNumber).getClassPart().equals("{")) {
                        TokenNumber++;
                        if (Class_Body()) {
                            if (TokenSet.get(TokenNumber).getClassPart().equals("}")) {
                                TokenNumber++;
                                return true;
                            }
                        }
                    }
                }
            }
        } else if (TokenSet.get(TokenNumber).getClassPart().equals("INTERFACE")) {
            TokenNumber++;
            if (TokenSet.get(TokenNumber).getClassPart().equals("ID")) {
                TokenNumber++;
                if (INH()) {
                    if (TokenSet.get(TokenNumber).getClassPart().equals("{")) {
                        TokenNumber++;
                        if (Interface_Body()) {
                            if (TokenSet.get(TokenNumber).getClassPart().equals("}")) {
                                TokenNumber++;
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean Interface_Body() {
        if (TokenSet.get(TokenNumber).getClassPart().matches("AM|STATIC|FINAL|int|float|String|char|bool|VOID")) {
            if (AM()) {
                if (Static()) {
                    if (Final()) {
                        if (Return_Type()) {
                            if (TokenSet.get(TokenNumber).getClassPart().equals("ID")) {
                                TokenNumber++;
                                if (TokenSet.get(TokenNumber).getClassPart().equals("(")) {
                                    TokenNumber++;
                                    if (Init_Parameter_List()) {
                                        if (TokenSet.get(TokenNumber).getClassPart().equals(")")) {
                                            TokenNumber++;
                                            if (TokenSet.get(TokenNumber).getClassPart().equals(";")) {
                                                TokenNumber++;
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean Return_Type() {
        if (TokenSet.get(TokenNumber).getClassPart().matches("int|float|String|char|bool|ID")) {
            if (DT_All()) {
                return true;
            }
        } else if (TokenSet.get(TokenNumber).getClassPart().equals("VOID")) {
            TokenNumber++;
            return true;
        }
        return false;
    }

    private boolean Cat() {
        if (TokenSet.get(TokenNumber).getClassPart().matches("STATIC|ABSTRACT|SEALED")) {
            TokenNumber++;
            return true;
        } else if (TokenSet.get(TokenNumber).getClassPart().matches("CLASS|INTERFACE")) {
            return true;
        }
        return false;
    }

    private boolean Class_Body() {
        if (TokenSet.get(TokenNumber).getClassPart().matches("AM|STATIC|FINAL|VOID|int|float|String|char|bool|ID")) {
            if (AM()) {
                if (Static()) {
                    if (Final()) {
                        if (Var_Fun_Dec()) {
                            if (Class_Body()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        else if (TokenSet.get(TokenNumber).getClassPart().equals("MAIN"))
            return true;
        else if (TokenSet.get(TokenNumber).getClassPart().equals("}"))
            return true;

        return false;
    }

    private boolean Var_Fun_Dec() {
        if (TokenSet.get(TokenNumber).getClassPart().equals("VOID")) {
            TokenNumber++;
            if (TokenSet.get(TokenNumber).getClassPart().matches("ID")) {
                TokenNumber++;
                if (TokenSet.get(TokenNumber).getClassPart().equals("(")) {
                    TokenNumber++;
                    if (Init_Parameter_List()) {
                        if (TokenSet.get(TokenNumber).getClassPart().equals(")")) {
                            TokenNumber++;
                            if (TokenSet.get(TokenNumber).getClassPart().equals("{")) {
                                TokenNumber++;
                                if (MST()) {
                                    if (TokenSet.get(TokenNumber).getClassPart().equals("}")) {
                                        TokenNumber++;
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (TokenSet.get(TokenNumber).getClassPart().matches("int|float|String|char|bool")) {
            if (DT_All()) {
                if (TokenSet.get(TokenNumber).getClassPart().equals("ID")) {
                    TokenNumber++;
                    if (PLD()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean PLD() {
        if (TokenSet.get(TokenNumber).getClassPart().matches(",|ASGN_OP|;") ||
                TokenSet.get(TokenNumber).getClassPart().equals("(")) {

            if (TokenSet.get(TokenNumber).getClassPart().equals("(")) {
                TokenNumber++;
                if (Init_Parameter_List()) {
                    if (TokenSet.get(TokenNumber).getClassPart().equals(")")) {
                        TokenNumber++;
                        if (TokenSet.get(TokenNumber).getClassPart().equals("{")) {
                            TokenNumber++;
                            if (MST()) {
                                if (TokenSet.get(TokenNumber).getClassPart().equals("}")) {
                                    TokenNumber++;
                                    return true;
                                }
                            }
                        }
                    }
                }
            } else if (DA()) {
                if (TokenSet.get(TokenNumber).getClassPart().equals(";")) {
                    TokenNumber++;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean DT_Pri(){

        if (TokenSet.get(TokenNumber).getClassPart().matches("int|float|String|char|bool")){
            TokenNumber++;
            return true;
        }
        return false;
    }

    private boolean DA() {
        if (TokenSet.get(TokenNumber).getClassPart().matches(",|ASGN_OP")) {
            if (One_More_DA()) {
                return true;
            } else if (TokenSet.get(TokenNumber).getClassPart().equals("ASGN_OP")) {
                TokenNumber++;
                if (SAO()) {
                    if (One_More_DA())
                        return true;
                }
            }
        }
        else if(TokenSet.get(TokenNumber).getClassPart().equals(";")) {
            return true;
        }
        return false;
    }

    private boolean SAO() {
        if (TokenSet.get(TokenNumber).getClassPart().matches("ID|int_Constant|float_Constant|String_Constant|char_Constant|bool_Constant|PPMM|!|NEW")) {
            if (OE())
                return true;
            else if (TokenSet.get(TokenNumber).getClassPart().equals("NEW")) {
                TokenNumber++;
                if (AO())
                    return true;
            }
        }
        return false;
    }

    private boolean One_More_DA() {
        if(TokenSet.get(TokenNumber).getClassPart().equals(",")) {
            TokenNumber++;
            if(TokenSet.get(TokenNumber).getClassPart().equals("ID")) {
                TokenNumber++;
                if(DA())
                    return true;
            }
        }
        else if(TokenSet.get(TokenNumber).getClassPart().equals(";")) {
            return true;
        }
        return false;
    }

    private boolean DT_All() {
        if (TokenSet.get(TokenNumber).getClassPart().matches("int|float|String|char|bool|ID")) {
            if (PNP()) {
                if (Array())
                    return true;
            }
        }
        return false;
    }

    private boolean Array() {
        if(TokenSet.get(TokenNumber).getClassPart().equals("[")) {
            TokenNumber++;
            if(TokenSet.get(TokenNumber).getClassPart().equals("]")) {
                TokenNumber++;
                return true;
            }
        }
        else if(TokenSet.get(TokenNumber).getClassPart().equals("ID")) {
            return true;
        }

       return false;
    }

    private boolean PNP() {
        if (TokenSet.get(TokenNumber).getClassPart().matches("int|float|String|char|bool|ID")) {
            if (DT_Pri()) {
                return true;
            }
            else if (TokenSet.get(TokenNumber).getClassPart().equals("ID")) {
                TokenNumber++;
                return true;
            }
        }
        return false;
    }

    private boolean MST() {
        if (TokenSet.get(TokenNumber).getClassPart().matches("int|float|String|char|bool|ID|FOR|IF|BREAK|CONTINUE|RETURN")) {
            if (SST()) {
                if (MST()) {
                    return true;
                }
            }
        }
        else if(TokenSet.get(TokenNumber).getClassPart().equals("}")) {
            return true;
        }
        return false;
    }

    private boolean SST() {

        if (TokenSet.get(TokenNumber).getClassPart().matches("int|float|String|char|bool|ID|FOR|IF|BREAK|CONTINUE|RETURN|PPMM")) {

            if (TokenSet.get(TokenNumber).getClassPart().equals("ID")) {
                TokenNumber++;
                if (ADA()) {
                    return true;
                }
            } else if (DecInit_Pri_st()) {
                return true;
            } else if (For_st()) {
                return true;
            } else if (If_Else_st()) {
                return true;
            } else if (Break_Continue_st()) {
                return true;
            } else if (Return_st()) {
                return true;
            }
        }
        return false;
    }

    private boolean Return_st() {
        if(TokenSet.get(TokenNumber).getClassPart().equals("RETURN")) {
            TokenNumber++;
            if(Return()){
                if(TokenSet.get(TokenNumber).getClassPart().equals(";")) {
                    TokenNumber++;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean Return() {
        if (TokenSet.get(TokenNumber).getClassPart().matches("int_Constant|float_Constant|String_Constant|char_Constant|bool_Constant|ID|PPMM|!")) {
            if (OE()) {
                return true;
            }
        }
        else if (TokenSet.get(TokenNumber).getClassPart().equals(";")) {
                return true;
        }
        return false;
    }

    private boolean OE() {
        if (TokenSet.get(TokenNumber).getClassPart().matches("int_Constant|float_Constant|String_Constant|char_Constant|bool_Constant|ID|PPMM|!")) {
            if (AE()) {
                if (OE_())
                    return true;
            }
        }
        return false;
    }

    private boolean OE_() {
        if(TokenSet.get(TokenNumber).getClassPart().equals("||")){
            TokenNumber++;
            if(OE()){
                return true;
            }
        }
        else if(TokenSet.get(TokenNumber).getClassPart().matches(",|]|;")||
                TokenSet.get(TokenNumber).getClassPart().equals(")")){
            return true;
        }
        return false;
    }

    private boolean AE() {
        if (TokenSet.get(TokenNumber).getClassPart().matches("int_Constant|float_Constant|String_Constant|char_Constant|bool_Constant|ID|PPMM|!")) {
            if (RE()) {
                if (AE_())
                    return true;
            }
        }
        return false;
    }

    private boolean AE_() {
        if(TokenSet.get(TokenNumber).getClassPart().equals("&&")){
            TokenNumber++;
            if(AE())
                return true;
        }
        else if(TokenSet.get(TokenNumber).getClassPart().matches(",|]|;")||
                TokenSet.get(TokenNumber).getClassPart().equals(")") ||
                TokenSet.get(TokenNumber).getClassPart().equals("||")){
            return true;
        }
        return false;
    }

    private boolean RE() {
        if (TokenSet.get(TokenNumber).getClassPart().matches("int_Constant|float_Constant|String_Constant|char_Constant|bool_Constant|ID|PPMM|!")) {
            if (E()) {
                if (RE_())
                    return true;
            }
        }
        return false;
    }

    private boolean RE_() {
         if(TokenSet.get(TokenNumber).getClassPart().equals("RELATIONAL_OP")){
             TokenNumber++;
             if(RE()){
                 if(RE_())
                     return true;
             }
        }
         else if(TokenSet.get(TokenNumber).getClassPart().matches(",|]|;|&&")||
                 TokenSet.get(TokenNumber).getClassPart().equals(")") ||
                 TokenSet.get(TokenNumber).getClassPart().equals("||")){
             return true;
         }
        return false;

    }

    private boolean E() {
        if (TokenSet.get(TokenNumber).getClassPart().matches("int_Constant|float_Constant|String_Constant|char_Constant|bool_Constant|ID|PPMM|!")) {
            if (T()) {
                if (E_()) {
                    return true;
                }
            }
        }
         return false;
    }

    private boolean E_() {
        if(TokenSet.get(TokenNumber).getClassPart().equals("PM")){
            TokenNumber++;
            if (E())
                return true;
        }
        else if(TokenSet.get(TokenNumber).getClassPart().matches(",|]|;|&&|RELATIONAL_OP")||
                TokenSet.get(TokenNumber).getClassPart().equals(")") ||
                TokenSet.get(TokenNumber).getClassPart().equals("||")){
            return true;
        }
        return false;
    }

    private boolean T() {
        if (TokenSet.get(TokenNumber).getClassPart().matches("int_Constant|float_Constant|String_Constant|char_Constant|bool_Constant|ID|PPMM|!")) {
            if (F()) {
                if (T_())
                    return true;
            }
        }
        return false;
    }

    private boolean T_() {
        if(TokenSet.get(TokenNumber).getClassPart().equals("MD")){
            TokenNumber++;
            if(T())
                return true;
        }
        else if(TokenSet.get(TokenNumber).getClassPart().matches(",|]|;|&&|RELATIONAL_OP|PM")||
                TokenSet.get(TokenNumber).getClassPart().equals(")") ||
                TokenSet.get(TokenNumber).getClassPart().equals("||")){
            return true;
        }
        return false;
    }

    private boolean F() {
        if(TokenSet.get(TokenNumber).getClassPart().equals("ID")){
            TokenNumber++;
            if(SAFOID())
                return true;
        }
        else if(TokenSet.get(TokenNumber).getClassPart().equals(LexicalAnalyser.CONSTANT_STRING)||
                TokenSet.get(TokenNumber).getClassPart().equals(LexicalAnalyser.CONSTANT_CHAR)||
                TokenSet.get(TokenNumber).getClassPart().equals(LexicalAnalyser.CONSTANT_FLOAT)||
                TokenSet.get(TokenNumber).getClassPart().equals(LexicalAnalyser.CONSTANT_INTEGER)||
                TokenSet.get(TokenNumber).getClassPart().equals(LexicalAnalyser.CONSTANT_BOOLEAN)){
            TokenNumber++;
                return true;
        }
       else  if(TokenSet.get(TokenNumber).getClassPart().equals("PPMM")){
            TokenNumber++;
            if(TokenSet.get(TokenNumber).getClassPart().equals("ID")){
                TokenNumber++;
                if(Asgnn())
                    return true;
            }
        }
       else if(TokenSet.get(TokenNumber).getClassPart().equals("!")){
            TokenNumber++;
            if(F())
                return true;
        }
        return false;
    }

    private boolean SAFOID() {

        if(TokenSet.get(TokenNumber).getClassPart().equals("[")){
            TokenNumber++;
            if(OE()){
                if(TokenSet.get(TokenNumber).getClassPart().equals("]")){
                    TokenNumber++;
                    return true;
                }
            }
        }
        else if(TokenSet.get(TokenNumber).getClassPart().equals("(")){
            TokenNumber++;
            if(Parameter_List()) {
                if(TokenSet.get(TokenNumber).getClassPart().equals(")")) {
                    TokenNumber++;
                    return true;
                }
            }
        }
        else if(TokenSet.get(TokenNumber).getClassPart().equals("PPMM")) {
            TokenNumber++;
            return true;
        }

        else if(Obj_F()) {
            return true;
        }

        else if(TokenSet.get(TokenNumber).getClassPart().matches("MD|PM|RELATIONAL_OP|&&|,|]|;")||
                TokenSet.get(TokenNumber).getClassPart().equals("||")||
                TokenSet.get(TokenNumber).getClassPart().equals(")")) {
            return true;
        }
        return false;
    }

    private boolean Obj_F() {
        if(TokenSet.get(TokenNumber).getClassPart().equals(".")) {
            TokenNumber++;
            if(TokenSet.get(TokenNumber).getClassPart().equals("ID")) {
                TokenNumber++;
                if(AP())
                    return true;
            }
        }
        return false;
    }

    private boolean AP() {
        if (TokenSet.get(TokenNumber).getClassPart().equals("[")) {
            if (isArray()) {
                if (Mo())
                    return true;
            }
        }
        else if(TokenSet.get(TokenNumber).getClassPart().equals("(")) {
            TokenNumber++;
            if(Parameter_List()){
                if(TokenSet.get(TokenNumber).getClassPart().equals(")")) {
                    TokenNumber++;
                    if(More())
                        return true;
                }
            }
        }
        else if(TokenSet.get(TokenNumber).getClassPart().matches("MD|PM|RELATIONAL_OP|&&|,|]|;")||
                TokenSet.get(TokenNumber).getClassPart().equals("||")||
                TokenSet.get(TokenNumber).getClassPart().equals(")")) {
            return true;
        }
        return false;
    }

    private boolean More() {
        if(TokenSet.get(TokenNumber).getClassPart().equals(".")) {
            if (Obj_F())
                return true;
        }

        else if(TokenSet.get(TokenNumber).getClassPart().matches("MD|PM|RELATIONAL_OP|&&|,|]|;")||
                TokenSet.get(TokenNumber).getClassPart().equals("||")||
                TokenSet.get(TokenNumber).getClassPart().equals(")")) {
            return true;
        }
        return false;
    }

    private boolean Mo() {
        if(TokenSet.get(TokenNumber).getClassPart().equals(".")) {
            if (More())
                return true;
        }
        else if(TokenSet.get(TokenNumber).getClassPart().equals("PPMM")) {
            TokenNumber++;
            return true;
        }
        else if(TokenSet.get(TokenNumber).getClassPart().matches("MD|PM|RELATIONAL_OP|&&|,|]|;")||
                TokenSet.get(TokenNumber).getClassPart().equals("||")||
                TokenSet.get(TokenNumber).getClassPart().equals(")")) {
            return true;
        }
        return false;
    }

    private boolean isArray() {
        if(TokenSet.get(TokenNumber).getClassPart().equals("[")) {
            TokenNumber++;
            if(OE()){
                if(TokenSet.get(TokenNumber).getClassPart().equals("]")) {
                    TokenNumber++;
                    return true;
                }
            }
        }
        else if(TokenSet.get(TokenNumber).getClassPart().matches("MD|PM|RELATIONAL_OP|&&|,|]|;|PPMM|ASGN_OP|.")||
                TokenSet.get(TokenNumber).getClassPart().equals("||")||
                TokenSet.get(TokenNumber).getClassPart().equals(")")) {
            return true;
        }
        return false;
    }

    private boolean Break_Continue_st() {

        if(TokenSet.get(TokenNumber).getClassPart().equals("BREAK")) {
            TokenNumber++;
            if(TokenSet.get(TokenNumber).getClassPart().equals(";")) {
                TokenNumber++;
                return true;
            }
        }
        else if(TokenSet.get(TokenNumber).getClassPart().equals("CONTINUE")) {
            TokenNumber++;
            if(TokenSet.get(TokenNumber).getClassPart().equals(";")) {
                TokenNumber++;
                return true;
            }
        }
        return false;
    }

    private boolean If_Else_st() {
        if(TokenSet.get(TokenNumber).getClassPart().equals("IF")){
            TokenNumber++;
            if(TokenSet.get(TokenNumber).getClassPart().equals("(")){
                TokenNumber++;
                if(OE()){
                    if(TokenSet.get(TokenNumber).getClassPart().equals(")")){
                        TokenNumber++;
                        if(Body())
                            if(Else())
                                return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean Else() {
        if(TokenSet.get(TokenNumber).getClassPart().equals("ELSE")){
            TokenNumber++;
            if(Body())
                return true;
        }
        else if(TokenSet.get(TokenNumber).getClassPart().matches("int|float|String|char|bool|ID|FOR|IF|BREAK|CONTINUE|RETURN|}")){
            return true;
        }
        return false;
    }

    private boolean Body() {
        if(TokenSet.get(TokenNumber).getClassPart().equals(";"))
            return true;
        else if(SST()){
            TokenNumber++;
            return true;
        }
        else if(TokenSet.get(TokenNumber).getClassPart().equals("{")){
            TokenNumber++;
            if(MST()){
                if(TokenSet.get(TokenNumber).getClassPart().equals("}")) {
                    TokenNumber++;
                    return true;
                }
            }
        }
            return false;
    }

    private boolean For_st() {
        if(TokenSet.get(TokenNumber).getClassPart().equals("FOR")) {
            TokenNumber++;
            if(TokenSet.get(TokenNumber).getClassPart().equals("(")) {
                TokenNumber++;
                if(C1()){
                    if (C2()){
                        if(TokenSet.get(TokenNumber).getClassPart().equals(";")) {
                            TokenNumber++;
                            if(C3()){
                                if(TokenSet.get(TokenNumber).getClassPart().equals(")")) {
                                    TokenNumber++;
                                    if(Body())
                                        return true;
                                }
                            }
                        }
                    }

                }
            }
        }
        return false;
    }

    private boolean C3() {
        if(TokenSet.get(TokenNumber).getClassPart().matches("ID|PPMM")) {
            if (Asgn_IncDec())
                return true;
        }
        else if (TokenSet.get(TokenNumber).getClassPart().equals(")")) {
            return true;
        }
        return false;
    }

    private boolean Asgn_IncDec() {
        if(TokenSet.get(TokenNumber).getClassPart().equals("ID")) {
            TokenNumber++;
            if(Asgnn()){
                if (AID())
                    return true;
            }
        }
        else if(TokenSet.get(TokenNumber).getClassPart().equals("PPMM")) {
            TokenNumber++;
            if(TokenSet.get(TokenNumber).getClassPart().equals("ID")) {
                TokenNumber++;
                if(Asgnn())
                    return true;
            }
        }
        return false;
    }

    private boolean AID() {
        if(TokenSet.get(TokenNumber).getClassPart().equals("PPMM")) {
            TokenNumber++;
            return true;
        }
        else if(TokenSet.get(TokenNumber).getClassPart().equals("ASGN_OP")) {
            TokenNumber++;
            if(OE())
                return true;
        }
        return false;
    }

    private boolean C2() {

        if(TokenSet.get(TokenNumber).getClassPart().matches("ID|PPMM|!|int_Constant|float_Constant|String_Constant|char_Constant|bool_Constant")) {
            if (OE())
                return true;
        }
        else if(TokenSet.get(TokenNumber).getClassPart().equals(";")) {
            return true;
        }
        return false;
    }

    private boolean C1() {
        if(TokenSet.get(TokenNumber).getClassPart().matches("ID|;|int|float|String|char|bool")) {
            if (TokenSet.get(TokenNumber).getClassPart().equals("ID")) {
                TokenNumber++;
                if (ADA()) {
                    return true;
                }
            } else if (TokenSet.get(TokenNumber).getClassPart().equals(";")) {
                TokenNumber++;
                return true;
            } else if (DecInit_Pri_st()) {
                return true;
            }
        }
        return false;
    }

    private boolean DecInit_Pri_st() {
        if(DT_Pri()){
            if(Array()){
                if(TokenSet.get(TokenNumber).getClassPart().equals("ID")) {
                    TokenNumber++;
                    if (DA()) {
                         if (TokenSet.get(TokenNumber).getClassPart().equals(";")) {
                             TokenNumber++;
                             return true;
                         }
                    }
                }
            }
        }
        return false;
    }

    private boolean ADA() {

        if(TokenSet.get(TokenNumber).getClassPart().matches("ID|.|int|float|String|char|bool|PPMM|ASGN_OP")||
                TokenSet.get(TokenNumber).getClassPart().equals("[")||
                TokenSet.get(TokenNumber).getClassPart().equals("(")) {

            if (TokenSet.get(TokenNumber).getClassPart().equals("ID")) {
                TokenNumber++;
                if (DA()) {
                    if (TokenSet.get(TokenNumber).getClassPart().equals(";")) {
                        TokenNumber++;
                        return true;
                    }
                }
            } else if (Asgnn()) {
                if (PPAS()) {
                    if (TokenSet.get(TokenNumber).getClassPart().equals(";")) {
                        TokenNumber++;
                        return true;
                    }
                }
            } else if (TokenSet.get(TokenNumber).getClassPart().equals("(")) {
                TokenNumber++;
                if (Parameter_List()) {
                    if (TokenSet.get(TokenNumber).getClassPart().equals(")")) {
                        TokenNumber++;
                        if (TokenSet.get(TokenNumber).getClassPart().equals(";")) {
                            TokenNumber++;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean PPAS() {

        if(TokenSet.get(TokenNumber).getClassPart().equals("ASGN_OP")) {
            TokenNumber++;
            if(OE_AO_Init()){
                return true;
            }
        }
        else if(TokenSet.get(TokenNumber).getClassPart().equals("PPMM")) {
            TokenNumber++;
            return true;
        }

        else if(TokenSet.get(TokenNumber).getClassPart().equals(";")) {
            return true;
        }
        return false;
    }

    private boolean OE_AO_Init() {
        if (TokenSet.get(TokenNumber).getClassPart().matches("int_Constant|float_Constant|String_Constant|char_Constant|bool_Constant|ID|PPMM|!|NEW")) {
            if (OE())
                return true;
            else if (TokenSet.get(TokenNumber).getClassPart().equals("NEW")) {
                TokenNumber++;
                if (AO()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean AO() {
        if(DT_Pri()){
            if(TokenSet.get(TokenNumber).getClassPart().equals("[")) {
                TokenNumber++;
                if(OE()){
                    if(TokenSet.get(TokenNumber).getClassPart().equals("]")) {
                        TokenNumber++;
                        return true;
                    }
                }
            }
        }
        else if(TokenSet.get(TokenNumber).getClassPart().equals("ID")) {
            TokenNumber++;
            if(AOO()){
                return true;
            }
        }
        return false;
    }

    private boolean AOO() {
        if(TokenSet.get(TokenNumber).getClassPart().equals("[")) {
            TokenNumber++;
            if(OE()){
                if(TokenSet.get(TokenNumber).getClassPart().equals("]")) {
                    TokenNumber++;
                    return true;
                }
            }
        }
        else if(TokenSet.get(TokenNumber).getClassPart().equals("(")) {
            TokenNumber++;
           if(Parameter_List()){
               if(TokenSet.get(TokenNumber).getClassPart().equals(")")) {
                   TokenNumber++;
                   return true;
               }
           }
        }
        return false;
    }

    private boolean Parameter_List() {
        if (TokenSet.get(TokenNumber).getClassPart().matches("int_Constant|float_Constant|String_Constant|char_Constant|bool_Constant|ID|PPMM|!")) {
            if (OE()) {
                if (MP()) {
                    return true;
                }
            }
        }
        else if(TokenSet.get(TokenNumber).getClassPart().equals(")")) {
            return true;
        }
        return false;
    }

    private boolean MP() {
        if(TokenSet.get(TokenNumber).getClassPart().equals(",")) {
            TokenNumber++;
            if (OE()){
                if(MP())
                    return true;
            }
        }
        else if(TokenSet.get(TokenNumber).getClassPart().equals(")")) {
            return true;
        }
        return false;
    }

    private boolean Asgnn() {
        if (TokenSet.get(TokenNumber).getClassPart().matches(".")||
                TokenSet.get(TokenNumber).getClassPart().equals("[")) {

            if (TokenSet.get(TokenNumber).getClassPart().equals("[")) {
                TokenNumber++;
                if (OE()) {
                    if (TokenSet.get(TokenNumber).getClassPart().equals("]")) {
                        TokenNumber++;
                        return true;
                    }
                }
            } else if (Obj_Val_Asgn()) {
                return true;
            }
        }
        else if(TokenSet.get(TokenNumber).getClassPart().matches("MD|PM|RELATIONAL_OP|&&|,|]|;|PPMM||ASGN_OP") ||
                TokenSet.get(TokenNumber).getClassPart().equals(")") ||
                TokenSet.get(TokenNumber).getClassPart().equals("||"))  {
            return true;
        }
        return false;
    }

    private boolean Obj_Val_Asgn() {
        if(TokenSet.get(TokenNumber).getClassPart().equals(".")) {
            TokenNumber++;
            if(At_Fn())
                return true;
        }
        return false;
    }

    private boolean At_Fn() {
        if(TokenSet.get(TokenNumber).getClassPart().equals("ID")) {
            TokenNumber++;
            if(AMP())
                return true;
        }
        return false;
    }

    private boolean AMP() {
        if(TokenSet.get(TokenNumber).getClassPart().equals("(") ||
                TokenSet.get(TokenNumber).getClassPart().equals("[") ||
                TokenSet.get(TokenNumber).getClassPart().equals(".")) {
            if (isArray()) {
                if (M())
                    return true;
            }
            else if (TokenSet.get(TokenNumber).getClassPart().equals("(")) {
                TokenNumber++;
                if (Parameter_List())
                    if (TokenSet.get(TokenNumber).getClassPart().equals(")")) {
                        TokenNumber++;
                        if (More_At_Fn())
                            return true;
                    }
            }
        }
        else if(TokenSet.get(TokenNumber).getClassPart().matches(",|]|;|&&|RELATIONAL_OP|PM|MD|ASGN_OP")||
                TokenSet.get(TokenNumber).getClassPart().equals("||") ||
                TokenSet.get(TokenNumber).getClassPart().equals(")")){
            return true;
        }

        return false;
    }

    private boolean More_At_Fn() {
        if(TokenSet.get(TokenNumber).getClassPart().equals(".")) {
            TokenNumber++;
            if (At_Fn())
                return true;
        }
        return false;
    }

    private boolean M() {
        if(TokenSet.get(TokenNumber).getClassPart().equals(".")) {
            if (More_At_Fn())
                return true;
        }
        else if(TokenSet.get(TokenNumber).getClassPart().matches("MD|PM|RELATIONAL_OP|ASGN_OP|&&|,|]|;|PPMM") ||
                TokenSet.get(TokenNumber).getClassPart().equals(")")||
                TokenSet.get(TokenNumber).getClassPart().equals("||")){
            TokenNumber++;
            return true;
        }
        return false;
    }

    private boolean Init_Parameter_List() {
        if (TokenSet.get(TokenNumber).getClassPart().matches("int|float|String|char|bool|ID")) {
            if (DT_All()) {
                if (TokenSet.get(TokenNumber).getClassPart().equals("ID")) {
                    TokenNumber++;
                    if (MIP()) {
                        return true;
                    }
                }
            }
        }
        else if(TokenSet.get(TokenNumber).getClassPart().equals(")")) {
            return true;
        }
        return false;
    }

    private boolean MIP() {

        if(TokenSet.get(TokenNumber).getClassPart().equals(",")) {
            TokenNumber++;
            if(DT_All()){
                if(TokenSet.get(TokenNumber).getClassPart().equals("ID")) {
                    TokenNumber++;
                    if(MIP()){
                        return true;
                    }
                }
            }
        }
        else if(TokenSet.get(TokenNumber).getClassPart().equals(")")) {
            return true;
        }
        return false;
    }

    private boolean Final() {
        if(TokenSet.get(TokenNumber).getClassPart().equals("FINAL")) {
            TokenNumber++;
            if(TokenSet.get(TokenNumber).getClassPart().matches("VOID|int|float|String|char|bool")){
                return true;
            }
        }
        else if(TokenSet.get(TokenNumber).getClassPart().matches("VOID|int|float|String|char|bool")){
            return true;
        }

        return false;
    }

    private boolean Static() {
        if(TokenSet.get(TokenNumber).getClassPart().equals("STATIC")) {
            TokenNumber++;
            if(TokenSet.get(TokenNumber).getClassPart().matches("FINAL|VOID|int|float|String|char|bool")){
                return true;
            }
        }
        else if(TokenSet.get(TokenNumber).getClassPart().matches("FINAL|VOID|int|float|String|char|bool")){
            return true;
        }
        return false;
    }

    private boolean AM() {
        if(TokenSet.get(TokenNumber).getClassPart().equals("AM")) {
            TokenNumber++;
            if(TokenSet.get(TokenNumber).getClassPart().matches("STATIC|FINAL|VOID|int|float|String|char|bool")){
                return true;
            }
        }
        else if(TokenSet.get(TokenNumber).getClassPart().matches("STATIC|FINAL|VOID|int|float|String|char|bool")){
            return true;
        }
        return false;
    }

    private boolean INH(){
        if(TokenSet.get(TokenNumber).getClassPart().equals("INH")){
            TokenNumber++;
            if(TokenSet.get(TokenNumber).getClassPart().equals("ID")){
                TokenNumber++;
                return true;
                }
            }
        else if(TokenSet.get(TokenNumber).getClassPart().equals("{")){
            return true;
        }
        return false;
    }

    private boolean returnFalseReportError(){
        System.out.println("Syntax Error at Line #" + TokenSet.get(TokenNumber).getLineNumber());
        System.out.println("Token is: " + TokenSet.get(TokenNumber).getValuePart());
        System.out.println("Token #" + TokenNumber);
        return false;
    }

}
