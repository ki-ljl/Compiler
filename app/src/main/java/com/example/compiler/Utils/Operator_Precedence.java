/**
 * Date   : 2021/1/4 21:55
 * Author : KI
 * File   : Operator_Precedence
 * Desc   : Operator_Precedence
 * Motto  : Hungry And Humble
 */
package com.example.compiler.Utils;

import com.example.compiler.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Operator_Precedence {
    ArrayList<String> Vt = new ArrayList<String>();
    Map<String, ArrayList<String>> firstVt= new HashMap<String, ArrayList<String>>();
    Map<String, ArrayList<String>> lastVt= new HashMap<String, ArrayList<String>>();
    Map<String, ArrayList<String>> priorMap = new HashMap<String, ArrayList<String>>();
    ArrayList<String> Vn = new ArrayList<String>();
    String []proRule;
    String tab = "";  //标准空格
    public String processString = "";

    public Operator_Precedence(ArrayList<String> Vn, ArrayList<String> Vt, String []proRule, String tab) {
        this.Vn = Vn;
        this.Vt = Vt;
        this.tab = tab;
        this.proRule = proRule;
    }

    public Map<String, ArrayList<String>> findFirstVt() {
        Map<String, ArrayList<String>> firstVt= new HashMap<String, ArrayList<String>>();
        for(String xString : Vn) {
            firstVt.put(xString, new ArrayList<String>());
        }
        for(int i = 0; i <proRule.length; i++) {
            String temp1 = "";
            String temp2 = "";
            String temp3 = "";
            if(proRule[i].length() >= 5) {   //可能出现A->a...或者A->Ba...
                temp1 = String.valueOf(proRule[i].charAt(0));//A
                temp2 = String.valueOf(proRule[i].charAt(3));//B
                temp3 = "";
                if(Vn.contains(temp2)) {  //A->Ba...
                    for(int j = 5; j <= proRule[i].length(); j++) {
                        if(Vt.contains(proRule[i].substring(4, j))) {
                            if(j == proRule[i].length()) {
                                temp3 = proRule[i].substring(4, j);
                                break;
                            }else {
                                if(Vt.contains(proRule[i].substring(4, j + 1))) {
                                    temp3 = proRule[i].substring(4, j + 1);
                                }else {
                                    temp3 = proRule[i].substring(4, j);
                                }
                                break;
                            }
                        }
                    }
                    if(!temp3.equals("")) {
                        firstVt.get(temp1).add(temp3);
                    }
                }else {   //A->a...
                    temp3 = "";
                    for(int j = 4; j <= proRule[i].length(); j++) {
                        if(Vt.contains(proRule[i].substring(3, j))) {
                            if(j == proRule[i].length()) {
                                temp3 = proRule[i].substring(3, j);
                                break;
                            }else {
                                if(Vt.contains(proRule[i].substring(3, j + 1))) {
                                    temp3 = proRule[i].substring(3, j + 1);
                                }else {
                                    temp3 = proRule[i].substring(3, j);
                                }
                                break;
                            }
                        }
                    }
                    if(!temp3.equals("")) {
                        firstVt.get(temp1).add(temp3);
                    }
                }
            }else {     //长度小于5中只有一种可能，即A->a...
                temp1 = String.valueOf(proRule[i].charAt(0));  //A
                temp3 = "";
                for(int j = 4; j <= proRule[i].length(); j++) {
                    if(Vt.contains(proRule[i].substring(3, j))) {
                        if(j == proRule[i].length()) {
                            temp3 = proRule[i].substring(3, j);
                            break;
                        }else {
                            if(Vt.contains(proRule[i].substring(3, j + 1))) {
                                temp3 = proRule[i].substring(3, j + 1);
                            }else {
                                temp3 = proRule[i].substring(3, j);
                            }
                            break;
                        }
                    }
                }
                if(!temp3.equals("")) {
                    firstVt.get(temp1).add(temp3);
                }
            }
        }
        for(int k = 0; k < proRule.length; k++) {
            for(int i = 0; i <proRule.length; i++) {   //A->B...
                String temp1 = String.valueOf(proRule[i].charAt(0));
                String temp2 = String.valueOf(proRule[i].charAt(3));
                if(Vn.contains(temp2)) {
                    if(!temp1.equals(temp2)) {
                        ArrayList<String> temp = firstVt.get(temp2);
                        for(int j = 0; j < temp.size(); j++) {
                            if(!firstVt.get(temp1).contains(temp.get(j))) {
                                firstVt.get(temp1).add(temp.get(j));  //A->B，则B的firstVt全部加入A中
                            }
                        }
                    }
                }
            }
        }
        //去重
        for(Map.Entry<String, ArrayList<String>> entry : firstVt.entrySet()) {
            String keyString = entry.getKey();
            ArrayList<String> list = entry.getValue();
            ArrayList<String> tempArrayList = new ArrayList<String>();
            for(int i = 0; i < list.size(); i++) {
                String tempString = list.get(i);
                if(!tempArrayList.contains(tempString)) {
                    tempArrayList.add(tempString);
                }
            }
            firstVt.put(keyString, tempArrayList);
        }
        return firstVt;
    }

    //求lastVt集合
    public Map<String, ArrayList<String>> findLastVt() {
        Map<String, ArrayList<String>> lastVt= new HashMap<String, ArrayList<String>>();
        for(String xString : Vn) {
            lastVt.put(xString, new ArrayList<String>());
        }
        for(int i = 0; i <proRule.length; i++) {
            String temp1 = "";
            String temp2 = "";
            String temp3 = "";
            if(proRule[i].length() >= 5) {  //A->...a或者A->...aB
                temp1 = String.valueOf(proRule[i].charAt(0));//A
                temp2 = String.valueOf(proRule[i].charAt(proRule[i].length() - 1));//B
                temp3 = "";
                if(Vn.contains(temp2)) {   //说明为A->...aB
                    for(int j = proRule[i].length() - 2; j >= 3; j--) {
                        if(Vt.contains(proRule[i].substring(j, proRule[i].length() - 1))) {
                            if(j == 3) {
                                temp3 = proRule[i].substring(j, proRule[i].length() - 1);
                                break;
                            }else {
                                if(Vt.contains(proRule[i].substring(j - 1, proRule[i].length() - 1))) {
                                    temp3 = proRule[i].substring(j - 1, proRule[i].length() - 1);
                                }else {
                                    temp3 = proRule[i].substring(j, proRule[i].length() - 1);
                                }
                                break;
                            }
                        }
                    }
                    if(!temp3.equals("")) {
                        lastVt.get(temp1).add(temp3);
                    }
                }else {   //说明为A->...a
                    temp3 = "";
                    for(int j = proRule[i].length() - 1 ; j >=3; j--) {
                        if(Vt.contains(proRule[i].substring(j, proRule[i].length()))) {
                            if(j == 3) {
                                temp3 = proRule[i].substring(j, proRule[i].length());
                                break;
                            }else {
                                if(Vt.contains(proRule[i].substring(j - 1, proRule[i].length()))) {
                                    temp3 = proRule[i].substring(j - 1, proRule[i].length());
                                }else {
                                    temp3 = proRule[i].substring(j, proRule[i].length());
                                }
                                break;
                            }
                        }
                    }
                    if(!temp3.equals("")) {
                        lastVt.get(temp1).add(temp3);
                    }
                }
            }else {     //A->....a这种情况
                temp1 = String.valueOf(proRule[i].charAt(0));  //A
                temp2 = "";
                for(int j = proRule[i].length() - 1 ; j >=3; j--) {
                    if(Vt.contains(proRule[i].substring(j, proRule[i].length()))) {
                        if(j == 3) {
                            temp2 = proRule[i].substring(j, proRule[i].length());
                            break;
                        }else {
                            if(Vt.contains(proRule[i].substring(j - 1, proRule[i].length()))) {
                                temp2 = proRule[i].substring(j - 1, proRule[i].length());
                            }else {
                                temp2 = proRule[i].substring(j, proRule[i].length());
                            }
                            break;
                        }
                    }
                }
                if(!temp2.equals("")) {
                    lastVt.get(temp1).add(temp2);
                }
            }
        }
        for(int k = 0; k < proRule.length; k++) {   //一定要多次扫描
            for(int i = 0; i <proRule.length; i++) {
                String temp1 = String.valueOf(proRule[i].charAt(0));
                String temp2 = String.valueOf(proRule[i].charAt(proRule[i].length() - 1));
                if(Vn.contains(temp2)) {
                    if(temp1.equals(temp2)) {
                        continue;
                    }else {
                        ArrayList<String> temp = lastVt.get(temp2);
                        for(int j = 0; j < temp.size(); j++) {
                            if(!lastVt.get(temp1).contains(temp.get(j))) {
                                lastVt.get(temp1).add(temp.get(j));  //A->...B，则B的lastVt全部加入A中
                            }
                        }
                    }
                }
            }
        }
        //去重
        for(Map.Entry<String, ArrayList<String>> entry : lastVt.entrySet()) {
            String keyString = entry.getKey();
            ArrayList<String> list = entry.getValue();
            ArrayList<String> tempArrayList = new ArrayList<String>();
            for(int i = 0; i < list.size(); i++) {
                String tempString = list.get(i);
                if(!tempArrayList.contains(tempString)) {
                    tempArrayList.add(tempString);
                }
            }
            lastVt.put(keyString, tempArrayList);
        }
        return lastVt;
    }

    public Map<String, ArrayList<String>> findRe() {    //根据两个集合建立优先关系表
        firstVt = findFirstVt();
        lastVt = findLastVt();

        Map<String, ArrayList<String>> priorMap = new HashMap<String, ArrayList<String>>();
        for(int i = 0; i < Vt.size(); i++) {
            priorMap.put(Vt.get(i), new ArrayList<String>());
            for(int j = 0; j < Vt.size(); j++) {
                priorMap.get(Vt.get(i)).add(" ");   //先占位
            }
        }
        //寻找等于关系
		/*for(int i = 0; i < proRule.length; i++) {
			String tempString = proRule[i];
			if(tempString.length() <= 4) {
				continue;
			}else {   //产生式右边至少两个符号且不可能两个非终结符连续
				for(int j = 3; j <= tempString.length(); j++) {
					String char1 = String.valueOf(tempString.charAt(j));
					if(Vn.contains(char1)) {
						continue;
					}else {   //当前位置j为一个终结符的开始符号
						if(j == tempString.length()) {
							break;   //A->Ba
						}
						int k = j + 1;
						while(!Vt.contains(tempString.substring(j, k))) {
							k++;
						}  //找到第一个终结符
						if(k >= tempString.length()) {
							break;   //右边只有一个终结符
						}else {
							String VtString1 = tempString.substring(j, k);  //第一个终结符
							String vnString = String.valueOf(tempString.charAt(k));
							if(Vn.contains(vnString)) {
								if(k + 1 == tempString.length()) {
									break;    //A->...aB
								}else {
									k++;
									int m = k + 1;
									while(!Vt.contains(tempString.substring(k, m))) {
										m++;
									}
									String VtString2 = tempString.substring(k, m);
									int index = Vt.indexOf(VtString2);
									priorMap.get(VtString1).set(index, "=");
								}
							}else {
								k++;
								int m = k;
								while(!Vt.contains(tempString.substring(k, m))) {
									m++;
								}
								String VtString2 = tempString.substring(k, m);
								int index = Vt.indexOf(VtString2);
								priorMap.get(VtString1).set(index, "=");
							}
						}
					}
				}
			}
		}*/
        //等于关系
        int index1 = Vt.indexOf("#");
        int index2 = Vt.indexOf(")");
        priorMap.get("#").set(index1, "=");
        priorMap.get("(").set(index2, "=");
        //寻找小于关系:A->...aB...则a<任意firstVt(B)
        for(int i = 0; i < proRule.length; i++) {
            String tempString = proRule[i];
            int j = 3;
            for(j = 3; j < tempString.length(); j++) {
                String xString = String.valueOf(tempString.charAt(j));
                if(Vn.contains(xString)) {
                    continue;
                }else {
                    break;
                }
            }//当前位置j为一个终结符的开始
            if(j == tempString.length()) {
                continue;
            }else {  //当前位置j为一个终结符的开始符号
                boolean flag = false;
                String VtString = "";
                String yString = "";
                int k = j + 1;
                for(k = j + 1; k <= tempString.length(); k++) {
                    if(Vt.contains(tempString.substring(j, k))) {
                        if(k == tempString.length()) {//A->...+
                            flag = true;
                            break;
                        }else if(!Vt.contains(tempString.substring(j, k + 1))) {
                            //A->...+B
                            VtString = tempString.substring(j, k);
                            yString = String.valueOf(tempString.charAt(k));
                            break;
                        }else if(Vt.contains(tempString.substring(j, k + 1)) && k + 1 == tempString.length()){
                            //A->...&&
                            flag = true;
                            break;
                        }else if(Vt.contains(tempString.substring(j, k + 1)) && k + 1 < tempString.length()) {
                            //A->....&&B
                            VtString = tempString.substring(j, k + 1);
                            yString = String.valueOf(tempString.charAt(k + 1));
                            break;
                        }
                    }
                }
                if(flag) {
                    continue;   //终结符后面没有非终结符跟着
                }else {
                    //VtString为终结符,yString为非终结符
                    if(Vn.contains(yString)) {
                        //System.out.println("vtString = " + VtString + " yString = " + yString);
                        ArrayList<String> tempArrayList = firstVt.get(yString); //VtString小于list中所有符号
                        for(int m = 0; m < tempArrayList.size(); m++) {
                            int index = Vt.indexOf(tempArrayList.get(m));
                            priorMap.get(VtString).set(index, "<");
                        }
                    }
                }
            }
        }
        //寻找大于关系:A->...Bb则任意lastVt(B)>b
        last:for(int i = 0; i < proRule.length; i++) {
            String tempString = proRule[i];
            if(tempString.length() <= 4) {
                continue;
            }
            int j = 3;
            for(j = 3; j < tempString.length(); j++) {
                String vnString = String.valueOf(tempString.charAt(j));
                if(!Vn.contains(vnString)) {
                    continue;
                }else {  //当前位置j为一个非终结符
                    String vnString2 = String.valueOf(tempString.charAt(j));
                    if(j + 1 == tempString.length()) {
                        continue last;
                    }else {
                        //位置j后面必然有一个终结符
                        String vtString = "";
                        int k = j + 2;
                        for(k = j + 2; k <= tempString.length(); k++) {
                            if(Vt.contains(tempString.substring(j + 1, k))) {
                                if(k == tempString.length()) {
                                    //A->..B+
                                    vtString = tempString.substring(j + 1, k);
                                    break;
                                }else if(k + 1 <= tempString.length()) {
                                    if(Vt.contains(tempString.substring(j + 1, k + 1))) {
                                        vtString = tempString.substring(j + 1, k + 1);
                                    }else {
                                        vtString = tempString.substring(j + 1, k);
                                    }
                                    break;
                                }
                            }
                        }
                        //System.out.println(vnString2 + " " + vtString);
                        int index = Vt.indexOf(vtString);  //任意List中符号>VtString
                        ArrayList<String> tempArrayList = lastVt.get(vnString2);
                        for(int m = 0; m < tempArrayList.size(); m++) {
                            priorMap.get(tempArrayList.get(m)).set(index, ">");
                        }
                    }
                }
            }
        }
        return priorMap;
    }

    public String createTab(int i) {
        String res = "";
        for(int k = 0; k < i; k++) {
            res += tab;
        }
        return res;
    }

    //#N1a1N2a2…NnanNn+1#，其中Ni为非终结符或者空
    public boolean check(String expString) {
        expString = "#" + expString + "#";
        priorMap = findRe();  //优先关系表
        int index = 0;
        String symbolStack = "#";  //符号栈
        String priorString = "";  //优先关系
        String waitInput = ""; //待输入符
        String restString = expString.substring(1, expString.length()); //剩余串
        String actionString = ""; //动作
        processString += ("step" + createTab(2) + "symbolStack" + createTab(8) +  "prior" + createTab(2)
                + "waitInput" + createTab(2) + "restString" + createTab(10)
                + "action" + "\n");
        System.out.println("step  " + "symbolStack        " + "prior  "
                + "waitInput  " + "restString                  " + "action");
        while(true) {
            String char1 = "";   //栈顶符号
            String char2 = "";   //待输入符号
            //寻找待输入符号
            for(int i = 1; i <= restString.length(); i++) {
                if(Vt.contains(restString.substring(0, i))) {
                    if(i == restString.length()) {
                        char2 = restString.substring(0, i);  //待输入符号
                        break;
                    }else {
                        if(Vt.contains(restString.substring(0, i + 1))) {
                            char2 = restString.substring(0, i + 1);  //待输入符号
                        }else {
                            char2 = restString.substring(0, i);  //待输入符号
                        }
                        break;
                    }
                }
            }
            waitInput = char2;
            //寻找栈顶符号
            char1 = "";
            for(int i = symbolStack.length() - 1; i >= 0; i--) {
                if(!char1.equals("")) {
                    break;
                }
                //要先找到第一个不为Z的地方
                if(symbolStack.charAt(i) == 'z') {
                    continue;
                }else {
                    int j = i;  //j位置处不为Z
                    for(int k = j; k >= 0; k--) {
                        if(Vt.contains(symbolStack.substring(k, j + 1))) {
                            if(k == 0) {
                                char1 = symbolStack.substring(k, j + 1);  //栈顶符号
                                break;
                            }else {
                                if(Vt.contains(symbolStack.substring(k - 1, j + 1))) {
                                    char1 = symbolStack.substring(k - 1, j + 1);  //栈顶符号
                                }else {
                                    char1 = symbolStack.substring(k, j + 1);  //栈顶符号
                                }
                                break;
                            }
                        }
                    }
                }
            }
            //System.out.println("栈顶符号 = " + char1);
            ArrayList<String> tempArrayList = priorMap.get(char1);
            int index1 = Vt.indexOf(char2);
            //System.out.println("待输入符号 = " + char2);
            priorString = tempArrayList.get(index1);
            restString = restString.substring(char2.length(), restString.length());
            //System.out.println("rest:" + restString);
            //System.out.println("waitInput:" + char2);
            if(priorString.equals("<") || priorString.equals("=")) {
                if(symbolStack.equals("#z") && waitInput.equals("#")) {
                    actionString = "接受";
                    showProcess(index, symbolStack, priorString, waitInput, restString, actionString);
                    return true;
                }
                actionString = "移进";
                showProcess(index, symbolStack, priorString, waitInput, restString, actionString);
                symbolStack += waitInput;
            }else {
                actionString = "规约";
                showProcess(index, symbolStack, priorString, waitInput, restString, actionString);
                restString = waitInput + restString;
                //在符号栈中向左遍历，寻找到第一个小于栈顶符号的符号
                ArrayList<String> tempArrayList2 = new ArrayList<String>();
                //寻找所有符号
                //System.out.println(symbolStack);
                //先寻找栈顶的所有符号
                flag:for(int t = symbolStack.length() - 1; t>= 0; t--) {
                    if(symbolStack.charAt(t) == 'z') {
                        continue;
                    }else {
                        //t为一个终结符的开始位置
                        int j = t + 1;
                        for(int k = t; k >=0; k--) {
                            if(Vt.contains(symbolStack.substring(k, j))) {
                                if(k == 0) {
                                    tempArrayList2.add(symbolStack.substring(k, j));
                                    continue flag;
                                }else {
                                    if(Vt.contains(symbolStack.substring(k - 1, j))) {
                                        tempArrayList2.add(symbolStack.substring(k - 1, j));
                                        t--;
                                        continue flag;
                                    }else {
                                        tempArrayList2.add(symbolStack.substring(k, j));
                                        continue flag;
                                    }
                                }
                            }
                        }
                        //System.out.println(symbolStack.substring(j, k));
                    }
                }
				/*System.out.println("输出一下" + symbolStack + "所有符号:");
				for(String xString : tempArrayList2) {
					System.out.print(xString + " ");
				}
				System.out.println();*/
                //找到了栈顶的所有符号，接着判断大小关系
                int m = 0;
                for(m = 0; m < tempArrayList2.size() - 1; m++) {
                    String text1 = tempArrayList2.get(m);  //i
                    String text2 = tempArrayList2.get(m + 1);  //#
                    ArrayList<String> priorArrayList = priorMap.get(text2);
                    int index2 = Vt.indexOf(text1);
                    if(priorArrayList.get(index2).equals("<")) {
                        break;
                    }  //从0到m的所有符号应该出现在句柄中
                }
                String reducedString = "";
                String reducedString2 = "";
                int flagX = 0;
                int flagY = 0;
                if(m >= 1) {   //至少两个符号
                    //两个符号的有两种可能：#Z+(Z)必然规约(Z);若为#Z(Z)还有可能规约Z(Z);
                    int x1 = symbolStack.lastIndexOf(tempArrayList2.get(0), symbolStack.length() - 1);
                    int x2 = symbolStack.lastIndexOf(tempArrayList2.get(m), x1 - 1);
                    reducedString = symbolStack.substring(x2, symbolStack.length());
                    if(x2 >= 1 && symbolStack.charAt(x2 - 1) == 'z') {
                        reducedString2 = symbolStack.substring(x2 - 1, symbolStack.length());
                    }
                }else {
                    //只有一个符号:#&&+Z只能规约+Z;若为#&&Z+Z可能会规约Z+Z也可能只是规约+Z
                    String lastFlag = tempArrayList2.get(0);  //该规约的符号
                    int _index = symbolStack.lastIndexOf(lastFlag, symbolStack.length() - 1);
                    reducedString = symbolStack.substring(_index, symbolStack.length());
                    if(_index >= 1 && symbolStack.charAt(_index - 1) == 'z') {
                        reducedString2 = symbolStack.substring(_index - 1, symbolStack.length());
                    }
                    //reducedString = symbolStack.substring(1, symbolStack.length());
                }
                //遍历所有产生式的右部，看是否有哪一个产生式右部包含所有的符号
                int i = 0;
                xx:for(i = 0; i < proRule.length; i++) {
                    //只要右边部分
                    String tempString = proRule[i].substring(3, proRule[i].length());
                    //先把产生式右边的所有终结符切割出来
                    boolean flag = false;
                    if(isMatch(tempString, reducedString) || isMatch(tempString, reducedString2)) {
                        flag = true;
                    }
                    if(flag) {
                        int length = tempString.length();
                        symbolStack = symbolStack.substring(0, symbolStack.length() - length);
                        symbolStack += "z";
                        break;
                    }else {
                        continue;
                    }
                }
                if(i == proRule.length) {
                    actionString = "拒绝";
                    restString = restString.substring(waitInput.length(), restString.length());
                    showProcess(index, symbolStack, priorString, waitInput, restString, actionString);
                    return false;
                }
            }
            index++;
        }
    }

    public void showProcess(int index, String symbolStack, String priorString, String waitInput, String restString, String actionString) {
        processString += (tab + index);
        System.out.print(" " + index);
        if(index < 10) {
            processString += (createTab(4) + symbolStack);
            System.out.print("    " + symbolStack);
        }else {
            processString += (createTab(3) + symbolStack);
            System.out.print("   " + symbolStack);
        }
        for(int i = 0; i < 19 - symbolStack.length(); i++) {
            processString += (tab);
            System.out.print(" ");
        }
        processString += (createTab(2) + priorString + createTab(4));
        System.out.print("  " + priorString + "    ");
        if(waitInput.length() > 1) {
            processString += (createTab(3) + waitInput + createTab(6));
            System.out.print("   " + waitInput + "      ");
        }else {
            processString += (createTab(4) + waitInput + createTab(6));
            System.out.print("    " + waitInput + "      ");
        }
        processString += restString;
        System.out.print(restString);
        for(int i = 0; i < 20 - restString.length(); i++) {
            processString += (tab);
            System.out.print(" ");
        }
        processString += (tab + actionString + "\n");
        System.out.println(" " + actionString);
    }

    public boolean isMatch(String rightString, String reducedString) {
        String tempString = "";
        for(int i = 0; i < rightString.length(); i++) {
            if(Character.isUpperCase(rightString.charAt(i))) {
                tempString += "z";
            }else {
                tempString += String.valueOf(rightString.charAt(i));
            }
        }
        if(reducedString.trim().equals(tempString)) {
            return true;
        }else {
            return false;
        }
    }
}

