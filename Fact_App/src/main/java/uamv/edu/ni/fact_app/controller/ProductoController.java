package uamv.edu.ni.fact_app.controller;

import java.io.File;
import java.math.BigDecimal;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import uamv.edu.ni.fact_app.dao.InventarioDAO;
import uamv.edu.ni.fact_app.models.Categoria;
import uamv.edu.ni.fact_app.models.Producto;
import uamv.edu.ni.fact_app.util.ConexionBD;
import uamv.edu.ni.fact_app.util.Mensajes;

public class ProductoController {

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtExistencia;
    @FXML private ComboBox<Categoria> cmbCategoria;
    @FXML private CheckBox chkActivo;
    @FXML private ImageView imgProducto;

    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Categoria> colCategoria;
    @FXML private TableColumn<Producto, BigDecimal> colPrecio;
    @FXML private TableColumn<Producto, Integer> colExistencia;
    @FXML private TableColumn<Producto, Boolean> colActivo;

    private final InventarioDAO dao = new InventarioDAO();

    private final ObservableList<Producto> productos =
            FXCollections.observableArrayList();

    private final ObservableList<Categoria> categorias =
            FXCollections.observableArrayList();

    private String rutaImagen;

    @FXML
    private void initialize() {
        colCodigo.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );
        colNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre")
        );
        colCategoria.setCellValueFactory(
                new PropertyValueFactory<>("categoria")
        );
        colPrecio.setCellValueFactory(
                new PropertyValueFactory<>("precioVenta")
        );
        colExistencia.setCellValueFactory(
                new PropertyValueFactory<>("existencia")
        );
        colActivo.setCellValueFactory(
                new PropertyValueFactory<>("activo")
        );

        tblProductos.setItems(productos);
        cmbCategoria.setItems(categorias);
        chkActivo.setSelected(true);

        tblProductos.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, seleccionado) -> {
                    if (seleccionado != null) {
                        mostrarProducto(seleccionado);
                    }
                });

        cmbCategoria.setOnShowing(evento -> {
            try {
                cargarCategorias();
            } catch (SQLException e) {
                Mensajes.errorSQL(e);
            }
        });

        try {
            ConexionBD.inicializar();
            cargarCategorias();
            cargarProductos();
        } catch (SQLException e) {
            Mensajes.errorSQL(e);
        }
    }

    private void cargarProductos() throws SQLException {
        productos.setAll(dao.listarProductos());
    }

    private void cargarCategorias() throws SQLException {
        Categoria anterior = cmbCategoria.getValue();

        categorias.setAll(dao.listarCategorias());

        cmbCategoria.getSelectionModel().clearSelection();

        if (anterior != null) {
            for (Categoria categoria : categorias) {
                if (categoria.getId().equals(anterior.getId())) {
                    cmbCategoria.setValue(categoria);
                    break;
                }
            }
        }
    }

    private void mostrarProducto(Producto producto) {
        txtCodigo.setText(producto.getId());
        txtNombre.setText(producto.getNombre());
        txtPrecio.setText(
                producto.getPrecioVenta().toPlainString()
        );
        txtExistencia.setText(
                String.valueOf(producto.getExistencia())
        );
        chkActivo.setSelected(producto.isActivo());

        cmbCategoria.setValue(producto.getCategoria());

        cargarImagen(producto.getRutaImagen());
    }

    private Producto validarYCrear(String codigoExcluir)
            throws SQLException {

        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String textoPrecio = txtPrecio.getText().trim();
        String textoExistencia = txtExistencia.getText().trim();

        if (codigo.isEmpty()) {
            Mensajes.advertencia("El código es obligatorio.");
            txtCodigo.requestFocus();
            return null;
        }

        if (nombre.isEmpty()) {
            Mensajes.advertencia("El nombre es obligatorio.");
            txtNombre.requestFocus();
            return null;
        }

        Categoria categoria = cmbCategoria.getValue();

        if (categoria == null) {
            Mensajes.advertencia("Seleccione una categoría.");
            cmbCategoria.requestFocus();
            return null;
        }

        if (textoPrecio.isEmpty() || textoExistencia.isEmpty()) {
            Mensajes.advertencia(
                    "El precio y la existencia son obligatorios."
            );
            return null;
        }

        BigDecimal precio;
        int existencia;

        try {
            precio = new BigDecimal(textoPrecio);
        } catch (NumberFormatException e) {
            Mensajes.advertencia(
                    "El precio debe ser numérico. "
                            + "Use punto decimal, por ejemplo: 150.50."
            );
            txtPrecio.requestFocus();
            return null;
        }

        try {
            existencia = Integer.parseInt(textoExistencia);
        } catch (NumberFormatException e) {
            Mensajes.advertencia(
                    "La existencia debe ser un número entero "
                            + "dentro del rango permitido. Ejemplo: 10."
            );
            txtExistencia.requestFocus();
            return null;
        }

        if (precio.signum() <= 0) {
            Mensajes.advertencia(
                    "El precio debe ser mayor que cero."
            );
            txtPrecio.requestFocus();
            return null;
        }

        if (existencia < 0) {
            Mensajes.advertencia(
                    "La existencia no puede ser negativa."
            );
            txtExistencia.requestFocus();
            return null;
        }

        if (!dao.existeCategoria(categoria.getId())) {
            Mensajes.advertencia(
                    "La categoría seleccionada ya no existe. "
                            + "Seleccione otra categoría."
            );
            cargarCategorias();
            return null;
        }

        if (dao.existeCodigoProducto(codigo, codigoExcluir)) {
            Mensajes.advertencia(
                    "Ya existe otro producto con ese código."
            );
            txtCodigo.requestFocus();
            return null;
        }

        return new Producto(
                codigo,
                nombre,
                categoria,
                precio,
                existencia,
                rutaImagen,
                chkActivo.isSelected()
        );
    }

    @FXML
    private void guardar() {
        if (tblProductos.getSelectionModel()
                .getSelectedItem() != null) {

            Mensajes.advertencia(
                    "Tiene un producto seleccionado. "
                            + "Use Editar para actualizarlo "
                            + "o Limpiar para registrar uno nuevo."
            );
            return;
        }

        try {
            Producto producto = validarYCrear(null);

            if (producto == null) {
                return;
            }

            dao.insertarProducto(producto);
            finalizarOperacion("Producto guardado correctamente.");

        } catch (SQLException e) {
            Mensajes.errorSQL(e);
        }
    }

    @FXML
    private void editar() {
        Producto seleccionado = tblProductos
                .getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            Mensajes.advertencia(
                    "Seleccione un producto para actualizar."
            );
            return;
        }

        String codigoOriginal = seleccionado.getId();

        try {
            Producto actualizado = validarYCrear(codigoOriginal);

            if (actualizado == null) {
                return;
            }

            dao.actualizarProducto(codigoOriginal, actualizado);
            finalizarOperacion("Producto actualizado correctamente.");

        } catch (SQLException e) {
            Mensajes.errorSQL(e);
        }
    }

    @FXML
    private void eliminar() {
        Producto seleccionado = tblProductos
                .getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            Mensajes.advertencia(
                    "Seleccione un producto para eliminar."
            );
            return;
        }

        if (!Mensajes.confirmar(
                "¿Desea eliminar el producto \""
                        + seleccionado.getNombre()
                        + "\" con código "
                        + seleccionado.getId() + "?"
        )) {
            return;
        }

        try {
            dao.eliminarProducto(seleccionado.getId());
            finalizarOperacion("Producto eliminado correctamente.");

        } catch (SQLException e) {
            Mensajes.errorSQL(e);
        }
    }

    private void finalizarOperacion(String mensaje) {
        limpiarCampos();
        Mensajes.informacion(mensaje);

        try {
            cargarCategorias();
            cargarProductos();
        } catch (SQLException e) {
            Mensajes.errorSQL(e);
            Mensajes.advertencia(
                    "La operación se realizó, "
                            + "pero no se pudo recargar el formulario."
            );
        }
    }

    @FXML
    private void seleccionarImagen() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Seleccionar imagen del producto");

        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Imágenes",
                        "*.png", "*.jpg", "*.jpeg"
                )
        );

        File archivo = selector.showOpenDialog(
                txtCodigo.getScene().getWindow()
        );

        if (archivo != null) {
            cargarImagen(archivo.toURI().toString());
        }
    }

    private void cargarImagen(String ruta) {
        rutaImagen = ruta;
        imgProducto.setImage(null);

        if (ruta == null || ruta.isBlank()) {
            return;
        }

        try {
            Image imagen = new Image(ruta);

            if (imagen.isError()) {
                Mensajes.advertencia(
                        "No se pudo cargar la imagen. "
                                + "Seleccione otra si desea reemplazarla."
                );
                return;
            }

            imgProducto.setImage(imagen);

        } catch (IllegalArgumentException e) {
            Mensajes.advertencia(
                    "La ruta de la imagen no es válida."
            );
        }
    }

    @FXML
    private void nuevo() {
        limpiarCampos();
    }

    @FXML
    private void limpiar() {
        limpiarCampos();
    }

    private void limpiarCampos() {
        tblProductos.getSelectionModel().clearSelection();

        txtCodigo.clear();
        txtNombre.clear();
        txtPrecio.clear();
        txtExistencia.clear();

        cmbCategoria.getSelectionModel().clearSelection();
        chkActivo.setSelected(true);

        imgProducto.setImage(null);
        rutaImagen = null;

        txtCodigo.requestFocus();
    }

    @FXML
    private void cerrar() {
        Stage ventana = (Stage) txtCodigo
                .getScene().getWindow();

        ventana.close();
    }
}