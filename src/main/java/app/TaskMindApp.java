package app;

import conexion.ConexionBD;
import java.io.IOException;
import controlador.AppState;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TaskMindApp extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/FXML/Inicio/Main.fxml"));
        Scene scene = ResponsiveScenes.create(root);

        stage.setTitle("TaskMind");
        stage.setMinWidth(960);
        stage.setMinHeight(640);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        AppState.getInstance().cerrarSesion();
        ConexionBD.cerrarConexion();
    }
}
