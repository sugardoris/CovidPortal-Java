package main.database;

import main.hr.java.covidportal.model.*;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Sadrži metode za povezivanje s bazom podataka, dohvat podataka iz baze i unos novih relacija u bazu.
 */
public class Database {

    public static boolean aktivnaVezaSBazomPodataka = false;

    private static final String DATABASE_CONNECTION = "src\\main\\resources\\connection.properties";

    /**
     * Ostvaruje vezu s bazom podataka.
     * @return vraća objekt tipa <code>Connection</code>
     * @throws SQLException iznimka
     * @throws IOException iznimka
     */
    public static synchronized Connection connectToDatabase() throws SQLException, IOException {

        Properties podaciSpajanje = new Properties();

        podaciSpajanje.load(new FileReader(DATABASE_CONNECTION));

        String urlBazePodataka = podaciSpajanje.getProperty("bazaPodatakaUrl");
        String korisnickoIme = podaciSpajanje.getProperty("korisnickoIme");
        String lozinka = podaciSpajanje.getProperty("lozinka");

        return DriverManager.getConnection(urlBazePodataka, korisnickoIme, lozinka);
    }

    /**
     * Zatvara vezu s bazom podataka
     * @param connection predstavlja vezu s bazom podataka
     * @throws SQLException iznimka
     */
    public static synchronized void closeConnectionToDatabase(Connection connection) throws SQLException {
        connection.close();
    }

    /**
     * Dohvaća sve simptome iz baze podataka.
     * @return vraća listu simptoma
     * @throws SQLException iznimka
     * @throws IOException iznimka
     */
    public static List<Simptom> dohvatiSveSimptome(Connection veza) throws SQLException, IOException {
        List<Simptom> listaSimptoma = new ArrayList<>();

        Statement stmt = veza.createStatement();
        ResultSet resultSet = stmt.executeQuery("SELECT * FROM SIMPTOM");

        while (resultSet.next()) {
            long id = resultSet.getLong("ID");
            String naziv = resultSet.getString("NAZIV");
            String vrijednost = resultSet.getString("VRIJEDNOST");

            Simptom noviSimptom = new Simptom(id, naziv, vrijednost);
            listaSimptoma.add(noviSimptom);
        }

        return listaSimptoma;
    }

    /**
     * Dohvaća jedan simptom iz baze podataka prema njegovom ID-ju.
     * @param idSimptoma identifikator simptoma u bazi podataka
     * @return vraća jedan objekt tipa <code>Simptom</code>
     * @throws SQLException iznimka
     */
    public static Simptom dohvatiJedanSimptom(Connection veza, Long idSimptoma) throws SQLException {
        Simptom simptom = null;
        PreparedStatement prepared = veza.prepareStatement("SELECT * FROM SIMPTOM WHERE ID = ?");
        prepared.setLong(1, idSimptoma);
        ResultSet jedanSimptom = prepared.executeQuery();

        while (jedanSimptom.next()) {
            long id = jedanSimptom.getLong("ID");
            String naziv = jedanSimptom.getString("NAZIV");
            String vrijednost = jedanSimptom.getString("VRIJEDNOST");

            simptom = new Simptom(id, naziv, vrijednost);
        }

        return simptom;
    }

    /**
     * Dohvaća sve županije iz baze podataka.
     * @return vraća listu županija
     * @throws SQLException iznimka
     * @throws IOException iznimka
     */
    public static List<Zupanija> dohvatiSveZupanije(Connection veza) throws SQLException, IOException {
        List<Zupanija> listaZupanija = new ArrayList<>();

        Statement stmt = veza.createStatement();
        ResultSet resultSet = stmt.executeQuery("SELECT * FROM ZUPANIJA");

        while (resultSet.next()) {
            long id = resultSet.getLong("ID");
            String naziv = resultSet.getString("NAZIV");
            int brStan = resultSet.getInt("BROJ_STANOVNIKA");
            int brZarazenih = resultSet.getInt("BROJ_ZARAZENIH_STANOVNIKA");

            Zupanija novaZupanija = new Zupanija(id, naziv, brStan, brZarazenih);
            listaZupanija.add(novaZupanija);
        }


        return listaZupanija;
    }

