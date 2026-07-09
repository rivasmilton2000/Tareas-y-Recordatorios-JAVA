package modelo;

import catalogo.Estado;
import catalogo.Prioridad;

public class Tarea extends Actividad {
    protected Estado estado;

    public Tarea(int id, String titulo, String descripcion, Prioridad prioridad, Estado estado) {
        this(id, 0, titulo, descripcion, prioridad, estado);
    }

    public Tarea(int id, int usuarioId, String titulo, String descripcion, Prioridad prioridad, Estado estado) {
        super(id, usuarioId, titulo, descripcion, prioridad);
        this.estado = estado;
    }

    public void cambiarEstado(Estado nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    @Override
    public Actividad clonarParaCompartir() {
        return new Tarea(id, usuarioId, titulo, descripcion, prioridad, estado);
    }

    @Override
    public void mostrarInfo() {
        super.mostrarInfo();
        System.out.println("Estado: " + estado.getEtiqueta());
    }

    @Override
    public String toString() {
        return "Tarea{" +
                "id=" + id +
                ", usuarioId=" + usuarioId +
                ", titulo='" + titulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", prioridad=" + prioridad +
                ", estado=" + estado +
                '}';
    }
}
