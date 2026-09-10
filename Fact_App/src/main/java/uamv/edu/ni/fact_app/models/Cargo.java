package uamv.edu.ni.fact_app.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cargo {

    private Integer id;
    private String nombre;
    private String descripcion;

    @Override
    public String toString() {
        return nombre;
    }
}