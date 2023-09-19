package org.example.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CalificacionProfesor {
    private String codigo;
    private String codigoMateria;
    private String codigoProfesor;
    private String prioridad;
}
