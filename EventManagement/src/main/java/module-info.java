module com.example.eventmanagement {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.eventmanagement to javafx.fxml;
    exports com.example.eventmanagement;
}