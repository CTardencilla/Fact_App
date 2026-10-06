package uamv.edu.ni.fact_app.controller;

import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import uamv.edu.ni.fact_app.dao.InventarioDAO;
import uamv.edu.ni.fact_app.models.Categoria;
import uamv.edu.ni.fact_app.util.ConexionBD;
import uamv.edu.ni.fact_app.util.Mensajes;

public class CategoriaController {

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private CheckBox chkActivo;
    @FXML private Label lblMensaje;

    @FXML private TableView<Categoria> tablaCategorias;
    @FXML private TableColumn<Categoria, Integer> colCodigo;
    @FXML private TableColumn<Categoria, String> colNombre;
    @FXML private TableColumn<Categoria, Boolean> colActivo;

    private final InventarioDAO dao = new InventarioDAO();

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
        txtCodigo.setEditable(false);
        chkActivo.setSelected(true);

        tablaCategorias.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, seleccionada) -> {
                    if (seleccionada != null) {
                        txtCodigo.setText(
                                seleccionada.getId().toString()
                        );
                        txtNombre.setText(seleccionada.getNombre());
                        chkActivo.setSelected(seleccionada.isActivo());
                    }
                });

        try {
            ConexionBD.inicializar();
            cargarCategorias();
            lblMensaje.setText("Listo.");
        } catch (SQLException e) {
            Mensajes.errorSQL(e);
            lblMensaje.setText("No se pudieron cargar las categorías.");
        }
    }

    private void cargarCategorias() throws SQLException {
        categorias.setAll(dao.listarCategorias());
    }

    private boolean validar(Integer idExcluir) throws SQLException {
        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            Mensajes.advertencia(
                    "El nombre de la categoría es obligatorio "
                            + "y no puede contener solamente espacios."
            );
            txtNombre.requestFocus();
            return false;
        }

        if (dao.existeNombreCategoria(nombre, idExcluir)) {
            Mensajes.advertencia(
                    "Ya existe una categoría con ese nombre."
            );
            txtNombre.requestFocus();
            return false;
        }

        return true;
    }

    @FXML
    private void guardar() {
        if (tablaCategorias.getSelectionModel()
                .getSelectedItem() != null) {

            Mensajes.advertencia(
                    "Tiene una categoría seleccionada. "
                            + "Use Editar para actualizarla o Nuevo para crear otra."
            );
            return;
        }

        try {
            if (!validar(null)) {
                return;
            }

            Categoria categoria = new Categoria(
                    null,
                    txtNombre.getText().trim(),
                    chkActivo.isSelected()
            );

            dao.insertarCategoria(categoria);
            finalizarOperacion("Categoría guardada correctamente.");

        } catch (SQLException e) {
            Mensajes.errorSQL(e);
        }
    }

    @FXML
    private void editar() {
        Categoria seleccionada = tablaCategorias
                .getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            Mensajes.advertencia(
                    "Seleccione una categoría para actualizar."
            );
            return;
        }

        try {
            if (!validar(seleccionada.getId())) {
                return;
            }

            Categoria actualizada = new Categoria(
                    seleccionada.getId(),
                    txtNombre.getText().trim(),
                    chkActivo.isSelected()
            );

            dao.actualizarCategoria(actualizada);
            finalizarOperacion("Categoría actualizada correctamente.");

        } catch (SQLException e) {
            Mensajes.errorSQL(e);
        }
    }

    @FXML
    private void eliminar() {
        Categoria seleccionada = tablaCategorias
                .getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            Mensajes.advertencia(
                    "Seleccione una categoría para eliminar."
            );
            return;
        }

        if (!Mensajes.confirmar(
                "¿Desea eliminar la categoría \""
                        + seleccionada.getNombre() + "\"?"
        )) {
            return;
        }

        try {
            if (dao.categoriaTieneProductos(seleccionada.getId())) {
                Mensajes.advertencia(
                        "No se puede eliminar esta categoría "
                                + "porque tiene productos asociados. "
                                + "Puede desactivarla o cambiar la categoría "
                                + "de esos productos."
                );
                return;
            }

            dao.eliminarCategoria(seleccionada.getId());
            finalizarOperacion("Categoría eliminada correctamente.");

        } catch (SQLException e) {
            Mensajes.errorSQL(e);
        }
    }

    private void finalizarOperacion(String mensaje) {
        limpiarCampos();
        lblMensaje.setText(mensaje);
        Mensajes.informacion(mensaje);

        try {
            cargarCategorias();
        } catch (SQLException e) {
            Mensajes.errorSQL(e);
            lblMensaje.setText(
                    "La operación se realizó, "
                            + "pero no se pudo recargar la tabla."
            );
        }
    }

    @FXML
    private void nuevo() {
        limpiarCampos();
        lblMensaje.setText("Ingrese una nueva categoría.");
    }

    @FXML
    private void limpiar() {
        limpiarCampos();
        lblMensaje.setText("Campos limpiados.");
    }

    private void limpiarCampos() {
        tablaCategorias.getSelectionModel().clearSelection();
        txtCodigo.clear();
        txtNombre.clear();
        chkActivo.setSelected(true);
        txtNombre.requestFocus();
    }
}