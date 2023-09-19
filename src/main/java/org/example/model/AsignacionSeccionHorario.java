package org.example.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Builder
@Getter
@Setter
public class AsignacionSeccionHorario {
    private List<Salon> salonesDisponibles;
    private String codigoMapaHorario;
    private int cantidadDisponible;
}
