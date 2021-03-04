package main.hr.java.covidportal.niti;

import main.database.Database;
import main.hr.java.covidportal.model.Zupanija;
import main.java.sample.Main;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Dohvaća sve županije iz baze podataka i sprema ih u listu.
 */
public class DohvatiSveZupanijeNit implements Callable<List<Zupanija>> {

    public List<Zupanija> zupanije;

    public DohvatiSveZupanijeNit() {
        zupanije = new ArrayList<>();
    }

    @Override
    public synchronized List<Zupanija> call() {

        while (Database.aktivnaVezaSBazomPodataka) {
            try {
                wait();
            } catch (InterruptedException exc) {
                exc.printStackTrace();
            }
        }

        Database.aktivnaVezaSBazomPodataka = true;

        try {
            zupanije = Database.dohvatiSveZupanije(Main.veza);
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }

        Database.aktivnaVezaSBazomPodataka = false;
        notifyAll();
        return zupanije;
    }
}
