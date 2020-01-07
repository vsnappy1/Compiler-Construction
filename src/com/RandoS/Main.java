package com.RandoS;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main {

    private static ArrayList<Token> Tokens;

    public static void main(String[] args) throws Exception {

         String sourceCode = readFileAsString("C:\\Users\\s\\Desktop\\test.txt")+"$";
         Tokens = new ArrayList<>();

         LexicalAnalyser lexicalAnalyser = new LexicalAnalyser(sourceCode);
         Tokens = lexicalAnalyser.getToken_list();
         lexicalAnalyser.displayTokens();

         //SemanticAnalyser semanticAnalyser = new SemanticAnalyser(Tokens);
        IntermediateCodeGenerator ICG = new IntermediateCodeGenerator(Tokens);



    }

    private static String readFileAsString(String fileName)throws Exception{
        String data = "";
        data = new String(Files.readAllBytes(Paths.get(fileName)));
        return data;
    }
}

