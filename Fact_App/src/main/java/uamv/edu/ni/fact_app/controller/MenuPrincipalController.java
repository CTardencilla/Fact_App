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
        abrirVentana(
                "/fxml/producto-view.fxml",
                "Gestión de productos"
        );
    }

    @FXML
    private void abrirCargos() {
        abrirVentana(
                "/fxml/cargo-view.fxml",
                "Gestión de cargos"
        );
    }

    @FXML
    private void abrirCategorias() {
        abrirVentana(
                "/fxml/categoria-view.fxml",
                "Gestión de categorías"
        );
    }

    @FXML
    private void abrirEmpleados() {
        abrirVentana(
                "/fxml/empleado-view.fxml",
                "Gestión de empleados"
        );
    }

    private void abrirVentana(
            String rutaFXML,
            String titulo
    ) {

        try {

            SceneManager.abrirVentana(
                    rutaFXML,
                    titulo
            );

        } catch (IOException e) {

            Alert alerta = new Alert(
                    Alert.AlertType.ERROR,
                    "No fue posible abrir la ventana.\n\n"
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

        if (alerta.showAndWait()
                .orElse(ButtonType.CANCEL)
                == ButtonType.OK) {

            Platform.exit();
        }
    }
}