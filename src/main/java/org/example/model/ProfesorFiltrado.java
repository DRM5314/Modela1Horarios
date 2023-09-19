package org.example.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Builder
@Getter
@Setter
public class ProfesorFiltrado {
    private String codigoProfesor;
    private List<MapaHorario> horarioDisponible;
    private CalificacionProfesor califacion;
}
