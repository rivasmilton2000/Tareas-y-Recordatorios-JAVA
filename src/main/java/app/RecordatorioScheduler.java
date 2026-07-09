package app;

import controlador.Alertas;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import modelo.Recordatorio;
import modelo.Usuario;
import sistema.GestorActividades;

public final class RecordatorioScheduler {
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final long INTERVALO_REVISION_SEGUNDOS = 5L;

    private final Object lock = new Object();
    private ScheduledExecutorService executor;
    private final Set<Integer> alertasEmitidas = new HashSet<>();

    public void iniciar(Usuario usuario, GestorActividades gestorActividades) {
        detener();
        if (usuario == null || gestorActividades == null) {
            return;
        }

        ThreadFactory threadFactory = runnable -> {
            Thread hilo = new Thread(runnable, "RecordatorioScheduler-" + usuario.getId());
            hilo.setDaemon(true);
            return hilo;
        };

        synchronized (lock) {
            executor = Executors.newSingleThreadScheduledExecutor(threadFactory);
            executor.scheduleWithFixedDelay(
                    () -> revisarRecordatorios(usuario, gestorActividades),
                    1L,
                    INTERVALO_REVISION_SEGUNDOS,
                    TimeUnit.SECONDS
            );
        }
    }

    public void detener() {
        synchronized (lock) {
            if (executor != null) {
                executor.shutdownNow();
                executor = null;
            }
            alertasEmitidas.clear();
        }
    }

    private void revisarRecordatorios(Usuario usuario, GestorActividades gestorActividades) {
        try {
            List<Recordatorio> pendientes = gestorActividades.obtenerNotificacionesPendientes(usuario);
            synchronized (lock) {
                alertasEmitidas.retainAll(
                        pendientes.stream().map(Recordatorio::getId).collect(java.util.stream.Collectors.toSet())
                );
            }

            for (Recordatorio recordatorio : pendientes) {
                boolean primeraAlerta;
                synchronized (lock) {
                    primeraAlerta = alertasEmitidas.add(recordatorio.getId());
                }
                if (primeraAlerta) {
                    mostrarNotificacion(recordatorio);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al revisar recordatorios: " + e.getMessage());
        }
    }

    private void mostrarNotificacion(Recordatorio recordatorio) {
        Platform.runLater(() -> Alertas.notificacion(
                "Recordatorio pendiente",
                recordatorio.getTitulo(),
                "Programado para " + recordatorio.getFechaHora().format(DISPLAY_FORMATTER)
                        + "\n" + recordatorio.getDescripcion()
        ));
    }
}
