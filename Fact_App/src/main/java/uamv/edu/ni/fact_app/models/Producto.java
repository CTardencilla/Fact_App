package uamv.edu.ni.fact_app.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Producto {
    private String id;
    private String nombre;
    private Categoria categoria;
    private BigDecimal precioVenta;
    private int existencia;
    private String rutaImagen;
    private boolean activo;
}
