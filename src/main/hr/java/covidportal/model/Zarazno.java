package main.hr.java.covidportal.model;

/**
 * Omogućava prelazak zaraze s osobe na njene kontakte
 * kada je osoba zaražena virusom.
 */

public interface Zarazno {

    /**
     * Prenosi zarazu virusom na kontakte zaražene osobe.
     * Kao parametar prima objekt tipa Osoba s koje širi zarazu.
     * @param osoba objekt tipa Osoba, predstavlja zaraženu osobu
     */
    void prelazakZarazeNaOsobu (Osoba osoba);
}
