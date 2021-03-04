package main.java.sample;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import main.database.Database;
import main.hr.java.covidportal.niti.UnesiZupanijuNit;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Sadrži metode potrebne za inicijalizaciju potrebnih vrijednosti i unos nove županije.
 */
public class DodavanjeNoveZupanijeController {

    ExecutorService executorService = Executors.newCachedThreadPool();

    @FXML
    private TextField nazivZupanije;

    @FXML
    private TextField brojStanovnika;

    @FXML
    private TextField brojZarazenih;

    /**
     * Dohvaća unesene korisničke vrijednosti i sprema ih u bazu podataka.
     */
    public void unesiZupaniju() {

        String naziv = nazivZupanije.getText();

        int brStan = Integer.parseInt(brojStanovnika.getText());

        int brZaraz = Integer.parseInt(brojZarazenih.getText());

        UnesiZupanijuNit unesiZupanijuNit = new UnesiZupanijuNit(naziv, brStan, brZaraz);
        executorService.execute(unesiZupanijuNit);

        nazivZupanije.clear();
        brojStanovnika.clear();
        brojZarazenih.clear();
    }

}
