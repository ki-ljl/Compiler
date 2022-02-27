# 1. 题目要求
设计一个 C 语言的预处理程序，将C语言中所有的宏常量进行计算，并生成另外一个文件，将宏常量展开和计算的结果全部显示出来，最后将定义的宏在源程序中全部进行替换。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210111200851296.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5cmlsX0tJ,size_16,color_FFFFFF,t_70)

例如，源程序为：

```c
#include <stdio.h>
#define  ADDR_START   0x20480000
#define  ADDR_A        ADDR_START + 0x04
#define  ADDR_B        ADDR_START + 15
#define  BUFFER_SIZE  1024
#define  BUFFER_END    ADDR_START + BUFFER_SIZE – 1
#define  PI      3.14
void main()
{
	float r, l;
	scanf(“%f”, &r);
	l = 2 * PI *r;
	memset(ADDR_START, BUFFER_SIZE, 0x00);
}

```
替换后为：

```c
#include <stdio.h>
#define  ADDR_START   0x20480000
#define  ADDR_A        0x20480004
#define  ADDR_B        0x2048000F
#define  BUFFER_SIZE  1024
#define  BUFFER_END   0x204803FF
#define  PI      3.14
void main()
{
	float r, l;
	scanf(“%f”, &r);
	l = 2 * 3.14 *r;
	memset(0x20480000, 1024, 0x00);
}

```

# 2.思路分析
## 2.1文法设计
&emsp;&emsp;我们知道，C 语言中运算符的优先级为：[] > (! ~) > (* / %) > (+ -) > (<< >>) > (> >= < <=) > (== !=) > & > ^ > | > && > || > ?: > (= += -= /= *= %=) > ,(逗号)。

&emsp;&emsp;为保证依据设计的文法求得的算符优先矩阵符合上述要求，根据求算符优先矩阵的方法，从优先级别最低的逗号开始逐次往高级别推导，这样算出来的算符优先矩阵就是符合预期的。

&emsp;&emsp;比如& < ==，则文法设计为 G->G&H，G->H，H->H==I。可以看到 FirstVt(G) = {&, ==}，FirstVt(H) = {==}，而 G->G&H，所以& < FirstVt(H)，即& < ==。同理根据 LastVt 可得== > &。

&emsp;&emsp;表达式文法设计过程中有以下几点值得考虑：
1. 推至结尾 O 时，为了将前后连贯起来，O 必须能导出最开始的符号 A，同时若 A 是表达式，(A)也必然是表达式，于是最后两条产生式为 O->i，O->(A)，文法中用 i 表征一切变量。
2. 下标运算符[]的设计。下标运算符在 C 语言中通常与数组结合起来，其标准用法为 a[i]，其中 a 为数组名，i 为下标。在上述所有双目运算符中，其产生式都是类似于 A->A&&B 这种形式，于是将[]运算符的产生式设计为：N->N[]O，其中 N 为数组名，O 为数组下标。
3. 有了以上前提，在词法分析阶段，要将源程序中所有含有条件运算符以及下标运算符的表达式改写成上述产生式形式。
4. 经过设计，最终文法为：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210111200655783.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5cmlsX0tJ,size_16,color_FFFFFF,t_70)

## 2.2表达式的计算
&emsp;&emsp;本次课设表达式的计算思路为：将原表达式转为逆波兰式，再进行计算，不使用属性文法计算。

## 2.3概要设计
&emsp;&emsp;事先写好三个待分析的源程序文件，点击 Open 按钮，可选择打开哪一个源程序，打开后，会马上进行词法分析，得到各种有意义字符串的种别号，然后根据种别号对源程序设置不同的颜色，例如大小括号为红色，define 以及 include 为粉红色等。

&emsp;&emsp;接下来点击 Lexical 按钮，开始进行词法分析。词法分析实际上在源程序打开后就已经结束了，点击 Lexical 按钮只是做一个展示功能。

&emsp;&emsp;词法分析程序的主要任务是对构成源程序的字符串从左到右扫描，逐个字符地读入源程序字符并按照构词规则切分成一个一个具有独立意义的单词。并确定其属性（如关键字、宏常量、标识符等）。

&emsp;&emsp;词法分析中将单词分为以下几类：
1. 关键字 keyWord：由程序语言定义的具有固定意义的标识符，也称为保留字或基本字。
如 auto、short、typedef 等
2. 宏常量 MACRO：本次实验的主要目的就是分析宏常量，所以单独定义。
3. 一般变量 var：用来表示程序中各种名字的字符串。
4. 常数 number：常数的类型一般有整型、实型、布尔型、文字型。
5. 运算符 ope：如+、－ 、*、/ 等。
6. 界限符：如逗号、分号、括号等。
7. 特殊字符 special：C 语言在语法上并未将 main、include 以及 define 等符号定义为关键字，单独列出。

