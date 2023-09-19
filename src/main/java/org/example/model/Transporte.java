package org.example.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
@Builder
@Getter
@Setter
public class Transporte {
    private List<Carrera> carrera;
    private List<Profesor> profesor;
    private List<Materia> materia;
    private List<Salon> salon;
    private List<Contrato> contrato;
    private List<PreAsignacion> preasignacion;
    private List<AsignacionHorario> asignacionHorario;
    private List<CalificacionProfesor> calificacionProfesor;
    private List<MapaHorario> horario;
    private List<Parametro> parametros;
}
