package main.java.sample;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import main.hr.java.covidportal.model.Bolest;
import main.hr.java.covidportal.model.Simptom;
import main.hr.java.covidportal.niti.DohvatiSveBolestiNit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Sadrži metode za inicijalizaciju tablice sa virusima, te metodu za pretragu tablice po nazivu virusa.
 */
public class PretragaVirusaController implements Initializable{

    private static final Logger loggerNew = LoggerFactory.getLogger(PretragaVirusaController.class);

    private static ObservableList<Bolest> observableVirusi;
    List<Bolest> virusiList = new ArrayList<>();

    DohvatiSveBolestiNit dohvatiSveBolestiNit = new DohvatiSveBolestiNit();
    ExecutorService dohvatiBolestiExecutor = Executors.newSingleThreadExecutor();
    Future<List<Bolest>> virusFuture;

    @FXML
    private TextField nazivVirusa;

    @FXML
    private TableView<Bolest> tablicaVirusa;

    @FXML
    private TableColumn<Bolest, String> stupacNaziv;

    @FXML
    private TableColumn<Simptom, List<Simptom>> stupacSimptomi;

    /**
     * Inicijalizira tablicu za pretragu virusa.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //postavlja vrijednost stupaca u tablici
        stupacNaziv.setCellValueFactory(new PropertyValueFactory<Bolest, String>("naziv"));
        stupacSimptomi.setCellValueFactory(new PropertyValueFactory<Simptom, List<Simptom>>("simptomi"));

        //preko niti dohvaća sve bolesti iz baze podataka i sprema ih u listu
        virusFuture = dohvatiBolestiExecutor.submit(dohvatiSveBolestiNit);
        try {
            virusiList = virusFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        //inicijalizira observable listu i uklanja elemente iz nje ako je već popunjena
        if(observableVirusi == null) {
            observableVirusi = FXCollections.observableArrayList();
        }
        else {
            observableVirusi.clear();
        }

        //ako se radi o virusu, sprema ga u observable listu
        for (Bolest bolest: virusiList) {
            if (bolest.isVirus()) {
                observableVirusi.add(bolest);
            }
        }

        tablicaVirusa.setItems(observableVirusi);
        loggerNew.info("Inicijalizirana tablica za pretragu virusa.");
        termination(dohvatiBolestiExecutor);
    }

    public static ObservableList<Bolest> getObservableVirusi() {
        return observableVirusi;
    }

    public static void setObservableVirusi(ObservableList<Bolest> observableList) {
        observableVirusi = observableList;
    }

    /**
     * Služi za pretragu virusa prema nazivu.
     */
    public void pretraziViruse() {
        String unos = nazivVirusa.getText();

        List<Bolest> filtriraniVirusi = virusiList.stream()
                .filter(s -> s.getNaziv().toLowerCase().contains(unos.toLowerCase()))
                .collect(Collectors.toList());

        observableVirusi.clear();
        observableVirusi.addAll(FXCollections.observableArrayList(filtriraniVirusi));
    }

    /**
     * Zaustavlja nit.
     * @param service
     */
    public void termination(ExecutorService service) {
        service.shutdown();
        try {
            if (!service.awaitTermination(5, TimeUnit.SECONDS)) {
                service.shutdownNow();
            }
        } catch (InterruptedException ex) {
            service.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
