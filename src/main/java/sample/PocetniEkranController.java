package main.java.sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.scene.control.Label;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pokreće razne funkcionalnosti aplikacije preko početnog ekrana.
 */
public class PocetniEkranController implements Initializable {

    private static final Logger loggerNew = LoggerFactory.getLogger(PocetniEkranController.class);

    @FXML
    private Label vrijeme = new Label();
    @FXML
    private Label datum = new Label();

    /**
     * Prikazuje ekran za pretragu županija.
     * @throws IOException iznimka
     */
    @FXML
    public void prikaziEkranZaPretraguZupanija() throws IOException {
        loggerNew.info("Prikazan je ekran za pretragu zupanija.");
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("pretragaZupanija.fxml"));
        Parent pretragaZupanijaFrame = loader.load();
        Scene pretragaZupanijaScene = new Scene(pretragaZupanijaFrame, 700, 500);
        Main.getMainStage().setScene(pretragaZupanijaScene);
    }

    /**
     * Prikazuje ekran za pretragu simptoma.
     * @throws IOException iznimka
     */
    @FXML
    public void prikaziEkranZaPretraguSimptoma() throws IOException {
        loggerNew.info("Prikazan je ekran za pretragu simptoma.");
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("pretragaSimptoma.fxml"));
        Parent pretragaSimptomaFrame = loader.load();
        Scene pretragaSimptomaScene = new Scene(pretragaSimptomaFrame, 700, 500);
        Main.getMainStage().setScene(pretragaSimptomaScene);
    }

    /**
     * Prikazuje ekran za pretragu bolesti.
     * @throws IOException iznimka
     */
    @FXML
    public void prikaziEkranZaPretraguBolesti() throws IOException {
        loggerNew.info("Prikazan je ekran za pretragu bolesti.");
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("pretragaBolesti.fxml"));
        Parent pretragaBolestiFrame = loader.load();
        Scene pretragaBolestiScene = new Scene(pretragaBolestiFrame, 700, 500);
        Main.getMainStage().setScene(pretragaBolestiScene);
    }

    /**
     * Prikazuje ekran za pretragu virusa.
     * @throws IOException iznimka
     */
    @FXML
    public void prikaziEkranZaPretraguVirusa() throws IOException {
        loggerNew.info("Prikazan je ekran za pretragu virusa.");
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("pretragaVirusa.fxml"));
        Parent pretragaVirusaFrame = loader.load();
        Scene pretragaVirusaScene = new Scene(pretragaVirusaFrame, 700, 500);
        Main.getMainStage().setScene(pretragaVirusaScene);
    }

    /**
     * Prikazuje ekran za pretragu osoba.
     * @throws IOException iznimka
     */
    @FXML
    public void prikaziEkranZaPretraguOsoba() throws IOException {
        loggerNew.info("Prikazan je ekran za pretragu osoba.");
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("pretragaOsoba.fxml"));
        Parent pretragaOsobaFrame = loader.load();
        Scene pretragaOsobaScene = new Scene(pretragaOsobaFrame, 700, 500);
        Main.getMainStage().setScene(pretragaOsobaScene);
    }

    /**
     * Prikazuje ekran za unos nove županije.
     * @throws IOException iznimka
     */
    @FXML
    public void prikaziEkranZaNovuZupaniju() throws IOException {
        loggerNew.info("Prikazan je ekran za unos nove županije.");
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("dodavanjeNoveZupanije.fxml"));
        Parent unosZupanijeFrame = loader.load();
        Scene unosZupanijeScene = new Scene(unosZupanijeFrame, 700, 500);
        Main.getMainStage().setScene(unosZupanijeScene);
    }

    /**
     * Prikazuje ekran za unos novog simptoma.
     * @throws IOException iznimka
     */
    @FXML
    public void prikaziEkranZaNoviSimptom() throws IOException {
        loggerNew.info("Prikazan je ekran za unos novog simptoma.");
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("dodavanjeNovogSimptoma.fxml"));
        Parent unosSimptomaFrame = loader.load();
        Scene unosSimptomaScene = new Scene(unosSimptomaFrame, 700, 500);
        Main.getMainStage().setScene(unosSimptomaScene);
    }

    /**
     * Prikazuje ekran za unos nove bolesti.
     * @throws IOException iznimka
     */
    @FXML
    public void prikaziEkranZaNovuBolest() throws IOException {
        loggerNew.info("Prikazan je ekran za unos nove bolesti.");
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("dodavanjeNoveBolesti.fxml"));
        Parent unosBolestiFrame = loader.load();
        Scene unosBolestiScene = new Scene(unosBolestiFrame, 700, 500);
        Main.getMainStage().setScene(unosBolestiScene);
    }

    /**
     * Prikazuje ekran za unos novog virusa.
     * @throws IOException iznimka
     */
    @FXML
    public void prikaziEkranZaNoviVirus() throws IOException {
        loggerNew.info("Prikazan je ekran za unos novog virusa.");
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("dodavanjeNovogVirusa.fxml"));
        Parent unosVirusaFrame = loader.load();
        Scene unosVirusaScene = new Scene(unosVirusaFrame, 700, 500);
        Main.getMainStage().setScene(unosVirusaScene);
    }

    /**
     * Prikazuje ekran za unos nove osobe.
     * @throws IOException iznimka
     */
    @FXML
    public void prikaziEkranZaNovuOsobu() throws IOException {
        loggerNew.info("Prikazan je ekran za unos nove osobe.");
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("dodavanjeNoveOsobe.fxml"));
        Parent unosOsobeFrame = loader.load();
        Scene unosOsobeScene = new Scene(unosOsobeFrame, 700, 500);
        Main.getMainStage().setScene(unosOsobeScene);
    }

public void vrijemeDatum() {
    Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> {
                LocalTime time = LocalTime.now();
                LocalDate date = LocalDate.now();
                vrijeme.setText(time.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                datum.setText(date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            })
    );
    timeline.setCycleCount(Animation.INDEFINITE);
    timeline.play();
}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        vrijemeDatum();
    }
}
