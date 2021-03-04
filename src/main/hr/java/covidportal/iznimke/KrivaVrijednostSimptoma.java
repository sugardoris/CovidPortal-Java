package main.hr.java.covidportal.iznimke;

/**
 * Predstavlja neoznačenu iznimku koja se baca u slučaju da korisnik pokuša
 * unijeti vrijednost simptoma koja je drugačija od ponuđenih.
 */
public class KrivaVrijednostSimptoma extends RuntimeException{

    /**
     * Inicijalizira poruku koja se ispisuje prilikom bacanja iznimke.
     * @param poruka poruka koja se ispisuje prilikom bacanja iznimke
     */
    public KrivaVrijednostSimptoma (String poruka) {
        super(poruka);
    }

    /**
     * Inicijalizira uzrok bacanja iznimke.
     * @param uzrok podatak o uzroku bacanja iznimke
     */
    public KrivaVrijednostSimptoma (Throwable uzrok) {
        super(uzrok);
    }

    /**
     * Inicijalizira uzrok bacanja iznimke i poruku koja se ispisuje prilikom
     * bacanja iznimke
     * @param poruka poruka koja se ispisuje prilikom bacanja iznimke
     * @param uzrok podatak o uzroku bacanja iznimke
     */
    public KrivaVrijednostSimptoma (String poruka, Throwable uzrok) {
        super(poruka, uzrok);
    }
}