&emsp;&emsp;词法分析得到的结果是一个初始符号表，每一个表项都是一个向量，每一个向量表示一个有意义的字符串，比如(SIZE, MACRO, X + Y)，表明 SIZE 是一个宏常量，其表达式为 X +Y。又如(+, 4)表明加法运算符的种别号为 4。

&emsp;&emsp;接下来点击 Grammar 按钮，即可进行语法分析。语法分析首先分析的是宏常量的表达式，
根据词法分析得到的符号表，找到每一个宏常量的表达式（可以是一个常数，也可以是一个很复杂的表达式），然后将每个表达式中的变量以及常数都用小写字母 i 替代，因为设计的文法当中默认用 i 表示操作数。例如 SIZE + X / Y 变为 i+i/i，然后用算符优先文法来规约这个表达式，并判断是否合法。

&emsp;&emsp;宏常量的表达式语法分析完毕后，紧接着分析主函数当中的表达式，对于主函数中的表达式，与宏常量表达式一样，也将除开运算符以外的所有变量用 i 表示，然后用上述定义的表达式文法进行规约分析。分析完毕后，所有的分析过程在点击 Grammar 按钮的一瞬间都会显示在模拟器界面上。

&emsp;&emsp;语法分析结束后，最后进行的是表达式计算。点击 Calculate 按钮，即可对所有经过语法分析并且合法的表达式进行计算。计算时首先将表达式转成逆波兰式，然后利用栈对其进行计算。每计算完一个表达式，就将符号表进行更新，方便下一步的计算，所有表达式的计算过程会显示在模拟器界面上。

&emsp;&emsp;表达式计算完毕之后，开始对源程序进行替换，替换过程与计算过程同步进行。扫描源程序，对宏常量以及相关表达式出现的地方，用计算得到的值进行替换，该值通过扫描符号表得到。替换完毕后，再根据结果变换程序字符串的颜色，显示在模拟器界面上。


# 3.系统的类图
系统中包含一个主界面 MainActivity 以及五个子类，分别介绍如下：
1. 主界面 MainActivity：主界面中包含两个 EditText 以及一个 RadioGroup，RadioGroup 中包含四个 RadioButton，分别对应打开文件、词法分析、语法分析以及表达式计算。
2. Grammatical_Analysis 类：该类中定义了识别数字以及标识符的文法，其方法 booleanisVar(String x)与 boolean isNum(string x)分别来判断字符串是否是合法的标识符或者数字。
3. Lexical_Analysis 类：词法分析类，该类中定义了所有可能出现的符号以及它们的类别号，并对源程序进行扫描，生成初始符号表。
4. Operator_Precedence 类：算符优先分析类，该类中定义了表达式文法。findFirstVt()与findLastVt()用于求解所有非终结符的 FirstVt 以及 LastVt 集合； findRe()根据两个集合建立算符优先矩阵；check(String x)对表达式 x 进行算符优先分析，并给出规约结果。其余还有一些方法都是为了配合以上几个方法而设计。
5. Compute 类：计算类，根据 Operator_Precedence 类的分析结果，对表达式进行计算。其中：mid2suffix()方法将给出的表达式转成逆波兰式；hex2dec()将计算过程中可能出现的十六进制数转成十进制数；calculateSuffix()用于计算 mid2suffix()产生的逆波兰式。
类图如下：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210111203755804.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5cmlsX0tJ,size_16,color_FFFFFF,t_70)

# 4.界面设计
## 4.1主界面
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210111202050639.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5cmlsX0tJ,size_16,color_FFFFFF,t_70)
## 4.2打开文件
![在这里插入图片描述](https://img-blog.csdnimg.cn/2021011120285791.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5cmlsX0tJ,size_16,color_FFFFFF,t_70#pic_center)

## 4.3词法分析
点击词法分析后，编辑器上缩，下方的显示栏显示词法分析结果，如下所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210111203011367.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5cmlsX0tJ,size_16,color_FFFFFF,t_70#pic_center)

## 4.4语法分析
点击语法分析按钮，可对源程序中所有表达式进行语法分析，如下所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210111203030251.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5cmlsX0tJ,size_16,color_FFFFFF,t_70#pic_center)
## 4.5表达式计算
点击表达式计算按钮，可将计算过程显示出来。另外可以看到，计算结果已经在源程序中替换。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210111203046815.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5cmlsX0tJ,size_16,color_FFFFFF,t_70#pic_center)
