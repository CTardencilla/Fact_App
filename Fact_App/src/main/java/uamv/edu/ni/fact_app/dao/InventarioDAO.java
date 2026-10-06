package uamv.edu.ni.fact_app.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import uamv.edu.ni.fact_app.models.Categoria;
import uamv.edu.ni.fact_app.models.Producto;
import uamv.edu.ni.fact_app.util.ConexionBD;

public class InventarioDAO {

    private String clave(String texto) {
        return texto.trim().toLowerCase(Locale.ROOT);
    }

    private void comprobarFilas(int filas) throws SQLException {
        if (filas != 1) {
            throw new SQLException(
                    "El registro ya no existe o no pudo modificarse."
            );
        }
    }

    // CATEGORÍAS

    public List<Categoria> listarCategorias() throws SQLException {
        List<Categoria> categorias = new ArrayList<>();

        String sql = """
                SELECT id, nombre, activo
                FROM categoria
                ORDER BY nombre
                """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {

            while (resultado.next()) {
                categorias.add(new Categoria(
                        resultado.getInt("id"),
                        resultado.getString("nombre"),
                        resultado.getBoolean("activo")
                ));
            }
        }

        return categorias;
    }

    public boolean existeNombreCategoria(
            String nombre,
            Integer idExcluir
    ) throws SQLException {

        String sql = """
                SELECT 1
                FROM categoria
                WHERE nombre_clave = ?
                  AND id <> ?
                LIMIT 1
                """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {

            consulta.setString(1, clave(nombre));
            consulta.setInt(2, idExcluir == null ? -1 : idExcluir);

            try (ResultSet resultado = consulta.executeQuery()) {
                return resultado.next();
            }
        }
    }

    public boolean existeCategoria(Integer id) throws SQLException {
        if (id == null) {
            return false;
        }

        String sql = "SELECT 1 FROM categoria WHERE id = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {

            consulta.setInt(1, id);

            try (ResultSet resultado = consulta.executeQuery()) {
                return resultado.next();
            }
        }
    }

    public boolean categoriaTieneProductos(Integer id)
            throws SQLException {

        String sql = """
                SELECT 1
                FROM producto
                WHERE categoria_id = ?
                LIMIT 1
                """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {

            consulta.setInt(1, id);

            try (ResultSet resultado = consulta.executeQuery()) {
                return resultado.next();
            }
        }
    }

    public void insertarCategoria(Categoria categoria)
            throws SQLException {

        String sql = """
                INSERT INTO categoria (nombre, nombre_clave, activo)
                VALUES (?, ?, ?)
                """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {

            consulta.setString(1, categoria.getNombre());
            consulta.setString(2, clave(categoria.getNombre()));
            consulta.setBoolean(3, categoria.isActivo());

            comprobarFilas(consulta.executeUpdate());
        }
    }

    public void actualizarCategoria(Categoria categoria)
            throws SQLException {

        String sql = """
                UPDATE categoria
                SET nombre = ?, nombre_clave = ?, activo = ?
                WHERE id = ?
                """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {

            consulta.setString(1, categoria.getNombre());
            consulta.setString(2, clave(categoria.getNombre()));
            consulta.setBoolean(3, categoria.isActivo());
            consulta.setInt(4, categoria.getId());

            comprobarFilas(consulta.executeUpdate());
        }
    }

    public void eliminarCategoria(Integer id) throws SQLException {
        String sql = "DELETE FROM categoria WHERE id = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {

            consulta.setInt(1, id);

            comprobarFilas(consulta.executeUpdate());
        }
    }

    // PRODUCTOS

    public List<Producto> listarProductos() throws SQLException {
        List<Producto> productos = new ArrayList<>();

        String sql = """
                SELECT p.codigo, p.nombre, p.precio_venta,
                       p.existencia, p.ruta_imagen, p.activo,
                       c.id AS categoria_id,
                       c.nombre AS categoria_nombre,
                       c.activo AS categoria_activo
                FROM producto p
                JOIN categoria c ON c.id = p.categoria_id
                ORDER BY p.codigo
                """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {

            while (resultado.next()) {
                Categoria categoria = new Categoria(
                        resultado.getInt("categoria_id"),
                        resultado.getString("categoria_nombre"),
                        resultado.getBoolean("categoria_activo")
                );

                productos.add(new Producto(
                        resultado.getString("codigo"),
                        resultado.getString("nombre"),
                        categoria,
                        new BigDecimal(
                                resultado.getString("precio_venta")
                        ),
                        resultado.getInt("existencia"),
                        resultado.getString("ruta_imagen"),
                        resultado.getBoolean("activo")
                ));
            }
        }

        return productos;
    }

    public boolean existeCodigoProducto(
            String codigo,
            String codigoExcluir
    ) throws SQLException {

        String sql = """
                SELECT codigo
                FROM producto
                WHERE codigo_clave = ?
                """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {

            consulta.setString(1, clave(codigo));

            try (ResultSet resultado = consulta.executeQuery()) {
                if (!resultado.next()) {
                    return false;
                }

                return codigoExcluir == null
                        || !resultado.getString("codigo")
                        .equals(codigoExcluir);
            }
        }
    }

    private void asignarProducto(
            PreparedStatement consulta,
            Producto producto
    ) throws SQLException {

        consulta.setString(1, producto.getId());
        consulta.setString(2, clave(producto.getId()));
        consulta.setString(3, producto.getNombre());
        consulta.setInt(4, producto.getCategoria().getId());
        consulta.setString(
                5,
                producto.getPrecioVenta().toPlainString()
        );
        consulta.setInt(6, producto.getExistencia());
        consulta.setString(7, producto.getRutaImagen());
        consulta.setBoolean(8, producto.isActivo());
    }

    public void insertarProducto(Producto producto)
            throws SQLException {

        String sql = """
                INSERT INTO producto (
                    codigo, codigo_clave, nombre, categoria_id,
                    precio_venta, existencia, ruta_imagen, activo
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {

            asignarProducto(consulta, producto);
            comprobarFilas(consulta.executeUpdate());
        }
    }

    public void actualizarProducto(
            String codigoOriginal,
            Producto producto
    ) throws SQLException {

        String sql = """
                UPDATE producto
                SET codigo = ?, codigo_clave = ?, nombre = ?,
                    categoria_id = ?, precio_venta = ?,
                    existencia = ?, ruta_imagen = ?, activo = ?
                WHERE codigo = ?
                """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {

            asignarProducto(consulta, producto);
            consulta.setString(9, codigoOriginal);

            comprobarFilas(consulta.executeUpdate());
        }
    }

    public void eliminarProducto(String codigo) throws SQLException {
        String sql = "DELETE FROM producto WHERE codigo = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {

            consulta.setString(1, codigo);

            comprobarFilas(consulta.executeUpdate());
        }
    }
}