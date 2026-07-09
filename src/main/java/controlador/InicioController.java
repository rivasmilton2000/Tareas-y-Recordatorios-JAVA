package controlador;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;

public class InicioController {
    @FXML
    private void irRegistro(ActionEvent event) {
        Navegacion.cambiar((Node) event.getSource(), "/FXML/Inicio/Registrarse.fxml");
    }

    @FXML
    private void irLogin(ActionEvent event) {
        Navegacion.cambiar((Node) event.getSource(), "/FXML/Inicio/IniciarSesion.fxml");
    }
}
