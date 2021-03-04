package main.hr.java.covidportal.niti;

import main.database.Database;
import main.hr.java.covidportal.model.Simptom;
import main.java.sample.Main;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Dohvaća sve simptome iz baze podataka i sprema ih u listu.
 */
public class DohvatiSveSimptomeNit implements Callable<List<Simptom>> {

    public List<Simptom> simptomi;

    public DohvatiSveSimptomeNit() {
        simptomi = new ArrayList<>();
    }

    @Override
    public synchronized List<Simptom> call() {

        while (Database.aktivnaVezaSBazomPodataka) {
            try {
                wait();
            } catch (InterruptedException exc) {
                exc.printStackTrace();
            }
        }

        Database.aktivnaVezaSBazomPodataka = true;

        try {
            simptomi = Database.dohvatiSveSimptome(Main.veza);
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }

        Database.aktivnaVezaSBazomPodataka = false;
        notifyAll();
        return simptomi;
    }
}
