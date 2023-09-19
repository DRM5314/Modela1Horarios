package org.example.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Carrera {
    private String codigo;
    private int numeroMaterias;
    private int numeroSemestres;
    private String nombre;
}
