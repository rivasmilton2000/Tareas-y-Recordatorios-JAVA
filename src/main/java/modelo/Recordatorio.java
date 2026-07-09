package modelo;

import catalogo.Prioridad;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Recordatorio extends Actividad {
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private LocalDateTime fechaHora;
    private boolean notificado;

    public Recordatorio(int id, String titulo, String descripcion, Prioridad prioridad, LocalDateTime fechaHora) {
        super(id, titulo, descripcion, prioridad);
        this.fechaHora = fechaHora;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public boolean isNotificado() {
        return notificado;
    }

    public void setNotificado(boolean notificado) {
        this.notificado = notificado;
    }

    public void editarFecha(LocalDateTime nuevaFechaHora) {
        this.fechaHora = nuevaFechaHora;
        this.notificado = false;
    }

    public synchronized boolean debeNotificar(LocalDateTime fechaActual) {
        return !notificado && !fechaHora.isAfter(fechaActual);
    }

    public synchronized void actividadAlerta() {
        if (notificado) {
            return;
        }
        notificado = true;
        System.out.println("Recordatorio activado para: " + fechaHora.format(DISPLAY_FORMATTER));
    }

    @Override
    public Actividad clonarParaCompartir() {
        return new Recordatorio(id, titulo, descripcion, prioridad, fechaHora);
    }

    @Override
    public void mostrarInfo() {
        super.mostrarInfo();
        System.out.println("Fecha y hora: " + fechaHora.format(DISPLAY_FORMATTER));
        System.out.println("Notificado: " + (notificado ? "Si" : "No"));
    }
}
