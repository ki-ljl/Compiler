package com.example.compiler;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.compiler.Utils.CheckAllExpression;
import com.example.compiler.Utils.Compute;
import com.example.compiler.Utils.Lexical_Analysis;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    private RadioGroup rg_tab_bar;
    private RadioButton rb_open;
    private RadioButton rb_lexer;
    private RadioButton rb_grammar;
    private RadioButton rb_calculate;
    private View div;
    private EditText editText;
    private EditText editTextComp;
    private Lexical_Analysis lexical_analysis;
    private PopupWindow popupWindow;
    public String resourceString = "";   //源程序
    public String tab = "";
    public Vector<Vector> resVectors = new Vector<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        rg_tab_bar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_open:
                        rb_open.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                View view=getLayoutInflater().inflate(R.layout.layout_pop,null);
                                TextView textView1 = view.findViewById(R.id.s1);
                                textView1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        popupWindow.dismiss();
                                        resourceString = getResources().getString(R.string.source_1);
                                        //lexical_analysis.show();
                                        SpannableStringBuilder builder = convert(resourceString);
                                        editText.setText(builder);
                                    }
                                });
                                TextView textView2 = view.findViewById(R.id.s2);
                                textView2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        popupWindow.dismiss();
                                        resourceString = getResources().getString(R.string.source_2);
                                        //lexical_analysis.show();
                                        SpannableStringBuilder builder = convert(resourceString);
                                        editText.setText(builder);
                                    }
                                });
                                TextView textView3 = view.findViewById(R.id.s3);
                                textView3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        popupWindow.dismiss();
                                        resourceString = getResources().getString(R.string.source_3);
                                        SpannableStringBuilder builder = convert(resourceString);
                                        editText.setText(builder);
                                    }
                                });
                                popupWindow=new PopupWindow(view, rb_open.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
                                popupWindow.setOutsideTouchable(true);
                                popupWindow.setFocusable(true);
                                popupWindow.showAsDropDown(rb_open);
                            }
                        });
                        break;
                    case R.id.rb_lexer:    //词法分析
                        Lexical();
                        break;
                    case R.id.rb_grammar:    //语法分析每一个跟宏有关的式子是否是合法的
                        Grammar();
                        break;
                    case R.id.rb_calculate:    //计算并替换所有表达式
                        try {
                            Calculate();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });
    }

    private void bindViews() {
        tab = getResources().getString(R.string.tab);
        rg_tab_bar =  findViewById(R.id.rg_tab_bar);
        rb_open =  findViewById(R.id.rb_open);
        rb_lexer =  findViewById(R.id.rb_lexer);
        rb_grammar =  findViewById(R.id.rb_grammar);
        rb_calculate =  findViewById(R.id.rb_calculate);
        div = findViewById(R.id.div_tab_bar);
        editText = findViewById(R.id.edit);
        editTextComp = findViewById(R.id.comp);
    }

    private SpannableStringBuilder convert(String x) {
        String yy = getResources().getString(R.string.source_1);
        String []yyStrings = x.split("\n");
        for(int i = 0; i < yyStrings.length; i++) {
            if(yyStrings[i].contains("main")) {
                break;
            }
            else {
                yyStrings[i] = yyStrings[i].trim();
            }
        }
        x = "";
        for(int i = 0; i <yyStrings.length; i++) {
            x += (yyStrings[i] + "\n");
        }
        lexical_analysis = new Lexical_Analysis(yy);
        lexical_analysis.initNum();
        lexical_analysis.mainFunc();
        SpannableStringBuilder builder = new SpannableStringBuilder(x);
        for(int i = 0; i < x.length(); i++) {
            char y = x.charAt(i);
            if (y == '{' || y == '}' || y == '(' || y == ')') {
                builder.setSpan(new ForegroundColorSpan(Color.RED), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//指定索引a到b-1的字符
            }
            if (y == '#' && x.charAt(i + 1) == 'i') {
                builder.setSpan(new ForegroundColorSpan(Color.rgb(204, 51, 204)), i, i + 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//指定索引a到b-1的字符
                int j = i + 8;
                while (x.charAt(i) != '>') {
                    i++;
                }
                builder.setSpan(new ForegroundColorSpan(Color.rgb(153, 102, 113)), j + 1, i, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (y == '#' && x.charAt(i + 1) == 'd') {
                builder.setSpan(new ForegroundColorSpan(Color.rgb(204, 51, 204)), i, i + 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            }
        }
        for(String xString : lexical_analysis.keyWord) {
            int index = 0;
            while(index != -1) {
                index = x.indexOf(xString, index + xString.length());
                if(index != -1) {
                    builder.setSpan(new ForegroundColorSpan(Color.rgb(26, 107, 230)), index, index + xString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        for(String xString : lexical_analysis.keyWord) {
            int index = 0;
            while(index != -1) {
                index = x.indexOf(xString, index + xString.length());
                if(index != -1) {
                    builder.setSpan(new ForegroundColorSpan(Color.rgb(26, 107, 230)), index, index + xString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        int index1 = x.indexOf("main");
        builder.setSpan(new ForegroundColorSpan(Color.rgb(213, 213, 43)), index1, index1 + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        for(String xString : lexical_analysis.function) {
            int index = x.indexOf(xString);
            if(index != -1) {
                builder.setSpan(new ForegroundColorSpan(Color.rgb(213, 213, 43)), index, index + xString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        //宏常量
        ArrayList<String> temp = new ArrayList<>();
        for(Vector<String> vector : lexical_analysis.resVector) {
            if(vector.get(1).equals("MACRO")) {
                temp.add(vector.get(0));
            }
        }
        System.out.println("temp = " + temp.size());
        for(String xString : temp) {
            int index = 0 - xString.length();
            while(index != -1) {
                index = x.indexOf(xString, index + xString.length());
                if(index != -1) {
                    builder.setSpan(new ForegroundColorSpan(Color.rgb(26, 107, 230)), index, index + xString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return builder;
    }

    //词法分析
    private void Lexical() {
        lexical_analysis = new Lexical_Analysis(resourceString);
        lexical_analysis.initNum();
        lexical_analysis.mainFunc();
        String finalString = "";
        for(Vector vector : lexical_analysis.resVector) {
            //System.out.print("(");
            String temp = "(";
            for(int j = 0; j < vector.size() - 1; j++) {
                //System.out.print(vector.get(j) + "," + " ");
                temp += (vector.get(j) + "," + " ");
            }
            //System.out.println(vector.get(vector.size() - 1) + ")");
            temp += (vector.get(vector.size() - 1) + ")" + "\n");
            finalString += temp;
        }
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) editText.getLayoutParams();
        lp.height = 500;
        editText.setLayoutParams(lp);
        RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams) editTextComp.getLayoutParams();
        lp1.height = 2000;
        editTextComp.setLayoutParams(lp1);
        finalString = "词法分析结果为:" + "\n" + finalString;
        SpannableStringBuilder builder1 = new SpannableStringBuilder(finalString);
        builder1.setSpan(new ForegroundColorSpan(Color.RED), 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//指定索引a到b-1的字符
        editTextComp.setText(builder1);
    }
    //语法分析
    private void Grammar() {
        CheckAllExpression checkAllExpression = new CheckAllExpression(resourceString, tab);
        checkAllExpression.checkMacro();
        checkAllExpression.checkMain();
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) editText.getLayoutParams();
        lp.height = 500;
        editText.setLayoutParams(lp);
        RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams) editTextComp.getLayoutParams();
        lp1.height = 1800;
        editTextComp.setLayoutParams(lp1);
        String finalString = checkAllExpression.resString;
        finalString = "语法分析结果为:" + "\n" + finalString;
        SpannableStringBuilder builder1 = new SpannableStringBuilder(finalString);
        builder1.setSpan(new ForegroundColorSpan(Color.RED), 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//指定索引a到b-1的字符
        editTextComp.setText(builder1);
    }

    private void Calculate() throws FileNotFoundException {
        lexical_analysis = new Lexical_Analysis(resourceString);
        lexical_analysis.initNum();
        lexical_analysis.mainFunc();
        resVectors = lexical_analysis.resVector;
        String showProcess = "";
        CheckAllExpression checkAllExpression = new CheckAllExpression(resourceString, "");
        checkAllExpression.checkMacro();
        checkAllExpression.checkMain();

        Map<String, String> expMap = checkAllExpression.expMap;  //存储表达式
        for(Map.Entry<String, String> entry : expMap.entrySet()) {
            String keyString = entry.getKey();
            String valueString = entry.getValue();
            //System.out.println(keyString + ":" + valueString);
            if(!keyString.equals("mainExp")) {
                String waitString = valueString.replace(" ", "");
                showProcess += ("正在计算的宏常量：" + keyString + "\n");
                showProcess += ("宏常量表达式：" + valueString + "\n");
                System.out.println("正在计算的宏常量：" + keyString);
                System.out.println("宏常量表达式：" + valueString);
                //System.out.println(waitString);
                waitString = Compute.hex2dec(waitString);
                //System.out.println(waitString);
                Compute compute = new Compute(waitString, resourceString);
                compute.lexical_Analysis.resVector = resVectors;
                String suffixString = compute.mid2suffix();
                showProcess += ("逆波兰式：" + suffixString + "\n");
                System.out.println("逆波兰式：" + suffixString);
                //System.out.println("suffix:" + suffixString);
                String resString = compute.calculateSuffix(suffixString);
                showProcess += compute.processString;
                showProcess += ("计算结果：" + resString + "\n" + "\n");
                System.out.println("计算结果：" + resString);
                System.out.println();
                for(int i = 0; i < resVectors.size(); i++) {
                    Vector<String> vector = resVectors.get(i);
                    if(vector.get(0).equals(keyString)) {
                        //System.out.println("当前被替换的宏常量为:" + keyString + " " + resString);
                        resVectors.get(i).set(2, resString);
                    }else {
                        continue;
                    }
                }
            }
        }

        //计算主函数中表达式
        System.out.println("计算主函数中表达式:");
        ArrayList<String> mainExpArrayList = checkAllExpression.mainExpArrayList;
        for(String valueString : mainExpArrayList) {
            if(valueString.contains("PI")) {
                continue;
            }
            String waitString = valueString.replace(" ", "");
            showProcess += ("正在计算的表达式：" + valueString + "\n");
            System.out.println("正在计算的表达式：" + valueString);
            //System.out.println(waitString);
            waitString = Compute.hex2dec(waitString);
            //System.out.println(waitString);
            Compute compute = new Compute(waitString, resourceString);
            compute.lexical_Analysis.resVector = resVectors;
            String suffixString = compute.mid2suffix();
            showProcess += ("逆波兰式：" + suffixString);
            System.out.println("逆波兰式：" + suffixString);
            //System.out.println("suffix:" + suffixString);
            String resString = compute.calculateSuffix(suffixString);
            showProcess += ("计算结果：" + resString + "\n" + "\n");
            System.out.println("计算结果：" + resString);
            System.out.println();
            for(int i = 0; i < resVectors.size(); i++) {
                Vector<String> vector = resVectors.get(i);
                if(waitString.contains(vector.get(0)) && vector.get(1).equals("var") || waitString.contains(vector.get(0)) && vector.get(1).equals("const")) {
                    //先找到resString中的答案
                    int index = resString.indexOf('=');
                    String real = resString.substring(index + 2, resString.length());
                    resVectors.get(i).add(real);
                    resVectors.get(i).set(1, "const");
                }
            }
        }

        //replace Macro
        for(int i = 0; i < resourceString.length(); i++) {
            if(resourceString.charAt(i) == '#' && i + 1 < resourceString.length() && resourceString.charAt(i + 1) == 'd') {
                //当前位置为#define
                i += 7;  //当前位置为e后面的空格
                while(resourceString.charAt(i) == ' ') {
                    i++;
                }//结束后位置为一个宏变量的开始符号
                int j = i;
                int k = i;
                while(resourceString.charAt(i) != ' ') {
                    i++;
                }//当前位置为变量后第一个空格
                String macroString = resourceString.substring(j, i);
                //System.out.println("待替换的变量为:" + macroString);
                String replaceString = "";
                for(Vector vector : resVectors) {
                    if(vector.get(0).equals(macroString)) {
                        replaceString = (String) vector.get(2);
                        //System.out.println(macroString + "替换为:" + replaceString);
                        break;
                    }
                }
                while(resourceString.charAt(i) == ' ') {
                    i++;
                }//当前结束为表达式第一个字符的位置
                j = i;
                while(resourceString.charAt(i) != '\n') {
                    i++;
                }
                StringBuilder builder = new StringBuilder(resourceString);
                builder.replace(j,  i, replaceString);
                resourceString = builder.toString();
                i = k;
                continue;
            }
        }

        int mainIndex = 0;
        for(int i = 0; i < resourceString.length(); i++) {
            if(resourceString.substring(i, i + 4).equals("main")) {
                mainIndex = i;
                break;
            }
        }

        String finalX = resourceString.substring(0, mainIndex);
        String finalY = resourceString.substring(mainIndex, resourceString.length());

        //替换主函数中的宏
        boolean flag = false;
        while(true) {
            flag = false;
            for(Vector vector : resVectors) {
                if(vector.get(1).equals("MACRO")) {
                    String macroString = (String) vector.get(0);
                    int index = finalY.indexOf(macroString);
                    if(index != -1) {
                        flag = true;
                        StringBuilder builder = new StringBuilder(finalY);
                        builder.replace(index, index + macroString.length(), (String) vector.get(2));
                        finalY = builder.toString();
                    }
                }
            }
            if(!flag) {
                break;
            }
        }

        //替换主函数中的表达式
        int index = 0;
        resourceString = finalX + finalY;
        String []subStrings = resourceString.split("\n");
        for(int i = 0; i < subStrings.length; i++) {
            if(subStrings[i].contains("define")) {
                continue;
            }
            if(subStrings[i].contains("=") && !subStrings[i].contains("3.14")) {
                int j = 0;
                //System.out.println("主函数中待替换: " + subStrings[i]);
                while (subStrings[i].charAt(j) == '\t' || subStrings[i].charAt(j) == ' ') {
                    j++;
                }//当前位置变量起始位置
                int k = j;
                while (subStrings[i].charAt(j) != ' ') {
                    j++;
                }//当前位置变量结束
                String varString = subStrings[i].substring(k, j);
                String replaceString = "";
                //search value
                for(Vector<String> vector : resVectors) {
                    if(vector.get(0).equals(varString)) {
                        replaceString = vector.get(2 + index++);
                        break;
                    }
                }
                subStrings[i] = subStrings[i].substring(0, k) + varString + " = " +
                        replaceString;
            }
        }
        resourceString = "";
        for(String xString : subStrings) {
            resourceString += (xString + "\n");
        }

        System.out.println("替换结束了");

        System.out.println(resourceString);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) editText.getLayoutParams();
        lp.height = 1000;
        editText.setLayoutParams(lp);
        RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams) editTextComp.getLayoutParams();
        lp1.height = 1300;
        editTextComp.setLayoutParams(lp1);
        SpannableStringBuilder builder = convert(resourceString);
        editText.setText(builder);
        editTextComp.setText(showProcess);
    }
}