package org.example.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Materia {
    private String codigo;
    private String codigoCarrera;
    private String nombre;
    private int semestre;
    private int maxSescciones;
}
