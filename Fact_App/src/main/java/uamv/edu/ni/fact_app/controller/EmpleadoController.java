package uamv.edu.ni.fact_app.controller;

import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import uamv.edu.ni.fact_app.models.Cargo;
import uamv.edu.ni.fact_app.models.Empleado;

public class EmpleadoController {

    @FXML
    private TextField txtCodigo;

    @FXML
    private TextField txtNombres;

    @FXML
    private TextField txtApellidos;

    @FXML
    private ComboBox<Cargo> cmbCargo;

    @FXML
    private DatePicker dpFechaContratacion;

    @FXML
    private CheckBox chkActivo;

    @FXML
    private TableView<Empleado> tblEmpleados;

    @FXML
    private TableColumn<Empleado, Integer> colCodigo;

    @FXML
    private TableColumn<Empleado, String> colNombres;

    @FXML
    private TableColumn<Empleado, String> colApellidos;

    @FXML
    private TableColumn<Empleado, Cargo> colCargo;

    @FXML
    private TableColumn<Empleado, LocalDate> colFecha;

    @FXML
    private TableColumn<Empleado, Boolean> colActivo;

    @FXML
    private Label lblMensaje;

    private final ObservableList<Empleado> empleados =
            FXCollections.observableArrayList();

    private final ObservableList<Cargo> cargos =
            FXCollections.observableArrayList(
                    new Cargo(1, "Administrador", "Administra el sistema"),
                    new Cargo(2, "Vendedor", "Realiza ventas"),
                    new Cargo(3, "Bodeguero", "Controla el inventario")
            );

    @FXML
    private void initialize() {

        cmbCargo.setItems(cargos);

        colCodigo.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        colNombres.setCellValueFactory(
                new PropertyValueFactory<>("nombres")
        );

        colApellidos.setCellValueFactory(
                new PropertyValueFactory<>("apellidos")
        );

        colCargo.setCellValueFactory(
                new PropertyValueFactory<>("cargo")
        );

        colFecha.setCellValueFactory(
                new PropertyValueFactory<>("fechaContratacion")
        );

        colActivo.setCellValueFactory(
                new PropertyValueFactory<>("activo")
        );

        tblEmpleados.setItems(empleados);

        chkActivo.setSelected(true);
        dpFechaContratacion.setValue(LocalDate.now());

        tblEmpleados.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, seleccionado) -> {

                    if (seleccionado != null) {
                        txtCodigo.setText(
                                String.valueOf(seleccionado.getId())
                        );
                        txtNombres.setText(
                                seleccionado.getNombres()
                        );
                        txtApellidos.setText(
                                seleccionado.getApellidos()
                        );
                        cmbCargo.setValue(
                                seleccionado.getCargo()
                        );
                        dpFechaContratacion.setValue(
                                seleccionado.getFechaContratacion()
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
        dpFechaContratacion.setValue(LocalDate.now());
        lblMensaje.setText("Nuevo empleado.");
    }

    @FXML
    private void guardar() {

        if (txtNombres.getText().trim().isEmpty()
                || txtApellidos.getText().trim().isEmpty()
                || cmbCargo.getValue() == null
                || dpFechaContratacion.getValue() == null) {

            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Complete todos los campos obligatorios."
            );
            return;
        }

        int nuevoId = empleados.size() + 1;

        Empleado empleado = new Empleado(
                nuevoId,
                txtNombres.getText().trim(),
                txtApellidos.getText().trim(),
                cmbCargo.getValue(),
                dpFechaContratacion.getValue(),
                chkActivo.isSelected()
        );

        empleados.add(empleado);

        mostrarMensaje(
                Alert.AlertType.INFORMATION,
                "Empleado guardado correctamente."
        );

        limpiarCampos();
    }

    @FXML
    private void editar() {

        Empleado seleccionado = tblEmpleados
                .getSelectionModel()
                .getSelectedItem();

        if (seleccionado == null) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Seleccione un empleado de la tabla."
            );
            return;
        }

        seleccionado.setNombres(txtNombres.getText().trim());
        seleccionado.setApellidos(txtApellidos.getText().trim());
        seleccionado.setCargo(cmbCargo.getValue());
        seleccionado.setFechaContratacion(
                dpFechaContratacion.getValue()
        );
        seleccionado.setActivo(chkActivo.isSelected());

        tblEmpleados.refresh();

        mostrarMensaje(
                Alert.AlertType.INFORMATION,
                "Empleado actualizado correctamente."
        );
    }

    @FXML
    private void eliminar() {

        Empleado seleccionado = tblEmpleados
                .getSelectionModel()
                .getSelectedItem();

        if (seleccionado == null) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Seleccione un empleado de la tabla."
            );
            return;
        }

        empleados.remove(seleccionado);

        mostrarMensaje(
                Alert.AlertType.INFORMATION,
                "Empleado eliminado correctamente."
        );

        limpiarCampos();
    }

    @FXML
    private void limpiar() {
        limpiarCampos();
        chkActivo.setSelected(true);
        dpFechaContratacion.setValue(LocalDate.now());
        lblMensaje.setText("Campos limpiados.");
    }

    private void limpiarCampos() {
        txtCodigo.clear();
        txtNombres.clear();
        txtApellidos.clear();
        cmbCargo.getSelectionModel().clearSelection();
        dpFechaContratacion.setValue(null);
        tblEmpleados.getSelectionModel().clearSelection();
    }

    private void mostrarMensaje(
            Alert.AlertType tipo,
            String texto
    ) {
        lblMensaje.setText(texto);

        Alert alerta = new Alert(tipo);
        alerta.setTitle("Gestión de empleados");
        alerta.setHeaderText(null);
        alerta.setContentText(texto);
        alerta.showAndWait();
    }
}