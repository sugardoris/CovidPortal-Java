package main.java.sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import main.database.Database;
import main.hr.java.covidportal.niti.UnesiSimptomNit;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Sadrži metode potrebne za inicijalizaciju potrebnih vrijednosti i unos novog simptoma.
 */
public class DodavanjeNovogSimptomaController implements Initializable {

    ExecutorService executorService = Executors.newCachedThreadPool();

    @FXML
    private TextField nazivSimptoma;

    @FXML
    private ComboBox vrijednostSimptoma = new ComboBox();


    /**
     * Dohvaća unesene korisničke vrijednosti i sprema ih u bazu podataka.
     */
    public void unesiSimptom() {

        String naziv = nazivSimptoma.getText();

        String vrijednost = vrijednostSimptoma.getSelectionModel().getSelectedItem().toString();

        UnesiSimptomNit unesiSimptomNit = new UnesiSimptomNit(naziv, vrijednost);
        executorService.execute(unesiSimptomNit);

        nazivSimptoma.clear();
        vrijednostSimptoma.getSelectionModel().clearSelection();
    }

    /**
     * Inicijalizira dostupne vrijednosti simptoma.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        vrijednostSimptoma.getItems().addAll("Produktivni", "Intenzivno", "Visoka", "Jaka");
        vrijednostSimptoma.setPromptText("Odaberite vrijednost");
    }
}
