package controlador;

import app.ResponsiveScenes;
import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class Navegacion {
    private Navegacion() {
    }

    public static void cambiar(Node origen, String rutaFxml) {
        try {
            URL recurso = Navegacion.class.getResource(rutaFxml);
            if (recurso == null) {
                throw new IllegalArgumentException("No se encontro el FXML: " + rutaFxml);
            }
            Parent root = FXMLLoader.load(recurso);
            Stage stage = (Stage) origen.getScene().getWindow();
            Scene escenaActual = stage.getScene();
            double width = escenaActual != null ? escenaActual.getWidth() : stage.getWidth();
            double height = escenaActual != null ? escenaActual.getHeight() : stage.getHeight();
            stage.setScene(ResponsiveScenes.create(root, width, height));
            stage.show();
        } catch (IOException | IllegalArgumentException e) {
            Alertas.error("No se pudo abrir la pantalla", e.getMessage());
        }
    }
}
