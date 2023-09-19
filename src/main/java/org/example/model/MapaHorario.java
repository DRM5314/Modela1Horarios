package org.example.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.beans.ConstructorProperties;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Builder
@Getter
@Setter
public class MapaHorario {
    private String codigo;
    private LocalTime horaInicio;
    private LocalTime horaFin;
}
