package Hilos;

import java.util.List;
import modelo.Actividad;
import modelo.Usuario;

public class CargadorActividades implements Runnable {
    private final Usuario usuario;
    private final List<Actividad> actividades;
    private final long pausaMs;
    private final String nombreProceso;

    public CargadorActividades(Usuario usuario, List<Actividad> actividades, long pausaMs, String nombreProceso) {
        this.usuario = usuario;
        this.actividades = actividades;
        this.pausaMs = pausaMs;
        this.nombreProceso = nombreProceso;
    }

    @Override
    public void run() {
        try {
            for (Actividad actividad : actividades) {
                usuario.crearElemento(actividad);
                System.out.println("[" + Thread.currentThread().getName() + "] "
                        + nombreProceso + ": actividad registrada.");
                Thread.sleep(pausaMs);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[" + Thread.currentThread().getName() + "] "
                    + nombreProceso + ": carga interrumpida.");
        }
    }
}
