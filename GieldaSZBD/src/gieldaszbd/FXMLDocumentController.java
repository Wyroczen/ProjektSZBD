package gieldaszbd;

import static gieldaszbd.GieldaSZBD.conn;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

/**
 *
 * @author corpt
 */
public class FXMLDocumentController implements Initializable {
    @FXML
    private TableView TablicaEncja;
    @FXML
    private ComboBox comboBox_relacje;
    @FXML
    private TextField textField_find;
    
    private ObservableList <ObservableList> dataTabelki;
    private ObservableList <String> relacje;
    private String currTable;
    //private void handleButtonAction(ActionEvent event) {
    //    System.out.println("You clicked me fucker oh click me please daddy!");
    //    label.setText("Hello World!");
    //}
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        relacje = FXCollections.observableArrayList(
            "Ludzie",
            "Inwestorzy",
            "Spółki",
            "Akcje",
            "Waluty",
            "Państwa",
            "Rynki"
        );
        comboBox_relacje.setItems(relacje);
        
        //listener comboboxa
        comboBox_relacje.valueProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue ov, String t, String t1) {
                currTable = t1;
                search(null);
            }
        });
        
        //listener szukajki
        textField_find.textProperty().addListener((observable, oldValue, newValue) -> {
            Statement stmt = null;
            ResultSet rs = null;
            search(newValue);
        });   
    }
    
    private void utworzTabele(ResultSet rs, Statement stmt, String rel, String find) throws SQLException{
        TablicaEncja.getColumns().clear();//czyści kolumny na początku
        //TablicaEncja.getItems().clear();
        dataTabelki = FXCollections.observableArrayList();
        TablicaEncja.setEditable(true);
        
        System.out.println("select * from ludzie" + find);
        if("Ludzie".equals(rel)) rs = stmt.executeQuery("select * from ludzie" + find);
        if("Inwestorzy".equals(rel)) rs = stmt.executeQuery("select * from inwestorzy");
        if("Spółki".equals(rel)) rs = stmt.executeQuery("select * from spolka");
        if("Akcje".equals(rel)) rs = stmt.executeQuery("select * from akcje");
        if("Waluty".equals(rel)) rs = stmt.executeQuery("select * from waluty");
        if("Państwa".equals(rel)) rs = stmt.executeQuery("select * from panstwa");
        if("Rynki".equals(rel)) rs = stmt.executeQuery("select * from rynki");

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
                dataTabelki.add(row);               
            }   
            //FINALLY ADDED TO TableView
            TablicaEncja.setItems(dataTabelki);
    }
    private void search(String text){
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            
            if(text == null || "".equals(text)) utworzTabele(rs, stmt, currTable, "");
            else{
                String find = "";
                if("Ludzie".equals(currTable)) find += " WHERE UPPER(PESEL) LIKE UPPER('" + text + "') OR " +
                    "UPPER(IMIE) LIKE UPPER('" + text + "') OR " +
                    "UPPER(NAZWISKO) LIKE UPPER('" + text + "')"; 
                else if("Inwestorzy".equals(currTable));
                else if("Spółki".equals(currTable)) ;
                else if("Akcje".equals(currTable));
                else if("Waluty".equals(currTable)); 
                else if("Państwa".equals(currTable));
                else if("Rynki".equals(currTable));
                
                utworzTabele(rs, stmt, currTable, find);
            }
        } 
        catch (SQLException ex) {
            System.out.println("Bład wykonania polecenia" + ex.toString());
        } 
        finally {
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
    }
}