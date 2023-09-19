package org.example.export;
import org.example.model.Transporte;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class GeneradorColores {
    public static void generarColores(Transporte transporte){
        Random random = new Random();
        Map<String,List<Float>> generaciones = new HashMap<>();
        transporte.getCarrera().forEach(car->{
            for (int i = 0; i < car.getNumeroSemestres(); i++) {
                List<Float> asignados = new ArrayList<>();
                asignados.add(90+random.nextFloat(125));
                asignados.add(100+random.nextFloat(125));
                asignados.add(90+random.nextFloat(125));
                generaciones.put(car.getCodigo()+i,asignados);
            }
        });
        String ruta = Paths.get(System.getProperty("user.dir")).toString()+"/src/main/java/org/example/export/pruebas/coloresAsignaciones.css";
        try {
            FileWriter archivo = new FileWriter(ruta);
            generaciones.forEach((clave,valor)->{
                String rgb = "background-color: rgb("+valor.get(0)+","+valor.get(1)+","+valor.get(2)+");\n";
                try {
                    archivo.write("."+clave+"{\n"+rgb+
                            "color: black;\n" +
                            "    width: 200px; /* Ancho de la columna */\n" +
                            "    height: 100px;\n" +
                            "    shape-outside: polygon(0% 0%, 100% 0%, 80% 100%, 0% 100%); /* Forma personalizada */\n" +
                            "    margin-right: 10px; /* Espacio entre columnas */\n" +
                            "    padding: 20px; /* Espacio interno */"
                            +"\n}\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        archivo.flush();
        archivo.close();
        }catch (IOException e){
            System.out.println("Error al escribir colores"+e);
        }
    }
}