    /**
     * Dohvaća jednu županiju iz baze podataka prema njenom ID-ju.
     * @param idZupanija identifikator županije u bazi podataka
     * @return vraća jedan objekt tipa <code>Zupanija</code>
     * @throws SQLException iznimka
     */
    public static Zupanija dohvatiJednuZupaniju(Connection veza, Long idZupanija) throws SQLException {
        Zupanija zupanija = null;
        PreparedStatement prepared = veza.prepareStatement("SELECT * FROM ZUPANIJA WHERE ID = ?");
        prepared.setLong(1, idZupanija);
        ResultSet jednaZupanija = prepared.executeQuery();

        while (jednaZupanija.next()) {
            long id = jednaZupanija.getLong("ID");
            String naziv = jednaZupanija.getString("NAZIV");
            int brStan = jednaZupanija.getInt("BROJ_STANOVNIKA");
            int brZarazenih = jednaZupanija.getInt("BROJ_ZARAZENIH_STANOVNIKA");

            zupanija = new Zupanija(id, naziv, brStan, brZarazenih);
        }

        return zupanija;
    }

    /**
     * Dohvaća sve bolesti iz baze podataka.
     * @return vraća listu bolesti
     * @throws SQLException iznimka
     * @throws IOException iznimka
     */
    public static List<Bolest> dohvatiSveBolesti(Connection veza) throws SQLException, IOException {
        List<Bolest> listaBolesti = new ArrayList<>();

        Statement stmtBolest = veza.createStatement();
        ResultSet resultSetBolest = stmtBolest.executeQuery("SELECT * FROM BOLEST");
        PreparedStatement preparedSimptomi =
                veza.prepareStatement("SELECT SIMPTOM_ID FROM BOLEST_SIMPTOM WHERE BOLEST_ID = ?");

        while (resultSetBolest.next()) {
            List<Simptom> listaSimptoma = new ArrayList<>();
            List<Long> simptomID = new ArrayList<>();
            long idBolesti = resultSetBolest.getLong("ID");
            String nazivBolesti = resultSetBolest.getString("NAZIV");
            boolean isVirus = resultSetBolest.getBoolean("VIRUS");

            preparedSimptomi.setLong(1, idBolesti);
            ResultSet resultSimptomi = preparedSimptomi.executeQuery();
            while (resultSimptomi.next()) {
                long simpID = resultSimptomi.getLong("SIMPTOM_ID");
                simptomID.add(simpID);
            }
            for (Long idSimp : simptomID) {
                Simptom simptom = dohvatiJedanSimptom(veza, idSimp);
                listaSimptoma.add(simptom);
            }

            Bolest novaBolest = new Bolest(idBolesti, nazivBolesti, listaSimptoma, isVirus);
            listaBolesti.add(novaBolest);
        }

        return listaBolesti;
    }

    /**
     * Dohvaća jednu bolest iz baze podataka prema njenom ID-ju.
     * @param idBolesti identifikator bolesti u bazi podataka
     * @return vraća jedan objekt tipa <code>Bolest</code>
     * @throws SQLException iznimka
     */
    public static Bolest dohvatiJednuBolest(Connection veza, Long idBolesti) throws SQLException {
        Bolest bolest = null;
        List<Simptom> listaSimptoma = new ArrayList<>();
        List<Long> simptomID = new ArrayList<>();

        PreparedStatement prepared = veza.prepareStatement("SELECT * FROM BOLEST WHERE ID = ?");
        prepared.setLong(1, idBolesti);
        ResultSet jednaBolest = prepared.executeQuery();

        //dohvacanje simptoma bolesti
        PreparedStatement preparedSimptomi =
                veza.prepareStatement("SELECT SIMPTOM_ID FROM BOLEST_SIMPTOM WHERE BOLEST_ID = ?");
        preparedSimptomi.setLong(1, idBolesti);
        ResultSet resultSimptomi = preparedSimptomi.executeQuery();
        while (resultSimptomi.next()) {
            long simpID = resultSimptomi.getLong("SIMPTOM_ID");
            simptomID.add(simpID);
        }
        for (Long idSimp : simptomID) {
            Simptom simptom = dohvatiJedanSimptom(veza, idSimp);
            listaSimptoma.add(simptom);
        }

        while (jednaBolest.next()) {
            long id = jednaBolest.getLong("ID");
            String naziv = jednaBolest.getString("NAZIV");
            boolean isVirus = jednaBolest.getBoolean("VIRUS");

            bolest = new Bolest(id, naziv, listaSimptoma, isVirus);
        }

        return bolest;
    }

