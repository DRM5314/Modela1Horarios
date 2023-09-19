package org.example.export;


import org.example.model.AsignacionHorario;
import org.example.model.Transporte;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExportHtml {
    private List<AsignacionHorario> asignaciones;
    private Transporte trasnporte;
    Map<String,String> reporteUso;
    Map<String,String> cursosNoAbiertos;
    public ExportHtml(List<AsignacionHorario> asignacionesEntrada,Transporte transporteEntrada,Map<String,String> reporteUso,Map<String,String> cursosNoAbiertosEntrada){
        this.asignaciones = asignacionesEntrada;
        this.trasnporte = transporteEntrada;
        GeneradorColores.generarColores(transporteEntrada);
        this.reporteUso = reporteUso;
        this.cursosNoAbiertos = cursosNoAbiertosEntrada;
        //System.out.println("\n\n"+tablaReporte());
    }

    public void send(){
        List<String> salida = new ArrayList<>();
        SalidaArchivo.guardar("parametros.html",tablaParametros());
        SalidaArchivo.guardar("horario.html",tablaHorario());
    }

    public List<String> tablaHorario(){
        List<String> retorno = new ArrayList<>();
        retorno.add("<!DOCTYPE html>\n" +
                "<html lang=\"es\">\n" +
                        "<link rel=\"stylesheet\" type=\"text/css\" href=\"estilos.css\">\n" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"coloresAsignaciones.css\">"+
                "<body>\n" +
                "<h2>Horario:</h2>\n" +
                "\n" +
                "\n" +
                "<div class=\"divReporte\">\n");
        retorno.add("<table class=\"tabla-Asignacion\">\n");
        var asigOrdenHora = this.asignaciones.stream().collect(Collectors.groupingBy(AsignacionHorario::getCodigoMapaHorario,Collectors.groupingBy(AsignacionHorario::getCodigoSalon)));
        retorno.add(cabezaTablaHorario());
        asigOrdenHora.forEach((codigoHora,salones)->{
            String horIni = this.trasnporte.getHorario().stream().filter(ho->ho.getCodigo().equals(codigoHora)).findFirst().get().getHoraInicio().toString();
            horIni +="\n-\n"+ this.trasnporte.getHorario().stream().filter(ho->ho.getCodigo().equals(codigoHora)).findFirst().get().getHoraFin().toString();
            retorno.add("<tr>\n<td class=\"fila-Hora\">"+horIni+"</td>\n");
            this.trasnporte.getSalon().forEach(sal->{
                var asig = this.asignaciones.stream().filter(asig1->(asig1.getCodigoSalon().equals(sal.getCodigo())&&asig1.getCodigoMapaHorario().equals(codigoHora))).findFirst();
                String asigAuxText = "";
                if(asig.isPresent()){
                    asigAuxText +="<td class=\""+asig.get().getCodigoCarrera()+asig.get().getSemestre()+"\">\n" +
                            "            <ul class=\"descripcionAsignacion\">\n";
                    asigAuxText += "<li>"+asig.get().getCodigoMateria()+"</li>\n";
                    asigAuxText += "<li>"+asig.get().getCodigoCarrera()+"</li>\n";
                    asigAuxText += "<li>"+asig.get().getSemestre()+"</li>\n";
                    asigAuxText += "<li>"+asig.get().getCodigoProfesor()+"</li>\n";
                    asigAuxText += "<li>Seccion:"+ asig.get().getEsSeccion()+"</li>\n";
                }else{
                    asigAuxText += "<td class=\"fila-Asignacion\">\n" +
                            "            <ul class=\"descripcionAsignacion\">\n";
                }
                asigAuxText+= "</ul>\n</td>\n";
                retorno.add(asigAuxText);
            });
            retorno.add("</tr>\n");
        });
        retorno.add("</table>\n" +"</div>\n");
        retorno.add(tablaReporte());
        retorno.add(tablaCursosNoAbiertos());
        retorno.add("</body>\n" +
                "</html>");
        return retorno;
    }

    private String cabezaTablaHorario(){
        String retorno = "<tr>\n" +
                "            <th class=\"fila-Salon\">Salon/Hora</th>";
        List<String> as = new ArrayList<>();
        this.trasnporte.getSalon().forEach(sal->{
            as.add("<th class=\"fila-Salon\">"+sal.getNombre()+"</th>\n");
        });
        retorno += String.join(" ",as);
        retorno += "</tr>";
        return retorno;
    }

    private String tablaReporte(){
        String retorno = "<h3>Reporte: </h3>\n" +
                " <table class=\"tabla-reporteAsignacion\">\n" +
                "    <tr>\n" +
                "        <th>Descripcion</th>\n" +
                "        <th>Valor</th>\n" +
                "    </tr>\n";
        List<String> aux = new ArrayList<>();
        this.reporteUso.forEach((des,va)->{
            aux.add("<tr>\n<td>"+des+"</td>\n<td>"+va+"</td>\n</tr>\n");
        });
        retorno += String.join(" ",aux);
        retorno += "\n</table>\n";
        return  retorno;
    }

    public List<String> tablaParametros(){
        List <String> retorno = new ArrayList<>();
        retorno.add("<!DOCTYPE html>\n" +
                "<html lang=\"es\">\n" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"estilos.css\">\n" +
                "<body>\n" +
                "<h2>Parametros:</h2>\n" +
                "<table class=\"mi-tabla\">\n" +
                "    <tr>\n" +
                "        <th>Codigo</th>\n" +
                "        <th>Descripcion</th>\n" +
                "        <th>Valor</th>\n" +
                "    </tr>");
        this.trasnporte.getParametros().forEach(par->{
            retorno.add("<tr>\n");
            retorno.add("<td class=\"fila-Codigo\">"+par.getCodigo()+"</td>\n");

            retorno.add("<td>\n" +
                    "            <p class=\"columnaDescripcion\">"+par.getDesripcion());
            retorno.add("    </p>\n" +
                    "        </td>\n");

            retorno.add("<td class=\"fila-Valor\">"+par.getValor()+"</td>");

            retorno.add("</tr>\n");
        });
        retorno.add("</table>\n");
        retorno.add("</body>\n" +
                "</html>\n");

        return retorno;
    }

    private String tablaCursosNoAbiertos(){
        List<String> retorno = new ArrayList<>();
        retorno.add("\n");
        if(!this.cursosNoAbiertos.isEmpty()){
            retorno.add("\n\n<h3>Cursos no abiertos: </h3>\n" +
                "<table class=\"tabla-reporteNoabierto\">\n" +
                "    <tr>\n" +
                "        <th>Codigo Curso</th>\n" +
                "        <th>Motivo</th>\n" +
                "    </tr>\n");
            this.cursosNoAbiertos.forEach((curso,motivo)->{
                retorno.add("<tr>\n");
                retorno.add("<td>"+curso+"</td>\n");
                retorno.add("<td>"+motivo+"</td>\n");
                retorno.add("</tr>\n");
            });
            retorno.add("</table>\n");

        }
        return  String.join(" ",retorno);
    }

    private String tablaSalon(){
        List<String> retorno = new ArrayList<>();
        retorno.add("<h2>Salones: </h2>\n" +
                "<table class=\"tabla-Entradas\">\n" +
                "    <tr>\n" +
                "        <th>Codigo</th>\n" +
                "        <th>Nombre</th>\n" +
                "        <th>Capacidad</th>\n" +
                "    </tr>\n");
        this.trasnporte.getSalon().forEach(sal->{
            retorno.add("<tr>\n");
            retorno.add("<td>"+sal.getCodigo()+"</td>");
            retorno.add("<td>"+sal.getNombre()+"</td>");
            retorno.add("<td>"+sal.getCapacidad()+"</td>");
            retorno.add("</tr>\n");
        });
        retorno.add("\n</table>\n\n\n");
        return String.join(" ",retorno);
    }

    private String tablaProfesores(){
        List<String> retorno = new ArrayList<>();
        retorno.add("<h2>Profesores: </h2>\n" +
                "<table class=\"tabla-Entradas\">\n" +
                "    <tr>\n" +
                "        <th>Codigo</th>\n" +
                "        <th>Nombre</th>\n" +
                "    </tr>\n");
        this.trasnporte.getProfesor().forEach(prof->{
            retorno.add("<tr>\n");
            retorno.add("<td>"+prof.getCodigo()+"</td>");
            retorno.add("<td>"+prof.getNombre()+"</td>");
            retorno.add("</tr>\n");
        });
        retorno.add("\n</table>\n\n\n");
        return String.join(" ",retorno);
    }



}
