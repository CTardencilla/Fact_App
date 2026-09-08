package uamv.edu.ni.fact_app.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor


public class Categoria {
    private Integer id;
    private String nombre;
    private boolean activa;


    @Override
    public String toString() {
        return nombre;
    }
}
