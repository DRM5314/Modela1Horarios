package org.example.data;

import org.example.model.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ConsultaDb {
    private Connection conexion;

    public ConsultaDb(Connection conexion){
        this.conexion = conexion;
    }

    private List<Salon> obtenerSalon(){
        var retorno = new ArrayList<Salon>();
        try (var result = this.conexion.createStatement().executeQuery("SELECT * FROM salon")){
            while (result.next()){
                retorno.add(Salon.builder().
                        codigo(result.getString("codigo")).
                        nombre(result.getString("nombre")).
                        capacidad(result.getInt("capacidad"))
                        .build());
            }
            return retorno;
        }catch (SQLException e){
            System.out.println("CDB:Csalon"+e);
        }
    return null;
    }

    private List<Carrera> obtenerCarrera(){
        var retorno = new ArrayList<Carrera>();
        try (var result = this.conexion.createStatement().executeQuery("SELECT * FROM carrera")){
            while (result.next()){
                retorno.add(
                        Carrera.builder().
                        codigo(result.getString("codigo")).
                        numeroMaterias(result.getInt("numeroMaterias")).
                        numeroSemestres(result.getInt("numeroSemestres")).
                        nombre(result.getString("nombre"))
                        .build()
                );
            }
            return retorno;
        }catch (SQLException e){
            System.out.println("CDB:Csalon"+e);
        }
        return null;
    }

    private List<Materia> obtenerMateria(){
        var retorno = new ArrayList<Materia>();
        try (var result = this.conexion.createStatement().executeQuery("SELECT * FROM materia")){
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

    private List<Profesor> obtenerProfesor(){
        var retorno = new ArrayList<Profesor>();
        try (var result = this.conexion.createStatement().executeQuery("SELECT * FROM profesor")){
            while (result.next()) {
                retorno.add(
                        Profesor.builder()
                        .codigo(result.getString("codigo"))
                        .nombre(result.getString("nombre"))
                        .codigoCarrera(result.getString("codigoCarrera"))
                        .build()
            );
            }
            return retorno;
        }catch (SQLException e){
            System.out.println("CDB:Maestro"+e);
        }
        return null;
    }

    private List<Contrato> obtenerContrato (){
        var retorno = new ArrayList<Contrato>();
        try (var resultado = this.conexion.createStatement().executeQuery("SELECT * FROM contrato")){
            while (resultado.next()){
                retorno.add(
                        Contrato.builder()
                                .codigo(resultado.getString("codigo"))
                                .codigoProfesor(resultado.getString("codigoProfesor"))
                                .horaEntrada(LocalTime.parse(resultado.getString("horarioEntrada")))
                                .horaSalida(LocalTime.parse(resultado.getString("horarioSalida")))
                                .fechaContrato(LocalDate.parse(resultado.getString("fechaContrato")))
                                .fechaVencimiento(LocalDate.parse(resultado.getString("fechaVencimiento")))
                                .build()
                );
            }
            return retorno;
        }catch (SQLException e){
            System.out.println("CDB:Contrato"+e);
        }
        return null;
    }

    private List<PreAsignacion> obtenerPreAsignacion(){
        var retorno = new ArrayList<PreAsignacion>();
        try (var resultado = this.conexion.createStatement().executeQuery("SELECT * FROM preAsignacion")){
            while (resultado.next()){
                retorno.add(
                        PreAsignacion.builder()
                        .codigo(resultado.getString("codigo"))
                        .codigoMateria(resultado.getString("codigoMateria"))
                        .numeroAsignaciones(resultado.getInt("numeroAsignaciones"))
                        .build()
                );
            }
            return retorno;
        }catch (SQLException e){
            System.out.println("CDB:Preasignacion"+e);
        }
        return null;
    }

    private List<AsignacionHorario> obtenerAsignacionHorario(){
        var retorno = new ArrayList<AsignacionHorario>();
        try (var resultado = this.conexion.createStatement().executeQuery("SELECT * FROM asignacionHorario")){
            while (resultado.next()){
                retorno.add(
                        AsignacionHorario.builder()
                        .codigo(resultado.getString("codigo"))
                        .codigoMateria(resultado.getString("codigoMateria"))
                        .codigoSalon(resultado.getString("codigoSalon"))
                        .codigoProfesor(resultado.getString("codigoProfesor"))
                        .build()
                );
            }
            return retorno;
        }catch (SQLException e){
            System.out.println("CDB:Horario"+e);
        }
        return null;
    }

    private List<CalificacionProfesor> obtenerCalificacionProfesor(){
        var retorno = new ArrayList<CalificacionProfesor>();
        try (var result = this.conexion.createStatement().executeQuery("SELECT * FROM calificacion")){
            while (result.next()){
                retorno.add(
                        CalificacionProfesor.builder()
                                .codigo(result.getString("codigo"))
                                .codigoMateria(result.getString("codigoMateria"))
                                .codigoProfesor(result.getString("codigoProfesor"))
                                .prioridad(result.getString("prioridad"))
                                .build()
                );
            }
            return retorno;
        }catch (SQLException e){
            System.out.println("CDB:Calificacion"+e);
        }
        return null;
    }

    private List<MapaHorario> obtenerHorario(){
        var retorno = new ArrayList<MapaHorario>();
        try (var result = this.conexion.createStatement().executeQuery("SELECT * FROM horario")){
            while (result.next()){
                retorno.add(
                    MapaHorario.builder().
                        codigo(result.getString("codigo")).
                        horaInicio(result.getTime("horaInicio").toLocalTime()).
                        horaFin(result.getTime("horaFin").toLocalTime()).
                    build()
                );
                ;
            }
            return retorno;
        }catch (SQLException e){
            System.out.println("Error en dao horario\n"+e);
        }
        return null;
    }

    private List<Parametro> obtenerParametros(){
        List<Parametro> retorno = new ArrayList<>();
        try (var result = this.conexion.createStatement().executeQuery("SELECT * FROM parametro")) {
            while (result.next()) {
                retorno.add(
                  Parametro.builder().
                      codigo(result.getString("codigo")).
                      desripcion(result.getString("descripcion")).
                      valor(result.getInt("valor")).
                  build()
                );
            }
        }catch (SQLException e){
            System.out.println("Error dao parametros");
        }
        return retorno;
    }

    public Transporte transporte(){
        return Transporte.builder()
                .carrera(obtenerCarrera())
                .profesor(obtenerProfesor())
                .materia(obtenerMateria())
                .salon(obtenerSalon())
                .contrato(obtenerContrato())
                .preasignacion(obtenerPreAsignacion())
                .asignacionHorario(obtenerAsignacionHorario())
                .calificacionProfesor(obtenerCalificacionProfesor())
                .horario(obtenerHorario())
                .parametros(obtenerParametros())
                .build();
    }
}
