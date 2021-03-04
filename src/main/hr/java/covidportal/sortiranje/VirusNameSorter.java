package main.hr.java.covidportal.sortiranje;

import main.hr.java.covidportal.model.Virus;

import java.util.Comparator;

/**
 * Implementira Comparator za sortiranje virusa prema nazivu.
 */
public class VirusNameSorter implements Comparator<Virus> {

    @Override
    public int compare(Virus v1, Virus v2) {
        return v2.getNaziv().compareTo(v1.getNaziv());
    }

    @Override
    public Comparator<Virus> reversed() {
        return null;
    }

}
