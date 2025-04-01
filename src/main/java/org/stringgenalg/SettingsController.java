package org.stringgenalg;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class SettingsController {
    public static
    @FXML TextField inputPop;
    @FXML TextField inputMut;

    @FXML
    protected void onClickApply() {
        System.out.println("This is a test1");
    }

    @FXML
    protected  void onClickBack() {
        System.out.println("Test2");
    }
}
