package main.hr.java.covidportal.model;

import java.io.Serializable;

/**
 * Predstavlja entitet županije koji je definiran nazivom i brojem stanovnika.
 * Nasljeđuje klasu ImenovaniEntitet.
 */

public class Zupanija extends ImenovaniEntitet implements Serializable {

    private int brojStanovnika;
    private int brojZarazenih;
    private double postotakZaraze;


    /**
     *Inicijalizira podatke o nazivu županije i broju stanovnika.
     * @param naziv podatak o nazivu županije
     * @param brojStanovnika podatak o broju stanovnika županije
     */
    public Zupanija(long id, String naziv, int brojStanovnika, int brojZarazenih) {
        super(id, naziv);
        this.brojStanovnika = brojStanovnika;
        this.brojZarazenih = brojZarazenih;
    }

    public int getBrojStanovnika() {
        return brojStanovnika;
    }

    public void setBrojStanovnika(int brojStanovnika) {
        this.brojStanovnika = brojStanovnika;
    }

    public int getBrojZarazenih() {
        return brojZarazenih;
    }

    public void setBrojZarazenih(int brojZarazenih) {
        this.brojZarazenih = brojZarazenih;
    }

    public double getPostotakZaraze() {

        return ((double) this.getBrojZarazenih() / (double) this.getBrojStanovnika()) * 100;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Zupanija zupanija)) return false;
        if (!super.equals(o)) return false;

        if (getBrojStanovnika() != zupanija.getBrojStanovnika()) return false;
        if (getBrojZarazenih() != zupanija.getBrojZarazenih()) return false;
        return Double.compare(zupanija.getPostotakZaraze(), getPostotakZaraze()) == 0;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        long temp;
        result = 31 * result + getBrojStanovnika();
        result = 31 * result + getBrojZarazenih();
        temp = Double.doubleToLongBits(getPostotakZaraze());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
