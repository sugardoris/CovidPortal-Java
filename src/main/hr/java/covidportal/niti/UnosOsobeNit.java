package main.hr.java.covidportal.niti;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import main.database.Database;
import main.java.sample.Main;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Sprema novu osobu u bazu podataka.
 */
public class UnosOsobeNit implements Runnable{

    private final String ime;
    private final String prezime;
    private final LocalDate datumRodjenja;
    private final long zupanijaPrebivaliste;
    private final long zarazenBolescu;
    private final List<Long> kontakti;

    public UnosOsobeNit(String ime, String prezime, LocalDate datumRodjenja, long zupanijaPrebivaliste,
                        long zarazenBolescu, List<Long> kontakti) {
        this.ime = ime;
        this.prezime = prezime;
        this.datumRodjenja = datumRodjenja;
        this.zupanijaPrebivaliste = zupanijaPrebivaliste;
        this.zarazenBolescu = zarazenBolescu;
        this.kontakti = kontakti;
    }

    @Override
    public synchronized void run() {

        while (Database.aktivnaVezaSBazomPodataka) {
            try {
                wait();
            } catch (InterruptedException exc) {
                exc.printStackTrace();
            }
        }

        Database.aktivnaVezaSBazomPodataka = true;

        try {
            Database.spremiNovuOsobu(Main.veza, ime, prezime, datumRodjenja, zupanijaPrebivaliste, zarazenBolescu, kontakti);
            Platform.runLater(() -> {
                Alert obavijest = new Alert(Alert.AlertType.INFORMATION);
                obavijest.setTitle("Unos osobe");
                obavijest.setHeaderText(null);
                obavijest.setContentText("Uspješan unos osobe.");
                obavijest.showAndWait();
            });
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
            Platform.runLater(() -> {
                Alert obavijest = new Alert(Alert.AlertType.ERROR);
                obavijest.setTitle("Unos osobe");
                obavijest.setHeaderText(null);
                obavijest.setContentText("Neuspješan unos osobe!");
                obavijest.showAndWait();
            });
        }

        Database.aktivnaVezaSBazomPodataka = false;
        notifyAll();

    }
}
