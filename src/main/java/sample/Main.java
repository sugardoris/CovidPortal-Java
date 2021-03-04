package main.java.sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.database.Database;
import main.hr.java.covidportal.model.Zupanija;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Služi za pokretanje programa. Sadrži main metodu, postavlja početni ekran aplikacije
 * i ostvaruje vezu s bazom podataka.
 */

public class Main extends Application {

    private static Stage mainStage;

    public static Connection veza;

    static {
        try {
            veza = Database.connectToDatabase();
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainStage = primaryStage;
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("pocetniEkran.fxml"));
        Parent root = loader.load();
        mainStage.setTitle("Covid aplikacija");
        mainStage.setScene(new Scene(root, 700, 500));
        mainStage.show();

        //za naslov aplikacije postavlja naziv najzaraženije županije
        prikazNajzarazenijeZupanije();

        //prilikom zatvaranja aplikacije, zatvara vezu s bazom podataka i zaustavlja sve niti
        primaryStage.setOnCloseRequest(e -> {
            try {
                Database.closeConnectionToDatabase(veza);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            Platform.exit();
            System.exit(0);
        });
    }

    public static Stage getMainStage() {
        return mainStage;
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Dohvaća županiju s najvećim brojem zaraženih.
     * @return vraća objekt klase Zupanija
     */
    public Zupanija najzarazenijaZupanija() {
        List<Zupanija> zupanije = new ArrayList<>();

        try {
            zupanije = Database.dohvatiSveZupanije(veza);
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }

        return zupanije
                .stream()
                .max(Comparator.comparingDouble(Zupanija::getPostotakZaraze)).orElse(null);
    }

    /**
     * Postavlja naziv županije s najvećim brojem zaraženih kao naslov aplikacije.
     * Naslov se osvježava svakih 10 sekundi.
     */
    public void prikazNajzarazenijeZupanije() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(10), e ->
                        mainStage.setTitle("Najzaraženija: " + najzarazenijaZupanija().getNaziv()))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}
