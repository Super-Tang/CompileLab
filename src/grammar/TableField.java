package grammar;

import javafx.beans.property.SimpleStringProperty;

public class TableField {
    private SimpleStringProperty generation = new SimpleStringProperty();
    private SimpleStringProperty number = new SimpleStringProperty();

    private SimpleStringProperty symbolStack = new SimpleStringProperty();
    private SimpleStringProperty statusStack = new SimpleStringProperty();

    private SimpleStringProperty leftString = new SimpleStringProperty();

    private SimpleStringProperty error = new SimpleStringProperty();
    private SimpleStringProperty message = new SimpleStringProperty();

    TableField(String generation, String number) {
        this.generation.set(generation);
        this.number.set(number);
    }

    TableField (String symbols, String status, String left, String gen){
        this.symbolStack.set(symbols);
        this.statusStack.set(status);
        this.leftString.set(left);
        this.generation.set(gen);
    }

    TableField(String error, String message, int number){
        this.error.set(error);
        this.message.set(message);
    }

    public String getGeneration() {
        return generation.get();
    }

    public String getSymbolStack() {
        return symbolStack.get();
    }

    public String getLeftString() {
        return leftString.get();
    }

    public String getNumber() {
        return number.get();
    }

    public String getError() {
        return error.get();
    }

    public String getMessage() {
        return message.get();
    }

    public String getStatusStack() {
        return statusStack.get();
    }

}
