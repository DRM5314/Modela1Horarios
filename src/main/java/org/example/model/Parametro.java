package org.example.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Parametro {
    private String codigo;
    private int valor;
    private String desripcion;
}
