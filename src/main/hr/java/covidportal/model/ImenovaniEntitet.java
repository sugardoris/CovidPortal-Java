package main.hr.java.covidportal.model;

import java.io.Serializable;

/**
 * Predstavlja bilo koji entitet koji sadrži naziv.
 * Apstraktna klasa.
 */

public abstract class ImenovaniEntitet implements Serializable {

    private long id;
    private String naziv;


    public ImenovaniEntitet(long id, String naziv) {
        this.id = id;
        this.naziv = naziv;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImenovaniEntitet that)) return false;

        if (getId() != that.getId()) return false;
        return getNaziv().equals(that.getNaziv());
    }

    @Override
    public int hashCode() {
        int result = getNaziv().hashCode();
        result = 31 * result + (int) (getId() ^ (getId() >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return naziv;
    }

}
