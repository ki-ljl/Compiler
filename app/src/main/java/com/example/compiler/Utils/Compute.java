/**
 * Date   : 2021/1/4 21:57
 * Author : KI
 * File   : Compute
 * Desc   : calculate result
 * Motto  : Hungry And Humble
 */
package com.example.compiler.Utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

public class Compute {
    String expression = "";   //要计算的表达式
    String resourceString = "";
    public String processString = "";
    public Lexical_Analysis lexical_Analysis;   //词法分析获取符号表
    Grammatical_Analysis grammatical_Analysis;  //判断是标识符还是数字
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
    Operator_Precedence operator_Precedence = new Operator_Precedence(Vn, Vt, proRule, "");
    public Compute(String x, String resource) {
        expression = x;
        grammatical_Analysis = new Grammatical_Analysis();
        resourceString = resource;
        lexical_Analysis = new Lexical_Analysis(resourceString);
        lexical_Analysis.initNum();
        lexical_Analysis.mainFunc();
    }
    //中缀->后缀
    public String mid2suffix() {
        expression += "#";
        String suffix = "";
        Map<String, ArrayList<String>> priorMap = operator_Precedence.findRe();
        Stack<String> opeStack = new Stack<String>();  //操作符栈
        opeStack.add("#");   //初始压入"#"
        flag:for(int i = 0; i < expression.length(); i++) {  //切割数字，当然也可能是变量
            if(Character.isDigit(expression.charAt(i))) {
                for(int j = i; j < expression.length(); j++) {
                    if(expression.charAt(j) == '.') {
                        continue;
                    }
                    if(!Character.isDigit(expression.charAt(j))) {
                        suffix += (expression.substring(i, j) + " ");
                        i = j - 1;
                        //System.out.println("遇到" + expression.substring(i, j));
                        //showStack(opeStack);
                        break;
                    }
                }
            }else if(Character.isLetter(expression.charAt(i)) || expression.charAt(i) == '_') {
                for(int j = i; j < expression.length(); j++) {
                    char temp = expression.charAt(j);
                    if(!Character.isLetter(temp) && temp != '_') {
                        suffix += (expression.substring(i, j) + " ");
                        i = j - 1;
                        break;
                    }
                }
            }else if(expression.charAt(i) == '(') {
                opeStack.add("(");
                //System.out.println("遇到(时");
                //showStack(opeStack);
            }else if(expression.charAt(i) == ')') {
                String tempString = "";
                //System.out.println("遇到)时");
                //showStack(opeStack);
                while(true && !opeStack.empty()) {
                    tempString = opeStack.pop();
                    if(!tempString.equals("(")) {
                        suffix += (tempString + " ");
                    }else {
                        break;
                    }
                }
            }else if(expression.charAt(i) == '#') {
                //System.out.println("遇到#");
                //showStack(opeStack);
                while (!opeStack.empty()) {
                    String xString = opeStack.pop();
                    suffix += (xString + " ");
                }
            }else {  //当前为一个运算符，先找到这个运算符
                String currentString = "";
                for(int j = i + 1; j <= expression.length(); j++) {
                    if(Vt.contains(expression.substring(i, j))) {
                        if(j + 1 <= expression.length() && Vt.contains(expression.substring(i, j + 1))) {
                            currentString = expression.substring(i, j + 1);
                            i = j;
                        }else {
                            currentString = expression.substring(i,  j);
                            i = j - 1;
                        }
                        if(opeStack.empty()) {
                            opeStack.add(currentString);   //为空直接加入当前运算符
                            continue flag;
                        }else {
                            //当前符号与其它符号的优先关系
                            //System.out.println("遇到" + currentString + "栈的内容为");
                            //showStack(opeStack);
                            ArrayList<String> tempArrayList = priorMap.get(currentString);
                            while(true) {
                                String peekString = opeStack.peek();  //取栈顶运算符(
                                int index = Vt.indexOf(peekString);
                                String priorString = tempArrayList.get(index);
                                if(peekString.equals("(")) {
                                    priorString = ">";
                                }
                                if(priorString.equals(">")) {
                                    //System.out.println("peek = " + peekString);
                                    opeStack.add(currentString);
                                    break;
                                }else {
                                    suffix += (peekString + " ");
                                    opeStack.pop();
                                }
                            }
                            continue flag;
                        }
                    }
                }
            }
        }
        //suffix = suffix.substring(0, suffix.length() - 1);
        return suffix;
    }

