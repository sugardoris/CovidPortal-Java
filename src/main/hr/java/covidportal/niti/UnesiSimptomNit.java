package main.hr.java.covidportal.niti;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import main.database.Database;
import main.java.sample.Main;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Sprema novi simptom u bazu podataka.
 */
public class UnesiSimptomNit implements Runnable{

    private final String naziv;
    private final String vrijednost;

    public UnesiSimptomNit(String naziv, String vrijednost) {
        this.naziv = naziv;
        this.vrijednost = vrijednost;
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
            Database.spremiNoviSimptom(Main.veza, naziv, vrijednost);
            Platform.runLater(() -> {
                Alert obavijest = new Alert(Alert.AlertType.INFORMATION);
                obavijest.setTitle("Unos simptoma");
                obavijest.setHeaderText(null);
                obavijest.setContentText("Uspješan unos simptoma.");
                obavijest.showAndWait();
            });
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
            Platform.runLater(() -> {
                Alert obavijest = new Alert(Alert.AlertType.ERROR);
                obavijest.setTitle("Unos simptoma");
                obavijest.setHeaderText(null);
                obavijest.setContentText("Neuspješan unos simptoma!");
                obavijest.showAndWait();
            });
        }

        Database.aktivnaVezaSBazomPodataka = false;
        notifyAll();

    }
}
