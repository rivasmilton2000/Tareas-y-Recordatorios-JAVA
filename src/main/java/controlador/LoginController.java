package controlador;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import modelo.Usuario;

public class LoginController {
    @FXML
    private TextField txtCorreo;
    @FXML
    private PasswordField txtPassword;

    @FXML
    private void iniciarSesion(ActionEvent event) {
        String correo = txtCorreo.getText().trim();
        String clave = txtPassword.getText().trim();

        if (correo.isEmpty() || clave.isEmpty()) {
            Alertas.error("Datos incompletos", "Ingrese correo y contrasena.");
            return;
        }

        Usuario usuario = AppState.getInstance().getGestorUsuarios().iniciarSesion(correo, clave);
        if (usuario == null) {
            Alertas.error("Inicio de sesion", "Credenciales incorrectas o base de datos no disponible.");
            return;
        }

        AppState.getInstance().setUsuarioActual(usuario);
        Navegacion.cambiar((Node) event.getSource(), "/FXML/Actividad/Menu.fxml");
    }

    @FXML
    private void irRegistro(MouseEvent event) {
        Navegacion.cambiar((Node) event.getSource(), "/FXML/Inicio/Registrarse.fxml");
    }
}
