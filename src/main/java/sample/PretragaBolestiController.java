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
 * Sadrži metode za inicijalizaciju tablice sa bolestima, te metodu za pretragu tablice po nazivu bolesti.
 */
public class PretragaBolestiController implements Initializable{

    private static final Logger loggerNew = LoggerFactory.getLogger(PretragaBolestiController.class);

    private static ObservableList<Bolest> observableBolesti;
    List<Bolest> bolestiList = new ArrayList<>();

    DohvatiSveBolestiNit dohvatiSveBolestiNit = new DohvatiSveBolestiNit();
    ExecutorService dohvatiBolestiExecutor = Executors.newSingleThreadExecutor();
    Future<List<Bolest>> bolestFuture;

    @FXML
    private TextField nazivBolesti;

    @FXML
    private TableView<Bolest> tablicaBolesti;

    @FXML
    private TableColumn<Bolest, String> stupacNaziv;

    @FXML
    private TableColumn<Simptom, List<Simptom>> stupacSimptomi;

    /**
     * Inicijalizira tablicu za pretragu bolesti.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //postavlja vrijednost stupaca u tablici
        stupacNaziv.setCellValueFactory(new PropertyValueFactory<Bolest, String>("naziv"));
        stupacSimptomi.setCellValueFactory(new PropertyValueFactory<Simptom, List<Simptom>>("simptomi"));

        //preko niti dohvaća sve bolesti iz baze podataka i sprema ih u listu
        bolestFuture = dohvatiBolestiExecutor.submit(dohvatiSveBolestiNit);
        try {
            bolestiList = bolestFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        //inicijalizira observable listu i uklanja elemente iz nje ako je već popunjena
        if(observableBolesti == null) {
            observableBolesti = FXCollections.observableArrayList();
        }
        else {
            observableBolesti.clear();
        }

        //ako bolest nije virus, sprema se u observable listu
        for (Bolest bolest: bolestiList) {
            if (!bolest.isVirus()) {
                observableBolesti.add(bolest);
            }
        }

        tablicaBolesti.setItems(observableBolesti);
        loggerNew.info("Inicijalizirana tablica za pretragu bolesti.");
        termination(dohvatiBolestiExecutor);
    }

    public static ObservableList<Bolest> getObservableBolesti() {
        return observableBolesti;
    }

    public static void setObservableBolesti(ObservableList<Bolest> observableList) {
        observableBolesti = observableList;
    }

    /**
     * Služi za pretragu bolesti prema nazivu.
     */
    public void pretraziBolesti() {
        String unos = nazivBolesti.getText();

        List<Bolest> filtriraneBolesti = bolestiList.stream()
                .filter(bolest -> bolest.getNaziv().toLowerCase().contains(unos.toLowerCase()))
                .collect(Collectors.toList());

        observableBolesti.clear();
        observableBolesti.addAll(FXCollections.observableArrayList(filtriraneBolesti));
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
