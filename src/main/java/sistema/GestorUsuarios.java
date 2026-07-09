package sistema;

import dao.UsuarioDAO;
import factory.UsuarioClasicoFactory;
import factory.UsuarioFactory;
import factory.UsuarioPremiumFactory;
import modelo.Usuario;

public class GestorUsuarios {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Usuario registrarUsuario(String tipo, String nombre, String correo, String clave) {
        UsuarioFactory factory = "premium".equalsIgnoreCase(tipo)
                ? new UsuarioPremiumFactory()
                : new UsuarioClasicoFactory();
        Usuario usuario = factory.crearUsuario(0, nombre, correo, clave);
        if (usuarioDAO.guardarUsuario(usuario)) {
            return usuario;
        }
        return null;
    }

    public Usuario iniciarSesion(String correo, String clave) {
        Usuario usuario = usuarioDAO.buscarUsuarioPorCorreo(correo);
        if (usuario != null && usuario.iniciarSesion(correo, clave)) {
            return usuario;
        }
        return null;
    }

    public Usuario buscarUsuario(String correo) {
        return usuarioDAO.buscarUsuarioPorCorreo(correo);
    }

    public String obtenerTipo(Usuario usuario) {
        if (usuario == null) {
            return "";
        }
        return usuario.getTipo();
    }

    public boolean hayUsuariosRegistrados() {
        return usuarioDAO.existenUsuarios();
    }
}
