package main.hr.java.covidportal.iznimke;

/**
 * Predstavlja neoznačenu iznimku koja se baca u slučaju da korisnik pokuša
 * unijeti više bolesti ili virusa sa istim simptomima.
 */

public class BolestIstihSimptoma extends RuntimeException{

    /**
     * Inicijalizira poruku koja se ispisuje prilikom bacanja iznimke.
     * @param poruka poruka koja se ispisuje prilikom bacanja iznimke
     */
    public BolestIstihSimptoma(String poruka) {  super(poruka);  }

    /**
     * Inicijalizira uzrok bacanja iznimke.
     * @param uzrok podatak o uzroku bacanja iznimke
     */
    public BolestIstihSimptoma(Throwable uzrok) {  super(uzrok);  }

    /**
     * Inicijalizira uzrok bacanja iznimke i poruku koja se ispisuje prilikom
     * bacanja iznimke
     * @param poruka poruka koja se ispisuje prilikom bacanja iznimke
     * @param uzrok podatak o uzroku bacanja iznimke
     */
    public BolestIstihSimptoma(String poruka, Throwable uzrok) { super(poruka, uzrok); }
}
