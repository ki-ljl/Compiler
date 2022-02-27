/**
 * Date   : 2020/12/18 21:09
 * Author : KI
 * File   : Lexical_Analysis
 * Desc   : Lexical Analysis
 * Motto  : Hungry And Humble
 */
package com.example.compiler.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

public class Lexical_Analysis {
    String procedure = "";     //要分析的源程序字符流
    public ArrayList<String> cutAfter = new ArrayList<String>();  //保存切割后的单词表
    public Vector<Vector> resVector  = new Vector<>();
    public ArrayList<String> special = new ArrayList<>(Arrays.asList("main", "include", "define", "#"));
    public ArrayList<Integer> specialNum = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
    public ArrayList<String> ope = new ArrayList<>(Arrays.asList("+", "-", "*", "/", "%", ">", "<", ">=", "<=", "==",
            "!=", "!", "&&", "||", "<<", ">>", "~", "|", "^", "&", "=", "+=", "-=", "*=", "/=", "?:", ",", "[]"));
    public ArrayList<Integer> opeNum = new ArrayList<>();
    //32个关键字
    public ArrayList<String> keyWord = new ArrayList<>(Arrays.asList("auto", "short", "int", "case", "long", "float", "double", "char", "struct", "union", "enum",
            "typedef", "const", "unsigned", "signed", "extern", "register", "static", "volatile", "void", "if",
            "else", "switch", "for", "do", "while", "goto", "continue", "break", "default", "sizeof", "return"));
    public ArrayList<Integer> keyWordNum = new ArrayList<>();
    public ArrayList<String> function = new ArrayList<String>(Arrays.asList("printf", "scanf", "memset"));
    public ArrayList<Integer> functionNum = new ArrayList<Integer>(Arrays.asList(64, 65, 66));

    public ArrayList<String> delimiter = new ArrayList<>(Arrays.asList(";", "(", ")", "{", "}"));
    public ArrayList<Integer> delimiterNum = new ArrayList<>(Arrays.asList(67, 68, 69, 70, 71));

    public Lexical_Analysis(String x) {
        procedure = x;
        //System.out.println(procedure);
    }

    public void initNum() {
        for(int i = 4; i <= 31; i++) {
            opeNum.add(i);
        }
        for(int i = 32; i <= 63; i++) {
            keyWordNum.add(i);
        }
    }

    public void Process(String x) {
        x = x.trim();
        System.out.println("正在处理:" + x);
        cutAfter.add(x);
        if(x.charAt(0) == '#') {    // #define和#include
            //resMap1.put("#", 3);
            Vector<String> tempVector1 = new Vector<String>();
            tempVector1.add("#");
            tempVector1.add("3");
            resVector.add(tempVector1);
            if(x.charAt(1) == 'd') {
                //resMap1.put("define", 2);
                Vector<String> tempVector2 = new Vector<String>();
                tempVector2.add("define");
                tempVector2.add("2");
                resVector.add(tempVector2);
            }else {
                //resMap1.put("include", 1);
                Vector<String> tempVector3 = new Vector<String>();
                tempVector3.add("include");
                tempVector3.add("1");
                resVector.add(tempVector3);
            }
        }else if(keyWord.contains(x)) {   //完整的关键字
            //resMap1.put(x, keyWordNum.get(keyWord.indexOf(x)));
            int temp = keyWordNum.get(keyWord.indexOf(x));
            Vector<String> tempVector4 = new Vector<String>();
            tempVector4.add(x);
            tempVector4.add(String.valueOf(temp));
            resVector.add(tempVector4);
        }else if(Character.isDigit(x.charAt(0))) {  //有两种情况123以及123,
            char temp = x.charAt(x.length() - 1);
            if(temp == ',') {
                //resMap1.put(",", opeNum.get(ope.indexOf(",")));
                int openum =  opeNum.get(ope.indexOf(","));
                Vector<String> tempVector5 = new Vector<String>();
                tempVector5.add(",");
                tempVector5.add(String.valueOf(openum));
                resVector.add(tempVector5);
                x = x.substring(0, x.length() - 2);
                //resMap2.put(x, "number");
                Vector<String> tempVector6 = new Vector<String>();
                tempVector6.add(x);
                tempVector6.add("number");
                resVector.add(tempVector6);
            }else {
                //resMap2.put(x, "number");
                Vector<String> tempVector6 = new Vector<String>();
                tempVector6.add(x);
                tempVector6.add("number");
                resVector.add(tempVector6);
            }
        }else if(x.contains("(")) {     //scanf(“%f”, &r);以及main()以及memset()等
            if(x.contains("scanf")) {
                //resMap1.put("scanf", 65);  //只是预处理，不考虑输入
                Vector<String> tempVector6 = new Vector<String>();
                tempVector6.add("scanf");
                tempVector6.add("65");
                resVector.add(tempVector6);
            }else if(x.contains("main")) {
                //System.out.println("gggg");
                //resMap1.put("main", 0);
                Vector<String> tempVector6 = new Vector<String>();
                tempVector6.add("main");
                tempVector6.add("0");
                resVector.add(tempVector6);
            }else if(x.contains("memset")) {  //memset(ADDR_START, BUFFER_SIZE, 0x00);
                //resMap1.put("memset", 66);
                Vector<String> tempVector6 = new Vector<String>();
                tempVector6.add("memset");
                tempVector6.add("66");
                resVector.add(tempVector6);
                x = x.substring(7, x.length() - 1);
                String []xTempStrings = x.split(",");   //切割
                for(String yString : xTempStrings) {
                    yString = yString.trim();
                    //System.out.println(yString);
                    if(yString.contains("0x")) {
                        continue;
                    }else {
                        //resMap2.put(yString, "var");  //120表示变量
                        Vector<String> tempVector7 = new Vector<String>();
                        tempVector7.add(yString);
                        tempVector7.add("const");
                        resVector.add(tempVector7);
                    }
                }
            }else {
                x = x.substring(1, x.length() - 1);
                String []xStrings = x.split(" ");
                for(String xString : xStrings) {
                    if(xString.contains("0x") || Character.isDigit(xString.charAt(0))) {
                        Vector<String> teStrings = new Vector<String>();
                        teStrings.add(xString);
                        teStrings.add("number");
                        resVector.add(teStrings);
                    }else if(ope.contains(xString)) {
                        int index = ope.indexOf(xString);
                        Vector<String> teStrings = new Vector<String>();
                        teStrings.add(xString);
                        teStrings.add(String.valueOf(index));
                        resVector.add(teStrings);
                    }else {
                        Vector<String> teStrings = new Vector<String>();
                        teStrings.add(xString);
                        teStrings.add("const");
                        resVector.add(teStrings);
                    }
                }
            }
        }else if(x.contains(".h")) {
            //resMap2.put(x, "headfile");
            Vector<String> tempVector6 = new Vector<String>();
            tempVector6.add(x);
            tempVector6.add("headfile");
            resVector.add(tempVector6);
        }else if(ope.contains(x)) {
            //resMap1.put(x, opeNum.get(ope.indexOf(x)));
            int temp = opeNum.get(ope.indexOf(x));
            Vector<String> tempVector6 = new Vector<String>();
            tempVector6.add(x);
            tempVector6.add(String.valueOf(temp));
            resVector.add(tempVector6);
        }else if(x.charAt(0) == '_' || Character.isLetter(x.charAt(0))){
            //resMap2.put("var", x);
            Vector<String> tempVector6 = new Vector<String>();
            tempVector6.add(x);
            tempVector6.add("const");
            resVector.add(tempVector6);
        }else if(x.equals(";")) {
            Vector<String> tempVector6 = new Vector<String>();
            int temp = delimiterNum.get(delimiter.indexOf(";"));
            tempVector6.add(x);
            tempVector6.add(String.valueOf(temp));
            resVector.add(tempVector6);
        }else if(x.equals("{")) {
            Vector<String> tempVector6 = new Vector<String>();
            int temp = delimiterNum.get(delimiter.indexOf("{"));
            tempVector6.add(x);
            tempVector6.add(String.valueOf(temp));
            resVector.add(tempVector6);
        }else if(x.equals("}")) {
            Vector<String> tempVector6 = new Vector<String>();
            int temp = delimiterNum.get(delimiter.indexOf("}"));
            tempVector6.add(x);
            tempVector6.add(String.valueOf(temp));
            resVector.add(tempVector6);
        }
    }

