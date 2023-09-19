package org.example.data;

import org.example.model.Carrera;
import org.example.model.Materia;
import org.example.model.PrioridadSemestre;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PrioridadSemestreDAO {
    private Connection conexion;
    public PrioridadSemestreDAO(Connection conexion){
        this.conexion = conexion;
    }

    public List<PrioridadSemestre> prioridadSemestre(){
        var retorno = new ArrayList<PrioridadSemestre>();
            String consulta = "select m.codigo, m.codigoCarrera, m.semestre " +
                "from materia as m " +
                "where m.codigo = (select p.codigoMateria from preAsignacion as p where p.codigoMateria = m.codigo);";
        try (var resultado = this.conexion.createStatement().executeQuery(consulta)){
            while (resultado.next()){
                retorno.add(
                        PrioridadSemestre.builder().
                        codigoCarrera(resultado.getString("m.codigoCarrera")).
                        semestre(resultado.getInt("m.semestre")).
                        codigoMateria(resultado.getString("m.codigo")).
                        build()
                );
            }
            return retorno;
        }catch (SQLException e){
            System.out.println("Error dao prioridad semestre");
        }
        return null;
    }

}
