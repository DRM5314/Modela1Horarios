package org.example.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Builder
@Getter
@Setter
public class SeccionesSalon {
    private String codigo;
    private List<Salon> salones;
    private List<MapaHorario> horario;
    private String codigoHorarioFila;
    private int capacidad;
}
