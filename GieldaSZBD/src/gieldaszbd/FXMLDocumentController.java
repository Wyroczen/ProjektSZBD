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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
    @FXML
    private VBox vbox;
        
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
        comboBox_relacje.getSelectionModel().select(0);
        //listener szukajki
        textField_find.textProperty().addListener((observable, oldValue, newValue) -> {
            Statement stmt = null;
            ResultSet rs = null;
            search(newValue);
        });   
        
        //listener wybranego wiersza w tabeli
        TablicaEncja.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            ObservableList <String> newSelected = (ObservableList <String>)newSelection;
            vbox.getChildren().clear();
            
            if(newSelection == null){
                
            }
            else{
                try{
                    if("Ludzie".equals(currTable)) editLudzie(newSelected);
                    else if("Inwestorzy".equals(currTable)){}
                    else if("Spółki".equals(currTable)){}
                    else if("Akcje".equals(currTable)){}
                    else if("Waluty".equals(currTable)){}
                    else if("Państwa".equals(currTable)){}
                    else if("Rynki".equals(currTable)){}
                }           
                catch (SQLException ex) {
                    System.out.println("Bład wykonania polecenia" + ex.toString());
                } 
            }
        });
    }
    
    private void utworzTabele(ResultSet rs, Statement stmt, String rel, String find) throws SQLException{
        TablicaEncja.getColumns().clear();//czyści kolumny na początku
        dataTabelki = FXCollections.observableArrayList();
        
        System.out.println("select * from" + " " + rel + find);
        if("Ludzie".equals(rel)) rs = stmt.executeQuery("select * from ludzie" + find);
        if("Inwestorzy".equals(rel)) rs = stmt.executeQuery("select * from inwestorzy" + find);
        if("Spółki".equals(rel)) rs = stmt.executeQuery("select * from spolka" + find);
        if("Akcje".equals(rel)) rs = stmt.executeQuery("select * from akcje" + find);
        if("Waluty".equals(rel)) rs = stmt.executeQuery("select * from waluty" + find);
        if("Państwa".equals(rel)) rs = stmt.executeQuery("select * from panstwa" + find);
        if("Rynki".equals(rel)) rs = stmt.executeQuery("select * from rynki" + find);

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
                else if("Inwestorzy".equals(currTable)) find += " WHERE UPPER(PESEL) LIKE UPPER('" + text + "') OR " +
                    "UPPER(IMIE) LIKE UPPER('" + text + "') OR " +
                    "UPPER(NAZWISKO) LIKE UPPER('" + text + "')";
                else if("Spółki".equals(currTable)) find += " WHERE UPPER(nazwa_spolki) LIKE UPPER('" + text + "') OR " +
                    "UPPER(CEO) LIKE UPPER('" + text + "')";
                else if("Akcje".equals(currTable)) find += " WHERE UPPER(id_spolki) LIKE UPPER('" + text + "')";
                else if("Waluty".equals(currTable)) find += " WHERE UPPER(nazwa_waluty) LIKE UPPER('" + text + "')";
                else if("Państwa".equals(currTable)) find += " WHERE UPPER(nazwa) LIKE UPPER('" + text + "') OR " +
                    "UPPER(waluta) LIKE UPPER('" + text + "') OR " +
                    "UPPER(skrot) LIKE UPPER('" + text + "')";
                else if("Rynki".equals(currTable)) find += " WHERE UPPER(nazwa_rynku) LIKE UPPER('" + text + "') OR " +
                    "UPPER(waluta) LIKE UPPER('" + text + "') OR " +
                    "UPPER(panstwo) LIKE UPPER('" + text + "')";
                
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
    private void editLudzie(ObservableList<String> selected) throws SQLException{
                    Label pesel = new Label();
                    pesel.setText("PESEL");
                    TextField tpesel = new TextField();
                    tpesel.setText(selected.get(0));
                    
                    Label imie = new Label();
                    imie.setText("Imię");
                    TextField timie = new TextField();
                    timie.setText(selected.get(1));
                    
                    Label nazwisko = new Label();
                    nazwisko.setText("Nazwisko");
                    TextField tnazwisko = new TextField();
                    tnazwisko.setText(selected.get(2));
                    
                    Label kraj = new Label();
                    kraj.setText("Narodowość");
                    ObservableList<String> panstwa = FXCollections.observableArrayList();
                    ComboBox cbkraj = new ComboBox();
                    Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    ResultSet rs = stmt.executeQuery("select nazwa from panstwa");
                    while(rs.next()) {
                        panstwa.add(rs.getString(1));
                    }
                    rs.close();
                    stmt.close();
                    cbkraj.setItems(panstwa);
                    cbkraj.getSelectionModel().select(selected.get(3));
                    
                    Button button1 = new Button();
                    button1.setText("Zapisz");
                    button1.setOnAction(new EventHandler<ActionEvent>() {
                        @Override 
                        public void handle(ActionEvent e) {
                            try {
                                Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                ResultSet rs = stmt.executeQuery(
                                        "update ludzie set pesel="+
                                        tpesel.getText()+
                                        ", imie='"+
                                        timie.getText()+
                                        "', nazwisko='"+
                                        tnazwisko.getText()+
                                        "', narodowosc='"+
                                        cbkraj.getSelectionModel().getSelectedItem().toString()+
                                        "' where pesel="+
                                        selected.get(0));
                                stmt.close();
                                rs.close();
                                search(textField_find.getText());
                            } catch (SQLException ex) {
                                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            
                    }
});
                    vbox.getChildren().add(pesel);
                    vbox.getChildren().add(tpesel);
                    vbox.getChildren().add(imie);
                    vbox.getChildren().add(timie);
                    vbox.getChildren().add(nazwisko);
                    vbox.getChildren().add(tnazwisko);
                    vbox.getChildren().add(kraj);
                    vbox.getChildren().add(cbkraj);
                    vbox.getChildren().add(button1);
    }
}