    public static String hex2dec(String waitString) {
        //System.out.println(waitString);
        int index = 0;
        while(true) {
            index = waitString.indexOf("0x", 0);
            //System.out.println("index = " + index);
            if(index != -1) {
                index += 2;
                int j = index;
                while (index + 1 < waitString.length() && Character.isDigit(waitString.charAt(index + 1))) {
                    index++;  //
                }
                String subString = waitString.substring(j, index + 1);
                //System.out.println("待转换的十六进制为:" + subString);
                BigInteger bigInteger = new BigInteger(subString, 16);
                String realString = String.valueOf(bigInteger.intValue());
                waitString = waitString.substring(0, j - 2) + realString + waitString.substring(index + 1, waitString.length());
            }else {
                break;
            }
        }
        return waitString;
    }

    public void showStack(Stack<String> stack) {
        for(String xString : stack) {
            System.out.println(xString);
        }
    }

    public String calculateSuffix(String suffixString) {
        Stack<String> digitStack = new Stack<String>();  //存放操作数或者变量
        for(int i = 0; i < suffixString.length(); i++) {
            if(suffixString.charAt(i) == ' ') {
                continue;
            }else if(Character.isDigit(suffixString.charAt(i))) {
                String digitString = "";
                while(suffixString.charAt(i) != ' ') {
                    digitString += String.valueOf(suffixString.charAt(i));
                    i++;
                }   //数字就直接进栈
                digitStack.add(digitString);
            }else if(Character.isLetter(suffixString.charAt(i))) {
                String varString = "";
                while(suffixString.charAt(i) != ' ') {
                    varString += String.valueOf(suffixString.charAt(i));
                    i++;
                }
                //System.out.println("var = " + varString);
                digitStack.add(varString);
            }else if(suffixString.charAt(i) == '#') {
                return digitStack.pop();
            }else {
                String opeString = "";
                while(suffixString.charAt(i) != ' ') {
                    opeString += String.valueOf(suffixString.charAt(i));
                    i++;
                }
                String x1 = "", x2 = "";
                ArrayList<String> opeArrayList = new ArrayList<String>();
                ArrayList<String> resArrayList = new ArrayList<String>();
                switch (opeString) {
                    case "+":
                        x2 = digitStack.pop();
                        x1 = digitStack.pop();
                        //先清空
                        opeArrayList = new ArrayList<String>();
                        resArrayList = new ArrayList<String>();
                        opeArrayList.add(x1);
                        opeArrayList.add(x2);
                        processString += ("当前正在计算：" + x1 + " + " + x2);
                        System.out.print("当前正在计算：" + x1 + " + " + x2);
                        resArrayList = searchVarValue(opeArrayList);//必然为两个常数
                        System.out.print(", " + "即：" + resArrayList.get(0) + " + " + resArrayList.get(1));
                        System.out.println(", " + "计算结果为：" + String.valueOf(Integer.parseInt(resArrayList.get(0)) + Integer.parseInt(resArrayList.get(1))));
                        processString += (", " + "即：" + resArrayList.get(0) + " + " + resArrayList.get(1));
                        processString += (", " + "计算结果为：" + String.valueOf(Integer.parseInt(resArrayList.get(0)) + Integer.parseInt(resArrayList.get(1))) + "\n");
                        digitStack.add(String.valueOf(Integer.parseInt(resArrayList.get(0)) + Integer.parseInt(resArrayList.get(1))));
                        break;
                    case "-":
                        x2 = digitStack.pop();
                        x1 = digitStack.pop();
                        //先清空
                        opeArrayList = new ArrayList<String>();
                        resArrayList = new ArrayList<String>();
                        opeArrayList.add(x1);
                        opeArrayList.add(x2);
                        processString += ("当前正在计算：" + x1 + " - " + x2);
                        System.out.print("当前正在计算：" + x1 + " - " + x2);
                        resArrayList = searchVarValue(opeArrayList);//必然为两个常数
                        System.out.print(", " + "即：" + resArrayList.get(0) + " - " + resArrayList.get(1));
                        System.out.println(", " + "计算结果为：" + String.valueOf(Integer.parseInt(resArrayList.get(0)) - Integer.parseInt(resArrayList.get(1))));
                        processString += (", " + "即：" + resArrayList.get(0) + " - " + resArrayList.get(1));
                        processString += (", " + "计算结果为：" + String.valueOf(Integer.parseInt(resArrayList.get(0)) - Integer.parseInt(resArrayList.get(1))) + "\n");
                        digitStack.add(String.valueOf(Integer.parseInt(resArrayList.get(0)) - Integer.parseInt(resArrayList.get(1))));
                        break;
                    case "*":
                        x2 = digitStack.pop();
                        x1 = digitStack.pop();
                        opeArrayList = new ArrayList<String>();
                        resArrayList = new ArrayList<String>();
                        opeArrayList.add(x1);
                        opeArrayList.add(x2);
                        processString += ("当前正在计算：" + x1 + " * " + x2);
                        System.out.print("当前正在计算：" + x1 + " * " + x2);
                        resArrayList = searchVarValue(opeArrayList);//必然为两个常数
                        System.out.print(", " + "即：" + resArrayList.get(0) + " * " + resArrayList.get(1));
                        System.out.println(", " + "计算结果为：" + String.valueOf(Integer.parseInt(resArrayList.get(0)) * Integer.parseInt(resArrayList.get(1))));
                        processString += (", " + "即：" + resArrayList.get(0) + " * " + resArrayList.get(1));
                        processString += (", " + "计算结果为：" + String.valueOf(Integer.parseInt(resArrayList.get(0)) * Integer.parseInt(resArrayList.get(1))) + "\n");
                        digitStack.add(String.valueOf(Integer.parseInt(resArrayList.get(0)) * Integer.parseInt(resArrayList.get(1))));
                        break;
                    case "/":
                        x2 = digitStack.pop();
                        x1 = digitStack.pop();
                        opeArrayList = new ArrayList<String>();
                        resArrayList = new ArrayList<String>();
                        opeArrayList.add(x1);
                        opeArrayList.add(x2);
                        processString += ("当前正在计算：" + x1 + " / " + x2);
                        System.out.print("当前正在计算：" + x1 + " / " + x2);
                        resArrayList = searchVarValue(opeArrayList);//必然为两个常数
                        System.out.print(", " + "即：" + resArrayList.get(0) + " / " + resArrayList.get(1));
                        System.out.println(", " + "计算结果为：" + String.valueOf(Integer.parseInt(resArrayList.get(0)) / Integer.parseInt(resArrayList.get(1))));
                        processString += (", " + "即：" + resArrayList.get(0) + " / " + resArrayList.get(1));
                        processString += (", " + "计算结果为：" + String.valueOf(Integer.parseInt(resArrayList.get(0)) / Integer.parseInt(resArrayList.get(1))) + "\n");
                        digitStack.add(String.valueOf(Integer.parseInt(resArrayList.get(0)) / Integer.parseInt(resArrayList.get(1))));
                        break;
                    case "%":
                        x2 = digitStack.pop();
                        x1 = digitStack.pop();
                        opeArrayList = new ArrayList<String>();
                        resArrayList = new ArrayList<String>();
                        opeArrayList.add(x1);
                        opeArrayList.add(x2);
                        processString += ("当前正在计算：" + x1 + " % " + x2);
                        System.out.print("当前正在计算：" + x1 + " % " + x2);
                        resArrayList = searchVarValue(opeArrayList);//必然为两个常数
                        System.out.print(", " + "即：" + resArrayList.get(0) + " % " + resArrayList.get(1));
                        System.out.println(", " + "计算结果为：" + String.valueOf(Integer.parseInt(resArrayList.get(0)) % Integer.parseInt(resArrayList.get(1))));
                        processString += (", " + "即：" + resArrayList.get(0) + " % " + resArrayList.get(1));
                        processString += (", " + "计算结果为：" + String.valueOf(Integer.parseInt(resArrayList.get(0)) % Integer.parseInt(resArrayList.get(1))) + "\n");
                        digitStack.add(String.valueOf(Integer.parseInt(resArrayList.get(0)) % Integer.parseInt(resArrayList.get(1))));
                        break;
                    case "=":
                        x2 = digitStack.pop();
                        x1 = digitStack.pop();  //假设必然为一个变量
                        opeArrayList = new ArrayList<String>();
                        resArrayList = new ArrayList<String>();
                        opeArrayList.add(x1);
                        opeArrayList.add(x2);
                        processString += ("当前正在计算：" + x1 + " = " + x2);
                        System.out.print("当前正在计算：" + x1 + " = " + x2);
                        resArrayList = searchVarValue(opeArrayList);//必然为两个常数
                        processString += (", " + "即：" + resArrayList.get(0) + " = " + resArrayList.get(1));
                        System.out.print(", " + "即：" + resArrayList.get(0) + " = " + resArrayList.get(1));
                        //System.out.println(", " + "计算结果为：" + String.valueOf(Integer.parseInt(resArrayList.get(0)) = Integer.parseInt(resArrayList.get(1))));
                        String resString = x1 + " = " + String.valueOf(Integer.parseInt(resArrayList.get(1)));
                        processString += (", " + "计算结果为：" + resString + "\n");
                        System.out.println(", " + "计算结果为：" + resString);
                        digitStack.add(resString);
                        break;
                    case "+=":
                        x2 = digitStack.pop();
                        x1 = digitStack.pop();  //假设必然为一个变量
                        opeArrayList = new ArrayList<String>();
                        resArrayList = new ArrayList<String>();
                        opeArrayList.add(x1);
                        opeArrayList.add(x2);
                        processString += ("当前正在计算：" + x1 + " += " + x2);
                        System.out.print("当前正在计算：" + x1 + " += " + x2);
                        resArrayList = searchVarValue(opeArrayList);//必然为两个常数
                        processString += (", " + "即：" + resArrayList.get(0) + " += " + resArrayList.get(1));
                        System.out.print(", " + "即：" + resArrayList.get(0) + " += " + resArrayList.get(1));
                        String resString1 = x1 + " = " + String.valueOf(Integer.parseInt(resArrayList.get(0)) + Integer.parseInt(resArrayList.get(1)));
                        processString += (", " + "计算结果为：" + resString1 + "\n");
                        System.out.println(", " + "计算结果为：" + resString1);
                        digitStack.add(resString1);
                        break;
                    case "-=":
                        x2 = digitStack.pop();
                        x1 = digitStack.pop();
                        opeArrayList = new ArrayList<String>();
                        resArrayList = new ArrayList<String>();
                        opeArrayList.add(x1);
                        opeArrayList.add(x2);
                        processString += ("当前正在计算：" + x1 + " -= " + x2);
                        System.out.print("当前正在计算：" + x1 + " -= " + x2);
                        resArrayList = searchVarValue(opeArrayList);
                        processString += (", " + "即：" + resArrayList.get(0) + " -= " + resArrayList.get(1));
                        System.out.print(", " + "即：" + resArrayList.get(0) + " -= " + resArrayList.get(1));
                        String resString2 = x1 + " = " + String.valueOf(Integer.parseInt(resArrayList.get(0)) - Integer.parseInt(resArrayList.get(1)));
                        processString += (", " + "计算结果为：" + resString2 + "\n");
                        System.out.println(", " + "计算结果为：" + resString2);
                        digitStack.add(resString2);
                        break;
                    case "/=":
                        x2 = digitStack.pop();
                        x1 = digitStack.pop();
                        opeArrayList = new ArrayList<String>();
                        resArrayList = new ArrayList<String>();
                        opeArrayList.add(x1);
                        opeArrayList.add(x2);
                        processString += ("当前正在计算：" + x1 + " /= " + x2);
                        System.out.print("当前正在计算：" + x1 + " /= " + x2);
                        //System.out.println(x1 + " " + x2);
                        resArrayList = searchVarValue(opeArrayList);//必然为两个常数
                        processString += (", " + "即：" + resArrayList.get(0) + " /= " + resArrayList.get(1));
                        System.out.print(", " + "即：" + resArrayList.get(0) + " /= " + resArrayList.get(1));
                        String resString3 = x1 + " = " + String.valueOf(Integer.parseInt(resArrayList.get(0)) / Integer.parseInt(resArrayList.get(1)));
                        processString += (", " + "计算结果为：" + resString3 + "\n");
                        System.out.println(", " + "计算结果为：" + resString3);
                        digitStack.add(resString3);
                        break;
                    case "*=":
                        x2 = digitStack.pop();
                        x1 = digitStack.pop();
                        opeArrayList = new ArrayList<String>();
                        resArrayList = new ArrayList<String>();
                        opeArrayList.add(x1);
                        opeArrayList.add(x2);
                        processString += ("当前正在计算：" + x1 + " *= " + x2);
                        System.out.print("当前正在计算：" + x1 + " *= " + x2);
                        resArrayList = searchVarValue(opeArrayList);//必然为两个常数
                        processString += (", " + "即：" + resArrayList.get(0) + " *= " + resArrayList.get(1));
                        System.out.print(", " + "即：" + resArrayList.get(0) + " *= " + resArrayList.get(1));
                        String resString4 = x1 + " = " + String.valueOf(Integer.parseInt(resArrayList.get(0)) * Integer.parseInt(resArrayList.get(1)));
                        processString += (", " + "计算结果为：" + resString4 + "\n");
                        System.out.println(", " + "计算结果为：" + resString4);
                        digitStack.add(resString4);
                        break;
                    case "%=":
                        x2 = digitStack.pop();
                        x1 = digitStack.pop();
                        opeArrayList = new ArrayList<String>();
                        resArrayList = new ArrayList<String>();
                        opeArrayList.add(x1);
                        opeArrayList.add(x2);
                        processString += ("当前正在计算：" + x1 + " %= " + x2);
                        System.out.print("当前正在计算：" + x1 + " %= " + x2);
                        resArrayList = searchVarValue(opeArrayList);//必然为两个常数
                        processString += (", " + "即：" + resArrayList.get(0) + " %= " + resArrayList.get(1));
                        System.out.print(", " + "即：" + resArrayList.get(0) + " %= " + resArrayList.get(1));
                        String resString5 = x1 + " = " + String.valueOf(Integer.parseInt(resArrayList.get(0)) % Integer.parseInt(resArrayList.get(1)));
                        processString += (", " + "计算结果为：" + resString5 + "\n");
                        System.out.println(", " + "计算结果为：" + resString5);
                        digitStack.add(resString5);
                        break;
                    case "&&":
                        x2 = digitStack.pop();
                        x1 = digitStack.pop();
                        opeArrayList = new ArrayList<String>();
                        resArrayList = new ArrayList<String>();
                        opeArrayList.add(x1);
                        opeArrayList.add(x2);
                        processString += ("当前正在计算：" + x1 + " && " + x2);
                        System.out.print("当前正在计算：" + x1 + " && " + x2);
                        resArrayList = searchVarValue(opeArrayList);//必然为两个常数
                        x2 = resArrayList.get(0);
                        x1 = resArrayList.get(1);
                        processString += (", " + "即：" + resArrayList.get(0) + " && " + resArrayList.get(1));
                        System.out.print(", " + "即：" + resArrayList.get(0) + " && " + resArrayList.get(1));
                        if(!x1.equals("0") && !x2.equals("0")) {
                            digitStack.add("1");
                            processString += (", " + "计算结果为：" + 1 + "\n");
                            System.out.println(", " + "计算结果为：" + 1);
                        }else {
                            digitStack.add("0");
                            processString += (", " + "计算结果为：" + 0 + "\n");
                            System.out.println(", " + "计算结果为：" + 0);
                        }
                        break;
                    case "||":
                        x2 = digitStack.pop();
                        x1 = digitStack.pop();
                        opeArrayList = new ArrayList<String>();
                        resArrayList = new ArrayList<String>();
                        opeArrayList.add(x1);
                        opeArrayList.add(x2);
                        processString += ("当前正在计算：" + x1 + " || " + x2);
                        System.out.print("当前正在计算：" + x1 + " || " + x2);
                        resArrayList = searchVarValue(opeArrayList);//必然为两个常数
                        x2 = resArrayList.get(0);
                        x1 = resArrayList.get(1);
                        processString += (", " + "即：" + resArrayList.get(0) + " || " + resArrayList.get(1));
                        System.out.print(", " + "即：" + resArrayList.get(0) + " || " + resArrayList.get(1));
                        if(!x1.equals("0") || !x2.equals("0")) {
                            digitStack.add("1");
                            processString += (", " + "计算结果为：" + 1 + "\n");
                            System.out.println(", " + "计算结果为：" + 1);
                        }else {
                            digitStack.add("0");
                            processString += (", " + "计算结果为：" + 0 + "\n");
                            System.out.println(", " + "计算结果为：" + 0);
                        }
                        break;
                    case "!":
                        x1 = digitStack.pop();
                        opeArrayList = new ArrayList<String>();
                        resArrayList = new ArrayList<String>();
                        opeArrayList.add(x1);
                        processString += ("当前正在计算：！" + x1);
                        System.out.print("当前正在计算：！" + x1);
                        resArrayList = searchVarValue(opeArrayList);//必然为两个常数
                        x1 = resArrayList.get(0);
                        processString += (", " + "即：!" + resArrayList.get(0));
                        System.out.print(", " + "即：!" + resArrayList.get(0));
                        if(!x1.equals("0")) {
                            digitStack.add("0");
                            processString += (", " + "计算结果为：" + 0 + "\n");
                            System.out.println(", " + "计算结果为：" + 0);
                        }else {
                            digitStack.add("1");
                            processString += (", " + "计算结果为：" + 1 + "\n");
                            System.out.println(", " + "计算结果为：" + 1);
                        }
                        break;
                    case ">":
                        x2 = digitStack.pop();
                        x1 = digitStack.pop();
                        opeArrayList = new ArrayList<String>();
                        resArrayList = new ArrayList<String>();
                        opeArrayList.add(x1);
                        opeArrayList.add(x2);
                        processString += ("当前正在计算：" + x1 + " > " + x2);
                        System.out.print("当前正在计算：" + x1 + " > " + x2);
                        resArrayList = searchVarValue(opeArrayList);//必然为两个常数
                        x2 = resArrayList.get(1);
                        x1 = resArrayList.get(0);
                        processString += (", " + "即：" + resArrayList.get(0) + " > " + resArrayList.get(1));
                        System.out.print(", " + "即：" + resArrayList.get(0) + " > " + resArrayList.get(1));
                        if(Integer.parseInt(x1) > Integer.parseInt(x2)) {
                            digitStack.add("1");
                            processString += (", " + "计算结果为：" + 1 + "\n");
                            System.out.println(", " + "计算结果为：" + 1);
                        }else {
                            digitStack.add("0");
                            processString += (", " + "计算结果为：" + 0 + "\n");
                            System.out.println(", " + "计算结果为：" + 0);
                        }
                        break;
                    case ">=":
                        x2 = digitStack.pop();
                        x1 = digitStack.pop();
                        opeArrayList = new ArrayList<String>();
                        resArrayList = new ArrayList<String>();
                        opeArrayList.add(x1);
                        opeArrayList.add(x2);
                        processString += ("当前正在计算：" + x1 + " >= " + x2);
                        System.out.print("当前正在计算：" + x1 + " >= " + x2);
                        resArrayList = searchVarValue(opeArrayList);//必然为两个常数
                        x2 = resArrayList.get(1);
                        x1 = resArrayList.get(0);
                        processString += (", " + "即：" + resArrayList.get(0) + " >= " + resArrayList.get(1));
                        System.out.print(", " + "即：" + resArrayList.get(0) + " >= " + resArrayList.get(1));
                        if(Integer.parseInt(x1) >= Integer.parseInt(x2)) {
                            digitStack.add("1");
                            processString += (", " + "计算结果为：" + 1 + "\n");
                            System.out.println(", " + "计算结果为：" + 1);
                        }else {
                            digitStack.add("0");
                            processString += (", " + "计算结果为：" + 0 + "\n");
                            System.out.println(", " + "计算结果为：" + 0);
                        }
                        break;
                    case "<":
                        x2 = digitStack.pop();
                        x1 = digitStack.pop();
                        opeArrayList = new ArrayList<String>();
                        resArrayList = new ArrayList<String>();
                        opeArrayList.add(x1);
                        opeArrayList.add(x2);
                        processString += ("当前正在计算：" + x1 + " < " + x2);
                        System.out.print("当前正在计算：" + x1 + " < " + x2);
                        resArrayList = searchVarValue(opeArrayList);//必然为两个常数
                        x2 = resArrayList.get(1);
                        x1 = resArrayList.get(0);
                        processString += (", " + "即：" + resArrayList.get(0) + " < " + resArrayList.get(1));
                        System.out.print(", " + "即：" + resArrayList.get(0) + " < " + resArrayList.get(1));
                        if(Integer.parseInt(x1) < Integer.parseInt(x2)) {
                            digitStack.add("1");
                            processString += (", " + "计算结果为：" + 1 + "\n");
                            System.out.println(", " + "计算结果为：" + 1);
                        }else {
                            digitStack.add("0");
                            processString += (", " + "计算结果为：" + 0 + "\n");
                            System.out.println(", " + "计算结果为：" + 0);
                        }
                        break;
                    case "<=":
                        x2 = digitStack.pop();
                        x1 = digitStack.pop();
                        opeArrayList = new ArrayList<String>();
                        resArrayList = new ArrayList<String>();
                        opeArrayList.add(x1);
                        opeArrayList.add(x2);
                        processString += ("当前正在计算：" + x1 + " <= " + x2);
                        System.out.print("当前正在计算：" + x1 + " <= " + x2);
                        resArrayList = searchVarValue(opeArrayList);//必然为两个常数
                        x2 = resArrayList.get(1);
                        x1 = resArrayList.get(0);
                        processString += (", " + "即：" + resArrayList.get(0) + " <= " + resArrayList.get(1));
                        System.out.print(", " + "即：" + resArrayList.get(0) + " <= " + resArrayList.get(1));
                        if(Integer.parseInt(x1) <= Integer.parseInt(x2)) {
                            digitStack.add("1");
                            processString += (", " + "计算结果为：" + 1 + "\n");
                            System.out.println(", " + "计算结果为：" + 1);
                        }else {
                            digitStack.add("0");
                            processString += (", " + "计算结果为：" + 0 + "\n");
                            System.out.println(", " + "计算结果为：" + 0);
                        }
                        break;
                    case "<<":
                        x2 = digitStack.pop();
                        x1 = digitStack.pop();
                        //先清空
                        opeArrayList = new ArrayList<String>();
                        resArrayList = new ArrayList<String>();
                        opeArrayList.add(x1);
                        opeArrayList.add(x2);
                        processString += ("当前正在计算：" + x1 + " << " + x2);
                        System.out.print("当前正在计算：" + x1 + " << " + x2);
                        resArrayList = searchVarValue(opeArrayList);//必然为两个常数
                        System.out.print(", " + "即：" + resArrayList.get(0) + " << " + resArrayList.get(1));
                        System.out.println(", " + "计算结果为：" + String.valueOf(Integer.parseInt(resArrayList.get(0)) << Integer.parseInt(resArrayList.get(1))));
                        processString += (", " + "即：" + resArrayList.get(0) + " << " + resArrayList.get(1));
                        processString += (", " + "计算结果为：" + String.valueOf(Integer.parseInt(resArrayList.get(0)) << Integer.parseInt(resArrayList.get(1))) + "\n");
                        digitStack.add(String.valueOf(Integer.parseInt(resArrayList.get(0)) << Integer.parseInt(resArrayList.get(1))));
                        break;
                    case "&":
                        x2 = digitStack.pop();
                        x1 = digitStack.pop();
                        //先清空
                        opeArrayList = new ArrayList<String>();
                        resArrayList = new ArrayList<String>();
                        opeArrayList.add(x1);
                        opeArrayList.add(x2);
                        processString += ("当前正在计算：" + x1 + " & " + x2);
                        System.out.print("当前正在计算：" + x1 + " & " + x2);
                        resArrayList = searchVarValue(opeArrayList);//必然为两个常数
                        System.out.print(", " + "即：" + resArrayList.get(0) + " & " + resArrayList.get(1));
                        System.out.println(", " + "计算结果为：" + String.valueOf(Integer.parseInt(resArrayList.get(0)) & Integer.parseInt(resArrayList.get(1))));
                        processString += (", " + "即：" + resArrayList.get(0) + " & " + resArrayList.get(1));
                        processString += (", " + "计算结果为：" + String.valueOf(Integer.parseInt(resArrayList.get(0)) & Integer.parseInt(resArrayList.get(1))) + "\n");
                        digitStack.add(String.valueOf(Integer.parseInt(resArrayList.get(0)) & Integer.parseInt(resArrayList.get(1))));
                        break;
                    case "|":
                        x2 = digitStack.pop();
                        x1 = digitStack.pop();
                        //先清空
                        opeArrayList = new ArrayList<String>();
                        resArrayList = new ArrayList<String>();
                        opeArrayList.add(x1);
                        opeArrayList.add(x2);
                        processString += ("当前正在计算：" + x1 + " | " + x2);
                        System.out.print("当前正在计算：" + x1 + " | " + x2);
                        resArrayList = searchVarValue(opeArrayList);//必然为两个常数
                        System.out.print(", " + "即：" + resArrayList.get(0) + " | " + resArrayList.get(1));
                        System.out.println(", " + "计算结果为：" + String.valueOf(Integer.parseInt(resArrayList.get(0)) | Integer.parseInt(resArrayList.get(1))));
                        processString += (", " + "即：" + resArrayList.get(0) + " | " + resArrayList.get(1));
                        processString += (", " + "计算结果为：" + String.valueOf(Integer.parseInt(resArrayList.get(0)) | Integer.parseInt(resArrayList.get(1))) + "\n");
                        digitStack.add(String.valueOf(Integer.parseInt(resArrayList.get(0)) | Integer.parseInt(resArrayList.get(1))));
                        break;
                    case ">>":
                        x2 = digitStack.pop();
                        x1 = digitStack.pop();
                        //先清空
                        opeArrayList = new ArrayList<String>();
                        resArrayList = new ArrayList<String>();
                        opeArrayList.add(x1);
                        opeArrayList.add(x2);
                        processString += ("当前正在计算：" + x1 + " >> " + x2);
                        System.out.print("当前正在计算：" + x1 + " >> " + x2);
                        resArrayList = searchVarValue(opeArrayList);//必然为两个常数
                        System.out.print(", " + "即：" + resArrayList.get(0) + " >> " + resArrayList.get(1));
                        System.out.println(", " + "计算结果为：" + String.valueOf(Integer.parseInt(resArrayList.get(0)) >> Integer.parseInt(resArrayList.get(1))));
                        processString += (", " + "即：" + resArrayList.get(0) + " >> " + resArrayList.get(1));
                        processString += (", " + "计算结果为：" + String.valueOf(Integer.parseInt(resArrayList.get(0)) >> Integer.parseInt(resArrayList.get(1))) + "\n");
                        digitStack.add(String.valueOf(Integer.parseInt(resArrayList.get(0)) >> Integer.parseInt(resArrayList.get(1))));
                        break;
                    default:
                        throw new IllegalArgumentException("Unexpected value: " + opeString);
                }
            }
        }
        return "";
    }

