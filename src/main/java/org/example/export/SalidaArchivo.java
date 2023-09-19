package org.example.export;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class SalidaArchivo {
    public static void guardar(String rutaEntrada, List<String> salida){
        String ruta = Paths.get(System.getProperty("user.dir")).toString()+"/src/main/java/org/example/export/pruebas/"+rutaEntrada;
        try {
            FileWriter archivo = new FileWriter(ruta);
            salida.forEach(valor->{
                try {
                    archivo.write(valor+"\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            archivo.flush();
            archivo.close();
        }catch (IOException e){
            System.out.println("Error al escribir "+ruta+e);
        }
    }
}
