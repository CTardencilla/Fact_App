package uamv.edu.ni.fact_app.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionBD {

    private static final String URL = "jdbc:sqlite:facturacion.db";

    public static Connection conectar() throws SQLException {
        Connection conexion = DriverManager.getConnection(URL);

        try (Statement sentencia = conexion.createStatement()) {
            sentencia.execute("PRAGMA foreign_keys = ON");
            sentencia.execute("PRAGMA busy_timeout = 5000");
        } catch (SQLException e) {
            conexion.close();
            throw e;
        }

        return conexion;
    }

    public static void inicializar() throws SQLException {

        String tablaCategoria = """
                CREATE TABLE IF NOT EXISTS categoria (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL
                        CHECK (length(trim(nombre)) > 0),
                    nombre_clave TEXT NOT NULL UNIQUE,
                    activo INTEGER NOT NULL
                        CHECK (activo IN (0, 1))
                )
                """;

        String tablaProducto = """
                CREATE TABLE IF NOT EXISTS producto (
                    codigo TEXT PRIMARY KEY NOT NULL
                        CHECK (length(trim(codigo)) > 0),
                    codigo_clave TEXT NOT NULL UNIQUE,
                    nombre TEXT NOT NULL
                        CHECK (length(trim(nombre)) > 0),
                    categoria_id INTEGER NOT NULL,
                    precio_venta TEXT NOT NULL
                        CHECK (CAST(precio_venta AS NUMERIC) > 0),
                    existencia INTEGER NOT NULL
                        CHECK (
                            typeof(existencia) = 'integer'
                            AND existencia >= 0
                        ),
                    ruta_imagen TEXT,
                    activo INTEGER NOT NULL
                        CHECK (activo IN (0, 1)),
                    FOREIGN KEY (categoria_id)
                        REFERENCES categoria(id)
                        ON DELETE RESTRICT
                        ON UPDATE RESTRICT
                )
                """;

        try (Connection conexion = conectar();
             Statement sentencia = conexion.createStatement()) {

            sentencia.execute(tablaCategoria);
            sentencia.execute(tablaProducto);
        }
    }
}