package main.hr.java.covidportal.model;

import java.io.Serializable;
import java.util.List;

/**
 * Predstavlja entitet bolesti koji je definiran nazivom i poljem simptoma.
 * Nasljeđuje klasu ImenovaniEntitet.
 */

public class Bolest extends ImenovaniEntitet implements Zarazno, Serializable {

    private List<Simptom> simptomi;
    private boolean isVirus;

    /**
     * Inicijalizira podatke o nazivu bolesti i njenim simptomima.
     * @param naziv podatak o nazivu bolesti
     * @param simptomi polje koje sadrži podatke o simptomima bolesti
     * @param isVirus određuje je li bolest virusna
     */
    public Bolest(long id, String naziv, List<Simptom> simptomi, boolean isVirus) {
        super(id, naziv);
        this.simptomi = simptomi;
        this.isVirus = isVirus;
    }

    public List<Simptom> getSimptomi() {
        return simptomi;
    }

    public void setSimptomi(List<Simptom> simptomi) {
        this.simptomi = simptomi;
    }

    public boolean isVirus() {
        return isVirus;
    }

    public void setVirus(boolean virus) {
        isVirus = virus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bolest bolest)) return false;
        if (!super.equals(o)) return false;

        if (isVirus() != bolest.isVirus()) return false;
        return getSimptomi().equals(bolest.getSimptomi());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getSimptomi().hashCode();
        result = 31 * result + (isVirus() ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public void prelazakZarazeNaOsobu(Osoba osoba) {
        osoba.setZarazenBolescu(this);
    }
}
