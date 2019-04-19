package grammar;

import java.util.Stack;

/**
 * @author Super-Tang
 */
public class ShiftOrReduction extends GrammarTable {
    private Stack<Character> symbols = new Stack<>();
    private Stack<Integer> status = new Stack<>();

    ShiftOrReduction(){
    }

    public void init(){
        readGenerate();
        calculateClosure();
        generateTable();
        calculateFirstOP();
        symbols.push('S');
        status.push(0);
    }

    public void shift(char c, int s) {
        symbols.push(c);
        status.push(s);
    }

    public void reduce(int number){
        String gen = code.get(number);
        char head = gen.charAt(0);
        String tail = gen.substring(3);
        for(int i=0; i<tail.length();i++){
            symbols.pop();
            status.pop();
        }
        symbols.push(head);
        if (number != 0){
            status.push(searchGoto(head, status.peek()));
        }
    }

    public char getTopSymbol(){
        return symbols.peek();
    }

    public void popStack(){
        status.pop();
        symbols.pop();
    }

    public int getTopStatus(){
        return status.peek();
    }

    public boolean isEmpty(){
        return status.isEmpty();
    }

    public boolean contains(char  c){
        return symbols.contains(c);
    }

    private int searchGoto(char c, int status){
        return gotoTable.get(status).get(c) != null ? gotoTable.get(status).get(c) : -1;
    }

    public String searchAction(char c){
        return actionTable.get(status.peek()).get(c) != null ? actionTable.get(status.peek()).get(c) : "error";
    }

    public String toSymbolsString() {
        StringBuilder stringBuilder = new StringBuilder();
        for(Character character : symbols){
            stringBuilder.append(character).append(" ");
        }
        return stringBuilder.toString();
    }

    public String toStatusString(){
        StringBuilder stringBuilder = new StringBuilder();
        for(Integer character : status){
            stringBuilder.append(character).append(" ");
        }
        return stringBuilder.toString();
    }
}
