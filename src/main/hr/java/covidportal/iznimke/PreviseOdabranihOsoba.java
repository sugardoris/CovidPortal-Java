package main.hr.java.covidportal.iznimke;

/**
 * Predstavlja označenu iznimku koja se baca u slučaju da korisnik pokuša
 * unijeti više kontakata za osobu od broja prethodno unesenih osoba.
 * Nasljeđuje klasu Exception.
 */

public class PreviseOdabranihOsoba extends Exception{

    /**
     * Inicijalizira poruku koja se ispisuje prilikom bacanja iznimke.
     * @param poruka poruka koja se ispisuje prilikom bacanja iznimke
     */
    public PreviseOdabranihOsoba(String poruka) { super(poruka); }

    /**
     * Inicijalizira uzrok bacanja iznimke.
     * @param uzrok podatak o uzroku bacanja iznimke
     */
    public PreviseOdabranihOsoba(Throwable uzrok) { super(uzrok); }

    /**
     * Inicijalizira uzrok bacanja iznimke i poruku koja se ispisuje prilikom
     * bacanja iznimke
     * @param poruka poruka koja se ispisuje prilikom bacanja iznimke
     * @param uzrok podatak o uzroku bacanja iznimke
     */
    public PreviseOdabranihOsoba(String poruka, Throwable uzrok) {
        super(poruka, uzrok);
    }
}
