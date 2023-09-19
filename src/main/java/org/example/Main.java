package org.example;

import com.sun.jna.IntegerType;
import org.example.controller.ServicioHorario;
import org.example.data.Conexion;
import org.example.model.Parametro;
import org.example.model.Transporte;
import org.mariadb.jdbc.Connection;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        char entrada = 'a';
        var conexion = new Conexion();
        Scanner scanner = new Scanner(System.in);
        var servicio = new ServicioHorario(conexion.conectar());
        while(entrada!='q'){
            System.out.println("\nSeleccione: \n\t1)Nuevo Analisis 2)Ajustar variables  3)Ver analisis \n'q' para salir");
            entrada = scanner.next().charAt(0);
            switch (entrada){
                case '1':
                    servicio.prioridadSemestre();
                    servicio = new ServicioHorario(conexion.conectar());
                    System.out.println("---Analisis completado!");
                    break;
                case '2':
                    parametros(servicio.getListado());
                    break;
                case '3':
                    String ruta = Paths.get(System.getProperty("user.dir")).toString()+"/src/main/java/org/example/export/pruebas/boceto.html";
                    try {
                        File archivo = new File(ruta);
                        if(archivo.exists()) Desktop.getDesktop().browse(archivo.toURI());
                        else System.out.println("No existe archivo hable con tecnico para recuperar");
                    }catch (IOException e){
                        System.out.println("Error al abrir resultado");
                    }
                    break;
                case 'q':
                    System.out.println("Hasta pronto");
                    break;
            }
        }

    }
    public static void parametros(Transporte trasnEntrada){
        System.out.println("\n");
        Map<Integer,String> map = new HashMap<>();
        Scanner sacaner = new Scanner(System.in);
        char opcion = 'a';
        while (opcion!='q'){
            int contador = 0;
            System.out.println("Nombre\tValor");
            for (Parametro par : trasnEntrada.getParametros()) {
                map.put(contador,par.getCodigo());
                System.out.println(contador+". "+par.getDesripcion()+"\t "+par.getValor());
                contador++;
            }
            System.out.println("\n Desea editar? 1:Si  2:No   'q' salir");
            opcion = sacaner.next().charAt(0);
            switch (opcion){
                case '1':
                    String nuevoValor = "";
                    System.out.println("Seleccione opcion a editar");
                    opcion = sacaner.next().charAt(0);
                    System.out.println("Ingrese nuevo valor");
                    nuevoValor = sacaner.next();
                    String clave = ""+opcion;
                    String editarBuscar ="";
                    try {
                         editarBuscar = map.get(Integer.parseInt(clave));
                         Integer.parseInt(nuevoValor);
                    }catch (NumberFormatException e){
                        System.out.println("No soportado");
                        return;
                    }
                    System.out.println("Buscando " + editarBuscar);
                    for (Parametro parametro : trasnEntrada.getParametros()) {
                        if(parametro.getCodigo().equals(editarBuscar)){
                                parametro.setValor(Integer.parseInt(nuevoValor));
                                System.out.println("\n\tEditado con exito!, nuevo valor es: "+nuevoValor+"\n\n");
                        }
                    }
                    break;
            }

        }

    }
}