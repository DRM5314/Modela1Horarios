package org.example.controller;
import lombok.Getter;
import org.example.data.ConsultaDb;
import org.example.data.PrioridadSemestreDAO;
import org.example.export.ExportHtml;
import org.example.model.*;

import java.sql.Connection;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class ServicioHorario {
    private Transporte listado;
    private Connection conexion;
    private List<MapaHorario> horario;
    private List<AsignacionHorario> asignacionHorario;
    private List <PrioridadSemestre> preAsignaciones;
    private List<AsignacionSeccionHorario> asigSecc = new ArrayList<>();
    private Map<String,String> reporteUso = new HashMap<>();
    private Map<String,String> cursosNoAbiertos = new HashMap<>();
    private int seccionesSize = 0;
    public ServicioHorario(java.sql.Connection conexion){
        this.conexion = conexion;
        this.listado = new ConsultaDb(this.conexion).transporte();
        this.horario = MapaHorario();
        this.asignacionHorario = new ArrayList<>();
        this.preAsignaciones =  new PrioridadSemestreDAO(this.conexion).prioridadSemestre();
        this.listado.setHorario(this.horario);
    }
    private List<MapaHorario> MapaHorario(){
        var retorno = new ArrayList<MapaHorario>();
        var horario = this.listado.getHorario();
        var horaEntrada = horario.get(0).getHoraInicio();
        var horaSalida = horario.get(0).getHoraFin();
        var tiempoPeriodo = this.listado.getParametros().stream().filter(para->para.getCodigo().equals(ParametroLogic.periodo)).findFirst().get().getValor();
        int horasDiponibles = (int) horaEntrada.until(horaSalida, ChronoUnit.HOURS);
        for (int i = 0; i < horasDiponibles; i++) {
            retorno.add(
            MapaHorario.builder().codigo("H"+i).
                horaInicio(horaEntrada).
                horaFin(horaEntrada.plusMinutes(tiempoPeriodo))
            .build()
            );
            horaEntrada = horaEntrada.plusMinutes(tiempoPeriodo);
        }
        return retorno;
    }
    public void prioridadSemestre(){
        if(this.listado.getParametros().stream().filter(p->p.getCodigo().equals(ParametroLogic.boolSecciones)).findFirst().get().getValor()==1){
            secciones();
        }
        var carreras = this.preAsignaciones.stream().collect(
        Collectors.groupingBy(
            PrioridadSemestre::getCodigoCarrera,
            Collectors.groupingBy(PrioridadSemestre::getSemestre,
            Collectors.groupingBy(PrioridadSemestre::getCodigoMateria))
        )
        );
        carreras.forEach((carrera,semestres)->{
            semestres.forEach((semestre,materias)->{
                materias.forEach((codigo,materia)->{
                    var moti = 0;
                    var asigAux = this.listado.getPreasignacion();
                    var restul = asigAux.stream().filter(asigAux1->asigAux1.getCodigoMateria().equals(codigo)).findFirst().get();
                    var salones = validacionSalonOcupacion(restul);
                    if(salones.isEmpty())cursosNoAbiertos.put(codigo+" M"+moti++,"Salon sin capacidad");
                    /*las validaciones en horario hace un filtro para saber en que horarios estan disponibles tanto salones como profesores
                    luego en la validacion de cirterio asignacion se toman otras consideracioes para ver que profesor y que salon son aptops para la aisgnacion
                     */
                    var listadoHorarioMateriaDisponibilidad = validacionTraslapeSemestres(carrera,semestre);
                    if(listadoHorarioMateriaDisponibilidad.isEmpty())cursosNoAbiertos.put(codigo+" M"+moti++,"No encaja en horario por traslape");
                    var salonesDiponiblesEnHorario = validacionSalonHorario(salones,listadoHorarioMateriaDisponibilidad);
                    if(salonesDiponiblesEnHorario.isEmpty() && !salones.isEmpty())cursosNoAbiertos.put(codigo+" M"+moti++,"Salon sin espacio");
                    var profesoresCapacidad = validacionProfesorCapacidad(codigo);
                    if(profesoresCapacidad.isEmpty())cursosNoAbiertos.put(codigo+" M"+moti++,"Sin maestros");
                    var profesoresDisponibilidad = validacionProfesorHorario(profesoresCapacidad,listadoHorarioMateriaDisponibilidad);
                    if(profesoresDisponibilidad.isEmpty() && !profesoresCapacidad.isEmpty())cursosNoAbiertos.put(codigo+" M"+moti++,"Sin maestros en horario");
                    if(!salonesDiponiblesEnHorario.isEmpty() && !profesoresDisponibilidad.isEmpty()) {
                        AsignacionHorario aux = validacionAsginacion(salonesDiponiblesEnHorario, profesoresDisponibilidad, listadoHorarioMateriaDisponibilidad, restul);
                        if(aux!=null){
                            this.asignacionHorario.add(aux);
                        }else{
                            this.cursosNoAbiertos.put(codigo+" M"+moti++,"Falta en horario");
                        }
                    }else {
                        //if(salonesDiponiblesEnHorario.isEmpty())this.cursosNoAbiertos.put(codigo,"Por falta de salon (Traslape)");
                        //if(profesoresDisponibilidad.isEmpty())this.cursosNoAbiertos.put(codigo,"Por falta de profesor");
                        //System.out.println("No asignado: "+codigo);
                    }
                    //System.out.println("Listo: "+codigo);
                });
            });
        });
        //System.out.println("Fin Asignados: "+this.asignacionHorario.size());
        //System.out.println("Sin seccion");
        //var resultado = this.asignacionHorario.stream().collect(
                //Collectors.groupingBy(AsignacionHorario::getCodigoSalon,Collectors.groupingBy(AsignacionHorario::getCodigoMapaHorario)));
        //var noseeeee = this.asignacionHorario.stream().collect(Collectors.groupingBy(AsignacionHorario::getCodigoSalon,Collectors.groupingBy(AsignacionHorario::getCodigoMapaHorario)));
        //var sdfs  = this.asignacionHorario.stream().collect(Collectors.groupingBy(AsignacionHorario::getCodigoMateria));
        //sdfs.forEach((no,si)-> System.out.println(no));
        this.reporteUso.put("Asignaciones Total: ",""+this.asignacionHorario.size());
        this.reporteUso.put("Asignaciones seccion:",this.seccionesSize+"");
        int cursosAsignados = this.asignacionHorario.size() - this.seccionesSize;
        this.reporteUso.put("Asignaciones Normal: ",""+cursosAsignados);
        this.reporteUso.put("Cursos no abiertos: ",""+this.cursosNoAbiertos.size());
        this.reporteUso.put("Preasignaciones",this.listado.getPreasignacion().size()+"");
        //System.out.println("Fin Asignados: "+this.asignacionHorario.size());
        ExportHtml export = new ExportHtml(this.asignacionHorario,this.listado,this.reporteUso,this.cursosNoAbiertos);
        export.send();
        //System.out.println(export.tablaHorario());
    }

    public HashMap<Salon,String> validacionSalonOcupacion(PreAsignacion preAsignacion){
        var retorno = new HashMap();
        this.listado.getSalon().forEach(salon ->{
            var diff = salon.getCapacidad() - preAsignacion.getNumeroAsignaciones();
            var porcentaje = (double) Math.abs(diff) / salon.getCapacidad();
            var maximoPorcentaje = this.listado.getParametros().stream().filter(para->para.getCodigo().equals(ParametroLogic.maxSalon)).findFirst().get().getValor();
            var resultado = (diff <= 0 )?
                    (porcentaje <= (double) maximoPorcentaje/100) ?"sobrePoblado" :"noOptimo" : "optimo";
            switch (resultado){
                case "sobrePoblado","optimo"-> retorno.put(salon,resultado);
            }
        });
        return retorno;
    }

    private List<SalonFiltrado> validacionSalonHorario(Map<Salon,String> salonEntrada1,List<MapaHorario> filtradoCurso) {
        List<SalonFiltrado> salonesLibres = new ArrayList<>();
        salonEntrada1.forEach((salonEntrada,valor) ->{
            if(!this.asignacionHorario.isEmpty()){
                List<MapaHorario> aux = new ArrayList<>(this.horario);
                var nose = this.asignacionHorario.stream().filter(
                        asignacionHorario1 -> asignacionHorario1.getCodigoSalon().equals(salonEntrada.getCodigo())
                ).collect(Collectors.toList());
                nose.forEach(
                        asignacion->{
                            if(asignacion.getCodigoSalon().equals(salonEntrada.getCodigo())) {
                                aux.removeIf(horarioAux->horarioAux.getCodigo().equals(asignacion.getCodigoMapaHorario()));
                            }
                        }
                );
                aux.retainAll(filtradoCurso);
                if(!aux.isEmpty()){
                    salonesLibres.add(SalonFiltrado.builder().salon(salonEntrada).mapaHorarioList(aux).tipo(valor).build());
                }
            }else {
                salonesLibres.add(SalonFiltrado.builder().salon(salonEntrada).mapaHorarioList(filtradoCurso).tipo(valor).build());
            }
        });
        return salonesLibres;
    }

    private List<CalificacionProfesor> validacionProfesorCapacidad(String materiaEntrada){
        return this.listado.getCalificacionProfesor().stream().filter(profesor->
            profesor.getCodigoMateria().equals(materiaEntrada)
        ).collect(Collectors.toList());
    }

    private List<ProfesorFiltrado> validacionProfesorHorario(List<CalificacionProfesor> caliProfEnt,List<MapaHorario> filtradoCurso){
        List<ProfesorFiltrado> retorno = new ArrayList<>();
        caliProfEnt.forEach(prof->{
            Profesor profAux = this.listado.getProfesor().stream().filter(profesor->profesor.getCodigo().equals(prof.getCodigoProfesor())).findFirst().get();
            ProfesorFiltrado profAuxFiltrado;
            if(!this.asignacionHorario.isEmpty()){
                    List<MapaHorario> aux = new ArrayList<>(this.horario);
                    var filtraciones = this.asignacionHorario.stream().filter(asignacionHorario1 -> asignacionHorario1.getCodigoProfesor().equals(prof.getCodigoProfesor()));
                    filtraciones.forEach(filtradoProfHorario->{
                        if(filtradoProfHorario.getCodigoProfesor().equals(prof.getCodigoProfesor())){
                            aux.removeIf(aux1->aux1.getCodigo().equals(filtradoProfHorario.getCodigoMapaHorario()));
                        }
                    });
                    aux.retainAll(filtradoCurso);
                    if(!aux.isEmpty()){
                        profAuxFiltrado = ProfesorFiltrado.builder().codigoProfesor(profAux.getCodigo()).horarioDisponible(aux).califacion(prof).build();
                        retorno.add(profAuxFiltrado);
                    }
            }else {
                profAuxFiltrado = ProfesorFiltrado.builder().codigoProfesor(profAux.getCodigo()).horarioDisponible(this.horario).califacion(prof).build();
                retorno.add(profAuxFiltrado);
            }
        });
        return retorno;
    }

    private List<MapaHorario> validacionTraslapeSemestres(String carrera, int semeste){
        List<MapaHorario> retorno;
        if(!asignacionHorario.isEmpty()){
            var resultado = this.asignacionHorario.stream().filter
                (asignacion->asignacion.getSemestre()==semeste && asignacion.getCodigoCarrera().equals(carrera)).
                map(asignacionHorario1 ->asignacionHorario1.getCodigoMapaHorario()).collect(Collectors.toList());
            retorno = this.horario.stream().filter(horario->!resultado.contains(horario.getCodigo())).collect(Collectors.toList());
            return  retorno;
        }else{
            return this.horario;
        }
    }

    private AsignacionHorario validacionAsginacion(List<SalonFiltrado> salonEntrada, List<ProfesorFiltrado> profesorEntrada, List<MapaHorario>materiaHorario, PreAsignacion preAsignacionEntrada){
        salonEntrada.forEach(entry->{
            entry.getMapaHorarioList().retainAll(materiaHorario);
        });
        profesorEntrada.forEach(prof->{
            prof.getHorarioDisponible().retainAll(materiaHorario);
        });
        Map<SalonFiltrado,Double> auxSalon = new HashMap<>();
        salonEntrada.forEach(salon->{
            double diff = (double)preAsignacionEntrada.getNumeroAsignaciones()/salon.getSalon().getCapacidad();
            auxSalon.put(salon,diff);
        });
        var salonMatch1 = auxSalon.entrySet().stream().sorted(
                Collections.reverseOrder(Map.Entry.comparingByValue())
        ).collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue,(e1,e2)->e1,LinkedHashMap::new));
        var salonMatch = salonMatch1.entrySet().stream().findFirst().get();
        MapaHorario horarioSeleccionado = salonMatch.getKey().getMapaHorarioList().stream().findFirst().get();
        Materia materiaa = this.listado.getMateria().stream().filter(materia -> materia.getCodigo().equals(preAsignacionEntrada.getCodigoMateria())).findFirst().get();
        String profSalida = null;
        for (ProfesorFiltrado profesorFiltrado : profesorEntrada) {
            if(profesorFiltrado.getHorarioDisponible().contains(horarioSeleccionado)){
                profSalida = profesorFiltrado.getCodigoProfesor();
                break;
            }
        }
        if(profSalida==null)return null;
        return AsignacionHorario.builder().
                codigoMateria(preAsignacionEntrada.getCodigoMateria()).
                codigoSalon(salonMatch.getKey().getSalon().getCodigo()).
                codigoProfesor(profSalida).
                codigoMapaHorario(horarioSeleccionado.getCodigo()).
                codigoCarrera(materiaa.getCodigoCarrera()).
                semestre(materiaa.getSemestre()).
                esSeccion("NO").
                build()
                ;
    }

    private void secciones(){
        var salonMasGrande = this.listado.getSalon().stream().sorted(Collections.reverseOrder(Comparator.comparingInt(Salon::getCapacidad))).collect(Collectors.toList()).stream().findFirst().get();
        var listadoOrden = this.listado.getPreasignacion().stream().sorted(Collections.reverseOrder(Comparator.comparingInt(PreAsignacion::getNumeroAsignaciones))).collect(Collectors.toList());
        List<String> vaASeccion = new ArrayList<>();
        List<PrioridadSemestre> secciones = new ArrayList<>();
        listadoOrden.forEach(asig->{
            var diff = salonMasGrande.getCapacidad() - asig.getNumeroAsignaciones();
            var porcentaje = (double) Math.abs(diff) / salonMasGrande.getCapacidad();
            var maximoPorcentaje = this.listado.getParametros().stream().filter(para->para.getCodigo().equals(ParametroLogic.maxSalon)).findFirst().get().getValor();
            if((diff <0 && porcentaje > (double) maximoPorcentaje/100)){
                vaASeccion.add(asig.getCodigoMateria());
            }
        });
        reporteUso.put("Cursos necesitan Seccion",vaASeccion.size()+"");
        vaASeccion.forEach(vaSeccion->{
            secciones.add(
                    preAsignaciones.stream().filter(pre->pre.getCodigoMateria().equals(vaSeccion)).findFirst().get()
            );
            this.preAsignaciones.removeIf(pre->pre.getCodigoMateria().equals(vaSeccion));
        });
        //remueve las preasignaciones restantes que no necesitan de secciones
        if(!vaASeccion.isEmpty())for (int i = listadoOrden.size()-1; i >=vaASeccion.size(); i--)listadoOrden.remove(i);

        secciones.stream().collect(Collectors.groupingBy(PrioridadSemestre::getCodigoCarrera,Collectors.groupingBy(PrioridadSemestre::getSemestre))).forEach(
                (carrera,semestres)->{
                    semestres.forEach((semestre,cursos)->{
                        cursos.forEach(curso->{
                            var horarioMateria = validacionTraslapeSemestres(carrera,semestre);
                            var prof = validacionProfesorCapacidad(curso.getCodigoMateria());
                            var profHor = validacionProfesorHorario(prof,horarioMateria);
                            var preAs = this.listado.getPreasignacion().stream().filter(pre->pre.getCodigoMateria().equals(curso.getCodigoMateria())).findFirst().get();
                            var salon = SeccionesControler.salonesDisponibles(asigSecc,horarioMateria,preAs.getNumeroAsignaciones(),this.listado,this.horario);
                            //var salon = SeccionesControler.validacionSalon(salones,horarioMateria,preAs.getNumeroAsignaciones(),asignacionHorario,horario,20);
                            if(!profHor.isEmpty() && !salon.isEmpty()){
                                asignarSecciones(horarioMateria,salon,profHor,curso.getCodigoMateria(),carrera,semestre);
                            }else{
                                if(profHor.isEmpty())cursosNoAbiertos.put(curso.getCodigoMateria()," Prof sin horario");
                                if(salon.isEmpty())cursosNoAbiertos.put(curso.getCodigoMateria()," Salon sin espacio");
                            }
                        });
                    });
                }
        );
        if(!this.asigSecc.isEmpty())this.seccionesSize = this.asignacionHorario.size();
        //System.out.println("Total asignados seccion "+this.asignacionHorario.size());
    }
    private void asignarSecciones(List<MapaHorario> materiaHorario,List<SeccionesSalon>salones,List<ProfesorFiltrado>prof,String materia,String codigCarrera,int semestre){
        if(salones.isEmpty())cursosNoAbiertos.put(codigCarrera," Salones sin espacio");
        if(prof.isEmpty())cursosNoAbiertos.put(codigCarrera,"Sin prof");
        for (MapaHorario mat : materiaHorario) {
            var filSal = salones.stream().filter(sal->sal.getHorario().stream().anyMatch(hor->hor.getCodigo().equals(mat.getCodigo()))).findFirst();
            var filProf = prof.stream().filter(pro->pro.getHorarioDisponible().stream().anyMatch(hor->hor.getCodigo().equals(mat.getCodigo()))).findAny();
            if(filSal.isPresent() && filProf.isPresent()){
                List<Salon> salonesDisponibles = new ArrayList<>(listado.getSalon());
                for (Salon sal : filSal.get().getSalones()) {
                    this.asignacionHorario.add(AsignacionHorario.builder().codigoMateria(materia).codigoSalon(sal.getCodigo()).codigoProfesor(filProf.get().getCodigoProfesor()).codigoMapaHorario(mat.getCodigo()).codigoCarrera(codigCarrera).semestre(semestre).esSeccion("SI").build());
                    salonesDisponibles.removeIf(salR->salR.getNombre().equals(sal.getCodigo()));
                }
                if(!this.asigSecc.stream().anyMatch(asi->asi.getCodigoMapaHorario().equals(mat.getCodigo()))){
                    this.asigSecc.add(AsignacionSeccionHorario.builder().salonesDisponibles(salonesDisponibles).codigoMapaHorario(mat.getCodigo()).build());
                }else{
                    this.asigSecc.stream().filter(as->as.getCodigoMapaHorario().equals(mat.getCodigo())).findFirst().get().setSalonesDisponibles(salonesDisponibles);
                }
                this.asignacionHorario.forEach(asiH->{
                    this.asigSecc.stream().filter(asiC->asiC.getCodigoMapaHorario().equals(asiH.getCodigoMapaHorario())).findFirst().get().getSalonesDisponibles().removeIf(sal->sal.getCodigo().equals(asiH.getCodigoSalon()));
                });
                break;
            }
        }
        //System.out.println();
    }
}
