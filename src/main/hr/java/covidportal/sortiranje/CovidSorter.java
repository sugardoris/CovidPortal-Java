package main.hr.java.covidportal.sortiranje;

import main.hr.java.covidportal.model.Zupanija;

import java.util.Comparator;

/**
 * Implementira Comparator za sortiranje županija prema postotku zaraženih osoba.
 */
public class CovidSorter implements Comparator<Zupanija> {

    @Override
    public int compare(Zupanija zup1, Zupanija zup2) {
        return Double.compare(zup1.getPostotakZaraze(), zup2.getPostotakZaraze());
    }
}
