package Hilos;

import java.time.LocalDateTime;
import modelo.Actividad;
import modelo.Recordatorio;
import modelo.Usuario;

public class MonitorRecordatorios implements Runnable {
    private final Usuario usuario;
    private final long intervaloRevisionMs;
    private volatile boolean activo = true;

    public MonitorRecordatorios(Usuario usuario, long intervaloRevisionMs) {
        this.usuario = usuario;
        this.intervaloRevisionMs = intervaloRevisionMs;
    }

    public void detener() {
        activo = false;
    }

    @Override
    public void run() {
        try {
            while (activo) {
                for (Actividad actividad : usuario.obtenerElementos()) {
                    if (actividad instanceof Recordatorio recordatorio
                            && recordatorio.debeNotificar(LocalDateTime.now())) {
                        recordatorio.actividadAlerta();
                    }
                }

                Thread.sleep(intervaloRevisionMs);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[" + Thread.currentThread().getName() + "] Monitor de recordatorios interrumpido.");
        }
    }
}