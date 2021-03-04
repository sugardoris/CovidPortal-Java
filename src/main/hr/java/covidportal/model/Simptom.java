package main.hr.java.covidportal.model;

import java.io.Serializable;

/**
 * Inicijalizira podatke o simptomima i njihovim vrijednostima.
 * Nasljeđuje klasu ImenovaniEntitet.
 */

public class Simptom extends ImenovaniEntitet implements Serializable {

    private String vrijednost;


    /**
     * Inicijalizira podatke o nazivu simptoma i njegovoj vrijednosti.
     * @param naziv podatak o nazivu simptoma
     * @param vrijednost podatak o vrijednosti simptoma
     */
    public Simptom(long id, String naziv, String vrijednost) {
        super(id, naziv);
        this.vrijednost = vrijednost;
    }

    public String getVrijednost() {
        return vrijednost;
    }

    public void setVrijednost(String vrijednost) {
        this.vrijednost = vrijednost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Simptom simptom)) return false;
        if (!super.equals(o)) return false;

        return getVrijednost().equals(simptom.getVrijednost());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getVrijednost().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
