package org.stringgenalg;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class StringGenAlgApplication extends Application {
    // Declare primary stage variable
    private Stage primaryStage;
    public static int populationSize = 250;
    public static double mutationRate = 0.01;

    @Override
    // Start method
    public void start (Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("String Genetic Algorithm!");
        showMainPage();
    }

    // Method to show the main page items as well as get controller class associated with the main page
    public void showMainPage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("StringGenAlg-view.fxml"));
        Parent root = fxmlLoader.load();
        StringGenAlgController controller = fxmlLoader.getController();
        controller.setMainApp(this);
        Scene scene = new Scene(root, 450, 340);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to display second page with the click of a button
    public void showSettingsPage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("settings.fxml"));
        Parent root = fxmlLoader.load();
        SettingsController controller = fxmlLoader.getController();
        controller.setMainApp(this);
        controller.updateFields(); // Sets values after FXML fields are initialized.
        Scene scene = new Scene(root, 450, 340);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
    }
    public static void main(String[] args) {
        launch();
    }
}