package org.example.data;

import org.mariadb.jdbc.Driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private java.sql.Connection conexion;
    private final String url = "jdbc:mariadb://localhost:3306/modela1";
    private final String usuario = "root";
    private final String contrasena = "mysql@321";
    public Connection conectar (){
        try{
            Class.forName("org.mariadb.jdbc.Driver");
            conexion =  DriverManager.getConnection(url,usuario,contrasena);
            System.out.println("CE");
            return conexion;
        }catch (SQLException e){
            System.out.println("Error de conexion");
            System.out.print(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void cerrarConexion(){
        try {
            this.conexion.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
