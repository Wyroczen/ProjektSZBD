/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gieldaszbd;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.*; //*
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Wyroczen&Socjalis
 */


public class GieldaSZBD extends Application {
    
    public static Connection conn = null;
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Properties connectionProps = new Properties();
        connectionProps.put("user", "homeuser");
        connectionProps.put("password", "password");
        try {
            conn = DriverManager.getConnection("jdbc:oracle:thin:hr/hr@localhost:1521/XE", connectionProps);
            System.out.println("Połączono z bazą danych");
        } catch (SQLException ex) {
            Logger.getLogger(GieldaSZBD.class.getName()).log(Level.SEVERE, "nie udało się połączyć z bazą danych", ex);
            System.exit(-1);
        }
        launch(args); //Tam gdzie ta linijka tam wyświetli się okno, a reszta wykona się po jego zamknięciu*
        try {
            conn.close();
            System.out.println("Rozłączono z bazą danych");
        } catch (SQLException ex) {
            Logger.getLogger(GieldaSZBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
