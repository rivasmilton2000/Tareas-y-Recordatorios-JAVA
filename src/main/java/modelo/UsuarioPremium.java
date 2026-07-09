package modelo;

public class UsuarioPremium extends Usuario {
    public UsuarioPremium(int id, String nombre, String correo, String clave) {
        super(id, nombre, correo, clave);
    }

    @Override
    public String getTipo() {
        return "Premium";
    }

    public void accesoCompleto() {
        System.out.println("Acceso completo habilitado");
    }
}
