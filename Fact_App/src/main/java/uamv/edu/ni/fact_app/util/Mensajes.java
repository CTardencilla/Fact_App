package uamv.edu.ni.fact_app.util;

import java.sql.SQLException;
import java.util.Locale;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class Mensajes {

    public static void informacion(String texto) {
        mostrar(Alert.AlertType.INFORMATION, texto);
    }

    public static void advertencia(String texto) {
        mostrar(Alert.AlertType.WARNING, texto);
    }

    public static void error(String texto) {
        mostrar(Alert.AlertType.ERROR, texto);
    }

    private static void mostrar(Alert.AlertType tipo, String texto) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Sistema de Facturación");
        alerta.setHeaderText(null);
        alerta.setContentText(texto);
        alerta.showAndWait();
    }

    public static boolean confirmar(String texto) {
        Alert alerta = new Alert(
                Alert.AlertType.CONFIRMATION,
                texto,
                ButtonType.OK,
                ButtonType.CANCEL
        );

        alerta.setTitle("Confirmar operación");
        alerta.setHeaderText(null);

        return alerta.showAndWait()
                .orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    public static void errorSQL(SQLException e) {
        e.printStackTrace();

        String detalle = e.getMessage() == null
                ? ""
                : e.getMessage().toLowerCase(Locale.ROOT);

        if (detalle.contains("unique constraint")
                || detalle.contains("primary key")) {

            error("Ya existe un registro con ese nombre o código.");

        } else if (detalle.contains("foreign key constraint")) {

            error("La operación afecta registros relacionados. "
                    + "Verifique la categoría y los productos asociados.");

        } else if (detalle.contains("check constraint")
                || detalle.contains("not null constraint")) {

            error("Los datos no cumplen las restricciones "
                    + "de la base de datos.");

        } else if (detalle.contains("database is locked")) {

            error("La base de datos está ocupada. "
                    + "Intente nuevamente.");

        } else {

            error("No se pudo completar la operación en la base de datos. "
                    + "Revise la conexión, los permisos y la consola.");
        }
    }
}