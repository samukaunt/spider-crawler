package br.com.wjaa.spider.ocr;

import br.com.wjaa.spider.importa.EspecialidadeEntity;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 *
 */
public class ImportTransportador {

    public static void main(String args[]){
        ImportTransportador i = new ImportTransportador();
        i.start();
    }

    private void start() {

        Connection c = null;

        try{

            c = connectDataBase();

            Transportador [] transportadorList = getTransportadorList();

            for (Transportador t : transportadorList){
                insert(c, t);
            }
            c.commit();

            c.close();
        }catch (SQLException e){
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    private void insert(Connection dbConnection, Transportador t) {
        PreparedStatement preparedStatement = null;

        try {

            String insertTableSQL = "INSERT INTO TRANSPORTADOR VALUES (?,?,?,?,?,?,?)";

            preparedStatement = dbConnection.prepareStatement(insertTableSQL);

            int count = 1;
            for (String value : t.getValues()){
                preparedStatement.setString(count++, value);
            }
            // execute insert SQL stetement
            preparedStatement.executeUpdate();
            preparedStatement.close();
            System.out.println("Registro inserido.");

        } catch (SQLException e) {

            System.out.println(e.getMessage());

        }


    }

    private Transportador[] getTransportadorList() throws FileNotFoundException {
        return new GsonBuilder().create().fromJson(new FileReader(new File("/home/wagner/Downloads/javaocr/testes/transportador-ok.json")),Transportador[].class);
    }

    private Connection connectDataBase() {

        System.out.println("-------- Oracle JDBC Connection Testing ------");

        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");

        } catch (ClassNotFoundException e) {

            System.out.println("Where is your Oracle JDBC Driver?");
            e.printStackTrace();
            return null;

        }

        System.out.println("Oracle JDBC Driver Registered!");

        Connection connection = null;

        try {

            connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS = (PROTOCOL = TCP)(HOST = BTI-SCAN-GRU.braspress.com.br)(PORT = 1524))(CONNECT_DATA = (SERVICE_NAME = BTI_SRV)))",
                    "tooldba",
                    "mastertool");

        } catch (SQLException e) {

            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return null;

        }

        if (connection != null) {
            System.out.println("You made it, take control your database now!");
        } else {
            System.out.println("Failed to make connection!");
        }


        return connection;


    }

}
