package main.java.sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import main.hr.java.covidportal.model.Simptom;
import main.hr.java.covidportal.niti.DohvatiSveSimptomeNit;
import main.hr.java.covidportal.niti.UnesiBolestNit;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Sadrži metode potrebne za inicijalizaciju potrebnih vrijednosti i unos nove bolesti.
 */
public class DodavanjeNoveBolestiController implements Initializable {

    List<Simptom> simptomiList = new ArrayList<>();
    List<Long> simptomiID = new ArrayList<>();
    private static ObservableList<Simptom> observableSimptomi;

    ExecutorService dohvatiSimptomeExecutor = Executors.newCachedThreadPool();
    DohvatiSveSimptomeNit dohvatiSveSimptomeNit = new DohvatiSveSimptomeNit();
    Future<List<Simptom>> simptomFuture;

    @FXML
    private TextField nazivBolesti;

    @FXML
    private ListView<Simptom> simptomiBolesti = new ListView<>();

    /**
     * Dohvaća unesene korisničke vrijednosti i sprema ih u bazu podataka.
     */
    public void unesiBolest() {

        String naziv = nazivBolesti.getText();

        final boolean isVirus = false;

        ObservableList<Simptom> odabraniSimptomi;
        odabraniSimptomi = simptomiBolesti.getSelectionModel().getSelectedItems();
        for (Simptom simptom: odabraniSimptomi) {
            simptomiID.add(simptom.getId());
        }

        UnesiBolestNit unesiBolestNit = new UnesiBolestNit(naziv, isVirus, simptomiID);
        dohvatiSimptomeExecutor.execute(unesiBolestNit);

        nazivBolesti.clear();
        simptomiBolesti.getSelectionModel().clearSelection();
    }

    public static ObservableList<Simptom> getObservableSimptomi() {
        return observableSimptomi;
    }

    public static void setObservableSimptomi(ObservableList<Simptom> observableSimptomi) {
        DodavanjeNoveBolestiController.observableSimptomi = observableSimptomi;
    }

    /**
     * Inicijalizira odabir simptoma za bolest.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        simptomFuture = dohvatiSimptomeExecutor.submit(dohvatiSveSimptomeNit);
        try {
            simptomiList = simptomFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        if(observableSimptomi == null) {
            observableSimptomi = FXCollections.observableArrayList();
        }
        else {
            observableSimptomi.clear();
        }

        observableSimptomi.addAll(simptomiList);
        simptomiBolesti.setEditable(true);
        simptomiBolesti.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        simptomiBolesti.setItems(observableSimptomi);
    }
}
