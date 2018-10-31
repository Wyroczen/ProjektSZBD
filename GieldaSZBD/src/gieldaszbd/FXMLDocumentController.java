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

    //private ObservableList<ObservableList> dataSpolki;// = FXCollections.observableArrayList();
    private ObservableList<ObservableList> dataTabelki;
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
    private void utworzTabele(ResultSet rs, Statement stmt) throws SQLException
    {
        TablicaEncja.getColumns().clear();//czyści kolumny na początku
        //TablicaEncja.getItems().clear();
        dataTabelki = FXCollections.observableArrayList();
        TablicaEncja.setEditable(true);
        
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
                dataTabelki.add(row);
                
            }   
            //FINALLY ADDED TO TableView
            TablicaEncja.setItems(dataTabelki);
        
    }
    @FXML
    private void handleMenuItemSpolkiAction(ActionEvent event) {

        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

            rs = stmt.executeQuery("select * " + "from etaty");
            System.out.println("xd");

            
            utworzTabele(rs,stmt);

            
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


}