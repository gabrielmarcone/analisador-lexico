package minipascal.lexico.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Ponto de entrada da interface JavaFX. Responsabilidade única: carregar
 * o FXML, montar a Scene, aplicar o CSS e abrir a janela. Nenhuma lógica
 * de análise léxica entra aqui — isso é papel do AnalisadorController.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

        stage.setTitle("MINIPASCAL LEXICAL ANALYZER — Compiler Lab");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}