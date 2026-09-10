package uamv.edu.ni.fact_app.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import uamv.edu.ni.fact_app.models.Categoria;

public class CategoriaController {

    @FXML
    private TextField txtCodigo;

    @FXML
    private TextField txtNombre;

    @FXML
    private CheckBox chkActivo;

    @FXML
    private TableView<Categoria> tablaCategorias;

    @FXML
    private TableColumn<Categoria, Integer> colCodigo;

    @FXML
    private TableColumn<Categoria, String> colNombre;

    @FXML
    private TableColumn<Categoria, Boolean> colActivo;

    @FXML
    private Label lblMensaje;

    private final ObservableList<Categoria> categorias =
            FXCollections.observableArrayList();

    @FXML
    private void initialize() {

        colCodigo.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        colNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre")
        );

        colActivo.setCellValueFactory(
                new PropertyValueFactory<>("activo")
        );

        tablaCategorias.setItems(categorias);

        chkActivo.setSelected(true);

        tablaCategorias.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, seleccionado) -> {

                    if (seleccionado != null) {
                        txtCodigo.setText(
                                String.valueOf(seleccionado.getId())
                        );
                        txtNombre.setText(
                                seleccionado.getNombre()
                        );
                        chkActivo.setSelected(
                                seleccionado.isActivo()
                        );
                    }
                });
    }

    @FXML
    private void nuevo() {
        limpiarCampos();
        chkActivo.setSelected(true);
        lblMensaje.setText("Nueva categoría.");
    }

    @FXML
    private void guardar() {

        if (txtNombre.getText().trim().isEmpty()) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Ingrese el nombre de la categoría."
            );
            return;
        }

        int nuevoId = categorias.size() + 1;

        Categoria categoria = new Categoria(
                nuevoId,
                txtNombre.getText().trim(),
                chkActivo.isSelected()
        );

        categorias.add(categoria);

        mostrarMensaje(
                Alert.AlertType.INFORMATION,
                "Categoría guardada correctamente."
        );

        limpiarCampos();
    }

    @FXML
    private void editar() {

        Categoria seleccionada = tablaCategorias
                .getSelectionModel()
                .getSelectedItem();

        if (seleccionada == null) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Seleccione una categoría de la tabla."
            );
            return;
        }

        if (txtNombre.getText().trim().isEmpty()) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Ingrese el nombre de la categoría."
            );
            return;
        }

        seleccionada.setNombre(txtNombre.getText().trim());
        seleccionada.setActivo(chkActivo.isSelected());

        tablaCategorias.refresh();

        mostrarMensaje(
                Alert.AlertType.INFORMATION,
                "Categoría actualizada correctamente."
        );
    }

    @FXML
    private void eliminar() {

        Categoria seleccionada = tablaCategorias
                .getSelectionModel()
                .getSelectedItem();

        if (seleccionada == null) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Seleccione una categoría de la tabla."
            );
            return;
        }

        categorias.remove(seleccionada);

        mostrarMensaje(
                Alert.AlertType.INFORMATION,
                "Categoría eliminada correctamente."
        );

        limpiarCampos();
    }

    @FXML
    private void limpiar() {
        limpiarCampos();
        chkActivo.setSelected(true);
        lblMensaje.setText("Campos limpiados.");
    }

    private void limpiarCampos() {
        txtCodigo.clear();
        txtNombre.clear();
        tablaCategorias.getSelectionModel().clearSelection();
    }

    private void mostrarMensaje(
            Alert.AlertType tipo,
            String texto
    ) {
        lblMensaje.setText(texto);

        Alert alerta = new Alert(tipo);
        alerta.setTitle("Gestión de categorías");
        alerta.setHeaderText(null);
        alerta.setContentText(texto);
        alerta.showAndWait();
    }
}