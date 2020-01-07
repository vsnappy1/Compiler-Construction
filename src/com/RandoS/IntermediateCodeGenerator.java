package com.RandoS;

import java.util.ArrayList;
import java.util.Stack;

import static com.RandoS.LexicalAnalyser.*;

// Semantic Analyser works with syntax analyser

public class IntermediateCodeGenerator {

    private static int TokenNumber;
    private ArrayList<Token> TokenSet;
    private boolean isFirstTime = true;

    private static ArrayList<Definition> definitionTable;
    private static ArrayList<ClassDefinition> classDefinitionTable;
    private static ArrayList<FunctionDefinition> functionDefinitionTable;

    private int currentScope;
    private int scope;
    private Stack<Integer> scopeStack;
    private Stack<NameType> typeStack;
    private Stack<String> operatorStack;
    private String ClassName;
    private String functionName;
    private String Parameters;

    private String Name;
    private String Type;
    private String Category;
    private String Parent;
    private String AccessModifier;
    private String TypeModifier;
    private Boolean Constant;

    private int indexT;
    private int indexL;
    private String intermediateCode;
    private ArrayList<String> temp;
    private ArrayList<String> tempFunCall;
    private String forLoop;


    public IntermediateCodeGenerator(ArrayList<Token> TokenSet) {

        TokenNumber = 0;
        currentScope = 0;
        scope = 0;
        indexT = 0;
        indexL = 0;
        intermediateCode = "";
        temp = new ArrayList<>();
        tempFunCall = new ArrayList<>();
        definitionTable = new ArrayList<>();
        classDefinitionTable = new ArrayList<>();
        functionDefinitionTable = new ArrayList<>();
        this.TokenSet = TokenSet;

        setParametersToDefault();
        scopeStack = new Stack<>();
        typeStack = new Stack<>();
        operatorStack = new Stack<>();

        start();
    }

