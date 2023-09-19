package org.example.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Salon {
    private String codigo;
    private String nombre;
    private int capacidad;
}
