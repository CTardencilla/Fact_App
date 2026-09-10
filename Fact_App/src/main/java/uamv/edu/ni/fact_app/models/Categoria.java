package uamv.edu.ni.fact_app.models;

public class Categoria {

    private Integer id;
    private String nombre;
    private boolean activo;

    public Categoria(Integer id, String nombre, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.activo = activo;
    }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isActivo() {
        return activo;
    }

    @Override
    public String toString() {
        return nombre;
    }
}