    /**
     * Dohvaća sve osobe iz baze podataka.
     * @return vraća listu osoba
     * @throws SQLException iznimka
     * @throws IOException iznimka
     */
    public static List<Osoba> dohvatiSveOsobe(Connection veza) throws SQLException, IOException {
        List<Osoba> listaOsoba = new ArrayList<>();

        Statement stmtOsoba = veza.createStatement();
        ResultSet resultSetOsoba = stmtOsoba.executeQuery("SELECT * FROM OSOBA");
        PreparedStatement preparedKontakti =
                veza.prepareStatement("SELECT KONTAKTIRANA_OSOBA_ID FROM KONTAKTIRANE_OSOBE " +
                        "WHERE OSOBA_ID = ?");

        while (resultSetOsoba.next()) {
            List<Osoba> kontaktiraneOsobe = new ArrayList<>();
            List<Long> kontaktID = new ArrayList<>();
            long idOsobe = resultSetOsoba.getLong("ID");
            String ime = resultSetOsoba.getString("IME");
            String prezime = resultSetOsoba.getString("PREZIME");
            int starost = izracunajStarost(resultSetOsoba);
            Zupanija zupanija = dohvatiJednuZupaniju(veza, resultSetOsoba.getLong("ZUPANIJA_ID"));
            Bolest bolest = dohvatiJednuBolest(veza, resultSetOsoba.getLong("BOLEST_ID"));

            preparedKontakti.setLong(1, idOsobe);
            ResultSet resultKontakti = preparedKontakti.executeQuery();
            while (resultKontakti.next()) {
                long kontaktiLong = resultKontakti.getLong("KONTAKTIRANA_OSOBA_ID");
                kontaktID.add(kontaktiLong);
            }
            for (Long idOsob : kontaktID) {
                Osoba osoba = dohvatiJednuOsobu(veza, idOsob);
                kontaktiraneOsobe.add(osoba);
            }

            Osoba novaOsoba = new Osoba.Builder(idOsobe, ime, prezime)
                        .starost(starost)
                        .prebivaliste(zupanija)
                        .bolestan(bolest)
                        .kontakti(kontaktiraneOsobe)
                        .build();

            listaOsoba.add(novaOsoba);
        }

        return listaOsoba;

    }

    /**
     * Dohvaća jednu osobu iz baze podataka prema njenom ID-ju.
     * @param idOsoba identifikator osobe u bazi podataka
     * @return vraća jedan objekt tipa <code>Osoba</code>
     * @throws SQLException iznimka
     */
    public static Osoba dohvatiJednuOsobu(Connection veza, Long idOsoba) throws SQLException {
        Osoba osoba = null;
        List<Osoba> kontaktiraneOsobe = new ArrayList<>();
        List<Long> kontaktID = new ArrayList<>();

        PreparedStatement prepared = veza.prepareStatement("SELECT * FROM OSOBA WHERE ID = ?");
        prepared.setLong(1, idOsoba);
        ResultSet jednaOsoba = prepared.executeQuery();

        //kontakti osobe
        PreparedStatement preparedKontakti =
                veza.prepareStatement("SELECT KONTAKTIRANA_OSOBA_ID FROM KONTAKTIRANE_OSOBE " +
                        "WHERE OSOBA_ID = ?");
        preparedKontakti.setLong(1, idOsoba);
        ResultSet resultKontakti = preparedKontakti.executeQuery();
        while (resultKontakti.next()) {
            long osobID = resultKontakti.getLong("KONTAKTIRANA_OSOBA_ID");
            kontaktID.add(osobID);
        }
        for (Long idOsob : kontaktID) {
            Osoba kontakt = dohvatiJednuOsobu(veza, idOsob);
            kontaktiraneOsobe.add(kontakt);
        }

        while (jednaOsoba.next()) {
            long idOsobe = jednaOsoba.getLong("ID");
            String ime = jednaOsoba.getString("IME");
            String prezime = jednaOsoba.getString("PREZIME");
            int starost = izracunajStarost(jednaOsoba);
            Zupanija zupanija = dohvatiJednuZupaniju(veza, jednaOsoba.getLong("ZUPANIJA_ID"));
            Bolest bolest = dohvatiJednuBolest(veza, jednaOsoba.getLong("BOLEST_ID"));

            osoba = new Osoba.Builder(idOsobe, ime, prezime)
                    .starost(starost)
                    .prebivaliste(zupanija)
                    .bolestan(bolest)
                    .kontakti(kontaktiraneOsobe)
                    .build();
        }

        return osoba;
    }

    /**
     * Računa starost osobe u godinama prema datumu rođenja iz baze podataka
     * @param resultSetOsoba podaci o osobi iz baze podataka
     * @return vraća int vrijednost
     * @throws SQLException iznimka
     */
    private static int izracunajStarost(ResultSet resultSetOsoba) throws SQLException {
        Date date = resultSetOsoba.getDate("DATUM_RODJENJA");
        LocalDate rodjendan = date.toLocalDate();
        LocalDate danas = LocalDate.now();
        long differenceInDays = rodjendan.until(danas, ChronoUnit.DAYS);

        return (int) (differenceInDays / 365);

    }

