package controlador;

import java.awt.Toolkit;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public final class Alertas {
    private Alertas() {
    }

    public static void info(String titulo, String mensaje) {
        mostrar(Alert.AlertType.INFORMATION, titulo, mensaje);
    }

    public static void error(String titulo, String mensaje) {
        mostrar(Alert.AlertType.ERROR, titulo, mensaje);
    }

    public static void notificacion(String tituloVentana, String titulo, String mensaje) {
        Runnable mostrarNotificacion = () -> {
            try {
                Toolkit.getDefaultToolkit().beep();
            } catch (Throwable ignored) {
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(tituloVentana);
            alert.setHeaderText(titulo);
            alert.setContentText(mensaje);
            alert.show();
            if (alert.getDialogPane().getScene() != null && alert.getDialogPane().getScene().getWindow() != null) {
                alert.getDialogPane().getScene().getWindow().requestFocus();
            }
        };

        if (Platform.isFxApplicationThread()) {
            mostrarNotificacion.run();
        } else {
            Platform.runLater(mostrarNotificacion);
        }
    }

    private static void mostrar(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
