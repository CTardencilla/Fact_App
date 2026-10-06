module uamv.edu.ni.fact_app {

    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;
    requires org.xerial.sqlitejdbc;

    requires org.slf4j.simple;

    requires static lombok;

    exports uamv.edu.ni.fact_app;
    exports uamv.edu.ni.fact_app.application;
    exports uamv.edu.ni.fact_app.controller;
    exports uamv.edu.ni.fact_app.models;
    exports uamv.edu.ni.fact_app.util;

    opens uamv.edu.ni.fact_app.application
            to javafx.graphics, javafx.fxml;

    opens uamv.edu.ni.fact_app.controller
            to javafx.fxml;

    opens uamv.edu.ni.fact_app.models
            to javafx.base, javafx.fxml;
}