    boolean start() {
        if (TokenSet.get(TokenNumber).getClassPart().equals("CLASS")) {
            Type = TokenSet.get(TokenNumber).getValuePart();
            TokenNumber++;
            if (TokenSet.get(TokenNumber).getClassPart().equals("ID")) {
                Name = TokenSet.get(TokenNumber).getValuePart();
                ClassName = Name;
                TokenNumber++;
                if (INH()) {
                    if(!insertIntoDefinitionTable(new Definition(Name,Type,Category,Parent))){
                        redeclarationError();
                    }
                    setParametersToDefault();
                    if (TokenSet.get(TokenNumber).getClassPart().equals("{")) {
                        CreateScope();
                        TokenNumber++;
                        if (Class_Body()) {
                            if (TokenSet.get(TokenNumber).getClassPart().equals("MAIN")) {
                                TokenNumber++;
                                if (TokenSet.get(TokenNumber).getClassPart().equals("(")) {
                                    TokenNumber++;
                                    if (TokenSet.get(TokenNumber).getClassPart().equals(")")) {
                                        TokenNumber++;
                                        if (TokenSet.get(TokenNumber).getClassPart().equals("{")) {
                                            CreateScope();
                                            TokenNumber++;
                                            if (MST()) {
                                                if (TokenSet.get(TokenNumber).getClassPart().equals("}")) {
                                                    DestroyScope();
                                                    TokenNumber++;
                                                    if (Class_Body()) {
                                                        if (TokenSet.get(TokenNumber).getClassPart().equals("}")) {
                                                            DestroyScope();
                                                            TokenNumber++;
                                                            if (Defs()) {
                                                                displayIC();
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
            Type = TokenSet.get(TokenNumber).getValuePart();
            TokenNumber++;
            if (TokenSet.get(TokenNumber).getClassPart().equals("ID")) {
                Name = TokenSet.get(TokenNumber).getValuePart();
                ClassName = Name;
                TokenNumber++;
                if (INH()) {
                    if(!insertIntoDefinitionTable(new Definition(Name,Type,Category,Parent))){
                        redeclarationError();
                    }
                    setParametersToDefault();
                    if (TokenSet.get(TokenNumber).getClassPart().equals("{")) {
                        CreateScope();
                        TokenNumber++;
                        if (Class_Body()) {
                            if (TokenSet.get(TokenNumber).getClassPart().equals("}")) {
                                DestroyScope();
                                TokenNumber++;
                                return true;
                            }
                        }
                    }
                }
            }
        } else if (TokenSet.get(TokenNumber).getClassPart().equals("INTERFACE")) {
            Type = TokenSet.get(TokenNumber).getValuePart();
            TokenNumber++;
            if (TokenSet.get(TokenNumber).getClassPart().equals("ID")) {
                Name = TokenSet.get(TokenNumber).getValuePart();
                ClassName = Name;
                TokenNumber++;
                if (INH()) {
                    if(!insertIntoDefinitionTable(new Definition(Name,Type,Category,Parent))){
                        redeclarationError();
                    }
                    setParametersToDefault();
                    if (TokenSet.get(TokenNumber).getClassPart().equals("{")) {
                        CreateScope();
                        TokenNumber++;
                        if (Interface_Body()) {
                            if (TokenSet.get(TokenNumber).getClassPart().equals("}")) {
                                DestroyScope();
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
            Category = TokenSet.get(TokenNumber).getValuePart();
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
            Type = TokenSet.get(TokenNumber).getValuePart();
            TokenNumber++;
            if (TokenSet.get(TokenNumber).getClassPart().matches("ID")) {
                Name = TokenSet.get(TokenNumber).getValuePart();
                TokenNumber++;
                if (TokenSet.get(TokenNumber).getClassPart().equals("(")) {
                    CreateScope();
                    TokenNumber++;
                    String tempName = Name;
                    String tempType = Type;
                    if (Init_Parameter_List()) {
                        if (TokenSet.get(TokenNumber).getClassPart().equals(")")) {
                            Type = createFunctionType(tempType,currentScope);
                            if(!insertIntoClassDefinitionTable(new ClassDefinition(tempName,Type,AccessModifier,Constant,TypeModifier),ClassName)){
                                redeclarationError();
                            }
                            setParametersToDefault();
                            TokenNumber++;
                            if (TokenSet.get(TokenNumber).getClassPart().equals("{")) {
                                TokenNumber++;
                                if (MST()) {
                                    if (TokenSet.get(TokenNumber).getClassPart().equals("}")) {
                                        DestroyScope();
                                        TokenNumber++;
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (TokenSet.get(TokenNumber).getClassPart().matches("int|float|String|char|bool|ID")) {
            if (DT_All()) {
                if (TokenSet.get(TokenNumber).getClassPart().equals("ID")) {
                    Name = TokenSet.get(TokenNumber).getValuePart();
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
                CreateScope();
                TokenNumber++;
                String tempName = Name;
                String tempType = Type;
                if (Init_Parameter_List()) {
                    if (TokenSet.get(TokenNumber).getClassPart().equals(")")) {
                        Type = createFunctionType(tempType,currentScope);
                        if(!insertIntoClassDefinitionTable(new ClassDefinition(tempName,Type,AccessModifier,Constant,TypeModifier),ClassName)){
                            redeclarationError();
                        }
                        setParametersToDefault();
                        TokenNumber++;
                        if (TokenSet.get(TokenNumber).getClassPart().equals("{")) {
                            TokenNumber++;
                            if (MST()) {
                                if (TokenSet.get(TokenNumber).getClassPart().equals("}")) {
                                    DestroyScope();
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
            Type = TokenSet.get(TokenNumber).getValuePart();
            TokenNumber++;
            return true;
        }
        return false;
    }

    private boolean DA() {

        if (scopeStack.size() != 1 || !insertIntoClassDefinitionTable(new ClassDefinition(Name, Type, AccessModifier, Constant, TypeModifier), ClassName)) {
            if (!insertIntoFunctionDefinitionTable(new FunctionDefinition(Name, Type, currentScope))) {
                redeclarationError();
            }
        }
        if (TokenSet.get(TokenNumber).getClassPart().matches(",|ASGN_OP")) {
            if (One_More_DA()) {
                return true;
            } else if (TokenSet.get(TokenNumber).getClassPart().equals("ASGN_OP")) {
                FunctionDefinition tempFunctionDefinition = lookUpFunctionDefinitionTable(TokenSet.get(TokenNumber-1).getValuePart(),currentScope);
                if (tempFunctionDefinition != null) {
                    typeStack.push(new NameType(tempFunctionDefinition.getName(), tempFunctionDefinition.getType()));
                }
                operatorStack.push(TokenSet.get(TokenNumber).getValuePart());
                temp.clear();
                temp.add(TokenSet.get(TokenNumber-1).getValuePart());
                temp.add(TokenSet.get(TokenNumber).getValuePart());
                TokenNumber++;
                temp.add(TokenSet.get(TokenNumber).getValuePart());
                if (SAO()) {
                    addIC();
                    if (One_More_DA())
                        return true;
                }
            }

        }
        else if(TokenSet.get(TokenNumber).getClassPart().equals(";")) {
            setParametersToDefault();
            return true;
        }
        return false;
    }

    private boolean SAO() {
        if (TokenSet.get(TokenNumber).getClassPart().matches("ID|int_Constant|float_Constant|String_Constant|char_Constant|bool_Constant|PPMM|!|NEW")) {
            if (OE()) {
                checkCompatibility();
                return true;
            }
            else if (TokenSet.get(TokenNumber).getClassPart().equals("NEW")) {
                TokenNumber++;
                if (AO()) {

                    return true;
                }
            }
        }
        return false;
    }

    private void checkCompatibility(String pop, String pop1, String pop2) {

        if (!pop2.equals(pop) && !pop2.equals(pop1)) {
            missMatchError();
        }
    }

    private boolean One_More_DA() {
        if(TokenSet.get(TokenNumber).getClassPart().equals(",")) {
            TokenNumber++;
            if(TokenSet.get(TokenNumber).getClassPart().equals("ID")) {
                Name = TokenSet.get(TokenNumber).getValuePart();
                TokenNumber++;
                if(DA())
                    return true;
            }
        }
        else if(TokenSet.get(TokenNumber).getClassPart().equals(";")) {
            setParametersToDefault();
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
                Type = TokenSet.get(TokenNumber).getValuePart();
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
                temp.add(TokenSet.get(TokenNumber).getValuePart());
                Type = TokenSet.get(TokenNumber).getValuePart();
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
            operatorStack.push(TokenSet.get(TokenNumber).getValuePart());
            TokenNumber++;
            if(OE()){
                checkCompatibility();
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
            operatorStack.push(TokenSet.get(TokenNumber).getValuePart());
            TokenNumber++;
            if(AE())
                checkCompatibility();
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
            operatorStack.push(TokenSet.get(TokenNumber).getValuePart());
            TokenNumber++;
            if(RE()){
                checkCompatibility();
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
            operatorStack.push(TokenSet.get(TokenNumber).getValuePart());
            TokenNumber++;
            if (E())
                checkCompatibility();
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
            operatorStack.push(TokenSet.get(TokenNumber).getValuePart());
            TokenNumber++;
            if(T()) {
                checkCompatibility();
                return true;
            }
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
                TokenSet.get(TokenNumber).getClassPart().equals(CONSTANT_INTEGER)||
                TokenSet.get(TokenNumber).getClassPart().equals(LexicalAnalyser.CONSTANT_BOOLEAN)){
            typeStack.push(new NameType(TokenSet.get(TokenNumber).getValuePart(),typeOfConstant(TokenSet.get(TokenNumber).getClassPart())));
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

        FunctionDefinition tempFunctionDefinition = lookUpFunctionDefinitionTable(TokenSet.get(TokenNumber-1).getValuePart(),currentScope);

        if(TokenSet.get(TokenNumber).getClassPart().equals("[")){

            if(tempFunctionDefinition != null){
                typeStack.push(new NameType(tempFunctionDefinition.getName(), tempFunctionDefinition.getType()));
            }
            else {
                variableNotFoundError();
            }

            TokenNumber++;
            if(OE()){
                if(TokenSet.get(TokenNumber).getClassPart().equals("]")){
                    TokenNumber++;
                    return true;
                }
            }
        }
        else if(TokenSet.get(TokenNumber).getClassPart().equals("(")){
            ClassDefinition tempClassDefinition = lookUpClassDefinitionTable(TokenSet.get(TokenNumber-1).getValuePart(),ClassName);
            Parameters = "";
            if (tempClassDefinition == null) {
                functionNotFoundError();
            }
            TokenNumber++;
            if (Parameter_List()) {
                if (TokenSet.get(TokenNumber).getClassPart().equals(")")) {
                    if (tempClassDefinition != null) {
                        typeStack.push(new NameType(tempClassDefinition.getName(),doesParametersMatch(tempClassDefinition.getType(),Parameters)));
                    }
                    TokenNumber++;
                    return true;
                }
            }
        }
        else if(TokenSet.get(TokenNumber).getClassPart().equals("PPMM")) {
            if(tempFunctionDefinition != null){
                typeStack.push(new NameType(tempFunctionDefinition.getName(), tempFunctionDefinition.getType()));
            }
            else {
                variableNotFoundError();
            }
            TokenNumber++;
            return true;
        }

        else if(Obj_F()) {
            return true;
        }

        else if(TokenSet.get(TokenNumber).getClassPart().matches("MD|PM|RELATIONAL_OP|&&|,|]|;")||
                TokenSet.get(TokenNumber).getClassPart().equals("||")||
                TokenSet.get(TokenNumber).getClassPart().equals(")")) {

            if(tempFunctionDefinition != null){
                typeStack.push(new NameType(tempFunctionDefinition.getName(), tempFunctionDefinition.getType()));
            }
            else {
                variableNotFoundError();
            }

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
                    if(!typeStack.pop().getType().equals("bool")){
                        conditionShouldBeBooleanError();
                    }
                    if(TokenSet.get(TokenNumber).getClassPart().equals(")")){
                        temp.add("if( t"+indexT +" == false) jmp "+createLabel());
                        addIC();
                        TokenNumber++;
                        CreateScope();
                        if(Body()) {
                            DestroyScope();
                        }
                        if(Else()) {
                            temp.add("L"+indexL+" : ");
                            addIC();
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }



    private boolean Else() {
        if(TokenSet.get(TokenNumber).getClassPart().equals("ELSE")){
            temp.add("jmp "+createLabel());
            addIC();
            temp.add("L"+(indexL-1)+" : ");
            addIC();

            TokenNumber++;
            CreateScope();
            if(Body()) {
                DestroyScope();
            }
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
                CreateScope();
                TokenNumber++;
                if(C1()){
                    temp.add("L"+(indexL+2)+ " : ");
                    addIC();
                    if (C2()){
                        if(TokenSet.get(TokenNumber).getClassPart().equals(";")) {
                            TokenNumber++;
                            if(C3()){
                                if(TokenSet.get(TokenNumber).getClassPart().equals(")")) {
                                    temp.add("if( t"+indexT+" == false ) jmp "+createLabel() );
                                    addIC();
                                    TokenNumber++;
                                    if(Body())
                                        DestroyScope();
                                    temp.add(forLoop);
                                    addIC();
                                    temp.add("jmp L"+(indexL+1)+" : ");
                                    addIC();
                                    temp.add("L"+(indexL)+" : ");
                                    addIC();
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
            temp.add(TokenSet.get(TokenNumber).getValuePart());
            FunctionDefinition tempFunctionDefinition = lookUpFunctionDefinitionTable(TokenSet.get(TokenNumber).getValuePart(),currentScope);
            if(tempFunctionDefinition != null){
                typeStack.push(new NameType(tempFunctionDefinition.getName(), tempFunctionDefinition.getType()));
            }
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
            temp.add(TokenSet.get(TokenNumber).getValuePart());
            forLoop = formString();
            TokenNumber++;
            return true;
        }
        else if(TokenSet.get(TokenNumber).getClassPart().equals("ASGN_OP")) {
            operatorStack.push(TokenSet.get(TokenNumber).getValuePart());
            TokenNumber++;
            if(OE()) {
                checkCompatibility();
                typeStack.clear();
                operatorStack.clear();
                return true;
            }
        }
        return false;
    }

    private boolean C2() {

        if(TokenSet.get(TokenNumber).getClassPart().matches("ID|PPMM|!|int_Constant|float_Constant|String_Constant|char_Constant|bool_Constant")) {
            if (OE()) {
                if(!typeStack.peek().getType().equals("bool")){
                    conditionShouldBeBooleanError();
                }
                return true;
            }
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
                    Name = TokenSet.get(TokenNumber).getValuePart();
                    temp.add(TokenSet.get(TokenNumber).getValuePart());
                    TokenNumber++;
                    if (DA()) {
                        if (TokenSet.get(TokenNumber).getClassPart().equals(";")) {
                            TokenNumber++;
                            setParametersToDefault();
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean ADA() {
        FunctionDefinition tempFunctionDefinition = lookUpFunctionDefinitionTable(TokenSet.get(TokenNumber-1).getValuePart(),currentScope);
        if(tempFunctionDefinition != null){
            typeStack.push(new NameType(tempFunctionDefinition.getName(), tempFunctionDefinition.getType()));
        }
        if(TokenSet.get(TokenNumber).getClassPart().matches("ID|.|int|float|String|char|bool|PPMM|ASGN_OP")||
                TokenSet.get(TokenNumber).getClassPart().equals("[")||
                TokenSet.get(TokenNumber).getClassPart().equals("(")) {

            if (TokenSet.get(TokenNumber).getClassPart().equals("ID")) {
                Name = TokenSet.get(TokenNumber).getValuePart();

                if(lookUpDefinitionTable(Type) == null){
                    variableNotFoundError();
                }

                TokenNumber++;
                if (DA()) {
                    if (TokenSet.get(TokenNumber).getClassPart().equals(";")) {
                        setParametersToDefault();
                        TokenNumber++;
                        return true;
                    }
                }
            } else if (Asgnn()) {
                if(tempFunctionDefinition != null){
                    //TODO Compatibility Check
                }
                else {
                    variableNotFoundError();
                }
                if (PPAS()) {

                    addIC();
                    if (TokenSet.get(TokenNumber).getClassPart().equals(";")) {
                        TokenNumber++;
                        return true;
                    }
                }
            } else if (TokenSet.get(TokenNumber).getClassPart().equals("(")) {

                ClassDefinition tempClassDefinition = lookUpClassDefinitionTable(TokenSet.get(TokenNumber-1).getValuePart(),ClassName);
                Parameters = "";
                if (tempClassDefinition == null) {
                    functionNotFoundError();
                }
                TokenNumber++;
                if (Parameter_List()) {
                    if (TokenSet.get(TokenNumber).getClassPart().equals(")")) {
                        if (tempClassDefinition != null) {
                            doesParametersMatch(tempClassDefinition.getType(),Parameters);
                        }
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

    private String doesParametersMatch(String type, String parameters) {
        String[] typeAndParameters = type.split("->");
        if(!typeAndParameters[1].equals(parameters)){
            parametersNotMatchError();
            return "";
        }
        return typeAndParameters[0];
    }

    private boolean PPAS() {

        if(TokenSet.get(TokenNumber).getClassPart().equals("ASGN_OP")) {
            operatorStack.push(TokenSet.get(TokenNumber).getValuePart());
            temp.add(TokenSet.get(TokenNumber).getValuePart());
            TokenNumber++;
            temp.add(TokenSet.get(TokenNumber).getValuePart());
            if(OE_AO_Init()){
                checkCompatibility();
                return true;
            }
        }
        else if(TokenSet.get(TokenNumber).getClassPart().equals("PPMM")) {
            FunctionDefinition tempFunctionDefinition = lookUpFunctionDefinitionTable(TokenSet.get(TokenNumber-1).getValuePart(),currentScope);
            if(tempFunctionDefinition != null){
                typeStack.push(new NameType(tempFunctionDefinition.getName(), tempFunctionDefinition.getType()));
                operatorStack.push(TokenSet.get(TokenNumber).getValuePart());
                String type = Compatibility(typeStack.pop().getType(),operatorStack.pop());
                if(type == null){
                    missMatchError();
                }
            }
            else{
                variableNotFoundError();}
            temp.add(TokenSet.get(TokenNumber).getValuePart());
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
                    if(!typeStack.peek().getType().equals("int")){
                        shouldBeIntegerError();
                    }
                    typeStack.pop();
                    if(TokenSet.get(TokenNumber).getClassPart().equals("]")) {
                        typeStack.push(new NameType("",Type));
                        checkCompatibility();
                        TokenNumber++;
                        return true;
                    }
                }
            }
        }
        else if(TokenSet.get(TokenNumber).getClassPart().equals("ID")) {
            Definition temp = lookUpDefinitionTable(TokenSet.get(TokenNumber).getValuePart());
            if(temp != null){
                typeStack.push(new NameType("",temp.getParent()));
                typeStack.push(new NameType("",temp.getName()));
                checkCompatibility(typeStack.pop().getType(), typeStack.pop().getType(), typeStack.pop().getType());
            }
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


                addInParameterList(typeStack.pop().getType());
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

                addInParameterList(typeStack.pop().getType());
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
                    if(!typeStack.peek().getType().equals("int")){
                        shouldBeIntegerError();
                    }
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
            FunctionDefinition tempFun = lookUpFunctionDefinitionTable(TokenSet.get(TokenNumber-2).getValuePart(),currentScope);
            Definition tempDefination = null;
            if (tempFun != null) {
                tempDefination = lookUpDefinitionTable(tempFun.getType());
            }
            else {
                tempDefination = lookUpDefinitionTable(typeStack.peek().getType());
            }
            if(tempDefination != null){
                ClassDefinition tempClassDefinition = lookUpClassDefinitionTable(TokenSet.get(TokenNumber).getValuePart(),tempDefination.getName());
                if(tempClassDefinition != null){
                    if(tempClassDefinition.getAccessModifier().equals("public") && tempClassDefinition.getTypeModifier().equals("")){
                        typeStack.push(new NameType(tempClassDefinition.getName(),tempClassDefinition.getType()));
                    }
                }
                else
                    variableNotFoundError();
            }
            else
                classNotFoundError();
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
                    Name = TokenSet.get(TokenNumber).getValuePart();
                    if(!insertIntoFunctionDefinitionTable(new FunctionDefinition(Name,Type,currentScope))){
                        redeclarationError();
                    }
                    TokenNumber++;
                    if (MIP()) {
                        setParametersToDefault();
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
                    Name = TokenSet.get(TokenNumber).getValuePart();
                    if(!insertIntoFunctionDefinitionTable(new FunctionDefinition(Name,Type,currentScope))){
                        redeclarationError();
                    }
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
            Constant = true;
            TokenNumber++;
            if(TokenSet.get(TokenNumber).getClassPart().matches("VOID|int|float|String|char|bool|ID")){
                return true;
            }
        }
        else if(TokenSet.get(TokenNumber).getClassPart().matches("VOID|int|float|String|char|bool|ID")){
            return true;
        }

        return false;
    }

    private boolean Static() {
        if(TokenSet.get(TokenNumber).getClassPart().equals("STATIC")) {
            TypeModifier = TokenSet.get(TokenNumber).getValuePart();
            TokenNumber++;
            if(TokenSet.get(TokenNumber).getClassPart().matches("FINAL|VOID|int|float|String|char|bool|ID")){
                return true;
            }
        }
        else if(TokenSet.get(TokenNumber).getClassPart().matches("FINAL|VOID|int|float|String|char|bool|ID")){
            return true;
        }
        return false;
    }

    private boolean AM() {
        if(TokenSet.get(TokenNumber).getClassPart().equals("AM")) {
            AccessModifier = TokenSet.get(TokenNumber).getValuePart();
            TokenNumber++;
            if(TokenSet.get(TokenNumber).getClassPart().matches("STATIC|FINAL|VOID|int|float|String|char|bool|ID")){
                return true;
            }
        }
        else if(TokenSet.get(TokenNumber).getClassPart().matches("STATIC|FINAL|VOID|int|float|String|char|bool|ID")){
            return true;
        }
        return false;
    }

    private boolean INH(){
        if(TokenSet.get(TokenNumber).getClassPart().equals("INH")){
            TokenNumber++;
            if(TokenSet.get(TokenNumber).getClassPart().equals("ID")){
                Parent = TokenSet.get(TokenNumber).getValuePart();

                Definition tempDefinition = lookUpDefinitionTable(TokenSet.get(TokenNumber).getValuePart());
                if( tempDefinition != null){
                    if (tempDefinition.getCategory().equals("sealed")) {
                        cannotInheritError();
                    }
                }
                else
                    classNotFoundError();

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

    /************************************ SEMANTIC FUNCTIONS ***********************************************/

    private boolean insertIntoDefinitionTable(Definition definition ){
        for (Definition value : definitionTable) {
            if (value.getName().equals(definition.getName())) {
                return false;
            }
        }
        definitionTable.add(definition);
        return true;
    }

    private boolean insertIntoClassDefinitionTable(ClassDefinition classDefinition, String className){
        for (Definition value : definitionTable) {
            if (value.getName().equals(className)) {
                for (ClassDefinition value1 :  value.getClassDefinitions()) {
                    if (value1.getName().equals(classDefinition.getName()) && value1.getType().equals(classDefinition.getType())) {
                        return false;
                    }
                }
                value.getClassDefinitions().add(classDefinition);
                // Insert only variable not functions
                if(!classDefinition.getType().contains("->")) {
                    insertIntoFunctionDefinitionTable(new FunctionDefinition(classDefinition.getName(), classDefinition.getType(), currentScope));
                }
                return true;
            }
        }
        return false;
    }

    private boolean insertIntoFunctionDefinitionTable(FunctionDefinition functionDefinition ){
        for (FunctionDefinition value : functionDefinitionTable) {
            if (value.getName().equals(functionDefinition.getName()) && value.getScope() == functionDefinition.getScope()){
                return false;
            }
        }
        functionDefinitionTable.add(functionDefinition);
        return true;
    }

    private Definition lookUpDefinitionTable(String Name){
        for (Definition value : definitionTable) {
            if (value.getName().equals(Name)) {
                return value;
            }
        }
        return null; // LookUp Failed

    }

    private ClassDefinition lookUpClassDefinitionTable(String Name, String className){

        for (Definition value : definitionTable) {
            if (value.getName().equals(className)) {
                for (ClassDefinition value1 :  value.getClassDefinitions()) {
                    if (value1.getName().equals(Name)) {
                        return value1;
                    }
                }
            }
        }
        return null; // LookUp Failed
    }

    private ClassDefinition lookUpFunction(String Name, String ParameterList, String className){
        for (Definition value : definitionTable) {
            if (value.getName().equals(className)) {
                for (ClassDefinition value1 :  value.getClassDefinitions()) {
                    if (value1.getName().equals(Name) && value1.getType().equals(ParameterList)) {
                        return value1;
                    }
                }
            }
        }
        return null; // LookUp Failed
    }

    private FunctionDefinition lookUpFunctionDefinitionTable(String Name, int Scope){
        int i;
        for( i = functionDefinitionTable.size() -1 ; i >= 0 ; i--)
        {
            if(functionDefinitionTable.get(i).getName().equals(Name)) { //LookUp current Scope
                if(functionDefinitionTable.get(i).getScope() == Scope)
                    return functionDefinitionTable.get(i);
                break;
            }
        }
        int hierarchySize = scopeStack.size();

        int k;
        while( hierarchySize > 0){                                      //LookUp Hierarchy

            Scope = scopeStack.get(--hierarchySize);
            for(k = i ; k >= 0 ; k--)
            {
                if(functionDefinitionTable.get(k).getName().equals(Name)) {
                    if(functionDefinitionTable.get(k).getScope() == Scope)
                        return functionDefinitionTable.get(i);
                }
            }
        }
        return null; // LookUp Failed
    }


    private void CreateScope(){
        scope++;
        scopeStack.push(currentScope);
        currentScope = scope;
    }

    private void DestroyScope(){
        currentScope = scopeStack.pop();
    }

    private String Compatibility(String Left, String Right, String Operator) {

        if (Left.equals("int")) {
            if (isArithmeticOperator(Operator)) {
                if (Right.equals("int"))
                    return "int";
            } else if (isRelationalOperator(Operator)) {
                if (Right.equals("int") || Right.equals("float"))
                    return "bool";
            }else if(isAssignmentOperator(Operator)){
                if(Right.equals("int"))
                    return "";
            }

        }

        else if (Left.equals("float")) {
            if (isArithmeticOperator(Operator)) {
                if (Right.equals("float") || Right.equals("int"))
                    return "float";
            } else if (isRelationalOperator(Operator)) {
                if (Right.equals("int") || Right.equals("float"))
                    return "bool";
            }
        }

        else if (Left.equals("char")) {
            if (Operator.equals("==")) {
                if (Right.equals("char") )
                    return "bool";
            }
        }

        else if (Left.equals("String")) {
            if (Operator.equals("==")) {
                if (Right.equals("String") )
                    return "bool";
            }
            else if (Operator.equals("+")){
                if (Right.equals("char") || Right.equals("String") )
                    return "String";
            }
            else if (Operator.equals("=")){
                if(Right.equals("String"))
                    return "";
            }
        }

        else if (Left.equals("bool")) {
            if (Operator.equals("&&") || Operator.equals("||") || Operator.equals("==")) {
                if (Right.equals("bool") )
                    return "bool";
            }
        }
        else if(Left.equals(Right)){ //Non premitive assignment
            return "";
        }

        return null; // Report Error
    }

    private void setParametersToDefault(){
        Name = "";
        Type = "";
        Category = "General";
        Parent = "";
        AccessModifier = "private";
        TypeModifier = "";
        Constant = false;

    }

    private boolean isAssignmentOperator(String Operator) {
        if (Operator.equals("=") || Operator.equals("+=") || Operator.equals("*=") || Operator.equals("/="))
            return true;
        return false;
    }

    private String Compatibility(String Operand, String Operator){

        if(Operand.equals("int")){
            if(Operator.equals("++") || Operator.equals("--"))
                return "int";
        }
        else if(Operand.equals("float")){
            if(Operator.equals("++") || Operator.equals("--"))
                return "float";
        }
        else if(Operand.equals("bool")){
            if(Operator.equals("!"))
                return "bool";
        }

        return null;
    }

    private boolean isArithmeticOperator(String Operator){
        if (Operator.equals("-") || Operator.equals("/") || Operator.equals("+") || Operator.equals("*"))
            return true;
        return false;
    }

    private boolean isRelationalOperator(String Operator){
        if (Operator.matches("==|<|>|>=|<=|!="))
            return true;

        return false;
    }

    private void redeclarationError(){
        System.out.println("\nSemantic Error at Line # " + TokenSet.get(TokenNumber).getLineNumber());
        System.out.println("Redeclaration Error  ** "+TokenSet.get(TokenNumber-1).getValuePart()+" **");
    }

    private void cannotInheritError(){
        System.out.println("\nSemantic Error at Line # " + TokenSet.get(TokenNumber).getLineNumber());
        System.out.println("Can not Inherit Error  ** "+TokenSet.get(TokenNumber).getValuePart()+" **");
    }

    private void conditionShouldBeBooleanError() {
        System.out.println("\nSemantic Error at Line # " + TokenSet.get(TokenNumber).getLineNumber());
        System.out.println("Condition Should Be Boolean Error  ** "+TokenSet.get(TokenNumber).getValuePart()+" **");
    }

    private void shouldBeIntegerError() {
        System.out.println("\nSemantic Error at Line # " + TokenSet.get(TokenNumber).getLineNumber());
        System.out.println("Should Be Integer Error  ** " +TokenSet.get(TokenNumber - 1).getValuePart()+" **");
    }

    private void missMatchError(){
        System.out.println("\nSemantic Error at Line # " + TokenSet.get(TokenNumber).getLineNumber());
        System.out.println("Miss Match Error  ** " +TokenSet.get(TokenNumber - 1).getValuePart()+" **");
    }

    private void variableNotFoundError(){
        System.out.println("\nSemantic Error at Line # " + TokenSet.get(TokenNumber).getLineNumber());
        System.out.println("Variable Not Found Error  ** " +TokenSet.get(TokenNumber - 1).getValuePart()+" **");
    }

    private void classNotFoundError() {
        System.out.println("\nSemantic Error at Line # " + TokenSet.get(TokenNumber).getLineNumber());
        System.out.println("Class Not Found Error  ** " +TokenSet.get(TokenNumber - 1).getValuePart()+" **");
    }

    private void functionNotFoundError(){
        System.out.println("\nSemantic Error at Line # " + TokenSet.get(TokenNumber).getLineNumber());
        System.out.println("Function Not Found Error  ** " +TokenSet.get(TokenNumber - 1).getValuePart()+" **");
    }

    private void parametersNotMatchError(){
        System.out.println("\nSemantic Error at Line # " + TokenSet.get(TokenNumber).getLineNumber());
        System.out.println("Parameters Not Match Error  ** " +TokenSet.get(TokenNumber - 1).getValuePart()+" **");
    }

    private String createFunctionType(String returnType, int s){

        String str = "";
        str += returnType;
        str += "->";
        str += parametersList(s);

        return str;

    }

    private void addInParameterList(String type){
        if(Parameters != ""){
            Parameters += ","+type;
        }
        else {
            Parameters += type;
        }
    }

    private void checkCompatibility(){
        if(typeStack.size() > 1 && operatorStack.size() > 0) {
            if(!operatorStack.peek().equals("=")) {
                String p1 = typeStack.get(typeStack.size() - 2).getName();
                String p2 = typeStack.peek().getName();

                if(p1.equals("")){
                    if(p2.equals("")) {
                        p1 = "t" + (indexT - 1);
                    }
                    else
                        p1 = "t"+(indexT);
                }
                if(p2.equals("")){
                    p2 = "t"+(indexT);
                }

                temp.add(createTemp() + " = " + p1+" " + operatorStack.peek()+" " + p2);
                addIC();
            }
            String tempType = Compatibility(typeStack.pop().getType(), typeStack.pop().getType(), operatorStack.pop());
            if (tempType != null) {
                if(!tempType.equals("")) {
                    typeStack.push(new NameType("",tempType));
                }
            } else {
                missMatchError();
            }
        }
        else {
            System.out.println("Some Error");
            System.out.println("Line # "+ TokenSet.get(TokenNumber).getLineNumber());
        }
    }

    private String typeOfConstant(String constantType){
        switch (constantType){

            case CONSTANT_INTEGER:
                return "int";
            case CONSTANT_FLOAT:
                return "float";
            case CONSTANT_STRING:
                return "String";
            case CONSTANT_CHAR:
                return "char";
            case CONSTANT_BOOLEAN:
                return "bool";
            default:
                return "xyz";
        }
    }

    private String parametersList(int s) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < functionDefinitionTable.size() ; i++){

            if(functionDefinitionTable.get(i).getScope() == s) {
                if (!str.toString().equals("")){ str.append(","); }

                str.append(functionDefinitionTable.get(i).getType());

            }
        }
        // If no Parameters
        if(str.length() == 0){
            str.append("void");
        }
        return str.toString();
    }

    private static class Definition{
        private String mName;
        private String mType;
        private String mCategory;
        private String mParent;
        private ArrayList<ClassDefinition> mClassDefinitions;

        public Definition(String Name, String Type, String Category, String Parent){
            mName = Name;
            mType = Type;
            mCategory = Category;
            mParent = Parent;
            mClassDefinitions = new ArrayList<>();
        }

        // Getter Method
        String getName(){
            return mName;
        }
        String getType(){
            return mType;
        }
        String getCategory(){
            return mCategory ;
        }
        String getParent(){
            return mParent;
        }
        ArrayList<ClassDefinition> getClassDefinitions(){
            return mClassDefinitions;
        }
    }

    private static class ClassDefinition{
        private String mName;
        private String mType;
        private String mAccessModifier;
        private Boolean mConstant;
        private String mTypeModifier;

        public ClassDefinition(String Name, String Type, String AccessModifier, Boolean Constant, String TypeModifier){
            mName = Name;
            mType = Type;
            mAccessModifier = AccessModifier;
            mConstant = Constant;
            mTypeModifier = TypeModifier;
        }

        // Getter Method
        public String getName(){
            return mName;
        }
        public String getType(){
            return mType;
        }
        public String getAccessModifier(){
            return mAccessModifier;
        }
        public Boolean getConstant(){
            return mConstant;
        }
        public String getTypeModifier(){
            return mTypeModifier;
        }
    }

    private static class FunctionDefinition{
        private String mName;
        private String mType;
        private int mScope;

        public FunctionDefinition(String Name, String Type, int Scope){
            mName = Name;
            mType = Type;
            mScope = Scope;
        }

        // Getter Method
        public String getName(){
            return mName;
        }
        public String getType(){
            return mType;
        }
        public int getScope(){
            return mScope ;
        }
    }


    //******************************INTERMEDIATE CODE GENERATOR********************************************
    private String createTemp(){
        indexT++;
        return "t"+indexT;
    }

    private String createLabel(){
        indexL++;
        return "L"+indexL;
    }

    private void addIC(){

        if(temp.size() > 1) {
            if (temp.get(1).matches("=|-=|/=|--") ||
                    temp.get(1).equals("+=") ||
                    temp.get(1).equals("*=") ||
                    temp.get(1).equals("++")) {
                intermediateCode += ICGFormat(temp.get(1)) + "\n";
                temp.clear();
            }
        }
        else if(temp.size()>0) {
            intermediateCode += temp.get(0)+ "\n";
            temp.clear();
        }
    }

    private void displayIC(){
        System.out.println(intermediateCode);
    }

    private String formString(){
        String result = ICGFormat(temp.get(1));
        temp.clear();
        return result;
    }

    private String ICGFormat(String OP){

        String result = "";

        switch (OP) {
            case "=":
                result = temp.get(0) + " = " + temp.get(2);
                break;
            case "++":
                result = temp.get(0) + " = " + temp.get(0) + " + " + "1";
                break;
            case "--":
                result = temp.get(0) + " = " + temp.get(0) + " - " + "1";
                break;
            case "+=":
                result = temp.get(0) + " = " + temp.get(0) + " + " + temp.get(2);
                break;
            case "-=":
                result = temp.get(0) + " = " + temp.get(0) + " - " + temp.get(2);
                break;
            case "*=":
                result = temp.get(0) + " = " + temp.get(0) + " * " + temp.get(2);
                break;
            case "/=":
                result = temp.get(0) + " = " + temp.get(0) + " / " + temp.get(2);
                break;
        }

        return result;
    }
}
