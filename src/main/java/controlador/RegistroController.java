package controlador;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import modelo.Usuario;

public class RegistroController {
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtCorreo;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private ComboBox<String> cmbTipoUsuario;

    @FXML
    private void registrar(ActionEvent event) {
        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();
        String clave = txtPassword.getText().trim();
        String tipo = cmbTipoUsuario.getValue();

        if (nombre.isEmpty() || correo.isEmpty() || clave.isEmpty() || tipo == null) {
            Alertas.error("Datos incompletos", "Complete nombre, correo, contrasena y tipo de usuario.");
            return;
        }

        Usuario usuario = AppState.getInstance().getGestorUsuarios()
                .registrarUsuario(tipo, nombre, correo, clave);
        if (usuario == null) {
            Alertas.error("Registro", "No se pudo crear la cuenta. Revise la base de datos o si el correo ya existe.");
            return;
        }

        AppState.getInstance().setUsuarioActual(usuario);
        Navegacion.cambiar((Node) event.getSource(), "/FXML/Actividad/Menu.fxml");
    }

    @FXML
    private void irLogin(MouseEvent event) {
        Navegacion.cambiar((Node) event.getSource(), "/FXML/Inicio/IniciarSesion.fxml");
    }
}
