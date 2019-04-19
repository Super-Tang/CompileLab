package grammar;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.HashMap;

import static grammar.GrammarTable.*;


public class GrammarUI extends Application {
    public static boolean debug;
    private ObservableList<TableField> generationField = FXCollections.observableArrayList();
    private ObservableList<TableField> stackField = FXCollections.observableArrayList();
    private ObservableList<TableField> errorField = FXCollections.observableArrayList();
    @FXML
    TableView stack, analyse, generate;
    @FXML
    TextArea table;
    @FXML
    Button all, next;
    @FXML
    TextField line;
    @FXML
    private Parent parent;
    private GrammarAnalyzer grammarAnalyzer;
    private int index = 0;


    @Override
    public void start(Stage primaryStage) throws Exception {
        parent = FXMLLoader.load(getClass().getResource("/GrammarUI.fxml"));
        grammarAnalyzer = new GrammarAnalyzer();
        grammarAnalyzer.readFromFile("m");
        primaryStage.setTitle("1160300311 汤嘉琦");
        Scene scene = new Scene(parent);
        primaryStage.setScene(scene);
        primaryStage.show();
        bind();
        showGeneration();
        showAnalyse();
        showStack();
        System.out.println(GrammarTable.items);
    }

    private void bind(){
        stack = (TableView) parent.lookup("#stack");
        table = (TextArea) parent.lookup("#table");
        analyse = (TableView) parent.lookup("#analyse");
        generate = (TableView) parent.lookup("#generate");
        all = (Button) parent.lookup("#all");
        next = (Button) parent.lookup("#next");
        line = (TextField) parent.lookup("#line");

        next.setOnAction(event -> {
            debug = true;
            line.setText(grammarAnalyzer.statements.get(index));
            grammarAnalyzer.analyse(grammarAnalyzer.transferredStatements.get(index), index);
            index ++;
            showStack();
        });

        all.setOnAction(event -> {
            debug = false;
            for(String s : grammarAnalyzer.transferredStatements){
                grammarAnalyzer.analyse(grammarAnalyzer.transferredStatements.get(index), index);
                System.out.println("index = " + index);
                index ++;
                showStack();
                showError();
            }
        });
    }

    private void showGeneration(){
        generationField.clear();
        for(int i = 0; i< code.size(); i++){
            generationField.add(new TableField(code.get(i),""+i));
        }
        TableColumn<TableField, String> gen = new TableColumn<>("产生式");
        gen.setCellValueFactory(new PropertyValueFactory<>("generation"));
        gen.setPrefWidth(245);
        TableColumn<TableField, String> num = new TableColumn<>("序号");
        num.setCellValueFactory(new PropertyValueFactory<>("number"));
        num.setPrefWidth(75);
        generate.getColumns().addAll(gen, num);
        generate.setItems(generationField);
    }

    private void showError(){
        errorField.clear();
        stack.getColumns().clear();
        TableColumn<TableField, String> gen1 = new TableColumn<>("错误信息");
        gen1.setCellValueFactory(new PropertyValueFactory<>("error"));
        TableColumn<TableField, String> gen2 = new TableColumn<>("解决方案");
        gen2.setCellValueFactory(new PropertyValueFactory<>("message"));
        stack.getColumns().addAll(gen1,gen2);
        for(int i=0; i < grammarAnalyzer.errors.size(); i += 2){
            errorField.add(new TableField(grammarAnalyzer.errors.get(i), grammarAnalyzer.errors.get(i+1),0));
        }
        stack.setItems(errorField);
    }

    private void showStack(){
        stackField.clear();
        analyse.getColumns().clear();
        TableColumn<TableField, String> symbol = new TableColumn<>("符号栈");
        symbol.setCellValueFactory(new PropertyValueFactory<>("symbolStack"));
        TableColumn<TableField, String> status = new TableColumn<>("状态栈");
        status.setCellValueFactory(new PropertyValueFactory<>("statusStack"));
        TableColumn<TableField, String> gen = new TableColumn<>("输入串");
        gen.setCellValueFactory(new PropertyValueFactory<>("leftString"));
        TableColumn<TableField, String> num = new TableColumn<>("产生式");
        num.setCellValueFactory(new PropertyValueFactory<>("generation"));
        analyse.getColumns().addAll(symbol, status, gen, num);
        for(int i=0; i< grammarAnalyzer.tokens.size(); i+=4){
            stackField.add(new TableField(grammarAnalyzer.tokens.get(i), grammarAnalyzer.tokens.get(i+1), grammarAnalyzer.tokens.get(i+2), grammarAnalyzer.tokens.get(i+3)));
        }
        analyse.setItems(stackField);
    }

    private void showAnalyse(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" \t");
        for(Character c : terminals){
            stringBuilder.append(c.toString() + "\t");
        }
        for(Character c : nonterminals){
            stringBuilder.append(c.toString() + "\t");
        }
        stringBuilder.append("\n");
        for(int i = 0; i< items.size(); i++){
            stringBuilder.append(String.valueOf(i) + "\t");
            HashMap hashMap = GrammarTable.actionTable.get(i);
            for(Character c : GrammarTable.terminals){
               stringBuilder.append(hashMap.getOrDefault(c, " ") + "\t");
            }
            HashMap hashMap1 = GrammarTable.gotoTable.get(i);
            for(Character c : GrammarTable.nonterminals){
                stringBuilder.append(String.valueOf(hashMap1.getOrDefault(c, " ") + "\t"));
            }
            stringBuilder.append("\n");
        }
        table.setText(stringBuilder.toString());
    }


    public static void main(String[] args) {
        launch(args);
    }
}
