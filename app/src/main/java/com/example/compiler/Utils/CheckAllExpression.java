/**
 * Date   : 2021/1/5 12:30
 * Author : KI
 * File   : CheckAllExpression
 * Desc   : check expression
 * Motto  : Hungry And Humble
 */
package com.example.compiler.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

public class CheckAllExpression {
    public Map<String, String> expMap = new LinkedHashMap<String, String>();
    String tab = "";
    public String resString = "";
    public ArrayList<String> mainExpArrayList = new ArrayList<>();
    Lexical_Analysis lexical_Analysis;
    Grammatical_Analysis grammatical_Analysis;
    Operator_Precedence operator_Precedence;
    ArrayList<String> Vt = new ArrayList<String>(Arrays.asList(
            ",", "=", "+=", "-=", "*=", "/=", "%=", "||", "&&", "|", "^", "&", "=", "!=",
            ">", ">=", "<", "<=", "<<", ">>", "+", "-", "*", "/", "%",  "!", "[]",
            "(", ")", "i", "#"));   //规定一个顺序
    ArrayList<String> Vn = new ArrayList<String>(Arrays.asList(
            "S", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N"));
    String []proRule = {"S->#A#", "A->A,B", "A->B", "B->C=B", "B->C+=B",
            "B->C-=B", "B->C*=B", "B->C/=B", "B->C%=B", "B->C", "C->C||D", "C->D",
            "D->D&&E", "D->E", "E->E|F", "E->F", "F->F^G", "F->G", "G->G&H",
            "G->H", "H->H=I", "H->H!=I", "H->I", "I->I>J", "I->I>=J", "I->I<J",
            "I->I<=J", "I->J", "J->J<<K", "J->J>>K", "J->K", "K->K+L", "K->K-L",
            "K->L", "L->L*M", "L->L/M", "L->L%M", "L->M", "M->!M", "M->N", "N->N[]N",
            "N->(A)", "N->i"};
    String procedure = "";
    public CheckAllExpression(String procedure, String tab) {
        this.tab = tab;
        lexical_Analysis = new Lexical_Analysis(procedure);
        lexical_Analysis.initNum();
        lexical_Analysis.mainFunc();
        this.procedure = procedure;
        grammatical_Analysis = new Grammatical_Analysis();
        operator_Precedence = new Operator_Precedence(Vn, Vt, proRule, tab);
    }

    public String convert(String xString) {
        xString = xString.trim();
        String resString = "";
        for(int i = 0; i < xString.length(); i++) {
            char char1 = xString.charAt(i);
            if(char1 == '(') {
                xString = xString.substring(0, i + 1) + " " + xString.substring(i + 1, xString.length());
                i++;
            }
            if(char1 == ')') {
                xString = xString.substring(0, i) + " " + xString.substring(i, xString.length());
                i++;
            }
        }
        String list[] = xString.split(" ");
        for(String yString : list) {
            if(yString.equals("(") || yString.equals(")") || Vt.contains(yString)) {
                resString += yString;
            }else {
                resString += "i";
            }
        }
        return resString;
    }

    public boolean checkMacro() {
        //convert("(ADD_t + (FR * tt + 100))");
        //先检查所有宏表达式定义的
        boolean resFlag = true;
        String []lineStrings = procedure.split("\n");
        ArrayList<String> tempArrayList = new ArrayList<String>();
        for(int k = 0; k < lineStrings.length; k++) {
            String xString = lineStrings[k];
            if(!xString.contains("define")) {
                continue;
            }
            if(xString.contains("define") && k + 1 < lineStrings.length && !lineStrings[k + 1].contains("main")) {
                //说明为宏定义语句
                for(int i = 0; i < lexical_Analysis.resVector.size(); i++) {
                    Vector<String> vector = lexical_Analysis.resVector.get(i);
                    if(vector.get(1).equals("MACRO") && !tempArrayList.contains(vector.get(0))) {
                        tempArrayList.add(vector.get(0));
                        String waitString = vector.get(2);  //待分析的表达式
                        resString += ("待分析的宏常量:" + vector.get(0) + "\n");
                        resString += ("待分析的表达式:" + waitString + "\n");
                        System.out.println("待分析的宏常量:" + vector.get(0));
                        System.out.println("待分析的表达式:" + waitString);
                        expMap.put(vector.get(0), waitString);
                        waitString = convert(waitString);
                        resString += ("转换后:" + waitString + "\n");
                        System.out.println("转换后:" + waitString);
                        operator_Precedence.processString = "";
                        boolean flag = operator_Precedence.check(waitString);
                        resString += operator_Precedence.processString;
                        operator_Precedence.processString = "";
                        if(flag) {
                            resString += ("Successful!" + "\n" + "\n");
                            System.out.println("Successful!");
                            System.out.println();
                        }else {
                            resFlag = false;
                            resString += ("failed!" + "\n" + "\n");
                            System.out.println("failed!");
                            System.out.println();
                        }
                    }
                }
            }
        }
        return resFlag;
    }


    public boolean checkMain() {
        boolean resFlag = true;
        String []lineStrings = procedure.split("\n");
        flag:for(String xString : lineStrings) {
            if(xString.contains("define") || xString.contains("include") || xString.contains("main") || xString.contains("{") || xString.contains("}")) {
                continue;
            }
            for(String yString : lexical_Analysis.keyWord) {
                if(xString.contains(yString)) {
                    continue flag;
                }
            }
            for(String yString : lexical_Analysis.function) {
                if(xString.contains(yString)) {
                    continue flag;
                }
            }
            resString += ("待分析的句子:" + xString + "\n");
            System.out.println("待分析的句子:" + xString);
            mainExpArrayList.add(xString);
            xString = convert(xString);
            resString += ("转换后:" + xString + "\n");
            System.out.println("转换后:" + xString);
            operator_Precedence.processString = "";
            boolean flag = operator_Precedence.check(xString);
            resString += operator_Precedence.processString;
            operator_Precedence.processString = "";
            if(flag) {
                resString += ("Successful!" + "\n" + "\n");
                System.out.println("Successful!");
                System.out.println();
            }else {
                resString += ("failed!" + "\n" + "\n");
                resFlag = false;
                System.out.println("failed!");
                System.out.println();
            }
        }
        return resFlag;
    }
}