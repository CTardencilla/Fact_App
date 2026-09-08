module uamv.edu.ni.fact_app {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;


    opens uamv.edu.ni.fact_app.controller to javafx.fxml;
    exports uamv.edu.ni.fact_app;
}