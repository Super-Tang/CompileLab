package grammar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class GrammarTable {
    private HashMap<String, HashSet<String>> generate = new HashMap<>();
    static HashMap<Integer, HashMap<Character, String>> actionTable = new HashMap<>();
    static HashMap<Integer, HashMap<Character, Integer>> gotoTable = new HashMap<>();
    private HashMap<Character, HashSet<Character>> firstOP = new HashMap<>();
    static ArrayList<ArrayList<String>> items = new ArrayList<>();
    static ArrayList<String> code = new ArrayList<>();
    static HashSet<Character> nonterminals = new HashSet<>();
    static HashSet<Character> terminals = new HashSet<>();
    private String start;

    public GrammarTable(){

    }
    /**
     *   从文件中读取文法产生式
     */
    void readGenerate(){
        try{
            terminals.add('#');
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("g.txt")));
            String temp;
            while ((temp = bufferedReader.readLine()) != null){
                nonterminals.add(temp.charAt(0));
                String []tokens = temp.split(" ");
                code.add(tokens[0]+"->"+tokens[2]);
                for(Character c : tokens[2].toCharArray()){
                    if(isTerminal(c)){
                        terminals.add(c);
                    }
                }
                if(start == null){
                    start = tokens[0];
                }
                if(generate.containsKey(tokens[0])){
                    generate.get(tokens[0]).add(tokens[2]);
                }else{
                    HashSet<String> hashSet = new HashSet<>();
                    hashSet.add(tokens[2]);
                    generate.put(tokens[0], hashSet);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *   采用递归方式计算首字符集合
     */
    void calculateFirstOP(){
        for(String s : code){
            if(!firstOP.containsKey(s.charAt(0))){
                HashSet<Character> hashSet = new HashSet<>();
                firstOP.put(s.charAt(0), hashSet);
            }
            HashSet<Character> hashSet = firstOP.get(s.charAt(0));
            if(isTerminal(s.charAt(3))){
                terminals.add(s.charAt(3));
                hashSet.add(s.charAt(3));
            }else if(isNotTerminal(s.charAt(3))){
                hashSet.addAll(calculateFirstOP(s.charAt(3)));
            }
        }

    }

    /**
     *   计算非终结符的首字符集合
     * @param c  非终结符
     * @return
     */
    private HashSet<Character> calculateFirstOP(char c){
        HashSet<Character> hashSet = new HashSet<>();
        for(String s : code){
            if(s.charAt(0) == c){
                if(isTerminal(s.charAt(3))) {
                    terminals.add(s.charAt(3));
                    hashSet.add(s.charAt(3));
                }else if(isNotTerminal(c) && c != s.charAt(3)){
                    hashSet.addAll(calculateFirstOP(s.charAt(3)));
                }
            }
        }
        return hashSet;
    }

    /**
     *   计算某个符号串的首字符集合
     * @param s  符号串
     * @return
     */
    private HashSet<Character> calculateFirstOP(String s){
        HashSet<Character> hashSet = new HashSet<>();
        //System.out.println(s);
        if(s.isEmpty()){
            hashSet.add('#');
        }else{
            if(isTerminal(s.charAt(0))){
                hashSet.add(s.charAt(0));
            }else if(isNotTerminal(s.charAt(0))){
                int i=0;
                while (isNotTerminal(s.charAt(i)) && code.contains(s.charAt(i)+"->#")){
                    hashSet.addAll(calculateFirstOP(s.charAt(i)));
                    i++;
                }
                if(isNotTerminal(s.charAt(i))){
                    hashSet.addAll(calculateFirstOP(s.charAt(i)));
                }
                if(i < s.length()-1){
                    hashSet.remove('#');
                }
            }
        }
        return hashSet;
    }

    /**
     *   递归方式计算项目集闭包
     */
    void calculateClosure(){
        ArrayList<String> arrayList = new ArrayList<>();
        HashSet<String> first = generate.get(start);
        for(String s : first){
            String s1 = start + "->." + s + "\t#/";
            arrayList.add(s1);
            if(isNotTerminal(s.charAt(0))){
                arrayList.addAll(calculateClosure(s1));
            }
        }
        items.add(arrayList);
        for(int i=0; i<items.size(); i++){
            for(String s : items.get(i)){
                int index = s.indexOf(".");
                if(index != s.indexOf("\t")-1) {
                    String next = s.substring(0, index) + s.substring(index + 1, index + 2) + "." + s.substring(index + 2);
                    ArrayList<String> arrayList2 = new ArrayList<>();
                    arrayList2.add(next);
                    arrayList2.addAll(calculateClosure(next));
                    if(!items.contains(arrayList2)){
                        items.add(arrayList2);
                    }
                }
            }
        }
    }

    /**
     *   生成动作表和状态转移表
     */
    void generateTable(){
        for(int i=0; i<items.size(); i++){
            if(gotoTable.get(i) == null){
                HashMap<Character, Integer> hashMap = new HashMap<>();
                gotoTable.put(i, hashMap);
            }
            if(actionTable.get(i) == null){
                HashMap<Character, String> hashMap = new HashMap<>();
                actionTable.put(i,hashMap);
            }
            for(String s : items.get(i)){
                int index = s.indexOf(".");
                if(index != s.indexOf("\t") - 1) {
                    char next = s.charAt(index + 1);
                    //System.out.println(items.get(9));
                   // System.out.println("i = " + i + " next = " + next);
                    if (isTerminal(next)) {
                        String nextState = s.substring(0, index) + s.substring(index + 1, index + 2) + "." + s.substring(index + 2);
                        //System.out.println("nextstate = " + nextState);
                        HashMap<Character, String> actionHashMap = actionTable.get(i);
                        actionHashMap.put(next, "s" + getNextState(nextState));
                        actionTable.put(i, actionHashMap);
                    }
                    if (isNotTerminal(next)) {
                        String nextState = s.substring(0, index) + s.substring(index + 1, index + 2) + "." + s.substring(index + 2);
                        //System.out.println("before stat + " + s + " tran "+ next + " next stat + " + nextState);
                        HashMap<Character, Integer> hashMap = gotoTable.get(i);
                        hashMap.put(next, getNextState(nextState));
                        gotoTable.put(i, hashMap);
                    }
                }else{
                    String []tokens = s.substring(s.indexOf("\t") + 1).split("/");
                   // System.out.println("here to reduce "  + s);
                    for (String token : tokens) {
                        int code = searchGeneration(s.substring(0, s.indexOf(".")));
                        HashMap<Character, String> actionHashMap = actionTable.get(i);
                        if (code != 0) {
                            actionHashMap.put(token.charAt(0), "r" + code);
                            //System.out.println(token.charAt(0));
                            actionTable.put(i, actionHashMap);
                        } else {
                            actionHashMap.put(token.charAt(0), "acc");
                            actionTable.put(i, actionHashMap);
                        }
                    }
                }
            }
        }
    }

    /**
     *  寻找产生式编号
     * @param s
     * @return
     */
    private int searchGeneration(String s){
        return code.indexOf(s);
    }

    /**
     *   获得下一个状态
     * @param s
     * @return
     */
    private int getNextState(String s){
        for(int i=0; i<items.size(); i++){
            if(items.get(i).contains(s)){
                return i;
            }
        }
        return -1;
    }

    /**
     *   计算某个产生式的闭包
     * @param s
     * @return
     */
    private HashSet<String> calculateClosure(String s){
        HashSet<String> arrayList = new HashSet<>();
        StringBuilder inherit = new StringBuilder();
        if(isNotTerminal(s.charAt(s.indexOf(".") + 1))) {
            HashSet<String> hashSet = generate.get(s.substring(s.indexOf(".")+1, s.indexOf(".") + 2));
            for (String s1 : hashSet) {
                String s2 = s.charAt(s.indexOf(".") + 1) + "->." + s1;
                // 如果beta 为空，则继承
                if(s.indexOf("\t") == s.indexOf(".") + 2){
                    inherit.append(s.substring(s.indexOf("\t") + 1));
                    s2 = s2 + "\t" + inherit;
                }else {  // 否则计算first（beta）
                    String next = s.substring(s.indexOf(".") + 2, s.indexOf("\t"));
                    for(Character c : calculateFirstOP(next)){
                        inherit.append(c).append("/");
                    }
                    s2 = s2 + "\t" + inherit.toString();
                }
                arrayList.add(s2);
                if (isNotTerminal(s1.charAt(0)) && !s2.equals(s)) {
                    arrayList.addAll(calculateClosure(s2));
                }
                inherit.delete(0,inherit.length());
            }
            //arrayList = mergeArray(arrayList);
            return arrayList;
        }else {
            return new HashSet<>();
        }
    }

    HashSet<String> mergeArray(HashSet<String> hashSet){
        HashSet<String> hashSet1 = new HashSet<>();
        ArrayList<String> arrayList = new ArrayList<>(hashSet);
        ArrayList<Integer> integers = new ArrayList<>();
        for(int i=0; i< arrayList.size(); i++){
            for(int j=0; j< arrayList.size(); j++){
                if(i!=j && arrayList.get(i).split("\t")[0].equals(arrayList.get(j).split("\t")[0])) {
                    HashSet<String> hashSet2 = new HashSet<>();
                    hashSet2.addAll(Arrays.asList(arrayList.get(i).split("\t")[1].split("/")));
                    hashSet2.addAll(Arrays.asList(arrayList.get(j).split("\t")[1].split("/")));
                    System.out.println(hashSet2);
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String s : hashSet2) {
                        stringBuilder.append(s).append("/");
                    }
                    hashSet1.add(arrayList.get(i).split("\t")[0] + "\t" + stringBuilder.toString());
                    integers.add(i);
                    integers.add(j);
                }
            }
        }
        for(int i=0; i<arrayList.size(); i++){
            if(!integers.contains(i)){
                hashSet1.add(arrayList.get(i));
            }
        }
        System.out.println(hashSet1);
        return hashSet1;
    }


    public static boolean isTerminal(char c){
        return c >= 33 && c < 'A' || c > 'Z' && c <= 126;
    }

    public boolean isNotTerminal(char c){
        return c >= 'A' && c <= 'Z';
    }

}
