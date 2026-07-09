package factory;

import modelo.Usuario;
import modelo.UsuarioClasico;

public class UsuarioClasicoFactory extends UsuarioFactory {
    @Override
    public Usuario crearUsuario(int id, String nombre, String correo, String clave) {
        return new UsuarioClasico(id, nombre, correo, clave);
    }
}
