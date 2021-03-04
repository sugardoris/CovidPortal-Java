package main.hr.java.covidportal.niti;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import main.database.Database;
import main.java.sample.Main;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Sprema novu županiju u bazu podataka.
 */
public class UnesiZupanijuNit implements Runnable {

    private final String naziv;
    private final int brStanovnika;
    private final int brZarazenih;

    public UnesiZupanijuNit( String naziv, int brStanovnika, int brZarazenih) {
        this.naziv = naziv;
        this.brStanovnika = brStanovnika;
        this.brZarazenih = brZarazenih;
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
            Database.spremiNovuZupaniju(Main.veza, naziv, brStanovnika, brZarazenih);
            Platform.runLater(() -> {
                Alert obavijest = new Alert(Alert.AlertType.INFORMATION);
                obavijest.setTitle("Unos županije");
                obavijest.setHeaderText(null);
                obavijest.setContentText("Uspješan unos županije.");
                obavijest.showAndWait();
            });
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
            Platform.runLater(() -> {
                Alert obavijest = new Alert(Alert.AlertType.ERROR);
                obavijest.setTitle("Unos županije");
                obavijest.setHeaderText(null);
                obavijest.setContentText("Neuspješan unos županije!");
                obavijest.showAndWait();
            });
        }

        Database.aktivnaVezaSBazomPodataka = false;
        notifyAll();
    }
}
