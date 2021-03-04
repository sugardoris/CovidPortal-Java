package main.java.sample;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import main.hr.java.covidportal.model.Zupanija;
import main.hr.java.covidportal.niti.DohvatiSveZupanijeNit;
import main.hr.java.covidportal.niti.NajviseZarazenihNit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Sadrži metode za inicijalizaciju tablice sa županijama, te metodu za pretragu tablice po nazivu županije.
 */
public class PretragaZupanijaController implements Initializable {

    private static final Logger loggerNew = LoggerFactory.getLogger(PretragaZupanijaController.class);

    private static ObservableList<Zupanija> observableZupanije;
    List<Zupanija> zupanijaList = new ArrayList<>();

    NajviseZarazenihNit najviseZarazenihNit = new NajviseZarazenihNit();
    ScheduledExecutorService najviseZarazenihExecutor = Executors.newScheduledThreadPool(1);

    DohvatiSveZupanijeNit dohvatiSveZupanijeNit = new DohvatiSveZupanijeNit();
    ExecutorService dohvatiSveZupanijeExecutor = Executors.newSingleThreadExecutor();
    Future<List<Zupanija>> zupanijaFuture;

    @FXML
    private TextField nazivZupanije;

    @FXML
    private TableView<Zupanija> tablicaZupanija;

    @FXML
    private TableColumn<Zupanija, String> stupacNaziv;

    @FXML
    private TableColumn<Zupanija, Integer> stupacBrStanovnika;

    @FXML
    private TableColumn<Zupanija, Integer> stupacBrZarazenih;

    /**
     * Inicijalizira tablicu za pretragu županija.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //postavlja vrijednost stupaca u tablici
        stupacNaziv.setCellValueFactory(new PropertyValueFactory<Zupanija, String>("naziv"));
        stupacBrStanovnika.setCellValueFactory(new PropertyValueFactory<Zupanija, Integer>("brojStanovnika"));
        stupacBrZarazenih.setCellValueFactory(new PropertyValueFactory<Zupanija, Integer>("brojZarazenih"));

        //preko niti dohvaća sve županije iz baze podataka i sprema ih u listu
        zupanijaFuture = dohvatiSveZupanijeExecutor.submit(dohvatiSveZupanijeNit);
        try {
            zupanijaList = zupanijaFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        //inicijalizira observable listu i uklanja elemente iz nje ako je već popunjena
        if(observableZupanije == null) {
            observableZupanije = FXCollections.observableArrayList();
        }
        else {
            observableZupanije.clear();
        }

        //dodaje sve elemente iz liste zupanija u observable listu, te ih postavlja u tablicu
        observableZupanije.addAll(zupanijaList);
        tablicaZupanija.setItems(observableZupanije);
        loggerNew.info("Inicijalizirana tablica za pretragu zupanija.");
        termination(dohvatiSveZupanijeExecutor);

        najviseZarazenihExecutor.scheduleWithFixedDelay(najviseZarazenihNit, 0, 10, TimeUnit.SECONDS);
    }

    public static ObservableList<Zupanija> getObservableZupanije() {
        return observableZupanije;
    }

    public static void setObservableZupanije(ObservableList<Zupanija> observableList) {
        observableZupanije = observableList;
    }

    /**
     * Služi za pretragu zupanije prema nazivu.
     */
    public void pretraziZupanije() {
        String unos = nazivZupanije.getText();

        List<Zupanija> filtriraneZupanije = zupanijaList.stream()
                .filter(zup -> zup.getNaziv().toLowerCase().contains(unos.toLowerCase()))
                .collect(Collectors.toList());

        observableZupanije.clear();
        observableZupanije.addAll(FXCollections.observableArrayList(filtriraneZupanije));
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
