package org.example.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PreAsignacion {
    private String codigo;
    private String codigoMateria;
    private int numeroAsignaciones;
}
