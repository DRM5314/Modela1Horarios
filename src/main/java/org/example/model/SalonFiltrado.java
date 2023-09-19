package org.example.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class SalonFiltrado{
    private Salon salon;
    private List<MapaHorario> mapaHorarioList;
    private String tipo;
}
