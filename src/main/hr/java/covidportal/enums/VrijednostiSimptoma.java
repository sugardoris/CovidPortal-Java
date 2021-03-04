package main.hr.java.covidportal.enums;

/**
 * Enumeracija dopuštenih vrijednosti simptoma.
 */
public enum VrijednostiSimptoma {

    Produktivni("Produktivni"), Intenzivno("Intenzivno"), Visoka("Visoka"), Jaka("Jaka");

    private final String vrijednost;

    VrijednostiSimptoma(String vrijednost) {
        this.vrijednost = vrijednost;
    }

    public String getVrijednost() {
        return vrijednost;
    }
}
