package org.example.data;

import org.example.model.Materia;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MateriaDAO {
    public static List<Materia> obtenerMateria(Connection conexion){
        var retorno = new ArrayList<Materia>();
        try (var result = conexion.createStatement().executeQuery("SELECT * FROM materia")){
            while (result.next()){
                retorno.add(
                        Materia.builder()
                                .codigo(result.getString("codigo"))
                                .codigoCarrera(result.getString("codigoCarrera"))
                                .nombre(result.getString("nombre"))
                                .semestre(result.getInt("semestre"))
                                .maxSescciones(result.getInt("maxSecciones"))
                                .build()
                );
            }
            return retorno;
        }catch (SQLException e){
            System.out.println("CDB:Materia"+e);
        }
        return null;
    }
}
