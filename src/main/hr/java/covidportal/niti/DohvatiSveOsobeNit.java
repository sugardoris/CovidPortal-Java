package main.hr.java.covidportal.niti;

import main.database.Database;
import main.hr.java.covidportal.model.Osoba;
import main.java.sample.Main;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Dohvaća sve osobe iz baze podataka i sprema ih u listu.
 */
public class DohvatiSveOsobeNit implements Callable<List<Osoba>> {

    public List<Osoba> osobe;

    public DohvatiSveOsobeNit() {
        osobe = new ArrayList<>();
    }

    @Override
    public synchronized List<Osoba> call() {

        while (Database.aktivnaVezaSBazomPodataka) {
            try {
                wait();
            } catch (InterruptedException exc) {
                exc.printStackTrace();
            }
        }

        Database.aktivnaVezaSBazomPodataka = true;

        try {
            osobe = Database.dohvatiSveOsobe(Main.veza);
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }

        Database.aktivnaVezaSBazomPodataka = false;
        notifyAll();
        return osobe;
    }
}
