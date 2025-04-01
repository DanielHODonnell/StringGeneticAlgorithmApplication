package org.stringgenalg;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class StringGenAlgApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StringGenAlgApplication.class.getResource("StringGenAlg-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 450, 340);
        stage.setTitle("String Genetic Algorithm!");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}