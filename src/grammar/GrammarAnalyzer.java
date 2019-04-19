package grammar;

import lexicon.Analyzer;

import javax.sql.rowset.CachedRowSet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class GrammarAnalyzer {
    public ArrayList<String> tokens = new ArrayList<>();
    private ShiftOrReduction shiftOrReduction;
    public ArrayList<String> statements = new ArrayList<>();
    public ArrayList<String> transferredStatements;
    public ArrayList<String> errors = new ArrayList<>();
    private Analyzer analyzer = new Analyzer();
    private String left = "";

    GrammarAnalyzer(){
        if(shiftOrReduction == null){
            shiftOrReduction = new ShiftOrReduction();
            shiftOrReduction.init();
        }
    }

    void analyse(String line, int n) {
        if(!left.isEmpty()){
            line = left + line;
        }
        for (int i = 0; i < line.length() * 5; i++) {
            String action = i >= line.length() ? shiftOrReduction.searchAction('#') : shiftOrReduction.searchAction(line.charAt(i));
            if (action.charAt(0) == 's') {
                shiftOrReduction.shift(line.charAt(i), Integer.parseInt(action.substring(1)));
                tokens.add(shiftOrReduction.toSymbolsString());
                tokens.add(shiftOrReduction.toStatusString());
                tokens.add(line.substring(i+1));
                tokens.add(" ");
            } else if (action.charAt(0) == 'r') {
                while (action.charAt(0) == 'r') {
                    shiftOrReduction.reduce(Integer.parseInt(action.substring(1)));
                    tokens.add(shiftOrReduction.toSymbolsString());
                    tokens.add(shiftOrReduction.toStatusString());
                    tokens.add(line.substring(i));
                    tokens.add(GrammarTable.code.get(Integer.parseInt(action.substring(1))));
                    action = i >= line.length() ? shiftOrReduction.searchAction('#') : shiftOrReduction.searchAction(line.charAt(i));
                }
                i--;
            } else if ("acc".equals(action)) {
               // tokens.add(GrammarTable.code.get(0));
                shiftOrReduction.reduce(0);
                tokens.add(shiftOrReduction.toSymbolsString());
                tokens.add(shiftOrReduction.toStatusString());
                tokens.add(line.substring(i));
                tokens.add(GrammarTable.code.get(0));
                break;
            } else {
                if(GrammarUI.debug) {
                    break;
                }else if(i == line.length()){
                    left = line.substring(i);
                    break;
                }else {
                    errors.add("Error at line [" + n + "]");
                    String message = errorHandle(line.substring(i));
                    if(message.endsWith("0")){
                        errors.add(message.substring(0, message.length()-1));
                        line = line.substring(0, line.indexOf("+") + 1) + "w" + line.substring(line.indexOf("+") + 1);
                        i--;
                        System.out.println(line);
                    }else if(message.endsWith("1")) {
                        errors.add(message.substring(0, message.length() - 1));
                        if (line.charAt(i) == '*' || line.charAt(i + 1) == '*') {
                            line = line.substring(0, i) + line.substring(i + 1);
                        }
                        i--;
                    }else if(message.endsWith("2")){
                        errors.add(message.substring(0, message.length()-1));
                        line = line.substring(0, line.indexOf(')')) + line.substring(line.indexOf(')') + 1);
                        i --;
                    }else if(message.endsWith("3")){
                        errors.add(message.substring(0, message.length()-1));
                        line = ";" + line;
                        i --;
                    }else if(message.endsWith("4")){
                        errors.add(message.substring(0, message.length()-1));
                        line = line.substring(0, i) + "+" + line.substring(i);
                        i --;
                    }
                }
                // TODO cause of error
               // i += errorHandle(line.substring(i));
            }
        }
    }

    void readFromFile(String fileName){
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(fileName)));
            String temp;
            while ((temp = bufferedReader.readLine()) != null){
                statements.add(temp);
            }
            for(String s : statements){
                analyzer.load(s);
                analyzer.scanner();
                analyzer.transferArray();
            }
            transferredStatements = analyzer.getTransferArray();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String errorHandle(String s){
        char c = shiftOrReduction.getTopSymbol();
        if(c == '+' || c == '*'){
            if(s.charAt(0) == '*' || (c == '*' && s.charAt(0) == '+')){
                return "incompatible operators, remove the second one 1";
            }
            else if(s.charAt(0) != 'w' || s.charAt(0) != 'e'){
                return "missing right operand, add w to the remained string 0";
            }
        }else if(s.charAt(0) == ')' && !shiftOrReduction.contains('(') || s.charAt(0) == '}' && !shiftOrReduction.contains('{')){
            return "unbalanced right parenthesis, remove it from remained string 2";
        }else if(shiftOrReduction.getTopSymbol() == s.charAt(0) && GrammarTable.actionTable.get(shiftOrReduction.getTopStatus()).get(';').charAt(0) == 'r'){
            return "missing \';\', add \';\' to the tail of the statement 3";
        }else if(shiftOrReduction.getTopSymbol() == 'e' && s.charAt(0) == 'w'){
            return "missing operator, add \'+\' to the middle of operands 4";
        }else {
            // TODO other conditions
            shiftOrReduction.popStack();
            for(Character c1 : GrammarTable.terminals){
                System.out.println();
            }
        }
        return " ";
    }

    public static void main(String[] args) {
        GrammarAnalyzer analyzer = new GrammarAnalyzer();
        analyzer.analyse("cdcd", 1);
        System.out.println(analyzer.tokens);
        System.out.println("ab".substring(0,1));
    }

}
