package org.example.controller;

import org.example.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class SeccionesControler {

    public static List<SeccionesSalon> salonesSeccion(Transporte transporteEntrada){
        List<SeccionesSalon> retorno = new ArrayList<>();
        var seccionesMaximas = transporteEntrada.getParametros().stream().filter(res->res.getCodigo().equals(ParametroLogic.maxSecciones)).findFirst().get().getValor();
        var salonesOrden = transporteEntrada.getSalon().stream().sorted(Collections.reverseOrder(Comparator.comparingInt(com-> com.getCapacidad()))).collect(Collectors.toList());
        seccionesMaximas = (seccionesMaximas<=salonesOrden.size())?seccionesMaximas:salonesOrden.size()-1;
        for (int i = 0; i < salonesOrden.size(); i++) {
            if(i+seccionesMaximas<=salonesOrden.size()){
                List<Salon> auxSalon = new ArrayList<>();
                int capacidad = 0;
                String codigo = "";
                for (int j = 0; j < seccionesMaximas; j++) {
                    auxSalon.add(salonesOrden.get(i+j));
                    capacidad += salonesOrden.get(i+j).getCapacidad();
                    codigo +=salonesOrden.get(i+j).getCodigo();
                }
                i = i + (seccionesMaximas-2);
                retorno.add(SeccionesSalon.builder().codigo(codigo).salones(auxSalon).horario(transporteEntrada.getHorario()).capacidad(capacidad).build());
            }
        }
        return retorno;
    }

    public static List<SeccionesSalon> validacionSalon(List<SeccionesSalon> salonEntrada,List<MapaHorario> materiaHorario,int numeroAsignaciones,List<AsignacionHorario> asignacionesEntrada,List<MapaHorario> horario,int porctMax){
        List<SeccionesSalon> retorno = new ArrayList<>();
        salonEntrada.forEach(sal->{
            sal.getSalones().forEach(salC->{
                List<MapaHorario> disponible = new ArrayList<>(horario);
                var sss = asignacionesEntrada.stream().anyMatch(asig->asig.getCodigoSalon().equals(salC.getCodigo()));
                if(!asignacionesEntrada.isEmpty() && sss){
                    asignacionesEntrada.stream().filter(asig->asig.getCodigoSalon().equals(salC.getCodigo())).forEach(el->{
                        if(el.getCodigoSalon().equals(salC.getCodigo())){
                            disponible.removeIf(dis->dis.getCodigo().equals(el.getCodigoMapaHorario()));
                        }
                    });
                }
                disponible.retainAll(materiaHorario);
                sal.setHorario(disponible);
            });
        });

        SeccionesSalon emergencia = salonEntrada.stream().findFirst().get();
        if(salonEntrada.isEmpty())return null;else{
            salonEntrada.forEach(sal->{
                var diff = sal.getCapacidad() - numeroAsignaciones;
                var porcentaje = (double) Math.abs(diff) / sal.getCapacidad();
                var maxOcupacion = (double) porctMax / 100;
                if(porcentaje<=maxOcupacion){
                    retorno.add(sal);
                }
            });
        }
        //if(retorno.isEmpty())retorno.add(emergencia);
        return retorno;
    }
    public static List<SeccionesSalon> salonesDisponibles(List<AsignacionSeccionHorario> seccionDisponible,List<MapaHorario> cursoHorarioDisp,int preAsignados,Transporte transEntrada,List<MapaHorario> horarioVirgen){
        var max = transEntrada.getParametros().stream().filter(par->par.getCodigo().equals(ParametroLogic.maxSalon)).findFirst().get().getValor();
        var maxSal = 1 + (double) max/100;
        var maxSecc = transEntrada.getParametros().stream().filter(par->par.getCodigo().equals(ParametroLogic.maxSecciones)).findFirst().get().getValor();
        List<SeccionesSalon> retorno = new ArrayList<>();
        if(!seccionDisponible.isEmpty()){
            List<Salon> salonDispEntrada = new ArrayList<>();
            seccionDisponible.forEach(seccDisp->{
                int cant = seccDisp.getSalonesDisponibles().stream().mapToInt(Salon::getCapacidad).sum();
                if(cant>=preAsignados){
                    var comp = cursoHorarioDisp.stream().anyMatch(hor->hor.getCodigo().equals(seccDisp.getCodigoMapaHorario()));
                    if(comp && preAsignados<=(cant)*maxSal){
                        salonDispEntrada.addAll(seccDisp.getSalonesDisponibles());
                        var salones= salonMatch(salonDispEntrada,preAsignados,maxSal,maxSecc);
                        List<MapaHorario> horario = horarioVirgen.stream().filter(hor->hor.getCodigo().equals(seccDisp.getCodigoMapaHorario())).collect(Collectors.toList());
                        retorno.add(SeccionesSalon.builder().salones(salones).horario(horario).build());
                    }
                }else{
                    var salones = salonMatch(transEntrada.getSalon(),preAsignados,maxSal,maxSecc);
                    List<MapaHorario> aux = new ArrayList<>(horarioVirgen);
                    for (int i = 0; i < seccionDisponible.size(); i++) {
                        aux.remove(0);
                    }
                    retorno.add(SeccionesSalon.builder().salones(salones).horario(aux).build());
                }
            });
        }else{
            var salones = salonMatch(transEntrada.getSalon(),preAsignados,maxSal,maxSecc);
            retorno.add(SeccionesSalon.builder().salones(salones).horario(horarioVirgen).build());
        }
        return retorno;
    }

    private static List<Salon> salonMatch(List<Salon> salEntrada,int preAsignados,double maxSal,int maxSecc){
        var seccDisp =  new ArrayList<>(salEntrada);
        int preAsignadosAux = preAsignados;
        List<Salon> salonUsado = new ArrayList<>();
        Map <Salon,Integer> salonesFil = new HashMap<>();
        while (preAsignadosAux>0 && salonUsado.size()<maxSecc){
            var salonGrande = seccDisp.stream().sorted(Collections.reverseOrder(Comparator.comparingInt(Salon::getCapacidad))).findFirst();
            if(salonGrande.isPresent()){
                if(salonGrande.get().getCapacidad()<preAsignadosAux){
                    salonUsado.add(salonGrande.get());
                    var resta = (preAsignadosAux - ((maxSal)*salonGrande.get().getCapacidad()));
                    preAsignadosAux = (int) resta;
                    seccDisp.removeIf(sal->salonGrande.get().getCodigo().equals(sal.getCodigo()));
                }else{
                    int finalPreAsignadosAux = preAsignadosAux;
                    seccDisp.forEach(sal->{
                        int rest =sal.getCapacidad()-finalPreAsignadosAux;
                        if(rest>=0)salonesFil.put(sal,rest);
                    });
                    System.out.println();
                    var salAsi = salonesFil.entrySet().stream().sorted(
                            Map.Entry.comparingByValue()
                    ).collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue,(e1,e2)->e1,LinkedHashMap::new));
                    Salon usado = salAsi.entrySet().stream().findFirst().get().getKey();
                    salonUsado.add(usado);
                    preAsignadosAux = preAsignadosAux - (int)(usado.getCapacidad()*maxSal);
                    seccDisp.removeIf(sal->usado.getCodigo().equals(sal.getCodigo()));
                }
            }
        }
        return salonUsado;
    }
}

/**
 var salonGrande = seccDisp.getSalonesDisponibles().stream().sorted(Collections.reverseOrder(Comparator.comparingInt(Salon::getCapacidad))).findFirst();
 List<Salon> salonesAsig = new ArrayList<>();
 seccDisp.getSalonesDisponibles().forEach(salonDisp->{
 if(salonesAsig.size()<=4){
 if(salonDisp.getCapacidad()*1.2>=preAsignados){
 //buscarAdaptado
 salonesAsig.add(salonDisp);
 }
 }
 });

 */
