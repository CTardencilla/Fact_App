package uamv.edu.ni.fact_app.controller;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import uamv.edu.ni.fact_app.util.SceneManager;

public class MenuPrincipalController {

    @FXML
    private void abrirProductos() {

        try {
            SceneManager.abrirVentana(
                    "/fxml/producto-view.fxml",
                    "Gestión de productos"
            );

        } catch (IOException e) {

            Alert alerta = new Alert(
                    Alert.AlertType.ERROR,
                    "No fue posible abrir Productos.\n\n"
                            + e.getMessage(),
                    ButtonType.OK
            );

            alerta.showAndWait();
        }
    }

    @FXML
    private void salir() {

        Alert alerta = new Alert(
                Alert.AlertType.CONFIRMATION,
                "¿Desea cerrar la aplicación?",
                ButtonType.OK,
                ButtonType.CANCEL
        );

        if (alerta.showAndWait().orElse(ButtonType.CANCEL)
                == ButtonType.OK) {

            Platform.exit();
        }
    }
}