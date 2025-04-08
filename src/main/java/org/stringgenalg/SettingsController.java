package org.stringgenalg;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.IOException;

public class SettingsController {
    private StringGenAlgApplication mainApp;

    @FXML
    private TextField inputPop;
    @FXML
    private TextField inputMut;

    @FXML
    private Label savedSettings;



    public void setMainApp(StringGenAlgApplication mainApp) {
        this.mainApp = mainApp;
        // Don't try to access FXML fields here as they might not be initialized yet
    }

    @FXML
    protected void onClickApply() {
        try {
            int newPopSize = Integer.parseInt(inputPop.getText());
            double newMutRate = Double.parseDouble(inputMut.getText()) / 100;
            StringGenAlgApplication.populationSize = newPopSize;
            StringGenAlgApplication.mutationRate = newMutRate;
            savedSettings.setText("Saved settings.");

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("Incorrect format!");
            alert.setContentText("Please enter the correct format.");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("pressed 2.");
                }
            });
        }
    }

    @FXML
    protected void onClickBack() {
        try {
            mainApp.showMainPage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Add this method to update fields when settings page is shown
    public void updateFields() {
        if (inputPop != null && inputMut != null) {
            inputPop.setText(String.valueOf(StringGenAlgApplication.populationSize));
            inputMut.setText(String.valueOf(StringGenAlgApplication.mutationRate * 100));
        }
    }
}
