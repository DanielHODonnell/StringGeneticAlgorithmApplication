module org.stringgenalg {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.stringgenalg to javafx.fxml;
    exports org.stringgenalg;
}