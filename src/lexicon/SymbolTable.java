package lexicon;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Super-Tang
 */
public class SymbolTable {
    private HashMap<String, String> table = new HashMap<>();
    private HashSet<Character> op = new HashSet<>();
    HashMap<String, String>transfer = new HashMap<>();
    private HashMap<String, String>transfer1 = new HashMap<>();

    // 初始化符号表
    public void initTable(){
        if(table.isEmpty()){
            table.put("$", "$");
            table.put("break", "BREAK");
            table.put("case", "CASE");
            table.put("char", "CHAR");
            table.put("const", "CONST");
            table.put("continue", "CONTINUE");
            table.put("string", "STRING");
            table.put("default", "DEFAULT");
            table.put("do", "DO");
            table.put("double", "DOUBLE");
            table.put("else", "ELSE");
            table.put("float", "FLOAT");
            table.put("for", "FOR");
            table.put("if", "IF");
            table.put("int", "INT");
            table.put("bool", "BOOL");
            table.put("long", "LONG");
            table.put("return", "RETURN");
            table.put("short", "SHORT");
            table.put("switch", "SWITCH");
            table.put("void", "VOID");
            table.put("struct", "STRUCT");
            table.put("while", "WHILE");
            table.put("+", "ADD");
            table.put("-", "MIN");
            table.put("*", "MUL");
            op.add('*');
            table.put("/", "DIV");
            op.add('/');
            table.put("<", "LT");
            table.put("<=", "LEQ");
            table.put(">", "GT");
            table.put(">=", "GEQ");
            table.put("=", "EQ");
            table.put("==", "CEQ");
            table.put("!=", "NEQ");
            table.put(";", "SEM");
            op.add(';');
            table.put("(", "SLP");
            op.add('(');
            table.put(")", "RLP");
            op.add(')');
            table.put("^", "XOR");
            op.add('^');
            table.put(",", "COM");
            op.add(',');
            table.put("\"", "DOQ");
            op.add('\"');
            table.put("\'", "SIQ");
            op.add('\'');
            table.put("#", "NUM");
            op.add('#');
            table.put("&", "BAND");
            table.put("&&", "LAND");
            table.put("|", "BOR");
            table.put("||", "LOR");
            table.put("%", "PER");
            op.add('%');
            table.put("~", "WAV");
            table.put("<<", "LSH");
            table.put(">>", "RSH");
            table.put("[", "OP");
            op.add('[');
            table.put("]", "CP");
            op.add(']');
            table.put("{", "LP");
            op.add('{');
            table.put("}", "RP");
            op.add('}');
            table.put("\\", "SLASH");
            op.add('\\');
            table.put(".", "DOT");
            op.add('.');
            table.put("?", "QM");
            op.add('?');
            table.put(":", "COL");
            op.add(':');
            table.put("!", "EXC");
            table.put("++", "INC");
            table.put("--", "DEC");
        }
        if(transfer.isEmpty()){
            transfer.put("INT" ,"a");
            transfer.put("BREAK", "b");
            transfer.put("CASE", "c");
            transfer.put("CHAR", "d");
            transfer.put("IDN", "e");
            transfer.put("CONTINUE", "f");
            transfer.put("STRING", "g");
            transfer.put("DEFAULT", "h");
            transfer.put("DO", "i");
            transfer.put("DOUBLE", "j");
            transfer.put("ELSE", "k");
            transfer.put("FLOAT", "l");
            transfer.put("FOR", "m");
            transfer.put("IF", "n");
            transfer.put("BOOL", "o");
            transfer.put("LONG", "p");
            transfer.put("RETURN", "q");
            transfer.put("SHORT", "r");
            transfer.put("SWITCH", "s");
            transfer.put("VOID", "t");
            transfer.put("STRUCT", "u");
            transfer.put("WHILE", "v");
            transfer.put("CONST", "w");
            transfer1.put("a","int");
            transfer1.put("b","break");
            transfer1.put("c","case");
            transfer1.put("d","char");
            transfer1.put("e", "const");
            transfer1.put("f","continue");
            transfer1.put("g", "string");
            transfer1.put("h", "default");
            transfer1.put("i" , "do");
            transfer1.put("j", "double");
            transfer1.put("k", "else");
            transfer1.put("l", "float");
            transfer1.put("m", "for");
            transfer1.put("n", "if");
            transfer1.put("o", "bool");
            transfer1.put("p", "long");
            transfer1.put("q", "return");
            transfer1.put("r", "short");
            transfer1.put( "s", "switch");
            transfer1.put("t", "void");
            transfer1.put("u", "struct");
            transfer1.put("v", "while");
        }
    }

    //查询符号表
    String searchTable(String s){
        return table.containsKey(s) ? table.get(s) : s.matches("[A-Za-z_][A-Za-z_0-9]*") ? "IDN" : "CONST";
    }

    //判断是否为单个操作符
    boolean containsInSet(char c){
        return op.contains(c);
    }

    //判断是否结束
    boolean isEnd(char c){
        return c == '\0';
    }


}
