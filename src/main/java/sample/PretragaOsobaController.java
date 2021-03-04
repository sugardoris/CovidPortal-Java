package main.java.sample;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import main.hr.java.covidportal.model.Osoba;
import main.hr.java.covidportal.niti.DohvatiSveOsobeNit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Sadrži metode za inicijalizaciju tablice sa osobama, te metodu za pretragu tablice po imenu, prezimenu,
 * županiji i bolesti osobe.
 */

public class PretragaOsobaController implements Initializable {

    private static final Logger loggerNew = LoggerFactory.getLogger(PretragaOsobaController.class);

    private static ObservableList<Osoba> observableOsobe;
    List<Osoba> osobeList = new ArrayList<>();

    DohvatiSveOsobeNit dohvatiSveOsobeNit = new DohvatiSveOsobeNit();
    ExecutorService dohvatiOsobeExecutor = Executors.newSingleThreadExecutor();
    Future<List<Osoba>> osobaFuture;

    @FXML
    private TextField imeOsobe;

    @FXML
    private TextField prezimeOsobe;

    @FXML
    private TextField zupanijaOsobe;

    @FXML
    private TextField bolestOsobe;

    @FXML
    private TableView<Osoba> tablicaOsoba;

    @FXML
    private TableColumn<Osoba, String> stupacIme;

    @FXML
    private TableColumn<Osoba, String> stupacPrezime;

    @FXML
    private TableColumn<Osoba, Integer> stupacStarost;

    @FXML
    private TableColumn<Osoba, String> stupacZupanija;

    @FXML
    private TableColumn<Osoba, String> stupacZarazen;

    @FXML
    private TableColumn<Osoba, List<Osoba>> stupacKontakti;

    /**
     * Inicijalizira tablicu za pretragu osoba.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //postavlja vrijednost stupaca u tablici
        stupacIme.setCellValueFactory(new PropertyValueFactory<Osoba, String>("ime"));
        stupacPrezime.setCellValueFactory(new PropertyValueFactory<Osoba, String>("prezime"));
        stupacStarost.setCellValueFactory(new PropertyValueFactory<Osoba, Integer>("starost"));
        stupacZupanija.setCellValueFactory(new PropertyValueFactory<Osoba, String>("zupanija"));
        stupacZarazen.setCellValueFactory(new PropertyValueFactory<Osoba, String>("zarazenBolescu"));
        stupacKontakti.setCellValueFactory(new PropertyValueFactory<Osoba, List<Osoba>>("kontaktiraneOsobe"));

        //preko niti dohvaća sve osobe iz baze podataka i sprema ih u listu
        osobaFuture = dohvatiOsobeExecutor.submit(dohvatiSveOsobeNit);
        try {
            osobeList = osobaFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        //inicijalizira observable listu i uklanja elemente iz nje ako je već popunjena
        if (observableOsobe == null) {
            observableOsobe = FXCollections.observableArrayList();
        }
        else {
            observableOsobe.clear();
        }

        //dodaje sve elemente iz liste osoba u observable listu, te ih postavlja u tablicu
        observableOsobe.addAll(osobeList);
        tablicaOsoba.setItems(observableOsobe);
        loggerNew.info("Inicijalizirana tablica za pretragu osoba.");
        termination(dohvatiOsobeExecutor);
    }

    public static ObservableList<Osoba> getObservableOsobe() {
        return observableOsobe;
    }

    public static void setObservableOsobe(ObservableList<Osoba> observableList) {
        observableOsobe = observableList;
    }

    /**
     * Služi za pretragu osobe prema imenu, prezimenu, županiji prebivanja i bolesti kojom je zaražen/a.
     */
    public void pretraziOsobe() {
        String unosImena = imeOsobe.getText();
        String unosPrezimena = prezimeOsobe.getText();
        String unosZupanije = zupanijaOsobe.getText();
        String unosBolesti = bolestOsobe.getText();

        Predicate<Osoba> predIme =
                p -> p.getIme().toLowerCase().contains(unosImena.toLowerCase());
        Predicate<Osoba> predPrezime =
                p -> p.getPrezime().toLowerCase().contains(unosPrezimena.toLowerCase());
        Predicate<Osoba> predZupanija =
                p -> p.getZupanija().getNaziv().toLowerCase().contains(unosZupanije.toLowerCase());
        Predicate<Osoba> predBolest =
                p -> p.getZarazenBolescu().getNaziv().toLowerCase().contains(unosBolesti.toLowerCase());

        List<Osoba> filtrirane = osobeList.stream()
                .filter(predIme.and(predPrezime.and(predZupanija.and(predBolest))))
                .collect(Collectors.toList());

        observableOsobe.clear();
        observableOsobe.addAll(FXCollections.observableArrayList(filtrirane));
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
