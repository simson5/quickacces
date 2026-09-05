module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jetbrains.annotations;
    requires com.fasterxml.jackson.core;

    opens org.example.controller to javafx.fxml;
    //opens org.exmaple.controller.Homecontroller to javafx.fxml;

    exports org.example;
    //opens org.example.model to javafx.fxml;
}