    public void mainFunc() {       //主函数，开始扫描所有字符
        for(int i = 0; i < procedure.length(); i++) {
            char temp = procedure.charAt(i);
            if(temp == ' ' || temp == '\n' || temp == '\t') {
                continue;    //一开始遇到空格等跳过
            }else {
                int j = i;
                //System.out.println("当前char:" + procedure.charAt(j));
                while(temp != ' ' && temp != '\n' && temp != '\t') {
                    temp = procedure.charAt(++i);
                }  //当前i位置为一个空格符号
                String subString = procedure.substring(j, i);  //可能遇到这种scanf("%s",
                if(subString.contains("(") && !subString.contains(")")) {
                    while(procedure.charAt(i) != ')') {
                        i++;
                    }
                    subString = procedure.substring(j, i + 1);  //i位置为')'
                    System.out.println(subString);
                    if(subString.charAt(subString.length() - 1) == ';') {
                        subString = subString.substring(0, subString.length() - 1);
                        Process(subString);
                        Process(";");
                    }else if(subString.charAt(subString.length() - 1) == ',') {
                        subString = subString.substring(0, subString.length() - 1);
                        Process(subString);
                        Process(",");
                    }else {
                        Process(subString);
                    }
                    continue;
                }else {
                    if(subString.charAt(subString.length() - 1) == ';' && subString.length() > 1) {
                        //System.out.print("subString = " + subString);
                        subString = subString.substring(0, subString.length() - 1);
                        Process(subString);
                        Process(";");
                    }else if(subString.charAt(subString.length() - 1) == ',') {
                        subString = subString.substring(0, subString.length() - 1);
                        Process(subString);
                        Process(",");
                    }else {
                        Process(subString);
                    }
                    //System.out.println(subString.trim());
                    i--;
                    continue;
                }
            }
        }
        //扫描完之后，找到所有宏常量的表达式
        for(int i = 0; i < cutAfter.size(); i++) {
            if(cutAfter.get(i).equals("void")) {
                break;
            }
            if(cutAfter.get(i).equals("#define")) {
                String varString = cutAfter.get(i + 1);   //宏常量
                i += 2;   //当前为宏常量值表达式的第一项
                String temp = "";
                while(!cutAfter.get(i).equals("#define") && !cutAfter.get(i).equals("void")) {
                    temp += (cutAfter.get(i) + " ");
                    i++;
                }  //结束后当前i为#define
                temp = temp.trim();   //宏常量表达式
                for(int j = 0; j < resVector.size(); j++) {
                    Vector vector = resVector.get(j);
                    if(vector.get(0).equals(varString)) {
                        resVector.get(j).set(1, "MACRO");
                        resVector.get(j).add(temp);
                    }
                }
                i--;
            }
        }
        for(int i = 0; i < resVector.size(); i++) {
            Vector<String> vector = resVector.get(i);
            if(vector.size() == 2 && vector.get(1).equals("const")) {
                resVector.get(i).set(1, "var");
            }
        }
    }
}
