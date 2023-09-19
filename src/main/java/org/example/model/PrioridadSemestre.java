package org.example.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PrioridadSemestre {
    private String codigoCarrera;
    private int semestre;
    private String codigoMateria;
}
