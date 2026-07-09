package factory;

import modelo.Usuario;

public abstract class UsuarioFactory {
    public abstract Usuario crearUsuario(int id, String nombre, String correo, String clave);
}
