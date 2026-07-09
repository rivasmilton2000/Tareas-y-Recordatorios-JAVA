package sistema;

import catalogo.Prioridad;
import java.time.LocalDateTime;

public class DatosActividad {
    private final int id;
    private final String tipo;
    private final String titulo;
    private final String descripcion;
    private final Prioridad prioridad;
    private final LocalDateTime fechaRecordatorio;

    public DatosActividad(int id, String tipo, String titulo, String descripcion, Prioridad prioridad, LocalDateTime fechaRecordatorio) {
        this.id = id;
        this.tipo = tipo;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.fechaRecordatorio = fechaRecordatorio;
    }

    public int getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public LocalDateTime getFechaRecordatorio() {
        return fechaRecordatorio;
    }
}
