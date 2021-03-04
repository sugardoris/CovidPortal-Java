package main.java.sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import main.database.Database;
import main.hr.java.covidportal.model.*;
import main.hr.java.covidportal.niti.DohvatiSveBolestiNit;
import main.hr.java.covidportal.niti.DohvatiSveOsobeNit;
import main.hr.java.covidportal.niti.DohvatiSveZupanijeNit;
import main.hr.java.covidportal.niti.UnosOsobeNit;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Sadrži metode potrebne za inicijalizaciju potrebnih vrijednosti i unos nove osobe.
 */
public class DodavanjeNoveOsobeController implements Initializable {

    List<Zupanija> sveZupanije = new ArrayList<>();
    List<Bolest> sveBolesti = new ArrayList<>();
    List<Osoba> sveOsobe = new ArrayList<>();
    List<Long> kontaktID = new ArrayList<>();
    private static ObservableList<Osoba> observableOsobe;

    ExecutorService executorService = Executors.newCachedThreadPool();
    DohvatiSveZupanijeNit dohvatiSveZupanijeNit = new DohvatiSveZupanijeNit();
    DohvatiSveBolestiNit dohvatiSveBolestiNit = new DohvatiSveBolestiNit();
    DohvatiSveOsobeNit dohvatiSveOsobeNit = new DohvatiSveOsobeNit();
    Future<List<Zupanija>> futureZupanija;
    Future<List<Bolest>> futureBolest;
    Future<List<Osoba>> futureOsoba;

    @FXML
    private TextField ime;

    @FXML
    private TextField prezime;

    @FXML
    private DatePicker datumRodenja;

    @FXML
    private ComboBox<Zupanija> zupanija = new ComboBox();

    @FXML
    private ComboBox<Bolest> zarazen = new ComboBox();

    @FXML
    private ListView<Osoba> kontakti = new ListView<>();

    /**
     * Dohvaća unesene korisničke vrijednosti i sprema ih u bazu podataka.
     */
    public void unesiOsobu() {

        String imeOsobe = ime.getText();
        String prezimeOsobe = prezime.getText();
        LocalDate rodendan = datumRodenja.getValue();

        Zupanija odabranaZupanija = zupanija.getSelectionModel().getSelectedItem();
        long zupanijaId = odabranaZupanija.getId();

        Bolest odabranaBolest = zarazen.getSelectionModel().getSelectedItem();
        long bolestId = odabranaBolest.getId();

        ObservableList<Osoba> odabraneOsobe = kontakti.getSelectionModel().getSelectedItems();
        for (Osoba odabrani : odabraneOsobe) {
            kontaktID.add(odabrani.getId());
        }

        UnosOsobeNit unosOsobeNit = new UnosOsobeNit(imeOsobe, prezimeOsobe, rodendan, zupanijaId, bolestId, kontaktID);
        executorService.execute(unosOsobeNit);

        ime.clear();
        prezime.clear();
        datumRodenja.setValue(null);
        zupanija.getSelectionModel().clearSelection();
        zarazen.getSelectionModel().clearSelection();
        kontakti.getSelectionModel().clearSelection();
    }

    public static ObservableList<Osoba> getObservableOsobe() {
        return observableOsobe;
    }

    public static void setObservableOsobe(ObservableList<Osoba> observableOsobe) {
        DodavanjeNoveOsobeController.observableOsobe = observableOsobe;
    }

    /**
     * Inicijalizira odabir županija, bolesti i kontataka za osobu.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //inicijalizacija zupanija
        futureZupanija = executorService.submit(dohvatiSveZupanijeNit);
        try {
            sveZupanije = futureZupanija.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        zupanija.getItems().addAll(sveZupanije);
        zupanija.setPromptText("Odaberite županiju prebivališta");

        //inicijalizacija bolesti
        futureBolest = executorService.submit(dohvatiSveBolestiNit);
        try {
            sveBolesti = futureBolest.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        zarazen.getItems().addAll(sveBolesti);
        zarazen.setPromptText("Odaberite bolest ili virus");

        //inicijalizacija kontaktiranih osoba, punjenje observable liste
        if(observableOsobe == null) {
            observableOsobe = FXCollections.observableArrayList();
        }
        else {
            observableOsobe.clear();
        }

        futureOsoba = executorService.submit(dohvatiSveOsobeNit);
        try {
            sveOsobe = futureOsoba.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        observableOsobe.addAll(sveOsobe);
        kontakti.setEditable(true);
        kontakti.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        kontakti.setItems(observableOsobe);
    }
}
