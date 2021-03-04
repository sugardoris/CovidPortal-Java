package main.hr.java.covidportal.model;

import java.io.Serializable;
import java.util.List;

/**
 * Predstavlja entitet osobe koji je određen imenom, prezimenom, starošću,
 * županijom prebivanja, bolešću kojom je zaražen/a, te poljem osoba s kojima
 * je bio/la u kontaktu.
 * Implementira builder pattern.
 */

public class Osoba implements Serializable {

    private long id;
    private final String ime;
    private final String prezime;
    private final int starost;
    private Zupanija zupanija;
    private Bolest zarazenBolescu;
    private List<Osoba> kontaktiraneOsobe;

    /**
     * Označava privatni konstruktor objekta klase Osoba koji
     * koristi builder pattern.
     * @param builder vraća builder za objekt klase Osoba
     */

    private Osoba(Builder builder) {
        this.id = builder.id;
        this.ime = builder.ime;
        this.prezime = builder.prezime;
        this.starost = builder.starost;
        this.zupanija = builder.zupanija;
        this.zarazenBolescu = builder.zarazenBolescu;
        this.kontaktiraneOsobe = builder.kontaktiraneOsobe;
        if (this.getZarazenBolescu().isVirus()) {
            if(kontaktiraneOsobe != null) {
                for (Osoba osoba : kontaktiraneOsobe) {
                    this.getZarazenBolescu().prelazakZarazeNaOsobu(osoba);
                }
            }
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setZupanija(Zupanija zupanija) {
        this.zupanija = zupanija;
    }

    public void setZarazenBolescu(Bolest zarazenBolescu) {
        this.zarazenBolescu = zarazenBolescu;
    }

    public void setKontaktiraneOsobe(List<Osoba> kontaktiraneOsobe) {
        this.kontaktiraneOsobe = kontaktiraneOsobe;
    }

    public String getIme() {
        return ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public String getImePrezime() {return ime + " " + prezime;}

    public int getStarost() {
        return starost;
    }

    public Zupanija getZupanija() {
        return zupanija;
    }

    public Bolest getZarazenBolescu() {
        return zarazenBolescu;
    }

    public List<Osoba> getKontaktiraneOsobe() {
        return kontaktiraneOsobe;
    }

    @Override
    public String toString() {
        return ime + ' ' + prezime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Osoba osoba)) return false;

        if (getId() != osoba.getId()) return false;
        if (getStarost() != osoba.getStarost()) return false;
        if (!getIme().equals(osoba.getIme())) return false;
        if (!getPrezime().equals(osoba.getPrezime())) return false;
        if (!getZupanija().equals(osoba.getZupanija())) return false;
        if (!getZarazenBolescu().equals(osoba.getZarazenBolescu())) return false;
        return getKontaktiraneOsobe().equals(osoba.getKontaktiraneOsobe());
    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + getIme().hashCode();
        result = 31 * result + getPrezime().hashCode();
        result = 31 * result + getStarost();
        result = 31 * result + getZupanija().hashCode();
        result = 31 * result + getZarazenBolescu().hashCode();
        result = 31 * result + getKontaktiraneOsobe().hashCode();
        return result;
    }

    public static class Builder {

        /**
         * Predstavlja builder patter za klasu Osoba, omogućava postavljanje
         * samo onih parametara koji su dostupni.
         */

        private final long id;
        private final String ime;
        private final String prezime;
        private int starost;
        private Zupanija zupanija;
        private Bolest zarazenBolescu;
        private List<Osoba> kontaktiraneOsobe;

        /**
         * Inicijalizira podatke o imenu i prezimenu osobe.
         * @param ime podatak o imenu osobe
         * @param prezime podatak o prezimenu osobe
         */

        public Builder(long id, String ime, String prezime) {
            this.id = id;
            this.ime = ime;
            this.prezime = prezime;
        }

        /**
         * Inicijalizira podatak o starosti osobe.
         * @param starost podatak o starosti osobe
         * @return vraća builder
         */

        public Builder starost(int starost) {
            this.starost = starost;
            return this;
        }

        /**
         * Inicijalizira podatak o županiji prebivališta osobe
         * @param zupanija objekt klase Zupanija, označava županiju prebivališta osobe
         * @return vraća builder
         */

        public Builder prebivaliste(Zupanija zupanija) {
            this.zupanija = zupanija;
            return this;
        }

        /**
         * Inicijalizira podatak o bolesti osobe.
         * @param bolest objekt klase Bolest, sadrži podatke o bolesti osobe
         * @return vraća builder
         */
        public Builder bolestan(Bolest bolest) {
            this.zarazenBolescu = bolest;
            return this;
        }

        /**
         * Inicijalizira podatak o kontaktima osobe
         * @param kontakti polje tipa klase Osoba, označava kontakte osobe
         * @return vraća builder
         */
        public Builder kontakti(List<Osoba> kontakti) {
            this.kontaktiraneOsobe = kontakti;
            return this;
        }

        /**
         * Inicijalizira novu osobu.
         * @return vraća builder za objekt klase Osoba
         */
        public Osoba build() {
            return new Osoba(this);
        }

    }
}
