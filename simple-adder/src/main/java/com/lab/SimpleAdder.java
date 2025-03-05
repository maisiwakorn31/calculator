package com.lab;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class SimpleAdder extends Application{
  public static void main(String[] args) {
        Application.launch(args);
  }  

  @Override
  public void start(Stage Frame) throws Exception {
    var Sc = new Scene(createMainWindow(),600,600);

    Frame.setScene(Sc);
    Frame.setTitle("calculator");
    Frame.setResizable(false);
    
    Frame.show();
  }

  private Region createMainWindow(){
    var sceneXY = new AnchorPane();
    var Sceneboder = new BorderPane();
    sceneXY.getChildren().add(createButton());
    Sceneboder.setBottom(createButton());
    return sceneXY;
  }

  private Node createButton(){
    var Button = new Button("click me");
    Button.setPrefSize(60,20);
    AnchorPane.setLeftAnchor(Button, 50.0); 
    AnchorPane.setTopAnchor(Button, 100.0);
    return Button;
  }
}
