package uamv.edu.ni.fact_app.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import uamv.edu.ni.fact_app.models.Cargo;

public class CargoController {

    @FXML
    private TextField txtCodigo;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtDescripcion;

    @FXML
    private TableView<Cargo> tablaCargos;

    @FXML
    private TableColumn<Cargo, Integer> colCodigo;

    @FXML
    private TableColumn<Cargo, String> colNombre;

    @FXML
    private TableColumn<Cargo, String> colDescripcion;

    @FXML
    private Label lblMensaje;

    private final ObservableList<Cargo> cargos =
            FXCollections.observableArrayList();

    @FXML
    private void initialize() {

        colCodigo.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        colNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre")
        );

        colDescripcion.setCellValueFactory(
                new PropertyValueFactory<>("descripcion")
        );

        tablaCargos.setItems(cargos);

        tablaCargos.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, seleccionado) -> {

                    if (seleccionado != null) {
                        txtCodigo.setText(
                                String.valueOf(seleccionado.getId())
                        );
                        txtNombre.setText(
                                seleccionado.getNombre()
                        );
                        txtDescripcion.setText(
                                seleccionado.getDescripcion()
                        );
                    }
                });
    }

    @FXML
    private void nuevo() {
        limpiarCampos();
        lblMensaje.setText("Nuevo cargo.");
    }

    @FXML
    private void guardar() {

        if (txtNombre.getText().trim().isEmpty()) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Ingrese el nombre del cargo."
            );
            return;
        }

        int nuevoId = cargos.size() + 1;

        Cargo cargo = new Cargo(
                nuevoId,
                txtNombre.getText().trim(),
                txtDescripcion.getText().trim()
        );

        cargos.add(cargo);

        mostrarMensaje(
                Alert.AlertType.INFORMATION,
                "Cargo guardado correctamente."
        );

        limpiarCampos();
    }

    @FXML
    private void editar() {

        Cargo seleccionado = tablaCargos
                .getSelectionModel()
                .getSelectedItem();

        if (seleccionado == null) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Seleccione un cargo de la tabla."
            );
            return;
        }

        if (txtNombre.getText().trim().isEmpty()) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Ingrese el nombre del cargo."
            );
            return;
        }

        seleccionado.setNombre(txtNombre.getText().trim());
        seleccionado.setDescripcion(txtDescripcion.getText().trim());

        tablaCargos.refresh();

        mostrarMensaje(
                Alert.AlertType.INFORMATION,
                "Cargo actualizado correctamente."
        );
    }

    @FXML
    private void eliminar() {

        Cargo seleccionado = tablaCargos
                .getSelectionModel()
                .getSelectedItem();

        if (seleccionado == null) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Seleccione un cargo de la tabla."
            );
            return;
        }

        cargos.remove(seleccionado);

        mostrarMensaje(
                Alert.AlertType.INFORMATION,
                "Cargo eliminado correctamente."
        );

        limpiarCampos();
    }

    @FXML
    private void limpiar() {
        limpiarCampos();
        lblMensaje.setText("Campos limpiados.");
    }

    private void limpiarCampos() {
        txtCodigo.clear();
        txtNombre.clear();
        txtDescripcion.clear();
        tablaCargos.getSelectionModel().clearSelection();
    }

    private void mostrarMensaje(
            Alert.AlertType tipo,
            String texto
    ) {
        lblMensaje.setText(texto);

        Alert alerta = new Alert(tipo);
        alerta.setTitle("Gestión de cargos");
        alerta.setHeaderText(null);
        alerta.setContentText(texto);
        alerta.showAndWait();
    }
}