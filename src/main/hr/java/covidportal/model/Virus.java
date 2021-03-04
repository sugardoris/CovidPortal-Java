package main.hr.java.covidportal.model;

import java.io.Serializable;
import java.util.List;

/**
 * Predstavlja entitet virusa koji je definiran nazivom i poljem simptoma.
 * Nasljeđuje klasu Bolest i implementira sučelje Zarazno.
 */

public class Virus extends Bolest implements Zarazno, Serializable {

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    /**
     * Inicijalizira podatke o nazivu virusa i njegovim simptomima.
     * @param naziv podatak o nazivu virusa
     * @param simptomi set koji sadrži podatke o simptomima virusa
     */

    public Virus(long id, String naziv, List<Simptom> simptomi, boolean isVirus) {
        super(id, naziv, simptomi, isVirus);
    }

    /**
     * Prenosi zarazu virusom na kontakte zaražene osobe.
     * Kao parametar prima objekt tipa Osoba.
     * @param osoba objekt tipa Osoba, predstavlja zaraženu osobu
     */
    @Override
    public void prelazakZarazeNaOsobu(Osoba osoba) {
        osoba.setZarazenBolescu(this);
    }
}
