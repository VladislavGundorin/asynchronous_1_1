module com.example.asynchronous_1_1 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.asynchronous_1_1 to javafx.fxml;
    exports com.example.asynchronous_1_1;
}