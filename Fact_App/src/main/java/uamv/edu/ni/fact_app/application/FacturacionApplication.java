package uamv.edu.ni.fact_app.application;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FacturacionApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        URL recursoFXML = FacturacionApplication.class.getResource(
                "/fxml/menu-principal.fxml"
        );

        if (recursoFXML == null) {
            throw new IOException(
                    "No se encontró el archivo "
                            + "src/main/resources/fxml/menu-principal.fxml"
            );
        }

        FXMLLoader loader = new FXMLLoader(recursoFXML);

        Parent root = loader.load();

        Scene scene = new Scene(root);

        stage.setTitle("Sistema de Facturación");
        stage.setScene(scene);
        stage.show();
    }
}