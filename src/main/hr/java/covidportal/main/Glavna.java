package main.hr.java.covidportal.main;

import main.hr.java.covidportal.enums.*;
import main.hr.java.covidportal.genericsi.*;
import main.hr.java.covidportal.model.*;
import main.hr.java.covidportal.sortiranje.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Služi za pokretanje programa. Sadrži main metodu, kao i sve ostale
 * metode potrebne za pravilno funkcioniranje programa.
 */
public class Glavna {

    private static final Logger logger = LoggerFactory.getLogger(Glavna.class);

    /**
     * Služi za pokretanje programa koji učitava podatke o županijama, simptomima,
     * bolestima, virusima i osobama iz tekstualnih datoteka, manipulira njima i ispisuje ih u konzolu.
     * @param args argumenti komandne linije
     */
    public static void main(String[] args) {

        Scanner unos = new Scanner(System.in);

        logger.info("Program je pokrenut.");

        System.out.println("Učitavanje podataka o županijama...");
        List<Zupanija> popisZupanija = new ArrayList<>();
        ucitavanjeZupanija(popisZupanija);
        logger.info("Uneseni su podaci o svim županijama.");

        System.out.println("Učitavanje podataka o simptomima...");
        List<Simptom> popisSimptoma = new ArrayList<>();
        ucitavanjeSimptoma(popisSimptoma);
        logger.info("Uneseni su podaci o svim simptomima.");

        List<Virus> sviVirusi = new ArrayList<>();
        List<Osoba> sveVirusneeOsobe = new ArrayList<>();
        KlinikaZaInfektivneBolesti<Virus, Osoba> klinika =
                new KlinikaZaInfektivneBolesti<>(sviVirusi, sveVirusneeOsobe);

        System.out.println("Učitavanje podataka o bolestima...");
        List<Bolest> popisBolesti = new ArrayList<>();
        ucitavanjeBolesti(popisSimptoma, popisBolesti);
        System.out.println("Učitavanje podataka o virusima...");
        ucitavanjeVirusa(popisSimptoma, popisBolesti, klinika);
        logger.info("Uneseni su podaci o svim bolestima i virusima.");

        System.out.println("Učitavanje osoba...");
        List<Osoba> popisOsoba = new ArrayList<>();
        ucitavanjeOsoba(popisZupanija, popisBolesti, popisOsoba);
        logger.info("Uneseni su podaci o svim osobama.");

        ispisOsoba(popisOsoba);

        Map<Bolest, List<Osoba>> bolesneOsobe = new HashMap<>();
        Map<Virus, List<Osoba>> virusneOsobe = new HashMap<>();
        odredi(popisOsoba, bolesneOsobe, virusneOsobe);

        if (!bolesneOsobe.isEmpty())
            ispisiBolesne(bolesneOsobe);
        if (!virusneOsobe.isEmpty())
            ispisiVirusne(virusneOsobe);

        TreeSet<Zupanija> najZarazeni = new TreeSet<>(new CovidSorter());
        najZarazeni.addAll(popisZupanija);
        System.out.println("Najviše zaraženih osoba ima u županiji " + najZarazeni.last().getNaziv() +
                ": " + najZarazeni.last().getPostotakZaraze() + "%.");

        poredajIspisiLambda(klinika);
        poredajCollections(klinika);
        mjerenjeVremena(klinika);

        pretraziPrezime(unos, popisOsoba);

        ispisiBrojSimptoma(popisBolesti);

        serijalizacija(popisZupanija);

        logger.info("Program je završen.");

    }

