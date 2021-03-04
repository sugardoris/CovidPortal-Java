package main.hr.java.covidportal.niti;

import main.database.Database;
import main.hr.java.covidportal.model.Bolest;
import main.java.sample.Main;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Dohvaća sve bolesti iz baze podataka i sprema ih u listu.
 */
public class DohvatiSveBolestiNit implements Callable<List<Bolest>> {

    public List<Bolest> bolesti;

    public DohvatiSveBolestiNit() {
        bolesti = new ArrayList<>();
    }
    @Override
    public synchronized List<Bolest> call() {

        while (Database.aktivnaVezaSBazomPodataka) {
            try {
                wait();
            } catch (InterruptedException exc) {
                exc.printStackTrace();
            }
        }

        Database.aktivnaVezaSBazomPodataka = true;

        try {
            bolesti = Database.dohvatiSveBolesti(Main.veza);
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }

        Database.aktivnaVezaSBazomPodataka = false;
        notifyAll();
        return bolesti;
    }
}
