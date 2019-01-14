package gieldaszbd;

import static gieldaszbd.GieldaSZBD.conn;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
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
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
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

    private ObservableList<ObservableList> dataTabelki;
    private ObservableList<String> relacje;
    private ObservableList<String> relacje2;
    private String currTable;

    @FXML
    private ComboBox comboBox_new;
    @FXML
    private VBox vbox2;
    @FXML
    private TableView transakcje;
    @FXML
    private Button button_transakcja;
    @FXML
    private ComboBox cb_inwestor;
    @FXML
    private ComboBox cb_akcje;
    @FXML
    private Button button_przelicz;
    @FXML
    private Label l_wynik;
    @FXML
    private TextField n_ilosc;
    @FXML
    private ComboBox wal1;
    @FXML
    private ComboBox wal2;
    @FXML
    private Button oblicz;
    @FXML
    private Label wynik;
    @FXML
    private Text KantorText;

    //Do sprawdzania poprawności wypełnionych pól:
    public static boolean toLiczba(String strNum) {
        try {
            int n = Integer.parseInt(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }

    public static boolean toDouble(String strNum) {
        try {
            Double n = Double.parseDouble(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }

    void updateCBs() throws SQLException {
        ObservableList<String> czlowiek = FXCollections.observableArrayList();
        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = stmt.executeQuery("select id_inwestora from inwestor");
        while (rs.next()) {
            czlowiek.add(rs.getString(1));
        }
        rs.close();
        stmt.close();
        cb_inwestor.setItems(czlowiek);

        ObservableList<String> akcje = FXCollections.observableArrayList();
        stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        rs = stmt.executeQuery("select id_spolki from akcja");
        while (rs.next()) {
            akcje.add(rs.getString(1));
        }
        rs.close();
        stmt.close();
        cb_akcje.setItems(akcje);
    }

    void updateCBwal() throws SQLException {
        ObservableList<String> czlowiek = FXCollections.observableArrayList();
        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = stmt.executeQuery("select nazwa_waluty from waluta");
        while (rs.next()) {
            czlowiek.add(rs.getString(1));
        }
        rs.close();
        stmt.close();
        wal1.setItems(czlowiek);
        wal1.getSelectionModel().select(0);
        wal2.setItems(czlowiek);
        wal2.getSelectionModel().select(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        relacje = FXCollections.observableArrayList(
                "Czlowiek",
                "Inwestor",
                "Spółki",
                "Akcja",
                "Waluta",
                "Państwa",
                "Gielda",
                "Kursy",
                "Transakcje"
        );

        relacje2 = FXCollections.observableArrayList(
                "Człowiek",
                "Inwestor indywidualny",
                "Fundusz inwestycyjny",
                "Spółka",
                "Waluta",
                "Państwo",
                "Giełda"
        );
        comboBox_relacje.setItems(relacje);
        //listener comboboxa
        comboBox_relacje.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
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

        comboBox_new.setItems(relacje2);
        //listener drugiego comboboxa
        comboBox_new.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                vbox2.getChildren().clear();
                if (t1 == null) {

                } else {
                    try {
                        if ("Człowiek".equals(t1)) {
                            addCzlowiek();
                        } else if ("Inwestor indywidualny".equals(t1)) {
                            addInwestorIndywidualny();
                        } else if ("Fundusz inwestycyjny".equals(t1)) {
                            addFunduszInwestycyjny();
                        } else if ("Spółka".equals(t1)) {
                            addSpolka();
                        } else if ("Waluta".equals(t1)) {
                            addWaluta();
                        } else if ("Państwo".equals(t1)) {
                            addPanstwo();
                        } else if ("Giełda".equals(t1)) {
                            addGielda();
                        } else if ("Transakcja".equals(t1)) ;
                    } catch (SQLException ex) {
                        System.out.println("Bład wykonania polecenia" + ex.toString());
                    }
                }
            }
        });
        comboBox_new.getSelectionModel().select(0);

        //listener wybranego wiersza w tabeli
        TablicaEncja.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            ObservableList<String> newSelected = (ObservableList<String>) newSelection;
            vbox.getChildren().clear();

            if (newSelection == null) {

            } else {
                try {
                    if ("Czlowiek".equals(currTable)) {
                        editCzlowiek(newSelected);
                    } else if ("Inwestor".equals(currTable)) {
                        editInwestor(newSelected);
                    } else if ("Spółki".equals(currTable)) {
                        editSpolka(newSelected);
                    } else if ("Akcja".equals(currTable)) {
                        editAkcja(newSelected);
                    } else if ("Waluta".equals(currTable)) {
                        editWaluta(newSelected);
                    } else if ("Państwa".equals(currTable)) {
                        editPanstwo(newSelected);
                    } else if ("Gielda".equals(currTable)) {
                        editGielda(newSelected);
                    }
                } catch (SQLException ex) {
                    System.out.println("Bład wykonania polecenia" + ex.toString());
                }
            }
        });

        try {
            utworzTabeleTransakcje();
        } catch (SQLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            updateCBs();
        } catch (SQLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            updateCBwal();
        } catch (SQLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }

        button_transakcja.setOnAction(
                new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                final Stage dialog = new Stage();
                dialog.initModality(Modality.APPLICATION_MODAL);
                //dialog.initOwner(dialog);
                VBox dialogVbox = new VBox();
                dialogVbox.setSpacing(5.0d);
                dialogVbox.setAlignment(Pos.CENTER);
                Scene dialogScene = new Scene(dialogVbox, 300, 400);
                try {
                    addTransakcja(dialogVbox);
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
                dialog.setScene(dialogScene);
                dialog.show();
            }
        });

        button_przelicz.setOnAction(
                new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    CallableStatement stmt;
                    stmt = conn.prepareCall("{? = call liczba_akcji(?, ?)}");
                    stmt.registerOutParameter(1, Types.INTEGER);
                    stmt.setInt(2, Integer.parseInt(cb_inwestor.getSelectionModel().getSelectedItem().toString()));
                    stmt.setInt(3, Integer.parseInt(cb_akcje.getSelectionModel().getSelectedItem().toString()));
                    stmt.execute();
                    Integer output = stmt.getInt(1);
                    stmt.close();
                    l_wynik.setText("Liczba akcji: " + output.toString());
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        oblicz.setOnAction(
                new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                KantorText.setFill(Color.RED);
                if (!toDouble(n_ilosc.getText())) {
                    KantorText.setText("Zła liczba!");
                } else if (Double.parseDouble(n_ilosc.getText()) < 1) {
                    KantorText.setText("Zła liczba!");
                } else {
                    try {
                        KantorText.setText("");
                        String waluta1 = wal1.getSelectionModel().getSelectedItem().toString();
                        String waluta2 = wal2.getSelectionModel().getSelectedItem().toString();
                        Float filosc = Float.parseFloat(n_ilosc.getText());
                        CallableStatement stmt;
                        stmt = conn.prepareCall("{? = call kantor(?, ?, ?)}");
                        stmt.registerOutParameter(1, Types.FLOAT);
                        stmt.setString(2, waluta1);
                        stmt.setString(3, waluta2);
                        stmt.setFloat(4, filosc);
                        stmt.execute();
                        Float output = stmt.getFloat(1);
                        wynik.setText(filosc.toString() + " " + waluta1 + "=" + output.toString() + " " + waluta2);
                        stmt.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }

    private void utworzTabele(ResultSet rs, Statement stmt, String rel, String find) throws SQLException {
        TablicaEncja.getColumns().clear();//czyści kolumny na początku
        dataTabelki = FXCollections.observableArrayList();

        if ("Czlowiek".equals(rel)) {
            rs = stmt.executeQuery("select * from czlowiek" + find);
        }
        if ("Inwestor".equals(rel)) {
            rs = stmt.executeQuery("select id_inwestora, budzet, typ, osoba, zarzadca, spolka from inwestor" + find);
        }
        if ("Spółki".equals(rel)) {
            rs = stmt.executeQuery("select nazwa_spolki, to_char(data_zalozenia,'YYYY-MM-DD') as Data_założenia, budzet, ceo from spolka" + find);
        }
        if ("Akcja".equals(rel)) {
            rs = stmt.executeQuery("select id_spolki, gielda, wartosc, ilosc as LICZBA from akcja" + find);
        }
        if ("Waluta".equals(rel)) {
            rs = stmt.executeQuery("select * from waluta" + find);
        }
        if ("Państwa".equals(rel)) {
            rs = stmt.executeQuery("select * from panstwo" + find);
        }
        if ("Gielda".equals(rel)) {
            rs = stmt.executeQuery("select * from gielda" + find);
        }
        if ("Kursy".equals(rel)) {
            rs = stmt.executeQuery("select * from kurs" + find);
        }
        if ("Transakcje".equals(rel)) {
            rs = stmt.executeQuery("select id_transakcji,"
                    + "(SELECT NVL((SELECT imie || ' ' || nazwisko from czlowiek where pesel=osoba), '') "
                    + "|| NVL((SELECT imie || ' ' || nazwisko from czlowiek where pesel=zarzadca), '') "
                    + "|| NVL((SELECT nazwa_spolki from spolka where id_spolki=spolka), '') "
                    + "FROM inwestor where id_inwestora=inwestor1) as KUPUJĄCY, "
                    + "(SELECT NVL((SELECT imie || ' ' || nazwisko from czlowiek where pesel=osoba), '') "
                    + "|| NVL((SELECT imie || ' ' || nazwisko from czlowiek where pesel=zarzadca), '') "
                    + "|| NVL((SELECT nazwa_spolki from spolka where id_spolki=spolka), '') "
                    + "FROM inwestor where id_inwestora=inwestor2) as SPRZEDAJĄCy, "
                    + "(SELECT nazwa_spolki FROM spolka WHERE id_spolki=akcja) as AKCJA, "
                    + "liczba, cena from transakcja" + find);
        }

        for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
            //We are using non property style for making dynamic table
            final int j = i;
            TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
            col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                    if (param.getValue().get(j) == null) {
                        return new SimpleStringProperty("brak");
                    } else {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                }
            });
            TablicaEncja.getColumns().addAll(col);
        }
        /**
         * ******************************
         * Data added to ObservableList * ******************************
         */
        while (rs.next()) {
            //Iterate Row
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                //Iterate Column
                row.add(rs.getString(i));
            }
            dataTabelki.add(row);
        }
        //FINALLY ADDED TO TableView
        TablicaEncja.setItems(dataTabelki);
    }

    private void utworzTabeleTransakcje() throws SQLException {
        transakcje.getColumns().clear();//czyści kolumny na początku
        dataTabelki = FXCollections.observableArrayList();
        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = stmt.executeQuery("select id_transakcji as id, inwestor1 as id_kup,"
                + "(SELECT NVL((SELECT imie || ' ' || nazwisko from czlowiek where pesel=osoba), '') "
                + "|| NVL((SELECT imie || ' ' || nazwisko from czlowiek where pesel=zarzadca), '') "
                + "|| NVL((SELECT nazwa_spolki from spolka where id_spolki=spolka), '') "
                + "FROM inwestor where id_inwestora=inwestor1) as KUPUJĄCY, inwestor2 as id_sprz,"
                + "(SELECT NVL((SELECT imie || ' ' || nazwisko from czlowiek where pesel=osoba), '') "
                + "|| NVL((SELECT imie || ' ' || nazwisko from czlowiek where pesel=zarzadca), '') "
                + "|| NVL((SELECT nazwa_spolki from spolka where id_spolki=spolka), '') "
                + "FROM inwestor where id_inwestora=inwestor2) as SPRZEDAJĄCY, "
                + "(SELECT nazwa_spolki FROM spolka WHERE id_spolki=akcja) as AKCJA, "
                + "liczba, cena from transakcja");

        for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
            //We are using non property style for making dynamic table
            final int j = i;
            TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
            col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                    if (param.getValue().get(j) == null) {
                        return new SimpleStringProperty("brak");
                    } else {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                }
            });
            transakcje.getColumns().addAll(col);
        }
        /**
         * ******************************
         * Data added to ObservableList ******************************
         */
        while (rs.next()) {
            //Iterate Row
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                //Iterate Column
                row.add(rs.getString(i));
            }
            dataTabelki.add(row);
        }
        //FINALLY ADDED TO TableView
        transakcje.setItems(dataTabelki);
    }

    private void search(String text) {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

            if (text == null || "".equals(text)) {
                utworzTabele(rs, stmt, currTable, "");
            } else {
                String find = "";
                if ("Czlowiek".equals(currTable)) {
                    find += " WHERE UPPER(PESEL) LIKE UPPER('" + text + "') OR "
                            + "UPPER(IMIE) LIKE UPPER('" + text + "') OR "
                            + "UPPER(NAZWISKO) LIKE UPPER('" + text + "')";
                } else if ("Inwestor".equals(currTable)) {
                    find += " WHERE UPPER(PESEL) LIKE UPPER('" + text + "') OR "
                            + "UPPER(IMIE) LIKE UPPER('" + text + "') OR "
                            + "UPPER(NAZWISKO) LIKE UPPER('" + text + "')";
                } else if ("Spółki".equals(currTable)) {
                    find += " WHERE UPPER(nazwa_spolki) LIKE UPPER('" + text + "') OR "
                            + "UPPER(CEO) LIKE UPPER('" + text + "')";
                } else if ("Akcja".equals(currTable)) {
                    find += " WHERE UPPER(id_spolki) LIKE UPPER('" + text + "')";
                } else if ("Waluta".equals(currTable)) {
                    find += " WHERE UPPER(nazwa_waluta) LIKE UPPER('" + text + "')";
                } else if ("Państwa".equals(currTable)) {
                    find += " WHERE UPPER(nazwa) LIKE UPPER('" + text + "') OR "
                            + "UPPER(waluta) LIKE UPPER('" + text + "') OR "
                            + "UPPER(skrot) LIKE UPPER('" + text + "')";
                } else if ("Gielda".equals(currTable)) {
                    find += " WHERE UPPER(nazwa_rynku) LIKE UPPER('" + text + "') OR "
                            + "UPPER(waluta) LIKE UPPER('" + text + "') OR "
                            + "UPPER(panstwo) LIKE UPPER('" + text + "')";
                }

                utworzTabele(rs, stmt, currTable, find);
            }
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
    }

    private void editCzlowiek(ObservableList<String> selected) throws SQLException {
        //Poprawność wprowadzonych danych:
        Text czlowiekImieNazwiskoError = new Text();
        czlowiekImieNazwiskoError.setFill(Color.RED);

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
        ResultSet rs = stmt.executeQuery("select nazwa from panstwo");
        while (rs.next()) {
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
                if (timie.getText() == null || timie.getText().trim().isEmpty() || tnazwisko.getText() == null || tnazwisko.getText().trim().isEmpty()) {
                    czlowiekImieNazwiskoError.setText("Podaj imię i nazwisko!");
                } else if (toDouble(timie.getText()) || toDouble(tnazwisko.getText()) || !timie.getText().matches("[A-Za-z\\u0080-\\u169f]*") || !tnazwisko.getText().matches("[A-Za-z]*")) {
                    czlowiekImieNazwiskoError.setText("Podaj poprawne imię i nazwisko!");
                } else {
                    try {
                        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        ResultSet rs = stmt.executeQuery(
                                "update czlowiek set imie='"
                                + timie.getText()
                                + "', nazwisko='"
                                + tnazwisko.getText()
                                + "', narodowosc='"
                                + cbkraj.getSelectionModel().getSelectedItem().toString()
                                + "' where pesel="
                                + selected.get(0));
                        stmt.close();
                        rs.close();
                        search(textField_find.getText());
                    } catch (SQLException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        vbox.getChildren().add(czlowiekImieNazwiskoError);
        vbox.getChildren().add(imie);
        vbox.getChildren().add(timie);
        vbox.getChildren().add(nazwisko);
        vbox.getChildren().add(tnazwisko);
        vbox.getChildren().add(kraj);
        vbox.getChildren().add(cbkraj);
        vbox.getChildren().add(button1);
    }

    private void editInwestor(ObservableList<String> selected) throws SQLException {
        //Poprawność wprowadzonych danych:
        Text inwestorBudzetError = new Text();
        inwestorBudzetError.setFill(Color.RED);

        if (selected.get(2).equals("Spolka akcyjna")) {
            return;
        }
        Label budzet = new Label();
        budzet.setText("Budzet");
        TextField tbudzet = new TextField();
        tbudzet.setText(selected.get(1));

        Label typ = new Label();
        typ.setText("Typ");
        TextField ttyp = new TextField();
        ttyp.setText(selected.get(2));
        ttyp.setEditable(false);
        ttyp.setDisable(true);
        System.out.println(selected.get(2));
        Button button1 = new Button();
        button1.setText("Zapisz");

        vbox.getChildren().add(inwestorBudzetError);
        vbox.getChildren().add(budzet);
        vbox.getChildren().add(tbudzet);
        vbox.getChildren().add(typ);
        vbox.getChildren().add(ttyp);

        if (selected.get(2).equals("Inwestor indywidualny")) {
            Label ceo = new Label();
            ceo.setText("Właściciel rachunku");
            ObservableList<String> czlowiek = FXCollections.observableArrayList();
            ComboBox cbczlowiek = new ComboBox();
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery("select pesel from czlowiek");
            while (rs.next()) {
                czlowiek.add(rs.getString(1));
            }
            rs.close();
            stmt.close();
            cbczlowiek.setItems(czlowiek);
            cbczlowiek.getSelectionModel().select(selected.get(3));

            vbox.getChildren().add(ceo);
            vbox.getChildren().add(cbczlowiek);

            button1.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    if (tbudzet.getText() == null || tbudzet.getText().trim().isEmpty()) {
                        inwestorBudzetError.setText("Podaj wartość!");
                    } else if (!toDouble(tbudzet.getText())) {
                        inwestorBudzetError.setText("Podaj liczbę większą od 0 1!");
                    } else if (Double.parseDouble(tbudzet.getText()) < 1 || tbudzet.getText().length() > 12) {
                        inwestorBudzetError.setText("Podaj liczbę większą od 0 i mniejszą niż 12 cyfr!");
                    } else {
                        try {
                            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                            ResultSet rs = stmt.executeQuery(
                                    "update inwestor set budzet="
                                    + tbudzet.getText()
                                    + ", osoba='"
                                    + cbczlowiek.getSelectionModel().getSelectedItem().toString()
                                    + "' where id_inwestora="
                                    + selected.get(0));
                            stmt.close();
                            rs.close();
                            search(textField_find.getText());
                            updateCBs();
                        } catch (SQLException ex) {
                            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
            );
        }

        if (selected.get(2).equals("Fundusz inwestycyjny")) {
            Label ceo = new Label();
            ceo.setText("Zarządca funduszu");
            ObservableList<String> czlowiek = FXCollections.observableArrayList();
            ComboBox cbczlowiek = new ComboBox();
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery("select pesel from czlowiek");
            while (rs.next()) {
                czlowiek.add(rs.getString(1));
            }
            rs.close();
            stmt.close();
            cbczlowiek.setItems(czlowiek);
            cbczlowiek.getSelectionModel().select(selected.get(4));

            vbox.getChildren().add(ceo);
            vbox.getChildren().add(cbczlowiek);

            button1.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    if (tbudzet.getText() == null || tbudzet.getText().trim().isEmpty()) {
                        inwestorBudzetError.setText("Podaj wartość!");
                    } else if (!toDouble(tbudzet.getText())) {
                        inwestorBudzetError.setText("Podaj liczbę większą od 0!");
                    } else if (Double.parseDouble(tbudzet.getText()) < 1 || tbudzet.getText().length() > 12) {
                        inwestorBudzetError.setText("Podaj liczbę większą od 0 i mniejszą niż 12 cyfr!");
                    } else {
                        try {
                            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                            ResultSet rs = stmt.executeQuery(
                                    "update inwestor set budzet="
                                    + tbudzet.getText()
                                    + ", zarzadca='"
                                    + cbczlowiek.getSelectionModel().getSelectedItem().toString()
                                    + "' where id_inwestora="
                                    + selected.get(0));
                            stmt.close();
                            rs.close();
                            search(textField_find.getText());
                        } catch (SQLException ex) {
                            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });
        }

        vbox.getChildren().add(button1);
    }//to do

    private void editSpolka(ObservableList<String> selected) throws SQLException {
        Label data = new Label();
        data.setText("Data założenia");
        //TextField tdata = new TextField();
        //tdata.setText(selected.get(1));
        DatePicker tdata = new DatePicker();

        Label budzet = new Label();
        budzet.setText("Budzet");
        TextField tbudzet = new TextField();
        tbudzet.setText(selected.get(2));

        //Poprawność wprowadzonych danych:
        Text spolkaDataBudzetError = new Text();
        spolkaDataBudzetError.setFill(Color.RED);

        Label ceo = new Label();
        ceo.setText("CEO");
        ObservableList<String> czlowiek = FXCollections.observableArrayList();
        ComboBox cbczlowiek = new ComboBox();
        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = stmt.executeQuery("select pesel from czlowiek");
        while (rs.next()) {
            czlowiek.add(rs.getString(1));
        }
        rs.close();
        stmt.close();
        cbczlowiek.setItems(czlowiek);
        cbczlowiek.getSelectionModel().select(selected.get(3));

        Button button1 = new Button();
        button1.setText("Zapisz");
        button1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (tbudzet.getText() == null || tbudzet.getText().trim().isEmpty()) {
                    spolkaDataBudzetError.setText("Podaj wartość!");
                } else if (!toDouble(tbudzet.getText())) {
                    spolkaDataBudzetError.setText("Podaj liczbę większą od 0!");
                } else if (Double.parseDouble(tbudzet.getText()) < 1 || tbudzet.getText().length() > 12) {
                    spolkaDataBudzetError.setText("Podaj liczbę > od 0 < niż 12 cyfr!");
                } else {
                    try {
                        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        ResultSet rs = stmt.executeQuery(
                                "update spolka set data_zalozenia=DATE '"
                                + Date.valueOf(tdata.getValue())//tdata.getText()
                                + "', budzet="
                                + tbudzet.getText()
                                + ", ceo='"
                                + cbczlowiek.getSelectionModel().getSelectedItem().toString()
                                + "' where nazwa_spolki='" + selected.get(0) + "'");
                        stmt.close();
                        rs.close();
                        search(textField_find.getText());
                    } catch (SQLException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
        });
        vbox.getChildren().add(spolkaDataBudzetError);
        vbox.getChildren().add(data);
        vbox.getChildren().add(tdata);
        vbox.getChildren().add(budzet);
        vbox.getChildren().add(tbudzet);
        vbox.getChildren().add(ceo);
        vbox.getChildren().add(cbczlowiek);
        vbox.getChildren().add(button1);
    }

    private void editAkcja(ObservableList<String> selected) throws SQLException {
        Label gielda = new Label();
        gielda.setText("Giełda");
        ObservableList<String> rynki = FXCollections.observableArrayList();
        ComboBox cbgielda = new ComboBox();
        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = stmt.executeQuery("select nazwa_gieldy from gielda");
        while (rs.next()) {
            rynki.add(rs.getString(1));
        }
        rs.close();
        stmt.close();

        //Poprawność wprowadzonych danych:
        Text akcjaWartoscLiczbaAkcjiError = new Text();
        akcjaWartoscLiczbaAkcjiError.setFill(Color.RED);

        cbgielda.setItems(rynki);

        cbgielda.getSelectionModel().select(selected.get(1));

        Label wartosc = new Label();
        wartosc.setText("Wartość");
        TextField twartosc = new TextField();
        twartosc.setText(selected.get(2));

        Label liczba = new Label();
        liczba.setText("Liczba akcji");
        TextField tliczba = new TextField();
        tliczba.setText(selected.get(3));

        Button button1 = new Button();
        button1.setText("Zapisz");
        button1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                //Poprawność wprowadzonych danych:
                if (twartosc.getText() == null || twartosc.getText().trim().isEmpty() || tliczba.getText() == null || tliczba.getText().trim().isEmpty()) {
                    akcjaWartoscLiczbaAkcjiError.setText("Podaj wartość!");
                } else if (!toDouble(twartosc.getText()) || !toDouble(tliczba.getText())) {
                    akcjaWartoscLiczbaAkcjiError.setText("Podaj liczbę większą od 0!");
                } else if (Double.parseDouble(twartosc.getText()) < 1 || Double.parseDouble(tliczba.getText()) < 1 || twartosc.getText().length() > 12) {
                    akcjaWartoscLiczbaAkcjiError.setText("Podaj liczbę większą od 0!");
                } else {
                    try {
                        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        ResultSet rs = stmt.executeQuery(
                                "update akcja set gielda='" + cbgielda.getSelectionModel().getSelectedItem().toString()
                                + "', wartosc="
                                + twartosc.getText()
                                + ", ilosc="
                                + tliczba.getText()
                                + " where id_spolki="
                                + selected.get(0));
                        stmt.close();
                        rs.close();
                        search(textField_find.getText());
                        updateCBs();
                    } catch (SQLException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
        });
        vbox.getChildren().add(akcjaWartoscLiczbaAkcjiError);
        vbox.getChildren().add(gielda);
        vbox.getChildren().add(cbgielda);
        vbox.getChildren().add(wartosc);
        vbox.getChildren().add(twartosc);
        vbox.getChildren().add(liczba);
        vbox.getChildren().add(tliczba);
        vbox.getChildren().add(button1);
    }

    private void editWaluta(ObservableList<String> selected) throws SQLException {
        Label wartosc = new Label();
        wartosc.setText("Wartość");
        TextField twartosc = new TextField();
        twartosc.setText(selected.get(1));
        //Poprawność wprowadzonych danych:
        Text walutaWartoscError = new Text();
        walutaWartoscError.setFill(Color.RED);

        Button button1 = new Button();
        button1.setText("Zapisz");
        button1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                //Poprawność wprowadzonych danych:
                if (twartosc.getText() == null || twartosc.getText().trim().isEmpty()) {
                    walutaWartoscError.setText("Podaj wartość!");
                } else if (!toLiczba(twartosc.getText()) || Integer.parseInt(twartosc.getText()) > 299 || Integer.parseInt(twartosc.getText()) < 1) {
                    walutaWartoscError.setText("Podaj liczbę z zakresu 1-299!");
                } else {
                    try {
                        CallableStatement cStmt = conn.prepareCall("{call update_waluta(?, ?)}");
                        cStmt.setString("nazwa1", selected.get(0));
                        cStmt.setDouble("new_wartosc", Double.parseDouble(twartosc.getText()));
                        cStmt.execute();
                        cStmt.close();
                        search(null);
                    } catch (SQLException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
        });
        vbox.getChildren().add(walutaWartoscError);
        vbox.getChildren().add(wartosc);
        vbox.getChildren().add(twartosc);
        vbox.getChildren().add(button1);
    }

    private void editPanstwo(ObservableList<String> selected) throws SQLException {
        Label skrot = new Label();
        skrot.setText("Skrót");
        TextField tskrot = new TextField();
        tskrot.setText(selected.get(2));

        //Poprawność wprowadzonych danych:
        Text panstwoSkrotError = new Text();
        panstwoSkrotError.setFill(Color.RED);

        Label waluta = new Label();
        waluta.setText("Waluta");
        ObservableList<String> waluty = FXCollections.observableArrayList();
        ComboBox cbwaluta = new ComboBox();
        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = stmt.executeQuery("select nazwa_waluty from waluta");
        while (rs.next()) {
            waluty.add(rs.getString(1));
        }
        rs.close();
        stmt.close();
        cbwaluta.setItems(waluty);
        cbwaluta.getSelectionModel().select(selected.get(1));

        Button button1 = new Button();
        button1.setText("Zapisz");
        button1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (tskrot.getText() == null || tskrot.getText().trim().isEmpty()) {
                    panstwoSkrotError.setText("Podaj skrót!");
                } else if (toLiczba(tskrot.getText()) || !tskrot.getText().equals(tskrot.getText().toUpperCase()) || !tskrot.getText().matches("[A-Z]*") || tskrot.getText().length() > 3) {
                    panstwoSkrotError.setText("Podaj tekst o nie dłuższy niż 3 znaki wielkimi literami!");
                } else {
                    try {
                        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        ResultSet rs = stmt.executeQuery(
                                "update panstwo set waluta='"
                                + cbwaluta.getSelectionModel().getSelectedItem().toString()
                                + "', skrot='"
                                + tskrot.getText()
                                + "' where nazwa='"
                                + selected.get(0) + "'");
                        stmt.close();
                        rs.close();
                        search(textField_find.getText());
                    } catch (SQLException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        vbox.getChildren().add(panstwoSkrotError);
        vbox.getChildren().add(skrot);
        vbox.getChildren().add(tskrot);
        vbox.getChildren().add(waluta);
        vbox.getChildren().add(cbwaluta);
        vbox.getChildren().add(button1);
    }

    private void editGielda(ObservableList<String> selected) throws SQLException {
        Label waluta = new Label();
        waluta.setText("Waluta");
        ObservableList<String> waluty = FXCollections.observableArrayList();
        ComboBox cbwaluta = new ComboBox();
        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = stmt.executeQuery("select nazwa_waluty from waluta");
        while (rs.next()) {
            waluty.add(rs.getString(1));
        }
        rs.close();
        stmt.close();
        cbwaluta.setItems(waluty);
        cbwaluta.getSelectionModel().select(selected.get(1));

        Label kraj = new Label();
        kraj.setText("Państwo");
        ObservableList<String> panstwa = FXCollections.observableArrayList();
        ComboBox cbkraj = new ComboBox();
        Statement stmt2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs2 = stmt2.executeQuery("select nazwa from panstwo");
        while (rs2.next()) {
            panstwa.add(rs2.getString(1));
        }
        rs2.close();
        stmt2.close();
        cbkraj.setItems(panstwa);
        cbkraj.getSelectionModel().select(selected.get(2));

        Button button1 = new Button();
        button1.setText("Zapisz");
        button1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet rs = stmt.executeQuery(
                            "update gielda set waluta='"
                            + cbwaluta.getSelectionModel().getSelectedItem().toString()
                            + "', kraj='"
                            + cbkraj.getSelectionModel().getSelectedItem().toString()
                            + "' where nazwa_gieldy='"
                            + selected.get(0) + "'");
                    stmt.close();
                    rs.close();
                    search(textField_find.getText());
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        vbox.getChildren().add(waluta);
        vbox.getChildren().add(cbwaluta);
        vbox.getChildren().add(kraj);
        vbox.getChildren().add(cbkraj);
        vbox.getChildren().add(button1);
    }

    private void addCzlowiek() throws SQLException {
        //Poprawność wprowadzonych danych:
        Text czlowiekAddError = new Text();
        czlowiekAddError.setFill(Color.RED);

        Label pesel = new Label();
        pesel.setText("Pesel");
        TextField tpesel = new TextField();
        tpesel.setMaxWidth(130.0d);

        Label imie = new Label();
        imie.setText("Imię");
        TextField timie = new TextField();
        timie.setMaxWidth(140.0d);

        Label nazwisko = new Label();
        nazwisko.setText("Nazwisko");
        TextField tnazwisko = new TextField();
        tnazwisko.setMaxWidth(140.0d);

        Label kraj = new Label();
        kraj.setText("Narodowość");
        ObservableList<String> panstwa = FXCollections.observableArrayList();
        ComboBox cbkraj = new ComboBox();
        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = stmt.executeQuery("select nazwa from panstwo");
        while (rs.next()) {
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
                if (timie.getText() == null || timie.getText().trim().isEmpty() || tnazwisko.getText() == null || tnazwisko.getText().trim().isEmpty() || tpesel.getText() == null || tpesel.getText().trim().isEmpty()) {
                    czlowiekAddError.setText("Podaj imię, nazwisko i pesel!");
                } else if (!timie.getText().matches("[A-Za-z\\u0080-\\u169f]*") /*("[A-Ża-ż]*")*/ || !tnazwisko.getText().matches("[A-Za-z\\u0080-\\u169f]*") || !tpesel.getText().matches("[0-9]*") || tpesel.getText().length() > 11) {
                    czlowiekAddError.setText("Niepoprawne dane!");
                } else {
                    try {
                        czlowiekAddError.setText("");
                        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        ResultSet rs = stmt.executeQuery(
                                "insert into czlowiek VALUES(" + tpesel.getText()
                                + ",'" + timie.getText()
                                + "','" + tnazwisko.getText()
                                + "','" + cbkraj.getSelectionModel().getSelectedItem().toString()
                                + "')");
                        stmt.close();
                        rs.close();
                        search(null);
                    } catch (SQLException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        vbox2.getChildren().add(czlowiekAddError);
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

    private void addInwestorIndywidualny() throws SQLException {
        //Poprawność wprowadzonych danych:
        Text inwestorIndywidualnyAddError = new Text();
        inwestorIndywidualnyAddError.setFill(Color.RED);

        Label budzet = new Label();
        budzet.setText("Budżet");
        TextField tbudzet = new TextField();
        tbudzet.setMaxWidth(120.0d);

        Label wlasc = new Label();
        wlasc.setText("Właściciel");
        ObservableList<String> ludzie = FXCollections.observableArrayList();
        ComboBox cbludzie = new ComboBox();
        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = stmt.executeQuery("select pesel from czlowiek");
        while (rs.next()) {
            ludzie.add(rs.getString(1));
        }
        rs.close();
        stmt.close();
        cbludzie.setItems(ludzie);
        cbludzie.getSelectionModel().select(0);

        Button button1 = new Button();
        button1.setText("Dodaj");
        button1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (tbudzet.getText() == null || tbudzet.getText().trim().isEmpty()) {
                    inwestorIndywidualnyAddError.setText("Podaj budzet!");
                } else if (tbudzet.getText().matches("[A-Za-z]*") || !toDouble(tbudzet.getText())) {
                    inwestorIndywidualnyAddError.setText("Niepoprawne dane!");
                } else if (Double.parseDouble(tbudzet.getText()) < 1 || tbudzet.getText().length() > 12) {
                    inwestorIndywidualnyAddError.setText("Niepoprawne dane!");
                } else {
                    try {
                        inwestorIndywidualnyAddError.setText("");
                        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        ResultSet rs = stmt.executeQuery(
                                "insert into inwestor(typ, budzet, osoba) VALUES('Inwestor indywidualny', " + tbudzet.getText()
                                + "," + cbludzie.getSelectionModel().getSelectedItem().toString()
                                + ")");
                        stmt.close();
                        rs.close();
                        search(null);
                        updateCBs();
                    } catch (SQLException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        vbox2.getChildren().add(inwestorIndywidualnyAddError);
        vbox2.getChildren().add(budzet);
        vbox2.getChildren().add(tbudzet);
        vbox2.getChildren().add(wlasc);
        vbox2.getChildren().add(cbludzie);
        vbox2.getChildren().add(button1);
    }

    private void addFunduszInwestycyjny() throws SQLException {
        //Poprawność wprowadzonych danych:
        Text funduszInwestycyjnyAddError = new Text();
        funduszInwestycyjnyAddError.setFill(Color.RED);

        Label budzet = new Label();
        budzet.setText("Budżet");
        TextField tbudzet = new TextField();
        tbudzet.setMaxWidth(120.0d);

        Label wlasc = new Label();
        wlasc.setText("Zarządca");
        ObservableList<String> ludzie = FXCollections.observableArrayList();
        ComboBox cbludzie = new ComboBox();
        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = stmt.executeQuery("select pesel from czlowiek");
        while (rs.next()) {
            ludzie.add(rs.getString(1));
        }
        rs.close();
        stmt.close();
        cbludzie.setItems(ludzie);
        cbludzie.getSelectionModel().select(0);

        Button button1 = new Button();
        button1.setText("Dodaj");
        button1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (tbudzet.getText() == null || tbudzet.getText().trim().isEmpty()) {
                    funduszInwestycyjnyAddError.setText("Podaj budzet!");
                } else if (tbudzet.getText().matches("[A-Za-z]*") || !toDouble(tbudzet.getText())) {
                    funduszInwestycyjnyAddError.setText("Niepoprawne dane!");
                } else if (Double.parseDouble(tbudzet.getText()) < 1 || tbudzet.getText().length() > 12) {
                    funduszInwestycyjnyAddError.setText("Niepoprawne dane!");
                } else {
                    try {
                        funduszInwestycyjnyAddError.setText("");
                        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        ResultSet rs = stmt.executeQuery(
                                "insert into inwestor(typ, budzet, zarzadca) VALUES('Fundusz inwestycyjny', " + tbudzet.getText()
                                + "," + cbludzie.getSelectionModel().getSelectedItem().toString()
                                + ")");
                        stmt.close();
                        rs.close();
                        search(null);
                        updateCBs();
                    } catch (SQLException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        vbox2.getChildren().add(funduszInwestycyjnyAddError);
        vbox2.getChildren().add(budzet);
        vbox2.getChildren().add(tbudzet);
        vbox2.getChildren().add(wlasc);
        vbox2.getChildren().add(cbludzie);
        vbox2.getChildren().add(button1);
    }

    private void addSpolka() throws SQLException {
        //Poprawność wprowadzonych danych:
        Text spolkaAddError = new Text();
        spolkaAddError.setFill(Color.RED);

        Label nazwa = new Label();
        nazwa.setText("Nazwa");
        TextField tnazwa = new TextField();
        tnazwa.setMaxWidth(220.0d);

        Label data = new Label();
        data.setText("Data założenia (YYYY-MM-DD)");
        DatePicker tdata = new DatePicker();
        tdata.setConverter(new StringConverter<LocalDate>() {
            String pattern = "yyyy-MM-dd";
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

            {
                tdata.setPromptText(pattern.toLowerCase());
            }

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
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
        tbudzet.setMaxWidth(80.0d);

        Label ceo = new Label();
        ceo.setText("CEO");
        ObservableList<String> czlowiek = FXCollections.observableArrayList();
        ComboBox cbczlowiek = new ComboBox();
        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = stmt.executeQuery("select pesel from czlowiek");
        while (rs.next()) {
            czlowiek.add(rs.getString(1));
        }
        rs.close();
        stmt.close();
        cbczlowiek.setItems(czlowiek);
        cbczlowiek.getSelectionModel().select(0);

        Label akcje = new Label();
        akcje.setText("Liczba akcji");
        TextField takcje = new TextField();
        takcje.setMaxWidth(100.0d);

        Label cena = new Label();
        cena.setText("Cena akcji");
        TextField tcena = new TextField();
        tcena.setMaxWidth(100.0d);

        Label gielda = new Label();
        gielda.setText("Giełda");
        ObservableList<String> rynki = FXCollections.observableArrayList();
        ComboBox cbgielda = new ComboBox();
        Statement stmt3 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs3 = stmt3.executeQuery("select nazwa_gieldy from gielda");
        while (rs3.next()) {
            rynki.add(rs3.getString(1));
        }
        rs3.close();
        stmt3.close();
        cbgielda.setItems(rynki);
        cbgielda.getSelectionModel().select(0);

        Button button1 = new Button();
        button1.setText("Zapisz");
        button1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (tdata.getValue() == null || tnazwa.getText() == null || tnazwa.getText().trim().isEmpty() || tbudzet.getText() == null || tbudzet.getText().trim().isEmpty() || cena.getText() == null || cena.getText().trim().isEmpty() || akcje.getText() == null || akcje.getText().trim().isEmpty()) {
                    spolkaAddError.setText("Uzupełnij pola!");
                } else if (!tnazwa.getText().matches("[A-Za-z\\u0080-\\u169f]*") || !toDouble(tbudzet.getText()) || !toLiczba(takcje.getText()) || !toDouble(tcena.getText())) {
                    spolkaAddError.setText("Niepoprawne dane!");
                } else if (Double.parseDouble(tbudzet.getText()) < 1 || Integer.parseInt(takcje.getText()) < 1 || Double.parseDouble(tcena.getText()) < 1 || tcena.getText().length() > 6 || tbudzet.getText().length() > 12) {
                    spolkaAddError.setText("Niepoprawne dane!");
                } else {
                    try {
                        spolkaAddError.setText("");
                        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        ResultSet rs = stmt.executeQuery(
                                "insert into spolka(nazwa_spolki, data_zalozenia, budzet, ceo) values('"
                                + tnazwa.getText()
                                + "', DATE '"
                                + tdata.getValue().toString()
                                + "', "
                                + tbudzet.getText()
                                + ", "
                                + cbczlowiek.getSelectionModel().getSelectedItem().toString()
                                + ")");
                        stmt.close();
                        rs.close();

                        Statement stmt2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        ResultSet rs2 = stmt2.executeQuery(
                                "insert into akcja values((select id_spolki from spolka where nazwa_spolki='" + tnazwa.getText()
                                + "'), '"
                                + cbgielda.getSelectionModel().getSelectedItem().toString()
                                + "', "
                                + tcena.getText()
                                + ", "
                                + takcje.getText()
                                + ")");
                        stmt2.close();
                        rs2.close();

                        Statement stmt4 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        ResultSet rs4 = stmt4.executeQuery(
                                "insert into inwestor(budzet, typ, spolka) values(0, 'Spolka akcyjna', (select id_spolki from spolka where nazwa_spolki='" + tnazwa.getText()
                                + "'))");
                        stmt4.close();
                        rs4.close();
                        search(null);
                        updateCBs();
                    } catch (SQLException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        vbox2.getChildren().add(spolkaAddError);
        vbox2.getChildren().add(nazwa);
        vbox2.getChildren().add(tnazwa);
        vbox2.getChildren().add(data);
        vbox2.getChildren().add(tdata);
        vbox2.getChildren().add(budzet);
        vbox2.getChildren().add(tbudzet);
        vbox2.getChildren().add(akcje);
        vbox2.getChildren().add(takcje);
        vbox2.getChildren().add(cena);
        vbox2.getChildren().add(tcena);
        vbox2.getChildren().add(ceo);
        vbox2.getChildren().add(cbczlowiek);
        vbox2.getChildren().add(gielda);
        vbox2.getChildren().add(cbgielda);
        vbox2.getChildren().add(button1);
    }

    private void addWaluta() throws SQLException {
        //Poprawność wprowadzonych danych:
        Text walutaAddError = new Text();
        walutaAddError.setFill(Color.RED);

        Label nazwa = new Label();
        nazwa.setText("Nazwa");
        TextField tnazwa = new TextField();
        tnazwa.setMaxWidth(120.0d);

        Label wartosc = new Label();
        wartosc.setText("Wartość");
        TextField twartosc = new TextField();
        twartosc.setMaxWidth(80.0d);

        Button button1 = new Button();
        button1.setText("Zapisz");
        button1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (tnazwa.getText() == null || tnazwa.getText().trim().isEmpty() || twartosc.getText() == null || twartosc.getText().trim().isEmpty()) {
                    walutaAddError.setText("Podaj nazwę i wartość!");
                } else if (!tnazwa.getText().matches("[A-Za-z\\u0080-\\u169f]*") || !toDouble(twartosc.getText())) {
                    walutaAddError.setText("Niepoprawne dane!");
                } else if (Double.parseDouble(twartosc.getText()) < 1 || twartosc.getText().length() > 12) {
                    walutaAddError.setText("Niepoprawne dane!");
                } else {
                    try {
                        walutaAddError.setText("");
                        CallableStatement cStmt = conn.prepareCall("{call insert_waluta(?, ?)}");
                        cStmt.setString("new_waluta", tnazwa.getText());
                        cStmt.setDouble("new_kurs", Double.parseDouble(twartosc.getText()));
                        cStmt.execute();
                        cStmt.close();
                        search(null);
                        updateCBwal();
                    } catch (SQLException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        vbox2.getChildren().add(walutaAddError);
        vbox2.getChildren().add(nazwa);
        vbox2.getChildren().add(tnazwa);
        vbox2.getChildren().add(wartosc);
        vbox2.getChildren().add(twartosc);
        vbox2.getChildren().add(button1);
    }

    private void addPanstwo() throws SQLException {
        //Poprawność wprowadzonych danych:
        Text panstwoAddError = new Text();
        panstwoAddError.setFill(Color.RED);

        Label nazwa = new Label();
        nazwa.setText("Nazwa");
        TextField tnazwa = new TextField();
        tnazwa.setMaxWidth(200.0d);

        Label skrot = new Label();
        skrot.setText("Skrót");
        TextField tskrot = new TextField();
        tskrot.setMaxWidth(60.0d);

        Label waluta = new Label();
        waluta.setText("Waluta");
        ObservableList<String> waluty = FXCollections.observableArrayList();
        ComboBox cbwaluta = new ComboBox();
        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = stmt.executeQuery("select nazwa_waluty from waluta");
        while (rs.next()) {
            waluty.add(rs.getString(1));
        }
        rs.close();
        stmt.close();
        cbwaluta.setItems(waluty);
        cbwaluta.getSelectionModel().select(0);

        Button button1 = new Button();
        button1.setText("Zapisz");
        button1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (tskrot.getText() == null || tskrot.getText().trim().isEmpty() || tnazwa.getText() == null || tnazwa.getText().trim().isEmpty()) {
                    panstwoAddError.setText("Podaj skrót i nazwę!");
                } else if (toDouble(tskrot.getText()) || !tskrot.getText().equals(tskrot.getText().toUpperCase()) || !tskrot.getText().matches("[A-Za-z\\u0080-\\u169f]*") || toDouble(tnazwa.getText()) || !tnazwa.getText().matches("[A-Za-z\\u0080-\\u169f]*")) {
                    panstwoAddError.setText("Podaj tekst o nie dłuższy niż 3 znaki wielkimi literami dla skrótu i poprawną nazwę!");
                } else {
                    try {
                        panstwoAddError.setText("");
                        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        ResultSet rs = stmt.executeQuery(
                                "insert into panstwo values('" + tnazwa.getText()
                                + "', '"
                                + cbwaluta.getSelectionModel().getSelectedItem().toString()
                                + "', '"
                                + tskrot.getText() + "')");
                        stmt.close();
                        rs.close();
                        search(textField_find.getText());
                    } catch (SQLException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        vbox2.getChildren().add(panstwoAddError);
        vbox2.getChildren().add(nazwa);
        vbox2.getChildren().add(tnazwa);
        vbox2.getChildren().add(skrot);
        vbox2.getChildren().add(tskrot);
        vbox2.getChildren().add(waluta);
        vbox2.getChildren().add(cbwaluta);
        vbox2.getChildren().add(button1);
    }

    private void addGielda() throws SQLException {
        //Poprawność wprowadzonych danych:
        Text gieldaAddError = new Text();
        gieldaAddError.setFill(Color.RED);

        Label nazwa = new Label();
        nazwa.setText("Nazwa giełdy");
        TextField tnazwa = new TextField();
        tnazwa.setMaxWidth(240.0d);

        Label waluta = new Label();
        waluta.setText("Waluta");
        ObservableList<String> waluty = FXCollections.observableArrayList();
        ComboBox cbwaluta = new ComboBox();
        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = stmt.executeQuery("select nazwa_waluty from waluta");
        while (rs.next()) {
            waluty.add(rs.getString(1));
        }
        rs.close();
        stmt.close();
        cbwaluta.setItems(waluty);
        cbwaluta.getSelectionModel().select(0);

        Label kraj = new Label();
        kraj.setText("Państwo");
        ObservableList<String> panstwa = FXCollections.observableArrayList();
        ComboBox cbkraj = new ComboBox();
        Statement stmt2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs2 = stmt2.executeQuery("select nazwa from panstwo");
        while (rs2.next()) {
            panstwa.add(rs2.getString(1));
        }
        rs2.close();
        stmt2.close();
        cbkraj.setItems(panstwa);
        cbkraj.getSelectionModel().select(0);

        Button button1 = new Button();
        button1.setText("Zapisz");
        button1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (tnazwa.getText() == null || tnazwa.getText().trim().isEmpty()) {
                    gieldaAddError.setText("Podaj skrót i nazwę!");
                } else if (toDouble(tnazwa.getText()) || !tnazwa.getText().matches("[A-Za-z\\u0080-\\u169f]*")) {
                    gieldaAddError.setText("Podaj poprawną nazwę!");
                } else {
                    try {
                        gieldaAddError.setText("");
                        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        ResultSet rs = stmt.executeQuery(
                                "insert into gielda(nazwa_gieldy, waluta, kraj) VALUES('"
                                + tnazwa.getText()
                                + "', '"
                                + cbwaluta.getSelectionModel().getSelectedItem().toString()
                                + "', '"
                                + cbkraj.getSelectionModel().getSelectedItem().toString()
                                + "')");
                        stmt.close();
                        rs.close();
                        search(textField_find.getText());
                    } catch (SQLException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        vbox2.getChildren().add(gieldaAddError);
        vbox2.getChildren().add(nazwa);
        vbox2.getChildren().add(tnazwa);
        vbox2.getChildren().add(waluta);
        vbox2.getChildren().add(cbwaluta);
        vbox2.getChildren().add(kraj);
        vbox2.getChildren().add(cbkraj);
        vbox2.getChildren().add(button1);
    }

    private void addTransakcja(VBox vboxx) throws SQLException {
        //Poprawność wprowadzonych danych:
        Text transakcjaAddError = new Text();
        transakcjaAddError.setFill(Color.RED);

        Label kupujacy = new Label();
        kupujacy.setText("Kupujący");
        ObservableList<String> czlowiek = FXCollections.observableArrayList();
        ComboBox cbczlowiek1 = new ComboBox();
        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = stmt.executeQuery("select id_inwestora from inwestor");
        while (rs.next()) {
            czlowiek.add(rs.getString(1));
        }
        rs.close();
        stmt.close();
        cbczlowiek1.setItems(czlowiek);
        cbczlowiek1.getSelectionModel().select(0);

        Label sprzedajacy = new Label();
        sprzedajacy.setText("Sprzedający");
        ComboBox cbczlowiek2 = new ComboBox();
        cbczlowiek2.setItems(czlowiek);
        cbczlowiek2.getSelectionModel().select(0);

        Label akcja = new Label();
        akcja.setText("Akcje");
        ObservableList<String> akcje = FXCollections.observableArrayList();
        ComboBox cbakcje = new ComboBox();
        stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        rs = stmt.executeQuery("select id_spolki from akcja");
        while (rs.next()) {
            akcje.add(rs.getString(1));
        }
        rs.close();
        stmt.close();
        cbakcje.setItems(akcje);
        cbakcje.getSelectionModel().select(0);

        Label liczba = new Label();
        liczba.setText("Liczba akcji");
        TextField tliczba = new TextField();
        tliczba.setMaxWidth(80.0d);

        Button button1 = new Button();
        button1.setText("Zapisz");
        button1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (tliczba.getText() == null || tliczba.getText().trim().isEmpty()) {
                    transakcjaAddError.setText("Podaj liczbę!");
                } else if (!toLiczba(tliczba.getText())) {
                    transakcjaAddError.setText("Podaj poprawną liczbę!");
                } else if (Integer.parseInt(tliczba.getText()) < 1) {
                    transakcjaAddError.setText("Podaj poprawną liczbę!");
                } else {
                    try {
                        transakcjaAddError.setText("");
                        CallableStatement cStmt = conn.prepareCall("{call insert_transakcja(?, ?, ?, ?)}");
                        cStmt.setInt("new_inwestor1", Integer.decode(cbczlowiek1.getSelectionModel().getSelectedItem().toString()));
                        cStmt.setInt("new_inwestor2", Integer.decode(cbczlowiek2.getSelectionModel().getSelectedItem().toString()));
                        cStmt.setInt("new_akcja", Integer.decode(cbakcje.getSelectionModel().getSelectedItem().toString()));
                        cStmt.setInt("new_liczba", Integer.decode(tliczba.getText()));

                        cStmt.execute();
                        cStmt.close();
                        search(null);
                        utworzTabeleTransakcje();
                    } catch (SQLException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        vboxx.getChildren().add(transakcjaAddError);
        vboxx.getChildren().add(kupujacy);
        vboxx.getChildren().add(cbczlowiek1);
        vboxx.getChildren().add(sprzedajacy);
        vboxx.getChildren().add(cbczlowiek2);
        vboxx.getChildren().add(akcja);
        vboxx.getChildren().add(cbakcje);
        vboxx.getChildren().add(liczba);
        vboxx.getChildren().add(tliczba);
        vboxx.getChildren().add(button1);
    }
}