    //根据变量名在符号表中查找具体的变量值
    public ArrayList<String> searchVarValue(ArrayList<String> opeArrayList) {
        ArrayList<String> resArrayList = new ArrayList<String>();
        for(String xString : opeArrayList) {
            //System.out.println("xString = " + xString);
            if(grammatical_Analysis.isVar(xString)) {  //变量返回十进制值或者本身(比如+=运算时就有可能有变量)
                //System.out.println("当前正在转换:" + xString);
                for(Vector<String> vector : lexical_Analysis.resVector) {
                    String tempString = vector.get(0);
                    if(tempString.trim().equals(xString.trim())) {
                        if(vector.get(1).equals("var")) {
                            resArrayList.add(xString);
                            break;
                        }else {
                            //有可能是小数3.14
                            String yString = vector.get(2);
                            if(yString.charAt(0) == '0' && yString.length() >= 3) {
                                BigInteger integer = new BigInteger(yString.substring(2, yString.length()), 16);
                                Integer integer2 = integer.intValue();
                                resArrayList.add(String.valueOf(integer2));
                            }else {
                                resArrayList.add(yString);
                            }
                            break;
                        }
                    }
                }
            }else {
                if(xString.charAt(0) == '0' && xString.length() >= 3) {
                    BigInteger integer = new BigInteger(xString.substring(2, xString.length()), 16);
                    Integer integer2 = integer.intValue();
                    resArrayList.add(String.valueOf(integer2));
                }else {
                    resArrayList.add(xString);
                }
            }
        }
		/*System.out.println("返回结果为:");
		for(String xString : resArrayList) {
			System.out.println(xString);
		}*/
        return resArrayList;
    }
}
