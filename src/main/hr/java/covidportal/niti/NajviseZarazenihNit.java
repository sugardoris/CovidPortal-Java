package main.hr.java.covidportal.niti;
import main.database.Database;
import main.hr.java.covidportal.model.Zupanija;
import main.java.sample.Main;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Ispisuje u konzolu naziv županije s najvećim brojem zaraženih stanovnika i postotak zaraženosti.
 */
public class NajviseZarazenihNit implements Runnable{

    private List<Zupanija> zupanije;

    public NajviseZarazenihNit() {
        zupanije = new ArrayList<>();
    }

    @Override
    public void run() {

        try {
            zupanije = Database.dohvatiSveZupanije(Main.veza);
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }

        Zupanija najzarazenija = zupanije
                .stream()
                .max(Comparator.comparingDouble(Zupanija::getPostotakZaraze)).orElse(null);

        assert najzarazenija != null;
        System.out.println("Županija s najviše zaraženih stanovnika je " + najzarazenija.getNaziv()
                + " i to s " + najzarazenija.getPostotakZaraze() + "%.");

    }
}
