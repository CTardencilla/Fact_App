package uamv.edu.ni.fact_app.util;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SceneManager {

    public static void abrirVentana(
            String rutaFXML,
            String titulo
    ) throws IOException {

        URL ubicacion = SceneManager.class.getResource(rutaFXML);

        if (ubicacion == null) {
            throw new IOException(
                    "No se encontró el archivo FXML: " + rutaFXML
            );
        }

        FXMLLoader loader = new FXMLLoader(ubicacion);

        Parent root = loader.load();

        Stage stage = new Stage();

        stage.setTitle(titulo);
        stage.setScene(new Scene(root));
        stage.setResizable(false);

        stage.show();
    }
}