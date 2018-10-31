package gieldaszbd;

import static gieldaszbd.GieldaSZBD.conn;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.application.Application.launch;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

/**
 *
 * @author corpt
 */
public class FXMLDocumentController implements Initializable {
    
    
    @FXML
    private TableView TablicaEncja;
    @FXML
    private Menu MenuEncja;
    @FXML
    private MenuItem MenuEncjaItemSpolki;
    @FXML
    private MenuBar MenuBarEncja;

    private ObservableList<ObservableList> dataSpolki;// = FXCollections.observableArrayList();
    //= //a może przenieść na górę?
    //FXCollections.observableArrayList(
    //        new Spolka(1, "A", "a@example.com"),
    //        new Spolka(2, "B", "b@example.com"),
    //        new Spolka(3, "C", "c@example.com")
    //);
    

    //private void handleButtonAction(ActionEvent event) {
    //    System.out.println("You clicked me fucker oh click me please daddy!");
    //    label.setText("Hello World!");
    //}
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
    
    }

    @FXML
    private void handleMenuItemSpolkiAction(ActionEvent event) {
//        //TablicaEncja = new TableView<Spolka>();
        Statement stmt = null;
        ResultSet rs = null;
//
        dataSpolki = FXCollections.observableArrayList();
//
//        //List<Integer> idPracownikow = new ArrayList<Integer>();
         TablicaEncja.setEditable(true); //potrzebne?
//
//        TablicaEncja.getColumns().clear(); //Wyczyszczenie kolumn, nie wiem czy niezbędne, sprawdzi się przy większej ilości działających
////        TableColumn<Spolka,Integer> idCol = new TableColumn("ID");
////        idCol.setCellValueFactory(
////                new PropertyValueFactory<Spolka, Integer>("int1")
////        );
////        TableColumn str1Col = new TableColumn("STR1");
////        str1Col.setCellValueFactory(
////                new PropertyValueFactory<>("str1")
////        );
////        TableColumn str2Col = new TableColumn("STR2");
////        str2Col.setCellValueFactory(
////                new PropertyValueFactory<>("str2")
////        );
//        //idCol.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue()));

        try {
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

//            //rs = stmt.executeQuery("select count(*) " + "from pracownicy");
            rs = stmt.executeQuery("select * " + "from zespoly");
            System.out.println("xd");

            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });

                TablicaEncja.getColumns().addAll(col);
                System.out.println("Column [" + i + "] ");
            }
            /********************************
             * Data added to ObservableList *
             ********************************/
            while(rs.next()){
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){
                    //Iterate Column
                    row.add(rs.getString(i));
                }
                System.out.println("Row [1] added "+row );
                dataSpolki.add(row);
                
            }   
            //FINALLY ADDED TO TableView
            TablicaEncja.setItems(dataSpolki);


            //while (rs.next()) {
            //dataSpolki.add(new Spolka(rs.getInt(1), "xd", "xd"));
            //idPracownikow.add(rs.getInt(1)); //Dodawanie wszystkich elementów na listę, potrzebne? może do edycji
            //
            //TablicaEncja.getItems().add(new Spolka(rs.getInt(1),"xd","xd"));
            //}
            //System.out.println("ID: " + dataSpolki.get(10).getFirstInteger());
            //TablicaEncja.setItems(dataSpolki);
            //TablicaEncja.getColumns().addAll(idCol, str1Col, str2Col);
        } catch (SQLException ex) {
            System.out.println("Bład wykonania polecenia" + ex.toString());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    /* kod obsługi */
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    /* kod obsługi */
                }
            }
        }
        try {
            conn.close();
            System.out.println("Rozłączono z bazą danych");
        } catch (SQLException ex) {
            Logger.getLogger(GieldaSZBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    public static class Spolka {
//
//        private final SimpleIntegerProperty int1;
//        private final SimpleStringProperty str1;
//        private final SimpleStringProperty str2;
//
//        private Spolka(Integer integer1, String string1, String string2) {
//            this.int1 = new SimpleIntegerProperty(integer1);
//            this.str1 = new SimpleStringProperty(string1);
//            this.str2 = new SimpleStringProperty(string2);
//        }
//
//        public Integer getFirstInteger() {
//            return int1.get();
//        }
//
//        public void setFirstInteger(Integer integer1) {
//            int1.set(integer1);
//        }
//
//        public String getFirstString() {
//            return str1.get();
//        }
//
//        public void setFirstString(String string1) {
//            str1.set(string1);
//        }
//
//        public String getSecondString() {
//            return str2.get();
//        }
//
//        public void setSecondString(String string2) {
//            str2.set(string2);
//        }

//    }
}