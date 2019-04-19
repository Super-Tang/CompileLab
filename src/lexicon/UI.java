package lexicon;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;


public class UI extends Application{
    @FXML
    TextArea input,output;
    @FXML
    Button run;
    @FXML
    Text text;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent parent = FXMLLoader.load(getClass().getResource("/UI.fxml"));
        primaryStage.setTitle("1160300311 汤嘉琦");
        Scene scene = new Scene(parent);
        primaryStage.setScene(scene);
        primaryStage.show();
        bind(parent);
    }

    private void bind(Parent p){
        input = (TextArea) p.lookup("#input");
        output = (TextArea) p.lookup("#output");
        run = (Button) p.lookup("#run");
        text = (Text) p.lookup("#text");

        run.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                writeIntoFile();
            }
        });
    }

    private void writeIntoFile(){
        if(input.getText().isEmpty()){
            text.setText("请输入内容");
        }else{
            try {
                FileWriter fileWriter = new FileWriter(new File("1.txt"));
                fileWriter.write(input.getText());
                fileWriter.close();
                Analyzer analyzer = new Analyzer();
                analyzer.run();
                output.setText(analyzer.getArrayList());
            }catch (Exception e){
                text.setText("文件写入异常");
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