    /**
     * Serijalizira sve objekte klase <code>Zupanija</code> u kojima je postotak zaraženosti stanovnika veći od 2%
     * @param popisZupanija lista objekata klase <code>Županija</code> koja sadrži sve unesene županije
     */
    private static void serijalizacija(List<Zupanija> popisZupanija) {
        List<Zupanija> serial = popisZupanija.stream()
                .filter(zupanija -> zupanija.getPostotakZaraze() > 2)
                .collect(Collectors.toList());

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("dat\\serijalizirane.dat"))) {
            for (Zupanija zup: serial) {
                out.writeObject(zup);
            }
        }
        catch (IOException exc) {
            exc.printStackTrace();
            logger.error(exc.getMessage());
        }
    }

    /**
     * Ispisuje sve podatke svih unesenih osoba.
     *
     * @param osobe lista objetaka klase <code>Osoba</code>
     */
    private static void ispisOsoba(List<Osoba> osobe) {
        System.out.println("Popis osoba: ");
        for(Osoba osoba : osobe) {
            System.out.println("Ime i prezime: " + osoba.getIme() + " " + osoba.getPrezime());
            System.out.println("Starost: " + osoba.getStarost());
            Zupanija prebivaliste = osoba.getZupanija();
            System.out.println("Županija prebivališta: " + prebivaliste.getNaziv());
            Bolest bolescina = osoba.getZarazenBolescu();
            System.out.println("Zaražen bolešću: " + bolescina.getNaziv());
            System.out.println("Kontaktirane osobe: ");
            List<Osoba> kontakti = osoba.getKontaktiraneOsobe();
            if(kontakti.isEmpty()) {
                System.out.println("Nema kontaktiranih osoba.");
            }
            else {
                for (Osoba kontakt : kontakti) {
                    System.out.println(kontakt.getIme() + " " + kontakt.getPrezime());
                }
            }
        }
    }

    /**
     * Za svaku unesenu bolest i virus ispisuje broj njenih simptoma
     * @param bolesti lista objekata klase <code>Bolest</code> sa svim unesenim bolestima i virusima
     */
    private static void ispisiBrojSimptoma(List<Bolest> bolesti) {
        bolesti.stream()
                .map(bolest -> bolest.getNaziv() + " ima " + bolest.getSimptomi().size() + " simptoma. ")
                .forEach(System.out::println);
    }

    /**
     * Traži od korisnika unos stringa, te pretražuje postoji li u listi unesenih osoba prezime koje sadrži taj string
     * @param unos podatak o korisničkom unosu
     * @param osobe lista objekata klase <code>Osoba</code> koja sadrži sve unesene osobe
     */
    private static void pretraziPrezime(Scanner unos, List<Osoba> osobe) {
        System.out.print("Unesite string za pretragu po prezimenu: ");
        String uvjet = unos.nextLine();

        List<Osoba> poredaniLjudi = osobe.stream()
                .filter(osoba -> osoba.getPrezime().contains(uvjet))
                .collect(Collectors.toList());

        Optional<Osoba> imaKoga = poredaniLjudi.stream().findFirst();
        if (imaKoga.isEmpty()) {
            System.out.println("Ne postoji prezime koje sadrži '" + uvjet + "'.");
            System.out.println(imaKoga);
        }
        else {
            System.out.println("Osobe čije prezime sadrži '" + uvjet + "' su sljedeće:");
            poredaniLjudi.forEach(System.out::println);
        }
    }

    /**
     * Mjeri i ispisuje vrijeme potrebno za sortiranje liste korištenjem lambda izraza i Collections.sort metode
     * @param klinika instanca klase <code>KlinikaZaInfektivneBolesti</code> unutar koje su spremljeni
     *      *      *                dosad uneseni virusi i osobe koje su njima zaražene
     */
    @SuppressWarnings("unused")
    private static void mjerenjeVremena(KlinikaZaInfektivneBolesti<Virus, Osoba> klinika) {
        Instant startLambda = Instant.now();
        List<Virus> poredani = klinika.getVirusi().stream()
                .sorted(Comparator.comparing(Virus::getNaziv).reversed())
                .collect(Collectors.toList());
        Instant endLambda = Instant.now();

        List<Virus> poredak = new ArrayList<>(klinika.getVirusi());
        Instant startClassic = Instant.now();
        poredak.sort(new VirusNameSorter());
        Instant endClassic = Instant.now();

        System.out.println("Sortiranje objekata korištenjem labmdi traje " +
                Duration.between(startLambda, endLambda).toMillis() +
                " milisekundi, a bez lambda traje " +
                Duration.between(startClassic, endClassic).toMillis() + " milisekundi.");
    }

    /**
     * Sortira sve unesene viruse prema nazivu suprotno od poretka abecede
     * @param klinika instanca klase <code>KlinikaZaInfektivneBolesti</code> unutar koje su spremljeni
     *      *                dosad uneseni virusi i osobe koje su njima zaražene
     */
    private static void poredajCollections(KlinikaZaInfektivneBolesti<Virus, Osoba> klinika) {
        List<Virus> poredak = new ArrayList<>(klinika.getVirusi());
        poredak.sort(new VirusNameSorter());
    }

    /**
     * Sortira sve unesene viruse prema nazivu suprotno od poretka abecede korištenjem lambda izraza
     * @param klinika instanca klase <code>KlinikaZaInfektivneBolesti</code> unutar koje su spremljeni
     *                dosad uneseni virusi i osobe koje su njima zaražene
     */
    private static void poredajIspisiLambda(KlinikaZaInfektivneBolesti<Virus, Osoba> klinika) {
        List<Virus> poredani = klinika.getVirusi().stream()
                .sorted(Comparator.comparing(Virus::getNaziv).reversed())
                .collect(Collectors.toList());


        int i = 1;
        for (Virus virusic: poredani) {
            System.out.println(i + ". " + virusic.getNaziv());
            i++;
        }
    }

    /**
     * Ispisuje sve osobe zaražene virusima.
     * @param virusneOsobe mapa svih osoba zaraženih nekim virusom
     */
    private static void ispisiVirusne(Map<Virus, List<Osoba>> virusneOsobe) {
        for (Virus virus : virusneOsobe.keySet()) {
            List<Osoba> popis = virusneOsobe.get(virus);
            System.out.print("Od virusa " + virus.getNaziv() + " boluju: ");
            List<String> list = popis.stream().map(Osoba::getImePrezime).collect(Collectors.toList());
            String result = String.join(", ", list);
            System.out.print(result);
        }
        System.out.println();
    }

    /**
     * Ispisuje sve osobe zaražene bolestima.
     * @param bolesneOsobe mapa svih osoba zaraženih nekom bolešću
     */
    private static void ispisiBolesne(Map<Bolest, List<Osoba>> bolesneOsobe) {
        for (Bolest bolest : bolesneOsobe.keySet()) {
            List<Osoba> popis = bolesneOsobe.get(bolest);
            System.out.print("Od bolesti " + bolest.getNaziv() + " boluju: ");
            List<String> list = popis.stream().map(Osoba::getImePrezime).collect(Collectors.toList());
            String result = String.join(", ", list);
            System.out.print(result);
        }
        System.out.println();
    }

    /**
     * Određuje koje osobe su zaražene nekom bolešću, a koje nekim virusom, te ih prema tome sprema u mapu u kojoj
     * je ključ određen nazivom bolesti ili virusa, a vrijednost listom osoba koje su zaražene tom bolešću.
     * @param osobe lista svih osoba
     * @param bolesneOsobe mapa svih osoba zaraženih nekom bolešću
     * @param virusneOsobe mapa svih osoba zaraženih nekim virusom
     */
    private static void odredi(List<Osoba> osobe, Map<Bolest, List<Osoba>> bolesneOsobe,
                               Map<Virus, List<Osoba>> virusneOsobe) {
        List<Osoba> popis;
        for(Osoba osoba : osobe) {
            if(osoba.getZarazenBolescu() instanceof Bolest bolest) {
                if (bolesneOsobe.containsKey(bolest)) {
                    popis = bolesneOsobe.get(bolest);
                    popis.add(osoba);
                }
                else {
                    popis = new ArrayList<>();
                    popis.add(osoba);
                    bolesneOsobe.put(bolest, popis);
                }
            }
            else //noinspection ConstantConditions
                if (osoba.getZarazenBolescu() instanceof Virus virus){
                if (virusneOsobe.containsKey(virus)) {
                    popis = virusneOsobe.get(virus);
                    popis.add(osoba);
                }
                else {
                    popis = new ArrayList<>();
                    popis.add(osoba);
                    virusneOsobe.put(virus, popis);
                }

            }
        }
    }

    /**
     * Učitava podatke o svim osobama iz tekstualne datoteke osobe.txt.
     * @param popisZupanija lista objekata klase <code>Županija</code> koja sadrži sve unesene županije
     * @param popisBolestiVirusa lista objekata klase <code>Bolest</code> koja sadrži sve unesene bolesti i viruse
     * @param popisOsoba lista objekata klase <code>Osoba</code> u koju se spremaju podaci pročitani iz datoteke
     * @return vraća parametar <code>popisOsoba</code>
     */
    public static List<Osoba> ucitavanjeOsoba(List<Zupanija> popisZupanija, List<Bolest> popisBolestiVirusa,
                                              List<Osoba> popisOsoba) {
        File osobe = new File("dat\\osobe.txt");
        try (FileReader fileReader = new FileReader(osobe);
             BufferedReader reader = new BufferedReader(fileReader)) {
            String procitanaLinija;
            while((procitanaLinija = reader.readLine()) != null) {
                long id = Long.parseLong(procitanaLinija);
                String imeOsobe = reader.readLine();
                String prezimeOsobe = reader.readLine();
                int starostOsobe = Integer.parseInt(reader.readLine());
                long idZupanija = Long.parseLong(reader.readLine());
                long idBolest = Long.parseLong(reader.readLine());
                String kontakti = reader.readLine();

                List<Osoba> kontaktirani  = new ArrayList<>();
                if (popisOsoba.isEmpty()) {
                    kontaktirani = Collections.emptyList();
                }
                else {
                    List<Long> numID = new ArrayList<>();
                    for (int i = 0; i < kontakti.length(); i++){
                        numID.add((long) (kontakti.charAt(i) - '0'));
                    }
                    for (long i : numID) {
                        for (Osoba kont : popisOsoba) {
                            if (kont.getId() == i) {
                                kontaktirani.add(kont);
                            }
                        }
                    }
                }

                Osoba nova = new Osoba.Builder(id, imeOsobe, prezimeOsobe)
                        .starost(starostOsobe)
                        .prebivaliste(popisZupanija.stream()
                                .filter(zupanija -> zupanija.getId() == idZupanija)
                                .findAny()
                                .orElse(null))
                        .bolestan(popisBolestiVirusa.stream()
                                .filter(bolest -> bolest.getId() == idBolest)
                                .findAny()
                                .orElse(null))
                        .kontakti(kontaktirani)
                        .build();

                popisOsoba.add(nova);
            }

        }
        catch (IOException exc) {
            exc.printStackTrace();
            logger.error(exc.getMessage());
        }

        return popisOsoba;
    }

    /**
     * Učitava podatke o svim virusima iz tekstualne datoteke virusi.txt.
     * @param popisSimptoma lista objekata klase <code>Simptom</code> koja sadrži sve unesene simptome
     * @param popisBolestiVirusa lista objekata klase <code>Bolest</code> koja sadrži sve unesene bolesti i u koju
     *                           se spremaju podaci pročitani iz datoteke
     * @param klinika instanca klase <code>KlinikaZaInfektivneBolesti</code> unutar koje su spremljeni
     *                dosad uneseni virusi i osobe koje su njima zaražene
     * @return vraća parametar <code>popisBolestiVirusa</code>
     */
    public static List<Bolest> ucitavanjeVirusa(List<Simptom> popisSimptoma, List<Bolest> popisBolestiVirusa,
                                                KlinikaZaInfektivneBolesti<Virus, Osoba> klinika) {
        File virusi = new File("dat\\virusi.txt");
        try (FileReader fileReader = new FileReader(virusi);
             BufferedReader reader = new BufferedReader(fileReader)) {
            String procitanaLinija;
            while((procitanaLinija = reader.readLine()) != null) {
                long id = popisBolestiVirusa.size() + 1;
                String nazivVirusa = procitanaLinija;
                String simptomID = reader.readLine();

                List<Simptom> trenutni  = new ArrayList<>();
                List<Long> numID = new ArrayList<>();
                for (int i = 0; i < simptomID.length(); i++){
                    numID.add((long) (simptomID.charAt(i) - '0'));
                }

                for (long i : numID) {
                    for (Simptom simptom : popisSimptoma) {
                        if (simptom.getId() == i) {
                            trenutni.add(simptom);
                        }
                    }
                }
                popisBolestiVirusa.add(new Virus(id, nazivVirusa, trenutni, true));
                klinika.dodajVirus(new Virus(id, nazivVirusa, trenutni, true));
            }

        }
        catch (IOException exc) {
            exc.printStackTrace();
            logger.error(exc.getMessage());
        }

        return popisBolestiVirusa;
    }

    /**
     * Učitava podatke o svim bolestima iz tekstualne datoteke bolesti.txt.
     * @param popisSimptoma lista objekata klase <code>Simptom</code> koja sadrži sve unesene simptome
     * @param popisBolestiVirusa lista objekata klase <code>Bolest</code> u koju se spremaju podaci pročitani
     *                           iz datoteke
     * @return vraća parametar <code>popisBolestiVirusa</code>
     */
    public static List<Bolest> ucitavanjeBolesti(List<Simptom> popisSimptoma, List<Bolest> popisBolestiVirusa) {
        File bolesti = new File("dat\\bolesti.txt");
        try (FileReader fileReader = new FileReader(bolesti);
             BufferedReader reader = new BufferedReader(fileReader)) {
            String procitanaLinija;
            while((procitanaLinija = reader.readLine()) != null) {
                long id = Long.parseLong(procitanaLinija);
                String nazivBolesti = reader.readLine();
                String simptomID = reader.readLine();

                List<Simptom> trenutni  = new ArrayList<>();
                List<Long> numID = new ArrayList<>();
                for (int i = 0; i < simptomID.length(); i++){
                    numID.add((long) (simptomID.charAt(i) - '0'));
                }

                for (long i : numID) {
                    for (Simptom simptom : popisSimptoma) {
                        if (simptom.getId() == i) {
                            trenutni.add(simptom);
                        }
                    }
                }
                popisBolestiVirusa.add(new Bolest(id, nazivBolesti, trenutni, false));
            }

        }
        catch (IOException exc) {
            exc.printStackTrace();
            logger.error(exc.getMessage());
        }

        return popisBolestiVirusa;
    }

    /**
     * Učitava podatke o svim simptomima iz tekstualne datoteke simptomi.txt.
     * @param popisSimptoma lista objekata klase <code>Simptom</code> u koju se spremaju podaci pročitani
     *                      iz datoteke
     * @return vraća parametar <code>popisSimptoma</code>
     */
    public static List<Simptom> ucitavanjeSimptoma(List<Simptom> popisSimptoma) {

        File simptomi = new File("dat\\simptomi.txt");
        try (FileReader fileReader = new FileReader(simptomi);
             BufferedReader reader = new BufferedReader(fileReader)) {
            String procitanaLinija;
            while((procitanaLinija = reader.readLine()) != null) {
                long id = Long.parseLong(procitanaLinija);
                String nazivSimptoma = reader.readLine();
                VrijednostiSimptoma vrij = VrijednostiSimptoma.valueOf(reader.readLine());
                String vrijednost = vrij.getVrijednost();
                popisSimptoma.add(new Simptom(id, nazivSimptoma, vrijednost));
            }

        }
        catch (IOException exc) {
            exc.printStackTrace();
            logger.error(exc.getMessage());
        }

        return popisSimptoma;
    }

    /**
     * Učitava podatke o svim županijama iz tekstualne datoteke zupanije.txt.
     * @param popisZupanija lista objekata klase <code>Županija</code> u koju se spremaju podaci pročitani
     *                      iz datoteke
     * @return vraća parametar <code>popisZupanija</code>
     */
    public static List<Zupanija> ucitavanjeZupanija(List<Zupanija> popisZupanija) {

        File zupanije = new File("dat\\zupanije.txt");
        try (FileReader fileReader = new FileReader(zupanije);
             BufferedReader reader = new BufferedReader(fileReader)) {
            String procitanaLinija;
            while((procitanaLinija = reader.readLine()) != null) {
                long id = Long.parseLong(procitanaLinija);
                String nazivZupanije = reader.readLine();
                int brStanovnika = Integer.parseInt(reader.readLine());
                int brZarazenih = Integer.parseInt(reader.readLine());
                popisZupanija.add(new Zupanija(id, nazivZupanije, brStanovnika, brZarazenih));
            }

        }
        catch (IOException exc) {
            exc.printStackTrace();
            logger.error(exc.getMessage());
        }

        return popisZupanija;
    }

}

