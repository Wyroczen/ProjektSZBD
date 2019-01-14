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
import javafx.scene.image.Image;
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
        //stage.getIcons().add(new Image("C:/Users/wisni/Desktop/Studia/Semestr5/SZBD/ProjektSZBD/GieldaSZBD/src/db.png"));
        stage.getIcons().add(new Image("https://cdn3.iconfinder.com/data/icons/programming/100/database-512.png"));
        stage.setTitle("Giełdy");
       
        
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Properties connectionProps = new Properties();
        connectionProps.put("user", "inf132306");
        connectionProps.put("password", "inf132306");
        try {
            conn = DriverManager.getConnection("jdbc:oracle:thin:hr/hr@//admlab2.cs.put.poznan.pl:1521/dblab02_students.cs.put.poznan.pl", connectionProps);
            System.out.println("Połączono z bazą danych");
        } catch (SQLException ex) {
            Logger.getLogger(GieldaSZBD.class.getName()).log(Level.SEVERE, "nie udało się połączyć z bazą danych", ex);
            System.exit(-1);
        }
        launch(args); //Tam gdzie ta linijka tam wyświetli się okno, a reszta wykona się po jego zamknięciu*
        try {
            conn.rollback();
            conn.close();
            System.out.println("Rozłączono z bazą danych");
        } catch (SQLException ex) {
            Logger.getLogger(GieldaSZBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
