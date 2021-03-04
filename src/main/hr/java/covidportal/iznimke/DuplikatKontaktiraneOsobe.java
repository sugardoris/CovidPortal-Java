package main.hr.java.covidportal.iznimke;

/**
 * Predstavlja označenu iznimku koja se baca u slučaju da korisnik pokuša
 * unijeti više istih osoba u popis kontakata.
 */

public class DuplikatKontaktiraneOsobe extends Exception{

    /**
     * Inicijalizira poruku koja se ispisuje prilikom bacanja iznimke.
     * @param poruka poruka koja se ispisuje prilikom bacanja iznimke
     */
    public DuplikatKontaktiraneOsobe(String poruka) { super(poruka); }

    /**
     * Inicijalizira uzrok bacanja iznimke.
     * @param uzrok podatak o uzroku bacanja iznimke
     */
    public DuplikatKontaktiraneOsobe(Throwable uzrok) { super(uzrok); }

    /**
     * Inicijalizira uzrok bacanja iznimke i poruku koja se ispisuje prilikom
     * bacanja iznimke
     * @param poruka poruka koja se ispisuje prilikom bacanja iznimke
     * @param uzrok podatak o uzroku bacanja iznimke
     */
    public DuplikatKontaktiraneOsobe(String poruka, Throwable uzrok) {
        super(poruka, uzrok);
    }
}
