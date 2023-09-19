package org.example.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
@Getter
@Setter
@Builder
public class Contrato {
    private String codigo;
    private String codigoProfesor;
    private LocalTime horaEntrada;
    private LocalTime horaSalida;
    private LocalDate fechaContrato;
    private LocalDate fechaVencimiento;
}
