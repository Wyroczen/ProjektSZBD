package gieldaszbd;

import static gieldaszbd.GieldaSZBD.conn;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;

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
    
    @FXML
    private ComboBox comboBox_new;
    @FXML
    private VBox vbox2;
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
        
        comboBox_new.setItems(relacje);        
        //listener drugiego comboboxa
        comboBox_new.valueProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue ov, String t, String t1) {
                vbox2.getChildren().clear();
                if(t1 == null){
                 
                }
                else{
                    try{
                        if("Ludzie".equals(t1)) addLudzie();
                        else if("Inwestorzy".equals(t1)) ;
                        else if("Spółki".equals(t1)) addSpolki();
                        else if("Akcje".equals(t1)) ;
                        else if("Waluty".equals(t1)) ;
                        else if("Państwa".equals(t1));
                        else if("Rynki".equals(t1)) ;
                    }           
                    catch (SQLException ex) {
                        System.out.println("Bład wykonania polecenia" + ex.toString());
                    } 
                }
            }
        });
        comboBox_new.getSelectionModel().select(0);
        
        //listener wybranego wiersza w tabeli
        TablicaEncja.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            ObservableList <String> newSelected = (ObservableList <String>)newSelection;
            vbox.getChildren().clear();
            
            if(newSelection == null){
                
            }
            else{
                try{
                    if("Ludzie".equals(currTable)) editLudzie(newSelected);
                    else if("Inwestorzy".equals(currTable)) editInwestorzy(newSelected);
                    else if("Spółki".equals(currTable)) editSpolki(newSelected);
                    else if("Akcje".equals(currTable)) editAkcje(newSelected);
                    else if("Waluty".equals(currTable)) editWaluty(newSelected);
                    else if("Państwa".equals(currTable))editPanstwa(newSelected);
                    else if("Rynki".equals(currTable)) editRynki(newSelected);
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
        if("Spółki".equals(rel)) rs = stmt.executeQuery("select id_spolki, nazwa_spolki, to_char(data_zalozenia,'YYYY-MM-DD') as Data_założenia, budzet, ceo from spolka" + find);
        if("Akcje".equals(rel)) rs = stmt.executeQuery("select id_akcji, nazwa_rynku as RYNEK, wartosc from akcja JOIN rynki on id_gieldy=id_rynku" + find);
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
                                        "update ludzie set imie='"+
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
                    vbox.getChildren().add(imie);
                    vbox.getChildren().add(timie);
                    vbox.getChildren().add(nazwisko);
                    vbox.getChildren().add(tnazwisko);
                    vbox.getChildren().add(kraj);
                    vbox.getChildren().add(cbkraj);
                    vbox.getChildren().add(button1);
    }
    private void editInwestorzy(ObservableList<String> selected) throws SQLException{
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
                                        "update inwestorzy set pesel="+
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
    }//to do
    private void editSpolki(ObservableList<String> selected) throws SQLException{
                    Label nazwa = new Label();
                    nazwa.setText("Nazwa");
                    TextField tnazwa = new TextField();
                    tnazwa.setText(selected.get(1));
                    
                    Label data = new Label();
                    data.setText("Data założenia");
                    TextField tdata = new TextField();
                    tdata.setText(selected.get(2));
                    
                    Label budzet = new Label();
                    budzet.setText("Budzet");
                    TextField tbudzet = new TextField();
                    tbudzet.setText(selected.get(3));
                    
                    Label ceo = new Label();
                    ceo.setText("CEO");
                    ObservableList<String> ludzie = FXCollections.observableArrayList();
                    ComboBox cbludzie = new ComboBox();
                    Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    ResultSet rs = stmt.executeQuery("select pesel from ludzie");
                    while(rs.next()) {
                        ludzie.add(rs.getString(1));
                    }
                    rs.close();
                    stmt.close();
                    cbludzie.setItems(ludzie);
                    cbludzie.getSelectionModel().select(selected.get(4));
                    
                    Button button1 = new Button();
                    button1.setText("Zapisz");
                    button1.setOnAction(new EventHandler<ActionEvent>() {
                        @Override 
                        public void handle(ActionEvent e) {
                            try {
                                Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                ResultSet rs = stmt.executeQuery(
                                        "update spolka set nazwa_spolki='"+
                                        tnazwa.getText()+
                                        "', data_zalozenia=DATE '"+
                                        tdata.getText()+
                                        "', budzet="+
                                        tbudzet.getText()+
                                        ", ceo='"+
                                        cbludzie.getSelectionModel().getSelectedItem().toString()+
                                        "' where id_spolki="+
                                        selected.get(0));
                                stmt.close();
                                rs.close();
                                search(textField_find.getText());
                            } catch (SQLException ex) {
                                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            
                        }
                    });
                    vbox.getChildren().add(nazwa);
                    vbox.getChildren().add(tnazwa);
                    vbox.getChildren().add(data);
                    vbox.getChildren().add(tdata);
                    vbox.getChildren().add(budzet);
                    vbox.getChildren().add(tbudzet);
                    vbox.getChildren().add(ceo);
                    vbox.getChildren().add(cbludzie);
                    vbox.getChildren().add(button1);
    }
    private void editAkcje(ObservableList<String> selected) throws SQLException{
        Label rynek = new Label();
        rynek.setText("Rynek");
        ObservableList<String> rynki = FXCollections.observableArrayList();
        ComboBox cbrynki = new ComboBox();
        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = stmt.executeQuery("select nazwa_rynku from rynki");
        while(rs.next()) {
            rynki.add(rs.getString(1));
        }
        rs.close();
        stmt.close();
        
        cbrynki.setItems(rynki);
        Statement stmt2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs2 = stmt2.executeQuery("select nazwa_rynku from rynki where id_rynku="+selected.get(1));
        rs2.next();
        String nazwaRynku = rs2.getString(1);
        cbrynki.getSelectionModel().select(nazwaRynku);
        rs2.close();
        stmt2.close();
        
        
        Label wartosc = new Label();
        wartosc.setText("Wartość");
        TextField twartosc = new TextField();
        twartosc.setText(selected.get(2));
        
        
        
        Button button1 = new Button();
        button1.setText("Zapisz");
        button1.setOnAction(new EventHandler<ActionEvent>() {
            @Override 
            public void handle(ActionEvent e) {
                try {
                    Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet rs = stmt.executeQuery(
                            "update akcja set id_gieldy=" +
                                    "(SELECT id_rynku " +
                                    "FROM rynki " + 
                                    "WHERE nazwa_rynku='" + cbrynki.getSelectionModel().getSelectedItem().toString()+ 
                            "')" + ", wartosc="+
                            twartosc.getText()+
                            " where id_akcji="+
                            selected.get(0));
                    stmt.close();
                    rs.close();
                    search(textField_find.getText());
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        });
        vbox.getChildren().add(rynek);
        vbox.getChildren().add(cbrynki);
        vbox.getChildren().add(wartosc);
        vbox.getChildren().add(twartosc);
        vbox.getChildren().add(button1);
    }
    private void editWaluty(ObservableList<String> selected) throws SQLException{        
        Label wartosc = new Label();
        wartosc.setText("Wartość");
        TextField twartosc = new TextField();
        twartosc.setText(selected.get(1));
        
        Button button1 = new Button();
        button1.setText("Zapisz");
        button1.setOnAction(new EventHandler<ActionEvent>() {
            @Override 
            public void handle(ActionEvent e) {
                try {
                    Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet rs = stmt.executeQuery(
                            "update waluty set wartosc="+
                            twartosc.getText()+
                            " where nazwa_waluty='"+
                            selected.get(0) + "'");
                    stmt.close();
                    rs.close();
                    search(textField_find.getText());
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        });
        vbox.getChildren().add(wartosc);
        vbox.getChildren().add(twartosc);
        vbox.getChildren().add(button1);
    }
    private void editPanstwa(ObservableList<String> selected) throws SQLException{
        Label skrot = new Label();
        skrot.setText("Skrót");
        TextField tskrot = new TextField();
        tskrot.setText(selected.get(2));
        
        Label waluta = new Label();
        waluta.setText("Waluta");
        ObservableList<String> waluty = FXCollections.observableArrayList();
        ComboBox cbwaluty = new ComboBox();
        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = stmt.executeQuery("select nazwa_waluty from waluty");
        while(rs.next()) {
            waluty.add(rs.getString(1));
        }
        rs.close();
        stmt.close();
        cbwaluty.setItems(waluty);
        cbwaluty.getSelectionModel().select(selected.get(1));
        
        Button button1 = new Button();
        button1.setText("Zapisz");
        button1.setOnAction(new EventHandler<ActionEvent>() {
            @Override 
            public void handle(ActionEvent e) {
                try {
                    Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet rs = stmt.executeQuery(
                            "update panstwa set waluta='"+
                            cbwaluty.getSelectionModel().getSelectedItem().toString() +
                            "', skrot='"+
                            tskrot.getText() +
                            "' where nazwa='"+
                            selected.get(0) + "'");
                    stmt.close();
                    rs.close();
                    search(textField_find.getText());
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        });
        vbox.getChildren().add(skrot);
        vbox.getChildren().add(tskrot);
        vbox.getChildren().add(waluta);
        vbox.getChildren().add(cbwaluty);
        vbox.getChildren().add(button1);
    }
    private void editRynki(ObservableList<String> selected) throws SQLException{
        Label nazwa = new Label();
        nazwa.setText("Nazwa rynku");
        TextField tnazwa = new TextField();
        tnazwa.setText(selected.get(1));
        
        Label waluta = new Label();
        waluta.setText("Waluta");
        ObservableList<String> waluty = FXCollections.observableArrayList();
        ComboBox cbwaluty = new ComboBox();
        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = stmt.executeQuery("select nazwa_waluty from waluty");
        while(rs.next()) {
            waluty.add(rs.getString(1));
        }
        rs.close();
        stmt.close();
        cbwaluty.setItems(waluty);
        cbwaluty.getSelectionModel().select(selected.get(2));
        
        Label kraj = new Label();
        kraj.setText("Państwo");
        ObservableList<String> panstwa = FXCollections.observableArrayList();
        ComboBox cbkraj = new ComboBox();
        Statement stmt2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs2 = stmt2.executeQuery("select nazwa from panstwa");
        while(rs2.next()) {
            panstwa.add(rs2.getString(1));
        }
        rs2.close();
        stmt2.close();
        cbkraj.setItems(panstwa);
        cbkraj.getSelectionModel().select(selected.get(3));
        
        Button button1 = new Button();
        button1.setText("Zapisz");
        button1.setOnAction(new EventHandler<ActionEvent>() {
            @Override 
            public void handle(ActionEvent e) {
                try {
                    Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    System.out.println("update rynki set nazwa_rynku='"+
                            tnazwa.getText()+
                            "', waluta='"+
                            cbwaluty.getSelectionModel().getSelectedItem().toString()+
                            "', panstwo='"+
                            cbkraj.getSelectionModel().getSelectedItem().toString()+
                            "' where id_rynku="+
                            selected.get(0));
                    ResultSet rs = stmt.executeQuery(
                            "update rynki set nazwa_rynku='"+
                            tnazwa.getText()+
                            "', waluta='"+
                            cbwaluty.getSelectionModel().getSelectedItem().toString()+
                            "', panstwo='"+
                            cbkraj.getSelectionModel().getSelectedItem().toString()+
                            "' where id_rynku="+
                            selected.get(0));
                    stmt.close();
                    rs.close();
                    search(textField_find.getText());
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        });
        vbox.getChildren().add(nazwa);
        vbox.getChildren().add(tnazwa);
        vbox.getChildren().add(waluta);
        vbox.getChildren().add(cbwaluty);
        vbox.getChildren().add(kraj);
        vbox.getChildren().add(cbkraj);
        vbox.getChildren().add(button1);
    }

    private void addLudzie() throws SQLException{
                    Label pesel = new Label();
                    pesel.setText("Pesel");
                    TextField tpesel = new TextField();
                    
                    Label imie = new Label();
                    imie.setText("Imię");
                    TextField timie = new TextField();
                    
                    Label nazwisko = new Label();
                    nazwisko.setText("Nazwisko");
                    TextField tnazwisko = new TextField();
                    
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
                    cbkraj.getSelectionModel().select(0);
                    
                    Button button1 = new Button();
                    button1.setText("Dodaj");
                    button1.setOnAction(new EventHandler<ActionEvent>() {
                        @Override 
                        public void handle(ActionEvent e) {
                            try {
                                Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                ResultSet rs = stmt.executeQuery(
                                        "insert into ludzie VALUES(" + tpesel.getText() +
                                        ",'" + timie.getText()+
                                        "','" + tnazwisko.getText()+
                                        "','" + cbkraj.getSelectionModel().getSelectedItem().toString()+
                                        "')");
                                stmt.close();
                                rs.close();
                                search(null);
                            } catch (SQLException ex) {
                                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            
                        }
                    });
                    vbox2.getChildren().add(pesel);
                    vbox2.getChildren().add(tpesel);
                    vbox2.getChildren().add(imie);
                    vbox2.getChildren().add(timie);
                    vbox2.getChildren().add(nazwisko);
                    vbox2.getChildren().add(tnazwisko);
                    vbox2.getChildren().add(kraj);
                    vbox2.getChildren().add(cbkraj);
                    vbox2.getChildren().add(button1);
    }
    private void addInwestorzy(ObservableList<String> selected) throws SQLException{
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
                                        "update inwestorzy set pesel="+
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
    }//to do
    private void addSpolki() throws SQLException{
                    Label nazwa = new Label();
                    nazwa.setText("Nazwa");
                    TextField tnazwa = new TextField();
                    
                    Label data = new Label();
                    data.setText("Data założenia (YYYY-MM-DD)");
                    DatePicker tdata = new DatePicker();
                    tdata.setConverter(new StringConverter<LocalDate>() {
                        String pattern = "yyyy-MM-dd";
                        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

                        {
                            tdata.setPromptText(pattern.toLowerCase());
                        }

                        @Override public String toString(LocalDate date) {
                            if (date != null) {
                                return dateFormatter.format(date);
                            } else {
                                return "";
                            }
                        }

                        @Override public LocalDate fromString(String string) {
                            if (string != null && !string.isEmpty()) {
                                return LocalDate.parse(string, dateFormatter);
                            } else {
                                return null;
                            }
                        }
                    });
                    
                    Label budzet = new Label();
                    budzet.setText("Budzet");
                    TextField tbudzet = new TextField();
                    
                    Label ceo = new Label();
                    ceo.setText("CEO");
                    ObservableList<String> ludzie = FXCollections.observableArrayList();
                    ComboBox cbludzie = new ComboBox();
                    Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    ResultSet rs = stmt.executeQuery("select pesel from ludzie");
                    while(rs.next()) {
                        ludzie.add(rs.getString(1));
                    }
                    rs.close();
                    stmt.close();
                    cbludzie.setItems(ludzie);
                    cbludzie.getSelectionModel().select(0);
                    
                    Button button1 = new Button();
                    button1.setText("Zapisz");
                    button1.setOnAction(new EventHandler<ActionEvent>() {
                        @Override 
                        public void handle(ActionEvent e) {
                            try {
                                Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                ResultSet rs = stmt.executeQuery(
                                        
                                        "insert into spolka(nazwa_spolki, data_zalozenia, budzet, ceo) values('"+
                                        tnazwa.getText()+
                                        "', DATE '"+
                                        tdata.getValue().toString()+
                                        "', "+
                                        tbudzet.getText()+
                                        ", '"+
                                        cbludzie.getSelectionModel().getSelectedItem().toString()+
                                        "')");
                                stmt.close();
                                rs.close();
                                search(null);
                            } catch (SQLException ex) {
                                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            
                        }
                    });
                    vbox2.getChildren().add(nazwa);
                    vbox2.getChildren().add(tnazwa);
                    vbox2.getChildren().add(data);
                    vbox2.getChildren().add(tdata);
                    vbox2.getChildren().add(budzet);
                    vbox2.getChildren().add(tbudzet);
                    vbox2.getChildren().add(ceo);
                    vbox2.getChildren().add(cbludzie);
                    vbox2.getChildren().add(button1);
    }
    private void addAkcje(ObservableList<String> selected) throws SQLException{
        Label rynek = new Label();
        rynek.setText("Rynek");
        ObservableList<String> rynki = FXCollections.observableArrayList();
        ComboBox cbrynki = new ComboBox();
        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = stmt.executeQuery("select nazwa_rynku from rynki");
        while(rs.next()) {
            rynki.add(rs.getString(1));
        }
        rs.close();
        stmt.close();
        
        cbrynki.setItems(rynki);
        Statement stmt2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs2 = stmt2.executeQuery("select nazwa_rynku from rynki where id_rynku="+selected.get(1));
        rs2.next();
        String nazwaRynku = rs2.getString(1);
        cbrynki.getSelectionModel().select(nazwaRynku);
        rs2.close();
        stmt2.close();
        
        
        Label wartosc = new Label();
        wartosc.setText("Wartość");
        TextField twartosc = new TextField();
        twartosc.setText(selected.get(2));
        
        
        
        Button button1 = new Button();
        button1.setText("Zapisz");
        button1.setOnAction(new EventHandler<ActionEvent>() {
            @Override 
            public void handle(ActionEvent e) {
                try {
                    Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet rs = stmt.executeQuery(
                            "update akcja set id_gieldy=" +
                                    "(SELECT id_rynku " +
                                    "FROM rynki " + 
                                    "WHERE nazwa_rynku='" + cbrynki.getSelectionModel().getSelectedItem().toString()+ 
                            "')" + ", wartosc="+
                            twartosc.getText()+
                            " where id_akcji="+
                            selected.get(0));
                    stmt.close();
                    rs.close();
                    search(textField_find.getText());
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        });
        vbox.getChildren().add(rynek);
        vbox.getChildren().add(cbrynki);
        vbox.getChildren().add(wartosc);
        vbox.getChildren().add(twartosc);
        vbox.getChildren().add(button1);
    }
    private void addWaluty(ObservableList<String> selected) throws SQLException{        
        Label wartosc = new Label();
        wartosc.setText("Wartość");
        TextField twartosc = new TextField();
        twartosc.setText(selected.get(1));
        
        Button button1 = new Button();
        button1.setText("Zapisz");
        button1.setOnAction(new EventHandler<ActionEvent>() {
            @Override 
            public void handle(ActionEvent e) {
                try {
                    Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet rs = stmt.executeQuery(
                            "update waluty set wartosc="+
                            twartosc.getText()+
                            " where nazwa_waluty='"+
                            selected.get(0) + "'");
                    stmt.close();
                    rs.close();
                    search(textField_find.getText());
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        });
        vbox.getChildren().add(wartosc);
        vbox.getChildren().add(twartosc);
        vbox.getChildren().add(button1);
    }
    private void addPanstwa(ObservableList<String> selected) throws SQLException{
        Label skrot = new Label();
        skrot.setText("Skrót");
        TextField tskrot = new TextField();
        tskrot.setText(selected.get(2));
        
        Label waluta = new Label();
        waluta.setText("Waluta");
        ObservableList<String> waluty = FXCollections.observableArrayList();
        ComboBox cbwaluty = new ComboBox();
        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = stmt.executeQuery("select nazwa_waluty from waluty");
        while(rs.next()) {
            waluty.add(rs.getString(1));
        }
        rs.close();
        stmt.close();
        cbwaluty.setItems(waluty);
        cbwaluty.getSelectionModel().select(selected.get(1));
        
        Button button1 = new Button();
        button1.setText("Zapisz");
        button1.setOnAction(new EventHandler<ActionEvent>() {
            @Override 
            public void handle(ActionEvent e) {
                try {
                    Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet rs = stmt.executeQuery(
                            "update panstwa set waluta='"+
                            cbwaluty.getSelectionModel().getSelectedItem().toString() +
                            "', skrot='"+
                            tskrot.getText() +
                            "' where nazwa='"+
                            selected.get(0) + "'");
                    stmt.close();
                    rs.close();
                    search(textField_find.getText());
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        });
        vbox.getChildren().add(skrot);
        vbox.getChildren().add(tskrot);
        vbox.getChildren().add(waluta);
        vbox.getChildren().add(cbwaluty);
        vbox.getChildren().add(button1);
    }
    private void addRynki(ObservableList<String> selected) throws SQLException{
        Label nazwa = new Label();
        nazwa.setText("Nazwa rynku");
        TextField tnazwa = new TextField();
        tnazwa.setText(selected.get(1));
        
        Label waluta = new Label();
        waluta.setText("Waluta");
        ObservableList<String> waluty = FXCollections.observableArrayList();
        ComboBox cbwaluty = new ComboBox();
        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = stmt.executeQuery("select nazwa_waluty from waluty");
        while(rs.next()) {
            waluty.add(rs.getString(1));
        }
        rs.close();
        stmt.close();
        cbwaluty.setItems(waluty);
        cbwaluty.getSelectionModel().select(selected.get(2));
        
        Label kraj = new Label();
        kraj.setText("Państwo");
        ObservableList<String> panstwa = FXCollections.observableArrayList();
        ComboBox cbkraj = new ComboBox();
        Statement stmt2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs2 = stmt2.executeQuery("select nazwa from panstwa");
        while(rs2.next()) {
            panstwa.add(rs2.getString(1));
        }
        rs2.close();
        stmt2.close();
        cbkraj.setItems(panstwa);
        cbkraj.getSelectionModel().select(selected.get(3));
        
        Button button1 = new Button();
        button1.setText("Zapisz");
        button1.setOnAction(new EventHandler<ActionEvent>() {
            @Override 
            public void handle(ActionEvent e) {
                try {
                    Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    System.out.println("update rynki set nazwa_rynku='"+
                            tnazwa.getText()+
                            "', waluta='"+
                            cbwaluty.getSelectionModel().getSelectedItem().toString()+
                            "', panstwo='"+
                            cbkraj.getSelectionModel().getSelectedItem().toString()+
                            "' where id_rynku="+
                            selected.get(0));
                    ResultSet rs = stmt.executeQuery(
                            "update rynki set nazwa_rynku='"+
                            tnazwa.getText()+
                            "', waluta='"+
                            cbwaluty.getSelectionModel().getSelectedItem().toString()+
                            "', panstwo='"+
                            cbkraj.getSelectionModel().getSelectedItem().toString()+
                            "' where id_rynku="+
                            selected.get(0));
                    stmt.close();
                    rs.close();
                    search(textField_find.getText());
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        });
        vbox.getChildren().add(nazwa);
        vbox.getChildren().add(tnazwa);
        vbox.getChildren().add(waluta);
        vbox.getChildren().add(cbwaluty);
        vbox.getChildren().add(kraj);
        vbox.getChildren().add(cbkraj);
        vbox.getChildren().add(button1);
    }    
}