package lexicon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * @author Super-Tang
 */
public class Analyzer {
    private char []chars = new char[10000];
    private StringBuilder stringBuilder = new StringBuilder();
    private SymbolTable table = new SymbolTable();
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayList<String> transferArray = new ArrayList<>();
    private ArrayList<String> identifies = new ArrayList<>();

    //将源文件进行过滤，去掉换行符，制表符，保留空格
    private int filter(String filename){
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(filename)));
            int i = 0;
            String temp;
            while ((temp = bufferedReader.readLine()) != null){
               stringBuilder.append(temp).append("\n");
            }
            StringBuilder stringBuilder1 = new StringBuilder();
            stringBuilder.append("$");
            for(int j=0; j<stringBuilder.length();j++){
                if(stringBuilder.charAt(j) == '/' && stringBuilder.charAt(j+1) =='/'){
                    stringBuilder1.append(stringBuilder.charAt(j));
                    j++;
                    while (stringBuilder.charAt(j) != '\n'){
                        stringBuilder1.append(stringBuilder.charAt(j));
                        j++;
                    }
                    arrayList.add("注释：" + stringBuilder1.toString() + "\n");
                    stringBuilder1.delete(0,stringBuilder1.length());
                }
                if(stringBuilder.charAt(j) == '/' && stringBuilder.charAt(j+1) == '*'){
                    stringBuilder1.append(stringBuilder.charAt(j));
                    while (stringBuilder.charAt(j) != '*' || stringBuilder.charAt(j+1) != '/'){
                        j++;
                        stringBuilder1.append(stringBuilder.charAt(j));
                        if(stringBuilder.charAt(j) == '$'){
                            System.out.println("注释出错，没有找到 \'*/\'，程序结束。");
                            return 0;
                        }
                    }
                    j += 2;
                    stringBuilder1.append(stringBuilder.charAt(j-1));
                    arrayList.add("注释：" + stringBuilder1.toString() +"\n");
                    stringBuilder1.delete(0,stringBuilder1.length());
                }
                if(stringBuilder.charAt(j) != '\n' && stringBuilder.charAt(j) != '\t' && stringBuilder.charAt(j) != '\r'){
                    chars[i] = stringBuilder.charAt(j);
                    i++;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    //词法分析主程序，使用DFA进行分析
    public int scanner(){
        table.initTable();
        String label;
        StringBuilder stringBuilder1 = new StringBuilder();
        int i=0;
        //去掉文件开头多余的空格
        while (chars[i] == ' '){
            i++;
        }
        while (i<chars.length ){
            if(isLetter(chars[i])){
                //  处理以字母开头的字符串
                stringBuilder1.append(chars[i]);
                i++;
                while (isDigit(chars[i]) || isLetter(chars[i])){
                    stringBuilder1.append(chars[i]);
                    i++;
                }
                label = table.searchTable(stringBuilder1.toString());
                if("IDN".equals(label) || "CONST".equals(label)) {
                    arrayList.add(stringBuilder1.toString() + "\t <" + label + ",\t " + stringBuilder1.toString() + ">\n");
                }else{
                    arrayList.add(stringBuilder1.toString() + "\t <" + label + ",\t _ > \n");
                }
                stringBuilder1.delete(0, stringBuilder1.length());
                i--;   //回退一个字符
            }else if(isDigit(chars[i])) {
                //  处理数字开头的字符串
                stringBuilder1.append(chars[i]);
                i++;
                while (isDigit(chars[i])) {
                    stringBuilder1.append(chars[i]);
                    i++;
                }
                if (chars[i] == '.') {
                    stringBuilder1.append(chars[i]);
                    i++;
                    if(isDigit(chars[i])) {
                        while (isDigit(chars[i])) {
                            stringBuilder1.append(chars[i]);
                            i++;
                        }
                        //TODO 指数型
                        label = table.searchTable(stringBuilder1.toString());
                        if ("IDN".equals(label) || "CONST".equals(label)) {
                            arrayList.add(stringBuilder1.toString() + "\t <" + label + ",\t " + stringBuilder1.toString() + "> \n");
                        } else {
                            arrayList.add(stringBuilder1.toString() + "\t <" + label + ",\t _ > \n");
                        }
                        stringBuilder1.delete(0, stringBuilder1.length());
                    }else{
                        while (chars[i] != ' '){
                            stringBuilder1.append(chars[i]);
                            i++;
                        }
                        arrayList.add("错误的常数格式：" + stringBuilder1.toString() + "\n");
                        stringBuilder1.delete(0, stringBuilder1.length());
                    }
                }else if(!table.containsInSet(chars[i]) && ! (chars[i] == ' ')){
                    stringBuilder1.append(chars[i]);
                    i++;
                    while (chars[i] != '\0' && chars[i] != ' '){
                        stringBuilder1.append(chars[i]);
                        i++;
                    }
                    arrayList.add("错误的常数格式：" + stringBuilder1.toString() + "\n");
                    stringBuilder1.delete(0, stringBuilder1.length());
                }else{
                    label = table.searchTable(stringBuilder1.toString());
                    if ("IDN".equals(label) || "CONST".equals(label)) {
                        arrayList.add(stringBuilder1.toString() + "\t <" + label + ",\t " + stringBuilder1.toString() + "> \n");
                    } else {
                        arrayList.add(stringBuilder1.toString() + "\t <" + label + ",\t _ > \n");
                    }
                    stringBuilder1.delete(0, stringBuilder1.length());
                }
                i--;   //回退一个字符
            }else if(table.containsInSet(chars[i])) {
                //  处理单个的符号
                stringBuilder1.append(chars[i]);
                label = table.searchTable(stringBuilder1.toString());
                arrayList.add(stringBuilder1.toString() + "\t <" + label + ",\t _ > \n");
                stringBuilder1.delete(0, stringBuilder1.length());
            }else if(chars[i] == '+'){
                //  处理+号和++
                stringBuilder1.append(chars[i]);
                i++;
                if(chars[i] == '+'){
                    stringBuilder1.append(chars[i]);
                    i++;
                    label = table.searchTable(stringBuilder1.toString());
                    arrayList.add(stringBuilder1.toString() + "\t <" + label + ",\t _ > \n");
                    stringBuilder1.delete(0, stringBuilder1.length());
                }else{
                    label = table.searchTable(stringBuilder1.toString());
                    arrayList.add(stringBuilder1.toString() + "\t <" + label + ",\t _ > \n");
                    stringBuilder1.delete(0, stringBuilder1.length());
                }
                i--;   //回退一个字符
            }else if(chars[i] == '-'){
                //  处理-号和--
                stringBuilder1.append(chars[i]);
                i++;
                if(chars[i] == '-'){
                    stringBuilder1.append(chars[i]);
                    i++;
                    label = table.searchTable(stringBuilder1.toString());
                    arrayList.add(stringBuilder1.toString() + "\t <" + label + ",\t _ > \n");
                    stringBuilder1.delete(0, stringBuilder1.length());
                }else{
                    label = table.searchTable(stringBuilder1.toString());
                    arrayList.add(stringBuilder1.toString() + "\t <" + label + ",\t _ > \n");
                    stringBuilder1.delete(0, stringBuilder1.length());
                }
                i--;   // 回退一个字符
            } else if(chars[i] == '<'){
                //  处理 < <= 和 <<
                stringBuilder1.append(chars[i]);
                i++;
                if(chars[i] == '=' || chars[i] == '<'){
                    stringBuilder1.append(chars[i]);
                    i++;
                    label = table.searchTable(stringBuilder1.toString());
                    arrayList.add(stringBuilder1.toString() + "\t <" + label + ",\t _ > \n");
                    stringBuilder1.delete(0, stringBuilder1.length());
                }else{
                    label = table.searchTable(stringBuilder1.toString());
                    arrayList.add(stringBuilder1.toString() + "\t <" + label + ",\t _ > \n");
                    stringBuilder1.delete(0, stringBuilder1.length());
                }
                i--;   //回退一个字符
            }else if(chars[i] == '>'){
                //   处理 > >= >>
                stringBuilder1.append(chars[i]);
                i++;
                if(chars[i] == '=' || chars[i] == '>'){
                    stringBuilder1.append(chars[i]);
                    i++;
                    label = table.searchTable(stringBuilder1.toString());
                    arrayList.add(stringBuilder1.toString() + "\t <" + label + ",\t _ > \n");
                    stringBuilder1.delete(0, stringBuilder1.length());
                }else{
                    label = table.searchTable(stringBuilder1.toString());
                    arrayList.add(stringBuilder1.toString() + "\t <" + label + ",\t _ > \n");
                    stringBuilder1.delete(0, stringBuilder1.length());
                }
                i--;  // 回退一个字符
            }else if(chars[i] == '=' || chars[i] == '!'){
                //处理 ！ 和 ！=
                stringBuilder1.append(chars[i]);
                i++;
                if(chars[i] == '='){
                    stringBuilder1.append(chars[i]);
                    i++;
                    label = table.searchTable(stringBuilder1.toString());
                    arrayList.add(stringBuilder1.toString() + "\t <" + label + ",\t _ > \n");
                    stringBuilder1.delete(0, stringBuilder1.length());
                }else{
                    label = table.searchTable(stringBuilder1.toString());
                    arrayList.add(stringBuilder1.toString() + "\t <" + label + ",\t _ > \n");
                    stringBuilder1.delete(0, stringBuilder1.length());
                }
                i--;  // 会退一个字符
            }else if(chars[i] == '&'){
                //  处理& 和 &&
                stringBuilder1.append(chars[i]);
                i++;
                if(chars[i] == '&'){
                    stringBuilder1.append(chars[i]);
                    i++;
                    label = table.searchTable(stringBuilder1.toString());
                    arrayList.add(stringBuilder1.toString() + "\t <" + label + ",\t _ > \n");
                    stringBuilder1.delete(0, stringBuilder1.length());
                }else{
                    label = table.searchTable(stringBuilder1.toString());
                    arrayList.add(stringBuilder1.toString() + "\t <" + label + ",\t _ > \n");
                    stringBuilder1.delete(0, stringBuilder1.length());
                }
                i--;   //回退一个字符
            }else if(chars[i] == '|'){
                //   处理| 和 ||
                stringBuilder1.append(chars[i]);
                i++;
                if(chars[i] == '|'){
                    stringBuilder1.append(chars[i]);
                    i++;
                    label = table.searchTable(stringBuilder1.toString());
                    arrayList.add(stringBuilder1.toString() + "\t <" + label + ",\t _ > \n");
                    stringBuilder1.delete(0, stringBuilder1.length());
                }else{
                    label = table.searchTable(stringBuilder1.toString());
                    arrayList.add(stringBuilder1.toString() + "\t <" + label + ",\t _ > \n");
                    stringBuilder1.delete(0, stringBuilder1.length());
                }
                i--;   //回退一个字符
            }else if(chars[i] == '$'){
                // 结束标志
                stringBuilder1.append(chars[i]);
                label = table.searchTable(stringBuilder1.toString());
                arrayList.add(stringBuilder1.toString() + "\t <" + label + ",\t _ > \n");
                stringBuilder1.delete(0, stringBuilder1.length());
            }else if(chars[i] == ' '){
                i++;
                while (chars[i] == ' '){
                    i++;
                }
                i--;  //回退一个字符
            }else{
                if(!table.isEnd(chars[i]) && i < chars.length) {
                    arrayList.add("非法字符：" + chars[i] +"\n");
                }
            }
            i++;  //  向前搜索一个字符
        }
        return 1;
    }

    public void load(String line){
        arrayList.clear();
        int i = 0;
        for(int j=0; j<line.length();j++){
            if(line.charAt(j) == '/' && line.charAt(j+1) =='/'){
                while (line.charAt(j) != '\n'){
                    j++;
                }
            }
            if(line.charAt(j) == '/' && line.charAt(j+1) == '*'){
                while (line.charAt(j) != '*' || line.charAt(j+1) != '/'){
                    j++;
                }
                j += 2;
            }
            if(line.charAt(j) != '\n' && line.charAt(j) != '\t' && line.charAt(j) != '\r'){
                chars[i] = line.charAt(j);
                i++;
            }
        }
    }

    //写入文件并打印
    private void writeIntoFile(String filename){
        try {
            FileWriter fileWriter = new FileWriter(new File(filename));
            for(String s : arrayList){
                fileWriter.write(s);
            }
            fileWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // 判断是否为字母
    private boolean isLetter(char c){
       return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c == '_');
    }

    //判断是否为数字
    private boolean isDigit(char c){
        return c >= '0' && c <= '9';
    }

    public void run() {
        filter("1.txt");
        scanner();
        writeIntoFile("2.txt");
    }

    public String getArrayList() {
        StringBuilder stringBuilder = new StringBuilder();
        for(String s : arrayList){
            stringBuilder.append(s);
        }
        return stringBuilder.toString();
    }

    public void transferArray(){
        StringBuilder stringBuilder = new StringBuilder();
        for(String s : arrayList){
            String s1 = s.split("\t")[1];
            s1 = s1.substring(2,s1.length()-1);
            if(table.transfer.containsKey(s1)){
                stringBuilder.append(table.transfer.get(s1));
            }else if(s1.matches("[0-9]+")){
                stringBuilder.append("e");
            }else{
                stringBuilder.append(s.split("\t")[0]);
            }
        }
//        System.out.println(stringBuilder.toString());
        transferArray.add(stringBuilder.toString());
    }

    public ArrayList<String> getTransferArray() {
        return transferArray;
    }
}
