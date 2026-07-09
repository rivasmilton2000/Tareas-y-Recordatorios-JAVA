package factory;

import modelo.Usuario;
import modelo.UsuarioPremium;

public class UsuarioPremiumFactory extends UsuarioFactory {
    @Override
    public Usuario crearUsuario(int id, String nombre, String correo, String clave) {
        return new UsuarioPremium(id, nombre, correo, clave);
    }
}
