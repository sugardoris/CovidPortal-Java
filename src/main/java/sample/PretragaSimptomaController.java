package main.java.sample;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import main.hr.java.covidportal.model.Simptom;
import main.hr.java.covidportal.niti.DohvatiSveSimptomeNit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Sadrži metode za inicijalizaciju tablice sa simptomima, te metodu za pretragu tablice po nazivu simptoma.
 */
public class PretragaSimptomaController implements Initializable {

    private static final Logger loggerNew = LoggerFactory.getLogger(PretragaSimptomaController.class);

    private static ObservableList<Simptom> observableSimptomi;
    public List<Simptom> simptomiList = new ArrayList<>();

    DohvatiSveSimptomeNit dohvatiSveSimptomeNit = new DohvatiSveSimptomeNit();
    ExecutorService dohvatiSimptomeExecutor = Executors.newSingleThreadExecutor();
    Future<List<Simptom>> simptomFuture;

    @FXML
    private TextField nazivSimptoma;

    @FXML
    private TableView<Simptom> tablicaSimptoma;

    @FXML
    private TableColumn<Simptom, String> stupacNaziv;

    @FXML
    private TableColumn<Simptom, String> stupacVrijednost;

    /**
     * Inicijalizira tablicu za pretragu simptoma.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //postavlja vrijednost stupaca u tablici
        stupacNaziv.setCellValueFactory(new PropertyValueFactory<Simptom, String>("naziv"));
        stupacVrijednost.setCellValueFactory(new PropertyValueFactory<Simptom, String>("vrijednost"));

        //preko niti dohvaća sve simptome iz baze podataka i sprema ih u listu
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

        //dodaje sve elemente iz liste simptoma u observable listu, te ih postavlja u tablicu
        observableSimptomi.addAll(simptomiList);
        tablicaSimptoma.setItems(observableSimptomi);
        loggerNew.info("Inicijalizirana tablica za pretragu simptoma.");
        termination(dohvatiSimptomeExecutor);
    }

    public static ObservableList<Simptom> getObservableSimptomi() {
        return observableSimptomi;
    }

    public static void setObservableSimptomi(ObservableList<Simptom> observableList) {
        observableSimptomi = observableList;
    }

    /**
     * Služi za pretragu simptoma prema nazivu.
     */
    public void pretraziSimptome() {
        String unos = nazivSimptoma.getText();

        List<Simptom> filtriraniSimptomi = simptomiList.stream()
                .filter(s -> s.getNaziv().toLowerCase().contains(unos.toLowerCase()))
                .collect(Collectors.toList());

        observableSimptomi.clear();
        observableSimptomi.addAll(FXCollections.observableArrayList(filtriraniSimptomi));
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
