package modelo;

public class UsuarioClasico extends Usuario {
    private static final int LIMITE_TAREAS = 5;
    private static final int LIMITE_COMPARTIDOS = 3;
    private int compartidosRecibidos;

    public UsuarioClasico(int id, String nombre, String correo, String clave) {
        super(id, nombre, correo, clave);
    }

    @Override
    public String getTipo() {
        return "Clasico";
    }

    @Override
    public void crearElemento(Actividad actividad) {
        if (puedeCrearActividad()) {
            super.crearElemento(actividad);
        }
    }

    @Override
    public boolean puedeCrearActividad() {
        return obtenerCantidadElementos() < LIMITE_TAREAS;
    }

    public boolean limiteAlcanzado() {
        return !puedeCrearActividad();
    }

    @Override
    public int limiteCompartidos() {
        return LIMITE_COMPARTIDOS;
    }

    @Override
    public boolean limiteDisponible() {
        return compartidosRecibidos < LIMITE_COMPARTIDOS;
    }

    @Override
    public void recibirActividadCompartida(Actividad actividad) {
        if (limiteDisponible()) {
            compartidosRecibidos++;
            super.crearElemento(actividad);
        }
    }
}
