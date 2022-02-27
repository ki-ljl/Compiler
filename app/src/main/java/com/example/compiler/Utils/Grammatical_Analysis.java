/**
 * Date   : 2021/1/1 17:53
 * Author : KI
 * File   : Grammatical_Analysis
 * Desc   : Grammatical_Analysis
 * Motto  : Hungry And Humble
 */
package com.example.compiler.Utils;

import java.util.ArrayList;
import java.util.Arrays;

public class Grammatical_Analysis {
    //识别整数的文法:N->D|ND;D->0|1|2|3|...|9
    //消除左递归之后为:N->DS;S->DS|空;D->0|1|2|3|...|9
    ArrayList<Character> Vn = new ArrayList<Character>(Arrays.asList('N', 'D', 'S'));
    ArrayList<Character> Vt = new ArrayList<Character>(Arrays.asList('0', '1', '3', '2', '4', '5', '6', '7', '8', '9'));
    public boolean isNum(String x) {   //x是待分析的字符串，返回true表示其是标准数字
        String temp = "DS";   //存储推导出的数字，每一步都是最左推导
        for(int i = 0; i < x.length(); i++) {
            if(Vt.contains(x.charAt(i))) {
                temp = temp.replace('D', x.charAt(i));
                temp = temp.replace("S", "DS");   //S->DS
            }else {
                return false;
            }
        }
        return true;
    }
    //识别标识符的文法:L->字母加下划线;D->数字;S->SD|SL|L
    //消除左递归:L->字母;D->数字;S->LM;M->DM|LM|空;开始符号为S
    public boolean isVar(String x) {    //分析是否是变量，跟上面文法一样，只是终结符为字母或者下划线
        String temp = "LM";   //存储推导出的变量
        if(Character.isLetter(x.charAt(0)) || x.charAt(0) == '_') {
            temp = temp.replace('L', x.charAt(0));
        }else {
            return false;
        }
        for(int i = 1; i < x.length(); i++) {
            char tempChar = x.charAt(i);
            if(Character.isDigit(tempChar)) {
                temp = temp.replace("M", "DM");  //M->DM
                temp = temp.replace('D', tempChar);//D->数字
            }else if(Character.isLetter(tempChar) || tempChar == '_'){
                temp = temp.replace("M", "LM");  //M->LM
                temp = temp.replace('L', tempChar);//L->字母
            }else {
                return false;
            }
        }
        temp = temp.replace("M", "");  //M->空
        if(temp.equals(x)) {
            return true;
        }
        return false;
    }
}
