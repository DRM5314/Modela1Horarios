package org.example.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class AsignacionHorario {
    private String codigo;
    private String codigoMateria;
    private String codigoSalon;
    private String codigoProfesor;
    private String codigoMapaHorario;
    private String codigoCarrera;
    private int semestre;
    private String esSeccion;
}
