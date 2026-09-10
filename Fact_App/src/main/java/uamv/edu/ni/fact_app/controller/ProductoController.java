package uamv.edu.ni.fact_app.controller;

import java.io.File;
import java.math.BigDecimal;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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

import uamv.edu.ni.fact_app.models.Categoria;
import uamv.edu.ni.fact_app.models.Producto;

public class ProductoController {

    @FXML
    private TextField txtCodigo;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtPrecio;

    @FXML
    private TextField txtExistencia;

    @FXML
    private ComboBox<Categoria> cmbCategoria;

    @FXML
    private CheckBox chkActivo;

    @FXML
    private ImageView imgProducto;

    @FXML
    private TableView<Producto> tblProductos;

    @FXML
    private TableColumn<Producto, String> colCodigo;

    @FXML
    private TableColumn<Producto, String> colNombre;

    @FXML
    private TableColumn<Producto, Categoria> colCategoria;

    @FXML
    private TableColumn<Producto, BigDecimal> colPrecio;

    @FXML
    private TableColumn<Producto, Integer> colExistencia;

    @FXML
    private TableColumn<Producto, Boolean> colActivo;

    private final ObservableList<Producto> productos =
            FXCollections.observableArrayList();

    private final ObservableList<Categoria> categorias =
            FXCollections.observableArrayList(
                    new Categoria(1, "Alimentos", true),
                    new Categoria(2, "Bebidas", true),
                    new Categoria(3, "Limpieza", true)
            );

    private String rutaImagen;

    @FXML
    private void initialize() {

        cmbCategoria.setItems(categorias);
        tblProductos.setItems(productos);

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

        chkActivo.setSelected(true);

        tblProductos.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, producto) -> {

                    if (producto != null) {
                        txtCodigo.setText(producto.getId());
                        txtNombre.setText(producto.getNombre());
                        cmbCategoria.setValue(producto.getCategoria());
                        txtPrecio.setText(
                                producto.getPrecioVenta().toString()
                        );
                        txtExistencia.setText(
                                String.valueOf(producto.getExistencia())
                        );
                        chkActivo.setSelected(producto.isActivo());

                        cargarImagen(producto.getRutaImagen());
                    }
                });
    }

    @FXML
    private void seleccionarImagen() {

        FileChooser chooser = new FileChooser();

        chooser.setTitle("Seleccionar imagen");

        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Imágenes",
                        "*.png",
                        "*.jpg",
                        "*.jpeg"
                )
        );

        Stage stage = (Stage) txtCodigo
                .getScene()
                .getWindow();

        File archivo = chooser.showOpenDialog(stage);

        if (archivo != null) {
            rutaImagen = archivo.toURI().toString();
            imgProducto.setImage(new Image(rutaImagen));
        }
    }

    @FXML
    private void guardar() {

        if (txtCodigo.getText().trim().isEmpty()
                || txtNombre.getText().trim().isEmpty()
                || txtPrecio.getText().trim().isEmpty()
                || txtExistencia.getText().trim().isEmpty()
                || cmbCategoria.getValue() == null) {

            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Complete todos los campos obligatorios."
            );
            return;
        }

        try {
            BigDecimal precio = new BigDecimal(
                    txtPrecio.getText().trim()
            );

            int existencia = Integer.parseInt(
                    txtExistencia.getText().trim()
            );

            if (precio.signum() <= 0 || existencia < 0) {
                mostrarMensaje(
                        Alert.AlertType.WARNING,
                        "Precio mayor que cero y existencia no negativa."
                );
                return;
            }

            Producto producto = new Producto(
                    txtCodigo.getText().trim(),
                    txtNombre.getText().trim(),
                    cmbCategoria.getValue(),
                    precio,
                    existencia,
                    rutaImagen,
                    chkActivo.isSelected()
            );

            productos.add(producto);

            mostrarMensaje(
                    Alert.AlertType.INFORMATION,
                    "Producto guardado correctamente."
            );

            limpiar();

        } catch (NumberFormatException e) {
            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Precio o existencia no válidos."
            );
        }
    }

    @FXML
    private void editar() {

        Producto producto = tblProductos
                .getSelectionModel()
                .getSelectedItem();

        if (producto == null) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Seleccione un producto."
            );
            return;
        }

        try {
            producto.setId(txtCodigo.getText().trim());
            producto.setNombre(txtNombre.getText().trim());
            producto.setCategoria(cmbCategoria.getValue());
            producto.setPrecioVenta(
                    new BigDecimal(txtPrecio.getText().trim())
            );
            producto.setExistencia(
                    Integer.parseInt(txtExistencia.getText().trim())
            );
            producto.setRutaImagen(rutaImagen);
            producto.setActivo(chkActivo.isSelected());

            tblProductos.refresh();

            mostrarMensaje(
                    Alert.AlertType.INFORMATION,
                    "Producto actualizado correctamente."
            );

        } catch (NumberFormatException e) {
            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Precio o existencia no válidos."
            );
        }
    }

    @FXML
    private void eliminar() {

        Producto producto = tblProductos
                .getSelectionModel()
                .getSelectedItem();

        if (producto == null) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Seleccione un producto."
            );
            return;
        }

        productos.remove(producto);
        limpiar();

        mostrarMensaje(
                Alert.AlertType.INFORMATION,
                "Producto eliminado correctamente."
        );
    }

    @FXML
    private void limpiar() {

        txtCodigo.clear();
        txtNombre.clear();
        txtPrecio.clear();
        txtExistencia.clear();

        cmbCategoria.getSelectionModel().clearSelection();

        chkActivo.setSelected(true);

        imgProducto.setImage(null);
        rutaImagen = null;

        tblProductos.getSelectionModel().clearSelection();
    }

    @FXML
    private void cerrar() {

        Stage stage = (Stage) txtCodigo
                .getScene()
                .getWindow();

        stage.close();
    }

    private void cargarImagen(String ruta) {

        if (ruta != null && !ruta.isBlank()) {
            rutaImagen = ruta;
            imgProducto.setImage(new Image(ruta));
        } else {
            rutaImagen = null;
            imgProducto.setImage(null);
        }
    }

    private void mostrarMensaje(
            Alert.AlertType tipo,
            String texto
    ) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Gestión de productos");
        alerta.setHeaderText(null);
        alerta.setContentText(texto);
        alerta.showAndWait();
    }
}