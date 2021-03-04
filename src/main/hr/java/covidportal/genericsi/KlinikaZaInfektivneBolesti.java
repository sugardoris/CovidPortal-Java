package main.hr.java.covidportal.genericsi;

import main.hr.java.covidportal.model.Osoba;
import main.hr.java.covidportal.model.Virus;

import java.util.List;

/**
 * Sadrži sve viruse unesene u aplikaciju, kao i sve osobe zaražene tim virusima.
 * @param <T> Može sadržavati samo instance klase <code>Virus</code> ili instance klasa
 *           koje nasljeđuju klasu <code>Virus</code>
 * @param <S> Može sadržavati samo instance klase <code>Osoba</code> ili instance klasa
 *  *           koje nasljeđuju klasu <code>Vosoba</code>
 */
public class KlinikaZaInfektivneBolesti<T extends Virus, S extends Osoba> {

    private List<T> virusi;
    private List<S> zarazeneOsobe;

    /**
     * Inicijalizira podatke o virusima i osobama koje su njima zaražene.
     * @param virusi lista koja sadrži podatke o virusima koji su uneseni u aplikaciju
     * @param zarazeneOsobe lista koja sadrži podatke o osobama koje su zaražene unesenim virusima
     */
    public KlinikaZaInfektivneBolesti(List<T> virusi, List<S> zarazeneOsobe) {
        this.virusi = virusi;
        this.zarazeneOsobe = zarazeneOsobe;
    }

    public void dodajVirus(T virusic) {
        virusi.add(virusic);
    }

    public void ukloniVirus(T virusic) {
        virusi.remove(virusic);
    }

    public void dodajOsobu(S osobica) {
        zarazeneOsobe.add(osobica);
    }

    public void ukloniOsobu(S osobica) {
        zarazeneOsobe.remove(osobica);
    }

    public List<T> getVirusi() {
        return virusi;
    }

    public void setVirusi(List<T> virusi) {
        this.virusi = virusi;
    }

    public List<S> getZarazeneOsobe() {
        return zarazeneOsobe;
    }

    public void setZarazeneOsobe(List<S> zarazeneOsobe) {
        this.zarazeneOsobe = zarazeneOsobe;
    }
}
