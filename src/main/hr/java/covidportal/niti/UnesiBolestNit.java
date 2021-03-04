package main.hr.java.covidportal.niti;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import main.database.Database;
import main.java.sample.Main;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Sprema novu bolest u bazu podataka.
 */
public class UnesiBolestNit implements Runnable{

    private final String naziv;
    private final boolean isVirus;
    private final List<Long> simptomiID;

    public UnesiBolestNit(String naziv, boolean isVirus, List<Long> simptomiID) {
        this.naziv = naziv;
        this.isVirus = isVirus;
        this.simptomiID = simptomiID;
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
            Database.spremiNovuBolest(Main.veza, naziv, isVirus, simptomiID);
            Platform.runLater(() -> {
                Alert obavijest = new Alert(Alert.AlertType.INFORMATION);
                obavijest.setTitle("Unos bolesti");
                obavijest.setHeaderText(null);
                obavijest.setContentText("Uspješan unos bolesti.");
                obavijest.showAndWait();
            });
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
            Platform.runLater(() -> {
                Alert obavijest = new Alert(Alert.AlertType.ERROR);
                obavijest.setTitle("Unos bolesti");
                obavijest.setHeaderText(null);
                obavijest.setContentText("Neuspješan unos bolesti!");
                obavijest.showAndWait();
            });
        }

        Database.aktivnaVezaSBazomPodataka = false;
        notifyAll();
    }
}
