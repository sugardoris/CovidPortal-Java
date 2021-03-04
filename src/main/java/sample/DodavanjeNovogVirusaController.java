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
 * Sadrži metode potrebne za inicijalizaciju potrebnih vrijednosti i unos novog virusa.
 */
public class DodavanjeNovogVirusaController  implements Initializable {

    List<Simptom> simptomiList = new ArrayList<>();
    List<Long> simptomiID = new ArrayList<>();
    private static ObservableList<Simptom> observableSimptomi;

    ExecutorService dohvatiSimptomeExecutor = Executors.newCachedThreadPool();
    DohvatiSveSimptomeNit dohvatiSveSimptomeNit = new DohvatiSveSimptomeNit();
    Future<List<Simptom>> futureSimptom;

    @FXML
    private TextField nazivVirusa;

    @FXML
    private ListView<Simptom> simptomiVirusa = new ListView<>();

    /**
     * Dohvaća unesene korisničke vrijednosti i sprema ih u bazu podataka.
     */
    public void unesiVirus() {

        String naziv = nazivVirusa.getText();

        final boolean isVirus = true;

        ObservableList<Simptom> odabraniSimptomi;
        odabraniSimptomi = simptomiVirusa.getSelectionModel().getSelectedItems();
        for (Simptom simptom: odabraniSimptomi) {
            simptomiID.add(simptom.getId());
        }

        UnesiBolestNit unesiVirusNit = new UnesiBolestNit(naziv, isVirus, simptomiID);
        dohvatiSimptomeExecutor.execute(unesiVirusNit);

        nazivVirusa.clear();
        simptomiVirusa.getSelectionModel().clearSelection();
    }

    public static ObservableList<Simptom> getObservableSimptomi() {
        return observableSimptomi;
    }

    public static void setObservableSimptomi(ObservableList<Simptom> observableSimptomi) {
        DodavanjeNovogVirusaController.observableSimptomi = observableSimptomi;
    }

    /**
     * Inicijalizira odabir simptoma za virus.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        futureSimptom = dohvatiSimptomeExecutor.submit(dohvatiSveSimptomeNit);
        try {
            simptomiList = futureSimptom.get();
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
        simptomiVirusa.setEditable(true);
        simptomiVirusa.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        simptomiVirusa.setItems(observableSimptomi);

    }
}
