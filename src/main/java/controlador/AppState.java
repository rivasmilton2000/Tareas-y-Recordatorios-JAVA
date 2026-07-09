package controlador;

import app.RecordatorioScheduler;
import java.util.concurrent.atomic.AtomicInteger;
import modelo.Usuario;
import sistema.GestorActividades;
import sistema.GestorUsuarios;

public final class AppState {
    private static final AppState INSTANCE = new AppState();

    private final GestorUsuarios gestorUsuarios = new GestorUsuarios();
    private final GestorActividades gestorActividades = new GestorActividades(gestorUsuarios);
    private final AtomicInteger siguienteIdTemporal = new AtomicInteger(1);
    private final RecordatorioScheduler recordatorioScheduler = new RecordatorioScheduler();
    private Usuario usuarioActual;

    private AppState() {
    }

    public static AppState getInstance() {
        return INSTANCE;
    }

    public GestorUsuarios getGestorUsuarios() {
        return gestorUsuarios;
    }

    public GestorActividades getGestorActividades() {
        return gestorActividades;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public void setUsuarioActual(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        if (usuarioActual == null) {
            recordatorioScheduler.detener();
        } else {
            recordatorioScheduler.iniciar(usuarioActual, gestorActividades);
        }
    }

    public int siguienteIdTemporal() {
        return siguienteIdTemporal.getAndIncrement();
    }

    public void cerrarSesion() {
        recordatorioScheduler.detener();
        usuarioActual = null;
    }
}