    /**
     * Unosi jedan simptom u bazu podataka.
     * @param naziv podatak o nazivu simptoma
     * @param vrijednost podatak o vrijednosti simptoma
     * @throws SQLException iznimka
     * @throws IOException iznimka
     */
    public static void spremiNoviSimptom (Connection veza, String naziv, String vrijednost)
            throws SQLException, IOException {

        PreparedStatement upit = veza.prepareStatement("INSERT INTO SIMPTOM (NAZIV, VRIJEDNOST) VALUES (?, ?)");

        upit.setString(1, naziv);
        upit.setString(2, vrijednost);

        upit.executeUpdate();
    }

    /**
     * Unosi jednu županiju u bazu podataka.
     * @param naziv podatak o nazivu županije
     * @param brojStanovnika podatak o broju stanovnika u županiji
     * @param brojZarazenih podatak o broju stanovnika zaraženih Covid-19 virusom
     * @throws SQLException iznimka
     * @throws IOException iznimka
     */
    public static void spremiNovuZupaniju(Connection veza, String naziv, int brojStanovnika, int brojZarazenih)
            throws SQLException, IOException{

        PreparedStatement upit =
                veza.prepareStatement("INSERT INTO ZUPANIJA(NAZIV, BROJ_STANOVNIKA, BROJ_ZARAZENIH_STANOVNIKA)" +
                "VALUES (?, ?, ?)");

        upit.setString(1, naziv);
        upit.setInt(2, brojStanovnika);
        upit.setInt(3, brojZarazenih);

        upit.executeUpdate();
    }

    /**
     * Unosi jednu bolest ili virus u bazu podataka
     * @param naziv podatak o nazivu bolesti ili virusa
     * @param virus boolean vrijednost, označava je li bolest virus
     * @param simptomi lista simptoma koji pripadaju bolesti
     * @throws SQLException iznimka
     * @throws IOException iznimka
     */
    public static void spremiNovuBolest(Connection veza, String naziv, boolean virus, List<Long> simptomi)
            throws SQLException, IOException {

        Long idBolesti = null;

        PreparedStatement upit1 = veza.prepareStatement("INSERT INTO BOLEST(NAZIV, VIRUS) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS);
        PreparedStatement upit2 = veza.prepareStatement("INSERT INTO BOLEST_SIMPTOM(BOLEST_ID, SIMPTOM_ID)" +
                "VALUES (?, ?)");

        upit1.setString(1, naziv);
        upit1.setBoolean(2, virus);
        upit1.executeUpdate();

        ResultSet bolestID = upit1.getGeneratedKeys();
        if (bolestID.next()) {
            idBolesti = bolestID.getLong(1);
        }

        assert idBolesti != null;
        for (long id: simptomi) {
            upit2.setLong(1, idBolesti);
            upit2.setLong(2, id);
            upit2.executeUpdate();
        }

    }

    /**
     * Unosi jednu osobu u bazu podataka
     * @param ime podatak o imenu osobe
     * @param prezime podatak o imenu osobe
     * @param datumRodjenja podatak o datumu rođenja osobe
     * @param zupanija id županije u kojoj prebiva osoba
     * @param boluje id bolesti od koje boluje osoba
     * @param kontakti lista kontakata osobe
     * @throws SQLException iznimka
     * @throws IOException iznimka
     */
    public static void spremiNovuOsobu(Connection veza, String ime, String prezime, LocalDate datumRodjenja,
                                       long zupanija, long boluje, List<Long> kontakti)
            throws SQLException, IOException {

        Long idOsobe = null;

        PreparedStatement upitOsoba =
                veza.prepareStatement("INSERT INTO OSOBA(IME, PREZIME, DATUM_RODJENJA, ZUPANIJA_ID, BOLEST_ID) " +
                                "VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        PreparedStatement upitKontakti =
                veza.prepareStatement("INSERT INTO KONTAKTIRANE_OSOBE(OSOBA_ID, KONTAKTIRANA_OSOBA_ID)" +
                "VALUES (?, ?)");

        Date rodendan = Date.valueOf(datumRodjenja);

        upitOsoba.setString(1, ime);
        upitOsoba.setString(2, prezime);
        upitOsoba.setDate(3, rodendan);
        upitOsoba.setLong(4, zupanija);
        upitOsoba.setLong(5, boluje);
        upitOsoba.executeUpdate();

        ResultSet osobaID = upitOsoba.getGeneratedKeys();
        while (osobaID.next()) {
            idOsobe = osobaID.getLong(1);
        }

        assert idOsobe != null;
        for (long id : kontakti) {
            upitKontakti.setLong(1, idOsobe);
            upitKontakti.setLong(2, id);
            upitKontakti.executeUpdate();
        }

    }

}
